package com.khtn.freebies.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    private val list: MutableList<Fragment>,
    manager: FragmentManager,
    lifecycle : Lifecycle
) : FragmentStateAdapter(
    manager,
    lifecycle
) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}