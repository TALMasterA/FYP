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
- Firebase Cloud Functions (Backend API)
- Azure Speech & Translation Services

**Tools:**
- GitHub
- Firebase

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
- **Discrete Mode:** Real-time voice translation for short phrases
- **Continuous Mode:** Live conversation translation with automatic speaker detection
- Multi-language support (English, Cantonese, Japanese, Mandarin, and more)

**Learning System:**
- AI-generated learning sheets based on translation history
- Quiz system with coin rewards
- Word bank automatically generated from user translations
- Favorites system for bookmarking important translations

**Customization:**
- UI language translation (supports 16+ languages)
- Theme settings (Light/Dark/System)
- Font size adjustment (80%-150%)
- Unlockable color palettes (purchase with coins)
- Voice settings per language

**History & Organization:**
- Translation history (100 records, expandable to 150)
- Filter by language or keyword
- Session management for continuous conversations
- Cloud sync via Firestore
- Favorites for quick access

**Coin System:**
- Earn coins through quiz performance
- Unlock color palettes
- Expand history limit
- Anti-cheat verification system

--------------------------------------------------------------

## 📂 Project Structure

Following the MVVM (Model–View–ViewModel) structure.

**Key Directories:**
- `app/src/main/java/com/example/fyp/`
    - `screens/` - UI screens (Home, Settings, History, Learning, etc.)
    - `model/` - Data models and UI text definitions
    - `data/` - Repository pattern implementations
    - `domain/` - Use cases for business logic
    - `core/` - Common composables and utilities
- `fyp-backend/functions/` - Firebase Cloud Functions (TypeScript)

--------------------------------------------------------------

## 🔧 Development Setup

**Prerequisites:**
1. Android Studio (latest stable version)
2. JDK 11 or higher
3. Android SDK
4. Firebase project with Authentication and Firestore enabled
5. Azure Speech/Translation API keys

**Configuration Files:**
1. `google-services.json` - Place in `app/` folder (from Firebase Console)
2. Backend environment variables for Cloud Functions (API keys)

**Firebase Services Used:**
- Firebase Authentication for user login/registration
- Firestore Database for storing translation history, user settings, learning materials
- Cloud Functions for secure API calls to Azure services

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
- Cached locally for performance
- Language name corrections applied for accuracy
- Guest users get 1 free UI language change, unlimited for logged-in users

**Data Flow:**
1. User interacts with UI (Compose screens)
2. ViewModel handles business logic
3. Use cases coordinate between layers
4. Repositories manage data sources (Firestore, Cloud Functions)
5. Cloud Functions call external APIs securely

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
- `TranslationRecord` - Individual translation entry
- `UserSettings` - User preferences and customization
- `LearningSheet` - AI-generated learning content
- `Quiz` - Quiz data and questions
- `CoinStats` - User coin balance and history

**Firestore Collections:**
- `users/{uid}/history` - Translation records
- `users/{uid}/settings` - User settings
- `users/{uid}/learning` - Learning sheets
- `users/{uid}/quizzes` - Quiz data
- `users/{uid}/coins` - Coin transactions
- `users/{uid}/favorites` - Bookmarked translations

--------------------------------------------------------------

## 🔐 Security & Privacy

**Data Protection:**
- All user data stored in Firestore with security rules
- API keys protected via Cloud Functions
- Audio captured only for recognition, not stored
- Firebase Authentication handles secure login

**API Security:**
Using Firebase Cloud Functions to protect API keys (backend).
- Azure Translation API
- Azure Speech Recognition API
- Generative AI API for learning materials

--------------------------------------------------------------

**Last Updated:** February 2026 - Manual checked
(Some content is by github copilot agent and may contain error)