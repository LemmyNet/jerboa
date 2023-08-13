@file:Suppress("UnstableApiUsage")
import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    id("androidx.baselineprofile")
}

android {
    namespace = "com.jerboa.benchmarks"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all-compatibility", "-opt-in=kotlin.RequiresOptIn")
    }

    defaultConfig {
        testInstrumentationRunnerArguments += mapOf("suppressErrors" to "EMULATOR")
        minSdk = 26
        targetSdk =  34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Only use the emulator to test benchmarks
    }

    targetProjectPath = ":app"
    // Enable the benchmark to run separately from the app process
    experimentalProperties["android.experimental.self-instrumenting"] = true

    // Baselines profiles needs to generated on a rooted phone
    // Use this plugin to setup and tear down a rooted phone
    testOptions.managedDevices.devices {
        maybeCreate<ManagedVirtualDevice>("pixel6Api31").apply {
            device = "Pixel 6"
            apiLevel = 31
            systemImageSource = "aosp"
        }
    }

    buildTypes {
       register("benchmark") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
    managedDevices += "pixel6Api31"
    useConnectedDevices = false
}

dependencies {
    implementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0-alpha04")
    implementation("androidx.benchmark:benchmark-macro-junit4:1.2.0-beta03")
}
