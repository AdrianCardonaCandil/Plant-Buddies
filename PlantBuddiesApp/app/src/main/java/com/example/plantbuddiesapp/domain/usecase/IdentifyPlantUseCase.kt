package com.example.plantbuddiesapp.domain.usecase

import android.net.Uri
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import javax.inject.Inject

class IdentifyPlantUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(imageUri: Uri): Result<Plant> {
        return plantRepository.identifyPlant(imageUri)
    }
}