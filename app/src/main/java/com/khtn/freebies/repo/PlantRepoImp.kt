package com.khtn.freebies.repo

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.gson.Gson
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.SharedPrefConstants
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Plant
import com.khtn.freebies.module.Species
import com.khtn.freebies.module.User

class PlantRepoImp(
    private val plantCollection: CollectionReference,
    private val followerCollection: CollectionReference,
    private val followingCollection: CollectionReference,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
): PlantRepo {
    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (user_str == null)
            result.invoke(null)
        else {
            val user = gson.fromJson(user_str, User::class.java)
            result.invoke(user)
        }
    }

    override fun getPlantsForSpecie(
        species: Species,
        result: (UiState<MutableList<Plant>>) -> Unit
    ) {
        plantCollection
            .whereEqualTo("speciesId", species.id)
            .get()
            .addOnSuccessListener {
                val plants: MutableList<Plant> = arrayListOf()
                for (document in it) {
                    val plant = document.toObject(Plant::class.java)
                    plants.add(plant)
                }
                result.invoke(UiState.Success(plants))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun getSiglePlant(
        plantId: String,
        result: (UiState<Plant>) -> Unit
    ) {
        plantCollection
            .document(plantId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val plant = it.result.toObject(Plant::class.java)!!
                    result.invoke(UiState.Success(plant))
                }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

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

    override fun addPlant(
        plant: Plant,
        result: (UiState<Pair<Plant, String>>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updatePlant(
        plant: Plant,
        result: (UiState<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deletePlant(
        plant: Plant,
        result: (UiState<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun uploadSingleFile(
        uri: Uri,
        onResult: (UiState<Uri>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun uploadMultipleFile(
        fileUri: List<Uri>,
        onResult: (UiState<List<Uri>>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    @Suppress("UNCHECKED_CAST")
    override fun checkFavoritePlant(
        id: String,
        plantId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        followerCollection
            .document(plantId)
            .get()
            .addOnSuccessListener {
                val map = it.data
                if (map != null) {
                    val request = map[FireStoreCollection.FOLLOWER]
                    if (request != null) {
                        val list: List<String> = request as List<String>

                        for (pid in list)
                            if (pid == id) {
                                result.invoke(UiState.Success(true))
                                return@addOnSuccessListener
                            }
                    }
                }
                result.invoke(UiState.Success(false))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun addFavoritePlant(
        id: String,
        plantId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        followerCollection
            .document(plantId)
            .get()
            .addOnSuccessListener {
                var request = it.data
                val listId = mutableListOf<String>()
                listId.add(id)

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.FOLLOWER] ?: mutableListOf<String>()) as MutableList<String>
                    listId.addAll(list)
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = plantId
                }

                request[FireStoreCollection.FOLLOWER] = listId
                followerCollection.document(plantId).set(request)
                    .addOnSuccessListener {
                        addAccountFavorite(id, plantId) { state ->
                            result.invoke(state)
                        }
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun addAccountFavorite(
        id: String,
        plantId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                var request = it.data
                val listPlantId = mutableListOf<String>()
                listPlantId.add(plantId)

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.PLANT] ?: mutableListOf<String>()) as MutableList<String>
                    listPlantId.addAll(list)
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = id
                }

                request[FireStoreCollection.PLANT] = listPlantId
                followingCollection.document(id).set(request)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(true))
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeSingleFavoritePlant(
        id: String,
        plantId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        followerCollection
            .document(plantId)
            .get()
            .addOnSuccessListener {
                var request = it.data
                var listId = mutableListOf<String>()

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.FOLLOWER] ?: mutableListOf<String>()) as MutableList<String>
                    list.remove(id)
                    listId = list
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = plantId
                }

                request[FireStoreCollection.FOLLOWER] = listId
                followerCollection.document(plantId).set(request)
                    .addOnSuccessListener {
                        removeSigleAccountFavorite(id, plantId) { state ->
                            result.invoke(state)
                        }
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun removeSigleAccountFavorite(
        id: String,
        plantId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        followingCollection
            .document(id)
            .get()
            .addOnSuccessListener {
                var request = it.data
                var listPlantId = mutableListOf<String>()

                if (request != null) {
                    val list: MutableList<String> = (request[FireStoreCollection.PLANT] ?: mutableListOf<String>()) as MutableList<String>
                    list.remove(plantId)
                    listPlantId = list
                } else {
                    request = hashMapOf()
                    request[FireStoreCollection.ID] = id
                }

                request[FireStoreCollection.PLANT] = listPlantId
                followingCollection.document(id).set(request)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success(true))
                    }
                    .addOnFailureListener { ex ->
                        result.invoke(UiState.Failure(ex.localizedMessage))
                    }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun removeAllFavoritePlant(id: String, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }
}