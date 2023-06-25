@file:Suppress("UnstableApiUsage")


import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.baselineprofile")
}

apply(from = "get_instances.gradle.kts")

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.jerboa"
        namespace = "com.jerboa"
        minSdk = 26
        targetSdk = 33
        versionCode = 35
        versionName = "0.0.35"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
      manifestPlaceholders["deepLinks_manifestPlaceholder"] = genManifestHosts()
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

        register("generateProfiles") { // use this variant to generate the profiles
            isMinifyEnabled = false // The startup profiles needs minification off
            isShrinkResources = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles("benchmark-rules.pro") // The baseline profile generator needs obfuscation off
            applicationIdSuffix = ".benchmark"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.github.alorma:compose-settings-ui-m3:0.27.0")

    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // Markdown support
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:image-coil:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")

    // Accompanist
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")

    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // gif support
    implementation("io.coil-kt:coil-gif:2.4.0")

    // To use Kotlin annotation processing tool
    ksp("androidx.room:room-compiler:2.5.1")

    implementation("androidx.room:room-runtime:2.5.1")
    annotationProcessor("androidx.room:room-compiler:2.5.1")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.1")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:2.5.1")
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:2.5.1")

    implementation("io.arrow-kt:arrow-core:1.1.5")
    // Unfortunately, ui tooling, and the markdown thing, still brings in the other material2 dependencies
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    implementation("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.activity:activity-compose:1.7.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.3")
    implementation("net.engawapg.lib:zoomable:1.4.3")
    implementation("androidx.browser:browser:1.5.0")

    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    baselineProfile(project(":benchmarks"))
}


fun genManifestHosts(): String {

    val DEFAULT_LEMMY_INSTANCES = arrayOf(
        "lemmy.world", // 13699 monthly users
        "lemmy.ml", // 4164 monthly users
        "beehaw.org", // 3708 monthly users
        "feddit.de", // 2300 monthly users
        "sh.itjust.works", // 2189 monthly users
        "www.hexbear.net", // 1600 monthly users
    )

    return DEFAULT_LEMMY_INSTANCES.map { "<data android:host=\"$it\"/>" }.joinToString { "\n" }
}

tasks.register("modifyManifest") {
    doLast {
        android.applicationVariants.all { variant ->
            variant.outputs.forEach { output ->
                val task = output.processManifestProvider.get()
                task.
                task.doLast {
                    val manifestFile = File (this.outputs.files., "AndroidManifest.xml")
                    var manifestContent = manifestFile.readText(Charsets.UTF_8)

                    // Perform any modifications to the manifestContent string as needed
                    // For example, replace a placeholder with a custom value
                    manifestContent = manifestContent.replace("%CUSTOM_KEY%", "customValue")

                    // Write the modified manifest back to the file
                    val writeSuccessful = manifestFile.writeText(manifestContent, Charsets.UTF_8, true)
                    if (!writeSuccessful) {
                        throw GradleException("Failed to write modified manifest file: ${manifestFile.absolutePath}")
                    }
                }
            }
            true
        }
    }
}
