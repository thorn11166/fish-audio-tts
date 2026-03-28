package com.example.fishaudiotts.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = VapDarkBg,
    primaryContainer = CyberPurple,
    onPrimaryContainer = VapText,
    secondary = DarkCyan,
    onSecondary = VapDarkBg,
    secondaryContainer = NeonBlue,
    onSecondaryContainer = VapText,
    tertiary = LimeLime,
    onTertiary = VapDarkBg,
    tertiaryContainer = CyberPurple,
    onTertiaryContainer = VapText,
    error = VapError,
    onError = VapDarkBg,
    errorContainer = VapError,
    onErrorContainer = VapText,
    background = VapDarkBg,
    onBackground = VapText,
    surface = VapSolidBg,
    onSurface = VapText,
    surfaceVariant = VapGradientLight,
    onSurfaceVariant = VapTextSecondary,
    outline = NeonPink,
    outlineVariant = CyberPurple,
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
        VapDarkBg,
        VapSolidBg,
        VapGradientLight
    )
)

val neonGradient = Brush.linearGradient(
    colors = listOf(
        NeonPink,
        CyberPurple,
        DarkCyan
    )
)

@Composable
fun FishAudioTTSTheme(
    darkTheme: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = vaporwaveTypography,
        shapes = VaporwaveShapes,
        content = content
    )
}
