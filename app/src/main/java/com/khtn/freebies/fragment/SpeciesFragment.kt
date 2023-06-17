@file:Suppress("DEPRECATION")

package com.khtn.freebies.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import com.khtn.freebies.helper.*
import com.khtn.freebies.module.Species
import com.khtn.freebies.viewmodel.SpecieViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpeciesFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSpeciesBinding
    private val textViewGroup: MutableList<TextView> = arrayListOf()
    private val specieViewModel: SpecieViewModel by viewModels()
    private lateinit var mapData:  Map<Char, MutableList<Species>>
    private lateinit var mapSearch: Map<Char, MutableList<Species>>
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

    private var currentFirstText: Char? = null
    private var currentPage: Int = 0
    private lateinit var animAlpha: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeciesBinding.inflate(inflater)
        addTextViewGroup()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        animAlpha = AnimationUtils.loadAnimation(context, R.anim.alpha)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recSpeciesAlphabet.layoutManager = linearLayoutManager
        binding.recSpeciesAlphabet.adapter = adapter

        specieViewModel.getSpecies()
        onDefault()

        setListener()
    }

    private fun addTextViewGroup() {
        if (textViewGroup.size > 0)
            textViewGroup.clear()
        textViewGroup.add(binding.Other)
        for (i in 0..25)
            textViewGroup.add(binding.root.findViewWithTag(('A' + i).toString()))
    }

    private fun setTextViewOnClick() {
        for (tv in textViewGroup)
            tv.setOnClickListener(this@SpeciesFragment)
    }

    override fun onResume() {
        super.onResume()
        onTextViewClick()
    }


    private fun observe() {
        specieViewModel.species.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {}

                is UiState.Failure -> {
                    toast(state.error)
                }

                is UiState.Success -> {
                    mapData = state.data.toMap()
                    if (currentFirstText == null)
                        adapter.updateList(mapData)
                }
            }
        }
    }

    private fun onDefault() {
        binding.Other.setTextColor(resources.getColor(R.color.background_button))
        binding.tvShowAlphabet.alpha = 0.4F
        binding.tvShowAlphabet.show()
        binding.tvShowAlphabet.y = binding.Other.y - 10
        binding.tvShowAlphabet.startAnimation(animAlpha)
    }

    private fun onSearch(tag: Char?) {
        tag?.let { it ->
            mapSearch = hashMapOf(it to mapData[it]!!)
            adapter.updateList(mapSearch)
            currentPage = if (tag == '#') 0 else it - 'A' + 1
            hideAlphabet()
            changeDataSearch(removeAccent(binding.edtSearchSpecies.text.toString().uppercase()))
        } ?: run {
            adapter.updateList(mapData)
            showAlphabet()
        }
        changeTextColor()
    }

    private fun changeDataSearch(str: String) {
        mapSearch = hashMapOf(str[0] to mapData[str[0]]!!)
        val listStr = str.split(" ")

        val mapStr = hashMapOf<Char, MutableList<Species>>()
        val list = arrayListOf<Species>()

        for (species in mapSearch[str[0]]!!){
            for (s in listStr) {
                if (s.isNotEmpty() && species.name.uppercase().contains(s)) {
                    list.add(species)
                    break
                }
            }
        }
        mapStr[str[0]] = list
        adapter.updateList(mapStr)
    }

    private fun hideAlphabet() {
        for (tv in textViewGroup)
            tv.hide()

        textViewGroup[currentPage].show()
    }

    private fun changeTextColor() {
        for (i in 0 until textViewGroup.size)
            textViewGroup[i].setTextColor(resources.getColor(if (i == currentPage) R.color.background_button else R.color.text_hint))

        showPositionCharacter()
    }

    private fun showAlphabet() {
        for (tv in textViewGroup)
            tv.show()
    }

    private fun showPositionCharacter() {
        binding.tvShowAlphabet.text = if (currentPage == 0) "#" else ('A' + currentPage - 1).toString()
        binding.tvShowAlphabet.y = textViewGroup[currentPage].y - 10
        binding.tvShowAlphabet.startAnimation(animAlpha)
    }

    private fun changePosition() {
        val pagePosition = (binding.recSpeciesAlphabet.layoutManager as LinearLayoutManager?)!!
            .findFirstCompletelyVisibleItemPosition()
        if (pagePosition in 0 until textViewGroup.size) {
            if (currentPage != pagePosition) {
                currentPage = pagePosition
                changeTextColor()
            }
        }
    }

    private fun onTextViewClick() {
        val manager: LinearLayoutManager = binding.recSpeciesAlphabet.layoutManager as LinearLayoutManager
        manager.scrollToPositionWithOffset(currentPage, 0)

        changeTextColor()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        binding.recSpeciesAlphabet.setOnTouchListener { _, _ ->
            if (binding.edtSearchSpecies.keyboardIsVisible) {
                binding.edtSearchSpecies.hideKeyboard()
                true
            } else false
        }

        binding.recSpeciesAlphabet.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (currentFirstText == null)
                    changePosition()
            }
        })

        setTextViewOnClick()
        binding.ibExitSpecies.setOnClickListener(this@SpeciesFragment)

        binding.edtSearchSpecies.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = binding.edtSearchSpecies.text.toString()
                val strTrim = str.trimStart()
                val countText = str.count()

                if (str.count() != strTrim.count())
                    binding.edtSearchSpecies.setText(strTrim)
                else  {
                    val char = if (countText == 0) null else removeAccent(binding.edtSearchSpecies.text[0]).uppercaseChar()
                    currentFirstText = if (char == null) null else if (char in 'A'..'Z') char else '#'
                    onSearch(currentFirstText)
                }
            }
        })

        binding.edtSearchSpecies.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.edtSearchSpecies.hideKeyboard()
                @Suppress("UNUSED_EXPRESSION")
                true
            }
            false // very important
        }
    }

    override fun onClick(v: View?) {
        if (v!!.tag != null) {
            currentPage = if (v.tag == "#") 0 else v.tag.toString()[0] - 'A' + 1
            onTextViewClick()
            return
        }

        when (v.id) {
            R.id.ib_exit_species -> findNavController().navigate(R.id.action_speciesFragment_to_homeFragment)
        }
    }
}