package com.khtn.freebies.repo

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.khtn.freebies.di.FollowingCollection
import com.khtn.freebies.di.PlantCollection
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant

class PlantLikedRepoImp(
    @FollowingCollection private val followingCollection: CollectionReference,
    @PlantCollection private val plantCollection: CollectionReference
): PlantLikedRepo {
    @Suppress("UNCHECKED_CAST")
    override fun getListPlantLiked(
        id: String,
        result: (UiState<List<Plant>>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                val request = it.data
                if (request != null) {
                    val list = (request[FireStoreCollection.PLANT] ?: listOf<String>()) as List<String>

                    if (list.isNotEmpty())
                        getPlant(list) { state ->
                            result.invoke(state)
                        }
                }
                result.invoke(UiState.Success(listOf()))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    private fun getPlant(listId: List<String>, result: (UiState<List<Plant>>) -> Unit) {
        plantCollection
            .whereIn(FireStoreCollection.ID, listId)
            .get()
            .addOnSuccessListener {
                val list: MutableList<Plant> = mutableListOf()
                for (document in it) {
                    val plant: Plant = document.toObject(Plant::class.java)
                    list.add(plant)
                }
                result.invoke(UiState.Success(list))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }
}