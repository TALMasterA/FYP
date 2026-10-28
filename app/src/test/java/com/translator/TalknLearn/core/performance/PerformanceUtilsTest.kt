package com.translator.TalknLearn.core.performance

import kotlinx.coroutines.runBlocking
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
        // Use a larger TTL (50ms) and longer sleep (150ms) to avoid flakiness
        // from Windows System.currentTimeMillis() granularity (~15ms)
        val cache = TimedCache<String, Int>(ttlMillis = 50L)
        cache.put("a", 1)
        Thread.sleep(150)
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

    @Test
    fun `timedCache - re-put after expiration returns new value`() {
        val cache = TimedCache<String, Int>(ttlMillis = 50L)
        cache.put("a", 1)
        Thread.sleep(150)
        assertNull(cache.get("a"))
        cache.put("a", 99)
        assertEquals(99, cache.get("a"))
    }

    @Test
    fun `timedCache - multiple keys independent expiry`() {
        val cache = TimedCache<String, Int>(ttlMillis = 60_000L)
        cache.put("a", 1)
        cache.put("b", 2)
        cache.remove("a")
        assertNull(cache.get("a"))
        assertEquals(2, cache.get("b"))
    }

    @Test
    fun `generateStableKeys - duplicate items collapsed by associateWith`() {
        val items = listOf("x", "x", "y")
        val keyMap = generateStableKeys(items) { "key_$it" }
        // associateWith uses last occurrence for duplicates
        assertEquals(2, keyMap.size)
        assertEquals("key_x", keyMap["x"])
        assertEquals("key_y", keyMap["y"])
    }

    // ── OperationBatcher ───────────────────────────────────────────

    @Test
    fun `batcher - submit below batch size returns null`() = runBlocking {
        val batcher = OperationBatcher<Int, Int>(batchSize = 3) { it.map { v -> v * 2 } }
        assertNull(batcher.submit(1))
        assertNull(batcher.submit(2))
    }

    @Test
    fun `batcher - submit reaching batch size triggers processor`() = runBlocking {
        val batcher = OperationBatcher<Int, Int>(batchSize = 3) { it.map { v -> v * 2 } }
        batcher.submit(1)
        batcher.submit(2)
        val result = batcher.submit(3)
        assertNotNull(result)
        assertEquals(listOf(2, 4, 6), result)
    }

    @Test
    fun `batcher - flush processes partial batch`() = runBlocking {
        val batcher = OperationBatcher<Int, Int>(batchSize = 10) { it.map { v -> v + 1 } }
        batcher.submit(5)
        batcher.submit(10)
        val result = batcher.flush()
        assertEquals(listOf(6, 11), result)
    }

    @Test
    fun `batcher - flush on empty returns empty list`() = runBlocking {
        val batcher = OperationBatcher<Int, Int>(batchSize = 5) { it }
        val result = batcher.flush()
        assertTrue(result.isEmpty())
    }
}
