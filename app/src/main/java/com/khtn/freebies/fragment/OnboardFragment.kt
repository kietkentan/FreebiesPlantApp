package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.khtn.freebies.R
import com.khtn.freebies.databinding.FragmentOnboardBinding
import com.khtn.freebies.module.OnboardItems
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardFragment(items : OnboardItems) : Fragment() {
    private lateinit var binding : FragmentOnboardBinding
    private val item : OnboardItems = items

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardBinding.inflate(inflater)

        binding.ivIntro.setImageResource(item.image)
        binding.tvTitleIntro.text = getText(item.title)
        binding.tvTextIntro.text = getText(item.desc)

        return binding.root
    }
}