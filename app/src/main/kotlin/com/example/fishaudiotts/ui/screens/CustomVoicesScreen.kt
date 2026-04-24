package com.example.fishaudiotts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.data.db.VoiceEntity
import com.example.fishaudiotts.ui.components.VaporwaveButton
import com.example.fishaudiotts.ui.components.VaporwaveCard
import com.example.fishaudiotts.ui.theme.CyberPurple
import com.example.fishaudiotts.ui.theme.DarkCyan
import com.example.fishaudiotts.ui.theme.NeonPink
import com.example.fishaudiotts.ui.theme.VapDarkBg
import com.example.fishaudiotts.ui.theme.VapText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Custom Voices Screen - Manage up to 5 favorite voices
 */
@Composable
fun CustomVoicesScreen(
    voices: Flow<List<VoiceEntity>> = flowOf(emptyList()),
    defaultVoiceId: String? = null,
    onNavigateBack: () -> Unit = {},
    onSetDefault: (String) -> Unit = {},
    onRemoveVoice: (String) -> Unit = {},
    onAddVoiceById: (String, String) -> Unit = { _, _ -> },
    onPlayVoice: (String) -> Unit = {}
) {
    val voiceList by voices.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var newVoiceId by remember { mutableStateOf("") }
    var newVoiceNickname by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
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
                    color = NeonPink,
                    modifier = Modifier.weight(1f)
                )

                // Add button
                if (voiceList.size < 5) {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add voice",
                            tint = DarkCyan
                        )
                    }
                }
            }

            Text(
                text = "${voiceList.size}/5 Favorites Saved",
                fontSize = 12.sp,
                color = DarkCyan,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Add Voice Dialog
        if (showAddDialog) {
            VaporwaveCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Add Voice by ID",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = newVoiceId,
                        onValueChange = { newVoiceId = it },
                        label = { Text("Voice ID (from fish.audio)") },
                        placeholder = { Text("Paste voice ID here...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = CyberPurple,
                            focusedTextColor = VapText,
                            unfocusedTextColor = VapText
                        )
                    )

                    OutlinedTextField(
                        value = newVoiceNickname,
                        onValueChange = { newVoiceNickname = it },
                        label = { Text("Nickname (optional)") },
                        placeholder = { Text("My Custom Voice") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = CyberPurple,
                            focusedTextColor = VapText,
                            unfocusedTextColor = VapText
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Button(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberPurple
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (newVoiceId.isNotBlank()) {
                                    onAddVoiceById(
                                        newVoiceId.trim(),
                                        newVoiceNickname.trim().ifEmpty { "Custom Voice" }
                                    )
                                    newVoiceId = ""
                                    newVoiceNickname = ""
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            enabled = newVoiceId.isNotBlank()
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
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
                            text = "Tap + to add by ID or visit Voice Discovery",
                            fontSize = 12.sp,
                            color = DarkCyan,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                voiceList.forEach { voice ->
                    VoiceListItem(
                        voice = voice,
                        isDefault = voice.id == defaultVoiceId,
                        onSetDefault = { onSetDefault(voice.id) },
                        onRemove = { onRemoveVoice(voice.id) },
                        onPlay = { onPlayVoice(voice.referenceId) }
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
    onRemove: () -> Unit,
    onPlay: () -> Unit
) {
    VaporwaveCard(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with name and default badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = voice.nickname,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPink,
                    modifier = Modifier.weight(1f)
                )

                if (isDefault) {
                    Box(
                        modifier = Modifier
                            .background(NeonPink, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DEFAULT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VapDarkBg
                        )
                    }
                }
            }

            // Voice ID
            Text(
                text = "ID: ${voice.referenceId}",
                fontSize = 11.sp,
                color = VapText.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            // Emotion if available
            if (voice.emotion.isNotEmpty()) {
                Text(
                    text = "Emotion: ${voice.emotion}",
                    fontSize = 13.sp,
                    color = DarkCyan,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play button
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier.background(
                        CyberPurple.copy(alpha = 0.3f),
                        androidx.compose.foundation.shape.CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Preview",
                        tint = DarkCyan
                    )
                }

                // Set Default button (if not already default)
                if (!isDefault) {
                    VaporwaveButton(
                        text = "Set Default",
                        onClick = onSetDefault,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }

                // Remove button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.background(
                        NeonPink.copy(alpha = 0.2f),
                        androidx.compose.foundation.shape.CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = NeonPink
                    )
                }
            }
        }
    }
}
