package com.example.fishaudiotts.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fishaudiotts.data.api.FishAudioApiClient
import com.example.fishaudiotts.data.db.AppDatabase
import com.example.fishaudiotts.data.db.VoiceEntity
import com.example.fishaudiotts.data.repository.VoiceRepository
import com.example.fishaudiotts.util.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel for app-wide state management
 * Handles initialization and common operations
 */
class SharedViewModel(context: Context) : ViewModel() {
    
    private val preferencesManager = PreferencesManager(context)
    private val database = AppDatabase.getInstance(context)
    private lateinit var repository: VoiceRepository
    
    private val _isApiConfigured = MutableStateFlow(false)
    val isApiConfigured: StateFlow<Boolean> = _isApiConfigured
    
    private val _currentTtsModel = MutableStateFlow("s2-pro")
    val currentTtsModel: StateFlow<String> = _currentTtsModel
    
    private val _defaultVoice = MutableStateFlow<VoiceEntity?>(null)
    val defaultVoice: StateFlow<VoiceEntity?> = _defaultVoice
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    init {
        initializeApp()
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            // Check if API is configured
            val apiKey = preferencesManager.getApiKey()
            _isApiConfigured.value = !apiKey.isNullOrEmpty()
            
            // Initialize repository
            if (!apiKey.isNullOrEmpty()) {
                val apiClient = FishAudioApiClient(apiKey, preferencesManager.getTtsModel())
                repository = VoiceRepository(database, apiClient)
                
                // Load default voice
                repository.getDefaultVoice().collect { voice ->
                    _defaultVoice.value = voice
                }
            }
            
            _currentTtsModel.value = preferencesManager.getTtsModel()
        }
    }
    
    /**
     * Set up API with new key
     */
    fun setupApi(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Create client and validate
                val apiClient = FishAudioApiClient(apiKey, preferencesManager.getTtsModel())
                repository = VoiceRepository(database, apiClient)
                
                // Validate key
                val isValid = repository.validateApiKey()
                if (isValid) {
                    preferencesManager.setApiKey(apiKey)
                    _isApiConfigured.value = true
                } else {
                    _errorMessage.value = "Invalid API key"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to configure API: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update TTS model
     */
    fun setTtsModel(model: String) {
        preferencesManager.setTtsModel(model)
        _currentTtsModel.value = model
        
        // Reinitialize repository with new model
        if (_isApiConfigured.value) {
            val apiKey = preferencesManager.getApiKey()
            if (!apiKey.isNullOrEmpty()) {
                val apiClient = FishAudioApiClient(apiKey, model)
                repository = VoiceRepository(database, apiClient)
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
