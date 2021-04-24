package com.iamshekhargh.myapplication.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.data.NotesDao
import com.iamshekhargh.myapplication.datastore.DataStoreManager
import com.iamshekhargh.myapplication.utils.logi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "NotesRepository"

/**
 * Created by <<-- iamShekharGH -->>
 * on 21 April 2021
 * at 3:24 PM.
 *
 */
@Singleton
class NotesRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val db: FirebaseFirestore,
    private val dataStorePrefs: DataStoreManager,
    private val scope: CoroutineScope,
    private val dbC: CollectionReference
) {

    companion object {
        private const val TAG = "NotesRepository"
    }

    private val dataStorePrefsFlow = dataStorePrefs.prefFlow
    private val searchQuery = MutableLiveData<String>("")

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
    }

    private fun addNoteToDao(n: Note) = scope.launch {
        val list = notesDao.getThisNote(n.current)
        val edit = (list.isNotEmpty())
        if (edit) {
            notesDao.updateNote(n)
        } else
            notesDao.insertNote(n)
    }

//    private val eventsChannel = Channel<ChannelEvents>()
//    val eventsAsFlow = eventsChannel.receiveAsFlow()

    val notes = combine(searchQuery.asFlow(), dataStorePrefsFlow) { q, dsp ->
        Pair(q, dsp)
    }.flatMapLatest { (query, dsPrefs) ->
        val ascendingOrderBool = dsPrefs.ascending
        val sortOrder = dsPrefs.sortOrder
        notesDao.getSearchResultNotes(query, ascendingOrderBool, sortOrder)

    }.asLiveData()

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
        updateNoteOnFirestore(note)
    }

    suspend fun deleteAllNotes() {
        notesDao.deleteAllNotes()
    }


    // FireStore functions.

    fun fetchFireStoreList() {
        //.whereEqualTo("userId", Firebase.auth.currentUser.uid)
        dbC.get()
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
                    uploadRoomListToFirestore()
                } else {
                    task.exception?.message?.let { logi(TAG, it) }
                    task.exception?.printStackTrace()
                }
            }
    }

    private fun uploadRoomListToFirestore() {
        val batch = db.batch()
        val pairHe = arrayListOf<Pair<DocumentReference, Note>>()

        scope.launch {
            notesDao.getAllNotes().collect { noteList ->
                noteList.forEach { note ->
                    logi(TAG, "-------------- this is uploaded to FB $note")
                    val dr = dbC.document(note.current.toString())
                    pairHe.add(Pair(dr, note))
                }
            }
        }

        pairHe.forEach { pair ->
            batch.set(pair.first, pair.second)
        }

        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logi(TAG, "ROOM DB uploaded to FB")
            } else {
                task.exception?.message?.let { logi(TAG, it) }
                task.exception?.printStackTrace()
            }
        }
    }

    private fun deleteNoteFromFirestore(n: Note) {
        dbC.document(n.current.toString())
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logi(TAG, "$n DELETED!!!")
                } else {
                    task.exception?.message?.let { logi(TAG, it) }
                    task.exception?.printStackTrace()
                }
            }
    }

    private fun insertNoteToFirestore(n: Note) {
        dbC.document(n.current.toString()).set(n)
    }

    private fun updateNoteOnFirestore(n: Note) {
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
        )
    }
}

//sealed class ChannelEvents {
//    data class InsertNoteIntoDao(val note: Note) : ChannelEvents()
//}