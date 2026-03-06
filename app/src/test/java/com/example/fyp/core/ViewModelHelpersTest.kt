package com.example.fyp.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ViewModelHelpers: LanguagePairCache.
 *
 * Tests:
 * 1. Basic get/set operations
 * 2. Cache invalidation for specific language
 * 3. clearIfPrimaryChanged clears cache on primary change
 * 4. clearIfPrimaryChanged no-op when primary unchanged
 * 5. clear resets all state
 * 6. contains operator
 * 7. keys() returns cached keys
 */
class ViewModelHelpersTest {

    // ── LanguagePairCache ───────────────────────────────────────────

    @Test
    fun `get returns null for uncached key`() {
        val cache = LanguagePairCache<String>()
        assertNull(cache["en-US"])
    }

    @Test
    fun `set and get round-trip`() {
        val cache = LanguagePairCache<String>()
        cache["en-US"] = "cached_value"
        assertEquals("cached_value", cache["en-US"])
    }

    @Test
    fun `contains returns false for missing key`() {
        val cache = LanguagePairCache<String>()
        assertFalse("en-US" in cache)
    }

    @Test
    fun `contains returns true for cached key`() {
        val cache = LanguagePairCache<String>()
        cache["ja-JP"] = "data"
        assertTrue("ja-JP" in cache)
    }

    @Test
    fun `invalidate removes specific key`() {
        val cache = LanguagePairCache<String>()
        cache["en-US"] = "english"
        cache["ja-JP"] = "japanese"
        cache.invalidate("en-US")

        assertNull(cache["en-US"])
        assertEquals("japanese", cache["ja-JP"])
    }

    @Test
    fun `clearIfPrimaryChanged clears cache when primary changes`() {
        val cache = LanguagePairCache<String>()
        cache.clearIfPrimaryChanged("en-US")
        cache["ja-JP"] = "data"

        val cleared = cache.clearIfPrimaryChanged("zh-TW")

        assertTrue(cleared)
        assertNull(cache["ja-JP"])
    }

    @Test
    fun `clearIfPrimaryChanged returns false when primary unchanged`() {
        val cache = LanguagePairCache<String>()
        cache.clearIfPrimaryChanged("en-US")
        cache["ja-JP"] = "data"

        val cleared = cache.clearIfPrimaryChanged("en-US")

        assertFalse(cleared)
        assertEquals("data", cache["ja-JP"])
    }

    @Test
    fun `keys returns all cached language codes`() {
        val cache = LanguagePairCache<String>()
        cache["en-US"] = "a"
        cache["ja-JP"] = "b"
        cache["zh-TW"] = "c"

        val keys = cache.keys()
        assertEquals(setOf("en-US", "ja-JP", "zh-TW"), keys)
    }

    @Test
    fun `clear resets all state`() {
        val cache = LanguagePairCache<String>()
        cache.clearIfPrimaryChanged("en-US")
        cache["ja-JP"] = "data"

        cache.clear()

        assertNull(cache["ja-JP"])
        assertTrue(cache.keys().isEmpty())
        // After clear, clearIfPrimaryChanged should return true for any code
        assertTrue(cache.clearIfPrimaryChanged("en-US"))
    }
}
