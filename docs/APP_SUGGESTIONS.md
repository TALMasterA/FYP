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

## 3. Quality Assurance / Testing

### 3.1 Drop `unitTests.isReturnDefaultValues = true`
- [`app/build.gradle.kts`](../app/build.gradle.kts#L60) hides Android-framework `NullPointerException`s by returning defaults from un-mocked Android classes. This silently turns "I forgot to mock `Context`" into a green test.
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

### 3.4.1 Make `postFYP` branch run CI automatically
- [`.github/workflows/ci.yml`](../.github/workflows/ci.yml#L3-L9) runs on pushes to `main` and PRs targeting `main`. If development continues directly on `postFYP`, a push to `postFYP` will not run Android tests, assemble, backend lint/build, or Jest coverage.
- **Action**: while using this branch for post-grade development, change the workflow filters to `branches: [main, postFYP]` for `push` and keep PR protection on `main`. Remove `postFYP` from the filter once work merges back.

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
- **Action**: add `kotest-property` (or `jqwik` for JUnit) for validators, but do **not** assert `sanitizeInput` idempotence. The documented invariant is encoding order, not idempotence. Good properties:
  - `sanitizeInput(s)` never contains raw `<`, `>`, `"`, `'`, or `/` after trimming.
  - `sanitizeInput("<x>") == "&lt;x&gt;"` and `sanitizeInput("&lt;x&gt;")` preserves the ampersand-first behavior expected by `SanitizeInputExtendedTest`.
  - `validateUsername(genAlnumUnderscore(3..20))` is always `Valid`; usernames containing spaces, slash, emoji, or hyphen are rejected unless the regex is intentionally changed.

### 3.9 Accessibility tests
- No automated check that touch targets ≥ 48 dp or that every Composable has `contentDescription`.
- **Action**: in `androidTest/`, add `composeTestRule.onRoot().assertNoUnlabelledImages()` helpers; run `Espresso.AccessibilityChecks.enable()` in a base test rule.

### 3.10 Firestore rules tests for wildcard and social schemas
- The rules are security-critical and have nuanced exceptions for shared inbox, friend requests, unread counters, and reciprocal friend operations.
- **Action**: add emulator tests for these negative cases: unknown `users/{uid}/{subCol}` write denied, friend-request extra field denied, blocked sender cannot create `shared_inbox`, non-mutual friend cannot chat, and client cannot write `rate_limits` or server-owned quiz/coin docs.

---

## 4. Performance / Efficiency

### 4.1 Use `mutableLongStateOf` / `mutableIntStateOf`
- `ThrottledLaunchedEffect` in [`PerformanceUtils.kt`](../app/src/main/java/com/example/fyp/core/performance/PerformanceUtils.kt#L40-L57) does `var lastExecutionTime by remember { mutableStateOf(0L) }` — this **boxes** every Long write. Compose has had `mutableLongStateOf` since 1.5.
- **Action**: replace all `mutableStateOf(0L)` / `mutableStateOf(0)` in `core/performance/**` and any ViewModel local Compose state.

### 4.2 Add a time-based flush to `OperationBatcher`
- Per repository memory and code, `OperationBatcher` only flushes on size or explicit `flush()`. If size is never reached, items sit forever.
- **Action**: add an optional `flushIntervalMs: Long? = null` parameter; on first `submit`, schedule `delay(flushIntervalMs); flush()` in the same scope. Cancel on size-flush. Update the consumer in `FavoritesViewModel` to set 1500 ms.

### 4.3 Replace `MutableList<Long>.removeAll { ... }` in `RateLimiter`
- [`SecurityUtils.kt`](../app/src/main/java/com/example/fyp/core/security/SecurityUtils.kt#L120-L150) uses `record.attempts.removeAll { now - it > windowMillis }` per `isAllowed`. With `maxAttempts = 5`, the cost is trivial; with chat-style 100+ per window it becomes O(n²) over a session.
- **Action**: switch `attempts` to `kotlin.collections.ArrayDeque<Long>` and `while (deque.isNotEmpty() && now - deque.first() > windowMillis) deque.removeFirst()` for O(amortised 1) per check.

### 4.4 Replace ABI universal APK in `release`
- [`app/build.gradle.kts`](../app/build.gradle.kts#L75-L86) `splits.abi { isUniversalApk = true }`. Universal APK is ~3× the size of any single-ABI APK and is rarely needed for store distribution.
- **Action**: keep `isUniversalApk = true` only in `releaseDemo` flavour from §2.9; set to `false` in real `release`.

### 4.5 Cloud Function: collapse `getLanguageHistoryCount` queries
- `learning.ts` runs three `count()` aggregations sequentially-in-parallel and computes `source + target - same`. Each `count()` is billed at 1 read per 1000 documents.
- **Action**: maintain a denormalised `users/{uid}/profile/private.languageCounts` map updated by a Firestore trigger on `history` write/delete. Reads become 1 doc-read instead of 3 aggregations.

### 4.6 `pruneStaleTokens` is O(users × subcollection-query)
- [`maintenance.ts`](../fyp-backend/functions/src/maintenance.ts#L18-L62) iterates every user and runs a `where("updatedAt", "<", cutoff)` on each `fcm_tokens` subcollection.
- **Action**: switch to `collectionGroup("fcm_tokens").where("updatedAt", "<", cutoff).limit(500)` with cursor pagination. Add any Firestore index the emulator/console requests; simple collection-group filters may be covered by single-field indexes, but test it before deploying.

### 4.7 Keep LazyColumn key stability as a regression check
- Current scan shows most dynamic `LazyColumn` / `items(...)` calls already supply stable keys (`messageId`, `friendId`, `requestId`, `languageCode`, etc.). The risk is regression, not an obvious current gap.
- **Action**: add a lightweight static test that scans `screens/**` for `items(` without `key =` except deliberate fixed-count placeholders like `items(3)` skeletons. This preserves the existing good pattern.

### 4.8 Translate-all batching window
- [`translateTexts`](../fyp-backend/functions/src/translation.ts#L220-L369) already chunks server-side into Azure batches of 100 texts, allows up to 800 texts, enforces 5,000 chars per element, and retries Azure 429s. The client [`TranslateAllMessagesUseCase`](../app/src/main/java/com/example/fyp/domain/friends/TranslateAllMessagesUseCase.kt) still sends all unique friend messages at once and uses a 30-second callable timeout.
- **Action**: add a client preflight: cap one translate-all request to the newest 800 unique friend messages, disable the button while translating, show progress/cancel, and provide "Translate older messages" for the next page. Consider increasing timeout only if metrics show legitimate 800-message batches exceed 30 seconds.

### 4.9 Cloud Function cold start
- `setGlobalOptions({maxInstances: 10})` and no `minInstances`. Translate cold-start can be 2–4 s on first message of the day for a user.
- **Action**: for `translateText` set `minInstances: 1` (cost: 1 instance × 24 h ≈ trivial vs. UX win). Skip for `generateLearningContent` (rare, can tolerate cold start).

### 4.10 Make HTTP cache lifecycle user-aware
- [`DaggerModule`](../app/src/main/java/com/example/fyp/data/di/DaggerModule.kt#L91-L103) creates a 50 MB OkHttp cache and [`CacheInterceptor`](../app/src/main/java/com/example/fyp/data/network/CacheInterceptor.kt) avoids caching requests with an `Authorization` header. That is a good baseline, but the cache is device-wide and no sign-out path calls `cache.evictAll()`.
- **Action**: either inject `Cache` into the `SessionDataCleaner` from §2.15 and evict on logout/update-signout, or document why only public unauthenticated assets ever enter it.

### 4.11 Centralize Firestore settings configuration
- [`FYPApplication`](../app/src/main/java/com/example/fyp/appstate/FYPApplication.kt#L27-L38) configures Firestore persistence with the older `setPersistenceEnabled(true)` API, while [`DaggerModule`](../app/src/main/java/com/example/fyp/data/di/DaggerModule.kt#L74-L87) configures `PersistentCacheSettings` with a 50 MB cap. Whichever runs second catches/logs an exception.
- **Action**: keep one authoritative Firestore configuration path. Prefer `PersistentCacheSettings` with an explicit cache-size cap, and remove the duplicate app-start configuration after verifying no startup ordering regression.

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

### 5.3.1 Add structured log redaction helpers
- Backend logs currently include raw `uid`, `senderId`, `receiverId`, and chat IDs in several places (`notifications.ts`, `helpers.ts`, `learning.ts`). These are useful for support but risky to spread across logs.
- **Action**: add `logUser(uid)` / `logChat(chatId)` helper functions that hash IDs with an environment-specific salt for routine info/warn logs. Keep raw IDs only in explicitly marked admin-debug logs with short retention.

### 5.4 `@PropertyName` Firebase JavaBean trap
- `ARCHITECTURE_NOTES.md` notes the `isDiscoverable` bug fix. Add a static analyser or unit test scanning `model/**/*.kt` for any `val isXxx: Boolean` without `@PropertyName("isXxx")` to prevent regression.

---

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
