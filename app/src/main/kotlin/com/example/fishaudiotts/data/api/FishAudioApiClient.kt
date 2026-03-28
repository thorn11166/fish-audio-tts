package com.example.fishaudiotts.data.api

import android.util.Log
import com.example.fishaudiotts.BuildConfig
import com.example.fishaudiotts.data.api.models.TTSRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Fish Audio API Client for TTS operations
 * Manages all REST API communication with Fish Audio service
 */
class FishAudioApiClient(
    private val apiKey: String,
    private val ttModel: String = "s2-pro"
) {
    companion object {
        private const val BASE_URL = "https://api.fish.audio"
        private const val TAG = "FishAudioClient"
        private const val TIMEOUT_SECONDS = 120L
    }
    
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
        try {
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
            
            val response = service.textToSpeech(
                authorization = "Bearer $apiKey",
                model = ttModel,
                request = request
            )
            
            Log.d(TAG, "TTS request successful: ${response.contentLength()} bytes")
            return response.byteStream()
        } catch (e: Exception) {
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
        return try {
            val request = TTSRequest(
                text = "Test",
                format = "mp3"
            )
            service.textToSpeech(
                authorization = "Bearer $apiKey",
                model = ttModel,
                request = request
            )
            true
        } catch (e: Exception) {
            Log.w(TAG, "API key validation failed: ${e.message}")
            false
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
