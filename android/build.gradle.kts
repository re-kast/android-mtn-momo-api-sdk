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
import org.gradle.process.ExecOperations

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.diffplug.spotless") version libs.versions.spotless
    id("org.jetbrains.dokka") version libs.versions.dokka
    id("com.github.ben-manes.versions") version libs.versions.gradleVersionsPlugin
    alias(libs.plugins.kover)
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-metadata-jvm") {
                useVersion(libs.versions.kotlinVersion.get())
            }
        }
    }

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/src/**/*.kt", "**/src/**/*.kts")
            targetExclude("**/buildSrc/src/main/kotlin/*.kt")
            trimTrailingWhitespace()
            ktlint(libs.versions.klint.get())
                // Use the root project's .editorconfig so that this path resolves correctly
                // for every subproject inside allprojects {}, not just the root module.
                .setEditorConfigPath("${rootProject.projectDir}/.editorconfig")

            endWithNewline()
            licenseHeaderFile("${rootProject.projectDir}/license-header.txt")
                .yearSeparator("-")
                .updateYearWithLatest(true)
        }
        kotlinGradle {
            target("*.gradle.kts")
            licenseHeaderFile(
                "${rootProject.projectDir}/license-header.txt",
                "^(import|@|plugins|pluginManagement|dependencyResolutionManagement|include|rootProject|buildscript|allprojects|subprojects|abstract|tasks)"
            ).yearSeparator("-")
                .updateYearWithLatest(true)
            ktlint()
        }
    }
}

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}

dokka {
    moduleName.set("| MTN MOMO ANDROID SDK")
    pluginsConfiguration.html {
        customAssets.from(layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
        separateInheritedMembers.set(false)
    }
}

dependencies {
    dokka(project(":momo-api-sdk"))
    dokka(project(":sample"))
    dokka(project(":app"))
    kover(project(":momo-api-sdk"))
    kover(project(":sample"))
}

tasks.register<Copy>("copyDocsToGhPages") {
    dependsOn("dokkaGenerate")
    from(layout.buildDirectory.dir("dokka/html"))
    into(file("docs"))
}

abstract class DeployDocsTask
    @Inject
    constructor(
        private val execOps: ExecOperations
    ) : DefaultTask() {
        @TaskAction
        fun deploy() {
            execOps.exec { commandLine("git", "add", ".") }
            val hasChanges =
                execOps
                    .exec {
                        commandLine("git", "diff", "--cached", "--quiet")
                        isIgnoreExitValue = true
                    }.exitValue != 0
            if (hasChanges) {
                execOps.exec { commandLine("git", "commit", "-m", "Update documentation") }
                execOps.exec { commandLine("git", "push") }
            }
        }
    }

tasks.register<DeployDocsTask>("deployDocs") {
    dependsOn("copyDocsToGhPages")
}
