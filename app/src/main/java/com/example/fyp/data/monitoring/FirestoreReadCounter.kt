package com.example.fyp.data.monitoring

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors and logs Firestore read operations to help track database usage.
 * This helps identify optimization opportunities and monitor read costs.
 */
@Singleton
class FirestoreReadCounter @Inject constructor() {
    private var readCount = 0
    private var cacheHitCount = 0

    /**
     * Increment read counter for database reads.
     */
    fun incrementRead() {
        readCount++
        if (readCount % 100 == 0) {
            Log.d("FirestoreReads", "Total database reads: $readCount (cache hits: $cacheHitCount)")
        }
    }

    /**
     * Increment cache hit counter.
     */
    fun incrementCacheHit() {
        cacheHitCount++
    }

    /**
     * Get current read count.
     */
    fun getReadCount(): Int = readCount

    /**
     * Get current cache hit count.
     */
    fun getCacheHitCount(): Int = cacheHitCount

    /**
     * Get cache hit rate as a percentage.
     */
    fun getCacheHitRate(): Float {
        val total = readCount + cacheHitCount
        return if (total > 0) (cacheHitCount.toFloat() / total * 100) else 0f
    }

    /**
     * Reset counters (useful for testing or periodic reports).
     */
    fun reset() {
        readCount = 0
        cacheHitCount = 0
    }

    /**
     * Log a summary of read statistics.
     */
    fun logSummary() {
        Log.i("FirestoreReads", """
            === Firestore Read Statistics ===
            Total database reads: $readCount
            Cache hits: $cacheHitCount
            Cache hit rate: ${String.format("%.1f", getCacheHitRate())}%
            =================================
        """.trimIndent())
    }
}

