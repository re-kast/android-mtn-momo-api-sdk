/*
 * Copyright 2026, Benjamin Mwalimu
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
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kover)
}

android {
    namespace = "io.rekast.sdk.sample"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            // Robolectric Compose UI tests need real string/resource lookups (stringResource, TopBar title).
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(project(":momo-api-sdk"))
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.google.dagger.hilt)
    implementation(libs.jakewharton.timber)
    // ViewModels read ResponseBody directly (SDK returns NetworkResult<ResponseBody>).
    // okhttp3.ResponseBody is not transitively exposed from momo-api-sdk's implementation deps.
    implementation(libs.squareup.okhttp)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.lifecycle.lifecycle.compiler)
    kspAndroidTest(libs.hilt.android.compiler)

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
    // Compose UI tests for the screen composables, run on the JVM via Robolectric.
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

dokka {
    dokkaSourceSets {
        create("main") {
            displayName.set("Sample")
            sourceRoots.from(file("src/main/kotlin"))
            externalDocumentationLinks.register("kotlinx.coroutines") {
                url("https://kotlinlang.org/api/kotlinx.coroutines/")
                packageListUrl("https://kotlinlang.org/api/kotlinx.coroutines/package-list")
            }
            externalDocumentationLinks.register("android") {
                url("https://developer.android.com/reference/kotlin/")
                packageListUrl("https://developer.android.com/reference/kotlin/package-list")
            }
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
                // Excludes generated code, all Compose @Preview functions (dev-only tooling), and
                // every @Composable function. Composables are declarative UI whose reachable body
                // branches are inseparable (same synthetic method) from the Compose compiler's
                // $changed/$default recomposition-skip branches, which no unit or UI test can
                // exercise. The composables remain covered by the Robolectric UI test suite as
                // regression protection; they are simply not counted toward branch coverage.
                annotatedBy(
                    "*Generated*",
                    "androidx.compose.ui.tooling.preview.Preview"
                )
                // Kover class filters match fully-qualified names with '.' separators and
                // '*'/'?' wildcards (where '*' also spans package separators). Slash-based
                // globs silently match nothing, so these use dotted patterns.
                classes(
                    // Standard Hilt-generated classes.
                    "*Hilt_*",
                    "*_HiltModules*",
                    "*_Provide*",
                    "*ComponentTreeDeps*",
                    "dagger.*",
                    // Hilt-generated InstanceHolder inner classes.
                    "*Factory\$InstanceHolder",
                    // Hilt aggregated dependency injectors (_io_* in hilt_aggregated_deps).
                    "hilt_aggregated_deps.*",
                    // Compose generated singletons and Activity entry points.
                    "*ActivityKt",
                    "*ActivityKt\$*",
                    "*ComposableSingletons*",
                    // CredentialStorage uses EncryptedSharedPreferences (Android runtime only).
                    "io.rekast.sdk.sample.utils.CredentialStorage",
                    "io.rekast.sdk.sample.utils.CredentialStorage\$*",
                    // AndroidExtensions uses android.* APIs not available in unit tests.
                    "io.rekast.sdk.sample.utils.AndroidExtensionsKt",
                    "io.rekast.sdk.sample.utils.AndroidExtensionsKt\$*",
                    // DispatcherProvider default implementations (interface defaults, not logic).
                    "io.rekast.sdk.sample.utils.DispatcherProvider",
                    "io.rekast.sdk.sample.utils.DispatcherProvider\$*",
                    "io.rekast.sdk.sample.utils.DefaultDispatcherProvider*",
                    // ViewModel emitSnackBarState lambdas (fire-and-forget SharedFlow emit).
                    "*\$emitSnackBarState\$*"
                )
            }
        }
    }
}
