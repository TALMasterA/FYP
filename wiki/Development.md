# Development Guide

Development workflow, best practices, and coding conventions for the FYP app.

---

## 🔄 Git Workflow

### Branch Strategy

```
main (production)
  ├── feature/new-feature
  ├── bugfix/issue-123
  └── hotfix/critical-fix
```

**Branch Naming**:
- `feature/description` - New features
- `bugfix/issue-number` - Bug fixes
- `hotfix/description` - Critical production fixes
- `refactor/description` - Code refactoring

### Standard Workflow

```bash
# 1. Update main branch
git checkout main
git pull --ff-only

# 2. Create feature branch
git checkout -b feature/my-feature

# 3. Make changes and commit
git add .
git commit -m "feat: add new feature"

# 4. Push to remote
git push -u origin feature/my-feature

# 5. Create Pull Request (using GitHub CLI)
gh pr create --title "Add new feature" --body "Description"

# 6. After PR merged, cleanup
git checkout main
git pull --ff-only
git branch -d feature/my-feature
```

### Commit Message Conventions

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>: <description>

[optional body]
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes

**Examples**:
```bash
feat: add voice settings screen
fix: resolve crash on empty translation
docs: update README with new features
refactor: simplify quiz generation logic
```

---

## 🏗️ Architecture Patterns

### MVVM Implementation

**ViewModel Example**:
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val myUseCase: MyUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
    
    fun onUserAction() {
        viewModelScope.launch {
            try {
                val result = myUseCase.invoke()
                _uiState.update { it.copy(data = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
```

**Screen Composable**:
```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    MyScreenContent(
        uiState = uiState,
        onAction = { viewModel.onUserAction() }
    )
}
```

---

### Use Case Pattern

**Creating a Use Case**:
```kotlin
class MyUseCase @Inject constructor(
    private val repository: MyRepository
) {
    suspend operator fun invoke(param: String): Result<MyData> {
        return try {
            val data = repository.fetchData(param)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 🎨 UI Development

### Jetpack Compose Best Practices

**State Management**:
```kotlin
// ✅ Good: Hoist state
@Composable
fun ParentScreen() {
    var text by remember { mutableStateOf("") }
    
    ChildComponent(
        text = text,
        onTextChange = { text = it }
    )
}

@Composable
fun ChildComponent(
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange
    )
}
```

**Reusable Composables**:
```kotlin
// Store in core/CommonUi.kt
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(text)
    }
}
```

---

## 📝 Adding New UI Text

### Step-by-Step Process

1. **Add Key to Enum** (`UiTextCore.kt`):
   ```kotlin
   enum class UiTextKey {
       // ... existing keys ...
       MyNewTextKey,
   }
   ```

2. **Add English Text** (`UiTextScreens.kt`):
   ```kotlin
   val BaseUiTexts = CoreUiTexts + listOf(
       // ... existing texts ...
       "My new text content",
   )
   ```

3. **Use in Composable**:
   ```kotlin
   @Composable
   fun MyScreen() {
       val text = rememberUiText(UiTextKey.MyNewTextKey)
       Text(text = text)
   }
   ```

**Important**:
- Enum order MUST match text list order
- Always add to the END to maintain order
- Test UI language switching after adding text

---

## 🗄️ Working with Firestore

### Reading Data

**Single Document**:
```kotlin
suspend fun getDocument(id: String): MyData {
    return firestore.collection("myCollection")
        .document(id)
        .get()
        .await()
        .toObject(MyData::class.java)
        ?: throw Exception("Not found")
}
```

**Real-time Listener**:
```kotlin
fun observeData(userId: String): Flow<List<MyData>> = callbackFlow {
    val listener = firestore.collection("myCollection")
        .whereEqualTo("userId", userId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val data = snapshot?.toObjects(MyData::class.java) ?: emptyList()
            trySend(data)
        }
    
    awaitClose { listener.remove() }
}
```

---

### Best Practices

**Use SharedDataSource for Common Listeners**:
```kotlin
// ✅ Good: Single shared listener
@Singleton
class SharedHistoryDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: FirebaseAuthRepository
) {
    private val historyFlow = authRepository.observeAuthState()
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else observeHistory(user.uid)
        }
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1
        )
}
```

**Field Projection for Performance**:
```kotlin
// ✅ Good: Only fetch needed fields
val snapshot = firestore.collection("data")
    .select("field1", "field2")
    .get()
    .await()
```

---

## ☁️ Cloud Functions

### Adding New Cloud Function

1. **Define in `index.ts`**:
   ```typescript
   export const myNewFunction = onCall(
     {secrets: [MY_SECRET]},
     async (request) => {
       requireAuth(request.auth);
       const param = requireString(request.data.param, "param");
       
       // Function logic here
       
       return {result: "success"};
     }
   );
   ```

2. **Deploy**:
   ```bash
   cd fyp-backend/functions
   firebase deploy --only functions:myNewFunction
   ```

3. **Call from Android**:
   ```kotlin
   suspend fun callMyFunction(param: String): String {
       return functions
           .getHttpsCallable("myNewFunction")
           .call(mapOf("param" to param))
           .await()
           .getData<Map<String, Any>>()
           ?.get("result") as? String
           ?: throw Exception("Invalid response")
   }
   ```

---

## 🧪 Testing

### Unit Testing

**ViewModel Test Example**:
```kotlin
@Test
fun `test feature works correctly`() = runTest {
    // Given
    val useCase = mockk<MyUseCase>()
    coEvery { useCase.invoke() } returns Result.success(MyData())
    val viewModel = MyViewModel(useCase)
    
    // When
    viewModel.loadData()
    
    // Then
    assertEquals(MyData(), viewModel.uiState.value.data)
}
```

---

## 🛠️ Build & Deployment

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean build

# Compile only
./gradlew compileDebugKotlin
```

### Firebase Deployment

```bash
cd fyp-backend/functions

# Deploy all functions
firebase deploy --only functions

# Deploy specific function
firebase deploy --only functions:translateText

# View logs
firebase functions:log
```

---

## 📋 Code Style & Conventions

### Kotlin Style

**Naming**:
- Classes: `PascalCase`
- Functions: `camelCase`
- Constants: `SCREAMING_SNAKE_CASE`
- Private properties: `_camelCase` (with underscore)

**Formatting**:
- 4 spaces indentation
- 100 character line limit
- Braces on same line

**Best Practices**:
- Use `val` over `var` when possible
- Prefer data classes for models
- Use sealed classes for states
- Leverage Kotlin coroutines for async

---

### Composable Conventions

```kotlin
// ✅ Good naming
@Composable
fun MyScreenContent() { }  // For full screen

@Composable
fun MyButton() { }  // For component

// ✅ Parameter order
@Composable
fun MyComponent(
    // 1. Required params
    title: String,
    
    // 2. Optional params with defaults
    subtitle: String = "",
    
    // 3. Modifiers
    modifier: Modifier = Modifier,
    
    // 4. Event handlers
    onClick: () -> Unit
) { }
```

---

## 📚 Additional Resources

- **Jetpack Compose**: [developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose)
- **Kotlin Coroutines**: [kotlinlang.org/docs/coroutines-overview](https://kotlinlang.org/docs/coroutines-overview.html)
- **Hilt**: [developer.android.com/training/dependency-injection/hilt-android](https://developer.android.com/training/dependency-injection/hilt-android)
- **Firebase**: [firebase.google.com/docs](https://firebase.google.com/docs)

---

**Next**: [Backend Setup →](Backend.md)
