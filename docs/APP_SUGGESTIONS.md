# App Suggestions — Codebase Audit

> **Scope:** Forward-looking suggestions across security, QA, performance, UX,
> backend, accessibility, and developer experience. Not a defect list — items
> below are improvement opportunities, ranked roughly by impact / effort.
>
> **Source of truth read for this audit:** `README.md`, `docs/ARCHITECTURE_NOTES.md`,
> `docs/CLOUD_FUNCTIONS_API.md`, `docs/TEST_COVERAGE.md`,
> `docs/PRIVACY_AND_COMPLIANCE.md`, `docs/treeOfImportantfiles.txt`,
> `fyp-backend/functions/src/`, `app/src/main/java/com/translator/TalknLearn/`.

---

## 1. Security & Privacy

1. **Re-enable account registration with abuse controls.** README §Development
    Cautions notes registration is disabled project-wide. Before submission /
    public release, gate registration behind: (a) Firebase App Check enforcement
    on the registration callable, (b) per-IP and per-device daily caps in the
    `rate_limits/` collection, (c) email-domain allow/deny list if needed.
    Document the re-enable runbook alongside `docs/SECRETS_ROTATION.md`.
2. **Firestore security-rules tests.** Backend has 193 Jest tests, but the
    repo doesn't expose a `firestore.rules` emulator test suite. Add a
    `@firebase/rules-unit-testing` suite covering: blocked-user write denial,
    `friend_requests` rate-limit doc immutability, `coin_awards` insert-only,
    `quiz_versions` monotonic increments, `users/{uid}/...` cross-user denial.

Implemented in this sweep: App Check wrapper guard, Crashlytics PII guard,
server-side right-to-erasure callable, backup-rule regression tests, and a
yearly secret-rotation issue template. Secret rotation no longer requires live
Firestore metadata.

## 2. Testing & Quality

8. **Coverage gate for Android.** Backend has Jest coverage thresholds of
    90/78/90/90 (statements/branches/functions/lines). Add a JaCoCo (or Kover) report task and start an Android coverage
    floor — even a low initial gate (e.g., 35% line) prevents drift in the
    2,486-test suite.
9. **Mutation testing for critical anti-cheat code.** Run Pitest /
   `pitest-gradle` on `domain/learning` and the coin-award guard
   (`QuizCoinEarningGuardTest`). These are the highest-blast-radius pieces of
   business logic; surviving mutants there are the most valuable to fix.
10. **UI snapshot / Compose tests.** The repo has 241 main Kotlin files and
    197 test files but appears to be unit-test-heavy. Add Compose UI tests
    (Paparazzi or `androidx.compose.ui.test`) for: bottom-nav badge states,
    offline banner, error auto-dismiss, empty-state screens for Learning /
    Word Bank when no language pair is eligible.
11. **Property-based tests for translation / language detection.** Use
    Kotest property tests for `DetectLanguageUseCase` fallback chain
    (Architecture §1.2) and for `SecurityUtils` input validation, where edge
    cases (Unicode, RTL, ZWJ, surrogate pairs) are easy to miss with
    example-based tests.
12. **Backend integration test against the emulator suite.** Current Jest
    tests are likely module-level. Add at least one happy-path test per
    callable (`translation`, `learning`, `coins`, `notifications`) running
    against the Firebase emulator in CI to catch wiring regressions.
13. **Flaky-test budget.** Add `--rerun-tasks` plus a CI step that fails the
    PR if any test was retried more than once, to keep the suite trustworthy.

## 3. Performance

14. **Cold-start budget.** Add a Macrobenchmark module measuring app startup
    (cold/warm), Quick Translate first-frame, and Friends list scroll jank.
    Check the result into CI as a baseline; fail PRs that regress p50/p90
    by >15%.
15. **Baseline Profiles.** Generate a Baseline Profile for the top 3 user
    flows (Quick Translate, History list, Friends chat). Documented gain on
    similar Compose apps is 20–30% TTI improvement.
16. **Firestore listener audit.** README §Shared Data Pattern says
    `SharedHistoryDataSource` is a single shared listener. Add a runtime
    debug counter (visible in a hidden dev menu) showing the live count of
    Firestore listeners, so accidental duplicates introduced later are
    immediately visible during manual QA.
17. **R8 / ProGuard rules audit.** Verify `proguard-rules.pro` keeps only
    what's actually needed. With Hilt + Serialization + Compose, accidental
    `-keep class **` rules are common and bloat APK size. Use
    `./gradlew :app:dependencyInsight` plus the R8 missing-rules report.
18. **Image / OCR pipeline.** OCR (camera/gallery) often holds large
    Bitmaps. Confirm decode happens with `inSampleSize` or
    `ImageDecoder.setTargetSize` and that bitmaps are recycled or scoped to
    a `DisposableEffect`. LeakCanary doc exists — add a specific leak-canary
    watch for the OCR ViewModel.
19. **Coroutine dispatcher discipline.** Audit repositories for
    `Dispatchers.IO` / `Dispatchers.Default` usage; mis-dispatching network
    work onto Main is a common source of jank in MVVM apps. A custom Detekt
    rule (`forbidden-comment` or a small custom rule) can flag
    `withContext(Dispatchers.Main)` around suspending I/O.

## 4. UX & Features

20. **Translation history search ranking.** Current filter is by
    language/keyword (README §History). Add fuzzy search (Levenshtein /
    bigram) so typo'd recall (`konichiwa` → `こんにちは`) still finds the
    record.
21. **Quiz review mode.** After a quiz attempt, let users see which
    questions they got wrong with the correct answer, without it counting
    as a re-attempt for coin rewards. Reuses existing `QuizAttempt` data.
22. **Word-bank spaced repetition.** Add an SRS layer (SM-2 / FSRS) on top
    of `word_banks/{pair}` so daily review surfaces due words. This unlocks
    far more learning value from data the app already collects.
23. **Conversation export.** Allow exporting a saved Live Conversation
    favorite as plain-text or `.srt`. Useful for users who want to reuse the
    transcript outside the app.
24. **Onboarding skip + replay.** Onboarding re-shows after every update
    (README §User Accounts). Add an explicit "Replay onboarding" entry in
    Settings → Help so users can revisit it on demand without waiting for an
    update; and a "Skip" link for power users who don't want it after each
    update.
25. **Friend chat: typing indicator + delivery state.** Both are cheap to
    add on top of existing `chats/{chatId}/messages` and would significantly
    improve perceived chat quality.
26. **Coin economy transparency.** Add a "How coins are earned" screen
    listing the rules already encoded in `QuizCoinEarningGuardTest`. Users
    currently learn the rules by trial and error.
27. **Feedback follow-up channel.** `feedback/{feedbackId}` is write-only
    today. Add an opt-in "allow developer reply" flag so users can be
    contacted via FCM when their report is acted on.

## 5. Accessibility & Internationalization

28. **TalkBack / content descriptions sweep.** With 17 UI languages, missing
    `contentDescription` on icon-only buttons (bottom nav, language swap,
    favorite toggle, retry) is a high-value accessibility fix. Add a Compose
    lint rule and a unit test that walks `Modifier.semantics` on key
    composables.
29. **Dynamic font scaling end-to-end.** App supports 80–150% font scale.
    Add a screenshot test at 150% for Quick Translate and Quiz screens to
    catch text clipping early.
30. **RTL support.** Arabic / Hebrew aren't in the 17 languages, but
    several supported scripts (Thai, Vietnamese diacritics, CJK
    line-breaking) are still layout-sensitive. Add `LayoutDirection.Rtl`
    snapshot tests so when RTL is added later, regressions are caught at
    PR time.
31. **High-contrast palette.** 11 color palettes exist; none is documented
    as WCAG-AA contrast-verified. Add a unit test using
    `androidx.core.graphics.ColorUtils.calculateContrast` to assert text /
    background contrast ≥ 4.5 for every palette.
32. **Locale completeness CI.** Architecture §1 enforces enum/list count
    parity for English. Extend the same guard to *every* locale file
    (`ZhTwUiTexts`, `CantoneseUiTexts`, …) so a missing translation key
    fails CI instead of silently falling back.

## 6. Backend / Cloud Functions

33. **Structured logging schema.** `logger.ts` exists; add a documented
    log-line schema (correlation id, uid hash, function name, latency,
    upstream-status) and emit metrics derived from it. Helps observability
    and SLO tracking on the free tier.
34. **Cost guard.** Translation, OCR, and Azure OpenAI calls are the cost
    drivers. Add a per-uid daily token/character budget enforced
    server-side, plus a Cloud Function alert when a single user exceeds
    e.g. 5x median daily usage (abuse signal).
35. **Translation cache.** Many phrases recur across users (greetings,
    common nouns). A keyed cache `translation_cache/{srcLang}_{tgtLang}_{hash}`
    with a TTL document would meaningfully cut Azure spend without sacrificing
    accuracy.
36. **Idempotency keys for coin awards.** README mentions per-version coin
    award history; harden it with a client-supplied idempotency key so
    network-retried callables never double-award even in pathological cases.
37. **Pinned Node runtime.** Confirm `runtime: nodejs20` (or current LTS) in
    `firebase.json` and add a CI check matching the runtime declared in
    `package.json` `engines.node`. Drift here causes silent deploy failures.

## 7. CI & Developer Experience

38. **CI matrix.** Add JDK 21 alongside JDK 17 in `ci.yml` to catch
    upcoming-toolchain regressions before the AGP minimum bumps.
39. **Auto-generated treeOfImportantfiles.** The tree file is hand-maintained
    (`docs/treeOfImportantfiles.txt`). A small Gradle task or Node script
    that emits a candidate tree from the actual filesystem and diffs it
    against the committed file would eliminate the entire class of
    "tree drifted" tasks the agent skills currently work around.
    *(This audit itself surfaced one such drift: the tree references
    `docs/APP_SUGGESTIONS.md`, created by this document.)*
40. **Renovate / Dependabot grouping.** Group AndroidX / Compose / Hilt
    updates so dependency PRs are reviewable. Today, ungrouped Dependabot
    creates noisy PR streams that get auto-stale.
41. **Pre-commit hooks.** Add `lefthook` or `pre-commit` running
    `ktlintCheck` and `eslint --max-warnings=0` on staged files. Cheap
    insurance vs. CI round-trips.
42. **Architecture decision records (ADRs).** Add `docs/adr/` with one ADR
    per major choice already made (Compose, Hilt, Firestore-only data
    layer, Azure OpenAI for generation, hardcoded UI translations). New
    contributors and future-you benefit immediately.
43. **Contributor checklist in PR template.** A `.github/PULL_REQUEST_TEMPLATE.md`
    embedding the agent skills checklist (tree update, docs audit, tests,
    build) would extend the same hygiene to human contributors.

## 8. Code Health

44. **Module split.** `app/` is a single Gradle module. Splitting into
    `:core`, `:data`, `:domain`, `:feature-speech`, `:feature-friends`,
    `:feature-learning` would (a) enable parallel compilation, (b) shorten
    incremental builds, (c) make `internal` visibility actually meaningful
    as an architectural boundary.
45. **Detekt + ktlint baseline.** Generate a baseline so existing warnings
    don't block PRs, but new ones do. Especially valuable for catching
    `runBlocking`, `GlobalScope`, and `!!` introductions.
46. **Compose stability annotations.** Audit data classes passed as Compose
    parameters and apply `@Stable` / `@Immutable` where appropriate. With
    240 Kotlin files, recomposition-cost wins are usually plentiful.
47. **Public API surface of `core/`.** `core/` packages tend to grow into
    everything-buckets. Periodically run `./gradlew :app:apiDump` (with the
    binary-compatibility-validator plugin) to keep the surface intentional.
48. **Replace `!!` and unsafe casts in domain layer.** A grep audit on
    `domain/` for `!!`, `as ` should be a quick targeted cleanup PR.

## 9. Observability

49. **Custom Crashlytics keys.** Attach `currentScreenRoute`,
    `appUiLanguage`, `primaryLanguage`, `accountAgeDays` (bucketed) to
    every crash. Massively improves triage on a multi-locale app.
50. **Performance traces.** Use Firebase Performance custom traces for
    *(a)* time-to-first-translation, *(b)* time-to-quiz-ready,
    *(c)* learning-sheet-generation latency. Surface p50/p95 weekly via a
    scheduled Cloud Function emailing a digest.
51. **Funnel analytics.** Onboarding → first translation → first quiz →
    first friend-add. A simple Analytics funnel makes feature-prioritisation
    decisions data-driven instead of guessed.

## 10. Documentation

52. **Single architecture diagram.** `ARCHITECTURE_NOTES.md` is text-heavy.
    Add one Mermaid diagram showing the data flow already described in
    README §Data Flow (UI → ViewModel → UseCase → Repository → Firestore /
    Cloud Function → Azure).
53. **Onboarding for new contributors.** A `CONTRIBUTING.md` covering
    bootstrap (Firebase project creation, App Check debug token, Azure
    keys), branching, and the agent-skills workflow lowers the friction
    for graders / external reviewers / future maintainers.
54. **API stability note.** `CLOUD_FUNCTIONS_API.md` documents callables;
    add an explicit "Stability: experimental / stable" tag per callable so
    consumers (including future mobile clients) know what to depend on.

---

## Suggested Triage

| Tier | Theme                                                                                  |
| ---- | -------------------------------------------------------------------------------------- |
| P0   | §1.2 Firestore rules tests, §2.10 Compose UI tests, §6.34 cost guard |
| P1   | §3.14 startup benchmark, §3.15 baseline profile, §4.22 SRS, §5.32 locale completeness CI    |
| P2   | §7.39 auto-generated tree, §7.42 ADRs, §8.44 module split, §9.50 perf traces                |
| P3   | Remaining nice-to-haves                                                                 |

---

*Generated as an audit-style suggestion document. None of these items are
required for the app to function; they are opportunities ranked for impact
on a final-year-project + production-readiness trajectory.*
