package com.example.plantbuddiesapp.data.services

import com.example.plantbuddiesapp.data.dto.AuthResponseDto
import com.example.plantbuddiesapp.data.dto.LoginRequestDto
import com.example.plantbuddiesapp.data.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDto
    ): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequestDto
    ): Response<AuthResponseDto>
}