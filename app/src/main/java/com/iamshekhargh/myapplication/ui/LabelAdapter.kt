package com.iamshekhargh.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamshekhargh.myapplication.databinding.ItemChipBinding

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 9:42 PM.
 */
class LabelAdapter(private val listener: LabelAdapterListEditingOptions?) :
    ListAdapter<String, LabelAdapter.LabelViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {

        val bindingChip =
            ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if (listener == null) {
            LabelViewHolder(bindingChip, null)
        } else {
            LabelViewHolder(bindingChip, listener)
        }
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class LabelViewHolder(
//        private val binding: LabelNoteItemBinding,
//        private val bindingLong: LabelNoteItemLongBinding,
        private val binding: ItemChipBinding,
        private val listener: LabelAdapterListEditingOptions?
    ) :
//        RecyclerView.ViewHolder(if (listener == null) binding.root else bindingLong.root) {
        RecyclerView.ViewHolder(binding.root) {


        fun bind(s: String) {
            if (listener == null) {
                binding.apply {
                    labelTvLabel.text = s
//                    labelTvLabel.isFocusable = false
                    labelTvLabel.isCloseIconVisible = false
                }

            } else {
                binding.apply {
                    labelTvLabel.text = s
                    labelTvLabel.setOnCloseIconClickListener() {
                        listener.deleteLabel(adapterPosition)
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
