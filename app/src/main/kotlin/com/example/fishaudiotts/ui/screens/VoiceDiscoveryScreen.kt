package com.example.fishaudiotts.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fishaudiotts.ui.components.VoicePreviewCard
import com.example.fishaudiotts.ui.theme.TorBoxCard
import com.example.fishaudiotts.ui.theme.TorBoxGreenLight
import com.example.fishaudiotts.ui.theme.TorBoxGreen
import com.example.fishaudiotts.ui.theme.TorBoxText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient
import com.example.fishaudiotts.util.FileLogger
import com.example.fishaudiotts.viewmodel.SharedViewModel

/**
 * Voice Discovery Screen - Browse and search Fish Audio voices
 */
@Composable
fun VoiceDiscoveryScreen(
    viewModel: SharedViewModel,
    onNavigateBack: () -> Unit,
    onAddToFavorites: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val logger = remember { FileLogger.getInstance(context) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by remember { mutableStateOf("") }
    var addedVoiceMessage by remember { mutableStateOf<String?>(null) }

    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val currentlyPlayingVoiceId by viewModel.currentlyPlayingVoiceId.collectAsState()
    val favoriteVoices by viewModel.favoriteVoices.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isApiConfigured by viewModel.isApiConfigured.collectAsState()

    // Get set of favorite voice IDs for quick lookup
    val favoriteVoiceIds = remember(favoriteVoices) {
        favoriteVoices.map { it.id }.toSet()
    }

    // Load voices on first launch (only if API is configured)
    LaunchedEffect(Unit) {
        logger.d("VoiceDiscoveryScreen", "Screen launched, isApiConfigured: $isApiConfigured")
        if (isApiConfigured) {
            viewModel.searchVoices("")
        }
    }

    val performSearch = {
        keyboardController?.hide()
        if (isApiConfigured) {
            logger.d("VoiceDiscoveryScreen", "Searching for: '$searchQuery'")
            viewModel.searchVoices(searchQuery)
        }
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
                    tint = TorBoxGreen
                )
            }

            Text(
                text = "🔍 Discover Voices",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TorBoxGreen
            )
        }

        // Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search voices...", color = TorBoxText.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TorBoxGreen,
                        unfocusedBorderColor = TorBoxCard,
                        focusedTextColor = TorBoxText,
                        unfocusedTextColor = TorBoxText
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    enabled = isApiConfigured,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { performSearch() }
                    ),
                    singleLine = true
                )

                // Search Button
                Button(
                    onClick = performSearch,
                    enabled = isApiConfigured,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TorBoxGreen,
                        disabledContainerColor = TorBoxCard.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }

            if (!isApiConfigured) {
                Text(
                    text = "⚠️ API key required to browse voices. Add your key in Settings.",
                    fontSize = 12.sp,
                    color = TorBoxGreen,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(
                    text = if (isSearching) "Searching..." else "${searchResults.size} voice${if (searchResults.size != 1) "s" else ""} found",
                    fontSize = 12.sp,
                    color = TorBoxGreenLight,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Error message
            errorMessage?.let { error ->
                Text(
                    text = "⚠️ $error",
                    fontSize = 12.sp,
                    color = TorBoxGreen,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Loading indicator
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏳ Loading voices...",
                    color = TorBoxGreen,
                    fontSize = 16.sp
                )
            }
        }

        // Voice List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            items(searchResults) { voice ->
                val isFavorite = favoriteVoiceIds.contains(voice.id)
                VoicePreviewCard(
                    voiceName = voice.name,
                    description = voice.description ?: "No description",
                    onPlay = {
                        viewModel.playVoicePreview(voice.id)
                    },
                    onFavorite = {
                        if (!isFavorite) {
                            onAddToFavorites(voice.id, voice.name, voice.description ?: "")
                            addedVoiceMessage = "Added ${voice.name} to favorites"
                        }
                    },
                    isPlaying = currentlyPlayingVoiceId == voice.id,
                    isFavorite = isFavorite,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

    }
}
