import org.gradle.process.ExecOperations

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.diffplug.spotless") version libs.versions.spotless
    id("org.jetbrains.dokka") version libs.versions.dokka
    id("com.github.ben-manes.versions") version libs.versions.gradleVersionsPlugin
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
                .setEditorConfigPath(".editorconfig")

            endWithNewline()
            licenseHeaderFile("$projectDir/license-header.txt")
        }
        kotlinGradle {
            target("*.gradle.kts")
            licenseHeaderFile("$projectDir/license-header.txt", "")
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
}


tasks.register<Copy>("copyDocsToGhPages") {
    dependsOn("dokkaGenerate")
    from(layout.buildDirectory.dir("dokka/html"))
    into(file("docs"))
}

abstract class DeployDocsTask @Inject constructor(private val execOps: ExecOperations) : DefaultTask() {
    @TaskAction
    fun deploy() {
        execOps.exec { commandLine("git", "add", ".") }
        val hasChanges = execOps.exec {
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
