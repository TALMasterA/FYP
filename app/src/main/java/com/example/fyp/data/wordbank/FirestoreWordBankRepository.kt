package com.example.fyp.data.wordbank

import com.example.fyp.core.NetworkRetry
import com.example.fyp.screens.wordbank.WordBank
import com.example.fyp.screens.wordbank.WordBankItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Metadata for a word bank document.
 * Used by getWordBankMetadata() to consolidate multiple queries into one.
 */
data class WordBankMetadata(
    val exists: Boolean,
    val historyCountAtGenerate: Int
)

@Singleton
class FirestoreWordBankRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun norm(code: String) = code.trim()
    private fun docId(primary: String, target: String) = "${norm(primary)}__${norm(target)}"

    private fun docRef(uid: String, primary: String, target: String) =
        db.collection("users")
            .document(uid)
            .collection("word_banks")
            .document(docId(primary, target))

    suspend fun getWordBank(uid: String, primary: String, target: String): WordBank? {
        val doc = docRef(uid, primary, target).get().await()
        if (!doc.exists()) return null

        val wordsData = doc.get("words") as? List<*> ?: emptyList<Any>()
        val words = wordsData.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            WordBankItem(
                id = map["id"] as? String ?: "",
                originalWord = map["originalWord"] as? String ?: "",
                translatedWord = map["translatedWord"] as? String ?: "",
                pronunciation = map["pronunciation"] as? String ?: "",
                example = map["example"] as? String ?: "",
                category = map["category"] as? String ?: "",
                difficulty = map["difficulty"] as? String ?: ""
            )
        }

        return WordBank(
            primaryLanguageCode = doc.getString("primaryLanguageCode") ?: primary,
            targetLanguageCode = doc.getString("targetLanguageCode") ?: target,
            words = words,
            generatedAt = doc.getTimestamp("generatedAt"),
            historyCountAtGenerate = (doc.getLong("historyCountAtGenerate") ?: 0).toInt()
        )
    }

    suspend fun saveWordBank(
        uid: String,
        primary: String,
        target: String,
        words: List<WordBankItem>,
        historyCount: Int
    ) {
        val wordsData = words.map { word ->
            mapOf(
                "id" to word.id,
                "originalWord" to word.originalWord,
                "translatedWord" to word.translatedWord,
                "pronunciation" to word.pronunciation,
                "example" to word.example,
                "category" to word.category,
                "difficulty" to word.difficulty
            )
        }

        val data = mapOf(
            "primaryLanguageCode" to norm(primary),
            "targetLanguageCode" to norm(target),
            "words" to wordsData,
            "generatedAt" to Timestamp.now(),
            "historyCountAtGenerate" to historyCount
        )

        NetworkRetry.withRetry(shouldRetry = NetworkRetry::isRetryableFirebaseException) {
            docRef(uid, primary, target).set(data).await()
        }
    }

    /**
     * Append new words to existing word bank, avoiding duplicates based on originalWord
     */
    suspend fun appendWords(
        uid: String,
        primary: String,
        target: String,
        newWords: List<WordBankItem>,
        historyCount: Int
    ) {
        val existingWordBank = getWordBank(uid, primary, target)
        val existingWords = existingWordBank?.words ?: emptyList()

        // Get existing original words (lowercase for comparison)
        val existingOriginalWords = existingWords.map { it.originalWord.lowercase().trim() }.toSet()

        // Filter out duplicates from new words
        val uniqueNewWords = newWords.filter { newWord ->
            newWord.originalWord.lowercase().trim() !in existingOriginalWords
        }

        // Merge existing and new words
        val mergedWords = existingWords + uniqueNewWords

        // Save the merged word bank
        saveWordBank(uid, primary, target, mergedWords, historyCount)
    }

    suspend fun wordBankExists(uid: String, primary: String, target: String): Boolean {
        return getWordBankMetadata(uid, primary, target).exists
    }

    suspend fun getWordBankHistoryCount(uid: String, primary: String, target: String): Int {
        return getWordBankMetadata(uid, primary, target).historyCountAtGenerate
    }

    /**
     * Get both existence and history count in a single Firestore read.
     * This consolidates the logic of wordBankExists() and getWordBankHistoryCount()
     * which previously made separate document reads.
     *
     * Use this method if you need both pieces of metadata, or when adding future code
     * that requires both pieces of information.
     */
    suspend fun getWordBankMetadata(uid: String, primary: String, target: String): WordBankMetadata {
        val doc = docRef(uid, primary, target).get().await()
        return WordBankMetadata(
            exists = doc.exists(),
            historyCountAtGenerate = if (doc.exists()) {
                (doc.getLong("historyCountAtGenerate") ?: 0).toInt()
            } else {
                0
            }
        )
    }
}
