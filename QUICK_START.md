# Quick Start Guide 🚀

Get the Fish Audio TTS app running in 5 minutes!

## 1. Prerequisites Check (1 min)

- ✅ Android Studio installed (Hedgehog+)
- ✅ JDK 11+ installed
- ✅ Android SDK API 26+ available
- ✅ Git (optional, for cloning)

## 2. Get API Key (2 min)

1. Go to [fish.audio](https://fish.audio)
2. Sign up or log in
3. Navigate to [API Keys](https://fish.audio/app/api-keys/)
4. Click "Create New Key"
5. Copy and save the key somewhere safe

**Example API Key Format:**
```
sk_live_abc123xyz789...
```

## 3. Clone & Open (1 min)

### Option A: Using Git
```bash
git clone https://github.com/yourusername/fish-audio-tts-app.git
cd fish-audio-tts-app
```

### Option B: Manual Download
1. Download project zip
2. Extract to desired location
3. Open Android Studio

## 4. Build & Run (1 min)

### In Android Studio:
1. **File** → **Open** → Select project directory
2. Wait for Gradle sync (1-2 minutes)
3. Select emulator or connect device
4. Click **Run** button (green play icon)
5. App launches!

### Via Command Line:
```bash
./gradlew clean build
./gradlew installDebug
```

## 5. Configure API Key (< 1 min)

1. **Open Settings screen** (⚙️ Settings button)
2. **Paste API Key** in the "Fish Audio API Key" field
3. Select TTS Model: **S2-Pro** (recommended)
4. Click **Save Settings**

Done! ✨

---

## 🎯 Quick Feature Test

### Test Voice Discovery
1. Home → "🔍 Discover Voices"
2. Browse voice list
3. Click any voice to preview
4. Click ⭐ to favorite

### Test Settings
1. Home → "⚙️ Settings"
2. Adjust Speed slider (0.5x - 2.0x)
3. Adjust Volume slider
4. Toggle Emotion Control
5. Save Settings

### Test My Voices
1. Home → "⭐ My Voices"
2. View saved favorite voices
3. Select radio button to set default
4. Click ❌ to remove

---

## 🔧 Troubleshooting

### "Gradle Sync Failed"
```bash
./gradlew clean
./gradlew --refresh-dependencies build
```

### "API Key Not Working"
- ✅ Copy entire key (including prefix)
- ✅ Check key hasn't expired in Fish Audio dashboard
- ✅ Verify internet connection
- ✅ Check account has available quota

### "App Won't Install"
```bash
adb uninstall com.example.fishaudiotts
./gradlew installDebug
```

### "App Crashes on Launch"
- Check logcat: `adb logcat | grep "FishAudio"`
- Look for error stack trace
- Report issue on GitHub

---

## 📱 Device Setup

### Using Emulator
1. **Android Studio** → **Device Manager**
2. Create new emulator (API 26+)
3. Click **Play** to launch
4. Run project

### Using Physical Device
1. **Enable Developer Mode**
   - Settings → About Phone → Tap "Build Number" 7 times
2. **Enable USB Debugging**
   - Settings → Developer Options → USB Debugging
3. Connect via USB
4. Run project

---

## 🎨 Theme Overview

The app uses a vaporwave aesthetic:

| Element | Color | Hex |
|---------|-------|-----|
| Primary | Neon Pink | #FF00FF |
| Secondary | Cyber Purple | #9D4EDD |
| Accent | Dark Cyan | #00D9FF |
| Background | Dark | #0A0A1A |
| Text | White | #FFFFFF |

The theme is already applied! Just enjoy the vibes. 🎹✨

---

## 🔗 Deep Linking (Advanced)

After app is running, test deep links:

```bash
# Navigate to Settings
adb shell am start -a android.intent.action.VIEW \
  -d "fishaudiotts://settings" com.example.fishaudiotts

# Navigate to Voice Discovery
adb shell am start -a android.intent.action.VIEW \
  -d "fishaudiotts://discover" com.example.fishaudiotts

# Generate speech with specific voice
adb shell am start -a android.intent.action.VIEW \
  -d "fishaudiotts://speak?voice=Sarah&text=Hello%20world" com.example.fishaudiotts
```

---

## 📖 Next Steps

1. **Read Full Documentation**: See [README.md](README.md)
2. **Explore Architecture**: See [ARCHITECTURE.md](ARCHITECTURE.md)
3. **Understand API**: See [API_INTEGRATION.md](API_INTEGRATION.md)
4. **Start Contributing**: See [DEVELOPMENT.md](DEVELOPMENT.md)

---

## 🎮 Demo Content

The app comes with 5 demo voices:

| Name | ID | Style |
|------|----|----|
| **E-girl** | 8ef4a238... | Young, energetic |
| **Energetic Male** | 802e3bc2... | Upbeat, motivated |
| **Sarah** | 933563129... | Professional, calm |
| **Adrian** | bf322df2... | Deep, authoritative |
| **Selene** | b347db033... | Mystical, soothing |

All voices work with the demo!

---

## 🆘 Help & Support

- **GitHub Issues**: Report bugs
- **GitHub Discussions**: Ask questions
- **Fish Audio Support**: support@fish.audio
- **Official Docs**: https://docs.fish.audio

---

## 🎉 Success!

If you can:
- ✅ See the home screen with vaporwave theme
- ✅ Access Settings and input API key
- ✅ Browse voices in Voice Discovery
- ✅ Save voices to favorites
- ✅ Adjust TTS parameters

**Then you're all set!** 🚀

Enjoy generating speech with the vaporwave vibes! 🎹✨

---

*Last Updated: 2026-03-28*
*App Version: 1.0.0*
