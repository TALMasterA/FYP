package com.example.fyp.core.performance

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for PerformanceUtils non-Composable utilities:
 * [generateStableKeys], [TimedCache], and [OperationBatcher].
 *
 * Composable helpers (rememberDebouncedValue, ThrottledLaunchedEffect, rememberMemoized)
 * require a Compose test rule and are covered separately in instrumentation tests.
 */
class PerformanceUtilsTest {

    // ── generateStableKeys ─────────────────────────────────────────

    @Test
    fun `generateStableKeys - maps each item to its key`() {
        data class Item(val id: Int, val name: String)

        val items = listOf(Item(1, "A"), Item(2, "B"), Item(3, "C"))
        val keyMap = generateStableKeys(items) { it.id }

        assertEquals(3, keyMap.size)
        assertEquals(1, keyMap[Item(1, "A")])
        assertEquals(2, keyMap[Item(2, "B")])
        assertEquals(3, keyMap[Item(3, "C")])
    }

    @Test
    fun `generateStableKeys - empty list returns empty map`() {
        val keyMap = generateStableKeys(emptyList<String>()) { it }
        assertTrue(keyMap.isEmpty())
    }

    @Test
    fun `generateStableKeys - string keys work`() {
        val items = listOf("alpha", "beta", "gamma")
        val keyMap = generateStableKeys(items) { "key_$it" }

        assertEquals("key_alpha", keyMap["alpha"])
    }

    // ── TimedCache ─────────────────────────────────────────────────

    @Test
    fun `timedCache - put and get returns value`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        cache.put("a", 1)
        assertEquals(1, cache.get("a"))
    }

    @Test
    fun `timedCache - missing key returns null`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        assertNull(cache.get("missing"))
    }

    @Test
    fun `timedCache - expired entry returns null`() {
        // TTL of 1 ms so entry expires almost immediately
        val cache = TimedCache<String, Int>(ttlMillis = 1L)
        cache.put("a", 1)
        Thread.sleep(10)
        assertNull(cache.get("a"))
    }

    @Test
    fun `timedCache - remove deletes entry`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        cache.put("a", 1)
        cache.remove("a")
        assertNull(cache.get("a"))
    }

    @Test
    fun `timedCache - clear removes all entries`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.clear()
        assertNull(cache.get("a"))
        assertNull(cache.get("b"))
    }

    @Test
    fun `timedCache - overwrite updates value`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        cache.put("a", 1)
        cache.put("a", 2)
        assertEquals(2, cache.get("a"))
    }

    @Test
    fun `timedCache - non-expired entry still valid`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        cache.put("a", 42)
        Thread.sleep(10)
        assertEquals(42, cache.get("a"))
    }
}
