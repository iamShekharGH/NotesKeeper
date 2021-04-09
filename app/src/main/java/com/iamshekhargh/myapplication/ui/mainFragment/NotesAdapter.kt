package com.iamshekhargh.myapplication.ui.mainFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.ItemNoteBinding

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 9:32 PM.
 */
class NotesAdapter : ListAdapter<Note, NotesAdapter.NotesViewHolder>(DiffUtilsItem()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotesViewHolder(private val binding: ItemNoteBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.apply {
                noteHeading.setText(note.heading)
                noteDiscription.setText(note.description)

                if (note.labels.isNotEmpty()) {
                    val lAdapter = LabelAdapter()
                    noteRvLabel.adapter = lAdapter
                    noteRvLabel.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
//                    noteRvLabel.layoutManager =
//                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
                    lAdapter.submitList(note.labels)
                } else {
                    noteRvLabel.visibility = View.INVISIBLE
                }
            }
        }
    }

    class DiffUtilsItem : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem

    }
}