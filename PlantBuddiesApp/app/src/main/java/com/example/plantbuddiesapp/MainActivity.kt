package com.example.plantbuddiesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.plantbuddiesapp.navigation.AppNavigation
import com.example.plantbuddiesapp.presentation.ui.theme.PlantBuddiesAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantBuddiesAppTheme {
                AppNavigation()
            }
        }
    }
}
