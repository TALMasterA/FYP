package com.translator.TalknLearn.data.clients

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure parsing logic extracted from CloudTranslatorClient.
 *
 * Covers:
 *  - translateText response parsing
 *  - translateTexts batch response parsing
 *  - detectLanguage response parsing (with alternatives)
 *  - Error handling for malformed responses
 *  - from parameter null/blank handling
 */
class CloudTranslatorLogicTest {

    // ── translateText response parsing ───────────────────────────────────────

    /**
     * Replicates the response parsing from CloudTranslatorClient.translateText:
     * map["translatedText"] as? String ?: throw
     */
    private fun parseTranslateResponse(data: Any?): String {
        @Suppress("UNCHECKED_CAST")
        val map = data as? Map<String, Any?>
            ?: throw IllegalStateException("Unexpected result type: $data")
        return map["translatedText"] as? String
            ?: throw IllegalStateException("Missing translatedText in result")
    }

    @Test
    fun `parseTranslateResponse - extracts translatedText`() {
        val data = mapOf("translatedText" to "こんにちは")
        assertEquals("こんにちは", parseTranslateResponse(data))
    }

    @Test
    fun `parseTranslateResponse - handles extra fields`() {
        val data = mapOf(
            "translatedText" to "Bonjour",
            "detectedLanguage" to "en",
            "score" to 0.95
        )
        assertEquals("Bonjour", parseTranslateResponse(data))
    }

    @Test(expected = IllegalStateException::class)
    fun `parseTranslateResponse - throws when translatedText missing`() {
        val data = mapOf("otherField" to "value")
        parseTranslateResponse(data)
    }

    @Test(expected = IllegalStateException::class)
    fun `parseTranslateResponse - throws when data is not a map`() {
        parseTranslateResponse("not a map")
    }

    @Test(expected = IllegalStateException::class)
    fun `parseTranslateResponse - throws when data is null`() {
        parseTranslateResponse(null)
    }

    @Test(expected = IllegalStateException::class)
    fun `parseTranslateResponse - throws when translatedText is not string`() {
        val data = mapOf("translatedText" to 42)
        parseTranslateResponse(data)
    }

    // ── translateTexts batch response parsing ────────────────────────────────

    /**
     * Replicates batch response parsing from CloudTranslatorClient.translateTexts:
     * map["translatedTexts"] as? List<*> -> map to String
     */
    private fun parseBatchTranslateResponse(data: Any?): List<String> {
        @Suppress("UNCHECKED_CAST")
        val map = data as? Map<String, Any?>
            ?: throw IllegalStateException("Unexpected result type: $data")
        val list = map["translatedTexts"] as? List<*>
            ?: throw IllegalStateException("Missing translatedTexts in result")
        return list.map { it as? String ?: "" }
    }

    @Test
    fun `parseBatchTranslateResponse - extracts list of translated texts`() {
        val data = mapOf("translatedTexts" to listOf("Hello", "World"))
        assertEquals(listOf("Hello", "World"), parseBatchTranslateResponse(data))
    }

    @Test
    fun `parseBatchTranslateResponse - handles null items as empty strings`() {
        val data = mapOf("translatedTexts" to listOf("Hello", null, "World"))
        assertEquals(listOf("Hello", "", "World"), parseBatchTranslateResponse(data))
    }

    @Test
    fun `parseBatchTranslateResponse - handles empty list`() {
        val data = mapOf("translatedTexts" to emptyList<String>())
        assertTrue(parseBatchTranslateResponse(data).isEmpty())
    }

    @Test(expected = IllegalStateException::class)
    fun `parseBatchTranslateResponse - throws when translatedTexts missing`() {
        val data = mapOf("other" to "value")
        parseBatchTranslateResponse(data)
    }

    @Test(expected = IllegalStateException::class)
    fun `parseBatchTranslateResponse - throws when data is not a map`() {
        parseBatchTranslateResponse(listOf("wrong type"))
    }

    // ── detectLanguage response parsing ──────────────────────────────────────

    /**
     * Replicates the detectLanguage response parsing from CloudTranslatorClient.
     */
    private fun parseDetectLanguageResponse(data: Any?): DetectedLanguage {
        @Suppress("UNCHECKED_CAST")
        val map = data as? Map<String, Any?>
            ?: throw IllegalStateException("Unexpected result type: $data")

        val alternatives = (map["alternatives"] as? List<*>)?.mapNotNull { alt ->
            val altMap = alt as? Map<*, *> ?: return@mapNotNull null
            LanguageAlternative(
                language = altMap["language"] as? String ?: "",
                score = (altMap["score"] as? Number)?.toDouble() ?: 0.0
            )
        } ?: emptyList()

        return DetectedLanguage(
            language = map["language"] as? String ?: "",
            score = (map["score"] as? Number)?.toDouble() ?: 0.0,
            isTranslationSupported = map["isTranslationSupported"] as? Boolean ?: false,
            alternatives = alternatives
        )
    }

    @Test
    fun `parseDetectLanguageResponse - parses full response`() {
        val data = mapOf(
            "language" to "en",
            "score" to 0.95,
            "isTranslationSupported" to true,
            "alternatives" to listOf(
                mapOf("language" to "de", "score" to 0.03),
                mapOf("language" to "fr", "score" to 0.02)
            )
        )
        val result = parseDetectLanguageResponse(data)

        assertEquals("en", result.language)
        assertEquals(0.95, result.score, 0.001)
        assertTrue(result.isTranslationSupported)
        assertEquals(2, result.alternatives.size)
        assertEquals("de", result.alternatives[0].language)
        assertEquals(0.03, result.alternatives[0].score, 0.001)
    }

    @Test
    fun `parseDetectLanguageResponse - handles missing alternatives`() {
        val data = mapOf(
            "language" to "ja",
            "score" to 0.88,
            "isTranslationSupported" to true
        )
        val result = parseDetectLanguageResponse(data)

        assertEquals("ja", result.language)
        assertTrue(result.alternatives.isEmpty())
    }

    @Test
    fun `parseDetectLanguageResponse - handles missing isTranslationSupported`() {
        val data = mapOf("language" to "zh", "score" to 0.7)
        val result = parseDetectLanguageResponse(data)

        assertFalse(result.isTranslationSupported)
    }

    @Test
    fun `parseDetectLanguageResponse - handles Integer score (Firestore coercion)`() {
        val data = mapOf("language" to "en", "score" to 1)
        val result = parseDetectLanguageResponse(data)

        assertEquals(1.0, result.score, 0.001)
    }

    @Test
    fun `parseDetectLanguageResponse - handles Long score`() {
        val data = mapOf("language" to "en", "score" to 1L)
        val result = parseDetectLanguageResponse(data)

        assertEquals(1.0, result.score, 0.001)
    }

    @Test
    fun `parseDetectLanguageResponse - handles null alternative entries`() {
        val data = mapOf(
            "language" to "en",
            "score" to 0.9,
            "isTranslationSupported" to true,
            "alternatives" to listOf(
                mapOf("language" to "fr", "score" to 0.05),
                "not a map",  // should be filtered out by mapNotNull
                null           // should be filtered out
            )
        )
        val result = parseDetectLanguageResponse(data)

        assertEquals(1, result.alternatives.size)
        assertEquals("fr", result.alternatives[0].language)
    }

    @Test
    fun `parseDetectLanguageResponse - empty map gives defaults`() {
        val data = emptyMap<String, Any?>()
        val result = parseDetectLanguageResponse(data)

        assertEquals("", result.language)
        assertEquals(0.0, result.score, 0.001)
        assertFalse(result.isTranslationSupported)
        assertTrue(result.alternatives.isEmpty())
    }

    @Test(expected = IllegalStateException::class)
    fun `parseDetectLanguageResponse - throws when data is not a map`() {
        parseDetectLanguageResponse("not a map")
    }

    // ── from parameter handling ──────────────────────────────────────────────

    /**
     * Replicates the "from" parameter logic:
     * if (!from.isNullOrBlank()) data["from"] = from
     */
    private fun buildRequestData(text: String, from: String?, to: String): Map<String, Any> {
        val data = hashMapOf<String, Any>("text" to text, "to" to to)
        if (!from.isNullOrBlank()) data["from"] = from
        return data
    }

    @Test
    fun `buildRequestData - includes from when provided`() {
        val data = buildRequestData("Hello", "en", "ja")
        assertEquals("en", data["from"])
    }

    @Test
    fun `buildRequestData - excludes from when null`() {
        val data = buildRequestData("Hello", null, "ja")
        assertFalse(data.containsKey("from"))
    }

    @Test
    fun `buildRequestData - excludes from when empty`() {
        val data = buildRequestData("Hello", "", "ja")
        assertFalse(data.containsKey("from"))
    }

    @Test
    fun `buildRequestData - excludes from when blank`() {
        val data = buildRequestData("Hello", "   ", "ja")
        assertFalse(data.containsKey("from"))
    }

    @Test
    fun `buildRequestData - always includes text and to`() {
        val data = buildRequestData("Test text", null, "fr")
        assertEquals("Test text", data["text"])
        assertEquals("fr", data["to"])
    }

    // ── SpeechTokenResponse parsing ──────────────────────────────────────────

    /**
     * Replicates CloudSpeechTokenClient.getSpeechToken parsing logic.
     */
    private fun parseSpeechTokenResponse(data: Any?): SpeechTokenResponse {
        @Suppress("UNCHECKED_CAST")
        val map = data as? Map<*, *>
            ?: throw IllegalStateException("Unexpected result type: $data")
        val token = map["token"] as? String
            ?: throw IllegalStateException("Missing token in result")
        val region = map["region"] as? String
            ?: throw IllegalStateException("Missing region in result")
        return SpeechTokenResponse(token = token, region = region)
    }

    @Test
    fun `parseSpeechTokenResponse - extracts token and region`() {
        val data = mapOf("token" to "abc123", "region" to "eastus")
        val result = parseSpeechTokenResponse(data)
        assertEquals("abc123", result.token)
        assertEquals("eastus", result.region)
    }

    @Test(expected = IllegalStateException::class)
    fun `parseSpeechTokenResponse - throws when token missing`() {
        val data = mapOf("region" to "eastus")
        parseSpeechTokenResponse(data)
    }

    @Test(expected = IllegalStateException::class)
    fun `parseSpeechTokenResponse - throws when region missing`() {
        val data = mapOf("token" to "abc123")
        parseSpeechTokenResponse(data)
    }

    @Test(expected = IllegalStateException::class)
    fun `parseSpeechTokenResponse - throws when data is not a map`() {
        parseSpeechTokenResponse("string")
    }

    @Test
    fun `parseSpeechTokenResponse - handles extra fields`() {
        val data = mapOf("token" to "xyz", "region" to "westus2", "expires" to 3600)
        val result = parseSpeechTokenResponse(data)
        assertEquals("xyz", result.token)
        assertEquals("westus2", result.region)
    }
}
