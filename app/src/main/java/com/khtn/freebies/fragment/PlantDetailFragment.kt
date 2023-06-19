package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ImageListAdapter
import com.khtn.freebies.databinding.FragmentPlantDetailBinding
import com.khtn.freebies.helper.*
import com.khtn.freebies.module.Plant
import com.khtn.freebies.viewmodel.PlantViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlantDetailBinding
    private val viewModel: PlantViewModel by viewModels()
    private var objPlant: Plant? = null
    private var imageUris: MutableList<String> = arrayListOf()
    private var isFavorite: Boolean = false
    private var id: String = ""
    private val adapterImage by lazy {
        ImageListAdapter(
            onItemClicked = { uri ->
                ImageUtils.loadImage(binding.ivItemReview, uri)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        updateUI()
    }

    private fun observe() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { state ->
            when(state){
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
                    binding.ivFavorite.setBackgroundColor(resources.getColor(if (state.data) R.color.liked else R.color.not_like))
                    binding.ivFavorite.show()
                    isFavorite = state.data
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateUI() {
        objPlant = arguments?.getParcelable(AppConstant.PLANT)
        binding.recListReview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recListReview.adapter = adapterImage

        binding.ivSeeMore.alpha = 0.8F

        objPlant?.let { it ->
            binding.detail = it
            setupImageReview(it.images)

            viewModel.getSession {
                it?.let {
                    viewModel.checkFavoritePlant(it.id, objPlant!!.id)
                    id = it.id
                }
            }
        }

        binding.ibExitPlantDetail.setOnClickListener {
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
                        manager.scrollToPositionWithOffset(imageUris.size - 1, 0)
                    }
                }
            }
        })
    }

    private fun handleFavoriteUpdate() {
        if (isFavorite)
            viewModel.removeFavoritePlant(id, objPlant!!.id)
        else
            viewModel.addFavoritePlant(id, objPlant!!.id)
    }

    private fun setupImageReview(list: List<String>) {
        imageUris = list.map { it }.toMutableList()
        imageUris.reverse()
        adapterImage.updateList(imageUris)
        imageUris[0].let {
            ImageUtils.loadImage(binding.ivItemReview, it)
        }
        binding.ivSeeMore.visibility = if (imageUris.size > 1) View.VISIBLE else View.GONE
    }
}