package com.translator.TalknLearn.data.ocr

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for MLKitOcrRepository's script recognizer selection logic.
 *
 * The recognizer selection maps language code prefixes (first 2 chars)
 * to one of four recognizer types: Latin, Chinese, Japanese, Korean.
 *
 * Since the actual ML Kit recognizers are Android-dependent, we test
 * the mapping logic independently.
 */
class OcrRecognizerSelectionTest {

    companion object {
        private const val LANGUAGE_PREFIX_LENGTH = 2
    }

    /**
     * Replicate the recognizer selection logic from MLKitOcrRepository.
     * Returns the recognizer name as a string for testing purposes.
     */
    private fun selectRecognizer(languageCode: String?): String {
        return when (languageCode?.lowercase()?.take(LANGUAGE_PREFIX_LENGTH)) {
            "zh" -> "chinese"
            "ja" -> "japanese"
            "ko" -> "korean"
            else -> "latin"
        }
    }

    // --- Chinese script ---

    @Test
    fun `zh-HK selects Chinese recognizer`() {
        assertEquals("chinese", selectRecognizer("zh-HK"))
    }

    @Test
    fun `zh-TW selects Chinese recognizer`() {
        assertEquals("chinese", selectRecognizer("zh-TW"))
    }

    @Test
    fun `zh-CN selects Chinese recognizer`() {
        assertEquals("chinese", selectRecognizer("zh-CN"))
    }

    @Test
    fun `zh lowercase selects Chinese recognizer`() {
        assertEquals("chinese", selectRecognizer("zh"))
    }

    // --- Japanese script ---

    @Test
    fun `ja-JP selects Japanese recognizer`() {
        assertEquals("japanese", selectRecognizer("ja-JP"))
    }

    @Test
    fun `ja lowercase selects Japanese recognizer`() {
        assertEquals("japanese", selectRecognizer("ja"))
    }

    // --- Korean script ---

    @Test
    fun `ko-KR selects Korean recognizer`() {
        assertEquals("korean", selectRecognizer("ko-KR"))
    }

    @Test
    fun `ko lowercase selects Korean recognizer`() {
        assertEquals("korean", selectRecognizer("ko"))
    }

    // --- Latin script (default) ---

    @Test
    fun `en-US selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("en-US"))
    }

    @Test
    fun `fr-FR selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("fr-FR"))
    }

    @Test
    fun `de-DE selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("de-DE"))
    }

    @Test
    fun `es-ES selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("es-ES"))
    }

    @Test
    fun `id-ID selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("id-ID"))
    }

    @Test
    fun `vi-VN selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("vi-VN"))
    }

    @Test
    fun `th-TH selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("th-TH"))
    }

    @Test
    fun `pt-BR selects Latin recognizer`() {
        assertEquals("latin", selectRecognizer("pt-BR"))
    }

    // --- Edge cases ---

    @Test
    fun `null language code defaults to Latin`() {
        assertEquals("latin", selectRecognizer(null))
    }

    @Test
    fun `empty language code defaults to Latin`() {
        assertEquals("latin", selectRecognizer(""))
    }

    @Test
    fun `case insensitive - uppercase ZH selects Chinese`() {
        assertEquals("chinese", selectRecognizer("ZH-HK"))
    }

    @Test
    fun `case insensitive - uppercase JA selects Japanese`() {
        assertEquals("japanese", selectRecognizer("JA-JP"))
    }

    @Test
    fun `case insensitive - uppercase KO selects Korean`() {
        assertEquals("korean", selectRecognizer("KO-KR"))
    }

    @Test
    fun `unknown language code defaults to Latin`() {
        assertEquals("latin", selectRecognizer("xx-YY"))
    }

    @Test
    fun `LANGUAGE_PREFIX_LENGTH is 2`() {
        assertEquals(2, LANGUAGE_PREFIX_LENGTH)
    }

    @Test
    fun `all 17 supported languages map to correct recognizer`() {
        val expectedMappings = mapOf(
            "en-US" to "latin",
            "zh-TW" to "chinese", "zh-HK" to "chinese", "zh-CN" to "chinese",
            "ja-JP" to "japanese",
            "ko-KR" to "korean",
            "fr-FR" to "latin", "de-DE" to "latin", "es-ES" to "latin",
            "id-ID" to "latin", "vi-VN" to "latin", "th-TH" to "latin",
            "fil-PH" to "latin", "ms-MY" to "latin",
            "pt-BR" to "latin", "it-IT" to "latin", "ru-RU" to "latin"
        )
        expectedMappings.forEach { (code, expected) ->
            assertEquals("$code should use $expected recognizer", expected, selectRecognizer(code))
        }
    }
}
