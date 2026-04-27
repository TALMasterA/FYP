---
name: ui-text-localization
description: 'Use when adding, renaming, or changing UiTextKey values, hardcoded UI labels, app language strings, or locale translation maps. Requires every UI text to be added to English plus all 16 supported languages.'
argument-hint: 'UiText key, screen label, or localization change scope'
user-invocable: true
---

# UiText Localization

## When To Use
- Adding a new `UiTextKey` or changing an existing key.
- Replacing a hardcoded Compose label with localized text.
- Updating `BaseUiTexts`, `CoreUiTexts`, `ScreenUiTexts`, or any file under `model/ui/strings/translations/`.
- Changing app UI language names, authentication labels, screen labels, dialogs, errors, placeholders, or button text.

## Required Rule
Every new user-facing UI text must be added for all supported UI languages in the same task:
- English base text in `UiTextScreens.kt` or `UiTextCore.kt`/`CoreUiTexts`, matching `UiTextKey` enum order.
- All 16 locale maps under `app/src/main/java/com/translator/TalknLearn/model/ui/strings/translations/`.

Never ship a new `UiTextKey` with only English text or with missing locale entries. Do not leave a production Compose label hardcoded when it should follow the app UI language.

## Implementation Checklist
1. Add or update the `UiTextKey` in `UiTextCore.kt`.
2. Add or update the matching English string in the correct base list, preserving enum/list order.
3. Add or update the same key in every locale map:
   - `CantoneseUiTexts.kt` (`zh-HK`)
   - `ZhTwUiTexts.kt` (`zh-TW`)
   - `ZhCnUiTexts.kt` (`zh-CN`)
   - `JaJpUiTexts.kt` (`ja-JP`)
   - `KoKrUiTexts.kt` (`ko-KR`)
   - `FrFrUiTexts.kt` (`fr-FR`)
   - `DeDeUiTexts.kt` (`de-DE`)
   - `EsEsUiTexts.kt` (`es-ES`)
   - `IdIdUiTexts.kt` (`id-ID`)
   - `ViVnUiTexts.kt` (`vi-VN`)
   - `ThThUiTexts.kt` (`th-TH`)
   - `FilPhUiTexts.kt` (`fil-PH`)
   - `MsMyUiTexts.kt` (`ms-MY`)
   - `PtBrUiTexts.kt` (`pt-BR`)
   - `ItItUiTexts.kt` (`it-IT`)
   - `RuRuUiTexts.kt` (`ru-RU`)
4. Wire production UI to `t(UiTextKey.NewKey)` or the existing localized lookup pattern.
5. Add or update focused tests when the text has special meaning, exact wording, template tokens, or risk of regression.

## Verification
Run the locale completeness test before finalizing any UiText change:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.translator.TalknLearn.model.ui.UiTextCompletenessTest" --console=plain
```

Then run the repository-required Android gates:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

## Final Review
- Search for the old hardcoded label and confirm it is no longer used in production UI.
- Confirm every locale map contains the changed key with a non-blank localized value.
- If test counts changed, update `docs/TEST_COVERAGE.md` and `README.md` after reading the actual XML output.