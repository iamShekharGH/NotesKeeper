package com.iamshekhargh.myapplication.ui.mainFragment

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.datastore.DataStoreManager
import com.iamshekhargh.myapplication.datastore.SortOrder
import com.iamshekhargh.myapplication.repository.ChannelEvents
import com.iamshekhargh.myapplication.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 8:30 PM.
 */
@HiltViewModel
class FragmentMainViewModel @Inject constructor(
    private val repository: NotesRepository,
    private val state: SavedStateHandle,
    private val dataStorePrefs: DataStoreManager,
) : ViewModel() {

    private val channel = Channel<EventHandler>()
    val channelFlow = channel.receiveAsFlow()

    fun getNotesList(): LiveData<List<Note>> {
        return repository.notes
    }

    fun setSearchQuery(text: String) {
        repository.setSearchQuery(text)
    }

    fun fabClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenAddNoteFragment)
    }

    fun testFabClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenTestingFragment)
    }

    fun itemSwiped(note: Note?) = viewModelScope.launch {
        note?.let { n ->
            repository.deleteNote(n)
            channel.send(EventHandler.ShowConfirmationSnackBar(note))
        }
    }

    fun noteItemClicked(n: Note) = viewModelScope.launch {
        channel.send(EventHandler.OpenEditNoteFragment(n))
    }

    fun swipePulled() = viewModelScope.launch {
        channel.send(EventHandler.ShowSnackbar("Kya ?"))
    }

    // Menu Item Clicks
    fun logoutMenuItemClicked() {
        Firebase.auth.signOut()
    }

    fun profileMenuItemClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenProfileFragment)
    }

    fun deleteUserClicked() {
        // add a confirmation alert dialog.
        // Then delete user from fireStore.
    }

    fun ascendingOrderToggleClicked(isAscending: Boolean) = viewModelScope.launch {
        dataStorePrefs.setAscendingOrderBool(isAscending)
    }

    fun insertItem(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }

    fun deleteAllNotesClicked() = viewModelScope.launch {
        repository.deleteAllNotes()
    }


    fun sortByNameMenuItemClicked() = viewModelScope.launch {
        dataStorePrefs.setSortOrder(SortOrder.SORT_BY_NAME)
    }

    fun sortByDateCreatedMenuItemClicked() = viewModelScope.launch {
        dataStorePrefs.setSortOrder(SortOrder.SORT_BY_DATE_CREATED)
    }

    fun listenToRepoEvents() = viewModelScope.launch {
        repository.eventsAsFlow.collect { event ->
            when (event) {
                is ChannelEvents.ShowProgressBar -> channel.send(EventHandler.ProgressBarShow(event.show))
                is ChannelEvents.ShowNetworkMessage -> channel.send(
                    EventHandler.ShowFirebaseMessage(
                        event.message
                    )
                )
            }
        }
    }

    fun fetchFromFirebase() {
        repository.fetchFireStoreList()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun uploadToFirebase(notes: List<Note>?) {
        if (notes != null)
            repository.uploadToFirebase(notes)
    }

}

sealed class EventHandler {
    data class OpenEditNoteFragment(val note: Note) : EventHandler()
    object OpenTestingFragment : EventHandler()
    object OpenAddNoteFragment : EventHandler()
    object OpenProfileFragment : EventHandler()
    data class ShowConfirmationSnackBar(val note: Note) : EventHandler()
    data class ShowSnackbar(val text: String) : EventHandler()
    data class ProgressBarShow(val show: Boolean) : EventHandler()
    data class ShowFirebaseMessage(val text: String) : EventHandler()

}