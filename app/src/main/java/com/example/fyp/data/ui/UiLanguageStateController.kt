package com.example.fyp.data.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey
import com.example.fyp.model.baseUiTextsHash

typealias UiTextMap = Map<UiTextKey, String>
typealias UpdateAppLanguage = (String, UiTextMap) -> Unit

@Composable
fun rememberUiLanguageState(
    uiLanguages: List<Pair<String, String>>,
): Pair<AppLanguageState, UpdateAppLanguage> {
    val context = LocalContext.current
    val cache = remember { UiLanguageCacheStore(context) }

    var appLanguageState by remember {
        mutableStateOf(
            AppLanguageState(
                selectedUiLanguage = uiLanguages[0].first,
                uiTexts = emptyMap(),
            )
        )
    }

    LaunchedEffect(Unit) {
        val defaultCode = uiLanguages[0].first
        val selected = cache.getSelectedLanguage(defaultCode)
        val currentHash = baseUiTextsHash()
        val cachedHash = cache.getBaseHash()

        if (selected.startsWith("en")) {
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = selected,
                uiTexts = emptyMap(),
            )
            return@LaunchedEffect
        }

        if (cachedHash == currentHash) {
            val cachedMap = cache.loadUiTexts(selected)
            if (!cachedMap.isNullOrEmpty()) {
                appLanguageState = appLanguageState.copy(
                    selectedUiLanguage = selected,
                    uiTexts = cachedMap,
                )
            } else {
                // Keep selected language, but no translations yet
                appLanguageState = appLanguageState.copy(
                    selectedUiLanguage = selected,
                    uiTexts = emptyMap(),
                )
            }
        } else {
            // Base texts changed => invalidate cache, but keep the selected language
            cache.clearUiTexts(selected)
            cache.setBaseHash(currentHash)
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = selected,
                uiTexts = emptyMap(),
            )
        }
    }

    var pendingSave by remember { mutableStateOf<Pair<String, UiTextMap>?>(null) }

    val updateAppLanguage: UpdateAppLanguage = { code, uiTexts ->
        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = code,
            uiTexts = uiTexts,
        )
        pendingSave = code to uiTexts
    }

    LaunchedEffect(pendingSave) {
        val pair = pendingSave ?: return@LaunchedEffect
        val (code, uiTexts) = pair

        cache.setSelectedLanguage(code)

        if (code.startsWith("en")) {
            cache.setBaseHash(baseUiTextsHash())
            // optional: clear stored english map (usually not needed)
            return@LaunchedEffect
        }

        cache.saveUiTexts(code, uiTexts)
        cache.setBaseHash(baseUiTextsHash())
    }

    return appLanguageState to updateAppLanguage
}