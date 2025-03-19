package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.AuthRequestDto
import com.example.plantbuddiesapp.data.dto.AuthResponseDto
import com.example.plantbuddiesapp.data.dto.LoginRequestDto
import com.example.plantbuddiesapp.data.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {

    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDto
    ): Response<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequestDto
    ): Response<AuthResponseDto>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>
}