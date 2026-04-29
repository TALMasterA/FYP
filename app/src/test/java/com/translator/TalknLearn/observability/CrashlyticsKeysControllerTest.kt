package com.translator.TalknLearn.observability

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Item 49 (docs/APP_SUGGESTIONS.md §9): unit tests for the privacy-friendly
 * [CrashlyticsKeysController.accountAgeBucket] helper.  We exercise every
 * boundary so future changes to bucket thresholds break the test instead
 * of silently shifting Crashlytics dimension labels.
 */
class CrashlyticsKeysControllerTest {

    private val now: Long = 1_700_000_000_000L
    private val day: Long = 24L * 60L * 60L * 1000L

    @Test
    fun `null timestamp returns unknown`() {
        assertEquals("unknown", CrashlyticsKeysController.accountAgeBucket(null, now))
    }

    @Test
    fun `non-positive timestamp returns unknown`() {
        assertEquals("unknown", CrashlyticsKeysController.accountAgeBucket(0L, now))
        assertEquals("unknown", CrashlyticsKeysController.accountAgeBucket(-1L, now))
    }

    @Test
    fun `creation in the future returns unknown`() {
        assertEquals("unknown", CrashlyticsKeysController.accountAgeBucket(now + day, now))
    }

    @Test
    fun `same day returns 0d`() {
        assertEquals("0d", CrashlyticsKeysController.accountAgeBucket(now, now))
        assertEquals("0d", CrashlyticsKeysController.accountAgeBucket(now - (day - 1), now))
    }

    @Test
    fun `1 to 6 days returns 1-6d`() {
        assertEquals("1-6d", CrashlyticsKeysController.accountAgeBucket(now - day, now))
        assertEquals("1-6d", CrashlyticsKeysController.accountAgeBucket(now - 6 * day, now))
    }

    @Test
    fun `7 to 29 days returns 7-29d`() {
        assertEquals("7-29d", CrashlyticsKeysController.accountAgeBucket(now - 7 * day, now))
        assertEquals("7-29d", CrashlyticsKeysController.accountAgeBucket(now - 29 * day, now))
    }

    @Test
    fun `30 to 89 days returns 30-89d`() {
        assertEquals("30-89d", CrashlyticsKeysController.accountAgeBucket(now - 30 * day, now))
        assertEquals("30-89d", CrashlyticsKeysController.accountAgeBucket(now - 60 * day, now))
        assertEquals("30-89d", CrashlyticsKeysController.accountAgeBucket(now - 89 * day, now))
    }

    @Test
    fun `90 to 364 days returns 90-364d`() {
        assertEquals("90-364d", CrashlyticsKeysController.accountAgeBucket(now - 90 * day, now))
        assertEquals("90-364d", CrashlyticsKeysController.accountAgeBucket(now - 364 * day, now))
    }

    @Test
    fun `365 days or more returns 365d+`() {
        assertEquals("365d+", CrashlyticsKeysController.accountAgeBucket(now - 365 * day, now))
        assertEquals("365d+", CrashlyticsKeysController.accountAgeBucket(now - 1000 * day, now))
    }
}
