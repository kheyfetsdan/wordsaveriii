package com.example.mysimpleapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006C5D),          // Основной мятный
    onPrimary = Color.White,
    primaryContainer = Color(0xFF74F8DC),  // Светло-мятный контейнер
    onPrimaryContainer = Color(0xFF002019), // Текст на мятном
    secondary = Color(0xFF4A635E),         // Вторичный мятный
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E1), // Светло-мятный вторичный
    onSecondaryContainer = Color(0xFF06201B), // Текст на вторичном
    tertiary = Color(0xFF436277),          // Третичный цвет
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6FF), // Контейнер третичного
    onTertiaryContainer = Color(0xFF001E2F), // Текст на третичном
    surface = Color(0xFFF5FAF8),           // Поверхность
    onSurface = Color(0xFF191C1B),         // Текст на поверхности
    surfaceVariant = Color(0xFFDBE5E1),    // Вариант поверхности
    onSurfaceVariant = Color(0xFF3F4946)   // Текст на варианте
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF53DBc0),          // Мятный для тёмной темы
    onPrimary = Color(0xFF003730),
    primaryContainer = Color(0xFF005045),  // Тёмно-мятный контейнер
    onPrimaryContainer = Color(0xFF74F8DC), // Текст на мятном
    secondary = Color(0xFFB1CCC5),         // Вторичный мятный
    onSecondary = Color(0xFF1C352F),
    secondaryContainer = Color(0xFF324B46), // Тёмно-мятный вторичный
    onSecondaryContainer = Color(0xFFCCE8E1), // Текст на вторичном
    tertiary = Color(0xFFABCAE3),          // Третичный цвет
    onTertiary = Color(0xFF0D3447),
    tertiaryContainer = Color(0xFF294A5E), // Контейнер третичного
    onTertiaryContainer = Color(0xFFC8E6FF), // Текст на третичном
    surface = Color(0xFF191C1B),           // Поверхность
    onSurface = Color(0xFFE0E3E1),         // Текст на поверхности
    surfaceVariant = Color(0xFF3F4946),    // Вариант поверхности
    onSurfaceVariant = Color(0xFFBFC9C5)   // Текст на варианте
)

@Composable
fun MySimpleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}