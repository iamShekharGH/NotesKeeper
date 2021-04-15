package com.iamshekhargh.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamshekhargh.myapplication.databinding.LabelNoteItemBinding
import com.iamshekhargh.myapplication.databinding.LabelNoteItemLongBinding

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 9:42 PM.
 */
class LabelAdapter(private val listener: LabelAdapterListEditingOptions?) :
    ListAdapter<String, LabelAdapter.LabelViewHolder>(DiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {

        val binding =
            LabelNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingLong =
            LabelNoteItemLongBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if (listener == null) {
            LabelViewHolder(binding, bindingLong, null)
        } else {
            LabelViewHolder(binding, bindingLong, listener)
        }
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

//    override fun deleteItem(position: Int) {
//        val newLIst = ArrayList(currentList)
//        newLIst.remove(currentList[position])
//        submitList(newLIst)
//
//    }


    class LabelViewHolder(
        private val binding: LabelNoteItemBinding,
        private val bindingLong: LabelNoteItemLongBinding,
        private val listener: LabelAdapterListEditingOptions?
    ) :
        RecyclerView.ViewHolder(if (listener == null) binding.root else bindingLong.root) {



        fun bind(s: String) {
            if (listener == null) {
                binding.apply {
                    labelTvLabel.setText(s)
                    labelTvLabel.isFocusable = false
                }

            } else {
                bindingLong.apply {
                    labelTvLabel.setText(s)

                    labelIvRemove.setOnClickListener() {
//                    onClickOperations.deleteItem(adapterPosition)
                        listener.deleteLabel(adapterPosition)
                    }

                    labelTvLabel.addTextChangedListener { newText ->
                        listener.editLabel(adapterPosition, newText.toString())
                    }
                }
            }

        }
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
}

//
//interface OnClickOperations {
//    fun deleteItem(position: Int)
//}

interface LabelAdapterListEditingOptions {
    fun deleteLabel(position: Int)
    fun editLabel(position: Int, newText: String)
}
