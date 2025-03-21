package com.example.plantbuddiesapp.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plantbuddiesapp.presentation.ui.screens.Home.HomeScreen
import com.example.plantbuddiesapp.presentation.ui.screens.Common.PlantInformationScreen
import com.example.plantbuddiesapp.presentation.ui.screens.MyPlants.MyPlantsScreen
import com.example.plantbuddiesapp.presentation.ui.screens.User.UserScreen
import com.example.plantbuddiesapp.presentation.ui.screens.Home.PlantCameraScreen
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.plantbuddiesapp.presentation.ui.screens.Common.BottomNavigationBar
import com.example.plantbuddiesapp.presentation.viewmodel.MockAuthViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val viewModel: PlantViewModel = hiltViewModel()

    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = when (currentRoute) {
        Screen.PlantCamera.route,
        "plantResults/{encodedUri}",
        "plant_information" -> false
        else -> true
    }
    val fakeAuth: MockAuthViewModel = hiltViewModel()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .statusBarsPadding()
            ) {
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController, viewModel)
                    }

                    composable(Screen.MyPlants.route) {
                        MyPlantsScreen(navController, viewModel)
                    }

                    composable(Screen.User.route) {
                        UserScreen(navController)
                    }

                    composable(Screen.PlantCamera.route) {

                        PlantCameraScreen(navController)
                    }

                    composable(
                        route = "plantResults/{encodedUri}",
                        arguments = listOf(
                            navArgument("encodedUri") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val encodedUri = backStackEntry.arguments?.getString("encodedUri") ?: ""
                        try {
                            val decodedUriString = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
                            val uri = Uri.parse(decodedUriString)
                            viewModel.identifyPlant(uri)
                        } catch (e: Exception) {
                            // Handle error
                        }

                        PlantInformationScreen(navController, viewModel)
                    }

                    composable("plant_information") {
                        PlantInformationScreen(navController, viewModel)
                    }
                }
            }
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    }
}