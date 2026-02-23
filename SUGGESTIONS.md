# App Enhancement Suggestions

Suggestions ordered by expected user-visible or cost impact.
**✅ Implemented** = done. **⏳ Pending** = involves breaking logic changes or major architecture.

---

## 1. Navigation screen transitions ✅ Implemented

**Area:** Navigation smoothness  
**File:** `AppNavigation.kt`

Added 300 ms `fadeIn`/`fadeOut` as the default for all routes.

---

## 2. In-memory mirror for `TranslationCache` writes ✅ Implemented

**Area:** Performance – DataStore I/O reduction  
**File:** `data/cloud/TranslationCache.kt`

Added `memCache` mirror — `loadCache()` serves from memory after the first read.

---

## 3. In-memory read cache for `LanguageDetectionCache` ✅ Implemented

**Area:** Performance – DataStore I/O reduction  
**File:** `data/cloud/LanguageDetectionCache.kt`

Added `memCache` mirror consistent with `WordBankCacheDataStore`.

---

## 4. Safety limits on two unlimited Firestore queries ✅ Implemented

**Area:** Database read safety  
**File:** `data/history/FirestoreHistoryRepository.kt`

- `rebuildLanguageCountsCache()` capped at 10 000 docs.
- `listenSessions()` listener capped at 1 000 docs.

---

## 5. Replace direct `android.util.Log` calls with `AppLogger` ✅ Implemented

**Area:** Logging consistency / Crashlytics coverage  
**File:** `data/repositories/FirebaseTranslationRepository.kt`

---

## 6. Migrate `SpeechViewModel` and `LearningViewModel` to `SharedSettingsDataSource` ✅ Implemented

**Area:** Firestore listener count reduction  
**Files:** `screens/speech/SpeechViewModel.kt`, `screens/learning/LearningViewModel.kt`

Both ViewModels previously called `ObserveUserSettingsUseCase(UserId(uid)).collect { ... }` which
created a separate Firestore snapshot listener for the user-settings document while each screen
was open.  Both now consume the shared `SharedSettingsDataSource.settings` `StateFlow`
(already warmed by `AppViewModel` before any screen is created), eliminating two redundant
listeners.

`SpeechViewModel` no longer needs `ObserveUserSettingsUseCase` in its constructor at all.
Voice settings are read on-demand from `sharedSettings.settings.value.voiceSettings`.
The previous `init` block contained a nested `collect` that blocked the outer auth-state
collector from emitting `LoggedOut` — this is also fixed by the migration.

---

## 7. Fix `getCountForLanguagePair` to use all-time counts ⏳ Pending

**Area:** Data accuracy  
**File:** `data/history/SharedHistoryDataSource.kt`

> **Verification (2026-02-23):** The counts shown on the Learning and Word Bank screens are
> already correct.  They come from `SharedHistoryDataSource.languageCounts`, which is populated
> by `refreshLanguageCounts()` → `FirestoreHistoryRepository.getLanguageCounts()` → reads from
> the `users/{uid}/user_stats/language_counts` stats document.  This document is atomically
> updated on every `save()` and `delete()` call and represents **all** translation records, not
> just the 50–100 shown in the History screen.  No change is required for the per-language counts.
>
> `getCountForLanguagePair()` (which counts only in-memory limited records) is used internally
> but not surfaced to the UI.  Fix is deferred to a dedicated task.

---

## 8. Apply `NetworkRetry` to critical Firestore writes ✅ Implemented

**Area:** Reliability  
**Files:** `data/history/FirestoreHistoryRepository.kt`, `data/learning/FirestoreLearningSheetsRepository.kt`,
`data/wordbank/FirestoreWordBankRepository.kt`

Wrapped `save()`, `upsertSheet()`, and `saveWordBank()` in
`NetworkRetry.withRetry(shouldRetry = NetworkRetry::isRetryableFirebaseException)`.
Transient network failures retry up to 3 times with exponential back-off.

---

## 9. Memoize expensive Compose filter derivations ✅ Implemented

**Area:** Compose recomposition performance  
**File:** `screens/wordbank/WordBankDetailView.kt`

Wrapped `filteredWords` O(n) list filter in
`remember(wordBank.words, filterKeyword, filterCategory, filterDifficulty) { ... }` so the
scan only re-runs when one of those values actually changes.

---

## 10. Background cache rebuild after session delete ✅ Implemented

**Area:** UI responsiveness / Firestore cost  
**File:** `data/history/FirestoreHistoryRepository.kt`

`deleteSession()` previously called `rebuildLanguageCountsCache()` synchronously — a full
history read that could scan thousands of documents and block the UI coroutine.  Added a
`backgroundScope` (`SupervisorJob + Dispatchers.IO`) on the repository and launched the
rebuild there, so `deleteSession()` returns immediately.

---

## 11. Debounce continuous-mode history saves ✅ Implemented

**Area:** Firestore write reduction  
**File:** `screens/speech/SpeechViewModel.kt`

Each recognised utterance segment previously triggered an immediate `historyRepo.save()` call.
During rapid speech this produced many sequential Firestore writes per minute.

Added a pending-record queue (`pendingContinuousSaves`) and a debounce job:
- `mode = "continuous"` → record is enqueued; flush job is cancelled and restarted with an
  800 ms delay.
- After 800 ms of silence, all queued records are saved individually (each retains its own
  `id`, `sessionId`, `speaker`, `direction`, `sequence`).
- `endContinuousSession()` and `onCleared()` flush / discard the queue immediately.
- `mode = "discrete"` → unchanged: saved immediately.

---

## 12. Offline write queue for history saves ⏳ Pending

**Area:** Offline resilience  
**File:** `domain/history/SaveTranslationUseCase.kt`

Major architectural addition — requires Room, a sync worker, conflict resolution strategy, and
an update to the SaveTranslation flow.  Deferred to a dedicated task.

---

## 13. Cache `AzureLanguageConfig.loadSupportedLanguages()` result ✅ Implemented

**Area:** Performance / memory  
**File:** `data/azure/AzureLanguageConfig.kt`

Added a `@Volatile cachedLanguages` field on the `AzureLanguageConfig` object.  The first
caller parses the JSON asset; every subsequent caller receives the cached list with zero I/O.
