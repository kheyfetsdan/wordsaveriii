package com.example.mysimpleapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState

@Composable
fun TooltipBox(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    delayMillis: Long = 500,
    tooltipOffset: Offset = Offset(0f, 16f),
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(Offset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    LaunchedEffect(isHovered) {
        if (isHovered) {
            delay(delayMillis)
            isVisible = true
        } else {
            isVisible = false
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                position = coordinates.positionInWindow()
            }
            .hoverable(interactionSource)
    ) {
        content()

        if (isVisible) {
            Popup(
                offset = IntOffset(
                    (position.x + tooltipOffset.x).toInt(),
                    (position.y + tooltipOffset.y).toInt()
                ),
                properties = PopupProperties(focusable = false)
            ) {
                Surface(
                    modifier = Modifier
                        .shadow(4.dp)
                        .defaultMinSize(minWidth = 40.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    ) {
                        tooltip()
                    }
                }
            }
        }
    }
} 