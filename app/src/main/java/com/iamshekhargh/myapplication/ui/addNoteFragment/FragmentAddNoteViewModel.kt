package com.iamshekhargh.myapplication.ui.addNoteFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.repository.NotesRepository
import com.iamshekhargh.myapplication.utils.ChannelEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 1:04 AM.
 */
@HiltViewModel
class FragmentAddNoteViewModel @Inject constructor(
    private val repository: NotesRepository,
) : ViewModel() {
    var cal: Calendar = Calendar.getInstance()

    private val channel = Channel<EventsAddNote>()
    val channelFlow = channel.receiveAsFlow()

    private val TAG = "FragmentAddNoteViewMode"
    var editNote: Note? = null

    val labelList: MutableList<String> = arrayListOf()

    fun onSubmitClicked(note: Note) = viewModelScope.launch {
        if (note.heading.isNotEmpty()) {
            repository.insertNote(note)
            channel.send(EventsAddNote.PopTheFragment)

        } else channel.send(EventsAddNote.ShowSnackbar("Heading cannot be Empty."))
    }

    fun addLabelClicked(s: String) = viewModelScope.launch {
        when {
            s.isNotEmpty() -> {
                labelList.add(s)
                labelList.forEach { l ->
                    Log.i(TAG, "addLabelClicked: $l")
                }
                channel.send(EventsAddNote.AddLabelToTheNote)
            }
            labelList.contains(s) -> {
                channel.send(EventsAddNote.ShowSnackbar("Already hai label, GADHE!!"))
            }
            else -> {
                channel.send(EventsAddNote.ShowSnackbar("Cannot add Empty Label."))
            }
        }
    }

    fun onSubmitClicked(heading: String, description: String, bookmark: Boolean) =
        viewModelScope.launch {
            if (heading.isNotEmpty()) {
                if (editNote == null) {

                    val note = Note(
                        heading = heading,
                        description = description,
                        labels = labelList.toList(),
                        bookmark = bookmark,
                        firebaseUserId = getUid(),
                        reminder = if (cal.get(Calendar.YEAR) != 1990) cal.timeInMillis else 0
                    )
                    repository.insertNote(note)

                    if (note.reminder != 0L)
                        setReminderAlarm(note)

                } else {
                    val note = editNote!!.copy(
                        heading = heading,
                        description = description,
                        labels = labelList.toList(),
                        bookmark = bookmark,
                        current = editNote!!.current,
                        reminder = cal.timeInMillis,
                        id = editNote!!.id,
                    )

                    repository.updateNote(note)
                    if (note.reminder != 0L)
                        setReminderAlarm(note)
                }

                channel.send(EventsAddNote.PopTheFragment)
            } else {
                channel.send(EventsAddNote.ShowSnackbar("Heading cannot be Empty."))
            }
        }

    private fun setReminderAlarm(note: Note) = viewModelScope.launch {
        channel.send(EventsAddNote.SetAlarmForNote(note, cal.timeInMillis))
    }

    private fun getUid(): String {
        return Firebase.auth.currentUser?.uid ?: ""
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

    fun deleteNote() = viewModelScope.launch {
        throwChannelEvents(EventsAddNote.ShowProgressBar(true))
        if (editNote != null)
            repository.deleteNote(editNote!!)

        channel.send(EventsAddNote.PopTheFragment)
    }

    fun listenToRepositoryEvents() = viewModelScope.launch {
        repository.eventsAsFlow.collect { event ->
            when (event) {
                is ChannelEvents.DeleteTaskCompleted -> {
                    throwChannelEvents(EventsAddNote.ShowProgressBar(false))
                    throwChannelEvents(EventsAddNote.PopTheFragment)
                }
                is ChannelEvents.ShowNetworkMessage -> {
                    throwChannelEvents(EventsAddNote.ShowSnackbar(event.message))
                }
                is ChannelEvents.ShowProgressBar -> {
                    throwChannelEvents(EventsAddNote.ShowProgressBar(event.show))
                }
            }
        }
    }

    private fun throwChannelEvents(e: EventsAddNote) = viewModelScope.launch {
        channel.send(e)
    }
}

sealed class EventsAddNote {
    data class ShowSnackbar(val message: String) : EventsAddNote()
    object PopTheFragment : EventsAddNote()
    object AddLabelToTheNote : EventsAddNote()
    object OpenAddLabelDialog : EventsAddNote()
    data class ShowProgressBar(val show: Boolean) : EventsAddNote()
    data class SetAlarmForNote(val note: Note, val time: Long) : EventsAddNote()
}