package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.databinding.ItemPhotographyBinding
import com.khtn.freebies.module.Photography
import com.squareup.picasso.Picasso

class PhotographyAdapter (
    val onItemClicked: (Photography) -> Unit
): RecyclerView.Adapter<PhotographyAdapter.MyViewHolder>() {
    private var listPhotography: MutableList<Photography> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemPhotographyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listPhotography[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<Photography>) {
        this.listPhotography = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listPhotography.size
    }

    inner class MyViewHolder(private val binding: ItemPhotographyBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Photography) {
            Picasso.get().load(item.image).into(binding.ivPhotography)
            binding.tvTagPhotography.text = "#${item.tag}"
            binding.tvTagPhotography.alpha = 0.6F
        }
    }
}