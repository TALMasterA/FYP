package com.example.fyp.data.azure

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for LanguageDisplayNames utility.
 * Tests display name mapping, detected-to-supported code mapping, and support checks.
 */
class LanguageDisplayNamesTest {

    // ── displayName ──────────────────────────────────────────────────────────

    @Test
    fun `displayName returns English for en-US`() {
        assertEquals("English", LanguageDisplayNames.displayName("en-US"))
    }

    @Test
    fun `displayName returns Cantonese for zh-HK`() {
        assertEquals("Cantonese", LanguageDisplayNames.displayName("zh-HK"))
    }

    @Test
    fun `displayName returns Japanese for ja-JP`() {
        assertEquals("Japanese", LanguageDisplayNames.displayName("ja-JP"))
    }

    @Test
    fun `displayName returns code itself for unknown code`() {
        assertEquals("xx-YY", LanguageDisplayNames.displayName("xx-YY"))
    }

    // ── mapDetectedToSupportedCode ───────────────────────────────────────────

    @Test
    fun `mapDetectedToSupportedCode maps en to en-US`() {
        assertEquals("en-US", LanguageDisplayNames.mapDetectedToSupportedCode("en"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps ja to ja-JP`() {
        assertEquals("ja-JP", LanguageDisplayNames.mapDetectedToSupportedCode("ja"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps zh-Hans to zh-CN`() {
        assertEquals("zh-CN", LanguageDisplayNames.mapDetectedToSupportedCode("zh-Hans"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps zh-Hant to zh-HK`() {
        assertEquals("zh-HK", LanguageDisplayNames.mapDetectedToSupportedCode("zh-Hant"))
    }

    @Test
    fun `mapDetectedToSupportedCode maps yue to zh-HK`() {
        assertEquals("zh-HK", LanguageDisplayNames.mapDetectedToSupportedCode("yue"))
    }

    @Test
    fun `mapDetectedToSupportedCode returns already-supported code unchanged`() {
        assertEquals("en-US", LanguageDisplayNames.mapDetectedToSupportedCode("en-US"))
        assertEquals("ja-JP", LanguageDisplayNames.mapDetectedToSupportedCode("ja-JP"))
    }

    // ── isSupportedLanguage ──────────────────────────────────────────────────

    @Test
    fun `isSupportedLanguage returns true for supported full codes`() {
        assertTrue(LanguageDisplayNames.isSupportedLanguage("en-US"))
        assertTrue(LanguageDisplayNames.isSupportedLanguage("zh-HK"))
    }

    @Test
    fun `isSupportedLanguage returns true for mapped short codes`() {
        assertTrue(LanguageDisplayNames.isSupportedLanguage("en"))
        assertTrue(LanguageDisplayNames.isSupportedLanguage("ja"))
    }

    @Test
    fun `isSupportedLanguage returns false for unsupported codes`() {
        assertFalse(LanguageDisplayNames.isSupportedLanguage("xx-YY"))
        assertFalse(LanguageDisplayNames.isSupportedLanguage("zz"))
    }
}
