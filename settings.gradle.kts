pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "7.4.2"
        id("org.jetbrains.kotlin.android") version "1.7.0"
        id("org.jetbrains.kotlin.android.extensions") version "1.7.0"
        id("com.android.library") version "7.4.2"
        id("com.google.firebase.crashlytics") version "2.5.2"
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.google.firebase.crashlytics" -> useModule("com.google.firebase:firebase-crashlytics-gradle:2.1.0")
            }
        }
    }
}

include("sample")
include(":momo-api-sdk")

rootProject.name = "MTN Momo API SDK"
