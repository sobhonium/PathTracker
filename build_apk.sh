#!/bin/bash

# PathTracker APK Build Script
# This script builds the PathTracker Android app APK

echo "🚶 PathTracker APK Build Script"
echo "================================"

# Check if Android SDK is installed
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME environment variable not set."
    echo "Please install Android SDK and set ANDROID_HOME to the SDK path."
    echo "Example: export ANDROID_HOME=/path/to/android/sdk"
    exit 1
fi

echo "✅ Android SDK found at: $ANDROID_HOME"

# Check if Gradle wrapper exists, if not create it
if [ ! -f "gradlew" ]; then
    echo "📦 Creating Gradle wrapper..."
    if command -v gradle &> /dev/null; then
        gradle wrapper --gradle-version=7.5
    else
        echo "❌ Gradle not found. Please install Gradle first."
        exit 1
    fi
fi

echo "🏗️  Building APK..."

# Make gradlew executable
chmod +x gradlew

# Clean and build APK
./gradlew clean
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "🔧 Installation instructions:"
    echo "1. Enable 'Unknown sources' in Android Settings > Security"
    echo "2. Copy the APK to your Android device"
    echo "3. Tap the APK file and install"
    echo ""
    echo "⚠️  Note: This is a debug build. For production, use 'assembleRelease' and sign the APK."
else
    echo "❌ Build failed. Check the error messages above."
    exit 1
fi