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
                // 1. Obtener las plantas del usuario
                plantRepository.getUserPlants().collect { plants ->
                    Log.d("MockAuthViewModel", "User plants: ${plants.size}")
                }

                // 2. Añadir una planta al usuario
                val response2 = plantRepository.savePlant("2325") // id de la planta: 2320, 2321, etc
                if (response2.isSuccess) {
                    val plant = response2.getOrNull()
                    Log.d("MockAuthViewModel", "Plant added: $plant")
                } else {
                    Log.e("MockAuthViewModel", "Error adding plant", response2.exceptionOrNull())
                }

                // 3. Eliminar una planta del usuario
                val response3 = plantRepository.deletePlant("2325") // id de la planta: 2320, 2321, etc
                if (response3.isSuccess) {
                    val plant = response3.getOrNull()
                    Log.d("MockAuthViewModel", "Plant deleted: $plant")
                } else {
                    Log.e("MockAuthViewModel", "Error deleting plant", response3.exceptionOrNull())
                }

                // 4. Buscar una planta por id
                val response4 = plantRepository.getPlant("2325") // id de la planta: 2320, 2321, etc
                if (response4.isSuccess) {
                    val plant = response4.getOrNull()
                    Log.d("MockAuthViewModel", "Plant found: $plant")
                } else {
                    Log.e("MockAuthViewModel", "Error finding plant", response4.exceptionOrNull())
                }

                // 5. Comprobar si una planta existe
                val response5 = plantRepository.plantExists("2325")
                if (response5.isSuccess) {
                    val exists = response5.getOrNull()
                    if (exists == true) {
                        Log.d("MockAuthViewModel", "Plant exists in the system.")
                    } else {
                        Log.d("MockAuthViewModel", "Plant does not exist.")
                    }
                } else {
                    Log.e("MockAuthViewModel", "Error checking if plant exists", response5.exceptionOrNull())
                }

                // 6. Intentar añadir una planta no válida
                val response6 = plantRepository.savePlant("9999")
                if (response6.isFailure) {
                    Log.e("MockAuthViewModel", "Error adding plant with invalid ID", response6.exceptionOrNull())
                }

                // 7. Intentar eliminar una planta no existente
                val response7 = plantRepository.deletePlant("9999")
                if (response7.isFailure) {
                    Log.e("MockAuthViewModel", "Error deleting non-existent plant", response7.exceptionOrNull())
                }

                // 8. Intentar añadir una planta duplicada
                val response8 = plantRepository.savePlant("2325")
                if (response8.isFailure) {
                    Log.e("MockAuthViewModel", "Error adding duplicate plant", response8.exceptionOrNull())
                }

                // 9. Actualizar la información de una planta
                val response9 = plantRepository.savePlant("2325")
                if (response9.isSuccess) {
                    Log.d("MockAuthViewModel", "Plant updated successfully.")
                } else {
                    Log.e("MockAuthViewModel", "Error updating plant", response9.exceptionOrNull())
                }

            } else {
                Log.e("MockAuthViewModel", "Error logging in user", result.exceptionOrNull())
            }
            */

        }
    }
}