package com.khtn.freebies.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.khtn.freebies.R
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.WorkerConstants
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.User
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.module.UserLog
import com.khtn.freebies.repo.AuthRepo
import com.khtn.freebies.service.OnUploadResponse
import com.khtn.freebies.service.UploadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    private val _uploadProfile = MutableLiveData<UiState<String>>()
    val uploadProfile: LiveData<UiState<String>>
        get() = _uploadProfile

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
            _uploadProfile.value = it
        }
    }

    fun uploadToCloud(
        accountSetting: UserAccountSetting
    ) {
        val uploadResponse = object : OnUploadResponse {
            override fun onSuccess(userAccountSetting: UserAccountSetting) {
                getSetting(userAccountSetting.id)
                context.toast(context.getString(R.string.upload_sucess))
            }

            override fun onFailed(error: String) {
                _uploadProfile.value = UiState.Failure(error)
            }
        }

        _uploadProfile.value = UiState.Loading
        try {
            val setting = Json.encodeToString(accountSetting)
            UploadWorker.setListner(uploadResponse)
            val data = Data.Builder()
                .putString(WorkerConstants.PROFILE_DATA, setting)
                .build()
            val uploadWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<UploadWorker>()
                    .setInputData(data)
                    .build()
            WorkManager.getInstance(context).enqueue(uploadWorkRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSettingSession(result: (UserAccountSetting?) -> Unit) {
        authRepo.getSettingSession(result)
    }
}