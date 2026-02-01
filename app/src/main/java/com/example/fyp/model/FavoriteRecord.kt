package com.example.fyp.model

import com.google.firebase.Timestamp

/**
 * A favorited/bookmarked translation record.
 */
data class FavoriteRecord(
    val id: String = "",
    val userId: String = "",
    val sourceText: String = "",
    val targetText: String = "",
    val sourceLang: String = "",
    val targetLang: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val note: String = ""
)
