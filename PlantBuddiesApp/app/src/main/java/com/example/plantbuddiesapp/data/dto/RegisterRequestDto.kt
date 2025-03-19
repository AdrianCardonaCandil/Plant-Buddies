package com.example.plantbuddiesapp.data.dto

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val name: String? = null
)

