package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Species
import com.khtn.freebies.repo.SpeciesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SpecieViewModel @Inject constructor(
    private val speciesRepo: SpeciesRepo
): ViewModel() {
    private val _species = MutableLiveData<UiState<Map<Char, MutableList<Species>>>>()
    val species: LiveData<UiState<Map<Char, MutableList<Species>>>>
        get() = _species

    fun getSpecies() {
        _species.value = UiState.Loading
        speciesRepo.getGenus {
            _species.value = it
        }
    }
}