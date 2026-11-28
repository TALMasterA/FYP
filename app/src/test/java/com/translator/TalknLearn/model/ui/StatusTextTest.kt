package com.translator.TalknLearn.model.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class StatusTextTest {

    private val templates = mapOf(
        UiTextKey.SpeechStatusDetectedConfidence to "Detected: {language} ({confidence}% confidence)",
        UiTextKey.TtsSpoken to "✓ Spoken",
    )
    private val t: (UiTextKey) -> String = { templates.getValue(it) }

    @Test
    fun `resolve without args returns the plain template`() {
        assertEquals("✓ Spoken", StatusText(UiTextKey.TtsSpoken).resolve(t))
    }

    @Test
    fun `resolve replaces every named placeholder`() {
        val status = StatusText(
            UiTextKey.SpeechStatusDetectedConfidence,
            mapOf("language" to "Japanese", "confidence" to "92"),
        )
        assertEquals("Detected: Japanese (92% confidence)", status.resolve(t))
    }

    @Test
    fun `resolve leaves unknown placeholders and ignores unused args`() {
        val status = StatusText(UiTextKey.SpeechStatusDetectedConfidence, mapOf("language" to "French", "extra" to "x"))
        assertEquals("Detected: French ({confidence}% confidence)", status.resolve(t))
    }

    @Test
    fun `resolve uses the translator for the current language`() {
        val zh: (UiTextKey) -> String = { "已說出" }
        assertEquals("已說出", StatusText(UiTextKey.TtsSpoken).resolve(zh))
    }
}
