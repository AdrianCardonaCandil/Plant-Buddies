package com.example.plantbuddiesapp.domain.repository

import android.net.Uri
import com.example.plantbuddiesapp.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun identifyPlant(imageUri: Uri): Result<Plant>
    suspend fun savePlant(plant: Plant): Result<Plant>
    suspend fun getUserPlants(): Flow<List<Plant>>
    suspend fun deletePlant(plantId: String): Result<Unit>
    suspend fun updatePlant(plant: Plant): Result<Plant>
    suspend fun searchPlants(query: String, filters: Set<String>):Flow<List<Plant>>

}