# FYP App Architecture

## Overview
This document describes the architecture and organization of the FYP (Final Year Project) Android application - a language learning app with social features.

## Architecture Pattern
The app follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (screens/, ui/components/, ui/theme/)  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│          Domain Layer                    │
│  (domain/ - Use Cases & Repositories)    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│           Data Layer                     │
│  (data/ - Repository Implementations)    │
└─────────────────────────────────────────┘
```

## Package Structure

### Root Level (`com.example.fyp`)
- `MainActivity.kt` - App entry point
- `AppNavigation.kt` - Navigation graph and routes

### Core Packages

#### 1. **screens/** - Presentation Layer
Feature-based organization, each screen has:
- `[Feature]Screen.kt` - Composable UI
- `[Feature]ViewModel.kt` - State management
- `[Feature]UiState.kt` - UI state data classes (if complex)

**Features:**
- `friends/` - Friend system (chat, profile, inbox)
- `learning/` - Learning materials and quizzes
- `wordbank/` - Word management
- `history/` - Translation history
- `settings/` - App settings
- `speech/` - Speech-to-text features
- `home/` - Home screen
- `login/` - Authentication
- `favorites/` - Favorite words
- `feedback/` - User feedback
- `help/` - Help and tutorials

#### 2. **domain/** - Business Logic Layer
Contains use cases and repository interfaces.

**Structure:**
```
domain/
├── auth/           # Authentication use cases
├── friends/        # Friend system use cases (18 files)
├── learning/       # Learning content use cases
├── speech/         # Speech recognition/translation
├── history/        # History management
├── settings/       # Settings use cases
├── ocr/            # OCR use cases
└── feedback/       # Feedback use cases
```

**Pattern:** Each use case is a single-responsibility class:
```kotlin
class [Action][Resource]UseCase @Inject constructor(
    private val repository: [Resource]Repository
) {
    suspend operator fun invoke(...): Result<T>
}
```

#### 3. **data/** - Data Layer
Repository implementations and data sources.

**Structure:**
```
data/
├── azure/          # Azure services integration
├── clients/        # API clients (Azure, Firebase)
├── cloud/          # Cloud Functions integration
├── di/             # Dependency Injection modules
├── friends/        # Friend system repositories
├── learning/       # Learning content repositories
├── history/        # History repositories
├── settings/       # Settings repositories
├── user/           # User/Auth repositories
├── wordbank/       # Word bank repositories
├── ocr/            # OCR repositories
├── feedback/       # Feedback repositories
├── ui/             # UI text translation
└── repositories/   # Legacy/shared repositories
```

#### 4. **model/** - Data Models
Domain and data transfer objects.

**Structure:**
```
model/
├── friends/        # Friend system models (5 files)
│   ├── FriendRequest.kt
│   ├── FriendMessage.kt
│   ├── FriendRelation.kt
│   ├── PublicUserProfile.kt
│   └── SharedItem.kt
├── ui/             # UI text models
│   ├── UiTextCore.kt
│   └── UiTextScreens.kt
├── user/           # User/Auth models
└── ValueTypes.kt   # Type-safe value classes
```

#### 5. **ui/** - UI Components & Theme
Reusable UI components and theming.

**Structure:**
```
ui/
├── components/     # Reusable Composables
│   ├── StandardComponents.kt
│   ├── EmptyStateView.kt
│   ├── LoadingSkeletons.kt
│   ├── AnimatedComponents.kt
│   └── FriendSelectorDialog.kt
└── theme/          # Material3 theme
    ├── Theme.kt
    ├── Color.kt
    ├── ColorPalette.kt
    ├── Type.kt
    ├── Dimens.kt
    └── ThemeHelper.kt
```

#### 6. **utils/** - Utilities
Helper classes and extension functions.
- `ErrorMessageMapper.kt` - Error message mapping

#### 7. **core/** - Core functionality
App-wide utilities and base classes.

## Key Design Patterns

### 1. Clean Architecture
- **Domain Layer** defines interfaces
- **Data Layer** implements interfaces
- **Presentation Layer** depends only on Domain

### 2. Repository Pattern
```kotlin
// Domain layer - Interface
interface FriendsRepository {
    suspend fun searchUsers(query: String): Result<List<PublicUserProfile>>
}

// Data layer - Implementation
class FirestoreFriendsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : FriendsRepository {
    override suspend fun searchUsers(query: String): Result<List<PublicUserProfile>> {
        // Implementation
    }
}
```

### 3. MVVM (Model-View-ViewModel)
- **View:** Composable functions in `screens/`
- **ViewModel:** State management with StateFlow
- **Model:** Data classes in `model/`

### 4. Dependency Injection (Hilt)
All dependencies injected via Hilt:
```kotlin
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val observeFriendsUseCase: ObserveFriendsUseCase,
    private val authRepository: FirebaseAuthRepository
) : ViewModel()
```

### 5. Use Case Pattern
Single responsibility use cases:
```kotlin
class SendFriendRequestUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(toUserId: UserId): Result<Unit> {
        return friendsRepository.sendFriendRequest(toUserId)
    }
}
```

## Navigation

**Route Definition:**
```kotlin
sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Friends : AppScreen("friends")
    object Chat : AppScreen("chat/{friendId}/{friendUsername}/{friendDisplayName}")
    // ...
}
```

**Navigation Implementation:** AppNavigation.kt using Jetpack Compose Navigation

## Data Flow

### Typical Flow Example (Loading Friends)
```
FriendsScreen (UI)
    ↓ user action
FriendsViewModel.loadFriends()
    ↓ calls
ObserveFriendsUseCase()
    ↓ calls
FriendsRepository.observeFriends()
    ↓ implements
FirestoreFriendsRepository.observeFriends()
    ↓ reads from
Firestore Database
    ↓ emits via Flow
Back through layers to UI
```

## State Management

### ViewModel Pattern
```kotlin
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val observeFriendsUseCase: ObserveFriendsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()
    
    init {
        loadFriends()
    }
    
    private fun loadFriends() {
        viewModelScope.launch {
            observeFriendsUseCase().collect { result ->
                result.onSuccess { friends ->
                    _uiState.update { it.copy(friends = friends) }
                }
            }
        }
    }
}
```

## Testing Structure

```
app/src/test/
└── java/com/example/fyp/
    ├── domain/         # Use case tests
    └── data/           # Repository tests
```

## Feature Modules

### Friend System (Comprehensive)
The most complete feature, serves as reference implementation.

**Files:** 36
**Lines of Code:** ~7,500

**Components:**
- 5 Data Models (model/friends/)
- 6 Repositories (data/friends/)
- 18 Use Cases (domain/friends/)
- 4 ViewModels + 4 Screens (screens/friends/)
- 71 UI Text Keys (localized in 16 languages)

**Features:**
1. Friend Management - Add, remove, search
2. Chat - Real-time messaging with translation
3. Shared Inbox - Share learning materials
4. My Profile - Display and share user info

## Database (Firestore)

### Collections
- `users` - User profiles
- `usernames` - Username registry
- `user_search` - Search index
- `friend_requests` - Friend requests
- `chats` - Message threads
- `shared_inbox` - Shared items
- `favorites` - Favorite words
- `history` - Translation history
- `learning_sheets` - Learning materials
- `quizzes` - Generated quizzes

### Security
Firestore security rules in: `fyp-backend/firestore.rules`

## Performance Optimizations

### 1. Database Read Reduction (40-60%)
- Message pagination (50 messages at a time)
- Search debouncing (300ms)
- Query limits (max 100 items)

### 2. UI Optimizations
- Loading skeletons for better perceived performance
- Empty state components
- Efficient list rendering with LazyColumn

### 3. Memory Management
- Proper Flow cancellation
- ViewModel lifecycle awareness
- Job cancellation on logout

## Localization

All UI text uses the `UiTextKey` enum system:
- Defined in: `model/ui/UiTextCore.kt` and `UiTextScreens.kt`
- Translated via Azure Cognitive Services
- Cached in Firestore with 30-day TTL
- Supports 16 languages

## Adding New Features

### Step-by-Step Guide

1. **Create Data Model** (if needed)
   - Add to `model/[feature]/`
   - Use data classes with validation

2. **Create Repository Interface**
   - Add to `domain/[feature]/`
   - Define contract

3. **Implement Repository**
   - Add to `data/[feature]/`
   - Implement interface

4. **Create Use Cases**
   - Add to `domain/[feature]/`
   - One class per action

5. **Create ViewModel**
   - Add to `screens/[feature]/`
   - Manage state with StateFlow

6. **Create Screen**
   - Add to `screens/[feature]/`
   - Composable UI

7. **Add Navigation Route**
   - Update `AppNavigation.kt`

8. **Add UI Text Keys**
   - Update `model/ui/UiTextCore.kt`

9. **Update Dependency Injection**
   - Add to `data/di/DaggerModule.kt`

10. **Write Tests**
    - Add to `app/src/test/`

## Best Practices

### 1. Naming Conventions
- **Screens:** `[Feature]Screen.kt`
- **ViewModels:** `[Feature]ViewModel.kt`
- **Use Cases:** `[Verb][Noun]UseCase.kt`
- **Repositories:** `[Feature]Repository.kt` (interface), `Firestore[Feature]Repository.kt` (impl)

### 2. State Management
- Use `StateFlow` for UI state
- Single source of truth in ViewModel
- Immutable state objects

### 3. Error Handling
- Use `Result<T>` type for operations
- Map errors to user-friendly messages
- Show dismissible error messages in UI

### 4. Async Operations
- Use `suspend` functions
- Collect Flows in `viewModelScope.launch`
- Cancel jobs appropriately

### 5. UI Components
- Prefer reusable Composables
- Follow Material3 guidelines
- Keep Composables small and focused

## Security Considerations

1. **Authentication:** Firebase Authentication required for all user data
2. **Authorization:** Firestore rules enforce access control
3. **Data Validation:** Input validation at multiple layers
4. **Sensitive Data:** Never log sensitive information
5. **API Keys:** Stored in environment variables, not committed

## Configuration Files

- `app/build.gradle.kts` - App dependencies and configuration
- `app/google-services.json` - Firebase configuration (gitignored in production)
- `fyp-backend/firestore.rules` - Firestore security rules
- `fyp-backend/firestore.indexes.json` - Firestore indexes

## Documentation Files

- `README.md` - Project overview and setup
- `PROJECT_DOCUMENTATION.md` - Technical implementation details
- `ARCHITECTURE.md` - This file
- `PROJECT_TREE.md` - Detailed file tree

## Maintainers

This architecture should be maintained and updated as the project evolves. When making significant changes, update this document accordingly.

## Version

Last Updated: February 18, 2026
Version: 1.0
