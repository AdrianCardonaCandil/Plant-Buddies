package com.example.plantbuddiesapp.domain.model

data class User(
    val uid: String,
    val email: String,
    val name: String,
    val country: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val plants: List<Plant> = emptyList()
)