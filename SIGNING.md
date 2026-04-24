# APK Signing Guide

## Quick Option: Install Debug APK (No signing needed)
The debug APK from GitHub Actions is already signed with Android's debug key and can be installed directly for testing.

## Option 1: Create Your Own Keystore (Recommended for distribution)

### Step 1: Generate a Keystore
Run this command on your computer (requires Java/JDK):

```bash
keytool -genkey -v \
  -keystore fish-audio-tts.keystore \
  -alias fishaudio \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

When prompted, enter:
- **Keystore password**: Choose a strong password (save this!)
- **Key password**: Can be same as keystore password
- **Your name/organization**: Fill in your details

This creates `fish-audio-tts.keystore` file.

### Step 2: Configure GitHub Secrets (For CI builds)
1. Go to your GitHub repo → Settings → Secrets and variables → Actions
2. Add these secrets:

| Secret Name | Value |
|-------------|-------|
| `KEYSTORE_BASE64` | Run: `base64 -i fish-audio-tts.keystore` and paste output |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | `fishaudio` (or whatever you chose) |
| `KEY_PASSWORD` | Your key password |

3. Push a new commit - the workflow will now build a signed release APK!

### Step 3: Download Signed APK
After the workflow runs, the release APK will be signed and ready to install.

## Option 2: Sign APK Locally

If you have the keystore file locally, you can sign any APK:

```bash
# Sign the APK
jarsigner -verbose \
  -sigalg SHA256withRSA \
  -digestalg SHA-256 \
  -keystore fish-audio-tts.keystore \
  app-release-unsigned.apk \
  fishaudio

# Verify the signature
jarsigner -verify -verbose -certs app-release-unsigned.apk

# Align the APK (optional but recommended)
zipalign -v 4 app-release-unsigned.apk app-release-signed.apk
```

## Option 3: Use Android Studio
1. Open the project in Android Studio
2. Build → Generate Signed App Bundle / APK
3. Choose "APK"
4. Create new or choose existing keystore
5. Build release APK

## Important Notes

- **Keep your keystore safe!** If you lose it, you can't update your app on the Play Store
- **Never commit keystore files to git!** Use GitHub secrets or keep locally
- The debug APK is fine for personal use, but a signed release APK is needed for:
  - Google Play Store
  - Distribution to others
  - Long-term use (debug keys expire)

## Checking if APK is Signed

```bash
# Check APK signature
apksigner verify -v your-app.apk

# Or using jarsigner
jarsigner -verify -verbose -certs your-app.apk
```
