/*
 * Copyright 2023-2024, Benjamin Mwalimu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.secrets)
    alias(libs.plugins.kover)
}

secrets {
    ignoreList.add("sdk.*")
}

android {
    namespace = "io.rekast.sdk.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.rekast.sdk.sample"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
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

dokka {
    dokkaSourceSets {
        create("main") {
            displayName.set("App")
            sourceRoots.from(file("src/main/kotlin"))
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

kover {
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                annotatedBy("*Generated*")
                classes(
                    "**/Hilt_*",
                    "**/*_HiltModules*",
                    "**/*_Provide*",
                    "**/*ComponentTreeDeps*",
                    "**/dagger/**",
                    // Hilt-generated InstanceHolder inner classes
                    "**/*Factory\$InstanceHolder",
                    // Hilt aggregated injectors
                    "hilt_aggregated_deps/**",
                    // MomoApplication is an Android Application class (not unit-testable)
                    "**/MomoApplication",
                    // DispatchersModule is a Hilt module providing coroutine dispatchers;
                    // its single line is not independently testable.
                    "**/DispatchersModule",
                )
            }
        }
    }
}

dependencies {
    // Unit-test dependencies.
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.squareup.okhttp.mockwebserver)

    // The sample library provides all UI, ViewModels, and activities.
    implementation(project(":sample"))

    // The SDK is declared explicitly here so that the DI modules in this module
    // can reference its service interfaces and model classes at compile time.
    // (implementation deps in a KMP library are non-transitive.)
    implementation(project(":momo-api-sdk"))

    // Hilt — application-level DI wiring.
    implementation(libs.google.dagger.hilt)
    ksp(libs.hilt.android.compiler)

    // Network stack used by NetworkModule.
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.serialization)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines (DispatchersModule, DefaultDispatcherProvider).
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines)

    implementation(libs.jakewharton.timber)
}
