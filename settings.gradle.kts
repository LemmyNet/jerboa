pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // For snapshot version of Telephoto
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        mavenLocal()
    }
}
rootProject.name = "jerboa"
include(":app")
include(":benchmarks")

