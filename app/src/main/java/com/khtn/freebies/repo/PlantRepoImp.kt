package com.khtn.freebies.repo

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.khtn.freebies.helper.FireDatabase
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species

class PlantRepoImp(
    val database: FirebaseDatabase,
    val storageReference: StorageReference
): PlantRepo {
    override fun getPlantsForSpecie(
        species: Species,
        result: (UiState<MutableList<Plant>>) -> Unit
    ) {
        val reference = database.reference.child(FireDatabase.PLANT)
        reference.child(species.id)
            .get()
            .addOnSuccessListener {
                val plans: MutableList<Plant> = arrayListOf()
                for (item in it.children) {
                    val plant = item.getValue(Plant::class.java)
                    plant?.let { it1 -> plans.add(it1) }
                }
                result.invoke(UiState.Success(plans))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun addPlant(plant: Plant, result: (UiState<Pair<Plant, String>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updatePlant(plant: Plant, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deletePlant(plant: Plant, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun uploadSingleFile(uri: Uri, onResult: (UiState<Uri>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit) {
        TODO("Not yet implemented")
    }
}