package com.translator.TalknLearn.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CustomWord data class.
 */
class CustomWordTest {

    @Test
    fun `default CustomWord has empty fields`() {
        val word = CustomWord()

        assertEquals("", word.id)
        assertEquals("", word.userId)
        assertEquals("", word.originalWord)
        assertEquals("", word.translatedWord)
        assertEquals("", word.pronunciation)
        assertEquals("", word.example)
        assertEquals("", word.sourceLang)
        assertEquals("", word.targetLang)
    }

    @Test
    fun `CustomWord with all fields set`() {
        val word = CustomWord(
            id = "w1",
            userId = "user1",
            originalWord = "hello",
            translatedWord = "こんにちは",
            pronunciation = "konnichiwa",
            example = "Hello, how are you?",
            sourceLang = "en-US",
            targetLang = "ja"
        )

        assertEquals("w1", word.id)
        assertEquals("user1", word.userId)
        assertEquals("hello", word.originalWord)
        assertEquals("こんにちは", word.translatedWord)
        assertEquals("konnichiwa", word.pronunciation)
        assertEquals("Hello, how are you?", word.example)
        assertEquals("en-US", word.sourceLang)
        assertEquals("ja", word.targetLang)
    }

    @Test
    fun `CustomWord copy works correctly`() {
        val original = CustomWord(id = "w1", originalWord = "hello")
        val copy = original.copy(translatedWord = "hola", targetLang = "es")

        assertEquals("w1", copy.id)
        assertEquals("hello", copy.originalWord)
        assertEquals("hola", copy.translatedWord)
        assertEquals("es", copy.targetLang)
    }

    @Test
    fun `CustomWord equality works by value`() {
        val word1 = CustomWord(id = "w1", originalWord = "hello", translatedWord = "hola")
        val word2 = CustomWord(id = "w1", originalWord = "hello", translatedWord = "hola")

        assertEquals(word1, word2)
    }

    @Test
    fun `CustomWord inequality on different fields`() {
        val word1 = CustomWord(id = "w1", originalWord = "hello")
        val word2 = CustomWord(id = "w2", originalWord = "hello")

        assertNotEquals(word1, word2)
    }

    @Test
    fun `CustomWord handles unicode characters`() {
        val word = CustomWord(
            originalWord = "你好",
            translatedWord = "Привет",
            pronunciation = "nǐ hǎo"
        )

        assertEquals("你好", word.originalWord)
        assertEquals("Привет", word.translatedWord)
        assertEquals("nǐ hǎo", word.pronunciation)
    }

    @Test
    fun `CustomWord handles long example text`() {
        val longExample = "A".repeat(1000)
        val word = CustomWord(example = longExample)

        assertEquals(1000, word.example.length)
    }
}
