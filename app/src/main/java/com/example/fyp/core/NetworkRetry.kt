package com.example.fyp.core

/**
 * Network retry utility with exponential backoff.
 *
 * This is a utility ready for integration into repository classes.
 * "Unused" warnings are expected until integrated.
 *
 * Usage:
 * ```
 * suspend fun fetchData() = NetworkRetry.withRetry(
 *     maxAttempts = 3,
 *     shouldRetry = NetworkRetry::isRetryableFirebaseException
 * ) {
 *     apiClient.getData()
 * }
 * ```
 */

import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

/**
 * Utility for executing network operations with exponential backoff retry logic.
 * Helps improve reliability when dealing with transient network failures.
 */
object NetworkRetry {

    /**
     * Execute a suspending operation with exponential backoff retry.
     *
     * @param maxAttempts Maximum number of retry attempts (default: 3)
     * @param initialDelayMs Initial delay before first retry in milliseconds (default: 500ms)
     * @param maxDelayMs Maximum delay between retries in milliseconds (default: 5000ms)
     * @param factor Multiplier for exponential backoff (default: 2.0)
     * @param shouldRetry Predicate to determine if an exception is retryable (default: all exceptions)
     * @param block The operation to execute
     * @return Result of the operation
     * @throws Exception if all retry attempts are exhausted
     */
    suspend fun <T> withRetry(
        maxAttempts: Int = 3,
        initialDelayMs: Long = 500,
        maxDelayMs: Long = 5000,
        factor: Double = 2.0,
        shouldRetry: (Exception) -> Boolean = { true },
        block: suspend () -> T
    ): T {
        var currentAttempt = 0
        var currentDelay = initialDelayMs

        while (true) {
            try {
                return block()
            } catch (e: Exception) {
                currentAttempt++

                // If we've exhausted all attempts or exception is not retryable, throw
                if (currentAttempt >= maxAttempts || !shouldRetry(e)) {
                    AppLogger.e("NetworkRetry", "Failed after $currentAttempt attempts", e)
                    throw e
                }

                // Log the retry attempt
                AppLogger.w(
                    "NetworkRetry",
                    "Attempt $currentAttempt/$maxAttempts failed, retrying in ${currentDelay}ms: ${e.message}"
                )

                // Wait before retrying
                delay(currentDelay)

                // Calculate next delay with exponential backoff
                currentDelay = min(
                    (currentDelay * factor.pow(currentAttempt.toDouble())).toLong(),
                    maxDelayMs
                )
            }
        }
    }

    /**
     * Determine if a Firebase Functions exception is retryable.
     * Network errors and server errors (5xx) should be retried,
     * while client errors (4xx) and authentication errors should not.
     */
    fun isRetryableFirebaseException(exception: Exception): Boolean {
        val message = exception.message?.lowercase() ?: ""

        // Retry on network/connection issues
        if (message.contains("network") ||
            message.contains("timeout") ||
            message.contains("connection") ||
            message.contains("unavailable")) {
            return true
        }

        // Don't retry on authentication or permission errors
        if (message.contains("unauthenticated") ||
            message.contains("permission") ||
            message.contains("unauthorized")) {
            return false
        }

        // Don't retry on invalid argument errors
        if (message.contains("invalid-argument") ||
            message.contains("invalid argument")) {
            return false
        }

        // Retry on internal/server errors
        if (message.contains("internal") ||
            message.contains("deadline") ||
            message.contains("resource-exhausted")) {
            return true
        }

        // By default, retry unknown errors
        return true
    }
}

