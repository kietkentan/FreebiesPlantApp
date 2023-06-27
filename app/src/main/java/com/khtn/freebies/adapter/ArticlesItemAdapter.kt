package com.khtn.freebies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.R
import com.khtn.freebies.databinding.ItemArticlesBinding
import com.khtn.freebies.helper.Time
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.User

class ArticlesItemAdapter(
    val isViewFavorite: Boolean = false,
    val onItemClick: (articles: Articles) -> Unit,
    val onFavoriteClick: (articlesId: String, isLiked: Boolean) -> Unit,
    val onBookMarkClick: (articlesId: String, isLiked: Boolean) -> Unit
): RecyclerView.Adapter<ArticlesItemAdapter.MyViewHolder>() {
    private var listArticles: MutableList<Articles> = arrayListOf()
    private var listUser: List<User> = arrayListOf()
    private var mapFavorite: Map<String, Boolean> = mapOf()
    private var mapBookMark: Map<String, Boolean> = mapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemArticlesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listArticles[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListArticles(list: MutableList<Articles>) {
        this.listArticles = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListUser(list: List<User>) {
        this.listUser = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMapFavorite(map: Map<String, Boolean>) {
        mapFavorite = map
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMapBookMark(map: Map<String, Boolean>) {
        mapBookMark = map
        notifyDataSetChanged()
    }

    fun getListArticles(): MutableList<Articles> {
        return listArticles
    }

    fun getMapFavorite(): MutableMap<String, Boolean> {
        return mapFavorite.toMutableMap()
    }

    fun getMapBookMark(): MutableMap<String, Boolean> {
        return mapBookMark.toMutableMap()
    }

    override fun getItemCount(): Int {
        return listArticles.size
    }

    inner class MyViewHolder(private val binding: ItemArticlesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Articles) {
            binding.articles = item

            if (isViewFavorite) binding.layoutFavorite.hide()
            else {
                binding.layoutItemArticles.show()
                if (mapFavorite.isEmpty()) {
                    binding.ibLike.hide()
                } else if (mapFavorite.containsKey(item.id)) {
                    binding.ibLike.show()
                    binding.ibLike.setImageResource(if (mapFavorite[item.id] == true) R.drawable.ic_heart_selected else R.drawable.ic_heart_none)
                }

                if (mapBookMark.isEmpty()) {
                    binding.ibBookMark.hide()
                } else {
                    binding.ibBookMark.show()
                    binding.ibBookMark.setImageResource(if (mapBookMark[item.id] == true) R.drawable.ic_book_mark_selected else R.drawable.ic_book_mark_none)
                }
            }

            for (user in listUser)
                if (user.id == item.createBy) {
                    binding.user = user
                    break
                }

            binding.time = Time.formatMilliToTimeDate(item.createAt)
            binding.layoutItemArticles.setOnClickListener { onItemClick(item) }
            binding.ibLike.setOnClickListener {
                onFavoriteClick(item.id, mapFavorite[item.id] ?: false)
            }
            binding.ibBookMark.setOnClickListener {
                onBookMarkClick(item.id, mapFavorite[item.id] ?: false)
            }
        }
    }
}