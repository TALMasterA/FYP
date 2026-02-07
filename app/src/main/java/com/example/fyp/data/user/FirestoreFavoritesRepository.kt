@file:Suppress("unused")

package com.example.fyp.data.user

import com.example.fyp.model.FavoriteRecord
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
class FirestoreFavoritesRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun colRef(uid: String) =
        db.collection("users").document(uid).collection("favorites")

    /**
     * Observe all favorites in real-time
     */
    fun observeFavorites(userId: String): Flow<List<FavoriteRecord>> = callbackFlow {
        val reg = colRef(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val favorites = snap?.toObjects(FavoriteRecord::class.java) ?: emptyList()
                trySend(favorites)
            }
        awaitClose { reg.remove() }
    }

    /**
     * Add a translation to favorites
     */
    suspend fun addFavorite(
        userId: String,
        sourceText: String,
        targetText: String,
        sourceLang: String,
        targetLang: String,
        note: String = ""
    ): Result<FavoriteRecord> = try {
        val docRef = colRef(userId).document()
        val favorite = FavoriteRecord(
            id = docRef.id,
            userId = userId,
            sourceText = sourceText,
            targetText = targetText,
            sourceLang = sourceLang,
            targetLang = targetLang,
            createdAt = Timestamp.now(),
            note = note
        )
        docRef.set(favorite).await()
        Result.success(favorite)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Remove a favorite
     */
    suspend fun removeFavorite(userId: String, favoriteId: String): Result<Unit> = try {
        colRef(userId).document(favoriteId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Update favorite note
     */
    suspend fun updateNote(userId: String, favoriteId: String, note: String): Result<Unit> = try {
        colRef(userId).document(favoriteId).update("note", note).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Find a favorite by source and target text.
     * Consolidates the duplicate query logic from isFavorited(), getFavorite(), and getFavoriteId().
     * Single method reduces Firestore reads when any of these operations is needed.
     *
     * Callers can derive what they need:
     * - isFavorited: findFavorite(...) != null
     * - favorite: findFavorite(...)
     * - favoriteId: findFavorite(...)?.id
     */
    suspend fun findFavorite(
        userId: String,
        sourceText: String,
        targetText: String
    ): FavoriteRecord? = try {
        val snapshot = colRef(userId)
            .whereEqualTo("sourceText", sourceText)
            .whereEqualTo("targetText", targetText)
            .limit(1)
            .get()
            .await()
        snapshot.documents.firstOrNull()?.toObject(FavoriteRecord::class.java)
    } catch (e: Exception) {
        null
    }

    /**
     * Check if a translation is already favorited.
     * Delegates to findFavorite() to avoid duplicate queries.
     */
    suspend fun isFavorited(
        userId: String,
        sourceText: String,
        targetText: String
    ): Boolean = findFavorite(userId, sourceText, targetText) != null

    /**
     * Get favorite by source and target text.
     * Delegates to findFavorite() to avoid duplicate queries.
     */
    suspend fun getFavorite(
        userId: String,
        sourceText: String,
        targetText: String
    ): FavoriteRecord? = findFavorite(userId, sourceText, targetText)

    /**
     * Get favorite ID by source and target text (for deletion).
     * Delegates to findFavorite() to avoid duplicate queries.
     */
    suspend fun getFavoriteId(
        userId: String,
        sourceText: String,
        targetText: String
    ): String? = findFavorite(userId, sourceText, targetText)?.id

    /**
     * Get all favorites once (not real-time) for loading initial state
     */
    suspend fun getAllFavoritesOnce(userId: String): List<FavoriteRecord> = try {
        val snapshot = colRef(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        snapshot.toObjects(FavoriteRecord::class.java)
    } catch (e: Exception) {
        emptyList()
    }
}