# 5. Hardcoded UI translations (no `res/values-*/strings.xml`)

- **Status:** Accepted
- **Date:** 2025-10-25

## Context

The app supports 17 in-app languages selectable at runtime from Settings,
independent of the device locale. Android's standard
`res/values-<locale>/strings.xml` mechanism is locale-driven — switching
language inside the app would require either recreating the Activity with a
wrapped `Configuration`, or shipping every translation in code anyway and
ignoring the resources system.

Either path means the canonical translations live somewhere the build system
cannot help us with completeness. We chose to make that "somewhere" first-class
instead of bolting it onto the resources system.

## Decision

All user-facing strings are referenced through a `UiTextKey` enum, and every
key has an entry in:

- `EnglishUiTexts` (the canonical base set)
- 16 locale-specific maps under
  `app/src/main/java/com/translator/TalknLearn/model/ui/strings/translations/`

A unit test, `UiTextCompletenessTest`, asserts that every locale map contains
every `UiTextKey`. The CI gate `:app:testDebugUnitTest` therefore fails any
PR that adds an English-only string. The `.github/skills/ui-text-localization`
skill encodes the same workflow for Copilot agents, and the same checklist
appears in `.github/PULL_REQUEST_TEMPLATE.md` for human contributors.

There are no `res/values-*/strings.xml` files. `res/values/strings.xml` only
contains `app_name` and other resources that the platform requires to be
declared as Android resources (e.g. notification channel labels referenced
from manifest).

## Consequences

### Positive

- Runtime language switching is a single state mutation, no Activity recreate.
- Locale completeness is enforced by a unit test, not by review discipline.
- Translations live next to the code that uses them, simplifying refactors.

### Negative / accepted trade-offs

- Android Studio's translation editor and Google Play's translation services
  do not see these strings; translators must work directly in Kotlin source.
- Platform-driven UI (system back-button labels, accessibility announcements)
  still follows the device locale, which can diverge from the in-app
  language. We accept this — it matches user expectations on Android.

### Follow-ups

- Item 32: extending locale completeness CI (already done for `UiTextKey`;
  the suggestion is to extend the same pattern to enum/list parity per
  locale, not just the key set).
