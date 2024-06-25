@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.baselineprofile")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"

}

apply(from = "update_instances.gradle.kts")

android {
    buildToolsVersion = "34.0.0"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jerboa"
        namespace = "com.jerboa"
        minSdk = 26
        targetSdk = 34
        versionCode = 69
        versionName = "0.0.69"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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
            register("release") {
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
            signingConfig = if (project.hasProperty("RELEASE_STORE_FILE")) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }

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
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all-compatibility", "-opt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        compose = true
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

baselineProfile {
    mergeIntoMain = true
    saveInSrc = true
    dexLayoutOptimization = true
}

dependencies {
    // Unfortunately, ui tooling, and the markdown thing, still brings in the other material2 dependencies
    // This is the "official" composeBom, but it breaks the imageviewer until 1.7 is released. See:
    // https://github.com/LemmyNet/jerboa/pull/1502#issuecomment-2137935525
    // val composeBom = platform("androidx.compose:compose-bom:2024.05.00")

    val composeBom = platform("dev.chrisbanes.compose:compose-bom:2024.05.00-alpha03")
    api(composeBom)
    implementation("androidx.activity:activity-compose")
    implementation("androidx.appcompat:appcompat:1.7.0")
    androidTestApi(composeBom)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    implementation("me.zhanghai.compose.preference:library:1.0.0")

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
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")
    implementation("io.coil-kt:coil-video:2.6.0")
    // Allows for proper subsampling of large images
    implementation("me.saket.telephoto:zoomable-image-coil:0.11.2")
    // Animated dropdowns
    implementation("me.saket.cascade:cascade-compose:2.3.0")

    // crash handling
    implementation("com.github.FunkyMuse:Crashy:1.2.0")

    // To use Kotlin annotation processing tool
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")

    implementation("io.arrow-kt:arrow-core:1.2.4")


    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("org.ocpsoft.prettytime:prettytime:5.0.8.Final")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit")
    androidTestImplementation("androidx.test.espresso:espresso-core")

    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")

    implementation("androidx.browser:browser:1.8.0")

    implementation("androidx.profileinstaller:profileinstaller")
    baselineProfile(project(":benchmarks"))

    implementation("it.vercruysse.lemmyapi:lemmy-api:0.2.16-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // For custom logging plugin
    implementation("io.ktor:ktor-client-logging:2.3.12")
}
