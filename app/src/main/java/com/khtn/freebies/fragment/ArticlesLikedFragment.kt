package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ArticlesItemAdapter
import com.khtn.freebies.databinding.FragmentArticlesLikedBinding
import com.khtn.freebies.helper.AppConstant
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.deepEqualTo
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.User
import com.khtn.freebies.viewmodel.ArticlesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticlesFragment : Fragment() {
    private lateinit var binding: FragmentArticlesLikedBinding
    private val viewModel: ArticlesViewModel by viewModels()
    private var userData: MutableList<User>? = null
    private var id: String = ""

    private val adapter by lazy {
        ArticlesItemAdapter(
            isViewFavorite = true,
            onItemClick = { articles ->
                findNavController().navigate(R.id.action_profileFragment_to_articlesDetailFragment, Bundle().apply {
                    putString(AppConstant.ARTICES, articles.id)
                })
            },
            onFavoriteClick = { articlesId, isLiked ->  },
            onBookMarkClick = { articlesId, isLiked ->  }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesLikedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recListArticlesLiked.layoutManager = linearLayoutManager
        binding.recListArticlesLiked.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSession {
            it?.id?.let { it1 ->
                viewModel.getListArticlesLiked(it1)
                id = it1
            }
        }
    }

    private fun fetchUser(articlesData: List<Articles>) {
        if (userData == null) {
            val list: MutableList<String> = mutableListOf()
            for (articles in articlesData) {
                if (articles.createBy !in list)
                    list.add(articles.createBy)
            }
            if (list.isNotEmpty())
                viewModel.getUser(list)
        }
    }

    private fun checkFavorite(articlesData: List<Articles>) {
        if (articlesData.isNotEmpty()) {
            val list: MutableList<String> = mutableListOf()
            val mapFavorite: HashMap<String, Boolean> = hashMapOf()

            for (articles in articlesData) {
                list.add(articles.id)
                mapFavorite[articles.id] = true
            }
            viewModel.checkMultiBookMark(id, list)
            adapter.updateMapFavorite(mapFavorite)
        }
    }

    private fun observe() {
        viewModel.articlesLiked.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.recListArticlesLiked.hide()
                    binding.shimmerArticlesList.show()
                    binding.shimmerArticlesList.startShimmer()
                }

                is UiState.Failure -> requireContext().toast(state.error)

                is UiState.Success -> {
                    binding.shimmerArticlesList.stopShimmer()
                    binding.shimmerArticlesList.hide()
                    binding.recListArticlesLiked.show()
                    if (!adapter.getListArticles().deepEqualTo(state.data)) {
                        adapter.updateListArticles(state.data.toMutableList())
                        fetchUser(state.data)
                        checkFavorite(state.data)
                    }
                }
            }
        }

        viewModel.getUsersList.observe(viewLifecycleOwner) {
            if (it is UiState.Success)
                adapter.updateListUser(it.data)
        }

        viewModel.checkMultiBookMark.observe(viewLifecycleOwner) {
            if (it is UiState.Success)
                adapter.updateMapBookMark(it.data)
        }
    }
}