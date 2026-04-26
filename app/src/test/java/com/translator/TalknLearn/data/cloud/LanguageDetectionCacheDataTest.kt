package com.translator.TalknLearn.data.cloud

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for LanguageDetectionCache data models.
 *
 * Tests pure logic that does not depend on Android DataStore.
 * The data class behavior and defaults are critical for correct caching.
 */
class LanguageDetectionCacheDataTest {

    // ── CachedDetection data class ──────────────────────────────────

    @Test
    fun `CachedDetection stores all fields correctly`() {
        val cached = CachedDetection(
            text = "hello world",
            language = "en",
            score = 0.95,
            isTranslationSupported = true,
            timestamp = 1000L
        )

        assertEquals("hello world", cached.text)
        assertEquals("en", cached.language)
        assertEquals(0.95, cached.score, 0.001)
        assertTrue(cached.isTranslationSupported)
        assertEquals(1000L, cached.timestamp)
    }

    @Test
    fun `CachedDetection timestamp defaults to current time`() {
        val before = System.currentTimeMillis()
        val cached = CachedDetection(
            text = "test",
            language = "en",
            score = 1.0,
            isTranslationSupported = true
        )
        val after = System.currentTimeMillis()

        assertTrue(cached.timestamp in before..after)
    }

    @Test
    fun `CachedDetection copy preserves other fields`() {
        val original = CachedDetection(
            text = "hello",
            language = "en",
            score = 0.99,
            isTranslationSupported = true,
            timestamp = 5000L
        )
        val updated = original.copy(score = 0.85)

        assertEquals("hello", updated.text)
        assertEquals("en", updated.language)
        assertEquals(0.85, updated.score, 0.001)
        assertEquals(5000L, updated.timestamp)
    }

    // ── LanguageDetectionCacheData ──────────────────────────────────

    @Test
    fun `LanguageDetectionCacheData defaults to empty entries`() {
        val data = LanguageDetectionCacheData()

        assertTrue(data.entries.isEmpty())
    }

    @Test
    fun `LanguageDetectionCacheData stores entries correctly`() {
        val entry = CachedDetection(
            text = "bonjour",
            language = "fr",
            score = 0.98,
            isTranslationSupported = true
        )
        val data = LanguageDetectionCacheData(entries = mapOf("bonjour" to entry))

        assertEquals(1, data.entries.size)
        assertEquals("fr", data.entries["bonjour"]?.language)
    }

    @Test
    fun `LanguageDetectionCacheData with multiple entries`() {
        val entries = mapOf(
            "hello" to CachedDetection("hello", "en", 0.99, true),
            "bonjour" to CachedDetection("bonjour", "fr", 0.98, true),
            "hola" to CachedDetection("hola", "es", 0.97, true)
        )
        val data = LanguageDetectionCacheData(entries = entries)

        assertEquals(3, data.entries.size)
        assertEquals("en", data.entries["hello"]?.language)
        assertEquals("fr", data.entries["bonjour"]?.language)
        assertEquals("es", data.entries["hola"]?.language)
    }

    @Test
    fun `CachedDetection with low score and unsupported translation`() {
        val cached = CachedDetection(
            text = "xyz123",
            language = "unknown",
            score = 0.1,
            isTranslationSupported = false
        )

        assertEquals(0.1, cached.score, 0.001)
        assertFalse(cached.isTranslationSupported)
    }
}
