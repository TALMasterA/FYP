<!--
Item 43, docs/APP_SUGGESTIONS.md — embed the agent-skills checklist
(.github/copilot-instructions.md) so human contributors follow the same hygiene
as Copilot agents.
-->

## Summary

<!-- One paragraph: what changed and why. Link the issue / suggestion item if any. -->

## Type of change

- [ ] Bug fix
- [ ] New feature
- [ ] Refactor / code health
- [ ] Documentation only
- [ ] CI / tooling
- [ ] Backend (Cloud Functions / Firestore rules)
- [ ] Other:

## Contributor checklist

The items below mirror `.github/copilot-instructions.md`. Tick each one or
explain why it does not apply.

### Tree & docs

- [ ] `docs/treeOfImportantfiles.txt` updated (or N/A — no files added / removed / renamed).
- [ ] `docs/ARCHITECTURE_NOTES.md` updated when invariants or architecture changed (or N/A).
- [ ] `docs/TEST_COVERAGE.md` and `README.md` test counts match the latest `testDebugUnitTest` XML output (or N/A — tests unchanged).
- [ ] `docs/CLOUD_FUNCTIONS_API.md` matches `exports.*` in `fyp-backend/functions/src/index.ts` (or N/A — backend API unchanged).
- [ ] No gitignored files (`google-services.json`, `local.properties`, `firebase-sa.json`) are referenced as included in submission docs.

### UI text & localization

- [ ] Any new / renamed `UiTextKey` is added to English **and** all 16 locale maps under `app/src/main/java/com/translator/TalknLearn/model/ui/strings/translations/` (or N/A — no UI text changed).
- [ ] `UiTextCompletenessTest` passes (run as part of `testDebugUnitTest`).

### Quality gates

- [ ] `./gradlew :app:testDebugUnitTest` — PASS
- [ ] `./gradlew :app:assembleDebug` — PASS
- [ ] If backend changed: `npm run lint`, `npm run test:coverage -- --runInBand`, `npm run build` in `fyp-backend/functions` — PASS

### Dead-code & report truthfulness

- [ ] Every new file / function / dependency is wired into a production code path (no dead code).
- [ ] No code referenced in `report-audit/Project_Report_ISA_22235876.md` was removed without an equivalent replacement.

## Test counts

<!-- Paste the suite/test totals from your latest run, e.g.: -->
<!-- `testDebugUnitTest`: 196 suites / 2,486 tests / 0 failures / 0 errors -->

## Screenshots / recordings

<!-- For UI changes only. Otherwise delete this section. -->
