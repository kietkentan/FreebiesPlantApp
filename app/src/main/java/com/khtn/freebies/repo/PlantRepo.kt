package com.khtn.freebies.repo

import android.net.Uri
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species

interface PlantRepo {
    fun getPlantsForSpecie(species: Species, result: (UiState<MutableList<Plant>>) -> Unit)
    fun addPlant(plant: Plant, result: (UiState<Pair<Plant, String>>) -> Unit)
    fun updatePlant(plant: Plant, result: (UiState<String>) -> Unit)
    fun deletePlant(plant: Plant, result: (UiState<String>) -> Unit)
    fun uploadSingleFile(uri: Uri, onResult: (UiState<Uri>) -> Unit)
    fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
}