package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.databinding.ItemAlphabetSpeciesBinding
import com.khtn.freebies.module.Species

class SpeciesAlphabetAdapter(
    val onItemClicked: (Species) -> Unit
): RecyclerView.Adapter<SpeciesAlphabetAdapter.MyViewHolder>() {
    private var speciesList: Map<Char, MutableList<Species>> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemAlphabetSpeciesBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val char = if (position == 0) '#' else 'A' + (position - 1)
        val item = speciesList[char]
        holder.bind(item, char)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(speciesList: Map<Char, MutableList<Species>>){
        this.speciesList = speciesList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return speciesList.size
    }

    inner class MyViewHolder(private val binding: ItemAlphabetSpeciesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(list: MutableList<Species>?, char: Char) {
            val adapter by lazy { SpeciesItemAdapter(onItemClicked) }
            adapter.updateList(list!!)

            val linearLayoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            binding.recSpeciesInAlphabet.layoutManager = linearLayoutManager

            binding.tvTitleAlphabet.text = char.toString()
            binding.recSpeciesInAlphabet.adapter = adapter
        }
    }
}