package com.khtn.freebies.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Photography
import com.khtn.freebies.module.PlantType

class PlantTypeRepoImp(
    private val database: FirebaseFirestore
): PlantTypeRepo {
    override fun getPlantType(result: (UiState<List<PlantType>>) -> Unit) {
        database.collection(FireStoreCollection.PLANT_TYPE)
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
        database.collection(FireStoreCollection.PHOTOGRAPHY)
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