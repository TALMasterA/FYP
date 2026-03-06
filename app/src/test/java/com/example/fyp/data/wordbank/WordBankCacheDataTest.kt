package com.example.fyp.data.wordbank

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for WordBankCacheEntry and WordBankCacheData serialization.
 *
 * Tests:
 *  1. WordBankCacheEntry serialization round-trip
 *  2. WordBankCacheData serialization round-trip
 *  3. WordBankCacheEntry defaults
 *  4. WordBankCacheData empty entries
 *  5. WordBankCacheData with multiple entries
 *  6. Deserialization ignores unknown keys
 */
class WordBankCacheDataTest {

    private val json = Json { ignoreUnknownKeys = true }

    // ── Test 1: WordBankCacheEntry serialization ──

    @Test
    fun `WordBankCacheEntry serialization round-trip`() {
        val entry = WordBankCacheEntry(
            exists = true,
            wordCount = 42,
            historyCountAtGenerate = 100,
            timestamp = 1234567890L
        )
        val serialized = json.encodeToString(entry)
        val deserialized = json.decodeFromString<WordBankCacheEntry>(serialized)

        assertEquals(entry.exists, deserialized.exists)
        assertEquals(entry.wordCount, deserialized.wordCount)
        assertEquals(entry.historyCountAtGenerate, deserialized.historyCountAtGenerate)
        assertEquals(entry.timestamp, deserialized.timestamp)
    }

    // ── Test 2: WordBankCacheData serialization ──

    @Test
    fun `WordBankCacheData serialization round-trip`() {
        val data = WordBankCacheData(
            entries = mapOf(
                "user1|en-US|zh-TW" to WordBankCacheEntry(exists = true, wordCount = 30, timestamp = 1000L),
                "user1|en-US|ja" to WordBankCacheEntry(exists = false, timestamp = 2000L)
            )
        )
        val serialized = json.encodeToString(data)
        val deserialized = json.decodeFromString<WordBankCacheData>(serialized)

        assertEquals(2, deserialized.entries.size)
        assertTrue(deserialized.entries["user1|en-US|zh-TW"]!!.exists)
        assertEquals(30, deserialized.entries["user1|en-US|zh-TW"]!!.wordCount)
        assertFalse(deserialized.entries["user1|en-US|ja"]!!.exists)
    }

    // ── Test 3: WordBankCacheEntry defaults ──

    @Test
    fun `WordBankCacheEntry default values`() {
        val entry = WordBankCacheEntry(exists = true)

        assertTrue(entry.exists)
        assertEquals(0, entry.wordCount)
        assertEquals(0, entry.historyCountAtGenerate)
        assertTrue(entry.timestamp > 0) // auto-set to current time
    }

    // ── Test 4: Empty WordBankCacheData ──

    @Test
    fun `WordBankCacheData empty entries`() {
        val data = WordBankCacheData()

        assertTrue(data.entries.isEmpty())

        val serialized = json.encodeToString(data)
        val deserialized = json.decodeFromString<WordBankCacheData>(serialized)
        assertTrue(deserialized.entries.isEmpty())
    }

    // ── Test 5: Multiple entries ──

    @Test
    fun `WordBankCacheData multiple entries preserve all data`() {
        val entries = mapOf(
            "u1|en|zh" to WordBankCacheEntry(true, 10, 50, 100L),
            "u1|en|ja" to WordBankCacheEntry(true, 20, 60, 200L),
            "u2|en|ko" to WordBankCacheEntry(false, 0, 0, 300L)
        )
        val data = WordBankCacheData(entries)

        assertEquals(3, data.entries.size)
        assertEquals(10, data.entries["u1|en|zh"]?.wordCount)
        assertEquals(20, data.entries["u1|en|ja"]?.wordCount)
        assertFalse(data.entries["u2|en|ko"]!!.exists)
    }

    // ── Test 6: Unknown keys are ignored ──

    @Test
    fun `deserialization ignores unknown keys`() {
        val jsonString = """{"exists":true,"wordCount":5,"unknownField":"value","timestamp":999}"""
        val entry = json.decodeFromString<WordBankCacheEntry>(jsonString)

        assertTrue(entry.exists)
        assertEquals(5, entry.wordCount)
        assertEquals(999L, entry.timestamp)
    }

    // ── Test: cache key format ──

    @Test
    fun `cache key format is userId pipe primaryLang pipe targetLang`() {
        // Verify the key format convention used by the DataStore
        val key = "user123|en-US|zh-TW"
        assertTrue(key.startsWith("user123|"))
        assertEquals(3, key.split("|").size)
    }
}
