# Google Play Release Checklist

This is a code-grounded checklist of what must be fixed or prepared before the app is made available on Google Play. It is intentionally practical: each item says why it matters, what the current repo shows, and what to do before the first production rollout.

Current verified baseline:

- [app/build.gradle.kts](../app/build.gradle.kts) uses `namespace = "com.example.fyp"` and `applicationId = "com.example.fyp"`.
- [app/build.gradle.kts](../app/build.gradle.kts) sets `versionCode = 55`, `versionName = "2.2.0"`, `compileSdk = 36`, `targetSdk = 36`, and `minSdk = 26`.
- [app/build.gradle.kts](../app/build.gradle.kts) has no `signingConfigs.release` block.
- [app/build.gradle.kts](../app/build.gradle.kts) disables release minification and resource shrinking: `isMinifyEnabled = false`, `isShrinkResources = false`.
- [app/build.gradle.kts](../app/build.gradle.kts) enables ABI APK splits and a universal APK, but Play uploads must use the AAB from `:app:bundleRelease`.
- [app/src/main/AndroidManifest.xml](../app/src/main/AndroidManifest.xml) requests `INTERNET`, `RECORD_AUDIO`, `CAMERA`, and `POST_NOTIFICATIONS`.
- [app/src/main/AndroidManifest.xml](../app/src/main/AndroidManifest.xml) has `android:allowBackup="false"`, `android:usesCleartextTraffic="false"`, and `android:networkSecurityConfig="@xml/network_security_config"`.
- [app/src/main/res/values/strings.xml](../app/src/main/res/values/strings.xml) still labels the launcher as `FYP`.
- Launcher icon resources still appear to be the default Android Studio adaptive icon set.
- [app/src/main/java/com/example/fyp/appstate/FYPApplication.kt](../app/src/main/java/com/example/fyp/appstate/FYPApplication.kt) installs Firebase App Check with the Debug provider in debug builds and Play Integrity in release builds.
- No top-level `LICENSE` file or standalone `PRIVACY_POLICY.md` file currently exists.

Priority legend:

- **BLOCKER:** Play Console rejection, production auth/backend failure, or cannot publish safely.
- **HIGH:** Likely privacy, security, crash, ANR, or pre-launch report issue.
- **MEDIUM:** Store listing, UX, rollout, and operational readiness.
- **LOW:** Polish, governance, and post-launch hygiene.

---

## 1. BLOCKERS

### 1.1 Rename the package before the first Play upload

**Current state:** [app/build.gradle.kts](../app/build.gradle.kts) uses `com.example.fyp` for both `namespace` and `applicationId`.

**Why it matters:** Google Play does not allow package names that start with `com.example`. The package name is permanent after publication, so this has to be fixed before the first internal-track upload.

**Actions:**

1. Choose a final reverse-DNS package name that you control, for example `com.<your-domain>.<product-name>`.
2. Update `namespace`, `applicationId`, and `testInstrumentationRunner` package references in [app/build.gradle.kts](../app/build.gradle.kts).
3. Rename all Kotlin package declarations and imports under `app/src/main`, `app/src/test`, and `app/src/androidTest`.
4. Re-register the Android app in Firebase using the new package name and download a fresh local Firebase Android config JSON.
5. Re-check Firebase Auth, Crashlytics, Analytics, FCM, Cloud Functions, and App Check because all are tied to the package name and signing fingerprints.

### 1.2 Add release signing and enrol in Play App Signing

**Current state:** [app/build.gradle.kts](../app/build.gradle.kts) has no `signingConfigs.release` block.

**Why it matters:** Play requires an upload-signed AAB. Debug or unsigned builds cannot be promoted to production.

**Actions:**

1. Generate a dedicated upload keystore with RSA 4096 or equivalent current Play guidance.
2. Store keystore passwords outside Git, preferably in user-level Gradle properties or CI secrets.
3. Add a `signingConfigs.release` block that reads from environment variables or user-level Gradle properties.
4. Wire `buildTypes.release.signingConfig` to the release config.
5. Enrol the app in Play App Signing and back up the upload key recovery information.
6. Register both the upload key SHA-1/SHA-256 and, after Play provides it, the app-signing key SHA-1/SHA-256 in Firebase.

### 1.3 Upload an AAB, not a universal APK

**Current state:** [app/build.gradle.kts](../app/build.gradle.kts) enables ABI APK splits and `isUniversalApk = true`. That is useful for sideload demos, but Play production uploads must use Android App Bundles.

**Why it matters:** New Play apps must be uploaded as AABs. The APK split configuration does not prevent AAB creation, but the release pipeline must call `:app:bundleRelease`, not `:app:assembleRelease`.

**Actions:**

1. Build the upload artifact with `./gradlew :app:bundleRelease`.
2. Upload `app/build/outputs/bundle/release/app-release.aab` to Play Console.
3. Keep APKs only for local sideload testing or non-Play distribution.

### 1.4 Replace placeholder app identity assets

**Current state:** [app/src/main/res/values/strings.xml](../app/src/main/res/values/strings.xml) uses `<string name="app_name">FYP</string>`, and launcher resources still look like the Android Studio default icon.

**Why it matters:** Placeholder app names and stock launcher artwork make the app look unfinished and can create branding/trademark problems.

**Actions:**

1. Choose the final app name and update `app_name`.
2. Add localized launcher-label resources if the public Play listing will be localized.
3. Replace adaptive icon foreground/background assets with original artwork.
4. Add a monochrome themed icon for Android 13+.
5. Prepare Play Console assets: 512 x 512 app icon, 1024 x 500 feature graphic, and production screenshots.

### 1.5 Publish a Privacy Policy and complete Data Safety

**Current state:** The repo has [docs/PRIVACY_AND_COMPLIANCE.md](PRIVACY_AND_COMPLIANCE.md), but there is no public, hosted Privacy Policy file/URL visible in the repo.

**Why it matters:** The app uses Firebase Auth, Firestore, Crashlytics, Analytics, Performance Monitoring, FCM, Azure Speech, ML Kit OCR, camera, microphone, and user-generated chat/profile data. Play requires a privacy policy and accurate Data Safety declarations for this kind of data processing.

**Actions:**

1. Create a user-facing Privacy Policy covering collected data, purpose, third-party processors, retention, deletion, security, children's-data stance, and contact email.
2. Host it at a stable HTTPS URL.
3. Link it in-app from Settings/About or Settings/Privacy.
4. Paste the hosted URL into Play Console.
5. Complete the Play Data Safety form for Firebase, Microsoft Azure Speech Services, Google Sign-In, ML Kit, Crashlytics, Analytics, FCM, and Performance Monitoring.
6. Keep the Console form aligned with [docs/PRIVACY_AND_COMPLIANCE.md](PRIVACY_AND_COMPLIANCE.md).

### 1.6 Provide the required account-deletion web URL

**Current state:** In-app account deletion exists in [ProfileViewModel.kt](../app/src/main/java/com/example/fyp/screens/settings/ProfileViewModel.kt) and [FirestoreProfileRepository.kt](../app/src/main/java/com/example/fyp/data/user/FirestoreProfileRepository.kt); it re-authenticates, deletes Firestore user data, and calls Firebase Auth account deletion. A public web deletion request URL is not present.

**Why it matters:** Play requires both an in-app deletion path and a web URL users can access after uninstalling.

**Actions:**

1. Add a hosted account deletion request page or form.
2. Link it from the Privacy Policy and Play Console.
3. Document how deletion requests are authenticated, processed, and completed.
4. Re-test in-app deletion for email/password users and decide how Google Sign-In users reauthenticate before deletion.

### 1.7 Configure App Check for release signing fingerprints

**Current state:** [FYPApplication.kt](../app/src/main/java/com/example/fyp/appstate/FYPApplication.kt) uses Play Integrity App Check in release builds.

**Why it matters:** If Firebase does not know the release signing fingerprints, production Firestore/Functions calls can fail with App Check errors. If App Check is not enforced, the app gets less protection than the code suggests.

**Actions:**

1. Register the upload key SHA-256 in Firebase App Check.
2. Register the Play App Signing SHA-256 after Play generates it.
3. Verify debug, internal, and release tracks use the intended App Check provider.
4. After testing, enforce App Check on Firestore, Cloud Functions, and any other Firebase products used by the app.

### 1.8 Verify backend deployment, billing, and rules

**Current state:** The app depends on Firestore and callable Cloud Functions documented in [docs/CLOUD_FUNCTIONS_API.md](CLOUD_FUNCTIONS_API.md).

**Why it matters:** A signed release can still fail in production if Functions are not deployed, billing is not enabled, Firestore rules are still permissive, or App Check enforcement is inconsistent.

**Actions:**

1. Deploy Firestore rules, indexes, and Functions to the intended production Firebase project.
2. Confirm the Firebase project is on the required billing plan for callable Functions and outbound API calls.
3. Confirm every Android callable name matches [docs/CLOUD_FUNCTIONS_API.md](CLOUD_FUNCTIONS_API.md).
4. Run the live Firestore audit tool at [report-audit/tools/audit_firestore_live.py](../report-audit/tools/audit_firestore_live.py) before launch.

---

## 2. HIGH: Security, Privacy, And Crash Risk

### 2.1 Re-enable R8 and resource shrinking for release

**Current state:** release builds in [app/build.gradle.kts](../app/build.gradle.kts) set `isMinifyEnabled = false` and `isShrinkResources = false` for demo safety.

**Why it matters:** Play may accept an unobfuscated AAB, but production should not ship demo build settings. R8 also activates the existing release log-stripping rules in [app/proguard-rules.pro](../app/proguard-rules.pro).

**Actions:**

1. Set release `isMinifyEnabled = true` and `isShrinkResources = true`.
2. Build and install a release variant on a physical device.
3. Smoke-test login, Google Sign-In, Firestore reads/writes, chat, push notifications, Azure speech recognition/TTS, OCR, quiz generation, and settings.
4. Upload mapping files so Crashlytics and Play Console can deobfuscate crashes.
5. Add keep rules only where tests or release smoke tests prove they are needed.

### 2.2 Audit ProGuard keep rules after R8 is enabled

**Current state:** [app/proguard-rules.pro](../app/proguard-rules.pro) keeps Firebase, Google Play Services, Azure Speech, Hilt, Retrofit-style interfaces, serialization annotations, ViewModel classes, native methods, and some log calls.

**Actions:**

1. Verify Firestore POJOs such as `FriendMessage`, `PublicUserProfile`, word-bank models, settings models, and history records still deserialize in a minified release build.
2. Verify `@Serializable` classes still serialize/deserialize correctly.
3. Verify Azure Speech native classes and ML Kit OCR models load correctly.
4. Confirm `firebase-appdistribution` is absent from `releaseRuntimeClasspath` because it should remain debug-only.

### 2.3 Remove or gate direct `android.util.Log` usage

**Current state:** The app has many direct `android.util.Log` calls. The most sensitive examples are in [AzureSpeechRepository.kt](../app/src/main/java/com/example/fyp/data/repositories/AzureSpeechRepository.kt), which logs recognized speech text and synthesized text. [FYPApplication.kt](../app/src/main/java/com/example/fyp/appstate/FYPApplication.kt) and [FirestoreProfileRepository.kt](../app/src/main/java/com/example/fyp/data/user/FirestoreProfileRepository.kt) also log directly.

**Why it matters:** Recognized speech, user IDs, chat IDs, usernames, and backend error messages can be personal data. Existing R8 rules only strip `Log.d`, `Log.v`, and `Log.i`; `Log.w` and `Log.e` remain.

**Actions:**

1. Route logging through [Logger.kt](../app/src/main/java/com/example/fyp/core/foundation/Logger.kt) or a production-safe wrapper.
2. Never log recognized speech text, translated text, chat message content, email addresses, raw UIDs, tokens, or request payloads.
3. Hash or truncate identifiers where correlation is needed.
4. Keep production error logs actionable but redacted.
5. Add a static test or lint rule that fails on new direct `android.util.Log` calls outside the approved logger.

### 2.4 Rotate and protect release secrets

**Current state:** The repo uses Firebase and Azure-backed functionality. Local Firebase config and service-account files are intentionally ignored and must stay out of submission files and tree metadata.

**Actions:**

1. Rotate any Azure Speech key before public release.
2. Store server-side API keys only in Cloud Functions config or a managed secret store, not in the Android APK/AAB.
3. Re-check [docs/SECRETS_ROTATION.md](SECRETS_ROTATION.md) for the final release procedure.
4. Confirm ignored secret/config files are not listed in [treeOfImportantfiles.txt](treeOfImportantfiles.txt) and are not included in submission documents.

### 2.5 Confirm Crashlytics, Analytics, and Performance privacy controls

**Actions:**

1. Confirm no email, username, raw UID, chat text, speech text, or OCR text is attached to Crashlytics logs, custom keys, or user IDs.
2. Add or verify an in-app privacy toggle for Analytics/Crashlytics/Performance collection where required by your target jurisdictions.
3. Confirm the Data Safety form matches the actual SDK collection behavior.
4. Verify Performance traces do not include sensitive query strings or payload-derived labels.

### 2.6 Audit Firestore and Cloud Functions access control

**Actions:**

1. Ensure no production rule has an unconditional `allow read, write: if true` or broad wildcard escape.
2. Confirm every user-scoped path checks `request.auth.uid` against the user being accessed.
3. Confirm friend/chat/shared-item rules enforce relationship and blocking semantics.
4. Confirm callable Functions validate auth, App Check, input shape, ownership, and rate limits server-side.
5. Keep [docs/CLOUD_FUNCTIONS_API.md](CLOUD_FUNCTIONS_API.md) aligned with actual exported callables.

### 2.7 Prepare permission disclosures and runtime flows

**Current state:** [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) requests microphone, camera, notifications, and internet.

**Actions:**

1. Prepare Play Console permission explanations for microphone, camera, and notifications.
2. In-app copy should explain why each permission is requested before the runtime prompt appears.
3. Microphone: explain Azure Speech processing and whether audio is stored.
4. Camera: explain OCR use and whether frames leave the device.
5. Notifications: explain chat, friend request, and learning notification use.

### 2.8 Verify 16 KB native library compatibility

**Current state:** [app/build.gradle.kts](../app/build.gradle.kts) uses `jniLibs.useLegacyPackaging = false` and keeps `.tflite` assets uncompressed. That is useful packaging hygiene, but it does not by itself prove every bundled native library is 16 KB page-size compatible.

**Actions:**

1. Build the release AAB/APK from current dependencies.
2. Inspect native libraries from Azure Speech, ML Kit, Firebase, and CameraX transitive dependencies.
3. Run Android Studio/AGP 16 KB compatibility checks or `zipalign -c -P 16 -v` on generated APK artifacts.
4. Upgrade any SDK that ships incompatible `.so` files before Play's 16 KB requirement applies to your target API level.

---

## 3. HIGH: UGC, Safety, And Policy

### 3.1 Add a dedicated user/content reporting flow

**Current state:** The app has friend chat and profile data. Code search found block/unblock and clear-chat/hide-message flows, but no dedicated `report user`, `report message`, or moderation queue implementation beyond generic feedback wording.

**Why it matters:** Apps with user-generated content need a way to report objectionable content/users, block users, and act on reports.

**Actions:**

1. Add per-user and per-message report actions in chat/profile surfaces.
2. Store reports in a moderation collection or callable Function with reporter ID, reported user/content ID, reason, timestamp, and redacted context.
3. Add an admin review process or documented manual workflow.
4. Add user-facing Terms/Acceptable Use text covering chat behavior.
5. Keep the existing block/unblock and hide/clear conversation flows, but do not treat them as a substitute for reporting.

### 3.2 Content rating and target audience

**Actions:**

1. Complete the IARC content rating questionnaire truthfully, including user interaction/chat.
2. Decide the target age range before release.
3. If targeting children or Families, rework Analytics, consent, data collection, and moderation to satisfy the stricter policy.
4. If not targeting children, make the Play Console target-audience answer and Privacy Policy consistent.

### 3.3 Account abuse and moderation controls

**Actions:**

1. Verify blocked users cannot send friend requests, messages, shared items, or profile interactions.
2. Verify server-side rules/functions enforce blocking, not just the Android UI.
3. Add rate limits for reports, messages, friend requests, and feedback submission.
4. Add a documented process for suspending abusive accounts if reports are validated.

---

## 4. MEDIUM: Store Listing And User Experience

### 4.1 Prepare final Play listing assets

Required or strongly recommended assets:

- Final app name.
- Short description, maximum 80 characters.
- Full description, maximum 4,000 characters.
- 512 x 512 app icon, PNG, no alpha.
- 1024 x 500 feature graphic.
- At least two phone screenshots; use real app screens such as Login, Conversation, Friends, Word Bank, OCR, Quiz, and Settings.
- Tablet screenshots only if tablet support is intentionally marketed.
- Support email and, ideally, a product/support website.

### 4.2 Localize the Play listing

**Current state:** The app has 16 locale translation maps under [app/src/main/java/com/example/fyp/model/ui/strings/translations](../app/src/main/java/com/example/fyp/model/ui/strings/translations), but Play listing text is separate from in-app UI strings.

**Actions:**

1. Localize title, short description, full description, screenshots, and feature graphic for the major target languages.
2. Verify the launcher label and app name are localized through Android resources if needed.
3. Keep claims in localized listings consistent with actual app behavior.

### 4.3 Accessibility and large-font pass

**Actions:**

1. Run TalkBack through login, onboarding, main conversation, mic, OCR, quiz, friends, chat, word bank, profile, and settings.
2. Confirm icon-only buttons have meaningful `contentDescription` values.
3. Confirm all tappable targets are at least 48 dp.
4. Test at 200 percent font scale and with high contrast/dark mode.
5. Fix clipped text, overlapped controls, and unlabeled custom UI.

### 4.4 First-run and account flows

**Actions:**

1. Verify email/password sign-up, Google Sign-In, password reset, sign-out, and account deletion on a release build.
2. Add Terms of Service and Privacy Policy links at sign-up if not already present.
3. Verify notification permission is requested only when it is contextually useful.
4. Verify microphone and camera permission denial paths are usable.

### 4.5 Staged rollout plan

Use release tracks in this order:

1. Internal testing.
2. Closed testing.
3. Open testing, if useful.
4. Production with staged rollout percentage.

Do not skip directly from local testing to 100 percent production.

---

## 5. MEDIUM: Release Quality Gates

Run these before tagging a Play candidate:

1. `./gradlew :app:testDebugUnitTest`
2. `./gradlew :app:assembleDebug`
3. `./gradlew :app:lintRelease`
4. `./gradlew :app:bundleRelease`
5. `./gradlew :app:dependencies --configuration releaseRuntimeClasspath`
6. Backend: `cd fyp-backend/functions && npm test:coverage`
7. Backend: `cd fyp-backend/functions && npm run build`

Additional manual gates:

- Install the release build on at least one physical device.
- Exercise login, speech recognition, TTS, OCR, chat, push notifications, account deletion, and settings.
- Upload to an internal Play track and review the Play pre-launch report.
- Fix all reproducible crashes, ANRs, security warnings, and high-confidence accessibility failures before closed/open testing.
- Keep [docs/TEST_COVERAGE.md](TEST_COVERAGE.md) and [README.md](../README.md) aligned with the actual latest test XML/Jest output after running tests.

---

## 6. LOW: Governance And Post-Launch Readiness

### 6.1 Licences and notices

**Current state:** No top-level `LICENSE` file was found.

**Actions:**

1. Decide whether the project is proprietary or open-source.
2. Add the appropriate top-level licence or EULA.
3. Add an open-source licences screen, commonly generated by the Google Play services OSS licences plugin.
4. Verify dependency notices for Compose, Firebase, OkHttp, Hilt, ML Kit, CameraX, Kotlin, and Azure Speech SDK.

### 6.2 SBOM and dependency monitoring

**Actions:**

1. Generate and archive an SBOM for each release.
2. Enable Dependabot or an equivalent dependency update process.
3. Review security advisories before each Play promotion.

### 6.3 Monitoring and incident response

**Actions:**

1. Configure Crashlytics alerting for crash-free users/sessions drops.
2. Define who receives Play Console and Firebase production alerts.
3. Document rollback, staged rollout pause, and hotfix procedures.
4. Document support email handling and expected response time.

### 6.4 Optional Play features

Consider later, after the first stable release:

- In-app review prompt.
- In-app update prompt.
- Store listing experiments.
- Play Integrity API checks beyond Firebase App Check, if abuse becomes a real issue.

---

## 7. Suggested Execution Order

1. Rename package and Firebase app registration.
2. Add release signing and Play App Signing.
3. Replace app name and launcher/store artwork.
4. Publish Privacy Policy and account-deletion web URL.
5. Complete Data Safety and content rating drafts.
6. Re-enable R8/resource shrinking and fix release-build issues.
7. Remove or gate direct logs and redact production diagnostics.
8. Add UGC report/moderation flow.
9. Configure App Check release fingerprints and enforcement.
10. Deploy/re-audit Firestore rules, indexes, and Cloud Functions.
11. Run release quality gates and physical-device smoke tests.
12. Upload to internal testing and fix the Play pre-launch report.
13. Move through closed/open testing before staged production rollout.

---

## 8. Cross-References

- [ARCHITECTURE_NOTES.md](ARCHITECTURE_NOTES.md)
- [TEST_COVERAGE.md](TEST_COVERAGE.md)
- [CLOUD_FUNCTIONS_API.md](CLOUD_FUNCTIONS_API.md)
- [PRIVACY_AND_COMPLIANCE.md](PRIVACY_AND_COMPLIANCE.md)
- [SECRETS_ROTATION.md](SECRETS_ROTATION.md)
- [APP_SUGGESTIONS.md](APP_SUGGESTIONS.md)
- [treeOfImportantfiles.txt](treeOfImportantfiles.txt)
