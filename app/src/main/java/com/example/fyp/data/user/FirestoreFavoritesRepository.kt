@file:Suppress("unused")

package com.example.fyp.data.user

import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.FavoriteSession
import com.example.fyp.model.FavoriteSessionRecord
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
    private companion object {
        const val QUERY_LIMIT = 500L
    }

    private fun colRef(uid: String) =
        db.collection("users").document(uid).collection("favorites")

    private fun sessionColRef(uid: String) =
        db.collection("users").document(uid).collection("favorite_sessions")

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
     * Find a favorite by source and target text.
     * Single method reduces Firestore reads.
     *
     * Callers can derive what they need:
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
            .limit(QUERY_LIMIT)
            .get()
            .await()
        snapshot.toObjects(FavoriteRecord::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    /**
     * Get the total number of favorite records for a user, including
     * records embedded inside favourite sessions.
     * Each individual record counts as 1; each session counts as N
     * where N is the number of records it contains.
     * Used for enforcing the MAX_FAVORITE_RECORDS limit.
     */
    suspend fun getTotalFavoriteRecordCount(userId: String): Int = try {
        val individualCount = colRef(userId).get().await().size()
        val sessions = sessionColRef(userId).get().await()
            .toObjects(FavoriteSession::class.java)
        val sessionRecordCount = sessions.sumOf { it.records.size }
        individualCount + sessionRecordCount
    } catch (e: Exception) {
        0
    }

    // ── Favorite Sessions ─────────────────────────────────────────────────

    /**
     * Save a full conversation session as a favourite.
     * The session document contains embedded records (no subcollection needed).
     */
    suspend fun addFavoriteSession(
        userId: String,
        sessionId: String,
        sessionName: String,
        records: List<FavoriteSessionRecord>
    ): Result<FavoriteSession> = try {
        val docRef = sessionColRef(userId).document()
        val session = FavoriteSession(
            id = docRef.id,
            userId = userId,
            sessionId = sessionId,
            sessionName = sessionName,
            records = records,
            createdAt = Timestamp.now()
        )
        docRef.set(session).await()
        Result.success(session)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Remove a favorited session by its Firestore document ID.
     */
    suspend fun removeFavoriteSession(userId: String, favoriteSessionId: String): Result<Unit> = try {
        sessionColRef(userId).document(favoriteSessionId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Find a favorited session by the original sessionId.
     */
    suspend fun findFavoriteSession(
        userId: String,
        sessionId: String
    ): FavoriteSession? = try {
        val snapshot = sessionColRef(userId)
            .whereEqualTo("sessionId", sessionId)
            .limit(1)
            .get()
            .await()
        snapshot.documents.firstOrNull()?.toObject(FavoriteSession::class.java)
    } catch (e: Exception) {
        null
    }

    /**
     * Get all favourite sessions once (not real-time) for loading in FavoritesScreen.
     */
    suspend fun getAllFavoriteSessionsOnce(userId: String): List<FavoriteSession> = try {
        val snapshot = sessionColRef(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(QUERY_LIMIT)
            .get()
            .await()
        snapshot.toObjects(FavoriteSession::class.java)
    } catch (e: Exception) {
        emptyList()
    }
}