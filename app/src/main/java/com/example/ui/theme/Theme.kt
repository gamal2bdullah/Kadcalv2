package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CosmicColorScheme = darkColorScheme(
    primary = CosmicOrange,
    onPrimary = Color.White,
    primaryContainer = CosmicBorder,
    onPrimaryContainer = CosmicText,
    secondary = CosmicAmber,
    onSecondary = CosmicBg,
    background = CosmicBg,
    onBackground = CosmicText,
    surface = CosmicPanel,
    onSurface = CosmicText,
    surfaceVariant = CosmicPanel2,
    onSurfaceVariant = CosmicMute,
    outline = CosmicBorder,
    error = CosmicRed,
    onError = Color.White
)

private val HighContrastColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    secondary = CosmicOrange,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color.White,
    outline = Color.White,
    error = Color(0xFFFF5252),
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark theme by default matching HTML
    dynamicColor: Boolean = false, // Use our strictly customized dark theme instead of Android's generic system palette
    highContrast: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (highContrast) HighContrastColorScheme else CosmicColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
