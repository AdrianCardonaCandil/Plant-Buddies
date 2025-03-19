package com.example.plantbuddiesapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.plantbuddiesapp.data.mapper.toDomain
import com.example.plantbuddiesapp.data.mapper.toDto
import com.example.plantbuddiesapp.data.services.UserService
import com.example.plantbuddiesapp.domain.model.User
import com.example.plantbuddiesapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val context: Context,
    private val userService: UserService,
    private val tokenManager: TokenManager
) : UserRepository {

    private val _favorites = MutableStateFlow<List<String>>(emptyList())
    private var favoritesCache: List<String>? = null

    override suspend fun getUserProfile(): Result<User?> {
        return try {
            val token = tokenManager.getToken() ?: return Result.success(null)

            val response = userService.getUserProfile(token)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                if (response.code() == 401) {
                    // Token inválido
                    tokenManager.clearToken()
                    Result.success(null)
                } else {
                    Result.failure(Exception("Failed to get user profile: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val userDto = user.toDto()
            val response = userService.updateProfile(token, userDto)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(imageUri: Uri): Result<String> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val file = convertUriToFile(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = userService.updateProfileImage(token, imagePart)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.imageUrl ?: "")
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to upload profile image: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavorites(): Result<List<String>> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = userService.getUserFavorites(token)

            if (response.isSuccessful) {
                response.body()?.let { favorites ->
                    favoritesCache = favorites
                    _favorites.value = favorites
                    Result.success(favorites)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to get favorites: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToFavorites(plantId: String): Result<Unit> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = userService.addToFavorites(token, plantId)

            if (response.isSuccessful) {

                favoritesCache?.let {
                    val updatedFavorites = it + plantId
                    favoritesCache = updatedFavorites
                    _favorites.value = updatedFavorites
                } ?: getFavorites()

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to add to favorites: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(plantId: String): Result<Unit> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = userService.removeFromFavorites(token, plantId)

            if (response.isSuccessful) {
                favoritesCache?.let {
                    val updatedFavorites = it - plantId
                    favoritesCache = updatedFavorites
                    _favorites.value = updatedFavorites
                } ?: getFavorites()

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove from favorites: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isPlantInFavorites(plantId: String): Result<Boolean> {
        val favorites = favoritesCache ?: getFavorites().getOrNull() ?: return Result.failure(Exception("Failed to get favorites"))
        return Result.success(favorites.contains(plantId))
    }

    override fun observeFavorites(): Flow<List<String>> = _favorites

    private fun convertUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}