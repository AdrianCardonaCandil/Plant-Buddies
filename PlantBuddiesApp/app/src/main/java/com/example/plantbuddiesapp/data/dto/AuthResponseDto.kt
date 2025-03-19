package com.example.plantbuddiesapp.data.dto


data class AuthResponseDto(
    val token: String,
    val userId: String,
    val email: String,
    val name: String
)