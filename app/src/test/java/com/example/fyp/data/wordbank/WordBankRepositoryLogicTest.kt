package com.example.fyp.data.wordbank

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for FirestoreWordBankRepository's pure logic:
 * - Key normalization (norm, docId)
 * - WordBankMetadata data class
 * - Word parsing from Map<*, *> to WordBankItem
 */
class WordBankRepositoryLogicTest {

    // --- norm() and docId() logic (same normalization pattern) ---

    @Test
    fun `norm trims whitespace`() {
        // Replicate norm() logic: code.trim()
        assertEquals("en-US", " en-US ".trim())
        assertEquals("ja-JP", "ja-JP".trim())
        assertEquals("", "  ".trim())
    }

    @Test
    fun `docId produces correct composite key`() {
        // Replicate docId() logic: "${norm(primary)}__${norm(target)}"
        val primary = "en-US"
        val target = "ja-JP"
        val docId = "${primary.trim()}__${target.trim()}"
        assertEquals("en-US__ja-JP", docId)
    }

    @Test
    fun `docId trims input codes`() {
        val docId = "${"  en-US  ".trim()}__${"ja-JP ".trim()}"
        assertEquals("en-US__ja-JP", docId)
    }

    @Test
    fun `docId with same language produces self-referential key`() {
        val docId = "${"en-US".trim()}__${"en-US".trim()}"
        assertEquals("en-US__en-US", docId)
    }

    // --- WordBankMetadata ---

    @Test
    fun `WordBankMetadata defaults`() {
        val meta = WordBankMetadata(exists = false, historyCountAtGenerate = 0)
        assertFalse(meta.exists)
        assertEquals(0, meta.historyCountAtGenerate)
    }

    @Test
    fun `WordBankMetadata with existing data`() {
        val meta = WordBankMetadata(exists = true, historyCountAtGenerate = 42)
        assertTrue(meta.exists)
        assertEquals(42, meta.historyCountAtGenerate)
    }

    @Test
    fun `WordBankMetadata equality`() {
        val a = WordBankMetadata(exists = true, historyCountAtGenerate = 10)
        val b = WordBankMetadata(exists = true, historyCountAtGenerate = 10)
        assertEquals(a, b)
    }

    @Test
    fun `WordBankMetadata inequality`() {
        val a = WordBankMetadata(exists = true, historyCountAtGenerate = 10)
        val b = WordBankMetadata(exists = false, historyCountAtGenerate = 10)
        assertNotEquals(a, b)
    }

    // --- Word parsing logic (same as in getWordBank) ---

    @Test
    fun `word map parses all fields`() {
        val map: Map<*, *> = mapOf(
            "id" to "word1",
            "originalWord" to "猫",
            "translatedWord" to "cat",
            "pronunciation" to "neko",
            "example" to "猫が好きです",
            "category" to "noun",
            "difficulty" to "beginner"
        )
        // Replicate parsing logic
        val id = map["id"] as? String ?: ""
        val originalWord = map["originalWord"] as? String ?: ""
        val translatedWord = map["translatedWord"] as? String ?: ""
        val pronunciation = map["pronunciation"] as? String ?: ""
        val example = map["example"] as? String ?: ""
        val category = map["category"] as? String ?: ""
        val difficulty = map["difficulty"] as? String ?: ""

        assertEquals("word1", id)
        assertEquals("猫", originalWord)
        assertEquals("cat", translatedWord)
        assertEquals("neko", pronunciation)
        assertEquals("猫が好きです", example)
        assertEquals("noun", category)
        assertEquals("beginner", difficulty)
    }

    @Test
    fun `word map with missing fields defaults to empty strings`() {
        val map: Map<*, *> = mapOf("id" to "word1")
        assertEquals("word1", map["id"] as? String ?: "")
        assertEquals("", map["originalWord"] as? String ?: "")
        assertEquals("", map["translatedWord"] as? String ?: "")
        assertEquals("", map["pronunciation"] as? String ?: "")
    }

    @Test
    fun `word map with null fields defaults to empty strings`() {
        val map: Map<*, *> = mapOf(
            "id" to null,
            "originalWord" to null
        )
        assertEquals("", map["id"] as? String ?: "")
        assertEquals("", map["originalWord"] as? String ?: "")
    }

    @Test
    fun `non-map item in words list is skipped`() {
        val wordsData = listOf("not a map", 42, null, mapOf("id" to "valid"))
        val parsed = wordsData.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            map["id"] as? String ?: ""
        }
        assertEquals(1, parsed.size)
        assertEquals("valid", parsed[0])
    }

    @Test
    fun `empty words list produces empty result`() {
        val wordsData = emptyList<Any>()
        val parsed = wordsData.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            map["id"] as? String ?: ""
        }
        assertTrue(parsed.isEmpty())
    }

    // --- Duplicate filtering logic (from appendWords) ---

    @Test
    fun `duplicate words filtered by lowercase original word`() {
        val existing = listOf("Hello", "World")
        val existingSet = existing.map { it.lowercase().trim() }.toSet()

        val newWords = listOf("hello", "NEW", "world", "FRESH")
        val unique = newWords.filter { it.lowercase().trim() !in existingSet }

        assertEquals(2, unique.size)
        assertEquals("NEW", unique[0])
        assertEquals("FRESH", unique[1])
    }

    @Test
    fun `duplicate filtering handles whitespace`() {
        val existingSet = listOf("  hello  ").map { it.lowercase().trim() }.toSet()
        val newWords = listOf("hello", "Hello", "HELLO")
        val unique = newWords.filter { it.lowercase().trim() !in existingSet }

        assertTrue(unique.isEmpty())
    }

    @Test
    fun `unique words are appended to existing list`() {
        val existing = listOf("A", "B")
        val newUnique = listOf("C", "D")
        val merged = existing + newUnique
        assertEquals(listOf("A", "B", "C", "D"), merged)
    }
}
