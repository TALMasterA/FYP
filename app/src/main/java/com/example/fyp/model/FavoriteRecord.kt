package com.example.fyp.model

import androidx.compose.runtime.Immutable
import com.google.firebase.Timestamp

/**
 * A favorited/bookmarked translation record.
 */
@Immutable
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

/**
 * A favorited live conversation session.
 * Stores the session metadata and all its translation records.
 */
@Immutable
data class FavoriteSession(
    val id: String = "",
    val userId: String = "",
    val sessionId: String = "",
    val sessionName: String = "",
    val records: List<FavoriteSessionRecord> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)

/**
 * A single record within a favorite session (lightweight copy).
 */
@Immutable
data class FavoriteSessionRecord(
    val sourceText: String = "",
    val targetText: String = "",
    val sourceLang: String = "",
    val targetLang: String = "",
    val speaker: String = "",
    val direction: String = "",
    val sequence: Int = 0
)
