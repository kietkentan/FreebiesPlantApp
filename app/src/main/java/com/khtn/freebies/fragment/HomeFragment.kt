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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.khtn.freebies.module.User
import com.khtn.freebies.viewmodel.AuthViewModel
import com.khtn.freebies.viewmodel.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {
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

        authViewModel.getSession { setupUser(it) }
        viewModel.getPlantType()
        viewModel.getPhotographyTag()

        binding.layoutSpecial.setOnClickListener(this@HomeFragment)
        binding.layoutIdentify.setOnClickListener { ImageUtils.askPermission(this, ImageOptions.TAKE_PHOTO) }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageUtils.TAKE_PHOTO && resultCode == RESULT_OK) {
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
                    toast(state.error)
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
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.recPhotography.show()
                    photographyAdapter.updateList(state.data.toMutableList())
                }
            }
        }
    }

    private fun setupUser(user: User?) {
        if (!user?.avatar.isNullOrEmpty())
            Picasso.get().load(user?.avatar).into(binding.ivAvatarInHome)
        binding.tvNameInHome.text = buildString {
            append(getText(R.string.hello).toString())
            append(" ")
            append(user?.name?.split(" ")?.get(0))
            append(",")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_special -> findNavController().navigate(R.id.action_homeFragment_to_speciesFragment)
        }
    }
}