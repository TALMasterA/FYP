@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        // Robolectric's native graphics jar (~160 MB, used by the screenshot
        // tests, ADR 0006) gets HTTP 429 from repo.maven.apache.org on CI.
        // Fetch only org.robolectric from Google's mirror of Maven Central.
        maven("https://maven-central.storage-download.googleapis.com/maven2/") {
            name = "MavenCentralGoogleMirror"
            content { includeGroup("org.robolectric") }
        }
        mavenCentral()
        google()
    }
}

rootProject.name = "FYP"
include(":app")
include(":macrobenchmark")
