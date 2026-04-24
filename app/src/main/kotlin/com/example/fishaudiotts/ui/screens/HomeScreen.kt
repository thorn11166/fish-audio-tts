package com.example.fishaudiotts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.ui.components.VaporwaveButton
import com.example.fishaudiotts.ui.components.VaporwaveCard
import com.example.fishaudiotts.ui.theme.TorBoxCard
import com.example.fishaudiotts.ui.theme.TorBoxGreenLight
import com.example.fishaudiotts.ui.theme.TorBoxGreen
import com.example.fishaudiotts.ui.theme.TorBoxText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient

/**
 * Home Screen - Main navigation hub
 */
@Composable
fun HomeScreen(
    isApiConfigured: Boolean,
    onNavigateToVoiceDiscovery: () -> Unit,
    onNavigateToCustomVoices: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vaporwaveGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎙️ Fish Audio TTS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TorBoxGreen,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Status Card
            if (!isApiConfigured) {
                VaporwaveCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ Setup Required",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TorBoxGreen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Configure your Fish Audio API key in Settings to get started.",
                            fontSize = 14.sp,
                            color = TorBoxText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        VaporwaveButton(
                            text = "Go to Settings",
                            onClick = onNavigateToSettings
                        )
                    }
                }
            } else {
                VaporwaveCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ Ready to Speak",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TorBoxGreen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "API configured and ready to generate speech.",
                            fontSize = 14.sp,
                            color = TorBoxText
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Main Navigation Buttons
            VaporwaveCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Discover Voices
                    VoiceNavButton(
                        icon = Icons.Default.Search,
                        title = "Discover Voices",
                        description = "Browse and preview voices from Fish Audio library",
                        onClick = onNavigateToVoiceDiscovery,
                        enabled = isApiConfigured
                    )
                    
                    // My Voices
                    VoiceNavButton(
                        icon = Icons.Default.Favorite,
                        title = "My Voices",
                        description = "Manage your 5 favorite voices",
                        onClick = onNavigateToCustomVoices,
                        enabled = isApiConfigured
                    )
                    
                    // Settings
                    VoiceNavButton(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        description = "Configure API key and TTS parameters",
                        onClick = onNavigateToSettings,
                        enabled = true
                    )
                    
                    // Debug Logs
                    VoiceNavButton(
                        icon = androidx.compose.material.icons.Icons.Default.List,
                        title = "Debug Logs",
                        description = "View and share debug logs for troubleshooting",
                        onClick = onNavigateToLogs,
                        enabled = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun VoiceNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (enabled) TorBoxCard.copy(alpha = 0.3f) else TorBoxCard.copy(alpha = 0.15f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) TorBoxGreen else TorBoxText.copy(alpha = 0.5f)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = if (enabled) TorBoxText else TorBoxText.copy(alpha = 0.3f)
            )
        }
    }
}
