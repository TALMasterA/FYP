# App Suggestions — Deep, Specific, Actionable (Post-FYP Branch)

Audit-style recommendations for the FYP language-learning app (Android Kotlin/Compose + Firebase Cloud Functions TypeScript).
Findings are anchored to concrete files and observed implementation patterns, not generic advice. Every suggestion is independently actionable.

> Scope of evidence: code reads of `app/src/main/java/com/example/fyp/core/security/**`, `core/performance/**`, `screens/friends/ChatViewModel.kt`, `screens/settings/ProfileScreen.kt`, `data/cloud/**`, `data/network/CacheInterceptor.kt`, `data/di/DaggerModule.kt`, `FYPApplication.kt`, `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/xml/{backup_rules,data_extraction_rules}.xml`, `gradle/libs.versions.toml`, `.github/workflows/{ci,codeql}.yml`, `fyp-backend/firestore.rules`, `fyp-backend/functions/src/{index,helpers,notifications,learning,maintenance,translation}.ts`, `docs/ARCHITECTURE_NOTES.md`, `docs/TEST_COVERAGE.md`, `README.md`.

---

## 1. Executive Summary

The codebase is mature for an FYP: 188 Android unit-test suites / 2,443 Android tests; well-documented invariants (`docs/ARCHITECTURE_NOTES.md`); strong coverage around friends, notifications, coins, history, learning, and UI text; Hilt DI; encrypted preferences via `EncryptedSharedPreferences`; secrets vended through `defineSecret(...)`. The biggest concrete post-FYP gaps are:

1. **Auth strength**: `validatePassword` enforces only 6-char minimum; no Firebase App Check; no MFA / email-verification gate.
2. **Branch safety**: `.github/workflows/ci.yml` only runs on `main` pushes and PRs to `main`; direct work on `postFYP` will not get automatic CI unless pushed through a PR or the workflow branch filter is widened.
3. **Release hardening**: `isMinifyEnabled = false` in `release` (no R8 / no obfuscation / no shrink) keeps demo builds predictable but should be separated from a real production release variant.
4. **Defence-in-depth gaps in Firestore rules**: the wildcard `match /users/{userId}/{subCol}/{docId}` works as a deny-list. Newly-added user subcollections silently inherit owner read/write unless explicitly excluded.
5. **In-memory `RateLimiter`** for login/reset/chat/feedback throttles is lost across process death; only the friend-request limiter is persisted.
6. **One instrumented Compose UI test** (`LoginScreenSmokeTest`); backend Jest coverage threshold is still permissive (50/45/50/50).
7. **`testOptions.unitTests.isReturnDefaultValues = true`** masks Android-framework misuse in JVM tests.
8. **Network and backup XMLs are placeholders**: cleartext is not explicitly disabled, and `backup_rules.xml` / `data_extraction_rules.xml` still contain sample TODO content.
9. **No App Check** integration on Cloud Functions / Firestore — abuse vector for unattested clients.

The rest of this document expands each, plus performance, observability, privacy, and feature ideas.

---

## 2. Security Hardening (Specific)

### 2.1 Strengthen client password policy
- `app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt` `validatePassword` only checks `trimmed.length < minLength` with default `6`. No complexity, no breach check.
- **Action**: require ≥8 chars and at least 3 of {upper, lower, digit, symbol}; reject the 100 most common passwords (ship a small static set in `assets/`); enforce on registration **and** password-change. Pair with the **Firebase Auth password policy** in the Firebase Console (set `MINIMUM_LENGTH=8`, `REQUIRE_UPPERCASE`, `REQUIRE_LOWERCASE`, `REQUIRE_NUMERIC`, `REQUIRE_NON_ALPHANUMERIC`).
- **Test gate**: add unit tests that `validatePassword("Abcdef1!")` is `Valid` and `validatePassword("aaaaaa")`, `validatePassword("Password1")` are `Invalid`.

### 2.1.1 Unify username regex rules and documentation
- [`SecurityUtils.kt`](../app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt#L28-L49) says usernames allow hyphens, but the actual regex allows only letters, numbers, and underscores. [`ARCHITECTURE_NOTES.md`](ARCHITECTURE_NOTES.md#L529-L533) says the canonical regex includes hyphens, while Firestore rules for public/search profiles currently reject hyphens.
- **Action**: choose one product rule. If hyphens are allowed, update `validateUsername`, `firestore.rules`, search-index rules, tests, and user-facing copy together. If hyphens are not allowed, fix the comments/docs so future changes do not accidentally drift.

### 2.2 Persist auth-path rate limiting
- The static `messageRateLimiter` companion in [`ChatViewModel.kt`](../app/src/main/java/com/example/fyp/screens/friends/ChatViewModel.kt#L83) and other `RateLimiter` instances live only in process memory. On process death (Doze, low-memory kill, swipe-from-recents) the counter resets — weakening the abuse defence.
- **Action**: introduce `PersistentRateLimiter` backed by `SecureStorage` (already present). Mirror the pattern used by `FriendRequestRateLimiter` per `ARCHITECTURE_NOTES.md`. Apply to: login attempts, password-reset requests, chat send, friend-request send.
- Schema in `secure_prefs`: `ratelimit:<key>` → CSV of millis (cap to `maxAttempts`).

### 2.3 Add Firebase App Check
- No `FirebaseAppCheck.getInstance().installAppCheckProviderFactory(...)` call exists. Without App Check, a leaked Firebase config + Cloud Functions endpoint is callable by any client.
- **Action**: enable App Check with **Play Integrity** in [`FYPApplication.kt`](../app/src/main/java/com/example/fyp/appstate/FYPApplication.kt) for release and the **Debug Provider** for debug builds. Redeploy Cloud Functions in the same change with `enforceAppCheck: true` on each `onCall(...)` definition in `translation.ts`, `learning.ts`, and `coins.ts`.
- **Replay protection**: use `consumeAppCheckToken: true` only on high-value operations such as `spendCoins*`, `awardQuizCoins`, and future data export functions. It adds latency and token refresh pressure, so do not enable it blindly on every translation call.
- **Rollout guard**: add a debug-token setup section to `README.md` before enforcing App Check, otherwise local development and CI will fail immediately.

### 2.4 Replace `SecureStorage` static singleton anti-pattern
- [`SecureStorage.kt`](../app/src/main/java/com/example/fyp/core/security/SecureStorage.kt#L58-L83) writes `staticInstance = this` from `init { ... }` (`@Suppress("LeakingThis")`) and exposes `forContext(...)` to bypass Hilt. Current callers include `FcmNotificationService` and `SeenItemsStorage`.
- **Action**: replace `forContext(...)` callers with a Hilt `@EntryPoint`:
  ```kotlin
  @EntryPoint @InstallIn(SingletonComponent::class)
  interface SecureStorageEntryPoint { fun secureStorage(): SecureStorage }
  ```
  and obtain via `EntryPoints.get(appContext, SecureStorageEntryPoint::class.java).secureStorage()`. Remove the `staticInstance` field and `forContext()` once all callers migrate.

### 2.5 Convert Firestore rules subcollection wildcard to allow-list
- [`firestore.rules`](../fyp-backend/firestore.rules#L112-L126) has `match /users/{userId}/{subCol}/{docId}` with a deny-list of protected subcollections. New user subcollections silently inherit owner read/write until someone remembers to add them to the deny-list. Firestore indexes do **not** change this rule behavior.
- **Action**: replace the wildcard with explicit per-collection matches (`history`, `learning_sheets`, `word_bank`, `custom_words`, `favorites`, `fcm_tokens`, etc.). Add a final `match /users/{userId}/{subCol=**} { allow read, write: if false }` catch-all. Update rules tests to assert that `users/{me}/test_unknown_collection/x` is denied.
- **Why it matters**: this prevents a future server-owned collection (for example `subscription_state`, `moderation_flags`, `app_check_debug_tokens`) from becoming client-writable by accident.

### 2.6 Enforce email verification before sensitive operations
- `auth.currentUser.isEmailVerified` is never checked in flows like friend-add, chat send, feedback submit.
- **Action**: add `firestore.rules` clause `&& request.auth.token.email_verified == true` to: `friend_requests` create, `shared_inbox` create, `feedback` create. Surface a "Verify your email" banner in `SettingsScreen` and disable the affected actions until verified.

### 2.7 Split storage sanitization from presentation escaping
- [`sanitizeInput`](../app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt#L71-L83) HTML-escapes `< > " ' / &` and is used before writing chat messages, friend-request notes, feedback, profile text, and custom words. The encoding order is intentionally documented in [`ARCHITECTURE_NOTES.md`](ARCHITECTURE_NOTES.md#L519-L526), but storing HTML-escaped user text means Compose can display entities such as `&#x27;` unless every read path decodes them consistently.
- **Action**: split this into two explicit APIs: `normalizeUserTextForStorage(...)` (trim, length, control-character rejection, optional Unicode normalization) and `escapeForHtmlExport(...)` (HTML escaping for generated HTML/PDF/email only). If escaped storage is intentionally retained for backward compatibility, add a central `displayUserText(...)` decoder and tests for chat, feedback, friend notes, and custom words.
- **Migration guard**: do not rewrite existing Firestore text blindly. Add a reader-side compatibility layer first, then migrate only fields proven to be escaped.

### 2.8 Configure `networkSecurityConfig` and explicit cleartext
- [`AndroidManifest.xml`](../app/src/main/AndroidManifest.xml#L13-L25) `<application>` does not set `android:usesCleartextTraffic="false"` and has no `android:networkSecurityConfig`. Default cleartext policy is `false` for `targetSdk >= 28`, but the app supports API 26-27, so be explicit.
- **Action**: add `android:usesCleartextTraffic="false"` and ship `res/xml/network_security_config.xml`:
  ```xml
  <network-security-config>
    <base-config cleartextTrafficPermitted="false">
      <trust-anchors><certificates src="system"/></trust-anchors>
    </base-config>
  </network-security-config>
  ```
  Reference it via `android:networkSecurityConfig="@xml/network_security_config"`.

### 2.9 Split demo release from hardened production release
- [`app/build.gradle.kts`](../app/build.gradle.kts#L29-L37) sets `isMinifyEnabled = false`, `isShrinkResources = false` in release with the comment "Demo-safe release". That is reasonable before grading, but it means the production release would ship unobfuscated, unshrunk code.
- **Action**: gate the demo-safe behaviour behind a build flavour (`releaseDemo`) and add a real `release` variant with `isMinifyEnabled = true`, `isShrinkResources = true`. Re-test with the unit-test gate AND a smoke run; when issues appear, add `-keep` rules in `proguard-rules.pro` (existing rules already cover Firebase, Hilt, Azure SDK).

### 2.10 Lock down Cloud Function abuse surface
- `index.ts` sets `setGlobalOptions({maxInstances: 10})`. With one abusive caller you can saturate translation for everyone.
- **Action**: per-function overrides — `translateText` and `translateTexts` should set `concurrency: 80` and `cpu: 1` per instance and a higher `maxInstances` (e.g., 30); `generateLearningContent` should remain at `maxInstances: 5` (already throttled by `enforceRateLimit`).
- Combine with App Check (2.3) to reject unattested traffic before the function even spins up.

### 2.11 Add strict field allow-lists to social writes
- [`friend_requests`](../fyp-backend/firestore.rules#L389-L404) create validates `fromUserId`, `status`, and block state, but does not constrain `toUserId` type, note length, username lengths, timestamps, or extra fields. `shared_inbox` and chat metadata have similar partial validation.
- **Action**: enforce `request.resource.data.keys().hasOnly([...])` plus type and size checks for every accepted social document. Suggested first pass:
  - `friend_requests`: `fromUserId`, `toUserId`, `fromUsername`, `toUsername`, `note`, `status`, `createdAt`, `updatedAt`; note <= 80, usernames <= 20, status enum.
  - `shared_inbox`: type-specific keys for `WORD`, `LEARNING_SHEET`, and future item types; content docs should enforce `contentType` enum and payload length.
  - `chats/{id}/metadata/info`: require `participants` shape if reintroduced, and cap `unreadCount` map size.

### 2.12 Keep `org.json` test-only and monitor dependency risk
- [`app/build.gradle.kts`](../app/build.gradle.kts#L120) already uses `testImplementation(libs.json)`, so the `org.json:json` dependency is not shipped in the APK. Keep it that way.
- **Action**: add dependency review / dependency submission in CI so test and runtime dependency advisories are visible. If `org.json` ever moves to `implementation`, replace it with `kotlinx.serialization` or pin to the latest stable after verifying compatibility.

### 2.13 Avoid logging PII in Cloud Function logs
- `logger.warn("Spam detected: link flooding", {chatId, senderId, linkCount})` in `notifications.ts` writes `senderId` (a UID) to logs. UIDs alone are PII under PDPO/GDPR.
- **Action**: hash UIDs with a per-environment salt for log fields (`hash(uid + LOG_SALT).slice(0,12)`), reserving raw UIDs for error stacks where the support team needs them.

### 2.14 Replace sample backup/data-extraction XMLs
- `android:allowBackup="false"` is good, but [`backup_rules.xml`](../app/src/main/res/xml/backup_rules.xml) and [`data_extraction_rules.xml`](../app/src/main/res/xml/data_extraction_rules.xml) still contain sample TODO content. The manifest references both files, so they should be intentional.
- **Action**: explicitly exclude `secure_prefs.xml`, `translation_cache.preferences_pb`, `language_detection_cache.preferences_pb`, `word_bank_cache.preferences_pb`, Firestore local cache files, and any Firebase token/cache files. Add a unit test or lint check that these filenames remain excluded.

### 2.15 Clear user-local secrets and caches on sign-out/update sign-out
- [`FirebaseAuthRepository.logout()`](../app/src/main/java/com/example/fyp/data/user/FirebaseAuthRepository.kt#L61-L65) removes the FCM token then signs out. [`MainActivity`](../app/src/main/java/com/example/fyp/appstate/MainActivity.kt#L34-L45) also signs out users after app updates. Neither path clears the cached Azure Speech token, translation cache, language-detection cache, word-bank metadata cache, or OkHttp disk cache.
- **Action**: add a `SessionDataCleaner` injected into logout/update-signout paths. Clear `SecureStorage.KEY_SESSION_TOKEN*`, per-user DataStore caches, and `http_cache`. Keep non-user UI language preferences if they are intentionally device-local.

---

> **Note (manual / infra items deferred from §3 / §4 / §5):** The following
> items require operational decisions, new tooling, or infrastructure changes
> outside the scope of a code-only sweep and have intentionally been left for
> the maintainer:
>
> - **§3.1 drop `unitTests.isReturnDefaultValues`** — flipping to `false`
>   surfaces dozens of latent Android-framework misuse failures across the
>   2,448-test suite; needs a dedicated mocking pass per failing test.
> - **§3.2 expanded instrumented Compose UI tests** — needs a
>   `HiltTestActivity` + fake-repository wiring rebuild and an emulator/CI
>   matrix; the existing `LoginScreenSmokeTest` is the only seed.
> - **§3.3 / §3.10 per-collection Firestore-rules emulator tests** — requires
>   the `@firebase/rules-unit-testing` harness and a CI emulator stage that
>   does not exist yet.
> - **§3.5 Detekt + ktlint** — adds two new Gradle plugins plus a baseline
>   pass to suppress legitimate existing findings; should ship with its own
>   ratchet config.
> - **§3.6 Paparazzi / dropshots screenshot tests** — new dependency,
>   per-locale × per-scale snapshot corpus, and golden-image storage policy.
> - **§3.7 macrobenchmark module** — needs a new Gradle module + an
>   instrumented benchmark device profile in CI.
> - **§3.9 accessibility tests** — needs `Espresso.AccessibilityChecks` to be
>   enabled in instrumented tests, which depend on §3.2's foundation.
> - **§4.2 `OperationBatcher` time-based flush** — only consumer
>   (`FavoritesViewModel.deleteSelected`) calls `flush()` immediately in the
>   same coroutine; adding a timer would be unreachable in production today.
> - **§4.4 disable universal APK in `release`** — current single `release`
>   build is the demo distribution; `package_submission.ps1` and the
>   demo-lab sideloading workflow rely on the universal APK. Defer until
>   a separate `releaseStore` flavour is introduced.
> - **§4.5 denormalised language counts** — requires a Firestore trigger plus
>   a one-off migration; weigh against current low call volume.
> - **§4.6 `pruneStaleTokens` collection-group rewrite** — needs a Firestore
>   composite index deployment plus rewriting `maintenance.test.ts` and
>   `maintenance-deep.test.ts` mock fixtures.
> - **§4.8 client-side translate-all batching window** — UX feature; a real
>   product decision rather than a quality fix.
> - **§4.9 `translateText` `minInstances: 1`** — has an ongoing cost; product
>   call.
> - **§5.3 Cloud Function alert policies** — needs `gcloud monitoring`
>   commands run against the live project plus a new
>   `docs/CLOUD_FUNCTIONS_ALERTS.md` document with the chosen thresholds.
> - **§5.3.1 `LOG_SALT`** — set this env var in the Cloud Functions runtime
>   config so deployed logs use salted hash tokens. The helpers fall back to
>   raw IDs locally when the salt is empty.

## 8. Feature Suggestions (Specific to a Translation-Learning App)

1. **On-device translation fallback** via ML Kit Translate (new dependency; the app already uses ML Kit text recognition, not ML Kit Translate). Use when `translateText` Cloud Function returns network error; restrict first release to common pairs such as English <-> Simplified/Traditional Chinese, Japanese, and Korean.
2. **Pronunciation scoring** via Azure Speech *Pronunciation Assessment* (same SDK as STT). Show 0–100 score + per-phoneme heatmap in the Quiz screen.
3. **Spaced-repetition algorithm (SM-2 / FSRS)** on Word Bank entries. Persist `intervalDays`, `easeFactor`, `nextReview` per word; surface a daily "Review N words" prompt.
4. **Daily streak + leaderboard among friends** (read-only mirror in `users/{uid}/profile/public.streakDays`, gated by Cloud Function so users can't edit their own streak).
5. **Group study rooms** — extend `shared_inbox` schema to `study_rooms/{roomId}` with up to 6 members, shared learning sheet, real-time pointer / cursor sync via Firestore.
6. **Learning-sheet PDF export** with embedded TTS QR codes (via Azure Speech long-form synthesis).
7. **AI conversation summariser** — at the bottom of long chat threads, a "Summarise last 50 messages in [ui language]" button calling a new `summariseChat` callable.
8. **OCR → Word Bank one-tap** — the OCR screen already extracts text; add a long-press "Save selection to Word Bank" shortcut.
9. **Voice-to-Voice Live Translation mode** — combine Speech SDK STT with TTS, switching every 1.5 s of silence; turn-detection threshold configurable in Settings.
10. **Offline first-launch ML Kit language ID** to seed `recentLanguages` without a network round-trip.
11. **Post-FYP retention dashboard** — a Settings screen showing local cache sizes, Firestore sync status, last backup/extraction policy, and one-tap "clear local data" action.
12. **Teacher/export mode** — export a learning sheet + quiz attempts + word-bank review list as PDF/CSV for a tutor or assessor, with HTML escaping done at export time.

---

## 9. Quick-Win Backlog (≤ 1 hour each)

| # | Action | File |
|---|---|---|
| 1 | Set `android:usesCleartextTraffic="false"` | `app/src/main/AndroidManifest.xml` |
| 2 | Set `unitTests.isReturnDefaultValues = false` | `app/build.gradle.kts` |
| 3 | Add `postFYP` to CI push branches while this branch is active | `.github/workflows/ci.yml` |
| 4 | Replace sample backup/data-extraction XML with explicit excludes | `app/src/main/res/xml/{backup_rules,data_extraction_rules}.xml` |
| 5 | Raise `validatePassword` minimum to 8 + add complexity | `core/security/SecurityUtils.kt` |
| 6 | Add App Check dependency + debug-provider setup | `FYPApplication.kt`, `app/build.gradle.kts` |
| 7 | Switch `RateLimiter.attempts` to `ArrayDeque<Long>` | `core/security/SecurityUtils.kt` |
| 8 | Add `SessionDataCleaner` for logout/update signout | `FirebaseAuthRepository.kt`, `MainActivity.kt` |
| 9 | Raise Jest coverage threshold to 70 % branch / 80 % rest | `fyp-backend/functions/jest.config.js` |
| 10 | Centralize Firestore cache settings | `FYPApplication.kt`, `DaggerModule.kt` |

---

## 10. Out of Scope / Known Constraints

- **Demo build constraint** (`isMinifyEnabled = false`) is intentional per the existing comment. Section 2.9 proposes a flavour split rather than a forced removal.
- **Account registration is disabled in dev** per `README.md`; suggestions targeting registration UX assume re-enabling for production.
- The **17 hardcoded UI languages** are intentional (no API call on switch); §2.7 only narrows `sanitizeInput`, it does not propose changing the i18n model.
- **Delete account is already surfaced**; the remaining recommendation is local cache/token cleanup and export/compliance polish, not adding the basic button.
- This document is **suggestions only** — none of the changes have been implemented in this audit pass.
