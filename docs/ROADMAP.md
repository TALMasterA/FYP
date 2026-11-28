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
| 1 | Correctness fixes, no new look | Part 1 in review; part 2 (localization sweep) next |
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

Phase 1 is split into two PRs so each one stays small enough to review.

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

**Part 2 (next PR) — localization sweep:**
- About 70 English strings in ViewModels and about 30 English `contentDescription`s still bypass the 17-language system. They move to `UiTextKey`.

## Later (needs a backend deploy or paid calls)

These items are out of scope for now:

- Report a user or message (Google Play checklist 3.1).
- Study reminders through FCM.
- Quiz challenges between friends.
- Pronunciation scoring.
- Building prompts on the server.
- Re-enabling email sign-up.
- **Security hole:** the `profile/settings` Firestore rule checks only types and ranges. A modified client could unlock palettes or raise `historyViewLimit` without spending coins.
