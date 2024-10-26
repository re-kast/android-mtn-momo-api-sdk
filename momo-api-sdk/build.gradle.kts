import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.secrets)
    alias(libs.plugins.vanniktech.maven.publish)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 34 // Update this to match your project's compileSdkVersion

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    secrets {
        ignoreList.add("sdk.*")
    }

    defaultConfig {
        minSdk = 24 // Update this to match your project's minSdkVersion
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        debug {
            // Debug-specific configurations
        }
        release {
            isMinifyEnabled = true
            // Release-specific configurations
        }
    }
    namespace = "io.rekast.sdk"
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

    implementation(libs.google.dagger.hilt)
    kapt(libs.hilt.android.compiler)
    debugImplementation(libs.chuckerteam.chucker)
    releaseImplementation(libs.chuckerteam.chucker.noop)

    // debug
    implementation(libs.jakewharton.timber)
    testImplementation(libs.junit)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01, true)
    signAllPublications()
}
