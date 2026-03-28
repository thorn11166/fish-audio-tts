# Fish Audio API Integration Guide

## Overview

This app integrates with Fish Audio's RESTful TTS API for text-to-speech synthesis. The integration follows Fish Audio's official API specification.

## Base Configuration

- **Base URL**: `https://api.fish.audio`
- **TTS Endpoint**: `POST /v1/tts`
- **Authentication**: Bearer token in Authorization header
- **Content Type**: `application/json`
- **Response**: Binary audio stream

## API Client Implementation

### FishAudioApiClient Class

Located at: `data/api/FishAudioApiClient.kt`

**Responsibilities:**
- HTTP client configuration (Retrofit + OkHttp)
- Request/response serialization
- Error handling and logging
- API key validation

**Key Methods:**
```kotlin
suspend fun generateSpeech(
    text: String,
    referenceId: String? = null,
    prosodySpeed: Double = 1.0,
    prosodyVolume: Double = 0.0,
    format: String = "mp3",
    sampleRate: Int? = 44100,
    temperature: Double = 0.7,
    topP: Double = 0.7
): InputStream
```

## Request Format

### TTSRequest Data Class

```kotlin
data class TTSRequest(
    val text: String,
    val reference_id: String? = null,
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
```

### Request Headers

```
Authorization: Bearer YOUR_API_KEY
Content-Type: application/json
model: s2-pro  (or s1)
```

## Voice Models

### Available Models

1. **S1 (Standard)**
   - Baseline quality
   - Single speaker support
   - Lower latency
   - Good for simple use cases

2. **S2-Pro (Professional)**
   - Higher quality
   - Multi-speaker support (dialogue)
   - Advanced prosody control
   - Emotion support
   - **Recommended for production**

### Model Selection

```kotlin
// In settings
val model = preferencesManager.getTtsModel()  // "s2-pro" or "s1"

// When making request
val apiClient = FishAudioApiClient(apiKey, model)
```

## Audio Formats

| Format | Sample Rate | Notes |
|--------|------------|-------|
| MP3 | 32k, 44.1k (default) | Bitrate: 64-192 kbps |
| WAV/PCM | 8k-44.1k (default) | 16-bit mono |
| Opus | 48k (default) | Bitrate: auto, 24-64 kbps |

## Voice Selection

### Reference IDs

Voice model IDs from Fish Audio library identify specific voices. Examples:
- `8ef4a238714b45718ce04243307c57a7` → E-girl
- `802e3bc2b27e49c2995d23ef70e6ac89` → Energetic Male
- `933563129e564b19a115bedd57b7406a` → Sarah

### Finding Voice IDs

1. Visit [fish.audio](https://fish.audio)
2. Browse voices in library
3. Click voice to open detail page
4. Copy ID from URL: `https://fish.audio/m/{VOICE_ID}`
5. Save in app under "My Voices"

## Prosody Control

### Speed Adjustment

```kotlin
prosody = ProsodyControl(
    speed = 1.5,  // 0.5 = half speed, 2.0 = double speed
    volume = 0.0,
    normalize_loudness = true
)
```

### Volume Adjustment

```kotlin
prosody = ProsodyControl(
    speed = 1.0,
    volume = 5.0,  // +5 dB (positive = louder, negative = quieter)
    normalize_loudness = true
)
```

### Loudness Normalization

- Enabled by default
- Ensures consistent output levels
- S2-Pro only

## Text Parameters

### Temperature (Expressiveness)

- **Range**: 0.0 - 1.0
- **Default**: 0.7
- **Effect**: Higher = more varied/emotional, Lower = more consistent

### Top P (Diversity)

- **Range**: 0.0 - 1.0
- **Default**: 0.7
- **Effect**: Controls nucleus sampling diversity

### Chunk Length

- **Range**: 100 - 300 chars
- **Default**: 300
- **Effect**: Text segmentation for processing

## Response Handling

### Success Response

```kotlin
// Returns binary audio stream (InputStream)
val audioStream: InputStream = apiClient.generateSpeech(...)

// Save to file
audioStream.use { input ->
    File("output.mp3").outputStream().use { output ->
        input.copyTo(output)
    }
}

// Or play directly
mediaPlayer.setDataSource(audioStream)
mediaPlayer.prepare()
mediaPlayer.start()
```

### Error Handling

```kotlin
try {
    val audio = apiClient.generateSpeech(text, referenceId)
    // Process audio
} catch (e: HttpException) {
    when (e.code()) {
        401 -> "Unauthorized - Invalid API key"
        402 -> "Payment required - Check account balance"
        422 -> "Validation error - Check parameters"
        500 -> "Server error - Try again later"
        else -> "Unknown error: ${e.message}"
    }
} catch (e: IOException) {
    "Network error: ${e.message}"
} catch (e: Exception) {
    "Unexpected error: ${e.message}"
}
```

## API Quota & Rate Limiting

### Rate Limits

- Depends on account tier
- Standard: X requests/minute
- Enterprise: Custom limits

### Quota Management

1. Monitor API usage in Fish Audio dashboard
2. Check response headers for quota info
3. Implement retry logic with exponential backoff

## Integration Examples

### Basic TTS

```kotlin
val apiClient = FishAudioApiClient(apiKey, "s2-pro")

val audioStream = apiClient.generateSpeech(
    text = "Hello, world!",
    referenceId = "8ef4a238714b45718ce04243307c57a7",
    format = "mp3"
)

// Save to file
val file = File(cacheDir, "output.mp3")
audioStream.copyTo(file.outputStream())
```

### With Custom Voice Settings

```kotlin
val audioStream = apiClient.generateSpeech(
    text = "Speak faster!",
    referenceId = voiceId,
    prosodySpeed = 1.5,
    prosodyVolume = 3.0,
    format = "mp3",
    sampleRate = 44100,
    temperature = 0.8,
    topP = 0.75
)
```

### Multiple Voices (S2-Pro only)

```kotlin
// NOT YET IMPLEMENTED - Requires array handling
// Future enhancement for dialogue synthesis
```

## Testing API Integration

### Validation Method

```kotlin
val isValid = apiClient.validateApiKey()
if (isValid) {
    // Key is working
} else {
    // Invalid key or account issue
}
```

### Test Request

```bash
curl -X POST https://api.fish.audio/v1/tts \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -H "model: s2-pro" \
  -d '{
    "text": "Test voice",
    "reference_id": "8ef4a238714b45718ce04243307c57a7",
    "format": "mp3"
  }' \
  --output test.mp3
```

## Performance Tips

1. **Reuse API Client**: Don't create new instances
2. **Stream Responses**: Don't load full audio in memory
3. **Async Operations**: Use suspend functions with coroutines
4. **Caching**: Consider caching generated audio
5. **Batch Processing**: Group requests where possible

## Troubleshooting

### 401 Unauthorized
- Verify API key is correct
- Check key hasn't expired
- Ensure account is active

### 402 Payment Required
- Check billing status in Fish Audio dashboard
- Ensure payment method is valid
- Check account quota

### 422 Validation Error
- Verify text length (not empty)
- Check voice ID exists
- Validate prosody parameters
- Ensure format is supported

### 500 Server Error
- Temporary service issue
- Implement retry with exponential backoff
- Try again in a few moments

### Timeout Errors
- Check network connection
- Increase timeout if needed
- Verify API endpoint reachability

## Documentation References

- Official Docs: https://docs.fish.audio
- API Reference: https://docs.fish.audio/api-reference/endpoint/openapi-v1/text-to-speech
- Quick Start: https://docs.fish.audio/developer-guide/getting-started/quickstart
- Discord Support: https://discord.com/invite/dF9Db2Tt3Y
