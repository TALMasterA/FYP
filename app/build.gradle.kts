plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")

    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")

    kotlin("kapt")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.fyp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fyp"
        minSdk = 26
        targetSdk = 36
        versionCode = 19
        versionName = "1.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.17"
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

    // Networking
    implementation(libs.okhttp)

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

    // Hilt DI
    implementation(libs.hilt.android)
    implementation(libs.espresso.core)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.preference)

    implementation(libs.kotlinx.serialization.json)
}

