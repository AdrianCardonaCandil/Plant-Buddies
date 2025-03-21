package com.example.plantbuddiesapp.domain.repository

import com.example.plantbuddiesapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, name: String, password: String): Result<String>
    suspend fun login(email: String, password: String): Result<String>
}