package com.example.plantbuddiesapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object MyPlants : Screen("My Plants")
    object User  : Screen("User")
}