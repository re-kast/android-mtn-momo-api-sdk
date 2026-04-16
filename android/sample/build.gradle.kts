plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.secrets)
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
}

secrets {
    ignoreList.add("sdk.*")
}

android {
    namespace = "io.rekast.sdk.sample"
    compileSdk = 37

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "io.rekast.sdk.sample"
        minSdk = 24
        targetSdk = 37
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

    buildTypes {
        debug {
            isDebuggable = true
            versionNameSuffix = " - debug"
            applicationIdSuffix = ".debug"
        }
        release {
            isShrinkResources = false
            isMinifyEnabled = false
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":momo-api-sdk"))

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    ksp(libs.androidx.lifecycle.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)
    implementation(libs.work.runtime.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime.rxjava)
    implementation(libs.androidx.customview)
    implementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.google.dagger.hilt)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.android.compiler)

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

    implementation(libs.kotlinx.coroutines)
    implementation(libs.retrofit.coroutines)

    debugImplementation(libs.chuckerteam.chucker)

    // Testing dependencies
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.lifecycle.runtime.testing)

    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // For Hilt testing
    // androidTestImplementation(libs.google.dagger.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    // testImplementation(libs.google.dagger.hilt.android.testing)
    kspTest(libs.hilt.android.compiler)

    releaseImplementation(libs.chuckerteam.chucker.noop)
}
dokka {
    dokkaSourceSets {
        register("main") {
            sourceRoots.from(file("src/main/kotlin"))
            displayName.set("Android")
        }
    }
    pluginsConfiguration.html {
        customAssets.from(rootProject.layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(rootProject.layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
    }
}

tasks.matching { it.name.startsWith("dokkaGenerate") }.configureEach {
    dependsOn("kspDebugKotlin", "kspReleaseKotlin")
}
