plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")

    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")

    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.fyp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fyp"
        minSdk = 26
        targetSdk = 36
        versionCode = 48
        versionName = "2.0.0"

        testInstrumentationRunner = "com.example.fyp.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true // Remove unused resources to reduce APK size
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false // Skip stripping native libs
            versionNameSuffix = "-dev"
        } //build faster but APK bigger
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // Enable 16KB page size support for Android 15+ compatibility
    // This aligns native libraries to 16KB boundaries
    androidResources {
        noCompress += "tflite"
    }

    // ABI splits to reduce APK size per architecture
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a")
            // Generate ABI-specific APKs only; avoids shipping duplicate native libs in one universal APK.
            isUniversalApk = false
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core UI
    implementation(libs.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    // Security - Encrypted SharedPreferences
    implementation(libs.androidx.security.crypto)

    // Networking
    implementation(libs.okhttp)

    // Image caching with Coil
    implementation(libs.coil.compose)

    // Azure/Speech
    implementation(libs.azure.speech.sdk)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Firebase - BOM + explicit KTX
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.functions)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    debugImplementation(libs.firebase.appdistribution)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.testing)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // Performance Monitoring & Debugging (Debug only)
    debugImplementation(libs.leakcanary.android)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.kotlinx.serialization.json)

    // ML Kit Text Recognition
    implementation(libs.mlkit.text.recognition)
    implementation(libs.mlkit.text.recognition.chinese)
    implementation(libs.mlkit.text.recognition.japanese)
    implementation(libs.mlkit.text.recognition.korean)

    // CameraX for image capture
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}