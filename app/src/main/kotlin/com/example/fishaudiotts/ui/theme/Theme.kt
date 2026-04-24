package com.example.fishaudiotts.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = TorBoxGreen,
    onPrimary = TorBoxBlack,
    primaryContainer = TorBoxCard,
    onPrimaryContainer = TorBoxText,
    secondary = TorBoxGreenLight,
    onSecondary = TorBoxBlack,
    secondaryContainer = TorBoxSurface,
    onSecondaryContainer = TorBoxText,
    tertiary = TorBoxGreenDark,
    onTertiary = TorBoxText,
    tertiaryContainer = TorBoxCard,
    onTertiaryContainer = TorBoxText,
    error = TorBoxError,
    onError = TorBoxText,
    errorContainer = TorBoxError.copy(alpha = 0.2f),
    onErrorContainer = TorBoxText,
    background = TorBoxDarkGray,
    onBackground = TorBoxText,
    surface = TorBoxSurface,
    onSurface = TorBoxText,
    surfaceVariant = TorBoxCard,
    onSurfaceVariant = TorBoxTextSecondary,
    outline = TorBoxGreen,
    outlineVariant = TorBoxGreenDark,
    scrim = Color.Black
)

val VaporwaveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val vaporwaveGradient = Brush.verticalGradient(
    colors = listOf(
        TorBoxDarkGray,
        TorBoxSurface,
        TorBoxCard
    )
)

val neonGradient = Brush.linearGradient(
    colors = listOf(
        TorBoxGreen,
        TorBoxGreenLight,
        TorBoxGreenDark
    )
)

@Composable
fun FishAudioTTSTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = vaporwaveTypography,
        shapes = VaporwaveShapes,
        content = content
    )
}
