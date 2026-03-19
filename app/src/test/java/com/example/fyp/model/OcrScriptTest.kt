package com.example.fyp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for OcrScript enum.
 * Verifies language code to script mapping and script properties.
 */
class OcrScriptTest {

    @Test
    fun `fromLanguageCode returns LATIN for English`() {
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("en-US"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("en"))
    }

    @Test
    fun `fromLanguageCode returns CHINESE for Chinese variants`() {
        assertEquals(OcrScript.CHINESE, OcrScript.fromLanguageCode("zh-HK"))
        assertEquals(OcrScript.CHINESE, OcrScript.fromLanguageCode("zh-CN"))
        assertEquals(OcrScript.CHINESE, OcrScript.fromLanguageCode("zh-TW"))
        assertEquals(OcrScript.CHINESE, OcrScript.fromLanguageCode("zh"))
    }

    @Test
    fun `fromLanguageCode returns JAPANESE for Japanese`() {
        assertEquals(OcrScript.JAPANESE, OcrScript.fromLanguageCode("ja-JP"))
        assertEquals(OcrScript.JAPANESE, OcrScript.fromLanguageCode("ja"))
    }

    @Test
    fun `fromLanguageCode returns KOREAN for Korean`() {
        assertEquals(OcrScript.KOREAN, OcrScript.fromLanguageCode("ko-KR"))
        assertEquals(OcrScript.KOREAN, OcrScript.fromLanguageCode("ko"))
    }

    @Test
    fun `fromLanguageCode returns LATIN for European languages`() {
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("fr-FR"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("de-DE"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("es-ES"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("it-IT"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("pt-BR"))
    }

    @Test
    fun `fromLanguageCode returns LATIN for Southeast Asian languages`() {
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("vi-VN"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("id-ID"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("ms-MY"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("fil-PH"))
    }

    @Test
    fun `fromLanguageCode returns LATIN for null input`() {
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode(null))
    }

    @Test
    fun `fromLanguageCode returns LATIN for blank input`() {
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode(""))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("   "))
    }

    @Test
    fun `fromLanguageCode is case insensitive`() {
        assertEquals(OcrScript.CHINESE, OcrScript.fromLanguageCode("ZH-HK"))
        assertEquals(OcrScript.CHINESE, OcrScript.fromLanguageCode("Zh-hk"))
        assertEquals(OcrScript.JAPANESE, OcrScript.fromLanguageCode("JA-JP"))
        assertEquals(OcrScript.KOREAN, OcrScript.fromLanguageCode("KO-KR"))
    }

    @Test
    fun `fromLanguageCode returns LATIN for unknown language`() {
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("xx-XX"))
        assertEquals(OcrScript.LATIN, OcrScript.fromLanguageCode("unknown"))
    }

    @Test
    fun `all scripts have positive estimated size`() {
        OcrScript.entries.forEach { script ->
            assertTrue("${script.name} should have positive size", script.estimatedSizeMb > 0)
        }
    }

    @Test
    fun `all scripts have non-empty display names`() {
        OcrScript.entries.forEach { script ->
            assertTrue("${script.name} should have non-empty display name", script.displayName.isNotBlank())
        }
    }

    @Test
    fun `all scripts have non-empty language prefixes`() {
        OcrScript.entries.forEach { script ->
            assertTrue("${script.name} should have language prefixes", script.languagePrefixes.isNotEmpty())
        }
    }

    @Test
    fun `there are exactly 4 OCR scripts`() {
        assertEquals(4, OcrScript.entries.size)
    }

    @Test
    fun `total estimated size is reasonable`() {
        val totalSize = OcrScript.entries.sumOf { it.estimatedSizeMb }
        // Expected: Latin (5) + Chinese (10) + Japanese (10) + Korean (8) = 33MB
        assertEquals(33, totalSize)
    }

    @Test
    fun `LATIN script covers most languages`() {
        assertTrue(OcrScript.LATIN.languagePrefixes.size > OcrScript.CHINESE.languagePrefixes.size)
    }
}
