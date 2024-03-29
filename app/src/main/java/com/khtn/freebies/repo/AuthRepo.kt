package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.User
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.module.UserLog

interface AuthRepo {
    // Account
    fun registerUser(password: String, user: User, result: (UiState<String>) -> Unit)
    fun createUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(userAccountSetting: UserAccountSetting, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, save: Boolean, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
    fun getLoginInfo(result: (UserLog?) -> Unit)

    // Setting
    fun updateAccountSetting(accountSetting: UserAccountSetting, result: (UiState<String>) -> Unit)
    fun getAccountSetting(id: String, result: (UiState<String>) -> Unit)
    fun storeSetting(id: String, result: (UserAccountSetting?) -> Unit)
    fun getSettingSession(result: (UserAccountSetting?) -> Unit)
}