package com.example.fishaudiotts.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fishaudiotts.util.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings screen
 * Manages TTS parameters and API configuration
 */
class SettingsViewModel(context: Context) : ViewModel() {
    
    private val preferencesManager = PreferencesManager(context)
    
    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey
    
    private val _ttsModel = MutableStateFlow("s2-pro")
    val ttsModel: StateFlow<String> = _ttsModel
    
    private val _voiceSpeed = MutableStateFlow(1.0f)
    val voiceSpeed: StateFlow<Float> = _voiceSpeed
    
    private val _voiceVolume = MutableStateFlow(0.0f)
    val voiceVolume: StateFlow<Float> = _voiceVolume
    
    private val _emotionEnabled = MutableStateFlow(false)
    val emotionEnabled: StateFlow<Boolean> = _emotionEnabled
    
    private val _temperature = MutableStateFlow(0.7f)
    val temperature: StateFlow<Float> = _temperature
    
    private val _topP = MutableStateFlow(0.7f)
    val topP: StateFlow<Float> = _topP
    
    private val _audioFormat = MutableStateFlow("mp3")
    val audioFormat: StateFlow<String> = _audioFormat
    
    private val _sampleRate = MutableStateFlow(44100)
    val sampleRate: StateFlow<Int> = _sampleRate
    
    private val _latencyMode = MutableStateFlow("normal")
    val latencyMode: StateFlow<String> = _latencyMode
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving
    
    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage: StateFlow<String?> = _saveMessage
    
    init {
        // Load settings synchronously in init block
        // PreferencesManager calls are fast (local SharedPreferences <5ms) - no coroutine needed
        // This ensures all settings are loaded before UI renders, preventing race conditions
        loadSettingsSynchronously()
    }
    
    private fun loadSettingsSynchronously() {
        _apiKey.value = preferencesManager.getApiKey() ?: ""
        _ttsModel.value = preferencesManager.getTtsModel()
        _voiceSpeed.value = preferencesManager.getVoiceSpeed()
        _voiceVolume.value = preferencesManager.getVoiceVolume()
        _emotionEnabled.value = preferencesManager.isEmotionEnabled()
        _temperature.value = preferencesManager.getTemperature()
        _topP.value = preferencesManager.getTopP()
        _audioFormat.value = preferencesManager.getAudioFormat()
        _sampleRate.value = preferencesManager.getSampleRate()
        _latencyMode.value = preferencesManager.getLatencyMode()
    }
    
    fun updateApiKey(key: String) {
        _apiKey.value = key
    }
    
    fun updateTtsModel(model: String) {
        _ttsModel.value = model
    }
    
    fun updateVoiceSpeed(speed: Float) {
        _voiceSpeed.value = speed
    }
    
    fun updateVoiceVolume(volume: Float) {
        _voiceVolume.value = volume
    }
    
    fun updateEmotionEnabled(enabled: Boolean) {
        _emotionEnabled.value = enabled
    }
    
    fun updateTemperature(temp: Float) {
        _temperature.value = temp
    }
    
    fun updateTopP(p: Float) {
        _topP.value = p
    }
    
    fun updateAudioFormat(format: String) {
        _audioFormat.value = format
    }
    
    fun updateSampleRate(rate: Int) {
        _sampleRate.value = rate
    }
    
    fun updateLatencyMode(mode: String) {
        _latencyMode.value = mode
    }
    
    fun saveSettings() {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                preferencesManager.setApiKey(_apiKey.value)
                preferencesManager.setTtsModel(_ttsModel.value)
                preferencesManager.setVoiceSpeed(_voiceSpeed.value)
                preferencesManager.setVoiceVolume(_voiceVolume.value)
                preferencesManager.setEmotionEnabled(_emotionEnabled.value)
                preferencesManager.setTemperature(_temperature.value)
                preferencesManager.setTopP(_topP.value)
                preferencesManager.setAudioFormat(_audioFormat.value)
                preferencesManager.setSampleRate(_sampleRate.value)
                preferencesManager.setLatencyMode(_latencyMode.value)
                
                _saveMessage.value = "Settings saved successfully!"
            } catch (e: Exception) {
                _saveMessage.value = "Failed to save settings: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    fun clearSaveMessage() {
        _saveMessage.value = null
    }
}
