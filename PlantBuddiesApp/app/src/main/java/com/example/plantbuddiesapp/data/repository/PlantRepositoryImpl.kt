package com.example.plantbuddiesapp.data.repository
import android.content.Context
import android.net.Uri
import com.example.plantbuddiesapp.data.dto.UpdatePlantNameDto
import com.example.plantbuddiesapp.data.mapper.toDomain
import com.example.plantbuddiesapp.data.services.PlantService
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val context: Context,
    private val plantService: PlantService,
    private val tokenManager: TokenManager
) : PlantRepository {

    override suspend fun identifyPlant(imageUri: Uri): Result<Plant> {
        return try {
            val file = convertUriToFile(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = plantService.identifyPlant(imagePart)

            if (response.isSuccessful) {
                response.body()?.let {
                    println("Respuesta recibida del servidor: ${it.message}")
                    val plantDto = it.plant
                    val plant = plantDto.toDomain()
                    Result.success(plant)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to identify plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("Error en identifyPlant: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun savePlant(plantId: String): Result<Plant> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = plantService.savePlant(token, plantId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.plant.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to save plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPlantToFavorites(plantId: String): Result<Plant> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = plantService.addPlantToFavorites(token, plantId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.plant.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to add plant to favorites: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPlants(): Flow<List<Plant>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token == null) {
                emit(emptyList())
                return@flow
            }

            val response = plantService.getUserPlants(token)
            if (response.isSuccessful) {
                val plants = response.body()?.plants?.map { it.toDomain() } ?: emptyList()
                emit(plants)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getUserFavoritePlants(): Flow<List<Plant>> = flow {
        try {
            val token = tokenManager.getToken()
            if (token == null) {
                emit(emptyList())
                return@flow
            }

            val response = plantService.getUserFavoritePlants(token)
            if (response.isSuccessful) {
                val plants = response.body()?.favorites?.map { it.toDomain() } ?: emptyList()
                emit(plants)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getPlant(plantId: String): Result<Plant> {
        return try {
            val response = plantService.getPlant(plantId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.plant.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to get plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePlant(plantId: String): Result<Plant> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = plantService.deletePlant(token, plantId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.plant.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to delete plant: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removePlantFromFavorites(plantId: String): Result<Plant> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = plantService.removePlantFromFavorites(token, plantId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.plant.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to remove plant from favorites: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchPlants(filters: Map<String, Any>): Flow<List<Plant>> = flow {
        try {
            println("Search Filters: $filters")

            // Convert map to JSON string using Gson
            val gson = Gson()
            val jsonBody = gson.toJson(filters)

            // Create request body with proper content type
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val response = plantService.searchPlants(requestBody)

            if (response.isSuccessful) {
                val plantListResponse = response.body()
                if (plantListResponse != null) {
                    val plants = plantListResponse.plants.map { plantDto ->
                        plantDto.toDomain()
                    }
                    emit(plants)
                } else {
                    println("Search returned null response body")
                    emit(emptyList())
                }
            } else {
                println("Search failed with error code: ${response.code()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            println("Search exception: ${e.message}")
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun plantExists(plantId: String): Result<Boolean> {
        return try {
            val response = plantService.getPlant(plantId)
            if (response.isSuccessful) {
                val plant = response.body()
                if (plant != null) {
                    Result.success(true)
                } else {
                    Result.success(false)
                }
            } else {
                Result.failure(Exception("Failed to check if plant exists: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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

    override suspend fun updatePlant(plant: Plant): Result<Plant> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))
            val nameUpdate = UpdatePlantNameDto(commonName = plant.commonName)

            val response = plantService.updatePlantName(token, plant.id, nameUpdate)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.plant.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failure to update plant name: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}