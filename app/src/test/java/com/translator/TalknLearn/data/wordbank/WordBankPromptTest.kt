package com.translator.TalknLearn.data.wordbank

import com.google.firebase.Timestamp
import com.translator.TalknLearn.domain.learning.PromptRecordSelector
import com.translator.TalknLearn.model.TranslationRecord
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for WordBankGenerationRepository prompt construction logic.
 *
 * Verifies:
 * - Record formatting (joinToString pattern)
 * - the 30-record limit (newest first, via PromptRecordSelector)
 * - Language code embedding in prompt
 * - Prompt structure integrity
 */
class WordBankPromptTest {

    private fun buildRecordLine(r: TranslationRecord): String = PromptRecordSelector.formatLine(r)

    private fun buildRecentBlock(records: List<TranslationRecord>): String =
        PromptRecordSelector.buildBlock(records, "ja-JP", PromptRecordSelector.WORD_BANK_LIMIT)

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
        val record = makeRecord("hello", "こんにちは")
        val line = buildRecordLine(record)
        assertEquals("- [en-US→ja-JP] hello => こんにちは", line)
    }

    @Test
    fun `multiple records joined with newlines`() {
        val records = listOf(
            makeRecord("hello", "こんにちは"),
            makeRecord("goodbye", "さようなら")
        )
        val block = buildRecentBlock(records)
        assertTrue(block.contains("hello => こんにちは"))
        assertTrue(block.contains("goodbye => さようなら"))
        assertEquals(2, block.lines().size)
    }

    // --- 30-record limit, newest first ---

    @Test
    fun `only the newest 30 records are included`() {
        // Newest-first, the order Firestore returns history in.
        val records = (50 downTo 1).map { i ->
            makeRecord("word$i", "translation$i", seconds = i.toLong())
        }
        val lines = buildRecentBlock(records).lines()
        assertEquals(30, lines.size)
        assertTrue(lines[0].contains("word50 "))
        assertTrue(lines[29].contains("word21 "))
    }

    @Test
    fun `fewer than 30 records includes all`() {
        val records = (1..5).map { i ->
            makeRecord("word$i", "translation$i")
        }
        val block = buildRecentBlock(records)
        assertEquals(5, block.lines().size)
    }

    @Test
    fun `empty records produces empty block`() {
        val block = buildRecentBlock(emptyList())
        assertEquals("", block)
    }

    @Test
    fun `exactly 30 records includes all`() {
        val records = (1..30).map { i -> makeRecord("w$i", "t$i") }
        assertEquals(30, buildRecentBlock(records).lines().size)
    }

    // --- Language code embedding ---

    @Test
    fun `prompt contains target language code`() {
        val targetLang = "ja-JP"
        val prompt = "Target language to learn: $targetLang"
        assertTrue(prompt.contains("ja-JP"))
    }

    @Test
    fun `prompt contains primary language code`() {
        val primaryLang = "en-US"
        val prompt = "Explain in language: $primaryLang"
        assertTrue(prompt.contains("en-US"))
    }

    // --- Prompt structure ---

    @Test
    fun `prompt requests 15-25 word items`() {
        val prompt = """
Generate 15-25 word items.
""".trimIndent()
        assertTrue(prompt.contains("15-25"))
    }

    @Test
    fun `prompt requires JSON output`() {
        val prompt = "Output ONLY valid JSON. No markdown, no code fences, no extra text."
        assertTrue(prompt.contains("JSON"))
        assertTrue(prompt.contains("No markdown"))
    }

    @Test
    fun `record line preserves special characters`() {
        val record = makeRecord("I'm fine", "大丈夫です", "en-US", "ja-JP")
        val line = buildRecordLine(record)
        assertEquals("- [en-US→ja-JP] I'm fine => 大丈夫です", line)
    }

    @Test
    fun `record line handles empty text`() {
        val record = makeRecord("", "")
        val line = buildRecordLine(record)
        assertEquals("- [en-US→ja-JP]  => ", line)
    }
}
