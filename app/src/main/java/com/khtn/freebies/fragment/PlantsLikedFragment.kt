package com.khtn.freebies.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.PlantItemAdapter
import com.khtn.freebies.databinding.FragmentPlantLikedBinding
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.deepEqualTo
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.viewmodel.PlantLikedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantsLikedFragment : Fragment() {
    private lateinit var binding: FragmentPlantLikedBinding
    private val viewModel: PlantLikedViewModel by viewModels()

    private val adapter by lazy {
        PlantItemAdapter(
            onItemClick = { plant ->
                findNavController().navigate(R.id.action_profileFragment_to_plantDetailFragment, Bundle().apply {
                    putParcelable("plant", plant)
                })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantLikedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recListPlantLiked.layoutManager = linearLayoutManager
        binding.recListPlantLiked.adapter = adapter
    }

    override fun onResume() {
        Log.i("TAG_U", "onResume: ")
        super.onResume()
        viewModel.getSession {
            it?.id?.let { it1 -> viewModel.getListPlantLiked(it1) }
        }
    }

    private fun observe() {
        viewModel.plantLiked.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.recListPlantLiked.hide()
                    binding.shimmerPlantList.show()
                    binding.shimmerPlantList.startShimmer()
                }

                is UiState.Failure -> requireContext().toast(state.error)

                is UiState.Success -> {
                    binding.shimmerPlantList.stopShimmer()
                    binding.shimmerPlantList.hide()
                    binding.recListPlantLiked.show()
                    if (!adapter.getList().deepEqualTo(state.data))
                        adapter.updateList(state.data.toMutableList())
                }
            }
        }
    }
}