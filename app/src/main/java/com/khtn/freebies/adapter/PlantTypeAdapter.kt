package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khtn.freebies.databinding.ItemPlantTypeBinding
import com.khtn.freebies.module.PlantType

class PlantTypeAdapter(
    val onItemClicked: (Int, PlantType) -> Unit
): RecyclerView.Adapter<PlantTypeAdapter.MyViewHolder>() {
    private var list: MutableList<PlantType> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemPlantTypeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<PlantType>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemPlantTypeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlantType){
            binding.type = item
        }
    }
}