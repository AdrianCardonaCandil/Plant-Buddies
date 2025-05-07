// main/java/com/example/plantbuddiesapp/presentation/viewmodel/AuthViewModel.kt
package com.example.plantbuddiesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.data.repository.TokenManager // Asegúrate que la ruta es correcta
import com.example.plantbuddiesapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.plantbuddiesapp.presentation.ui.states.AuthState
import com.example.plantbuddiesapp.presentation.ui.states.NavigationEvent
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject




@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val isLoggedIn = tokenManager.currentUser
        .map { firebaseUser -> firebaseUser != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = tokenManager.currentUser.value != null
        )

    val currentUser: StateFlow<FirebaseUser?> = tokenManager.currentUser

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, error = null) }
    }
    fun login() {
        viewModelScope.launch { // launch ahora debería resolverse
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(state.value.email, state.value.password)
            result.fold(
                onSuccess = {
                    _state.update { currentState -> currentState.copy(isLoading = false, navigationEvent = NavigationEvent.NavigateToHome) }
                },
                onFailure = { exception ->
                    _state.update { currentState -> currentState.copy(isLoading = false, error = exception.message ?: "Login failed") }
                }
            )
        }
    }

    fun register() {
        if (state.value.password != state.value.confirmPassword) {
            _state.update { it.copy(error = "Passwords do not match") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.register(
                email = state.value.email,
                name = state.value.name,
                password = state.value.password
            )
            result.fold(
                onSuccess = {
                    _state.update { currentState -> currentState.copy(isLoading = false, navigationEvent = NavigationEvent.NavigateToHome) }
                },
                onFailure = { exception ->
                    _state.update { currentState -> currentState.copy(isLoading = false, error = exception.message ?: "Registration failed") }
                }
            )
        }
    }

    fun navigateToRegister() {
        _state.update { it.copy(navigationEvent = NavigationEvent.NavigateToRegister, error = null) }
    }

    fun navigateToLogin() {
        _state.update { it.copy(navigationEvent = NavigationEvent.NavigateToLogin, error = null) }
    }

    fun logout() {
        viewModelScope.launch {
            val result = authRepository.logout()
            result.fold(
                onSuccess = {
                     _state.update { it.copy(isLoading = false, ) }
                },
                onFailure = { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message ?: "Logout failed") }
                }
            )
        }
    }


    fun consumeNavigationEvent() {
        _state.update { it.copy(navigationEvent = null) }
    }
}