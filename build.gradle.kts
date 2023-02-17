plugins {
    id(BuildPlugins.ktlintPlugin) version Versions.ktlint
    id(BuildPlugins.detektPlugin) version Versions.detekt
    id(BuildPlugins.spotlessPlugin) version Versions.spotless
    id(BuildPlugins.androidLibrary) version Versions.library apply false
    id(BuildPlugins.androidApplication) version Versions.application apply false
    id(BuildPlugins.kotlinAndroid) version Versions.kotlinAndroid apply false
    id(BuildPlugins.dokkaPlugin) version Versions.dokka
    id(BuildPlugins.gradleVersionsPlugin) version Versions.gradleVersionsPlugin
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
    apply(plugin = BuildPlugins.ktlintPlugin)
    ktlint {
        android.set(true)
        verbose.set(true)
        filter {
            exclude { element -> element.file.path.contains("generated/") }
        }
    }
}

buildscript {
    val kotlinVersion by extra("1.5.21")
    val jacocoVersion by extra("0.2")
    val nexusPublishVersion by extra("1.1.0")

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.hiya:jacoco-android:$jacocoVersion")
    }
}
