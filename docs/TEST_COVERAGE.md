# Test Coverage Report

_Last updated: 2026-03-14_

## Summary

| Metric                    | Count   |
|---------------------------|---------|
| Source files               | 251     |
| Test files                 | 199     |
| Total `@Test` methods      | 2,619   |
| Key logic files            | ~137    |
| Key logic files tested     | ~137    |
| Key logic coverage         | ~100%   |

## Coverage by Layer

| Layer                     | Key Files | Tested | Coverage |
|---------------------------|-----------|--------|----------|
| **screens/ (ViewModels)** | 19        | 19     | 100%     |
| **domain/ (Use Cases)**   | 40        | 40     | 100%     |
| **model/ (Data Models)**  | 23        | 23     | 100%     |
| **core/ (Utilities)**     | 12        | 12     | 100%     |
| **navigation/**           | 2         | 2      | 100%     |
| **ui/ + utils/**          | 6         | 6      | 100%     |
| **data/ (Repositories)**  | 34        | 34     | 100%     |

## What Is Tested (199 files, 2,619 tests)

### All ViewModels & Controllers (19/19)
- AppViewModel (16), AuthViewModel (15), ChatViewModel (17)
- FavoritesViewModel (13), FeedbackViewModel (8), FriendsViewModel (30)
- HistoryViewModel (21), LearningViewModel (14), LearningSheetViewModel (19)
- MyProfileViewModel (13), ProfileViewModel (11), SettingsViewModel (21)
- SharedInboxViewModel (11), SharedMaterialDetailViewModel (9), ShopViewModel (13)
- SpeechViewModel (13), WordBankViewModel (15), CustomWordsViewModel (19)
- ContinuousConversationController (14), TtsController (12)

### All Domain Use Cases (40/40)
- Friends: 20 use cases — individual + integration tests (51 extra)
  - **NEW:** UsernameEnforcementIntegrationTest (12): domain-layer username flow, profile creation, username preservation
  - **NEW:** UsernameRequirementIntegrationTest (11): ViewModel-layer username gate for send/accept/accept-all
- Learning: CoinEligibility (19), GenerationEligibility (24), GenerateLearningMaterials (3), GenerateQuiz (3), ParseAndStoreQuiz (13)
- Settings: 9 use cases via SettingsUseCasesTest (20)
- Speech: 7 use cases tested individually
- History: all use cases
- OCR: RecognizeTextFromImage (4)

### Key Integration / Rule Tests
- CrossLayerIntegration (18): language pipeline, cooldown symmetry, coin economy, notification defaults
- QuizCoinEarningRules (10), QuizFlowIntegration (13), CoinAndGenerationIntegration (7)
- LearningGenerationRules (25), FriendSystemRules (29+11+11)
- LanguageValidation (18), NavigationAccess (18), ErrorHandling (25)
- **NEW:** EligibilityEdgeCases (18): boundary values for generation/coin eligibility, constant verification
- **NEW:** SessionManagementLogic (13): session/user ID validation, delete preconditions, session naming

### Models (23/23)
- TranslationRecord (18), HistorySession (21), HistorySessionExtended (9), Quiz (18), FavoriteRecord (24)
- FavoriteSession (14): FavoriteSession and FavoriteSessionRecord defaults, copy, sorting
- ValueTypes (21), SpeechResult (6), OcrResult (8), CustomWord (7)
- FriendMessage (14), FriendRelation (9), FriendRequest (6), PublicUserProfile (5)
- SharedItem (15), UserSettings (55), ColorPalette (17)
- **NEW:** UserAndProfile (17): User/UserProfile data class defaults, AuthState sealed interface pattern matching
- **NEW:** SpeechModels (15): SpeechScreenState defaults, RecognizePhase enum, ChatMessage properties
- **NEW:** WordBankModels (18): WordBankItem, WordBank, WordBankUiState, SpeakingType enum
- **NEW:** LearningModels (7): LanguageClusterUi data class, sorting, equality

### Core Utilities (12/12)
- SecurityUtils (40+9), CertificatePinning (7), ErrorMessages (35), NetworkRetry (21)
- ExtensionFunctions (16), Constants (13), FontSizeUtils (13)
- PerformanceUtils (13), AuditLogger (9), ViewModelHelpers (9), Pagination (9)
- **NEW:** FeatureFlagDefaults (16): default flag values, key constants, flag type verification

### Data Layer (34/34 key files)
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
- **NEW:** SettingsNotificationFields (18): notification field allowlist, history limit clamping, parse defaults
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
- `FirestoreUserSettingsRepository` — notification field allowlist, history limit clamping, parse defaults → `SettingsNotificationFieldsTest` (18 tests)
- `FirestoreWordBankRepository` — key normalization, word parsing, duplicate filtering → `WordBankRepositoryLogicTest` (16 tests)
- `WordBankGenerationRepository` — prompt construction → `WordBankPromptTest` (12 tests)
- `LearningContentRepositoryImpl` — prompt construction → `LearningContentPromptTest` (14 tests)
- `MLKitOcrRepository` — recognizer selection logic → `OcrRecognizerSelectionTest` (24 tests)
- `UiLanguageStateController` — language correction logic → `UiLanguageCorrectionTest` (15 tests)

### Cloud/Network Clients (3 files)
- `CloudSpeechTokenClient.kt` — token management
- `CloudTranslatorClient.kt` — translation API client
- `NetworkMonitor.kt` — connectivity observer (Android ConnectivityManager)

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
