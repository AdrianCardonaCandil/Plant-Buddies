package com.example.plantbuddiesapp.domain.repository

import android.net.Uri
import com.example.plantbuddiesapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserProfile(): Result<User?>
    suspend fun updateUserProfile(user: User): Result<User>
    suspend fun uploadProfileImage(imageUri: Uri): Result<String>
    suspend fun getFavorites(): Result<List<String>>
    suspend fun addToFavorites(plantId: String): Result<Unit>
    suspend fun removeFromFavorites(plantId: String): Result<Unit>
    suspend fun isPlantInFavorites(plantId: String): Result<Boolean>
    fun observeFavorites(): Flow<List<String>>
}