import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")

    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")

    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    kotlin("plugin.serialization")
}

// App Check debug token — read from local.properties (gitignored) or CI env var.
val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) localPropsFile.inputStream().use(localProps::load)
val appCheckToken: String = localProps.getProperty("appCheckDebugToken")
    ?: System.getenv("APP_CHECK_DEBUG_TOKEN") ?: ""
fun String.asBuildConfigString(): String = "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.translator.TalknLearn"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.translator.TalknLearn"
        minSdk = 26
        targetSdk = 36
        versionCode = 56
        versionName = "2.3.0"

        testInstrumentationRunner = "com.translator.TalknLearn.HiltTestRunner"
    }

    buildTypes {
        release {
            // Demo-safe release: avoid R8/obfuscation issues during final presentation builds.
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Keep debug APK predictable for live demos and on-device troubleshooting.
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            versionNameSuffix = "-dev"
            // App Check debug token — read from local.properties (gitignored) or CI env var.
            buildConfigField("String", "APP_CHECK_DEBUG_TOKEN", appCheckToken.asBuildConfigString())
        }
        // Macrobenchmark target build type (item 14 from docs/APP_SUGGESTIONS.md).
        // Mirrors `release` but is signed with the debug key so the :macrobenchmark
        // module can install + launch it on a developer device without release keys.
        // `profileable` is injected via `app/src/benchmark/AndroidManifest.xml`.
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            // Reuse the debug App Check token so callable APIs still work during benchmarking.
            buildConfigField("String", "APP_CHECK_DEBUG_TOKEN", appCheckToken.asBuildConfigString())
        }
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
            include("armeabi-v7a", "arm64-v8a", "x86_64")
            // Keep universal APK for easy sideloading in demos across mixed device labs.
            isUniversalApk = true
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
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.functions)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)
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

    // Android Keystore-backed encrypted storage
    implementation(libs.androidx.security.crypto)

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

// ---------------------------------------------------------------------------
// Kover — Android unit-test coverage gate (item 8 from docs/APP_SUGGESTIONS.md).
// Run: .\gradlew.bat :app:koverHtmlReportDebug      (HTML report)
//      .\gradlew.bat :app:koverXmlReportDebug       (XML, used by CI)
//      .\gradlew.bat :app:koverVerifyDebug          (enforces the floor below)
// Reports land under app/build/reports/kover/.
// The initial floor is intentionally low (35% line coverage) to prevent drift
// without blocking day-to-day work; raise it once the suite stabilises.
// ---------------------------------------------------------------------------
kover {
    reports {
        // Filters and verify rules applied to every report variant (debug, release, total).
        filters {
            excludes {
                // Generated code — Hilt, Dagger, KSP, Compose, BuildConfig, R.
                classes(
                    "hilt_aggregated_deps.*",
                    "dagger.hilt.internal.*",
                    "*_HiltModules*",
                    "*_HiltModules\$*",
                    "*_Factory",
                    "*_Factory\$*",
                    "*_MembersInjector",
                    "*_MembersInjector\$*",
                    "Hilt_*",
                    "*ComposableSingletons*",
                    "*\$\$serializer",
                    "*_Impl",
                    "*_Impl\$*",
                    "com.translator.TalknLearn.BuildConfig",
                    "com.translator.TalknLearn.*.R",
                    "com.translator.TalknLearn.*.R\$*"
                )
                packages(
                    // Static localization tables — pure data, exercised only at runtime.
                    "com.translator.TalknLearn.model.ui.strings.translations",
                    // Hilt / DI wiring — covered by integration, not unit tests.
                    "com.translator.TalknLearn.di"
                )
                annotatedBy(
                    "androidx.compose.runtime.Composable",
                    "dagger.Module",
                    "javax.inject.Singleton"
                )
            }
        }
        verify {
            rule("Minimum line coverage") {
                bound {
                    minValue = 35
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                    aggregationForGroup = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Detekt — static analysis + ktlint formatting (item 45 from docs/APP_SUGGESTIONS.md).
// Run: .\gradlew.bat :app:detekt                  (analysis; fails on new issues)
//      .\gradlew.bat :app:detektBaseline          (regenerate baseline after a major refactor)
// Reports land under app/build/reports/detekt/ (HTML + XML).
// The `formatting` ruleset is provided by detekt-formatting (wraps ktlint), so
// this single tool covers both static analysis and code-style checks.
// Existing issues are silenced via app/detekt-baseline.xml; only NEW issues
// fail the build, per repo guards in .github/copilot-instructions.md.
// ---------------------------------------------------------------------------
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    baseline = file("$projectDir/detekt-baseline.xml")
    autoCorrect = false
    parallel = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "11"
    reports {
        html.required.set(true)
        xml.required.set(true)
        sarif.required.set(false)
        md.required.set(false)
        txt.required.set(false)
    }
    // Skip generated sources (Hilt, KSP, BuildConfig, R, navigation args).
    exclude("**/build/**", "**/generated/**", "**/*_HiltModules*", "**/*_Factory*")
}

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "11"
}
