package com.example.fyp.data.repositories

import com.example.fyp.data.cloud.LanguageDetectionCache
import com.example.fyp.data.cloud.TranslationCache
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.model.SpeechResult
import javax.inject.Inject

class FirebaseTranslationRepository @Inject constructor(
    private val cloudTranslatorClient: CloudTranslatorClient,
    private val translationCache: TranslationCache,
    private val languageDetectionCache: LanguageDetectionCache
) : TranslationRepository {

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
            SpeechResult.Error(e.message ?: "Translation failed")
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
                android.util.Log.d("BatchTranslate", "All ${texts.size} texts found in cache")
                return Result.success(result)
            }

            android.util.Log.d("BatchTranslate", "Cache: ${cacheResult.found.size} hits, ${cacheResult.notFound.size} misses")

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
            android.util.Log.e("BatchTranslate", "Batch translation failed", e)
            Result.failure(e)
        }
    }

    override suspend fun detectLanguage(text: String): DetectedLanguage? {
        return try {
            // Check cache first
            val cached = languageDetectionCache.getCached(text)
            if (cached != null) {
                android.util.Log.d("DetectLanguage", "Cache hit: ${cached.language}, score: ${cached.score}")
                return cached
            }

            // Call API if not cached
            val result = cloudTranslatorClient.detectLanguage(text)
            android.util.Log.d("DetectLanguage", "API call - Detected: ${result.language}, score: ${result.score}")

            // Cache the result
            languageDetectionCache.cache(text, result)

            result
        } catch (e: Exception) {
            android.util.Log.e("DetectLanguage", "Detection failed", e)
            null
        }
    }
}