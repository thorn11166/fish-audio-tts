package com.example.fishaudiotts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.ui.components.VaporwaveButton
import com.example.fishaudiotts.ui.components.VaporwaveCard
import com.example.fishaudiotts.ui.theme.CyberPurple
import com.example.fishaudiotts.ui.theme.DarkCyan
import com.example.fishaudiotts.ui.theme.NeonPink
import com.example.fishaudiotts.ui.theme.VapDarkBg
import com.example.fishaudiotts.ui.theme.VapSolidBg
import com.example.fishaudiotts.ui.theme.VapText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient
import com.example.fishaudiotts.viewmodel.SettingsViewModel

/**
 * Settings Screen - API configuration and TTS parameters
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val apiKey by viewModel.apiKey.collectAsState()
    val ttsModel by viewModel.ttsModel.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()
    val voiceVolume by viewModel.voiceVolume.collectAsState()
    val emotionEnabled by viewModel.emotionEnabled.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val topP by viewModel.topP.collectAsState()
    val audioFormat by viewModel.audioFormat.collectAsState()
    val latencyMode by viewModel.latencyMode.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveMessage by viewModel.saveMessage.collectAsState()
    
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
                text = "⚙️ Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPink
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // API Key Section
            VaporwaveCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🔑 API Configuration",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { viewModel.updateApiKey(it) },
                        label = { Text("Fish Audio API Key", color = DarkCyan) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = CyberPurple,
                            focusedTextColor = VapText,
                            unfocusedTextColor = VapText
                        )
                    )
                }
            }
            
            // Model Selection
            VaporwaveCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🎵 TTS Model",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    listOf("s1", "s2-pro").forEach { model ->
                        ModelButton(
                            label = model.uppercase(),
                            isSelected = ttsModel == model,
                            onClick = { viewModel.updateTtsModel(model) }
                        )
                    }
                }
            }
            
            // Prosody Settings
            VaporwaveCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🎙️ Voice Control",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Speed
                    SettingSlider(
                        label = "Speed: ${String.format("%.1f", voiceSpeed)}x",
                        value = voiceSpeed,
                        onValueChange = { viewModel.updateVoiceSpeed(it) },
                        valueRange = 0.5f..2.0f
                    )
                    
                    // Volume
                    SettingSlider(
                        label = "Volume: ${String.format("%+.1f", voiceVolume)}dB",
                        value = voiceVolume,
                        onValueChange = { viewModel.updateVoiceVolume(it) },
                        valueRange = -20f..20f
                    )
                    
                    // Temperature
                    SettingSlider(
                        label = "Temperature: ${String.format("%.1f", temperature)}",
                        value = temperature,
                        onValueChange = { viewModel.updateTemperature(it) },
                        valueRange = 0f..1f
                    )
                    
                    // Emotion Toggle
                    SettingToggle(
                        label = "Emotion Control",
                        value = emotionEnabled,
                        onValueChange = { viewModel.updateEmotionEnabled(it) }
                    )
                }
            }
            
            // Save Button
            if (saveMessage != null) {
                Text(
                    text = saveMessage!!,
                    color = DarkCyan,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            VaporwaveButton(
                text = "Save Settings",
                onClick = { viewModel.saveSettings() },
                isLoading = isSaving
            )
        }
    }
}

@Composable
private fun ModelButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) NeonPink.copy(alpha = 0.3f) else CyberPurple.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) NeonPink else VapText
        )
    }
}

@Composable
private fun SettingSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 12.sp, color = DarkCyan)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SettingToggle(
    label: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = DarkCyan)
        Switch(
            checked = value,
            onCheckedChange = onValueChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonPink,
                uncheckedThumbColor = CyberPurple
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
