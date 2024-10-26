plugins {
    id(BuildPlugins.spotlessPlugin) version Versions.spotless
    id(BuildPlugins.androidLibrary) version Versions.library apply false
    id(BuildPlugins.androidApplication) version Versions.application apply false
    id(BuildPlugins.kotlinAndroid) version Versions.kotlinAndroid apply false
    id(BuildPlugins.mapsSecret) version Versions.secrets apply false
    id(BuildPlugins.dokkaPlugin) version Versions.dokka
    id(BuildPlugins.gradleVersionsPlugin) version Versions.gradleVersionsPlugin
    id(BuildPlugins.kotlinSerialization) version Versions.kotlinAndroid
    id(BuildPlugins.vanniktechMavenPublish) version Versions.vanniktechMavenPublish apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    apply(plugin = BuildPlugins.dokkaPlugin)
    apply(plugin = BuildPlugins.spotlessPlugin)

    spotless {
        kotlin {
            target("**/src/**/*.kt", "**/src/**/*.kts")
            targetExclude("**/buildSrc/src/main/kotlin/*.kt")
            ktlint(Versions.klint)
                .setEditorConfigPath(".editorconfig")
                .userData(
                    mapOf(
                        "android" to "true"
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
    val kotlinVersion by extra(Versions.kotlinAndroid)
    val jacocoVersion by extra(Versions.hiyaJacoco)
    val daggerHilt by extra(Versions.hilt)
    val safeNavigation by extra(Versions.navigation)

    dependencies {
        classpath("${BuildPlugins.kotlinGradlePlugin}:$kotlinVersion")
        //classpath("${BuildPlugins.hiyaJacocoPlugin}:$jacocoVersion")
        classpath("${BuildPlugins.hiltGradlePlugin}:$daggerHilt")
        classpath("${BuildPlugins.navigationGradlePlugin}:$safeNavigation")
    }
}
