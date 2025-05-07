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
                Screen.Login -> null
                Screen.Register -> null
                Screen.PlantCamera -> null
                Screen.PlantResults -> null
                Screen.Search -> null
                Screen.SearchResults -> null
                Screen.PlantInformation -> null
            }

            // Solo muestra el item si tiene un icono asignado
            if (iconVector != null) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = screen.route
                        )
                    },
                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                    selected = currentRoute == screen.route || (screen == Screen.User && currentRoute == Screen.Login.route && !isLoggedIn),
                    onClick = {

                        val targetRoute: String

                        // --- LÓGICA CLAVE: Destino condicional SOLO para User ---
                        if (screen == Screen.User) {
                            targetRoute = if (isLoggedIn) Screen.User.route else Screen.Login.route
                        } else {
                            // Para todas las demás pestañas, el destino es la propia ruta de la pantalla
                            targetRoute = screen.route
                        }

                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                // Lógica de navegación estándar para limpiar stack, etc.
                                // Aplica popUpTo para las pestañas principales si se está logueado
                                // o si se navega a Home.
                                if (targetRoute == Screen.Home.route || isLoggedIn) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        // Guarda el estado solo para las pestañas principales cuando estás logueado
                                        // O siempre para Home
                                        saveState = (isLoggedIn && screen != Screen.User && screen != Screen.Home) || screen == Screen.Home
                                    }
                                }
                                // Evita múltiples instancias de la misma pantalla
                                launchSingleTop = true
                                // Restaura estado si es apropiado (ej. al volver a una pestaña principal)
                                restoreState = (isLoggedIn && screen != Screen.User && screen != Screen.Home) || screen == Screen.Home
                            }
                        }
                    },
                    alwaysShowLabel = true, // Muestra siempre las etiquetas
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