package com.example.plantbuddiesapp.data.dto

import com.google.gson.annotations.SerializedName

data class PlantSearchRequest(
    @SerializedName("commonName") val commonName: String? = null,
    @SerializedName("sunlight") val sunlight: String? = null,
    @SerializedName("watering") val watering: String? = null,
    @SerializedName("indoor") val indoor: Boolean? = null,
    @SerializedName("careLevel") val careLevel: String? = null,
)