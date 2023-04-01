package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.databinding.ItemSpeciesInAlphabetBinding
import com.khtn.freebies.module.Species

class SpeciesItemAdapter(
    val onItemClicked: (Species) -> Unit
): RecyclerView.Adapter<SpeciesItemAdapter.MyViewHolder>() {
        private var speciesList: MutableList<Species> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = ItemSpeciesInAlphabetBinding.inflate(LayoutInflater.from(parent.context), parent,false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = speciesList[position]
            holder.bind(item)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateList(speciesList: MutableList<Species>){
            this.speciesList = speciesList
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return speciesList.size
        }

        inner class MyViewHolder(private val binding: ItemSpeciesInAlphabetBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(species: Species) {
                binding.tvSpeciesInAlphabet.text = species.name
                binding.tvSpeciesInAlphabet.setOnClickListener { onItemClicked.invoke(species) }
            }
        }
    }