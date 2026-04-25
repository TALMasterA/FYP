# App Suggestions — Deep, Specific, Actionable (Post-FYP Branch)

Audit-style recommendations for the FYP language-learning app (Android Kotlin/Compose + Firebase Cloud Functions TypeScript).
Findings are anchored to concrete files and observed implementation patterns, not generic advice. Every suggestion is independently actionable.

> Scope of evidence: code reads of `app/src/main/java/com/example/fyp/core/security/**`, `core/performance/**`, `screens/friends/ChatViewModel.kt`, `screens/settings/ProfileScreen.kt`, `data/cloud/**`, `data/network/CacheInterceptor.kt`, `data/di/DaggerModule.kt`, `FYPApplication.kt`, `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/xml/{backup_rules,data_extraction_rules}.xml`, `gradle/libs.versions.toml`, `.github/workflows/{ci,codeql}.yml`, `fyp-backend/firestore.rules`, `fyp-backend/functions/src/{index,helpers,notifications,learning,maintenance,translation}.ts`, `docs/ARCHITECTURE_NOTES.md`, `docs/TEST_COVERAGE.md`, `README.md`.

---

## 1. Executive Summary

The codebase is mature for an FYP: 188 Android unit-test suites / 2,443 Android tests; well-documented invariants (`docs/ARCHITECTURE_NOTES.md`); strong coverage around friends, notifications, coins, history, learning, and UI text; Hilt DI; encrypted preferences via `EncryptedSharedPreferences`; secrets vended through `defineSecret(...)`. The biggest concrete post-FYP gaps are:

1. **Auth strength**: no Firebase App Check; no MFA / email-verification gate. (Client password minimum was raised to 8 in the §2 sweep; complexity / breach-list checks remain deferred — see NOTE block.)
2. **Branch safety**: `.github/workflows/ci.yml` only runs on `main` pushes and PRs to `main`; direct work on `postFYP` will not get automatic CI unless pushed through a PR or the workflow branch filter is widened.
3. **Release hardening**: `isMinifyEnabled = false` in `release` (no R8 / no obfuscation / no shrink) keeps demo builds predictable but should be separated from a real production release variant.
4. **Defence-in-depth gaps in Firestore rules**: the wildcard `match /users/{userId}/{subCol}/{docId}` works as a deny-list. Newly-added user subcollections silently inherit owner read/write unless explicitly excluded.
5. **In-memory `RateLimiter`** for login/reset/chat/feedback throttles is lost across process death; only the friend-request limiter is persisted.
6. **One instrumented Compose UI test** (`LoginScreenSmokeTest`); backend Jest coverage threshold is still permissive (50/45/50/50).
7. **`testOptions.unitTests.isReturnDefaultValues = true`** masks Android-framework misuse in JVM tests.
8. ~~**Network and backup XMLs are placeholders**~~ — addressed in the §2 sweep: cleartext is now explicitly disabled via `usesCleartextTraffic="false"` + `network_security_config.xml`, and `backup_rules.xml` / `data_extraction_rules.xml` now carry explicit excludes for `secure_prefs.xml`, the DataStore caches, and `http_cache`.
9. **No App Check** integration on Cloud Functions / Firestore — abuse vector for unattested clients.

The rest of this document expands each, plus performance, observability, privacy, and feature ideas.

---

## 2. Security Hardening (Specific)

> _All implementable items from this section have been applied (see commit history and the NOTE block below for the deferred items)._ The original sub-sections §2.1–§2.15 have been removed; their actionable changes are now reflected in the codebase, and items that require new dependencies, infra, or coordinated UX/translation work have been folded into the NOTE block as deferred follow-ups.

---

> **Note (manual / infra items deferred from §2 / §3 / §4 / §5):** The following
> items require operational decisions, new tooling, or infrastructure changes
> outside the scope of a code-only sweep and have intentionally been left for
> the maintainer:
>
> - **§2.1 (extension) password complexity + common-password blocklist** —
>   the trimmed-length minimum was raised from 6 to 8 in the §2 sweep, but
>   character-class complexity and the static `assets/common-passwords.txt`
>   blocklist add a new user-facing error key per failure category and need
>   translations across every supported UI language plus a coordinated
>   Firebase Auth password-policy update in the console.
> - **§2.2 persistent auth-path rate limiting** — needs a new
>   `PersistentRateLimiter` backed by `SecureStorage` plus migration of
>   every static `RateLimiter` call site (`ChatViewModel`, login, reset,
>   feedback) to the persistent variant.
> - **§2.3 Firebase App Check** — new dependency + Play Integrity wiring +
>   debug-token bootstrap in CI + redeploying every `onCall` Cloud Function
>   with `enforceAppCheck: true`; breaks local dev until the README setup
>   section is added.
> - **§2.4 replace `SecureStorage` static singleton** — needs a Hilt
>   `@EntryPoint` plus migration of `FcmNotificationService` and
>   `SeenItemsStorage` away from `forContext()` before the static field can
>   be removed safely.
> - **§2.5 Firestore subcollection allow-list** — the wildcard rule cannot
>   be tightened safely without rules-emulator tests (§3.3) running first;
>   any missed subcollection silently breaks production writes.
> - **§2.6 email-verification gate** — UX banner in `SettingsScreen`,
>   feature-disablement copy in 16+ locale strings files, and a Firestore
>   rules tightening that depends on §3.3 emulator coverage.
> - **§2.7 split sanitization from presentation escaping** — large API
>   change touching every screen that reads user-generated text plus a
>   reader-side compatibility decoder for already-escaped Firestore data.
> - **§2.9 split demo release from hardened production release** — needs a
>   new `releaseStore` build flavour and a fresh smoke pass with R8 +
>   resource shrinking enabled; demo distribution still relies on the
>   universal APK.
> - **§2.10 per-function Cloud Function options** — raising
>   `concurrency` / `cpu` / `maxInstances` on `translateText` /
>   `translateTexts` only buys the intended protection together with App
>   Check (§2.3); deferring the two together avoids a window where the
>   raised limits actually amplify abuse.
> - **§2.11 strict field allow-lists on social writes** — high
>   client-breakage risk without rules-emulator tests; tied to §3.3.
> - **§2.12 `org.json` test-only + dependency-review CI** — the test-only
>   guard is already in place; the dependency-review GitHub Action remains
>   a CI workflow change.
> - **§2.13 hash UIDs in Cloud Function logs** — the `hashUid` helper +
>   `LOG_SALT` env wiring is still pending (see §5.3.1 below); deploy them
>   together so the salted hash takes effect the moment the salt is set.
> - **§2.15 `SessionDataCleaner` on logout / post-update sign-out** —
>   needs a new helper plus integration into `FirebaseAuthRepository.logout()`
>   and `MainActivity` post-update sign-out, with care not to wipe the
>   user's UI-language preference.
> - **§3.1 drop `unitTests.isReturnDefaultValues`** — flipping to `false`
>   surfaces dozens of latent Android-framework misuse failures across the
>   2,459-test suite; needs a dedicated mocking pass per failing test.
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
| 1 | ~~Set `android:usesCleartextTraffic="false"`~~ — done in §2.8 | `app/src/main/AndroidManifest.xml` |
| 2 | Set `unitTests.isReturnDefaultValues = false` | `app/build.gradle.kts` |
| 3 | Add `postFYP` to CI push branches while this branch is active | `.github/workflows/ci.yml` |
| 4 | ~~Replace sample backup/data-extraction XML with explicit excludes~~ — done in §2.14 | `app/src/main/res/xml/{backup_rules,data_extraction_rules}.xml` |
| 5 | ~~Raise `validatePassword` minimum to 8~~ — done in §2.1 (complexity rule deferred to NOTE block) | `core/security/SecurityUtils.kt` |
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
