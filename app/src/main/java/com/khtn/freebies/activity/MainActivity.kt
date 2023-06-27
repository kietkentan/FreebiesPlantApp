package com.khtn.freebies.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.khtn.freebies.R
import com.khtn.freebies.databinding.ActivityMainBinding
import com.khtn.freebies.helper.AppConstant
import com.khtn.freebies.helper.ImageOptions
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.isValidDestination
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.helper.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val uriList = arrayListOf<String>()

    var statusBar = 0
    private var doubleBackPress: Boolean = false
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getStatusBarHeight(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()
        transparentStatusBar(true)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.isValidDestination(R.id.homeFragment) ||
                    navController.isValidDestination(R.id.loginFragment)) {
                    if (doubleBackPress) finishAffinity()
                    doubleBackPress = true
                    toast(getString(R.string.press_back_exit))
                    startTimer()
                }
                else
                    navController.popBackStack()
            }
        })

        initView()
        clickView()
    }

    @Suppress("DEPRECATION")
    private fun initView() {
        binding.mainLayout.setPadding(0, -statusBar, 0, 0)
        try {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                onDestinationChanged(destination.id)
            }
            binding.bottomNavigation.setOnNavigationItemSelectedListener(onBottomNavigationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickView() {
        binding.btnOpenGallery.setOnClickListener {
            ImageUtils.askPermission(this, ImageOptions.CHOSE_GALLERY)
        }
    }

    private fun startTimer() {
        try {
            timer = object : CountDownTimer(2000L, 1000L) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    doubleBackPress = false
                }
            }
            timer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    private fun getStatusBarHeight(context: Context) {
        val res: Resources = context.resources
        val resourceId: Int = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBar = res.getDimensionPixelSize(resourceId)
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("TAG_U", "onRawActivityResult: $data")
        if (requestCode == ImageUtils.FROM_GALLERY_MAIN && resultCode == RESULT_OK) {
            uriList.clear()
            if (data?.clipData != null) {
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val tempUri = data.clipData!!.getItemAt(i).uri.toString()
                    uriList.add(tempUri)
                }
            } else if (data?.data != null) {
                val tempUri = data.data.toString()
                uriList.add(tempUri)
            }

            if (uriList.size > 0) {
                if (navController.isValidDestination(R.id.homeFragment)) {
                    navController.navigate(R.id.action_homeFragment_to_addingNewPlantFragment,
                        Bundle().apply {
                            putStringArrayList(AppConstant.LIST_IMAGE, uriList)
                        })
                } else if (navController.isValidDestination(R.id.profileFragment)) {
                    navController.navigate(R.id.action_profileFragment_to_addingNewPlantFragment,
                        Bundle().apply {
                            putStringArrayList(AppConstant.LIST_IMAGE, uriList)
                        })
                }
            }
        } else {
            val navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
            if (navHostFragment != null) {
                val childFragments = navHostFragment.childFragmentManager.fragments
                childFragments.forEach { fragment ->
                    fragment.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun onDestinationChanged(currentDestination: Int) {
        try {
            when (currentDestination) {
                R.id.homeFragment -> {
                    binding.bottomNavigation.selectedItemId = R.id.menu_home
                    showView()
                }

                R.id.profileFragment -> {
                    binding.bottomNavigation.selectedItemId = R.id.menu_profile
                    showView()
                }

                else -> {
                    binding.bottomAppBar.hide()
                    binding.btnOpenGallery.hide()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION")
    private val onBottomNavigationListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    if (isNotSameDestination(R.id.homeFragment))
                        navController.navigate(R.id.homeFragment)

                    true
                }

                R.id.menu_profile -> {
                    if (isNotSameDestination(R.id.profileFragment))
                        navController.navigate(R.id.profileFragment)

                    true
                }

                else -> true
            }

        }

    private fun isNotSameDestination(destination: Int): Boolean {
        return destination != navController.currentDestination!!.id
    }

    private fun showView() {
        binding.bottomAppBar.show()
        binding.bottomAppBar.apply {
            if (isScrolledDown) performShow()
        }
        binding.btnOpenGallery.show()
    }

    override fun onContentChanged() {
        super.onContentChanged()
        if (binding.bottomAppBar.isScrolledDown)
            binding.bottomAppBar.performShow()
    }
}