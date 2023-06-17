package com.khtn.freebies.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khtn.freebies.R
import com.khtn.freebies.databinding.FragmentAddPlantBinding

class AddPlantFragment : Fragment() {
    private lateinit var binding: FragmentAddPlantBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlantBinding.inflate(inflater)
        return binding.root
    }
}