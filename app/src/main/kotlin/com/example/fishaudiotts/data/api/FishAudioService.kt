package com.example.fishaudiotts.data.api

import com.example.fishaudiotts.data.api.models.TTSRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit service interface for Fish Audio API
 */
interface FishAudioService {
    
    @POST("v1/tts")
    suspend fun textToSpeech(
        @Header("Authorization") authorization: String,
        @Header("model") model: String,
        @Body request: TTSRequest
    ): ResponseBody
}
