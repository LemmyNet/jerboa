#!/bin/bash
pushd ../

TAG=$(git describe --tags --abbrev=0)

./gradlew clean
./gradlew assembleRelease
./gradlew bundleRelease

# Full app
cp app/build/outputs/apk/gplay/release/app-gplay-release.apk ~/Sync/jerboa-gplay-$TAG.apk
cp app/build/outputs/bundle/gplayRelease/app-gplay-release.aab ~/Sync/jerboa-gplay-$TAG.aab

# fdroid apk
cp app/build/outputs/apk/fdroid/release/app-fdroid-release.apk ~/Sync/jerboa-fdroid-$TAG.apk
