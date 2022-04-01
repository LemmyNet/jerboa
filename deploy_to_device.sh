#!/bin/bash
set -e

port=$1

# Get port

# Build and push
./gradlew installDebug

# Run the app
adb shell monkey -p com.jerboa 1
