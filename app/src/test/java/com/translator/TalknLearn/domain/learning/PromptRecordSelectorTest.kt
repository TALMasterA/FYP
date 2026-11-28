package com.translator.TalknLearn.domain.learning

import com.google.firebase.Timestamp
import com.translator.TalknLearn.model.TranslationRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptRecordSelectorTest {

    private fun record(
        text: String,
        seconds: Long,
        source: String = "en-US",
        target: String = "ja-JP"
    ) = TranslationRecord(
        sourceText = text,
        targetText = "t-$text",
        sourceLang = source,
        targetLang = target,
        timestamp = Timestamp(seconds, 0)
    )

    @Test
    fun `keeps the newest records when history is newest-first`() {
        // Firestore order: newest first. The old takeLast(N) code kept the OLDEST.
        val history = (10 downTo 1).map { record("w$it", seconds = it.toLong()) }

        val picked = PromptRecordSelector.select(history, "ja-JP", limit = 3)

        assertEquals(listOf("w10", "w9", "w8"), picked.map { it.sourceText })
    }

    @Test
    fun `keeps the newest records when history is oldest-first`() {
        val history = (1..10).map { record("w$it", seconds = it.toLong()) }

        val picked = PromptRecordSelector.select(history, "ja-JP", limit = 3)

        assertEquals(listOf("w10", "w9", "w8"), picked.map { it.sourceText })
    }

    @Test
    fun `drops records in other languages`() {
        val history = listOf(
            record("ja-new", seconds = 5),
            record("fr-newest", seconds = 9, target = "fr-FR"),
            record("ja-as-source", seconds = 3, source = "ja-JP", target = "en-US"),
            record("ko", seconds = 7, target = "ko-KR")
        )

        val picked = PromptRecordSelector.select(history, "ja-JP", limit = 10)

        assertEquals(listOf("ja-new", "ja-as-source"), picked.map { it.sourceText })
    }

    @Test
    fun `other languages do not use up the limit`() {
        val history = (1..30).map { record("fr$it", seconds = 100L + it, target = "fr-FR") } +
            (1..5).map { record("ja$it", seconds = it.toLong()) }

        val picked = PromptRecordSelector.select(history, "ja-JP", limit = 20)

        assertEquals(5, picked.size)
        assertTrue(picked.all { it.targetLang == "ja-JP" })
    }

    @Test
    fun `empty history gives an empty block`() {
        assertEquals("", PromptRecordSelector.buildBlock(emptyList(), "ja-JP", limit = 20))
    }

    @Test
    fun `block lists one formatted line per record, newest first`() {
        val history = listOf(record("old", seconds = 1), record("new", seconds = 2))

        val block = PromptRecordSelector.buildBlock(history, "ja-JP", limit = 20)

        assertEquals("- [en-US→ja-JP] new => t-new\n- [en-US→ja-JP] old => t-old", block)
    }

    @Test
    fun `limits match the prompt sizes`() {
        assertEquals(20, PromptRecordSelector.LEARNING_SHEET_LIMIT)
        assertEquals(30, PromptRecordSelector.WORD_BANK_LIMIT)
    }
}
