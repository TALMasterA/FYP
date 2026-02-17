package com.example.fyp.domain.friends

import com.example.fyp.data.repositories.TranslationRepository
import com.example.fyp.model.friends.FriendMessage
import javax.inject.Inject

/**
 * Use case for batch translating all messages in a chat conversation.
 * Detects the language of each message and translates them to the target language.
 */
class TranslateAllMessagesUseCase @Inject constructor(
    private val translationRepository: TranslationRepository
) {
    /**
     * Translates all messages to the target language.
     * Automatically detects source language for each message.
     * 
     * @param messages List of messages to translate
     * @param targetLanguage Target language code (e.g., "en", "ja", "zh-Hans")
     * @return Map of message content -> translated content
     */
    suspend operator fun invoke(
        messages: List<FriendMessage>,
        targetLanguage: String
    ): Result<Map<String, String>> {
        return try {
            // Extract unique message contents
            val uniqueTexts = messages.map { it.content }.distinct()
            
            if (uniqueTexts.isEmpty()) {
                return Result.success(emptyMap())
            }
            
            // Use batch translation for efficiency
            // The API will auto-detect source language
            translationRepository.translateBatch(
                texts = uniqueTexts,
                fromLanguage = "", // Empty means auto-detect
                toLanguage = targetLanguage
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
