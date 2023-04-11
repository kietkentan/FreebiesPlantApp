package com.khtn.freebies.repo

import com.khtn.freebies.helper.UiState
import com.khtn.freebies.module.Species

interface SpeciesRepo {
    fun getSpecies(result: (UiState<Map<Char, MutableList<Species>>>) -> Unit)
}