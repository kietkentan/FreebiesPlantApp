package com.khtn.freebies.repo

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.SharedPrefConstants
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.UserAccountSetting

class AccountSettingRepoImp(
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
): AccountSettingRepo {
    override fun updateAccountSetting(
        id: String,
        accountSetting: UserAccountSetting,
        result: (UiState<String>) -> Unit
    ) {
        val document = database.collection(FireStoreCollection.ACCOUNT_SETTING).document(id)
        document
            .set(accountSetting)
            .addOnSuccessListener {
                result.invoke(UiState.Success("User has been update successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun getAccountSetting(id: String, result: (UiState<String>) -> Unit) {
        storeSetting(id = id) {
            if (it == null)
                result.invoke(UiState.Failure("Failed to store local setting"))
            else
                result.invoke(UiState.Success("Get setting successfully!"))
        }
    }

    override fun storeSetting(id: String, result: (UserAccountSetting?) -> Unit) {
        database.collection(FireStoreCollection.ACCOUNT_SETTING).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val userSetting = it.result.toObject(UserAccountSetting::class.java)
                    appPreferences.edit().putString(SharedPrefConstants.ACCOUNT_SETTING_SESSION, gson.toJson(userSetting)).apply()
                    result.invoke(userSetting)
                } else
                    result.invoke(null)
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSettingSession(result: (UserAccountSetting?) -> Unit) {
        val setting = appPreferences.getString(SharedPrefConstants.ACCOUNT_SETTING_SESSION,null)
        if (setting == null)
            result.invoke(null)
        else {
            val userSetting = gson.fromJson(setting, UserAccountSetting::class.java)
            result.invoke(userSetting)
        }
    }
}
