package com.example.fyp.core

import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

/**
 * Centralized utilities for UI text translation.
 * Eliminates repetitive translation function creation across screens.
 */
object TextTranslationUtils {

    /**
     * Create a translation function for a screen.
     * Usage:
     *   val t = TextTranslationUtils.createTranslator(appLanguageState)
     *   val text = t(UiTextKey.SomeKey)
     */
    fun createTranslator(
        appLanguageState: AppLanguageState,
        baseTexts: List<String> = BaseUiTexts  // ← CHANGE FROM Array<String>
    ): (UiTextKey) -> String {
        return { key ->
            val baseText = baseTexts.getOrNull(key.ordinal) ?: ""
            baseText
        }
    }

    fun getFallbackText(key: UiTextKey): String {
        return BaseUiTexts.getOrNull(key.ordinal) ?: "N/A"
    }

    /**
     * Safely get translated text with fallback.
     */
    fun getText(
        key: UiTextKey,
        translations: Map<String, String>? = null
    ): String {
        return translations?.get(key.name) ?: getFallbackText(key)
    }
}