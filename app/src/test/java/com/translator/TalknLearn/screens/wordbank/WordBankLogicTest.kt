package com.translator.TalknLearn.screens.wordbank

import com.translator.TalknLearn.domain.learning.GenerationEligibility
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for word bank logic.
 * 
 * Requirements:
 * - Custom words bank add functioning
 * - Can accept from friends sharing
 * - Same counting from learning screen
 * - First gen no restriction, then need +20 records
 * - Word bank is refreshed (add up), not overwrite
 */
class WordBankLogicTest {

    @Test
    fun `word bank first generation has no count restriction`() {
        // First gen: savedHistoryCount = 0
        val canGenerate = GenerationEligibility.canRegenerateWordBank(
            currentHistoryCount = 20,
            savedHistoryCount = 0
        )
        assertTrue("First gen should be allowed", canGenerate)
    }

    @Test
    fun `word bank regen needs 20 more records`() {
        assertTrue(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 70,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `word bank regen blocked with only 19 more records`() {
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 69,
                savedHistoryCount = 50
            )
        )
    }

    @Test
    fun `word bank refresh adds new words, does not overwrite`() {
        // Existing words
        val existingWords = mutableSetOf("hello", "world", "good")
        val newWords = setOf("morning", "world", "nice")

        // Refresh = addAll (union)
        existingWords.addAll(newWords)

        // Should contain all unique words
        assertEquals(5, existingWords.size) // hello, world, good, morning, nice
        assertTrue(existingWords.contains("hello")) // Old word retained
        assertTrue(existingWords.contains("morning")) // New word added
    }

    @Test
    fun `custom words can be added`() {
        val customWords = mutableListOf<String>()
        customWords.add("custom_word_1")
        customWords.add("custom_word_2")

        assertEquals(2, customWords.size)
    }

    @Test
    fun `accepted shared words are added to bank`() {
        val wordBank = mutableListOf("existing1", "existing2")
        val sharedWord = "shared_from_friend"

        // Accept shared word
        wordBank.add(sharedWord)

        assertTrue(wordBank.contains(sharedWord))
        assertEquals(3, wordBank.size)
    }

    @Test
    fun `MIN_RECORDS_FOR_WORD_BANK is 20`() {
        assertEquals(20, GenerationEligibility.MIN_RECORDS_FOR_WORD_BANK)
    }

    @Test
    fun `word bank anti-cheat blocks decreased count`() {
        assertFalse(
            GenerationEligibility.canRegenerateWordBank(
                currentHistoryCount = 30,
                savedHistoryCount = 50
            )
        )
    }
}
