package com.translator.TalknLearn.data.ui

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.uiLangDataStore by preferencesDataStore(name = "ui_lang_cache")

/**
 * Persists the user's selected UI-language code.
 * Since all UI translations are now hardcoded, only the language preference is stored.
 */
class UiLanguageCacheStore(private val context: Context) {

    private val keySelected = stringPreferencesKey("selected_ui_language")

    suspend fun getSelectedLanguage(defaultCode: String): String {
        val prefs = context.uiLangDataStore.data.first()
        return prefs[keySelected] ?: defaultCode
    }

    suspend fun setSelectedLanguage(code: String) {
        context.uiLangDataStore.edit { it[keySelected] = code }
    }
}