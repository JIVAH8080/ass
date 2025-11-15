# Quick Installation Guide

## Prerequisites
- Android device with Android 8.0 or higher
- Computer with ADB installed
- USB cable for connecting device to computer

## Step-by-Step Installation

### 1. Enable Developer Options on Your Android Device

1. Go to **Settings** → **About Phone**
2. Tap **Build Number** 7 times
3. Developer options will be enabled

### 2. Enable USB Debugging

1. Go to **Settings** → **Developer Options**
2. Enable **USB Debugging**
3. Connect your device to computer via USB
4. Accept the debugging authorization prompt on your device

### 3. Verify ADB Connection

On your computer, run:
```bash
adb devices
```

You should see your device listed.

### 4. Build the APK

#### Option A: Using Android Studio
1. Open the NextDNSProtector project in Android Studio
2. Select **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Wait for build to complete
4. The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

#### Option B: Using Command Line
```bash
cd NextDNSProtector
./gradlew assembleDebug
```

For release build:
```bash
./gradlew assembleRelease
```

### 5. Install the APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or for release:
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### 6. Grant Required Permission

**CRITICAL**: This permission is required for DNS configuration:

```bash
adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS
```

### 7. Launch and Configure

1. Open **NextDNS Protector** app on your device
2. Tap **"Enable Device Admin (Make Undeletable)"**
3. Read the permissions and tap **"Activate"**
4. Tap **"Apply NextDNS Settings"**
5. Tap **"Check Status"** to verify everything is working

### 8. Verify DNS is Working

Visit https://test.nextdns.io in your browser. You should see:
- Profile: `e628d2`
- Status: Connected

## Quick Command Reference

### One-Line Installation
```bash
# Build, install, and grant permissions
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS
```

### Check if Permission is Granted
```bash
adb shell dumpsys package com.nextdns.protector | grep WRITE_SECURE_SETTINGS
```

### Check Device Admin Status
```bash
adb shell dpm list-owners
```

### Check DNS Configuration
```bash
adb shell getprop | grep dns
```

### Uninstall (if needed)
```bash
adb shell dpm remove-active-admin com.nextdns.protector/.AdminReceiver
adb uninstall com.nextdns.protector
```

## Troubleshooting

### "INSTALL_FAILED_UPDATE_INCOMPATIBLE"
Uninstall the old version first:
```bash
adb uninstall com.nextdns.protector
```

### "Permission Denial: not allowed to send broadcast"
Make sure you granted WRITE_SECURE_SETTINGS permission.

### "Device unauthorized"
Check your device screen for USB debugging authorization prompt.

### "No devices/emulators found"
- Check USB cable connection
- Enable USB debugging
- Try different USB port
- Install device drivers (Windows only)

## Android Studio Setup

If using Android Studio:

1. **Install Android Studio**: Download from https://developer.android.com/studio
2. **Open Project**: File → Open → Select NextDNSProtector folder
3. **Sync Gradle**: Wait for Gradle sync to complete
4. **Connect Device**: Connect via USB or use emulator
5. **Run**: Click the green Run button or Shift+F10

## Building Signed Release APK

For production deployment:

1. **Generate Keystore** (first time only):
```bash
keytool -genkey -v -keystore nextdns-release.keystore -alias nextdns -keyalg RSA -keysize 2048 -validity 10000
```

2. **Create signing config** in `app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file("../nextdns-release.keystore")
            storePassword "your_store_password"
            keyAlias "nextdns"
            keyPassword "your_key_password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

3. **Build signed APK**:
```bash
./gradlew assembleRelease
```

## Deployment to Multiple Devices

### Via ADB
```bash
# Install to all connected devices
adb devices | tail -n +2 | cut -sf 1 | xargs -I {} adb -s {} install app-debug.apk
```

### Via MDM Solution
- Upload APK to your MDM console
- Push to managed devices
- Grant WRITE_SECURE_SETTINGS via MDM policy

### Via Internal Distribution
1. Host the APK on internal server
2. Provide download link to users
3. Users enable "Install from Unknown Sources"
4. Guide users through installation and ADB permission grant

## Next Steps

After installation:
1. Configure NextDNS settings at https://my.nextdns.io
2. Set up Security filters
3. Configure Privacy settings
4. Enable Parental Controls if needed
5. Review Analytics and Logs

## Support

For issues:
- Check README.md for detailed documentation
- Review troubleshooting section
- Check Android logs: `adb logcat | grep NextDNS`
