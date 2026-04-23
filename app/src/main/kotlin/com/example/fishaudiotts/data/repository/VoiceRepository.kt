package com.example.fishaudiotts.data.repository

import android.util.Log
import com.example.fishaudiotts.data.api.FishAudioApiClient
import com.example.fishaudiotts.data.db.AppDatabase
import com.example.fishaudiotts.data.db.VoiceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.InputStream

/**
 * Repository pattern implementation for voice operations
 * Abstracts data access from database and API layers
 */
class VoiceRepository(
    private val database: AppDatabase,
    private val apiClient: FishAudioApiClient
) {
    companion object {
        private const val TAG = "VoiceRepository"
        private const val MAX_FAVORITES = 5
    }
    
    private val voiceDao = database.voiceDao()
    
    /**
     * Add a voice to favorites
     * Enforces max 5 favorite voices limit
     */
    suspend fun addFavoriteVoice(voice: VoiceEntity): Boolean {
        return try {
            // Collect the Flow<Int> to get the current voice count
            val count = withContext(Dispatchers.IO) {
                voiceDao.getVoiceCount().first()
            }
            
            // Enforce 5-voice limit
            if (count >= MAX_FAVORITES) {
                Log.w(TAG, "Cannot add voice: maximum favorites ($MAX_FAVORITES) reached")
                return false
            }
            
            if (voice.isDefault) {
                voiceDao.clearDefaultVoices()
            }
            voiceDao.insert(voice)
            Log.d(TAG, "Voice added: ${voice.nickname}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add voice: ${e.message}", e)
            false
        }
    }
    
    /**
     * Remove voice from favorites
     */
    suspend fun removeFavoriteVoice(voiceId: String): Boolean {
        return try {
            voiceDao.deleteVoiceById(voiceId)
            Log.d(TAG, "Voice removed: $voiceId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove voice: ${e.message}", e)
            false
        }
    }
    
    /**
     * Update voice nickname
     */
    suspend fun updateVoiceNickname(voiceId: String, newNickname: String): Boolean {
        return try {
            val voice = voiceDao.getVoiceById(voiceId) ?: return false
            voiceDao.update(voice.copy(nickname = newNickname))
            Log.d(TAG, "Voice renamed: $voiceId -> $newNickname")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update voice: ${e.message}", e)
            false
        }
    }
    
    /**
     * Set voice as default
     */
    suspend fun setDefaultVoice(voiceId: String): Boolean {
        return try {
            voiceDao.clearDefaultVoices()
            voiceDao.setDefaultVoice(voiceId)
            Log.d(TAG, "Default voice set: $voiceId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set default voice: ${e.message}", e)
            false
        }
    }
    
    /**
     * Get all favorite voices as Flow
     */
    fun getAllFavoriteVoices(): Flow<List<VoiceEntity>> {
        return voiceDao.getAllVoices()
    }
    
    /**
     * Get default voice as Flow
     */
    fun getDefaultVoice(): Flow<VoiceEntity?> {
        return voiceDao.getDefaultVoice()
    }
    
    /**
     * Get voice count as Flow
     */
    fun getVoiceCount(): Flow<Int> {
        return voiceDao.getVoiceCount()
    }
    
    /**
     * Generate TTS audio from text
     * Returns InputStream for audio playback or saving
     */
    suspend fun generateSpeech(
        text: String,
        referenceId: String? = null,
        prosodySpeed: Double = 1.0,
        prosodyVolume: Double = 0.0,
        format: String = "mp3",
        sampleRate: Int? = 44100,
        temperature: Double = 0.7,
        topP: Double = 0.7
    ): Result<InputStream> {
        return try {
            val audioStream = apiClient.generateSpeech(
                text = text,
                referenceId = referenceId,
                prosodySpeed = prosodySpeed,
                prosodyVolume = prosodyVolume,
                format = format,
                sampleRate = sampleRate,
                temperature = temperature,
                topP = topP
            )
            Result.success(audioStream)
        } catch (e: Exception) {
            Log.e(TAG, "Speech generation failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Validate API key
     */
    suspend fun validateApiKey(): Boolean {
        return try {
            apiClient.validateApiKey()
        } catch (e: Exception) {
            Log.e(TAG, "API key validation failed: ${e.message}", e)
            false
        }
    }

    /**
     * Search voices from Fish Audio library
     */
    suspend fun searchVoices(query: String? = null, page: Int = 1, perPage: Int = 20): Result<com.example.fishaudiotts.data.api.models.VoiceListResponse> {
        return try {
            val response = apiClient.searchVoices(query = query, page = page, perPage = perPage)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Voice search failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Generate voice preview (short TTS sample)
     */
    suspend fun generateVoicePreview(voiceId: String): Result<java.io.InputStream> {
        return try {
            val previewText = "Hello! This is my voice. I hope you like how I sound."
            val audioStream = apiClient.generateSpeech(
                text = previewText,
                referenceId = voiceId,
                prosodySpeed = 1.0,
                prosodyVolume = 0.0,
                format = "mp3",
                sampleRate = 44100
            )
            Result.success(audioStream)
        } catch (e: Exception) {
            Log.e(TAG, "Voice preview generation failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
