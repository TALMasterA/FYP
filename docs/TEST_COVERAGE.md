# Test Coverage Report

_Last updated: 2026-03-27_

## Summary

| Metric                    | Count   |
|---------------------------|---------|
| Source files               | 251     |
| Test files                 | 210     |
| Total `@Test` methods      | 2,788   |
| Key logic coverage         | ~100%   |

## CI/CD Pipeline & Quality Checks

**GitHub Actions workflows:**

1. **CI (`ci.yml`)**: Triggered on push/PR/dispatch. Runs Android unit tests (`testDebugUnitTest`), builds debug APK (`assembleDebug`), lints & tests backend (`npm run test:coverage`). Uploads debug APK and coverage artifacts.

2. **CodeQL (`codeql.yml`)**: Weekly + on-demand semantic analysis for Java/Kotlin/TypeScript to detect security vulnerabilities.

## Coverage by Layer

| Layer                     | Files | Coverage |
|---------------------------|-------|----------|
| **Screens / ViewModels**  | 21    | 100%     |
| **Domain / Use Cases**    | 40    | 100%     |
| **Models**                | 24    | 100%     |
| **Core / Utilities**      | 12    | 100%     |
| **Data / Repositories**   | 35    | 100%     |
| **Navigation / UI**       | 8     | 100%     |

---

## What Is Tested

### ViewModels & Controllers (21/21)
All 21 ViewModels and controllers fully tested: AppViewModel, AuthViewModel, ChatViewModel, FavoritesViewModel, FeedbackViewModel, FriendsViewModel, HistoryViewModel, LearningViewModel, LearningSheetViewModel, MyProfileViewModel, ProfileViewModel, SettingsViewModel, SharedInboxViewModel, SharedMaterialDetailViewModel, ShopViewModel, SpeechViewModel, WordBankViewModel, CustomWordsViewModel, ContinuousConversationController, TtsController.

Also includes critical controller tests: `LearningScreenLogicTest` (18 tests — cluster filtering, generation eligibility, quiz regeneration).

### Domain Use Cases (40/40)
**Friends (20 use cases):** Send request, accept, reject, cancel, remove, chat, share word/material. Integration tests verify friend system rules (51 extra tests). **NEW:** `UsernameEnforcementIntegrationTest` (12 tests) and `UsernameRequirementIntegrationTest` (11 tests) verify domain-layer vs ViewModel-layer username enforcement split.

**Learning:** Coin eligibility (19), generation eligibility (24), learning material generation (3), quiz generation (3), quiz parsing (13). Integration: coin economy (7), quiz flow (13), generation rules (25).

**Settings (9 use cases):** Covered via `SettingsUseCasesTest` (20 tests).

**History:** All use cases via `HistoryUseCasesTest` + dedicated files for delete session, observe history, rename session (12 tests total).

**Speech (7 use cases):** Individual unit tests.

**OCR / Auth:** Covered individually.

### Models (24/24)
TranslationRecord, HistorySession, HistorySessionExtended, Quiz, FavoriteRecord, FavoriteSession, ValueTypes, SpeechResult, OcrResult, CustomWord, FriendMessage, FriendRelation, FriendRequest, PublicUserProfile, SharedItem, UserSettings, ColorPalette, UserAndProfile, SpeechModels, WordBankModels, LearningModels, QuizAttemptTest, OcrScriptTest.

### Core Utilities (12/12)
SecurityUtils (40+9 tests), CertificatePinning (7), ErrorMessages (35), NetworkRetry (21), ExtensionFunctions (16), Constants (13), FontSizeUtils (13), PerformanceUtils (13), AuditLogger (9), ViewModelHelpers (9), Pagination (9), FeatureFlagDefaults (16).

### Data Layer (35/35 key files)
Cloud clients: CloudGenAiClient (11), CloudQuizClient (12), AzureVoiceConfig (18).
Repositories: FirebaseTranslationRepository (8), FirestoreFeedbackRepository (23), FirestoreChatRepository (27), FirestoreHistoryRepository (25).
Caches: FriendsCache (32), TranslationCache (8), LanguageDetectionCache (7), WordBankCacheData (7).
Shared data sources: SharedFriendsDataSource (31), SharedHistoryDataSource (17 extended), SharedSettingsDataSource (15).
Utilities: DatabaseUtils (2), DataCleanupUtils (tracked), ContentCleaner (17), QuizParser (11).

**Logic extraction:** Complex business logic is extracted from thin Firestore wrappers into testable classes (e.g., FriendsCache extracts 32 tests' worth of logic; ChatRepository logic tests cover 27 tests).

### Integration & Rule Tests
- **CrossLayerIntegrationTest** (18): Language pipeline, cooldown symmetry, coin economy, notification defaults
- **DataLayerIntegrationTest** (19): Cross-repository invariants (chatId↔friends, sharing↔friends consistency)
- **FriendSystemRules** (29+11+11): Friend request, chat, sharing, blocking rules
- **CoinAndGenerationIntegration** (7): Coin-generation rule interaction
- **LanguageValidation** (18): Language code validation across layers
- **UiTextAlignmentTest** (4): Critical guard preventing enum/list misalignment crashes
- **TranslationCompletenessTest** (6): Cantonese + ZhTw map completeness
- **SettingsViewModelNotificationCacheTest** (1): Ensures push toggles sync to FCM cache

### Guard Tests (Regression Prevention)

| Test | Guards |
|------|--------|
| `UiTextAlignmentTest` | UiTextKey enum count == BaseUiTexts list count |
| `TranslationCompletenessTest` | All languages have complete translation maps |
| `AccountDeletionGuardTest` | All 17 subcollections listed for cleanup |
| `PrimaryLanguageCooldownTest` | 30-day cooldown arithmetic |
| `UsernameCooldownTest` | Username 30-day cooldown arithmetic |
| `FavoriteLimitTest` | 20-record favorites cap |
| `UsernameEnforcementIntegrationTest` | Domain layer does NOT enforce username |
| `UsernameRequirementIntegrationTest` | ViewModel gate blocks send/accept without username |
| `DataLayerIntegrationTest` | Cross-repository consistency invariants |

---

## Known Gaps (Low Risk)

| File | Reason |
|------|--------|
| `AzureSpeechRepository.kt` | Android hardware-dependent |
| `SecureStorage.kt` | Android Keystore wrapper (internal API) |
| `AppLanguageState.kt` | Trivial data class (2 fields, no methods) |
| `NetworkMonitor.kt` | ConnectivityManager observer (system API) |

All complex business logic has been extracted to testable classes (see "Logic extraction" section above).

---

## Backend Tests (Firebase Cloud Functions)

_14 test files, 172 tests_

| File | Tests | Coverage |
|------|-------|----------|
| `helpers.test.ts` | 31 | Validation, auth guards, rate limiting, URL building |
| `logger.test.ts` | 6 | Structured JSON logger output format |
| `translation.test.ts` | 24 | getSpeechToken, translateText(s), detectLanguage, legacy-code compatibility, 429 mapping |
| `coins.test.ts` | 26 | awardQuizCoins anti-cheat, spendCoins purchases |
| `notifications.test.ts` | 12 | FCM triggers, spam detection, rate limiting |
| `learning.test.ts` | 17 | generateLearningContent: auth, validation, rate-limit ordering |
| `maintenance.test.ts` | 9 | Scheduled cleanup: stale tokens, rate-limits, friends repair |
| `health.test.ts` | 3 | Readiness endpoint validation |
| `rate-limit.test.ts` | 5 | Rate-limit enforcement, fail-closed behavior |
| `firestore-rules-settings.test.ts` | 6 | Guard tests for settings booleans, profile read-access, shared inbox write gating, and chat write authorization invariants in `firestore.rules` |

**Coverage Gate (CI-enforced):** `npm run test:coverage` with `coverageThreshold.global`: statements 50%, branches 45%, functions 50%, lines 50%.
**Current baseline:** Statements 99.1%, Branches 82.88%, Functions 100%, Lines 100%.

---

## Recent Test Additions

- `FavoritesViewModelTest`: Delete mode functions (toggleDeleteMode, exitDeleteMode, toggleSelection, deleteSelected) - 11 new tests
- `HistoryViewModelTest`: Pagination and TTS (loadMoreHistory, speakText, refreshCoinStats, checkIfFavorited) - 14 new tests
- `ChatViewModelTest`: Batch translation and pagination (translateAllMessages, loadOlderMessages) - 8 new tests
- `UsernameEnforcementIntegrationTest`: Domain-layer username enforcement split (12 tests)
- `UsernameRequirementIntegrationTest`: ViewModel-layer username gate (11 tests)
- `SessionManagementLogicTest`: Session ID/name validation, delete preconditions (13 tests)
- `ChatRepositoryLogicTest`: Chat ID generation, participant validation, unread math (27 tests)
- `FriendsRepositoryLogicTest`: Note sanitization, expiry filter, username sync (21 tests)
- `SharingRepositoryLogicTest`: shared-word accept translation uses receiver primary language when sender/receiver primaries differ (3 regression tests)
- `SharedHistoryDataSourceExtendedLogic`: Language filtering, bidirectional counting (17 tests)
- `WordBankRepositoryLogic`: Key normalization, word parsing, duplicates (16 tests)
- `QuizRepositoryLogic`: Running average, coin debounce, score init (25 tests)
- `CustomWordsValidation`: Input validation, trimming, blank checks (22 tests)
- `OcrRecognizerSelection`: Language-to-recognizer mapping (24 tests)
- `AppViewModelTest`: logout/login seen-state persistence guard, unread badge collector lifecycle guard (post-logout emissions remain reset), and user-switch collector de-duplication
- `SeenItemsStorageTest`: user-scoped clear guard for seen-state keys
- `FriendsViewModelBadgeSettingsTest`: badge gating remains disabled across logout/relogin
- `firestore-rules-settings.test.ts`: protects bool checks for notification/badge settings fields
- `SettingsViewModelTest`: fixed `autoThemeEnabled` contract mismatch and added logged-out notification preference guard test
- `SettingsViewModelTest`: added invalid notification field edge-case regression test (settings unchanged + cache write path asserted)
- `WordBankViewModelTest`: settings primary change auto-updates word-bank primary language source-of-truth
- `CustomWordsViewModelTest`: translateCustomWord source language follows account primary from settings
- `FirestoreUserSettingsRepositoryVoiceFieldPathTest`: guards voiceSettings nested-field updates (preserve existing languages), NOT_FOUND create fallback, and non-NOT_FOUND error propagation
- `LearningSheetScreenLogicTest`: regression guard that prefers fresh `sheetCountByLanguage` metadata so generated-count updates immediately after sheet generation
- `UiLanguageSwitchErrorMessageTest`: regression guard for UI-language switch error messaging (rate-limit explanation + generic fallback)
- `SharedFriendsDataSourceTest`: startup generation invalidation + pending startup-job cancellation guard for stop/start race safety
- `firestore-rules-settings.test.ts`: chat message + metadata write guards require mutual friendship/no-block parity with app-side checks

---
