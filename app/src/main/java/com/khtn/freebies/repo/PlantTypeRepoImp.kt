package com.khtn.freebies.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.khtn.freebies.di.PhotographyCollection
import com.khtn.freebies.di.PlantTypeCollection
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Photography
import com.khtn.freebies.module.PlantType

class PlantTypeRepoImp(
    private val plantTypeCollection: CollectionReference,
    private val photographyCollection: CollectionReference
): PlantTypeRepo {
    override fun getPlantType(result: (UiState<List<PlantType>>) -> Unit) {
        plantTypeCollection
            .get()
            .addOnSuccessListener {
                val types = arrayListOf<PlantType>()
                for (document in it) {
                    val type = document.toObject(PlantType::class.java)
                    types.add(type)
                }
                result.invoke(UiState.Success(types))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun getPhotographyTag(result: (UiState<List<Photography>>) -> Unit) {
        photographyCollection
            .get()
            .addOnSuccessListener {
                val tags = arrayListOf<Photography>()
                for (document in it) {
                    val tag = document.toObject(Photography::class.java)
                    tags.add(tag)
                }
                result.invoke(UiState.Success(tags))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }
}