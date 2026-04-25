# App Suggestions — Deep, Specific, Actionable

Audit-style recommendations for the FYP language-learning app (Android Kotlin/Compose + Firebase Cloud Functions TypeScript).
Findings are anchored to concrete files and line patterns, not generic advice. Every suggestion is independently actionable.

> Scope of evidence: code reads of `app/src/main/java/com/example/fyp/core/security/**`, `core/performance/**`, `screens/friends/ChatViewModel.kt`, `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/proguard-rules.pro`, `gradle/libs.versions.toml`, `fyp-backend/firestore.rules`, `fyp-backend/functions/src/{index,helpers,notifications,learning,maintenance}.ts`, `docs/ARCHITECTURE_NOTES.md`, `docs/TEST_COVERAGE.md`, `README.md`.

---

## 1. Executive Summary

The codebase is mature for an FYP: 187 unit-test suites / 2,437 tests; well-documented invariants (`docs/ARCHITECTURE_NOTES.md`); strong Firestore rules with type validation; Hilt DI; encrypted preferences via `EncryptedSharedPreferences`; secrets vended through `defineSecret(...)`. The biggest concrete gaps are:

1. **Auth strength**: `validatePassword` enforces only 6-char minimum; no Firebase App Check; no MFA / email-verification gate.
2. **Release hardening**: `isMinifyEnabled = false` in `release` (no R8 / no obfuscation / no shrink) → larger APK and exposed class names.
3. **Defence-in-depth gaps in Firestore rules**: subcollection deny-list pattern (per `ARCHITECTURE_NOTES.md`) means newly-added subcollections silently inherit "open to owner" semantics.
4. **In-memory `RateLimiter`** for client-side throttles (auth, chat) is lost across process death; only `FriendRequestRateLimiter` persists.
5. **One instrumented Compose UI test** (`LoginScreenSmokeTest`); backend Jest coverage threshold only 50%.
6. **`testOptions.unitTests.isReturnDefaultValues = true`** masks `NullPointerException`-class regressions in JVM tests.
7. **Network security config missing** — `usesCleartextTraffic` is unset and there is no `networkSecurityConfig` XML; defaults are weaker than necessary.
8. **No App Check** integration on Cloud Functions / Firestore — abuse vector for unattested clients.

The rest of this document expands each, plus performance, observability, privacy, and feature ideas.

---

## 2. Security Hardening (Specific)

### 2.1 Strengthen client password policy
- `app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt` `validatePassword` only checks `trimmed.length < minLength` with default `6`. No complexity, no breach check.
- **Action**: require ≥8 chars and at least 3 of {upper, lower, digit, symbol}; reject the 100 most common passwords (ship a small static set in `assets/`); enforce on registration **and** password-change. Pair with the **Firebase Auth password policy** in the Firebase Console (set `MINIMUM_LENGTH=8`, `REQUIRE_UPPERCASE`, `REQUIRE_LOWERCASE`, `REQUIRE_NUMERIC`, `REQUIRE_NON_ALPHANUMERIC`).
- **Test gate**: add unit tests that `validatePassword("Abcdef1!")` is `Valid` and `validatePassword("aaaaaa")`, `validatePassword("Password1")` are `Invalid`.

### 2.2 Persist auth-path rate limiting
- The static `messageRateLimiter` companion in [`ChatViewModel.kt`](app/src/main/java/com/example/fyp/screens/friends/ChatViewModel.kt#L77) and other `RateLimiter` instances live only in process memory. On process death (Doze, low-memory kill, swipe-from-recents) the counter resets — defeating the abuse defence.
- **Action**: introduce `PersistentRateLimiter` backed by `SecureStorage` (already present). Mirror the pattern used by `FriendRequestRateLimiter` per `ARCHITECTURE_NOTES.md`. Apply to: login attempts, password-reset requests, chat send, friend-request send.
- Schema in `secure_prefs`: `ratelimit:<key>` → CSV of millis (cap to `maxAttempts`).

### 2.3 Add Firebase App Check
- No `FirebaseAppCheck.getInstance().installAppCheckProviderFactory(...)` call exists. Without App Check, a leaked Firebase config + Cloud Functions endpoint is callable by any client.
- **Action**: enable App Check with **Play Integrity** in `FYPApplication.onCreate()` for release, and **Debug Provider** for `debug` build type. Enforce `consumeAppCheckToken` on every `onCall` callable (`callableOptions: { enforceAppCheck: true }`) in `fyp-backend/functions/src/{translation,learning,coins}.ts`.

### 2.4 Replace `SecureStorage` static singleton anti-pattern
- [`SecureStorage.kt`](app/src/main/java/com/example/fyp/core/security/SecureStorage.kt#L58-L83) writes `staticInstance = this` from `init { ... }` (`@Suppress("LeakingThis")`) and exposes `forContext(...)` to bypass Hilt.
- **Action**: replace `forContext(...)` callers with a Hilt `@EntryPoint`:
  ```kotlin
  @EntryPoint @InstallIn(SingletonComponent::class)
  interface SecureStorageEntryPoint { fun secureStorage(): SecureStorage }
  ```
  and obtain via `EntryPoints.get(appContext, SecureStorageEntryPoint::class.java).secureStorage()`. Remove the `staticInstance` field and `forContext()` once all callers migrate.

### 2.5 Convert Firestore rules subcollection wildcard to allow-list
- `match /users/{userId}/{subCol}/{docId}` matches **any** subcollection. New collections silently inherit owner-only semantics, including ones that should be server-write-only.
- **Action**: replace the wildcard with explicit per-collection matches (`match /users/{userId}/history/{id}`, `…/word_bank/{id}`, etc.). Add a final `match /users/{userId}/{subCol=**} { allow read,write: if false }` catch-all. Update `firestore.rules` test file to assert that a write to `users/{me}/test_unknown_collection/x` is denied.

### 2.6 Enforce email verification before sensitive operations
- `auth.currentUser.isEmailVerified` is never checked in flows like friend-add, chat send, feedback submit.
- **Action**: add `firestore.rules` clause `&& request.auth.token.email_verified == true` to: `friend_requests` create, `shared_inbox` create, `feedback` create. Surface a "Verify your email" banner in `SettingsScreen` and disable the affected actions until verified.

### 2.7 Reconsider `sanitizeInput` scope
- [`sanitizeInput`](app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt#L70-L80) HTML-escapes `< > " ' / &`. Compose `Text(...)` does **not** render HTML, so escaping for display is unnecessary and corrupts user content (e.g., usernames containing `'`, learning notes containing `/`).
- **Action**: rename to `escapeForHtmlExport` and use **only** at HTML/PDF export boundaries (e.g., learning-sheet PDF export, server-side echo for emails). For Firestore writes, validate with `validateTextLength` and a deny-list of control characters instead.

### 2.8 Configure `networkSecurityConfig` and explicit cleartext
- [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml#L13-L25) `<application>` does not set `android:usesCleartextTraffic="false"` and has no `android:networkSecurityConfig`. Default cleartext policy is `false` for `targetSdk ≥ 28` but `minSdk = 26` allows cleartext on API 26–27.
- **Action**: add `android:usesCleartextTraffic="false"` and ship `res/xml/network_security_config.xml`:
  ```xml
  <network-security-config>
    <base-config cleartextTrafficPermitted="false">
      <trust-anchors><certificates src="system"/></trust-anchors>
    </base-config>
  </network-security-config>
  ```
  Reference it via `android:networkSecurityConfig="@xml/network_security_config"`.

### 2.9 Re-enable R8 / shrinking / obfuscation in release
- [`app/build.gradle.kts`](app/build.gradle.kts#L29-L37) sets `isMinifyEnabled = false`, `isShrinkResources = false` in release with the comment "Demo-safe release". This means: (a) class names are unobfuscated → easier reverse engineering; (b) APK ~30–50 % larger; (c) dead code shipped to users.
- **Action**: gate the demo-safe behaviour behind a build flavour (`releaseDemo`) and add a real `release` variant with `isMinifyEnabled = true`, `isShrinkResources = true`. Re-test with the unit-test gate AND a smoke run; when issues appear, add `-keep` rules in `proguard-rules.pro` (existing rules already cover Firebase, Hilt, Azure SDK).

### 2.10 Lock down Cloud Function abuse surface
- `index.ts` sets `setGlobalOptions({maxInstances: 10})`. With one abusive caller you can saturate translation for everyone.
- **Action**: per-function overrides — `translateText` and `translateTexts` should set `concurrency: 80` and `cpu: 1` per instance and a higher `maxInstances` (e.g., 30); `generateLearningContent` should remain at `maxInstances: 5` (already throttled by `enforceRateLimit`).
- Combine with App Check (2.3) to reject unattested traffic before the function even spins up.

### 2.11 Validate inbound Firestore field shapes server-side too
- Rules already type-check most fields. However, `friend_requests` create only checks `status == "PENDING"` and `fromUserId == auth.uid` — but does **not** type-check `toUserId`, `createdAt`, etc. (need to verify lines 380–400 of `firestore.rules`).
- **Action**: enforce `request.resource.data.keys().hasOnly([...])` plus `is string`, `is timestamp` for every accepted field. This prevents attackers writing oversized or unexpected fields.

### 2.12 Remove `org.json:json` if not strictly needed
- `gradle/libs.versions.toml` pins `json = "20240303"`. Recent versions (`20240303` onwards) had multiple CVEs in 2024. If only used in tests, mark as `testImplementation` only; otherwise upgrade to `20250517+`.

### 2.13 Avoid logging PII in Cloud Function logs
- `logger.warn("Spam detected: link flooding", {chatId, senderId, linkCount})` in `notifications.ts` writes `senderId` (a UID) to logs. UIDs alone are PII under PDPO/GDPR.
- **Action**: hash UIDs with a per-environment salt for log fields (`hash(uid + LOG_SALT).slice(0,12)`), reserving raw UIDs for error stacks where the support team needs them.

### 2.14 Backup-rules audit
- `android:allowBackup="false"` is good. Verify `res/xml/data_extraction_rules.xml` and `backup_rules.xml` explicitly exclude `secure_prefs.xml`, `datastore/*.preferences_pb`, `databases/*`, and Firebase `.json` caches.

---

## 3. Quality Assurance / Testing

### 3.1 Drop `unitTests.isReturnDefaultValues = true`
- [`app/build.gradle.kts`](app/build.gradle.kts#L60) hides Android-framework `NullPointerException`s by returning defaults from un-mocked Android classes. This silently turns "I forgot to mock `Context`" into a green test.
- **Action**: set to `false`. Replace any failures with explicit `@MockK` / `mockito-kotlin` mocks. Keep `Robolectric` only where a `Context` is genuinely required.

### 3.2 Expand instrumented Compose UI tests
- `app/src/androidTest/` contains essentially one Compose smoke test (`LoginScreenSmokeTest`).
- **Action priority order**:
  1. `FriendsListScreenTest` — search, send/cancel/accept request, block, unfriend (covers most-used path).
  2. `ChatScreenTest` — send, mark-as-read, translate-all, infinite scroll back, blocked banner.
  3. `QuizScreenTest` — answer correctness, coin award, retry-on-network-error.
  4. `WordBankScreenTest` — add/edit/delete with offline banner.
- Use `createAndroidComposeRule<HiltTestActivity>()` with `FakeFriendsRepository`, `FakeAuthRepository` Hilt test modules.

### 3.3 Add Firestore-rules emulator test for every collection
- Currently `fyp-backend/functions/__tests__/firestore-rules-settings.test.ts` (per memory). One file is not enough for a 400-line rules document.
- **Action**: separate test files for `friend_requests`, `shared_inbox`, `feedback`, `usernames`, `user_search`, `chats/{id}/messages`, `coin_awards`. Each must include negative tests (cross-user write, oversized field, missing required field, blocked sender).

### 3.4 Raise backend Jest coverage threshold
- `fyp-backend/functions/jest.config.js` caps at 50 %. With 184 tests across 14 files in 8 modules, coverage is likely already higher; the threshold is just permissive.
- **Action**: raise `coverageThreshold.global` to `{ branches: 70, functions: 80, lines: 80, statements: 80 }`. Ratchet only — never lower.

### 3.5 Add Detekt + ktlint to the build
- No static-analysis plugin in `build.gradle.kts`.
- **Action**: apply `id("io.gitlab.arturbosch.detekt") version "1.23.6"` and `id("org.jlleitschuh.gradle.ktlint") version "12.1.1"`. Add `:app:detekt` and `:app:ktlintCheck` to `ci.yml`. Start with `failFast = false` then ratchet.

### 3.6 Add screenshot / golden tests
- Theme + font scaling (Settings allows 0.5–2.0×) is regression-prone in Compose.
- **Action**: introduce `dropbox/dropshots` or **Paparazzi**; snapshot `LoginScreen`, `FriendsScreen`, `ChatBubble`, `QuizCard` at scale `0.85`, `1.0`, `1.5` × {Light, Dark}.

### 3.7 Add macrobenchmark module
- No `:macrobenchmark` module exists.
- **Action**: add `app:macrobenchmark` to measure cold-start, `History` scroll, `FriendsList` scroll. Fail CI if `startupTimeMs.median > 1500ms` on the standard CI emulator.

### 3.8 Property / fuzz tests for input validators
- `sanitizeInput`, `validateUsername`, `validateLanguageCode`, FCM token shape — all good fuzz candidates.
- **Action**: add `kotest-property` (or `jqwik` for JUnit) to assert: `sanitizeInput(sanitizeInput(s)) == sanitizeInput(s)` (idempotent), `validateUsername(genAlnum(3..20))` is always `Valid`, etc.

### 3.9 Accessibility tests
- No automated check that touch targets ≥ 48 dp or that every Composable has `contentDescription`.
- **Action**: in `androidTest/`, add `composeTestRule.onRoot().assertNoUnlabelledImages()` helpers; run `Espresso.AccessibilityChecks.enable()` in a base test rule.

---

## 4. Performance / Efficiency

### 4.1 Use `mutableLongStateOf` / `mutableIntStateOf`
- `ThrottledLaunchedEffect` in [`PerformanceUtils.kt`](app/src/main/java/com/example/fyp/core/performance/PerformanceUtils.kt#L40-L57) does `var lastExecutionTime by remember { mutableStateOf(0L) }` — this **boxes** every Long write. Compose has had `mutableLongStateOf` since 1.5.
- **Action**: replace all `mutableStateOf(0L)` / `mutableStateOf(0)` in `core/performance/**` and any ViewModel local Compose state.

### 4.2 Add a time-based flush to `OperationBatcher`
- Per repository memory and code, `OperationBatcher` only flushes on size or explicit `flush()`. If size is never reached, items sit forever.
- **Action**: add an optional `flushIntervalMs: Long? = null` parameter; on first `submit`, schedule `delay(flushIntervalMs); flush()` in the same scope. Cancel on size-flush. Update the consumer in `FavoritesViewModel` to set 1500 ms.

### 4.3 Replace `MutableList<Long>.removeAll { ... }` in `RateLimiter`
- [`SecurityUtils.kt`](app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt#L120-L150) uses `record.attempts.removeAll { now - it > windowMillis }` per `isAllowed`. With `maxAttempts = 5`, the cost is trivial; with chat-style 100+ per window it becomes O(n²) over a session.
- **Action**: switch `attempts` to `kotlin.collections.ArrayDeque<Long>` and `while (deque.isNotEmpty() && now - deque.first() > windowMillis) deque.removeFirst()` for O(amortised 1) per check.

### 4.4 Replace ABI universal APK in `release`
- [`app/build.gradle.kts`](app/build.gradle.kts#L75-L86) `splits.abi { isUniversalApk = true }`. Universal APK is ~3× the size of any single-ABI APK and is rarely needed for store distribution.
- **Action**: keep `isUniversalApk = true` only in `releaseDemo` flavour from §2.9; set to `false` in real `release`.

### 4.5 Cloud Function: collapse `getLanguageHistoryCount` queries
- `learning.ts` runs three `count()` aggregations sequentially-in-parallel and computes `source + target - same`. Each `count()` is billed at 1 read per 1000 documents.
- **Action**: maintain a denormalised `users/{uid}/profile/private.languageCounts` map updated by a Firestore trigger on `history` write/delete. Reads become 1 doc-read instead of 3 aggregations.

### 4.6 `pruneStaleTokens` is O(users × subcollection-query)
- [`maintenance.ts`](fyp-backend/functions/src/maintenance.ts#L18-L62) iterates every user and runs a `where("updatedAt", "<", cutoff)` on each `fcm_tokens` subcollection.
- **Action**: switch to `collectionGroup("fcm_tokens").where("updatedAt", "<", cutoff).limit(500)` with cursor pagination. Single index, single query loop. Add the composite index to `firestore.indexes.json`.

### 4.7 Verify all `LazyColumn` calls supply `key = `
- Stable keys are critical for animation correctness and avoid full recomposition on list-shape change.
- **Action**: `grep "LazyColumn" -A 4` across `app/src/main/java/com/example/fyp/screens/**` and ensure every `items(...)` passes `key = { it.id.value }` (or equivalent stable identity).

### 4.8 Translate-all batching window
- `TranslateAllMessagesUseCase` (called from `ChatViewModel`) — verify it groups under the **5,000-character** limit `MAX_TRANSLATE_TEXT_LENGTH` from `helpers.ts`. If a user opens a chat with hundreds of long messages, a single call may exceed the limit and fail; current behaviour likely throws.
- **Action**: split into client-side batches of `≤4500` chars per call, await sequentially with progress UI.

### 4.9 Cloud Function cold start
- `setGlobalOptions({maxInstances: 10})` and no `minInstances`. Translate cold-start can be 2–4 s on first message of the day for a user.
- **Action**: for `translateText` set `minInstances: 1` (cost: 1 instance × 24 h ≈ trivial vs. UX win). Skip for `generateLearningContent` (rare, can tolerate cold start).

### 4.10 OkHttp cache size
- Confirm `CacheInterceptor` (`docs/ARCHITECTURE_NOTES.md` mentions it) sets `Cache(File(context.cacheDir, "http"), 10 * 1024 * 1024)` (10 MiB) and that authenticated responses bypass cache. If cache is shared across users, log out should clear it (`cache.evictAll()`).

---

## 5. Reliability & Observability

### 5.1 End-to-end correlation ID
- No `X-Request-ID` header is generated client-side and propagated to Cloud Functions.
- **Action**: in OkHttp `Interceptor`, attach `X-Request-ID = UUID.randomUUID().toString()`. In every Cloud Function, log `{requestId: req.headers["x-request-id"]}`. Surface the ID in any user-facing error toast for support escalations.

### 5.2 Crashlytics custom keys
- `AuditLogger` already sets `last_audit_event` / `last_audit_user`. Missing: `current_screen`, `current_locale`, `auth_state`, `quiz_in_progress`.
- **Action**: in `Navigation` `NavController.OnDestinationChangedListener`, set `FirebaseCrashlytics.getInstance().setCustomKey("current_screen", route)`.

### 5.3 Cloud Function alert policies
- No `gcloud alpha monitoring policies create ...` documented in `docs/`.
- **Action**: add `docs/CLOUD_FUNCTIONS_ALERTS.md` with three policies: error-rate > 2 % over 10 min, p95 latency > 5 s over 10 min, function-killed-by-OOM count > 0. Reference Cloud Console links.

### 5.4 `@PropertyName` Firebase JavaBean trap
- `ARCHITECTURE_NOTES.md` notes the `isDiscoverable` bug fix. Add a static analyser or unit test scanning `model/**/*.kt` for any `val isXxx: Boolean` without `@PropertyName("isXxx")` to prevent regression.

---

## 6. Code Quality / Architecture

### 6.1 Domain layer purity
- Verify `domain/**` has no Android imports (`android.*`, `androidx.*`, `com.google.firebase.*`). Add a Detekt rule (`ForbiddenImport`) to enforce.

### 6.2 ViewModel constructor sprawl
- `ChatViewModel` has **10** constructor dependencies. Even with Hilt, this is a smell.
- **Action**: introduce a `ChatDependencies` aggregate (Use-Cases + Repositories) or split the VM into `ChatMessagesViewModel` + `ChatProfileViewModel` + `ChatTranslationViewModel`.

### 6.3 Reduce stringly-typed `SavedStateHandle` access
- `savedStateHandle.get<String>("friendId")` etc. — no compile-time safety.
- **Action**: use `androidx.navigation` `Navigation Compose 2.8` typed routes (`@Serializable data class ChatRoute(val friendId: String, ...)`) and `toRoute<ChatRoute>()` helper.

### 6.4 Replace `mutableMapOf` in `RateLimiter` with `LinkedHashMap` LRU
- For deterministic eviction order in `pruneStaleKeys`, an LRU map (or `ArrayMap` with manual ordering) is clearer than the current first-iterator-wins eviction.

### 6.5 Update stale dependencies
- `composeBom = 2024.09.00` → `2025.10.00+`
- `kotlin = 2.0.21` → `2.1.20+` (faster KSP)
- `composeUiTest = 1.7.6` (hard-coded) — drop the version and let the BOM control it
- `securityCrypto = 1.1.0-alpha06` — alpha; pin only after re-encryption test
- Run `.\gradlew.bat dependencyUpdates` (Ben Manes plugin) monthly.

### 6.6 Add CI dependency-graph submission for SCA
- `.github/workflows/codeql.yml` covers source CodeQL but not vulnerable dependencies.
- **Action**: add `gradle/actions/dependency-submission@v3` and `actions/dependency-review-action@v4` so Dependabot alerts surface for transitive deps.

---

## 7. Privacy & Compliance

### 7.1 Surface "Delete my account" UX
- The 18-subcollection cleanup is implemented (per `ARCHITECTURE_NOTES.md`). Verify it is exposed in `SettingsScreen` with a destructive confirmation dialog (re-type email + password). If buried, users can't exercise GDPR Art. 17.

### 7.2 Add "Export my data"
- Provide a callable `exportUserData` Cloud Function that returns a signed-URL ZIP containing the user's history, word bank, learning sheets, friend list, and chat history JSON. Ship with a 7-day URL expiry.

### 7.3 Document privacy posture
- Create `docs/PRIVACY_AND_COMPLIANCE.md` covering: data categories collected, retention windows (FCM 60 d, rate-limits 30 d), processors (Firebase, Azure Translator, Azure Speech, Azure OpenAI), legal basis, right-to-erasure procedure (1-tap delete), DPO contact.

### 7.4 Azure Speech token caching scope
- `SecureStorage.KEY_SESSION_TOKEN` is global. After log-out, clear it (`secureStorage.remove(KEY_SESSION_TOKEN)`) to prevent the next user on a shared device from using the cached token.

---

## 8. Feature Suggestions (Specific to a Translation-Learning App)

1. **On-device translation fallback** via `com.google.mlkit:translate` (already a Google ML Kit dependency-cousin). Use when `translateText` Cloud Function returns network error; saves cost on common pairs (`en-US ↔ zh-CN`).
2. **Pronunciation scoring** via Azure Speech *Pronunciation Assessment* (same SDK as STT). Show 0–100 score + per-phoneme heatmap in the Quiz screen.
3. **Spaced-repetition algorithm (SM-2 / FSRS)** on Word Bank entries. Persist `intervalDays`, `easeFactor`, `nextReview` per word; surface a daily "Review N words" prompt.
4. **Daily streak + leaderboard among friends** (read-only mirror in `users/{uid}/profile/public.streakDays`, gated by Cloud Function so users can't edit their own streak).
5. **Group study rooms** — extend `shared_inbox` schema to `study_rooms/{roomId}` with up to 6 members, shared learning sheet, real-time pointer / cursor sync via Firestore.
6. **Learning-sheet PDF export** with embedded TTS QR codes (via Azure Speech long-form synthesis).
7. **AI conversation summariser** — at the bottom of long chat threads, a "Summarise last 50 messages in [ui language]" button calling a new `summariseChat` callable.
8. **OCR → Word Bank one-tap** — the OCR screen already extracts text; add a long-press "Save selection to Word Bank" shortcut.
9. **Voice-to-Voice Live Translation mode** — combine Speech SDK STT with TTS, switching every 1.5 s of silence; turn-detection threshold configurable in Settings.
10. **Offline first-launch ML Kit language ID** to seed `recentLanguages` without a network round-trip.

---

## 9. Quick-Win Backlog (≤ 1 hour each)

| # | Action | File |
|---|---|---|
| 1 | Set `android:usesCleartextTraffic="false"` | `app/src/main/AndroidManifest.xml` |
| 2 | Set `unitTests.isReturnDefaultValues = false` | `app/build.gradle.kts` |
| 3 | Replace `mutableStateOf(0L)` with `mutableLongStateOf` | `core/performance/PerformanceUtils.kt` |
| 4 | Raise `validatePassword` minimum to 8 + add complexity | `core/security/SecurityUtils.kt` |
| 5 | Add `enforceAppCheck: true` to every `onCall` | `fyp-backend/functions/src/{translation,learning,coins}.ts` |
| 6 | Switch `RateLimiter.attempts` to `ArrayDeque<Long>` | `core/security/SecurityUtils.kt` |
| 7 | Hash UIDs in Cloud Function logs | `fyp-backend/functions/src/notifications.ts` |
| 8 | Add `collectionGroup("fcm_tokens")` index for prune | `fyp-backend/firestore.indexes.json` |
| 9 | Raise Jest coverage threshold to 70 % branch / 80 % rest | `fyp-backend/functions/jest.config.js` |
| 10 | Add `ratchet` Detekt + ktlint Gradle plugins | `app/build.gradle.kts` |

---

## 10. Out of Scope / Known Constraints

- **Demo build constraint** (`isMinifyEnabled = false`) is intentional per the existing comment. Section 2.9 proposes a flavour split rather than a forced removal.
- **Account registration is disabled in dev** per `README.md`; suggestions targeting registration UX assume re-enabling for production.
- The **17 hardcoded UI languages** are intentional (no API call on switch); §2.7 only narrows `sanitizeInput`, it does not propose changing the i18n model.
- This document is **suggestions only** — none of the changes have been implemented in this audit pass.
