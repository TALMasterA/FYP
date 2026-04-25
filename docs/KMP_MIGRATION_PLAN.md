# KMP Migration Plan — FYP App (Android → Cross-Platform)

> **Scope:** Migrate the Android-only `com.example.fyp` app to Kotlin Multiplatform (KMP), targeting Android + iOS.
> **Goal:** Share ~70% of code (models, domain logic, repositories, Compose UI) across platforms.
> **Date drafted:** April 2026
> **Branch:** `postFYP`

---

## Quick Reference: What the Agent Cannot Do For You

| Step | Stage | Reason |
|---|---|---|
| Get a Mac (physical or cloud) | Before Phase 1 | iOS compilation requires macOS + Xcode |
| Install Xcode (≥ 15) | Before Phase 1 | KMP iOS target build tool |
| Register iOS app in Firebase Console | Before Phase 3 | Generates `GoogleService-Info.plist` |
| Upload APNs key to Firebase Console | Before Phase 4c | Enables push notifications on iOS |
| Create Google Sign-In iOS OAuth client in Firebase/GCP Console | Before Phase 4d | Generates `iOS_CLIENT_ID` |
| Add `GoogleService-Info.plist` to Xcode project | Before Phase 4d | Required by Firebase iOS SDK |
| Add URL scheme for Google Sign-In in `Info.plist` | Before Phase 4d | Required by GoogleSignIn iOS pod |
| Run `pod install` in `iosApp/` | Before Phase 5 | CocoaPods must run on macOS |
| Create an Apple Developer account | Before Phase 5 | Required to run on physical iOS device / App Store |

---

## Tool Versions

| Tool | Version |
|---|---|
| Kotlin Multiplatform plugin | `2.x` (`kotlin("multiplatform")`) |
| Compose Multiplatform | `2.3.21` |
| Koin (DI) | `4.2` |
| GitLive Firebase Kotlin SDK | `2.4.0` (`dev.gitlive:firebase-*`) |
| Kotlin coroutines | `1.9.0` |
| kotlinx.serialization | `1.7.3` |

---

## Windows-Only Development: What's Possible Without a Mac

> **Question:** Can the entire migration be done on a Windows PC + Android phone + iOS phone, with no Mac?
> **Answer:** Partly. You can write 100% of the code on Windows, but **iOS binaries (`.framework`, `.ipa`) cannot be built on Windows** — Kotlin/Native's iOS toolchain is macOS-only, and CocoaPods + `xcodebuild` only run on macOS. Your iPhone is a *test target*, not a build machine.

| Activity | Windows PC alone | Needs macOS somewhere |
|---|---|---|
| Phases 1–3 (extract `:shared`, Hilt→Koin, Firebase→GitLive) | ✅ Yes | ❌ No |
| Phase 4a–4d code authoring (`expect`/`actual` skeletons) | ✅ Yes | ❌ No |
| Compiling `iosMain` source set | ❌ No | ✅ Yes |
| Producing `.framework` for iOS | ❌ No | ✅ Yes |
| Running `pod install` | ❌ No | ✅ Yes |
| Building `.ipa` for iPhone | ❌ No | ✅ Yes |
| Installing app on physical iPhone | Via TestFlight or sideload after CI build | ✅ Yes (first-time signing setup) |
| Running iOS Simulator | ❌ No | ✅ Yes |

**Mac-free workarounds (pick one before Phase 4e):**
1. **GitHub Actions `macos-latest` runners** — push from Windows, CI compiles iOS framework + signs `.ipa`, you install via TestFlight on your iPhone. Free tier (2,000 min/month) usually covers solo dev workflow. Requires Apple Developer account ($99/yr) for TestFlight.
2. **Cloud Mac rental** — MacInCloud (~$30/mo), MacStadium, AWS EC2 Mac. RDP into a Mac when you need to compile/debug iOS.
3. **Codemagic / Bitrise / Appcircle** — KMP-aware managed CI; builds + signs iOS without ever touching a Mac directly.

**Bottom line:** You can do everything on Windows up through Phase 4d. From Phase 4e onward, a Mac (physical, cloud, or CI) is mandatory — there is no workaround. This document's Phases 1–3 plan below is fully Windows-executable.

---

## Executable Plan: Phases 1–3 (Windows, branch `kmp-migration` off `postFYP`)

> **Drafted:** April 25, 2026. **Status:** Awaiting approval. **Test bar:** `:app:assembleDebug` + `:app:testDebugUnitTest` must pass at the end of every phase. Coverage parity, not test-count parity. **Baseline:** 192 suites / 2,457 tests / 0 failures.
>
> Audited surface area informing this plan:
> - **Hilt:** 2 modules (`DaggerModule.kt` 33 `@Provides`, `SettingsModule.kt` 2 `@Provides`); 18 `@HiltViewModel`; 63 `@Inject constructor`; 29 `hiltViewModel<>()` Compose call sites; 1 `@AndroidEntryPoint` (`MainActivity`); 1 `@HiltAndroidTest` (`LoginScreenSmokeTest`); `HiltTestRunner` + manifest override.
> - **Firebase:** 12 Firestore repositories; 26 `.toObject(s)`; 11 `.addSnapshotListener`; 7 `FieldValue.increment`; 6+ `SetOptions.merge`; 2 `Source.SERVER`; 5 callable clients; 7 backend callables (`translateText`, `translateTexts`, `detectLanguage`, `getSpeechToken`, `generateLearningContent`, `awardQuizCoins`, `spendCoins`); 17 Firestore models needing `@Serializable`; 2 `@field:PropertyName` + `@get:PropertyName` pairs (`FriendMessage.isRead`, `PublicUserProfile.isDiscoverable`).
> - **Module structure:** 35/38 `model/` files KMP-shareable (blocker: `OcrResult.kt` uses `android.graphics.Rect`); 44/47 `domain/` files (blocker: `domain/ocr/RecognizeTextFromImageUseCase.kt` uses `android.net.Uri`); 19/19 `model/ui/strings/` shareable.

### Phase 0 — Branch + pre-flight (Windows)

1. Create branch `kmp-migration` off `postFYP`.
2. Confirm AGP 8.13.2 + Kotlin 2.0.21 + KSP 2.0.21-1.0.28 stay (verified KMP-compatible).
3. Confirm `kotlinx-serialization` 1.7.3 already on classpath.
4. Snapshot baseline test counts.

**Verification:** branch exists; baseline recorded.

### Phase 1 — Extract `:shared` KMP module (Windows)

Create Gradle module `:shared` with `kotlin("multiplatform")` and `androidTarget()` only (no `ios()` yet — that comes in Phase 4e). Move max platform-agnostic code into `commonMain`; `:app` depends on `:shared`. Behavior unchanged.

1. **Module scaffolding:** `settings.gradle.kts` adds `:shared`. `gradle/libs.versions.toml` gets `kotlin-multiplatform`, `kotlinx-coroutines-core`, `kotlinx-datetime` aliases. New `shared/build.gradle.kts` declares `kotlin("multiplatform") + kotlin("plugin.serialization") + com.android.library`, with `androidTarget { kotlinOptions.jvmTarget = "11" }`. New `shared/src/main/AndroidManifest.xml`.
2. **Move pure-Kotlin code** to `shared/src/commonMain/kotlin/` keeping packages identical:
   - `model/ui/strings/` — all 19 files (3 core + 16 locale maps). Zero blockers.
   - `model/` — 35/38 files (Quiz, QuizAnswer, CustomWord, TranslationRecord, FavoriteRecord, HistorySession, SpeechResult, ValueTypes, `user/*`, `friends/*`, etc.).
   - `domain/` — 44/47 files (`speech/`, `learning/`, `friends/`, `history/`, `settings/`, `feedback/`).
3. **Stay in `:app`** (KMP-blocking, deferred to Phase 4e via `expect/actual`): `model/OcrResult.kt`, `domain/ocr/RecognizeTextFromImageUseCase.kt`, `data/ocr/MLKitOcrRepository.kt`.
4. **Wire `:app`** → `implementation(project(":shared"))`. Delete moved source folders from `:app`.
5. Note: `@Inject` annotations stay temporarily in moved files (Phase 2 strips them); `javax.inject` jar is multiplatform-safe so this compiles.

**Verification:** `:shared:build` passes; `:app:assembleDebug` passes; `:app:testDebugUnitTest` passes. Update `docs/treeOfImportantfiles.txt`, `docs/TEST_COVERAGE.md`, `README.md`.

### Phase 2 — Hilt → Koin (Windows)

Replace 2 Hilt modules / 18 `@HiltViewModel` / 63 `@Inject constructor` / 29 `hiltViewModel<>()` call sites / 1 `@AndroidEntryPoint` / 1 `@HiltAndroidTest` / `@HiltAndroidApp` with Koin 4.x.

1. Add Koin: `koin-core` to `:shared/commonMain`; `koin-android`, `koin-androidx-compose` to `:app`; `koin-test` to test source.
2. Convert `DaggerModule.kt` (33 `@Provides`) + `SettingsModule.kt` (2 `@Provides`) → single `AppKoinModule.kt` (`single { }` for the 25+2 singletons, `factory { }` for the 8 unscoped). Use `androidContext()` for `Context`-needing bindings.
3. Convert 18 `@HiltViewModel` → plain classes + new `ViewModelKoinModule.kt` with `viewModel { ... }` per VM. Strip `@Inject` / `@Singleton` from all 63 non-VM classes.
4. Replace 29 `hiltViewModel<X>()` Compose call sites with `koinViewModel<X>()`. Mechanical.
5. `MainActivity` — drop `@AndroidEntryPoint`. `FYPApplication` — drop `@HiltAndroidApp`, add `startKoin { androidContext(this@FYPApplication); modules(appKoinModule, viewModelKoinModule) }` after Firebase init.
6. Delete `HiltTestRunner.kt` + manifest test-instrumentation override; rewrite `LoginScreenSmokeTest` without `@HiltAndroidTest`.
7. Remove Hilt: drop plugin from root + app build files; drop 4 Hilt aliases from `libs.versions.toml`.
8. **Add `KoinModuleVerificationTest`** that calls Koin's `checkModules { ... }` graph verifier — Koin fails at runtime not compile time, so this is cheap insurance.

**Verification:** `:app:assembleDebug` passes; `:app:testDebugUnitTest` passes; `grep "@HiltAndroidApp|@AndroidEntryPoint|@HiltViewModel|hiltViewModel<"` returns 0 hits; manual emulator smoke test (Home, Settings, Friends, Quiz). Update `docs/ARCHITECTURE_NOTES.md`, tree.

### Phase 3 — Firebase → GitLive 2.4.0 (Windows)

Replace `com.google.firebase:firebase-{auth,firestore,functions}` with `dev.gitlive:firebase-{auth,firestore,functions}:2.4.0` in repositories that should live in `commonMain`. **Keep on Android SDK in `:app`:** Crashlytics, FCM, App Check Play Integrity, Analytics, Performance — all Android-only by nature or with thin GitLive coverage.

| Product | Current SDK | Target | Where |
|---|---|---|---|
| Auth | `firebase-auth` | `dev.gitlive:firebase-auth:2.4.0` | `:shared/commonMain` |
| Firestore | `firebase-firestore` | `dev.gitlive:firebase-firestore:2.4.0` | `:shared/commonMain` |
| Functions | `firebase-functions` | `dev.gitlive:firebase-functions:2.4.0` | `:shared/commonMain` |
| Crashlytics, FCM, App Check, Analytics, Performance | unchanged | unchanged | `:app` only |
| Remote Config | declared but unused | **remove dep** | n/a |

1. Add 3 GitLive deps to `:shared/commonMain`. Drop `firebase-auth`, `firebase-firestore`, `firebase-functions`, `firebase-config` from `:app`. Keep `firebase-bom`, `firebase-messaging`, `firebase-appcheck-*`, `firebase-crashlytics`, `firebase-analytics`, `firebase-perf`, `play-services-auth` on `:app`.
2. Add `@Serializable` to ~17 Firestore model classes. Replace `@field:PropertyName("isRead")` + `@get:PropertyName("isRead")` with `@SerialName("isRead")` to **preserve wire format exactly** (no Firestore data migration needed). Switch all `com.google.firebase.Timestamp` imports to `dev.gitlive.firebase.Timestamp`.
3. Migrate 12 Firestore repositories to `:shared/commonMain`. Mechanical rewrites:
   - `.toObject(Foo::class.java)` → `.data<Foo>()` (26 call sites)
   - `.toObjects(Foo::class.java)` → `.documents.map { it.data<Foo>() }`
   - `.addSnapshotListener { ... }` → `.snapshots` Flow API (11 listeners)
   - `FieldValue.increment(n)` → GitLive equivalent (7 call sites)
   - `.set(x, SetOptions.merge())` → `.set(x, merge = true)` (6+ call sites)
   - `Source.SERVER` → GitLive `Source.SERVER` (2 call sites)
4. Migrate 5 callable clients (`CloudQuizClient`, `CloudGenAiClient`, `CloudTranslatorClient`, `CloudSpeechTokenClient`, the `DaggerModule`-resident factory). Imports + `Firebase.functions.httpsCallable(name).invoke(...)`. Map `FirebaseFunctionsException.Code.*` to GitLive equivalents. Confirm callable names still match the 7 backend exports.
5. Migrate `FirebaseAuthRepository` + adjust the 6 other auth-touching files. `Firebase.auth`, `EmailAuthProvider.credential(...)`, `GoogleAuthProvider.credential(idToken, null)`. `FirebaseUser` namespace changes.
6. Update `AppKoinModule` `single { }` types to GitLive types.
7. **DO NOT** modify `firestore.rules` or any backend code — wire format unchanged.

**Verification:** `:shared:build` + `:app:assembleDebug` pass; `:app:testDebugUnitTest` passes; `grep "com.google.firebase\\.\\(firestore\\|functions\\|auth\\)" app/src/main shared/src` returns only pre-approved exceptions (FCM, AppCheck, Crashlytics, MainActivity Auth bridge); manual emulator smoke covering register/login, history write, quiz attempt, friend request, chat message, `awardQuizCoins` callable; spot-check Firestore reads against existing prod data for `isRead` / `isDiscoverable` / `Timestamp` fields. Update `docs/ARCHITECTURE_NOTES.md`, `docs/CLOUD_FUNCTIONS_API.md`, tree, `README.md`.

### Excluded from this work (require macOS)

iOS targets, Compose Multiplatform UI, `expect/actual` for `Rect` / `ImageUri` / `SecureStorage` / Remote Config / FCM / AppCheck, Crashlytics/FCM/AppCheck/Analytics/Performance GitLive migration, Apple Developer / iOS Firebase Console setup, backend changes, report DOCX updates. After Phase 3, `commonMain` will be platform-agnostic Kotlin so the iOS target can be added later in one step on a Mac (or via CI).

### Risks + mitigations

1. **Koin runtime DI failures** — mitigated by `KoinModuleVerificationTest` (`checkModules { }`) in Phase 2.
2. **Firestore wire format drift** — mitigated by `@SerialName` preserving exact field names; no schema migration.
3. **`Timestamp` test fakes** — ~5 unit tests fabricate `com.google.firebase.Timestamp`; audit + switch to `dev.gitlive.firebase.Timestamp` in Phase 3.
4. **`HiltTestRunner` removal** — must include manifest `testInstrumentationRunner` override removal, else instrumented tests fail to launch.
5. **Phase 3 ordering** — Firestore (largest) → Functions → Auth (smallest) to isolate compile errors.

---

## Phase 0 — Environment Prerequisites

> **Who:** You manually. The agent cannot install software or provision machines.

### 0.1 — You Must Do Before Anything Else

- [ ] **Get access to a macOS machine** (local Mac, cloud Mac like MacStadium, or GitHub Actions `macos-latest` CI).
  - iOS Kotlin/Native compilation (`iosMain`) **only works on macOS**.
  - You can complete Phases 1–3 on Windows; you need macOS starting Phase 4 (when `iosMain` source sets are compiled).
- [ ] **Install Xcode ≥ 15** on the Mac. Open it once to accept the license and install command-line tools.
  - Run: `xcode-select --install`
- [ ] **Install KMP tooling** on the Mac:
  - Android Studio (Meerkat or newer) with the Kotlin Multiplatform plugin enabled
  - Or IntelliJ IDEA Ultimate with KMP plugin
  - Run the [KMP Doctor](https://kotlinlang.org/docs/multiplatform/quickstart.html#set-up-the-environment): `brew install kdoctor && kdoctor`
- [ ] **Install CocoaPods** on the Mac: `sudo gem install cocoapods`

### 0.2 — Compatibility Warning (Agent Can Remind, Not Fix)

The KMP Gradle plugin is **not compatible with the latest Android Gradle Plugin (AGP)**. Before starting, check the [KMP–AGP compatibility table](https://kotlinlang.org/docs/multiplatform-compatibility-guide.html#version-compatibility) and pin the AGP version in `gradle/libs.versions.toml` if needed.

---

## Phase 1 — Extract `shared` Module (Pure Kotlin, Zero Risk)

> **Can be done entirely by agent on Windows.**
> **Duration estimate:** 1–2 weeks.
> **Risk:** Zero — Android behavior is unchanged.

### What the Agent Will Do

1. Add `kotlin("multiplatform")` plugin to `shared/build.gradle.kts` (new module).
2. Register `shared` in `settings.gradle.kts`.
3. Move `app/src/main/java/com/example/fyp/model/` → `shared/src/commonMain/kotlin/com/example/fyp/model/`.
4. Move `app/src/main/java/com/example/fyp/domain/` → `shared/src/commonMain/kotlin/com/example/fyp/domain/` (after removing `@Inject` annotations).
5. Move `app/src/main/java/com/example/fyp/model/ui/` (UiTextKey, UiTextScreens, translations) → `shared/src/commonMain/`.
6. Update `app/build.gradle.kts` to depend on `project(":shared")`.
7. Run `./gradlew :app:testDebugUnitTest` to confirm nothing regressed.

### You Must Do (Phase 1)

- [ ] **Nothing** — this phase is entirely automatable. Review and merge the PR when ready.

---

## Phase 2 — Replace Hilt with Koin

> **Can be done entirely by agent on Windows.**
> **Duration estimate:** 2–4 weeks (mechanical, large surface area: ~20 ViewModels + ~8 DI modules).
> **Risk:** Medium — DI wiring changes touch every screen. All existing unit tests continue to work.

### What the Agent Will Do

1. Add `io.insert-koin:koin-core` to `shared`, `io.insert-koin:koin-android` to `androidApp`, `io.insert-koin:koin-androidx-compose` for Compose screens.
2. Remove `hilt-android`, `hilt-compiler`, `hilt-navigation-compose` from `app/build.gradle.kts`.
3. Replace `@HiltAndroidApp` on `FYPApplication` with `startKoin { androidContext(...); modules(...) }`.
4. Replace every `@HiltViewModel` + `@Inject constructor(...)` with plain constructor + Koin `viewModel { }` declaration.
5. Replace all `@Module @InstallIn(SingletonComponent::class)` classes with `koin module { }` declarations.
6. Replace `@AndroidEntryPoint` on Activities/Fragments (none in this codebase — already Compose-only).
7. Update tests: replace `HiltAndroidTest` with `KoinTest`.

### You Must Do (Phase 2)

- [ ] **Review the Koin module graph** before merging — ensure every dependency is declared (Koin fails at runtime, not compile time, for missing bindings, unlike Hilt).
- [ ] **Smoke test on a real device** after the agent completes — launch 3–4 screens to catch any missing `get()` calls.

---

## Phase 3 — Firebase: Migrate to GitLive KMP SDK

> **Can be done by agent on Windows (only `commonMain`/`androidMain` code, no iOS compilation yet).**
> **Duration estimate:** 2–4 weeks.

### Firebase Coverage Per Product

| Firebase Product | GitLive Coverage | Migration Target |
|---|---|---|
| Auth | 80% | `commonMain` — `dev.gitlive:firebase-auth` |
| Firestore | 60% | `commonMain` — `dev.gitlive:firebase-firestore` |
| Functions | 80% | `commonMain` — `dev.gitlive:firebase-functions` |
| Crashlytics | 80% | `commonMain` — `dev.gitlive:firebase-crashlytics` |
| Analytics | 80% | `commonMain` — `dev.gitlive:firebase-analytics` |
| Remote Config | 20% ⚠️ | `expect/actual` — native SDK on each platform |
| FCM / Messaging | 1% ❌ | `androidMain` only; iOS uses APNs natively |
| Performance Monitoring | 1% ❌ | `androidMain` only; stub on iOS |
| App Check (Play Integrity) | Not supported ❌ | `expect/actual` — Play Integrity on Android, DeviceCheck on iOS |

### What the Agent Will Do

1. Replace `com.google.firebase:firebase-*` BOM dependencies with `dev.gitlive:firebase-*:2.4.0` in `shared`.
2. Update all repository imports from `com.google.firebase` → `dev.gitlive.firebase`.
3. Add `@Serializable` annotations to Firestore model classes (GitLive uses `kotlinx.serialization`).
4. Move repositories to `shared/src/commonMain` using GitLive SDK.
5. Create `expect/actual` scaffolding for Remote Config, FCM token upload, and App Check.
6. Run `./gradlew :app:testDebugUnitTest` and fix any test compilation failures.

### You Must Do (Phase 3) — BEFORE Agent Starts

- [ ] **Register an iOS app in the Firebase Console** ([console.firebase.google.com](https://console.firebase.google.com)):
  1. Go to Project Settings → Add app → iOS.
  2. Use bundle ID: `com.example.fyp` (match Android, or choose a new iOS bundle ID).
  3. Download the generated `GoogleService-Info.plist`.
  4. Keep this file safe — the agent will place it in `iosApp/` in Phase 5, but you need it now so it exists.
- [ ] **Do NOT commit `GoogleService-Info.plist` to git** — add it to `.gitignore` just like `google-services.json`.

### You Must Do (Phase 3) — AFTER Agent Finishes

- [ ] **Test Auth flows on Android device** — login, register, Google Sign-In, sign out.
- [ ] **Test Firestore reads/writes** — word bank, chat, friend requests — the 60% coverage means some APIs may behave differently.

---

## Phase 4 — `expect/actual` for Platform-Specific Features

> **Phase 4a–4c: Agent can write the code on Windows (no iOS compilation yet).**
> **Phase 4d–4e: Requires macOS to compile `iosMain` source set.**
> **Duration estimate:** 3–6 weeks.

### Phase 4a — `SecureStorage` (Android Keystore ↔ iOS Keychain)

**Agent does:** Write `expect class SecureStorage` in `commonMain`, `actual` impl using `EncryptedSharedPreferences` in `androidMain`, stub `actual` in `iosMain` (compiles but throws `NotImplementedError` until Phase 5).

**You must do:** Nothing for this sub-phase.

---

### Phase 4b — Remote Config (`expect/actual`)

**Agent does:** Define `expect interface RemoteConfigRepository`, implement with native Firebase Remote Config in `androidMain`, provide hardcoded defaults in `iosMain` stub.

**You must do:** Nothing for this sub-phase.

---

### Phase 4c — Push Notifications (FCM ↔ APNs)

**Agent does:** Define `expect class NotificationService`, implement with FCM in `androidMain`, write APNs stub in `iosMain`.

**You must do — BEFORE agent writes `iosMain` APNs code:**
- [ ] **Upload an APNs authentication key** to the Firebase Console:
  1. Apple Developer account → Certificates, Identifiers & Profiles → Keys → Create a new key with "Apple Push Notifications service (APNs)" enabled.
  2. Download the `.p8` file (you can only download it once).
  3. Firebase Console → Project Settings → Cloud Messaging → iOS app → upload the `.p8` key, enter Team ID and Key ID.
- [ ] This is **not needed for Android to work** — only needed before testing iOS push notifications.

---

### Phase 4d — Google Sign-In (iOS)

**Agent does:** Write iOS `actual` implementation skeleton using `GoogleSignIn` framework.

**You must do — BEFORE agent writes iOS Google Sign-In code:**
- [ ] **Create an iOS OAuth 2.0 client ID** in the [Google Cloud Console](https://console.cloud.google.com/apis/credentials):
  1. Go to APIs & Services → Credentials → Create credentials → OAuth client ID.
  2. Application type: iOS. Enter the bundle ID.
  3. Copy the `iOS CLIENT ID` value.
- [ ] **Add URL scheme to `Info.plist`** (in `iosApp/`):
  ```xml
  <key>CFBundleURLTypes</key>
  <array>
    <dict>
      <key>CFBundleURLSchemes</key>
      <array>
        <string>com.googleusercontent.apps.YOUR_IOS_CLIENT_ID</string>
      </array>
    </dict>
  </array>
  ```
  - The reversed client ID string (e.g., `com.googleusercontent.apps.123-abc`) comes from `GoogleService-Info.plist` → `REVERSED_CLIENT_ID`.
- [ ] **Add `GoogleService-Info.plist`** to the Xcode project (drag into Xcode, check "Copy items if needed").

---

### Phase 4e — Camera + ML Kit OCR (CameraX ↔ iOS Vision)

**Agent does:** Define `expect class OcrCapture` in `commonMain`, CameraX + ML Kit `actual` in `androidMain`, iOS Vision framework `actual` in `iosMain`.

**You must do:**
- [ ] **macOS required** from here on — `iosMain` source sets use Kotlin/Native and can only compile on macOS.
- [ ] **Switch development to the Mac** you set up in Phase 0.

---

## Phase 5 — iOS App + Compose Multiplatform UI

> **Entire phase requires macOS + Xcode + CocoaPods.**
> **Duration estimate:** 6–12 weeks (largest phase).

### What the Agent Will Do

1. Create `iosApp/` Xcode project (KMP template via IntelliJ on Mac or `kotlinc` scaffold).
2. Add `Podfile` to `iosApp/`:
   ```ruby
   target 'iosApp' do
     use_frameworks!
     pod 'Firebase/Auth'
     pod 'Firebase/Firestore'
     pod 'Firebase/Functions'
     pod 'Firebase/Crashlytics'
     pod 'Firebase/Analytics'
     pod 'GoogleSignIn'
   end
   ```
3. Move Compose UI screens from `androidApp/` to `shared/src/commonMain` using Compose Multiplatform.
4. Replace `androidx.navigation.compose` with a KMP-compatible navigation library (e.g., `decompose` or `voyager`).
5. Replace `accompanist-permissions` with `expect/actual` permission handling.
6. Implement `actual SecureStorage` for iOS using Security framework (`kSecClassGenericPassword`).
7. Implement `actual SpeakTextUseCase` for iOS using `AVSpeechSynthesizer`.
8. Implement `actual OcrCapture` for iOS using `AVCaptureSession` + `VNRecognizeTextRequest`.

### You Must Do (Phase 5) — BEFORE Agent Starts

- [ ] **Run `pod install`** in `iosApp/` after the agent creates `Podfile`: `cd iosApp && pod install`
- [ ] **Open `iosApp.xcworkspace`** (not `.xcodeproj`) in Xcode after `pod install`.
- [ ] **Set the development team** in Xcode → project target → Signing & Capabilities → Team (requires Apple Developer account).
- [ ] **Add `GoogleService-Info.plist`** to the Xcode target (if not already done in Phase 4d).

### You Must Do (Phase 5) — DURING

- [ ] **Compile and run on iOS Simulator** after each sub-step — the agent cannot run iOS builds.
- [ ] **Report Xcode/linker errors** back to the agent — CocoaPods framework linking issues are common and require human interaction with Xcode.
- [ ] **Test on a physical iPhone** at least once before considering a phase complete (simulator does not test APNs or camera).

### You Must Do (Phase 5) — Compose Multiplatform UI Notes

- [ ] Some Material3 components behave slightly differently on iOS. Visually verify each screen on an iPhone.
- [ ] `TextField` IME (keyboard) behavior differs. Test the chat screen and translation input screen on iOS.
- [ ] Camera permission dialog is iOS-native — verify `NSCameraUsageDescription` is in `Info.plist`.

---

## What Stays Android-Only Permanently

Even after full KMP migration, these remain in `androidApp/`:

| Feature | Reason |
|---|---|
| Firebase App Check (Play Integrity) | Play Integrity is Android-only; iOS uses DeviceCheck |
| Firebase Performance Monitoring | 1% GitLive coverage; Android-only |
| Firebase App Distribution | Developer tooling — not a runtime feature |
| LeakCanary | Android debug tooling — not needed on iOS |
| ProGuard / R8 | Android build system only |
| ABI splits (`arm64-v8a`, `x86`, `x86_64`) | Android packaging |

---

## Overall Migration Checklist

```
[ ] Phase 0:  Mac + Xcode + KDoctored environment ready
[ ] Phase 0:  CocoaPods installed
[ ] Phase 1:  shared/ module created, model/ and domain/ extracted (agent)
[ ] Phase 1:  Android tests still pass after extraction
[ ] Phase 2:  Hilt replaced with Koin across all ViewModels + DI modules (agent)
[ ] Phase 2:  Smoke tested on device
[ ] Phase 3:  iOS app registered in Firebase Console (YOU)
[ ] Phase 3:  GoogleService-Info.plist downloaded and gitignored (YOU)
[ ] Phase 3:  Firebase repos migrated to GitLive SDK (agent)
[ ] Phase 3:  Android Auth + Firestore tested on device
[ ] Phase 4a: SecureStorage expect/actual written (agent)
[ ] Phase 4b: Remote Config expect/actual written (agent)
[ ] Phase 4c: APNs key uploaded to Firebase Console (YOU)
[ ] Phase 4c: NotificationService expect/actual written (agent)
[ ] Phase 4d: iOS OAuth client ID created in GCP Console (YOU)
[ ] Phase 4d: URL scheme + GoogleService-Info.plist added to Xcode (YOU)
[ ] Phase 4d: Google Sign-In iOS actual written (agent)
[ ] Phase 4e: Switch dev machine to Mac (YOU)
[ ] Phase 5:  Podfile created and pod install run (YOU + agent)
[ ] Phase 5:  Compose UI migrated to commonMain (agent)
[ ] Phase 5:  Navigation library swapped to KMP-compatible one (agent)
[ ] Phase 5:  expect/actual impls completed for iOS (agent)
[ ] Phase 5:  iOS builds tested on Simulator (YOU)
[ ] Phase 5:  iOS builds tested on physical iPhone (YOU)
```

---

## Phased Effort Summary

| Phase | Who Does Most Work | Requires Mac | Risk Level | Estimated Effort |
|---|---|---|---|---|
| 0 — Environment | You | Yes (setup) | Low | 1–3 days |
| 1 — Extract shared module | Agent | No | Zero | 1–2 weeks |
| 2 — Hilt → Koin | Agent | No | Medium | 2–4 weeks |
| 3 — Firebase GitLive SDK | Agent + You (Firebase Console) | No | Medium | 2–4 weeks |
| 4 — expect/actual impls | Agent + You (Apple/Google consoles) | Phase 4e only | Medium–High | 3–6 weeks |
| 5 — iOS UI + full build | Agent + You (Xcode) | Yes | High | 6–12 weeks |

**Total: 4–6 months** for a production-quality iOS app sharing ~70% of the codebase.

---

*Last updated: April 2026*
