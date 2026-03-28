package com.example.fishaudiotts.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.util.Log

/**
 * Encrypted SharedPreferences manager for storing sensitive settings
 * 
 * Uses lazy initialization for MasterKey and EncryptedSharedPreferences to prevent
 * blocking the app startup. Encryption key generation and preference file initialization
 * are deferred until first access, keeping the critical startup path fast.
 */
class PreferencesManager(private val context: Context) {
    companion object {
        private const val TAG = "PreferencesManager"
        private const val PREFS_NAME = "fish_audio_prefs"
        
        // Keys
        const val KEY_API_KEY = "api_key"
        const val KEY_TTS_MODEL = "tts_model"
        const val KEY_VOICE_SPEED = "voice_speed"
        const val KEY_VOICE_VOLUME = "voice_volume"
        const val KEY_EMOTION_ENABLED = "emotion_enabled"
        const val KEY_DEFAULT_VOICE_ID = "default_voice_id"
        const val KEY_TEMPERATURE = "temperature"
        const val KEY_TOP_P = "top_p"
        const val KEY_AUDIO_FORMAT = "audio_format"
        const val KEY_SAMPLE_RATE = "sample_rate"
        const val KEY_NORMALIZE_TEXT = "normalize_text"
        const val KEY_LATENCY_MODE = "latency_mode"
    }
    
    // Lazy initialization prevents blocking app startup
    // MasterKey generation is deferred until first preferences access
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    // Lazy initialization prevents blocking app startup
    // EncryptedSharedPreferences initialization is deferred until first preferences access
    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    // API Key Management
    fun setApiKey(apiKey: String) {
        try {
            encryptedPrefs.edit().putString(KEY_API_KEY, apiKey).apply()
            Log.d(TAG, "API key saved")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save API key: ${e.message}", e)
        }
    }
    
    fun getApiKey(): String? {
        return try {
            encryptedPrefs.getString(KEY_API_KEY, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get API key: ${e.message}", e)
            null
        }
    }
    
    fun clearApiKey() {
        encryptedPrefs.edit().remove(KEY_API_KEY).apply()
    }
    
    // TTS Model Management
    fun setTtsModel(model: String) {
        encryptedPrefs.edit().putString(KEY_TTS_MODEL, model).apply()
    }
    
    fun getTtsModel(): String {
        return encryptedPrefs.getString(KEY_TTS_MODEL, Constants.MODEL_S2_PRO) ?: Constants.MODEL_S2_PRO
    }
    
    // Voice Speed Management
    fun setVoiceSpeed(speed: Float) {
        encryptedPrefs.edit().putFloat(KEY_VOICE_SPEED, speed).apply()
    }
    
    fun getVoiceSpeed(): Float {
        return encryptedPrefs.getFloat(KEY_VOICE_SPEED, 1.0f)
    }
    
    // Voice Volume Management
    fun setVoiceVolume(volume: Float) {
        encryptedPrefs.edit().putFloat(KEY_VOICE_VOLUME, volume).apply()
    }
    
    fun getVoiceVolume(): Float {
        return encryptedPrefs.getFloat(KEY_VOICE_VOLUME, 0.0f)
    }
    
    // Emotion Control (S2-Pro feature)
    fun setEmotionEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_EMOTION_ENABLED, enabled).apply()
    }
    
    fun isEmotionEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_EMOTION_ENABLED, false)
    }
    
    // Default Voice
    fun setDefaultVoiceId(voiceId: String) {
        encryptedPrefs.edit().putString(KEY_DEFAULT_VOICE_ID, voiceId).apply()
    }
    
    fun getDefaultVoiceId(): String? {
        return encryptedPrefs.getString(KEY_DEFAULT_VOICE_ID, null)
    }
    
    // Temperature (expressiveness)
    fun setTemperature(temperature: Float) {
        encryptedPrefs.edit().putFloat(KEY_TEMPERATURE, temperature).apply()
    }
    
    fun getTemperature(): Float {
        return encryptedPrefs.getFloat(KEY_TEMPERATURE, 0.7f)
    }
    
    // Top P (diversity)
    fun setTopP(topP: Float) {
        encryptedPrefs.edit().putFloat(KEY_TOP_P, topP).apply()
    }
    
    fun getTopP(): Float {
        return encryptedPrefs.getFloat(KEY_TOP_P, 0.7f)
    }
    
    // Audio Format
    fun setAudioFormat(format: String) {
        encryptedPrefs.edit().putString(KEY_AUDIO_FORMAT, format).apply()
    }
    
    fun getAudioFormat(): String {
        return encryptedPrefs.getString(KEY_AUDIO_FORMAT, Constants.FORMAT_MP3) ?: Constants.FORMAT_MP3
    }
    
    // Sample Rate
    fun setSampleRate(sampleRate: Int) {
        encryptedPrefs.edit().putInt(KEY_SAMPLE_RATE, sampleRate).apply()
    }
    
    fun getSampleRate(): Int {
        return encryptedPrefs.getInt(KEY_SAMPLE_RATE, 44100)
    }
    
    // Text Normalization
    fun setNormalizeText(normalize: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_NORMALIZE_TEXT, normalize).apply()
    }
    
    fun getNormalizeText(): Boolean {
        return encryptedPrefs.getBoolean(KEY_NORMALIZE_TEXT, true)
    }
    
    // Latency Mode
    fun setLatencyMode(mode: String) {
        encryptedPrefs.edit().putString(KEY_LATENCY_MODE, mode).apply()
    }
    
    fun getLatencyMode(): String {
        return encryptedPrefs.getString(KEY_LATENCY_MODE, "normal") ?: "normal"
    }
    
    // Clear all preferences
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
        Log.d(TAG, "All preferences cleared")
    }
    
    // Check if app is configured
    fun isConfigured(): Boolean {
        return !getApiKey().isNullOrEmpty()
    }
}
