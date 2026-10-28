# 2. Use Hilt for dependency injection

- **Status:** Accepted
- **Date:** 2025-09-01

## Context

Repositories, Firebase clients (Auth, Firestore, Functions, App Check,
Crashlytics), Azure Speech and OpenAI clients, the central `Logger`, and
`SessionDataCleaner` are referenced from view-models, navigation hosts, and
the `Application` class. Without DI, each call site would either construct
its own client (breaking singleton semantics for things like `FirebaseAuth`)
or pull from a hand-rolled service locator that has no test seam.

The team also wants ViewModels that are constructor-injectable so that unit
tests (~196 suites / 2,486 tests) can pass fakes without a robolectric runner.

## Decision

Use Hilt (Dagger 2) with `@HiltAndroidApp` on `FYPApplication`,
`@HiltViewModel` on every view-model, and `@AndroidEntryPoint` on
`MainActivity`. KSP (`com.google.devtools.ksp`) generates the Hilt code at
build time. Modules live in `core/di/` and are split per responsibility
(network, firebase, repositories, dispatchers).

Instrumented tests use a custom `HiltTestRunner` so AndroidX Test pulls Hilt
test components in.

## Consequences

### Positive

- Constructor-injected view-models are trivially unit-testable.
- Singleton scope on Firebase clients is enforced by the DI graph, removing a
  whole class of "two `FirebaseAuth` instances" bugs.
- KSP keeps incremental builds fast vs. KAPT.

### Negative / accepted trade-offs

- Adds ~5–10 s of cold-build time vs. manual DI.
- Hilt errors at build time are noisy when a binding is missing; new
  contributors should read the generated stack trace, not the Kotlin one.
- Module split (suggestion §8.44) is harder while everything lives in `:app`
  because Hilt aggregating roots discover bindings module-wide.

### Follow-ups

- Item 44: when splitting into `:core` / `:data` / `:domain` / `:feature-*`,
  keep one Hilt aggregating root in `:app` and use `@EntryPoint` for cross-
  module access.
