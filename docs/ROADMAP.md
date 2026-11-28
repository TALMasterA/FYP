# Post-FYP roadmap — enhancements and full UI/UX redesign

This roadmap is the plan for the portfolio polish round, which happens after
the FYP was graded. Keep the status table up to date in every PR.

**Goals**
- A full UI/UX redesign.
- Better features in four areas: the learning loop, translation tools, the Home dashboard, and social.

**Cost rule**
- New features are **client-only**: no new paid AI or Azure calls.
- They reuse data the app already stores: history, word banks, quizzes, custom words and favorites.

## Status

| Phase | Scope | Status |
| --- | --- | --- |
| 0 | Agent docs (AGENTS.md), roadmap, screenshot tests, baseline screenshots | Done |
| 1 | Correctness fixes, no new look | Parts 1–3 done; part 4 in review; part 5 next |
| 2 | Design system: mockup approval, theme, component kit, navigation shell | Not started |
| 3 | Home dashboard and translation tools | Not started |
| 4 | Learning loop: review (SRS), streak, daily goal, stats, quiz feedback | Not started |
| 5 | Social: chat, friends, sharing | Not started |
| 6 | Settings, account, onboarding, accessibility pass, docs | Not started |

Each phase is one draft PR. The next phase starts after the previous PR is merged.

## Decisions

These are the decisions for this roadmap. Each one gets its own ADR in the
phase that carries it out.

1. **Client-only data goes in `users/{uid}/user_stats/*`**, for example `user_stats/learning_progress` and `user_stats/reviews_{primary}__{target}`.
   - The generic owner-write rule in `fyp-backend/firestore.rules` already covers these documents.
   - The account-deletion list in `fyp-backend/functions/src/accountDeletion.ts` already deletes them.
   - So there are no rules changes and no backend deploy.
2. **Keep the stack and the conventions.** This means the same layering and the same `UiTextKey` system with 17 languages. There is no Compose BOM upgrade; version 2024.09 already has what the redesign needs.
3. **Build the theme from each palette's seed color** with MaterialKolor, a port of Google's material-color-utilities (Phase 2, new ADR).
   - This fixes WCAG contrast for all 11 palettes and adds the missing color roles (`surfaceContainer*`).
4. **Roborazzi screenshot tests** run on the JVM, so every UI change can be seen without an emulator ([ADR 0006](adr/0006-roborazzi-screenshot-tests.md)).
5. **A small in-house Markdown renderer** for learning sheets (headings, bullets, bold), unit-tested. The sheet format does not need a library.

## Phase 1 — correctness fixes

Phase 1 is split into five PRs so each one stays small enough to review.

**Part 1 (fixed):**
- **Learning-sheet and word-bank prompts sent the oldest history records**, in all languages.
  - Cause: `takeLast(N)` on a list sorted newest first.
  - Fix: `domain/learning/PromptRecordSelector.kt` keeps only records in the target language and sorts by timestamp itself, so the input order does not matter. Tested in `PromptRecordSelectorTest`.
- **Quiz history returned the oldest attempts.** `FirestoreQuizRepository` now sorts `completedAt` descending. The composite index already exists in `fyp-backend/firestore.indexes.json`, so no deploy is needed.
- **System back** now closes the WordBank sub-views and the History session view, and asks "Leave quiz?" when a quiz has answers.
- **`popBackStack()` ran during composition** in `LearningWordBankGraph.kt` when an argument was blank. It now runs in a `LaunchedEffect`.
- **The "regeneration blocked" dialog** in the learning sheet now shows when new records are needed.
- **Settings:** Tester Feedback shows only in debug builds. Reset Password shows only for email accounts; Google accounts see "Password managed by Google".
- **Keyboard:** `adjustResize` plus the IME inset in `StandardScreenScaffold`, so the keyboard no longer covers Chat, Login or word inputs.
- **First launch** uses the device language when the app has texts for it (`data/ui/DeviceLocaleMatcher.kt`). The choice is not saved, so it follows the device until the user picks a language.

**Decision — the mic has no real Stop.** Azure's `recognizeOnceAsync()` stops by itself at the end of speech and has no safe cancel call. Closing the native recognizer mid-call cannot be tested without a device. So instead of a disabled Stop icon, the button now shows a clear "Listening… speak now" state. A real start/stop toggle comes with continuous recognition in Phase 3.

**Decision — Detekt allows PascalCase `@Composable` names** (`FunctionNaming.ignoreAnnotated: ['Composable']` in `config/detekt/detekt.yml`). This is the Compose naming rule. Without it, every new composable is a Detekt issue.

**Part 2 (fixed) — localization sweep, first half:**
- **Icon labels:** 29 English `contentDescription`s (History, Favorites, Chat, Speech, Word bank, Quiz) now use `UiTextKey`, so TalkBack reads them in the UI language.
- **History:** error messages use `errorKey`, and the text-to-speech status ("Speaking…", "✓ Spoken") is localized.
- **Speech / Translate / Continuous:** every status line (detecting, translating, errors, image scan) is localized.
- 46 new keys, in English and all 16 locale maps.

**Decision — `StatusText` for ViewModel messages** (`model/ui/StatusText.kt`). A ViewModel has no translator, and some messages contain data ("Detected: Japanese"). So a ViewModel emits `StatusText(key, args)` and the screen calls `.resolve(t)`. Args hold data only (a service error, a language name), never English UI text. Fixed messages without data can use a plain `errorKey: UiTextKey?` field, like `SettingsViewModel`. Logic that used to compare English strings (the auto-clear of "Detected: …") now compares keys.

**Part 3 (fixed) — localization sweep, social area:**
- **Friends:** every message from `FriendsViewModel` and its delegates (send, accept, decline, cancel, batch, remove, block, unblock) is a `StatusText`. Numbers (limits, counts, minutes) are args.
- **Chat, My profile, Shared inbox, Shared material detail:** the `error`, `successMessage` and `translationError` fields are `StatusText?`, and the screens call `.resolve(t)`.
- **`ErrorMessages.kt`:** the new `statusFromException(e, fallbackKey)` returns a `StatusText`. The old `fromException(e, fallback): String` still works for callers that are not migrated yet. It now builds its text from the same English keys, so both forms always agree (tested in `ErrorMessagesTest`).
- 73 new keys, in English and all 16 locale maps.

**Decision — `StatusText.raw(text)` for data-layer text.** Some exceptions carry a message from our own checks (`SecurityException("Cannot share with this user")`). We have no key for every such message, so `ErrorMessages` passes it through with the `StatusRaw` key (template `{detail}`). This is the same behavior as before; it is only for data, never for UI text written in a ViewModel.

**Decision — Shared inbox and Chat send errors go through `ErrorMessages`.** Before, they showed the raw `error.message` (for example a Firestore message). Now known errors (no network, permission, not found …) show a localized message, and unknown ones show the localized fallback.

**Decision — the shared `validateTextLength` stays English.** It has 7 callers. The Chat message and friend-request note now check its result and emit their own key ("Message must not exceed {max} characters"). The other callers move in parts 4 and 5.

**Part 4 (fixed) — localization sweep, Settings, Shop, Profile, Login and Feedback:**
- **Feedback:** `errorMessage` is a `StatusText?`. Empty, too short ({min}), too long ({max}), rate limit and send failure each have a key. `setError` takes a `UiTextKey`.
- **Shop:** `purchaseError`, `purchaseSuccess` and `unlockError` are `StatusText?`. "Not enough coins" and "Maximum limit reached" reuse the existing `ShopInsufficientCoins` and `ShopMaxLimitReached` keys.
- **Profile:** `error`, `successMessage` and `deleteError` are `StatusText?`. Username rules, "taken", the update results and the delete and Google re-auth errors each have a key. The Google re-auth hint and the "Continue with Google" button on the delete dialog are localized too.
- **Settings:** the save-failure messages (language, font size, theme, auto theme, voice, notifications) each have a key.
- **Login and Reset password:** see the `AuthUiState` decision below.
- 58 new keys, in English and all 16 locale maps.

**Decision — `ErrorMessageMapper.mapKey(throwable): UiTextKey`.** The mapper turns Firebase Auth, Firestore and network exceptions into safe messages. `mapKey` returns a key; the old `map` now returns `BaseUiTexts[mapKey(t).ordinal]`, so its English text is the same as before (tested in `ErrorMessageMapperKeyTest`). Login uses `mapKey`. `mapSpeechRecognitionError`, `mapTranslationError` and `mapOcrError` stay English for now; they are data-layer text and move in a later part.

**Decision — `SettingsUiState` drops `errorRaw`.** Every Settings message now has a key, so the screen reads only `errorKey`.

**Decision — `AuthUiState` has one `error: StatusText?`.** It replaces `errorKey` + `errorRaw`. `messageRaw` was never set, so it is removed; `messageKey` stays. `reportGoogleSignInError` takes a `StatusText`, and the Google error code is an arg ("Google sign-in failed (code {code}).").

**Decision — Shop no longer shows raw server reasons or exception messages.** An unknown `reason` from the coin Cloud Function shows "Purchase failed" or "Failed to unlock palette". Exceptions go through `ErrorMessages.statusFromException` with the same fallback keys.

**Part 5:** Learning and learning sheets, the Word bank delegates, custom words, Favorites, the other `ErrorMessages.fromException` callers, the `EmptyStateView` titles and Startup. "Session {id}" in `HistoryViewModel` stays as it is, because it is saved data, not UI text.

## Later (needs a backend deploy or paid calls)

These items are out of scope for now:

- Report a user or message (Google Play checklist 3.1).
- Study reminders through FCM.
- Quiz challenges between friends.
- Pronunciation scoring.
- Building prompts on the server.
- Re-enabling email sign-up.
- **Security hole:** the `profile/settings` Firestore rule checks only types and ranges. A modified client could unlock palettes or raise `historyViewLimit` without spending coins.
