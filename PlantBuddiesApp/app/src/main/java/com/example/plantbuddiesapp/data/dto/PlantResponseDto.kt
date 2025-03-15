package com.example.plantbuddiesapp.data.dto

data class PlantResponseDto(
    val scientificName: String,
    val commonName: String,
    val description: String,
    val waterNeeds: Float,
    val sunlightNeeds: Float,
    val careLevel: String,
    val careTips: List<String>,
    val confidence: Float
)
