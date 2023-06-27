package com.khtn.freebies.repo

import android.net.Uri
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species
import com.khtn.freebies.module.User

interface PlantRepo {
    fun getSession(result: (User?) -> Unit)
    fun getPlantsForSpecie(species: Species, result: (UiState<MutableList<Plant>>) -> Unit)
    fun getSiglePlant(plantId: String, result: (UiState<Plant>) -> Unit)
    fun getListPlantLiked(id: String, result: (UiState<List<Plant>>) -> Unit)
    fun addPlant(plant: Plant, result: (UiState<Pair<Plant, String>>) -> Unit)
    fun updatePlant(plant: Plant, result: (UiState<String>) -> Unit)
    fun deletePlant(plant: Plant, result: (UiState<String>) -> Unit)
    fun uploadSingleFile(uri: Uri, onResult: (UiState<Uri>) -> Unit)
    fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    fun checkFavoritePlant(id: String, plantId: String, result: (UiState<Boolean>) -> Unit)
    fun addFavoritePlant(id: String, plantId: String, result: (UiState<Boolean>) -> Unit)
    fun removeSingleFavoritePlant(id: String, plantId: String, result: (UiState<Boolean>) -> Unit)
    fun removeAllFavoritePlant(id: String, result: (UiState<String>) -> Unit)
}