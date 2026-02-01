package com.example.fyp.model

import com.google.firebase.Timestamp

/**
 * A custom word added by user to their word bank.
 */
data class CustomWord(
    val id: String = "",
    val userId: String = "",
    val originalWord: String = "",
    val translatedWord: String = "",
    val pronunciation: String = "",
    val example: String = "",
    val sourceLang: String = "",
    val targetLang: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
