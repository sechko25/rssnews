package com.example.yetanotherfeed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.yetanotherfeed.databinding.ListItemBinding
import com.example.yetanotherfeed.models.Item

class FeedListAdapter(
    val clickListener: ItemListener
) : ListAdapter<Item, FeedListAdapter.ViewHolder>(FeedDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.clickListener = clickListener
        holder.bind(getItem(position)!!)
    }


    class ViewHolder private constructor(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}



class FeedDiffCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.guid == newItem.guid
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}


class ItemListener(val clickListener: (link: String) -> Unit) {
    fun onClick(item: Item) = clickListener(item.link)
}