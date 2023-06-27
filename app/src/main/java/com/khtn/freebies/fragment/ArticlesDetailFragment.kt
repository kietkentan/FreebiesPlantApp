package com.khtn.freebies.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ImageListAdapter
import com.khtn.freebies.databinding.FragmentArticlesDetailBinding
import com.khtn.freebies.helper.AppConstant
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.Time
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.dpToPx
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.viewmodel.ArticlesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticlesDetailFragment: Fragment() {
    private lateinit var binding: FragmentArticlesDetailBinding
    private val viewModel: ArticlesViewModel by viewModels()
    private var imageUris: MutableList<String> = arrayListOf()
    private var isFavorite: Boolean = false
    private var id: String = ""
    private var articlesId: String = ""
    private val adapterImage by lazy {
        ImageListAdapter(
            onItemClicked = { uri, _ ->
                ImageUtils.loadImage(binding.ivItemReview, uri)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        updateUI()
    }

    private fun observe() {
        viewModel.articles.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    binding.detail = state.data
                    setupImageReview(state.data.images)
                    viewModel.getSingleUser(state.data.createBy)
                    binding.time = Time.formatMilliToTimeDate(state.data.createAt)

                    viewModel.getSession {
                        it?.let {
                            viewModel.checkSingleFavoriteArticles(it.id, articlesId)
                            id = it.id
                            binding.myId = it.id
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.updateSingleFavorite.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.ivFavorite.hide()
                    isFavorite = false
                }

                is UiState.Failure -> {
                    binding.ivFavorite.hide()
                    isFavorite = false
                    requireContext().toast(state.error)
                }

                is UiState.Success -> {
                    @Suppress("DEPRECATION")

                    if (articlesId == state.data.first) {
                        binding.ivFavorite.setBackgroundColor(resources.getColor(if (state.data.second) R.color.liked else R.color.not_like))
                        binding.ivFavorite.show()
                        isFavorite = state.data.second
                    }
                }
            }
        }

        viewModel.getUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {}

                is UiState.Failure -> {}

                is UiState.Success -> {
                    if (state.data != null) {
                        binding.layoutUserArticles.show()
                        binding.user = state.data
                    } else binding.layoutUserArticles.hide()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateUI() {
        articlesId = arguments?.getString(AppConstant.ARTICES) ?: ""
        binding.recListReview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recListReview.adapter = adapterImage

        binding.ivSeeMore.alpha = 0.8F

        if (articlesId.isNotEmpty()) {
            viewModel.getArticles(articlesId)
        }

        binding.ibExitArticlesDetail.setOnClickListener {
            requireActivity().onContentChanged()
            requireActivity().onBackPressed()
        }

        binding.ivSeeMore.setOnClickListener {
            if (binding.recListReview.visibility == View.VISIBLE) {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_open)
                binding.recListReview.hide()
            } else {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_close)
                binding.recListReview.show()
            }
        }

        binding.ivFavorite.setOnClickListener {
            handleFavoriteUpdate()
        }

        binding.recListReview.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val width = binding.recListReview.width
                if (width > 0) {
                    binding.recListReview.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val screen = requireContext().applicationContext.resources.displayMetrics.widthPixels
                    val maxWidth: Int = (screen*0.7 - 65.dpToPx).toInt()
                    if (width > maxWidth) {
                        val layoutParam = binding.recListReview.layoutParams
                        layoutParam.width = maxWidth
                        binding.recListReview.layoutParams = layoutParam
                        val manager: LinearLayoutManager = binding.recListReview.layoutManager as LinearLayoutManager
                        manager.scrollToPositionWithOffset(0, 0)
                    }
                }
            }
        })
    }

    private fun handleFavoriteUpdate() {
        if (isFavorite) viewModel.removeSingleFavoriteArticles(id, articlesId)
        else viewModel.addSingleFavoritePlant(id, articlesId)
    }

    private fun setupImageReview(list: List<String>) {
        imageUris = list.map { it }.toMutableList()
        adapterImage.updateList(imageUris)
        imageUris[0].let {
            ImageUtils.loadImage(binding.ivItemReview, it)
        }
        binding.ivSeeMore.visibility = if (imageUris.size > 1) View.VISIBLE else View.GONE
    }
}