package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khtn.freebies.databinding.FragmentHomeMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeMainFragment: Fragment() {
    private lateinit var binding: FragmentHomeMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMainBinding.inflate(inflater)
        return binding.root
    }
}