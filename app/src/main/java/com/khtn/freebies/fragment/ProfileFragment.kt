package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ViewPagerAdapter
import com.khtn.freebies.databinding.FragmentProfileBinding
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.viewmodel.AccountSettingViewModel
import com.khtn.freebies.viewmodel.AuthViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var tabMedicator: TabLayoutMediator
    private val viewModel: AccountSettingViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

        fragmentList.add(ArticlesFragment())
        fragmentList.add(SpeciesPostedFragment())
        fragmentList.add(LikesFragment())

        titleList.add(getString(R.string.articles_uppercase))
        titleList.add(getString(R.string.species_uppercase))
        titleList.add(getString(R.string.likes_uppercase))

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
            it?.id?.let { it1 -> viewModel.getSetting(it1) }
        }
    }

    private fun observe(){
        viewModel.getSetting.observe(viewLifecycleOwner) { state ->
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
                    viewModel.getSettingSession { showInfo(it) }
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