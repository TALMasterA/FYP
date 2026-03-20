# MCP + Skills Playbook for FYP

Last updated: 2026-03-20

This playbook lists practical skills/MCP tooling that work well for this Android + Firebase app and how to use them during audits.

## Recommended Skills

1. firebase-firestore-basics
- Use for Firestore rules review and index checks.
- Best for: access-control audits, data-model guardrails.

2. firebase-ai-logic
- Use for AI generation feature review (prompt flow, safety, model setup).
- Best for: learning-content generation architecture checks.

3. fyp-frontend-state-audit (local, machine-level)
- Focuses on non-atomic StateFlow updates and race-safe UI state patterns.
- Best for: ViewModel quality checks.

4. fyp-backend-database-alignment (local, machine-level)
- Focuses on frontend callable contracts vs backend implementations and rules.
- Best for: backend/database/frontend alignment reviews.

## Recommended MCP/Tooling Workflow

1. Codebase exploration
- Use Explore subagent for quick alignment scans across frontend, backend, and rules.

2. Frontend state-safety review
- Search patterns like `_uiState.value = _uiState.value.copy(...)`.
- Convert read-modify-write paths to atomic `update { ... }`.

3. Backend and rules alignment
- Compare callable names in Android clients with backend function exports.
- Verify identity-sensitive Firestore rule fields (`fromUserId`, `toUserId`, counters).

4. Rules validation
- Run Firestore rules validator before commit.

5. Verification gate
- Android: `./gradlew testDebugUnitTest` and `./gradlew assembleDebug`
- Backend: `npm run test:coverage -- --runInBand` and `npm run build`

## Notes

- `.agents/` is gitignored in this repository, so local custom skills should be treated as machine-level tooling.
- Keep production behavior unchanged; prefer safety hardening and contract validation.
