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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeFilters = MutableStateFlow<Map<String, Any>>(emptyMap())
    val activeFilters: StateFlow<Map<String, Any>> = _activeFilters.asStateFlow()

    private var isIdentifying = false
    private val _waterNeeds = MutableStateFlow<Map<String?, Float>>(emptyMap())
    private val _sunlightNeeds = MutableStateFlow<Map<String?, Float>>(emptyMap())

    init {
        loadUserPlants()

        viewModelScope.launch {
            _searchQuery
                .debounce(300) // 300ms debounce
                .collectLatest { query ->
                    if (query.isNotEmpty() || _activeFilters.value.isNotEmpty()) {
                        searchPlants()
                    } else {
                        _searchResults.value = emptyList()
                        _searchState.value = SearchState.Initial
                    }
                }
        }
    }

    fun identifyPlant(imageUri: Uri) {
        if (isIdentifying) {
            println("Ya hay una identificación en proceso. Ignorando solicitud.")
            return
        }

        viewModelScope.launch {
            isIdentifying = true
            _identificationState.value = IdentificationState.Loading
            println("Iniciando identificación de planta para imagen: $imageUri")

            plantRepository.identifyPlant(imageUri).fold(
                onSuccess = { plant ->
                    println("Planta identificada exitosamente: ${plant.commonName}")
                    updatePlantUIProperties(plant.id, 0.7f, 0.6f)

                    _identificationState.value = IdentificationState.Success(plant)
                    _selectedPlant.value = plant
                    isIdentifying = false
                },
                onFailure = { error ->
                    println("Error al identificar planta: ${error.message}")
                    _identificationState.value = IdentificationState.Error(error.message ?: "Error desconocido")
                    isIdentifying = false
                }
            )
        }
    }

    fun savePlant(plantId: String) {
        viewModelScope.launch {
            _savePlantState.value = SavePlantState.Loading

            plantRepository.savePlant(plantId).fold(
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

    data class FilterOption(val displayName: String, val value: String)

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
    fun setSelectedPlant(plant: Plant) {
        _selectedPlant.value = plant
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        val currentFilters = _activeFilters.value.toMutableMap()

        if (query.isNotEmpty()) {
            currentFilters["commonName"] = query
        } else {
            currentFilters.remove("commonName")
        }

        _activeFilters.value = currentFilters
    }

    fun toggleFilter(key: String, value: Any) {
        val currentFilters = _activeFilters.value.toMutableMap()
        if (currentFilters[key] == value) {
            currentFilters.remove(key)
        } else {
            currentFilters[key] = value
        }
        _activeFilters.value = currentFilters
    }

    fun clearFilters() {
        _activeFilters.value = emptyMap()
        _searchQuery.value = ""
    }

    fun isFilterActive(key: String, value: Any): Boolean {
        return _activeFilters.value[key] == value
    }

    fun searchPlants() {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading

            try {

                val filters = _activeFilters.value.toMutableMap()
                if (_searchQuery.value.isNotEmpty()) {
                    filters["commonName"] = _searchQuery.value
                }

                plantRepository.searchPlants(filters).collect { plants ->
                    _searchResults.value = plants

                    if (plants.isEmpty()) {
                        _searchState.value = SearchState.Empty("No plants found matching your criteria")
                    } else {
                        _searchState.value = SearchState.Success
                    }
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun savePlant() {
        viewModelScope.launch {
            val plantToSave = _selectedPlant.value ?: return@launch
            val plantId = plantToSave.id ?: return@launch

            try {
                val result = plantRepository.savePlant(plantId)
                if (result.isSuccess) {
                    getUserPlants()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getUserPlants() {
        viewModelScope.launch {
            plantRepository.getUserPlants().collect { plants ->
                _userPlants.value = plants
            }
        }
    }

    fun deletePlant(plantId: String) {
        viewModelScope.launch {
            try {
                val result = plantRepository.deletePlant(plantId)
                if (result.isSuccess) {
                    getUserPlants()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getFilterOptions(): Map<String, List<FilterOption>> {
        return mapOf(
            "sunlight" to listOf(
                FilterOption("Full Sun", "full sun"),
                FilterOption("Partial Shade", "partial shade"),
                FilterOption("Full Shade", "full shade")
            ),
            "watering" to listOf(
                FilterOption("Low", "Low"),
                FilterOption("Medium", "Medium"),
                FilterOption("High", "High")
            ),
            "indoor" to listOf(
                FilterOption("Indoor", "true"),
                FilterOption("Outdoor", "false")
            ),
            "careLevel" to listOf(
                FilterOption("Easy", "Easy"),
                FilterOption("Medium", "Medium"),
                FilterOption("Hard", "Hard")
            ),
        )
    }
}