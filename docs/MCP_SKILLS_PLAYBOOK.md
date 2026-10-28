# MCP + Skills Playbook for FYP

Last updated: 2026-04-25

This playbook lists practical skills/MCP tooling that work well for this Android + Firebase app and how to use them during audits and coding tasks.

## Recommended Skills

1. backend-quality-gates-and-build (repo skill)
- Use to standardize Cloud Functions verification (`npm run test:coverage -- --runInBand`, `npm run build`).
- Best for: any backend code change that must be CI-aligned before merge.

2. backend-test-gap-audit (repo skill)
- Use for backend Jest gap analysis and coverage-first test additions.
- Best for: branch-heavy callable/triggers and regression hardening.

3. android-quality-gates-and-docs (repo skill)
- Use for Android unit-test fixes, regression additions, and docs/tree maintenance.
- Best for: repository-changing Android tasks that must pass mandatory gates.

4. create-reusable-skill (repo skill)
- Use to turn a repeated workflow or user prompt into a reusable shared skill when the process is likely to recur.
- Best for: skill authoring, workflow standardization, and proactive skill curation.

5. **docs-accuracy-audit (repo skill)** ← mandatory pre-close gate
- Run as the final step before `task_complete` on every repository-changing task.
- Verifies: test counts in TEST_COVERAGE.md + README.md, security constant vs locale strings, Cloud Functions API names, ARCHITECTURE_NOTES symbols, tree file completeness, UiText locale coverage, numeric comment accuracy, and README command correctness.
- Best for: preventing documentation from silently drifting from the live codebase.

6. fyp-frontend-state-audit (local, machine-level)
- Focuses on non-atomic StateFlow updates and race-safe UI state patterns.
- Best for: ViewModel quality checks.

7. fyp-backend-database-alignment (local, machine-level)
- Focuses on frontend callable contracts vs backend implementations and rules.
- Best for: backend/database/frontend alignment reviews.

8. firebase-firestore-basics
- Use for Firestore rules review and index checks.
- Best for: access-control audits and data-model guardrails.

8. firebase-ai-logic
- Use for AI generation feature review (prompt flow, safety, model setup).
- Best for: learning-content generation architecture checks.

## Recommended MCP/Tooling Workflow

1. Codebase exploration
- Use Explore subagent for quick alignment scans across frontend, backend, and rules.
- Use semantic/code search before broad file edits.

2. Failure-first test triage
- Reproduce failures with `.\\gradlew.bat :app:testDebugUnitTest`.
- Fix compile/test breaks before refactors.

3. Frontend state-safety review
- Search patterns like `_uiState.value = _uiState.value.copy(...)`.
- Convert read-modify-write paths to atomic `update { ... }` where race risk exists.

4. Backend and rules alignment
- Compare callable names in Android clients with backend function exports.
- Verify identity-sensitive Firestore rule fields (`fromUserId`, `toUserId`, counters).

5. Rules validation and coverage
- Validate Firestore rules before commit.
- Run backend quality gate: `npm run test:coverage -- --runInBand` and `npm run build`.

6. Android verification gate
- `.\\gradlew.bat :app:testDebugUnitTest`
- `.\\gradlew.bat :app:assembleDebug`

7. Documentation gate
- Update `docs/TEST_COVERAGE.md` if test totals or suite notes change.
- Update `README.md` if workflows, commands, or user-visible behavior changes.
- Update `docs/treeOfImportantfiles.txt` when files are added/removed/renamed.

8. Skill curation
- If a task reveals a repeatable, high-value workflow that is not covered by an existing skill, prefer creating or refining a repo skill instead of re-solving it ad hoc.
- Prefer `.github/skills/` for shared FYP workflows and `.agents/skills/` only for machine-local personal tooling.

## Notes

- `.agents/` is gitignored in this repository, so local custom skills should be treated as machine-level tooling.
- Keep production behavior unchanged; prefer safety hardening, contract validation, and targeted regression tests.
