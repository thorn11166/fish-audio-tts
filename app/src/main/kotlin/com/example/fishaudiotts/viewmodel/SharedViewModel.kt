package com.example.fishaudiotts.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fishaudiotts.data.api.FishAudioApiClient
import com.example.fishaudiotts.data.api.models.VoiceModel
import com.example.fishaudiotts.data.db.AppDatabase
import com.example.fishaudiotts.data.db.VoiceEntity
import com.example.fishaudiotts.data.repository.VoiceRepository
import com.example.fishaudiotts.util.AudioPlayer
import com.example.fishaudiotts.util.FileLogger
import com.example.fishaudiotts.util.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel for app-wide state management
 * Handles initialization and common operations
 */
class SharedViewModel(context: Context) : ViewModel() {

    private val logger = FileLogger.getInstance(context)
    private val preferencesManager = PreferencesManager(context)
    private val database = AppDatabase.getInstance(context)
    private var repository: VoiceRepository? = null

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

    // Voice search state
    private val _searchResults = MutableStateFlow<List<VoiceModel>>(emptyList())
    val searchResults: StateFlow<List<VoiceModel>> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    // Audio playback state
    private val _currentlyPlayingVoiceId = MutableStateFlow<String?>(null)
    val currentlyPlayingVoiceId: StateFlow<String?> = _currentlyPlayingVoiceId

    // Favorite voices
    private val _favoriteVoices = MutableStateFlow<List<VoiceEntity>>(emptyList())
    val favoriteVoices: StateFlow<List<VoiceEntity>> = _favoriteVoices

    private val audioPlayer = AudioPlayer(context)

    init {
        initializeApp()
    }

    /**
     * Refresh API configuration (call when settings change)
     */
    fun refreshApiConfig() {
        viewModelScope.launch {
            val apiKey = preferencesManager.getApiKey()
            _isApiConfigured.value = !apiKey.isNullOrEmpty()

            if (!apiKey.isNullOrEmpty()) {
                initRepository(apiKey)
            }

            // Reload favorite voices
            loadFavoriteVoices()
        }
    }

    /**
     * Load favorite voices from database
     */
    private fun loadFavoriteVoices() {
        viewModelScope.launch {
            repository?.getAllFavoriteVoices()?.collect { voices ->
                _favoriteVoices.value = voices
            }
        }
    }

    /**
     * Add a voice to favorites
     */
    fun addFavoriteVoice(voiceId: String, nickname: String, description: String = "") {
        viewModelScope.launch {
            val repo = repository
            if (repo == null) {
                val apiKey = preferencesManager.getApiKey()
                if (!apiKey.isNullOrEmpty()) {
                    initRepository(apiKey)
                }
            }

            val voice = VoiceEntity(
                voiceId = voiceId,
                nickname = nickname,
                description = description,
                isDefault = _favoriteVoices.value.isEmpty() // First voice becomes default
            )

            val result = repository?.addFavoriteVoice(voice)
            if (result == true) {
                logger.d("SharedViewModel", "Added voice to favorites: $voiceId")
                loadFavoriteVoices()
            } else {
                _errorMessage.value = "Failed to add voice (max 5 favorites reached)"
            }
        }
    }

    /**
     * Remove a voice from favorites
     */
    fun removeFavoriteVoice(voiceId: String) {
        viewModelScope.launch {
            repository?.removeFavoriteVoice(voiceId)
            logger.d("SharedViewModel", "Removed voice from favorites: $voiceId")
            loadFavoriteVoices()
        }
    }

    /**
     * Set a voice as the default
     */
    fun setDefaultVoice(voiceId: String) {
        viewModelScope.launch {
            repository?.setDefaultVoice(voiceId)
            logger.d("SharedViewModel", "Set default voice: $voiceId")
            loadFavoriteVoices()
        }
    }

    private fun initializeApp() {
        viewModelScope.launch {
            // Check if API is configured
            val apiKey = preferencesManager.getApiKey()
            _isApiConfigured.value = !apiKey.isNullOrEmpty()

            // Initialize repository
            if (!apiKey.isNullOrEmpty()) {
                initRepository(apiKey)

                // Load default voice
                repository?.getDefaultVoice()?.collect { voice ->
                    _defaultVoice.value = voice
                }
            } else {
                // Still need repository for local database operations
                initRepository("")
            }

            _currentTtsModel.value = preferencesManager.getTtsModel()

            // Load favorite voices
            loadFavoriteVoices()
        }
    }

    /**
     * Initialize repository with API key
     */
    private fun initRepository(apiKey: String) {
        val apiClient = FishAudioApiClient(apiKey, preferencesManager.getTtsModel())
        repository = VoiceRepository(database, apiClient)
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
                initRepository(apiKey)

                // Validate key
                val isValid = repository?.validateApiKey() ?: false
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

    // Demo voices as fallback when API search fails
    private val demoVoices = listOf(
        VoiceModel("8ef4a238714b45718ce04243307c57a7", "E-girl", "Young, energetic female voice", listOf("english", "female"), "en", "cheerful"),
        VoiceModel("802e3bc2b27e49c2995d23ef70e6ac89", "Energetic Male", "Upbeat, motivated male voice", listOf("english", "male"), "en", "happy"),
        VoiceModel("933563129e564b19a115bedd57b7406a", "Sarah", "Professional, calm female voice", listOf("english", "female"), "en", "neutral"),
        VoiceModel("bf322df2096a46f18c579d0baa36f41d", "Adrian", "Deep, authoritative male voice", listOf("english", "male"), "en", "serious"),
        VoiceModel("b347db033a6549378b48d00acb0d06cd", "Selene", "Mystical, soothing female voice", listOf("english", "female"), "en", "calm")
    )

    /**
     * Search voices - falls back to demo voices since Fish Audio doesn't have a voice search API
     */
    fun searchVoices(query: String = "") {
        viewModelScope.launch {
            logger.d("SharedViewModel", "searchVoices called with query: '$query'")

            _isSearching.value = true
            _errorMessage.value = null

            try {
                // Filter demo voices based on query
                val filtered = if (query.isEmpty()) {
                    demoVoices
                } else {
                    demoVoices.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
                    }
                }

                logger.d("SharedViewModel", "Showing ${filtered.size} demo voices")
                _searchResults.value = filtered

                // Note: Fish Audio doesn't have a public voice search API
                // Using curated demo voices instead
            } catch (e: Exception) {
                logger.e("SharedViewModel", "Search error", e)
                _errorMessage.value = "Search error: ${e.message}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    /**
     * Play voice preview
     */
    fun playVoicePreview(voiceId: String) {
        viewModelScope.launch {
            if (!_isApiConfigured.value) {
                _errorMessage.value = "API key not configured"
                return@launch
            }

            // Initialize repository if needed
            val apiKey = preferencesManager.getApiKey()
            if (repository == null && !apiKey.isNullOrEmpty()) {
                initRepository(apiKey)
            }

            val repo = repository
            if (repo == null) {
                _errorMessage.value = "Repository not initialized"
                return@launch
            }

            // Stop if already playing this voice
            if (_currentlyPlayingVoiceId.value == voiceId && audioPlayer.isPlaying()) {
                stopVoicePreview()
                return@launch
            }

            _currentlyPlayingVoiceId.value = voiceId
            _errorMessage.value = null

            try {
                val result = repo.generateVoicePreview(voiceId)
                result.onSuccess { audioStream ->
                    audioPlayer.playPreview(audioStream) {
                        // On complete
                        _currentlyPlayingVoiceId.value = null
                    }
                }.onFailure { error ->
                    _errorMessage.value = "Preview failed: ${error.message}"
                    _currentlyPlayingVoiceId.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Preview error: ${e.message}"
                _currentlyPlayingVoiceId.value = null
            }
        }
    }

    /**
     * Stop voice preview
     */
    fun stopVoicePreview() {
        audioPlayer.stop()
        _currentlyPlayingVoiceId.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
