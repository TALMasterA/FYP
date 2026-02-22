package com.example.fyp.model.ui

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for UiTextKey/BaseUiTexts alignment.
 *
 * Ensures that every entry in the UiTextKey enum has a corresponding
 * default string in BaseUiTexts. Misalignment causes ArrayIndexOutOfBoundsException
 * at runtime.
 */
class UiTextAlignmentTest {

    @Test
    fun `UiTextKey count equals BaseUiTexts count`() {
        val keyCount = UiTextKey.entries.size
        val textCount = BaseUiTexts.size
        assertEquals(
            "UiTextKey ($keyCount entries) must match BaseUiTexts ($textCount entries). " +
            "Did you add a key without adding its default text, or vice-versa?",
            keyCount,
            textCount
        )
    }

    @Test
    fun `all BaseUiTexts entries are non-null and non-empty`() {
        UiTextKey.entries.forEach { key ->
            val text = BaseUiTexts[key.ordinal]
            assertNotNull("BaseUiTexts[${key.name}] should not be null", text)
            assertTrue(
                "BaseUiTexts[${key.name}] should not be blank",
                text.isNotBlank()
            )
        }
    }

    @Test
    fun `accessing BaseUiTexts by ordinal does not throw`() {
        for (key in UiTextKey.entries) {
            // This should not throw ArrayIndexOutOfBoundsException
            val text = BaseUiTexts[key.ordinal]
            assertNotNull(text)
        }
    }

    @Test
    fun `new settings UI text keys have correct defaults`() {
        assertEquals("T.Feedback", BaseUiTexts[UiTextKey.SettingsTesterFeedback.ordinal])
        assertEquals("System Notes & Info", BaseUiTexts[UiTextKey.SettingsSystemNotesButton.ordinal])
        assertEquals("System Notes", BaseUiTexts[UiTextKey.SystemNotesTitle.ordinal])
    }
}
