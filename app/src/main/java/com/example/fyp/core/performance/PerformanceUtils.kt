package com.example.fyp.core.performance

import androidx.compose.runtime.*
import kotlinx.coroutines.delay

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
    var lastExecutionTime by remember { mutableStateOf(0L) }

    LaunchedEffect(key) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastExecution = currentTime - lastExecutionTime

        if (timeSinceLastExecution >= intervalMillis) {
            lastExecutionTime = currentTime
            block()
        }
    }
}

/**
 * Memoized computation result that only recalculates when dependencies change.
 * Prevents expensive recalculations on every recomposition.
 *
 * @param calculation The expensive calculation to memoize
 * @return The memoized result
 */
@Composable
fun <T> rememberMemoized(calculation: () -> T): T {
    return remember(calculation)
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
 * Collects multiple requests and processes them together.
 *
 * Note: This is a simplified version. For production use, consider using
 * a proper batching library or implementing with proper coroutine scopes.
 */
class OperationBatcher<T, R>(
    private val batchSize: Int = 10,
    private val batchDelayMillis: Long = 100L,
    private val processor: suspend (List<T>) -> List<R>
) {
    // Implementation requires careful coroutine handling
    // Users should implement based on their specific needs
    // Example pattern:
    // - Collect items in a Channel
    // - Process in batches using Flow.chunked()
    // - Use proper CoroutineScope for lifecycle management
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
