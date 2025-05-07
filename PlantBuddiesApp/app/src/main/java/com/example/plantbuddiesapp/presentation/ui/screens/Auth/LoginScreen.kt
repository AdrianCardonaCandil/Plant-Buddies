package com.example.plantbuddiesapp.presentation.ui.screens.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.presentation.viewmodel.AuthViewModel
import com.example.plantbuddiesapp.navigation.Screen // Asegúrate que la ruta es correcta
import com.example.plantbuddiesapp.presentation.ui.states.NavigationEvent

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Handle Navigation Events
    LaunchedEffect(state.navigationEvent) {
        when (state.navigationEvent) {
            is NavigationEvent.NavigateToHome -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true } // Limpia stack hasta Login
                }
                viewModel.consumeNavigationEvent()
            }
            is NavigationEvent.NavigateToRegister -> {
                navController.navigate(Screen.Register.route)
                viewModel.consumeNavigationEvent()
            }
            else -> {} // Ignore other events or null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Usa tu logo
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(150.dp).padding(bottom = 24.dp)
            )

            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Login to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login()
                    }
                ),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Error Message
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link to Register
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = viewModel::navigateToRegister) {
                    Text("Register", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(32.dp)) // Extra space at bottom
        }
    }
}