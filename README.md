# FYP - Translation & Learning App (Reviewing)

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

## ⚠️ Development Cautions

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
- Send/accept/reject/cancel friend requests
- Real-time chat with friends; translate entire conversation to your language (shown only when friend has sent at least one message)
- Share words and learning materials with friends
- Shared inbox for received items with accept/dismiss confirmation
- **Block / Unblock:** Block icon on each friend card; blocked users managed in a dedicated Blocked Users screen; Firestore security rules prevent blocked users from sending new friend requests
- Red dot (badge) notifications for unread messages and new shared items
- Friend username automatically synced on app launch and pull-to-refresh
- Usernames are unique; changing your username releases the old one for others

**Customization:**
- UI language: English, Cantonese (hardcoded), Traditional Chinese (hardcoded), Simplified Chinese, Japanese, and 10+ more via Azure Translator API
- Language dropdown order: English → Cantonese → Traditional Chinese → other languages (Auto Detect keeps default)
- Theme settings (Light / Dark / System)
- Font size adjustment (80%–150%)
- 11 color palettes (1 free default + 10 unlockable at 10 coins each: Ocean, Sunset, Lavender, Rose, Mint, Crimson, Amber, Indigo, Emerald, Coral); palette settings accessible from the in-app Shop
- Voice settings per language
- Notification settings screen (push notifications + in-app badge toggles)

**History & Organisation:**
- Translation history (recent 50–100 records, configurable)
- Filter by language or keyword; retry button on load error
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
- First-launch onboarding (3-step welcome screen)

**Navigation & UI:**
- Bottom navigation bar (Home, Quick Translate, Learn, Friends, Settings) with unread badge on Friends tab
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
        - `settings/` - Settings, profile, shop, voice settings, notification settings
        - `login/` - Login, registration, password reset
        - `help/` - Help, notes & cautions screen
        - `onboarding/` - First-launch 3-step welcome screen
    - `model/` - Data models
        - `ui/` - `UiTextKey` enum, `BaseUiTexts` (English), `ZhTwUiTexts` (zh-TW hardcoded), `CantoneseUiTexts` (zh-HK hardcoded), `UiTextHelpers` (language name translations)
        - `user/` - `AuthState`, `UserProfile`, `UserSettings`
        - `friends/` - `FriendRequest`, `FriendRelation`, `FriendMessage`, `SharedItem`, `PublicUserProfile`, `BlockedUser`
    - `data/` - Repository implementations and data sources
        - `azure/` - Azure Speech & language configuration, `LanguageDisplayNames`
        - `cloud/` - Cloud function clients and caching (translation, language detection)
        - `clients/` - Speech token and translation HTTP clients
        - `di/` - Hilt dependency injection modules
        - `history/` - History repository and shared data source
        - `learning/` - Quiz, learning sheet, and content repositories
        - `friends/` - Friend, chat, and sharing repositories
        - `repositories/` - Speech and translation repositories
        - `settings/` - User settings repository and shared data source
        - `ui/` - UI language cache and state controller (intercepts zh-TW/zh-HK for hardcoded maps)
        - `user/` - Auth, profile, and favorites repositories
        - `wordbank/` - Word bank repositories, cache, and generation
    - `domain/` - Use cases for business logic
        - `auth/` - Login use case
        - `history/` - History CRUD operations and repository interface
        - `learning/` - Learning materials, quiz generation, eligibility checks, repository interfaces
        - `friends/` - Friend management, chat, and sharing use cases
        - `settings/` - Settings modification use cases
        - `speech/` - Speech recognition, translation, TTS use cases
    - `core/` - Common composables and utilities (logging via `AppLogger`, audio, permissions, pagination, font scaling)
    - `ui/` - Theme configuration (colors, palettes, dimensions, typography, animated components)
- `fyp-backend/functions/` - Firebase Cloud Functions (TypeScript): translation, speech token, AI generation, FCM notifications, daily stale-token pruning
- `docs/` - Architecture notes (`ARCHITECTURE_NOTES.md`)

--------------------------------------------------------------

## 🔧 Development Setup

**Prerequisites:**
1. Android Studio (latest stable version)
2. JDK 11 or higher
3. Android SDK
4. Firebase project with Authentication, Firestore, Cloud Functions, Crashlytics enabled
5. Azure Speech/Translation API keys
6. Azure OpenAI deployment for learning content generation

**Configuration Files:**
1. `google-services.json` - Place in `app/` folder (from Firebase Console)
2. Backend environment variables for Cloud Functions (Azure API keys configured via Firebase Functions config)

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

# Run all unit tests (53 test files, 398+ tests)
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
- **53 test files** covering critical app logic
- **398+ unit tests** passing
- Test categories:
  - Domain layer use cases (20+ test files)
  - ViewModels (6 test files)
  - Models and serialization (10+ test files)
  - Repositories and data layer (8+ test files)
  - Anti-cheat and generation eligibility (comprehensive)

**Key Test Suites:**
- `GenerationEligibilityTest` - Word bank and learning sheet regeneration rules
- `CoinEligibilityTest` - Coin award anti-cheat logic
- `CoinAndGenerationIntegrationTest` - End-to-end eligibility scenarios
- `UiTextAlignmentTest` - Critical test preventing enum/list misalignment crashes
- Friend system tests - Friend requests, chat operations, shared items
- Repository tests - Firestore operations, caching, cleanup

**Running Tests:**
```bash
# Run all tests
./gradlew testDebugUnitTest

# Run with coverage report
./gradlew testDebugUnitTest jacocoTestReport

# Run specific test class
./gradlew testDebugUnitTest --tests "com.example.fyp.domain.learning.*"
```

**Test Requirements:**
- Mock `google-services.json` file in `app/` folder (gitignored)
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
- `BlockedUser` - Blocked user record with userId, username, and blocked timestamp

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

**Last Updated:** March 1, 2026

(Some content is by github copilot agent and may contain error)