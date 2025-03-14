package com.example.plantbuddiesapp.ui.screens.Home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

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