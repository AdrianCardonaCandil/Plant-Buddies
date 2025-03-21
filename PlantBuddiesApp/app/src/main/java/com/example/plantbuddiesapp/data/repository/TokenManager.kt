package com.example.plantbuddiesapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clase que maneja el usuario actual de la aplicación mediante el SDK de Firebase.
 * @property firebaseAuth Instancia de FirebaseAuth.
 * */

@Singleton
class TokenManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    /**
     * Método que permite obtener el token de autenticación del usuario actual.
     * @param forceRefresh Indica si se debe forzar la actualización del token.
     * @return Flow que emite el token de autenticación del usuario actual.
     */
    fun getToken(forceRefresh: Boolean = true): Flow<String?> = flow {
        _currentUser.value?.let { user ->
            try {
                val tokenResult = user.getIdToken(forceRefresh).await()
                emit(tokenResult.token)
            } catch (e: Exception) {
                emit(null)
            }
        } ?: emit(null)
    }
}