package com.example.fyp.screens.wordbank

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for WordBank data models.
 *
 * Covers: WordBankItem, WordBank, WordBankUiState, WordBankLanguageCluster, SpeakingType.
 */
class WordBankModelsTest {

    // ── WordBankItem ───────────────────────────────────────────────

    @Test
    fun `WordBankItem has correct defaults`() {
        val item = WordBankItem()
        assertEquals("", item.id)
        assertEquals("", item.originalWord)
        assertEquals("", item.translatedWord)
        assertEquals("", item.pronunciation)
        assertEquals("", item.example)
        assertEquals("", item.category)
        assertEquals("", item.difficulty)
    }

    @Test
    fun `WordBankItem stores all fields`() {
        val item = WordBankItem(
            id = "w1",
            originalWord = "apple",
            translatedWord = "蘋果",
            pronunciation = "píng guǒ",
            example = "I eat an apple.",
            category = "noun",
            difficulty = "beginner"
        )
        assertEquals("w1", item.id)
        assertEquals("apple", item.originalWord)
        assertEquals("蘋果", item.translatedWord)
        assertEquals("píng guǒ", item.pronunciation)
        assertEquals("noun", item.category)
        assertEquals("beginner", item.difficulty)
    }

    @Test
    fun `WordBankItem equality based on all fields`() {
        val a = WordBankItem(id = "w1", originalWord = "cat")
        val b = WordBankItem(id = "w1", originalWord = "cat")
        assertEquals(a, b)
    }

    @Test
    fun `WordBankItem inequality when any field differs`() {
        val a = WordBankItem(id = "w1", originalWord = "cat")
        val b = WordBankItem(id = "w1", originalWord = "dog")
        assertNotEquals(a, b)
    }

    // ── WordBank ───────────────────────────────────────────────────

    @Test
    fun `WordBank has correct defaults`() {
        val wb = WordBank()
        assertEquals("", wb.primaryLanguageCode)
        assertEquals("", wb.targetLanguageCode)
        assertTrue(wb.words.isEmpty())
        assertNull(wb.generatedAt)
        assertEquals(0, wb.historyCountAtGenerate)
    }

    @Test
    fun `WordBank stores language codes and word list`() {
        val words = listOf(
            WordBankItem(id = "1", originalWord = "hello"),
            WordBankItem(id = "2", originalWord = "world")
        )
        val wb = WordBank(
            primaryLanguageCode = "en-US",
            targetLanguageCode = "ja-JP",
            words = words,
            historyCountAtGenerate = 25
        )
        assertEquals("en-US", wb.primaryLanguageCode)
        assertEquals("ja-JP", wb.targetLanguageCode)
        assertEquals(2, wb.words.size)
        assertEquals(25, wb.historyCountAtGenerate)
    }

    // ── WordBankLanguageCluster ────────────────────────────────────

    @Test
    fun `WordBankLanguageCluster stores fields correctly`() {
        val cluster = WordBankLanguageCluster(
            languageCode = "ja-JP",
            recordCount = 42,
            hasWordBank = true
        )
        assertEquals("ja-JP", cluster.languageCode)
        assertEquals(42, cluster.recordCount)
        assertTrue(cluster.hasWordBank)
    }

    @Test
    fun `WordBankLanguageCluster without word bank`() {
        val cluster = WordBankLanguageCluster(
            languageCode = "ko-KR",
            recordCount = 5,
            hasWordBank = false
        )
        assertFalse(cluster.hasWordBank)
    }

    // ── WordBankUiState ────────────────────────────────────────────

    @Test
    fun `WordBankUiState has correct defaults`() {
        val state = WordBankUiState()
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertTrue(state.languageClusters.isEmpty())
        assertNull(state.selectedLanguageCode)
        assertNull(state.currentWordBank)
        assertFalse(state.isGenerating)
        assertFalse(state.isSpeaking)
        assertNull(state.speakingItemId)
        assertNull(state.speakingType)
        assertFalse(state.isCustomWordBankSelected)
        assertEquals(0, state.customWordsCount)
        assertTrue(state.friends.isEmpty())
        assertFalse(state.isSharing)
        assertNull(state.shareSuccess)
        assertNull(state.shareError)
        assertNull(state.pendingShareWord)
        assertNull(state.wordBankGenerationCompleted)
    }

    @Test
    fun `WordBankUiState copy updates generating state`() {
        val state = WordBankUiState()
        val generating = state.copy(isGenerating = true, isLoading = false)
        assertTrue(generating.isGenerating)
        assertFalse(generating.isLoading)
    }

    @Test
    fun `WordBankUiState with error clears loading`() {
        val state = WordBankUiState().copy(
            isLoading = false,
            error = "Network error"
        )
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `WordBankUiState speaking state tracks item and type`() {
        val state = WordBankUiState().copy(
            isSpeaking = true,
            speakingItemId = "w1",
            speakingType = SpeakingType.ORIGINAL
        )
        assertTrue(state.isSpeaking)
        assertEquals("w1", state.speakingItemId)
        assertEquals(SpeakingType.ORIGINAL, state.speakingType)
    }

    // ── SpeakingType ───────────────────────────────────────────────

    @Test
    fun `SpeakingType has exactly two values`() {
        assertEquals(2, SpeakingType.entries.size)
    }

    @Test
    fun `SpeakingType values are ORIGINAL and TRANSLATED`() {
        val names = SpeakingType.entries.map { it.name }
        assertTrue(names.contains("ORIGINAL"))
        assertTrue(names.contains("TRANSLATED"))
    }
}

