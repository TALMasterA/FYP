package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.TranslationRepository
import javax.inject.Inject

/**
 * Use case for batch translating multiple texts at once.
 * More efficient than individual translations - reduces API calls significantly.
 *
 * Usage:
 * ```
 * val result = translateBatchUseCase(
 *     listOf("Hello", "World", "How are you?"),
 *     fromLanguage = "en",
 *     toLanguage = "ja"
 * )
 * result.onSuccess { translations ->
 *     // translations["Hello"] = "こんにちは"
 *     // translations["World"] = "世界"
 *     // ...
 * }
 * ```
 */
class TranslateBatchUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    /**
     * Translate multiple texts from one language to another.
     * Uses caching to avoid redundant API calls.
     *
     * @param texts List of texts to translate
     * @param fromLanguage Source language code (e.g., "en", "en-US")
     * @param toLanguage Target language code (e.g., "ja", "zh-Hans")
     * @return Map of original text -> translated text
     */
    suspend operator fun invoke(
        texts: List<String>,
        fromLanguage: String,
        toLanguage: String
    ): Result<Map<String, String>> {
        return repository.translateBatch(texts, fromLanguage, toLanguage)
    }
}
