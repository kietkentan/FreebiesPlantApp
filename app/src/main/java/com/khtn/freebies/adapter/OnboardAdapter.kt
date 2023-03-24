package com.khtn.freebies.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.khtn.freebies.fragment.OnboardFragment
import com.khtn.freebies.module.OnboardItems

class OnboardAdapter(list : List<OnboardItems>, manager: FragmentManager, lifecycle : Lifecycle) : FragmentStateAdapter(manager, lifecycle) {
    private val items = list

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardFragment(items[position])
    }
}