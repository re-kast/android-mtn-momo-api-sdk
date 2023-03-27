import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.dagger)
    id(BuildPlugins.kapt)
    id(BuildPlugins.jacocoAndroid)
    id(BuildPlugins.mapsSecret)
    id(BuildPlugins.vanniktechMavenPublish)
    id(BuildPlugins.vanniktechMavenPublishBase)
    id(BuildPlugins.signing)
    id(BuildPlugins.kotlinSerialization) version Versions.kotlinAndroid
}

jacoco {
    toolVersion = Versions.jacoco
}

android {
    compileSdk = AndroidSdk.compileSdkVersion

    android.buildFeatures.dataBinding = true
    android.buildFeatures.viewBinding = true

    secrets {
        ignoreList.add("sdk.*")
    }

    defaultConfig {
        minSdk = AndroidSdk.minSdkVersion
        targetSdk = AndroidSdk.targetSdkVersion
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildTypes {
        getByName("debug") {
        }
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    namespace = "io.rekast.sdk"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Libraries.coreKtx)
    implementation(Libraries.kotlinxSerializationJson)

    // Network - Retrofit, OKHTTP, chucker
    implementation(Libraries.retrofit)
    implementation(Libraries.gson)
    implementation(Libraries.ohttp)

    implementation(Libraries.loggingInterceptor)
    implementation(Libraries.androidXTestMonitor)
    implementation(Libraries.androidXJunitTest)
    implementation(Libraries.commonsLang3)
    implementation(Libraries.navigationFragment)
    implementation(Libraries.navigationUi)
    implementation(Libraries.navigationCompose)

    debugImplementation(Libraries.chunkerDebug)
    releaseImplementation(Libraries.chunkerRelease)

    // debug
    implementation(Libraries.timber)
    testImplementation(TestLibraries.jUnit)
}

mavenPublishing {
    // publish to https://s01.oss.sonatype.org
    publishToMavenCentral(SonatypeHost.S01, true)

    signAllPublications()
}
