package com.example.fyp.data.learning

import com.example.fyp.data.cloud.CloudGenAiClient
import com.example.fyp.model.TranslationRecord
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

/**
 * Unit tests for [QuizGenerationRepositoryImpl.selectOptimalContext].
 *
 * The method is private, so we invoke it via reflection.  It implements a
 * diverse-sampling algorithm:
 *   1. Early return when records.size <= targetSize (no selection needed).
 *   2. Deduplicate by sourceText (case-insensitive, trimmed).
 *   3. Select 50 % most-frequent, 30 % recent, 20 % random.
 *   4. Return up to targetSize records sorted by timestamp descending.
 */
class QuizGenerationRepositoryImplTest {

    private lateinit var repository: QuizGenerationRepositoryImpl
    private val mockGenAiClient: CloudGenAiClient = mock()

    /** Reflected handle to the private `selectOptimalContext` method. */
    private val selectOptimalContext by lazy {
        QuizGenerationRepositoryImpl::class.java.getDeclaredMethod(
            "selectOptimalContext",
            List::class.java,
            Int::class.javaPrimitiveType
        ).also { it.isAccessible = true }
    }

    @Before
    fun setUp() {
        repository = QuizGenerationRepositoryImpl(mockGenAiClient)
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Invoke the private method and cast the result. */
    @Suppress("UNCHECKED_CAST")
    private fun invoke(
        records: List<TranslationRecord>,
        targetSize: Int = 20
    ): List<TranslationRecord> {
        return selectOptimalContext.invoke(repository, records, targetSize)
                as List<TranslationRecord>
    }

    /** Create a [Timestamp] from seconds for deterministic ordering. */
    private fun ts(seconds: Long) = Timestamp(seconds, 0)

    /** Convenience builder for a [TranslationRecord]. */
    private fun rec(
        sourceText: String,
        targetText: String = "target",
        timestamp: Timestamp = ts(1L)
    ) = TranslationRecord(
        sourceText = sourceText,
        targetText = targetText,
        sourceLang = "en",
        targetLang = "zh",
        timestamp = timestamp
    )

    // ------------------------------------------------------------------
    // 1. Empty input
    // ------------------------------------------------------------------

    @Test
    fun `empty input returns empty list`() {
        val result = invoke(emptyList(), 20)
        assertTrue("Expected empty result for empty input", result.isEmpty())
    }

    // ------------------------------------------------------------------
    // 2. Single record
    // ------------------------------------------------------------------

    @Test
    fun `single record returns that record`() {
        val records = listOf(rec("hello", timestamp = ts(5)))
        val result = invoke(records, 20)
        assertEquals(1, result.size)
        assertEquals("hello", result[0].sourceText)
    }

    // ------------------------------------------------------------------
    // 3. Returns all records when size <= targetSize (early-return path)
    // ------------------------------------------------------------------

    @Test
    fun `returns all records when count is below targetSize`() {
        val records = (1..15).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 20)
        assertEquals(15, result.size)
    }

    @Test
    fun `returns all records when count equals targetSize`() {
        val records = (1..20).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 20)
        assertEquals(20, result.size)
    }

    // ------------------------------------------------------------------
    // 4. Result never exceeds targetSize
    // ------------------------------------------------------------------

    @Test
    fun `result never exceeds targetSize`() {
        val records = (1..100).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 10)
        assertTrue("Result size ${result.size} should be <= 10", result.size <= 10)
    }

    @Test
    fun `result never exceeds targetSize with large input`() {
        val records = (1..500).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 20)
        assertTrue("Result size ${result.size} should be <= 20", result.size <= 20)
    }

    // ------------------------------------------------------------------
    // 5. Deduplication by sourceText (case-insensitive)
    // ------------------------------------------------------------------

    @Test
    fun `deduplicates records by sourceText case insensitive`() {
        // 25 unique words, each appearing 3 times in different cases = 75 records
        val records = mutableListOf<TranslationRecord>()
        for (i in 1..25) {
            records.add(rec("Word$i", timestamp = ts(i.toLong())))
            records.add(rec("WORD$i", timestamp = ts(i.toLong() + 100)))
            records.add(rec("word$i", timestamp = ts(i.toLong() + 200)))
        }

        val result = invoke(records, 10)

        // No two results should share the same lowered+trimmed sourceText
        val keys = result.map { it.sourceText.lowercase().trim() }
        assertEquals(
            "Result should contain no duplicate sourceText (case-insensitive)",
            keys.size, keys.distinct().size
        )
    }

    // ------------------------------------------------------------------
    // 6. Same text different case are deduplicated
    // ------------------------------------------------------------------

    @Test
    fun `all records with same text different case collapse to one`() {
        // 30 records that are all "hello" in various cases
        val records = (1..30).map { i ->
            when (i % 3) {
                0 -> rec("Hello", timestamp = ts(i.toLong()))
                1 -> rec("HELLO", timestamp = ts(i.toLong()))
                else -> rec("hello", timestamp = ts(i.toLong()))
            }
        }

        val result = invoke(records, 10)
        assertEquals(
            "All variants of 'hello' should collapse to 1 record",
            1, result.size
        )
    }

    // ------------------------------------------------------------------
    // 7. Frequent words are prioritized
    // ------------------------------------------------------------------

    @Test
    fun `most frequent word is included in selection`() {
        val records = mutableListOf<TranslationRecord>()

        // "frequent_word" appears 50 times
        repeat(50) { records.add(rec("frequent_word", timestamp = ts(1L))) }

        // 30 unique rare words (each once)
        for (i in 1..30) {
            records.add(rec("rare_$i", timestamp = ts(100L + i)))
        }
        // Total: 80 records, 31 unique words

        val result = invoke(records, 10)

        assertTrue(
            "Most frequent word should appear in result",
            result.any { it.sourceText == "frequent_word" }
        )
    }

    @Test
    fun `top-3 frequent words are all selected`() {
        val records = mutableListOf<TranslationRecord>()

        repeat(100) { records.add(rec("top1", timestamp = ts(1L))) }
        repeat(80) { records.add(rec("top2", timestamp = ts(2L))) }
        repeat(60) { records.add(rec("top3", timestamp = ts(3L))) }

        // 30 filler words, one occurrence each
        for (i in 1..30) {
            records.add(rec("filler_$i", timestamp = ts(100L + i)))
        }
        // 33 unique words, 270 records total; frequentCount = 10/2 = 5

        val result = invoke(records, 10)

        assertTrue("top1 should be selected", result.any { it.sourceText == "top1" })
        assertTrue("top2 should be selected", result.any { it.sourceText == "top2" })
        assertTrue("top3 should be selected", result.any { it.sourceText == "top3" })
    }

    // ------------------------------------------------------------------
    // 8. Recent words are included
    // ------------------------------------------------------------------

    @Test
    fun `recent unique words appear in selection`() {
        val records = mutableListOf<TranslationRecord>()

        // 25 old frequent words (each appears 3 times, old timestamps)
        for (i in 1..25) {
            repeat(3) { records.add(rec("old_$i", timestamp = ts(100L + i))) }
        }

        // 5 brand-new unique words (high timestamps, at the end of list)
        for (i in 1..5) {
            records.add(rec("new_$i", timestamp = ts(10_000L + i)))
        }
        // Total: 80 records, 30 unique words

        val result = invoke(records, 20)

        val recentCount = result.count { it.sourceText.startsWith("new_") }
        assertTrue(
            "At least one recent word should be included (found $recentCount)",
            recentCount >= 1
        )
    }

    // ------------------------------------------------------------------
    // 9. Result is sorted by timestamp descending
    // ------------------------------------------------------------------

    @Test
    fun `result is sorted by timestamp descending when selection occurs`() {
        // 50 records with distinct timestamps
        val records = (1..50).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 10)

        for (i in 0 until result.size - 1) {
            assertTrue(
                "result[$i] timestamp (${result[i].timestamp.seconds}) should be " +
                        ">= result[${i + 1}] timestamp (${result[i + 1].timestamp.seconds})",
                result[i].timestamp >= result[i + 1].timestamp
            )
        }
    }

    @Test
    fun `result with mixed timestamps is sorted descending`() {
        val records = mutableListOf<TranslationRecord>()
        // High-frequency word with old timestamp
        repeat(40) { records.add(rec("old_freq", timestamp = ts(10L))) }
        // Low-frequency words with newer timestamps
        for (i in 1..25) {
            records.add(rec("newer_$i", timestamp = ts(1000L + i)))
        }

        val result = invoke(records, 10)

        for (i in 0 until result.size - 1) {
            assertTrue(
                "Results must be timestamp-descending",
                result[i].timestamp >= result[i + 1].timestamp
            )
        }
    }

    // ------------------------------------------------------------------
    // 10. Mixed frequency/recency selection
    // ------------------------------------------------------------------

    @Test
    fun `selection includes both frequent and recent words`() {
        val records = mutableListOf<TranslationRecord>()

        // 10 high-frequency words (each appears 10 times, old timestamps)
        for (i in 1..10) {
            repeat(10) { records.add(rec("freq_$i", timestamp = ts(50L + i))) }
        }

        // 20 medium-frequency words (each appears 3 times)
        for (i in 1..20) {
            repeat(3) { j -> records.add(rec("med_$i", timestamp = ts(500L + i * 10 + j))) }
        }

        // 10 unique recent words (highest timestamps, at end of list)
        for (i in 1..10) {
            records.add(rec("recent_$i", timestamp = ts(10_000L + i)))
        }
        // Total: 100 + 60 + 10 = 170 records, 40 unique words

        val result = invoke(records, 20)

        val freqCount = result.count { it.sourceText.startsWith("freq_") }
        val recentCount = result.count { it.sourceText.startsWith("recent_") }

        assertTrue("Should include frequent words (found $freqCount)", freqCount > 0)
        assertTrue("Should include recent words (found $recentCount)", recentCount > 0)
        assertEquals("Should return exactly targetSize", 20, result.size)
    }

    // ------------------------------------------------------------------
    // 11. Whitespace trimming during deduplication
    // ------------------------------------------------------------------

    @Test
    fun `whitespace around sourceText is trimmed for deduplication`() {
        val records = mutableListOf<TranslationRecord>()
        for (i in 1..25) {
            records.add(rec("  hello  ", timestamp = ts(i.toLong())))
            records.add(rec("hello", timestamp = ts(i.toLong() + 100)))
        }
        // 50 records, 1 unique word after trimming

        val result = invoke(records, 10)
        assertEquals(
            "Whitespace-trimmed duplicates should collapse to 1",
            1, result.size
        )
    }

    // ------------------------------------------------------------------
    // 12. targetSize = 1
    // ------------------------------------------------------------------

    @Test
    fun `targetSize of 1 returns exactly one record`() {
        val records = (1..50).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 1)
        assertEquals(1, result.size)
    }

    // ------------------------------------------------------------------
    // 13. No duplicates in output (general invariant)
    // ------------------------------------------------------------------

    @Test
    fun `output never contains duplicate sourceTexts`() {
        val records = mutableListOf<TranslationRecord>()
        // Mix of duplicates and unique words
        for (i in 1..40) {
            repeat(if (i <= 10) 5 else 1) {
                records.add(rec("word$i", timestamp = ts(i.toLong())))
            }
        }

        val result = invoke(records, 15)

        val keys = result.map { it.sourceText.lowercase().trim() }
        assertEquals(
            "Output must never contain duplicate sourceTexts",
            keys.size, keys.distinct().size
        )
    }

    // ------------------------------------------------------------------
    // 14. Frequency bucket sizing
    // ------------------------------------------------------------------

    @Test
    fun `frequent bucket gets roughly half the targetSize`() {
        val records = mutableListOf<TranslationRecord>()

        // 5 very-high-frequency words (each appears 20 times)
        for (i in 1..5) {
            repeat(20) { records.add(rec("highfreq_$i", timestamp = ts(i.toLong()))) }
        }
        // 5 medium-frequency words (each appears 5 times)
        for (i in 1..5) {
            repeat(5) { records.add(rec("medfreq_$i", timestamp = ts(100L + i))) }
        }
        // 20 unique low-frequency words (appear once, recent timestamps)
        for (i in 1..20) {
            records.add(rec("lowfreq_$i", timestamp = ts(1000L + i)))
        }
        // Total: 100 + 25 + 20 = 145 records, 30 unique words

        val result = invoke(records, 10)
        // frequentCount = 10 / 2 = 5
        // All 5 high-frequency words should be included
        val highFreqSelected = result.count { it.sourceText.startsWith("highfreq_") }
        assertEquals(
            "All 5 high-frequency words should fill the frequent bucket",
            5, highFreqSelected
        )
    }

    // ------------------------------------------------------------------
    // 15. Fewer unique words than targetSize after dedup
    // ------------------------------------------------------------------

    @Test
    fun `returns fewer than targetSize when unique words are scarce`() {
        // 5 unique words, each repeated many times -> 50 records
        val records = mutableListOf<TranslationRecord>()
        for (i in 1..5) {
            repeat(10) { j -> records.add(rec("word$i", timestamp = ts(i.toLong() + j))) }
        }

        // records.size = 50 > targetSize = 10, so selection kicks in
        val result = invoke(records, 10)

        // Only 5 unique words exist, so result cannot be larger than 5
        assertTrue(
            "Result (${result.size}) should be <= unique word count (5)",
            result.size <= 5
        )
    }

    // ------------------------------------------------------------------
    // 16. Early-return path preserves original order (no sorting)
    // ------------------------------------------------------------------

    @Test
    fun `early return preserves original list order`() {
        // Provide records in ascending timestamp order, fewer than targetSize
        val records = (1..10).map { rec("word$it", timestamp = ts(it.toLong())) }
        val result = invoke(records, 20)

        // Because records.size <= targetSize the method returns the input list as-is
        assertEquals(records, result)
    }
}
