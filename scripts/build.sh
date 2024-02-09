#!/bin/bash
pushd ../

./gradlew assembleRelease
./gradlew bundleRelease

cp app/build/outputs/apk/release/app-release.apk ~/Sync/
cp app/build/outputs/bundle/release/app-release.aab ~/Sync/
