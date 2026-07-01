#!/bin/sh
set -e

pushd ../

# Build and push
./gradlew installFdroidDebug

# Run the app
adb shell monkey -p com.jerboa 1
