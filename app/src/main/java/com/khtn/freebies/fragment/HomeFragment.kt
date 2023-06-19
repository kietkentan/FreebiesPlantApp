package com.khtn.freebies.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.khtn.freebies.R
import com.khtn.freebies.adapter.PhotographyAdapter
import com.khtn.freebies.adapter.PlantTypeAdapter
import com.khtn.freebies.databinding.FragmentHomeBinding
import com.khtn.freebies.helper.ImageOptions
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.viewmodel.AuthViewModel
import com.khtn.freebies.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var uri: Uri? = null

    private val plantTypeAdapter by lazy {
        PlantTypeAdapter(
            onItemClicked = {pos, item -> }
        )
    }
    private val photographyAdapter by lazy {
        PhotographyAdapter(
            onItemClicked = {item -> }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        val managerPlantType = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val managerPhotography = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.recPlantType.layoutManager = managerPlantType
        binding.recPlantType.adapter = plantTypeAdapter

        binding.recPhotography.layoutManager = managerPhotography
        binding.recPhotography.adapter = photographyAdapter

        authViewModel.getSession { binding.header = it }
        viewModel.getPlantType()
        viewModel.getPhotographyTag()

        binding.layoutSpecial.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_speciesFragment) }
        binding.layoutIdentify.setOnClickListener { ImageUtils.askPermission(this, ImageOptions.TAKE_PHOTO) }
        binding.layoutArticle.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_articlesListFragment) }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageUtils.TAKE_PHOTO_HOME && resultCode == RESULT_OK) {
            Log.i("TAG_U", "onActivityResult: ${ImageUtils.getPhotoUri(data)}")
        }
    }

    private fun observe() {
        viewModel.plantType.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.shimmerPlantType.startShimmer()
                    binding.shimmerPlantType.show()
                    binding.recPlantType.hide()
                }
                is UiState.Failure -> {
                    binding.shimmerPlantType.stopShimmer()
                    binding.shimmerPlantType.hide()
                    binding.recPlantType.show()
                    requireContext().toast(state.error)
                }
                is UiState.Success -> {
                    binding.shimmerPlantType.stopShimmer()
                    binding.shimmerPlantType.hide()
                    binding.recPlantType.show()
                    plantTypeAdapter.updateList(state.data.toMutableList())
                }
            }
        }

        viewModel.photography.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Loading -> {
                    binding.recPhotography.hide()
                }

                is UiState.Failure -> {
                    binding.recPhotography.hide()
                    requireContext().toast(state.error)
                }

                is UiState.Success -> {
                    binding.recPhotography.show()
                    photographyAdapter.updateList(state.data.toMutableList())
                }
            }
        }
    }
}