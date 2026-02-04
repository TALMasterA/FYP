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