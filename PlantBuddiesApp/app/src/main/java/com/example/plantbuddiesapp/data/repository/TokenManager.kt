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
     * Método que obtiene el token de autenticación del usuario actual.
     * @param forceRefresh Indica si se debe forzar la actualización del token.
     * @return El token de autenticación del usuario actual o null si no se puede obtener.
     */
    suspend fun getToken(forceRefresh: Boolean = true): String? {
        return try {
            _currentUser.value?.getIdToken(forceRefresh)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
}