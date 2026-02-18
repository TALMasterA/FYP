# FYP Project File Tree

## Overview
This document provides a comprehensive tree of all important files in the FYP Android application, organized by functionality.

## Table of Contents
- [Root Files](#root-files)
- [App Module](#app-module)
- [Backend](#backend)
- [Documentation](#documentation)

---

## Root Files

```
FYP/
├── README.md                           # Project overview and setup guide
├── PROJECT_DOCUMENTATION.md            # Technical implementation details
├── ARCHITECTURE.md                     # Architecture and design patterns
├── PROJECT_TREE.md                     # This file
├── TEST_FIX_SUMMARY.md                # Test fixes documentation
├── gradle.properties                   # Gradle configuration
├── settings.gradle.kts                 # Project settings
└── build.gradle.kts                    # Root build configuration
```

---

## App Module

### Configuration

```
app/
├── build.gradle.kts                    # App-level build configuration
├── google-services.json                # Firebase configuration (mock for CI/CD)
├── proguard-rules.pro                  # ProGuard rules
└── src/
    ├── main/
    │   ├── AndroidManifest.xml         # App manifest
    │   └── res/                        # Resources (layouts, values, etc.)
    └── test/                           # Unit tests
```

### Source Code (`app/src/main/java/com/example/fyp/`)

#### Root Level

```
fyp/
├── MainActivity.kt                     # App entry point
└── AppNavigation.kt                    # Navigation graph with all routes
```

#### 1. Presentation Layer - Screens

##### Friends Feature (Complete Implementation)

```
screens/friends/
├── FriendsScreen.kt                    # Friends list, search, requests
├── FriendsViewModel.kt                 # State management for friends
├── ChatScreen.kt                       # Real-time chat with friend
├── ChatViewModel.kt                    # Chat state & translation
├── SharedInboxScreen.kt                # Received shared items
├── SharedInboxViewModel.kt             # Inbox state management
├── MyProfileScreen.kt                  # Display user profile info
└── MyProfileViewModel.kt               # Profile state management
```

**Total:** 8 files, ~2,500 lines
**Features:** Friend management, chat, translation, sharing, profile display

##### Learning Feature

```
screens/learning/
├── LearningScreen.kt                   # Learning materials browser
├── LearningViewModel.kt                # Learning state & quiz generation
├── QuizPlayScreen.kt                   # Interactive quiz player
└── QuizPlayViewModel.kt                # Quiz play state
```

##### Word Bank Feature

```
screens/wordbank/
├── WordBankScreen.kt                   # Word management UI
└── WordBankViewModel.kt                # Word bank state
```

##### History Feature

```
screens/history/
├── HistoryScreen.kt                    # Translation history
├── HistoryViewModel.kt                 # History state management
└── HistoryDiscreteTab.kt              # History tab view
```

##### Settings Feature

```
screens/settings/
├── SettingsScreen.kt                   # App settings & preferences
├── SettingsViewModel.kt                # Settings state
└── SystemNotesScreen.kt                # System notes & info
```

##### Speech Feature

```
screens/speech/
├── SpeechScreen.kt                     # Speech-to-text UI
└── SpeechViewModel.kt                  # Speech recognition state
```

##### Other Screens

```
screens/
├── home/
│   ├── HomeScreen.kt                   # Main home screen
│   └── HomeViewModel.kt                # Home state
├── login/
│   ├── LoginScreen.kt                  # Authentication
│   └── LoginViewModel.kt               # Auth state
├── favorites/
│   ├── FavoritesScreen.kt              # Favorite words
│   └── FavoritesViewModel.kt           # Favorites state
├── feedback/
│   ├── FeedbackScreen.kt               # User feedback form
│   └── FeedbackViewModel.kt            # Feedback state
└── help/
    └── HelpScreen.kt                   # Help & tutorials
```

#### 2. Domain Layer - Use Cases

##### Friends Use Cases (18 files)

```
domain/friends/
├── GetCurrentUserProfileUseCase.kt     # Get current user profile
├── ObserveFriendsUseCase.kt            # Real-time friends list
├── ObserveIncomingRequestsUseCase.kt   # Real-time friend requests
├── SearchUsersUseCase.kt               # Search users by username
├── SendFriendRequestUseCase.kt         # Send friend request
├── AcceptFriendRequestUseCase.kt       # Accept friend request
├── RejectFriendRequestUseCase.kt       # Reject friend request
├── RemoveFriendUseCase.kt              # Remove friend
├── ObserveMessagesUseCase.kt           # Real-time chat messages
├── SendMessageUseCase.kt               # Send chat message
├── MarkMessagesAsReadUseCase.kt        # Mark messages as read
├── ObserveUnreadCountUseCase.kt        # Unread message count
├── TranslateAllMessagesUseCase.kt      # Batch translate messages
├── ObserveSharedInboxUseCase.kt        # Real-time shared items
├── AcceptSharedItemUseCase.kt          # Accept shared item
├── DismissSharedItemUseCase.kt         # Dismiss shared item
├── ShareWordUseCase.kt                 # Share word to friend
└── ShareLearningMaterialUseCase.kt     # Share learning material
```

**Pattern:** Each use case handles one specific action

##### Learning Use Cases

```
domain/learning/
├── GenerateLearningMaterialsUseCase.kt # Generate learning sheets
├── GenerateQuizUseCase.kt              # Generate quiz
├── ParseAndStoreQuizUseCase.kt         # Parse & store quiz
├── CoinEligibility.kt                  # Coin reward eligibility
├── GenerationEligibility.kt            # Generation eligibility
├── LearningSheetsRepository.kt         # Repository interface
├── LearningContentRepository.kt        # Content repository interface
├── QuizRepository.kt                   # Quiz repository interface
└── QuizGenerationRepository.kt         # Quiz generation interface
```

##### Speech Use Cases

```
domain/speech/
├── RecognizeFromMicUseCase.kt          # Recognize speech from mic
├── RecognizeWithAutoDetectUseCase.kt   # Auto-detect language & recognize
├── StartContinuousConversationUseCase.kt # Start continuous recognition
├── SpeakTextUseCase.kt                 # Text-to-speech
├── TranslateTextUseCase.kt             # Translate text
├── TranslateBatchUseCase.kt            # Batch translation
└── DetectLanguageUseCase.kt            # Detect language
```

##### Other Use Cases

```
domain/
├── auth/
│   └── LoginUseCase.kt                 # User authentication
├── history/
│   ├── ObserveSessionNamesUseCase.kt   # History session names
│   ├── SaveRecordUseCase.kt            # Save translation record
│   ├── DeleteHistoryRecordUseCase.kt   # Delete record
│   └── DeleteSessionUseCase.kt         # Delete session
├── settings/
│   ├── UnlockColorPaletteWithCoinsUseCase.kt # Unlock color palette
│   └── (other settings use cases)
├── feedback/
│   └── SubmitFeedbackUseCase.kt        # Submit feedback
└── ocr/
    └── ProcessImageForTextUseCase.kt   # OCR processing
```

#### 3. Data Layer - Repositories

##### Friends Repositories (6 files)

```
data/friends/
├── FriendsRepository.kt                # Friends repository interface
├── FirestoreFriendsRepository.kt       # Friends implementation
├── ChatRepository.kt                   # Chat repository interface
├── FirestoreChatRepository.kt          # Chat implementation
├── SharingRepository.kt                # Sharing repository interface
└── FirestoreSharingRepository.kt       # Sharing implementation
```

**Total:** ~3,500 lines
**Features:** All friend system data operations

##### Learning Repositories

```
data/learning/
├── FirestoreLearningSheetsRepository.kt # Learning sheets data
├── FirestoreLearningContentRepository.kt # Learning content data
├── FirestoreQuizRepository.kt          # Quiz data
└── FirestoreQuizGenerationRepository.kt # Quiz generation data
```

##### Other Data Layer

```
data/
├── azure/
│   ├── AzureSpeechRecognizer.kt        # Azure Speech SDK
│   └── AzureTranslationRepository.kt   # Azure Translation
├── clients/
│   ├── AzureSpeechClient.kt            # Azure Speech client
│   ├── AzureTranslationClient.kt       # Azure Translation client
│   └── FirebaseClient.kt               # Firebase client
├── cloud/
│   └── CloudFunctionsRepository.kt     # Cloud Functions calls
├── di/
│   └── DaggerModule.kt                 # Hilt DI configuration
├── feedback/
│   └── FirestoreFeedbackRepository.kt  # Feedback data
├── history/
│   └── FirestoreHistoryRepository.kt   # History data
├── settings/
│   └── FirestoreUserSettingsRepository.kt # Settings data
├── user/
│   ├── FirebaseAuthRepository.kt       # Authentication
│   └── FirestoreUserRepository.kt      # User profile data
├── wordbank/
│   ├── FirestoreFavoritesRepository.kt # Favorites data
│   └── FirestoreCustomWordsRepository.kt # Custom words data
├── ocr/
│   └── AzureOCRRepository.kt           # OCR data
├── ui/
│   ├── FirebaseTranslationRepository.kt # UI text translation
│   └── UiTextRepository.kt             # UI text caching
└── repositories/
    └── (legacy/shared repositories)
```

#### 4. Model Layer - Data Models

##### Friends Models (5 files)

```
model/friends/
├── FriendRequest.kt                    # Friend request model
├── FriendMessage.kt                    # Chat message model
├── FriendRelation.kt                   # Friendship model
├── PublicUserProfile.kt                # Public user profile
└── SharedItem.kt                       # Shared item model
```

##### User Models

```
model/user/
├── AuthState.kt                        # Authentication state
└── UserSettings.kt                     # User settings model
```

##### UI Models

```
model/ui/
├── UiTextCore.kt                       # Core UI text keys (71 keys)
└── UiTextScreens.kt                    # Screen-specific text keys
```

##### Other Models

```
model/
├── ValueTypes.kt                       # Type-safe value classes:
│                                       #   - UserId
│                                       #   - Username
│                                       #   - LanguageCode
│                                       #   - RecordId
│                                       #   - SessionId
│                                       #   - PaletteId
│                                       #   - VoiceName
│                                       #   - DeploymentName
├── TranslationRecord.kt                # Translation history record
├── CustomWord.kt                       # Custom word model
└── (other models)
```

#### 5. UI Layer - Components & Theme

##### Reusable Components

```
ui/components/
├── StandardComponents.kt               # Standard UI components
├── EmptyStateView.kt                   # Empty state displays
├── LoadingSkeletons.kt                 # Loading skeletons
├── AnimatedComponents.kt               # Animated components
└── FriendSelectorDialog.kt             # Friend picker dialog
```

##### Theme

```
ui/theme/
├── Theme.kt                            # Material3 theme setup
├── Color.kt                            # Color definitions
├── ColorPalette.kt                     # 11 color palettes
├── Type.kt                             # Typography system
├── Dimens.kt                           # Dimension constants
└── ThemeHelper.kt                      # Theme helper functions
```

#### 6. Utilities & Core

```
utils/
└── ErrorMessageMapper.kt               # Error message mapping

core/
└── (core functionality)
```

---

## Backend

```
fyp-backend/
├── firestore.rules                     # Firestore security rules
├── firestore.indexes.json              # Firestore indexes
└── functions/
    └── src/
        └── index.ts                    # Cloud Functions
```

### Firestore Collections

1. **users** - User profiles
2. **usernames** - Username registry (unique)
3. **user_search** - User search index
4. **friend_requests** - Friend requests (pending/accepted/rejected)
5. **chats** - Chat messages between friends
6. **shared_inbox** - Shared items (words, materials)
7. **favorites** - Favorite words
8. **customWords** - User's custom words
9. **history** - Translation history records
10. **sessions** - History session metadata
11. **learning_sheets** - Learning materials
12. **quizzes** - Generated quizzes
13. **ui_texts** - Cached UI translations

---

## Documentation

```
Documentation/
├── README.md                           # Project overview & setup
├── PROJECT_DOCUMENTATION.md            # Technical details (889 lines)
│   ├── Friend System Implementation
│   ├── Workflow Test Fixes
│   ├── Database Optimizations
│   └── My Profile Feature
├── ARCHITECTURE.md                     # Architecture guide (this doc)
├── PROJECT_TREE.md                     # File tree (this file)
└── TEST_FIX_SUMMARY.md                # Test fixes summary
```

---

## Statistics

### Code Metrics

**Total Files:** 210 Kotlin files
**Total Directories:** 46
**Lines of Code:** ~15,000+

### By Feature

#### Friend System (Largest Feature)
- **Files:** 36
- **Lines:** ~7,500
- **Models:** 5
- **Repositories:** 6 (3 interfaces + 3 implementations)
- **Use Cases:** 18
- **ViewModels:** 4
- **Screens:** 4
- **UI Text Keys:** 71

#### Learning System
- **Files:** ~25
- **Repositories:** 4
- **Use Cases:** 8+
- **Screens:** 2

#### Speech System
- **Files:** ~15
- **Use Cases:** 7
- **Screens:** 1

### By Layer

**Presentation Layer (screens/):** ~80 files
**Domain Layer (domain/):** ~40 files
**Data Layer (data/):** ~50 files
**Models (model/):** ~20 files
**UI Components:** ~15 files

---

## Important File Relationships

### Friend System Data Flow

```
FriendsScreen.kt
    ↓
FriendsViewModel.kt
    ↓
ObserveFriendsUseCase.kt
    ↓
FriendsRepository.kt (interface)
    ↓
FirestoreFriendsRepository.kt (implementation)
    ↓
Firestore Database
```

### Chat Feature Data Flow

```
ChatScreen.kt
    ↓
ChatViewModel.kt
    ↓ ↓ ↓
    ↓ ObserveMessagesUseCase.kt → ChatRepository → FirestoreChatRepository
    ↓ SendMessageUseCase.kt → ChatRepository → FirestoreChatRepository
    ↓ TranslateAllMessagesUseCase.kt → TranslationRepository
    ↓
Firestore Database / Azure Translate API
```

### Shared Components Usage

```
EmptyStateView.kt
    ↓ used by
    ├── FriendsScreen.kt (NoFriends)
    ├── ChatScreen.kt (NoMessages)
    ├── SharedInboxScreen.kt (NoSharedItems)
    ├── HistoryScreen.kt (NoHistory)
    └── (other screens)

FriendSelectorDialog.kt
    ↓ used by
    ├── WordBankScreen.kt (share words - future)
    └── LearningScreen.kt (share materials - future)
```

---

## Key Files to Know

### Essential Configuration
1. `app/build.gradle.kts` - Dependencies & config
2. `app/google-services.json` - Firebase config
3. `fyp-backend/firestore.rules` - Security rules

### Entry Points
1. `MainActivity.kt` - App entry point
2. `AppNavigation.kt` - Navigation routes
3. `data/di/DaggerModule.kt` - DI setup

### Core Architecture
1. `ARCHITECTURE.md` - Architecture guide
2. `PROJECT_DOCUMENTATION.md` - Technical details
3. `README.md` - Setup guide

### Reference Implementation
- **Friend System** - Complete implementation example
  - Clean architecture
  - All layers implemented
  - Database optimizations
  - Full UI/UX
  - Comprehensive testing

---

## Finding Files

### By Feature
- **Friends:** Look in `screens/friends/`, `domain/friends/`, `data/friends/`, `model/friends/`
- **Learning:** Look in `screens/learning/`, `domain/learning/`, `data/learning/`
- **Speech:** Look in `screens/speech/`, `domain/speech/`, `data/azure/`

### By Layer
- **UI:** `screens/[feature]/` and `ui/components/`
- **Business Logic:** `domain/[feature]/`
- **Data:** `data/[feature]/`
- **Models:** `model/[feature]/`

### By Type
- **ViewModels:** `screens/[feature]/[Feature]ViewModel.kt`
- **Screens:** `screens/[feature]/[Feature]Screen.kt`
- **Use Cases:** `domain/[feature]/[Action][Noun]UseCase.kt`
- **Repositories:** `data/[feature]/[Firestore][Feature]Repository.kt`

---

## Maintenance Notes

### When Adding New Features

1. Create model in `model/[feature]/`
2. Create repository interface in `domain/[feature]/`
3. Implement repository in `data/[feature]/`
4. Create use cases in `domain/[feature]/`
5. Create ViewModel in `screens/[feature]/`
6. Create Screen in `screens/[feature]/`
7. Add route in `AppNavigation.kt`
8. Add UI text keys in `model/ui/`
9. Update this document

### When Refactoring

1. Follow existing patterns (see Friend System)
2. Maintain layer separation
3. Update documentation
4. Run tests

---

## Version

Last Updated: February 18, 2026
Version: 1.0

This file tree should be updated whenever significant file structure changes are made.
