
package com.example.plantbuddiesapp.ui.screens.Home

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.plantbuddiesapp.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    imageCapture: ImageCapture? = null,
    onUseCase: (Preview) -> Unit = { }
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val preview = remember { Preview.Builder().build() }

    LaunchedEffect(cameraSelector) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        preview.surfaceProvider = previewView.surfaceProvider
        onUseCase(preview)

        if (imageCapture != null) {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } else {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}


suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}


class CameraHelper(
    private val context: Context,
    private val executor: Executor
) {
    private var imageCapture: ImageCapture? = null

    fun setImageCapture(imageCapture: ImageCapture) {
        this.imageCapture = imageCapture
    }

    fun capturePhoto(
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PlantBuddies")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    onError(exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: return
                    onImageCaptured(savedUri)
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PlantCameraScreen(navController: NavController) {
    val context = LocalContext.current

    var showPermissionDialog by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var analysisInProgress by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

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
                imageCapture = imageCapture,
                onUseCase = { preview ->
                }
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
                onComplete = {
                    analysisInProgress = false
                    isScanning = false

                    photoUri?.let { uri ->
                        val encodedUri = URLEncoder.encode(
                            uri.toString(),
                            StandardCharsets.UTF_8.toString()
                        )
                        navController.navigate("plantResults/$encodedUri")
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


@Composable
fun PlantDetectionOverlay(isScanning: Boolean, analysisInProgress: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse animation"
    )

    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan line"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val color =  MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.fillMaxSize()) {

            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size,
                blendMode = BlendMode.Darken
            )

            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width * 0.4f * (if (isScanning) pulseAnimation else 1f)

            drawCircle(
                color = Color.Transparent,
                radius = radius,
                center = Offset(centerX, centerY),
                blendMode = BlendMode.Clear
            )

            drawCircle(
                color = Color.White,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx())
            )

            if (isScanning && !analysisInProgress) {
                val lineY = centerY - radius + scanLinePosition * radius * 2
                drawLine(
                    color = color.copy(alpha = 0.7f),
                    start = Offset(centerX - radius, lineY),
                    end = Offset(centerX + radius, lineY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            val cornerLength = 24.dp.toPx()
            val offset = 4.dp.toPx()

            drawLine(
                color = color,
                start = Offset(centerX - radius - offset, centerY - radius - offset),
                end = Offset(centerX - radius - offset + cornerLength, centerY - radius - offset),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = color,
                start = Offset(centerX - radius - offset, centerY - radius - offset),
                end = Offset(centerX - radius - offset, centerY - radius - offset + cornerLength),
                strokeWidth = 3.dp.toPx()
            )

            drawLine(
                color = color,
                start = Offset(centerX + radius + offset, centerY - radius - offset),
                end = Offset(centerX + radius + offset - cornerLength, centerY - radius - offset),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = color,
                start = Offset(centerX + radius + offset, centerY - radius - offset),
                end = Offset(centerX + radius + offset, centerY - radius - offset + cornerLength),
                strokeWidth = 3.dp.toPx()
            )

            drawLine(
                color =color,
                start = Offset(centerX - radius - offset, centerY + radius + offset),
                end = Offset(centerX - radius - offset + cornerLength, centerY + radius + offset),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color =color,
                start = Offset(centerX - radius - offset, centerY + radius + offset),
                end = Offset(centerX - radius - offset, centerY + radius + offset - cornerLength),
                strokeWidth = 3.dp.toPx()
            )

            drawLine(
                color = color,
                start = Offset(centerX + radius + offset, centerY + radius + offset),
                end = Offset(centerX + radius + offset - cornerLength, centerY + radius + offset),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = color,
                start = Offset(centerX + radius + offset, centerY + radius + offset),
                end = Offset(centerX + radius + offset, centerY + radius + offset - cornerLength),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

@Composable
fun AnalysisProgressOverlay(onComplete: () -> Unit) {

    val analysisPhrases = listOf(
        "Analyzing leaf structure...",
        "Identifying plant species...",
        "Checking growth patterns...",
        "Almost there...",
        "Found a match!"
    )

    var currentPhraseIndex by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    LaunchedEffect(Unit) {
        for (i in analysisPhrases.indices) {
            currentPhraseIndex = i
            delay(800)
        }
        delay(500)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing, delayMillis = index * 100),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot $index"
                    )

                    Box(
                        modifier = Modifier
                            .size((12 * dotScale).dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    )
                }
            }

            Text(
                text = analysisPhrases[currentPhraseIndex],
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}