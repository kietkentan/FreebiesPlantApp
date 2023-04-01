package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.databinding.ItemPlantBinding
import com.khtn.freebies.helper.hide
import com.khtn.freebies.module.Plant
import com.squareup.picasso.Picasso

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

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemPlantBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Plant) {
            if (item.images.isNotEmpty())
                Picasso.get().load(item.images[0]).into(binding.ivReviewPlant)

            if (item.kingdom.isNotEmpty())
                binding.tvKingdom.text = item.kingdom
            else binding.layoutKingdom.hide()

            if (item.family.isNotEmpty())
                binding.tvFamily.text = item.family
            else binding.layoutFamily.hide()

            binding.tvNamePlant.text = item.name
            binding.tvDescriptionPlant.text = item.description
        }
    }
}