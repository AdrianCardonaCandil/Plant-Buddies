package com.example.plantbuddiesapp.presentation.ui.screens.Search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.plantbuddiesapp.presentation.ui.screens.MyPlants.PlantCard
import com.example.plantbuddiesapp.presentation.ui.screens.Home.ActiveFilterChips
import com.example.plantbuddiesapp.presentation.ui.screens.Home.FiltersSection
import com.example.plantbuddiesapp.presentation.ui.states.SearchState
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    navController: NavHostController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilters by viewModel.activeFilters.collectAsState()

    // Ejecuta la búsqueda cuando se carga la pantalla
    LaunchedEffect(key1 = Unit) {
        if (searchQuery.isNotEmpty() || activeFilters.isNotEmpty()) {
            viewModel.searchPlants()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mostrar filtros activos
                if (activeFilters.isNotEmpty()) {
                    ActiveFilterChips(
                        activeFilters = activeFilters,
                        onClearFilter = { key ->
                            val value = activeFilters[key] ?: return@ActiveFilterChips
                            viewModel.toggleFilter(key, value)
                        },
                        onClearAll = { viewModel.clearFilters() }
                    )
                }

                when {
                    searchState is SearchState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    searchResults.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(searchResults) { plant ->
                                PlantCard(
                                    plant = plant,
                                    onDelete = { /* No delete in search results */ },
                                    onClick = {
                                        viewModel.selectPlant(plant)
                                        navController.navigate("plant_information")
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }

                    searchState is SearchState.Empty || searchResults.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (searchState is SearchState.Empty)
                                        (searchState as SearchState.Empty).message
                                    else
                                        "No plants found matching your criteria",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}