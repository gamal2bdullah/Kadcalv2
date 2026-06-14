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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark theme by default matching HTML
    dynamicColor: Boolean = false, // Use our strictly customized dark theme instead of Android's generic system palette
    content: @Composable () -> Unit,
) {
    val colorScheme = CosmicColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
