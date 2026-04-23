package com.example.fishaudiotts

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install crash handler
        CrashHandler.install(this)
        
        // Initialize logger
        logger = FileLogger.getInstance(this)
        logger.i("MainActivity", "App starting...")
        
        try {
        
        setContent {
            FishAudioTTSTheme {
                val navController = rememberNavController()
                val sharedViewModel = SharedViewModel(this@MainActivity)
                val settingsViewModel = SettingsViewModel(this@MainActivity)
                
                val isApiConfigured by sharedViewModel.isApiConfigured.collectAsState()
                
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
                            onFavoriteVoice = { id, name ->
                                // TODO: Add to favorites
                            }
                        )
                    }
                    
                    composable("custom_voices") {
                        CustomVoicesScreen(
                            voices = kotlinx.coroutines.flow.flowOf(emptyList()),
                            defaultVoiceId = null,
                            onNavigateBack = {
                                navController.navigateUp()
                            },
                            onSetDefault = { voiceId ->
                                // TODO: Set default voice
                            },
                            onRemoveVoice = { voiceId ->
                                // TODO: Remove voice
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
        } catch (e: Exception) {
            logger.e("MainActivity", "Fatal error in onCreate", e)
            throw e
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
