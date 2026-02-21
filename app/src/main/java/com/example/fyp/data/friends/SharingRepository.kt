package com.example.fyp.data.friends

import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemType
import kotlinx.coroutines.flow.Flow

/**
 * Repository for sharing words and learning materials between friends.
 */
interface SharingRepository {
    
    /**
     * Share a word bank word with a friend.
     * @param fromUserId The user sharing the word
     * @param fromUsername The username of the sharing user (passed in-memory to avoid a profile read)
     * @param toUserId The friend receiving the word
     * @param wordData Map containing word information (sourceText, targetText, sourceLang, targetLang, etc.)
     */
    suspend fun shareWord(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        wordData: Map<String, Any>
    ): Result<SharedItem>
    
    /**
     * Share a learning material (sheet or quiz) with a friend.
     * @param fromUserId The user sharing the material
     * @param fromUsername The username of the sharing user (passed in-memory to avoid a profile read)
     * @param toUserId The friend receiving the material
     * @param type Type of material (LEARNING_SHEET or QUIZ)
     * @param materialData Map containing material information
     */
    suspend fun shareLearningMaterial(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        type: SharedItemType,
        materialData: Map<String, Any>
    ): Result<SharedItem>
    
    /**
     * Accept a shared item (add to user's collection).
     */
    suspend fun acceptSharedItem(
        itemId: String,
        userId: UserId
    ): Result<Unit>
    
    /**
     * Dismiss a shared item without adding it.
     */
    suspend fun dismissSharedItem(
        itemId: String,
        userId: UserId
    ): Result<Unit>
    
    /**
     * Observe shared items inbox in real-time.
     */
    fun observeSharedInbox(userId: UserId): Flow<List<SharedItem>>
    
    /**
     * Get pending (unacted upon) shared items count.
     */
    suspend fun getPendingItemsCount(userId: UserId): Int

    /**
     * Fetch the full content text for a shared learning material.
     * Stored in a sub-document to avoid Firestore 1 MB document limit.
     * Returns null if not found (older items that didn't store full content).
     */
    suspend fun fetchSharedItemFullContent(userId: UserId, itemId: String): String?

    /**
     * Fetch a single shared item by its ID.
     * Used when navigating directly to a shared material detail screen
     * and the in-memory list hasn't loaded yet.
     */
    suspend fun fetchSharedItemById(userId: UserId, itemId: String): SharedItem?
}
