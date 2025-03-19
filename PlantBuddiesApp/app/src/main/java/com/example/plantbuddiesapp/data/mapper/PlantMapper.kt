package com.example.plantbuddiesapp.data.mapper


import com.example.plantbuddiesapp.data.dto.PlantDto
import com.example.plantbuddiesapp.data.dto.UserDto
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.model.User

fun PlantDto.toDomain(): Plant {
    return Plant(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        description = description,
        family = family,
        genus = genus,
        dimensions = dimensions,
        leaf = leaf,
        flowers = flowers,
        fruits = fruits,
        seeds = seeds,
        watering = watering,
        sunlight = sunlight,
        careLevel = careLevel,
        growthRate = growthRate,
        indoor = indoor,
        prunningMonth = prunningMonth,
        harvestSeason = harvestSeason,
        poisonous = poisonous,
        careGuides = careGuides,
        imageUri = imageUrl
    )
}
fun Plant.toDto(confidence: Float? = null): PlantDto {
    return PlantDto(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        description = description,
        family = family,
        genus = genus,
        dimensions = dimensions,
        leaf = leaf,
        flowers = flowers,
        fruits = fruits,
        seeds = seeds,
        watering = watering,
        sunlight = sunlight,
        careLevel = careLevel,
        growthRate = growthRate,
        indoor = indoor,
        prunningMonth = prunningMonth,
        harvestSeason = harvestSeason,
        poisonous = poisonous,
        careGuides = careGuides,
        imageUrl = imageUri,
        confidence = confidence
    )
}
fun Plant.toRequestDto(): PlantDto {
    return toDto().copy(id = null)
}

