# 1. Use Jetpack Compose for the Android UI

- **Status:** Accepted
- **Date:** 2025-09-01 (project inception)

## Context

The app is a single-Activity Android application targeting API 26+ (compileSdk 36,
targetSdk 36). It needs ~30 screens spanning translation, learning, friends/chat,
shop, and settings, with 17 user-selectable in-app languages. Building each
screen as XML layouts plus per-language `res/values-*/strings.xml` would have
multiplied the maintenance surface and made dynamic theming (11 colour palettes,
80–150 % font scaling) painful.

The team is one developer; recompose-aware DSLs reduce the amount of glue code
between view-model state and the rendered UI.

## Decision

All UI is written in Jetpack Compose (`compose-bom`, Material 3). Navigation
uses `androidx.navigation:navigation-compose` driven by a sealed `AppScreen`
class and feature sub-graphs (`MainFeatureGraph`, `LearningWordBankGraph`,
`FriendsChatGraph`, `SettingsProfileGraph`, `StartupAuthGraph`). Each screen
is a stateless `@Composable` taking a `ViewModel` provided by Hilt.

There is no `androidx.fragment` usage, and no XML layout outside `res/xml/`
configuration files (backup rules, network security config, navigation graph
metadata).

## Consequences

### Positive

- One Kotlin source for state + UI; recomposition handles diffing.
- Themes and localised text are plain Kotlin maps, which is what makes ADR-5
  (hardcoded UI translations) tractable.
- Compose Previews give per-screen visual regression detection without an
  emulator.

### Negative / accepted trade-offs

- Compose stability is sensitive to data-class shape; suggestion §8.46 calls
  out a future `@Stable` / `@Immutable` audit.
- Compose UI tests are heavier than view-based espresso tests; current coverage
  is unit-tests-first, with one Compose smoke test (`LoginScreenSmokeTest`).

### Follow-ups

- Item 28 (`docs/APP_SUGGESTIONS.md`): TalkBack content-description sweep.
- Item 29: 150 % font-scale screenshot test.
