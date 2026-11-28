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
| 0 | Agent docs (AGENTS.md), roadmap, screenshot tests, baseline screenshots | In review |
| 1 | Correctness fixes, no new look | Not started |
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

## Known bugs that Phase 1 fixes

- **Learning-sheet and word-bank prompts send the oldest history records**, and the records cover all languages.
  - Cause: `takeLast(N)` is used on a list sorted newest first.
  - Locations: `LearningContentRepositoryImpl.kt`, `WordBankGenerationRepository.kt` and `LearningViewModel.generateFor`.
- **`FirestoreQuizRepository.getRecentAttempts` and `getAttemptsByLanguagePair` return the oldest attempts**, because they sort in ascending order.
- **System back does not close in-screen sub-views** (WordBank, the History session view), and it leaves a quiz without asking.
- **The mic button shows "Stop" while listening, but it is disabled**, so the user cannot stop.
- **The "regeneration blocked" dialog in the learning sheet is never shown.**
- **The Tester Feedback button shows in release builds.**
- **English text bypasses the 17-language system:** about 70 strings in ViewModels and about 30 `contentDescription`s.

## Later (needs a backend deploy or paid calls)

These items are out of scope for now:

- Report a user or message (Google Play checklist 3.1).
- Study reminders through FCM.
- Quiz challenges between friends.
- Pronunciation scoring.
- Building prompts on the server.
- Re-enabling email sign-up.
- **Security hole:** the `profile/settings` Firestore rule checks only types and ranges. A modified client could unlock palettes or raise `historyViewLimit` without spending coins.
