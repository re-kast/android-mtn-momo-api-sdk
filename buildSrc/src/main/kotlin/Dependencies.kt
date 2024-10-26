object Versions {
    //publishing
    const val vanniktechMavenPublish = "0.25.1"

    //Android
    const val application = "8.7.1"
    const val library = "8.7.1"
    const val appCompat ="1.6.1"

    //Compose Bom
    const val composeBom = "2023.01.00"
    const val composeActivity = "1.6.1"
    const val lifecycleComposeViewModel = "2.5.1"
    const val composeMaterial ="1.3.1"
    const val composeMaterial3 ="1.0.1"
    const val composeUi ="1.3.3"
    const val foundation ="1.3.1"
    const val flowLayout ="0.30.0"

    // Material and androidX
    const val material = "1.3.0-alpha02"
    const val materialIcon = "1.3.1"
    const val constraintLayout = "2.0.1"
    const val navigation = "2.6.0-alpha06"
    const val rxJava = "1.3.3"

    // Firebase
    const val firebase = "26.2.0"

    //Networking
    const val retrofit = "2.9.0"
    const val okhttp = "5.0.0-alpha.11"
    const val loggingInterceptor = "5.0.0-alpha.11"

    // Lifecycle
    const val lifecycle = "2.2.0-rc02"
    const val lifecycleViewModel = "2.2.0-rc02"

    // Logging - debug builds
    const val timber = "5.0.1"
    const val leakCanary = "2.10"
    const val chucker = "3.5.2"

    // Kotlin
    const val kotlinVersion = "1.5.21"
    const val coreKtx = "1.10.0-alpha02"
    const val kotlinAndroid = "1.8.10"
    const val kotlinxSerializationJson = "1.5.0"
    const val customView = "1.2.0-alpha02"
    const val poolingContainer = "1.0.0"

    //Apache Commons
    const val commonsLang3 = "3.12.0"

    //Hilt
    const val hilt = "2.45"

    // Gradle Plugins
    const val klint = "0.48.2"
    const val spotless = "6.17.0"
    const val jacoco = "0.8.5"
    const val hiyaJacoco = "0.2"
    const val dokka = "1.5.0"
    const val gradleVersionsPlugin = "0.29.0"
    const val secrets = "2.0.1"

    // tests
    const val junit = "4.13"
    const val espresso = "3.3.0"
    const val roboelectric = "4.4-beta-1"
    const val androidXJUnit = "1.1.1"
    const val truth = "1.0.1"
    const val mockWebServer = "4.8.1"
    const val androidXTestCore = "1.3.0"
    const val runner = "1.3.0"
    const val rules = "1.3.0"
    const val archComponentTest = "2.1.0"
    const val kakao = "2.3.4"
    const val mockK = "1.10.0"
    const val liveDataTesting = "1.1.2"
    const val kotlinxCoroutines = "1.2.1"
    const val androidXTestMonitor = "1.6.1"
    const val androidXJunitTest = "1.1.5"
}

object BuildPlugins {
    const val androidLibrary = "com.android.library"
    const val kapt = "kotlin-kapt"
    const val dagger = "kotlin-android"
    const val detektPlugin = "io.gitlab.arturbosch.detekt"
    const val dokkaPlugin = "org.jetbrains.dokka"
    const val spotlessPlugin = "com.diffplug.spotless"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val kotlinAndroidExtensions = "org.jetbrains.kotlin.android.extensions"
    const val kotlinSerialization = "org.jetbrains.kotlin.plugin.serialization"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin"
    const val gradleVersionsPlugin = "com.github.ben-manes.versions"
    const val jacocoAndroid = "com.hiya.jacoco-android"
    const val mvnPublishPlugin = "com.vanniktech.maven.publish"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val testJunit4 = "androidx.compose.ui:ui-test-junit4"
    const val testManifest = "androidx.compose.ui:ui-test-manifest"
    const val mapsSecret = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin"
    const val hiltAndroid = "com.google.dagger.hilt.android"
    const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin"
    const val nexusPublishPlugin = "io.github.gradle-nexus.publish-plugin"
    const val vanniktechMavenPublish = "com.vanniktech.maven.publish"
    const val vanniktechMavenPublishBase = "com.vanniktech.maven.publish.base"
    const val hiyaJacocoPlugin = "com.hiya:jacoco-android"
    const val navigationGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin"

    //Publishing
    const val mavenPublish = "maven-publish"
    const val signing = "signing"
}

object Libraries {
    // androidX and Material
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigation}"
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val dynamicNavigation = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigation}"
    const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
    const val lifecycleComposeViewModel =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycleComposeViewModel}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"

    //Compose
    const val composeUi = "androidx.compose.ui:ui:${Versions.composeUi}"
    const val composeToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.composeUi}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.composeUi}"
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.composeMaterial}"
    const val materialIconsCore = "androidx.compose.material:material-icons-core:${Versions.materialIcon}"
    const val materialIconsExtended = "androidx.compose.material:material-icons-extended:${Versions.materialIcon}"
    const val composeMaterial3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"
    const val composeMaterial3Window = "androidx.compose.material3:material3-window-size-class:${Versions.composeMaterial3}"
    const val foundation = "androidx.compose.foundation:foundation:${Versions.foundation}"
    const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:${Versions.rxJava}"
    const val runtimeRxJava = "androidx.compose.runtime:runtime-rxjava2:${Versions.rxJava}"
    const val flowLayout = "com.google.accompanist:accompanist-flowlayout:${Versions.flowLayout}"
    const val customView = "androidx.customview:customview:${Versions.customView}"
    const val poolingContainer = "androidx.customview:customview-poolingcontainer:${Versions.poolingContainer}"

    // Firebase
    const val bom = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx:${Versions.firebase}"

    // Networking - Retrofit, OKHTTP and loggingInterceptor
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val ohttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val loggingInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"

    // Lifecycle
    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleViewModel}"

    // Debug - for debug builds only
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val chunkerDebug = "com.github.chuckerteam.chucker:library:${Versions.chucker}"
    const val chunkerRelease = "com.github.chuckerteam.chucker:library-no-op:${Versions.chucker}"

    // Kotlin
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationJson}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val androidXTestMonitor = "androidx.test:monitor:${Versions.androidXTestMonitor}"
    const val androidXJunitTest = "androidx.test.ext:junit-ktx:${Versions.androidXJunitTest}"
    const val commonsLang3 = "org.apache.commons:commons-lang3:${Versions.commonsLang3}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltComplier = "com.google.dagger:hilt-compiler:${Versions.hilt}"
}

object TestLibraries {
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val jUnit = "junit:junit:${Versions.junit}"
    const val androidXJUnit = "androidx.test.ext:junit:${Versions.androidXJUnit}"
    const val roboelectric = "org.robolectric:robolectric:${Versions.roboelectric}"
    const val truth = "com.google.truth:truth:${Versions.truth}"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
    const val androidXTestCore = "androidx.test:core:${Versions.androidXTestCore}"
    const val runner = "androidx.test:runner:${Versions.runner}"
    const val rules = "androidx.test:rules:${Versions.rules}"
    const val archComponentTest = "androidx.arch.core:core-testing:${Versions.archComponentTest}"
    const val kakao = "com.agoda.kakao:kakao:${Versions.kakao}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"
    const val androidMockK = "io.mockk:mockk-android:${Versions.mockK}"
    const val liveDataTesting = "com.jraska.livedata:testing-ktx:${Versions.liveDataTesting}"
    const val kotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinxCoroutines}"
    const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${Versions.hilt}"
    const val navigationTesting = "androidx.navigation:navigation-testing:${Versions.navigation}"
}

object BuildModules {
    const val momoModule = ":momo-api-sdk"
}

object AndroidSdk {
    const val minSdkVersion = 24
    const val compileSdkVersion = 33
    const val targetSdkVersion = compileSdkVersion
    const val versionCode = 1
    const val versionName = "0.0.1"
}
