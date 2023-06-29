package com.khtn.freebies.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.firebase.firestore.CollectionReference
import com.khtn.freebies.R
import com.khtn.freebies.di.FamilyCollection
import com.khtn.freebies.di.KingdomCollection
import com.khtn.freebies.di.SpeciesCollection
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.WorkerConstants
import com.khtn.freebies.helper.toast
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.repo.AuthRepo
import com.khtn.freebies.service.OnUploadResponse
import com.khtn.freebies.service.UploadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AddingNewPlantViewModel @Inject constructor(
    @KingdomCollection private val kingdomCollection: CollectionReference,
    @FamilyCollection private val familyCollection: CollectionReference,
    @SpeciesCollection private val speciesCollection: CollectionReference,
    private val authRepo: AuthRepo
): ViewModel() {
    private val _imageUris = MutableLiveData<MutableList<String>>()
    val imageUris: LiveData<MutableList<String>>
        get() = _imageUris

    private val _chips = MutableLiveData<MutableList<String>>()
    val chips: LiveData<MutableList<String>>
        get() = _chips

    private val _listSpecies = MutableLiveData<MutableList<Pair<String, String>>>()
    val listSpecies: LiveData<MutableList<Pair<String, String>>>
        get() = _listSpecies

    private val _listKingdom = MutableLiveData<List<String>>()
    val listKingdom: LiveData<List<String>>
        get() = _listKingdom

    private val _listFamily = MutableLiveData<List<String>>()
    val listFamily: LiveData<List<String>>
        get() = _listFamily

    private val _errorListImage = MutableLiveData<String>()
    val errorListImage: LiveData<String>
        get() = _errorListImage

    private val _addNewPlant = MutableLiveData<UiState<Pair<String, Boolean>>>()
    val addNewPlant: LiveData<UiState<Pair<String, Boolean>>>
        get() = _addNewPlant

    val namePlant = MutableLiveData<String>()
    val speciesId = MutableLiveData<String>()
    val species = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val kingdom = MutableLiveData<String>()
    val family = MutableLiveData<String>()

    fun updateImageUris(list: MutableList<String>) {
        if (_imageUris.value == null)
            _imageUris.value = list
        else if (list.isNotEmpty())
            _imageUris.value!!.addAll(list)
    }

    fun getImageUris(): List<String> = _imageUris.value!!.toList()

    fun clearImageUris() {
        _imageUris.value!!.clear()
    }

    fun removeAt(position: Int) {
        _imageUris.value!!.removeAt(position)
    }

    fun addChip(str: String) {
        if (_chips.value == null)
            _chips.value = mutableListOf()
        _chips.value!!.add(str)
    }

    fun removeChip(position: Int) {
        if (_chips.value == null)
            return
        _chips.value!!.removeAt(position)
    }

    fun getListSpecies() {
        speciesCollection
            .get()
            .addOnSuccessListener {
                val list: MutableList<Pair<String, String>> = mutableListOf()
                for (document in it) {
                    val name = document.getString(FireStoreCollection.NAME)
                    val id = document.getString(FireStoreCollection.ID)

                    if (!name.isNullOrBlank() && !id.isNullOrBlank())
                        list.add(Pair(id, name))
                }
                list.sortBy { it -> it.second }
                _listSpecies.value = list
            }
    }

    fun getListKingdom() {
        kingdomCollection
            .get()
            .addOnSuccessListener {
                val list: MutableList<String> = mutableListOf()
                for (document in it) {
                    val str = document.getString(FireStoreCollection.NAME)
                    if (!str.isNullOrBlank())
                        list.add(str)
                }
                _listKingdom.value = list
            }
    }

    fun getListFamily() {
        familyCollection
            .get()
            .addOnSuccessListener {
                val list: MutableList<String> = mutableListOf()
                for (document in it) {
                    val str = document.getString(FireStoreCollection.NAME)
                    if (!str.isNullOrBlank())
                        list.add(str)
                }
                _listFamily.value = list
            }
    }

    fun setErrorListImage(error: String) {
        _errorListImage.value = error
    }

    fun uploadPlant(context: Context) {
        val uploadResponse = object : OnUploadResponse {
            override fun onSuccess(userAccountSetting: UserAccountSetting) {}

            override fun onSuccess(uiState: UiState<Pair<String, Boolean>>) {
                _addNewPlant.value = uiState
            }

            override fun onFailed(error: String) {
                _addNewPlant.value = UiState.Failure(error)
            }
        }

        _addNewPlant.value = UiState.Loading
        try {
            val time = System.currentTimeMillis()
            authRepo.getSession {
                val plant = Plant(
                    name = namePlant.value!!,
                    kingdom = kingdom.value,
                    family = family.value,
                    speciesId = speciesId.value!!,
                    description = description.value!!,
                    tags = _chips.value,
                    createAt = time,
                    updateAt = time,
                    createBy = it!!.id
                )

                val plantStr = Json.encodeToString(plant)
                val uris: Array<String> = Array(_imageUris.value!!.size) { i -> _imageUris.value!![i] }

                UploadWorker.setListner(uploadResponse)
                val data = Data.Builder()
                    .putStringArray(WorkerConstants.LIST_IMAGE, uris)
                    .putString(WorkerConstants.PLANT, plantStr)
                    .build()
                val uploadWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<UploadWorker>()
                        .setInputData(data)
                        .build()
                WorkManager.getInstance(context).enqueue(uploadWorkRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}