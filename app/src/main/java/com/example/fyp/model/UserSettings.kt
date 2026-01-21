package com.example.fyp.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val primaryLanguageCode: String = "en-US",
    val fontSizeScale: Float = 1.0f
)