package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species
import com.khtn.freebies.module.User
import com.khtn.freebies.repo.PlantRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val plantRepo: PlantRepo
): ViewModel() {
    private val _plant = MutableLiveData<UiState<Plant>>()
    val plant: LiveData<UiState<Plant>>
        get() = _plant

    private val _plantsList = MutableLiveData<UiState<MutableList<Plant>>>()
    val plansList: LiveData<UiState<MutableList<Plant>>>
        get() = _plantsList

    private val _isFavorite = MutableLiveData<UiState<Boolean>>()
    val isFavorite: LiveData<UiState<Boolean>>
        get() = _isFavorite

    private val _plantLiked = MutableLiveData<UiState<List<Plant>>>()
    val plantLiked: LiveData<UiState<List<Plant>>>
        get() = _plantLiked

    fun getSession(result: (User?) -> Unit){
        plantRepo.getSession(result)
    }

    fun getPlansForSpecie(species: Species) {
        _plantsList.value = UiState.Loading
        plantRepo.getPlantsForSpecie(species) {
            _plantsList.value = it
        }
    }

    fun getPlant(plantId: String) {
        _plant.value = UiState.Loading
        plantRepo.getSiglePlant(plantId) {
            _plant.value = it
        }
    }

    fun getListPlantLiked(id: String) {
        _plantLiked.value = UiState.Loading
        plantRepo.getListPlantLiked(id) {
            _plantLiked.value = it
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