package com.example.fyp.data

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
        "es-ES" to "Spanish"
    )

    fun displayName(code: String): String = map[code] ?: code
}