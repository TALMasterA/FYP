package com.translator.TalknLearn.data.history

import org.junit.Assert.*
import org.junit.Test

/**
 * Pure-logic tests for computation patterns used inside [FirestoreHistoryRepository].
 *
 * The repository is tightly coupled to Firestore, so these tests replicate the
 * algorithms in isolation (no mocking, no Android dependencies) to verify:
 *  - language count cache increment / decrement rules
 *  - batch-save chunking at the 490-record boundary
 *  - deleteSession language-pair aggregation
 *  - getLanguageCounts positive-count filtering
 */
class FirestoreHistoryRepositoryLogicTest {

    // ── Helpers replicating repository logic ────────────────────────────

    /**
     * Mirrors [FirestoreHistoryRepository.updateLanguageCountsCache]:
     * returns the set of language keys that would be updated and their delta (+1 or -1).
     *
     * Rules (from lines 333-337 of the source):
     *  - sourceLang is counted if it is not blank
     *  - targetLang is counted if it is not blank AND differs from sourceLang
     */
    private fun computeCountUpdates(
        sourceLang: String,
        targetLang: String,
        increment: Boolean
    ): Map<String, Long> {
        val updates = mutableMapOf<String, Long>()
        val delta = if (increment) 1L else -1L

        if (sourceLang.isNotBlank()) {
            updates[sourceLang] = delta
        }
        if (targetLang.isNotBlank() && targetLang != sourceLang) {
            updates[targetLang] = delta
        }
        return updates
    }

    /**
     * Mirrors the deleteSession aggregation logic (lines 499-503):
     * given a list of (sourceLang, targetLang) pairs collected from documents,
     * aggregate a single decrement map.
     */
    private fun aggregateDecrements(
        langPairs: List<Pair<String, String>>
    ): Map<String, Long> {
        val decrements = mutableMapOf<String, Long>()
        langPairs.forEach { (src, tgt) ->
            if (src.isNotBlank()) decrements[src] = (decrements[src] ?: 0L) - 1
            if (tgt.isNotBlank() && tgt != src) decrements[tgt] = (decrements[tgt] ?: 0L) - 1
        }
        return decrements
    }

    /**
     * Mirrors the getLanguageCounts positive-count filter (lines 295-301):
     * given raw data from a Firestore document, keep only entries where
     * the key is non-empty and the value is a positive number.
     */
    private fun filterPositiveCounts(data: Map<String, Any?>): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        data.forEach { (lang, count) ->
            if (lang.isNotEmpty() && count is Number) {
                val value = count.toInt()
                if (value > 0) {
                    counts[lang] = value
                }
            }
        }
        return counts
    }

    // ── 1. Increment adds 1 to both source and target languages ────────

    @Test
    fun `increment adds 1 to both source and target languages`() {
        val updates = computeCountUpdates("en-US", "zh-HK", increment = true)

        assertEquals(2, updates.size)
        assertEquals(1L, updates["en-US"])
        assertEquals(1L, updates["zh-HK"])
    }

    // ── 2. Decrement subtracts 1 from both languages ───────────────────

    @Test
    fun `decrement subtracts 1 from both source and target languages`() {
        val updates = computeCountUpdates("en-US", "zh-HK", increment = false)

        assertEquals(2, updates.size)
        assertEquals(-1L, updates["en-US"])
        assertEquals(-1L, updates["zh-HK"])
    }

    // ── 3. Same source and target language increments only once ─────────

    @Test
    fun `same source and target language produces single entry`() {
        val updates = computeCountUpdates("ja", "ja", increment = true)

        assertEquals("Duplicate language should appear only once", 1, updates.size)
        assertEquals(1L, updates["ja"])
    }

    @Test
    fun `same source and target language decrements only once`() {
        val updates = computeCountUpdates("ja", "ja", increment = false)

        assertEquals(1, updates.size)
        assertEquals(-1L, updates["ja"])
    }

    // ── 4. Blank language codes are skipped ─────────────────────────────

    @Test
    fun `blank source language is skipped`() {
        val updates = computeCountUpdates("", "zh-HK", increment = true)

        assertEquals(1, updates.size)
        assertNull(updates[""])
        assertEquals(1L, updates["zh-HK"])
    }

    @Test
    fun `blank target language is skipped`() {
        val updates = computeCountUpdates("en-US", "", increment = true)

        assertEquals(1, updates.size)
        assertEquals(1L, updates["en-US"])
        assertNull(updates[""])
    }

    @Test
    fun `both languages blank produces empty map`() {
        val updates = computeCountUpdates("", "", increment = true)
        assertTrue(updates.isEmpty())
    }

    @Test
    fun `whitespace-only language codes are treated as blank`() {
        val updates = computeCountUpdates("   ", "\t", increment = true)
        assertTrue("Whitespace-only strings should be treated as blank", updates.isEmpty())
    }

    // ── 5. Batch chunking at 490 boundary ──────────────────────────────

    @Test
    fun `batch chunking splits at 490`() {
        val records = (1..490).toList()
        val chunks = records.chunked(490)

        assertEquals("Exactly 490 records should yield 1 chunk", 1, chunks.size)
        assertEquals(490, chunks[0].size)
    }

    @Test
    fun `batch chunking produces two chunks for 491 records`() {
        val records = (1..491).toList()
        val chunks = records.chunked(490)

        assertEquals("491 records should yield 2 chunks", 2, chunks.size)
        assertEquals(490, chunks[0].size)
        assertEquals(1, chunks[1].size)
    }

    @Test
    fun `batch chunking handles exactly 980 records as two full chunks`() {
        val records = (1..980).toList()
        val chunks = records.chunked(490)

        assertEquals(2, chunks.size)
        assertEquals(490, chunks[0].size)
        assertEquals(490, chunks[1].size)
    }

    @Test
    fun `batch chunking with empty list produces no chunks`() {
        val chunks = emptyList<Int>().chunked(490)
        assertTrue(chunks.isEmpty())
    }

    // ── 6. Language pair aggregation sums correctly for multiple records ─

    @Test
    fun `aggregation sums decrements across multiple records`() {
        val langPairs = listOf(
            "en-US" to "zh-HK",
            "en-US" to "ja",
            "zh-HK" to "ja"
        )
        val decrements = aggregateDecrements(langPairs)

        assertEquals(-2L, decrements["en-US"])  // appears as src twice
        assertEquals(-2L, decrements["zh-HK"])  // src once + tgt once
        assertEquals(-2L, decrements["ja"])     // tgt twice
    }

    @Test
    fun `aggregation with duplicate pairs accumulates correctly`() {
        val langPairs = listOf(
            "en-US" to "zh-HK",
            "en-US" to "zh-HK",
            "en-US" to "zh-HK"
        )
        val decrements = aggregateDecrements(langPairs)

        assertEquals(-3L, decrements["en-US"])
        assertEquals(-3L, decrements["zh-HK"])
    }

    @Test
    fun `aggregation skips blank source in pairs`() {
        val langPairs = listOf(
            "" to "zh-HK",
            "en-US" to "ja"
        )
        val decrements = aggregateDecrements(langPairs)

        assertNull(decrements[""])
        assertEquals(-1L, decrements["zh-HK"])
        assertEquals(-1L, decrements["en-US"])
        assertEquals(-1L, decrements["ja"])
    }

    @Test
    fun `aggregation skips blank target and deduplicates same src-tgt`() {
        val langPairs = listOf(
            "en-US" to "",      // target blank -> only src counted
            "ja" to "ja"        // same src and tgt -> only src counted
        )
        val decrements = aggregateDecrements(langPairs)

        assertEquals(-1L, decrements["en-US"])
        assertEquals(-1L, decrements["ja"])
        assertNull(decrements[""])
        assertEquals(2, decrements.size)
    }

    @Test
    fun `aggregation with empty list returns empty map`() {
        val decrements = aggregateDecrements(emptyList())
        assertTrue(decrements.isEmpty())
    }

    // ── 7. Positive count filter excludes zero values ───────────────────

    @Test
    fun `positive count filter excludes zero values`() {
        val data: Map<String, Any?> = mapOf(
            "en-US" to 0,
            "zh-HK" to 5
        )
        val result = filterPositiveCounts(data)

        assertNull("Zero count should be excluded", result["en-US"])
        assertEquals(5, result["zh-HK"])
    }

    // ── 8. Positive count filter excludes negative values ───────────────

    @Test
    fun `positive count filter excludes negative values`() {
        val data: Map<String, Any?> = mapOf(
            "en-US" to -3,
            "ja" to -1,
            "zh-HK" to 2
        )
        val result = filterPositiveCounts(data)

        assertNull(result["en-US"])
        assertNull(result["ja"])
        assertEquals(2, result["zh-HK"])
    }

    // ── 9. Positive count filter includes normal positive values ────────

    @Test
    fun `positive count filter includes normal positive values`() {
        val data: Map<String, Any?> = mapOf(
            "en-US" to 10,
            "zh-HK" to 1,
            "ja" to 999
        )
        val result = filterPositiveCounts(data)

        assertEquals(3, result.size)
        assertEquals(10, result["en-US"])
        assertEquals(1, result["zh-HK"])
        assertEquals(999, result["ja"])
    }

    @Test
    fun `positive count filter works with Long values from Firestore`() {
        val data: Map<String, Any?> = mapOf(
            "en-US" to 7L,
            "zh-HK" to 0L,
            "ja" to -2L
        )
        val result = filterPositiveCounts(data)

        assertEquals(1, result.size)
        assertEquals(7, result["en-US"])
    }

    // ── 10. Empty language strings are filtered out ──────────────────────

    @Test
    fun `empty language string keys are filtered out`() {
        val data: Map<String, Any?> = mapOf(
            "" to 5,
            "en-US" to 3
        )
        val result = filterPositiveCounts(data)

        assertEquals(1, result.size)
        assertNull(result[""])
        assertEquals(3, result["en-US"])
    }

    @Test
    fun `null values are filtered out`() {
        val data: Map<String, Any?> = mapOf(
            "en-US" to null,
            "zh-HK" to 4
        )
        val result = filterPositiveCounts(data)

        assertEquals(1, result.size)
        assertNull(result["en-US"])
        assertEquals(4, result["zh-HK"])
    }

    @Test
    fun `non-number values are filtered out`() {
        val data: Map<String, Any?> = mapOf(
            "en-US" to "not a number",
            "zh-HK" to true,
            "ja" to 6
        )
        val result = filterPositiveCounts(data)

        assertEquals(1, result.size)
        assertEquals(6, result["ja"])
    }

    @Test
    fun `filter on empty data returns empty map`() {
        val result = filterPositiveCounts(emptyMap())
        assertTrue(result.isEmpty())
    }

    // ── 11. Rebuild counts deduplicates when sourceLang == targetLang ────

    /**
     * Mirrors [FirestoreHistoryRepository.rebuildLanguageCountsCache] counting logic.
     * Uses a set so that a record whose sourceLang == targetLang is only counted once.
     */
    private fun rebuildCounts(langPairs: List<Pair<String, String>>): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        langPairs.forEach { (src, tgt) ->
            setOf(src, tgt).forEach { lang ->
                if (lang.isNotEmpty()) {
                    counts[lang] = (counts[lang] ?: 0) + 1
                }
            }
        }
        return counts
    }

    @Test
    fun `rebuild counts does not double-count when sourceLang equals targetLang`() {
        val langPairs = listOf("en-US" to "en-US")
        val counts = rebuildCounts(langPairs)

        assertEquals("Same source and target should be counted only once", 1, counts["en-US"])
    }

    @Test
    fun `rebuild counts handles mixed records correctly`() {
        val langPairs = listOf(
            "en-US" to "zh-HK",
            "en-US" to "en-US",   // same-language record
            "ja-JP" to "en-US"
        )
        val counts = rebuildCounts(langPairs)

        // en-US: counted in record 1 (source), record 2 (once, deduplicated), record 3 (target)
        assertEquals(3, counts["en-US"])
        assertEquals(1, counts["zh-HK"])
        assertEquals(1, counts["ja-JP"])
    }

    // ── 12. Save guard rejects records with sourceLang == targetLang ────

    @Test
    fun `saveBatch filter removes records where sourceLang equals targetLang`() {
        data class FakeRecord(val sourceLang: String, val targetLang: String)

        val records = listOf(
            FakeRecord("en-US", "zh-HK"),     // valid
            FakeRecord("ja-JP", "ja-JP"),      // invalid – same language
            FakeRecord("fr-FR", "de-DE"),      // valid
            FakeRecord("", ""),                // blank – allowed through
        )

        val filtered = records.filter { it.sourceLang != it.targetLang || it.sourceLang.isBlank() }
        assertEquals(3, filtered.size)
        assertEquals("en-US", filtered[0].sourceLang)
        assertEquals("fr-FR", filtered[1].sourceLang)
        assertEquals("", filtered[2].sourceLang)
    }
}
