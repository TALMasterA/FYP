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
