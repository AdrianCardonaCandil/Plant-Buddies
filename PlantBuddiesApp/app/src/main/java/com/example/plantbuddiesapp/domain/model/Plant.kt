package com.example.plantbuddiesapp.domain.model

data class Plant(
    val id: String? = null,
    val scientificName: String,
    val commonName: String,
    val description: String,
    val waterNeeds: Float,
    val sunlightNeeds: Float,
    val careLevel: String,
    val careTips: List<String>,
    val imageUri: String? = null
)