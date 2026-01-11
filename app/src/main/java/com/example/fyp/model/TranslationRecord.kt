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
    val mode: String = ""
)