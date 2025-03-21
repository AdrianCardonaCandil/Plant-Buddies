package com.example.plantbuddiesapp.presentation.ui.screens.Home.Identificator

import android.Manifest
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.plantbuddiesapp.R
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.collectAsState
import com.example.plantbuddiesapp.presentation.ui.states.IdentificationState
import com.example.plantbuddiesapp.presentation.viewmodel.PlantViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PlantCameraScreen(
    navController: NavController,
    viewModel: PlantViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var showPermissionDialog by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var analysisInProgress by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val identificationState by viewModel.identificationState.collectAsState()

    val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }
    val cameraHelper = remember { CameraHelper(context, cameraExecutor) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }
    cameraHelper.setImageCapture(imageCapture)

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (cameraPermissionState.status.isGranted) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
                imageCapture = imageCapture
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        PlantDetectionOverlay(
            isScanning = isScanning,
            analysisInProgress = analysisInProgress
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { flashEnabled = !flashEnabled },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = stringResource(R.string.toggle_flash),
                    tint = if (flashEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isScanning && !analysisInProgress,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Text(
                    text = stringResource(R.string.center_plant_hint),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            IconButton(
                onClick = {
                    if (!isScanning && cameraPermissionState.status.isGranted) {
                        isScanning = true

                        cameraHelper.capturePhoto(
                            onImageCaptured = { uri ->
                                photoUri = uri
                                analysisInProgress = true
                                viewModel.identifyPlant(uri)
                            },
                            onError = { error ->
                                isScanning = false
                            }
                        )
                    } else if (!cameraPermissionState.status.isGranted) {
                        showPermissionDialog = true
                    }
                },
                modifier = Modifier
                    .size(72.dp)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = stringResource(R.string.capture_button),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        if (analysisInProgress) {
            AnalysisProgressOverlay(
                identificationState = identificationState,
                onComplete = {
                    analysisInProgress = false
                    isScanning = false

                    if (identificationState is IdentificationState.Success) {
                        photoUri?.let { uri ->
                            val encodedUri = URLEncoder.encode(
                                uri.toString(),
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("plantResults/$encodedUri")
                        }
                    }
                }
            )
        }
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text(stringResource(R.string.camera_permission_title)) },
                text = {
                    Text(
                        if (cameraPermissionState.status.shouldShowRationale) {
                            stringResource(R.string.camera_permission_rationale)
                        } else {
                            stringResource(R.string.camera_permission_text)
                        }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showPermissionDialog = false
                        cameraPermissionState.launchPermissionRequest()
                    }) {
                        Text(stringResource(R.string.request_permission))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showPermissionDialog = false
                        navController.popBackStack()
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}