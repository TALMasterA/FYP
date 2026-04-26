package com.translator.TalknLearn.data.azure

import org.junit.Assert.*
import org.junit.Test

class AzureVoiceConfigTest {

    // ── getVoicesForLanguage ────────────────────────────────────────

    @Test
    fun `getVoicesForLanguage - returns voices for supported language`() {
        val voices = AzureVoiceConfig.getVoicesForLanguage("en-US")
        assertTrue(voices.isNotEmpty())
    }

    @Test
    fun `getVoicesForLanguage - returns empty list for unsupported language`() {
        val voices = AzureVoiceConfig.getVoicesForLanguage("xx-XX")
        assertTrue(voices.isEmpty())
    }

    @Test
    fun `getVoicesForLanguage - each supported language has at least one voice`() {
        for (lang in AzureVoiceConfig.getSupportedLanguages()) {
            val voices = AzureVoiceConfig.getVoicesForLanguage(lang)
            assertTrue("$lang should have at least one voice", voices.isNotEmpty())
        }
    }

    @Test
    fun `getVoicesForLanguage - voices have valid fields`() {
        val voices = AzureVoiceConfig.getVoicesForLanguage("zh-HK")
        for (v in voices) {
            assertTrue("name should not be blank", v.name.isNotBlank())
            assertTrue("displayName should not be blank", v.displayName.isNotBlank())
            assertTrue(
                "gender should be Male or Female, was '${v.gender}'",
                v.gender == "Male" || v.gender == "Female"
            )
        }
    }

    // ── getDefaultVoice ─────────────────────────────────────────────

    @Test
    fun `getDefaultVoice - returns first voice for supported language`() {
        val default = AzureVoiceConfig.getDefaultVoice("en-US")
        assertNotNull(default)
        assertEquals("en-US-JennyNeural", default!!.name)
    }

    @Test
    fun `getDefaultVoice - returns null for unsupported language`() {
        val default = AzureVoiceConfig.getDefaultVoice("xx-XX")
        assertNull(default)
    }

    @Test
    fun `getDefaultVoice - matches first element of getVoicesForLanguage`() {
        for (lang in AzureVoiceConfig.getSupportedLanguages()) {
            val default = AzureVoiceConfig.getDefaultVoice(lang)
            val firstVoice = AzureVoiceConfig.getVoicesForLanguage(lang).firstOrNull()
            assertEquals("Default voice for $lang should be first voice", firstVoice, default)
        }
    }

    // ── getVoiceOrDefault ───────────────────────────────────────────

    @Test
    fun `getVoiceOrDefault - null voiceName returns default voice`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("en-US", null)
        val default = AzureVoiceConfig.getDefaultVoice("en-US")
        assertEquals(default, voice)
    }

    @Test
    fun `getVoiceOrDefault - blank voiceName returns default voice`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("en-US", "")
        val default = AzureVoiceConfig.getDefaultVoice("en-US")
        assertEquals(default, voice)
    }

    @Test
    fun `getVoiceOrDefault - whitespace voiceName returns default voice`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("en-US", "   ")
        val default = AzureVoiceConfig.getDefaultVoice("en-US")
        assertEquals(default, voice)
    }

    @Test
    fun `getVoiceOrDefault - valid voiceName returns matching voice`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("en-US", "en-US-GuyNeural")
        assertNotNull(voice)
        assertEquals("en-US-GuyNeural", voice!!.name)
        assertEquals("Male", voice.gender)
    }

    @Test
    fun `getVoiceOrDefault - unknown voiceName falls back to first voice`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("en-US", "nonexistent-voice")
        val default = AzureVoiceConfig.getDefaultVoice("en-US")
        assertEquals(default, voice)
    }

    @Test
    fun `getVoiceOrDefault - unsupported language returns null`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("xx-XX", "some-voice")
        assertNull(voice)
    }

    @Test
    fun `getVoiceOrDefault - unsupported language with null voiceName returns null`() {
        val voice = AzureVoiceConfig.getVoiceOrDefault("xx-XX", null)
        assertNull(voice)
    }

    // ── getSupportedLanguages ───────────────────────────────────────

    @Test
    fun `getSupportedLanguages - contains expected languages`() {
        val languages = AzureVoiceConfig.getSupportedLanguages()
        assertTrue(languages.contains("en-US"))
        assertTrue(languages.contains("zh-HK"))
        assertTrue(languages.contains("zh-CN"))
        assertTrue(languages.contains("ja-JP"))
        assertTrue(languages.contains("ko-KR"))
        assertTrue(languages.contains("fr-FR"))
        assertTrue(languages.contains("de-DE"))
        assertTrue(languages.contains("es-ES"))
    }

    @Test
    fun `getSupportedLanguages - returns non-empty set`() {
        assertTrue(AzureVoiceConfig.getSupportedLanguages().isNotEmpty())
    }

    // ── Voice data consistency ──────────────────────────────────────

    @Test
    fun `all voice names contain their language locale prefix`() {
        for (lang in AzureVoiceConfig.getSupportedLanguages()) {
            for (voice in AzureVoiceConfig.getVoicesForLanguage(lang)) {
                assertTrue(
                    "Voice '${voice.name}' should start with locale '$lang'",
                    voice.name.startsWith(lang)
                )
            }
        }
    }

    @Test
    fun `all voice names end with Neural`() {
        for (lang in AzureVoiceConfig.getSupportedLanguages()) {
            for (voice in AzureVoiceConfig.getVoicesForLanguage(lang)) {
                assertTrue(
                    "Voice '${voice.name}' should end with 'Neural'",
                    voice.name.endsWith("Neural")
                )
            }
        }
    }
}
