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
import com.example.plantbuddiesapp.ui.screens.Home.HomeScreen
import com.example.plantbuddiesapp.ui.screens.Home.PlantResultsScreen
import com.example.plantbuddiesapp.ui.screens.MyPlants.MyPlantsScreen
import com.example.plantbuddiesapp.ui.screens.User.UserScreen
import com.example.plantbuddiesapp.ui.screens.Home.PlantCameraScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = when (currentRoute) {
        Screen.PlantCamera.route,
        "plantResults/{encodedUri}" -> false

        else -> true
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .statusBarsPadding()
            ) {
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) { HomeScreen(navController) }
                    composable(Screen.MyPlants.route) { MyPlantsScreen(navController) }
                    composable(Screen.User.route) { UserScreen(navController) }
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
                        val decodedUri =
                            URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
                        PlantResultsScreen(navController, Uri.parse(decodedUri))
                    }
                }
            }
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    }

}