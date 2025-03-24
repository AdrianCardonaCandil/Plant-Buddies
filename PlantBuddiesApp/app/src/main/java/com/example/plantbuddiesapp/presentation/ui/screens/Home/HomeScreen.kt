package com.example.plantbuddiesapp.presentation.ui.screens.Home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.presentation.ui.screens.MyPlants.PlantCard
import com.example.plantbuddiesapp.presentation.ui.states.SearchState
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilters by viewModel.activeFilters.collectAsState()

    var isSearchFocused by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = stringResource(R.string.welcome_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.identify_description),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        stringResource(R.string.search_hint),
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_icon_description),
                        tint = if (isSearchFocused) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    Row {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.updateSearchQuery("")
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = if (activeFilters.isNotEmpty())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (isSearchFocused) 8.dp else 4.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .onFocusChanged { isSearchFocused = it.isFocused },
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (searchQuery.isNotEmpty() || activeFilters.isNotEmpty()) {
                        focusManager.clearFocus()
                        navController.navigate("search_results")
                    }
                })
            )

            if (searchQuery.isNotEmpty() || activeFilters.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        navController.navigate("search_results")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search Plants")
                }
            }

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

            AnimatedVisibility(
                visible = showFilters,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                FiltersSection(
                    viewModel = viewModel,
                    onFilterSelected = { key, value ->
                        viewModel.toggleFilter(key, value)
                    }
                )
            }

            when {
                searchState is SearchState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                else -> {
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { navController.navigate("plantCamera") },
                        modifier = Modifier
                            .width(200.dp)
                            .height(52.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(26.dp),
                                spotColor = MaterialTheme.colorScheme.primary
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = stringResource(R.string.camera_icon_description),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.identify_button),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveFilterChips(
    activeFilters: Map<String, Any>,
    onClearFilter: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(
                onClick = onClearAll
            ) {
                Text("Clear All")
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            activeFilters.forEach { (key, _) ->
                val displayName = when (key) {
                    "sunlight" -> "Sunlight"
                    "watering" -> "Water"
                    "indoor" -> "Location"
                    "careLevel" -> "Care"
                    "size" -> "Size"
                    "features" -> "Feature"
                    "query" -> "Search"
                    else -> key
                }

                val valueDisplay = when (val value = activeFilters[key]) {
                    is String -> when(value) {
                        "full sun" -> "Full Sun"
                        "partial shade" -> "Partial Shade"
                        "full shade" -> "Full Shade"
                        "low" -> "Low Water"
                        "medium" -> "Medium Water"
                        "high" -> "High Water"
                        "indoor" -> "Indoor"
                        "outdoor" -> "Outdoor"
                        "easy" -> "Easy Care"
                        "medium" -> "Medium Care"
                        "hard" -> "Hard Care"
                        "small" -> "Small"
                        "medium" -> "Medium"
                        "large" -> "Large"
                        "flowers" -> "Flowering"
                        "edible" -> "Edible"
                        "pet_safe" -> "Pet Safe"
                        else -> value
                    }
                    is Boolean -> if (value) "Yes" else "No"
                    else -> value.toString()
                }

                FilterChip(
                    selected = true,
                    onClick = { onClearFilter(key) },
                    label = { Text("$displayName: $valueDisplay") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear filter",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersSection(
    viewModel: PlantViewModel,
    onFilterSelected: (String, String) -> Unit
) {
    val filterOptions = viewModel.getFilterOptions()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Filter by:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        filterOptions.forEach { (category, options) ->
            Text(
                text = when(category) {
                    "sunlight" -> "Sunlight"
                    "watering" -> "Water Needs"
                    "indoor" -> "Location"
                    "careLevel" -> "Difficulty"
                    "size" -> "Plant Size"
                    "features" -> "Features"
                    else -> category
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isSelected = viewModel.isFilterActive(category, option.value)

                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSelected(category, option.value.toString()) },
                        label = { Text(option.displayName) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
    }
}