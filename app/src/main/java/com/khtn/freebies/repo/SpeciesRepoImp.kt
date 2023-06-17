package com.khtn.freebies.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.khtn.freebies.di.SpeciesCollection
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Species

class SpeciesRepoImp(
    private val speciesCollection: CollectionReference
): SpeciesRepo {
    override fun getSpecies(result: (UiState<Map<Char, MutableList<Species>>>) -> Unit) {
        val speciesList: Map<Char, MutableList<Species>> = addMap()

        speciesCollection
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val genus = document.toObject(Species::class.java)
                    val char: Char = genus.name[0]

                    if (char in 'A'..'Z') speciesList[char]!!.add(genus)
                    else speciesList['#']!!.add(genus)
                }
                result.invoke(UiState.Success(speciesList))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    private fun addMap(): Map<Char, MutableList<Species>> {
        val speciesList = hashMapOf<Char, MutableList<Species>>()

        speciesList['#'] = mutableListOf()
        for (i in 0..25)
            speciesList['A' + i] = mutableListOf()

        return speciesList
    }
}