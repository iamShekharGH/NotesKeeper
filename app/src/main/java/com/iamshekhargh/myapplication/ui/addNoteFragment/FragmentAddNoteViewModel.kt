package com.iamshekhargh.myapplication.ui.addNoteFragment

import android.util.Log
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
    private val TAG = "FragmentAddNoteViewMode"
    var editNote: Note? = null

    val labelList: MutableList<String> = arrayListOf()

    fun onSubmitClicked(note: Note) = viewModelScope.launch {
        if (note.heading.isNotEmpty()) {
            notesDao.insertNote(note)
            channel.send(EventsAddNote.PopTheFragment)

        } else channel.send(EventsAddNote.HeadingIsEmpty("Heading cannot be Empty."))
    }

    fun addLabelClicked(s: String) = viewModelScope.launch {
        if (s.isNotEmpty()) {
            labelList.add(s)
            labelList.forEach { l ->
                Log.i(TAG, "addLabelClicked: $l")
            }
            channel.send(EventsAddNote.AddLabelToTheNote)
        } else {
            channel.send(EventsAddNote.HeadingIsEmpty("Cannot add Empty Label."))
        }
    }

    fun onSubmitClicked(heading: String, description: String) = viewModelScope.launch {
        if (heading.isNotEmpty()) {
            val note = Note(
                heading = heading,
                description = description,
                labels = labelList.toList()
            )

            notesDao.insertNote(note)
            channel.send(EventsAddNote.PopTheFragment)
        } else {
            channel.send(EventsAddNote.HeadingIsEmpty("Heading cannot be Empty."))
        }
    }

    fun openLabelAdder(label: String) = viewModelScope.launch {
        channel.send(EventsAddNote.OpenAddLabelDialog)
    }

    fun newLabelEntered(l: String) {
        labelList.add(l)
    }

    fun getTheLabelList(): List<String> {
        return labelList.toList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared: <- vo Destroy ho geya")
    }

    fun loadNoteItem(note: Note?) {
        editNote = note

    }
}

sealed class EventsAddNote {
    class HeadingIsEmpty(val message: String) : EventsAddNote()
    object PopTheFragment : EventsAddNote()
    object AddLabelToTheNote : EventsAddNote()
    object OpenAddLabelDialog : EventsAddNote()
}