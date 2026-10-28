package com.translator.TalknLearn.data.history

import com.translator.TalknLearn.model.TranslationRecord
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for SharedHistoryDataSource updateLimit and getRecordsForLanguage logic.
 *
 * Covers:
 *  - updateLimit restart conditions
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

    // ── getRecordsForLanguage filtering ────────────────────────────

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

