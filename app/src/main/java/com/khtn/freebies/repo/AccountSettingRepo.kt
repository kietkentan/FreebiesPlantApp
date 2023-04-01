package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.UserAccountSetting

interface AccountSettingRepo {
    fun updateAccountSetting(id: String, accountSetting: UserAccountSetting, result: (UiState<String>) -> Unit)
    fun getAccountSetting(id: String, result: (UiState<String>) -> Unit)
    fun storeSetting(id: String, result: (UserAccountSetting?) -> Unit)
    fun getSettingSession(result: (UserAccountSetting?) -> Unit)
}