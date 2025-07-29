#!/bin/bash

# Voice Cloning omidnini1 - APK Build Script
# This script automates the APK building process

echo "🎤 Voice Cloning omidnini1 - APK Builder"
echo "========================================"

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo "❌ Error: gradlew not found. Make sure you're in the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

echo "🔧 Cleaning previous builds..."
./gradlew clean

echo "📦 Building Debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Debug APK built successfully!"
    echo "📍 Location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Failed to build Debug APK"
    exit 1
fi

echo ""
echo "🚀 Building Release APK..."
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo "✅ Release APK built successfully!"
    echo "📍 Location: app/build/outputs/apk/release/app-release.apk"
    
    # Copy APK to root directory for easy access
    cp app/build/outputs/apk/debug/app-debug.apk ./voice-cloning-debug.apk
    cp app/build/outputs/apk/release/app-release.apk ./voice-cloning-release.apk
    
    echo ""
    echo "📱 APK files copied to project root:"
    echo "   - voice-cloning-debug.apk"
    echo "   - voice-cloning-release.apk"
    echo ""
    echo "🎉 Build completed successfully!"
    echo "   You can now install the APK on your Android device."
else
    echo "❌ Failed to build Release APK"
    exit 1
fi

echo ""
echo "📋 Installation Instructions:"
echo "1. Transfer the APK file to your Android device"
echo "2. Enable 'Install from unknown sources' in Settings"
echo "3. Tap the APK file to install"
echo "4. Grant required permissions when prompted"
echo ""
echo "🎤 Enjoy voice cloning with omidnini1!"