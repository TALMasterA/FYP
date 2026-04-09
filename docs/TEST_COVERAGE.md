# Test Coverage Report

_Last updated: 2026-04-09_

## Verified Snapshot (from local audit)

| Metric | Count |
|---|---:|
| Android source files (`app/src/main/java/com/example/fyp`) | 252 |
| Backend source files (`fyp-backend/functions/src/*.ts`) | 9 |
| Android unit test files (`app/src/test/java/com/example/fyp`) | 212 |
| Android test suites in latest `testDebugUnitTest` run | 212 |
| Android unit tests executed (`testDebugUnitTest`) | 2,779 |
| Backend test files (`fyp-backend/functions/src/__tests__`) | 14 |
| Backend tests (`it`/`test` blocks) | 190 |

## Backend Coverage (from `coverage-summary.json`)

| Metric | Current |
|---|---:|
| Statements | 94.70% |
| Branches | 80.70% |
| Functions | 93.58% |
| Lines | 95.63% |

CI threshold in `fyp-backend/functions/jest.config.js`:
- statements >= 50%
- branches >= 45%
- functions >= 50%
- lines >= 50%

## CI/CD Pipeline Checks

- `ci.yml` runs Android unit tests (`testDebugUnitTest`) and debug build (`assembleDebug`).
- `ci.yml` backend job runs `npm ci`, `npm run lint`, `npm run build`, and `npm run test:coverage`.
- `codeql.yml` runs scheduled + on-demand analysis for `java-kotlin` and `javascript-typescript`.

## High-Value Regression Suites

- Android guard/invariant suites: `UiTextAlignmentTest`, `AccountDeletionGuardTest`, `PrimaryLanguageCooldownTest`, `UsernameCooldownTest`, `FavoriteLimitTest`, `NavigationBarInsetsTest`, `LanguageValidationTest`, `EnsurePublicProfileExistsUseCaseTest` (visibility-persistence merge-safety).
- Translation resilience coverage includes `ErrorHandlingTest` assertions for `resource-exhausted` / rate-limit message mapping.
- Android flow/integration suites: `FriendSystemIntegrationTest`, `FriendsFlowIntegrationTest`, `CoinAndGenerationIntegrationTest`, `QuizFlowIntegrationTest`, `DataLayerIntegrationTest`, `CrossLayerIntegrationTest`.
- Backend contract/security suites: `translation.test.ts`, `learning.test.ts`, `coins.test.ts`, `notifications.test.ts` (includes friend-request, chat, and feedback rate-limit enforcement), `maintenance.test.ts`, `firestore-rules-settings.test.ts`.

## Known Low-Risk Gaps

| File | Reason |
|---|---|
| `app/src/main/java/com/example/fyp/data/repositories/AzureSpeechRepository.kt` | Hardware-dependent speech stack |
| `app/src/main/java/com/example/fyp/core/security/SecureStorage.kt` | Android Keystore/system API wrapper |
| `app/src/main/java/com/example/fyp/model/ui/AppLanguageState.kt` | Simple data holder |
| `app/src/main/java/com/example/fyp/data/network/NetworkMonitor.kt` | Android connectivity system observer |

## Notes

- Counts above are generated from the current workspace and latest executed test artifacts.
- Prefer this file as the source of truth for test/coverage metrics; update it when test counts or thresholds change.
