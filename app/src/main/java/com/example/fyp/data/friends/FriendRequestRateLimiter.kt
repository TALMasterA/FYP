package com.example.fyp.data.friends

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val MAX_FRIEND_REQUESTS_PER_HOUR = 10
private const val FRIEND_REQUEST_RATE_LIMIT_WINDOW_MS = 60 * 60 * 1000L

data class FriendRequestRateLimitStatus(
    val allowed: Boolean,
    val retryAfterMillis: Long = 0L,
    val remainingSends: Int = MAX_FRIEND_REQUESTS_PER_HOUR
)

interface FriendRequestRateLimiter {
    fun canSend(userId: String, nowMillis: Long = System.currentTimeMillis()): FriendRequestRateLimitStatus
    fun recordSend(userId: String, nowMillis: Long = System.currentTimeMillis())
    fun clear(userId: String)
}

@Singleton
class SharedPreferencesFriendRequestRateLimiter @Inject constructor(
    @ApplicationContext private val context: Context
) : FriendRequestRateLimiter {

    private companion object {
        const val PREFS_NAME = "friend_request_rate_limit_prefs"
        const val KEY_PREFIX = "friend_request_timestamps_"
    }

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun canSend(
        userId: String,
        nowMillis: Long
    ): FriendRequestRateLimitStatus {
        val activeTimestamps = pruneAndPersist(userId, nowMillis)
        if (activeTimestamps.size < MAX_FRIEND_REQUESTS_PER_HOUR) {
            return FriendRequestRateLimitStatus(
                allowed = true,
                remainingSends = MAX_FRIEND_REQUESTS_PER_HOUR - activeTimestamps.size
            )
        }

        val oldestTimestamp = activeTimestamps.minOrNull() ?: nowMillis
        val retryAfterMillis =
            (FRIEND_REQUEST_RATE_LIMIT_WINDOW_MS - (nowMillis - oldestTimestamp)).coerceAtLeast(0L)

        return FriendRequestRateLimitStatus(
            allowed = false,
            retryAfterMillis = retryAfterMillis,
            remainingSends = 0
        )
    }

    override fun recordSend(userId: String, nowMillis: Long) {
        val timestamps = pruneAndPersist(userId, nowMillis).toMutableList()
        timestamps.add(nowMillis)
        saveTimestamps(userId, timestamps)
    }

    override fun clear(userId: String) {
        prefs().edit().remove(keyFor(userId)).apply()
    }

    private fun pruneAndPersist(userId: String, nowMillis: Long): List<Long> {
        val activeTimestamps = loadTimestamps(userId)
            .filter { nowMillis - it < FRIEND_REQUEST_RATE_LIMIT_WINDOW_MS }
            .sorted()
        saveTimestamps(userId, activeTimestamps)
        return activeTimestamps
    }

    private fun loadTimestamps(userId: String): List<Long> {
        val csv = prefs().getString(keyFor(userId), null).orEmpty()
        if (csv.isBlank()) return emptyList()

        return csv.split(',')
            .mapNotNull { token -> token.trim().toLongOrNull() }
    }

    private fun saveTimestamps(userId: String, timestamps: List<Long>) {
        prefs().edit().putString(keyFor(userId), timestamps.joinToString(",")).apply()
    }

    private fun keyFor(userId: String): String = KEY_PREFIX + userId
}
