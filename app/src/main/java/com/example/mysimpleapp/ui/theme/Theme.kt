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

private val MintLightColorScheme = lightColorScheme(
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

private val MintDarkColorScheme = darkColorScheme(
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

private val GreyLightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A1A),          // Почти чёрный
    onPrimary = Color.White,
    primaryContainer = Color(0xFF404040),  // Тёмно-серый контейнер
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF505050),         // Серый
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8E8E8), // Светло-серый
    onSecondaryContainer = Color(0xFF0A0A0A), // Чёрный
    tertiary = Color(0xFF707070),          // Средне-серый
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF8F8F8), // Почти белый
    onTertiaryContainer = Color(0xFF0A0A0A), // Чёрный
    surface = Color(0xFFFFFFFF),           // Белый фон
    onSurface = Color(0xFF0A0A0A),         // Чёрный текст
    surfaceVariant = Color(0xFFF0F0F0),    // Светло-серый вариант
    onSurfaceVariant = Color(0xFF202020)   // Тёмно-серый текст
)

private val GreyDarkColorScheme = darkColorScheme(
    primary = Color(0xFFE0E0E0),          // Светло-серый
    onPrimary = Color(0xFF0A0A0A),        // Чёрный
    primaryContainer = Color(0xFF404040),  // Тёмно-серый контейнер
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFA0A0A0),         // Средне-серый
    onSecondary = Color(0xFF0A0A0A),
    secondaryContainer = Color(0xFF202020), // Очень тёмно-серый
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFD0D0D0),          // Светло-серый
    onTertiary = Color(0xFF0A0A0A),
    tertiaryContainer = Color(0xFF404040), // Тёмно-серый
    onTertiaryContainer = Color.White,
    surface = Color(0xFF0A0A0A),           // Чёрный
    onSurface = Color.White,
    surfaceVariant = Color(0xFF202020),    // Очень тёмно-серый
    onSurfaceVariant = Color(0xFFE0E0E0)   // Светло-серый текст
)

enum class AppTheme {
    MINT,
    GREY
}

@Composable
fun MySimpleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.MINT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.MINT -> if (darkTheme) MintDarkColorScheme else MintLightColorScheme
        AppTheme.GREY -> if (darkTheme) GreyDarkColorScheme else GreyLightColorScheme
    }
    
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