import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "9.2.1" apply false
    id("com.android.library") version "9.2.1" apply false
    id("com.android.test") version "9.2.1" apply false
    id("org.jmailen.kotlinter") version "5.6.0" apply false
    id("com.google.devtools.ksp") version "2.3.10" apply false
    id("androidx.baselineprofile") version "1.5.0-alpha07" apply false
}

subprojects {
    apply(plugin = "org.jmailen.kotlinter") // Version should be inherited from parent
}

// AGP 9.0 has a runtime dependency on the Kotlin Gradle plugin (KGP), defaulting to 2.2.10.
// To use a higher KGP version, declare it here on the top-level buildscript classpath.
// see https://developer.android.com/build/releases/agp-9-0-0-release-notes

// If not forced to 2.4.0, ComposeMappingFile task fails since it will try to get 2.2.10 dependency
// But that was only added in 2.3.0
// https://kotlinlang.org/docs/whatsnew23.html#compose-compiler-stack-traces-for-minified-android-applications
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0")
    }
}


// Enables compose compiler metrics
// Generate them with `./gradlew assembleRelease --rerun-tasks -P enableComposeCompilerReports=true`
// see https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md
subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            val destination = project.layout.buildDirectory.get().asFile.absolutePath
            if (project.findProperty("enableComposeCompilerReports") == "true") {

                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            destination + "/compose_metrics"
                )

                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        destination + "/compose_metrics"
                )
            }
            val configPath = "${project.projectDir.absolutePath}/../compose_compiler_config.conf"
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=$configPath"
            )
        }
    }
}
