package com.khtn.freebies.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.PlantTypeAdapter
import com.khtn.freebies.databinding.FragmentHomeBinding
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.User
import com.khtn.freebies.viewmodel.AuthViewModel
import com.khtn.freebies.viewmodel.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val adapter by lazy {
        PlantTypeAdapter(
            onItemClicked = {pos, item -> }
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

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.recPlantType.layoutManager = linearLayoutManager
        binding.recPlantType.adapter = adapter

        authViewModel.getSession {
            setupUser(it)
            viewModel.getPlantType()
        }
    }

    private fun observe(){
        viewModel.plantType.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    //binding.progressBar.show()
                }
                is UiState.Failure -> {
                    //binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    //binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
                else -> {}
            }
        }
    }

    private fun setupUser(user: User?) {
        if (user?.avatar != null) Picasso.get().load(user.avatar).into(binding.ivAvatarInHome)
        binding.tvNameInHome.text = buildString {
            append(getText(R.string.hello).toString())
            append(" ")
            append(user?.name?.split(" ")?.get(0))
            append(",")
        }
    }
}