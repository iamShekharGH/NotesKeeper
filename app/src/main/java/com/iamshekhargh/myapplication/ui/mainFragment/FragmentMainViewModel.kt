package com.iamshekhargh.myapplication.ui.mainFragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.data.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val notesDao: NotesDao,
    private val state: SavedStateHandle
) : ViewModel() {

    private val channel = Channel<EventHandler>()
    val channelFlow = channel.receiveAsFlow()

    val searchQuery = MutableStateFlow("")

    val notesList = notesDao.getAllNotes().asLiveData()

    fun fabClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenAddNoteFragment)
    }


    fun testFabClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenTestingFragment)
    }

    fun itemSwiped(note: Note?) = viewModelScope.launch {
        // TODO give a undo option here!!
        note?.let { notesDao.deleteNote(it) }
    }

    fun noteItemClicked(n: Note) = viewModelScope.launch {
        channel.send(EventHandler.OpenEditNoteFragment(n))
    }

    fun logoutMenuItemClicked() {
        Firebase.auth.signOut()
    }

    fun profileMenuItemClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenProfileFragment)
    }

}

sealed class EventHandler {
    class OpenEditNoteFragment(val note: Note) : EventHandler()
    object OpenTestingFragment : EventHandler()
    object OpenAddNoteFragment : EventHandler()
    object OpenProfileFragment : EventHandler()
}