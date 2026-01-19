package com.example.fyp.screens.learning

import com.example.fyp.model.TranslationRecord

fun buildLanguageClusters(
    records: List<TranslationRecord>,
    primaryLanguageCode: String,
    supportedLanguages: Set<String>
): List<LanguageClusterUi> {
    val counts = mutableMapOf<String, Int>()

    records.forEach { r ->
        listOf(r.sourceLang, r.targetLang).forEach { code ->
            val trimmed = code.trim()
            val isSupported = supportedLanguages.contains(trimmed)
            val isNotPrimary = trimmed != primaryLanguageCode
            if (trimmed.isNotEmpty() && isSupported && isNotPrimary) {
                counts[trimmed] = (counts[trimmed] ?: 0) + 1
            }
        }
    }

    return counts.entries
        .map { (code, count) -> LanguageClusterUi(code, count) }
        .sortedWith(compareByDescending<LanguageClusterUi> { it.count }.thenBy { it.languageCode })
}