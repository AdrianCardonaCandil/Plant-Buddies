package com.example.plantbuddiesapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantbuddiesapp.ui.screens.Home.HomeScreen
import com.example.plantbuddiesapp.ui.screens.MyPlants.MyPlantsScreen
import com.example.plantbuddiesapp.ui.screens.User.UserScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize())
    {
        Column(modifier = Modifier.fillMaxSize())
        {
            Box(modifier = Modifier.weight(1f).statusBarsPadding()) {
                NavHost(navController = navController, startDestination = Screen.Home.route)
                {
                    composable(Screen.Home.route) { HomeScreen(navController) }
                    composable(Screen.MyPlants.route) { MyPlantsScreen(navController) }
                    composable(Screen.User.route) { UserScreen(navController) }
                }
            }
            BottomNavigationBar(navController = navController)
        }
    }

}
