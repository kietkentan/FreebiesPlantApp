package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.PlantType
import com.khtn.freebies.repo.PlantTypeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plantTypeRepo: PlantTypeRepo
): ViewModel(){

    private val _plantType = MutableLiveData<UiState<List<PlantType>>>()
    val plantType: LiveData<UiState<List<PlantType>>>
        get() = _plantType

    fun getPlantType() {
        _plantType.value = UiState.Loading
        plantTypeRepo.getPlantType {
            _plantType.value = it
        }
    }
}