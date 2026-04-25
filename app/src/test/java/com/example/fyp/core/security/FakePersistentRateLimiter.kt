package com.example.fyp.core.security

import org.mockito.kotlin.mock

/**
 * In-memory test double for [PersistentRateLimiter] that does not touch
 * encrypted storage. Replicates the production behaviour (sliding window,
 * per scope+key tracking) so existing rate-limit tests can exercise the
 * same code paths without an Android context.
 */
class FakePersistentRateLimiter : PersistentRateLimiter(mock()) {

    private val records: MutableMap<String, MutableList<Long>> = mutableMapOf()

    @Synchronized
    override fun isAllowed(
        scope: String,
        key: String,
        maxAttempts: Int,
        windowMillis: Long,
        now: Long
    ): Boolean {
        val k = "${scope}_$key"
        val list = records.getOrPut(k) { mutableListOf() }
        list.removeAll { now - it >= windowMillis }
        return if (list.size < maxAttempts) {
            list.add(now)
            true
        } else {
            false
        }
    }

    @Synchronized
    override fun clear(scope: String, key: String) {
        records.remove("${scope}_$key")
    }

    @Synchronized
    fun clearAll() {
        records.clear()
    }
}
