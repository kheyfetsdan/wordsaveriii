package com.example.mysimpleapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.ui.theme.NeumorphColors

@Composable
fun NeumorphicSurface(
    modifier: Modifier = Modifier,
    pressed: Boolean = false,
    cornerRadius: Int = 16,
    content: @Composable BoxScope.() -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background == NeumorphColors.LightBackground
    
    Box(
        modifier = modifier
            .shadow(
                elevation = if (pressed) 2.dp else 4.dp,
                shape = RoundedCornerShape(cornerRadius.dp),
                spotColor = if (isLight) NeumorphColors.LightShadowDark else NeumorphColors.DarkShadowDark,
                ambientColor = if (isLight) NeumorphColors.LightShadowLight else NeumorphColors.DarkShadowLight
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
    ) {
        content()
    }
} 