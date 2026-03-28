package com.example.fishaudiotts.util

/**
 * App-wide constants
 */
object Constants {
    // API
    const val FISH_AUDIO_BASE_URL = "https://api.fish.audio"
    const val TTS_ENDPOINT = "/v1/tts"
    const val API_TIMEOUT_SECONDS = 120L
    
    // TTS Models
    const val MODEL_S1 = "s1"
    const val MODEL_S2_PRO = "s2-pro"
    
    // Audio Formats
    const val FORMAT_MP3 = "mp3"
    const val FORMAT_WAV = "wav"
    const val FORMAT_PCM = "pcm"
    const val FORMAT_OPUS = "opus"
    
    // Sample Rates
    const val SAMPLE_RATE_8K = 8000
    const val SAMPLE_RATE_16K = 16000
    const val SAMPLE_RATE_24K = 24000
    const val SAMPLE_RATE_44_1K = 44100
    const val SAMPLE_RATE_48K = 48000
    
    // Prosody
    const val SPEED_MIN = 0.5
    const val SPEED_MAX = 2.0
    const val SPEED_DEFAULT = 1.0
    const val VOLUME_MIN = -20.0
    const val VOLUME_MAX = 20.0
    const val VOLUME_DEFAULT = 0.0
    
    // Database
    const val DATABASE_NAME = "fish_audio_tts.db"
    const val MAX_FAVORITE_VOICES = 5
    
    // Deep Link Schemes
    const val DEEP_LINK_SCHEME = "fishaudiotts"
    const val DEEP_LINK_HOST_SPEAK = "speak"
    const val DEEP_LINK_HOST_SETTINGS = "settings"
    const val DEEP_LINK_HOST_DISCOVER = "discover"
    
    // Default Voice Models (for demo)
    val DEMO_VOICES = listOf(
        "8ef4a238714b45718ce04243307c57a7",  // E-girl
        "802e3bc2b27e49c2995d23ef70e6ac89",  // Energetic Male
        "933563129e564b19a115bedd57b7406a",  // Sarah
        "bf322df2096a46f18c579d0baa36f41d",  // Adrian
        "b347db033a6549378b48d00acb0d06cd"   // Selene
    )
    
    val DEMO_VOICE_NAMES = mapOf(
        "8ef4a238714b45718ce04243307c57a7" to "E-girl",
        "802e3bc2b27e49c2995d23ef70e6ac89" to "Energetic Male",
        "933563129e564b19a115bedd57b7406a" to "Sarah",
        "bf322df2096a46f18c579d0baa36f41d" to "Adrian",
        "b347db033a6549378b48d00acb0d06cd" to "Selene"
    )
}
