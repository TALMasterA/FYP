package com.example.fyp.model

import com.google.firebase.Timestamp

data class HistorySession(
    val sessionId: String = "",
    val name: String = "",
    val updatedAt: Timestamp = Timestamp.now()
)