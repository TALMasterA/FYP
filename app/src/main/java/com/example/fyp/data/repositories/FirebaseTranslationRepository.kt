package com.example.fyp.data.repositories

import com.example.fyp.core.AppLogger
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.data.cloud.LanguageDetectionCache
import com.example.fyp.data.cloud.TranslationCache
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.model.SpeechResult
import com.example.fyp.utils.ErrorMessageMapper
import com.google.firebase.functions.FirebaseFunctionsException
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

    private fun canonicalLanguageCode(code: String): String {
        val normalized = code.trim().replace('_', '-')
        if (normalized.isEmpty() || normalized.equals("auto", ignoreCase = true)) {
            return ""
        }

        val mapped = LanguageDisplayNames.mapDetectedToSupportedCode(normalized)
        return if (LanguageDisplayNames.isSupportedLanguage(mapped)) mapped else ""
    }

    private fun resolveTargetLanguageCode(code: String): String? {
        val canonical = canonicalLanguageCode(code)
        return canonical.takeIf { it.isNotBlank() }
    }

    private fun isExpectedTranslationFailure(error: Throwable): Boolean {
        if (error is FirebaseFunctionsException) {
            return when (error.code) {
                FirebaseFunctionsException.Code.INVALID_ARGUMENT,
                FirebaseFunctionsException.Code.UNAUTHENTICATED,
                FirebaseFunctionsException.Code.PERMISSION_DENIED,
                FirebaseFunctionsException.Code.FAILED_PRECONDITION -> true
                else -> false
            }
        }

        val message = error.message?.lowercase().orEmpty()
        return message.contains("permission_denied") ||
            message.contains("unauthenticated") ||
            message.contains("invalid-argument") ||
            message.contains("must be one of:") ||
            message.contains("resource-exhausted") ||
            message.contains("rate limit") ||
            message.contains("too many requests") ||
            message.contains("unavailable") ||
            message.contains("deadline exceeded")
    }

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
        val sourceText = text.trim()
        if (sourceText.isEmpty()) {
            return SpeechResult.Success("")
        }

        val requestFromLanguage = canonicalLanguageCode(fromLanguage)
        val requestToLanguage = resolveTargetLanguageCode(toLanguage)
            ?: return SpeechResult.Error("Selected language is not supported.")

        if (requestFromLanguage.isNotEmpty() && requestFromLanguage == requestToLanguage) {
            return SpeechResult.Success(sourceText)
        }

        return try {
            // Check cache first
            val cached = translationCache.getCached(sourceText, requestFromLanguage, requestToLanguage)
            if (cached != null) {
                return SpeechResult.Success(cached)
            }

            // Call API if not cached
            val result = cloudTranslatorClient.translateText(
                text = sourceText,
                from = requestFromLanguage,
                to = requestToLanguage
            )

            // Cache the result (fire-and-forget to avoid blocking the caller)
            translationCache.cache(sourceText, result.translatedText, requestFromLanguage, requestToLanguage)

            SpeechResult.Success(
                text = result.translatedText,
                detectedLanguage = result.detectedLanguage,
                detectedScore = result.detectedScore
            )
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

            val requestFromLanguage = canonicalLanguageCode(fromLanguage)
            val requestToLanguage = resolveTargetLanguageCode(toLanguage)
                ?: return Result.failure(IllegalArgumentException("Unsupported target language code: $toLanguage"))

            val cleanedTexts = texts.map { it.trim() }.filter { it.isNotEmpty() }
            if (cleanedTexts.isEmpty()) {
                return Result.success(emptyMap())
            }

            if (requestFromLanguage.isNotEmpty() && requestFromLanguage == requestToLanguage) {
                return Result.success(cleanedTexts.associateWith { it })
            }

            // Check cache for already translated texts
            val cacheResult = translationCache.getBatchCached(cleanedTexts, requestFromLanguage, requestToLanguage)
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
                from = requestFromLanguage,
                to = requestToLanguage
            )

            // Map results back to source texts
            val newTranslations = mutableMapOf<String, String>()
            cacheResult.notFound.forEachIndexed { index, sourceText ->
                val translated = apiTranslations.getOrElse(index) { "" }
                result[sourceText] = translated
                newTranslations[sourceText] = translated
            }

            // Cache new translations in batch
            translationCache.cacheBatch(newTranslations, requestFromLanguage, requestToLanguage)

            Result.success(result)
        } catch (e: Exception) {
            if (isExpectedTranslationFailure(e)) {
                AppLogger.w("BatchTranslate", "Batch translation rejected by upstream validation/auth", e)
            } else {
                AppLogger.e("BatchTranslate", "Batch translation failed", e)
            }
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
        val sourceText = text.trim()
        if (sourceText.isBlank()) return null

        return try {
            // Check cache first
            val cached = languageDetectionCache.getCached(sourceText)
            if (cached != null) {
                AppLogger.d("DetectLanguage", "Cache hit: ${cached.language}, score: ${cached.score}")
                return cached
            }

            // Call API if not cached
            val result = cloudTranslatorClient.detectLanguage(sourceText)
            AppLogger.d("DetectLanguage", "API call - Detected: ${result.language}, score: ${result.score}")

            // Cache the result
            languageDetectionCache.cache(sourceText, result)

            result
        } catch (e: Exception) {
            if (isExpectedTranslationFailure(e)) {
                AppLogger.w("DetectLanguage", "Detection rejected by upstream validation/auth", e)
            } else {
                AppLogger.e("DetectLanguage", "Detection failed", e)
            }
            null
        }
    }
}