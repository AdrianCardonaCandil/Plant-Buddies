package com.example.plantbuddiesapp.presentation.ui.screens.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.plantbuddiesapp.navigation.Screen
import com.example.plantbuddiesapp.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    // Redirect to Login if not logged in
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    user?.let { user ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)), // Softer background
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                ProfileHeader(
                    name = user.displayName ?: "Plant Buddy", // Default name
                    email = user.email ?: "No email provided",
                    imageUrl = user.photoUrl?.toString()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Menu Items
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Filled.Edit,
                        text = "Edit Profile",
                        onClick = { /* TODO: Implement or navigate to Edit Profile Screen */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Filled.FavoriteBorder,
                        text = "My Favorite Plants",
                        onClick = { navController.navigate(Screen.FavoritePlants.route) }
                    )
                    ProfileMenuItem(
                        icon = Icons.Filled.Settings,
                        text = "App Settings",
                        onClick = { /* TODO: Implement or navigate to Settings Screen */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Filled.Shield,
                        text = "Privacy Policy",
                        onClick = { /* TODO: Show Privacy Policy (e.g., WebView or simple text screen) */ }
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Pushes logout button to bottom

                // Logout Button
                Button(
                    onClick = {
                        authViewModel.logout()
                        // Navigation to Login is handled by LaunchedEffect(isLoggedIn)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp) // More vertical padding
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    } ?: run {
        // Show a loading indicator if firebaseUser is null but still logged in (e.g. initial load)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoggedIn) {
                CircularProgressIndicator()
            }
            // If !isLoggedIn, LaunchedEffect will handle redirection.
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String, imageUrl: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // Match TopAppBar color
            .padding(top = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AccountCircle, // Placeholder
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp), // Icon size within the circle
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp) // Spacing between cards
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), // More rounded corners
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Consistent item background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Subtle elevation
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp), // Generous padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary, // Icon color matches theme
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(20.dp)) // More space after icon
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, // Standard disclosure icon
                contentDescription = "Go to $text",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), // Softer arrow
                modifier = Modifier.size(18.dp)
            )
        }
    }
}