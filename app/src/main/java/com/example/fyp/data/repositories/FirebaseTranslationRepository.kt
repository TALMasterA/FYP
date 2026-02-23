package com.example.fyp.data.repositories

import com.example.fyp.core.AppLogger
import com.example.fyp.data.cloud.LanguageDetectionCache
import com.example.fyp.data.cloud.TranslationCache
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.model.SpeechResult
import com.example.fyp.utils.ErrorMessageMapper
import javax.inject.Inject

/**
 * Repository for cloud-based translation services.
 * Implements intelligent caching to reduce API calls and improve performance.
 *
 * Features:
 * - Single and batch translation with automatic caching
 * - Language detection with caching
 * - Efficient cache management to minimize cloud API usage
 *
 * All translation results are cached locally for 30 days to improve
 * responsiveness and reduce costs.
 */
class FirebaseTranslationRepository @Inject constructor(
    private val cloudTranslatorClient: CloudTranslatorClient,
    private val translationCache: TranslationCache,
    private val languageDetectionCache: LanguageDetectionCache
) : TranslationRepository {

    /**
     * Translates text from one language to another.
     * Checks cache first to avoid unnecessary API calls.
     *
     * @param text The text to translate
     * @param fromLanguage Source language code (e.g., "en-US")
     * @param toLanguage Target language code (e.g., "zh-CN")
     * @return SpeechResult.Success with translated text or SpeechResult.Error
     */
    override suspend fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): SpeechResult {
        return try {
            // Check cache first
            val cached = translationCache.getCached(text, fromLanguage, toLanguage)
            if (cached != null) {
                return SpeechResult.Success(cached)
            }

            // Call API if not cached
            val translated = cloudTranslatorClient.translateText(
                text = text,
                from = fromLanguage,
                to = toLanguage
            )

            // Cache the result
            translationCache.cache(text, translated, fromLanguage, toLanguage)

            SpeechResult.Success(translated)
        } catch (e: Exception) {
            SpeechResult.Error(ErrorMessageMapper.mapTranslationError(e.message ?: "Translation failed"))
        }
    }

    /**
     * Batch translate multiple texts efficiently.
     * 1. Checks cache for already translated texts
     * 2. Only calls API for texts not in cache
     * 3. Caches new translations
     *
     * This significantly reduces API calls when translating UI strings
     * or multiple items at once.
     */
    override suspend fun translateBatch(
        texts: List<String>,
        fromLanguage: String,
        toLanguage: String
    ): Result<Map<String, String>> {
        return try {
            if (texts.isEmpty()) {
                return Result.success(emptyMap())
            }

            // Check cache for already translated texts
            val cacheResult = translationCache.getBatchCached(texts, fromLanguage, toLanguage)
            val result = cacheResult.found.toMutableMap()

            // If all texts are cached, return immediately
            if (cacheResult.notFound.isEmpty()) {
                AppLogger.d("BatchTranslate", "All ${texts.size} texts found in cache")
                return Result.success(result)
            }

            AppLogger.d("BatchTranslate", "Cache: ${cacheResult.found.size} hits, ${cacheResult.notFound.size} misses")

            // Call API for texts not in cache
            val apiTranslations = cloudTranslatorClient.translateTexts(
                texts = cacheResult.notFound,
                from = fromLanguage,
                to = toLanguage
            )

            // Map results back to source texts
            val newTranslations = mutableMapOf<String, String>()
            cacheResult.notFound.forEachIndexed { index, sourceText ->
                val translated = apiTranslations.getOrElse(index) { "" }
                result[sourceText] = translated
                newTranslations[sourceText] = translated
            }

            // Cache new translations in batch
            translationCache.cacheBatch(newTranslations, fromLanguage, toLanguage)

            Result.success(result)
        } catch (e: Exception) {
            AppLogger.e("BatchTranslate", "Batch translation failed", e)
            Result.failure(e)
        }
    }

    /**
     * Detects the language of the given text.
     * Results are cached to avoid repeated API calls for the same text.
     *
     * @param text The text to analyze
     * @return DetectedLanguage with language code and confidence score, or null on error
     */
    override suspend fun detectLanguage(text: String): DetectedLanguage? {
        return try {
            // Check cache first
            val cached = languageDetectionCache.getCached(text)
            if (cached != null) {
                AppLogger.d("DetectLanguage", "Cache hit: ${cached.language}, score: ${cached.score}")
                return cached
            }

            // Call API if not cached
            val result = cloudTranslatorClient.detectLanguage(text)
            AppLogger.d("DetectLanguage", "API call - Detected: ${result.language}, score: ${result.score}")

            // Cache the result
            languageDetectionCache.cache(text, result)

            result
        } catch (e: Exception) {
            AppLogger.e("DetectLanguage", "Detection failed", e)
            null
        }
    }
}