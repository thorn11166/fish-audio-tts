package com.example.fishaudiotts.service

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.SynthesisCallback
import android.speech.tts.SynthesisRequest
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.util.Log
import com.example.fishaudiotts.data.api.FishAudioApiClient
import com.example.fishaudiotts.data.db.AppDatabase
import com.example.fishaudiotts.data.repository.VoiceRepository
import com.example.fishaudiotts.util.PreferencesManager
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

/**
 * Android TTS Engine Service for Fish Audio
 * Allows the app to be used as a system TTS engine
 */
class FishAudioTTSService : TextToSpeechService() {
    companion object {
        private const val TAG = "FishAudioTTS"
    }

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var database: AppDatabase
    private var repository: VoiceRepository? = null
    private var currentMediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "TTS Service created")
        preferencesManager = PreferencesManager(applicationContext)
        database = AppDatabase.getInstance(applicationContext)
        initRepository()
    }

    private fun initRepository() {
        val apiKey = preferencesManager.getApiKey()
        if (!apiKey.isNullOrEmpty()) {
            val apiClient = FishAudioApiClient(apiKey, preferencesManager.getTtsModel())
            repository = VoiceRepository(database, apiClient)
            Log.d(TAG, "Repository initialized")
        } else {
            Log.w(TAG, "No API key configured")
        }
    }

    /**
     * Called by the system to check if the engine is ready
     */
    override fun onIsLanguageAvailable(lang: String?, country: String?, variant: String?): Int {
        Log.d(TAG, "Checking language: $lang-$country-$variant")
        // Fish Audio supports many languages, return AVAILABLE for common ones
        return when (lang?.lowercase()) {
            "en", "zh", "ja", "ko", "es", "fr", "de", "it", "pt", "ru" -> TextToSpeech.LANG_AVAILABLE
            else -> TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /**
     * Get the default language for this engine
     */
    override fun onGetLanguage(): Array<String> {
        return arrayOf("en", "US", "")
    }

    /**
     * Get a list of voices supported by this engine
     */
    override fun onGetVoices(): List<android.speech.tts.Voice>? {
        Log.d(TAG, "Getting voices")
        val voices = mutableListOf<android.speech.tts.Voice>()

        // Add default voices
        voices.add(
            android.speech.tts.Voice(
                "fish_default",
                java.util.Locale("en", "US"),
                android.speech.tts.Voice.QUALITY_HIGH,
                android.speech.tts.Voice.LATENCY_LOW,
                false,
                null
            )
        )

        // Add favorite voices from database
        val repo = repository
        if (repo != null) {
            try {
                runBlocking {
                    val favoriteVoices = repo.getAllFavoriteVoices()
                    favoriteVoices.collect { voicesList ->
                        voicesList.forEach { voice ->
                            voices.add(
                                android.speech.tts.Voice(
                                    voice.referenceId,
                                    java.util.Locale("en", "US"),
                                    android.speech.tts.Voice.QUALITY_HIGH,
                                    android.speech.tts.Voice.LATENCY_LOW,
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading voices: ${e.message}")
            }
        }

        return voices
    }

    /**
     * Load a specific language
     */
    override fun onLoadLanguage(lang: String?, country: String?, variant: String?): Int {
        Log.d(TAG, "Loading language: $lang-$country-$variant")
        return onIsLanguageAvailable(lang, country, variant)
    }

    /**
     * Stop any ongoing speech
     */
    override fun onStop() {
        Log.d(TAG, "Stop requested")
        try {
            currentMediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            currentMediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping playback: ${e.message}")
        }
    }

    /**
     * Synthesize speech from text
     * This is the main method called by the system
     */
    override fun onSynthesizeText(request: SynthesisRequest?, callback: SynthesisCallback?) {
        if (request == null || callback == null) {
            Log.e(TAG, "Invalid request or callback")
            callback?.error(TextToSpeech.ERROR_INVALID_REQUEST)
            return
        }

        val text = request.charSequenceText?.toString() ?: ""
        if (text.isEmpty()) {
            Log.w(TAG, "Empty text provided")
            callback.done()
            return
        }

        Log.d(TAG, "Synthesizing: $text")

        val repo = repository
        if (repo == null) {
            Log.e(TAG, "Repository not initialized - API key may be missing")
            callback.error(TextToSpeech.ERROR_NETWORK)
            return
        }

        // Get the voice to use (default or requested)
        val voiceId = request.voiceName ?: runBlocking {
            repo.getDefaultVoice().first()?.referenceId
        } ?: run {
            Log.w(TAG, "No default voice set")
            callback.error(TextToSpeech.ERROR_INVALID_REQUEST)
            return
        }

        try {
            // Generate speech
            runBlocking {
                val result = repo.generateSpeech(
                    text = text,
                    referenceId = voiceId,
                    format = "wav"  // Use WAV for better compatibility
                )

                result.onSuccess { audioStream ->
                    // Save to temp file
                    val tempFile = File.createTempFile("tts_output", ".wav", cacheDir)
                    FileOutputStream(tempFile).use { output ->
                        audioStream.copyTo(output)
                    }

                    // Play audio through callback
                    playAudioFile(tempFile, callback)
                }.onFailure { error ->
                    Log.e(TAG, "Speech generation failed: ${error.message}")
                    callback.error(TextToSpeech.ERROR_NETWORK)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Synthesis error: ${e.message}", e)
            callback.error(TextToSpeech.ERROR_NETWORK)
        }
    }

    private fun playAudioFile(audioFile: File, callback: SynthesisCallback) {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            currentMediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                setDataSource(audioFile.absolutePath)
                prepare()

                // Get audio format info
                val sampleRate = 44100  // Fish Audio default
                val audioFormat = android.media.AudioFormat.ENCODING_PCM_16BIT

                // Start synthesis callback
                callback.start(sampleRate, audioFormat, 1)  // 1 channel = mono

                // Read audio data and send to callback
                // Note: This is a simplified version - in production you'd decode the WAV properly
                val buffer = ByteArray(8192)
                audioFile.inputStream().use { input ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        callback.audioAvailable(buffer, 0, bytesRead)
                    }
                }

                callback.done()
            }

            // Clean up temp file
            audioFile.delete()

        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio: ${e.message}")
            callback.error(TextToSpeech.ERROR_OUTPUT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onStop()
        Log.d(TAG, "TTS Service destroyed")
    }
}
