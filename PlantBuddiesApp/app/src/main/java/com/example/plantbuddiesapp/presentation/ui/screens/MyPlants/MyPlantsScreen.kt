package com.example.plantbuddiesapp.presentation.ui.screens.MyPlants

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.domain.model.Plant
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun MyPlantsScreen(
    navController: NavHostController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    // We're using the ViewModel's myPlants list which is updated in the ViewModel
    val plants = viewModel.myPlants

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (plants.isEmpty()) {
            EmptyPlantsView()
        } else {
            PlantsList(
                plants = plants,
                onPlantClick = {
                    viewModel.selectPlant(it)
                    navController.navigate("plant_information")
                },
                onDeletePlant = { viewModel.removePlant(it) },
                viewModel = viewModel
            )
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

        // Use a placeholder image resource that should exist in your project
        Image(
            painter = painterResource(id = R.drawable.plants_empty_photo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "No Plants Yet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add your first plant by identifying one with the camera!",
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
    viewModel: PlantViewModel
) {

    var isGridView by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { isGridView = !isGridView }
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = if (isGridView) "Switch to List" else "Switch to Grid",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

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
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
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
                        modifier = Modifier
                            .fillMaxSize(),
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
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Plant",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    Column {
                        plant.commonName?.let {
                            Text(
                                text = it,
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

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(36.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Plant",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

            }
        }
    }
}