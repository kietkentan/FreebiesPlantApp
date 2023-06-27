package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khtn.freebies.databinding.ItemImage1Binding

class ImageListAdapter(
    val onItemClicked: (String, Int) -> Unit
): RecyclerView.Adapter<ImageListAdapter.MyViewHolder>() {
    private var list: MutableList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemImage1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemImage1Binding): RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: String) {
            binding.img = uri
            binding.ivImage1.setOnClickListener { onItemClicked(uri, adapterPosition) }
        }
    }
}