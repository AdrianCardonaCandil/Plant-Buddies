package com.example.plantbuddiesapp.data.repository
import android.content.Context
import android.net.Uri
import com.example.plantbuddiesapp.data.dto.SavePlantRequestDto
import com.example.plantbuddiesapp.data.mapper.toDomain
import com.example.plantbuddiesapp.data.services.PlantService
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val context: Context,
    private val plantService: PlantService
) : PlantRepository {

    override suspend fun identifyPlant(imageUri: Uri): Result<Plant> {
        return try {
            val file = convertUriToFile(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = plantService.identifyPlant(imagePart)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain(imageUri.toString()))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to identify plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun savePlant(plant: Plant): Result<Plant> {
        return try {
            val plantRequest = SavePlantRequestDto(
                scientificName = plant.scientificName,
                commonName = plant.commonName,
                imageUrl = plant.imageUri ?: "",
                description = plant.description,
                careLevel = plant.careLevel,
                waterNeeds = plant.waterNeeds,
                sunlightNeeds = plant.sunlightNeeds,
                careTips = plant.careTips
            )

            val response = plantService.savePlant(plantRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to save plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPlants(): Flow<List<Plant>> = flow {
        try {
            val response = plantService.getUserPlants()
            if (response.isSuccessful) {
                val plants = response.body()?.map { it.toDomain() } ?: emptyList()
                emit(plants)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun deletePlant(plantId: String): Result<Unit> {
        return try {
            val response = plantService.deletePlant(plantId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlant(plant: Plant): Result<Plant> {
        return try {
            val plantRequest = SavePlantRequestDto(
                scientificName = plant.scientificName,
                commonName = plant.commonName,
                imageUrl = plant.imageUri ?: "",
                description = plant.description,
                careLevel = plant.careLevel,
                waterNeeds = plant.waterNeeds,
                sunlightNeeds = plant.sunlightNeeds,
                careTips = plant.careTips
            )

            val response = plantService.updatePlant(plant.id!!, plantRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchPlants(query: String, filters: Set<String>): Flow<List<Plant>> = flow {
        try {
            val response = plantService.searchPlants(query, filters.toList())
            if (response.isSuccessful) {
                val plants = response.body()?.map { it.toDomain() } ?: emptyList()
                emit(plants)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    private fun convertUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "plant_image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}