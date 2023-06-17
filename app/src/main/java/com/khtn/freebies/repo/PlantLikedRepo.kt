package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant

interface PlantLikedRepo {
    fun getListPlantLiked(id: String, result: (UiState<List<Plant>>) -> Unit)
}