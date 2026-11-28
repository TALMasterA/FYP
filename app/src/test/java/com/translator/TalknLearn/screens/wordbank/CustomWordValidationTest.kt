package com.translator.TalknLearn.screens.wordbank

import com.translator.TalknLearn.model.ui.StatusText
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.model.ui.english
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Tests for [validateCustomWordInput]: each field gets its own localizable message. */
class CustomWordValidationTest {

    private fun validate(
        original: String = "hello",
        translated: String = "hola",
        pronunciation: String = "",
        example: String = ""
    ) = validateCustomWordInput(original, translated, pronunciation, example)

    @Test
    fun `valid input returns null`() {
        assertNull(validate())
    }

    @Test
    fun `limits are inclusive`() {
        assertNull(
            validate(
                original = "a".repeat(CUSTOM_WORD_MAX_LENGTH),
                translated = "b".repeat(CUSTOM_WORD_MAX_LENGTH),
                pronunciation = "c".repeat(CUSTOM_PRONUNCIATION_MAX_LENGTH),
                example = "d".repeat(CUSTOM_EXAMPLE_MAX_LENGTH)
            )
        )
    }

    @Test
    fun `empty original is required`() {
        assertEquals(StatusText(UiTextKey.CustomWordsMsgOriginalRequired), validate(original = ""))
    }

    @Test
    fun `empty translated is required`() {
        assertEquals(StatusText(UiTextKey.CustomWordsMsgTranslatedRequired), validate(translated = ""))
    }

    @Test
    fun `too long original fills in the max`() {
        val status = validate(original = "a".repeat(CUSTOM_WORD_MAX_LENGTH + 1))
        assertEquals(UiTextKey.CustomWordsMsgOriginalTooLong, status?.key)
        assertEquals("Original word must not exceed 100 characters", status.english())
    }

    @Test
    fun `too long translated fills in the max`() {
        val status = validate(translated = "a".repeat(CUSTOM_WORD_MAX_LENGTH + 1))
        assertEquals("Translated word must not exceed 100 characters", status.english())
    }

    @Test
    fun `optional fields can be empty but not too long`() {
        assertEquals(
            "Pronunciation must not exceed 200 characters",
            validate(pronunciation = "p".repeat(CUSTOM_PRONUNCIATION_MAX_LENGTH + 1)).english()
        )
        assertEquals(
            "Example must not exceed 500 characters",
            validate(example = "e".repeat(CUSTOM_EXAMPLE_MAX_LENGTH + 1)).english()
        )
    }

    @Test
    fun `original is checked before translated`() {
        assertEquals(StatusText(UiTextKey.CustomWordsMsgOriginalRequired), validate(original = "", translated = ""))
    }
}
