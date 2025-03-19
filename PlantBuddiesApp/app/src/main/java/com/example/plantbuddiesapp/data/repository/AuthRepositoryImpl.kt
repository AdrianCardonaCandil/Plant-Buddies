package com.example.plantbuddiesapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.plantbuddiesapp.data.dto.LoginRequestDto
import com.example.plantbuddiesapp.data.dto.RegisterRequestDto
import com.example.plantbuddiesapp.data.mapper.toDomain
import com.example.plantbuddiesapp.data.mapper.toProfileUpdateDto
import com.example.plantbuddiesapp.data.services.AuthService
import com.example.plantbuddiesapp.data.services.UserService
import com.example.plantbuddiesapp.domain.model.User
import com.example.plantbuddiesapp.domain.repository.AuthRepository
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
class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val authService: AuthService,
    private val userService: UserService,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val _authState = MutableStateFlow(tokenManager.getToken() != null)

    override suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val loginRequest = LoginRequestDto(email = email, password = password)
            val response = authService.login(loginRequest)

            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    tokenManager.saveToken(authResponse.token)
                    _authState.value = true
                    Result.success(authResponse.userId)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<String> {
        return try {
            val registerRequest = RegisterRequestDto(
                email = email,
                password = password,
                name = name
            )
            val response = authService.register(registerRequest)

            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    tokenManager.saveToken(authResponse.token)
                    _authState.value = true
                    Result.success(authResponse.userId)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("No token found"))
            val response = authService.logout(token)

            tokenManager.clearToken()
            _authState.value = false

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed on server: ${response.code()}"))
            }
        } catch (e: Exception) {
            tokenManager.clearToken()
            _authState.value = false
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val token = tokenManager.getToken() ?: return Result.success(null)

            val response = userService.getUserProfile(token)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                if (response.code() == 401) {
                    // Token inválido o expirado
                    tokenManager.clearToken()
                    _authState.value = false
                    Result.success(null)
                } else {
                    Result.failure(Exception("Failed to get user: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("Not authenticated"))
            val userDto = user.toProfileUpdateDto()
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

            // Convertir URI a archivo y crear parte para multipart
            val file = convertUriToFile(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = userService.updateProfileImage(token, imagePart)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.imageUrl ?: "")
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to update profile image: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAuthState(): Flow<Boolean> = _authState

    override suspend fun getToken(): String? {
        return tokenManager.getToken()
    }

    private fun convertUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}