package com.example.plantbuddiesapp.domain.model

data class Plant(
    val scientificName: String? = null,
    val commonName: String,
    val id: String? = null,
    val imageUri: String? = null,
    val description: String? = null,
    val family: String? = null,
    val genus: String? = null,
    val dimensions: String? = null,
    val leaf: Boolean? = null,
    val flowers: Boolean? = null,
    val fruits: Boolean? = null,
    val seeds: Boolean? = null,
    val watering: String? = null,
    val sunlight: String? = null,
    val careLevel: String? = null,
    val growthRate: String? = null,
    val indoor: Boolean? = null,
    val prunningMonth: String? = null,
    val harvestSeason: String? = null,
    val poisonous: Boolean? = null,
    val careGuides: List<String> = emptyList()
)