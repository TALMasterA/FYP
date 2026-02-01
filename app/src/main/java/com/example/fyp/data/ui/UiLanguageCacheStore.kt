package com.example.fyp.data.ui

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.fyp.model.ui.UiTextKey
import kotlinx.coroutines.flow.first
import org.json.JSONObject

private val Context.uiLangDataStore by preferencesDataStore(name = "ui_lang_cache")

class UiLanguageCacheStore(private val context: Context) {

    private val keySelected = stringPreferencesKey("selected_ui_language")
    private val keyGuestTranslationUsed = booleanPreferencesKey("guest_translation_used")

    private fun keyForLang(code: String): Preferences.Key<String> =
        stringPreferencesKey("uiTexts_json_$code")

    private fun keyHashForLang(code: String): Preferences.Key<Int> =
        intPreferencesKey("base_ui_texts_hash_$code")

    suspend fun getSelectedLanguage(defaultCode: String): String {
        val prefs = context.uiLangDataStore.data.first()
        return prefs[keySelected] ?: defaultCode
    }

    suspend fun setSelectedLanguage(code: String) {
        context.uiLangDataStore.edit { it[keySelected] = code }
    }

    /**
     * Check if a guest (non-logged-in) user has already used their one-time translation.
     */
    suspend fun hasGuestUsedTranslation(): Boolean {
        val prefs = context.uiLangDataStore.data.first()
        return prefs[keyGuestTranslationUsed] ?: false
    }

    /**
     * Mark that a guest user has used their one-time translation.
     */
    suspend fun setGuestTranslationUsed() {
        context.uiLangDataStore.edit { it[keyGuestTranslationUsed] = true }
    }

    /**
     * Reset guest translation usage (called when user logs in, so they can use cached translations).
     */
    suspend fun resetGuestTranslationUsed() {
        context.uiLangDataStore.edit { it.remove(keyGuestTranslationUsed) }
    }

    suspend fun getBaseHash(code: String): Int? {
        val prefs = context.uiLangDataStore.data.first()
        return prefs[keyHashForLang(code)]
    }

    suspend fun setBaseHash(code: String, hash: Int) {
        context.uiLangDataStore.edit { it[keyHashForLang(code)] = hash }
    }

    suspend fun loadUiTexts(code: String): Map<UiTextKey, String>? {
        val prefs = context.uiLangDataStore.data.first()
        val json = prefs[keyForLang(code)] ?: return null
        val obj = JSONObject(json)

        return UiTextKey.entries
            .associateWith { key -> obj.optString(key.name, "") }
            .filterValues { it.isNotBlank() }
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