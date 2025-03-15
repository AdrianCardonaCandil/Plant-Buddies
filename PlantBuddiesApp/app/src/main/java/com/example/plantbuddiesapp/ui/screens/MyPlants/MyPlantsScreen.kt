package com.example.plantbuddiesapp.ui.screens.MyPlants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.navigation.PlantViewModel
import com.example.plantbuddiesapp.ui.screens.Home.PlantInfo
import coil.compose.rememberAsyncImagePainter

@Composable
fun MyPlantsScreen(navController: NavHostController, viewModel: PlantViewModel) {
    val myPlants = viewModel.myPlants

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (myPlants.isEmpty()) {
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
                    contentDescription = stringResource(R.string.plants_empty_description),
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

                Button(
                    onClick = { navController.navigate("add_plant") },
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = MaterialTheme.colorScheme.primary
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.camera_icon_description),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.plants_empty_button),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                myPlants.forEach { plant ->
                    PlantCard(plant = plant, onDelete = { viewModel.removePlant(plant) })
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { navController.navigate("add_plant") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add Plant"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Plant")
                }

            }
        }


    }

}

@Composable
fun PlantCard(plant: PlantInfo, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            plant.imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = plant.commonName,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            Text(
                text = plant.commonName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.End)
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