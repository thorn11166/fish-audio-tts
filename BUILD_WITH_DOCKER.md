# Building Fish Audio TTS with Docker

This guide explains how to build the Fish Audio TTS Android app using Docker and Docker Compose, without requiring Android SDK, Java, or Gradle to be installed on your host machine.

## Prerequisites

- Docker (version 20.10+)
- Docker Compose (version 1.29+)
- At least 10 GB of free disk space for Android SDK and build artifacts
- 4 GB minimum RAM allocated to Docker

## Quick Installation

### On macOS or Windows (Docker Desktop)

1. Download [Docker Desktop](https://www.docker.com/products/docker-desktop)
2. Install and launch the application
3. Verify installation:
   ```bash
   docker --version
   docker-compose --version
   ```

### On Linux (Ubuntu/Debian)

```bash
sudo apt-get update
sudo apt-get install -y docker.io docker-compose
sudo systemctl start docker
sudo usermod -aG docker $USER  # Add current user to docker group
```

### On Linux (RHEL/CentOS/Fedora)

```bash
sudo dnf install -y docker docker-compose
sudo systemctl start docker
sudo usermod -aG docker $USER
```

## Building the APK

### Build Everything

```bash
docker-compose run build build
```

This command:
- Pulls the Ubuntu 22.04 base image
- Installs Java 21, Android SDK, and build-tools-34.0.0
- Compiles your project
- Generates the APK in `app/build/outputs/apk/`

### Build Specific Variants

```bash
# Debug build
docker-compose run build build -x test

# Release build (requires signing)
docker-compose run build assembleRelease

# Build only specific app modules
docker-compose run build build app

# Skip tests
docker-compose run build build -x test
```

## Extracting the APK

After a successful build, the APK is automatically available on your host machine at:

```
app/build/outputs/apk/debug/app-debug.apk
```

Or for release builds:

```
app/build/outputs/apk/release/app-release.apk
```

### Accessing Build Artifacts

All build outputs are stored in the project's `app/build/` directory (mounted from the host), so they're immediately accessible without needing to extract from the container.

## Useful Commands

### Clean build

```bash
docker-compose run build clean
```

### Run specific Gradle task

```bash
docker-compose run build <TASK_NAME>
# Examples:
docker-compose run build assemble
docker-compose run build compileDebugJava
docker-compose run build test
```

### Interactive shell in container

```bash
docker-compose run build bash
```

Then you can run Gradle commands manually:

```bash
./gradlew build
./gradlew bundleRelease
./gradlew :app:dependencies
```

### View Gradle wrapper version

```bash
docker-compose run build --version
```

## Caching and Performance

The Docker Compose setup uses named volumes to cache:

- **gradle-cache**: Gradle distributions, dependencies, and build cache
- **android-sdk-cache**: Android SDK components

These volumes persist between builds, so subsequent builds are **much faster** (seconds instead of minutes).

### Clearing caches

To start fresh (useful if you encounter build issues):

```bash
docker volume rm fish-audio-tts-app_gradle-cache fish-audio-tts-app_android-sdk-cache
docker-compose build --no-cache
```

## Troubleshooting

### "Permission denied while trying to connect to Docker daemon"

**On Linux:** Add your user to the docker group:

```bash
sudo usermod -aG docker $USER
newgrp docker
```

### Build fails with "Out of memory"

Allocate more RAM to Docker:

- **Docker Desktop (macOS/Windows):** Settings → Resources → Memory (increase to 4+ GB)
- **Linux:** Edit `/etc/docker/daemon.json`:
  ```json
  {
    "memory": 4294967296
  }
  ```
  Then restart: `sudo systemctl restart docker`

### "Could not download Android SDK"

This can happen with network issues. Try:

```bash
# Rebuild the image from scratch
docker-compose build --no-cache

# Then rebuild
docker-compose run build build
```

### Gradle version mismatch

The Dockerfile installs Gradle 8.5 globally. If your project requires a different version, the Gradle wrapper (`./gradlew`) will automatically download and use the correct version specified in `gradle/wrapper/gradle-wrapper.properties`.

### Java version issues

The Dockerfile uses Java 21 (OpenJDK). If your project requires a different Java version:

1. Edit the `Dockerfile` to change:
   ```dockerfile
   openjdk-21-jdk-headless  →  openjdk-XX-jdk-headless
   ```
2. Rebuild: `docker-compose build --no-cache`

## Advanced Usage

### Building multiple architectures

The current Dockerfile builds for the native architecture. To support ARM64 and x86_64:

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t fish-audio-tts:latest .
```

(Requires `docker buildx` enabled in Docker Desktop)

### Custom Gradle properties

Pass custom Gradle properties via environment variables:

```bash
docker-compose run -e ORG_GRADLE_PROJECT_myProp=myValue build build
```

Or create a `gradle.properties` file in your project root (already present).

### Using your own signing keys

For release builds, mount your signing keystore:

```bash
docker-compose run \
  -v /path/to/your/keystore.jks:/workspace/keystore.jks \
  build assembleRelease \
  -Pandroid.injected.signing.store.file=/workspace/keystore.jks \
  -Pandroid.injected.signing.store.password=YOUR_PASSWORD \
  -Pandroid.injected.signing.key.alias=YOUR_ALIAS \
  -Pandroid.injected.signing.key.password=YOUR_PASSWORD
```

## Docker Compose File Reference

The `docker-compose.yml` defines:

- **build.context:** Project root
- **build.dockerfile:** Path to Dockerfile
- **volumes:**
  - `.:/workspace` — Project source code (read/write)
  - `gradle-cache:/root/.gradle` — Gradle cache
  - `android-sdk-cache:/opt/android-sdk` — Android SDK
- **environment:** `GRADLE_USER_HOME` and `ANDROID_HOME`
- **entrypoint:** `./gradlew` (Gradle wrapper)
- **command:** `["build"]` (default task)

## Monitoring Build Progress

For verbose output during builds:

```bash
docker-compose run build build --info
```

For even more detail:

```bash
docker-compose run build build --debug
```

## Next Steps

- Read [QUICK_START.md](QUICK_START.md) for app-specific setup
- Check [DEVELOPMENT.md](DEVELOPMENT.md) for coding guidelines
- Review [README.md](README.md) for project overview

## Support

If you encounter issues:

1. Verify Docker is running: `docker ps`
2. Check disk space: `df -h`
3. View build logs: `docker-compose logs build`
4. Clean and rebuild: `docker-compose down && docker-compose build --no-cache`

---

**Happy building! 🐟🎨**
