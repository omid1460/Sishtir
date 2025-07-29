@echo off
echo 🎤 Voice Cloning omidnini1 - APK Builder (Windows)
echo ================================================

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo ❌ Error: gradlew.bat not found. Make sure you're in the project root directory.
    pause
    exit /b 1
)

echo 🔧 Cleaning previous builds...
call gradlew.bat clean

echo 📦 Building Debug APK...
call gradlew.bat assembleDebug

if %errorlevel% equ 0 (
    echo ✅ Debug APK built successfully!
    echo 📍 Location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ❌ Failed to build Debug APK
    pause
    exit /b 1
)

echo.
echo 🚀 Building Release APK...
call gradlew.bat assembleRelease

if %errorlevel% equ 0 (
    echo ✅ Release APK built successfully!
    echo 📍 Location: app\build\outputs\apk\release\app-release.apk
    
    REM Copy APK to root directory for easy access
    copy "app\build\outputs\apk\debug\app-debug.apk" "voice-cloning-debug.apk"
    copy "app\build\outputs\apk\release\app-release.apk" "voice-cloning-release.apk"
    
    echo.
    echo 📱 APK files copied to project root:
    echo    - voice-cloning-debug.apk
    echo    - voice-cloning-release.apk
    echo.
    echo 🎉 Build completed successfully!
    echo    You can now install the APK on your Android device.
) else (
    echo ❌ Failed to build Release APK
    pause
    exit /b 1
)

echo.
echo 📋 Installation Instructions:
echo 1. Transfer the APK file to your Android device
echo 2. Enable 'Install from unknown sources' in Settings
echo 3. Tap the APK file to install
echo 4. Grant required permissions when prompted
echo.
echo 🎤 Enjoy voice cloning with omidnini1!
pause