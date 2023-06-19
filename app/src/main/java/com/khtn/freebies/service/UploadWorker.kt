package com.khtn.freebies.service

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.khtn.freebies.di.AccountSettingCollection
import com.khtn.freebies.di.AvatarStorage
import com.khtn.freebies.di.UserCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.WorkerConstants
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.repo.AuthRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.CountDownLatch

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @AvatarStorage private val avatarStorage: StorageReference,
    private val authRepo: AuthRepo
): Worker(appContext, workerParams) {
    private val params = workerParams

    companion object {
        private var listener: OnUploadResponse? = null

        fun setListner(onUploadResponse: OnUploadResponse) {
            listener = onUploadResponse
        }
    }

    override fun doWork(): Result {
        Log.i("TAG_U", "doWork: ")
        val stringData = params.inputData.getString(WorkerConstants.PROFILE_DATA) ?: ""
        val userAccountSetting = Json.decodeFromString<UserAccountSetting>(stringData)
        val name = System.currentTimeMillis()

        val child = avatarStorage.child(userAccountSetting.id.replace(" ", "")).child("$name.jpg")
        val task: UploadTask = child.putFile(Uri.parse(userAccountSetting.profile_photo))

        val countDownLatch = CountDownLatch(1)
        val result = arrayOf(Result.failure())
        task.addOnSuccessListener {
            child.downloadUrl.addOnCompleteListener { taskResult ->
                val downloadUrl = taskResult.result.toString()
                Log.i("TAG_U", "doWork: $downloadUrl")
                updateProfile(userAccountSetting, downloadUrl, result, countDownLatch)
            }.addOnFailureListener { e ->
                result[0] = Result.failure()
                countDownLatch.countDown()
                listener!!.onFailed(e.message.toString())
            }
        }
        countDownLatch.await()
        return result[0]
    }

    private fun updateProfile(
        userAccountSetting: UserAccountSetting,
        downloadUrl: String,
        result: Array<Result>,
        countDownLatch: CountDownLatch
    ) {
        setUrl(userAccountSetting, downloadUrl)
        authRepo.updateUserInfo(userAccountSetting = userAccountSetting) {
            if (it is UiState.Success) {
                result[0] = Result.success()
                countDownLatch.countDown()
                listener!!.onSuccess(userAccountSetting)
            } else if (it is UiState.Failure) {
                result[0] = Result.failure()
                countDownLatch.countDown()
                listener!!.onFailed(it.error.toString())
            }
        }
    }

    private fun setUrl(userAccountSetting: UserAccountSetting, imgUrl: String) {
        userAccountSetting.profile_photo = imgUrl
    }
}

interface OnUploadResponse {
    fun onSuccess(userAccountSetting: UserAccountSetting)
    fun onFailed(error: String)
}