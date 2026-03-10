package com.example.fyp.data.history

import com.example.fyp.model.TranslationRecord
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for SharedHistoryDataSource updateLimit and getCountForLanguagePair logic.
 *
 * Covers:
 *  - updateLimit restart conditions
 *  - getCountForLanguagePair bidirectional counting
 *  - getRecordsForLanguage filtering (cache miss path)
 */
class SharedHistoryDataSourceExtendedLogicTest {

    // ── updateLimit restart logic ──────────────────────────────────

    /**
     * Replicates the updateLimit decision: restart only when newLimit != currentLimit.
     */
    private fun shouldRestartForLimitChange(
        currentUserId: String?,
        currentLimit: Long,
        newLimit: Long
    ): Boolean {
        if (currentUserId == null) return false
        return newLimit != currentLimit
    }

    @Test
    fun `updateLimit - restarts when limit changes`() {
        assertTrue(shouldRestartForLimitChange("user1", 30L, 60L))
    }

    @Test
    fun `updateLimit - does not restart when limit is same`() {
        assertFalse(shouldRestartForLimitChange("user1", 30L, 30L))
    }

    @Test
    fun `updateLimit - does nothing when no current user`() {
        assertFalse(shouldRestartForLimitChange(null, 30L, 60L))
    }

    @Test
    fun `updateLimit - does not restart when limit decreases to same`() {
        assertFalse(shouldRestartForLimitChange("user1", 60L, 60L))
    }

    @Test
    fun `updateLimit - restarts when limit decreases`() {
        assertTrue(shouldRestartForLimitChange("user1", 60L, 30L))
    }

    // ── getCountForLanguagePair bidirectional ──────────────────────

    private fun record(
        sourceLang: String,
        targetLang: String,
        timestampSeconds: Long = 1000L
    ) = TranslationRecord(
        id = "rec-$timestampSeconds-$sourceLang-$targetLang",
        sourceLang = sourceLang,
        targetLang = targetLang,
        timestamp = Timestamp(timestampSeconds, 0)
    )

    /**
     * Replicates getCountForLanguagePair: counts records in both directions.
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
        val records = listOf(
            record("en-US", "ja-JP"),
            record("en-US", "ja-JP"),
            record("en-US", "ko-KR")
        )
        assertEquals(2, countForLanguagePair(records, "en-US", "ja-JP"))
    }

    @Test
    fun `countForLanguagePair - counts reverse direction`() {
        val records = listOf(
            record("ja-JP", "en-US"),
            record("ja-JP", "en-US")
        )
        assertEquals(2, countForLanguagePair(records, "en-US", "ja-JP"))
    }

    @Test
    fun `countForLanguagePair - counts both directions combined`() {
        val records = listOf(
            record("en-US", "ja-JP"),
            record("ja-JP", "en-US"),
            record("en-US", "ja-JP")
        )
        assertEquals(3, countForLanguagePair(records, "en-US", "ja-JP"))
    }

    @Test
    fun `countForLanguagePair - excludes other language pairs`() {
        val records = listOf(
            record("en-US", "ja-JP"),
            record("en-US", "ko-KR"),
            record("fr-FR", "de-DE")
        )
        assertEquals(1, countForLanguagePair(records, "en-US", "ja-JP"))
    }

    @Test
    fun `countForLanguagePair - returns zero for empty list`() {
        assertEquals(0, countForLanguagePair(emptyList(), "en-US", "ja-JP"))
    }

    @Test
    fun `countForLanguagePair - returns zero for no matches`() {
        val records = listOf(record("fr-FR", "de-DE"))
        assertEquals(0, countForLanguagePair(records, "en-US", "ja-JP"))
    }

    // ── getRecordsForLanguage filtering ────────────────────────────

    /**
     * Replicates getRecordsForLanguage: filters records where source OR target matches.
     */
    private fun filterByLanguage(
        records: List<TranslationRecord>,
        languageCode: String
    ): List<TranslationRecord> {
        return records.filter {
            it.sourceLang == languageCode || it.targetLang == languageCode
        }
    }

    @Test
    fun `filterByLanguage - includes source matches`() {
        val records = listOf(
            record("en-US", "ja-JP"),
            record("fr-FR", "de-DE")
        )
        val result = filterByLanguage(records, "en-US")
        assertEquals(1, result.size)
    }

    @Test
    fun `filterByLanguage - includes target matches`() {
        val records = listOf(
            record("ja-JP", "en-US"),
            record("fr-FR", "de-DE")
        )
        val result = filterByLanguage(records, "en-US")
        assertEquals(1, result.size)
    }

    @Test
    fun `filterByLanguage - includes both directions`() {
        val records = listOf(
            record("en-US", "ja-JP"),
            record("ja-JP", "en-US"),
            record("fr-FR", "de-DE")
        )
        val result = filterByLanguage(records, "en-US")
        assertEquals(2, result.size)
    }

    @Test
    fun `filterByLanguage - empty list returns empty`() {
        assertTrue(filterByLanguage(emptyList(), "en-US").isEmpty())
    }

    @Test
    fun `filterByLanguage - no matches returns empty`() {
        val records = listOf(record("fr-FR", "de-DE"))
        assertTrue(filterByLanguage(records, "en-US").isEmpty())
    }
}

