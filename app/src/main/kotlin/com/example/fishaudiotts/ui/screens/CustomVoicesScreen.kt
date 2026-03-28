package com.example.fishaudiotts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.data.db.VoiceEntity
import com.example.fishaudiotts.ui.components.VaporwaveCard
import com.example.fishaudiotts.ui.theme.CyberPurple
import com.example.fishaudiotts.ui.theme.DarkCyan
import com.example.fishaudiotts.ui.theme.NeonPink
import com.example.fishaudiotts.ui.theme.VapText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Custom Voices Screen - Manage up to 5 favorite voices
 */
@Composable
fun CustomVoicesScreen(
    voices: Flow<List<VoiceEntity>>,
    defaultVoiceId: String?,
    onNavigateBack: () -> Unit,
    onSetDefault: (String) -> Unit,
    onRemoveVoice: (String) -> Unit
) {
    val voiceList by voices.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vaporwaveGradient)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonPink
                )
            }
            
            Text(
                text = "⭐ My Voices",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPink
            )
            
            Text(
                text = "${voiceList.size}/5 Favorites Saved",
                fontSize = 12.sp,
                color = DarkCyan,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Voices List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            if (voiceList.isEmpty()) {
                VaporwaveCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No favorite voices yet",
                            fontSize = 16.sp,
                            color = VapText,
                            modifier = Modifier.padding(24.dp)
                        )
                        Text(
                            text = "Visit Voice Discovery to add some!",
                            fontSize = 12.sp,
                            color = DarkCyan
                        )
                    }
                }
            } else {
                voiceList.forEach { voice ->
                    VoiceListItem(
                        voice = voice,
                        isDefault = voice.id == defaultVoiceId,
                        onSetDefault = { onSetDefault(voice.id) },
                        onRemove = { onRemoveVoice(voice.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceListItem(
    voice: VoiceEntity,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onRemove: () -> Unit
) {
    VaporwaveCard(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Voice Name & Nickname
            Text(
                text = voice.nickname,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPink,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "ID: ${voice.referenceId}",
                fontSize = 10.sp,
                color = VapText.copy(alpha = 0.5f)
            )
            
            if (voice.emotion.isNotEmpty()) {
                Text(
                    text = "Emotion: ${voice.emotion}",
                    fontSize = 12.sp,
                    color = DarkCyan,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Set Default Radio
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSetDefault)
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadioButton(
                    selected = isDefault,
                    onClick = onSetDefault,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = NeonPink,
                        unselectedColor = CyberPurple
                    )
                )
                Text(
                    text = if (isDefault) "Default Voice" else "Set as Default",
                    fontSize = 12.sp,
                    color = if (isDefault) NeonPink else DarkCyan
                )
            }
            
            // Remove Button
            Text(
                text = "❌ Remove",
                fontSize = 12.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onRemove)
                    .padding(top = 12.dp, bottom = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
