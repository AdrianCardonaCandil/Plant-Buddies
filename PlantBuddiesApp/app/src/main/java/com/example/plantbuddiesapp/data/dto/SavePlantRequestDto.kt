package com.example.plantbuddiesapp.data.dto

data class SavePlantRequestDto(
    val scientificName: String,
    val commonName: String,
    val imageUrl: String,
    val description: String,
    val careLevel: String,
    val waterNeeds: Float,
    val sunlightNeeds: Float,
    val careTips: List<String>
)