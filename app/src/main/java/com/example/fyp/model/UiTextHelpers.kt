package com.example.fyp.model

/**
 * Combined BaseUiTexts that merges Core and Screen-specific strings.
 * CRITICAL: Order must exactly match UiTextKey enum order!
 */
val BaseUiTexts: List<String>
    get() = CoreUiTexts + ScreenUiTexts

/**
 * Builds a UI text map from translated strings.
 * Handles translation fallback and ensures template tokens are preserved.
 *
 * @param translatedJoined Translated strings joined by '\u0001' separator
 * @return Map of UiTextKey to translated (or fallback) strings
 */
fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')
    val map = UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts.getOrNull(index).orEmpty()
        key to value
    }.toMap().toMutableMap()

    // Ensure tokens exist (translation sometimes breaks/removes them)
    fun ensureContains(key: UiTextKey, vararg tokens: String) {
        val v = map[key].orEmpty()
        if (tokens.any { !v.contains(it) }) {
            map[key] = BaseUiTexts.getOrNull(key.ordinal).orEmpty()
        }
    }

    // Template strings that must contain specific tokens
    ensureContains(UiTextKey.HistorySessionTitleTemplate, "{id}")
    ensureContains(UiTextKey.HistoryItemsCountTemplate, "{count}")
    ensureContains(UiTextKey.PaginationPageLabelTemplate, "{page}", "{total}")

    ensureContains(UiTextKey.LearningOpenSheetTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetTitleTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetPrimaryTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetHistoryCountTemplate, "{nowCount}", "{savedCount}")
    ensureContains(UiTextKey.SettingsScaleTemplate, "{pct}")
    ensureContains(UiTextKey.DialogGenerateOverwriteMessageTemplate, "{speclanguage}")

    return map
}

/**
 * Generates a hash of all base UI texts for version comparison.
 * Useful for detecting when translations are out of sync.
 */
fun baseUiTextsHash(): Int {
    return BaseUiTexts.joinToString(separator = "\u0001").hashCode()
}

/**
 * Helper: Get a single UI text by key with fallback
 */
fun getUiText(key: UiTextKey): String {
    return BaseUiTexts.getOrNull(key.ordinal).orEmpty()
}

/**
 * Helper: Replace template variables in UI text
 * Example: "Scale: {pct}%" -> "Scale: 125%"
 */
fun String.replaceTemplateTokens(vararg replacements: Pair<String, String>): String {
    var result = this
    replacements.forEach { (token, value) ->
        result = result.replace("{$token}", value)
    }
    return result
}