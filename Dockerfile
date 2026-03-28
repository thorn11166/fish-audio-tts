FROM ubuntu:22.04

# Set environment variables
ENV DEBIAN_FRONTEND=noninteractive \
    ANDROID_HOME=/opt/android-sdk \
    PATH=/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:$PATH \
    JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 \
    GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx2048m"

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk-headless \
    wget \
    unzip \
    git \
    curl \
    lib32z1 \
    lib32stdc++6 \
    libc6-i386 \
    && rm -rf /var/lib/apt/lists/*

# Create Android SDK directory
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools

# Download and install Android SDK command-line tools
RUN cd /tmp && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q commandlinetools-linux-11076708_latest.zip && \
    mv cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm commandlinetools-linux-11076708_latest.zip

# Accept licenses and install SDK components
RUN yes | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager --licenses && \
    ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"

# Set working directory
WORKDIR /workspace

# Copy gradle wrapper files first
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle.properties gradle.properties
COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts

# Grant execute permissions to gradlew
RUN chmod +x gradlew

# Download gradle wrapper dependencies
RUN ./gradlew --version

# Copy remaining project files
COPY . .

# Default command - can be overridden when running container
CMD ["/bin/bash"]
