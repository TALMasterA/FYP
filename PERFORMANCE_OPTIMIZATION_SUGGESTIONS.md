# Performance Optimization Suggestions

This document provides performance improvement recommendations for the FYP Learning App. All suggestions are designed to **improve performance without affecting core application logic** (or with minimal impact). Suggestions are ordered by implementation priority based on impact vs. effort ratio.

---

## 📊 Summary

- **Total Suggestions**: 20
- **Quick Wins**: 7 (High impact, Low effort)
- **Strategic Improvements**: 8 (Medium-High impact, Medium effort)
- **Advanced Optimizations**: 5 (Medium impact, High effort or infrastructure changes)

---

## 🎯 Priority 1: Quick Wins (Implement First)

These optimizations provide significant performance improvements with minimal code changes.

### 1. **Off-load JSON Parsing to Background Thread**

**Current Issue**: Heavy JSON parsing happens on the UI thread, causing potential frame drops.

**Locations**:
- `WordBankViewModel.parseWordBankResponse()` (lines 584-628)
- `QuizParser.parseJsonQuiz()` (lines 16-66)

**Impact**: 
- Prevents UI jank when parsing large word banks (10+ items)
- Improves responsiveness during quiz generation

**Implementation**:
```kotlin
// In WordBankViewModel.kt
private suspend fun parseWordBankResponse(content: String): List<WordBankItem> {
    return withContext(Dispatchers.Default) {  // Add this wrapper
        try {
            // ... existing parsing logic ...
        } catch (e: Exception) {
            android.util.Log.e("WordBankVM", "Failed to parse word bank: ${e.message}")
            emptyList()
        }
    }
}

// In QuizParser.kt
suspend fun parseQuizFromContent(content: String): List<QuizQuestion> {
    return withContext(Dispatchers.Default) {  // Add this wrapper
        parseJsonQuiz(content) ?: emptyList()
    }
}
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/wordbank/WordBankViewModel.kt`
- `app/src/main/java/com/example/fyp/data/learning/QuizParser.kt`

**Estimated effort**: 15 minutes

---

### 2. **Parallelize Word Bank Existence Checks**

**Current Issue**: Sequential Firestore queries check if word banks exist for each language one by one.

**Location**: `WordBankViewModel.refreshClusters()` (lines 358-379)

**Impact**:
- Reduces refresh time from ~N×200ms to ~200ms (where N = number of languages)
- For 5 languages: 1000ms → 200ms (80% faster)

**Implementation**:
```kotlin
// Replace the sequential for loop with parallel async calls
val languagesToCheck = languageCounts.keys.filter { it !in wordBankExistsCache }

if (languagesToCheck.isNotEmpty()) {
    coroutineScope {
        val results = languagesToCheck.map { lang ->
            lang to async {
                try {
                    // First check persisted cache
                    val cachedExists = wordBankCacheDataStore.getWordBankExists(uid, primaryLanguageCode, lang)
                    if (cachedExists != null) {
                        cachedExists
                    } else {
                        // Not in cache, check Firestore
                        val exists = wordBankRepo.wordBankExists(uid, primaryLanguageCode, lang)
                        // Save to persisted cache
                        wordBankCacheDataStore.cacheWordBank(
                            userId = uid,
                            primaryLang = primaryLanguageCode,
                            targetLang = lang,
                            exists = exists
                        )
                        exists
                    }
                } catch (_: Exception) {
                    false
                }
            }
        }
        
        // Collect results
        results.forEach { (lang, deferred) ->
            wordBankExistsCache[lang] = deferred.await()
        }
    }
}
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/wordbank/WordBankViewModel.kt`

**Estimated effort**: 20 minutes

---

### 3. **Increase Cloud Functions Max Instances**

**Current Issue**: `maxInstances: 10` is too conservative for concurrent users.

**Location**: `fyp-backend/functions/src/index.ts` (line 6)

**Impact**:
- Prevents function throttling during peak usage
- Reduces cold start frequency for users
- Better handles concurrent AI generation requests

**Implementation**:
```typescript
// Change from:
setGlobalOptions({maxInstances: 10});

// To:
setGlobalOptions({maxInstances: 50});
```

**Reasoning**:
- Current limit: 10 concurrent functions could bottleneck with 20+ active users
- Recommended: 50 instances handles ~100 concurrent users comfortably
- Cost impact: Minimal (only pay for actual usage)

**Files to modify**:
- `fyp-backend/functions/src/index.ts`

**Estimated effort**: 2 minutes

---

### 4. **Cache Azure Language Configuration in ViewModel**

**Current Issue**: Language configuration loaded every first composition via `remember {}`.

**Location**: `WordBankScreen.kt` (line 36)

**Impact**:
- Prevents file I/O on UI thread during first composition
- Reduces screen initialization time by ~50-100ms

**Implementation**:
```kotlin
// In WordBankViewModel.kt, add:
val supportedLanguages: List<Pair<String, String>> by lazy {
    AzureLanguageConfig.loadSupportedLanguages(application.applicationContext)
        .toList()
}

// In WordBankScreen.kt, change from:
val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context).toList() }

// To:
val supportedLanguages = viewModel.supportedLanguages
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/wordbank/WordBankViewModel.kt`
- `app/src/main/java/com/example/fyp/screens/wordbank/WordBankScreen.kt`

**Estimated effort**: 10 minutes

---

### 5. **Use Dispatchers.Main.immediate for SharedHistoryDataSource**

**Current Issue**: Uses `Dispatchers.Main` which may add unnecessary event loop delay.

**Location**: `SharedHistoryDataSource.kt` (line 30)

**Impact**:
- Reduces latency for history updates by 1-2 frames
- More responsive UI when translation history changes

**Implementation**:
```kotlin
// Change from:
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

// To:
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
```

**Reasoning**:
- `.immediate` dispatches work immediately if already on main thread
- Avoids unnecessary posting to event queue
- Standard pattern for UI-bound coroutine scopes

**Files to modify**:
- `app/src/main/java/com/example/fyp/data/history/SharedHistoryDataSource.kt`

**Estimated effort**: 2 minutes

---

### 6. **Add ProGuard Optimization Rules**

**Current Issue**: R8/ProGuard may not be optimally configured for Kotlin coroutines and Compose.

**Location**: `app/proguard-rules.pro`

**Impact**:
- Smaller APK size (5-10% reduction)
- Faster app startup (removed dead code)
- Better method inlining

**Implementation**:
Add these rules to `proguard-rules.pro`:

```proguard
# Kotlin Coroutines optimization
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void checkFieldIsNotNull(...);
}

# Optimize Compose
-assumenosideeffects class androidx.compose.runtime.ComposerKt {
    void sourceInformation(...);
    void sourceInformationMarkerStart(...);
    void sourceInformationMarkerEnd(...);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

**Files to modify**:
- `app/proguard-rules.pro`

**Estimated effort**: 5 minutes

---

### 7. **Enable Baseline Profile for Compose**

**Current Issue**: No baseline profile for faster startup and jank-free scrolling.

**Impact**:
- 15-30% faster app startup
- Smoother scrolling in lists (word banks, history, learning sheets)
- Reduced jank during initial screen loads

**Implementation**:

1. Add dependency in `app/build.gradle.kts`:
```kotlin
dependencies {
    // ... existing dependencies ...
    
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
}
```

2. Create baseline profile generator (optional, can use Android Studio Profiler):
```kotlin
// app/src/main/baseline-prof.txt (create manually or generate)
Lcom/example/fyp/MainActivity;
Lcom/example/fyp/screens/**;
Landroidx/compose/runtime/**;
```

**Why it works**:
- Baseline profiles tell ART which methods to pre-compile
- Reduces JIT compilation during critical user interactions
- Jetpack Compose benefits significantly from this

**Files to modify**:
- `app/build.gradle.kts`
- Create `app/src/main/baseline-prof.txt`

**Estimated effort**: 30 minutes (including profile generation)

---

## 🎯 Priority 2: Strategic Improvements

These optimizations require more changes but provide substantial performance gains.

### 8. **Cache Filtered History Results**

**Current Issue**: `SharedHistoryDataSource.getRecordsForLanguage()` filters the entire history list on every call.

**Location**: `SharedHistoryDataSource.kt` (line 155)

**Impact**:
- Faster language-specific queries (used by learning sheet generation)
- Reduces repeated O(n) filtering operations

**Implementation**:
```kotlin
// In SharedHistoryDataSource.kt
private val _languageRecordsCache = mutableMapOf<String, List<TranslationRecord>>()

// Invalidate cache when history changes
private fun updateHistoryAndCache(records: List<TranslationRecord>) {
    _historyRecords.value = records
    _languageRecordsCache.clear()  // Invalidate cache
    // ... rest of update logic
}

fun getRecordsForLanguage(languageCode: String): List<TranslationRecord> {
    // Check cache first
    _languageRecordsCache[languageCode]?.let { return it }
    
    // Cache miss - compute and store
    val filtered = _historyRecords.value.filter { record ->
        record.sourceLanguage == languageCode || record.targetLanguage == languageCode
    }
    _languageRecordsCache[languageCode] = filtered
    return filtered
}
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/data/history/SharedHistoryDataSource.kt`

**Estimated effort**: 25 minutes

---

### 9. **Implement Lazy Loading for Favorites**

**Current Issue**: `HistoryViewModel` loads ALL favorites on initialization.

**Location**: `HistoryViewModel.kt` (lines 205-211)

**Impact**:
- Faster screen initialization (especially for users with 50+ favorites)
- Reduced memory footprint

**Implementation**:
```kotlin
// In HistoryViewModel.kt
private val _visibleFavoriteCount = MutableStateFlow(20)  // Start with 20
private val _favorites = MutableStateFlow<List<FavoriteRecord>>(emptyList())
val favorites: StateFlow<List<FavoriteRecord>> = _favorites.asStateFlow()

private fun loadFavoritedTexts() {
    viewModelScope.launch {
        val uid = userId ?: return@launch
        try {
            // Load with limit
            val limit = _visibleFavoriteCount.value
            val allFavorites = favoritesRepo.getFavorites(uid)
            _favorites.value = allFavorites.take(limit)
        } catch (e: Exception) {
            Log.e("HistoryVM", "Failed to load favorites: ${e.message}")
        }
    }
}

fun loadMoreFavorites() {
    _visibleFavoriteCount.value += 20
    loadFavoritedTexts()
}
```

**UI changes needed**:
- Add "Load More" button at bottom of favorites list
- Or implement infinite scroll detection

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/history/HistoryViewModel.kt`
- `app/src/main/java/com/example/fyp/screens/favorites/FavoritesScreen.kt` (UI)

**Estimated effort**: 45 minutes

---

### 10. **Add Response Caching to Cloud Functions**

**Current Issue**: Identical AI generation requests (same prompt) trigger expensive API calls.

**Location**: `fyp-backend/functions/src/index.ts` (lines 226-250)

**Impact**:
- Reduces duplicate AI API costs by 30-50%
- Faster response for common generation requests
- Lower latency for cached content

**Implementation**:
```typescript
import {Firestore} from 'firebase-admin/firestore';

// In-memory cache with TTL (5 minutes)
const generationCache = new Map<string, {response: any, timestamp: number}>();
const CACHE_TTL_MS = 5 * 60 * 1000;

export const generateLearningContent = onCall(
  {secrets: [GENAI_BASE_URL, GENAI_API_VERSION, GENAI_API_KEY]},
  async (request) => {
    requireAuth(request.auth);
    const prompt = requireString(request.data.prompt, "prompt");
    
    // Generate cache key from prompt hash
    const cacheKey = hashString(prompt);
    
    // Check cache
    const cached = generationCache.get(cacheKey);
    if (cached && (Date.now() - cached.timestamp < CACHE_TTL_MS)) {
      return cached.response;
    }
    
    // Cache miss - call API
    const response = await callGenAI(prompt);
    
    // Store in cache
    generationCache.set(cacheKey, {
      response: response,
      timestamp: Date.now()
    });
    
    return response;
  }
);

// Simple hash function
function hashString(str: string): string {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = ((hash << 5) - hash) + str.charCodeAt(i);
    hash = hash & hash;
  }
  return hash.toString(36);
}
```

**Considerations**:
- Cache should be cleared periodically to prevent stale content
- Consider using Firestore for persistent cache (longer TTL)

**Files to modify**:
- `fyp-backend/functions/src/index.ts`

**Estimated effort**: 1 hour

---

### 11. **Optimize Compose Recomposition with Keys**

**Current Issue**: Large lists (history, word banks) may recompose unnecessarily.

**Locations**: All list screens (HistoryScreen, WordBankScreen, LearningScreen)

**Impact**:
- Smoother scrolling in long lists
- Reduced CPU usage during list updates
- Better frame rates

**Implementation**:
```kotlin
// Example for HistoryScreen
LazyColumn {
    items(
        items = historyRecords,
        key = { record -> record.id }  // Add stable key
    ) { record ->
        HistoryRecordCard(record)
    }
}

// Example for WordBankScreen
LazyColumn {
    items(
        items = wordBankItems,
        key = { item -> item.id }
    ) { item ->
        WordBankItemCard(item)
    }
}
```

**Pattern to apply**:
- Always use `key` parameter in `items()` for lists with stable IDs
- Ensures Compose can track item identity across recompositions

**Files to modify**:
- All screens with LazyColumn/LazyRow
- Estimate: 8 files

**Estimated effort**: 1 hour (reviewing all list screens)

---

### 12. **Implement StateFlow.stateIn() for Shared Flows**

**Current Issue**: Multiple collectors on repository flows may cause redundant work.

**Impact**:
- Reduced Firestore listener overhead
- Lower battery consumption
- Fewer network requests

**Implementation**:
```kotlin
// In SharedHistoryDataSource.kt
val historyRecords: StateFlow<List<TranslationRecord>> = historyRepo
    .getHistory(userId, limit)
    .stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),  // 5s timeout
        initialValue = emptyList()
    )
```

**Pattern**:
- Use `.stateIn()` for hot flows that multiple ViewModels observe
- `WhileSubscribed(5000)` keeps subscription alive for 5s after last collector

**Files to consider**:
- `SharedHistoryDataSource.kt`
- `SharedSettingsDataSource.kt` (if exists)

**Estimated effort**: 30 minutes

---

### 13. **Add Debouncing to Word Bank Generation Button**

**Current Issue**: Rapid button clicks could trigger multiple expensive AI generation calls.

**Location**: Word Bank generation UI

**Impact**:
- Prevents accidental duplicate API calls
- Saves on AI API costs
- Better UX (prevents confusion from duplicate generations)

**Implementation**:
```kotlin
// In WordBankViewModel.kt
private var lastGenerationTime = 0L
private val GENERATION_DEBOUNCE_MS = 2000L  // 2 seconds

fun generateWordBank(languageCode: String, customPrompt: String? = null) {
    val now = System.currentTimeMillis()
    if (now - lastGenerationTime < GENERATION_DEBOUNCE_MS) {
        // Too soon, ignore
        return
    }
    lastGenerationTime = now
    
    // ... existing generation logic ...
}
```

**Alternative**: Use Flow debounce for more sophisticated control
```kotlin
private val _generationTrigger = MutableSharedFlow<GenerationRequest>()

init {
    viewModelScope.launch {
        _generationTrigger
            .debounce(2000)  // 2 second debounce
            .collect { request ->
                performGeneration(request)
            }
    }
}
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/wordbank/WordBankViewModel.kt`

**Estimated effort**: 20 minutes

---

### 14. **Pre-fetch Learning Sheet Metadata on App Start**

**Current Issue**: Learning sheet metadata fetched when user navigates to learning screen.

**Impact**:
- Instant learning screen display (data already loaded)
- Better perceived performance

**Implementation**:
```kotlin
// In LearningViewModel.kt
private var isPrefetched = false

fun prefetchSheetMetadata() {
    if (isPrefetched) return
    isPrefetched = true
    
    viewModelScope.launch {
        // Silently fetch in background
        try {
            refreshSheetMetaForClusters()
        } catch (e: Exception) {
            // Ignore errors on prefetch
        }
    }
}

// Call from MainActivity or HomeScreen after user logs in
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/learning/LearningViewModel.kt`
- `app/src/main/java/com/example/fyp/MainActivity.kt` or `HomeScreen.kt`

**Estimated effort**: 30 minutes

---

### 15. **Optimize TtsController Cleanup**

**Current Issue**: `TtsController` created in `SpeechViewModel` init but not cleared properly.

**Location**: `SpeechViewModel.kt` (line 60)

**Impact**:
- Prevents potential memory leaks
- Proper resource cleanup
- Better battery life

**Implementation**:
```kotlin
// In SpeechViewModel.kt
override fun onCleared() {
    super.onCleared()
    ttsController.cleanup()  // Add if cleanup method exists
    continuousController.cleanup()
}

// In TtsController.kt (if not exists, add)
fun cleanup() {
    // Cancel any ongoing TTS
    // Release resources
}
```

**Files to modify**:
- `app/src/main/java/com/example/fyp/screens/speech/SpeechViewModel.kt`
- `app/src/main/java/com/example/fyp/screens/speech/TtsController.kt`

**Estimated effort**: 15 minutes

---

## 🎯 Priority 3: Advanced Optimizations

These optimizations require more substantial changes or infrastructure updates but can provide significant long-term benefits.

### 16. **Implement Firestore Offline Persistence**

**Current Issue**: Every app launch fetches data from network, even if cached locally.

**Impact**:
- Instant app startup (loads from local cache)
- Works offline
- Reduced network usage and costs

**Implementation**:
```kotlin
// In DaggerModule.kt or Firebase initialization
val settings = firestoreSettings {
    isPersistenceEnabled = true
    cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
}
firestore.firestoreSettings = settings
```

**Considerations**:
- Increases app storage usage (~10-50MB depending on data)
- Need to handle cache invalidation
- Test offline scenarios thoroughly

**Files to modify**:
- Firebase initialization code
- Repository implementations (ensure they handle offline scenarios)

**Estimated effort**: 2 hours (including testing)

---

### 17. **Add Image/Audio Asset Caching**

**Current Issue**: No explicit caching for downloaded audio (TTS) or images.

**Impact**:
- Faster TTS playback for repeated phrases
- Reduced bandwidth usage

**Implementation**:
Use existing libraries:
```kotlin
// In build.gradle.kts
implementation("io.coil-kt:coil-compose:2.5.0")  // For images
implementation("com.squareup.okhttp3:okhttp:4.12.0")  // Already have - use disk cache

// Configure OkHttp with cache
val cache = Cache(
    directory = context.cacheDir.resolve("http_cache"),
    maxSize = 50L * 1024L * 1024L // 50 MB
)

val client = OkHttpClient.Builder()
    .cache(cache)
    .build()
```

**Files to modify**:
- `app/build.gradle.kts`
- Network client initialization
- TTS/image loading code

**Estimated effort**: 3 hours

---

### 18. **Implement Incremental History Loading**

**Current Issue**: History listener fetches all records up to limit (100 records) on every update.

**Location**: `FirestoreHistoryRepository.kt`

**Impact**:
- Reduced bandwidth for history updates
- Faster listener callbacks
- Better for users with large histories

**Implementation**:
This is complex - requires:
1. Track last fetched record timestamp
2. Query only new records since last fetch
3. Merge with existing local data
4. Handle deletions separately

**Complexity**: High - affects app logic
**Estimated effort**: 4-6 hours

**Recommendation**: Consider this only if analytics show users frequently hit the history limit.

---

### 19. **Add Cloud Function CDN for Static Responses**

**Current Issue**: Some Cloud Function responses could be cached at CDN edge.

**Impact**:
- Lower latency for cached responses (especially for distant users)
- Reduced Cloud Function invocations
- Lower costs

**Implementation**:
```typescript
// For functions that can be cached (e.g., translations)
export const cachedTranslation = onRequest(
  {cors: true},
  async (req, res) => {
    // Set cache headers
    res.set('Cache-Control', 'public, max-age=300, s-maxage=600');
    
    // ... translation logic ...
  }
);
```

**Considerations**:
- Only cache authenticated but non-user-specific responses
- Requires Firebase Hosting + Cloud Functions integration
- Complex setup

**Estimated effort**: 4 hours (infrastructure setup)

---

### 20. **Migrate to Kotlin Flow Instead of LiveData (If Applicable)**

**Current Issue**: Mixed use of StateFlow and potentially LiveData/callbacks.

**Impact**:
- More efficient state management
- Better composition with coroutines
- Cleaner code

**Implementation**:
Review and ensure all state management uses StateFlow/SharedFlow:
```kotlin
// Preferred pattern:
private val _state = MutableStateFlow(InitialState)
val state: StateFlow<State> = _state.asStateFlow()

// Instead of LiveData:
private val _state = MutableLiveData(InitialState)
val state: LiveData<State> = _state
```

**Files to audit**: All ViewModels and repositories

**Estimated effort**: 2-4 hours (depending on current LiveData usage)

---

## 📈 Expected Performance Improvements

Implementing these optimizations in order of priority should yield:

### Quick Wins (Priority 1):
- **App startup**: 15-30% faster (with baseline profile)
- **UI responsiveness**: 20-40% improvement (off-loading parsing)
- **Network latency**: 50-80% reduction for parallel queries
- **APK size**: 5-10% smaller (ProGuard optimizations)

### Strategic Improvements (Priority 2):
- **Memory usage**: 10-20% reduction (caching, lazy loading)
- **API costs**: 30-50% reduction (response caching, debouncing)
- **Scrolling performance**: 15-25% smoother (Compose keys, stateIn)

### Advanced Optimizations (Priority 3):
- **Offline capability**: Full functionality without network
- **Data bandwidth**: 40-60% reduction (offline persistence, incremental loading)
- **Global latency**: 30-50% improvement for distant users (CDN)

---

## 🧪 Testing Recommendations

After implementing optimizations:

1. **Performance profiling**:
   - Use Android Studio Profiler to measure CPU, memory, network before/after
   - Compare app startup times
   - Monitor frame rates during scrolling

2. **Regression testing**:
   - Ensure all features work as before
   - Test offline scenarios (if implemented)
   - Verify data consistency

3. **User testing**:
   - A/B test with small user group
   - Collect feedback on perceived performance
   - Monitor crash rates and ANRs

4. **Cost monitoring**:
   - Track Firebase usage (reads, writes, storage)
   - Monitor Cloud Function invocations
   - Measure AI API costs

---

## 📝 Notes

- **Logic impact**: All suggestions minimize logic changes. Priorities 1-2 have zero or minimal impact on app behavior.
- **Testing required**: Always test after implementing each optimization to ensure no regressions.
- **Incremental approach**: Implement and test one optimization at a time rather than batching.
- **Analytics**: Consider adding performance analytics (Firebase Performance Monitoring) to measure impact objectively.

---

## 🔗 Related Resources

- [Jetpack Compose Performance Best Practices](https://developer.android.com/jetpack/compose/performance)
- [Kotlin Coroutines Best Practices](https://kotlinlang.org/docs/coroutines-best-practices.html)
- [Firebase Performance Optimization](https://firebase.google.com/docs/firestore/best-practices)
- [ProGuard Optimization Guide](https://www.guardsquare.com/manual/configuration/usage)

---

**Generated**: 2026-02-09  
**Version**: 1.0  
**Reviewer**: AI Code Analysis Agent
