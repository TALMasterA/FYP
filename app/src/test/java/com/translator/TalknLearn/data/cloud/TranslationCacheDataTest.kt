package com.translator.TalknLearn.data.cloud

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for TranslationCache data models and cache key logic.
 *
 * Tests pure logic that does not depend on Android DataStore.
 * The cache key generation and data class behavior are critical
 * for correct cache hits/misses.
 */
class TranslationCacheDataTest {

    // ── CachedTranslation data class ────────────────────────────────

    @Test
    fun `CachedTranslation stores all fields correctly`() {
        val cached = CachedTranslation(
            sourceText = "hello",
            translatedText = "hola",
            sourceLang = "en-US",
            targetLang = "es-ES",
            timestamp = 1000L,
            lastAccessedAt = 2000L
        )

        assertEquals("hello", cached.sourceText)
        assertEquals("hola", cached.translatedText)
        assertEquals("en-US", cached.sourceLang)
        assertEquals("es-ES", cached.targetLang)
        assertEquals(1000L, cached.timestamp)
        assertEquals(2000L, cached.lastAccessedAt)
    }

    @Test
    fun `CachedTranslation copy preserves other fields`() {
        val original = CachedTranslation(
            sourceText = "hello",
            translatedText = "hola",
            sourceLang = "en-US",
            targetLang = "es-ES",
            timestamp = 1000L,
            lastAccessedAt = 2000L
        )
        val updated = original.copy(lastAccessedAt = 3000L)

        assertEquals("hello", updated.sourceText)
        assertEquals(1000L, updated.timestamp)
        assertEquals(3000L, updated.lastAccessedAt)
    }

    // ── TranslationCacheData ────────────────────────────────────────

    @Test
    fun `TranslationCacheData defaults to empty entries`() {
        val data = TranslationCacheData()

        assertTrue(data.entries.isEmpty())
    }

    @Test
    fun `TranslationCacheData stores entries correctly`() {
        val entry = CachedTranslation(
            sourceText = "test",
            translatedText = "prueba",
            sourceLang = "en-US",
            targetLang = "es-ES"
        )
        val data = TranslationCacheData(entries = mapOf("key1" to entry))

        assertEquals(1, data.entries.size)
        assertEquals("prueba", data.entries["key1"]?.translatedText)
    }

    // ── CacheStats ──────────────────────────────────────────────────

    @Test
    fun `CacheStats computes correctly`() {
        val stats = CacheStats(totalEntries = 100, validEntries = 80, expiredEntries = 20)

        assertEquals(100, stats.totalEntries)
        assertEquals(80, stats.validEntries)
        assertEquals(20, stats.expiredEntries)
    }

    // ── BatchCacheResult ────────────────────────────────────────────

    @Test
    fun `BatchCacheResult with all found`() {
        val result = BatchCacheResult(
            found = mapOf("hello" to "hola", "cat" to "gato"),
            notFound = emptyList()
        )

        assertEquals(2, result.found.size)
        assertTrue(result.notFound.isEmpty())
    }

    @Test
    fun `BatchCacheResult with mixed found and not found`() {
        val result = BatchCacheResult(
            found = mapOf("hello" to "hola"),
            notFound = listOf("world", "dog")
        )

        assertEquals(1, result.found.size)
        assertEquals(2, result.notFound.size)
        assertTrue(result.notFound.contains("world"))
    }

    @Test
    fun `BatchCacheResult with none found`() {
        val result = BatchCacheResult(
            found = emptyMap(),
            notFound = listOf("hello", "world")
        )

        assertTrue(result.found.isEmpty())
        assertEquals(2, result.notFound.size)
    }
}
