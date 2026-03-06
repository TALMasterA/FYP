package com.example.fyp.core.connectivity

import com.example.fyp.core.NetworkRetry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkRetryTest {

    // ── isRetryableFirebaseException ────────────────────────────────

    @Test
    fun `isRetryable - network error is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("network unreachable")))
    }

    @Test
    fun `isRetryable - timeout error is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("connection timeout")))
    }

    @Test
    fun `isRetryable - connection error is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("connection refused")))
    }

    @Test
    fun `isRetryable - unavailable error is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("service unavailable")))
    }

    @Test
    fun `isRetryable - internal error is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("internal server error")))
    }

    @Test
    fun `isRetryable - deadline error is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("deadline exceeded")))
    }

    @Test
    fun `isRetryable - resource-exhausted is retryable`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("resource-exhausted")))
    }

    @Test
    fun `isRetryable - unknown error is retryable by default`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception("some unknown error")))
    }

    @Test
    fun `isRetryable - unauthenticated is NOT retryable`() {
        assertFalse(NetworkRetry.isRetryableFirebaseException(Exception("unauthenticated")))
    }

    @Test
    fun `isRetryable - permission error is NOT retryable`() {
        assertFalse(NetworkRetry.isRetryableFirebaseException(Exception("permission denied")))
    }

    @Test
    fun `isRetryable - unauthorized is NOT retryable`() {
        assertFalse(NetworkRetry.isRetryableFirebaseException(Exception("unauthorized access")))
    }

    @Test
    fun `isRetryable - invalid-argument is NOT retryable`() {
        assertFalse(NetworkRetry.isRetryableFirebaseException(Exception("invalid-argument")))
    }

    @Test
    fun `isRetryable - invalid argument (with space) is NOT retryable`() {
        assertFalse(NetworkRetry.isRetryableFirebaseException(Exception("invalid argument provided")))
    }

    @Test
    fun `isRetryable - null message exception is retryable (unknown)`() {
        assertTrue(NetworkRetry.isRetryableFirebaseException(Exception()))
    }

    // ── withRetry: success on first attempt ─────────────────────────

    @Test
    fun `withRetry - returns immediately on success`() = runTest {
        var callCount = 0
        val result = NetworkRetry.withRetry(maxAttempts = 3, initialDelayMs = 0) {
            callCount++
            "success"
        }
        assertEquals("success", result)
        assertEquals(1, callCount)
    }

    // ── withRetry: retries on failure then succeeds ─────────────────

    @Test
    fun `withRetry - retries and then succeeds`() = runTest {
        var callCount = 0
        val result = NetworkRetry.withRetry(maxAttempts = 3, initialDelayMs = 0) {
            callCount++
            if (callCount < 3) throw Exception("transient failure")
            "success"
        }
        assertEquals("success", result)
        assertEquals(3, callCount)
    }

    // ── withRetry: exhausted attempts throws ────────────────────────

    @Test(expected = Exception::class)
    fun `withRetry - throws after exhausting all attempts`() = runTest {
        NetworkRetry.withRetry(maxAttempts = 2, initialDelayMs = 0) {
            throw Exception("persistent failure")
        }
    }

    @Test
    fun `withRetry - exhausted attempts calls block maxAttempts times`() = runTest {
        var callCount = 0
        try {
            NetworkRetry.withRetry(maxAttempts = 3, initialDelayMs = 0) {
                callCount++
                throw Exception("persistent failure")
            }
        } catch (_: Exception) {
            // expected
        }
        assertEquals(3, callCount)
    }

    // ── withRetry: shouldRetry predicate ────────────────────────────

    @Test(expected = IllegalArgumentException::class)
    fun `withRetry - stops retrying when shouldRetry returns false`() = runTest {
        var callCount = 0
        NetworkRetry.withRetry(
            maxAttempts = 5,
            initialDelayMs = 0,
            shouldRetry = { it !is IllegalArgumentException }
        ) {
            callCount++
            if (callCount == 1) throw RuntimeException("retryable")
            throw IllegalArgumentException("non-retryable")
        }
    }

    @Test
    fun `withRetry - non-retryable exception thrown on first attempt stops immediately`() = runTest {
        var callCount = 0
        try {
            NetworkRetry.withRetry(
                maxAttempts = 5,
                initialDelayMs = 0,
                shouldRetry = { false }
            ) {
                callCount++
                throw Exception("fail")
            }
        } catch (_: Exception) {
            // expected
        }
        assertEquals(1, callCount)
    }

    // ── withRetry: single attempt ───────────────────────────────────

    @Test(expected = Exception::class)
    fun `withRetry - maxAttempts 1 does not retry`() = runTest {
        var callCount = 0
        NetworkRetry.withRetry(maxAttempts = 1, initialDelayMs = 0) {
            callCount++
            throw Exception("fail")
        }
    }
}
