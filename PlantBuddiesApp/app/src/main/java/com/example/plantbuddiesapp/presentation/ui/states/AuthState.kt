package com.example.plantbuddiesapp.presentation.ui.states

data class AuthState(
    val email: String = "",
    val name: String = "", // Para registro
    val password: String = "",
    val confirmPassword: String = "", // Para registro
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigationEvent: NavigationEvent? = null // Usa la clase NavigationEvent definida abajo
)

// --- DEFINE NavigationEvent Sealed Class ---
sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
    object NavigateToRegister : NavigationEvent()
}