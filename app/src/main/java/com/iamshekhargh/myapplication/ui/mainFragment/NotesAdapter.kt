package com.iamshekhargh.myapplication.ui.mainFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.ItemNoteBinding
import com.iamshekhargh.myapplication.ui.LabelAdapter

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 9:32 PM.
 */
class NotesAdapter constructor(private val listener: OnNoteClicked) :
    ListAdapter<Note, NotesAdapter.NotesViewHolder>(DiffUtilsItem()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotesViewHolder(
        private val binding: ItemNoteBinding,
        private val listener: OnNoteClicked
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.apply {

                root.setOnClickListener {
                    listener.noteItemClicked(note)
                }
                noteHeading.text = note.heading
                noteDescription.text = note.description

                if (note.bookmark) {
                    noteBookmark.visibility = View.VISIBLE
                } else {
                    noteBookmark.visibility = View.GONE
                }

                noteCreateOn.visibility = View.VISIBLE
                noteCreateOn.text = note.simpleDate

                noteId.text = note.firebaseUserId

                if (note.firebaseUserId.isNotEmpty()) {
                    noteOnFirebase.visibility = View.VISIBLE
                } else {
                    noteOnFirebase.visibility = View.GONE
                }

                if (note.labels.isNotEmpty()) {

                    val lAdapter = LabelAdapter(null)
                    noteRvLabel.adapter = lAdapter
                    lAdapter.submitList(note.labels)
                    noteRvLabel.visibility = View.VISIBLE

                } else {
                    noteRvLabel.visibility = View.INVISIBLE
                }
            }
        }
    }

    class DiffUtilsItem : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.current == newItem.current

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem == newItem

    }

    interface OnNoteClicked {
        fun noteItemClicked(n: Note)
    }
}