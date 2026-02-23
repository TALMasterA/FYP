# App Enhancement Suggestions

Suggestions ordered by expected user-visible or cost impact.
**✅ Implemented** = done in this session. **⏳ Pending** = involves logic changes, deferred.

---

## 1. Navigation screen transitions ✅ Implemented

**Area:** Navigation smoothness  
**File:** `AppNavigation.kt`

The `NavHost` had no `enterTransition`/`exitTransition` parameters, so every screen change was
an abrupt cut with no animation. Added a 300 ms `fadeIn`/`fadeOut` as the default for all routes,
giving the app a polished feel without changing any navigation logic.

---

## 2. In-memory mirror for `TranslationCache` writes ✅ Implemented

**Area:** Performance – DataStore I/O reduction  
**File:** `data/cloud/TranslationCache.kt`

Every call to `cache()` or `cacheBatch()` previously called `loadCache()` which deserialises the
entire translation cache JSON blob from DataStore before appending the new entry. Added a
`memCache: TranslationCacheData?` mirror (the same pattern already used by `WordBankCacheDataStore`
and `LanguageDetectionCache`). `loadCache()` now serves from memory after the first read, and
`saveCache()` keeps the mirror in sync. This eliminates a redundant DataStore read on every
translation result that is newly cached — high-frequency during normal usage.

---

## 3. In-memory read cache for `LanguageDetectionCache` ✅ Implemented

**Area:** Performance – DataStore I/O reduction  
**File:** `data/cloud/LanguageDetectionCache.kt`

`LanguageDetectionCache.getCached()` always read the full DataStore blob. Added a `memCache`
mirror and an `inMemoryLookup` map (key → entry), so repeat lookups for the same text are served
from memory with zero I/O. The pattern is consistent with `WordBankCacheDataStore`.

---

## 4. Safety limits on two unlimited Firestore queries ✅ Implemented

**Area:** Database read safety  
**File:** `data/history/FirestoreHistoryRepository.kt`

Two queries had no `.limit()`:

- `rebuildLanguageCountsCache()` — fetches **all** history documents when the language-counts
  stats document is missing. Could read tens of thousands of records on first install. Capped at
  **10 000** (far beyond any realistic user history).
- `listenSessions()` — real-time listener on the sessions collection with no bound. Capped at
  **1 000** for consistency with the rest of the codebase.

---

## 5. Replace direct `android.util.Log` calls with `AppLogger` ✅ Implemented

**Area:** Logging consistency / Crashlytics coverage  
**File:** `data/repositories/FirebaseTranslationRepository.kt`

`FirebaseTranslationRepository` contained several `android.util.Log.d/e` calls that bypass the
centralised `AppLogger`. `AppLogger.d` is debug-gated (no-op in release) and `AppLogger.e`
reports to Crashlytics, so using the abstraction improves crash visibility in production and
avoids cluttering release logs.

---

## 6. Migrate `SpeechViewModel` settings to `SharedSettingsDataSource` ⏳ Pending

**Area:** Firestore listener count reduction  
**File:** `screens/speech/SpeechViewModel.kt`

`SpeechViewModel` calls `observeSettings(UserId(...))` in its `init` block, which creates its own
dedicated Firestore snapshot listener for the user-settings document. `SharedSettingsDataSource`
(a `@Singleton`) is already started by `AppViewModel` before any screen ViewModel is created, and
is shared by `SettingsViewModel`, `WordBankViewModel`, and `FavoritesViewModel`. Migrating
`SpeechViewModel` to consume `sharedSettings.settings` instead would eliminate one redundant
listener while the Speech screen is active.

**Logic impact:** Requires injecting `SharedSettingsDataSource` into `SpeechViewModel`, removing
the `ObserveUserSettingsUseCase` dependency from this ViewModel, and adjusting how the settings
`StateFlow` is consumed. The SpeechViewModel is complex (OCR, continuous mode, TTS), so the
change should be done carefully with full re-test of all speech paths.

---

## 7. Fix `getCountForLanguagePair` to use all-time counts ⏳ Pending

**Area:** Data accuracy  
**File:** `data/history/SharedHistoryDataSource.kt` (line 198)

`getCountForLanguagePair()` counts matching records from `_historyRecords` (limited to the
display limit, e.g. 100–200). The comment in the code already flags this: *"this may be inaccurate
with history limits"*. For users with more records than the display limit, the pair count will be
understated, which could affect generation eligibility decisions.

**Fix approach:** Either (a) add a dedicated Firestore aggregation query for the pair count, or (b)
store pair counts alongside per-language counts in the `user_stats/language_counts` document.
Option (b) is cheaper to read but requires a more complex write path in `updateLanguageCountsCache`.

---

## 8. Apply `NetworkRetry` to critical Firestore writes ⏳ Pending

**Area:** Reliability  
**Files:** `data/history/FirestoreHistoryRepository.kt`, `data/learning/FirestoreLearningSheetsRepository.kt`,
`data/wordbank/FirestoreWordBankRepository.kt`

`NetworkRetry.withRetry` is currently used on outgoing chat messages
(`FirestoreChatRepository.sendTextMessage`) and batch translation (`CloudTranslatorClient.translateTexts`
/ `CloudSpeechTokenClient.getSpeechToken`). However, the history `save()`, sheet `upsertSheet()`,
and word-bank `saveWordBank()` writes have no retry logic. Transient network errors will silently
lose data for these operations.

**Logic impact:** Wrapping with `NetworkRetry.withRetry(shouldRetry = NetworkRetry::isRetryableFirebaseException)`
is low risk, but history save is called from within a coroutine in `SpeechViewModel` and the
retry duration adds latency that should be tested on slow connections.

---

## 9. Use `derivedStateOf` for expensive Compose derivations ⏳ Pending

**Area:** Compose recomposition performance  
**Files:** `screens/wordbank/WordBankScreen.kt`, `screens/learning/LearningScreen.kt`

Expressions like `uiState.clusters.filter { ... }` or `uiState.favorites.filter { ... }` inside
composable lambdas re-execute on every recomposition even when the underlying list has not
changed. Wrapping these in `remember { derivedStateOf { ... } }` makes Compose skip downstream
recompositions when the derived value is stable, reducing unnecessary UI work for lists that
update infrequently compared to overall recomposition rate.

**Logic impact:** None — purely a Compose optimisation. However, each `derivedStateOf` usage must
be verified to have the correct structural equality (data classes with `equals` already work
correctly).

---

## 10. Introduce `WorkManager` for cache maintenance ⏳ Pending

**Area:** Background maintenance / Firestore cost  
**Files:** `data/history/FirestoreHistoryRepository.kt`, `data/cloud/TranslationCache.kt`

Currently the `language_counts` stats document is rebuilt synchronously inside `deleteSession()`
by calling `rebuildLanguageCountsCache()`. For a session with thousands of records this blocks
the UI coroutine until the full history is fetched. Moving periodic cache maintenance (rebuilds,
expired-entry eviction, translation cache compaction) into a `CoroutineWorker` scheduled with
`WorkManager` would keep the UI responsive and avoid redundant work on every session deletion.

**Logic impact:** Requires adding `WorkManager` as a dependency, defining a maintenance worker,
and decoupling `rebuildLanguageCountsCache` from the synchronous delete path.

---

## 11. Add input debouncing to the continuous-translation word auto-save ⏳ Pending

**Area:** API call reduction  
**File:** `screens/speech/SpeechViewModel.kt` + `data/history/FirestoreHistoryRepository.kt`

In continuous conversation mode, a `save()` Firestore write is triggered for every recognised
utterance segment. High-speed speakers or sessions with many short segments can generate dozens
of writes per minute. Debouncing consecutive saves (e.g., coalescing segments within a 1-second
window) and batching them into a single Firestore write would reduce document-write billing and
improve throughput.

**Logic impact:** Changes the granularity of history records. The existing session/segment model
must be preserved, so coalescing must only combine records within the same speaker turn.

---

## 12. Offline write queue for history saves ⏳ Pending

**Area:** Offline resilience  
**File:** `domain/history/SaveTranslationUseCase.kt`

The app shows an offline banner when connectivity is lost but still attempts Firestore writes for
each translation. Firestore's offline persistence will queue these automatically, but there is no
user-visible acknowledgement or retry status. Adding a local `Room`-backed write queue with
`WorkManager` delivery would give stronger guarantees and allow showing "syncing N records"
progress when connectivity is restored.

**Logic impact:** Major architectural addition — requires Room, a sync worker, conflict resolution
strategy, and an update to the SaveTranslation flow.

---

## 13. Shared language-display name lookup ⏳ Pending

**Area:** Minor performance / memory  
**File:** `AppNavigation.kt`, all screens that call `LanguageDisplayNames.displayName(code)`

`AzureLanguageConfig.loadSupportedLanguagesSuspend(context)` is called inside `produceState` on
every `AppNavigation` recomposition. The result is `remember`d, which is correct, but multiple
screens independently call `LanguageDisplayNames.displayName()` which re-parses the same JSON
asset. Extracting a singleton `LanguageDisplayNameCache` pre-populated on app start would remove
repeated asset parsing across screens.

**Logic impact:** Low — requires moving the display-name map out of a static method into a
`@Singleton` injectable, and updating call sites.
