package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khtn.freebies.databinding.ItemPlantBinding
import com.khtn.freebies.helper.hide
import com.khtn.freebies.module.Plant

class PlantItemAdapter (
    val onItemClick: (plant: Plant) -> Unit
): RecyclerView.Adapter<PlantItemAdapter.MyViewHolder>() {
    private var list: MutableList<Plant> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemPlantBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<Plant>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun getList(): MutableList<Plant> {
        return list
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemPlantBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Plant) {
            binding.plant = item
            binding.layoutItemPlant.setOnClickListener { onItemClick(item) }
        }
    }
}