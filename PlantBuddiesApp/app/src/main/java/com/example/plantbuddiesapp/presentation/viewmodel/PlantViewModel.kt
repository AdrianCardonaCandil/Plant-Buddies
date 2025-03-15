package com.example.plantbuddiesapp.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _identificationState = MutableStateFlow<IdentificationState>(IdentificationState.Initial)
    val identificationState: StateFlow<IdentificationState> = _identificationState.asStateFlow()

    private val _userPlants = MutableStateFlow<List<Plant>>(emptyList())
    val userPlants: StateFlow<List<Plant>> = _userPlants.asStateFlow()

    private val _savePlantState = MutableStateFlow<SavePlantState>(SavePlantState.Initial)
    val savePlantState: StateFlow<SavePlantState> = _savePlantState.asStateFlow()

    init {
        loadUserPlants()
    }

    fun identifyPlant(imageUri: Uri) {
        viewModelScope.launch {
            _identificationState.value = IdentificationState.Loading

            plantRepository.identifyPlant(imageUri).fold(
                onSuccess = { plant ->
                    _identificationState.value = IdentificationState.Success(plant)
                },
                onFailure = { error ->
                    _identificationState.value = IdentificationState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun savePlant(plant: Plant) {
        viewModelScope.launch {
            _savePlantState.value = SavePlantState.Loading

            plantRepository.savePlant(plant).fold(
                onSuccess = { savedPlant ->
                    _savePlantState.value = SavePlantState.Success(savedPlant)
                    loadUserPlants() // Refresh plants list
                },
                onFailure = { error ->
                    _savePlantState.value = SavePlantState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun loadUserPlants() {
        viewModelScope.launch {
            plantRepository.getUserPlants().collectLatest { plants ->
                _userPlants.value = plants
            }
        }
    }

    sealed class IdentificationState {
        object Initial : IdentificationState()
        object Loading : IdentificationState()
        data class Success(val plant: Plant) : IdentificationState()
        data class Error(val message: String) : IdentificationState()
    }

    sealed class SavePlantState {
        object Initial : SavePlantState()
        object Loading : SavePlantState()
        data class Success(val plant: Plant) : SavePlantState()
        data class Error(val message: String) : SavePlantState()
    }
}