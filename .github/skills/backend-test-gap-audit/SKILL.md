---
name: backend-test-gap-audit
description: 'Audit backend test gaps with coverage-first analysis and add targeted Jest tests. Use for requests like test gaps, improve coverage, add regression tests, guard branches, and review missing backend tests.'
argument-hint: 'Area to audit (for example: notifications, learning trigger, maintenance jobs)'
user-invocable: true
---

# Backend Test Gap Audit

## When To Use
- User asks to find test gaps in backend Cloud Functions.
- Coverage is below expected confidence for branch-heavy logic.
- Recent refactors need regression guard tests.

## MCP-First Workflow
1. Read current coverage snapshot from `fyp-backend/functions/coverage/coverage-summary.json`.
2. Prioritize files with low branch coverage and production risk.
3. Read source and existing tests side-by-side before writing tests.
4. Add tests that assert behavior, not implementation details.
5. Run verification command:

```bash
cd fyp-backend/functions
npm run test:coverage -- --runInBand
```

6. Report what was covered, what remains risky, and next candidate gaps.

## Test Design Rules
- Prefer one high-value branch-path test over many shallow tests.
- Cover negative/error paths for trigger handlers.
- For entrypoints, verify initialization side effects and re-export wiring.
- Use deterministic mocks for Firestore query chains and function wrappers.
- Keep tests local to `src/__tests__/` and align with existing Jest setup.

## Common Targets In This Repo
- `learning.ts` trigger paths around quiz-version sync.
- `notifications.ts` branches that gate sends and anti-spam conditions.
- `maintenance.ts` scheduled pagination and empty-snapshot behavior.
- `index.ts` bootstrap wiring and exported function surface.
