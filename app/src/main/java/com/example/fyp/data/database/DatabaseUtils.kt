package com.example.fyp.data.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await

/**
 * Utility functions for Firestore batch operations.
 * Handles batch size limits (500 per batch) automatically
 * and provides reusable patterns for common database operations.
 */
object DatabaseUtils {

    /** Firestore maximum batch size per commit. */
    const val MAX_BATCH_SIZE = 500

    /**
     * Executes a batch write operation, automatically chunking items
     * to respect Firestore's 500-operations-per-batch limit.
     *
     * @param items The list of items to process
     * @param batchSize Maximum operations per batch (default 500)
     * @param writer Lambda that adds operations to the [WriteBatch] for each item
     * @throws Exception if any batch commit fails
     */
    suspend fun <T> batchWrite(
        items: List<T>,
        batchSize: Int = MAX_BATCH_SIZE,
        writer: WriteBatch.(T) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        items.chunked(batchSize).forEach { chunk ->
            val batch = db.batch()
            chunk.forEach { item -> batch.writer(item) }
            batch.commit().await()
        }
    }

    /**
     * Batch deletes a collection of documents by their references.
     *
     * @param documentRefs The list of document references to delete
     * @param batchSize Maximum deletes per batch (default 500)
     */
    suspend fun batchDelete(
        documentRefs: List<DocumentReference>,
        batchSize: Int = MAX_BATCH_SIZE
    ) {
        batchWrite(documentRefs, batchSize) { ref ->
            delete(ref)
        }
    }

    /**
     * Batch sets data to multiple document references.
     *
     * @param items Pairs of document reference and data to set
     * @param batchSize Maximum operations per batch (default 500)
     */
    suspend fun <T : Any> batchSet(
        items: List<Pair<DocumentReference, T>>,
        batchSize: Int = MAX_BATCH_SIZE
    ) {
        batchWrite(items, batchSize) { (ref, data) ->
            set(ref, data)
        }
    }

    /**
     * Batch updates fields on multiple documents.
     *
     * @param items Pairs of document reference and field map to update
     * @param batchSize Maximum operations per batch (default 500)
     */
    suspend fun batchUpdate(
        items: List<Pair<DocumentReference, Map<String, Any>>>,
        batchSize: Int = MAX_BATCH_SIZE
    ) {
        batchWrite(items, batchSize) { (ref, fields) ->
            update(ref, fields)
        }
    }
}
