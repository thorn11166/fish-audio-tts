package com.example.fishaudiotts.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

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
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    val context = LocalContext.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                // Android 15+ - use edge to edge
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
            } else {
                // Pre-Android 15
                window.statusBarColor = TorBoxDarkGray.toArgb()
                window.navigationBarColor = TorBoxDarkGray.toArgb()
            }
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = vaporwaveTypography,
        shapes = VaporwaveShapes,
        content = content
    )
}
