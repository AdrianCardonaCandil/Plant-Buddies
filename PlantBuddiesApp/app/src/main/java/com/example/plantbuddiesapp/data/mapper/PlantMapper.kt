package com.example.plantbuddiesapp.data.mapper

import com.example.plantbuddiesapp.data.dto.CareGuidesDto
import com.example.plantbuddiesapp.data.dto.DimensionDto
import com.example.plantbuddiesapp.data.dto.PlantDto
import com.example.plantbuddiesapp.domain.model.Plant

fun PlantDto.toDomain(): Plant {
    println("Convirtiendo DTO a Plant: scientificName=${this.scientificName}, commonName=${this.commonName}")

    val dimensionsStr = dimensions?.let { dims ->
        if (dims.isNotEmpty()) {
            val dim = dims[0]
            "${dim.min_value?.toString() ?: "?"}-${dim.max_value?.toString() ?: "?"} ${dim.unit ?: "units"}"
        } else null
    }


    val careGuidesList = mutableListOf<String>()
    careGuides?.watering?.let { careGuidesList.add("Watering: $it") }
    careGuides?.sunlight?.let { careGuidesList.add("Sunlight: $it") }
    careGuides?.pruning?.let { careGuidesList.add("Pruning: $it") }

    val harvestSeasonStr = harvestSeason?.joinToString(", ")

    return Plant(
        id = id?.toString(),
        scientificName = scientificName,
        commonName = commonName ?: "Unknown Plant",
        description = description,
        family = family,
        genus = genus,
        dimensions = dimensionsStr,
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
        harvestSeason = harvestSeasonStr,
        poisonous = poisonous,
        careGuides = careGuidesList,
        imageUri = imageUrl
    )
}

fun Plant.toDto(confidence: Float? = null): PlantDto {
    val dimensionsList = mutableListOf<DimensionDto>()
    dimensions?.let { dimStr ->
        val parts = dimStr.split(" ")
        if (parts.size >= 2) {
            val rangeValues = parts[0].split("-")
            if (rangeValues.size >= 2) {
                try {
                    val minValue = rangeValues[0].toFloatOrNull()
                    val maxValue = rangeValues[1].toFloatOrNull()
                    val unit = parts[1]

                    dimensionsList.add(
                        DimensionDto(
                            min_value = minValue,
                            max_value = maxValue,
                            type = null,
                            unit = unit
                        )
                    )
                } catch (e: Exception) {
                    println("Error al parsear dimensions: ${e.message}")
                }
            }
        }
    }

    var wateringGuide: String? = null
    var sunlightGuide: String? = null
    var pruningGuide: String? = null

    careGuides.forEach { guide ->
        when {
            guide.startsWith("Watering:") -> wateringGuide = guide.substringAfter("Watering:").trim()
            guide.startsWith("Sunlight:") -> sunlightGuide = guide.substringAfter("Sunlight:").trim()
            guide.startsWith("Pruning:") -> pruningGuide = guide.substringAfter("Pruning:").trim()
        }
    }

    val careGuidesDto = CareGuidesDto(
        watering = wateringGuide,
        sunlight = sunlightGuide,
        pruning = pruningGuide
    )

    val harvestSeasonList = harvestSeason?.split(", ")?.filter { it.isNotEmpty() }

    return PlantDto(
        id = id?.toIntOrNull(),
        scientificName = scientificName,
        commonName = commonName,
        description = description,
        family = family,
        genus = genus,
        dimensions = if (dimensionsList.isEmpty()) null else dimensionsList,
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
        harvestSeason = harvestSeasonList,
        poisonous = poisonous,
        imageUrl = imageUri,
        careGuides = careGuidesDto,
        confidence = confidence
    )
}

fun Plant.toRequestDto(): PlantDto {
    return toDto().copy(id = null)
}

