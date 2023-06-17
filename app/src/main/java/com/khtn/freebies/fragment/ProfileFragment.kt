package com.khtn.freebies.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ViewPagerAdapter
import com.khtn.freebies.databinding.FragmentProfileBinding
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.viewmodel.AuthViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var tabMedicator: TabLayoutMediator
    private val authViewModel: AuthViewModel by viewModels()

    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

        addView()
        clickView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()

        binding.viewPagerOnprofile.adapter = ViewPagerAdapter(
            fragmentList,
            parentFragmentManager,
            lifecycle
        )
        binding.viewPagerOnprofile.currentItem = 1

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
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("TAG_U", "onActivityResult: ")
    }

    private fun addView() {
        if (fragmentList.isEmpty()) {
            fragmentList.add(ArticlesFragment())
            fragmentList.add(PlantLikedFragment())
        }

        if (titleList.isEmpty()) {
            titleList.add(getString(R.string.articles_uppercase))
            titleList.add(getString(R.string.species_uppercase))
        }
    }

    private fun clickView() {
        binding.ivAvatarInProfile.setOnClickListener {
            ImageUtils.askPermission(this)
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
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.shimmerProfile.stopShimmer()
                    binding.shimmerProfile.hide()
                    binding.layoutProfile.show()
                    authViewModel.getSettingSession { showInfo(it) }
                }
            }
        }
    }

    private fun showInfo(userAccountSetting: UserAccountSetting?) {
        if (!userAccountSetting?.profile_photo.isNullOrEmpty())
            Picasso.get().load(userAccountSetting?.profile_photo).into(binding.ivAvatarInProfile)
        binding.tvNameInProfile.text = userAccountSetting?.display_name
        binding.tvLocationInProfile.text = if (userAccountSetting?.location.isNullOrEmpty())
            getText(R.string.unlocated) else
                userAccountSetting?.location
    }
}