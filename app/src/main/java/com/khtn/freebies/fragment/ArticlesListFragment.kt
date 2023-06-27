package com.khtn.freebies.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ArticlesItemAdapter
import com.khtn.freebies.databinding.FragmentArticlesListBinding
import com.khtn.freebies.helper.AppConstant
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.hideKeyboard
import com.khtn.freebies.helper.removeAccent
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.Articles
import com.khtn.freebies.module.User
import com.khtn.freebies.viewmodel.ArticlesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticlesListFragment : Fragment() {
    private lateinit var binding: FragmentArticlesListBinding
    private val viewModel: ArticlesViewModel by viewModels()
    private lateinit var articlesData: MutableList<Articles>
    private var userData: MutableList<User>? = null
    private var lastedSearch: String? = null
    private var id: String = ""

    private val adapter by lazy {
        ArticlesItemAdapter(
            onItemClick = { articles ->
                findNavController().navigate(R.id.action_articlesListFragment_to_articlesDetailFragment, Bundle().apply {
                    putString(AppConstant.ARTICES, articles.id)
                })
            },
            onFavoriteClick = { articlesId, isLiked ->
                handleFavoriteClicked(articlesId, isLiked)
            },
            onBookMarkClick = { articlesId, isLiked ->
                handleBookMarkClicked(articlesId, isLiked)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesListBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oberver()

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recArticles.layoutManager = linearLayoutManager
        binding.recArticles.adapter = adapter

        setListener()
        viewModel.getSession {
            viewModel.getArticles()
            id = it!!.id
        }
    }

    private fun oberver() {
        viewModel.getArticles.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.recArticles.hide()
                    binding.shimmerArticlesList.show()
                    binding.shimmerArticlesList.startShimmer()
                }

                is UiState.Failure -> requireContext().toast(state.error)

                is UiState.Success -> {
                    binding.shimmerArticlesList.stopShimmer()
                    binding.shimmerArticlesList.hide()
                    binding.recArticles.show()
                    articlesData = state.data.toMutableList()
                    if (lastedSearch.isNullOrEmpty())
                        adapter.updateListArticles(articlesData)
                    fetchUser()
                    checkFavorite()
                }
            }
        }

        viewModel.getUsersList.observe(viewLifecycleOwner) {
            if (it is UiState.Success)
                adapter.updateListUser(it.data)
        }

        viewModel.checkMultiFavorite.observe(viewLifecycleOwner) {
            if (it is UiState.Success)
                adapter.updateMapFavorite(it.data)
        }

        viewModel.checkMultiBookMark.observe(viewLifecycleOwner) {
            if (it is UiState.Success)
                adapter.updateMapBookMark(it.data)
        }

        viewModel.updateSigleBookMark.observe(viewLifecycleOwner) {
            Log.i("TAG_U", "oberverBookMark: $it")
            if (it is UiState.Success) {
                val map = adapter.getMapBookMark()
                map[it.data.first] = it.data.second
                adapter.updateMapBookMark(map)
            }
        }

        viewModel.updateSingleFavorite.observe(viewLifecycleOwner) {
            Log.i("TAG_U", "oberverFavorite: $it")
            if (it is UiState.Success) {
                val map = adapter.getMapFavorite()
                map[it.data.first] = it.data.second
                adapter.updateMapFavorite(map)
            }
        }
    }

    private fun handleFavoriteClicked(articlesId: String, isLiked: Boolean) {
        if (isLiked) viewModel.removeSingleFavoriteArticles(id, articlesId)
        else viewModel.addSingleFavoritePlant(id, articlesId)
    }

    private fun handleBookMarkClicked(articlesId: String, isLiked: Boolean) {
        if (isLiked) viewModel.removeSingleBookMarkArticles(id, articlesId)
        else viewModel.addSingleBookMarkArticles(id, articlesId)
    }

    private fun fetchUser() {
        if (userData == null) {
            val list: MutableList<String> = mutableListOf()
            for (articles in articlesData) {
                if (articles.createBy !in list)
                    list.add(articles.createBy)
            }
            viewModel.getUser(list)
        }
    }

    private fun checkFavorite() {
        if (articlesData.isNotEmpty()) {
            val list: MutableList<String> = mutableListOf()
            for (articles in articlesData)
                list.add(articles.id)
            viewModel.checkMultiFavorite(id, list)
            viewModel.checkMultiBookMark(id, list)
        }
    }

    @Suppress("DEPRECATION")
    private fun setListener() {
        binding.ibExitArticles.setOnClickListener {
            requireActivity().onContentChanged()
            requireActivity().onBackPressed()
        }

        binding.edtSearchArticle.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = binding.edtSearchArticle.text.toString()
                val strTrim = str.trimStart()

                if (str.isEmpty()) {
                    adapter.updateListArticles(articlesData)
                    return
                }

                if (str.count() != strTrim.count())
                    binding.edtSearchArticle.setText(strTrim)
                else  {
                    lastedSearch = str.trim()
                    onSearch(removeAccent(lastedSearch))
                }
            }

        })


        binding.edtSearchArticle.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.edtSearchArticle.hideKeyboard()
                @Suppress("UNUSED_EXPRESSION")
                true
            }
            false // very important
        }
    }

    private fun onSearch(str: String) {
        val listStr = str.uppercase().split(" ")
        val list = mutableListOf<Articles>()
        for (articles in articlesData) {
            val sortTitle = removeAccent(articles.sortTitle).uppercase()
            for (s in listStr)
                if (sortTitle.startsWith(s)) {
                    list.add(0, articles)
                    break
                }
                else if (sortTitle.contains(s)) {
                    list.add(articles)
                    break
                }
        }
        adapter.updateListArticles(list)
    }
}