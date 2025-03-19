package com.example.plantbuddiesapp.presentation.ui.states

import com.example.plantbuddiesapp.domain.model.Plant

sealed class IdentificationState {
    object Initial : IdentificationState()
    object Loading : IdentificationState()
    data class Success(val plant: Plant) : IdentificationState()
    data class Error(val message: String) : IdentificationState()
}