plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.jacocoAndroid)
    id(BuildPlugins.kapt)
    id(BuildPlugins.mapsSecret)
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

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
    implementation(BuildPlugins.composeUi)
    implementation(BuildPlugins.composeToolingPreview)
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
    // Material and AndroidX
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)
    implementation(Libraries.navigationFragment)
    implementation(Libraries.navigationUi)
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
    debugImplementation(Libraries.leakCanary)
    debugImplementation(BuildPlugins.composeUiTooling)
    debugImplementation(BuildPlugins.testManifest)
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
    // testImplementation(TestLibraries.liveDataTesting)
    testImplementation(TestLibraries.kotlinxCoroutines)
}
