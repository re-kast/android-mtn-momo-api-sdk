/*
 * Copyright 2023-2026, Benjamin Mwalimu
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
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // AGP 9.x combined plugin — replaces com.android.library and resolves the
    // kotlin.multiplatform + com.android.library incompatibility introduced in AGP 9.0.
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // With com.android.kotlin.multiplatform.library the Android target is configured
    // inside kotlin { android { } } — there is no separate top-level android {} block.
    android {
        namespace = "io.rekast.sdk"
        compileSdk = 37
        minSdk = 24

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }

        // Enable unit tests (host-side); disabled by default in the new plugin.
        withHostTestBuilder {}.configure {
            isReturnDefaultValues = true
            isIncludeAndroidResources = false
        }

        // Enable instrumented tests (device-side); disabled by default.
        withDeviceTestBuilder {}
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.squareup.retrofit)
            implementation(libs.squareup.okhttp)
            implementation(libs.squareup.okhttp.logging)
            implementation(libs.squareup.retrofit.serialization)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            // JSR-330 annotations used in commonMain (@Inject, @Singleton).
            // Hilt (androidMain) pulls this in transitively on Android; the explicit
            // declaration here makes it available for JVM target compilation too.
            implementation(libs.javax.inject)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.jakewharton.timber)
            implementation(libs.kotlinx.coroutines)
        }
        // With the new plugin, unit-test sources live in androidHostTest and
        // instrumented-test sources live in androidDeviceTest.
        named("androidHostTest") {
            kotlin.srcDirs("src/test/kotlin")
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk)
                implementation(libs.mockito.core)
                implementation(libs.mockito.inline)
                implementation(libs.mockito.kotlin)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        named("androidDeviceTest") {
            kotlin.srcDirs("src/androidTest/kotlin")
            dependencies {
                implementation(libs.androidx.monitor)
                implementation(libs.androidx.test.runner)
            }
        }
    }
}

// No KSP processors needed at the SDK level — Hilt wiring is done in the consuming app.

dokka {
    dokkaSourceSets {
        named("commonMain") {
            displayName.set("Common")
            sourceRoots.from(file("src/commonMain/kotlin"))
            externalDocumentationLinks.register("kotlinx.coroutines") {
                url("https://kotlinlang.org/api/kotlinx.coroutines/")
                packageListUrl("https://kotlinlang.org/api/kotlinx.coroutines/package-list")
            }
            externalDocumentationLinks.register("okhttp") {
                url("https://square.github.io/okhttp/4.x/okhttp/")
                packageListUrl("https://square.github.io/okhttp/4.x/okhttp/package-list")
            }
        }
        named("androidMain") {
            displayName.set("Android")
            sourceRoots.from(file("src/androidMain/kotlin"))
            externalDocumentationLinks.register("android") {
                url("https://developer.android.com/reference/kotlin/")
                packageListUrl("https://developer.android.com/reference/kotlin/package-list")
            }
        }
        named("jvmMain") {
            displayName.set("JVM")
            sourceRoots.from(file("src/jvmMain/kotlin"))
        }
    }
    pluginsConfiguration.html {
        customAssets.from(rootProject.layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(rootProject.layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
    }
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
                    "**/dagger/**"
                )
                // DefaultSource is pure delegation to sealed Retrofit service interfaces.
                // The sealed keyword prevents both MockK and JVM Proxy from creating
                // test doubles, making unit testing impossible without a full Hilt graph.
                // All meaningful logic is tested via DefaultRepository (which mocks DefaultSource).
                classes("**/DefaultSource")
            }
        }
    }
}

afterEvaluate {
    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        val dokkaHtml = tasks.named("dokkaGenerateModuleHtml")
        dependsOn(dokkaHtml)
        from(dokkaHtml.map { it.outputs.files })
    }

    publishing {
        val versionName = (project.properties["VERSION_NAME"] as? String) ?: "unspecified"
        val groupName = (project.properties["GROUP"] as? String) ?: "io.rekast"

        publications.withType<MavenPublication>().configureEach {
            artifact(javadocJar)
            groupId = groupName
            version = versionName

            pom {
                name.set(project.properties["POM_NAME"] as? String ?: "MTN MoMo API SDK")
                description.set(project.properties["POM_DESCRIPTION"] as? String ?: "")
                url.set(project.properties["POM_URL"] as? String ?: "")
                inceptionYear.set(project.properties["POM_INCEPTION_YEAR"] as? String ?: "2023")

                licenses {
                    license {
                        name.set(project.properties["POM_LICENSE_NAME"] as? String ?: "")
                        url.set(project.properties["POM_LICENSE_URL"] as? String ?: "")
                        distribution.set(project.properties["POM_LICENSE_DIST"] as? String ?: "")
                    }
                }

                developers {
                    developer {
                        id.set(project.properties["POM_DEVELOPER_ID"] as? String ?: "")
                        name.set(project.properties["POM_DEVELOPER_NAME"] as? String ?: "")
                        email.set(project.properties["POM_DEVELOPER_EMAIL"] as? String ?: "")
                        url.set(project.properties["POM_DEVELOPER_URL"] as? String ?: "")
                    }
                }

                scm {
                    url.set(project.properties["POM_SCM_URL"] as? String ?: "")
                    connection.set(project.properties["POM_SCM_CONNECTION"] as? String ?: "")
                    developerConnection.set(project.properties["POM_SCM_DEV_CONNECTION"] as? String ?: "")
                }
            }
        }

        repositories {
            if (versionName.endsWith("SNAPSHOT")) {
                maven {
                    name = "mavenCentralSnapshots"
                    url = uri("https://central.sonatype.com/repository/maven-snapshots/")
                    credentials {
                        username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull
                        password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull
                    }
                }
            } else {
                maven {
                    name = "localStaging"
                    url = uri(layout.buildDirectory.dir("staging-deploy"))
                }
            }

            maven {
                name = "githubPackages"
                url = uri("https://maven.pkg.github.com/re-kast/android-mtn-momo-api-sdk")
                credentials {
                    username = providers.environmentVariable("GITHUB_ACTOR").orNull
                    password = providers.environmentVariable("GITHUB_TOKEN").orNull
                }
            }
        }
    }

    signing {
        val signingKey = providers.environmentVariable("SIGNING_KEY").orNull
        val signingPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull
        if (signingKey != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }

    // Gradle 9.x strict dependency ordering fix for KMP + signing:
    // each publication's publish task shares the Javadoc .asc artifact produced
    // by the other publications' sign tasks, so we must declare explicit ordering.
    tasks.withType<AbstractPublishToMaven>().configureEach {
        mustRunAfter(tasks.withType<Sign>())
    }
}
