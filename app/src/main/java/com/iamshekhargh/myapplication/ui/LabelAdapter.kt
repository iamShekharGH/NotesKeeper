package com.iamshekhargh.myapplication.ui.mainFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamshekhargh.myapplication.databinding.LabelNoteItemBinding

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 9:42 PM.
 */
class LabelAdapter() : OnClickOperations,
    ListAdapter<String, LabelAdapter.LabelViewHolder>(DiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val binding =
            LabelNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position), this)

    }

    override fun deleteItem(position: Int) {
//        currentList.removeAt(position)
        val newLIst = ArrayList(currentList)
        newLIst.remove(currentList[position])
        submitList(newLIst)

    }


    class LabelViewHolder(private val binding: LabelNoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(s: String, onClickOperations: OnClickOperations) {
            binding.apply {
                labelTvLabel.setText(s)

                labelIvRemove.setOnClickListener() {

                    onClickOperations.deleteItem(adapterPosition)
                    Log.i(
                        "TAG",
                        "bind: adapterPosition $adapterPosition layoutPosition $layoutPosition"
                    )
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


interface OnClickOperations {
    fun deleteItem(position: Int)
}
