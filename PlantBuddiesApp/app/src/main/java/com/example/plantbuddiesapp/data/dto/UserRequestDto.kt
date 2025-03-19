package com.example.plantbuddiesapp.data.dto

data class UserDto(
    val uid: String,
    val email: String,
    val name: String,
    val country: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val plants: List<PlantDto> = emptyList()
)