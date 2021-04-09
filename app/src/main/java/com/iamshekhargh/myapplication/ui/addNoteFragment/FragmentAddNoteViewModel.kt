package com.iamshekhargh.myapplication.ui.addNoteFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.data.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 1:04 AM.
 */
@HiltViewModel
class FragmentAddNoteViewModel @Inject constructor(
    private val notesDao: NotesDao
) : ViewModel() {
    private val channel = Channel<EventsAddNote>()
    val channelFlow = channel.receiveAsFlow()
    lateinit var note: Note

    fun onSubmitClicked(note: Note) = viewModelScope.launch {
        if (note.heading.isNotEmpty()) {
            notesDao.insertNote(note)
            channel.send(EventsAddNote.PopTheFragment)

        } else channel.send(EventsAddNote.HeadingIsEmpty("Heading cannot be Empty."))
    }
}

sealed class EventsAddNote {
    class HeadingIsEmpty(val message: String) : EventsAddNote()
    object PopTheFragment : EventsAddNote()
}