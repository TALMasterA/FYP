# MCP + Skills Playbook for FYP

Last updated: 2026-03-24

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

4. fyp-frontend-state-audit (local, machine-level)
- Focuses on non-atomic StateFlow updates and race-safe UI state patterns.
- Best for: ViewModel quality checks.

5. fyp-backend-database-alignment (local, machine-level)
- Focuses on frontend callable contracts vs backend implementations and rules.
- Best for: backend/database/frontend alignment reviews.

6. firebase-firestore-basics
- Use for Firestore rules review and index checks.
- Best for: access-control audits and data-model guardrails.

7. firebase-ai-logic
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

## Notes

- `.agents/` is gitignored in this repository, so local custom skills should be treated as machine-level tooling.
- Keep production behavior unchanged; prefer safety hardening, contract validation, and targeted regression tests.
