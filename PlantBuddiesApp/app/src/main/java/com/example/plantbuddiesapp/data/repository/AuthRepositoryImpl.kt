package com.example.plantbuddiesapp.data.repository

import android.content.Context
import android.util.Log
import com.example.plantbuddiesapp.data.dto.LoginRequestDto
import com.example.plantbuddiesapp.data.dto.RegisterRequestDto
import com.example.plantbuddiesapp.data.services.AuthService
import com.example.plantbuddiesapp.data.services.UserService
import com.example.plantbuddiesapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val authService: AuthService,
    private val userService: UserService,
    private val tokenManager: TokenManager,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    /**
     * @param email Correo electrónico del usuario.
     * @param name Nombre del usuario.
     * @param password Contraseña del usuario.
     * @return Resultado de la operación.
     * */
    override suspend fun register(email: String, name: String, password: String): Result<String> {
        return try {
            val registerRequest = RegisterRequestDto(email, name, password)
            val response = authService.register(registerRequest)
            val token = response.body()?.token ?: return Result.failure(Exception("Error retrieving token"))
            try {
                firebaseAuth.signInWithCustomToken(token).await()
                Log.d("AuthRepositoryImpl", "User registered successfully")
                Result.success(token)
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Error login user by custom token", e)
                Result.failure(e)
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error registering user", e)
            Result.failure(e)
        }
    }

    /**
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Resultado de la operación.
     * */
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val loginRequest = LoginRequestDto(email, password)
            val response = authService.login(loginRequest)
            val token = response.body()?.token ?: return Result.failure(Exception("Error retrieving token"))
            try {
                firebaseAuth.signInWithCustomToken(token).await()
                Log.d("AuthRepositoryImpl", "User logged in successfully")
                Result.success(token)
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Error login user by custom token", e)
                Result.failure(e)
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error logging in user", e)
            Result.failure(e)
        }
    }
}