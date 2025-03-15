package com.example.plantbuddiesapp.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val plants: List<Plant>
)