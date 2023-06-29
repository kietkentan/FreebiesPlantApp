package com.khtn.freebies.service

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.khtn.freebies.di.AvatarStorage
import com.khtn.freebies.di.PlantStorage
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.WorkerConstants
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.repo.AuthRepo
import com.khtn.freebies.repo.PlantRepo
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
    @PlantStorage private val plantStorage: StorageReference,
    private val authRepo: AuthRepo,
    private val plantRepo: PlantRepo
): Worker(appContext, workerParams) {
    private val params = workerParams

    companion object {
        private var listener: OnUploadResponse? = null

        fun setListner(onUploadResponse: OnUploadResponse) {
            listener = onUploadResponse
        }
    }

    override fun doWork(): Result {
        val profileStr = params.inputData.getString(WorkerConstants.PROFILE_DATA) ?: ""
        val strList = params.inputData.getStringArray(WorkerConstants.LIST_IMAGE) ?: arrayOf<String>()
        val plantStr = params.inputData.getString(WorkerConstants.PLANT) ?: ""
        val countDownLatch = CountDownLatch(1)
        val result = arrayOf(Result.failure())

        if (profileStr.isNotEmpty()) {
            val userAccountSetting = Json.decodeFromString<UserAccountSetting>(profileStr)
            val name = System.currentTimeMillis()

            val child = avatarStorage.child(userAccountSetting.id.replace(" ", "")).child("$name.jpg")
            val task: UploadTask = child.putFile(Uri.parse(userAccountSetting.profile_photo))

            task.addOnSuccessListener {
                child.downloadUrl.addOnCompleteListener { taskResult ->
                    val downloadUrl = taskResult.result.toString()
                    updateProfile(userAccountSetting, downloadUrl, result, countDownLatch)
                }.addOnFailureListener { e ->
                    result[0] = Result.failure()
                    countDownLatch.countDown()
                    listener!!.onFailed(e.message.toString())
                }
            }
        } else if (strList.isNotEmpty() && plantStr.isNotEmpty()) {
            val listUriResponse = mutableListOf<String>()
            val plant: Plant = Json.decodeFromString(plantStr)

            for (i in strList.indices) {
                val name = System.currentTimeMillis() + i.hashCode()
                val child = plantStorage.child("$name.jpg")
                val task: UploadTask = child.putFile(Uri.parse(strList[i]))

                task.addOnCompleteListener {
                    child.downloadUrl.addOnCompleteListener { taskResult ->
                        val downloadUrl = taskResult.result.toString()
                        listUriResponse.add(downloadUrl)

                        if (listUriResponse.size == strList.size) {
                            plant.images = listUriResponse
                            addPlant(plant, result, countDownLatch)
                        }
                    }.addOnFailureListener { e ->
                        result[0] = Result.failure()
                        countDownLatch.countDown()
                        listener!!.onFailed(e.message.toString())
                    }
                }
            }
        }
        countDownLatch.await()
        return result[0]
    }

    private fun addPlant(
        plant: Plant,
        result: Array<Result>,
        countDownLatch: CountDownLatch
    ) {
        plantRepo.addPlant(plant = plant) {
            if (it is UiState.Success) {
                result[0] = Result.success()
                countDownLatch.countDown()
                listener!!.onSuccess(it)
            } else if (it is UiState.Failure) {
                result[0] = Result.failure()
                countDownLatch.countDown()
                listener!!.onFailed(it.error.toString())
            }
        }
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
    fun onSuccess(uiState: UiState<Pair<String, Boolean>>)
    fun onFailed(error: String)
}