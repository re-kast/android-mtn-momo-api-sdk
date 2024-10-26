pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "8.7.1"
        id("org.jetbrains.kotlin.android") version "1.8.10"
        id("org.jetbrains.kotlin.android.extensions") version "1.7.0"
        id("com.android.library") version "8.7.1"
        id("com.google.firebase.crashlytics") version "3.0.2"
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
