package com.example.fyp.data.repositories

import com.example.fyp.data.cloud.TranslationCache
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.model.SpeechResult
import javax.inject.Inject

class FirebaseTranslationRepository @Inject constructor(
    private val cloudTranslatorClient: CloudTranslatorClient,
    private val translationCache: TranslationCache
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
            val result = cloudTranslatorClient.detectLanguage(text)
            // Azure returns short codes like "ja", we might need "ja-JP" for some APIs
            // Log the detected language for debugging
            android.util.Log.d("DetectLanguage", "Detected: ${result.language}, score: ${result.score}")
            result
        } catch (e: Exception) {
            android.util.Log.e("DetectLanguage", "Detection failed", e)
            null
        }
    }
}