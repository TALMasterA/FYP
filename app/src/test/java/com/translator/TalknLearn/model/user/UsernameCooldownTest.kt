package com.translator.TalknLearn.model.user

import org.junit.Test
import org.junit.Assert.*

/**
 * Integration-style tests for username change cooldown logic.
 * Verifies the full lifecycle of username cooldown alongside
 * the primary language cooldown to ensure they are independent.
 */
class UsernameCooldownTest {

    private val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000

    @Test
    fun `new user can change username immediately`() {
        val settings = UserSettings() // lastUsernameChangeMs defaults to 0
        assertTrue(UserSettings.canChangeUsername(settings.lastUsernameChangeMs, System.currentTimeMillis()))
    }

    @Test
    fun `user who just changed username cannot change again`() {
        val now = System.currentTimeMillis()
        assertFalse(UserSettings.canChangeUsername(now, now + 1000))
    }

    @Test
    fun `cooldown remaining is approximately 30 days right after change`() {
        val now = System.currentTimeMillis()
        val remaining = UserSettings.usernameCooldownRemainingMs(now, now + 1000)
        // Should be approximately 30 days minus 1 second
        assertTrue(remaining > thirtyDaysMs - 2000)
        assertTrue(remaining <= thirtyDaysMs)
    }

    @Test
    fun `cooldown remaining at halfway is approximately 15 days`() {
        val now = System.currentTimeMillis()
        val halfwayPoint = now + (thirtyDaysMs / 2)
        val remaining = UserSettings.usernameCooldownRemainingMs(now, halfwayPoint)
        val fifteenDaysMs = 15L * 24 * 60 * 60 * 1000
        assertTrue(remaining in (fifteenDaysMs - 1000)..(fifteenDaysMs + 1000))
    }

    @Test
    fun `cooldown remaining is 0 after 31 days`() {
        val now = System.currentTimeMillis()
        val thirtyOneDaysLater = now + (31L * 24 * 60 * 60 * 1000)
        assertEquals(0L, UserSettings.usernameCooldownRemainingMs(now, thirtyOneDaysLater))
    }

    @Test
    fun `both cooldowns can be active simultaneously`() {
        val now = System.currentTimeMillis()
        val recentChange = now - (5L * 24 * 60 * 60 * 1000) // 5 days ago

        assertFalse(UserSettings.canChangePrimaryLanguage(recentChange, now))
        assertFalse(UserSettings.canChangeUsername(recentChange, now))
    }

    @Test
    fun `both cooldowns can be expired simultaneously`() {
        val now = System.currentTimeMillis()
        val oldChange = now - (35L * 24 * 60 * 60 * 1000) // 35 days ago

        assertTrue(UserSettings.canChangePrimaryLanguage(oldChange, now))
        assertTrue(UserSettings.canChangeUsername(oldChange, now))
    }

    @Test
    fun `cooldown constants are equal for username and primary language`() {
        assertEquals(
            UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS,
            UserSettings.USERNAME_CHANGE_COOLDOWN_MS
        )
    }

    @Test
    fun `lastUsernameChangeMs is persisted in data class`() {
        val ts = 1234567890L
        val settings = UserSettings(lastUsernameChangeMs = ts)
        assertEquals(ts, settings.lastUsernameChangeMs)
    }

    @Test
    fun `cooldown check handles future timestamps gracefully`() {
        val now = System.currentTimeMillis()
        val futureChange = now + (10L * 24 * 60 * 60 * 1000) // 10 days in the future
        // If somehow the change timestamp is in the future, canChange should return false
        assertFalse(UserSettings.canChangeUsername(futureChange, now))
    }

    @Test
    fun `cooldown remaining for future timestamp clamps to positive`() {
        val now = System.currentTimeMillis()
        val futureChange = now + (1L * 24 * 60 * 60 * 1000) // 1 day in the future
        val remaining = UserSettings.usernameCooldownRemainingMs(futureChange, now)
        // remaining = (cooldown - (now - futureChange)) = (cooldown + positive) > cooldown
        assertTrue(remaining > 0)
    }
}
