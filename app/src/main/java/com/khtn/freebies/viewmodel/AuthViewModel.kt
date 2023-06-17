package com.khtn.freebies.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.User
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.module.UserLog
import com.khtn.freebies.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
): ViewModel() {
    // Account
    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    // Setting
    private val _setting = MutableLiveData<UiState<String>>()
    val getSetting: LiveData<UiState<String>>
        get() = _setting

    private val _updateSetting = MutableLiveData<UiState<String>>()
    val updateSetting: LiveData<UiState<String>>
        get() = _updateSetting

    fun register(
        password: String,
        user: User
    ) {
        _register.value = UiState.Loading
        authRepo.registerUser(
            password = password,
            user = user
        ) {
            _register.value = it
        }
    }

    fun login(
        email: String,
        password: String,
        save: Boolean
    ) {
        _login.value = UiState.Loading
        authRepo.loginUser(
            email,
            password,
            save
        ) {
            _login.value = it
        }
    }

    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        authRepo.forgotPassword(email){
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit){
        authRepo.logout(result)
    }

    fun getSession(result: (User?) -> Unit){
        authRepo.getSession(result)
    }

    fun getLoginInfo(result: (UserLog?) -> Unit){
        authRepo.getLoginInfo(result)
    }

    fun getSetting(id: String) {
        _setting.value = UiState.Loading
        authRepo.getAccountSetting(id) {
            _setting.value = it
        }
    }

    fun updateSetting(
        id: String,
        accountSetting: UserAccountSetting
    ) {
        _updateSetting.value = UiState.Loading
        authRepo.updateAccountSetting(
            id,
            accountSetting
        ) {
            _updateSetting.value = it
        }
    }

    fun getSettingSession(result: (UserAccountSetting?) -> Unit) {
        authRepo.getSettingSession(result)
    }
}