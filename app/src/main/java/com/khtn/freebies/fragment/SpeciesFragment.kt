@file:Suppress("DEPRECATION")

package com.khtn.freebies.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khtn.freebies.R
import com.khtn.freebies.adapter.SpeciesAlphabetAdapter
import com.khtn.freebies.databinding.FragmentSpeciesBinding
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.toast
import com.khtn.freebies.viewmodel.SpecieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpeciesFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSpeciesBinding
    private val specieViewModel: SpecieViewModel by viewModels()
    private val adapter by lazy {
        SpeciesAlphabetAdapter(
            onItemClicked = { species ->
                requireActivity().onContentChanged()
                findNavController().navigate(R.id.action_speciesFragment_to_plantsListFragment, Bundle().apply {
                    putParcelable("species", species)
                })
            }
        )
    }

    private var currentPage: Int = 0
    private lateinit var animAlpha: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeciesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setOnClick()

        animAlpha = AnimationUtils.loadAnimation(context, R.anim.alpha)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recSpeciesAlphabet.layoutManager = linearLayoutManager
        binding.recSpeciesAlphabet.adapter = adapter

        specieViewModel.getSpecies()
        binding.Other.setTextColor(resources.getColor(R.color.background_button))
        binding.tvShowAlphabet.alpha = 0.4F
        binding.tvShowAlphabet.startAnimation(animAlpha)

        binding.recSpeciesAlphabet.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pagePosition = (recyclerView.layoutManager as LinearLayoutManager?)!!
                    .findFirstCompletelyVisibleItemPosition()

                if (currentPage != pagePosition) {
                    currentPage = pagePosition
                    changeTextColor(if (pagePosition == 0) "#" else ('A' + pagePosition - 1).toString())
                }
            }
        })

        binding.ibExitSpecies.setOnClickListener(this@SpeciesFragment)
    }

    override fun onResume() {
        super.onResume()
        binding.tvShowAlphabet.startAnimation(animAlpha)
    }

    private fun observe() {
        specieViewModel.species.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                    toast(state.error)
                }
                is UiState.Success -> {
                    adapter.updateList(state.data.toMap())
                }
            }
        }
    }

    private fun setOnClick() {
        var textView: TextView = binding.root.findViewWithTag("#")
        textView.setOnClickListener(this@SpeciesFragment)

        for (i in 0..25) {
            textView = binding.root.findViewWithTag(('A' + i).toString())
            textView.setOnClickListener(this@SpeciesFragment)
        }
    }

    private fun changeTextColor(tag: String) {
        var textView: TextView = binding.root.findViewWithTag("#")
        textView.setTextColor(resources.getColor(R.color.text_hint))

        for (i in 0..25) {
            textView = binding.root.findViewWithTag(('A' + i).toString())
            textView.setTextColor(resources.getColor(R.color.text_hint))
        }

        textView = binding.root.findViewWithTag(tag)
        textView.setTextColor(resources.getColor(R.color.background_button))

        val coordinates = IntArray(2)
        textView.getLocationOnScreen(coordinates)

        onShowAlphabet(textView.tag as String, coordinates[1])
    }

    private fun onShowAlphabet(tag: String, y: Int) {
        binding.tvShowAlphabet.text = tag
        binding.tvShowAlphabet.y = (y - 90).toFloat()
        binding.tvShowAlphabet.startAnimation(animAlpha)
    }

    override fun onClick(v: View?) {
        if (v!!.tag != null) {
            val char = (v.tag as String)[0]

            val manager: LinearLayoutManager = binding.recSpeciesAlphabet.layoutManager as LinearLayoutManager
            manager.scrollToPositionWithOffset(if (char == '#') 0 else (char - 'A' + 1), 0)
            currentPage = if (char == '#') 0 else (char - 'A' + 1)

            changeTextColor(v.tag as String)
            return
        }

        when (v.id) {
            R.id.ib_exit_species -> findNavController().navigate(R.id.action_speciesFragment_to_homeFragment)
        }
    }
}