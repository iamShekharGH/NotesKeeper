package com.iamshekhargh.myapplication.repository

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.data.NotesDao
import com.iamshekhargh.myapplication.datastore.DataStoreManager
import com.iamshekhargh.myapplication.utils.logi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "NotesRepository"

/**
 * Created by <<-- iamShekharGH -->>
 * on 21 April 2021
 * at 3:24 PM.
 */
@Singleton
class NotesRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val db: FirebaseFirestore,
    private val dataStorePrefs: DataStoreManager,
    private val scope: CoroutineScope,
    private val dbC: CollectionReference,
    private val auth: FirebaseAuth,
) {

    companion object {
        private const val TAG = "NotesRepository"
    }

    init {
        dbC.addSnapshotListener { value, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }
            value?.documents?.forEach { documentSnapshot ->
                val note = documentSnapshot.toObject<Note>()
                Log.i(TAG, "addSnapshotListener: $note")
                logi(TAG, "----------------------------------")
            }
        }

        dbC.whereEqualTo("firebaseUserId", auth.currentUser?.uid ?: "noUser")
    }

    private val dataStorePrefsFlow = dataStorePrefs.prefFlow
    private val searchQuery = MutableLiveData<String>("")

    val notes = combine(searchQuery.asFlow(), dataStorePrefsFlow) { q, dsp ->
        Pair(q, dsp)
    }.flatMapLatest { (query, dsPrefs) ->
        val ascendingOrderBool = dsPrefs.ascending
        val sortOrder = dsPrefs.sortOrder
        notesDao.getSearchResultNotes(query, ascendingOrderBool, sortOrder)

    }.asLiveData()

    private val eventsChannel = Channel<ChannelEvents>()
    val eventsAsFlow = eventsChannel.receiveAsFlow()


    private fun addNoteToDao(n: Note) = scope.launch {
        val list = notesDao.getThisNote(n.current)
        val edit = (list.isNotEmpty())
        if (edit) {
            notesDao.updateNote(n)
        } else
            notesDao.insertNote(n)
    }

    fun setSearchQuery(text: String) {
        try {
            searchQuery.value = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Notes DAO functions.
    suspend fun deleteNote(n: Note) {
        notesDao.deleteNote(n)
        deleteNoteFromFirestore(n)
    }

    suspend fun insertNote(n: Note) {
        notesDao.insertNote(n)
        insertNoteToFirestore(n)
    }

    suspend fun updateNote(note: Note) {
        notesDao.updateNote(note)
//        updateNoteOnFirestore(note)
        insertNoteToFirestore(note)
    }

    suspend fun deleteAllNotes() {
        notesDao.deleteAllNotes()
    }

    // util functions

    private fun showProgressBar(decision: Boolean) = scope.launch {
        eventsChannel.send(ChannelEvents.ShowProgressBar(decision))
    }

    private fun showNetworkMessage(message: String) = scope.launch {
        eventsChannel.send(ChannelEvents.ShowNetworkMessage(message))
    }


    // FireStore functions.

    fun fetchFireStoreList() {
        if (checkIfLoggedIn()) {
            showProgressBar(true)
            getFirebaseUserIdQuery().get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.addOnSuccessListener { querySnapshot ->
                            querySnapshot.documents.forEach { documentSnapshot ->
                                val n = documentSnapshot.toObject(Note::class.java)

                                if (n != null) {
                                    scope.launch {
                                        logi(TAG, "Inserting [${n.heading}] [${n.id}] to room DB.")

                                        notesDao.insertNote(n)
                                    }
                                }
                            }
                        }
//                        uploadRoomListToFirestore2()
                    } else {
                        task.exception?.message?.let { message ->
                            logi(TAG, message)
                            showNetworkMessage(message)
                        }
                        task.exception?.printStackTrace()
                    }

                    showProgressBar(false)
                }
        }
    }

    private fun uploadRoomListToFirestore() = scope.launch {
        if (checkIfLoggedIn()) {
            val batch = db.batch()
            val pairHe = arrayListOf<Pair<DocumentReference, Note>>()

            notesDao.getAllNotes().collect { noteList ->
                noteList.forEach { note ->
                    val dr = dbC.document(note.current.toString())
                    val newNote: Note =
                        if (note.firebaseUserId.isEmpty() && getFirebaseUid().isNotBlank()) {
                            note.copy(firebaseUserId = getFirebaseUid())
                        } else {
                            note
                        }
                    pairHe.add(Pair(dr, newNote))
                }
            }

            pairHe.forEach { (documentReference, note) ->
                batch.set(documentReference, note)
            }

            showProgressBar(true)
            batch.commit()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        logi(TAG, "ROOM DB uploaded to FB")
                    } else {
                        task.exception?.message?.let { logi(TAG, it) }
                        task.exception?.printStackTrace()
                    }
                    showProgressBar(false)
                }
                .addOnFailureListener { exception ->
                    logi(TAG, "------------BATCH UPLOAD FAILED---------------")
                    logi(TAG, exception.message.toString())
                    exception.printStackTrace()
                }
        }
    }

    fun uploadToFirebase(notes: List<Note>) {
        if (checkIfLoggedIn()) {
            val batch = db.batch()
            notes.forEach { note ->
                val newNote: Note =
                    if (note.firebaseUserId.isEmpty() && getFirebaseUid().isNotBlank()) {
                        note.copy(firebaseUserId = getFirebaseUid())
                    } else {
                        note
                    }
                batch.set(dbC.document(note.current.toString()), newNote)
            }

            batch.commit()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        logi(TAG, "ROOM DB uploaded to FB")
                    } else {
                        task.exception?.message?.let { logi(TAG, it) }
                        task.exception?.printStackTrace()
                    }
                    showProgressBar(false)
                    showNetworkMessage("Upload to firebase complete.")
                }
        }
    }

    private fun deleteNoteFromFirestore(n: Note) {
        showProgressBar(true)

        dbC.document(n.current.toString())
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logi(TAG, "$n DELETED!!!")
                } else {
                    task.exception?.message?.let { message ->
                        logi(TAG, message)
                        showNetworkMessage(message)
                    }
                    task.exception?.printStackTrace()
                }
                showProgressBar(false)
            }
    }

    private fun insertNoteToFirestore(n: Note) {
        if (checkIfLoggedIn()) {
            showProgressBar(true)
            dbC.document(n.current.toString()).set(n.copy(firebaseUserId = getFirebaseUid()))
                .addOnCompleteListener { showProgressBar(false) }
        } else {

        }
    }

    private fun updateNoteOnFirestore(n: Note) {
        if (checkIfLoggedIn()) {
            showProgressBar(true)

            dbC.document(n.current.toString()).update(
                mapOf(
                    "heading" to n.heading,
                    "description" to n.description,
                    "labels" to n.labels,
                    "bookmark" to n.bookmark,
                    "current" to n.current,
                    "reminder" to n.reminder,
                    "id" to n.id,
                )
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    logi(TAG, "------------------------------ update note firestore.")
                    task.exception?.message?.let { logi(TAG, it) }
                    task.exception?.hashCode()?.let { logi(TAG, it.toString()) }
                    task.exception?.printStackTrace()
                }
                showProgressBar(false)
            }
        }
    }

    private fun checkIfLoggedIn(): Boolean { // return message here to user bout logging in.
        return (auth.currentUser?.uid ?: "").isNotEmpty()
    }

    private fun getFirebaseUserIdQuery(): Query {
        return dbC.whereEqualTo("firebaseUserId", auth.currentUser?.uid)
    }

    private fun getFirebaseUid(): String {
        return auth.currentUser?.uid ?: ""
    }


}

sealed class ChannelEvents {
    data class ShowProgressBar(val show: Boolean) : ChannelEvents()
    data class ShowNetworkMessage(val message: String) : ChannelEvents()
}
