package com.example.fyp.data.azure

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object AzureLanguageConfig {
    // Cache the result so the JSON asset is only read once across all call sites.
    @Volatile private var cachedLanguages: List<String>? = null

    fun loadSupportedLanguages(context: Context): List<String> {
        cachedLanguages?.let { return it }
        return try {
            val input = context.assets.open("azure_languages.json")
            val text = input.bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            List(arr.length()) { i -> arr.getString(i) }
        } catch (e: Exception) {
            listOf("en-US")
        }.also { cachedLanguages = it }
    }

    suspend fun loadSupportedLanguagesSuspend(context: Context): List<String> {
        return withContext(Dispatchers.IO) {
            loadSupportedLanguages(context)
        }
    }
}

object LanguageDisplayNames {
    private val map = mapOf(
        "en-US" to "English",
        "zh-TW" to "Traditional Chinese",
        "zh-HK" to "Cantonese",
        "zh-CN" to "Simplified Chinese",
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
        "zh-Hans" to "zh-CN",  // Simplified Chinese detected -> zh-CN
        "zh-Hant" to "zh-TW",  // Traditional Chinese detected -> zh-TW
        "yue" to "zh-HK",       // Cantonese detected -> zh-HK
        "zh" to "zh-CN"         // Generic Chinese detected -> zh-CN
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