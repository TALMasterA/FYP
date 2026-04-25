package com.example.fyp.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PersistentRateLimiter] using the in-memory [FakePersistentRateLimiter]
 * test double. The fake replicates the production sliding-window algorithm and per
 * scope+key isolation, so these tests guard the contract that callers depend on.
 */
class PersistentRateLimiterTest {

    private val limiter = FakePersistentRateLimiter()

    @Test
    fun `allows up to maxAttempts within window`() {
        repeat(3) { i ->
            assertTrue("attempt ${i + 1} should be allowed",
                limiter.isAllowed("scope", "k", maxAttempts = 3, windowMillis = 60_000L, now = 1_000L))
        }
    }

    @Test
    fun `blocks after maxAttempts in window`() {
        repeat(3) {
            limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_000L)
        }
        assertFalse(
            limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_500L)
        )
    }

    @Test
    fun `re-allows after window expires`() {
        repeat(3) {
            limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_000L)
        }
        // Advance well past the window
        assertTrue(
            limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_000L + 60_001L)
        )
    }

    @Test
    fun `clear resets recorded attempts`() {
        repeat(3) {
            limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_000L)
        }
        assertFalse(limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_500L))

        limiter.clear("scope", "k")

        assertTrue(limiter.isAllowed("scope", "k", 3, 60_000L, now = 1_500L))
    }

    @Test
    fun `scopes are isolated`() {
        repeat(3) {
            limiter.isAllowed("scopeA", "k", 3, 60_000L, now = 1_000L)
        }
        // scopeB should not be affected by scopeA exhaustion
        assertTrue(
            limiter.isAllowed("scopeB", "k", 3, 60_000L, now = 1_500L)
        )
    }

    @Test
    fun `keys are isolated within a scope`() {
        repeat(3) {
            limiter.isAllowed("scope", "user1", 3, 60_000L, now = 1_000L)
        }
        // user2 should not be affected by user1 exhaustion
        assertTrue(
            limiter.isAllowed("scope", "user2", 3, 60_000L, now = 1_500L)
        )
    }

    @Test
    fun `expired attempts do not count toward limit`() {
        // Two old attempts (outside window)
        limiter.isAllowed("scope", "k", 3, 60_000L, now = 0L)
        limiter.isAllowed("scope", "k", 3, 60_000L, now = 100L)
        // Three fresh attempts inside a new window
        repeat(3) { i ->
            assertTrue(
                "attempt ${i + 1} should be allowed after old ones expired",
                limiter.isAllowed("scope", "k", 3, 60_000L, now = 200_000L + i)
            )
        }
        // 4th fresh attempt blocked
        assertFalse(
            limiter.isAllowed("scope", "k", 3, 60_000L, now = 200_010L)
        )
    }

    @Test
    fun `single attempt allowed when maxAttempts is one`() {
        assertTrue(limiter.isAllowed("scope", "k", 1, 60_000L, now = 1_000L))
        assertFalse(limiter.isAllowed("scope", "k", 1, 60_000L, now = 1_001L))
    }

    @Test
    fun `scope and key constants are stable`() {
        // Guard against accidental rename: callers depend on these literal values
        // across persisted SharedPreferences keys.
        assertEquals("login", PersistentRateLimiter.SCOPE_LOGIN)
        assertEquals("pwreset", PersistentRateLimiter.SCOPE_PASSWORD_RESET)
        assertEquals("chatmsg", PersistentRateLimiter.SCOPE_CHAT_MESSAGE)
        assertEquals("feedback", PersistentRateLimiter.SCOPE_FEEDBACK)
    }
}
