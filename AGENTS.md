# AGENTS.md — rules for AI coding agents

This is the **single source of rules** for any AI agent (Claude Code, Copilot,
Codex, Cursor, and others) and for human contributors. `CLAUDE.md` and
`.github/copilot-instructions.md` only point here. Change rules in this file only.

## 1. What this project is

**TalknLearn** is an Android app for translation and language learning, built as
a final-year project (FYP). The FYP is already graded. The current work is
portfolio polish: a UI/UX redesign plus client-only learning features. See
[`docs/ROADMAP.md`](docs/ROADMAP.md) for the plan and the status of each phase.

- **Android app:** `app/`, package `com.translator.TalknLearn`.
- **Backend:** `fyp-backend/`, holding Firebase Cloud Functions (TypeScript) and the Firestore rules.
- **Startup benchmark:** `macrobenchmark/`.

## 2. Tech stack (do not change without an ADR)

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.0, JVM target 11, JDK 17+ |
| UI | Jetpack Compose with Material 3 (Compose BOM `2024.09.00`) and Navigation Compose |
| Architecture | MVVM + Clean Architecture: UI → ViewModel → UseCase → Repository |
| Dependency injection | Hilt |
| Async | Coroutines + Flow |
| Data | Firestore only (no Room). DataStore is only for non-authoritative local state ([ADR 0003](docs/adr/0003-firestore-only-data-layer.md)) |
| Backend | Firebase Auth, Firestore, Cloud Functions v2 behind App Check, FCM |
| Speech and translation | Azure Speech SDK and Azure Translator, through tokens issued by callables |
| AI generation | Server-side only; the key never reaches the app ([ADR 0004](docs/adr/0004-azure-openai-for-generation.md)) |
| UI strings | Hardcoded Kotlin maps for 17 languages, not `strings.xml` ([ADR 0005](docs/adr/0005-hardcoded-ui-translations.md)) |
| Tests | JUnit 4, mockito-kotlin, kotlinx-coroutines-test, hand-written fakes, and Roborazzi screenshots ([ADR 0006](docs/adr/0006-roborazzi-screenshot-tests.md)) |
| Static analysis | Detekt with ktlint formatting and a baseline (`app/detekt-baseline.xml`) |

Versions live in `gradle/libs.versions.toml`. Do not bump the Compose BOM,
Kotlin or AGP unless the user asks for it.

## 3. Folder layout

The paths below are inside `app/src/main/java/com/translator/TalknLearn/`.

| Folder | What lives there |
| --- | --- |
| `navigation/` | The Compose nav graph (`AppNavigation.kt`) and its feature sub-graphs |
| `appstate/` | App-wide state: auth, badges, offline, UI language |
| `screens/<feature>/` | One folder per feature, holding its Screen composables and ViewModels |
| `domain/` | Use cases and pure business rules, unit-tested with no Android dependencies |
| `data/` | Repositories, Cloud Function clients, caches and Firestore wrappers |
| `core/` | Shared utilities. `core/ui/CommonUi.kt` holds `StandardScreenScaffold`, `StandardScreenBody` and `ConfirmationDialog` |
| `ui/theme/` | Theme (`FYPTheme`), palettes, typography and `Dimens.kt` (spacing and corner tokens) |
| `model/ui/strings/` | `UiTextKey` (in `UiTextCore.kt`), the English base texts and `translations/` (16 locale maps) |

Other locations:

- Unit tests mirror the main package under `app/src/test/java/...`.
- Screenshot tests are under `app/src/test/java/.../screenshots/`, and their images go in `app/src/test/screenshots/`.
- A map of the important files is in [`docs/treeOfImportantfiles.txt`](docs/treeOfImportantfiles.txt).

## 4. Code style

- **Follow the existing structure and patterns.** Do not restructure folders or replace libraries unless the user asks.
- **Layering:** UI never calls repositories or Firebase directly. ViewModels expose `StateFlow` UI state, and screens collect it with `collectAsStateWithLifecycle()`.
- **Firestore writes:** use `update()` with dot-notation for nested fields ([ARCHITECTURE_NOTES §2](docs/ARCHITECTURE_NOTES.md)).
- **Screens:** build them with `StandardScreenScaffold` and the tokens in `ui/theme/Dimens.kt`. Do not hardcode colors; use `MaterialTheme.colorScheme`.
- **User-facing text:** always use `t(UiTextKey.X)`. No English literals in UI or ViewModels, and that includes `contentDescription`. ViewModels have no translator: emit a `UiTextKey` (for example an `errorKey` field) or a `StatusText(key, args)` (`model/ui/StatusText.kt`) when the text contains data, and let the screen resolve it. Args hold data only, never English UI text.
- **Formatting:** Detekt and ktlint. New code must not add Detekt issues; the baseline only covers old code. Keep imports sorted (lexicographic, with `java`, `javax` and `kotlin` last). `@Composable` functions use PascalCase names; Detekt allows this (`FunctionNaming.ignoreAnnotated` in `config/detekt/detekt.yml`).
- **Previews are allowed.** Write them as `private @Preview` functions wrapped in `FYPTheme(dynamicColor = false) { }`. They are the one exception to the "no unwired code" rule in §7.
- **Screenshot tests:** screens must render from plain parameters, as stateless composables, so they can be screenshot-tested. Test helpers (`ScreenshotTheme`, `SampleData`) are in `screenshots/ScreenshotSupport.kt` in the test sources.

## 5. Branches and pull requests

- **Never push to `main` and never merge.** Only the user merges.
- Work on a feature branch. Each piece of work gets one **draft** PR.
- Fill in the `.github/PULL_REQUEST_TEMPLATE.md` headings, including the test counts and, for UI changes, screenshots.
- Update the docs (README, `docs/`, the tree file, this file) **in the same PR** as the code change.
- Never commit secrets or gitignored config: `app/google-services.json`, `local.properties`, `firebase-sa.json`, `.env`.

## 6. Checks to run before you say "done"

```bash
cp -n ci/google-services-mock.json app/google-services.json   # once, in a clean checkout
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
./gradlew :app:detekt
./gradlew :app:verifyRoborazziDebug        # screenshot checks; use recordRoborazziDebug after an intended UI change
```

The checks need `ANDROID_HOME` set, or `sdk.dir` in `local.properties`, pointing
to an SDK that has platform 36. On Windows, run `gradlew.bat` instead.

If you changed the backend:

```bash
cd fyp-backend/functions && npm ci && npm run lint && npm run build && npm run test:coverage -- --runInBand
```

## 7. End-of-task checklist (mandatory for every change to the repo)

1. **Tree file.** If you add, remove, rename or move files, update `docs/treeOfImportantfiles.txt`. If no update is needed, say that you checked.
2. **Docs audit.** Run the **docs-accuracy-audit** skill (`.github/skills/docs-accuracy-audit/SKILL.md`). At minimum check:
   - The test counts in `docs/TEST_COVERAGE.md` and `README.md` match the `testDebugUnitTest` XML output exactly.
   - The security constants (for example the minimum password length in `SecurityUtils.kt`) match all 17 UI string files.
   - The callable names in `docs/CLOUD_FUNCTIONS_API.md` match the exports in `fyp-backend/functions/src/index.ts`.
   - The symbols named in `docs/ARCHITECTURE_NOTES.md` still exist.
   - Every file listed in the tree file exists, and no gitignored file appears in it.
   - Numbers written in code comments match the constants they describe.
3. **UI text.** Every new or changed `UiTextKey` is in English **and** all 16 locale maps. Use the **ui-text-localization** skill and run `UiTextCompletenessTest`.
4. **Quality gates.** Run the checks in §6 and report the results honestly. If something failed or you skipped it, say so.
5. **Summary.** Put the docs-accuracy-audit table (PASS / FAIL / N/A for each check) in your final summary and in the PR.

**No dead code, and the report stays truthful:**
- Every new file, function or class must be called from at least one production code path. `@Preview` functions and test-only code are the exceptions. Check usages with grep before you finish.
- Do not remove code that the submitted coursework report describes unless you first re-implement the same functionality. If you are unsure, ask the user.
- Never document a feature that does not actually work.

## 8. Things not to change (invariants)

These rules are explained in full in [`docs/ARCHITECTURE_NOTES.md`](docs/ARCHITECTURE_NOTES.md). Breaking one costs money, opens a cheat, or loses data.

**Strings and language**
- **UiText alignment (§1):** `UiTextKey` ordinals map 1-to-1 onto `BaseUiTexts`. Add new keys only at the end of their group, keep the order, and never reorder existing keys.
- **Learning language (§24.2):** learning, word bank and quiz content is keyed by the account's **primary language**, not by the app's UI language.

**Firestore cost**
- **History limit (§7):** do not raise `DEFAULT_HISTORY_LIMIT` or remove the history `limit()`. It caps Firestore read costs.
- **Shared listeners (§6, `SharedHistoryDataSource`):** keep one shared listener. Do not add per-screen listeners on history or friends.

**Coins and anti-cheat**
- **Coins (§5, §28):** coins are awarded only by the server. The client's eligibility logic must match the server's.
- **Favorites limit (§37):** the server counts the favorites limit. Do not move that check to the client only.

**Account deletion (§4)**
- If you add a new per-user subcollection, you must also add it to `USER_SUBCOLLECTIONS` in `fyp-backend/functions/src/accountDeletion.ts` and to `AccountDeletionGuardTest`, and that needs a backend deploy.
- Prefer storing new client-only data in `users/{uid}/user_stats/*`, which is already deleted with the account and is writable by its owner.

**Backend**
- **App Check:** create callables through `functionWrappers.ts`, never with a raw `onCall` or `onRequest`.
- **Provider spending (§1.3.2, `docs/DEMO_SPENDING.md`):** do not remove the monthly allowance, the rate limits or the request-ID checks.
- **No new paid calls:** do not add new paid AI or Azure calls without the user's approval. New features should reuse the data the app already stores.

## 9. Decision log

Large decisions are recorded as ADRs in [`docs/adr/`](docs/adr/README.md). Read
them before you change a related area. If you need to change a decision, write
a new ADR that supersedes the old one; do not edit an accepted ADR.

| ADR | Decision |
| --- | --- |
| 0001 | Jetpack Compose for all UI |
| 0002 | Hilt for dependency injection |
| 0003 | Firestore is the only data layer (no Room) |
| 0004 | AI generation stays on the server (Azure OpenAI-compatible gateway) |
| 0005 | UI translations are hardcoded Kotlin maps |
| 0006 | Roborazzi JVM screenshot tests, plus private `@Preview`s |

Smaller decisions and the phase plan are in [`docs/ROADMAP.md`](docs/ROADMAP.md).

## 10. Repo skills

Step-by-step guides for common jobs are in `.github/skills/`:

| Skill | Use it for |
| --- | --- |
| `android-quality-gates-and-docs` | Running the Android checks and updating the docs |
| `backend-quality-gates-and-build` | Running the backend checks and build |
| `backend-test-gap-audit` | Finding gaps in backend tests |
| `comprehensive-quality-workflow` | A full fix-test-document pass |
| `create-reusable-skill` | Writing a new skill |
| `dead-code-prevention` | Checking that new code is wired in |
| `docs-accuracy-audit` | The mandatory final docs check |
| `ui-text-localization` | Adding UI text in all 17 languages |
