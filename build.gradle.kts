plugins {
    id(BuildPlugins.detektPlugin) version Versions.detekt
    id(BuildPlugins.spotlessPlugin) version Versions.spotless
    id(BuildPlugins.androidLibrary) version Versions.library apply false
    id(BuildPlugins.androidApplication) version Versions.application apply false
    id(BuildPlugins.kotlinAndroid) version Versions.kotlinAndroid apply false
    id(BuildPlugins.mapsSecret) version Versions.secrets apply false
    id(BuildPlugins.dokkaPlugin) version Versions.dokka
    id(BuildPlugins.gradleVersionsPlugin) version Versions.gradleVersionsPlugin
    id(BuildPlugins.kotlinSerialization) version Versions.kotlinAndroid
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    apply(plugin = BuildPlugins.dokkaPlugin)
    apply(plugin = BuildPlugins.spotlessPlugin)

    spotless {
        kotlin {
            target("**/src/**/*.kt", "**/src/**/*.kts")
            targetExclude("**/buildSrc/src/main/kotlin/*.kt")
            ktlint("0.48.2")
                .setEditorConfigPath(".editorconfig")
                .userData(
                    mapOf(
                        "android" to "true",
                        "ij_kotlin_allow_trailing_comma" to "true",
                        "ij_kotlin_allow_trailing_comma_on_call_site" to "true"
                    )
                )
            licenseHeaderFile("$projectDir/license-header.txt")
        }
        kotlinGradle {
            target("*.gradle.kts")
            licenseHeaderFile("$projectDir/license-header.txt", "")
            ktlint()
        }
    }
}

buildscript {
    val kotlinVersion by extra("1.8.10")
    val jacocoVersion by extra("0.2")
    val daggerHilt by extra("2.45")
    val safeNavigation by extra("2.6.0-alpha06")

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.hiya:jacoco-android:$jacocoVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$daggerHilt")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$safeNavigation")
    }
}
