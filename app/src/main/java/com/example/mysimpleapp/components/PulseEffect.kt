package com.example.mysimpleapp.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun PulseEffect(
    modifier: Modifier = Modifier,
    pulseFraction: Float = 1.05f,
    duration: Int = 1000,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier.scale(scale),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
} 