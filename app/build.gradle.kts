plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.fyp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fyp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "AZURE_SPEECH_KEY", "\"\"")
        buildConfigField("String", "AZURE_SPEECH_REGION", "\"\"")
        buildConfigField("String", "AZURE_TRANSLATOR_KEY", "\"\"")
        buildConfigField("String", "AZURE_TRANSLATOR_REGION", "\"\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.ui) // your alias pointing to "androidx.compose.ui:ui"
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // Use only one Azure Speech SDK version
    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.45.0")

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

