package com.example.plantbuddiesapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object MyPlants : Screen("My Plants")
    object FavoritePlants : Screen("Favorites")
    object User  : Screen("User")
    object PlantCamera : Screen("plantCamera")
    object PlantResults : Screen("plantResults/{imageUri}")
    object Search : Screen("search")
    object Schedule : Screen("Schedule")
    object SearchResults : Screen("search_results")
    object PlantInformation : Screen("plant_information")
    object Login : Screen("Login")
    object Register : Screen("Register")
}