package com.example.plantbuddiesapp.data.repository
import android.content.Context
import android.net.Uri
import com.example.plantbuddiesapp.data.mapper.toDomain
import com.example.plantbuddiesapp.data.mapper.toRequestDto
import com.example.plantbuddiesapp.data.services.PlantService
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    override suspend fun searchPlants(filters: Map<String, Any>): Flow<List<Plant>> = flow {
        try {
            println("Search Filters: $filters")
            val stringFilters = filters.mapValues { it.value.toString() }
            val response = plantService.searchPlants(stringFilters)

            if (response.isSuccessful) {
                val plantListResponse = response.body()
                if (plantListResponse != null) {
                    val plants = plantListResponse.plants.map { plantDto ->
                        plantDto.toDomain()
                    }
                    emit(plants)
                } else {
                    emit(emptyList())
                }
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
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