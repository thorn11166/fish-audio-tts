# Fish Audio TTS App 🎙️✨

A production-ready Android Text-to-Speech application with **vaporwave theming**, built with **Kotlin + Jetpack Compose + MVVM architecture**.

## Features

- 🎨 **Vaporwave Aesthetic** - Neon pinks/purples, gradients, retro synthwave animations
- 🔊 **Voice Discovery** - Search and preview voices from Fish Audio library
- 🎯 **Settings Management** - Configure API key, voice model, emotion, speed, and more
- ⭐ **Custom Voices** - Save up to 5 favorite voices with custom nicknames
- 📱 **External App Integration** - Accept voice nickname as deep-link parameter
- 🗄️ **Persistent Storage** - Room database for voices, SharedPreferences for settings
- 📡 **API Integration** - Full Fish Audio v1/tts REST API support with streaming
- 🎵 **Audio Output** - Stream audio playback + MediaStore integration for external apps

## Project Structure

```
fish-audio-tts-app/
├── app/
│   ├── build.gradle.kts          # App-level build config
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/example/fishaudiotts/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── VoiceDao.kt
│   │   │   │   │   │   └── VoiceEntity.kt
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── FishAudioApiClient.kt
│   │   │   │   │   │   ├── FishAudioService.kt
│   │   │   │   │   │   └── models/
│   │   │   │   │   │       ├── TTSRequest.kt
│   │   │   │   │   │       └── VoiceModel.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── VoiceRepository.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── VoiceDiscoveryScreen.kt
│   │   │   │   │   │   ├── CustomVoicesScreen.kt
│   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   └── VoicePreviewScreen.kt
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Type.kt
│   │   │   │   │   └── components/
│   │   │   │   │       ├── VaporwaveButton.kt
│   │   │   │   │       ├── VaporwaveCard.kt
│   │   │   │   │       └── AnimatedBackground.kt
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   ├── VoiceDiscoveryViewModel.kt
│   │   │   │   │   ├── SettingsViewModel.kt
│   │   │   │   │   └── SharedViewModel.kt
│   │   │   │   └── util/
│   │   │   │       ├── Constants.kt
│   │   │   │       └── PreferencesManager.kt
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/
│   │   │       ├── values/
│   │   │       │   └── strings.xml
│   │   │       └── colors.xml
│   │   └── test/
│   │       └── ...
│   └── proguard-rules.pro
├── build.gradle.kts             # Project-level build config
├── gradle.properties
├── settings.gradle.kts
└── .gitignore
```

## Setup Instructions

### Prerequisites
- Android SDK 26+ (API level)
- Android Studio Hedgehog or newer
- Kotlin 1.9.0+
- JDK 11+

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/fish-audio-tts-app.git
   cd fish-audio-tts-app
   ```

2. **Get Your Fish Audio API Key**
   - Sign up at [fish.audio](https://fish.audio)
   - Navigate to [API Keys](https://fish.audio/app/api-keys/)
   - Create a new API key
   - Copy and save securely

3. **Build the Project**
   ```bash
   ./gradlew build
   ```

4. **Run the App**
   ```bash
   ./gradlew installDebug
   ```

5. **Initial Configuration**
   - Launch the app
   - Go to Settings screen
   - Enter your Fish Audio API key
   - Select default voice model (s1 or s2-pro)
   - Customize other TTS parameters as desired

## Architecture

### MVVM Pattern
- **Models**: Data classes for API responses and database entities
- **Views**: Jetpack Compose screens with reactive state
- **ViewModels**: Manage UI state and business logic with Flow/StateFlow

### Key Components

#### Data Layer
- **FishAudioApiClient**: Retrofit-based HTTP client with OkHttp
- **VoiceRepository**: Abstracts data access (database + API)
- **AppDatabase**: Room database for persistent voice storage

#### UI Layer
- **HomeScreen**: Main navigation hub
- **VoiceDiscoveryScreen**: Browse and search voices
- **CustomVoicesScreen**: Manage saved voices
- **SettingsScreen**: Configure API key and TTS parameters

#### Theme
- Neon pink (#FF00FF), cyber purple (#9D4EDD)
- Gradient backgrounds
- Smooth animations and transitions
- Retro synthwave typography

## Dependencies

```kotlin
// Core Android
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
androidx.lifecycle:lifecycle-runtime-ktx:2.7.0

// Compose
androidx.compose.ui:ui:1.6.0
androidx.compose.material3:material3:1.2.0
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0

// Network
com.squareup.retrofit2:retrofit:2.10.0
com.squareup.retrofit2:converter-gson:2.10.0
com.squareup.okhttp3:okhttp:4.11.0

// Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3

// Serialization
com.google.code.gson:gson:2.10.1

// Media
androidx.media:media:1.7.0
```

## API Integration

### Base URL
```
https://api.fish.audio
```

### Authentication
All requests require Bearer token in Authorization header:
```
Authorization: Bearer YOUR_API_KEY
```

### TTS Endpoint

**POST** `/v1/tts`

**Headers:**
- `Authorization: Bearer <API_KEY>`
- `Content-Type: application/json`
- `model: s2-pro` (or `s1`)

**Body:**
```json
{
  "text": "Hello world!",
  "reference_id": "voice-model-id",
  "format": "mp3",
  "sample_rate": 44100,
  "prosody": {
    "speed": 1.0,
    "volume": 0
  }
}
```

**Response:** Binary audio stream (mp3, wav, pcm, or opus)

## Usage Examples

### In Settings
1. Navigate to Settings → API Key
2. Enter Fish Audio API key
3. Toggle TTS Model (S1 or S2-Pro)
4. Adjust speed (0.5 - 2.0x)
5. Enable/disable emotion (S2-Pro only)

### Voice Discovery
1. Home Screen → "Discover Voices"
2. Search by name or category
3. Tap voice card to preview sample
4. Tap ⭐ to add to favorites

### Custom Voices
1. Home Screen → "My Voices"
2. View all 5 favorite voices
3. Tap voice to set as default (radio button)
4. Long-press to rename or remove

### External App Integration
Call this app with voice nickname:
```kotlin
val intent = Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse("fishaudiotts://speak?voice=MyFavVoice&text=Hello%20world")
}
startActivity(intent)
```

## Deep Linking

The app supports deep links for integration with other apps:

```
fishaudiotts://speak?voice=VoiceNickname&text=TextToSpeak
fishaudiotts://settings
fishaudiotts://discover
```

## Database Schema

### VoiceEntity
```kotlin
@Entity(tableName = "favorite_voices")
data class VoiceEntity(
    @PrimaryKey val id: String,
    val nickname: String,
    val referenceId: String,
    val emotion: String,
    val isDefault: Boolean,
    val createdAt: Long
)
```

## SharedPreferences Keys

- `api_key` - Encrypted Fish Audio API key
- `tts_model` - Selected model (s1 or s2-pro)
- `voice_speed` - Speed multiplier
- `emotion_enabled` - Boolean for emotion support
- `default_voice_id` - Current default voice reference ID

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist
- [ ] API key validation
- [ ] Voice search and preview
- [ ] Voice save/load from database
- [ ] Voice playback with different speeds
- [ ] Settings persistence after app restart
- [ ] External app deep links
- [ ] Audio streaming quality

## Deployment

### GitHub Setup
1. Create repository: `fish-audio-tts-app`
2. Enable GitHub Actions
3. Set up secrets:
   - `SIGNING_KEY` - Base64 encoded signing key
   - `SIGNING_KEY_ALIAS` - Alias name
   - `SIGNING_KEY_PASSWORD` - Key password

### CI/CD with GitHub Actions
See `.github/workflows/build.yml` for automated APK builds and releases.

### Release Build
```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing config)
./gradlew assembleRelease

# Android App Bundle (for Play Store)
./gradlew bundleRelease
```

## Performance Considerations

- Retrofit with connection pooling for efficient API calls
- Coroutines for async TTS generation
- Caching of voice discovery results
- Streaming audio playback (no full download needed)
- Room database for instant local access

## Security

- 🔒 API keys stored in encrypted SharedPreferences
- 🔐 HTTPS-only API communication
- 📱 No sensitive data logged
- 🛡️ Uses Android's WebView SafetyNet (future)

## Troubleshooting

### API Key Errors
- Verify key format (should start with bearer token)
- Check key hasn't expired in Fish Audio dashboard
- Ensure account has available quota

### Audio Quality Issues
- Check latency setting (normal/balanced/low)
- Adjust prosody parameters (speed/volume)
- Try different TTS model (s1 vs s2-pro)

### Database Issues
- Clear app cache: Settings → Apps → Fish Audio TTS → Storage → Clear Cache
- Reset database: uninstall and reinstall app

## Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create feature branch
3. Submit pull request with clear description

## License

MIT License - See LICENSE file

## Support

- 📧 Email: support@fish.audio
- 💬 Discord: [Fish Audio Community](https://discord.com/invite/dF9Db2Tt3Y)
- 📖 Documentation: [fish.audio/docs](https://docs.fish.audio)

## Future Enhancements

- [ ] Voice cloning with reference audio upload
- [ ] Real-time streaming with WebSocket
- [ ] Multi-speaker dialogue support (S2-Pro)
- [ ] Batch TTS with queue management
- [ ] Advanced emotion controls
- [ ] Voice analytics dashboard
- [ ] Play Store distribution

---

**Built with ❤️ and neon vibes** ✨🎹
