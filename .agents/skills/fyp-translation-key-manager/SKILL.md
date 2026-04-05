# fyp-translation-key-manager Skill

## Description

This skill automates the process of adding new UI text keys to the FYP Android application. It updates the `UiTextKey` enum, adds the English text to `BaseUiTexts`, and syncs with the hardcoded Traditional Chinese (`ZhTwUiTexts.kt`) and Cantonese (`CantoneseUiTexts.kt`) language files. This skill helps streamline the localization process and reduces manual errors.

## Usage

To use this skill, execute the `add_translation_key.py` script with the new key and its English value.

```bash
python3 add_translation_key.py <KEY_NAME> "<ENGLISH_VALUE>"
```

**Example:**

```bash
python3 add_translation_key.py GREETING_MESSAGE "Hello, welcome!"
```

## Implementation Details

The skill consists of a Python script that:
1. Reads `UiTextCore.kt` to find the `UiTextKey` enum and inserts the new key.
2. Reads `UiTextScreens.kt` to find `BaseUiTexts` and inserts the English value.
3. Reads `ZhTwUiTexts.kt` and `CantoneseUiTexts.kt` to insert placeholder values for the new key.
4. Ensures proper formatting and handles existing content.

**Files Modified:**
- `app/src/main/java/com/example/fyp/core/ui/text/UiTextCore.kt`
- `app/src/main/java/com/example/fyp/core/ui/text/UiTextScreens.kt`
- `app/src/main/java/com/example/fyp/core/ui/text/ZhTwUiTexts.kt`
- `app/src/main/java/com/example/fyp/core/ui/text/CantoneseUiTexts.kt`
