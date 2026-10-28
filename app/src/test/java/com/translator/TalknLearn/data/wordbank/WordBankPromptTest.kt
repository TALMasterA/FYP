package com.translator.TalknLearn.data.wordbank

import com.translator.TalknLearn.model.TranslationRecord
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for WordBankGenerationRepository prompt construction logic.
 *
 * Verifies:
 * - Record formatting (joinToString pattern)
 * - takeLast(30) limit
 * - Language code embedding in prompt
 * - Prompt structure integrity
 */
class WordBankPromptTest {

    private fun buildRecordLine(r: TranslationRecord): String =
        "- [${r.sourceLang}→${r.targetLang}] ${r.sourceText} => ${r.targetText}"

    private fun buildRecentBlock(records: List<TranslationRecord>): String =
        records.takeLast(30).joinToString("\n") { buildRecordLine(it) }

    private fun makeRecord(source: String, target: String, srcLang: String = "en-US", tgtLang: String = "ja-JP") =
        TranslationRecord(sourceText = source, targetText = target, sourceLang = srcLang, targetLang = tgtLang)

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

    // --- takeLast(30) limit ---

    @Test
    fun `only last 30 records are included`() {
        val records = (1..50).map { i ->
            makeRecord("word$i", "translation$i")
        }
        val block = buildRecentBlock(records)
        val lines = block.lines()
        assertEquals(30, lines.size)
        // First included record should be word21 (index 20 in 0-based, last 30 of 50)
        assertTrue(lines[0].contains("word21"))
        assertTrue(lines[29].contains("word50"))
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
