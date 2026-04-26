package com.translator.TalknLearn.model.ui

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for UiText translation system completeness and consistency.
 *
 * Verifies that:
 *  1. Every UiTextKey has a non-empty English default in BaseUiTexts
 *  2. BaseUiTexts size matches UiTextKey entries count
 *  3. Every locale translation map covers every UiTextKey with a non-blank value
 *     (catches missing or stale keys after enum renames in any of the 16 locales)
 */
class UiTextCompletenessTest {

    // ── 1. English defaults ──

    @Test
    fun `every UiTextKey has a non-empty English default in BaseUiTexts`() {
        for (key in UiTextKey.entries) {
            val value = BaseUiTexts[key.ordinal]
            assertFalse(
                "BaseUiTexts[${key.name}] (ordinal ${key.ordinal}) must not be blank",
                value.isBlank()
            )
        }
    }

    // ── 2. Size alignment ──

    @Test
    fun `BaseUiTexts size matches UiTextKey entries count`() {
        assertEquals(
            "BaseUiTexts size must equal UiTextKey.entries.size",
            UiTextKey.entries.size,
            BaseUiTexts.size
        )
    }

    // ── 3. Per-locale coverage ──

    private val localeMaps: Map<String, Map<UiTextKey, String>> = mapOf(
        "Cantonese" to CantoneseUiTexts,
        "DeDe" to DeDeUiTexts,
        "EsEs" to EsEsUiTexts,
        "FilPh" to FilPhUiTexts,
        "FrFr" to FrFrUiTexts,
        "IdId" to IdIdUiTexts,
        "ItIt" to ItItUiTexts,
        "JaJp" to JaJpUiTexts,
        "KoKr" to KoKrUiTexts,
        "MsMy" to MsMyUiTexts,
        "PtBr" to PtBrUiTexts,
        "RuRu" to RuRuUiTexts,
        "ThTh" to ThThUiTexts,
        "ViVn" to ViVnUiTexts,
        "ZhCn" to ZhCnUiTexts,
        "ZhTw" to ZhTwUiTexts,
    )

    @Test
    fun `every locale map contains every UiTextKey with a non-blank value`() {
        val failures = mutableListOf<String>()
        for ((localeName, map) in localeMaps) {
            for (key in UiTextKey.entries) {
                val value = map[key]
                when {
                    value == null -> failures += "$localeName missing key ${key.name}"
                    value.isBlank() -> failures += "$localeName has blank value for ${key.name}"
                }
            }
        }
        assertTrue(
            "Locale completeness gaps:\n" + failures.joinToString("\n"),
            failures.isEmpty()
        )
    }

    @Test
    fun `no locale map contains keys outside the current UiTextKey enum`() {
        // Map keys are typed UiTextKey, so this is a static guarantee at compile time;
        // this test guards against accidental String-keyed maps being introduced later.
        for ((localeName, map) in localeMaps) {
            for (key in map.keys) {
                assertTrue(
                    "$localeName contains stale key ${key.name} not in UiTextKey.entries",
                    key in UiTextKey.entries
                )
            }
        }
    }
}
