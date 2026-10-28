package com.translator.TalknLearn.core.security

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistent, per-scope rate limiter backed by [SecureStorage] (encrypted
 * preferences).
 *
 * Unlike the in-memory [RateLimiter] in `SecurityUtils.kt`, attempt timestamps
 * survive process death. Used for sensitive auth-path / abuse-prone flows
 * (login, password reset, chat, feedback) where a malicious caller could
 * otherwise reset their throttle by force-stopping the app.
 *
 * The on-disk format is a comma-separated list of millisecond timestamps,
 * keyed `ratelimit_${scope}_${key}` in [SecureStorage.prefs]. Stale entries
 * (outside the window) are pruned on every access.
 */
@Singleton
open class PersistentRateLimiter @Inject constructor(
    private val secureStorage: SecureStorage
) {

    private fun prefs(): SharedPreferences = secureStorage.prefs

    private fun keyFor(scope: String, key: String): String =
        KEY_PREFIX + scope + "_" + key

    /**
     * Returns true if the operation is allowed and records the attempt;
     * false if the rate limit is exceeded.
     */
    @Synchronized
    open fun isAllowed(
        scope: String,
        key: String,
        maxAttempts: Int,
        windowMillis: Long,
        now: Long = System.currentTimeMillis()
    ): Boolean {
        require(maxAttempts > 0) { "maxAttempts must be positive" }
        require(windowMillis > 0) { "windowMillis must be positive" }

        val active = pruneAndPersist(scope, key, windowMillis, now)
        if (active.size >= maxAttempts) return false

        val updated = active.toMutableList().apply { add(now) }
        saveTimestamps(scope, key, updated)
        return true
    }

    /** Clears recorded attempts for a single scope+key (test isolation). */
    @Synchronized
    open fun clear(scope: String, key: String) {
        prefs().edit().remove(keyFor(scope, key)).apply()
    }

    private fun pruneAndPersist(
        scope: String,
        key: String,
        windowMillis: Long,
        now: Long
    ): List<Long> {
        val active = loadTimestamps(scope, key)
            .filter { now - it < windowMillis }
            .sorted()
        saveTimestamps(scope, key, active)
        return active
    }

    private fun loadTimestamps(scope: String, key: String): List<Long> {
        val csv = prefs().getString(keyFor(scope, key), null).orEmpty()
        if (csv.isBlank()) return emptyList()
        return csv.split(',').mapNotNull { it.trim().toLongOrNull() }
    }

    private fun saveTimestamps(scope: String, key: String, timestamps: List<Long>) {
        prefs().edit()
            .putString(keyFor(scope, key), timestamps.joinToString(","))
            .apply()
    }

    companion object {
        private const val KEY_PREFIX = "ratelimit_"

        // Centralised scope identifiers — keep in sync with call sites.
        const val SCOPE_LOGIN = "login"
        const val SCOPE_PASSWORD_RESET = "pwreset"
        const val SCOPE_CHAT_MESSAGE = "chatmsg"
        const val SCOPE_FEEDBACK = "feedback"
    }
}
