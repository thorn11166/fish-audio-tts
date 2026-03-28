# Changelog

All notable changes to Fish Audio TTS App will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned Features
- Voice cloning with reference audio upload
- Real-time streaming with WebSocket
- Multi-speaker dialogue support (S2-Pro)
- Batch TTS processing with queue
- Advanced emotion controls
- Voice analytics dashboard
- Play Store distribution
- Offline voice caching
- Custom voice effects (reverb, echo)
- Dark/Light theme toggle

## [1.0.0] - 2026-03-28

### Added
- ✨ Complete Android TTS application with vaporwave theming
- 🎙️ Fish Audio API v1/tts integration
  - Text-to-speech generation
  - Multiple voice models (S1, S2-Pro)
  - Prosody control (speed, volume)
  - Audio format selection (MP3, WAV, PCM, Opus)
  - Sample rate configuration (8kHz-48kHz)
  
- 🎨 Vaporwave Theme
  - Neon pink/purple color scheme
  - Gradient backgrounds
  - Retro synthwave typography
  - Smooth animations

- 🔊 Voice Discovery Screen
  - Browse Fish Audio voice library
  - Search and filter voices
  - Voice preview functionality
  - Save favorites to app

- ⭐ Custom Voices Management
  - Save up to 5 favorite voices
  - Custom voice nicknames
  - Set default voice
  - Voice metadata persistence

- ⚙️ Settings Screen
  - API key configuration (encrypted)
  - TTS model selection
  - Prosody parameter adjustment
  - Audio format configuration
  - Text normalization toggle
  - Emotion control (S2-Pro)
  - Temperature and top-p tuning
  - Latency mode selection

- 🗄️ Data Persistence
  - Room database for favorite voices
  - EncryptedSharedPreferences for settings
  - Automatic data recovery

- 📱 Deep Linking Support
  - fishaudiotts://speak?voice=Name&text=Text
  - fishaudiotts://settings
  - fishaudiotts://discover
  - External app integration

- 🏗️ Architecture
  - MVVM pattern with ViewModel
  - Repository pattern for data access
  - Jetpack Compose for UI
  - Kotlin Coroutines for async
  - Flow/StateFlow for state management

- 📚 Documentation
  - Comprehensive README
  - Architecture documentation
  - API integration guide
  - Development guide
  - This changelog

- 🚀 CI/CD
  - GitHub Actions workflow
  - Automated APK builds
  - Release automation

### Technical Stack
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose 1.6+
- **Architecture**: MVVM + Repository Pattern
- **Database**: Room 2.6+
- **Networking**: Retrofit 2.10 + OkHttp 4.11
- **Async**: Coroutines 1.7+
- **Security**: EncryptedSharedPreferences
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

### Dependencies
- `androidx.core:core-ktx:1.12.0`
- `androidx.compose.ui:ui:1.6.0`
- `androidx.compose.material3:material3:1.2.0`
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0`
- `com.squareup.retrofit2:retrofit:2.10.0`
- `androidx.room:room-runtime:2.6.1`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3`
- `com.google.code.gson:gson:2.10.1`
- `androidx.security:security-crypto:1.1.0-alpha06`

### Initial Features
- Voice discovery and preview
- Favorite voice management (max 5)
- TTS generation with multiple parameters
- Settings persistence
- Encrypted API key storage
- Deep linking for external app integration
- Vaporwave UI theme with animations
- Production-ready code structure

---

## Notes on Versioning

- **Major**: Breaking changes, significant features
- **Minor**: New features, backward compatible
- **Patch**: Bug fixes, security updates

## Future Release Targets

- **1.1.0**: Voice cloning feature
- **1.2.0**: WebSocket real-time streaming
- **2.0.0**: Multi-speaker dialogue support
- **2.1.0**: Play Store release
