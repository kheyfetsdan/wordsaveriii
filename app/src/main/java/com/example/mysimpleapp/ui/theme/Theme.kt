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
    primary = Color(0xFF004B00),          // Основной зеленый
    onPrimary = Color.White,
    primaryContainer = Color(0xFF95F88B),  // Светло-зеленый контейнер
    onPrimaryContainer = Color(0xFF002200), // Текст на зеленом
    secondary = Color(0xFF52634C),         // Вторичный зеленый
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4E8CC), // Светло-зеленый вторичный
    onSecondaryContainer = Color(0xFF101F0D), // Текст на вторичном
    tertiary = Color(0xFF386136),          // Третичный цвет
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB8F4AF), // Контейнер третичного
    onTertiaryContainer = Color(0xFF002200), // Текст на третичном
    surface = Color(0xFFF8FAF6),           // Поверхность
    onSurface = Color(0xFF191C19),         // Текст на поверхности
    surfaceVariant = Color(0xFFE1E4DE),    // Вариант поверхности
    onSurfaceVariant = Color(0xFF43483F)   // Текст на варианте
)

private val MintDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7BDB70),          // Зеленый для тёмной темы
    onPrimary = Color(0xFF003700),
    primaryContainer = Color(0xFF004B00),  // Тёмно-зеленый контейнер
    onPrimaryContainer = Color(0xFF95F88B), // Текст на зеленом
    secondary = Color(0xFFB8CCB1),         // Вторичный зеленый
    onSecondary = Color(0xFF253423),
    secondaryContainer = Color(0xFF3B4B37), // Тёмно-зеленый вторичный
    onSecondaryContainer = Color(0xFFD4E8CC), // Текст на вторичном
    tertiary = Color(0xFF9CD894),          // Третичный цвет
    onTertiary = Color(0xFF013401),
    tertiaryContainer = Color(0xFF1F4B1C), // Контейнер третичного
    onTertiaryContainer = Color(0xFFB8F4AF), // Текст на третичном
    surface = Color(0xFF191C19),           // Поверхность
    onSurface = Color(0xFFE1E3DF),         // Текст на поверхности
    surfaceVariant = Color(0xFF43483F),    // Вариант поверхности
    onSurfaceVariant = Color(0xFFBFC9BB)   // Текст на варианте
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