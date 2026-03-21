# Test Coverage Report

_Last updated: 2026-03-21_

## Summary

| Metric                    | Count   |
|---------------------------|---------|
| Source files               | 251     |
| Test files                 | 205     |
| Total `@Test` methods      | 2,655   |
| Key logic files            | ~140    |
| Key logic files tested     | ~140    |
| Key logic coverage         | ~100%   |

## CI/CD Pipeline & Quality Checks

The project maintained by GitHub Actions workflows:

1.  **CI (`ci.yml`)**:
    *   Triggers on `push` and `pull_request` to `main`, and manual `workflow_dispatch`.
    *   **Android Unit Tests**: Sets up JDK 17, caches Gradle, injects `google-services.json` from secrets, runs unit tests (`testDebugUnitTest`).
    *   **Debug APK Build**: After unit tests pass, builds and uploads the debug APK as a CI artifact (`assembleDebug`).
    *   **Backend**: Sets up Node.js 24, installs dependencies, runs linting (ESLint), compiles TypeScript (`build`), and runs Jest coverage (`npm run test:coverage`).
    *   **Backend Coverage Artifact**: Uploads `fyp-backend/functions/coverage` for each successful backend CI run.

2.  **CodeQL (`codeql.yml`)**:
    *   Runs detailed semantic code analysis for Java/Kotlin (Android) and JavaScript/TypeScript (Backend) on a weekly schedule and on push/PRs.
    *   Uses a dummy Firebase config for compilation during analysis to ensure build stability without exposing secrets.

## Coverage by Layer

| Layer                     | Key Files | Tested | Coverage |
|---------------------------|-----------|--------|----------|
| **screens/ (ViewModels)** | 20        | 20     | 100%     |
| **domain/ (Use Cases)**   | 40        | 40     | 100%     |
| **model/ (Data Models)**  | 24        | 24     | 100%     |
| **core/ (Utilities)**     | 12        | 12     | 100%     |
| **navigation/**           | 2         | 2      | 100%     |
| **ui/ + utils/**          | 6         | 6      | 100%     |
| **data/ (Repositories)**  | 35        | 35     | 100%     |

## What Is Tested (205 files, 2,655 tests)

### All ViewModels & Controllers (21/21)
- AppViewModel (16), AuthViewModel (15), ChatViewModel (17)
- FavoritesViewModel (13), FeedbackViewModel (8), FriendsViewModel (30)
- HistoryViewModel (21), LearningViewModel (14), LearningSheetViewModel (19)
- MyProfileViewModel (14), ProfileViewModel (11), SettingsViewModel (22)
- SharedInboxViewModel (11), SharedMaterialDetailViewModel (9), ShopViewModel (13)
- SpeechViewModel (13), WordBankViewModel (15), CustomWordsViewModel (19)
- ContinuousConversationController (14), TtsController (12)
- **NEW:** LearningScreenLogicTest (18): cluster filtering, generation button state, quiz regeneration eligibility

### All Domain Use Cases (40/40)
- Friends: 20 use cases — individual + integration tests (51 extra)
  - **NEW:** UsernameEnforcementIntegrationTest (12): domain-layer username flow, profile creation, username preservation
  - **NEW:** UsernameRequirementIntegrationTest (11): ViewModel-layer username gate for send/accept/accept-all
- Learning: CoinEligibility (19), GenerationEligibility (24), GenerateLearningMaterials (3), GenerateQuiz (3), ParseAndStoreQuiz (13)
- Settings: 9 use cases via SettingsUseCasesTest (20)
- Speech: 7 use cases tested individually
- History: all use cases via HistoryUseCasesTest + dedicated files:
  - **NEW:** DeleteSessionUseCaseTest (4): delegation, different IDs, blank ID guards
  - **NEW:** ObserveUserHistoryUseCaseTest (3): flow delegation, empty list, per-user isolation
  - **NEW:** RenameSessionUseCaseTest (3): delegation, empty name, long name
- OCR: RecognizeTextFromImage (4)

### Key Integration / Rule Tests
- CrossLayerIntegration (18): language pipeline, cooldown symmetry, coin economy, notification defaults
- QuizCoinEarningRules (10), QuizFlowIntegration (13), CoinAndGenerationIntegration (7)
- LearningGenerationRules (25), FriendSystemRules (29+11+11)
- LanguageValidation (18), NavigationAccess (18), ErrorHandling (25)
- SettingsViewModel notification cache (ensures push/badge toggles persist to SharedPreferences for FCM gating)
- **NEW:** EligibilityEdgeCases (18): boundary values for generation/coin eligibility, constant verification
- **NEW:** SessionManagementLogic (13): session/user ID validation, delete preconditions, session naming

### Models (24/24)
- TranslationRecord (18), HistorySession (21), HistorySessionExtended (9), Quiz (18), FavoriteRecord (24)
- FavoriteSession (14): FavoriteSession and FavoriteSessionRecord defaults, copy, sorting
- ValueTypes (21), SpeechResult (6), OcrResult (8), CustomWord (7)
- FriendMessage (14), FriendRelation (9), FriendRequest (6), PublicUserProfile (5)
- SharedItem (15), UserSettings (55), ColorPalette (17)
- **NEW:** UserAndProfile (17): User/UserProfile data class defaults, AuthState sealed interface pattern matching
- **NEW:** SpeechModels (15): SpeechScreenState defaults, RecognizePhase enum, ChatMessage properties
- **NEW:** WordBankModels (18): WordBankItem, WordBank, WordBankUiState, SpeakingType enum
- **NEW:** LearningModels (7): LanguageClusterUi data class, sorting, equality
- **NEW:** QuizAttemptTest (20): QuizQuestion, QuizAnswer, QuizAttempt scoring, QuizStats, UserCoinStats
- **NEW:** OcrScriptTest (17): language-to-script mapping, script properties, size estimates

### Core Utilities (12/12)
- SecurityUtils (40+9), CertificatePinning (7), ErrorMessages (35), NetworkRetry (21)
- ExtensionFunctions (16), Constants (13), FontSizeUtils (13)
- PerformanceUtils (13), AuditLogger (9), ViewModelHelpers (9), Pagination (9)
- **NEW:** FeatureFlagDefaults (16): default flag values, key constants, flag type verification

### Data Layer (35/35 key files)
- CloudGenAiClient (11), CloudQuizClient (12), AzureVoiceConfig (18)
- ContentCleaner (17), QuizParser (11), QuizGenerationRepositoryImpl (20)
- CacheInterceptor (10), FirebaseTranslationRepository (8)
- FriendsCache (32), SeenItemsStorage (21), SharedFriendsDataSource (31), FriendRequestRateLimiter (3)
- FirestoreHistoryRepositoryLogic (25), FirestoreFeedbackRepository (23)
- FirestoreChatRepository (5), LanguageDetectionCache (7), TranslationCache (8)
- WordBankCacheData (7), DatabaseUtils (2), DataCleanupUtils (tests via DataCleanupUtilsTest)
- **NEW:** SharedHistoryDataSourceExtendedLogic (17): updateLimit restart, bidirectional pair counting, language filtering
- **NEW:** SharedSettingsDataSourceLogic (15): startObserving idempotency, stopObserving reset, updateCache semantics
- **NEW:** LanguageDisplayNamesExtended (14): full 17-language mapping, Chinese variants, isSupportedLanguage comprehensive
- **NEW:** SettingsNotificationFields (21): notification field allowlist, history limit clamping, font-size clamping, parse defaults
- **NEW:** WordBankRepositoryLogic (16): key normalization, metadata, word parsing, duplicate filtering
- **NEW:** WordBankPrompt (12): prompt construction, takeLast(30) limit, language embedding
- **NEW:** LearningContentPrompt (14): prompt construction, takeLast(20) limit, MAX_VOCABULARY_ITEMS
- **NEW:** OcrRecognizerSelection (24): language-to-recognizer mapping for all 17 languages
- **NEW:** UiLanguageCorrection (15): language name correction algorithm, LanguageNameTranslations integrity
- **NEW:** ChatRepositoryLogic (27): generateChatId ordering, isParticipant, message validation, unread math
- **NEW:** FriendsRepositoryLogic (21): note sanitization, expiry filter, username sync freshness
- **NEW:** SharingRepositoryLogic (21): type validation, content stripping, word field mapping
- **NEW:** QuizRepositoryLogic (25): running average, lowestScore init, coin debounce, deductCoins
- **NEW:** LearningSheetsLogic (23): norm, docId, empty targets, fill-missing, batch metadata
- **NEW:** CustomWordsValidation (22): input validation, trimming, truncation, blank checks
- **NEW:** CloudClientLogic (15): request building, response parsing, deployment validation
- **NEW:** DataLayerIntegration (19): cross-repository invariants, chatId↔friends consistency
- **NEW:** FriendRequestRateLimiterTest (3): persisted hourly send limit, expiry pruning, restart-safe quota checks

### UI Text System
- UiTextAlignment (4), UiTextCompleteness (8), UiTextHelpers (17)
- TranslationCompleteness (6): verifies Cantonese + ZhTw maps have all keys

## Known Gaps

### Firestore/Firebase Repository Implementations (1 file)
These files are thin Firestore CRUD wrappers with no extractable pure logic.
All complex business logic has been extracted to testable classes:

| File | Notes |
|------|-------|
| `AzureSpeechRepository.kt` | Android hardware-dependent |

**Previously untested logic now covered:**
- `FirestoreFriendsRepository` — Pure logic extracted to FriendsCache (32 tests) + FriendsRepositoryLogicTest (21 tests)
- `FirestoreSharingRepository` — type validation, content stripping → SharingRepositoryLogicTest (21 tests)
- `FirestoreLearningSheetsRepository` — norm, docId, batch metadata → LearningSheetsLogicTest (23 tests)
- `FirestoreQuizRepository` — running average, coin debounce → QuizRepositoryLogicTest (25 tests)
- `FirestoreChatRepository` — chatId generation, unread math → ChatRepositoryLogicTest (27 tests)
- `FirebaseAuthRepository` — covered via AuthViewModel tests
- `FirestoreFavoritesRepository` — covered via FavoritesViewModel tests
- `FirestoreCustomWordsRepository` — validation logic → CustomWordsValidationTest (22 tests)
- `FirestoreUserSettingsRepository` — notification field allowlist, history limit clamping, font-size clamping, parse defaults → `SettingsNotificationFieldsTest` (21 tests)
- `FirestoreWordBankRepository` — key normalization, word parsing, duplicate filtering → `WordBankRepositoryLogicTest` (16 tests)
- `WordBankGenerationRepository` — prompt construction → `WordBankPromptTest` (12 tests)
- `LearningContentRepositoryImpl` — prompt construction → `LearningContentPromptTest` (14 tests)
- `MLKitOcrRepository` — recognizer selection logic → `OcrRecognizerSelectionTest` (24 tests)
- `UiLanguageStateController` — language correction logic → `UiLanguageCorrectionTest` (15 tests)

### Cloud/Network Clients (1 file)
- `NetworkMonitor.kt` — connectivity observer (Android ConnectivityManager)

**Previously untested clients now covered:**
- `CloudTranslatorClient.kt` — request building, response parsing → `CloudClientLogicTest` (15 tests) + `CloudTranslatorLogicTest`
- `CloudSpeechTokenClient.kt` — client logic partially covered by `CloudClientLogicTest`

### Minor Gaps (low risk)
- `SecureStorage.kt` — Android Keystore wrapper
- `AppLanguageState.kt` — trivial data class (2 fields, no methods)

## Guard Tests

These tests prevent regressions in critical invariants:

| Test | What it guards |
|------|---------------|
| `UiTextAlignmentTest` | UiTextKey enum count == BaseUiTexts list count |
| `TranslationCompletenessTest` | Cantonese + ZhTw maps contain all UiTextKey entries |
| `AccountDeletionGuardTest` | All 17 subcollections are listed for cleanup |
| `PrimaryLanguageCooldownTest` | 30-day cooldown arithmetic |
| `UsernameCooldownTest` | 30-day username cooldown arithmetic |
| `FavoriteLimitTest` | 20-record favorites cap enforcement |
| `CrossLayerIntegrationTest` | Cooldown symmetry, coin economy balance, notification defaults |
| `UsernameEnforcementIntegrationTest` | Domain layer does NOT enforce username; profile creation uses empty username |
| `UsernameRequirementIntegrationTest` | ViewModel gate blocks send/accept/accept-all without username |
| `DataLayerIntegrationTest` | Cross-repository invariants: chatId↔friends, sharing↔friends consistency |

---

## Backend Tests (Firebase Cloud Functions)

_9 test files, 122 tests_

| File | Tests | What it covers |
|------|-------|----------------|
| `helpers.test.ts` | 29 | `requireAuth`, `requireString`, `optionalString`, `safeParseJson`, `toTranslatorCode`, `buildTranslateUrl`, `validateGenAiConfig` |
| `logger.test.ts` | 6 | Structured JSON logger output format |
| `translation.test.ts` | 15 | `getSpeechToken`, `translateText`, `translateTexts`, `detectLanguage` — auth guards, error paths, success paths |
| `coins.test.ts` | 26 | `awardQuizCoins` (anti-cheat rules, strict language-code validation, version match, increment check), `spendCoins` (history expansion, palette unlock) |
| `notifications.test.ts` | 12 | FCM triggers: missing data, status guards, spam detection (link flooding), friend request rate limiting |
| `health.test.ts` | 3 | `healthcheck` readiness endpoint (200 valid config, 500 invalid config, 405 method guard) |
| `rate-limit.test.ts` | 5 | `enforceRateLimit` fail-closed behavior for read/write failures and malformed stored payload |
| `learning.test.ts` | 17 | `generateLearningContent` callable: auth/input validation, rate-limit gate ordering, Azure OpenAI error mapping, response parsing |
| `maintenance.test.ts` | 9 | Scheduled maintenance handlers: stale token/rate-limit pruning and friend data repair batch behavior |

### Backend Coverage Gate (CI-enforced)

- Command: `npm run test:coverage`
- Enforced in `fyp-backend/functions/jest.config.js` via `coverageThreshold.global`:
  - statements: 50
  - branches: 45
  - functions: 50
  - lines: 50

### Latest Backend Coverage Baseline

- Statements: 72.03%
- Branches: 59.68%
- Functions: 63.93%
- Lines: 73.47%
