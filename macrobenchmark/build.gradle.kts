// Macrobenchmark module — item 14 from docs/APP_SUGGESTIONS.md.
//
// Run locally on a connected device or emulator (API 29+):
//   .\gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest
//
// CI only assembles this module (no device available on ubuntu-latest);
// the actual benchmark must be executed on a developer device.
plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.translator.TalknLearn.macrobenchmark"
    compileSdk = 36

    defaultConfig {
        minSdk = 29 // Macrobenchmark requires API 29+
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildTypes {
        // Match :app benchmark build type so this test module installs against it.
        create("benchmark") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
    beforeVariants(selector().all()) {
        // Only enable the `benchmark` build type — keeps debug/release variants out.
        it.enable = it.buildType == "benchmark"
    }
}
