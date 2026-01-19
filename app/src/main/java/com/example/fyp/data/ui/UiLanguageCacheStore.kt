package com.example.fyp.data.ui

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.fyp.model.UiTextKey
import kotlinx.coroutines.flow.first
import org.json.JSONObject

private val Context.uiLangDataStore by preferencesDataStore(name = "ui_lang_cache")

class UiLanguageCacheStore(private val context: Context) {

    private val keyselected = stringPreferencesKey("selected_ui_language")
    private val keybasehash = intPreferencesKey("base_ui_texts_hash")

    private fun keyForLang(code: String): Preferences.Key<String> =
        stringPreferencesKey("uiTexts_json_$code")

    suspend fun getSelectedLanguage(defaultCode: String): String {
        val prefs = context.uiLangDataStore.data.first()
        return prefs[keyselected] ?: defaultCode
    }

    suspend fun setSelectedLanguage(code: String) {
        context.uiLangDataStore.edit { it[keyselected] = code }
    }

    suspend fun getBaseHash(): Int? {
        val prefs = context.uiLangDataStore.data.first()
        return prefs[keybasehash]
    }

    suspend fun setBaseHash(hash: Int) {
        context.uiLangDataStore.edit { it[keybasehash] = hash }
    }

    suspend fun loadUiTexts(code: String): Map<UiTextKey, String>? {
        val prefs = context.uiLangDataStore.data.first()
        val json = prefs[keyForLang(code)] ?: return null
        val obj = JSONObject(json)

        return UiTextKey.entries.associateWith { key ->
            obj.optString(key.name, "")
        }.filterValues { it.isNotBlank() }
    }

    suspend fun saveUiTexts(code: String, map: Map<UiTextKey, String>) {
        val obj = JSONObject()
        map.forEach { (k, v) -> obj.put(k.name, v) }
        context.uiLangDataStore.edit { prefs ->
            prefs[keyForLang(code)] = obj.toString()
        }
    }

    suspend fun clearUiTexts(code: String) {
        context.uiLangDataStore.edit { it.remove(keyForLang(code)) }
    }
}