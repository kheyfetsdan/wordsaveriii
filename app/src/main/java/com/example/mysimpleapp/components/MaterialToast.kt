package com.example.mysimpleapp.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun MaterialToast(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically {
            with(density) { 40.dp.roundToPx() }
        } + fadeIn(),
        exit = slideOutVertically {
            with(density) { 40.dp.roundToPx() }
        } + fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 80.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.inverseSurface
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3000)
            onDismiss()
        }
    }
} 