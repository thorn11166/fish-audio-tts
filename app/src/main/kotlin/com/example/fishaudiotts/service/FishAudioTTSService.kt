package com.example.fishaudiotts.service

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
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
import java.nio.ByteBuffer

/**
 * Android TTS Engine Service for Fish Audio
 * Allows the app to be used as a system TTS engine
 */
class FishAudioTTSService : TextToSpeechService() {
    companion object {
        private const val TAG = "FishAudioTTS"
        private const val SAMPLE_RATE = 44100
        private const val AUDIO_FORMAT = android.media.AudioFormat.ENCODING_PCM_16BIT
        private const val CHANNEL_COUNT = 1 // Mono
    }

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var database: AppDatabase
    private var repository: VoiceRepository? = null

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

    override fun onIsLanguageAvailable(lang: String?, country: String?, variant: String?): Int {
        Log.d(TAG, "Checking language: $lang-$country-$variant")
        return TextToSpeech.LANG_AVAILABLE
    }

    override fun onGetLanguage(): Array<String> {
        return arrayOf("en", "US", "")
    }

    override fun onLoadLanguage(lang: String?, country: String?, variant: String?): Int {
        Log.d(TAG, "Loading language: $lang-$country-$variant")
        return onIsLanguageAvailable(lang, country, variant)
    }

    override fun onStop() {
        Log.d(TAG, "Stop requested")
    }

    override fun onSynthesizeText(request: SynthesisRequest?, callback: SynthesisCallback?) {
        if (request == null || callback == null) {
            Log.e(TAG, "Invalid request or callback")
            callback?.error(TextToSpeech.ERROR_INVALID_REQUEST)
            return
        }

        val text = request.charSequenceText?.toString() ?: ""
        if (text.isEmpty()) {
            callback.done()
            return
        }

        Log.d(TAG, "Synthesizing: $text")

        // Re-init repository in case API key was added
        if (repository == null) {
            initRepository()
        }

        val repo = repository
        if (repo == null) {
            Log.e(TAG, "Repository not initialized - API key missing")
            callback.error(TextToSpeech.ERROR_NETWORK)
            return
        }

        try {
            // Start synthesis
            callback.start(SAMPLE_RATE, AUDIO_FORMAT, CHANNEL_COUNT)

            // Generate speech using blocking call (TTS service requires synchronous operation)
            val audioFile = runBlocking {
                generateSpeechFile(repo, text)
            }

            if (audioFile == null) {
                Log.e(TAG, "Failed to generate speech")
                callback.error(TextToSpeech.ERROR_NETWORK)
                return
            }

            // Decode and stream audio
            decodeAndStreamAudio(audioFile, callback)

            // Clean up
            audioFile.delete()

            callback.done()
            Log.d(TAG, "Synthesis complete")

        } catch (e: Exception) {
            Log.e(TAG, "Synthesis error: ${e.message}", e)
            callback.error(TextToSpeech.ERROR_NETWORK)
        }
    }

    private suspend fun generateSpeechFile(repo: VoiceRepository, text: String): File? {
        return try {
            // Get default voice or use null for default TTS
            val defaultVoice = repo.getDefaultVoice()
            val voiceId = defaultVoice.first()?.referenceId

            val result = repo.generateSpeech(
                text = text,
                referenceId = voiceId,
                format = "mp3",
                sampleRate = SAMPLE_RATE
            )

            result.getOrNull()?.let { audioStream ->
                val tempFile = File.createTempFile("tts_", ".mp3", cacheDir)
                FileOutputStream(tempFile).use { output ->
                    audioStream.copyTo(output)
                }
                tempFile
            }
        } catch (e: Exception) {
            Log.e(TAG, "Speech generation failed: ${e.message}")
            null
        }
    }

    private fun decodeAndStreamAudio(audioFile: File, callback: SynthesisCallback) {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null

        try {
            extractor.setDataSource(audioFile.absolutePath)

            val trackIndex = (0 until extractor.trackCount).firstOrNull { i ->
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                mime?.startsWith("audio/") == true
            } ?: throw IllegalStateException("No audio track found")

            extractor.selectTrack(trackIndex)
            val format = extractor.getTrackFormat(trackIndex)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: "audio/mp3"

            Log.d(TAG, "Decoding audio: $mime")

            codec = MediaCodec.createDecoderByType(mime)
            codec.configure(format, null, null, 0)
            codec.start()

            val bufferInfo = MediaCodec.BufferInfo()
            var sawInputEOS = false
            var sawOutputEOS = false

            while (!sawOutputEOS) {
                // Feed input
                if (!sawInputEOS) {
                    val inputBufferId = codec.dequeueInputBuffer(10000)
                    if (inputBufferId >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferId) ?: continue
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)

                        if (sampleSize < 0) {
                            codec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            sawInputEOS = true
                        } else {
                            codec.queueInputBuffer(inputBufferId, 0, sampleSize, extractor.sampleTime, 0)
                            extractor.advance()
                        }
                    }
                }

                // Get output
                val outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 10000)
                if (outputBufferId >= 0) {
                    val outputBuffer = codec.getOutputBuffer(outputBufferId) ?: continue

                    if (bufferInfo.size > 0) {
                        val chunk = ByteArray(bufferInfo.size)
                        outputBuffer.get(chunk)
                        callback.audioAvailable(chunk, 0, chunk.size)
                    }

                    codec.releaseOutputBuffer(outputBufferId, false)

                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        sawOutputEOS = true
                    }
                } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    Log.d(TAG, "Output format changed")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Audio decode error: ${e.message}", e)
            throw e
        } finally {
            codec?.stop()
            codec?.release()
            extractor.release()
        }
    }
}
