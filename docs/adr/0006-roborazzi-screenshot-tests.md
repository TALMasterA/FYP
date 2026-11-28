# 6. Roborazzi JVM screenshot tests and private previews

- **Status:** Accepted
- **Date:** 2026-11-28

## Context

The post-FYP roadmap ([ROADMAP.md](../ROADMAP.md)) includes a full UI/UX
redesign. Before this ADR, the UI had no visual safety net:

- There were zero `@Preview` functions.
- There was one Compose smoke test in `androidTest`, which needs an emulator. Neither CI nor the agent containers can run an emulator.
- Screen changes could only be checked by hand on a device.

During a redesign, agents (and the next person on the repo) need to *see* each
screen and catch visual regressions without a device.

## Decision

We use **Roborazzi** with **Robolectric native graphics** for screenshot
tests. These tests run on the JVM as part of `testDebugUnitTest`.

**Where things live**
- The tests are under `app/src/test/java/com/translator/TalknLearn/screenshots/`.
- The reference PNGs are committed under `app/src/test/screenshots/`.
- The Robolectric defaults are in `app/src/test/resources/robolectric.properties`:
  - **SDK 35**, because CI uses JDK 17 and the SDK 36 jars need JDK 21;
  - a plain `Application`, so the Hilt and Firebase app class does not start.

**How to run them**
- `./gradlew :app:recordRoborazziDebug` re-records the images after an intended UI change.
- `./gradlew :app:verifyRoborazziDebug` fails when a screen no longer matches its image.
- A plain `testDebugUnitTest` still runs the screenshot tests, but it does not compare or save images.

**Which screens are covered**
- Screens are rendered as stateless composables wrapped in `FYPTheme(dynamicColor = false)`.
- Screens that create their own `hiltViewModel()` are covered once the redesign splits them into a stateless content composable.

**Previews**
- `private @Preview` functions are allowed. They are the only exception to the "no unwired code" rule in AGENTS.md.

## Consequences

### Positive

- Every UI PR can include real rendered images, created in CI or in an agent container.
- A visual regression fails the check instead of reaching a device.
- There is no emulator, Hilt test runner or Firebase setup to maintain.

### Negative / accepted trade-offs

- The libraries are test-only (`testImplementation`) and add nothing to the APK. They do add build time: Robolectric downloads an Android runtime jar on its first run.
- Robolectric's native graphics jar (`nativeruntime-dist-compat`) is about 160 MB, and Maven Central answers it with HTTP 429 (too many requests), on CI too. `settings.gradle.kts` therefore fetches only the `org.robolectric` group from Google's mirror of Maven Central (`maven-central.storage-download.googleapis.com`). Every other dependency still comes from Maven Central and Google. Do not remove this repository unless CI stays green without it.
- Robolectric rendering is close to a device but not identical. It does not replace a final check on a real device.
- The reference PNGs make the repository bigger. We keep the set small: key screens only, in light and dark.

### Follow-ups

- CI runs the screenshot tests as part of `testDebugUnitTest` but does not compare images yet. Once a few PRs show the images are stable on the CI runner, change the CI test step to `verifyRoborazziDebug` and upload the diff images when it fails.
- In Phase 2, add screenshots at 200% font scale once the design system lands.
- Consider generating screenshots from `@Preview`s automatically (the Roborazzi preview scanner) when there are many previews.
