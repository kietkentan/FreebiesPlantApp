package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.repo.AccountSettingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val accountSettingRepo: AccountSettingRepo
): ViewModel() {

    private val _getSetting = MutableLiveData<UiState<String>>()
    val getSetting: LiveData<UiState<String>>
        get() = _getSetting

    private val _updateSetting = MutableLiveData<UiState<String>>()
    val updateSetting: LiveData<UiState<String>>
        get() = _updateSetting


    fun getSetting(id: String) {
        _getSetting.value = UiState.Loading
        accountSettingRepo.getAccountSetting(id) {
            _getSetting.value = it
        }
    }

    fun updateSetting(
        id: String,
        accountSetting: UserAccountSetting
    ) {
        _updateSetting.value = UiState.Loading
        accountSettingRepo.updateAccountSetting(
            id,
            accountSetting
        ) {
            _updateSetting.value = it
        }
    }

    fun getSettingSession(result: (UserAccountSetting?) -> Unit) {
        accountSettingRepo.getSettingSession(result)
    }
}