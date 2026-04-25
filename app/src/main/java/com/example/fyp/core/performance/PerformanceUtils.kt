package com.example.fyp.core.performance

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Performance optimization utilities for the app.
 * Provides helpers for debouncing, throttling, and other performance improvements.
 */

/**
 * Debounced value that only emits after the specified delay has passed since the last change.
 * Useful for search fields and other user input that should trigger expensive operations.
 *
 * @param value The value to debounce
 * @param delayMillis The delay in milliseconds before emitting the value
 * @return The debounced value
 */
@Composable
fun <T> rememberDebouncedValue(value: T, delayMillis: Long = 300L): T {
    var debouncedValue by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        delay(delayMillis)
        debouncedValue = value
    }

    return debouncedValue
}

/**
 * Throttled effect that ensures the block is only executed once within the specified interval.
 * Useful for rate-limiting expensive operations like API calls or database writes.
 *
 * @param key The key to watch for changes
 * @param intervalMillis The minimum interval between executions in milliseconds
 * @param block The block to execute
 */
@Composable
fun ThrottledLaunchedEffect(
    key: Any?,
    intervalMillis: Long = 1000L,
    block: suspend () -> Unit
) {
    val lastExecutionTime = remember { mutableLongStateOf(0L) }

    LaunchedEffect(key) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastExecution = currentTime - lastExecutionTime.longValue

        if (timeSinceLastExecution >= intervalMillis) {
            lastExecutionTime.longValue = currentTime
            block()
        }
    }
}

/**
 * Memoized computation with a single dependency.
 */
@Composable
fun <T, R> rememberMemoized(key: T, calculation: (T) -> R): R {
    return remember(key) { calculation(key) }
}

/**
 * Memoized computation with two dependencies.
 */
@Composable
fun <T1, T2, R> rememberMemoized(key1: T1, key2: T2, calculation: (T1, T2) -> R): R {
    return remember(key1, key2) { calculation(key1, key2) }
}

/**
 * LazyColumn item key generator for stable list items.
 * Provides stable keys for better list performance and animations.
 *
 * @param items The list of items
 * @param keySelector Function to extract a unique key from each item
 * @return A map of items to their stable keys
 */
fun <T> generateStableKeys(items: List<T>, keySelector: (T) -> Any): Map<T, Any> {
    return items.associateWith { keySelector(it) }
}

/**
 * Helper for batching operations to reduce repeated expensive calls.
 * Collects multiple requests and processes them together once [batchSize]
 * items have been submitted or [flush] is called explicitly.
 *
 * Thread-safe: all mutations are guarded by a [Mutex].
 */
class OperationBatcher<T, R>(
    private val batchSize: Int = 10,
    private val processor: suspend (List<T>) -> List<R>
) {
    private val mutex = Mutex()
    private val pending = mutableListOf<T>()

    /**
     * Add an item to the pending batch.
     * If the batch reaches [batchSize], it is flushed automatically
     * and the results are returned. Otherwise returns `null`.
     */
    suspend fun submit(item: T): List<R>? {
        val batch: List<T>
        mutex.withLock {
            pending.add(item)
            if (pending.size < batchSize) return null
            batch = pending.toList()
            pending.clear()
        }
        return processor(batch)
    }

    /**
     * Force-process whatever items are pending, regardless of batch size.
     * Returns an empty list when there is nothing pending.
     */
    suspend fun flush(): List<R> {
        val batch: List<T>
        mutex.withLock {
            batch = pending.toList()
            pending.clear()
        }
        return if (batch.isEmpty()) emptyList() else processor(batch)
    }

}

/**
 * Cache for expensive computations with time-based expiration.
 */
class TimedCache<K, V>(private val ttlMillis: Long = 5 * 60 * 1000L) {
    private data class CacheEntry<V>(val value: V, val timestamp: Long)

    private val cache = mutableMapOf<K, CacheEntry<V>>()

    fun get(key: K): V? {
        val entry = cache[key] ?: return null
        val age = System.currentTimeMillis() - entry.timestamp

        return if (age < ttlMillis) {
            entry.value
        } else {
            cache.remove(key)
            null
        }
    }

    fun put(key: K, value: V) {
        cache[key] = CacheEntry(value, System.currentTimeMillis())
    }

    fun clear() {
        cache.clear()
    }

    fun remove(key: K) {
        cache.remove(key)
    }
}
