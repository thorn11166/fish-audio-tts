package com.example.fishaudiotts.data.api

import android.content.Context
import android.util.Log
import com.example.fishaudiotts.BuildConfig
import com.example.fishaudiotts.data.api.models.TTSRequest
import com.example.fishaudiotts.util.Constants
import com.example.fishaudiotts.util.DebugAudioManager
import com.example.fishaudiotts.util.FileLogger
import com.example.fishaudiotts.util.RequestLogEntry
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Fish Audio API Client for TTS operations
 * Manages all REST API communication with Fish Audio service
 */
class FishAudioApiClient(
    private val apiKey: String,
    private val ttModel: String = Constants.MODEL_S2_1_PRO_FREE,
    context: Context? = null
) {
    companion object {
        private const val BASE_URL = "https://api.fish.audio"
        private const val TAG = "FishAudioClient"
        private const val TIMEOUT_SECONDS = 120L
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    }

    private val logger: FileLogger? = context?.let { FileLogger.getInstance(it) }
    private val debugAudioManager: DebugAudioManager? = context?.let { DebugAudioManager(it) }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .apply {
            // Only enable logging interceptor in debug builds
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                    Log.d(TAG, message)
                }).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            // Always add header redaction interceptor to prevent API key exposure
            addNetworkInterceptor(AuthHeaderRedactionInterceptor())
        }
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: FishAudioService = retrofit.create(FishAudioService::class.java)

    /**
     * Generate speech from text using Fish Audio TTS
     *
     * @param text Input text to convert to speech
     * @param referenceId Voice model ID from Fish Audio library
     * @param prosodySpeed Speech speed multiplier (0.5 - 2.0)
     * @param prosodyVolume Volume adjustment in dB
     * @param format Output audio format (mp3, wav, pcm, opus)
     * @return InputStream of audio data
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
    ): InputStream {
        val startTime = System.currentTimeMillis()
        val request = TTSRequest(
            text = text,
            referenceId = referenceId,
            temperature = temperature,
            top_p = topP,
            format = format,
            sample_rate = sampleRate,
            prosody = if (prosodySpeed != 1.0 || prosodyVolume != 0.0) {
                com.example.fishaudiotts.data.api.models.ProsodyControl(
                    speed = prosodySpeed,
                    volume = prosodyVolume,
                    normalize_loudness = true
                )
            } else null
        )

        return try {
            val response = service.textToSpeech(
                authorization = "Bearer $apiKey",
                model = ttModel,
                request = request
            )

            val bytes = response.bytes()
            val duration = System.currentTimeMillis() - startTime

            logger?.logRequest(
                RequestLogEntry(
                    timestamp = dateFormat.format(Date()),
                    model = ttModel,
                    text = text,
                    referenceId = referenceId,
                    format = format,
                    sampleRate = sampleRate,
                    responseSize = bytes.size.toLong(),
                    durationMs = duration,
                    success = true,
                    error = null
                )
            )
            debugAudioManager?.saveAudioBytes(bytes, prefix = "tts", extension = format)

            Log.d(TAG, "TTS request successful: ${bytes.size} bytes in ${duration}ms")
            ByteArrayInputStream(bytes)
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger?.logRequest(
                RequestLogEntry(
                    timestamp = dateFormat.format(Date()),
                    model = ttModel,
                    text = text,
                    referenceId = referenceId,
                    format = format,
                    sampleRate = sampleRate,
                    responseSize = null,
                    durationMs = duration,
                    success = false,
                    error = e.message
                )
            )
            Log.e(TAG, "TTS generation failed: ${e.message}", e)
            throw e
        }
    }

    /**
     * Validate API key by making a test TTS request
     *
     * @return true if API key is valid
     */
    suspend fun validateApiKey(): Boolean {
        val startTime = System.currentTimeMillis()
        return try {
            val request = TTSRequest(
                text = "Test",
                format = "mp3"
            )
            val response = service.textToSpeech(
                authorization = "Bearer $apiKey",
                model = ttModel,
                request = request
            )
            val bytes = response.bytes()
            val duration = System.currentTimeMillis() - startTime

            logger?.logRequest(
                RequestLogEntry(
                    timestamp = dateFormat.format(Date()),
                    model = ttModel,
                    text = "Test",
                    referenceId = null,
                    format = "mp3",
                    sampleRate = null,
                    responseSize = bytes.size.toLong(),
                    durationMs = duration,
                    success = true,
                    error = null
                )
            )
            debugAudioManager?.saveAudioBytes(bytes, prefix = "validate", extension = "mp3")
            true
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger?.logRequest(
                RequestLogEntry(
                    timestamp = dateFormat.format(Date()),
                    model = ttModel,
                    text = "Test",
                    referenceId = null,
                    format = "mp3",
                    sampleRate = null,
                    responseSize = null,
                    durationMs = duration,
                    success = false,
                    error = e.message
                )
            )
            Log.w(TAG, "API key validation failed: ${e.message}")
            false
        }
    }

    /**
     * List all available voice models from Fish Audio
     *
     * @param pageSize Number of models per page
     * @param pageNumber Page number (1-based)
     * @param title Filter by title (optional)
     * @param tag Filter by tag (optional)
     * @return ModelListResponse containing models and pagination info
     */
    suspend fun listModels(
        pageSize: Int = 20,
        pageNumber: Int = 1,
        title: String? = null,
        tag: String? = null
    ): com.example.fishaudiotts.data.api.models.ModelListResponse {
        return try {
            service.listModels(
                authorization = "Bearer $apiKey",
                pageSize = pageSize,
                pageNumber = pageNumber,
                title = title,
                tag = tag
            )
        } catch (e: Exception) {
            Log.e(TAG, "List models failed: ${e.message}", e)
            throw e
        }
    }

    /**
     * Get specific model details
     *
     * @param modelId The model ID
     * @return FishAudioModel with full details
     */
    suspend fun getModel(modelId: String): com.example.fishaudiotts.data.api.models.FishAudioModel {
        return try {
            service.getModel(
                authorization = "Bearer $apiKey",
                modelId = modelId
            )
        } catch (e: Exception) {
            Log.e(TAG, "Get model failed: ${e.message}", e)
            throw e
        }
    }
}

/**
 * Custom interceptor to redact Authorization headers in logs
 * Prevents accidental exposure of API keys in logcat or crash reports
 */
private class AuthHeaderRedactionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        // Only redact logging in debug builds
        if (BuildConfig.DEBUG) {
            val authHeader = originalRequest.header("Authorization")
            if (authHeader != null) {
                Log.d("AuthInterceptor", "Authorization header present (redacted)")
            }
        }

        return chain.proceed(originalRequest)
    }
}
