plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.jacocoAndroid)
    id(BuildPlugins.kapt)
    id(BuildPlugins.mapsSecret)
    id(BuildPlugins.kotlinSerialization) version Versions.kotlinAndroid
    id(BuildPlugins.hiltAndroid)
}

jacoco {
    toolVersion = Versions.jacoco
}

android {
    namespace = "com.rekast.momoapi.sample"
    compileSdk = AndroidSdk.compileSdkVersion
    android.buildFeatures.dataBinding = true
    android.buildFeatures.viewBinding = true

    secrets {
        ignoreList.add("sdk.*")
    }

    defaultConfig {
        applicationId = "com.rekast.momoapi.sample"
        minSdk = AndroidSdk.minSdkVersion
        targetSdk = AndroidSdk.targetSdkVersion
        versionCode = AndroidSdk.versionCode
        versionName = AndroidSdk.versionName
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "com.rekast.momoapi.sample.runner.MockTestRunner"
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
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.4.2" }

/*    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystore/debug.keystore")
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storePassword = "android"
        }
    }*/

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            versionNameSuffix = " - debug"
            applicationIdSuffix = ".debug"
            // signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
        }
    }
}

kapt {
    generateStubs = true
    correctErrorTypes = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(BuildModules.momoModule))
    implementation(Libraries.coreKtx)
    implementation(Libraries.lifecycleComposeViewModel)
    // Compose
    val composeBom = platform(Libraries.composeBom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(BuildPlugins.meterial3)
    implementation(Libraries.composeUi)
    implementation(Libraries.composeToolingPreview)
    implementation(Libraries.composeUiTooling)
    implementation(Libraries.composeActivity)
    implementation(Libraries.lifecycleComposeViewModel)
    implementation(BuildPlugins.materialIconsCore)
    implementation(BuildPlugins.materialIconsExtended)
    implementation(BuildPlugins.materialWindow)
    implementation(BuildPlugins.runtimeLiveData)
    implementation(BuildPlugins.runtimeRxJava)
    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.androidXTestMonitor)
    implementation(Libraries.androidXJunitTest)
    implementation(Libraries.kotlinxSerializationJson)
    implementation(Libraries.composeMaterial)
    implementation(Libraries.composeMaterialWindow)

    implementation("androidx.compose.material:material-icons-extended:1.3.1")
    implementation("androidx.compose.material:material-icons-core:1.3.1")
    implementation("androidx.compose.foundation:foundation:1.3.1")
    implementation("androidx.compose.material:material:1.3.1")
    // hilt
    implementation(Libraries.hiltAndroid)
    kapt(Libraries.hiltComplier)
    // Material and AndroidX
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)
    implementation(Libraries.navigationFragment)
    implementation(Libraries.navigationUi)
    implementation(Libraries.navigationCompose)
    // Network - Retrofit, OKHTTP, chucker
    implementation(Libraries.retrofit)
    implementation(Libraries.gson)
    implementation(Libraries.ohttp)
    implementation(Libraries.loggingInterceptor)
    // Lifecycle
    implementation(Libraries.lifecycle)
    // Debug - for debug builds only
    implementation(Libraries.timber)
    implementation(Libraries.commonsLang3)

    debugImplementation("androidx.customview:customview:1.2.0-alpha02")
    debugImplementation("androidx.customview:customview-poolingcontainer:1.0.0")

    debugImplementation(Libraries.leakCanary)
    debugImplementation(BuildPlugins.composeUiTooling)
    debugImplementation(BuildPlugins.testManifest)
    // hilt
    androidTestImplementation(TestLibraries.hiltAndroidTesting)
    kaptAndroidTest(Libraries.hiltComplier)
    // UI Tests
    androidTestImplementation(TestLibraries.espresso)
    androidTestImplementation(TestLibraries.kakao)
    androidTestImplementation(BuildPlugins.testJunit4)
    // Instrumentation Tests
    androidTestImplementation(TestLibraries.runner)
    androidTestImplementation(TestLibraries.rules)
    androidTestImplementation(TestLibraries.androidXJUnit)
    androidTestImplementation(TestLibraries.androidXTestCore)
    androidTestImplementation(TestLibraries.androidMockK)
    // Unit Tests
    testImplementation(TestLibraries.jUnit)
    testImplementation(TestLibraries.mockWebServer)
    testImplementation(TestLibraries.mockK)
    testImplementation(TestLibraries.roboelectric)
    testImplementation(TestLibraries.truth)
    testImplementation(TestLibraries.runner)
    testImplementation(TestLibraries.androidXJUnit)
    testImplementation(TestLibraries.archComponentTest)
    // hilt
    testImplementation(TestLibraries.hiltAndroidTesting)
    kaptAndroidTest(Libraries.hiltComplier)
    // testImplementation(TestLibraries.liveDataTesting)
    testImplementation(TestLibraries.kotlinxCoroutines)
}
