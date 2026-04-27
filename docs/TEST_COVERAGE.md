# Test Coverage Report

_Last updated: 2026-04-27_

## Verified Snapshot (from local audit)

| Metric | Count |
|---|---:|
| Android source files (`app/src/main/java/com/translator/TalknLearn`) | 241 |
| Backend source files (`fyp-backend/functions/src/*.ts`) | 11 |
| Android unit test files (`app/src/test/java/com/translator/TalknLearn`) | 197 |
| Android test suites in latest `testDebugUnitTest` run | 196 |
| Android unit tests executed (`testDebugUnitTest`) | 2,486 |
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

## CI And Security Checks

- `ci.yml` runs Android unit tests (`testDebugUnitTest`) and debug build (`assembleDebug`).
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
