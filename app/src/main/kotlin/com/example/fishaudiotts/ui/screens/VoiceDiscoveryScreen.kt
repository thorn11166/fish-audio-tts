package com.example.fishaudiotts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.ui.components.VoicePreviewCard
import com.example.fishaudiotts.ui.theme.CyberPurple
import com.example.fishaudiotts.ui.theme.DarkCyan
import com.example.fishaudiotts.ui.theme.NeonPink
import com.example.fishaudiotts.ui.theme.VapText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient

/**
 * Voice Discovery Screen - Browse and search Fish Audio voices
 */
@Composable
fun VoiceDiscoveryScreen(
    onNavigateBack: () -> Unit,
    onFavoriteVoice: (String, String) -> Unit = { _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedVoice by remember { mutableStateOf<String?>(null) }
    
    // Demo voices - in production, these would come from Fish Audio API
    val demoVoices = listOf(
        Triple("8ef4a238714b45718ce04243307c57a7", "E-girl", "Young, energetic female voice"),
        Triple("802e3bc2b27e49c2995d23ef70e6ac89", "Energetic Male", "Upbeat, motivated male voice"),
        Triple("933563129e564b19a115bedd57b7406a", "Sarah", "Professional, calm female voice"),
        Triple("bf322df2096a46f18c579d0baa36f41d", "Adrian", "Deep, authoritative male voice"),
        Triple("b347db033a6549378b48d00acb0d06cd", "Selene", "Mystical, soothing female voice"),
    )
    
    val filteredVoices = demoVoices.filter {
        it.second.contains(searchQuery, ignoreCase = true) ||
        it.third.contains(searchQuery, ignoreCase = true)
    }
    
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
                text = "🔍 Discover Voices",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPink
            )
        }
        
        // Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search voices...", color = VapText.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = CyberPurple,
                    focusedTextColor = VapText,
                    unfocusedTextColor = VapText
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            
            Text(
                text = "${filteredVoices.size} voice${if (filteredVoices.size != 1) "s" else ""} found",
                fontSize = 11.sp,
                color = DarkCyan,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        // Voice List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            items(filteredVoices) { (id, name, description) ->
                VoicePreviewCard(
                    voiceName = name,
                    onPlay = {
                        selectedVoice = id
                        // In production, play audio from Fish Audio
                    },
                    onFavorite = {
                        onFavoriteVoice(id, name)
                    },
                    isFavorite = selectedVoice == id,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
