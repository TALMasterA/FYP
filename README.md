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
**Key Libraries:** Hilt (DI), Coroutines & Flow, Serialization, OkHttp, Coil, DataStore

--------------------------------------------------------------

## 🛠️ CI/CD & Quality Checks

**GitHub Actions workflows:**
1. **CI (`ci.yml`)**: Android unit tests, debug APK build, backend linting & Jest coverage (50% threshold)
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

--------------------------------------------------------------

## 🎯 Core Features

**Translation Modes:**
- **Quick Translate (Discrete):** Real-time voice translation for short phrases with auto-detect or manual language selection
- **Live Conversation (Continuous):** Multi-turn dialogue with automatic speaker detection (Person A/B)
- **Camera / OCR:** Scan text from images via camera or gallery with language hints
- Multi-language: English, Cantonese, Traditional/Simplified Chinese, Japanese, and 10+ more via Azure
- Language swap button (swaps recognized/translated text)

**Learning System:**
- AI-generated learning sheets from translation history (≥20 records per language pair)
- Quiz system with coin rewards (anti-cheat verification: first-attempt only)
- Auto-generated word bank with progress bar toward next regen
- Learning actions stay disabled until account sheet metadata finishes loading, preventing premature generate/open operations on stale fetch state
- Learning sheet, word bank, and quiz language pairs are controlled by account primary language (not App UI language)
- Custom word bank for user-defined vocabulary
- Favorites for bookmarking translations and saving full conversation sessions
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
- Client-side friend-request rate limiting: 10 per hour, persisted across app restarts
- Username changes have 30-day cooldown (same as primary language changes)
- Red dot badges clear only by viewing their corresponding screen (active/resumed Chat, Shared Inbox, Friends); refresh actions do not clear badges

**Customization:**
- UI language: English, Cantonese (hardcoded), Traditional Chinese (hardcoded), Simplified Chinese, Japanese, 10+ others via Azure
- UI language dropdown shows translation progress/status; completion auto-hides
- UI language switching keeps current language on failures and surfaces explicit rate-limit/service timeout messages
- Primary language selector (30-day cooldown) in Settings
- Theme: Light / Dark / System (smooth animated transitions)
- Font size: 80%–150%
- 11 color palettes (1 free + 10 unlockable at 10 coins each)
- Voice settings per language
- Custom Words entries can update target language per word, and translation is regenerated for the selected language
- Notification settings (push + badge toggles) accessible from the Friends bell icon flow
- Feedback submission is available in `Settings -> Feedback`
- OCR settings (on-device model status and footprint)

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
- Profile management (display name, account deletion)
- Password reset via email
- Auto sign-out on app version update
- First-launch and post-update onboarding (re-shown after each update)
- Except App UI language, all settings and data are tied to user accounts and synced via Firestore

**Navigation & UI:**
- Bottom navigation bar (Home, Quick Translate, Learn, Friends, Settings) with unread badge
- Offline banner when connectivity lost
- Help & Notes screen (Cautions, Features, Tips, Friend System, Privacy)
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
npm run build
npm test
npm run deploy
```

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

**Translation System:**
- Base: English (hardcoded)
- Hardcoded: Traditional Chinese (zh-TW), Cantonese (zh-HK)
- Cached: All other languages via Azure Translator API (DataStore, 30-day TTL, 1000 max entries)
- Guest users: 1 free UI language change; logged-in: unlimited
- Dropdown order: English → Cantonese → Traditional Chinese → others
- Azure codes normalized: `zh-HK → yue`, `zh-TW → zh-Hant`, `zh-CN → zh-Hans`

**Data Flow:**
1. Compose UI → ViewModel → Use Cases → Repositories → Firestore / Cloud Functions → Azure APIs

**Shared Data Pattern:**
- `SharedHistoryDataSource` provides single Firestore listener shared by History, Learning, WordBank ViewModels
- Includes language count caching with debounced refresh (5s) to reduce reads

**Agent Completion Policy (mandatory for repository-changing prompts):**
- Refresh `docs/treeOfImportantfiles.txt` when files/structure or key entries change
- Audit and update affected docs in `docs/` plus `README.md` when behavior/workflow/metrics change
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
- **Android:** 210 test files, 2,788 unit tests (100% on key logic)
- **Backend:** 14 test files, 182 tests

See `docs/TEST_COVERAGE.md` for detailed breakdown.

**Key test suites:**
- `OnboardingLogicTest` — First-launch & version-based re-show
- `GenerationEligibilityTest` — Word bank & learning sheet regen rules
- `CoinEligibilityTest` — Coin award anti-cheat
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
- `users/{uid}/profile/` — Settings, info, public
- `users/{uid}/learning_sheets/{pair}` — Generated content per language pair
- `users/{uid}/quiz_attempts`, `quiz_stats/{pair}` — Quiz data
- `users/{uid}/generated_quizzes/{pair}` — Cached quiz questions
- `users/{uid}/user_stats/coins` — Coin balance
- `users/{uid}/favorites`, `favorite_sessions` — Bookmarks
- `users/{uid}/word_banks/{pair}`, `custom_words` — Vocabulary
- `users/{uid}/friends`, `shared_inbox`, `blocked_users` — Friend system
- `users/{uid}/fcm_tokens` — Notification tokens (pruned after 60 days)
- `usernames`, `user_search` — Username registry & searchable index
- `friend_requests`, `chats/{chatId}/messages` — Requests & messages

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

**Last Updated:** March 24, 2026
