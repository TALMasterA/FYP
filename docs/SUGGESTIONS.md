# Code Review — Suggestions & Improvements

Ordered by priority (highest first).
**Status column:** ✅ Implemented | 📋 Needs review before implementing

---

## 1. Architecture

### 1.1 Split `AppNavigation.kt` (718 lines) into sub-graph files — ✅ Implemented
**Problem:** A single 718-line composable function owns the entire nav graph, all shared ViewModels, five `LaunchedEffect` snackbar handlers, and the scaffold. Any change to any route requires opening and editing this file.

**Fix:** Extract `NavGraphBuilder` extension functions into focused files:
- `StartupAuthGraph.kt` — startup, onboarding, login, reset-password
- `MainFeatureGraph.kt` — home, speech, help, continuous, history
- `LearningWordBankGraph.kt` — learning, sheet, quiz, word bank
- `FriendsChatGraph.kt` — friends, chat, shared-inbox, blocked-users, my-profile
- `SettingsProfileGraph.kt` — settings, profile, shop, voice-settings, notifications, feedback, notification-settings

`AppNavigation.kt` keeps only the shared state setup and the `NavHost {}` scaffold.

---

### 1.2 Split `WordBankViewModel.kt` (904 lines) — 📋 Needs review
**Problem:** The ViewModel mixes user authentication observation, CRUD (load/delete/favourite), AI generation orchestration, TTS playback, sharing, and pagination. A single bug anywhere can bring down the whole word-bank flow.

**Suggested split:**
- `WordBankCrudViewModel` — load, delete, favourite, custom words, pagination
- `WordBankGenerationViewModel` — AI generation pipeline, progress, eligibility gating
- `WordBankPlaybackViewModel` (or folded into Crud) — TTS speak actions

`AppNavigation.kt` already holds one shared `WordBankViewModel`; the new split means holding 2–3 smaller VMs instead.

---

### 1.3 `SettingsViewModel` directly injects `FirestoreQuizRepository` concrete class — 📋 Needs review
**File:** `SettingsViewModel.kt:61`
```kotlin
private val quizRepo: com.example.fyp.data.learning.FirestoreQuizRepository
```
The Settings layer has no business knowing about the quiz Firestore repository. The responsibility is "observe coin stats", which should go through a dedicated `CoinStatsRepository` interface (or piggy-backed through `SharedSettingsDataSource`). The concrete FQN import also bypasses the normal injected-interface pattern used elsewhere in the project.

---

### 1.4 `HistoryScreen` injects `SpeechViewModel` — 📋 Needs review
**File:** `HistoryScreen.kt:45`
```kotlin
val speechVm: SpeechViewModel = hiltViewModel()
```
A history screen should not need the speech ViewModel. This is likely a leftover from an earlier design where re-playing from history called directly into the speech module. The dependency needs auditing — if the functionality is still needed, it should go through a shared use-case or repository, not a cross-screen ViewModel.

---

### 1.5 `FriendsScreen` injects `SettingsViewModel` — 📋 Needs review
**File:** `FriendsScreen.kt:44`
```kotlin
settingsViewModel: com.example.fyp.screens.settings.SettingsViewModel = hiltViewModel()
```
A friends screen injecting the settings ViewModel is a cross-concern coupling. Worth auditing what settings data is actually needed here; it should be passed down as a plain value or through a shared data source, not via the full settings ViewModel.

---

## 2. Code Quality (safe to implement)

### 2.1 `SettingsViewModel` — FQN references instead of imports — ✅ Implemented
**File:** `SettingsViewModel.kt:393–397`
```kotlin
com.example.fyp.core.FcmNotificationService.saveNotifPrefToCache(...)
com.example.fyp.core.AppLogger.e("SettingsViewModel", ...)
```
Both are used by fully-qualified name with no import. Every other call-site in the project uses top-level imports. Inconsistent and harder to read.

---

### 2.2 `SettingsViewModel` — dead `refreshCoinStats()` method — ✅ Implemented
**File:** `SettingsViewModel.kt:126–129`
The method body is a no-op comment block kept "for backward-compat". If call-sites outside the project don't exist, it should be removed entirely. If they do, the signature should at minimum not accept a nullable `uid` parameter it never uses.

---

### 2.3 Magic strings for notification field names — ✅ Implemented
**File:** `SettingsViewModel.kt:381–387`
```kotlin
"notifyNewMessages", "notifyFriendRequests", "notifyRequestAccepted",
"notifySharedInbox", "inAppBadgeMessages", "inAppBadgeFriendRequests", "inAppBadgeSharedInbox"
```
These raw strings appear in at least `SettingsViewModel`, `SetNotificationPrefUseCase`, and `FcmNotificationService`. Extract them as a `NotificationPrefKeys` object with named constants so a typo can be caught at compile time.

---

### 2.4 Language-code `when` in `CommonUi.kt` — ✅ Implemented
**File:** `CommonUi.kt:63–82`
A 19-branch `when` mapping language BCP-47 codes to `UiTextKey` enum values is verbose and grows every time a new language is added. Replace with a `companion object` `Map<String, UiTextKey>` that requires only one entry per new language.

---

### 2.5 `@file:Suppress("AssignedValueIsNeverRead")` in `HistoryScreen.kt` — 📋 Needs review
The file-level suppressor hides one or more dead variable assignments. Track down which variable is never read and remove either the variable or the suppressor. File-level suppressors are broad and can mask future real issues.

---

### 2.6 Inconsistent `collectAsState` vs `collectAsStateWithLifecycle` usage — 📋 Needs review
**Example:** `HistoryScreen.kt:46` uses `collectAsState()` while the rest of the project consistently uses `collectAsStateWithLifecycle()`. The latter is preferred because it cancels collection when the screen is not in the foreground, saving resources. Audit all screens and standardise on `collectAsStateWithLifecycle`.

---

### 2.7 Hardcoded fallback strings in navigation snackbars — 📋 Needs review
**File:** `AppNavigation.kt:176–218`
```kotlin
?: "Learning sheet ready! Tap to open."
?: "Quiz ready! Tap to start."
?: "Word bank ready! Tap to view."
```
These English fallbacks are scattered inline. They should either be moved to `BaseUiTexts` (the existing fallback system) or centralised in a string resource, so the i18n layer can cover them.

---

### 2.8 `URLEncoder.encode` called without a `Charset` constant — 📋 Needs review
**File:** `AppScreens.kt:34,39`
```kotlin
java.net.URLEncoder.encode(itemId, "UTF-8")
```
The `"UTF-8"` string should be `Charsets.UTF_8.name()` or `java.nio.charset.StandardCharsets.UTF_8.name()` to avoid creating string types from raw literals, consistent with the rest of Kotlin idioms.

---

## 3. Feature / Logic Suggestions

### 3.1 No input validation feedback when navigating with empty route args — 📋 Needs review
**File:** `AppNavigation.kt:534–535`
```kotlin
val primaryCode = backStackEntry.arguments?.getString("primaryCode").orEmpty()
val targetCode  = backStackEntry.arguments?.getString("targetCode").orEmpty()
```
`orEmpty()` silently degrades to an empty string. Downstream screens receive `""` as a language code and may crash or show blank content. Add a guard that redirects to the previous screen (or shows an error) when required route args are empty.

---

### 3.2 `isOnboardingComplete()` is a synchronous SharedPreferences read on the Compose thread — 📋 Needs review
**File:** `AppNavigation.kt:110–112`
```kotlin
val isOnboardingDone = remember { isOnboardingComplete(context) }
```
`isOnboardingComplete` reads SharedPreferences synchronously inside a `remember` on the main thread during composition. While SP reads are usually fast, this should be moved to a `produceState {}` or a ViewModel init block and replaced with a loading state while the value is read.

---

### 3.3 `AzureLanguageConfig.loadSupportedLanguagesSuspend` runs on every `AppNavigation` recomposition — 📋 Needs review
**File:** `AppNavigation.kt:115`
The `produceState` that loads supported languages re-runs whenever `AppNavigation` recomposes. A cold recomposition (e.g., theme change) triggers a re-load. Cache the result in a singleton/ViewModel so it is only loaded once per app session.

---

### 3.4 Friends badge count hardcodes "1" for booleans — 📋 Needs review
**File:** `AppNavigation.kt:273–275`
```kotlin
val friendsBadgeCount = pendingFriendRequestCount +
    (if (hasUnreadMessages) 1 else 0) +
    (if (hasUnseenSharedItems) 1 else 0)
```
`hasUnreadMessages` and `hasUnseenSharedItems` are booleans so the badge only ever adds +1 per category regardless of the real count. If the data layer can return actual unread counts, the badge would be more informative.

---

### 3.5 No error boundary around the nav graph — 📋 Needs review
An unhandled exception in any screen composable will crash the entire app. Consider wrapping each screen boundary (or at minimum the `NavHost`) in a top-level error handler that shows a graceful "something went wrong" screen instead of a hard crash.

---

### 3.6 Notification permission prompt fires every time `uid` changes — 📋 Needs review
**File:** `AppNavigation.kt:141–146`
The permission launcher is triggered on every change of `settingsUiState.uid`. If the UID changes sign-in/sign-out multiple times in a session, the prompt reappears. Track whether the prompt has already been shown this session using a `remember { mutableStateOf(false) }` flag.

---

## 4. File-size Summary

| File | Lines | Assessment |
|------|-------|------------|
| `model/ui/strings/UiTextScreens.kt` | 1540 | Data file — acceptable (key-value translations) |
| `model/ui/strings/UiTextCore.kt` | 912 | Data file — acceptable |
| `screens/wordbank/WordBankViewModel.kt` | 904 | **Too large — split (§1.2)** |
| `screens/friends/FriendsScreen.kt` | 824 | Large — refactor extracting sub-composables |
| `data/friends/FirestoreFriendsRepository.kt` | 802 | Large repository — consider splitting by concern |
| `navigation/AppNavigation.kt` | 718 | **Split into sub-graphs (§1.1, implemented)** |
| `screens/wordbank/WordBankDetailView.kt` | 673 | Large view — extract sub-composables |
| `screens/friends/FriendsViewModel.kt` | 635 | Large — review split opportunity |
| `screens/friends/ChatScreen.kt` | 603 | Large screen |
| `screens/history/HistoryScreen.kt` | 576 | Large — review cross-VM injection (§1.4) |
