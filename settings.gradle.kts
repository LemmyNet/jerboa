pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // For Snapshots of LemmyAPI
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    }
}
rootProject.name = "jerboa"
include(":app")
include(":benchmarks")

