@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.baselineprofile")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"

}

// Temp disabled until https://issuetracker.google.com/issues/430991549 fixed
//apply(from = "update_instances.gradle.kts")


kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
        freeCompilerArgs = listOf("-Xjvm-default=all-compatibility", "-opt-in=kotlin.RequiresOptIn")
    }
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jerboa"
        namespace = "com.jerboa"
        minSdk = 26
        targetSdk = 36
        versionCode = 84
        versionName = "0.0.84"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    lint {
        disable += "MissingTranslation"
        disable += "KtxExtensionAvailable"
        disable += "UseKtx"
    }

    // Necessary for f-droid builds
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }


    if (project.hasProperty("RELEASE_STORE_FILE")) {
        signingConfigs {
            create("release") {
                storeFile = file(project.property("RELEASE_STORE_FILE")!!)
                storePassword = project.property("RELEASE_STORE_PASSWORD") as String?
                keyAlias = project.property("RELEASE_KEY_ALIAS") as String?
                keyPassword = project.property("RELEASE_KEY_PASSWORD") as String?

                // Optional, specify signing versions used
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }

    buildTypes {
        release {
            if (project.hasProperty("RELEASE_STORE_FILE")) {
                signingConfig = signingConfigs.getByName("release")
            }

            // Keep using until AGP 9.0 then research proguard rules to retain debug info for stack traces
            postprocessing {
                isRemoveUnusedCode = true
                isObfuscate = false
                isOptimizeCode = true
                isRemoveUnusedResources = true
                proguardFiles("proguard-rules.pro")
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = " (DEBUG)"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    // Match build tools with CI image
    buildToolsVersion = "36.0.0"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

baselineProfile {
    mergeIntoMain = true
    saveInSrc = true
    dexLayoutOptimization = true
}

dependencies {
    // Exporting / importing DB helper
    implementation("com.github.dessalines:room-db-export-import:0.1.0")

    val composeBom = platform("androidx.compose:compose-bom:2025.09.00")

    api(composeBom)
    implementation("androidx.activity:activity-ktx")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.appcompat:appcompat:1.7.1")
    androidTestApi(composeBom)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    implementation("me.zhanghai.compose.preference:library:1.1.1")

    // Markdown support
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:image-coil:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")

    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-runtime-compose")

    // Images
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")
    implementation("io.coil-kt:coil-svg:2.7.0")
    implementation("io.coil-kt:coil-video:2.7.0")

    // Media3 for video playback
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-common:1.8.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.8.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:1.8.0")
    // Allows for proper subsampling of large images
    implementation("me.saket.telephoto:zoomable-image-coil:0.17.0")
    // Animated dropdowns
    implementation("me.saket.cascade:cascade-compose:2.3.0")

    // crash handling
    implementation("com.github.FunkyMuse:Crashy:1.2.0")

    // To use Kotlin annotation processing tool
    ksp("androidx.room:room-compiler:2.8.0")

    implementation("androidx.room:room-runtime:2.8.0")
    annotationProcessor("androidx.room:room-compiler:2.8.0")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.8.0")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:2.8.0")
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
    androidTestImplementation("androidx.room:room-testing:2.8.0")

    implementation("io.arrow-kt:arrow-core:2.1.2")


    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("org.ocpsoft.prettytime:prettytime:5.0.9.Final")
    implementation("androidx.navigation:navigation-compose:2.9.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit")
    androidTestImplementation("androidx.test.espresso:espresso-core")

    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.0.0")

    implementation("androidx.browser:browser:1.9.0")

    implementation("androidx.profileinstaller:profileinstaller")
    baselineProfile(project(":benchmarks"))

    implementation("it.vercruysse.lemmyapi:lemmy-api:0.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // For custom logging plugin
    implementation("io.ktor:ktor-client-logging:3.3.0")
}
