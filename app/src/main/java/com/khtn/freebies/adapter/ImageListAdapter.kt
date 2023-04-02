package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.databinding.ItemImageBinding
import com.squareup.picasso.Picasso

class ImageListAdapter(
    val onItemClicked: (Uri) -> Unit
): RecyclerView.Adapter<ImageListAdapter.MyViewHolder>() {
    private var list: MutableList<Uri> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<Uri>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            Picasso.get().load(uri).into(binding.ivImage)
            binding.ivImage.setOnClickListener { onItemClicked(uri) }
        }
    }
}