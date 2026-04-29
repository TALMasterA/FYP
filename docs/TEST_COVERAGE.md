# Test Coverage Report

_Last updated: 2026-04-27_

## Verified Snapshot (from local audit)

| Metric | Count |
|---|---:|
| Android source files (`app/src/main/java/com/translator/TalknLearn`) | 241 |
| Backend source files (`fyp-backend/functions/src/*.ts`) | 11 |
| Android unit test files (`app/src/test/java/com/translator/TalknLearn`) | 197 |
| Android test suites in latest `testDebugUnitTest` run | 197 |
| Android unit tests executed (`testDebugUnitTest`) | 2,495 |
| Backend test files (`fyp-backend/functions/src/__tests__`) | 17 |
| Backend Jest tests executed (`npm run test:coverage`) | 193 |

## Backend Coverage (from `coverage-summary.json`)

| Metric | Current |
|---|---:|
| Statements | 93.97% |
| Branches | 79.50% |
| Functions | 93.40% |
| Lines | 94.83% |

CI threshold in `fyp-backend/functions/jest.config.js`:
- statements >= 90%
- branches >= 78%
- functions >= 90%
- lines >= 90%

## Android Coverage (Kover)

Android module `:app` is wired with [Kover](https://github.com/Kotlin/kotlinx-kover) `0.9.1`
(plugin `org.jetbrains.kotlinx.kover`, configured at the bottom of
[app/build.gradle.kts](../app/build.gradle.kts)).

Local tasks:

- `./gradlew :app:koverHtmlReportDebug` → HTML report at `app/build/reports/kover/htmlDebug/index.html`
- `./gradlew :app:koverXmlReportDebug` → XML report at `app/build/reports/kover/reportDebug.xml`
- `./gradlew :app:koverVerifyDebug` → enforces the coverage floor

Latest measured coverage on `debug` (after exclusions):

| Metric | Current | Floor |
|---|---:|---:|
| Line | 87.09% | 35% |
| Instruction | 84.33% | — |
| Branch | 47.54% | — |
| Method | 62.06% | — |
| Class | 72.71% | — |

Floor is intentionally low (35%, line-based) as an initial regression guard;
raise `minValue` in [app/build.gradle.kts](../app/build.gradle.kts) when ready.

Excluded from the report (generated / non-logic code): Hilt/Dagger generated
classes (`*_Factory`, `*_Impl`, `Hilt_*`, `Dagger*`), KSP-generated classes
(`*_GeneratedInjector`, `*_HiltModules*`), Compose composables (`@Composable`),
Hilt modules/singletons, `BuildConfig`, `R`/`R$*`, the `model.ui.strings.translations.*`
package (static localization data), and the `di.*` package (Hilt wiring).

## CI And Security Checks

- `ci.yml` runs Android unit tests (`testDebugUnitTest`) and debug build (`assembleDebug`).
- `ci.yml` runs Kover (`koverXmlReportDebug`, `koverHtmlReportDebug`, `koverVerifyDebug`) **non-blocking**
  (`continue-on-error: true`) and uploads `android-coverage` artifact (HTML + XML).
  Flip to blocking by removing `continue-on-error: true` from the Kover step in `.github/workflows/ci.yml`.
- `ci.yml` backend job runs `npm ci`, `npm run lint`, `npm run build`, and `npm run test:coverage`.
- `codeql.yml` runs scheduled + on-demand analysis for `java-kotlin` and `javascript-typescript`.

## High-Value Regression Suites

- Android guard/invariant suites: `UiTextAlignmentTest`, `AccountDeletionGuardTest`, `PrimaryLanguageCooldownTest`, `UsernameCooldownTest`, `FavoriteLimitTest`, `NavigationBarInsetsTest`, `LanguageValidationTest`, `EnsurePublicProfileExistsUseCaseTest` (visibility-persistence merge-safety).
- Translation resilience coverage includes `ErrorHandlingTest` assertions for `resource-exhausted` / rate-limit message mapping (content-translation only; UI language translations are now fully hardcoded — no API calls).
- Android flow/integration suites: `FriendSystemIntegrationTest`, `FriendsFlowIntegrationTest`, `CoinAndGenerationIntegrationTest`, `QuizFlowIntegrationTest`, `DataLayerIntegrationTest`, `CrossLayerIntegrationTest`.
- Backend contract/security suites: `translation.test.ts`, `learning.test.ts`, `coins.test.ts`, `account-deletion.test.ts`, `app-check-wrapper.test.ts`, `notifications.test.ts` (includes friend-request, chat, and feedback rate-limit enforcement), `maintenance.test.ts`, `firestore-rules-settings.test.ts`.

## Known Low-Risk Gaps

| File | Reason |
|---|---|
| `app/src/main/java/com/translator/TalknLearn/data/repositories/AzureSpeechRepository.kt` | Hardware-dependent speech stack |
| `app/src/main/java/com/translator/TalknLearn/model/ui/AppLanguageState.kt` | Simple data holder |

## Notes

- Counts above are generated from the current workspace and latest executed test artifacts.
- Prefer this file as the source of truth for test/coverage metrics; update it when test counts or thresholds change.

## Performance Benchmarks (Macrobenchmark)

The `:macrobenchmark` Gradle module (item 14 of `docs/APP_SUGGESTIONS.md`) provides a baseline cold/warm startup measurement for the launcher activity (`com.translator.TalknLearn`).

- Source: `macrobenchmark/src/main/java/com/translator/TalknLearn/macrobenchmark/StartupBenchmark.kt`
- Target build type: `:app:benchmark` (release-equivalent, debug-signed, `<profileable shell="true"/>` overlay in `app/src/benchmark/AndroidManifest.xml`)
- Local run: `./gradlew :macrobenchmark:connectedBenchmarkAndroidTest` (requires a connected device or emulator on API 29+)
- CI: `:macrobenchmark:assemble` runs in the `build-debug-apk` job as a non-blocking step. CI cannot execute the benchmarks themselves because GitHub Actions ubuntu-latest runners do not provide a device.

