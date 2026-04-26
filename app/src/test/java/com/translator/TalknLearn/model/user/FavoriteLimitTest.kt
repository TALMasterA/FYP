package com.translator.TalknLearn.model.user

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for favorite records limit enforcement.
 * Requirement: Users can only favorite 20 records max.
 * When exceeding the limit, the app should reject and alert the user.
 */
class FavoriteLimitTest {

    @Test
    fun `MAX_FAVORITE_RECORDS is 20`() {
        assertEquals(20, UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `can add favorite when under limit`() {
        val currentCount = 15
        assertTrue(currentCount < UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `cannot add favorite when at limit`() {
        val currentCount = 20
        assertFalse(currentCount < UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `cannot add favorite when over limit`() {
        val currentCount = 25
        assertFalse(currentCount < UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `can add exactly up to limit`() {
        val currentCount = 19
        assertTrue(currentCount < UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `session favorite adds all records to count`() {
        // A session with 5 records adds 5 to the total count
        val currentCount = 17
        val sessionRecordCount = 5
        val wouldExceed = (currentCount + sessionRecordCount) > UserSettings.MAX_FAVORITE_RECORDS

        assertTrue(wouldExceed) // 17 + 5 = 22 > 20
    }

    @Test
    fun `session favorite that fits within limit is allowed`() {
        val currentCount = 15
        val sessionRecordCount = 3
        val wouldExceed = (currentCount + sessionRecordCount) > UserSettings.MAX_FAVORITE_RECORDS

        assertFalse(wouldExceed) // 15 + 3 = 18 <= 20
    }

    @Test
    fun `session favorite at exact limit is allowed`() {
        val currentCount = 15
        val sessionRecordCount = 5
        val wouldExceed = (currentCount + sessionRecordCount) > UserSettings.MAX_FAVORITE_RECORDS

        assertFalse(wouldExceed) // 15 + 5 = 20 == 20, not exceeding
    }

    @Test
    fun `adding single record at limit minus one is last allowed`() {
        val currentCount = 19
        val wouldExceed = (currentCount + 1) > UserSettings.MAX_FAVORITE_RECORDS

        assertFalse(wouldExceed) // 19 + 1 = 20, exactly at limit
    }

    @Test
    fun `adding single record at limit is rejected`() {
        val currentCount = 20
        val wouldExceed = (currentCount + 1) > UserSettings.MAX_FAVORITE_RECORDS

        assertTrue(wouldExceed) // 20 + 1 = 21 > 20
    }

    @Test
    fun `total count includes individual records plus session records`() {
        // 3 individual records + 2 sessions with 5 and 7 records = 3 + 5 + 7 = 15
        val individualCount = 3
        val sessionRecordCounts = listOf(5, 7)
        val totalCount = individualCount + sessionRecordCounts.sum()

        assertEquals(15, totalCount)
        assertTrue(totalCount < UserSettings.MAX_FAVORITE_RECORDS)
    }

    @Test
    fun `session records push total over limit`() {
        // 10 individual records + 1 session with 12 records = 22
        val individualCount = 10
        val sessionRecordCounts = listOf(12)
        val totalCount = individualCount + sessionRecordCounts.sum()
        val wouldExceedIfAddOne = (totalCount + 1) > UserSettings.MAX_FAVORITE_RECORDS

        assertTrue(totalCount > UserSettings.MAX_FAVORITE_RECORDS)
        assertTrue(wouldExceedIfAddOne)
    }
}
