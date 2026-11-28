# TalknLearn test coverage

Measured on 2026-11-28 HKT. Baseline `13d37d3d4c84590fc56429782d159ec890f92df6`. Branch code revision `429598d3fdf22ab48d3722662345ed6393ec7064`.

## Results

| Check | Baseline | Branch |
|---|---:|---:|
| Android JVM suites | 196 | 196 |
| Android JVM tests | 2,484 | 2,484 |
| Android Kover lines | 82.96% | 82.93% |
| Android Kover instructions | 80.11% | 80.07% |
| Android Kover branches | 45.60% | 45.60% |
| Android Kover methods | 60.72% | 60.72% |
| Android Kover classes | 71.43% | 71.43% |
| Backend Jest suites | 17 | 18 |
| Backend Jest tests | 195 | 213 |
| Backend statements | 94.00% | 94.82% |
| Backend branches | 79.68% | 81.06% |
| Backend functions | 93.40% | 94.73% |
| Backend lines | 94.85% | 95.61% |
| Firestore emulator | No executed suite | 5 passed |

The six additional Android measured lines came from request ID handling. Covered lines stayed at 13,643 and covered branches at 1,393. Detekt, the mock-config debug APK build and macrobenchmark assembly passed on both revisions. The app has 269 tracked production Kotlin files and 197 JVM test files, including the shared `FakePersistentRateLimiter.kt` test double.

## Reproduce

In a clean checkout with JDK 17, SDK Platform 36 and the checked-in Gradle wrapper, run from the repository root:

```powershell
Copy-Item ci/google-services-mock.json app/google-services.json
.\gradlew.bat testDebugUnitTest :app:koverXmlReportDebug :app:koverHtmlReportDebug :app:koverVerifyDebug :app:detekt assembleDebug :macrobenchmark:assemble
```

Under `fyp-backend/functions` with Node 24:

```text
npm ci
npm run lint
npm run build
npm run test:coverage -- --runInBand
```

With the same backend build, JDK 21 and Firebase CLI 15.36.0, run from `fyp-backend`:

```text
firebase emulators:exec --only firestore --project demo-talknlearn "npm --prefix functions run test:emulator"
```

JUnit XML is in `app/build/test-results/testDebugUnitTest`. Kover emits `app/build/reports/kover/reportDebug.xml` and `htmlDebug/index.html`. Jest emits `fyp-backend/functions/coverage/coverage-summary.json` and LCOV. The Android line floor remains 35%. CI uploads Kover and treats that step as non-blocking. The backend CI run requires 90% statements, 78% branches, 90% functions and 90% lines, plus a separate passing Firestore emulator step.

## Coverage boundaries

JVM tests do not exercise emulator hardware, signed Google Sign-In, Play Integrity or live providers. The Firestore emulator exercises owner access, denied cross-user access, invalid settings, denied access to server quota state and concurrent quota transactions using synthetic data. Provider dispatch tests use mocked responses. The Android instrumented login smoke source and macrobenchmark compilation are distinct from JVM and Firestore tests.

Kover excludes selected Hilt/Dagger/KSP classes, localization tables, DI modules, `BuildConfig`, resources, and functions or classes matched by the Compose, Hilt module and singleton annotations. Some generated Hilt Java files still appear in the XML. `CloudGenAiClient` is annotated with `Singleton` and therefore outside the measured report. Android device execution is needed for the direct Azure Speech SDK, Android permissions and audible output.

## Backend files with partial coverage

No production backend file has zero lines. Each Jest run reports the exact uncovered lines and branches in LCOV.

| Production file | Lines | Branches |
|---|---:|---:|
| `accountDeletion.ts` | 91.17% | 53.33% |
| `coins.ts` | 98.29% | 82.05% |
| `health.ts` | 100% | 83.33% |
| `helpers.ts` | 87.69% | 78.64% |
| `learning.ts` | 99.00% | 83.23% |
| `maintenance.ts` | 100% | 82.75% |
| `notifications.ts` | 92.96% | 74.28% |
| `providerAllowance.ts` | 100% | 94.36% |
| `translation.ts` | 96.15% | 84.55% |

The new tests target exhausted or missing allowances, malformed inputs, duplicate retries, provider failures, UTC month rollover, cross-account contention, bounded notification reads and nested account-deletion cleanup. Remaining branch gaps include account-deletion failure paths, notification outcomes and provider error variants.

## Android files with zero or partial measured lines

Both revisions have 240 source-file entries with line counters, including generated Java. Both have 80 entries at zero and 54 at partial coverage. The following lists cover tracked Kotlin files present in the branch XML.


**Zero.** `AiModelAttribution.kt`, `AppLanguageState.kt`, `AppNavigation.kt`, `AzureSpeechProvider.kt`, `AzureSpeechRepository.kt`, `ChatScreen.kt`, `Color.kt`, `CustomWordBankView.kt`, `Dimens.kt`, `FirestoreBlockHelper.kt`, `FirestoreFriendListHelper.kt`, `FirestoreFriendRequestHelper.kt`, `FirestoreProfileHelper.kt`, `FirestoreQuizRepository.kt`, `FriendsChatGraph.kt`, `FYPApplication.kt`, `HapticFeedback.kt`, `HistoryRepository.kt`, `ImageCaptureComponents.kt`, `LearningContentRepositoryImpl.kt`, `LearningWordBankGraph.kt`, `LoadingSkeletons.kt`, `LocalAppLanguage.kt`, `MainActivity.kt`, `MainFeatureGraph.kt`, `MyProfileScreen.kt`, `NavigationHelpers.kt`, `SettingsProfileGraph.kt`, `SharedInboxScreen.kt`, `StartupAuthGraph.kt`, `StartupScreen.kt`, `Type.kt`, `UiLanguageCacheStore.kt`, `UiLanguageStateController.kt`, `WordBankDetailView.kt`, `WordBankGenerationRepository.kt`.

**Partial.** `AppViewModel.kt`, `AuditLogger.kt`, `AzureLanguageConfig.kt`, `BlockUserDelegate.kt`, `ChatRepository.kt`, `ChatViewModel.kt`, `CloudSpeechTokenClient.kt`, `CloudTranslatorClient.kt`, `CommonUi.kt`, `ContinuousConversationController.kt`, `CustomWordsViewModel.kt`, `ErrorMessageMapper.kt`, `FavoritesViewModel.kt`, `FcmNotificationService.kt`, `FeedbackViewModel.kt`, `FirebaseTranslationRepository.kt`, `FirestoreLearningSheetsRepository.kt`, `FirestoreUserSettingsRepository.kt`, `FontSizeUtils.kt`, `FriendListDelegate.kt`, `FriendRequestDelegate.kt`, `FriendSearchDelegate.kt`, `FriendsRepository.kt`, `FriendsViewModel.kt`, `HistoryViewModel.kt`, `LanguageDetectionCache.kt`, `LearningSheetsRepository.kt`, `LearningSheetViewModel.kt`, `LearningViewModel.kt`, `Logger.kt`, `LoginScreen.kt`, `NetworkRetry.kt`, `OnboardingScreen.kt`, `ParseAndStoreQuizUseCase.kt`, `PerformanceTracer.kt`, `ProfileViewModel.kt`, `Quiz.kt`, `QuizGenerationRepositoryImpl.kt`, `QuizParser.kt`, `QuizRepository.kt`, `SecurityUtils.kt`, `SettingsViewModel.kt`, `SharedInboxViewModel.kt`, `ShopViewModel.kt`, `SpeechRepository.kt`, `SpeechViewModel.kt`, `TranslationCache.kt`, `UserSettings.kt`, `ValueTypes.kt`, `WordBankCacheDataStore.kt`, `WordBankGenerationDelegate.kt`, `WordBankShareDelegate.kt`, `WordBankSpeechDelegate.kt`, `WordBankViewModel.kt`.
