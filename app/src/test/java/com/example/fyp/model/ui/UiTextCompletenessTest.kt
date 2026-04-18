package com.example.fyp.model.ui

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for UiText translation system completeness and consistency.
 *
 * Verifies that:
 *  1. Every UiTextKey has a non-empty English default in BaseUiTexts
 *  2. BaseUiTexts size matches UiTextKey entries count
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
}
