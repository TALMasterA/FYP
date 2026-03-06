package com.example.fyp.data.cloud

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CloudQuizClient data models and response parsing logic.
 *
 * Note: Firebase Functions HttpsCallableReference is a final class that cannot
 * be mocked in standard JVM tests. These tests verify the data models (CoinAwardResult,
 * SpendCoinsResult) that CloudQuizClient produces and consumes.
 *
 * Tests:
 *  1. CoinAwardResult defaults
 *  2. CoinAwardResult with all fields
 *  3. CoinAwardResult awarded state
 *  4. CoinAwardResult error state
 *  5. SpendCoinsResult defaults
 *  6. SpendCoinsResult with all fields
 *  7. SpendCoinsResult success state
 *  8. SpendCoinsResult failure with reason
 *  9. SpendCoinsResult newLimit nullable
 * 10. CoinAwardResult copy behavior
 */
class CloudQuizClientTest {

    // ── CoinAwardResult tests ──

    @Test
    fun `CoinAwardResult defaults`() {
        val result = CoinAwardResult(awarded = false)
        assertFalse(result.awarded)
        assertNull(result.reason)
        assertEquals(0, result.coinsAwarded)
        assertEquals(0, result.newTotal)
    }

    @Test
    fun `CoinAwardResult with all fields`() {
        val result = CoinAwardResult(
            awarded = true,
            reason = "first_attempt",
            coinsAwarded = 8,
            newTotal = 150
        )
        assertTrue(result.awarded)
        assertEquals("first_attempt", result.reason)
        assertEquals(8, result.coinsAwarded)
        assertEquals(150, result.newTotal)
    }

    @Test
    fun `CoinAwardResult awarded state`() {
        val result = CoinAwardResult(awarded = true, coinsAwarded = 10, newTotal = 200)
        assertTrue(result.awarded)
        assertEquals(10, result.coinsAwarded)
        assertEquals(200, result.newTotal)
    }

    @Test
    fun `CoinAwardResult error state`() {
        val result = CoinAwardResult(awarded = false, reason = "error: Network timeout")
        assertFalse(result.awarded)
        assertNotNull(result.reason)
        assertTrue(result.reason!!.startsWith("error:"))
        assertEquals(0, result.coinsAwarded)
    }

    @Test
    fun `CoinAwardResult copy preserves fields`() {
        val original = CoinAwardResult(awarded = true, coinsAwarded = 5, newTotal = 100)
        val copied = original.copy(coinsAwarded = 10)
        assertTrue(copied.awarded)
        assertEquals(10, copied.coinsAwarded)
        assertEquals(100, copied.newTotal)
    }

    @Test
    fun `CoinAwardResult equality`() {
        val a = CoinAwardResult(true, "ok", 5, 100)
        val b = CoinAwardResult(true, "ok", 5, 100)
        assertEquals(a, b)
    }

    // ── SpendCoinsResult tests ──

    @Test
    fun `SpendCoinsResult defaults`() {
        val result = SpendCoinsResult(success = false)
        assertFalse(result.success)
        assertNull(result.reason)
        assertEquals(0, result.newBalance)
        assertNull(result.newLimit)
    }

    @Test
    fun `SpendCoinsResult with all fields`() {
        val result = SpendCoinsResult(
            success = true,
            reason = null,
            newBalance = 500,
            newLimit = 40
        )
        assertTrue(result.success)
        assertNull(result.reason)
        assertEquals(500, result.newBalance)
        assertEquals(40, result.newLimit)
    }

    @Test
    fun `SpendCoinsResult failure with reason`() {
        val result = SpendCoinsResult(
            success = false,
            reason = "insufficient_coins",
            newBalance = 50
        )
        assertFalse(result.success)
        assertEquals("insufficient_coins", result.reason)
        assertEquals(50, result.newBalance)
    }

    @Test
    fun `SpendCoinsResult newLimit is nullable`() {
        val withLimit = SpendCoinsResult(success = true, newBalance = 300, newLimit = 40)
        val withoutLimit = SpendCoinsResult(success = true, newBalance = 300)

        assertEquals(40, withLimit.newLimit)
        assertNull(withoutLimit.newLimit)
    }

    @Test
    fun `SpendCoinsResult equality`() {
        val a = SpendCoinsResult(true, null, 500, 40)
        val b = SpendCoinsResult(true, null, 500, 40)
        assertEquals(a, b)
    }

    @Test
    fun `SpendCoinsResult error from exception`() {
        // Simulates how CloudQuizClient builds error results
        val errorResult = SpendCoinsResult(
            success = false,
            reason = "error: Timeout"
        )
        assertFalse(errorResult.success)
        assertTrue(errorResult.reason!!.contains("error"))
    }
}
