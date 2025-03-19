package com.example.plantbuddiesapp.presentation.ui.states

import com.example.plantbuddiesapp.domain.model.Plant

sealed class SavePlantState {
    object Initial : SavePlantState()
    object Loading : SavePlantState()
    data class Success(val plant: Plant) : SavePlantState()
    data class Error(val message: String) : SavePlantState()
}