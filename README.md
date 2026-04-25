# FYP - Translation & Learning App

An Android-based translation and language learning application with AI-powered features.

Develop in Android Studio, Android ONLY.

--------------------------------------------------------------

## 📱 Try the App

Register to try this app!

Link: [Click here to register](https://appdistribution.firebase.dev/i/5ebf3d592700b0f7)

Please use your gmail to register.

--------------------------------------------------------------

## 🛠️ Tech Stack

**Frontend:** Android Studio, Kotlin, Compose  
**Backend:** Firebase (Auth, Firestore, Cloud Functions), Azure (Speech, Translation, OpenAI)  
**Key Libraries:** Hilt (DI), Coroutines & Flow, Serialization, OkHttp, DataStore

--------------------------------------------------------------

## 🛠️ CI & Quality Checks

**GitHub Actions workflows:**
1. **CI (`ci.yml`)**: Android unit tests, debug APK build, backend linting, backend build, and Jest coverage (50% threshold)
2. **CodeQL (`codeql.yml`)**: Weekly + on-demand semantic analysis for security vulnerabilities

**Status:**  
![CI](https://github.com/TALMasterA/FYP/actions/workflows/ci.yml/badge.svg)
![CodeQL](https://github.com/TALMasterA/FYP/actions/workflows/codeql.yml/badge.svg)

--------------------------------------------------------------

## ⚠️ Development Cautions

**Secrets in CI:** GitHub secret `GOOGLE_SERVICES_JSON` required for builds (valid `app/google-services.json` content).

**USB Debugging:** Enable in Developer Options when testing; disable after (security risk for online banking). Alternatively, use Android Studio emulator.

**Firebase Setup:** Requires own Firebase project + `google-services.json` in `app/` folder. Firebase login required.

**Backup/Security Posture:** App manifest sets `android:allowBackup="false"` to avoid unintended cloud/device backup of app data by default.

**Registration Disabled:** The account registration action is disabled during development stage (whole project).

**Language Pair Visibility:** When there are no related translation records, the Learning screen and Word Bank screen will NOT show that language/sheet pair for generation. Users must first create translation history for a language pair before it becomes visible for learning content generation.

--------------------------------------------------------------

## 🎯 Core Features

**Translation Modes:**
- **Quick Translate (Discrete):** Real-time voice translation for short phrases with auto-detect or manual language selection
- **Live Conversation (Continuous):** Multi-turn dialogue with automatic speaker detection (Person A/B)
- Translation requests now apply a short local cooldown after upstream rate-limit responses, and continuous mode auto-stops on rate-limit errors to prevent repeated failed calls
- Continuous speaker-toggle restarts are briefly debounced to stabilize microphone/session handoff on some devices
- **Camera / OCR:** Scan text from images via camera or gallery with language hints
- Multi-language: English, Cantonese, Traditional/Simplified Chinese, Japanese, and 10+ more via Azure
- Language swap button (swaps recognized/translated text)

**Learning System:**
- AI-generated learning sheets from translation history
- Quiz system with coin rewards (anti-cheat verification: first-attempt only)
- Auto-generated word bank with progress bar toward next regen
- Learning actions stay disabled until account sheet metadata finishes loading, preventing premature generate/open operations on stale fetch state
- Learning sheet, word bank, and quiz language pairs are controlled by account primary language (not App UI language)
- Generation rules (per non-primary language, using your translation history counts):
  - **Learning sheet:** needs at least 1 translation involving the language; first generate is allowed, regenerate only after 5+ additional records (blocks if counts drop).
  - **Quiz:** locked to sheet versions; first quiz is allowed, later regeneration only when the sheet’s saved history count changes (one quiz per sheet version).
  - **Word bank:** needs translation history for the target; first generate is allowed, refresh only after 20+ additional records since last generation and it appends new words without overwriting existing entries.
- Custom word bank for user-defined vocabulary
- Favorites for bookmarking translations and saving full conversation sessions (20-record limit; sessions count as N records where N is the number of messages inside)
- Word/share actions now fail fast on incomplete language payloads and show a retry hint instead of submitting malformed requests

**Friend System:**
- Search & add friends by username or User ID with optional note (≤80 chars)
- Real-time chat with automatic message translation to your language
- Chat mark-read uses aggregated unread counters (no per-message read loop)
- Share words and learning materials with friends
- Shared inbox for received items with accept/dismiss actions
- Shared inbox actions are single-flight guarded (rapid repeated taps are ignored while one action is processing)
- Accepting shared words adds them directly to Custom Words only (no extra bank routing or receiver-side retranslation)
- Block/unblock users; Firestore rules prevent blocked users from sending requests
- Client & server friend-request rate limiting: 5 per hour, persisted across app restarts
- Username changes have 30-day cooldown (same as primary language changes)
- Red dot badges clear only by viewing their corresponding screen (active/resumed Chat, Shared Inbox, Friends); refresh actions do not clear badges

**Customization:**
- UI language: 17 languages fully hardcoded (English, Cantonese, Traditional Chinese, Simplified Chinese, Japanese, Korean, French, German, Spanish, Indonesian, Vietnamese, Thai, Filipino, Malay, Portuguese, Italian, Russian) — instant switching, no API calls
- Primary language selector (30-day cooldown) in Settings
- Theme: Light / Dark / System (smooth animated transitions)
- Font size: 80%–150%
- 11 color palettes (1 free + 10 unlockable at 10 coins each)
- Voice settings per language
- Custom Words entries can update target language per word, and translation is regenerated for the selected language
- Word direction is unified across generated and custom banks: top text is the source/original side, bottom text is its translation (including accepted shared words)
- Changing a custom word's translation language now keeps the original/source text fixed and only refreshes the translated side
- Notification settings (push + badge toggles) accessible from the Friends bell icon flow
- Feedback submission is available in `Settings -> Feedback`

**History & Organization:**
- Translation history (30–60 records, configurable)
- Filter by language/keyword; retry on error; pull-to-refresh
- Session management for Live Conversation (rename, delete)
- Cloud sync via Firestore with offline persistence

**Coin System:**
- Earn coins through quiz performance
- Unlock color palettes (10 coins each)
- Expand history limit (1000 coins per 10-record increment)
- Anti-cheat: history count snapshotted at quiz generation

**User Accounts:**
- Email/password authentication via Firebase
- Google Sign-In (Firebase Auth Google provider; classic `play-services-auth` flow)
- Profile management (username, account deletion)
- Password reset via email
- Auto sign-out on app version update
- First-launch and post-update onboarding (re-shown after each update)
- Except App UI language, all settings and data are tied to user accounts and synced via Firestore

**Navigation & UI:**
- Bottom navigation bar (Home, Quick Translate, Learn, Friends, Settings) with unread badge
- Offline banner when connectivity lost
- Help & Notes screen (Cautions, Features, Tips, Friend System, Privacy)
- App-wide troubleshooting guidance: if an action stays unavailable after required steps, restart the app and try again
- Centralized error handling: auto-dismiss (3s), list screens auto-scroll to errors, Crashlytics logging
- Edge-to-edge display with proper system insets handling

--------------------------------------------------------------

## 📂 Project Structure

**MVVM + Clean Architecture**

Key directories:
- `navigation/` — Compose nav graph with sub-graphs for major features
- `appstate/` — Top-level app state (auth, badges, offline)
- `screens/` — UI screens & ViewModels (home, speech, history, learning, wordbank, friends, settings, login, onboarding)
- `model/` — Data models (TranslationRecord, UserSettings, Quiz, etc.) and UI text system (`UiTextKey` enum + hardcoded translations)
- `domain/` — Use cases & business logic
- `data/` — Repositories, Cloud Function clients, caching, Firestore wrappers
- `core/` — Common utilities (logging, audio, permissions, pagination, security, performance)
- `ui/` — Theme (colors, typography, dimensions)
- `fyp-backend/functions/` — Firebase Cloud Functions (TypeScript): translation, speech token, AI generation, FCM, maintenance

See `docs/treeOfImportantfiles.txt` for complete file tree.

--------------------------------------------------------------

## 🔧 Development Setup

**Prerequisites:**
- Android Studio (latest)
- JDK 17+
- Firebase project with Auth, Firestore, Cloud Functions, Crashlytics
- Azure Speech/Translation API keys
- Azure OpenAI deployment

**Configuration:**
1. Place `google-services.json` in `app/` (from Firebase Console)
2. Backend secrets via Firebase:
   ```bash
   firebase functions:secrets:set AZURE_SPEECH_KEY
   firebase functions:secrets:set AZURE_SPEECH_REGION
   firebase functions:secrets:set AZURE_TRANSLATOR_KEY
   firebase functions:secrets:set AZURE_TRANSLATOR_REGION
   firebase functions:secrets:set GENAI_BASE_URL
   firebase functions:secrets:set GENAI_API_VERSION
   firebase functions:secrets:set GENAI_API_KEY
   ```

**Backend setup:**
```bash
cd fyp-backend/functions
npm install
npm run lint
npm run build
npm run test:coverage -- --runInBand
npm run deploy
```

Keep `fyp-backend/functions/package-lock.json` committed. If Firebase backend dependencies need updating, edit `fyp-backend/functions/package.json` intentionally and run a clean `npm install`; avoid `npm audit fix --force` here because npm can suggest incompatible Firebase package downgrades that break deploy analysis.

See `docs/SECRETS_ROTATION.md` for rotation runbook.

--------------------------------------------------------------

## 💻 Development Workflow

**Adding New UI Text:**
1. Add key to `enum class UiTextKey` in `UiTextCore.kt`
2. Add English text to `val BaseUiTexts` in `UiTextScreens.kt` (keep order in sync)
3. Use capitalised placeholders (e.g., `{Coins}`) to prevent Azure translation of tokens
4. Add to `ZhTwUiTexts.kt` and `CantoneseUiTexts.kt` (hardcoded, same placeholders)
5. Use composable via `t(UiTextKey.YourKey)`
6. Run `UiTextAlignmentTest` to verify enum count == list count

**UI Language System:**
- All 17 UI languages are hardcoded — switching is instant with zero API calls
- Dropdown order: English → Cantonese → Traditional Chinese → others

**Data Flow:**
1. Compose UI → ViewModel → Use Cases → Repositories → Firestore / Cloud Functions → Azure APIs

**Shared Data Pattern:**
- `SharedHistoryDataSource` provides single Firestore listener shared by History, Learning, WordBank ViewModels
- Includes language count caching with debounced refresh (5s) to reduce reads

**Agent Completion Policy (mandatory for repository-changing prompts):**
- Refresh `docs/treeOfImportantfiles.txt` when files/structure or key entries change
- Audit and update affected docs in `docs/` plus `README.md` when behavior/workflow/metrics change
- If a repeated FYP workflow is likely to recur, prefer creating or refining a shared repo skill under `.github/skills/` instead of leaving the process ad hoc
- Run Android verification before finalizing: `.\\gradlew.bat :app:testDebugUnitTest` and `.\\gradlew.bat :app:assembleDebug`
- Include verification outcomes in task summary
- Policy source: `.github/copilot-instructions.md`

--------------------------------------------------------------

## 🚀 Commands

**Build & Test:**
```bash
./gradlew compileDebugKotlin
./gradlew testDebugUnitTest
./gradlew testDebugUnitTest --tests "com.example.fyp.domain.learning.GenerationEligibilityTest"
./gradlew assembleDebug
```

**Firebase:**
```bash
firebase deploy --only functions
firebase deploy
```

**GitHub CLI:**
```bash
winget install --id GitHub.cli
gh auth login
gh pr checkout "PR number"
```

--------------------------------------------------------------

## 🧪 Testing

**Coverage:**
- **Android:** 192 test files, 2,457 unit tests (from the latest `testDebugUnitTest` report)
- **Backend:** 15 test files, 189 tests

See `docs/TEST_COVERAGE.md` for detailed breakdown.

**Key test suites:**
- `OnboardingLogicTest` — First-launch & version-based re-show
- `GenerationEligibilityTest` — Word bank & learning sheet regen rules
- `QuizCoinEarningGuardTest` — Coin award anti-cheat
- `UiTextAlignmentTest` — Critical guard: enum count == list count
- `SecurityUtilsTest` — Input validation & encoding correctness
- `ChatRepositoryLogicTest` — Chat ID generation, unread math
- `FriendSystemIntegrationTest` — Full friend workflow rules
- `DataLayerIntegrationTest` — Cross-repository consistency

**Running tests:**
```bash
./gradlew testDebugUnitTest
cd fyp-backend/functions && npm test
```

--------------------------------------------------------------

## 📊 Data Models

**Key entities:**
- `TranslationRecord` — Single translation (source/target, languages, mode, session, timestamp)
- `UserSettings` — Preferences (language, font scale, theme, palette, voice, history limit, notifications)
- `QuizAttempt` — Full quiz with answers, scores, timestamps
- `FavoriteRecord` / `FavoriteSession` — Bookmarks & saved conversations
- `CustomWord` — User-defined vocabulary
- `HistorySession` — Continuous conversation metadata
- `PublicUserProfile` — Searchable profile (username, `isDiscoverable`)
- `FriendRequest`, `FriendRelation`, `FriendMessage`, `SharedItem` — Friend system models

**Firestore collections:**
- `users/{uid}/history` — Translation records
- `users/{uid}/sessions` — Continuous sessions
- `users/{uid}/profile/settings` — User settings (theme, font, language prefs)
- `users/{uid}/profile/public` — Public profile (username, isDiscoverable)
- `users/{uid}/learning_sheets/{pair}` — Generated content per language pair
- `users/{uid}/quiz_attempts/{attemptId}`, `users/{uid}/quiz_stats/{pair}` — Quiz data
- `users/{uid}/generated_quizzes/{pair}` — Cached quiz questions
- `users/{uid}/user_stats/coins` — Coin balance
- `users/{uid}/favorites/{favoriteId}`, `users/{uid}/favorite_sessions/{sessionId}` — Bookmarks
- `users/{uid}/word_banks/{pair}`, `users/{uid}/custom_words/{wordId}` — Vocabulary
- `users/{uid}/friends/{friendUid}`, `users/{uid}/shared_inbox/{itemId}`, `users/{uid}/blocked_users/{blockedUid}` — Friend system
- `users/{uid}/shared_inbox/{itemId}/content/body` — Shared item full-content document
- `users/{uid}/fcm_tokens/{token}` — Notification tokens (pruned after 60 days)
- `users/{uid}/last_awarded_quiz/{pair}` — Anti-cheat last quiz award state
- `users/{uid}/quiz_versions/{pair}` — Server-side quiz version tracking (anti-cheat)
- `users/{uid}/coin_awards/{versionKey}` — Server-side coin award history (anti-cheat)
- `users/{uid}/user_stats/language_counts` — Per-user, per-language history record counts cache
- `usernames/{username}`, `user_search/{uid}` — Username registry & searchable index
- `friend_requests/{requestId}`, `chats/{chatId}/messages/{messageId}` — Requests & messages
- `rate_limits/{docId}` — Server-side rate limiting (learning content generation and write throttles)
- `feedback/{feedbackId}` — User feedback submissions

--------------------------------------------------------------

## 🔐 Security & Privacy

- All user data in Firestore with security rules
- API keys protected via Cloud Functions (never exposed to client)
- Audio captured only for recognition, not stored
- Firebase Authentication for secure login
- Auto sign-out on app version update
- Profile visibility setting (Public/Private)
- Block system (server-side enforcement in Firestore rules)
- FCM tokens pruned after 60 days
- ProGuard obfuscation in release builds

--------------------------------------------------------------

**Last Updated:** April 18, 2026
