package com.example.fishaudiotts.data.api

import com.example.fishaudiotts.data.api.models.TTSRequest
import com.example.fishaudiotts.data.api.models.VoiceListResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

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

    /**
     * Search voices from Fish Audio library
     */
    @GET("v1/voices")
    suspend fun searchVoices(
        @Header("Authorization") authorization: String,
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): VoiceListResponse
}
