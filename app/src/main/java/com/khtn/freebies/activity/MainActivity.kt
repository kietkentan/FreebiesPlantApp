package com.khtn.freebies.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ViewPagerAdapter
import com.khtn.freebies.databinding.ActivityMainBinding
import com.khtn.freebies.fragment.HomeMainFragment
import com.khtn.freebies.fragment.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentList.add(HomeMainFragment())
        fragmentList.add(ProfileFragment())

        binding.viewPagerOnmain.adapter = ViewPagerAdapter(fragmentList, supportFragmentManager, lifecycle)
        binding.viewPagerOnmain.isUserInputEnabled = false
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> binding.viewPagerOnmain.currentItem = 0

                R.id.menu_profile -> binding.viewPagerOnmain.currentItem = 1
            }

            true
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
        if (binding.bottomAppBar.isScrolledDown)
            binding.bottomAppBar.performShow()
    }

    public fun onChangeX() {}

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.viewPagerOnmain.currentItem == 1) {
            binding.viewPagerOnmain.currentItem = 0
            binding.bottomNavigation.selectedItemId = R.id.menu_home
        } else {
            val navController = Navigation.findNavController(this, R.id.nav_home)
            if (navController.currentDestination?.id == R.id.homeFragment)
                finish()
            else
                navController.popBackStack()
        }
        if (binding.bottomAppBar.isScrolledDown)
            binding.bottomAppBar.performShow()
    }
}