package com.example.fyp.data.repositories

import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.cloud.BatchCacheResult
import com.example.fyp.data.cloud.LanguageDetectionCache
import com.example.fyp.data.cloud.TranslationCache
import com.example.fyp.model.SpeechResult
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
        val cachedTranslation = "Hola (cached)"

        translationCache.stub {
            onBlocking { getCached(text, from, to) } doReturn cachedTranslation
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Success)
        assertEquals(cachedTranslation, (result as SpeechResult.Success).text)
        verify(translationCache).getCached(text, from, to)
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `translate calls API when cache misses and caches result`() = runTest {
        val text = "Hello"
        val from = "en"
        val to = "es"
        val apiTranslation = "Hola"

        translationCache.stub {
            onBlocking { getCached(text, from, to) } doReturn null
        }

        cloudClient.stub {
            onBlocking { translateText(text, from, to) } doReturn apiTranslation
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Success)
        assertEquals(apiTranslation, (result as SpeechResult.Success).text)
        verify(translationCache).getCached(text, from, to)
        verify(cloudClient).translateText(text, from, to)
        verify(translationCache).cache(text, apiTranslation, from, to)
    }

    @Test
    fun `translate handles API errors gracefully`() = runTest {
        val text = "Hello"
        val from = "en"
        val to = "es"
        val exception = RuntimeException("Network error")

        translationCache.stub {
            onBlocking { getCached(text, from, to) } doReturn null
        }

        cloudClient.stub {
            onBlocking { translateText(text, from, to) } doThrow exception
        }

        val result = repository.translate(text, from, to)

        assertTrue(result is SpeechResult.Error)
        assertTrue((result as SpeechResult.Error).message.contains("Translation failed") ||
                   result.message.contains("Network error"))
    }

    @Test
    fun `translateBatch translates multiple texts correctly`() = runTest {
        val texts = listOf("Hello", "Goodbye", "Thank you")
        val from = "en"
        val to = "es"
        val translations = listOf("Hola", "Adiós", "Gracias")
        val cacheResult = BatchCacheResult(
            found = emptyMap(),
            notFound = texts
        )

        translationCache.stub {
            onBlocking { getBatchCached(texts, from, to) } doReturn cacheResult
        }

        cloudClient.stub {
            onBlocking { translateTexts(texts, from, to) } doReturn translations
        }

        val result = repository.translateBatch(texts, from, to)

        assertTrue(result.isSuccess)
        val resultMap = result.getOrNull()
        assertNotNull(resultMap)
        assertEquals(3, resultMap!!.size)
        verify(cloudClient).translateTexts(texts, from, to)
    }

    @Test
    fun `detectLanguage returns cached result when available`() = runTest {
        val text = "Bonjour"
        val cachedDetection = com.example.fyp.data.clients.DetectedLanguage(
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
        val apiDetection = com.example.fyp.data.clients.DetectedLanguage(
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

