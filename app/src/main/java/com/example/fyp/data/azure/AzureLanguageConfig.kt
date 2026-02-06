package com.example.fyp.data.azure

import android.content.Context
import org.json.JSONArray

object AzureLanguageConfig {
    fun loadSupportedLanguages(context: Context): List<String> {
        return try {
            val input = context.assets.open("azure_languages.json")
            val text = input.bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            List(arr.length()) { i -> arr.getString(i) }
        } catch (e: Exception) {
            listOf("en-US")
        }
    }
}

object LanguageDisplayNames {
    private val map = mapOf(
        "en-US" to "English",
        "zh-HK" to "Cantonese",
        "zh-CN" to "Mandarin",
        "ja-JP" to "Japanese",
        "fr-FR" to "French",
        "de-DE" to "German",
        "ko-KR" to "Korean",
        "es-ES" to "Spanish",
        "id-ID" to "Indonesian",
        "vi-VN" to "Vietnamese",
        "th-TH" to "Thai",
        "fil-PH" to "Filipino",
        "ms-MY" to "Malay",
        "pt-BR" to "Portuguese",
        "it-IT" to "Italian",
        "ru-RU" to "Russian"
    )

    fun displayName(code: String): String = map[code] ?: code

    /**
     * Map Azure detected language short codes to supported full language codes.
     * Azure detect returns codes like "ja", "en", "zh-Hans", "zh-Hant"
     * We need to map them to our supported codes like "ja-JP", "en-US", "zh-CN", "zh-HK"
     */
    private val detectedToSupported = mapOf(
        "en" to "en-US",
        "ja" to "ja-JP",
        "ko" to "ko-KR",
        "fr" to "fr-FR",
        "de" to "de-DE",
        "es" to "es-ES",
        "id" to "id-ID",
        "vi" to "vi-VN",
        "th" to "th-TH",
        "fil" to "fil-PH",
        "ms" to "ms-MY",
        "pt" to "pt-BR",
        "it" to "it-IT",
        "ru" to "ru-RU",
        "zh-Hans" to "zh-CN",  // Simplified Chinese -> Mandarin
        "zh-Hant" to "zh-HK",  // Traditional Chinese -> Cantonese
        "yue" to "zh-HK",       // Cantonese
        "zh" to "zh-CN"         // Generic Chinese -> Mandarin
    )

    /**
     * Convert detected language code to a supported full code.
     * Returns the original code if no mapping exists.
     */
    fun mapDetectedToSupportedCode(detectedCode: String): String {
        // First check if it's already a supported code
        if (map.containsKey(detectedCode)) return detectedCode

        // Try direct mapping
        detectedToSupported[detectedCode]?.let { return it }

        // Try extracting base language (e.g., "en-GB" -> "en" -> "en-US")
        val baseCode = detectedCode.substringBefore("-")
        detectedToSupported[baseCode]?.let { return it }

        // Return original if no mapping found
        return detectedCode
    }

    /**
     * Check if a language code (detected or full) maps to a supported language
     */
    fun isSupportedLanguage(code: String): Boolean {
        return map.containsKey(code) || detectedToSupported.containsKey(code) ||
                detectedToSupported.containsKey(code.substringBefore("-"))
    }
}