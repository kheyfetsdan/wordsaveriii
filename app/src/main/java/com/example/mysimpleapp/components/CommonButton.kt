package com.example.mysimpleapp.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: ButtonType = ButtonType.Primary
) {
    val buttonColors = when (type) {
        ButtonType.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.90f)
        )
        ButtonType.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f)
        )
        ButtonType.Tertiary -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        )

        ButtonType.Danger -> TODO()
    }

    val buttonHeight = when (type) {
        ButtonType.Primary -> 56.dp
        ButtonType.Secondary -> 48.dp
        ButtonType.Tertiary -> 40.dp
        ButtonType.Danger -> TODO()
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        enabled = enabled,
        colors = buttonColors,
        shape = when (type) {
            ButtonType.Primary -> MaterialTheme.shapes.medium
            ButtonType.Secondary -> MaterialTheme.shapes.medium
            ButtonType.Tertiary -> MaterialTheme.shapes.small
            ButtonType.Danger -> TODO()
        },
        border = if (type == ButtonType.Tertiary) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else null,
        contentPadding = when (type) {
            ButtonType.Primary -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ButtonType.Secondary -> PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ButtonType.Tertiary -> PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ButtonType.Danger -> TODO()
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

enum class ButtonType {
    Primary,
    Secondary,
    Tertiary,
    Danger
} 