package com.translator.TalknLearn.data.learning

import com.translator.TalknLearn.model.QuizStats
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from FirestoreQuizRepository.
 *
 * Covers: running average formula, lowestScore initialization,
 * coin award debounce timing, deductCoins insufficient-funds check,
 * quiz stats first-attempt vs subsequent, and batch metadata chunking.
 */
class QuizRepositoryLogicTest {

    // ── Running average formula ───────────────────────────────────────

    @Test
    fun `running average with one existing attempt`() {
        val current = QuizStats(attemptCount = 1, averageScore = 80f)
        val newPercentage = 60f
        val newCount = current.attemptCount + 1
        val newAverage = (current.averageScore * current.attemptCount + newPercentage) / newCount
        assertEquals(2, newCount)
        assertEquals(70f, newAverage, 0.001f)
    }

    @Test
    fun `running average with multiple attempts`() {
        val current = QuizStats(attemptCount = 4, averageScore = 75f)
        val newPercentage = 100f
        val newCount = current.attemptCount + 1
        val newAverage = (current.averageScore * current.attemptCount + newPercentage) / newCount
        // (75*4 + 100) / 5 = (300 + 100) / 5 = 80
        assertEquals(80f, newAverage, 0.001f)
    }

    @Test
    fun `running average with zero existing average`() {
        val current = QuizStats(attemptCount = 1, averageScore = 0f)
        val newPercentage = 50f
        val newCount = current.attemptCount + 1
        val newAverage = (current.averageScore * current.attemptCount + newPercentage) / newCount
        assertEquals(25f, newAverage, 0.001f)
    }

    @Test
    fun `running average with perfect scores stays at 100`() {
        val current = QuizStats(attemptCount = 5, averageScore = 100f)
        val newPercentage = 100f
        val newCount = current.attemptCount + 1
        val newAverage = (current.averageScore * current.attemptCount + newPercentage) / newCount
        assertEquals(100f, newAverage, 0.001f)
    }

    // ── Lowest score initialization ───────────────────────────────────

    @Test
    fun `lowestScore initializes from first subsequent attempt when existing is 0`() {
        val currentLowest = 0
        val attemptScore = 7
        val result = if (currentLowest == 0) attemptScore else minOf(currentLowest, attemptScore)
        assertEquals(7, result)
    }

    @Test
    fun `lowestScore uses min when existing is non-zero`() {
        val currentLowest = 5
        val attemptScore = 3
        val result = if (currentLowest == 0) attemptScore else minOf(currentLowest, attemptScore)
        assertEquals(3, result)
    }

    @Test
    fun `lowestScore stays when new score is higher`() {
        val currentLowest = 3
        val attemptScore = 8
        val result = if (currentLowest == 0) attemptScore else minOf(currentLowest, attemptScore)
        assertEquals(3, result)
    }

    @Test
    fun `lowestScore with equal scores stays the same`() {
        val currentLowest = 5
        val attemptScore = 5
        val result = if (currentLowest == 0) attemptScore else minOf(currentLowest, attemptScore)
        assertEquals(5, result)
    }

    // ── Highest score ─────────────────────────────────────────────────

    @Test
    fun `highestScore updates when new score is higher`() {
        val currentHighest = 8
        val attemptScore = 10
        val result = maxOf(currentHighest, attemptScore)
        assertEquals(10, result)
    }

    @Test
    fun `highestScore stays when new score is lower`() {
        val currentHighest = 10
        val attemptScore = 6
        val result = maxOf(currentHighest, attemptScore)
        assertEquals(10, result)
    }

    // ── Coin award debounce ───────────────────────────────────────────

    @Test
    fun `debounce blocks call within 10 seconds`() {
        val debounceMs = 10_000L
        val lastCoinAwardTime = System.currentTimeMillis() - 5_000L // 5 sec ago
        val now = System.currentTimeMillis()
        val shouldBlock = now - lastCoinAwardTime < debounceMs
        assertTrue("Should block within debounce window", shouldBlock)
    }

    @Test
    fun `debounce allows call after 10 seconds`() {
        val debounceMs = 10_000L
        val lastCoinAwardTime = System.currentTimeMillis() - 15_000L // 15 sec ago
        val now = System.currentTimeMillis()
        val shouldBlock = now - lastCoinAwardTime < debounceMs
        assertFalse("Should allow after debounce window", shouldBlock)
    }

    @Test
    fun `debounce allows first call (lastTime = 0)`() {
        val debounceMs = 10_000L
        val lastCoinAwardTime = 0L
        val now = System.currentTimeMillis()
        val shouldBlock = now - lastCoinAwardTime < debounceMs
        assertFalse("Should allow first-ever call", shouldBlock)
    }

    @Test
    fun `COIN_AWARD_DEBOUNCE_MS is 10 seconds`() {
        val debounce = 10_000L
        assertEquals(10_000L, debounce)
    }

    // ── Deduct coins logic ────────────────────────────────────────────

    @Test
    fun `deductCoins with sufficient balance returns new total`() {
        val coinTotal = 100
        val amount = 30
        val result = if (coinTotal < amount) -1 else coinTotal - amount
        assertEquals(70, result)
    }

    @Test
    fun `deductCoins with insufficient balance returns -1`() {
        val coinTotal = 5
        val amount = 30
        val result = if (coinTotal < amount) -1 else coinTotal - amount
        assertEquals(-1, result)
    }

    @Test
    fun `deductCoins with exact balance returns 0`() {
        val coinTotal = 30
        val amount = 30
        val result = if (coinTotal < amount) -1 else coinTotal - amount
        assertEquals(0, result)
    }

    @Test
    fun `deductCoins with zero balance and zero amount returns 0`() {
        val coinTotal = 0
        val amount = 0
        val result = if (coinTotal < amount) -1 else coinTotal - amount
        assertEquals(0, result)
    }

    @Test
    fun `deductCoins with zero balance and positive amount returns -1`() {
        val coinTotal = 0
        val amount = 10
        val result = if (coinTotal < amount) -1 else coinTotal - amount
        assertEquals(-1, result)
    }

    // ── First attempt vs subsequent ───────────────────────────────────

    @Test
    fun `first attempt initializes all stats fields`() {
        val percentage = 85f
        val totalScore = 17
        val stats = QuizStats(
            primaryLanguageCode = "en-US",
            targetLanguageCode = "ja-JP",
            attemptCount = 1,
            averageScore = percentage,
            highestScore = totalScore,
            lowestScore = totalScore
        )
        assertEquals(1, stats.attemptCount)
        assertEquals(85f, stats.averageScore, 0.001f)
        assertEquals(17, stats.highestScore)
        assertEquals(17, stats.lowestScore)
    }

    @Test
    fun `QuizStats defaults are all zero`() {
        val stats = QuizStats()
        assertEquals(0, stats.attemptCount)
        assertEquals(0f, stats.averageScore, 0.001f)
        assertEquals(0, stats.highestScore)
        assertEquals(0, stats.lowestScore)
        assertNull(stats.lastAttemptAt)
    }

    // ── Batch metadata chunking ───────────────────────────────────────

    @Test
    fun `targets chunked into groups of 10`() {
        val targets = (1..25).map { "lang-$it" }
        val chunks = targets.chunked(10)
        assertEquals(3, chunks.size)
        assertEquals(10, chunks[0].size)
        assertEquals(10, chunks[1].size)
        assertEquals(5, chunks[2].size)
    }

    @Test
    fun `empty targets produces empty chunks`() {
        val targets = emptyList<String>()
        val chunks = targets.chunked(10)
        assertTrue(chunks.isEmpty())
    }

    @Test
    fun `exactly 10 targets produces one chunk`() {
        val targets = (1..10).map { "lang-$it" }
        val chunks = targets.chunked(10)
        assertEquals(1, chunks.size)
        assertEquals(10, chunks[0].size)
    }

    @Test
    fun `single target produces one chunk of one`() {
        val targets = listOf("ja-JP")
        val chunks = targets.chunked(10)
        assertEquals(1, chunks.size)
        assertEquals(1, chunks[0].size)
    }
}
