package com.example.fishaudiotts.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * TTS Request body for Fish Audio API
 * Supports both single and multi-speaker synthesis
 */
data class TTSRequest(
    val text: String,
    @SerializedName("reference_id")
    val referenceId: String? = null,
    val temperature: Double = 0.7,
    val top_p: Double = 0.7,
    val prosody: ProsodyControl? = null,
    val chunk_length: Int = 300,
    val normalize: Boolean = true,
    val format: String = "mp3",
    val sample_rate: Int? = 44100,
    val mp3_bitrate: Int = 128,
    val latency: String = "normal",
    val max_new_tokens: Int = 1024,
    val repetition_penalty: Double = 1.2,
    val min_chunk_length: Int = 50,
    val condition_on_previous_chunks: Boolean = true,
    val early_stop_threshold: Double = 1.0
)

/**
 * Prosody control for speech adjustments
 */
data class ProsodyControl(
    val speed: Double = 1.0,
    val volume: Double = 0.0,
    val normalize_loudness: Boolean = true
)

/**
 * Fish Audio Model - represents a voice model from the API
 */
data class FishAudioModel(
    @SerializedName("_id")
    val id: String,
    val type: String? = null,  // "svc" or "tts"
    val title: String? = null,
    val description: String? = null,
    @SerializedName("cover_image")
    val coverImage: String? = null,
    val tags: List<String>? = null,
    @SerializedName("train_mode")
    val trainMode: String? = null,  // "fast" or "full"
    val status: String? = null,
    @SerializedName("is_public")
    val isPublic: Boolean? = null,
    val likes: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Response from listing models
 */
data class ModelListResponse(
    val items: List<FishAudioModel>? = null,
    val total: Int? = null,
    @SerializedName("page_size")
    val pageSize: Int? = null,
    @SerializedName("page_number")
    val pageNumber: Int? = null
)

/**
 * Legacy VoiceModel for demo voices and compatibility
 */
data class VoiceModel(
    val id: String,
    val name: String,
    val description: String? = null,
    val tags: List<String>? = emptyList(),
    val language: String? = null,
    val emotion: String? = null
)

/**
 * Legacy Voice search/discovery response
 */
data class VoiceListResponse(
    val voices: List<VoiceModel>,
    val total: Int,
    val page: Int,
    val page_size: Int
)

/**
 * API error response
 */
data class ApiErrorResponse(
    val status: Int,
    val message: String
)
