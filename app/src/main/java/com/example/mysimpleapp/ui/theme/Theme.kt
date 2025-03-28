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
    primary = Color(0xFF0066CC),          // Более тёмный синий для лучшего контраста
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1F0FF),  // Чуть более контрастный светло-голубой
    onPrimaryContainer = Color(0xFF003166), // Более тёмный синий для текста
    
    secondary = Color(0xFF2EB84D),         // Более насыщенный зеленый
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F5E6), // Более контрастный светло-зеленый
    onSecondaryContainer = Color(0xFF0A3D15), // Более тёмный зеленый для текста
    
    tertiary = Color(0xFF4F4FD1),          // Более насыщенный фиолетовый
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEAEAFC), // Более контрастный светло-фиолетовый
    onTertiaryContainer = Color(0xFF191966), // Более тёмный фиолетовый для текста
    
    surface = Color(0xFFFFFFFF),           // Чисто белый для максимального контраста
    onSurface = Color(0xFF000000),         // Чисто черный для текста
    surfaceVariant = Color(0xFFF5F5F7),    // Более контрастный серый
    onSurfaceVariant = Color(0xFF1A1A1A),  // Почти черный для текста
    
    error = Color(0xFFE11900),             // Более тёмный красный
    onError = Color.White,
    errorContainer = Color(0xFFFFE9E5),    // Более контрастный светло-красный
    onErrorContainer = Color(0xFF800F00)    // Более тёмный красный для текста
)

private val MintDarkColorScheme = darkColorScheme(
    primary = Color(0xFF47A3FF),          // Более яркий синий
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004C99),  // Более насыщенный тёмно-синий
    onPrimaryContainer = Color(0xFFD6EBFF), // Более светлый для контраста
    
    secondary = Color(0xFF4CD365),         // Более яркий зеленый
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0F5524), // Более насыщенный тёмно-зеленый
    onSecondaryContainer = Color(0xFFCFFED7), // Более светлый зеленый
    
    tertiary = Color(0xFF7A7AFF),          // Более яркий фиолетовый
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2E2E7F), // Более насыщенный тёмно-фиолетовый
    onTertiaryContainer = Color(0xFFE6E6FF), // Более светлый фиолетовый
    
    surface = Color(0xFF000000),           // Чисто черный
    onSurface = Color(0xFFFFFFFF),         // Чисто белый
    surfaceVariant = Color(0xFF1A1A1A),    // Более контрастный тёмный
    onSurfaceVariant = Color(0xFFF0F0F0),  // Более контрастный светлый
    
    error = Color(0xFFFF5449),             // Более яркий красный
    onError = Color.Black,
    errorContainer = Color(0xFF930F00),    // Более насыщенный тёмно-красный
    onErrorContainer = Color(0xFFFFDAD6)    // Более светлый красный
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