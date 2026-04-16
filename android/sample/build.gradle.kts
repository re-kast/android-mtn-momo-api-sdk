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
            isIncludeAndroidResources = false
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(project(":momo-api-sdk"))
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
                )
            }
        }
    }
}
