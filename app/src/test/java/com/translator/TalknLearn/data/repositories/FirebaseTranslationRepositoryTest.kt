package com.translator.TalknLearn.data.repositories

import com.translator.TalknLearn.data.clients.CloudTranslatorClient
import com.translator.TalknLearn.data.clients.TranslationResult
import com.translator.TalknLearn.data.cloud.BatchCacheResult
import com.translator.TalknLearn.data.cloud.LanguageDetectionCache
import com.translator.TalknLearn.data.cloud.TranslationCache
import com.translator.TalknLearn.model.SpeechResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

/**
 * Integration tests for FirebaseTranslationRepository.
 *
 * Tests:
 * 1. Translation with caching behavior
 * 2. Language detection with cache
 * 3. Batch translation
 * 4. Error handling and fallback mechanisms
 * 5. Cache hit/miss scenarios
 */
class FirebaseTranslationRepositoryTest {

    private lateinit var cloudClient: CloudTranslatorClient
    private lateinit var translationCache: TranslationCache
    private lateinit var detectionCache: LanguageDetectionCache
    private lateinit var repository: FirebaseTranslationRepository

    @Before
    fun setup() {

        cloudClient = mock()
        translationCache = mock()
        detectionCache = mock()

        repository = FirebaseTranslationRepository(
            cloudTranslatorClient = cloudClient,
            translationCache = translationCache,
            languageDetectionCache = detectionCache
        )
    }

    @Test
    fun `translate returns cached result when available`() = runTest {
        val text = "Hello"
        val from = "en"
        val to = "es"
        val canonicalFrom = "en-US"
        val canonicalTo = "es-ES"
        val cachedTranslation = "Hola (cached)"

        translationCache.stub {
            onBlocking { getCached(text, canonicalFrom, canonicalTo) } doReturn cachedTranslation
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Success)
        assertEquals(cachedTranslation, (result as SpeechResult.Success).text)
        verify(translationCache).getCached(text, canonicalFrom, canonicalTo)
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `translate calls API when cache misses and caches result`() = runTest {
        val text = "Hello"
        val from = "en"
        val to = "es"
        val canonicalFrom = "en-US"
        val canonicalTo = "es-ES"
        val apiTranslation = "Hola"

        translationCache.stub {
            onBlocking { getCached(text, canonicalFrom, canonicalTo) } doReturn null
        }

        cloudClient.stub {
            onBlocking { translateText(text, canonicalFrom, canonicalTo) } doReturn TranslationResult(apiTranslation)
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Success)
        assertEquals(apiTranslation, (result as SpeechResult.Success).text)
        verify(translationCache).getCached(text, canonicalFrom, canonicalTo)
        verify(cloudClient).translateText(text, canonicalFrom, canonicalTo)
        verify(translationCache).cache(text, apiTranslation, canonicalFrom, canonicalTo)
    }

    @Test
    fun `translate returns detected language from API when auto-detect`() = runTest {
        val text = "Hello"
        val from = "" // empty = auto-detect
        val to = "es"
        val canonicalTo = "es-ES"
        val apiResult = TranslationResult(
            translatedText = "Hola",
            detectedLanguage = "en",
            detectedScore = 0.98
        )

        translationCache.stub {
            onBlocking { getCached(text, from, canonicalTo) } doReturn null
        }

        cloudClient.stub {
            onBlocking { translateText(text, from, canonicalTo) } doReturn apiResult
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Success)
        val success = result as SpeechResult.Success
        assertEquals("Hola", success.text)
        assertEquals("en", success.detectedLanguage)
        assertEquals(0.98, success.detectedScore)
    }

    @Test
    fun `translate handles API errors gracefully`() = runTest {
        val text = "Hello"
        val from = "en"
        val to = "es"
        val canonicalFrom = "en-US"
        val canonicalTo = "es-ES"
        val exception = RuntimeException("Network error")

        translationCache.stub {
            onBlocking { getCached(text, canonicalFrom, canonicalTo) } doReturn null
        }

        cloudClient.stub {
            onBlocking { translateText(text, canonicalFrom, canonicalTo) } doThrow exception
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Error)
        // mapTranslationError maps "Network error" -> "Network connection failed..."
        // or falls back to "Translation failed. Please try again."
        val errorMessage = (result as SpeechResult.Error).message
        assertTrue(
            "Expected error message to contain network or translation failure text, but was: $errorMessage",
            errorMessage.contains("Network", ignoreCase = true) ||
            errorMessage.contains("Translation failed", ignoreCase = true) ||
            errorMessage.contains("failed", ignoreCase = true)
        )
    }

    @Test
    fun `translateBatch translates multiple texts correctly`() = runTest {
        val texts = listOf("Hello", "Goodbye", "Thank you")
        val from = "en"
        val to = "es"
        val canonicalFrom = "en-US"
        val canonicalTo = "es-ES"
        val translations = listOf("Hola", "Adiós", "Gracias")
        val cacheResult = BatchCacheResult(
            found = emptyMap(),
            notFound = texts
        )

        translationCache.stub {
            onBlocking { getBatchCached(texts, canonicalFrom, canonicalTo) } doReturn cacheResult
        }

        cloudClient.stub {
            onBlocking { translateTexts(texts, canonicalFrom, canonicalTo) } doReturn translations
        }

        val result = repository.translateBatch(texts, from, to)

        assertTrue(result.isSuccess)
        val resultMap = result.getOrNull()
        assertNotNull(resultMap)
        assertEquals(3, resultMap!!.size)
        verify(cloudClient).translateTexts(texts, canonicalFrom, canonicalTo)
    }

    @Test
    fun `detectLanguage returns cached result when available`() = runTest {
        val text = "Bonjour"
        val cachedDetection = com.translator.TalknLearn.data.clients.DetectedLanguage(
            language = "fr",
            score = 0.95,
            isTranslationSupported = true
        )

        detectionCache.stub {
            onBlocking { getCached(text) } doReturn cachedDetection
        }

        val result = repository.detectLanguage(text)

        assertEquals(cachedDetection, result)
        verify(detectionCache).getCached(text)
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `detectLanguage calls API when cache misses and caches result`() = runTest {
        val text = "Bonjour"
        val apiDetection = com.translator.TalknLearn.data.clients.DetectedLanguage(
            language = "fr",
            score = 0.98,
            isTranslationSupported = true
        )

        detectionCache.stub {
            onBlocking { getCached(text) } doReturn null
        }

        cloudClient.stub {
            onBlocking { detectLanguage(text) } doReturn apiDetection
        }

        val result = repository.detectLanguage(text)

        assertEquals(apiDetection, result)
        verify(detectionCache).getCached(text)
        verify(cloudClient).detectLanguage(text)
        verify(detectionCache).cache(text, apiDetection)
    }

    @Test
    fun `detectLanguage returns null on API error`() = runTest {
        val text = "Hello"

        detectionCache.stub {
            onBlocking { getCached(text) } doReturn null
        }

        cloudClient.stub {
            onBlocking { detectLanguage(text) } doThrow RuntimeException("API error")
        }

        val result = repository.detectLanguage(text)

        assertNull(result)
        verify(detectionCache).getCached(text)
        verify(cloudClient).detectLanguage(text)
    }

    @Test
    fun `translateBatch handles API failures gracefully`() = runTest {
        val texts = listOf("Hello", "Goodbye", "Thank you")
        val from = "en"
        val to = "es"
        val cacheResult = BatchCacheResult(
            found = emptyMap(),
            notFound = texts
        )

        translationCache.stub {
            onBlocking { getBatchCached(any(), any(), any()) } doReturn cacheResult
        }

        cloudClient.stub {
            onBlocking { translateTexts(any(), any(), any()) } doThrow RuntimeException("Batch failed")
        }

        val result = repository.translateBatch(texts, from, to)

        assertTrue(result.isFailure)
    }
}

