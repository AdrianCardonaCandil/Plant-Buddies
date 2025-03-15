package com.example.plantbuddiesapp.domain.usecase

import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import javax.inject.Inject

class SavePlantUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(plant: Plant): Result<Plant> {
        return plantRepository.savePlant(plant)
    }
}