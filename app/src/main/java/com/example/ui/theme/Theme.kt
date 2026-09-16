package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CampusNavyLight,
    onPrimary = Color.White,
    primaryContainer = CampusNavyDarkSurface,
    onPrimaryContainer = CampusNavySoft,
    secondary = CampusTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = CampusTealContainer,
    tertiary = CampusAmber,
    onTertiary = Color.White,
    background = CampusNavyDarkBg,
    onBackground = CampusTextPrimaryDark,
    surface = CampusNavyDarkSurface,
    onSurface = CampusTextPrimaryDark,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = CampusTextSecondaryDark,
    outline = CampusNavyDarkBorder,
    error = StatusDanger
)

private val LightColorScheme = lightColorScheme(
    primary = CampusNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = CampusNavyContainer,
    onPrimaryContainer = CampusNavyPrimary,
    secondary = CampusTeal,
    onSecondary = Color.White,
    secondaryContainer = CampusTealContainer,
    onSecondaryContainer = Color(0xFF0F766E),
    tertiary = CampusAmber,
    onTertiary = Color.White,
    tertiaryContainer = CampusAmberContainer,
    onTertiaryContainer = Color(0xFF92400E),
    background = CampusBgLight,
    onBackground = CampusTextPrimaryLight,
    surface = CampusSurfaceLight,
    onSurface = CampusTextPrimaryLight,
    surfaceVariant = CampusSurfaceVariantLight,
    onSurfaceVariant = CampusTextSecondaryLight,
    outline = CampusBorderLight,
    error = StatusDanger
)

@Composable
fun CampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional collegiate styling
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards-compat alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CampusTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
