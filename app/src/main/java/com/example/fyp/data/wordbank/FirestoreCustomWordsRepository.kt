package com.example.fyp.data.wordbank

import android.util.Log
import com.example.fyp.model.CustomWord
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreCustomWordsRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    companion object {
        const val MAX_WORD_LENGTH = 200
        const val MAX_EXAMPLE_LENGTH = 500
        private const val QUERY_LIMIT = 500L
    }

    private fun colRef(uid: String) =
        db.collection("users").document(uid).collection("custom_words")

    /**
     * Observe custom words for a specific language pair
     */
    fun observeCustomWords(
        userId: String,
        sourceLang: String,
        targetLang: String
    ): Flow<List<CustomWord>> = callbackFlow {
        val reg = colRef(userId)
            .whereEqualTo("sourceLang", sourceLang)
            .whereEqualTo("targetLang", targetLang)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(QUERY_LIMIT)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val words = snap?.toObjects(CustomWord::class.java) ?: emptyList()
                trySend(words)
            }
        awaitClose { reg.remove() }
    }

    /**
     * Observe all custom words
     */
    fun observeAllCustomWords(userId: String): Flow<List<CustomWord>> = callbackFlow {
        val reg = colRef(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(QUERY_LIMIT)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val words = snap?.toObjects(CustomWord::class.java) ?: emptyList()
                trySend(words)
            }
        awaitClose { reg.remove() }
    }

    /**
     * Add a custom word with input validation
     */
    suspend fun addCustomWord(
        userId: String,
        originalWord: String,
        translatedWord: String,
        pronunciation: String = "",
        example: String = "",
        sourceLang: String,
        targetLang: String
    ): Result<CustomWord> {
        return try {
            // Validate input lengths
            val trimmedOriginal = originalWord.trim().take(MAX_WORD_LENGTH)
            val trimmedTranslated = translatedWord.trim().take(MAX_WORD_LENGTH)
            val trimmedPronunciation = pronunciation.trim().take(MAX_WORD_LENGTH)
            val trimmedExample = example.trim().take(MAX_EXAMPLE_LENGTH)

            if (trimmedOriginal.isBlank() || trimmedTranslated.isBlank()) {
                return Result.failure(IllegalArgumentException("Word and translation are required"))
            }

            val docRef = colRef(userId).document()
            val word = CustomWord(
                id = docRef.id,
                userId = userId,
                originalWord = trimmedOriginal,
                translatedWord = trimmedTranslated,
                pronunciation = trimmedPronunciation,
                example = trimmedExample,
                sourceLang = sourceLang,
                targetLang = targetLang,
                createdAt = Timestamp.now()
            )
            docRef.set(word).await()
            Result.success(word)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a custom word with input validation
     */
    suspend fun updateCustomWord(
        userId: String,
        wordId: String,
        originalWord: String,
        translatedWord: String,
        pronunciation: String,
        example: String
    ): Result<Unit> = try {
        colRef(userId).document(wordId).update(
            mapOf(
                "originalWord" to originalWord.trim().take(MAX_WORD_LENGTH),
                "translatedWord" to translatedWord.trim().take(MAX_WORD_LENGTH),
                "pronunciation" to pronunciation.trim().take(MAX_WORD_LENGTH),
                "example" to example.trim().take(MAX_EXAMPLE_LENGTH)
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Delete a custom word
     */
    suspend fun deleteCustomWord(userId: String, wordId: String): Result<Unit> = try {
        colRef(userId).document(wordId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Get custom words once (not real-time) for a specific language pair
     */
    suspend fun getCustomWordsOnce(
        userId: String,
        sourceLang: String,
        targetLang: String
    ): List<CustomWord> = try {
        val snapshot = colRef(userId)
            .whereEqualTo("sourceLang", sourceLang)
            .whereEqualTo("targetLang", targetLang)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(QUERY_LIMIT)
            .get()
            .await()
        snapshot.toObjects(CustomWord::class.java)
    } catch (e: Exception) {
        Log.w("FirestoreCustomWordsRepository", "Failed to get custom words for user $userId, langs: $sourceLang/$targetLang", e)
        emptyList()
    }

    /**
     * Get all custom words once (not real-time)
     */
    suspend fun getAllCustomWordsOnce(userId: String): List<CustomWord> = try {
        val snapshot = colRef(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(QUERY_LIMIT)
            .get()
            .await()
        snapshot.toObjects(CustomWord::class.java)
    } catch (e: Exception) {
        Log.w("FirestoreCustomWordsRepository", "Failed to get all custom words for user $userId", e)
        emptyList()
    }

    /**
     * Check if word already exists
     */
    suspend fun wordExists(
        userId: String,
        originalWord: String,
        sourceLang: String,
        targetLang: String
    ): Boolean = try {
        val snapshot = colRef(userId)
            .whereEqualTo("originalWord", originalWord.trim().lowercase())
            .whereEqualTo("sourceLang", sourceLang)
            .whereEqualTo("targetLang", targetLang)
            .limit(1)
            .get()
            .await()
        !snapshot.isEmpty
    } catch (e: Exception) {
        Log.w("FirestoreCustomWordsRepository", "Failed to check if word exists for user $userId", e)
        false
    }
}
