---
name: android-quality-gates-and-docs
description: 'Run Android quality gates, fix failing unit tests, and keep docs/README/tree files consistent in repository-changing tasks.'
argument-hint: 'Scope of change (for example: settings viewmodel tests, navigation refactor, backend contract change)'
user-invocable: true
---

# Android Quality Gates And Docs

## When To Use
- User asks to fix Android unit test failures.
- A task changes Android behavior and must be verified before completion.
- Documentation and repository tree metadata need to stay aligned with code changes.

## MCP-First Workflow
1. Reproduce the failure:
```bash
.\\gradlew.bat :app:testDebugUnitTest
```
2. Fix the smallest failing scope first (compile/test issue before refactors).
3. Add at least one focused regression test for the fixed behavior branch.
4. Re-run unit tests:
```bash
.\\gradlew.bat :app:testDebugUnitTest
```
5. Run assemble gate:
```bash
.\\gradlew.bat :app:assembleDebug
```
6. Update docs in the same task:
- `docs/TEST_COVERAGE.md` if test counts or suites changed
- `README.md` if user-visible workflow or metrics changed
- `docs/treeOfImportantfiles.txt` when files are added/removed/renamed

## Tooling Recommendations
- Use `grep_search` to find stale fields/tests quickly.
- Use `read_file` to inspect both failing tests and production model signatures.
- Use `apply_patch` for minimal, reviewable diffs.
- Use `run_in_terminal` for Gradle verification commands.

## Output Checklist
- Confirm failing test root cause.
- Confirm added regression test(s).
- Report both Android gate outcomes (`testDebugUnitTest`, `assembleDebug`).
- Explicitly state whether tree file update was required.
