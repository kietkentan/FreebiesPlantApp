package com.khtn.freebies.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ViewPagerAdapter
import com.khtn.freebies.databinding.FragmentProfileBinding
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.forEachChildView
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var tabMedicator: TabLayoutMediator
    private val authViewModel: AuthViewModel by viewModels()

    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()
    private var userAccountSetting: UserAccountSetting? = null
    private var currentItem: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("TAG_U", "onCreateView: ")
        binding = FragmentProfileBinding.inflate(inflater)

        addView()
        clickView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("TAG_U", "onViewCreated: ")
        observe()

        binding.viewPagerOnprofile.adapter = ViewPagerAdapter(
            fragmentList,
            childFragmentManager,
            lifecycle
        )
        binding.viewPagerOnprofile.currentItem = currentItem

        tabMedicator = TabLayoutMediator(
            binding.tabInProfile,
            binding.viewPagerOnprofile
        ) { tab, position ->
            tab.text = titleList[position]
        }

        tabMedicator.attach()

        authViewModel.getSession {
            it?.id?.let { it1 -> authViewModel.getSetting(it1) }
        }

        binding.viewPagerOnprofile.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentItem = position
            }
        })
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data!!.data != null) {
            val uri = ImageUtils.getPhotoUri(data).toString()

            if (userAccountSetting != null && uri.isNotEmpty()) {
                userAccountSetting!!.profile_photo = uri
                authViewModel.uploadToCloud(userAccountSetting!!)
            }
        }
    }

    private fun addView() {
        if (fragmentList.isEmpty()) {
            fragmentList.add(ArticlesFragment())
            fragmentList.add(PlantsLikedFragment())
        }

        if (titleList.isEmpty()) {
            titleList.add(getString(R.string.articles_uppercase))
            titleList.add(getString(R.string.species_uppercase))
        }
    }

    private fun clickView() {
        binding.ivAvatarInProfile.setOnClickListener {
            ImageUtils.askPermission(this, ImageUtils.IN_PROFILE)
        }
    }

    private fun observe(){
        authViewModel.getSetting.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.shimmerProfile.startShimmer()
                    binding.layoutProfile.hide()
                }
                is UiState.Failure -> {
                    binding.shimmerProfile.stopShimmer()
                    binding.shimmerProfile.hide()
                    binding.layoutProfile.show()
                    requireContext().toast(state.error)
                }
                is UiState.Success -> {
                    binding.shimmerProfile.stopShimmer()
                    binding.shimmerProfile.hide()
                    binding.layoutProfile.show()
                    authViewModel.getSettingSession {
                        binding.profile = it
                        userAccountSetting = it
                    }
                }
            }
        }

        authViewModel.uploadProfile.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressProfile.show()
                    this.view?.forEachChildView { it.isEnabled = false }
                }

                is UiState.Failure -> {
                    binding.progressProfile.hide()
                    requireContext().toast(state.error)
                    this.view?.forEachChildView { it.isEnabled = true }
                }

                is UiState.Success -> {
                    binding.progressProfile.hide()
                    this.view?.forEachChildView { it.isEnabled = true }
                }
            }
        }
    }
}