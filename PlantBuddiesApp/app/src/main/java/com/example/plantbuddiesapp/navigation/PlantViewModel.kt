package com.example.plantbuddiesapp.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.plantbuddiesapp.ui.screens.Home.PlantInfo

class PlantViewModel : ViewModel() {
    private val _myPlants = mutableStateListOf<PlantInfo>()
    val myPlants: List<PlantInfo> = _myPlants
    var selectedPlant = mutableStateOf<PlantInfo?>(null)

    fun selectPlant(plant: PlantInfo) { selectedPlant.value = plant }
    
    fun addPlant(plant: PlantInfo) {
        if (!_myPlants.contains(plant)) {
            _myPlants.add(plant)
        }
    }

    fun removePlant(plant: PlantInfo) { _myPlants.remove(plant) }

}
