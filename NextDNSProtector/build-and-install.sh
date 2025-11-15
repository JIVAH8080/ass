#!/bin/bash

# Build and Install Script for NextDNS Protector
# This script builds the APK and installs it with proper permissions

set -e

echo "========================================"
echo "NextDNS Protector - Build & Install"
echo "========================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if ADB is available
if ! command -v adb &> /dev/null; then
    echo -e "${RED}ERROR: ADB not found. Please install Android SDK Platform Tools.${NC}"
    exit 1
fi

# Check if device is connected
echo -e "${YELLOW}Checking for connected devices...${NC}"
DEVICE_COUNT=$(adb devices | grep -w "device" | wc -l)

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo -e "${RED}ERROR: No devices found. Please connect your device and enable USB debugging.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Device connected${NC}"
echo ""

# Build the APK
echo -e "${YELLOW}Building APK...${NC}"
if [ -f "gradlew" ]; then
    chmod +x gradlew
    ./gradlew clean assembleDebug
else
    echo -e "${RED}ERROR: gradlew not found${NC}"
    exit 1
fi

if [ ! -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo -e "${RED}ERROR: APK build failed${NC}"
    exit 1
fi

echo -e "${GREEN}✓ APK built successfully${NC}"
echo ""

# Install the APK
echo -e "${YELLOW}Installing APK...${NC}"
adb install -r app/build/outputs/apk/debug/app-debug.apk

if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Installation failed${NC}"
    exit 1
fi

echo -e "${GREEN}✓ APK installed${NC}"
echo ""

# Grant WRITE_SECURE_SETTINGS permission
echo -e "${YELLOW}Granting WRITE_SECURE_SETTINGS permission...${NC}"
adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS

if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Failed to grant permission${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Permission granted${NC}"
echo ""

# Verify installation
echo -e "${YELLOW}Verifying installation...${NC}"
PKG_INFO=$(adb shell pm list packages | grep com.nextdns.protector)

if [ -z "$PKG_INFO" ]; then
    echo -e "${RED}ERROR: Package not found after installation${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Installation verified${NC}"
echo ""

# Check permission
PERM_CHECK=$(adb shell dumpsys package com.nextdns.protector | grep "WRITE_SECURE_SETTINGS" | grep "granted=true")

if [ -n "$PERM_CHECK" ]; then
    echo -e "${GREEN}✓ WRITE_SECURE_SETTINGS permission confirmed${NC}"
else
    echo -e "${YELLOW}⚠ WRITE_SECURE_SETTINGS permission may not be granted${NC}"
fi

echo ""
echo "========================================"
echo -e "${GREEN}Installation Complete!${NC}"
echo "========================================"
echo ""
echo "Next steps:"
echo "1. Open the NextDNS Protector app on your device"
echo "2. Tap 'Enable Device Admin'"
echo "3. Tap 'Apply NextDNS Settings'"
echo "4. Verify at https://test.nextdns.io"
echo ""
