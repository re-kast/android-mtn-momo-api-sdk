import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
}

secrets {
    ignoreList.add("sdk.*")
}

android {
    namespace = "io.rekast.sdk"
    compileSdk = 36

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        animationsDisabled = true
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)

    // Network - Retrofit, OKHTTP, chucker
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging)

    implementation(libs.apache.commons.lang3)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.jakewharton.timber)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.retrofit.coroutines)

    implementation(libs.google.dagger.hilt)
    implementation(libs.androidx.hilt.work)
    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.androidx.test.runner)
    ksp(libs.hilt.android.compiler)

    debugImplementation(libs.chuckerteam.chucker)
    releaseImplementation(libs.chuckerteam.chucker.noop)

    testImplementation(libs.mockk.core)
    testImplementation(libs.mockk.inline)
    testImplementation(libs.mockk.kotlin)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

tasks.named<org.jetbrains.dokka.gradle.DokkaTaskPartial>("dokkaHtmlPartial") {
    dependsOn("kspDebugKotlin", "kspReleaseKotlin")
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(layout.projectDirectory.file("assets/logo-icon.svg").asFile)
        customStyleSheets = listOf((layout.projectDirectory.file("assets/rekast.css").asFile))
        footerMessage = "&copy; Re.Kast Limited"
    }
}
