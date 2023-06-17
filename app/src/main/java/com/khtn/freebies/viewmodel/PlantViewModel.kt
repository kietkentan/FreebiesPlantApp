package com.khtn.freebies.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species
import com.khtn.freebies.module.User
import com.khtn.freebies.module.UserAccountSetting
import com.khtn.freebies.repo.PlantRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val plantRepo: PlantRepo
): ViewModel() {
    private val _plants = MutableLiveData<UiState<MutableList<Plant>>>()
    val plans: LiveData<UiState<MutableList<Plant>>>
        get() = _plants

    private val _isFavorite = MutableLiveData<UiState<Boolean>>()
    val isFavorite: LiveData<UiState<Boolean>>
        get() = _isFavorite

    fun getSession(result: (User?) -> Unit){
        plantRepo.getSession(result)
    }

    fun getPlansForSpecie(species: Species) {
        _plants.value = UiState.Loading
        plantRepo.getPlantsForSpecie(species) {
            _plants.value = it
        }
    }

    fun checkFavoritePlant(
        id: String,
        plantId: String
    ) {
        _isFavorite.value = UiState.Loading
        plantRepo.checkFavoritePlant(
            id = id,
            plantId = plantId
        ) {
            _isFavorite.value = it
        }
    }

    fun addFavoritePlant(
        id: String,
        plantId: String
    ) {
        plantRepo.addFavoritePlant(
            id = id,
            plantId = plantId
        ) {
            if (it is UiState.Success)
                _isFavorite.value = UiState.Success(true)
        }
    }

    fun removeFavoritePlant(
        id: String,
        plantId: String
    ) {
        plantRepo.removeSingleFavoritePlant(
            id = id,
            plantId = plantId
        ) {
            if (it is UiState.Success)
                _isFavorite.value = UiState.Success(false)
        }
    }
}