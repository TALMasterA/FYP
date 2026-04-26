---
name: comprehensive-quality-workflow
description: 'Enforce comprehensive quality workflow: fix problems + related issues, add tests, update docs, verify build before commit.'
argument-hint: 'Task description (for example: fix offline bar overlap, add new feature, refactor component)'
user-invocable: true
---

# Comprehensive Quality Workflow

## Purpose

This skill enforces a complete quality workflow for **every repository-changing prompt**. It ensures:
1. **Problem + Related Issues** — Fix the immediate problem AND find/fix similar issues
2. **Test Coverage** — Add or update tests after code amendments
3. **Documentation** — Update docs when behavior changes
4. **Build Verification** — Verify tests and build before finalizing

## When To Use

**Mandatory** for any task that:
- Fixes bugs or issues
- Adds new features
- Refactors existing code
- Modifies UI components
- Changes business logic

## Complete Workflow

### Phase 1: Fix Primary Problem + Related Issues

1. **Understand the problem** — Read relevant source files
2. **Fix the primary issue** — Make the necessary changes
3. **Search for related issues** — Proactively find similar problems:
   - Search for similar patterns in the codebase
   - Check if other files have the same anti-pattern
   - Look for related components that might have the same issue
4. **Fix related issues** — Apply the same fix pattern to related problems

**Example searches for related issues:**
```bash
# If fixing status bar insets, find other places missing inset handling
grep -r "fillMaxSize" --include="*.kt" | grep -v "insets\|padding"

# If fixing error handling, find other places with similar patterns
grep -r "catch.*Exception" --include="*.kt" | grep -v "ErrorMessages"
```

### Phase 2: Add/Modify Tests

1. **Identify what was changed** — List all modified files and behaviors
2. **Check existing test coverage** — Read corresponding test files
3. **Add new tests if needed**:
   - Unit tests for new functions
   - Regression tests for bug fixes
   - Edge case tests for modified logic
4. **Update existing tests** — If API signatures changed

**Test file locations:**
- Kotlin unit tests: `app/src/test/java/com/translator/TalknLearn/**/*Test.kt`
- Backend tests: `fyp-backend/functions/src/__tests__/**/*.test.ts`

**Test patterns:**
```kotlin
// For bug fix — add regression test
@Test
fun `should not fail when edge case happens`() { ... }

// For new feature — add happy path + error tests
@Test
fun `new feature works correctly`() { ... }

@Test
fun `new feature handles error gracefully`() { ... }
```

### Phase 3: Update Documentation

1. **Check if docs need updating**:
   - `docs/ARCHITECTURE_NOTES.md` — for invariants, business rules, complex logic
   - `docs/TEST_COVERAGE.md` — if test counts changed
   - `docs/CLOUD_FUNCTIONS_API.md` — if backend contracts changed
   - `README.md` — if user-visible behavior changed
   - `docs/treeOfImportantfiles.txt` — if files added/removed/renamed

2. **Update relevant docs in the same task** — Never defer doc updates

### Phase 4: Verify Build and Tests

**Run verification commands in order:**

```bash
# 1. Run Android unit tests
.\gradlew.bat :app:testDebugUnitTest

# 2. Run Android build
.\gradlew.bat :app:assembleDebug

# 3. (If backend changed) Run backend tests
cd fyp-backend/functions && npm test
```

**All verifications must pass before marking task complete.**

### Phase 5: Final Checklist

Before finalizing, confirm:
- [ ] Primary problem is fixed
- [ ] Related/similar problems are identified and fixed
- [ ] Tests added or updated for all changes
- [ ] Documentation updated if behavior changed
- [ ] `testDebugUnitTest` passes
- [ ] `assembleDebug` passes
- [ ] Tree file updated (if files changed)

## Quality Gates — Required Outcomes

| Gate | Command | Must Pass |
|------|---------|-----------|
| Unit Tests | `.\gradlew.bat :app:testDebugUnitTest` | Yes |
| Build | `.\gradlew.bat :app:assembleDebug` | Yes |
| Backend Tests | `cd fyp-backend/functions && npm test` | If backend changed |

## Finding Related Issues — Search Patterns

When fixing any issue, use these search patterns to find similar problems:

### UI/Compose Issues
```kotlin
// Missing status bar handling
grep -r "Modifier.fillMaxSize()" --include="*.kt"

// Missing error handling in ViewModels
grep -r "viewModelScope.launch" --include="*.kt" | grep -v "try\|catch"

// Hardcoded strings (should use UiTextKey)
grep -r '"Error:' --include="*.kt"
```

### Backend Issues
```typescript
// Missing error mapping
grep -r "throw new functions.https.HttpsError" --include="*.ts"

// Missing validation
grep -r "context.auth" --include="*.ts" | grep -v "if (!context.auth)"
```

## Summary Format

At task completion, report:

```
## Summary

### Changes Made
- [Primary fix description]
- [Related issue 1 fixed]
- [Related issue 2 fixed]

### Tests Added/Modified
- [Test file]: [Description of test changes]

### Docs Updated
- [Doc file]: [What was updated]

### Verification Results
- testDebugUnitTest: PASS/FAIL
- assembleDebug: PASS/FAIL
- Tree file: Updated/Not needed
```
