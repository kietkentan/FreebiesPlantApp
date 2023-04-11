package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Photography
import com.khtn.freebies.module.PlantType

interface PlantTypeRepo {
    fun getPlantType(result: (UiState<List<PlantType>>) -> Unit)
    fun getPhotographyTag(result: (UiState<List<Photography>>) -> Unit)
}