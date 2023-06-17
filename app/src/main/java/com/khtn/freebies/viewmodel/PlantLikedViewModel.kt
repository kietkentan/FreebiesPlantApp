package com.khtn.freebies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.User
import com.khtn.freebies.repo.AuthRepo
import com.khtn.freebies.repo.PlantLikedRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlantLikedViewModel @Inject constructor(
    private val plantLikedRepo: PlantLikedRepo,
    private val authRepo: AuthRepo
): ViewModel() {
    private val _plantLiked = MutableLiveData<UiState<List<Plant>>>()
    val plantLiked: LiveData<UiState<List<Plant>>>
        get() = _plantLiked

    fun getSession(result: (User?) -> Unit){
        authRepo.getSession(result)
    }

    fun getListPlantLiked(id: String) {
        _plantLiked.value = UiState.Loading
        plantLikedRepo.getListPlantLiked(id) {
            _plantLiked.value = it
        }
    }
}