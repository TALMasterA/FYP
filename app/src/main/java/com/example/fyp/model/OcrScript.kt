package com.example.fyp.model

/**
 * OCR script types supported by ML Kit text recognition.
 * Each script covers a group of languages that share the same writing system.
 * All models are bundled with the app for reliable offline operation.
 */
enum class OcrScript(
    val displayName: String,
    val estimatedSizeMb: Int,
    val languagePrefixes: List<String>
) {
    LATIN(
        displayName = "Latin (English, European)",
        estimatedSizeMb = 5,
        languagePrefixes = listOf("en", "es", "fr", "de", "it", "pt", "vi", "id", "ms", "fil", "th", "ru")
    ),
    CHINESE(
        displayName = "Chinese",
        estimatedSizeMb = 10,
        languagePrefixes = listOf("zh")
    ),
    JAPANESE(
        displayName = "Japanese",
        estimatedSizeMb = 10,
        languagePrefixes = listOf("ja")
    ),
    KOREAN(
        displayName = "Korean",
        estimatedSizeMb = 8,
        languagePrefixes = listOf("ko")
    )
}
