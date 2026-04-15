pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "9.1.1"
        id("org.jetbrains.kotlin.android") version "2.3.20"
        id("org.jetbrains.kotlin.android.extensions") version "2.3.20"
        id("com.android.library") version "9.1.1"
        id("com.google.firebase.crashlytics") version "3.0.6"
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.google.firebase.crashlytics" -> useModule("com.google.firebase:firebase-crashlytics-gradle:2.1.0")
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("sample")
include(":momo-api-sdk")

rootProject.name = "MTN Momo API SDK"
