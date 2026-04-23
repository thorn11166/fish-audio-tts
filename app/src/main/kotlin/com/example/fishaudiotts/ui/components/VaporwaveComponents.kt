package com.example.fishaudiotts.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.ui.theme.CyberPurple
import com.example.fishaudiotts.ui.theme.DarkCyan
import com.example.fishaudiotts.ui.theme.NeonBlue
import com.example.fishaudiotts.ui.theme.NeonPink
import com.example.fishaudiotts.ui.theme.VapDarkBg
import com.example.fishaudiotts.ui.theme.VapError
import com.example.fishaudiotts.ui.theme.VapGradientLight
import com.example.fishaudiotts.ui.theme.VapSolidBg
import com.example.fishaudiotts.ui.theme.VapText
import com.example.fishaudiotts.ui.theme.neonGradient
import com.example.fishaudiotts.ui.theme.vaporwaveGradient

/**
 * Vaporwave-themed button with gradient and glow effect
 */
@Composable
fun VaporwaveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonPink,
            disabledContainerColor = CyberPurple.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading
    ) {
        Text(
            text = if (isLoading) "Loading..." else text,
            fontSize = 16.sp,
            color = VapDarkBg,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * Vaporwave card with gradient border and dark background
 */
@Composable
fun VaporwaveCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VapSolidBg)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(NeonPink, CyberPurple, DarkCyan)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        content()
    }
}

/**
 * Animated glowing background
 */
@Composable
fun AnimatedGlowingBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(vaporwaveGradient)
    )
}

/**
 * Neon text display
 */
@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 24,
    glowing: Boolean = false
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = NeonPink,
        modifier = if (glowing) {
            modifier.shadow(elevation = 4.dp)
        } else {
            modifier
        }
    )
}

/**
 * Voice preview card with play button
 */
@Composable
fun VoicePreviewCard(
    voiceName: String,
    description: String? = null,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    VaporwaveCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = voiceName,
                color = NeonPink,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            description?.let {
                Text(
                    text = it,
                    color = VapText.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Play Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = if (isPlaying) Brush.linearGradient(
                                colors = listOf(DarkCyan, NeonBlue)
                            ) else neonGradient,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable(onClick = onPlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Stop preview" else "Play voice",
                        tint = VapDarkBg,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Favorite Button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .background(
                            color = if (isFavorite) NeonPink else VapGradientLight,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) VapDarkBg else DarkCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Pulse animation modifier
 */
@Composable
fun PulseAnimationBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = 1.05f,
        animationSpec = tween(durationMillis = 1500),
        label = "pulse"
    )
    
    Box(
        modifier = modifier.scale(animatedScale),
    ) {
        content()
    }
}
