package com.khtn.freebies.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Species

class SpeciesRepoImp(
    private val database: FirebaseFirestore
): SpeciesRepo {
    override fun getGenus(result: (UiState<Map<Char, MutableList<Species>>>) -> Unit) {
        val genusList: Map<Char, MutableList<Species>> = addMap()

        database.collection(FireStoreCollection.SPECIES)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val genus = document.toObject(Species::class.java)
                    val char: Char = genus.name[0]

                    if (char in 'A'..'Z') genusList[char]!!.add(genus)
                    else genusList['#']!!.add(genus)
                }
                result.invoke(UiState.Success(genusList))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    private fun addMap(): Map<Char, MutableList<Species>> {
        val genusList = hashMapOf<Char, MutableList<Species>>()

        genusList['#'] = mutableListOf()
        for (i in 0..25)
            genusList['A' + i] = mutableListOf()

        return genusList
    }
}