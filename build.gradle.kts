import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "8.5.1" apply false
    id("com.android.library") version "8.5.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.10" apply false
    id("org.jmailen.kotlinter") version "4.4.1" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.24" apply false
    id("com.android.test") version "8.5.1" apply false
    id("androidx.baselineprofile") version "1.3.0-beta02" apply false
}

subprojects {
    apply(plugin = "org.jmailen.kotlinter") // Version should be inherited from parent
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
