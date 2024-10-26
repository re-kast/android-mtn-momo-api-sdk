plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version libs.versions.secrets.get()
    id("com.diffplug.spotless") version libs.versions.spotless.get()
    id("org.jetbrains.dokka") version libs.versions.dokka.get()
    id("com.github.ben-manes.versions") version libs.versions.gradleVersionsPlugin.get()
}

android {
    namespace = "io.rekast.sdk.sample"
    compileSdk = 34

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
        buildConfig = true
    }

    secrets {
        ignoreList.add("sdk.*")
    }

    defaultConfig {
        applicationId = "io.rekast.sdk.sample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "io.rekast.sdk.sample.runner.MockTestRunner"
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinAndroid.get()
    }

    buildTypes {
        debug {
            isDebuggable = true
            versionNameSuffix = " - debug"
            applicationIdSuffix = ".debug"
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

kapt {
    generateStubs = true
    correctErrorTypes = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":momo-api-sdk"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime.rxjava)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.customview)
    implementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.google.dagger.hilt)
    kapt(libs.google.dagger.hilt.compiler)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.dynamic)

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging)

    implementation(libs.jakewharton.timber)
    implementation(libs.apache.commons.lang3)

    debugImplementation(libs.squareup.leakcanary)

    // Testing dependencies
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.kotlinx.coroutines.test)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
