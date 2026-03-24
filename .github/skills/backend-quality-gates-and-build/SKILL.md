---
name: backend-quality-gates-and-build
description: 'Standardize Cloud Functions verification by running npm test:coverage and npm run build with actionable reporting for backend changes.'
argument-hint: 'Backend scope to verify (for example: learning callable, notification trigger, rules-alignment change)'
user-invocable: true
---

# Backend Quality Gates And Build

## When To Use
- Any change under fyp-backend/functions source, tests, or build config.
- Callable API or trigger refactors that need coverage and compile verification.
- Security/rules-adjacent updates that can impact backend behavior.

## Standard Verification Flow
1. Move to backend functions folder:
```bash
cd fyp-backend/functions
```
2. Run coverage gate (CI-aligned):
```bash
npm run test:coverage -- --runInBand
```
3. Run TypeScript build gate:
```bash
npm run build
```
4. Report key outcomes:
- Total tests and failures
- Coverage threshold status
- Build success/failure
- Any risky untested branch areas

## MCP-First Triage
- Use search tools to locate branch-heavy logic and recent edits.
- Add targeted regression tests before broad refactors.
- Prefer deterministic mocks for Firestore/admin/function wrappers.

## Completion Checklist
- Coverage gate passed or explicitly documented as blocked.
- Build gate passed or explicitly documented as blocked.
- Docs updated when API/behavior/test metrics changed.
- Tree file updated if skill/test/doc files were added or renamed.
