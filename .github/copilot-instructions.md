# Repository Instructions for Copilot Agents

These instructions are mandatory for any prompt that results in repository changes.

## Required End-of-Task Checklist

Before marking a task complete, the agent must do all items below:

1. Refresh docs/treeOfImportantfiles.txt if files, structure, or key documentation entries changed.
2. Audit and update documentation for accuracy using the **docs-accuracy-audit** skill (`.github/skills/docs-accuracy-audit/SKILL.md`). This is mandatory for every repository-changing task. At minimum verify:
   - Test counts in `docs/TEST_COVERAGE.md` and `README.md` match the actual `testDebugUnitTest` XML output exactly.
   - Security constants (e.g., password minimum length in `SecurityUtils.kt`) match all 17 UI string files (English + 16 locales).
   - Cloud Functions callable names in `docs/CLOUD_FUNCTIONS_API.md` match actual `exports.*` in `fyp-backend/functions/src/index.ts`.
   - Symbols named in `docs/ARCHITECTURE_NOTES.md` still exist in the codebase.
   - No file listed in `docs/treeOfImportantfiles.txt` is absent from the workspace.
   - Gitignored files (`google-services.json`, `local.properties`, `firebase-sa.json`) do NOT appear in the tree file or in any submission document.
   - Numeric literals in code comments match the actual constant or default value they describe.
3. Verify Android quality gates:
- .\\gradlew.bat :app:testDebugUnitTest
- .\\gradlew.bat :app:assembleDebug
4. Report verification outcomes in the final summary. Include the docs-accuracy-audit checklist table with PASS / FAIL / N/A for each applicable check.

## Tree File Guard

If any files are added, removed, renamed, or materially reorganized, docs/treeOfImportantfiles.txt must be updated in the same task.
If no tree update is required, explicitly state that it was checked.

## Documentation Guard

When behavior, invariants, limits, counters, architecture, or operational procedures change, the corresponding docs file must be updated in the same task.
At minimum, review:
- docs/ARCHITECTURE_NOTES.md
- docs/TEST_COVERAGE.md (if tests or counts change)
- docs/CLOUD_FUNCTIONS_API.md (if callable API/contract changes)
- README.md (if user-visible behavior or workflow changed)

## UiText Localization Guard

When adding, renaming, or changing any user-facing `UiTextKey`, hardcoded UI label, or app UI language entry, use the **ui-text-localization** skill (`.github/skills/ui-text-localization/SKILL.md`). Every new UI text must be added to the English base text and all 16 supported locale maps before finalizing. Run the locale completeness tests and do not leave English-only fallback text in a localized UI path.

## Completion Rule

Do not finalize until code changes, tree maintenance, documentation updates, and verification commands are all completed (or explicitly blocked with a reason).

## Dead Code / Report Truthfulness Guard

1. **Never create code that is not wired into the live app.** Every new file, function, or class must be called from at least one production code path. Run a grep/usage check before finalizing.
2. **Never remove code that is referenced in the submitted report** (`report-audit/Project_Report_ISA_22235876.md`) without re-implementing equivalent functionality first.
3. **Never write report content about code that does not actually work.** If a feature is described in the report, the corresponding code must exist, compile, be reachable, and be tested.
4. When deleting or refactoring, check whether the affected symbol appears in the submitted report. If it does, either preserve the functionality or flag it to the user before proceeding.
