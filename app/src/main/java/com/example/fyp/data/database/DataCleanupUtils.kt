package com.example.fyp.data.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Utilities for periodic data housekeeping in Firestore.
 * Intended to be called from a background worker or admin action
 * to remove stale data and reduce storage costs.
 */
object DataCleanupUtils {

    private const val TAG = "DataCleanup"

    /**
     * Deletes documents in a collection that are older than the given
     * [cutoffTimestamp] (epoch millis).
     *
     * @param collectionPath Full Firestore path (e.g. "users/uid/history")
     * @param timestampField The field that stores the document timestamp
     * @param cutoffTimestamp Documents created before this time are removed
     * @param batchSize Number of documents to delete per batch
     * @return The total number of deleted documents
     */
    suspend fun deleteOldDocuments(
        collectionPath: String,
        timestampField: String = "timestamp",
        cutoffTimestamp: Long,
        batchSize: Int = DatabaseUtils.MAX_BATCH_SIZE
    ): Int {
        val db = FirebaseFirestore.getInstance()
        var totalDeleted = 0

        while (true) {
            val snapshot = db.collection(collectionPath)
                .whereLessThan(timestampField, cutoffTimestamp)
                .orderBy(timestampField, Query.Direction.ASCENDING)
                .limit(batchSize.toLong())
                .get()
                .await()

            if (snapshot.isEmpty) break

            val refs = snapshot.documents.map { it.reference }
            DatabaseUtils.batchDelete(refs, batchSize)
            totalDeleted += refs.size
        }

        return totalDeleted
    }

    /**
     * Removes friend requests that have been in "pending" state for longer
     * than [maxAgeDays].
     *
     * @param userId The user whose pending requests should be cleaned
     * @param maxAgeDays Maximum age in days before a request is considered stale
     * @return Number of stale requests removed
     */
    suspend fun cleanupStaleFriendRequests(
        userId: String,
        maxAgeDays: Int = 30
    ): Int {
        val cutoff = System.currentTimeMillis() - maxAgeDays.toLong() * 24 * 60 * 60 * 1000
        return deleteOldDocuments(
            collectionPath = "users/$userId/friendRequests",
            timestampField = "sentAt",
            cutoffTimestamp = cutoff
        )
    }

    /**
     * Archives (deletes from Firestore) translation history older than
     * [maxAgeMonths] months for the given user.
     *
     * @param userId The user whose history should be trimmed
     * @param maxAgeMonths Maximum age in months
     * @return Number of records removed
     */
    suspend fun archiveOldHistory(
        userId: String,
        maxAgeMonths: Int = 6
    ): Int {
        val cutoff = System.currentTimeMillis() -
                maxAgeMonths.toLong() * 30 * 24 * 60 * 60 * 1000
        return deleteOldDocuments(
            collectionPath = "users/$userId/history",
            timestampField = "timestamp",
            cutoffTimestamp = cutoff
        )
    }

    /**
     * Removes orphaned shared items where the original sender account
     * no longer exists.
     *
     * @param userId The recipient user whose inbox should be cleaned
     * @param existingSenderIds Set of user IDs that still exist
     * @return Number of orphan items removed
     */
    suspend fun cleanupOrphanedSharedItems(
        userId: String,
        existingSenderIds: Set<String>
    ): Int {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("users/$userId/sharedInbox")
            .get()
            .await()

        val orphanRefs = snapshot.documents
            .filter { doc ->
                val senderId = doc.getString("senderId") ?: return@filter false
                senderId !in existingSenderIds
            }
            .map { it.reference }

        if (orphanRefs.isNotEmpty()) {
            DatabaseUtils.batchDelete(orphanRefs)
        }
        return orphanRefs.size
    }
}
