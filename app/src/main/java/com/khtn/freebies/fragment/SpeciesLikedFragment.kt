package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khtn.freebies.databinding.FragmentSpeciesLikedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpeciesLikedFragment : Fragment() {
    private lateinit var binding: FragmentSpeciesLikedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeciesLikedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}