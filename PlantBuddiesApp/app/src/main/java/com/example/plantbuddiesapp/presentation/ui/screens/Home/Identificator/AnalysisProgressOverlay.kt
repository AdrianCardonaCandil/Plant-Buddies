package com.example.plantbuddiesapp.presentation.ui.screens.Home.Identificator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantbuddiesapp.presentation.ui.states.IdentificationState
import kotlinx.coroutines.delay

@Composable
fun AnalysisProgressOverlay(
    identificationState: IdentificationState,
    onComplete: () -> Unit
) {
    val initialPhrases = listOf(
        "Analyzing leaf structure...",
        "Identifying plant species...",
        "Checking growth patterns...",
        "Almost there..."
    )

    var currentPhraseIndex by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val currentPhrase = when {
        identificationState is IdentificationState.Success -> "Found a match!"
        identificationState is IdentificationState.Error -> "Error identifying plant"
        currentPhraseIndex < initialPhrases.size -> initialPhrases[currentPhraseIndex]
        else -> "Almost there..."
    }

    LaunchedEffect(Unit) {
        for (i in initialPhrases.indices) {
            currentPhraseIndex = i
            delay(800)
        }
    }

    LaunchedEffect(identificationState) {
        if (identificationState is IdentificationState.Success ||
            identificationState is IdentificationState.Error) {
            delay(800)
            onComplete()
        }
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
                            .background(
                                when {
                                    identificationState is IdentificationState.Error -> MaterialTheme.colorScheme.error
                                    identificationState is IdentificationState.Success -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.primary
                                }.copy(alpha = 0.8f)
                            )
                    )
                }
            }

            Text(
                text = currentPhrase,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}