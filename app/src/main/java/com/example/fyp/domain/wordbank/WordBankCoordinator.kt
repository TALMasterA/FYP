package com.example.fyp.domain.wordbank

import com.example.fyp.data.wordbank.FirestoreWordBankRepository
import com.example.fyp.data.wordbank.WordBankGenerationRepository
import com.example.fyp.data.wordbank.WordBankCacheDataStore
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.model.TranslationRecord
import javax.inject.Inject

/**
 * Coordinator for WordBank operations.
 * Simplifies WordBankViewModel by centralizing complex word bank logic.
 */
class WordBankCoordinator @Inject constructor(
    private val wordBankRepo: FirestoreWordBankRepository,
    private val wordBankGenRepo: WordBankGenerationRepository,
    private val wordBankCacheDataStore: WordBankCacheDataStore
) {
    
    /**
     * Loads word bank with cache support.
     */
    suspend fun loadWordBankWithCache(
        uid: String,
        sourceLang: String,
        targetLang: String
    ): Result<List<com.example.fyp.model.WordBankItem>> {
        return try {
            val items = wordBankRepo.getWordBankForLanguagePair(uid, sourceLang, targetLang)
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates word bank if eligible based on record count.
     */
    suspend fun generateWordBankIfNeeded(
        uid: String,
        sourceLang: String,
        targetLang: String,
        records: List<TranslationRecord>,
        existsInFirestore: Boolean
    ): Result<String> {
        return try {
            val currentCount = records.size
            
            // Check if eligible for generation
            if (existsInFirestore) {
                if (!GenerationEligibility.canRegenerateWordBank(
                        existsInFirestore,
                        0,
                        currentCount
                    )) {
                    return Result.failure(Exception("Not enough new records to regenerate"))
                }
            }

            // Generate word bank
            val json = wordBankGenRepo.generateWordBank(records, sourceLang, targetLang)
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates cache for language pair.
     */
    suspend fun updateCache(uid: String, primary: String, target: String, exists: Boolean) {
        wordBankCacheDataStore.setCacheExists(uid, primary, target, exists)
    }

    /**
     * Loads persisted cache.
     */
    suspend fun loadCache(uid: String, primary: String): Map<String, Boolean> {
        return wordBankCacheDataStore.getCachedExistence(uid, primary)
    }

    /**
     * Clears cache for a specific primary language.
     */
    suspend fun clearCache(uid: String, primary: String) {
        wordBankCacheDataStore.clearCacheForPrimary(uid, primary)
    }
}
