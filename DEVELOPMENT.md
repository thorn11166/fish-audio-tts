# Development Guide

## Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 11 or higher
- **Android SDK**: API 26+ (Minimum Android 8.0)
- **Kotlin**: 1.9.0+
- **Gradle**: 8.x (included via wrapper)

## Project Setup

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/fish-audio-tts-app.git
cd fish-audio-tts-app
```

### 2. Open in Android Studio

1. Open Android Studio
2. Select "Open" and navigate to project directory
3. Wait for Gradle sync to complete
4. Android Studio will download required SDK components

### 3. Get API Key

1. Create account at [fish.audio](https://fish.audio)
2. Navigate to [API Keys](https://fish.audio/app/api-keys/)
3. Create new API key
4. Copy and store securely

### 4. Configure Local Development

Create `local.properties` (if not present):

```properties
sdk.dir=/path/to/android/sdk

# Optional: Custom build settings
org.gradle.jvmargs=-Xmx2048m
```

## Building

### Debug APK

```bash
# Using Gradle wrapper (recommended)
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (unsigned)

```bash
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

### Android App Bundle (for Play Store)

```bash
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

## Running

### Via Android Studio

1. Select target device/emulator
2. Click "Run" (Shift+F10)
3. Wait for app to build and deploy

### Via Gradle

```bash
# Debug build and install
./gradlew installDebug

# Run on connected device
./gradlew connectedAndroidTest
```

### Via Command Line

```bash
# Build and install
./gradlew installDebug

# Install via adb
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Run app
adb shell am start -n com.example.fishaudiotts/.MainActivity
```

## Testing

### Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests TestClassName

# Generate coverage report
./gradlew testDebugUnitTest
```

### Instrumented Tests

```bash
# Run all instrumented tests (requires connected device)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest --tests TestClassName
```

### Manual Testing Checklist

- [ ] App launches without crashes
- [ ] Settings screen loads
- [ ] API key input and validation works
- [ ] Voice discovery screen displays
- [ ] Voice search/filter functions
- [ ] Voice preview button works
- [ ] Add to favorites functionality
- [ ] Manage favorites (edit, delete, set default)
- [ ] Settings persist after app restart
- [ ] Deep link handling works
- [ ] Audio generation completes
- [ ] Audio playback works
- [ ] Theme applies correctly
- [ ] No memory leaks (check logcat)

## Code Style

### Kotlin Style Guide

Follow Google's Kotlin Style Guide:

```kotlin
// Good
class VoiceRepository(
    private val database: AppDatabase,
    private val apiClient: FishAudioApiClient
) {
    suspend fun generateSpeech(text: String): Result<InputStream> {
        return try {
            // Implementation
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Bad
class voice_repo(var db: AppDatabase, var api: FishAudioApiClient) {
    fun generate_speech(text: String) {
        // No error handling
    }
}
```

### Naming Conventions

- **Classes**: PascalCase (e.g., `VoiceRepository`)
- **Functions**: camelCase (e.g., `generateSpeech`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `API_TIMEOUT`)
- **Variables**: camelCase (e.g., `voiceSpeed`)
- **Private**: underscore prefix for backing properties (e.g., `_state`)

### Documentation

Include KDoc comments on public APIs:

```kotlin
/**
 * Generate speech from text using Fish Audio TTS
 * 
 * @param text Input text to convert to speech
 * @param referenceId Voice model ID from Fish Audio library
 * @param prosodySpeed Speech speed multiplier (0.5 - 2.0)
 * @return InputStream of audio data
 * @throws IOException if network request fails
 */
suspend fun generateSpeech(
    text: String,
    referenceId: String? = null,
    prosodySpeed: Double = 1.0
): InputStream
```

## Debugging

### Logcat

```bash
# View all logs
adb logcat

# Filter by tag
adb logcat | grep "FishAudioClient"

# Filter by log level
adb logcat *:W  # Warnings and errors only

# Save to file
adb logcat > logcat.txt
```

### Android Studio Debugger

1. Set breakpoints by clicking line number
2. Click "Debug" (Shift+F9) instead of "Run"
3. App will pause at breakpoints
4. Use Variables panel to inspect state
5. Step through code with Step Over/Into

### Memory Profiler

1. Android Studio → Profiler (top right)
2. Select Memory tab
3. Look for memory leaks (increasing baseline)
4. Take heap dumps to analyze

### Network Interceptor

Add to OkHttp client for debugging requests:

```kotlin
.addNetworkInterceptor(HttpLoggingInterceptor { message ->
    Log.d("HTTP", message)
}.apply {
    level = HttpLoggingInterceptor.Level.BODY
})
```

## Common Issues

### Gradle Sync Fails

**Solution:**
```bash
./gradlew clean
./gradlew --refresh-dependencies build
```

### Kotlin Compilation Error

**Solution:**
- Update Kotlin version in `build.gradle.kts`
- Clean build: `./gradlew clean build`

### API Key Not Working

**Solution:**
- Verify key in Fish Audio dashboard
- Check key hasn't expired
- Ensure account is active with balance
- Check internet connection

### App Crashes on Start

**Solution:**
1. Check logcat for stack trace
2. Look for null pointer exceptions
3. Verify Room database initialization
4. Check AndroidManifest.xml

### Compose Recomposition Issues

**Solution:**
- Use `remember` for state
- Move state up to ViewModel
- Use `key()` for list items
- Profile with Compose layout inspector

## Dependencies Management

### Adding New Dependencies

1. Open `app/build.gradle.kts`
2. Add to appropriate block:

```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    
    // New dependency
    implementation("com.example:library:1.0.0")
}
```

3. Sync Gradle
4. Update documentation

### Updating Dependencies

```bash
# Check for updates
./gradlew dependencyUpdates

# Update specific dependency
# Edit build.gradle.kts version numbers
# Then sync
```

## Performance Optimization

### Profiling

**CPU Profiler:**
- Android Studio → Profiler → CPU
- Record method traces
- Identify bottlenecks

**Memory Profiler:**
- Android Studio → Profiler → Memory
- Monitor heap size
- Detect memory leaks

**Network Profiler:**
- Android Studio → Profiler → Network
- Monitor API calls
- Check payload sizes

### Optimization Tips

1. **Reduce Recompositions**: Use `remember` and `mutableStateOf`
2. **Lazy Loading**: Use `LazyColumn` for lists
3. **Coroutines**: Proper scope management
4. **Image Caching**: Implement caching for voice previews
5. **Database Queries**: Use indexed queries

## Releasing

### Pre-Release Checklist

- [ ] All tests passing
- [ ] No lint warnings
- [ ] Version code incremented
- [ ] Version name updated
- [ ] README updated
- [ ] CHANGELOG added
- [ ] API key not hardcoded
- [ ] No debug logging enabled

### Version Management

Update in `app/build.gradle.kts`:

```kotlin
versionCode = 2          // Increment by 1
versionName = "1.1.0"    // semantic versioning
```

### Create Release Tag

```bash
git tag -a v1.1.0 -m "Release version 1.1.0"
git push origin v1.1.0
```

GitHub Actions will automatically build and create release.

## Contributing

### Commit Messages

Follow conventional commits:

```
feat: add voice discovery search
fix: correct prosody volume calculation
docs: update API integration guide
style: format VaporwaveComponents.kt
refactor: extract SettingSlider component
test: add VoiceRepository tests
```

### Pull Requests

1. Create feature branch: `git checkout -b feature/description`
2. Make changes and commit
3. Push to fork: `git push origin feature/description`
4. Create pull request on GitHub
5. Wait for review

### Code Review

- Check code style consistency
- Verify tests are included
- Look for performance issues
- Ensure documentation is updated

## Resources

- [Android Documentation](https://developer.android.com)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Fish Audio API Docs](https://docs.fish.audio)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)

## Support

- Report bugs: GitHub Issues
- Discuss features: GitHub Discussions
- Get help: Stack Overflow, Reddit r/androiddev
- Fish Audio support: support@fish.audio
