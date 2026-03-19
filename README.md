# FYP - Translation & Learning App

An Android-based translation and language learning application with AI-powered features.

Develop in Android Studio, Android ONLY.

--------------------------------------------------------------

## 📱 Try the App

Register to try this app!

Link: [Click here to register](https://appdistribution.firebase.dev/i/5ebf3d592700b0f7)

Please use your gmail to register.

--------------------------------------------------------------

## 🛠️ Tech Stack

**Frontend:**
- Android Studio
- Kotlin

**Backend:**
- Firebase Authentication (Email/Password)
- Firebase Firestore (Database)
- Firebase Cloud Functions (TypeScript backend API)
- Firebase Crashlytics & Performance Monitoring
- Azure Speech & Translation Services
- Azure OpenAI (learning content generation)

**Key Libraries:**
- Hilt (Dependency Injection)
- Kotlin Coroutines & Flow
- Kotlin Serialization
- OkHttp (Networking)
- Coil (Image loading)
- DataStore Preferences (Local caching)

--------------------------------------------------------------

## 🛠️ CI/CD & Quality Checks

This project uses **GitHub Actions** for continuous integration and static analysis.

### Workflows
1.  **CI (`ci.yml`)**:
    *   **Android Unit Tests**: Builds the debug APK and runs unit tests (`./gradlew testDebugUnitTest`).
    *   **Debug APK Build**: Triggered after unit tests pass.
    *   **Backend**: Lints, builds, and tests Cloud Functions.
    *   **Artifacts**: A fresh Debug APK is uploaded as an artifact on every successful Android unit test + build run.

2.  **CodeQL Analysis (`codeql.yml`)**:
    *   Performs semantic code analysis on Java/Kotlin and TypeScript files to detect security vulnerabilities and bugs.

### Status
*   ![CI](https://github.com/TALMasterA/FYP/actions/workflows/ci.yml/badge.svg)
*   ![CodeQL](https://github.com/TALMasterA/FYP/actions/workflows/codeql.yml/badge.svg)

--------------------------------------------------------------

## ⚠️ Development Cautions

**Secrets in CI:**
The CI pipeline requires a `GOOGLE_SERVICES_JSON` secret in the GitHub repository to build the Android app. This secret should contain the valid contents of `app/google-services.json`.

**USB Debugging:**
When testing the app, connect the phone and the Computer with the USB.
Enable USB debugging in the phone's Developer Options.
Turn off when not testing, or you may not be able to access your online bank.
You can also use android studio phone emulator for testing, if you don't have android phone.

**Firebase Setup:**
Require your own Firebase project and the related SDK for testing. (google-services.json).
Using android studio to open the project will required you to add the above file to app folder by yourself.
Firebase login is required.

--------------------------------------------------------------

## 🎯 Core Features

**Translation Modes:**
- **Quick Translate (Discrete):** Real-time voice translation for short phrases with auto-detect or manual language selection; shortcut button to switch to Live Conversation mode
- **Camera / OCR:** Scan text from images via camera or gallery; language hint reminds users to set the correct source language for best accuracy
- **Live Conversation (Continuous):** Live conversation translation with automatic speaker detection (Person A/B)
- Multi-language support: English, Cantonese (zh-HK), Traditional Chinese (zh-TW), Simplified Chinese (zh-CN), Japanese, and 10+ more via Azure
- Language swap button also swaps the recognised/translated text

**Learning System:**
- AI-generated learning sheets based on translation history (requires 20+ records per language pair)
- Quiz system with multiple question types and coin rewards
- In-app banner notification when generation completes (tapping opens the relevant screen)
- Word bank automatically generated from user translations; progress bar shows records toward next regen
- Custom word bank for user-defined vocabulary entries
- Favorites system for bookmarking important translations and saving entire live conversation sessions

**Friend System:**
- **My Profile:** Display and share your User ID and Username for easy friend discovery
- **Profile Visibility:** Set profile to Public (searchable) or Private (hidden from search)
- Search and add friends by username or User ID; attach an optional note (≤80 chars) with your request
- Send/accept/reject/cancel friend requests; search results show localized relationship status labels
- Incoming requests support bulk accept/reject actions with live progress and cancellation
- Client-side friend-request sending is rate-limited to 10 per hour and persists across app restarts
- Users without a username are guided straight to Profile before opening the add-friends flow or accepting requests
- Real-time chat with friends; translate entire conversation to your language (shown only when friend has sent at least one message)
- Share words and learning materials with friends; learning sheets can also be saved to your own shared inbox to archive a snapshot of the current version
- Shared inbox for received items with accept/dismiss confirmation and pull-to-refresh
- **Block / Unblock:** Block icon on each friend card; blocked users managed in a dedicated Blocked Users screen; Firestore security rules prevent blocked users from sending new friend requests
- Red dot (badge) notifications for unread messages and new shared items
- Friend username automatically synced on app launch and pull-to-refresh
- Usernames are unique; changing your username releases the old one for others
- Username changes have a 30-day cooldown (same as primary language changes), with confirmation dialog

**Customization:**
- UI language: English, Cantonese (hardcoded), Traditional Chinese (hardcoded), Simplified Chinese, Japanese, and 10+ more via Azure Translator API
- Language dropdown order: English → Cantonese → Traditional Chinese → other languages (Auto Detect keeps default)
- Primary language selector (30-day cooldown) sits directly under App UI language in Settings for quick access
- Theme settings (Light / Dark / System)
- Font size adjustment (80%–150%)
- 11 color palettes (1 free default + 10 unlockable at 10 coins each: Ocean, Sunset, Lavender, Rose, Mint, Crimson, Amber, Indigo, Emerald, Coral); palette settings accessible from the in-app Shop
- Voice settings per language
- Notification settings screen (push notifications + in-app badge toggles)
- OCR settings screen showing bundled on-device OCR model status and estimated footprint

**History & Organisation:**
- Translation history (recent 30–60 records, configurable)
- Filter by language or keyword; retry button on load error; pull-to-refresh available on History and Favorites
- Session management for Live Conversation (rename, delete)
- Cloud sync via Firestore with offline persistence
- Favorites for quick access (individual records and full sessions with view-only conversation replay)

**Coin System:**
- Earn coins through quiz performance (first-attempt anti-cheat verification)
- Unlock color palettes (10 coins each)
- Expand history view limit (1000 coins per 10-record increment)
- Anti-cheat: history count snapshotted at quiz generation time

**User Accounts:**
- Email/password authentication via Firebase
- Profile management (display name, account deletion)
- Password reset via email
- Auto sign-out on app version update
- First-launch and post-update onboarding (3-step welcome screen, re-shown after each app update)

**Navigation & UI:**
- Bottom navigation bar (Home, Quick Translate, Learn, Friends, Settings) with unread badge on Friends tab
- Persistent offline banner when connectivity is lost
- Edge-to-edge display; all screens properly padded above system navigation keys
- Help & Notes screen with ordered sections (Cautions, Features, Tips, Friend System, Privacy) using card layout
- Centralised error handling: user-facing errors auto-dismiss after 3 s; system errors logged to Crashlytics via `AppLogger.e`

--------------------------------------------------------------

## 📂 Project Structure

Following the MVVM (Model–View–ViewModel) structure with Clean Architecture layers.

**Key Directories:**
- `app/src/main/java/com/example/fyp/`
    - `navigation/` - Compose navigation graph & sub-graphs
        - `AppNavigation.kt` - Main composable nav graph (routes, auth guards, bottom nav, generation banners)
        - `AppScreens.kt` - Sealed class of all `AppScreen` route destinations
        - `MainFeatureGraph.kt`, `LearningWordBankGraph.kt`, `FriendsChatGraph.kt`, `SettingsProfileGraph.kt`, `StartupAuthGraph.kt` - Modular sub-graphs
    - `appstate/`
        - `AppViewModel.kt` - Top-level state (auth, unread badge counts, offline flag)
    - `screens/` - UI screens and ViewModels
        - `home/` - Home screen (welcome greeting, Quick Translate / Live Conversation cards)
        - `speech/` - Discrete (Quick Translate) & continuous (Live Conversation) translation screens
        - `history/` - Translation history with discrete/continuous tabs; pagination, retry
        - `learning/` - Learning sheets, quiz taking & results, quiz dialogs
        - `wordbank/` - Generated & custom word banks with progress bars
        - `favorites/` - Bookmarked translations and saved sessions (Records/Sessions tabs)
        - `friends/` - Friend management, chat, shared inbox, blocked users
        - `settings/` - Settings, profile, shop, voice settings, notification settings, OCR settings
        - `login/` - Login, registration, password reset
        - `help/` - Help, notes & cautions screen
        - `onboarding/` - First-launch 3-step welcome screen
        - `startup/` - App startup screen
        - `feedback/` - User feedback screen
    - `model/` - Data models
        - `ui/strings/` - `UiTextKey` enum (`UiTextCore.kt`), `BaseUiTexts` (`UiTextScreens.kt`), `UiTextHelpers`
        - `ui/strings/translations/` - `ZhTwUiTexts` (zh-TW hardcoded), `CantoneseUiTexts` (zh-HK hardcoded)
        - `user/` - `AuthState`, `UserProfile`, `UserSettings`
        - `friends/` - `FriendRequest`, `FriendRelation`, `FriendMessage`, `SharedItem`, `PublicUserProfile`
        - `OcrResult` - OCR text recognition result
    - `data/` - Repository implementations and data sources
        - `azure/` - Azure Speech & language configuration, `LanguageDisplayNames`
        - `cloud/` - Cloud function clients and caching (translation, language detection)
        - `clients/` - Speech token and translation HTTP clients
        - `database/` - Data cleanup and database utilities
        - `di/` - Hilt dependency injection modules
        - `feedback/` - Feedback repository
        - `history/` - History repository and shared data source
        - `image/` - Image loading module
        - `learning/` - Quiz, learning sheet, and content repositories
        - `friends/` - Friend, chat, and sharing repositories
        - `network/` - Cache interceptor and network monitor
        - `ocr/` - ML Kit OCR repository
        - `repositories/` - Speech and translation repositories
        - `settings/` - User settings repository and shared data source
        - `ui/` - UI language cache and state controller (intercepts zh-TW/zh-HK for hardcoded maps)
        - `user/` - Auth, profile, and favorites repositories
        - `wordbank/` - Word bank repositories, cache, and generation
    - `domain/` - Use cases for business logic
        - `auth/` - Login use case
        - `feedback/` - Feedback use cases
        - `history/` - History CRUD operations and repository interface
        - `learning/` - Learning materials, quiz generation, eligibility checks, repository interfaces
        - `friends/` - Friend management, chat, and sharing use cases
        - `ocr/` - OCR text recognition use case
        - `settings/` - Settings modification use cases
        - `speech/` - Speech recognition, translation, TTS use cases
    - `core/` - Common composables and utilities (logging via `AppLogger`, audio, permissions, pagination, font scaling, security, connectivity, notifications, performance)
    - `utils/` - General utility classes (e.g. `ErrorMessageMapper`)
    - `ui/` - Theme configuration (colors, palettes, dimensions, typography, animated components)
- `fyp-backend/functions/` - Firebase Cloud Functions (TypeScript): translation, speech token, AI generation, FCM notifications, daily stale-token pruning
- `docs/` - Architecture notes (`ARCHITECTURE_NOTES.md`), API reference (`CLOUD_FUNCTIONS_API.md`), test coverage report (`TEST_COVERAGE.md`), secrets rotation (`SECRETS_ROTATION.md`), LeakCanary explanation (`LEAKCANARY_EXPLANATION.md`)

--------------------------------------------------------------

## 🔧 Development Setup

**Prerequisites:**
1. Android Studio (latest stable version)
2. JDK 17 or higher
3. Android SDK
4. Firebase project with Authentication, Firestore, Cloud Functions, Crashlytics enabled
5. Azure Speech/Translation API keys
6. Azure OpenAI deployment for learning content generation

**Configuration Files:**
1. `google-services.json` - Place in `app/` folder (from Firebase Console)
   - For local/CI builds without Firebase: A mock `google-services.json` is provided
   - The mock file contains placeholder values sufficient for compilation
   - Replace with actual Firebase configuration for production deployment
2. Backend environment variables for Cloud Functions (Azure API keys configured via Firebase Functions config)

**Backend Setup:**
1. Install Node.js 24+ and the Firebase CLI (`npm install -g firebase-tools`)
2. Navigate to the backend directory: `cd fyp-backend/functions`
3. Install dependencies: `npm install`
4. Configure secrets via Firebase:
   ```bash
   firebase functions:secrets:set AZURE_SPEECH_KEY
   firebase functions:secrets:set AZURE_SPEECH_REGION
   firebase functions:secrets:set AZURE_TRANSLATOR_KEY
   firebase functions:secrets:set AZURE_TRANSLATOR_REGION
   firebase functions:secrets:set GENAI_BASE_URL
   firebase functions:secrets:set GENAI_API_VERSION
   firebase functions:secrets:set GENAI_API_KEY
   ```
5. Build: `npm run build`
6. Run tests: `npm test`
7. Deploy: `npm run deploy`

See `docs/SECRETS_ROTATION.md` for the secrets rotation runbook.

**Firebase Services Used:**
- Firebase Authentication for user login/registration
- Firestore Database for storing all user data (history, settings, learning materials, quizzes, favorites, word banks)
- Cloud Functions for secure API calls to Azure services (speech token, translation, AI generation)
- Firebase Crashlytics for crash reporting
- Firebase Performance Monitoring for performance tracking

--------------------------------------------------------------

## 💻 Development Workflow

**Adding New UI Text:**
For now, to add new UI text to the UI language translation scope, you need to:
1. Add the key to `enum class UiTextKey` in `UiTextCore.kt`
2. Add the English text to `val BaseUiTexts` in `UiTextScreens.kt` (keep order in sync with enum)
3. If the string contains a template placeholder that is also a common English word (e.g. `coins`, `answer`), use a **capitalised placeholder** (e.g. `{Coins}`, `{Answer}`) so Azure Translator API does not translate the placeholder token
4. Add corresponding entries to `ZhTwUiTexts.kt` (Traditional Chinese) and `CantoneseUiTexts.kt` (Cantonese) — use the same capitalised placeholder
5. Apply the UI text composable using the key with `t(UiTextKey.YourKey)`
6. Run `UiTextAlignmentTest` to verify enum count matches `BaseUiTexts` list count

**Translation System:**
- Base language is English (hardcoded in `UiTextScreens.kt`)
- Traditional Chinese (zh-TW) and Cantonese (zh-HK) are hardcoded in `ZhTwUiTexts.kt` and `CantoneseUiTexts.kt`; no API call is made for these languages
- All other UI languages are translated via Azure Translator API
- Cached locally via DataStore for performance (30-day TTL, max 1000 entries)
- Language name corrections applied for accuracy
- Guest users get 1 free UI language change; unlimited for logged-in users
- Language dropdown order: English → Cantonese → Traditional Chinese → other languages (Auto Detect keeps default position)
- Azure language codes are normalised before API calls: `zh-HK → yue`, `zh-TW → zh-Hant`, `zh-CN → zh-Hans`

**Data Flow:**
1. User interacts with UI (Compose screens)
2. ViewModel handles business logic
3. Use cases coordinate between layers
4. Repositories manage data sources (Firestore, Cloud Functions)
5. Cloud Functions call external APIs securely (Azure Speech, Translation, OpenAI)

**Shared Data Pattern:**
- `SharedHistoryDataSource` provides a single Firestore listener shared by History, Learning, and WordBank ViewModels
- Includes language count caching and debounced count refresh (5s) to reduce Firestore reads

--------------------------------------------------------------

## 🚀 Commands

**Build & Test:**
```bash
# Compile Kotlin code
./gradlew compileDebugKotlin

# Run all unit tests (206 test files, 2,668 tests)
./gradlew testDebugUnitTest

# Run specific test class
./gradlew testDebugUnitTest --tests "com.example.fyp.domain.learning.GenerationEligibilityTest"

# Build debug APK
./gradlew assembleDebug
```

**Git Workflow:**
```bash
# Update main branch from remote
git pull --ff-only

# Check out PR branch for testing
gh pr checkout "PR number"
```

**Firebase Deployment:**
```bash
# Deploy Cloud Functions after updating index.ts
firebase deploy --only functions

# Deploy all Firebase services
firebase deploy
```

**GitHub CLI:**
```bash
# Install GitHub CLI (Windows)
winget install --id GitHub.cli

# Verify installation
gh --version

# Authenticate
gh auth login
```

--------------------------------------------------------------

## 🧪 Testing

**Test Coverage:**
- **Android — 206 test files, 2,668 unit tests** (from `app/build/test-results/testDebugUnitTest`)
- **Backend — 5 test files, 79 tests** covering Cloud Functions logic
- See `docs/TEST_COVERAGE.md` for a detailed breakdown by layer and package
- Test categories:
  - Domain layer use cases (30+ test files)
  - ViewModels (10+ test files)
  - Models and serialization (18+ test files)
  - Repositories and data layer (30+ test files)
- Navigation routes and rules (5 test files)
- UI components (StandardButtons, StandardTextFields, StandardDialogs, ThemeHelper, ColorPalette)
- Security utilities (validation, sanitization, rate limiting, encoding correctness)
- Performance utilities (debouncing, throttling)
- Anti-cheat and generation eligibility (comprehensive, including edge cases)
- Caching logic (translation, language detection)
- Onboarding persistence logic (first-launch and version-based re-show)
- Speech controllers (TTS controller, continuous conversation controller)
- Speech models (SpeechScreenState, RecognizePhase, ChatMessage)
- Data layer algorithms (quiz context selection, data cleanup utilities)
- Shared data sources (SharedHistoryDataSource, SharedFriendsDataSource, SharedSettingsDataSource)
- Feedback repository (validation, retry logic, error classification)
- Language display names (full mapping, detection-to-supported, support checking)
- Word bank models (WordBankItem, WordBank, WordBankUiState, SpeakingType)
- Session management (validation, naming, delete preconditions)
- Notification delivery rules (FCM preference gating + cache sync for toggles)

**Key Test Suites:**
- `OnboardingLogicTest` - Onboarding first-launch and version-based re-show logic
- `GenerationEligibilityTest` - Word bank and learning sheet regeneration rules
- `CoinEligibilityTest` - Coin award anti-cheat logic
- `CoinAndGenerationIntegrationTest` - End-to-end eligibility scenarios
- `UiTextAlignmentTest` - Critical test preventing enum/list misalignment crashes
- `SecurityUtilsTest` + `SanitizeInputExtendedTest` - Input validation, sanitization, and encoding correctness
- `PerformanceUtilsTest` - Debouncing and throttling optimization patterns
- `QuizGenerationRepositoryImplTest` - Quiz context selection algorithm (weighted sampling, deduplication)
- `TtsControllerTest` - Text-to-speech state management and error handling
- `ContinuousConversationControllerTest` - Continuous conversation session lifecycle and guards
- `DataCleanupUtilsTest` - Data housekeeping cutoff calculations and orphan detection
- `DataLayerIntegrationTest` - Cross-repository invariants: chatId↔friends, sharing↔friends consistency
- `ChatRepositoryLogicTest` - Chat ID generation, participant validation, unread count math
- `QuizRepositoryLogicTest` - Running average formula, coin debounce, score initialization
- `SharedHistoryDataSourceTest` - Shared history caching, LRU filtering, debounced refresh
- `SharedFriendsDataSourceTest` - Friends state management, seen/unseen tracking, username cache
- `FirestoreHistoryRepositoryLogicTest` - Language count aggregation, batch chunking, positive filtering
- `FirestoreFeedbackRepositoryTest` - Feedback validation, retry logic, error classification
- Friend system tests - Friend requests, chat operations, shared items, blocking
- Repository tests - Firestore operations, caching, cleanup
- Cache tests - Translation cache, language detection cache data models
- Constant validation tests - UI, AI, data, and generation constants
- `EligibilityEdgeCasesTest` - Boundary values for generation and coin eligibility
- `SessionManagementLogicTest` - Session ID/name validation and delete preconditions
- `SpeechModelsTest` - Speech screen state, recognize phase, and chat message models
- `WordBankModelsTest` - Word bank data models and UI state
- `UserAndProfileTest` - User/UserProfile data classes and AuthState sealed interface
- `LanguageDisplayNamesExtendedTest` - Comprehensive language mapping and detection tests

**Backend Key Test Suites (fyp-backend):**
- `helpers.test.ts` - Auth guards, input validation, rate limiting, URL building helpers (24 tests)
- `logger.test.ts` - Structured JSON logger output format (6 tests)
- `translation.test.ts` - getSpeechToken, translateText, translateTexts, detectLanguage — auth, error and success paths (15 tests)
- `coins.test.ts` - awardQuizCoins anti-cheat rules (version match, increment gate) and spendCoins shop purchases (22 tests)
- `notifications.test.ts` - FCM triggers: data guards, status filtering, spam detection (link flooding), friend request rate limiting (12 tests)

**Running Tests:**
```bash
# Run all tests
./gradlew testDebugUnitTest

# Run with coverage report
./gradlew testDebugUnitTest jacocoTestReport

# Run specific test class
./gradlew testDebugUnitTest --tests "com.example.fyp.domain.learning.*"
```

**Running Backend Tests:**
```bash
cd fyp-backend/functions
npm test           # Run all Jest tests (79 tests)
npm run lint       # ESLint check
npm run build      # TypeScript compile
```

**Test Requirements:**
- Mock `google-services.json` file in `app/` folder (gitignored, mock version for CI/local builds)
- JUnit 4 + Mockito + Kotlin Coroutines Test utilities

--------------------------------------------------------------

## 📊 Data Models

**Key Models:**
- `TranslationRecord` - Individual translation entry (source/target text, languages, mode, session, speaker, timestamp)
- `UserSettings` - User preferences (language, font scale, theme, color palette, voice settings, history limit, notification toggles)
- `UserProfile` - Display name, photo URL, timestamps
- `QuizQuestion` - Quiz question with options, correct answer, and explanation
- `QuizAttempt` - Full quiz attempt with answers, scores, and timestamps
- `QuizAttemptDoc` - Firestore storage format with serialized JSON
- `UserCoinStats` - Coin balance and lifetime stats
- `FavoriteRecord` - Bookmarked translation with optional note
- `FavoriteSession` - Saved live conversation session with embedded records
- `FavoriteSessionRecord` - Single record within a saved session
- `CustomWord` - User-defined vocabulary entry with pronunciation and example
- `HistorySession` - Continuous conversation session metadata
- `SpeechResult` - Sealed class for speech recognition results (Success/Error)
- `PublicUserProfile` - Searchable user profile with username and `isDiscoverable` flag
- `FriendRequest` - Friend request with sender/receiver info, status, and optional note (≤80 chars)
- `FriendRelation` - Bidirectional friendship record with cached friend username
- `FriendMessage` - Chat message with sender, text, timestamp, read status
- `SharedItem` - Shared word/material with sender, type, content
- `BlockedUser` - Blocked user record with userId, username, and blocked timestamp (defined in `data/friends/FriendsRepository.kt`)

**Firestore Collections:**
- `users/{uid}/history` - Translation records
- `users/{uid}/sessions` - Continuous conversation sessions
- `users/{uid}/profile/settings` - User settings document
- `users/{uid}/profile/info` - User profile document
- `users/{uid}/profile/public` - Public profile (username, isDiscoverable)
- `users/{uid}/learning_sheets/{primary}_{target}` - AI-generated learning content per language pair
- `users/{uid}/quiz_attempts` - Quiz attempt records
- `users/{uid}/quiz_stats/{primary}_{target}` - Quiz statistics per language pair
- `users/{uid}/generated_quizzes/{primary}_{target}` - Cached generated quiz questions
- `users/{uid}/user_stats/coins` - User coin balance
- `users/{uid}/user_stats/language_counts` - Language usage statistics
- `users/{uid}/coin_awards/{versionKey}` - Coin award tracking for anti-cheat
- `users/{uid}/last_awarded_quiz/{primary}_{target}` - Last awarded quiz count per language pair
- `users/{uid}/favorites` - Bookmarked translations
- `users/{uid}/favorite_sessions` - Saved live conversation sessions
- `users/{uid}/word_banks/{primary}_{target}` - Generated vocabulary per language pair
- `users/{uid}/custom_words` - User-defined vocabulary entries
- `users/{uid}/friends` - Friend relation records
- `users/{uid}/shared_inbox` - Items shared with user by friends
- `users/{uid}/blocked_users` - Blocked user records
- `users/{uid}/fcm_tokens` - FCM push notification tokens (pruned after 60 days by Cloud Function)
- `usernames` - Username registry for uniqueness enforcement
- `user_search` - Searchable user index by username
- `friend_requests` - Friend requests between users
- `chats/{chatId}/messages` - Chat messages between friends

--------------------------------------------------------------

## 🔐 Security & Privacy

**Data Protection:**
- All user data stored in Firestore with security rules
- API keys protected via Cloud Functions (never exposed to client)
- Audio captured only for recognition, not stored
- Firebase Authentication handles secure login
- Auto sign-out on app version update for security
- Profile visibility setting: users can set their profile to Private to prevent discovery via search
- Block system: Firestore security rules prevent blocked users from creating new friend requests (server-side enforcement)
- FCM tokens pruned automatically after 60 days by scheduled Cloud Function

**API Security:**
Using Firebase Cloud Functions to protect API keys (backend).
- Azure Translation API
- Azure Speech Recognition API
- Azure OpenAI API for learning materials generation

**Release Build:**
- ProGuard enabled for code obfuscation and optimization
- Debug logging stripped in release builds
- Compose debug source info removed

--------------------------------------------------------------

**Last Updated:** March 19, 2026

(Some content was auto-generated and may contain inaccuracies.)
