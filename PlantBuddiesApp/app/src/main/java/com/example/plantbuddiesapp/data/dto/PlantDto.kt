package com.example.plantbuddiesapp.data.dto

import com.google.gson.annotations.SerializedName

data class PlantDto(
    val id: Int? = null,
    val scientificName: String? = null,
    val commonName: String? = null,
    val description: String? = null,
    val family: String? = null,
    val genus: String? = null,
    val dimensions: List<DimensionDto>? = null,
    val watering: String? = null,
    val sunlight: String? = null,
    val prunningMonth: String? = null,
    val seeds: Boolean? = null,
    val growthRate: String? = null,
    val indoor: Boolean? = null,
    val careLevel: String? = null,
    val flowers: Boolean? = null,
    val fruits: Boolean? = null,
    val harvestSeason: List<String>? = null,
    val leaf: Boolean? = null,
    val poisonous: Boolean? = null,
    @SerializedName("image")
    val imageUrl: String? = null,
    val careGuides: CareGuidesDto? = null,
    val confidence: Float? = null
)
data class DimensionDto(
    val max_value: Float? = null,
    val min_value: Float? = null,
    val type: String? = null,
    val unit: String? = null
)

data class CareGuidesDto(
    val watering: String? = null,
    val sunlight: String? = null,
    val pruning: String? = null
)