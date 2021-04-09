package com.iamshekhargh.myapplication.ui.mainFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.iamshekhargh.myapplication.data.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    notesDao: NotesDao
) : ViewModel() {
    private val channel = Channel<EventHandler>()
    val channelFlow = channel.receiveAsFlow()

    fun fabClicked() = viewModelScope.launch {
        channel.send(EventHandler.OpenAddNoteFragment)
    }

    val notesList = notesDao.getAllNotes().asLiveData()
}

sealed class EventHandler {
    object OpenAddNotesFrag : EventHandler()
    object OpenAddNoteFragment : EventHandler()
}