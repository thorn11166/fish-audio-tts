package com.example.fishaudiotts.data.api

import com.example.fishaudiotts.data.api.models.TTSRequest
import com.example.fishaudiotts.data.api.models.FishAudioModel
import com.example.fishaudiotts.data.api.models.ModelListResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for Fish Audio API
 * Base URL: https://api.fish.audio
 */
interface FishAudioService {

    @POST("v1/tts")
    suspend fun textToSpeech(
        @Header("Authorization") authorization: String,
        @Header("model") model: String,
        @Body request: TTSRequest
    ): ResponseBody

    /**
     * List all available voice models
     * GET /model?page_size=10&page_number=1&title=&tag=
     */
    @GET("model")
    suspend fun listModels(
        @Header("Authorization") authorization: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page_number") pageNumber: Int = 1,
        @Query("title") title: String? = null,
        @Query("tag") tag: String? = null
    ): ModelListResponse

    /**
     * Get specific model details
     * GET /model/{id}
     */
    @GET("model/{id}")
    suspend fun getModel(
        @Header("Authorization") authorization: String,
        @Path("id") modelId: String
    ): FishAudioModel
}
