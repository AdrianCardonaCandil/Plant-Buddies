package com.example.plantbuddiesapp.domain.repository

import android.net.Uri
import com.example.plantbuddiesapp.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun identifyPlant(imageUri: Uri): Result<Plant>
    suspend fun savePlant(plantId: String): Result<Plant>
    suspend fun addPlantToFavorites(plantId: String): Result<Plant>
    suspend fun getUserPlants(): Flow<List<Plant>>
    suspend fun getUserFavoritePlants(): Flow<List<Plant>>
    suspend fun getPlant(plantId: String): Result<Plant>
    suspend fun deletePlant(plantId: String): Result<Plant>
    suspend fun removePlantFromFavorites(plantId: String): Result<Plant>
    suspend fun searchPlants(filters: Map<String,Any>):Flow<List<Plant>>
    suspend fun plantExists(plantId: String): Result<Boolean>
}