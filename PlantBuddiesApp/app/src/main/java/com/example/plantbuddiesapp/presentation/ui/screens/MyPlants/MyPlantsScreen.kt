package com.example.plantbuddiesapp.presentation.ui.screens.MyPlants

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.plantbuddiesapp.navigation.Screen
import com.example.plantbuddiesapp.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlantsScreen(
    navController: NavHostController,
    viewModel: PlantViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val allPlants = viewModel.myPlants
    var filteredPlants by remember { mutableStateOf(allPlants.toList()) }
    var showFilters by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    if (!isLoggedIn) {
        return
    }

    viewModel.loadUserPlants()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.plants_title),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )},
            actions = {
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.switch_to_list)
                    )
                }
                IconButton(onClick = { isGridView = !isGridView }) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = if (isGridView) stringResource(R.string.switch_to_list)
                        else stringResource(R.string.switch_to_grid),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        if (showFilters) {
            FilterBar { filters ->
                filteredPlants = allPlants.filter { plant ->
                    filters.all { (key, value) ->
                        when (key) {
                            "sunlight" -> (plant.sunlight ?: "").trim().equals(value.trim(), ignoreCase = true)
                            "water" -> (plant.watering ?: "").trim().equals(value.trim(), ignoreCase = true)
                            "care" -> (plant.careLevel ?: "").trim().equals(value.trim(), ignoreCase = true)
                            else -> true
                        }
                    }
                }
            }
        }

        if (filteredPlants.isEmpty()) {
            EmptyPlantsView()
        } else {
            PlantsList(
                plants = filteredPlants,
                onPlantClick = {
                    viewModel.selectPlant(it)
                    navController.navigate("plant_information")
                },
                onDeletePlant = { viewModel.removePlant(it) },
                viewModel = viewModel,
                isGridView = isGridView,

            )
        }
    }
}

@Composable
fun FilterBar(
    onApplyFilters: (Map<String, String>) -> Unit
) {
    var sunlight by remember { mutableStateOf<String?>(null) }
    var water by remember { mutableStateOf<String?>(null) }
    var care by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxWidth().padding(8.dp)) {
        FilterChipGroup(
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbSunny, contentDescription = "Sunlight")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sunlight")
                }
            },
            options = listOf("Full Sun", "Partial Shade", "Full Shade"),
            selectedOption = sunlight
        ) { sunlight = it }

        FilterChipGroup(
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Opacity, contentDescription = "Water Needs")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Water Needs")
                }
            },
            options = listOf("Low", "Average", "High"),
            selectedOption = water
        ) { water = it }

        FilterChipGroup(
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Face, contentDescription = "Care Level")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Care Level")
                }
            },
            options = listOf("Low", "Medium", "High"),
            selectedOption = care
        ) { care = it }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    val filters = mutableMapOf<String, String>()
                    sunlight?.let { filters["sunlight"] = it }
                    water?.let { filters["water"] = it }
                    care?.let { filters["care"] = it }
                    onApplyFilters(filters)
                }
            ) { Text(text = stringResource(R.string.apply_filters_button)) }

            OutlinedButton(
                onClick = {
                    sunlight = null
                    water = null
                    care = null
                    onApplyFilters(emptyMap())
                }
            ) { Text(text = stringResource(R.string.reset_filters_button)) }
        }
    }
}

@Composable
fun FilterChipGroup(
    label: @Composable () -> Unit,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)) {
        label()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
fun EmptyPlantsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.plants_empty_photo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.plants_empty_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.plants_empty_description),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun PlantsList(
    plants: List<Plant>,
    onPlantClick: (Plant) -> Unit,
    onDeletePlant: (Plant) -> Unit,
    viewModel: PlantViewModel,
    isGridView: Boolean,
) {
    Column {
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    items(plants) { plant ->
                        PlantCard(
                            plant = plant,
                            onDelete = { onDeletePlant(plant) },
                            onClick = { onPlantClick(plant) },
                            viewModel = viewModel,
                            isGridView = true
                        )
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                plants.forEach { plant ->
                    PlantCard(
                        plant = plant,
                        onDelete = { onDeletePlant(plant) },
                        onClick = { onPlantClick(plant) },
                        viewModel = viewModel,
                        isGridView = false
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun Chip(label: String, modifier: Modifier = Modifier, isGridView: Boolean = false) {
    val backgroundColor = if (isGridView) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }

    val contentColor = if (isGridView) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun PlantCard(
    plant: Plant,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    viewModel: PlantViewModel,
    isGridView: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(plant.commonName ?: "") }

    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 500)
    )

    // Get UI-specific properties from the ViewModel
    val waterNeeds = viewModel.getWaterNeeds(plant.id)
    val sunlightNeeds = viewModel.getSunlightNeeds(plant.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (isGridView) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                plant.imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = plant.commonName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    RoundedCornerShape(50)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_icon_description),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    RoundedCornerShape(50)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Plant",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column {
                        plant.commonName?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                plant.imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = plant.commonName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Chip(label = "Care: ${plant.careLevel ?: "Medium"}", modifier = Modifier.weight(1f))
                    Chip(label = "Water: ${(waterNeeds*10).toInt()}L", modifier = Modifier.weight(1f))
                    Chip(label = "Sun: ${(sunlightNeeds*10).toInt()}hrs", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                plant.commonName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isGridView) 16.sp else 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = plant.description ?: "No description available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_icon_description),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Plant",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                    }
                }

            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) { Text(text = stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            title = { Text(text = stringResource(R.string.confirmation_message_title),
                fontWeight = FontWeight.Bold) },
            text = {
                Text(text = "Are you sure you want to delete ${plant.commonName ?: "this plant"}?")
            }
        )
    }

    if (showEditDialog) {
        var newName by remember { mutableStateOf(plant.commonName ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updatePlantName(plant.id, newName)
                    showEditDialog = false
                }) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            title = { Text("Edit Plant") },
            text = {
                Column {
                    Text("Enter a new name for ${plant.commonName ?: "the plant"}:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Name") }
                    )
                }
            }
        )
    }
}