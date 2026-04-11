# Repository Instructions for Copilot Agents

These instructions are mandatory for any prompt that results in repository changes.

## Required End-of-Task Checklist

Before marking a task complete, the agent must do all items below:

1. Refresh docs/treeOfImportantfiles.txt if files, structure, or key documentation entries changed.
2. Audit and update documentation for accuracy:
- docs/ folder files impacted by the change
- README.md if feature behavior, workflow, commands, test counts, or architecture notes changed
3. Verify Android quality gates:
- .\\gradlew.bat :app:testDebugUnitTest
- .\\gradlew.bat :app:assembleDebug
4. Report verification outcomes in the final summary.

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

## Completion Rule

Do not finalize until code changes, tree maintenance, documentation updates, and verification commands are all completed (or explicitly blocked with a reason).

## Dead Code / Report Truthfulness Guard

1. **Never create code that is not wired into the live app.** Every new file, function, or class must be called from at least one production code path. Run a grep/usage check before finalizing.
2. **Never remove code that is referenced in the submitted report** (`report-audit/Project_Report_ISA_22235876.md`) without re-implementing equivalent functionality first.
3. **Never write report content about code that does not actually work.** If a feature is described in the report, the corresponding code must exist, compile, be reachable, and be tested.
4. When deleting or refactoring, check whether the affected symbol appears in the submitted report. If it does, either preserve the functionality or flag it to the user before proceeding.
