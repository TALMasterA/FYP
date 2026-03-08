package com.example.fyp.domain.learning

import com.example.fyp.data.learning.QuizParser
import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Integration-style tests for the complete quiz flow:
 * QuizParser → ParseAndStoreQuizUseCase (create attempt, record answers, complete) → CoinEligibility.
 *
 * These tests verify the end-to-end logic without any Firestore dependency.
 *
 * Tests:
 *  1. Parse → create attempt → answer all correctly → score = 10/10
 *  2. Parse → answer mixed → correct score and percentage
 *  3. Parse → complete → check coin eligibility (first quiz always eligible)
 *  4. Parse → complete → check coin eligibility (need 10+ more records)
 *  5. Replace existing answer updates instead of duplicating
 *  6. Zero score quiz is not eligible for coins
 *  7. Malformed JSON produces no questions
 *  8. JSON with missing required fields skips invalid questions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizFlowIntegrationTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val validQuizJson = """
    [
        {"question":"What is 'hello' in Chinese?","options":["你好","再见","谢谢","不好"],"correctIndex":0,"explanation":"你好 means hello"},
        {"question":"What does '谢谢' mean?","options":["Goodbye","Hello","Thank you","Sorry"],"correctIndex":2,"explanation":"谢谢 means thank you"},
        {"question":"Translate 'book'","options":["笔","书","车","猫"],"correctIndex":1,"explanation":"书 means book"},
        {"question":"'我' means?","options":["You","He","She","I/Me"],"correctIndex":3,"explanation":"我 means I or me"},
        {"question":"What is 'water'?","options":["水","火","风","土"],"correctIndex":0,"explanation":"水 means water"},
        {"question":"'大' means?","options":["Small","Big","Fast","Slow"],"correctIndex":1,"explanation":"大 means big"},
        {"question":"Translate 'cat'","options":["狗","鸟","猫","鱼"],"correctIndex":2,"explanation":"猫 means cat"},
        {"question":"'吃' means?","options":["Drink","Run","Sleep","Eat"],"correctIndex":3,"explanation":"吃 means eat"},
        {"question":"What is 'sun'?","options":["太阳","月亮","星星","云"],"correctIndex":0,"explanation":"太阳 means sun"},
        {"question":"'学' means?","options":["Teach","Learn","Read","Write"],"correctIndex":1,"explanation":"学 means learn"}
    ]
    """.trimIndent()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Test 1: Parse + all correct → score = 10/10 ──

    @Test
    fun `full quiz flow with all correct answers scores 10 out of 10`() = runTest {
        val questions = QuizParser.parseQuizFromContent(validQuizJson)
        assertEquals(10, questions.size)

        val parseAndStore: ParseAndStoreQuizUseCase = ParseAndStoreQuizUseCase(mock())
        var attempt = parseAndStore.createAttempt("user1", "en-US", "zh-TW", questions, 50)
        assertEquals(10, attempt.maxScore)

        // Answer all correctly (correctIndex: 0, 2, 1, 3, 0, 1, 2, 3, 0, 1)
        val correctIndices = listOf(0, 2, 1, 3, 0, 1, 2, 3, 0, 1)
        for ((i, q) in questions.withIndex()) {
            attempt = parseAndStore.recordAnswer(attempt, q.id, correctIndices[i])
        }

        val completed = parseAndStore.completeAttempt(attempt)
        assertEquals(10, completed.totalScore)
        assertEquals(10, completed.maxScore)
        assertEquals(100f, completed.percentage)
        assertNotNull(completed.completedAt)
    }

    // ── Test 2: Mixed answers → correct score ──

    @Test
    fun `mixed answers produce correct score and percentage`() = runTest {
        val questions = QuizParser.parseQuizFromContent(validQuizJson)
        val parseAndStore: ParseAndStoreQuizUseCase = ParseAndStoreQuizUseCase(mock())
        var attempt = parseAndStore.createAttempt("user1", "en-US", "zh-TW", questions, 50)

        // Answer first 5 correctly, last 5 wrong
        val correctIndices = listOf(0, 2, 1, 3, 0, 1, 2, 3, 0, 1)
        for ((i, q) in questions.withIndex()) {
            val answer = if (i < 5) correctIndices[i] else (correctIndices[i] + 1) % 4
            attempt = parseAndStore.recordAnswer(attempt, q.id, answer)
        }

        val completed = parseAndStore.completeAttempt(attempt)
        assertEquals(5, completed.totalScore)
        assertEquals(50f, completed.percentage)
    }

    // ── Test 3: First quiz always eligible for coins ──

    @Test
    fun `first quiz for language pair is always eligible for coins`() = runTest {
        val eligible = CoinEligibility.isEligibleForCoins(
            attemptScore = 8,
            generatedHistoryCount = 50,
            currentSheetHistoryCount = 50,
            lastAwardedCount = null // first quiz
        )

        assertTrue(eligible)
    }

    // ── Test 4: Need 10+ more records for subsequent quiz ──

    @Test
    fun `subsequent quiz needs 10+ more records than last awarded`() {
        // Not enough records (only 5 more than last awarded at 50)
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 8,
                generatedHistoryCount = 55,
                currentSheetHistoryCount = 55,
                lastAwardedCount = 50
            )
        )

        // Exactly 10 more records (should be eligible)
        assertTrue(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 8,
                generatedHistoryCount = 60,
                currentSheetHistoryCount = 60,
                lastAwardedCount = 50
            )
        )
    }

    // ── Test 5: Replace existing answer ──

    @Test
    fun `replacing answer updates instead of duplicating`() = runTest {
        val questions = QuizParser.parseQuizFromContent(validQuizJson)
        val parseAndStore: ParseAndStoreQuizUseCase = ParseAndStoreQuizUseCase(mock())
        var attempt = parseAndStore.createAttempt("user1", "en-US", "zh-TW", questions, 50)

        // Answer question 1 wrong first
        attempt = parseAndStore.recordAnswer(attempt, questions[0].id, 3)
        assertEquals(1, attempt.answers.size)
        assertFalse(attempt.answers[0].isCorrect)

        // Change answer to correct
        attempt = parseAndStore.recordAnswer(attempt, questions[0].id, 0)
        assertEquals(1, attempt.answers.size) // still 1 answer, not 2
        assertTrue(attempt.answers[0].isCorrect)
    }

    // ── Test 6: Zero score not eligible ──

    @Test
    fun `zero score quiz is not eligible for coins`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 0,
                generatedHistoryCount = 50,
                currentSheetHistoryCount = 50,
                lastAwardedCount = null
            )
        )
    }

    // ── Test 7: Malformed JSON ──

    @Test
    fun `malformed JSON produces empty question list`() = runTest {
        val questions = QuizParser.parseQuizFromContent("not valid json at all")
        assertTrue(questions.isEmpty())
    }

    // ── Test 8: Invalid questions skipped ──

    @Test
    fun `questions with missing fields are skipped`() = runTest {
        val json = """[
            {"question":"Valid?","options":["A","B","C","D"],"correctIndex":0},
            {"question":"","options":["A","B","C","D"],"correctIndex":0},
            {"question":"Missing options","correctIndex":0},
            {"question":"Bad index","options":["A","B","C","D"],"correctIndex":5}
        ]"""

        val questions = QuizParser.parseQuizFromContent(json)
        assertEquals(1, questions.size) // Only the first valid question
    }

    // ── Test: History count mismatch prevents coins ──

    @Test
    fun `history count mismatch prevents coin award`() {
        assertFalse(
            CoinEligibility.isEligibleForCoins(
                attemptScore = 10,
                generatedHistoryCount = 50,
                currentSheetHistoryCount = 60, // Changed since quiz was generated
                lastAwardedCount = null
            )
        )
    }
}
