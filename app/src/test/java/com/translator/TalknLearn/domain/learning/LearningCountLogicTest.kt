package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for the learning count logic.
 * 
 * Requirement: Count user ALL translate records.
 * E.g. A record: English → Cantonese = English count +1, Cantonese count +1
 * Except the primary language, show all other languages in learning screen.
 */
class LearningCountLogicTest {

    /**
     * Simulate counting logic: each translation record adds +1 to both source and target language.
     */
    private fun countLanguagesFromRecords(
        records: List<Pair<String, String>>, // (sourceLang, targetLang)
        primaryLanguage: String
    ): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()

        records.forEach { (source, target) ->
            counts[source] = (counts[source] ?: 0) + 1
            counts[target] = (counts[target] ?: 0) + 1
        }

        // Remove primary language from display
        counts.remove(primaryLanguage)
        return counts
    }

    @Test
    fun `each record increments both source and target language counts`() {
        val records = listOf(
            "en-US" to "zh-HK",  // English → Cantonese
        )
        val counts = countLanguagesFromRecords(records, "none")

        assertEquals(1, counts["en-US"])
        assertEquals(1, counts["zh-HK"])
    }

    @Test
    fun `multiple records accumulate counts`() {
        val records = listOf(
            "en-US" to "zh-HK",  // English +1, Cantonese +1
            "en-US" to "ja",     // English +1, Japanese +1
            "zh-HK" to "ja",    // Cantonese +1, Japanese +1
        )
        val counts = countLanguagesFromRecords(records, "none")

        assertEquals(2, counts["en-US"])  // 2 records involving English
        assertEquals(2, counts["zh-HK"]) // 2 records involving Cantonese
        assertEquals(2, counts["ja"])    // 2 records involving Japanese
    }

    @Test
    fun `primary language is excluded from learning screen`() {
        val records = listOf(
            "en-US" to "zh-HK",
            "en-US" to "ja",
            "en-US" to "es-ES",
        )
        val counts = countLanguagesFromRecords(records, "en-US")

        assertNull("Primary language should not appear", counts["en-US"])
        assertEquals(1, counts["zh-HK"])
        assertEquals(1, counts["ja"])
        assertEquals(1, counts["es-ES"])
    }

    @Test
    fun `same language pair counted correctly for multiple records`() {
        val records = listOf(
            "en-US" to "zh-HK",
            "en-US" to "zh-HK",
            "en-US" to "zh-HK",
        )
        val counts = countLanguagesFromRecords(records, "en-US")

        assertEquals(3, counts["zh-HK"])
    }

    @Test
    fun `empty records produces empty counts`() {
        val counts = countLanguagesFromRecords(emptyList(), "en-US")
        assertTrue(counts.isEmpty())
    }

    @Test
    fun `learning cards show count next to language name`() {
        val records = listOf(
            "en-US" to "zh-HK",
            "en-US" to "zh-HK",
            "en-US" to "ja",
        )
        val counts = countLanguagesFromRecords(records, "en-US")

        // Cantonese card should show (2)
        assertEquals(2, counts["zh-HK"])
        // Japanese card should show (1)
        assertEquals(1, counts["ja"])
    }

    // ── Primary Language Change Cooldown ──

    @Test
    fun `primary language change cooldown is 30 days`() {
        assertEquals(
            30L * 24 * 60 * 60 * 1000,
            UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS
        )
    }

    @Test
    fun `first primary language change is always allowed`() {
        assertTrue(
            UserSettings.canChangePrimaryLanguage(0L, System.currentTimeMillis())
        )
    }

    @Test
    fun `primary language change within cooldown is blocked`() {
        val now = System.currentTimeMillis()
        val fiveDaysAgo = now - (5L * 24 * 60 * 60 * 1000)

        assertFalse(
            UserSettings.canChangePrimaryLanguage(fiveDaysAgo, now)
        )
    }
}
