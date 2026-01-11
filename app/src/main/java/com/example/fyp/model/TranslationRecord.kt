package com.example.fyp.model

import com.google.firebase.Timestamp

data class TranslationRecord(
    val id: String = "",
    val userId: String = "",
    val sourceText: String = "",
    val targetText: String = "",
    val sourceLang: String = "",
    val targetLang: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val mode: String = "",
    val sessionId: String = "",      // same for all items in one continuous session
    val speaker: String? = null,        // "A" or "B" for continuous
    val direction: String? = null,      // e.g. "A_to_B", "B_to_A"
    val sequence: Long? = null          // index inside one session if needed
)