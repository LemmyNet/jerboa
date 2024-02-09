#!/bin/sh
set -e

pushd ../

# Build and push
./gradlew installDebug

# Run the app
adb shell monkey -p com.jerboa 1
