package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species
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

    fun getPlansForSpecie(species: Species) {
        _plants.value = UiState.Loading
        plantRepo.getPlantsForSpecie(species) {
            _plants.value = it
        }
    }
}