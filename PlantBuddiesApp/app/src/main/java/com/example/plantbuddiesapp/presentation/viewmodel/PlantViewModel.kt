package com.example.plantbuddiesapp.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import com.example.plantbuddiesapp.presentation.ui.states.IdentificationState
import com.example.plantbuddiesapp.presentation.ui.states.SavePlantState
import com.example.plantbuddiesapp.presentation.ui.states.SearchState
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

    val myPlants = mutableStateListOf<Plant>()

    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant.asStateFlow()

    private val _savePlantState = MutableStateFlow<SavePlantState>(SavePlantState.Initial)
    val savePlantState: StateFlow<SavePlantState> = _savePlantState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Plant>>(emptyList())
    val searchResults: StateFlow<List<Plant>> = _searchResults.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _waterNeeds = MutableStateFlow<Map<String?, Float>>(emptyMap())
    val waterNeeds: StateFlow<Map<String?, Float>> = _waterNeeds.asStateFlow()

    private val _sunlightNeeds = MutableStateFlow<Map<String?, Float>>(emptyMap())
    val sunlightNeeds: StateFlow<Map<String?, Float>> = _sunlightNeeds.asStateFlow()

    init {
        loadUserPlants()
        loadSamplePlants()
    }

    fun identifyPlant(imageUri: Uri) {
        viewModelScope.launch {
            _identificationState.value = IdentificationState.Loading

            plantRepository.identifyPlant(imageUri).fold(
                onSuccess = { plant ->
                    updatePlantUIProperties(plant.id, 0.7f, 0.6f)

                    _identificationState.value = IdentificationState.Success(plant)
                    _selectedPlant.value = plant
                },
                onFailure = { error ->
                    _identificationState.value = IdentificationState.Error(error.message ?: "Error desconocido")
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
                    loadUserPlants()

                    if (myPlants.none { it.id == savedPlant.id }) {
                        updatePlantUIProperties(savedPlant.id, 0.7f, 0.6f)
                        myPlants.add(savedPlant)
                    }
                },
                onFailure = { error ->
                    _savePlantState.value = SavePlantState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun selectPlant(plant: Plant) {
        _selectedPlant.value = plant
    }

    fun removePlant(plant: Plant) {
        myPlants.removeIf { it.id == plant.id }

        if (plant.id != null) {
            viewModelScope.launch {
                plantRepository.deletePlant(plant.id)
                loadUserPlants()
            }
        }
    }

    fun loadUserPlants() {
        viewModelScope.launch {
            plantRepository.getUserPlants().collectLatest { plants ->
                _userPlants.value = plants

                myPlants.clear()
                myPlants.addAll(plants)

                plants.forEach { plant ->
                    updatePlantUIProperties(plant.id, 0.7f, 0.6f)
                }
            }
        }
    }

    fun searchPlants(query: String, filters: Set<Any>) {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading

            try {
                plantRepository.searchPlants(query, filters).collectLatest { plants ->
                    _searchResults.value = plants
                    _searchState.value = SearchState.Success
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Error searching plants")
            }
        }
    }

    private fun updatePlantUIProperties(plantId: String?, waterNeeds: Float, sunlightNeeds: Float) {
        _waterNeeds.value = _waterNeeds.value.toMutableMap().apply {
            put(plantId, waterNeeds)
        }
        _sunlightNeeds.value = _sunlightNeeds.value.toMutableMap().apply {
            put(plantId, sunlightNeeds)
        }
    }

    fun getWaterNeeds(plantId: String?): Float {
        return _waterNeeds.value[plantId] ?: 0.5f
    }

    fun getSunlightNeeds(plantId: String?): Float {
        return _sunlightNeeds.value[plantId] ?: 0.5f
    }

    private fun loadSamplePlants() {
        if (myPlants.isEmpty()) {
            val samplePlant = Plant(
                id = "sample1",
                scientificName = "Monstera Deliciosa",
                commonName = "Swiss Cheese Plant",
                description = "The Monstera deliciosa is a species of flowering plant native to tropical forests of southern Mexico, south to Panama.",
                family = "Araceae",
                genus = "Monstera",
                careLevel = "Intermediate",
                careGuides = listOf(
                    "Water when the top 2-3 inches of soil feels dry",
                    "Prefers bright, indirect light",
                    "Enjoys high humidity but adapts to normal home conditions"
                )
            )

            myPlants.add(samplePlant)
            updatePlantUIProperties(samplePlant.id, 0.6f, 0.7f)
        }
    }
}