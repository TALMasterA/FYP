# FYP - Translation & Learning App (Reviewing)

An Android-based translation and language learning application with AI-powered features.

Develop in Android Studio, Android ONLY.

--------------------------------------------------------------

## 📱 Try the App

Register to try this app!

Link: https://appdistribution.firebase.dev/i/5ebf3d592700b0f7

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
- **Discrete Mode:** Real-time voice translation for short phrases with auto-detect or manual language selection
- **Continuous Mode:** Live conversation translation with automatic speaker detection (Person A/B)
- Multi-language support (English, Cantonese, Japanese, Mandarin, and more via Azure)

**Learning System:**
- AI-generated learning sheets based on translation history (requires 20+ records per language pair)
- Quiz system with multiple question types and coin rewards
- Word bank automatically generated from user translations
- Custom word bank for user-defined vocabulary entries
- Favorites system for bookmarking important translations

**Customization:**
- UI language translation (supports 16+ languages)
- Theme settings (Light/Dark/System)
- Font size adjustment (80%-150%)
- 6 color palettes (1 free default + 5 unlockable at 10 coins each: Ocean, Sunset, Lavender, Rose, Mint)
- Voice settings per language

**History & Organization:**
- Translation history
- Filter by language or keyword
- Session management for continuous conversations (rename, delete)
- Cloud sync via Firestore
- Favorites for quick access

**Coin System:**
- Earn coins through quiz performance (first-attempt anti-cheat verification)
- Unlock color palettes (10 coins each)
- Expand history view limit (1000 coins per 10-record increment)
- Anti-cheat: history count snapshots at quiz generation time

**User Accounts:**
- Email/password authentication via Firebase
- Profile management (display name, account deletion)
- Password reset via email
- Auto sign-out on app version update

--------------------------------------------------------------

## 📂 Project Structure

Following the MVVM (Model–View–ViewModel) structure with Clean Architecture layers.

**Key Directories:**
- `app/src/main/java/com/example/fyp/`
    - `screens/` - UI screens and ViewModels
        - `home/` - Home screen
        - `speech/` - Discrete & continuous translation screens
        - `history/` - Translation history with discrete/continuous tabs
        - `learning/` - Learning sheets, quiz taking & results
        - `wordbank/` - Generated & custom word banks
        - `favorites/` - Bookmarked translations
        - `settings/` - Settings, profile, shop, voice settings
        - `login/` - Login, registration, password reset
        - `help/` - Help & info screen
    - `model/` - Data models (`TranslationRecord`, `Quiz`, `UserSettings`, etc.)
        - `ui/` - UI text localization keys and translations
        - `user/` - User-related models (`AuthState`, `UserProfile`, `UserSettings`)
    - `data/` - Repository implementations and data sources
        - `azure/` - Azure Speech & language configuration
        - `cloud/` - Cloud function clients and caching (translation, language detection)
        - `clients/` - Speech token and translation HTTP clients
        - `di/` - Hilt dependency injection modules
        - `history/` - History repository and shared data source
        - `learning/` - Quiz, learning sheet, and content repositories
        - `repositories/` - Speech and translation repositories
        - `settings/` - User settings repository and shared data source
        - `ui/` - UI language cache and state controller
        - `user/` - Auth, profile, and favorites repositories
        - `wordbank/` - Word bank repositories, cache, and generation
    - `domain/` - Use cases for business logic
        - `auth/` - Login use case
        - `history/` - History CRUD operations and repository interface
        - `learning/` - Learning materials, quiz generation, eligibility checks, repository interfaces
        - `settings/` - Settings modification use cases
        - `speech/` - Speech recognition, translation, TTS use cases
    - `core/` - Common composables and utilities (logging, audio, permissions, pagination, font scaling)
    - `ui/` - Theme configuration (colors, palettes, dimensions, typography, animated components)
- `fyp-backend/functions/` - Firebase Cloud Functions (TypeScript)

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
1. Add the key to enum class in `UiTextCore.kt`
2. Add the English text to val `BaseUiTexts` in `UiTextScreens.kt`, you will manage the UI description here
3. Apply the UI text composable using the key

**Translation System:**
- Base language is English
- UI translations are generated via Azure Translator API
- Cached locally via DataStore for performance (30-day TTL, max 1000 entries)
- Language name corrections applied for accuracy
- Guest users get 1 free UI language change, unlimited for logged-in users

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

## 📊 Data Models

**Key Models:**
- `TranslationRecord` - Individual translation entry (source/target text, languages, mode, session, speaker, timestamp)
- `UserSettings` - User preferences (language, font scale, theme, color palette, voice settings, history limit)
- `UserProfile` - Display name, photo URL, timestamps
- `QuizQuestion` - Quiz question with options, correct answer, and explanation
- `QuizAttempt` - Full quiz attempt with answers, scores, and timestamps
- `QuizAttemptDoc` - Firestore storage format with serialized JSON
- `UserCoinStats` - Coin balance and lifetime stats
- `FavoriteRecord` - Bookmarked translation with optional note
- `CustomWord` - User-defined vocabulary entry with pronunciation and example
- `HistorySession` - Continuous conversation session metadata
- `SpeechResult` - Sealed class for speech recognition results (Success/Error)

**Firestore Collections:**
- `users/{uid}/history` - Translation records
- `users/{uid}/sessions` - Continuous conversation sessions
- `users/{uid}/profile/settings` - User settings document
- `users/{uid}/profile/info` - User profile document
- `users/{uid}/learning_sheets/{primary}_{target}` - AI-generated learning content per language pair
- `users/{uid}/quiz_attempts` - Quiz attempt records
- `users/{uid}/quiz_stats/{primary}_{target}` - Quiz statistics per language pair
- `users/{uid}/generated_quizzes/{primary}_{target}` - Cached generated quiz questions
- `users/{uid}/user_stats/coins` - User coin balance
- `users/{uid}/user_stats/language_counts` - Language usage statistics
- `users/{uid}/coin_awards/{versionKey}` - Coin award tracking for anti-cheat
- `users/{uid}/last_awarded_quiz/{primary}_{target}` - Last awarded quiz count per language pair
- `users/{uid}/favorites` - Bookmarked translations
- `users/{uid}/word_banks/{primary}_{target}` - Generated vocabulary per language pair
- `users/{uid}/custom_words` - User-defined vocabulary entries

--------------------------------------------------------------

## 🔐 Security & Privacy

**Data Protection:**
- All user data stored in Firestore with security rules
- API keys protected via Cloud Functions (never exposed to client)
- Audio captured only for recognition, not stored
- Firebase Authentication handles secure login
- Auto sign-out on app version update for security

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

**Last Updated:** February 2026 - Manual checked
(Some content is by github copilot agent and may contain error)