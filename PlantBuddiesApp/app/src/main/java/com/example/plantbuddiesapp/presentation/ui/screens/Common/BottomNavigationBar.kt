// main/java/com/example/plantbuddiesapp/presentation/ui/screens/Common/BottomNavigationBar.kt
package com.example.plantbuddiesapp.presentation.ui.screens.Common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Grass
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.plantbuddiesapp.navigation.Screen
import com.example.plantbuddiesapp.presentation.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState // Importa collectAsState
import androidx.compose.runtime.getValue //

@Composable
fun BottomNavigationBar(
    navController: NavController,

    authViewModel: AuthViewModel
) {
    val items = listOf(Screen.Home, Screen.MyPlants, Screen.FavoritePlants, Screen.Schedule, Screen.User)
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            val iconVector = when (screen) {
                Screen.Home -> Icons.Default.Home
                Screen.MyPlants -> Icons.Default.Grass
                Screen.FavoritePlants -> Icons.Default.Favorite
                Screen.Schedule -> Icons.Default.CalendarToday
                Screen.User -> Icons.Default.Person
                else -> null
            }

            if (iconVector != null) {

                val targetRoute = if (screen == Screen.User) {
                    if (isLoggedIn) Screen.User.route else Screen.Login.route
                } else {
                    screen.route
                }
                val isSelected = currentRoute == targetRoute || (currentRoute == Screen.User.route && screen == Screen.User)

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = screen.route
                        )
                    },
                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                    selected =isSelected,
                    onClick = {

                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState =
                                        true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}