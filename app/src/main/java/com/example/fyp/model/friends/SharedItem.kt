package com.example.fyp.model.friends

import com.google.firebase.Timestamp

/**
 * Represents an item shared between friends.
 */
data class SharedItem(
    val itemId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val toUserId: String = "",
    val type: SharedItemType = SharedItemType.WORD,
    val content: Map<String, Any> = emptyMap(),
    val status: SharedItemStatus = SharedItemStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * Type of shared item.
 */
enum class SharedItemType {
    WORD,                    // Word bank word
    LEARNING_SHEET,          // Learning sheet
    QUIZ                     // Quiz
}

/**
 * Status of a shared item.
 */
enum class SharedItemStatus {
    PENDING,                 // Not yet acted upon
    ACCEPTED,                // Added to user's collection
    DISMISSED                // Dismissed/ignored
}
