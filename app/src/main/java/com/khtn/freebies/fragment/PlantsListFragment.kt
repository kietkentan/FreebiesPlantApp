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
import com.khtn.freebies.adapter.PlantItemAdapter
import com.khtn.freebies.databinding.FragmentPlantsListBinding
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.Species
import com.khtn.freebies.viewmodel.PlantViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantsListFragment : Fragment() {
    private lateinit var binding: FragmentPlantsListBinding
    private val viewModel: PlantViewModel by viewModels()
    private val adapter by lazy {
        PlantItemAdapter(
            onItemClick = { plant ->
                findNavController().navigate(R.id.action_plantsListFragment_to_plantDetailFragment, Bundle().apply {
                    putParcelable("plant", plant)
                })
            }
        )
    }

    private var objSpecies: Species? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantsListBinding.inflate(inflater)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oberver()

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        objSpecies = arguments?.getParcelable("species")

        binding.recPlant.layoutManager = linearLayoutManager
        binding.recPlant.adapter = adapter

        objSpecies?.let {
            updateUI(it)
            viewModel.getPlansForSpecie(it)
        }

        binding.ibExitPlants.setOnClickListener {
            requireActivity().onContentChanged()
            requireActivity().onBackPressed()
        }
    }

    private fun oberver(){
        viewModel.plans.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.recPlant.hide()
                    binding.shimmerPlantList.show()
                    binding.shimmerPlantList.startShimmer()
                }
                is UiState.Failure -> {
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.shimmerPlantList.stopShimmer()
                    binding.shimmerPlantList.hide()
                    binding.recPlant.show()
                    adapter.updateList(state.data.toMutableList())
                }
            }
        }
    }

    private fun updateUI(species: Species) {
        binding.tvNameSpecies.text = species.name
        binding.edtSearchPlant.hint = "${getText(R.string.search_for)} ${species.name}"
    }
}