---
name: dead-code-prevention
description: 'Prevent dead code: verify every new file, function, dependency, and constant is actually referenced in production before finalizing. Use when adding files, functions, dependencies, or constants.'
argument-hint: 'Describe the new code you added or plan to add'
user-invocable: true
---

# Dead Code Prevention

## Purpose

Ensures every new file, function, class, dependency, or constant added to the codebase is **actually wired in and referenced by production code** before the task is marked complete. Prevents "scaffold-only" additions that inflate the report without real usage.

## When To Use

**Mandatory** when any task:
- Adds a new `.kt` source file
- Adds a new function, class, or object
- Adds a new Gradle dependency
- Adds new constants or enum values
- Adds a new `@Module`, `@Provides`, or `@Inject` binding

## Verification Checklist

### 1. New Files — Must Be Imported

For every new `.kt` file added under `app/src/main/`:

```powershell
# Verify at least one other production file imports or references it
grep -r "import com.example.fyp....NewClass" --include="*.kt" app/src/main/ | grep -v "the-new-file-itself"
```

**Fail condition:** Zero import hits outside the file itself means it is dead code.

### 2. New Functions / Methods — Must Be Called

For every new public or internal function:

```powershell
# Verify it is called somewhere in production code
grep -rn "functionName(" --include="*.kt" app/src/main/ | grep -v "fun functionName"
```

**Fail condition:** The only match is the definition line → dead code.

### 3. New Dependencies — Must Be Used

For every new `implementation(...)` line in `build.gradle.kts`:

```powershell
# Verify at least one source file imports from the dependency's package
grep -r "import <dependency-package>" --include="*.kt" app/src/main/
```

**Fail condition:** No import from that dependency's package → orphaned dependency.

### 4. New Constants / Enum Values — Must Be Referenced

```powershell
# Verify the constant is used outside its declaration file
grep -rn "CONSTANT_NAME" --include="*.kt" app/src/main/ | grep -v "val CONSTANT_NAME"
```

**Fail condition:** Only the declaration line matches → dead constant.

### 5. New DI Bindings — Must Be Injected

For every new `@Provides` or `@Binds` method:

```powershell
# Verify the provided type appears in an @Inject constructor or parameter
grep -rn "TypeName" --include="*.kt" app/src/main/ | grep -v "Module.kt"
```

**Fail condition:** No injection site outside the module → unused binding.

## Integration With Other Skills

This skill supplements the **comprehensive-quality-workflow** and **android-quality-gates-and-docs** skills. Run this check **after** code changes and **before** running quality gates.

## Remediation

If a verification check fails:
1. **Wire it in** — Add the missing call site, import, or injection.
2. **Or remove it** — If the code is not needed, delete it entirely instead of leaving dead code.
3. **Never leave scaffold-only code** — Every addition must have a production call path.

## Quick Summary

| What Was Added | Verification | Tool |
|----------------|-------------|------|
| New `.kt` file | At least 1 import outside itself | `grep -r "import ...ClassName"` |
| New function | At least 1 call site outside definition | `grep -rn "funcName("` |
| New dependency | At least 1 import from its package | `grep -r "import <pkg>"` |
| New constant | At least 1 reference outside declaration | `grep -rn "CONST_NAME"` |
| New DI binding | At least 1 `@Inject` usage of provided type | `grep -rn "TypeName"` |
