package com.example.plantbuddiesapp.domain.repository

import com.example.plantbuddiesapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signIn(email: String, password: String): Result<String>
    suspend fun signUp(email: String, password: String, name: String): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun updateUserProfile(user: User): Result<User>
    suspend fun uploadProfileImage(imageUri: android.net.Uri): Result<String>
    fun getAuthState(): Flow<Boolean>
    suspend fun getToken(): String?
}