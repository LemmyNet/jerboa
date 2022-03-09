#!/bin/bash
set -e

port=$1

# Get port

# Build
./gradlew assembleDebug

# Push the apk
adb -t $port install -r app/build/outputs/apk/debug/app-debug.apk

# Run jhe app
adb -t $port shell monkey -p com.jerboa 1
