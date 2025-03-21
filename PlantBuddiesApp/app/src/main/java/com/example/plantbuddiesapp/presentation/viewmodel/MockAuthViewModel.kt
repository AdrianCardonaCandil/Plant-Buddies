package com.example.plantbuddiesapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.domain.repository.AuthRepository
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MockAuthViewModel @Inject constructor(
    authRepository: AuthRepository,
    plantRepository: PlantRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            val result = authRepository.login(
                email = "test@example.com",
                password = "password"
            )
            /*
            if (result.isSuccess) {
                // Ejemplo para coger las plantas del usuario
                plantRepository.getUserPlants().collect { plants ->
                    Log.d("MockAuthViewModel", "User plants: ${plants.size}")
                }

                // Ejemplo para añadir una planta al usuario
                val response2 = plantRepository.savePlant("2325") // id de la planta: 2320, 2321, etc
                if (response2.isSuccess) {
                    val plant = response2.getOrNull()
                    Log.d("MockAuthViewModel", "Plant added: $plant")
                } else {
                    Log.e("MockAuthViewModel", "Error adding plant", response2.exceptionOrNull())
                }

                // Ejemplo para eliminar una planta del usuario
                val response3 = plantRepository.deletePlant("2325") // id de la planta: 2320, 2321, etc
                if (response3.isSuccess) {
                    val plant = response3.getOrNull()
                    Log.d("MockAuthViewModel", "Plant deleted: $plant")
                } else {
                    Log.e("MockAuthViewModel", "Error deleting plant", response3.exceptionOrNull())
                }

                // Ejemplo para buscar una planta por id
                val response4 = plantRepository.getPlant("2325") // id de la planta: 2320, 2321, etc
                if (response4.isSuccess) {
                    val plant = response4.getOrNull()
                    Log.d("MockAuthViewModel", "Plant found: $plant")
                } else {
                    Log.e("MockAuthViewModel", "Error finding plant", response4.exceptionOrNull())
                }
            } else {
                Log.e("MockAuthViewModel", "Error logging in user", result.exceptionOrNull())
            }
            */
        }
    }
}