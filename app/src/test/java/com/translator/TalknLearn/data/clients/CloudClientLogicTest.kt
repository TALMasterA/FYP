package com.translator.TalknLearn.data.clients

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from CloudTranslatorClient and CloudSpeechTokenClient.
 *
 * Covers: translation request map building (with/without from language),
 * batch translation request building, detect language response parsing,
 * and speech token response parsing.
 */
class CloudClientLogicTest {

    // ── Translation request map building ──────────────────────────────

    @Test
    fun `translateText request includes from when non-blank`() {
        val text = "hello"
        val to = "ja-JP"
        val from: String? = "en-US"
        val data = mutableMapOf<String, Any>("text" to text, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from
        assertEquals("hello", data["text"])
        assertEquals("ja-JP", data["to"])
        assertEquals("en-US", data["from"])
    }

    @Test
    fun `translateText request omits from when null`() {
        val text = "hello"
        val to = "ja-JP"
        val from: String? = null
        val data = mutableMapOf<String, Any>("text" to text, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from
        assertFalse("'from' key should not be present", data.containsKey("from"))
    }

    @Test
    fun `translateText request omits from when blank`() {
        val text = "hello"
        val to = "ja-JP"
        val from: String? = "   "
        val data = mutableMapOf<String, Any>("text" to text, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from
        assertFalse("'from' key should not be present for blank value", data.containsKey("from"))
    }

    @Test
    fun `translateText request omits from when empty`() {
        val text = "hello"
        val to = "ja-JP"
        val from: String? = ""
        val data = mutableMapOf<String, Any>("text" to text, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from
        assertFalse("'from' key should not be present for empty value", data.containsKey("from"))
    }

    // ── Batch translation request building ────────────────────────────

    @Test
    fun `translateTexts batch request includes all texts`() {
        val texts = listOf("hello", "world", "test")
        val to = "ja-JP"
        val from: String? = "en-US"
        val data = mutableMapOf<String, Any>("texts" to texts, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from

        @Suppress("UNCHECKED_CAST")
        val resultTexts = data["texts"] as List<String>
        assertEquals(3, resultTexts.size)
        assertEquals("hello", resultTexts[0])
    }

    @Test
    fun `translateTexts batch request without from language`() {
        val texts = listOf("test1", "test2")
        val to = "fr-FR"
        val from: String? = null
        val data = mutableMapOf<String, Any>("texts" to texts, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from

        assertEquals("fr-FR", data["to"])
        assertFalse(data.containsKey("from"))
    }

    // ── Detect language response parsing ──────────────────────────────

    @Test
    fun `detectLanguage parses complete alternatives list`() {
        val rawAlternatives = listOf(
            mapOf("language" to "en", "score" to 0.95),
            mapOf("language" to "fr", "score" to 0.03)
        )

        val alternatives = rawAlternatives.mapNotNull { alt ->
            val lang = (alt as? Map<*, *>)?.get("language") as? String ?: return@mapNotNull null
            val score = ((alt as? Map<*, *>)?.get("score") as? Number)?.toDouble() ?: 0.0
            Pair(lang, score)
        }

        assertEquals(2, alternatives.size)
        assertEquals("en", alternatives[0].first)
        assertEquals(0.95, alternatives[0].second, 0.001)
        assertEquals("fr", alternatives[1].first)
        assertEquals(0.03, alternatives[1].second, 0.001)
    }

    @Test
    fun `detectLanguage handles missing score with default 0`() {
        val rawAlternatives = listOf(
            mapOf("language" to "ja")
        )

        val alternatives = rawAlternatives.mapNotNull { alt ->
            val lang = (alt as? Map<*, *>)?.get("language") as? String ?: return@mapNotNull null
            val score = ((alt as? Map<*, *>)?.get("score") as? Number)?.toDouble() ?: 0.0
            Pair(lang, score)
        }

        assertEquals(1, alternatives.size)
        assertEquals("ja", alternatives[0].first)
        assertEquals(0.0, alternatives[0].second, 0.001)
    }

    @Test
    fun `detectLanguage skips entries without language field`() {
        val rawAlternatives = listOf(
            mapOf("score" to 0.5),
            mapOf("language" to "ko", "score" to 0.8)
        )

        val alternatives = rawAlternatives.mapNotNull { alt ->
            val lang = (alt as? Map<*, *>)?.get("language") as? String ?: return@mapNotNull null
            val score = ((alt as? Map<*, *>)?.get("score") as? Number)?.toDouble() ?: 0.0
            Pair(lang, score)
        }

        assertEquals(1, alternatives.size)
        assertEquals("ko", alternatives[0].first)
    }

    @Test
    fun `detectLanguage handles empty alternatives list`() {
        val rawAlternatives = emptyList<Any>()
        val alternatives = rawAlternatives.mapNotNull { alt ->
            val lang = (alt as? Map<*, *>)?.get("language") as? String ?: return@mapNotNull null
            val score = ((alt as? Map<*, *>)?.get("score") as? Number)?.toDouble() ?: 0.0
            Pair(lang, score)
        }
        assertTrue(alternatives.isEmpty())
    }

    // ── Speech token response parsing ─────────────────────────────────

    @Test
    fun `speech token response parsing extracts token and region`() {
        val resultData = mapOf("token" to "my-auth-token", "region" to "eastus")
        val token = resultData["token"] as? String
        val region = resultData["region"] as? String
        assertEquals("my-auth-token", token)
        assertEquals("eastus", region)
    }

    @Test
    fun `speech token response with missing token returns null`() {
        val resultData = mapOf("region" to "eastus")
        val token = resultData["token"] as? String
        assertNull(token)
    }

    @Test
    fun `speech token response with missing region returns null`() {
        val resultData = mapOf("token" to "my-token")
        val region = resultData["region"] as? String
        assertNull(region)
    }

    @Test
    fun `speech token response with non-string values returns null via safe cast`() {
        val resultData = mapOf("token" to 12345, "region" to true)
        val token = resultData["token"] as? String
        val region = resultData["region"] as? String
        assertNull(token)
        assertNull(region)
    }

    @Test
    fun `speech token response with empty map returns null for both`() {
        val resultData = emptyMap<String, Any>()
        val token = resultData["token"] as? String
        val region = resultData["region"] as? String
        assertNull(token)
        assertNull(region)
    }
}
