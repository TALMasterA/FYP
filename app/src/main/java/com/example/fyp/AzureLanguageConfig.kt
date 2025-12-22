package com.example.fyp

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
            listOf("en-US") // safe fallback
        }
    }
}