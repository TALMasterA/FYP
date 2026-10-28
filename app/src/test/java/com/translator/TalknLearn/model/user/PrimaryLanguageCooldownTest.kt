package com.translator.TalknLearn.model.user

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for primary language change cooldown logic.
 * Requirement: User can only change primary language once a month.
 */
class PrimaryLanguageCooldownTest {

    @Test
    fun `first change is always allowed when lastChangeMs is 0`() {
        assertTrue(
            UserSettings.canChangePrimaryLanguage(
                lastChangeMs = 0L,
                currentTimeMs = System.currentTimeMillis()
            )
        )
    }

    @Test
    fun `change not allowed within 30 days`() {
        val now = System.currentTimeMillis()
        val twentyDaysAgo = now - (20L * 24 * 60 * 60 * 1000)

        assertFalse(
            UserSettings.canChangePrimaryLanguage(
                lastChangeMs = twentyDaysAgo,
                currentTimeMs = now
            )
        )
    }

    @Test
    fun `change allowed after 30 days`() {
        val now = System.currentTimeMillis()
        val thirtyOneDaysAgo = now - (31L * 24 * 60 * 60 * 1000)

        assertTrue(
            UserSettings.canChangePrimaryLanguage(
                lastChangeMs = thirtyOneDaysAgo,
                currentTimeMs = now
            )
        )
    }

    @Test
    fun `change allowed exactly at 30 days`() {
        val now = System.currentTimeMillis()
        val exactlyThirtyDays = now - UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS

        assertTrue(
            UserSettings.canChangePrimaryLanguage(
                lastChangeMs = exactlyThirtyDays,
                currentTimeMs = now
            )
        )
    }

    @Test
    fun `change not allowed 1ms before 30 days`() {
        val now = System.currentTimeMillis()
        val justBefore30Days = now - UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS + 1

        assertFalse(
            UserSettings.canChangePrimaryLanguage(
                lastChangeMs = justBefore30Days,
                currentTimeMs = now
            )
        )
    }

    @Test
    fun `cooldown remaining is zero when allowed`() {
        val remaining = UserSettings.primaryLanguageCooldownRemainingMs(
            lastChangeMs = 0L,
            currentTimeMs = System.currentTimeMillis()
        )
        assertEquals(0L, remaining)
    }

    @Test
    fun `cooldown remaining is positive when blocked`() {
        val now = System.currentTimeMillis()
        val tenDaysAgo = now - (10L * 24 * 60 * 60 * 1000)

        val remaining = UserSettings.primaryLanguageCooldownRemainingMs(
            lastChangeMs = tenDaysAgo,
            currentTimeMs = now
        )

        assertTrue(remaining > 0L)
        // Should be about 20 days remaining
        val twentyDaysMs = 20L * 24 * 60 * 60 * 1000
        assertTrue(remaining in (twentyDaysMs - 1000)..(twentyDaysMs + 1000))
    }

    @Test
    fun `cooldown remaining is zero after 30 days`() {
        val now = System.currentTimeMillis()
        val thirtyOneDaysAgo = now - (31L * 24 * 60 * 60 * 1000)

        val remaining = UserSettings.primaryLanguageCooldownRemainingMs(
            lastChangeMs = thirtyOneDaysAgo,
            currentTimeMs = now
        )
        assertEquals(0L, remaining)
    }

    @Test
    fun `cooldown constant is 30 days in milliseconds`() {
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        assertEquals(thirtyDaysMs, UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS)
    }

    @Test
    fun `lastPrimaryLanguageChangeMs default is 0`() {
        val settings = UserSettings()
        assertEquals(0L, settings.lastPrimaryLanguageChangeMs)
    }

    @Test
    fun `lastPrimaryLanguageChangeMs can be set`() {
        val now = System.currentTimeMillis()
        val settings = UserSettings(lastPrimaryLanguageChangeMs = now)
        assertEquals(now, settings.lastPrimaryLanguageChangeMs)
    }
}
