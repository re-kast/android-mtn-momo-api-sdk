// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    // alias(libs.plugins.kotlin.kapt) version libs.versions.kotlinKapt
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.diffplug.spotless") version libs.versions.spotless
    id("org.jetbrains.dokka") version libs.versions.dokka
    id("com.github.ben-manes.versions") version libs.versions.gradleVersionsPlugin
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/src/**/*.kt", "**/src/**/*.kts")
            targetExclude("**/buildSrc/src/main/kotlin/*.kt")
            trimTrailingWhitespace()
            ktlint(libs.versions.klint.get())
                .setEditorConfigPath(".editorconfig")
            indentWithSpaces()
            endWithNewline()
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
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}
