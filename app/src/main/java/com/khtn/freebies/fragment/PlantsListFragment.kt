package com.khtn.freebies.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import com.khtn.freebies.helper.*
import com.khtn.freebies.module.Plant
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
    private lateinit var plantData: MutableList<Plant>
    private var lastedSearch: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantsListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oberver()

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        @Suppress("DEPRECATION")
        objSpecies = arguments?.getParcelable(AppConstant.SPECIES)

        binding.recPlant.layoutManager = linearLayoutManager
        binding.recPlant.adapter = adapter

        objSpecies?.let {
            updateUI(it)
            viewModel.getPlansForSpecie(it)
        }
        setListener()
    }

    private fun oberver(){
        viewModel.plans.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.recPlant.hide()
                    binding.shimmerPlantList.show()
                    binding.shimmerPlantList.startShimmer()
                }

                is UiState.Failure -> requireContext().toast(state.error)

                is UiState.Success -> {
                    binding.shimmerPlantList.stopShimmer()
                    binding.shimmerPlantList.hide()
                    binding.recPlant.show()
                    plantData = state.data.toMutableList()
                    if (lastedSearch.isNullOrEmpty())
                        adapter.updateList(plantData)
                }
            }
        }
    }

    private fun updateUI(species: Species) {
        binding.tvNameSpecies.text = species.name
        binding.edtSearchPlant.hint = "${getText(R.string.search_for)} ${species.name}"
    }

    private fun onSearch(str: String) {
        val listStr = str.uppercase().split(" ")
        val list = mutableListOf<Plant>()
        for (plant in plantData) {
            val name = removeAccent(plant.name).uppercase()
            for (s in listStr)
                if (name.startsWith(s)) {
                    list.add(0, plant)
                    break
                }
                else if (name.contains(s)) {
                    list.add(plant)
                    break
                }
        }
        adapter.updateList(list)
    }

    private fun setListener() {
        binding.ibExitPlants.setOnClickListener {
            requireActivity().onContentChanged()
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }

        binding.edtSearchPlant.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = binding.edtSearchPlant.text.toString()
                val strTrim = str.trimStart()

                if (str.isEmpty()) {
                    adapter.updateList(plantData)
                    return
                }

                if (str.count() != strTrim.count())
                    binding.edtSearchPlant.setText(strTrim)
                else  {
                    lastedSearch = str.trim()
                    onSearch(removeAccent(lastedSearch))
                }
            }

        })


        binding.edtSearchPlant.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.edtSearchPlant.hideKeyboard()
                @Suppress("UNUSED_EXPRESSION")
                true
            }
            false // very important
        }
    }
}