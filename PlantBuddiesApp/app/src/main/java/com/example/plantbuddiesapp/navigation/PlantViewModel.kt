package com.example.plantbuddiesapp.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Plant(val name: String)

class PlantViewModel : ViewModel() {

    private val _plants = mutableStateListOf<Plant>()
    val plants: List<Plant> get() = _plants

    fun addPlant(name: String) {
        if (name.isNotBlank()) {
            _plants.add(Plant(name))
        }
    }

    fun removePlant(plant: Plant) {
        _plants.remove(plant)
    }

}