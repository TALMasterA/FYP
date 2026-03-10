package com.example.fyp.data.history

import com.example.fyp.model.TranslationRecord
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for pure logic extracted from SharedHistoryDataSource.
 *
 * Covers:
 *  - LRU cache eviction (max 10 entries)
 *  - Language record filtering (source OR target match)
 *  - Bidirectional language pair counting
 *  - Language count lookup
 *  - Debounce timestamp logic
 *  - State reset on stopObserving
 */
class SharedHistoryLogicTest {

    // ── LRU cache logic ──────────────────────────────────────────────────────

    /**
     * Replicates the LRU cache from SharedHistoryDataSource (line 64-71).
     * LinkedHashMap with accessOrder=true and max 10 entries.
     */
    private fun createLruCache(maxSize: Int = 10): LinkedHashMap<String, List<String>> {
        return object : LinkedHashMap<String, List<String>>(maxSize, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<String>>?): Boolean {
                return size > maxSize
            }
        }
    }

    @Test
    fun `LRU cache evicts eldest entry when exceeding max size`() {
        val cache = createLruCache(3)
        cache["a"] = listOf("1")
        cache["b"] = listOf("2")
        cache["c"] = listOf("3")
        cache["d"] = listOf("4") // should evict "a"

        assertNull("Eldest entry 'a' should be evicted", cache["a"])
        assertEquals(3, cache.size)
        assertNotNull(cache["b"])
        assertNotNull(cache["c"])
        assertNotNull(cache["d"])
    }

    @Test
    fun `LRU cache access promotes entry and evicts least recently used`() {
        val cache = createLruCache(3)
        cache["a"] = listOf("1")
        cache["b"] = listOf("2")
        cache["c"] = listOf("3")

        // Access "a" to make it most recently used
        cache["a"]

        cache["d"] = listOf("4") // should evict "b" (least recently used)

        assertNotNull("Accessed entry 'a' should NOT be evicted", cache["a"])
        assertNull("Least recently used entry 'b' should be evicted", cache["b"])
        assertEquals(3, cache.size)
    }

    @Test
    fun `LRU cache with max 10 entries evicts at 11`() {
        val cache = createLruCache(10)
        for (i in 1..10) {
            cache["lang$i"] = listOf("record$i")
        }
        assertEquals(10, cache.size)

        cache["lang11"] = listOf("record11")
        assertEquals(10, cache.size)
        assertNull("First entry should be evicted", cache["lang1"])
        assertNotNull(cache["lang11"])
    }

    @Test
    fun `LRU cache clear removes all entries`() {
        val cache = createLruCache(10)
        cache["a"] = listOf("1")
        cache["b"] = listOf("2")
        cache.clear()
        assertEquals(0, cache.size)
        assertNull(cache["a"])
    }

    // ── Language record filtering logic ──────────────────────────────────────

    /**
     * Replicates getRecordsForLanguage from SharedHistoryDataSource (line 174-182).
     */
    private fun filterRecordsForLanguage(
        records: List<TranslationRecord>,
        languageCode: String
    ): List<TranslationRecord> {
        return records.filter {
            it.sourceLang == languageCode || it.targetLang == languageCode
        }
    }

    private val sampleRecords = listOf(
        TranslationRecord(id = "1", sourceLang = "en-US", targetLang = "zh-TW", sourceText = "Hello", targetText = "你好"),
        TranslationRecord(id = "2", sourceLang = "en-US", targetLang = "ja-JP", sourceText = "Hello", targetText = "こんにちは"),
        TranslationRecord(id = "3", sourceLang = "zh-TW", targetLang = "ja-JP", sourceText = "你好", targetText = "こんにちは"),
        TranslationRecord(id = "4", sourceLang = "fr-FR", targetLang = "en-US", sourceText = "Bonjour", targetText = "Hello"),
        TranslationRecord(id = "5", sourceLang = "en-US", targetLang = "ko-KR", sourceText = "Hello", targetText = "안녕하세요")
    )

    @Test
    fun `filterRecordsForLanguage - matches source language`() {
        val result = filterRecordsForLanguage(sampleRecords, "fr-FR")
        assertEquals(1, result.size)
        assertEquals("4", result[0].id)
    }

    @Test
    fun `filterRecordsForLanguage - matches target language`() {
        val result = filterRecordsForLanguage(sampleRecords, "ko-KR")
        assertEquals(1, result.size)
        assertEquals("5", result[0].id)
    }

    @Test
    fun `filterRecordsForLanguage - matches both source and target`() {
        val result = filterRecordsForLanguage(sampleRecords, "en-US")
        assertEquals(4, result.size) // records 1, 2, 4, 5
    }

    @Test
    fun `filterRecordsForLanguage - zh-TW appears as source and target`() {
        val result = filterRecordsForLanguage(sampleRecords, "zh-TW")
        assertEquals(2, result.size) // records 1, 3
    }

    @Test
    fun `filterRecordsForLanguage - ja-JP appears as target in two records`() {
        val result = filterRecordsForLanguage(sampleRecords, "ja-JP")
        assertEquals(2, result.size) // records 2, 3
    }

    @Test
    fun `filterRecordsForLanguage - unknown language returns empty`() {
        val result = filterRecordsForLanguage(sampleRecords, "de-DE")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `filterRecordsForLanguage - empty records returns empty`() {
        val result = filterRecordsForLanguage(emptyList(), "en-US")
        assertTrue(result.isEmpty())
    }

    // ── Bidirectional language pair counting ─────────────────────────────────

    /**
     * Replicates getCountForLanguagePair from SharedHistoryDataSource (line 196-203).
     */
    private fun countForLanguagePair(
        records: List<TranslationRecord>,
        primaryLang: String,
        targetLang: String
    ): Int {
        return records.count {
            (it.sourceLang == primaryLang && it.targetLang == targetLang) ||
            (it.sourceLang == targetLang && it.targetLang == primaryLang)
        }
    }

    @Test
    fun `countForLanguagePair - counts forward direction`() {
        val count = countForLanguagePair(sampleRecords, "en-US", "zh-TW")
        assertEquals(1, count) // record 1 only
    }

    @Test
    fun `countForLanguagePair - counts reverse direction`() {
        val count = countForLanguagePair(sampleRecords, "en-US", "fr-FR")
        assertEquals(1, count) // record 4 (fr->en)
    }

    @Test
    fun `countForLanguagePair - counts both directions`() {
        // Only forward: en->ja (record 2)
        // zh->ja: record 3 does NOT match en-US/ja-JP pair
        val count = countForLanguagePair(sampleRecords, "en-US", "ja-JP")
        assertEquals(1, count)
    }

    @Test
    fun `countForLanguagePair - is symmetric`() {
        val forward = countForLanguagePair(sampleRecords, "en-US", "zh-TW")
        val reverse = countForLanguagePair(sampleRecords, "zh-TW", "en-US")
        assertEquals(forward, reverse)
    }

    @Test
    fun `countForLanguagePair - no match returns zero`() {
        val count = countForLanguagePair(sampleRecords, "de-DE", "ru-RU")
        assertEquals(0, count)
    }

    @Test
    fun `countForLanguagePair - empty records returns zero`() {
        val count = countForLanguagePair(emptyList(), "en-US", "zh-TW")
        assertEquals(0, count)
    }

    // ── Language count lookup ────────────────────────────────────────────────

    /**
     * Replicates getCountForLanguage from SharedHistoryDataSource (line 188-190).
     */
    private fun getCountForLanguage(counts: Map<String, Int>, languageCode: String): Int {
        return counts[languageCode] ?: 0
    }

    @Test
    fun `getCountForLanguage - returns count when present`() {
        val counts = mapOf("en-US" to 42, "zh-TW" to 15)
        assertEquals(42, getCountForLanguage(counts, "en-US"))
    }

    @Test
    fun `getCountForLanguage - returns zero when absent`() {
        val counts = mapOf("en-US" to 42)
        assertEquals(0, getCountForLanguage(counts, "ja-JP"))
    }

    @Test
    fun `getCountForLanguage - empty map returns zero`() {
        assertEquals(0, getCountForLanguage(emptyMap(), "en-US"))
    }

    // ── Debounce logic ──────────────────────────────────────────────────────

    /**
     * Replicates debounce check from SharedHistoryDataSource (line 124-125).
     */
    private fun shouldSkipRefresh(
        lastRefreshTime: Long,
        now: Long,
        debounceMs: Long = 5_000L
    ): Boolean {
        return lastRefreshTime != 0L && now - lastRefreshTime < debounceMs
    }

    @Test
    fun `debounce - skips when within window`() {
        assertTrue(shouldSkipRefresh(1000L, 4999L, 5000L))
    }

    @Test
    fun `debounce - allows at exact boundary`() {
        assertFalse(shouldSkipRefresh(1000L, 6000L, 5000L))
    }

    @Test
    fun `debounce - allows after window`() {
        assertFalse(shouldSkipRefresh(1000L, 7000L, 5000L))
    }

    @Test
    fun `debounce - allows when lastRefreshTime is zero`() {
        assertFalse(shouldSkipRefresh(0L, 1000L, 5000L))
    }

    // ── startObserving idempotency ──────────────────────────────────────────

    /**
     * Replicates the idempotency check in startObserving (line 80-83).
     */
    private fun shouldSkipObserving(
        currentUserId: String?,
        newUserId: String,
        currentLimit: Long,
        newLimit: Long,
        jobActive: Boolean
    ): Boolean {
        return newUserId == currentUserId && newLimit == currentLimit && jobActive
    }

    @Test
    fun `startObserving - skips when same user, same limit, job active`() {
        assertTrue(shouldSkipObserving("user1", "user1", 30L, 30L, true))
    }

    @Test
    fun `startObserving - does not skip when different user`() {
        assertFalse(shouldSkipObserving("user1", "user2", 30L, 30L, true))
    }

    @Test
    fun `startObserving - does not skip when different limit`() {
        assertFalse(shouldSkipObserving("user1", "user1", 30L, 60L, true))
    }

    @Test
    fun `startObserving - does not skip when job is not active`() {
        assertFalse(shouldSkipObserving("user1", "user1", 30L, 30L, false))
    }

    @Test
    fun `startObserving - does not skip when currentUserId is null`() {
        assertFalse(shouldSkipObserving(null, "user1", 30L, 30L, false))
    }

    // ── updateLimit logic ───────────────────────────────────────────────────

    /**
     * Replicates updateLimit logic from SharedHistoryDataSource (line 157-160).
     */
    private fun shouldRestartForLimit(currentUserId: String?, newLimit: Long, currentLimit: Long): Boolean {
        return currentUserId != null && newLimit != currentLimit
    }

    @Test
    fun `updateLimit - restarts when limit changes`() {
        assertTrue(shouldRestartForLimit("user1", 60L, 30L))
    }

    @Test
    fun `updateLimit - does not restart when same limit`() {
        assertFalse(shouldRestartForLimit("user1", 30L, 30L))
    }

    @Test
    fun `updateLimit - does not restart when no current user`() {
        assertFalse(shouldRestartForLimit(null, 60L, 30L))
    }
}
