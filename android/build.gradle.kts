import org.gradle.process.ExecOperations
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
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
        classpath(libs.dokka.base)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}

tasks.named<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>("dokkaHtmlMultiModule") {
    moduleName.set("| MTN MOMO ANDROID SDK")
    moduleVersion.set(project.version.toString())
    outputDirectory.set(layout.buildDirectory.dir("dokka"))

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(layout.projectDirectory.file("assets/logo-icon.svg").asFile)
        customStyleSheets = listOf((layout.projectDirectory.file("assets/rekast.css").asFile))
        footerMessage = "&copy; Re.Kast Limited"
        separateInheritedMembers = false
    }

    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": false }"""
        )
    )
}

tasks.register<Copy>("copyDocsToGhPages") {
    dependsOn("dokkaHtml")
    from(layout.buildDirectory.dir("dokka"))
    into(file("docs"))
}

abstract class DeployDocsTask @Inject constructor(private val execOps: ExecOperations) : DefaultTask() {
    @TaskAction
    fun deploy() {
        execOps.exec { commandLine("git", "add", ".") }
        execOps.exec { commandLine("git", "commit", "-m", "Update documentation") }
        execOps.exec { commandLine("git", "push") }
    }
}

tasks.register<DeployDocsTask>("deployDocs") {
    dependsOn("copyDocsToGhPages")
}
