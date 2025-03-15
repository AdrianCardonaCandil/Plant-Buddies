package com.example.plantbuddiesapp.data.mapper


import com.example.plantbuddiesapp.data.dto.PlantResponseDto
import com.example.plantbuddiesapp.data.dto.UserPlantDto
import com.example.plantbuddiesapp.domain.model.Plant

fun PlantResponseDto.toDomain(imageUri: String? = null): Plant {
    return Plant(
        scientificName = scientificName,
        commonName = commonName,
        description = description,
        waterNeeds = waterNeeds,
        sunlightNeeds = sunlightNeeds,
        careLevel = careLevel,
        careTips = careTips,
        imageUri = imageUri
    )
}

fun UserPlantDto.toDomain(): Plant {
    return Plant(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        description = description,
        waterNeeds = waterNeeds,
        sunlightNeeds = sunlightNeeds,
        careLevel = careLevel,
        careTips = careTips,
        imageUri = imageUrl
    )
}

fun Plant.toUserPlantDto(imageUrl: String): UserPlantDto {
    return UserPlantDto(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        imageUrl = imageUrl,
        description = description,
        careLevel = careLevel,
        waterNeeds = waterNeeds,
        sunlightNeeds = sunlightNeeds,
        careTips = careTips
    )
}