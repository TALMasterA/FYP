package com.example.fyp.core

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LanguagePairCacheTest {

    private lateinit var cache: LanguagePairCache<String>

    @Before
    fun setUp() {
        cache = LanguagePairCache()
    }

    // ── get / set ───────────────────────────────────────────────────

    @Test
    fun `get - returns null for uncached key`() {
        assertNull(cache["en-US"])
    }

    @Test
    fun `set and get - stores and retrieves value`() {
        cache["en-US"] = "English content"
        assertEquals("English content", cache["en-US"])
    }

    @Test
    fun `set - overwrites existing value`() {
        cache["en-US"] = "old"
        cache["en-US"] = "new"
        assertEquals("new", cache["en-US"])
    }

    @Test
    fun `multiple keys are independent`() {
        cache["en-US"] = "English"
        cache["zh-CN"] = "Chinese"
        cache["ja-JP"] = "Japanese"

        assertEquals("English", cache["en-US"])
        assertEquals("Chinese", cache["zh-CN"])
        assertEquals("Japanese", cache["ja-JP"])
    }

    // ── contains ────────────────────────────────────────────────────

    @Test
    fun `contains - false for missing key`() {
        assertFalse("en-US" in cache)
    }

    @Test
    fun `contains - true for existing key`() {
        cache["en-US"] = "data"
        assertTrue("en-US" in cache)
    }

    @Test
    fun `contains - false after invalidation`() {
        cache["en-US"] = "data"
        cache.invalidate("en-US")
        assertFalse("en-US" in cache)
    }

    // ── invalidate ──────────────────────────────────────────────────

    @Test
    fun `invalidate - removes specific key`() {
        cache["en-US"] = "English"
        cache["zh-CN"] = "Chinese"

        cache.invalidate("en-US")

        assertNull(cache["en-US"])
        assertEquals("Chinese", cache["zh-CN"])
    }

    @Test
    fun `invalidate - no-op for missing key`() {
        cache["en-US"] = "English"
        cache.invalidate("nonexistent")
        assertEquals("English", cache["en-US"])
    }

    // ── clearIfPrimaryChanged ───────────────────────────────────────

    @Test
    fun `clearIfPrimaryChanged - first call sets primary and returns true`() {
        cache["zh-CN"] = "data"
        val cleared = cache.clearIfPrimaryChanged("en-US")
        assertTrue(cleared)
    }

    @Test
    fun `clearIfPrimaryChanged - clears all entries when primary changes`() {
        cache.clearIfPrimaryChanged("en-US")
        cache["zh-CN"] = "Chinese"
        cache["ja-JP"] = "Japanese"

        val cleared = cache.clearIfPrimaryChanged("fr-FR")
        assertTrue(cleared)
        assertNull(cache["zh-CN"])
        assertNull(cache["ja-JP"])
    }

    @Test
    fun `clearIfPrimaryChanged - same primary returns false and keeps cache`() {
        cache.clearIfPrimaryChanged("en-US")
        cache["zh-CN"] = "Chinese"

        val cleared = cache.clearIfPrimaryChanged("en-US")
        assertFalse(cleared)
        assertEquals("Chinese", cache["zh-CN"])
    }

    @Test
    fun `clearIfPrimaryChanged - changing primary twice clears both times`() {
        cache.clearIfPrimaryChanged("en-US")
        cache["zh-CN"] = "data1"

        assertTrue(cache.clearIfPrimaryChanged("fr-FR"))
        assertNull(cache["zh-CN"])

        cache["de-DE"] = "data2"
        assertTrue(cache.clearIfPrimaryChanged("ja-JP"))
        assertNull(cache["de-DE"])
    }

    // ── keys ────────────────────────────────────────────────────────

    @Test
    fun `keys - empty cache returns empty set`() {
        assertTrue(cache.keys().isEmpty())
    }

    @Test
    fun `keys - returns all cached keys`() {
        cache["en-US"] = "English"
        cache["zh-CN"] = "Chinese"
        cache["ja-JP"] = "Japanese"

        val keys = cache.keys()
        assertEquals(3, keys.size)
        assertTrue(keys.contains("en-US"))
        assertTrue(keys.contains("zh-CN"))
        assertTrue(keys.contains("ja-JP"))
    }

    @Test
    fun `keys - returns snapshot, not live reference`() {
        cache["en-US"] = "English"
        val keysBefore = cache.keys()

        cache["zh-CN"] = "Chinese"
        // The snapshot should not have the new key
        assertFalse(keysBefore.contains("zh-CN"))
    }

    // ── clear ───────────────────────────────────────────────────────

    @Test
    fun `clear - removes all entries`() {
        cache["en-US"] = "English"
        cache["zh-CN"] = "Chinese"

        cache.clear()

        assertNull(cache["en-US"])
        assertNull(cache["zh-CN"])
        assertTrue(cache.keys().isEmpty())
    }

    @Test
    fun `clear - resets lastPrimaryCode`() {
        cache.clearIfPrimaryChanged("en-US")
        assertFalse(cache.clearIfPrimaryChanged("en-US")) // same code, no clear

        cache.clear()
        // After full clear, setting the same primary should count as "changed"
        assertTrue(cache.clearIfPrimaryChanged("en-US"))
    }

    // ── Type safety ─────────────────────────────────────────────────

    @Test
    fun `works with Int values`() {
        val intCache = LanguagePairCache<Int>()
        intCache["en-US"] = 42
        assertEquals(42, intCache["en-US"])
    }

    @Test
    fun `works with List values`() {
        val listCache = LanguagePairCache<List<String>>()
        listCache["en-US"] = listOf("hello", "world")
        assertEquals(listOf("hello", "world"), listCache["en-US"])
    }
}
