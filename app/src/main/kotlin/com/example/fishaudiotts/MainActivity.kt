package com.example.fishaudiotts

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fishaudiotts.ui.screens.ApiKeyDialog
import com.example.fishaudiotts.ui.screens.CustomVoicesScreen
import com.example.fishaudiotts.ui.screens.HomeScreen
import com.example.fishaudiotts.ui.screens.LogsScreen
import com.example.fishaudiotts.ui.screens.SettingsScreen
import com.example.fishaudiotts.ui.screens.VoiceDiscoveryScreen
import com.example.fishaudiotts.ui.theme.FishAudioTTSTheme
import com.example.fishaudiotts.util.CrashHandler
import com.example.fishaudiotts.util.FileLogger
import com.example.fishaudiotts.viewmodel.SettingsViewModel
import com.example.fishaudiotts.viewmodel.SharedViewModel

/**
 * Main Activity for Fish Audio TTS app
 * Handles navigation, deep linking, and theme initialization
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var logger: FileLogger
    
    companion object {
        private const val PREFS_NAME = "fish_audio_prefs"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        fun isFirstLaunch(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        }
        
        fun setFirstLaunchComplete(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install crash handler
        CrashHandler.install(this)
        
        // Initialize logger
        logger = FileLogger.getInstance(this)
        logger.i("MainActivity", "App starting...")
        
        // Check if this is the first launch
        val isFirstLaunch = isFirstLaunch(this)
        
        setContent {
            FishAudioTTSTheme {
                val navController = rememberNavController()
                val sharedViewModel = SharedViewModel(this@MainActivity)
                val settingsViewModel = SettingsViewModel(this@MainActivity)
                
                val isApiConfigured by sharedViewModel.isApiConfigured.collectAsState()
                val favoriteVoices by sharedViewModel.favoriteVoices.collectAsState()
                val defaultVoice by sharedViewModel.defaultVoice.collectAsState()
                val currentlyPlayingVoiceId by sharedViewModel.currentlyPlayingVoiceId.collectAsState()
                
                // Show first-run dialog if needed
                var showApiKeyDialog by remember { 
                    mutableStateOf(isFirstLaunch && !isApiConfigured) 
                }
                
                // First-run API key dialog
                if (showApiKeyDialog) {
                    ApiKeyDialog(
                        onSave = { apiKey ->
                            // Save the API key
                            settingsViewModel.updateApiKey(apiKey)
                            settingsViewModel.saveSettings()
                            setFirstLaunchComplete(this@MainActivity)
                            showApiKeyDialog = false
                        },
                        onLater = {
                            setFirstLaunchComplete(this@MainActivity)
                            showApiKeyDialog = false
                        }
                    )
                }
                
                // Set up settings change listener to refresh main screen
                LaunchedEffect(Unit) {
                    settingsViewModel.setSettingsChangeListener(object : SettingsViewModel.SettingsChangeListener {
                        override fun onSettingsChanged() {
                            sharedViewModel.refreshApiConfig()
                        }
                    })
                }
                
                // Handle deep links from intent
                LaunchedEffect(Unit) {
                    intent.data?.let { deepLink ->
                        handleDeepLink(deepLink, navController)
                    }
                }
                
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            isApiConfigured = isApiConfigured,
                            onNavigateToVoiceDiscovery = {
                                navController.navigate("voice_discovery")
                            },
                            onNavigateToCustomVoices = {
                                navController.navigate("custom_voices")
                            },
                            onNavigateToSettings = {
                                navController.navigate("settings")
                            },
                            onNavigateToLogs = {
                                navController.navigate("logs")
                            }
                        )
                    }
                    
                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                    
                    composable("voice_discovery") {
                        VoiceDiscoveryScreen(
                            viewModel = sharedViewModel,
                            onNavigateBack = {
                                navController.navigateUp()
                            },
                            onAddToFavorites = { voiceId, voiceName, description ->
                                sharedViewModel.addFavoriteVoice(voiceId, voiceName, description)
                            }
                        )
                    }

                    composable("custom_voices") {
                        CustomVoicesScreen(
                            voices = kotlinx.coroutines.flow.flowOf(favoriteVoices),
                            defaultVoiceId = defaultVoice?.id,
                            currentlyPlayingVoiceId = currentlyPlayingVoiceId,
                            onNavigateBack = {
                                navController.navigateUp()
                            },
                            onSetDefault = { voiceId ->
                                sharedViewModel.setDefaultVoice(voiceId)
                            },
                            onRemoveVoice = { voiceId ->
                                sharedViewModel.removeFavoriteVoice(voiceId)
                            },
                            onAddVoiceById = { voiceId, nickname ->
                                sharedViewModel.addFavoriteVoice(voiceId, nickname, "Custom voice")
                            },
                            onPlayVoice = { voiceId ->
                                sharedViewModel.playVoicePreview(voiceId)
                            },
                            onStopVoice = {
                                sharedViewModel.stopVoicePreview()
                            }
                        )
                    }
                    
                    composable("logs") {
                        LogsScreen(
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::logger.isInitialized) {
            logger.i("MainActivity", "App shutting down")
        }
    }
    
    /**
     * Handle deep links from the app scheme
     * Supports:
     * - fishaudiotts://speak?voice=nickname&text=text
     * - fishaudiotts://settings
     * - fishaudiotts://discover
     */
    private fun handleDeepLink(deepLink: Uri, navController: NavHostController) {
        when (deepLink.scheme) {
            "fishaudiotts" -> {
                when (deepLink.host) {
                    "speak" -> {
                        // Navigate to home and potentially trigger speak action
                        val voiceNickname = deepLink.getQueryParameter("voice")
                        val text = deepLink.getQueryParameter("text")
                        // Could pass arguments to home screen via bundle or SharedViewModel
                        navController.navigate("home")
                    }
                    "settings" -> {
                        navController.navigate("settings")
                    }
                    "discover" -> {
                        navController.navigate("voice_discovery")
                    }
                    else -> {
                        navController.navigate("home")
                    }
                }
            }
            else -> {
                navController.navigate("home")
            }
        }
    }
}
