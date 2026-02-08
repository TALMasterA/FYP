# Project Architecture

This document provides a detailed overview of the FYP app's architecture, design patterns,  
and codebase organization.

---

## 🏗️ Architecture Overview

The app follows **Clean Architecture** principles with **MVVM (Model-View-ViewModel)**  
pattern for clear separation of concerns and maintainability.

```
┌─────────────────────────────────────────────┐
│         Presentation Layer                  │
│    (Screens, ViewModels, Compose UI)        │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│           Domain Layer                      │
│      (Use Cases, Business Logic)            │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│            Data Layer                       │
│  (Repositories, Data Sources, APIs)         │
└─────────────────────────────────────────────┘
```

### Key Principles

- **Separation of Concerns**: Each layer has a specific responsibility
- **Dependency Inversion**: Inner layers don't depend on outer layers
- **Testability**: Business logic isolated from Android framework
- **Scalability**: Easy to add features without breaking existing code

---

## 📂 Folder Structure

### Root Directory

```
FYP/
├── app/                          # Android application
├── fyp-backend/                  # Firebase Cloud Functions
├── gradle/                       # Gradle wrapper files
├── .github/                      # GitHub workflows
├── build.gradle.kts             # Project-level build configuration
├── settings.gradle.kts          # Gradle settings
└── README.md                    # Project README
```

---

## 📱 Android App Structure

### Main Package: `app/src/main/java/com/example/fyp/`

```
com/example/fyp/
├── MainActivity.kt              # Single activity (Jetpack Compose)
├── FYPApplication.kt           # Application class (Hilt setup)
├── AppNavigation.kt            # Navigation graph
│
├── screens/                    # 🖼️ UI Screens (Presentation Layer)
├── core/                       # 🔧 Core utilities and common UI
├── model/                      # 📦 Data models
├── domain/                     # 💼 Business logic (Use Cases)
├── data/                       # 🗄️ Data layer (Repositories, APIs)
└── ui/                         # 🎨 Theme and design system
```

---

## 🖼️ Presentation Layer (`screens/`)

Each feature has its own package containing screen composables and ViewModels:

### Structure

```
screens/
├── home/
│   └── HomeScreen.kt           # Main landing page
│
├── login/
│   ├── LoginScreen.kt          # Login UI
│   ├── AuthViewModel.kt        # Authentication logic
│   └── ResetPasswordScreen.kt  # Password reset
│
├── speech/
│   ├── SpeechRecognitionScreen.kt          # Discrete mode
│   ├── ContinuousConversationScreen.kt     # Continuous mode
│   ├── SpeechViewModel.kt                  # Speech logic
│   └── SpeechModels.kt                     # UI state models
│
├── history/
│   ├── HistoryScreen.kt                    # Main history screen
│   ├── HistoryViewModel.kt                 # History logic
│   ├── HistoryDiscreteTab.kt               # Discrete history
│   └── HistoryContinuousTab.kt             # Continuous history
│
├── learning/
│   ├── LearningScreen.kt                   # Learning sheets list
│   ├── LearningViewModel.kt                # Learning logic
│   ├── QuizScreen.kt                       # Quiz UI
│   └── QuizResultsScreen.kt                # Results display
│
├── wordbank/
│   ├── WordBankScreen.kt                   # Word bank UI
│   └── WordBankViewModel.kt                # Word bank logic
│
├── settings/
│   ├── SettingsScreen.kt                   # Settings dashboard
│   ├── ProfileScreen.kt                    # User profile
│   ├── ShopScreen.kt                       # Coin shop
│   └── VoiceSettingsScreen.kt              # Voice preferences
│
├── favorites/
│   ├── FavoritesScreen.kt                  # Favorites list
│   └── FavoritesViewModel.kt               # Favorites logic
│
└── help/
    └── HelpScreen.kt                       # Help documentation
```

---

## 💼 Domain Layer (`domain/`)

Contains business logic encapsulated in **Use Cases**:

```
domain/
├── auth/
│   └── LoginUseCase.kt
│
├── speech/
│   ├── RecognizeFromMicUseCase.kt
│   ├── TranslateTextUseCase.kt
│   ├── DetectLanguageUseCase.kt
│   └── SpeakTextUseCase.kt
│
├── history/
│   ├── SaveTranslationUseCase.kt
│   ├── ObserveUserHistoryUseCase.kt
│   ├── DeleteHistoryRecordUseCase.kt
│   └── RenameSessionUseCase.kt
│
├── learning/
│   ├── GenerateLearningMaterialsUseCase.kt
│   ├── GenerateQuizUseCase.kt
│   └── ParseAndStoreQuizUseCase.kt
│
└── settings/
    ├── ObserveUserSettingsUseCase.kt
    ├── SetThemeModeUseCase.kt
    ├── SetPrimaryLanguageUseCase.kt
    └── UnlockColorPaletteWithCoinsUseCase.kt
```

### Use Case Example

```kotlin
class TranslateTextUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    suspend operator fun invoke(
        text: String,
        targetLanguage: String
    ): Result<String> {
        return repository.translate(text, targetLanguage)
    }
}
```

---

## 🗄️ Data Layer (`data/`)

Implements data access and external service integration:

```
data/
├── user/
│   ├── FirebaseAuthRepository.kt           # Authentication
│   └── FirestoreProfileRepository.kt       # User profiles
│
├── history/
│   ├── FirestoreHistoryRepository.kt       # Translation history
│   └── SharedHistoryDataSource.kt          # Shared Firestore listener
│
├── settings/
│   ├── FirestoreUserSettingsRepository.kt  # User settings
│   └── SharedSettingsDataSource.kt         # Shared settings listener
│
├── wordbank/
│   ├── FirestoreWordBankRepository.kt      # Word bank data
│   └── WordBankGenerationRepository.kt     # Word generation logic
│
├── learning/
│   ├── FirestoreLearningSheetRepository.kt # Learning sheets
│   ├── FirestoreQuizRepository.kt          # Quizzes & coin system
│   ├── LearningContentRepositoryImpl.kt    # AI content generation
│   └── QuizParser.kt                       # Quiz format parser
│
├── repositories/
│   ├── FirebaseTranslationRepository.kt    # Cloud translation
│   ├── AzureSpeechRepository.kt            # Speech recognition
│   └── FirestoreFavoritesRepository.kt     # Favorites
│
├── clients/
│   ├── CloudTranslatorClient.kt            # Translation API client
│   └── CloudSpeechTokenClient.kt           # Speech token client
│
├── cloud/
│   ├── CloudGenAiClient.kt                 # Generative AI
│   └── TranslationCache.kt                 # Translation caching
│
├── azure/
│   ├── AzureSpeechProvider.kt              # Azure SDK setup
│   ├── AzureLanguageConfig.kt              # Language configs
│   └── AzureVoiceConfig.kt                 # Voice configs
│
├── ui/
│   ├── UILanguageCacheStore.kt             # UI language cache
│   └── UILanguageStateController.kt        # UI language state
│
└── di/
    ├── DaggerModule.kt                     # Hilt DI module
    └── SettingsModule.kt                   # Settings DI
```

---

## 📦 Models (`model/`)

Data classes and UI state models:

```
model/
├── user/
│   ├── User.kt                 # User data
│   ├── UserProfile.kt          # Profile info
│   ├── UserSettings.kt         # Settings model
│   └── AuthState.kt            # Auth state
│
├── ui/
│   ├── UiTextCore.kt           # UI text keys (enum)
│   ├── UiTextScreens.kt        # UI text strings
│   └── AppLanguageState.kt     # Language state
│
├── TranslationRecord.kt        # Translation entry
├── HistorySession.kt           # Conversation session
├── Quiz.kt                     # Quiz model
└── SpeechResult.kt             # Speech recognition result
```

---

## 🔧 Core Utilities (`core/`)

Shared utilities and common UI components:

```
core/
├── CommonUi.kt                 # Reusable composables
├── PermissionUi.kt             # Permission handling
├── RequireLoginGate.kt         # Login gate
├── AudioRecorder.kt            # Audio recording
├── FontSizeUtils.kt            # Font scaling
└── NavigationHelpers.kt        # Navigation utils
```

---

## 🎨 UI & Theme (`ui/`)

Design system and theming:

```
ui/
└── theme/
    ├── Theme.kt                # App theme composable
    ├── Color.kt                # Color definitions
    ├── ColorPalette.kt         # Color palettes
    ├── Type.kt                 # Typography
    └── Dimens.kt               # Dimensions
```

---

## 🔥 Backend Structure

### Firebase Cloud Functions (`fyp-backend/functions/`)

```
fyp-backend/
├── firebase.json               # Firebase configuration
├── .firebaserc                 # Project aliases
└── functions/
    ├── package.json            # Dependencies
    ├── tsconfig.json           # TypeScript config
    ├── .eslintrc.js            # Linting rules
    └── src/
        └── index.ts            # Cloud Functions
            ├── getSpeechToken          # Azure Speech token
            ├── translateText           # Translation API
            ├── detectLanguage          # Language detection
            ├── generateLearningMaterial
            └── generateQuiz
```

---

## 📊 Data Flow Example

### Translating Text

```
1. User types text in SpeechRecognitionScreen
2. SpeechViewModel.translateText() called
3. → TranslateTextUseCase.invoke()
4.   → FirebaseTranslationRepository.translate()
5.     → CloudTranslatorClient.translate()
6.       → Cloud Function: translateText
7.         → Azure Translator API
8.       ← Translation result
9.     ← Repository caches result
10.   ← Use case returns translation
11. ← ViewModel updates UI state
12. UI displays translation
```

---

## 🔐 Security & Configuration

### Firestore Collections

```
users/{uid}/
├── profile                     # User profile document
├── settings                    # User settings document
├── history/{recordId}         # Translation history
├── favorites/{recordId}       # Favorite translations
├── learning/{sheetId}         # Learning sheets
├── quizzes/{quizId}           # Quiz data
└── coins/{transactionId}      # Coin transactions
```

### Important Files

- **`google-services.json`** (app/) - Firebase configuration (NOT in repo)
- **`azure_languages.json`** (assets/) - Language mappings
- **Cloud Functions Secrets** - API keys stored in Firebase

---

## 🎓 Design Patterns Used

### Repository Pattern
Abstracts data sources from business logic

### Singleton Pattern
Shared data sources prevent duplicate Firestore listeners

### Observer Pattern
ViewModels observe data via Kotlin Flows

### Dependency Injection
Hilt provides dependencies throughout the app

### Use Case Pattern
Encapsulates business logic in reusable units

---

**Next**: [Features Overview →](Features.md)
