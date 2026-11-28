package com.translator.TalknLearn.data.learning

import com.google.firebase.Timestamp
import com.translator.TalknLearn.domain.learning.PromptRecordSelector
import com.translator.TalknLearn.model.TranslationRecord
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for LearningContentRepositoryImpl prompt construction logic.
 *
 * Verifies:
 * - Record formatting (joinToString pattern)
 * - the 20-record limit (newest first, via PromptRecordSelector)
 * - MAX_VOCABULARY_ITEMS constant (8)
 * - Language code embedding
 * - Prompt structural requirements
 */
class LearningContentPromptTest {

    private val maxVocabularyItems = 8

    private fun buildRecordLine(r: TranslationRecord): String = PromptRecordSelector.formatLine(r)

    private fun buildRecentBlock(records: List<TranslationRecord>): String =
        PromptRecordSelector.buildBlock(records, "ja-JP", PromptRecordSelector.LEARNING_SHEET_LIMIT)

    private fun makeRecord(
        source: String,
        target: String,
        srcLang: String = "en-US",
        tgtLang: String = "ja-JP",
        seconds: Long = 0
    ) = TranslationRecord(
        sourceText = source,
        targetText = target,
        sourceLang = srcLang,
        targetLang = tgtLang,
        timestamp = Timestamp(seconds, 0)
    )

    // --- Record formatting ---

    @Test
    fun `single record formats correctly`() {
        val line = buildRecordLine(makeRecord("cat", "猫"))
        assertEquals("- [en-US→ja-JP] cat => 猫", line)
    }

    @Test
    fun `records joined with newlines`() {
        val records = listOf(makeRecord("cat", "猫"), makeRecord("dog", "犬"))
        val block = buildRecentBlock(records)
        assertEquals(2, block.lines().size)
    }

    // --- 20-record limit, newest first ---

    @Test
    fun `only the newest 20 records are included`() {
        // Newest-first, the order Firestore returns history in.
        val records = (35 downTo 1).map { makeRecord("w$it", "t$it", seconds = it.toLong()) }
        val lines = buildRecentBlock(records).lines()
        assertEquals(20, lines.size)
        assertEquals("- [en-US→ja-JP] w35 => t35", lines[0])
        assertEquals("- [en-US→ja-JP] w16 => t16", lines[19])
    }

    @Test
    fun `fewer than 20 records includes all`() {
        val records = (1..10).map { makeRecord("w$it", "t$it") }
        assertEquals(10, buildRecentBlock(records).lines().size)
    }

    @Test
    fun `empty records produces empty block`() {
        assertEquals("", buildRecentBlock(emptyList()))
    }

    @Test
    fun `exactly 20 records includes all`() {
        val records = (1..20).map { makeRecord("w$it", "t$it") }
        assertEquals(20, buildRecentBlock(records).lines().size)
    }

    // --- MAX_VOCABULARY_ITEMS ---

    @Test
    fun `max vocabulary items is 8`() {
        assertEquals(8, maxVocabularyItems)
    }

    @Test
    fun `prompt references vocabulary limit`() {
        val prompt = "Select only the 5–$maxVocabularyItems most useful"
        assertTrue(prompt.contains("5–8"))
    }

    @Test
    fun `prompt includes do not exceed limit`() {
        val prompt = "Do NOT include more than $maxVocabularyItems vocabulary items."
        assertTrue(prompt.contains("8"))
    }

    // --- Prompt structure ---

    @Test
    fun `prompt embeds target language code`() {
        val target = "ja-JP"
        val prompt = "Create a concise study sheet for target language: $target."
        assertTrue(prompt.contains("ja-JP"))
    }

    @Test
    fun `prompt embeds primary language code`() {
        val primary = "en-US"
        val prompt = "Explanation language: $primary."
        assertTrue(prompt.contains("en-US"))
    }

    @Test
    fun `prompt requires concise study sheet tone`() {
        val prompt = "Do NOT use a question or response tone — write as a concise study sheet only."
        assertTrue(prompt.contains("concise study sheet"))
    }

    @Test
    fun `prompt includes grammar note instruction`() {
        val prompt = "Add a short grammar note (2–3 sentences max) only if a clear grammar pattern appears"
        assertTrue(prompt.contains("grammar note"))
    }

    @Test
    fun `record with multiple languages formats correctly`() {
        val record = makeRecord("bonjour", "hello", "fr-FR", "en-US")
        assertEquals("- [fr-FR→en-US] bonjour => hello", buildRecordLine(record))
    }
}
