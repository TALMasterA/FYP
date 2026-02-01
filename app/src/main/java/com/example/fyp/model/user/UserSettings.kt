package com.example.fyp.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val primaryLanguageCode: String = "en-US",
    val fontSizeScale: Float = 1.0f,
    val themeMode: String = "system", // "system" | "light" | "dark"
)