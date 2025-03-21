package com.example.plantbuddiesapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.domain.repository.AuthRepository
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MockAuthViewModel @Inject constructor(
    authRepository: AuthRepository,
    plantRepository: PlantRepository
) : ViewModel() {
    init {
        viewModelScope.launch {
            val result = authRepository.register(
                email = "test@example.com",
                name = "test",
                password = "password"
            )

            if (result.isSuccess) {
                val result2 = plantRepository.savePlant("2320")
                if (result2.isSuccess) {
                    Log.d("MockAuthViewModel", "Plant saved")
                } else {
                    Log.e("MockAuthViewModel", "Failed to save plant")
                }
            }
        }
    }
}