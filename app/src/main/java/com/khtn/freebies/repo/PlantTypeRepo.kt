package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.PlantType

interface PlantTypeRepo {
    fun getPlantType(result: (UiState<List<PlantType>>) -> Unit)
}