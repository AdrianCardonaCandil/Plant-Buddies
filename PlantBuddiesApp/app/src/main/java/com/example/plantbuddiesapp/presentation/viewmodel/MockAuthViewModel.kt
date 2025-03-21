package com.example.plantbuddiesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MockAuthViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            authRepository.register(
                email = "test@example.com",
                name = "test",
                password = "password"
            )
        }
    }
}