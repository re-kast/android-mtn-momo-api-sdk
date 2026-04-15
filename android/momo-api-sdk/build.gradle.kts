plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
}

secrets {
    ignoreList.add("sdk.*")
}

android {
    namespace = "io.rekast.sdk"
    compileSdk = 37

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

    publishing {
        singleVariant("release")
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

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

dokka { pluginsConfiguration.html {
        customAssets.from(layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
    }
}

tasks.matching { it.name.startsWith("dokkaGenerate") }.configureEach {
    dependsOn("kspDebugKotlin", "kspReleaseKotlin")
}

afterEvaluate {
    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
    }

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        val dokkaHtml = tasks.named("dokkaGenerateHtml")
        dependsOn(dokkaHtml)
        from(dokkaHtml.map { it.outputs.files })
    }

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifact(sourcesJar)
                artifact(javadocJar)

                groupId = project.properties["GROUP"] as String
                artifactId = project.properties["POM_ARTIFACT_ID"] as String
                version = project.properties["VERSION_NAME"] as String

                pom {
                    name.set(project.properties["POM_NAME"] as String)
                    description.set(project.properties["POM_DESCRIPTION"] as String)
                    url.set(project.properties["POM_URL"] as String)
                    inceptionYear.set(project.properties["POM_INCEPTION_YEAR"] as String)

                    licenses {
                        license {
                            name.set(project.properties["POM_LICENSE_NAME"] as String)
                            url.set(project.properties["POM_LICENSE_URL"] as String)
                            distribution.set(project.properties["POM_LICENSE_DIST"] as String)
                        }
                    }

                    developers {
                        developer {
                            id.set(project.properties["POM_DEVELOPER_ID"] as String)
                            name.set(project.properties["POM_DEVELOPER_NAME"] as String)
                            url.set(project.properties["POM_DEVELOPER_URL"] as String)
                        }
                    }

                    scm {
                        url.set(project.properties["POM_SCM_URL"] as String)
                        connection.set(project.properties["POM_SCM_CONNECTION"] as String)
                        developerConnection.set(project.properties["POM_SCM_DEV_CONNECTION"] as String)
                    }
                }
            }
        }

        repositories {
            val version = project.properties["VERSION_NAME"] as String

            if (version.endsWith("SNAPSHOT")) {
                // Snapshots deploy directly to the Maven Central snapshots repository.
                maven {
                    name = "mavenCentralSnapshots"
                    url = uri("https://central.sonatype.com/repository/maven-snapshots/")
                    credentials {
                        username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull
                        password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull
                    }
                }
            } else {
                // Releases are written to a local staging dir. CI bundles them into a
                // ZIP and uploads to the Maven Central portal API.
                maven {
                    name = "localStaging"
                    url = uri(layout.buildDirectory.dir("staging-deploy"))
                }
            }

            // GitHub Packages — published for both releases and snapshots.
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
            sign(publishing.publications["release"])
        }
    }
}
