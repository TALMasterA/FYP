# Codebase Review & Improvement Suggestions

**Date**: 2026-03-01
**Scope**: Full codebase review of Talk & Learn Translator Android FYP app

---

## Table of Contents

1. [Performance Issues](#1-performance-issues)
2. [Database Read/Write Optimization](#2-database-readwrite-optimization)
3. [Error Handling](#3-error-handling)
4. [Code Reuse / DRY Violations](#4-code-reuse--dry-violations)
5. [Memory Leaks](#5-memory-leaks)
6. [Architecture Issues](#6-architecture-issues)
7. [Security Concerns](#7-security-concerns)
8. [Priority Summary](#priority-summary)

---

## 1. Performance Issues

### 1.1 N+1 Query in Batch Quiz Metadata Fetch
**File**: `data/learning/FirestoreQuizRepository.kt` (lines 269-278)
**Issue**: `getBatchQuizMetadata` uses `whereIn` queries limited to 10 items per chunk. For large target lists, this creates multiple sequential queries.
**Fix**: Cache quiz metadata more aggressively at the app level or use a denormalized structure.

### 1.2 Large State Objects Copied Frequently
**File**: `screens/learning/LearningViewModel.kt` (lines 339-345)
**Issue**: `LearningUiState` contains large maps (`sheetExistsByLanguage`, `sheetCountByLanguage`, `quizCountByLanguage`) copied on every state update via `.copy()`.
**Fix**: Consider `kotlinx.collections.immutable.PersistentMap` or split state into smaller, focused state objects.

### 1.3 Missing `derivedStateOf` for Expensive Computations
**File**: `screens/wordbank/WordBankScreen.kt` (lines 177-179)
**Issue**: Filtered word list computation may recalculate on every recomposition without `derivedStateOf`.
**Fix**: Wrap expensive filtering in `remember { derivedStateOf { ... } }`.

### 1.4 Uncached Language Support Loading
**File**: `screens/learning/LearningViewModel.kt` (lines 91-93)
**Issue**: `supportedLanguages` loads from context/assets on first access via `lazy`, which could cause jank.
**Fix**: Pre-load in Application class or use a background coroutine.

### 1.5 Oversized LRU Cache
**File**: `screens/learning/LearningViewModel.kt` (lines 110-118)
**Issue**: `MAX_SHEET_CACHE_SIZE = 100` may be excessive for mobile memory constraints.
**Fix**: Reduce to 20-30 entries.

---

## 2. Database Read/Write Optimization

### 2.1 Missing Freshness Check in SyncFriendUsernames
**File**: `data/friends/FirestoreFriendsRepository.kt` (lines 608-666)
**Issue**: Fetches ALL friend profiles (1 read per friend) even if most usernames are still fresh.
**Fix**: Add a timestamp-based freshness check (e.g., only sync if cache is older than 24 hours).

### 2.2 Full Language Count Rebuild After Session Delete
**File**: `data/history/FirestoreHistoryRepository.kt` (lines 442-448)
**Issue**: After `deleteSession`, the entire language counts cache is rebuilt by reading all documents.
**Fix**: Implement incremental cache updates by decrementing counts for deleted records' language codes atomically.

### 2.3 Language Count Mismatch Between Shared Data Source and Full DB
**File**: `data/history/SharedHistoryDataSource.kt` (lines 171-180)
**Issue**: `getRecordsForLanguage` filters in-memory records (limited to ~200) while generation eligibility requires counts from ALL records.
**Fix**: Ensure all callers use `getCountForLanguage()` from the cached language counts rather than filtering records directly.

### 2.4 Sequential Batch Processing in Remove Friend
**File**: `data/friends/FirestoreFriendsRepository.kt` (lines 493-512)
**Issue**: Friend request cleanup processes chunks sequentially. For 1000+ stale requests, this creates multiple sequential batch commits.
**Fix**: Use `coroutineScope { chunks.map { async { ... } }.awaitAll() }`.

### 2.5 Wasted Reads for Non-Existent Quiz Documents
**File**: `data/learning/FirestoreQuizRepository.kt` (lines 274-288)
**Issue**: `whereIn(FieldPath.documentId(), chunkQuizIds)` still counts as reads for non-existent documents.
**Fix**: Pre-filter the targets list by checking a client-side cache.

---

## 3. Error Handling

### 3.1 Swallowed Exception in WordBankViewModel
**File**: `screens/wordbank/WordBankViewModel.kt` (line 628)
**Issue**: `parseWordBankResponse` catches `Exception` and returns `emptyList()` silently. Users see a generic error.
**Fix**: Return `Result<List<WordBankItem>>` to preserve error details.

### 3.2 Silent Chat Failures
**File**: `screens/friends/ChatViewModel.kt`
**Issue**: If Firestore listener setup fails, exceptions are caught but not surfaced to UI state.
**Fix**: Add `error: String?` field to `ChatUiState` and populate it in catch blocks.

### 3.3 Inconsistent Error Handling in SettingsViewModel
**File**: `screens/settings/SettingsViewModel.kt` (lines 402-411)
**Issue**: Some settings updates set `errorKey` (localized) while others set `errorRaw` (raw message).
**Fix**: Standardize on either `errorKey` with proper localization or `errorRaw` with user-friendly messages.

### 3.4 Silent Failure in Friend Cleanup
**File**: `data/friends/FirestoreFriendsRepository.kt` (lines 509-512, 723-725)
**Issue**: Friend request cleanup in `removeFriend` and `blockUser` silently logs failures. Stale requests can prevent re-adding.
**Fix**: Surface a warning to the user or implement retry logic.

### 3.5 Missing CancellationException Re-throw
**File**: `screens/wordbank/WordBankViewModel.kt` (line 627)
**Issue**: `parseWordBankResponse` catches all `Exception` types which could swallow `CancellationException`.
**Fix**: Add `if (e is CancellationException) throw e` before handling other exceptions.

---

## 4. Code Reuse / DRY Violations

### 4.1 Duplicated TTS Logic Across ViewModels
**Files**: `HistoryViewModel.kt`, `WordBankViewModel.kt`, `SpeechViewModel.kt`
**Issue**: TTS handling (voice name retrieval, state management, error handling) duplicated across 3+ ViewModels.
**Fix**: Extract into a shared `TtsHelper` class or `PlayTextWithVoiceUseCase`.

### 4.2 Repeated Auth State Collection Pattern
**Files**: All ViewModels (~10+)
**Issue**: Near-identical `init` blocks collecting `authRepo.currentUserState` with `when (auth)`.
**Fix**: Create a base `AuthAwareViewModel` or utility extension `ViewModel.collectAuthState(...)`.

### 4.3 Duplicated Friend Request Status Logic
**File**: `screens/friends/FriendsViewModel.kt` (lines 621-634)
**Issue**: `getRequestStatusFor` and `canSendRequestTo` duplicate filtering across friends/incoming/outgoing lists.
**Fix**: Consolidate into a single `FriendshipStatus` resolver.

### 4.4 Repeated Cache Invalidation Pattern
**Files**: `LearningViewModel.kt`, `WordBankViewModel.kt`
**Issue**: Both implement similar cache invalidation logic for sheet/word bank metadata.
**Fix**: Extract into a shared `MetadataCache` class.

### 4.5 Double Unread Count Observation
**File**: `appstate/AppViewModel.kt` (lines 124-154)
**Issue**: `startObservingUnread` observes the same flow twice with similar logic.
**Fix**: Use a single `combine` emitting `Pair<Boolean, Int>`.

---

## 5. Memory Leaks

### 5.1 Potential Leaked Coroutine in SharedHistoryDataSource
**File**: `data/history/SharedHistoryDataSource.kt` (lines 84-100)
**Issue**: `historyJob` launched in a `CoroutineScope(SupervisorJob())`. If `stopObserving()` isn't called, the job continues indefinitely.
**Fix**: Ensure `stopObserving()` is called in all ViewModels' `onCleared()`. Consider tying scope to lifecycle.

### 5.2 Missing Cancel for Quiz Generation Job
**File**: `screens/learning/LearningViewModel.kt` (lines 458-508)
**Issue**: `quizGenerationJob` set to null in `finally` but not cancelled in `stopJobs()`.
**Fix**: Add `quizGenerationJob?.cancel()` to `stopJobs()`.

### 5.3 Uncancelled Background Scope in FirestoreHistoryRepository
**File**: `data/history/FirestoreHistoryRepository.kt` (line 42)
**Issue**: `backgroundScope = CoroutineScope(SupervisorJob())` is never cancelled. As a `@Singleton`, it lives for the app lifecycle.
**Fix**: Add `close()` method or bind lifecycle to user auth state.

### 5.4 Excessive Cache Object Retention
**File**: `screens/learning/LearningViewModel.kt` (lines 110-118)
**Issue**: LRU cache stores `SheetMetaCache` objects that may hold large data structures.
**Fix**: Ensure cache values are lightweight (primitive types or small DTOs only).

### 5.5 Obsolete unreadJobs Map
**File**: `screens/friends/FriendsViewModel.kt` (lines 88-91)
**Issue**: `unreadJobs` map storing per-friend chat observers may be stale from a replaced implementation.
**Fix**: Remove `unreadJobs` and related code if it's been replaced by a single document observer.

---

## 6. Architecture Issues

### 6.1 God Class: SettingsViewModel
**File**: `screens/settings/SettingsViewModel.kt`
**Issue**: Manages theme, language, fonts, voices, color palettes, notifications, and coin stats. Violates SRP.
**Fix**: Split into `ThemeSettingsViewModel`, `LanguageSettingsViewModel`, `VoiceSettingsViewModel`, etc.

### 6.2 ViewModels Directly Depend on Repositories
**Files**: `HistoryViewModel`, `WordBankViewModel`, others
**Issue**: ViewModels inject repositories directly instead of use cases for many operations.
**Fix**: Wrap repository calls in use cases (e.g., `GetUserCoinStatsUseCase`, `ToggleFavoriteUseCase`).

### 6.3 Missing Interfaces for Shared Data Sources
**Files**: `SharedHistoryDataSource`, `SharedSettingsDataSource`, `SharedFriendsDataSource`
**Issue**: No interface or abstraction, making testing difficult.
**Fix**: Define interfaces and inject them instead of concrete implementations.

### 6.4 Data Layer Models Exposed to UI
**File**: `domain/` (multiple files)
**Issue**: Use cases return repository types directly without domain-specific models.
**Fix**: Introduce domain models and map in use cases to decouple UI from Firestore schema.

### 6.5 Excessive Constructor Dependencies
**File**: `screens/wordbank/WordBankViewModel.kt` (lines 48-60)
**Issue**: Constructor has 10+ dependencies, indicating too many responsibilities.
**Fix**: Extract related dependencies into facade classes.

---

## 7. Security Concerns

### 7.1 No Client-Side Rate Limiting for Coin Awards
**File**: `data/learning/FirestoreQuizRepository.kt` (lines 365-379)
**Issue**: No client-side rate limiting before calling `awardQuizCoins` Cloud Function.
**Fix**: Add client-side debouncing (max once per minute per quiz).

### 7.2 Missing Input Sanitization for Friend Request Notes
**File**: `data/friends/FirestoreFriendsRepository.kt` (line 305)
**Issue**: `note` field truncated to 80 chars but not sanitized for injection attacks.
**Fix**: Sanitize input with HTML escaping.

### 7.3 No Client-Side Rate Limiting for Firestore Writes
**File**: `data/history/FirestoreHistoryRepository.kt` (lines 71-96)
**Issue**: `saveBatch` has no client-side throttling.
**Fix**: Implement max 100 writes per minute throttle.

### 7.4 Non-Atomic Username Propagation
**File**: `data/friends/FirestoreFriendsRepository.kt` (lines 551-606)
**Issue**: `propagateUsernameChange` updates multiple documents in separate batches. Partial failure causes inconsistency.
**Fix**: Use Firestore transactions or implement rollback logic.

### 7.5 Exposed Implementation Details in UiState
**File**: `screens/history/HistoryViewModel.kt` (lines 35-52)
**Issue**: `HistoryUiState` exposes `favoriteIds` and `favoritedTexts` sets which are implementation details.
**Fix**: Move to private ViewModel fields and expose only computed values.

---

## Priority Summary

### High Priority (implement now)
| # | Finding | Category | Impact |
|---|---------|----------|--------|
| 5.1 | Leaked coroutine in SharedHistoryDataSource | Memory | Data leak, battery drain |
| 5.2 | Missing quiz generation job cancel | Memory | Background work after nav away |
| 3.1 | Swallowed exception in WordBankViewModel | Error handling | Users see generic errors |
| 3.5 | Missing CancellationException re-throw | Error handling | Broken coroutine cancellation |
| 2.2 | Full language count rebuild on delete | DB reads | Unnecessary Firestore reads |

### Medium Priority (address next)
| # | Finding | Category | Impact |
|---|---------|----------|--------|
| 1.3 | Missing derivedStateOf | Performance | Unnecessary recompositions |
| 4.1 | Duplicated TTS logic | DRY | Maintenance burden |
| 4.2 | Repeated auth state pattern | DRY | Boilerplate across ViewModels |
| 7.1 | No rate limiting for coin awards | Security | Potential abuse |
| 6.3 | Missing interfaces for shared data sources | Architecture | Testability |

### Low Priority (backlog)
| # | Finding | Category | Impact |
|---|---------|----------|--------|
| 1.2 | Large state objects | Performance | Memory pressure |
| 6.1 | God class SettingsViewModel | Architecture | Maintainability |
| 6.4 | Data models exposed to UI | Architecture | Coupling |
| 7.5 | Exposed internal state | Security | Low risk |
| 4.5 | Double unread observation | DRY | Minor inefficiency |
