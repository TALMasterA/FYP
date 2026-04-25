---
name: docs-accuracy-audit
description: 'Audit existing documentation accuracy against the live codebase before closing any task. Checks test counts, UI string values, function names, architecture notes, and tree file entries against actual code state. Use before finalizing any repository-changing task, or when asked to verify docs are aligned with the codebase.'
argument-hint: 'Scope of change (e.g., all docs, test counts only, UI strings + locales, backend API, architecture notes)'
user-invocable: true
---

# Docs Accuracy Audit

## Purpose

Prevent documentation from silently drifting away from the live codebase. Every number, class name, function name, constant value, and structural claim in any documentation file must match the actual live code state before a task is closed.

This skill is a **mandatory pre-close accuracy gate** for any repository-changing task. It is *not* about updating docs to reflect new changes — it is about catching claims that were already stale before or after the current change.

## When To Use

- **Always**, as the final sweep before calling `task_complete` on any repository-changing task.
- When you suspect a documented value (test count, API name, constant, class name) may be stale.
- When asked to "verify docs", "align documentation with the codebase", or "check docs accuracy".
- After running quality gates, as the last step of the `comprehensive-quality-workflow`.

## When Not To Use

- For purely read-only analysis tasks with zero repository changes.
- For tasks explicitly scoped to a single-character typo fix with no systemic drift risk.

---

## Audit Workflow

Run every section that is **in scope** for the current task. Mark each check `PASS`, `FAIL`, or `N/A`. All `FAIL` items must be resolved before `task_complete` is called.

---

### Check 1 — Android Test Count Alignment

**Files to verify:** `docs/TEST_COVERAGE.md`, `README.md`

**Step 1 — Extract the actual count from test XML:**

```powershell
$files = Get-ChildItem app\build\test-results\testDebugUnitTest\TEST-*.xml
$tests = 0; $suites = $files.Count
foreach ($f in $files) { [xml]$x = Get-Content $f.FullName; $tests += [int]$x.testsuite.tests }
"Suites: $suites  Tests: $tests"
```

**Step 2 — Check the documented number:**

```powershell
Select-String -Path docs\TEST_COVERAGE.md, README.md -Pattern "\d,\d{3}"
```

**Rule:** Both files must state exactly the count from the XML. If they differ, update both files immediately. Never leave a stale count in either file.

---

### Check 2 — Backend Test Count Alignment (if backend was touched)

**File to verify:** `docs/TEST_COVERAGE.md`

```powershell
cd fyp-backend\functions
npx jest --passWithNoTests 2>$null | Select-String "Tests:"
```

**Rule:** The backend row in `TEST_COVERAGE.md` must match the jest output. Update if they differ.

---

### Check 3 — Security Constant vs UI String Alignment

**Source of truth:** `SecurityUtils.kt`

```powershell
Select-String -Path "app\src\main\java\com\example\fyp\core\security\SecurityUtils.kt" -Pattern "minLength"
```

**Check all locale strings mention the same number:**

```powershell
Select-String -Path "app\src\main\java\com\example\fyp\model\ui\strings\UiTextScreens.kt", `
  "app\src\main\java\com\example\fyp\model\ui\strings\translations\*.kt" `
  -Pattern "password|Password|パスワード|비밀번호|密碼|密码|contraseña|mot de passe|Passwort|senha|пароль|رمز" |
  Select-String -Pattern "\d"
```

**Rule:** Every locale's `AuthRegisterRules` bullet and `AuthErrorPasswordTooShort` value must contain the same digit as `SecurityUtils.validatePassword(minLength = N)`. A mismatch means the UI lies to the user about the actual minimum. Update all 17 files (English + 16 locales) and the corresponding test assertion in `TranslationConsistencyTest.kt`.

---

### Check 4 — Cloud Functions API Doc Alignment

**File to verify:** `docs/CLOUD_FUNCTIONS_API.md`

**Step 1 — List actual exported callables:**

```powershell
Select-String -Path "fyp-backend\functions\src\index.ts" -Pattern "exports\."
```

**Step 2 — List documented function names:**

```powershell
Select-String -Path "docs\CLOUD_FUNCTIONS_API.md" -Pattern "^#{1,3} " | Select-Object -First 40
```

**Rule:** Every `onCall`/`onRequest` export in `index.ts` must appear in the doc. Every function name in the doc must match an actual export. Renamed or removed callables are a contract break — flag them explicitly in the summary.

---

### Check 5 — Architecture Notes Symbol Verification

**File to verify:** `docs/ARCHITECTURE_NOTES.md`

**Step 1 — Extract class/function names referenced in the doc:**

Use `grep_search` to identify 5–10 symbol names mentioned in `ARCHITECTURE_NOTES.md` (look for backtick-quoted names or PascalCase identifiers).

**Step 2 — Verify each still exists:**

```powershell
Select-String -Path "app\src\main\java\**\*.kt" -Pattern "class SymbolName|fun SymbolName|object SymbolName" -Recurse
```

**Rule:** If a named symbol no longer exists with the same name and the doc still references it as current, the doc is inaccurate. Update the doc or flag it. Do not silently leave a dead reference.

---

### Check 6 — Tree File Alignment

**File to verify:** `docs/treeOfImportantfiles.txt`

**Step 1 — Check for files listed in the tree that no longer exist:**

```powershell
Get-Content docs\treeOfImportantfiles.txt |
  Where-Object { $_ -match "\." -and $_ -notmatch "#" } |
  ForEach-Object {
    $p = $_.Trim() -replace "^[│├└─\s]+"
    if ($p -ne "" -and !(Test-Path $p)) { "MISSING: $p" }
  }
```

**Step 2 — Gitignore guard:**

```powershell
Select-String -Path "docs\treeOfImportantfiles.txt" -Pattern "google-services\.json|local\.properties|firebase-sa\.json"
```

The above must return **zero matches**.

**Rule:** No file listed in the tree may be absent from the workspace. The three gitignored secrets (`google-services.json`, `local.properties`, `firebase-sa.json`) must never appear in the tree. If any structural drift is found, update the tree file in the same task.

---

### Check 7 — UiText Locale Completeness

**Source of truth:** The `UiTextKey` enum in `UiTextCore.kt`

**Verify the locale-completeness regression tests are green** (the `UiTextCompletenessTest` suite covers all 16 locales × all enum keys):

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.example.fyp.model.ui.UiTextCompletenessTest" --console=plain 2>$null | Select-Object -Last 5
```

**Rule:** If the test fails, a locale map has either missing keys or keys outside the current enum. Fix the locale map file before closing. Never let a partial locale ship.

---

### Check 8 — Numeric Comment vs Constant Alignment

**Scan for comments that embed a literal number referencing a limit, threshold, or default:**

```powershell
Select-String -Path "app\src\main\java\**\*.kt" -Pattern "//.*\d+.*(char|length|min|max|limit|size|timeout|retry)" -Recurse -CaseSensitive:$false
```

**Rule:** For each hit, read the actual value of the constant or parameter it describes. If they differ, the comment is wrong — update it. This is especially important for `SecurityUtils.kt`, `OperationBatcher`, and cache TTL constants.

---

### Check 9 — README Command and Feature Accuracy

For tasks that change a command, workflow step, or user-visible feature:

```powershell
Select-String -Path README.md -Pattern "gradlew|firebase|npm run|kotlin|version" | Select-Object -First 30
```

**Rule:** Any command listed in `README.md` must be executable as written. Any version number must match `gradle/libs.versions.toml` or `package.json`. Any feature description must have corresponding live code.

---

## Output Checklist

Report the result of every applicable check in the final task summary:

| Check | Result |
|-------|--------|
| 1. Android test count matches TEST_COVERAGE.md + README.md | PASS / FAIL / N/A |
| 2. Backend test count matches TEST_COVERAGE.md | PASS / FAIL / N/A |
| 3. Security constant matches all locale UI strings | PASS / FAIL / N/A |
| 4. CLOUD_FUNCTIONS_API.md matches actual index.ts exports | PASS / FAIL / N/A |
| 5. ARCHITECTURE_NOTES.md symbols still exist in codebase | PASS / FAIL / N/A |
| 6. treeOfImportantfiles.txt — no missing or gitignored files | PASS / FAIL / N/A |
| 7. UiTextCompletenessTest GREEN (all locales × all keys) | PASS / FAIL / N/A |
| 8. Numeric comments match actual constant values | PASS / FAIL / N/A |
| 9. README commands and versions are accurate | PASS / FAIL / N/A |

**All FAIL items must be fixed before `task_complete` is called. N/A items must be justified.**

---

## Integration With Other Skills

This skill runs **after** `comprehensive-quality-workflow` Phase 3 (doc updates) and **after** `android-quality-gates-and-docs`. It is the final gate, not a replacement for those skills.

Execution order:
1. `comprehensive-quality-workflow` (fix, test, update docs for changes)
2. `android-quality-gates-and-docs` (run quality gates)
3. **`docs-accuracy-audit`** ← this skill (verify no doc claim is stale)
4. `task_complete`
