# App Improvement Suggestions

## 1. Database Read Optimizations (Implemented in This PR)

### 1.1 Field Projection on `getLanguageCounts()`
**File:** `FirestoreHistoryRepository.kt`
**Change:** Added `.select("sourceLang", "targetLang")` to the query.
**Impact:** Previously, every call to `getLanguageCounts()` downloaded ALL fields (sourceText, targetText, timestamp, sessionId, etc.) for EVERY history document. With field projection, only the 2 small string fields needed are transferred. For a user with 500 records, this reduces data transfer by ~90%.

### 1.2 Eliminated Read-Before-Write in `unlockColorPalette()`
**File:** `FirestoreUserSettingsRepository.kt`
**Change:** Replaced read-then-write with `FieldValue.arrayUnion()`.
**Impact:** Saves 1 Firestore document read per palette unlock. The arrayUnion operation atomically adds the palette to the array without needing to fetch the current array first.

### 1.3 Eliminated Read-Before-Write in `setVoiceForLanguage()`
**File:** `FirestoreUserSettingsRepository.kt`
**Change:** Replaced read-then-write with `SetOptions.merge()` using a nested map.
**Impact:** Saves 1 Firestore document read per voice setting change. The merge operation only updates the specific key within the voiceSettings map.

### 1.4 `deductCoins()` Now Returns New Balance
**File:** `FirestoreQuizRepository.kt`, `ShopViewModel.kt`
**Change:** `deductCoins()` returns the new balance (Int) instead of a Boolean. Returns -1 for insufficient coins.
**Impact:** Eliminates 2 redundant `fetchUserCoinStats()` calls in ShopViewModel (one in `expandHistoryLimit()` and one in `unlockPalette()`). Each call saved 1 Firestore document read.

### 1.5 Extracted `parseSettings()` Helper
**File:** `FirestoreUserSettingsRepository.kt`
**Change:** Extracted duplicated field parsing logic from `observeUserSettings()` and `fetchUserSettings()` into a shared `parseSettings()` method.
**Impact:** No read reduction, but ensures consistency between the two methods and reduces maintenance burden when adding new settings fields.

---

## 2. Code Improvement Suggestions

### 2.1 Add Pagination to `getLanguag(eCounts)` for Very Large Histories
Currently `getLanguageCounts()` fetches ALL records (even with field projection). For users with 10,000+ records, consider:
- Using Firestore aggregation queries per language (if the set of languages is bounded)
- Storing language counts as a separate document that's updated incrementally on each history write (via Cloud Functions)

### 2.2 Consolidate `wordBankExists()` and `getWordBankHistoryCount()` into Single Read
**File:** `FirestoreWordBankRepository.kt`
Both methods read the same document independently. Consider adding a `getWordBankMetadata()` method:
```kotlin
data class WordBankMetadata(val exists: Boolean, val historyCountAtGenerate: Int)

suspend fun getWordBankMetadata(uid: String, primary: String, target: String): WordBankMetadata {
    val doc = docRef(uid, primary, target).get().await()
    return WordBankMetadata(
        exists = doc.exists(),
        historyCountAtGenerate = if (doc.exists()) (doc.getLong("historyCountAtGenerate") ?: 0).toInt() else 0
    )
}
```
This would be beneficial if any future code path calls both methods in sequence.

### 2.3 Consolidate Favorites Lookup Methods
**File:** `FirestoreFavoritesRepository.kt`
`isFavorited()`, `getFavorite()`, and `getFavoriteId()` all run the exact same query (whereEqualTo on sourceText and targetText) but return different data. Consider a single method:
```kotlin
suspend fun findFavorite(userId: String, sourceText: String, targetText: String): FavoriteRecord?
```
Then callers can derive what they need:
- `isFavorited` → `findFavorite(...) != null`
- `getFavoriteId` → `findFavorite(...)?.id`

### 2.4 Use `FieldValue.increment()` for Quiz Stats Updates
**File:** `FirestoreQuizRepository.kt` → `updateStats()`
Currently uses read-then-write. Firestore supports atomic increments:
```kotlin
statsRef.set(mapOf(
    "attemptCount" to FieldValue.increment(1),
    "lastAttemptAt" to attempt.completedAt
), SetOptions.merge())
```
However, `averageScore` and `highestScore` need the current values, so this optimization only applies to `attemptCount`. Consider restructuring to store `totalScore` (sum) and compute average client-side.

### 2.5 Add Error Logging
Several `catch` blocks silently swallow exceptions (e.g., `SharedHistoryDataSource.refreshLanguageCounts`, various ViewModel operations). Consider logging errors even when the UI doesn't show them:
```kotlin
catch (e: Exception) {
    Log.w("SharedHistoryDataSource", "Failed to refresh language counts", e)
}
```

### 2.6 Use `limitToLast` or Cursor-Based Pagination for History
**File:** `HistoryViewModel.kt` / `SharedHistoryDataSource.kt`
The history limit is currently a hard cap. If users have the "expand history" shop item, consider lazy loading with cursor-based pagination instead of fetching all at once.

### 2.7 Consider Room/DataStore Caching for Offline Support
The app currently relies entirely on Firestore for data. Adding a local Room database as a cache layer would:
- Reduce Firestore reads significantly (read from cache first)
- Enable offline mode
- Improve startup performance

This is a larger architectural change but would have the biggest long-term impact on read costs.

---

## 3. Architecture Protection for Critical Logic

The following logic areas are complex and must not be accidentally broken by future changes:

### Critical Paths:
1. **Learning screen fetch logic** (LearningViewModel)
2. **Learning materials gen/regen logic** (LearningViewModel.generateFor)
3. **Quiz gen/regen logic** (LearningViewModel.generateQuizFor)
4. **Coins earning logic** (FirestoreQuizRepository.awardCoinsIfEligible)
5. **Word bank fetch/refresh logic** (WordBankViewModel)

### 3.1 Recommendation: Extract Critical Logic into Pure Domain Functions

Move anti-cheat validation logic out of repositories and ViewModels into pure Kotlin functions that are easy to test and hard to accidentally modify:

```kotlin
// domain/learning/GenerationEligibility.kt
object GenerationEligibility {
    const val MIN_RECORDS_FOR_REGEN = 5

    fun canRegenerateMaterial(currentCount: Int, savedCount: Int): Boolean {
        return (currentCount - savedCount) >= MIN_RECORDS_FOR_REGEN
    }

    fun canRegenerateQuiz(sheetVersion: Int, quizVersion: Int): Boolean {
        return sheetVersion != quizVersion
    }
}

// domain/learning/CoinEligibility.kt
object CoinEligibility {
    const val MIN_INCREMENT_FOR_COINS = 10

    fun isEligibleForCoins(
        attemptScore: Int,
        generatedHistoryCount: Int,
        currentHistoryCount: Int?,
        lastAwardedCount: Int?,
        alreadyAwarded: Boolean
    ): Boolean {
        if (generatedHistoryCount <= 0) return false
        if (attemptScore <= 0) return false
        if (alreadyAwarded) return false
        if (currentHistoryCount != generatedHistoryCount) return false
        if (lastAwardedCount != null && generatedHistoryCount < lastAwardedCount + MIN_INCREMENT_FOR_COINS) return false
        return true
    }
}
```

**Benefits:**
- Pure functions with no side effects → easy to unit test exhaustively
- Constants defined in one place → no risk of inconsistency
- Repository only handles Firestore mechanics, not business logic decisions

### 3.2 Recommendation: Add Comprehensive Unit Tests for Anti-Cheat Logic

Create parameterized tests for each eligibility check:

```kotlin
// test/domain/learning/CoinEligibilityTest.kt
class CoinEligibilityTest {
    @Test fun `score 0 is not eligible`()
    @Test fun `already awarded is not eligible`()
    @Test fun `history count mismatch is not eligible`()
    @Test fun `first quiz for language pair is always eligible`()
    @Test fun `need 10 more records than last awarded`()
    @Test fun `exactly 10 more records is eligible`()
    @Test fun `9 more records is not eligible`()
}
```

### 3.3 Recommendation: Use `internal` Visibility for Critical Methods

Mark methods that are only used within the data/domain layer as `internal` to prevent UI layer from directly calling them:

```kotlin
// Only learning use cases should call this
internal suspend fun awardCoinsIfEligible(...)
```

### 3.4 Recommendation: Add @Deprecated Warnings for Risky Refactoring

When methods have complex pre-conditions, document them:

```kotlin
/**
 * ⚠️ ANTI-CHEAT: Do NOT modify the eligibility checks below without
 * updating the corresponding tests in CoinEligibilityTest.
 *
 * The 5 checks are:
 * 1. Version dedup (versionKey prevents double-award)
 * 2. Sheet version match (quiz count == current sheet count)
 * 3. 10+ record increment (prevents farming)
 * 4. lastAwardedCount tracking
 * 5. Score > 0 (no coins for 0 score)
 */
```

### 3.5 Recommendation: Use a Separate Kotlin Module for Core Business Logic

Create a `:domain` Gradle module that contains ONLY pure Kotlin (no Android/Firebase dependencies):

```
fyp/
├── app/           (Android UI, ViewModels, DI)
├── domain/        (Pure Kotlin: use cases, eligibility checks, models)
└── data/          (Firebase, Azure implementations)
```

**Benefits:**
- The `:domain` module compiles fast and can be tested without Android SDK
- Business logic cannot accidentally depend on Firebase internals
- Changes to Firebase SDK versions don't affect business logic
- Developers can run domain tests in <1 second

### 3.6 Recommendation: Feature Flags for Risky Changes

For future feature development, consider a simple feature flag system:

```kotlin
object FeatureFlags {
    val NEW_QUIZ_SCORING = false // Set to true when ready
    val ENHANCED_WORD_BANK = false
}
```

This allows:
- Developing new features behind flags without risking existing logic
- Quick rollback by setting flag to false
- A/B testing different logic paths
