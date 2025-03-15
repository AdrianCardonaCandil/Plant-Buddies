package com.example.plantbuddiesapp.domain.usecase

import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(): Flow<List<Plant>> {
        return plantRepository.getUserPlants()
    }
}
