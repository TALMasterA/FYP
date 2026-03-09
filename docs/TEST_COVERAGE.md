# Test Coverage Report

_Last updated: 2026-03-09_

## Summary

| Metric                    | Count   |
|---------------------------|---------|
| Source files               | 250     |
| Test files                 | 156     |
| Total `@Test` methods      | 1,860   |
| Key logic files            | ~137    |
| Key logic files tested     | ~114    |
| Key logic coverage         | ~83%    |

## Coverage by Layer

| Layer                     | Key Files | Tested | Coverage |
|---------------------------|-----------|--------|----------|
| **screens/ (ViewModels)** | 19        | 19     | 100%     |
| **domain/ (Use Cases)**   | 40        | 40     | 100%     |
| **model/ (Data Models)**  | 23        | 20     | 87%      |
| **core/ (Utilities)**     | 12        | 10     | 83%      |
| **navigation/**           | 2         | 2      | 100%     |
| **ui/ + utils/**          | 6         | 6      | 100%     |
| **data/ (Repositories)**  | 34        | 16     | 47%      |

## What Is Tested (156 files, 1,860 tests)

### All ViewModels & Controllers (19/19)
- AppViewModel (16), AuthViewModel (15), ChatViewModel (17)
- FavoritesViewModel (13), FeedbackViewModel (8), FriendsViewModel (29)
- HistoryViewModel (21), LearningViewModel (14), LearningSheetViewModel (19)
- MyProfileViewModel (13), ProfileViewModel (11), SettingsViewModel (21)
- SharedInboxViewModel (11), SharedMaterialDetailViewModel (9), ShopViewModel (13)
- SpeechViewModel (13), WordBankViewModel (15), CustomWordsViewModel (19)
- ContinuousConversationController (14), TtsController (12)

### All Domain Use Cases (40/40)
- Friends: 20 use cases — individual + integration tests (51 extra)
- Learning: CoinEligibility (19), GenerationEligibility (24), GenerateLearningMaterials (3), GenerateQuiz (3), ParseAndStoreQuiz (13)
- Settings: 9 use cases via SettingsUseCasesTest (20)
- Speech: 7 use cases tested individually
- History: all use cases
- OCR: RecognizeTextFromImage (4)

### Key Integration / Rule Tests
- QuizCoinEarningRules (10), QuizFlowIntegration (13), CoinAndGenerationIntegration (7)
- LearningGenerationRules (25), FriendSystemRules (29+11+11)
- LanguageValidation (18), NavigationAccess (18), ErrorHandling (25)

### Models (20/23)
- TranslationRecord (18), HistorySession (21), Quiz (18), FavoriteRecord (24)
- ValueTypes (21), SpeechResult (6), OcrResult (8), CustomWord (7)
- FriendMessage (14), FriendRelation (9), FriendRequest (6), PublicUserProfile (5)
- SharedItem (15), UserSettings (55), ColorPalette (17)

### Core Utilities
- SecurityUtils (40+9), ErrorMessages (35), NetworkRetry (21)
- ExtensionFunctions (16), Constants (13), FontSizeUtils (13)
- PerformanceUtils (10), AuditLogger (9), ViewModelHelpers (9), Pagination (9)

### Data Layer (16/34 key files)
- CloudGenAiClient (11), CloudQuizClient (12), AzureVoiceConfig (18)
- ContentCleaner (17), QuizParser (11), QuizGenerationRepositoryImpl (20)
- CacheInterceptor (10), FirebaseTranslationRepository (8)
- FriendsCache (32), SeenItemsStorage (21), SharedFriendsDataSource (31)
- FirestoreHistoryRepositoryLogic (25), FirestoreFeedbackRepository (23)
- FirestoreChatRepository (5), LanguageDetectionCache (7), TranslationCache (8)
- WordBankCacheData (7), DatabaseUtils (2)

### UI Text System
- UiTextAlignment (4), UiTextCompleteness (8), UiTextHelpers (17)
- TranslationCompleteness (6): verifies Cantonese + ZhTw maps have all keys

## Known Gaps

### Firestore/Firebase Repository Implementations (14 files)
These files depend directly on Firebase SDK, making pure unit testing difficult
without an emulator or integration test setup:

| File | Notes |
|------|-------|
| `FirestoreFriendsRepository.kt` | Heavy Firestore usage; pure logic extracted to FriendsCache |
| `FirestoreSharingRepository.kt` | Firestore CRUD operations |
| `FirestoreLearningSheetsRepository.kt` | Document read/write with caching |
| `FirestoreQuizRepository.kt` | Quiz persistence and stats |
| `FirestoreUserSettingsRepository.kt` | Settings persistence via set-merge |
| `FirebaseAuthRepository.kt` | Firebase Auth wrapper |
| `FirestoreFavoritesRepository.kt` | Favorites CRUD |
| `FirestoreProfileRepository.kt` | Profile + account deletion |
| `FirestoreCustomWordsRepository.kt` | Custom words CRUD |
| `FirestoreWordBankRepository.kt` | Word bank persistence |
| `WordBankGenerationRepository.kt` | Word bank generation logic |
| `LearningContentRepositoryImpl.kt` | Learning content assembly |
| `AzureSpeechRepository.kt` | Android hardware-dependent |
| `MLKitOcrRepository.kt` | Android ML Kit-dependent |

**Mitigation:** Pure logic is extracted into testable classes (FriendsCache,
ContentCleaner, QuizParser, CoinEligibility, GenerationEligibility, etc.) and
thoroughly tested. The untested Firestore layers are mostly thin document
read/write wrappers.

### Cloud/Network Clients (4 files)
- `CloudSpeechTokenClient.kt` — token management
- `CloudTranslatorClient.kt` — translation API client
- `AzureLanguageConfig.kt` — language config mapping
- `NetworkMonitor.kt` — connectivity observer

### Minor Gaps (low risk)
- `FeatureFlags.kt` — simple boolean flags
- `CertificatePinning.kt` — pinning configuration
- `SecureStorage.kt` — Android Keystore wrapper
- `AuthState.kt`, `User.kt`, `UserProfile.kt` — simple data classes

## Guard Tests

These tests prevent regressions in critical invariants:

| Test | What it guards |
|------|---------------|
| `UiTextAlignmentTest` | UiTextKey enum count == BaseUiTexts list count |
| `TranslationCompletenessTest` | Cantonese + ZhTw maps contain all UiTextKey entries |
| `AccountDeletionGuardTest` | All 16 subcollections are listed for cleanup |
| `PrimaryLanguageCooldownTest` | 30-day cooldown arithmetic |
| `FavoriteLimitTest` | 20-record favorites cap enforcement |
