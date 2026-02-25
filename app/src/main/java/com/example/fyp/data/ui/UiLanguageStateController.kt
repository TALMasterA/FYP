package com.example.fyp.data.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.model.ui.ZhTwUiTexts
import com.example.fyp.model.ui.YueUiTexts
import com.example.fyp.model.ui.baseUiTextsHash
import com.example.fyp.model.ui.LanguageNameTranslations
import com.example.fyp.model.ui.LanguageNameKeys

typealias UiTextMap = Map<UiTextKey, String>
typealias UpdateAppLanguage = (String, UiTextMap) -> Unit

/**
 * Apply predefined language name corrections to a UI text map.
 * This fixes translation API errors where language names are incorrectly translated.
 */
private fun applyLanguageNameCorrections(
    map: Map<UiTextKey, String>,
    uiLanguageCode: String
): Map<UiTextKey, String> {
    if (uiLanguageCode.startsWith("en")) return map

    val langNameTranslations = LanguageNameTranslations[uiLanguageCode] ?: return map

    val correctedMap = map.toMutableMap()
    LanguageNameKeys.forEach { key ->
        langNameTranslations[key]?.let { correctTranslation ->
            correctedMap[key] = correctTranslation
        }
    }
    return correctedMap
}

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

        if (selected.startsWith("en")) {
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = selected,
                uiTexts = emptyMap()
            )
            return@LaunchedEffect
        }

        // Traditional Chinese (zh-TW): use hardcoded map, no API call needed
        if (selected == "zh-TW") {
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = "zh-TW",
                uiTexts = ZhTwUiTexts
            )
            return@LaunchedEffect
        }

        // Cantonese (zh-HK): use hardcoded map, no API call needed
        if (selected == "zh-HK") {
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = "zh-HK",
                uiTexts = YueUiTexts
            )
            return@LaunchedEffect
        }

        val cachedHash = cache.getBaseHash(selected)

        // If cache matches current BaseUiTexts => load it; else fallback to English (empty map)
        if (cachedHash == currentHash) {
            val cachedMap = cache.loadUiTexts(selected)
            // Apply language name corrections to fix any translation API errors
            val correctedMap = if (cachedMap != null) {
                applyLanguageNameCorrections(cachedMap, selected)
            } else {
                emptyMap()
            }
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = selected,
                uiTexts = correctedMap
            )
        } else {
            // Cached UI texts belong to an older BaseUiTexts version.
            // Reset dropdown to default (English) so UI + dropdown are consistent.
            cache.setSelectedLanguage(defaultCode)
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = defaultCode,
                uiTexts = emptyMap()
            )
        }
    }

    var pendingSave by remember { mutableStateOf<Pair<String, UiTextMap>?>(null) }

    val updateAppLanguage: UpdateAppLanguage = { code, uiTexts ->
        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = code,
            uiTexts = uiTexts
        )
        pendingSave = code to uiTexts
    }

    LaunchedEffect(pendingSave) {
        val pair = pendingSave ?: return@LaunchedEffect
        val (code, uiTexts) = pair

        cache.setSelectedLanguage(code)

        // If english, we store no map (fallback to BaseUiTexts)
        if (code.startsWith("en")) {
            cache.setBaseHash(code, baseUiTextsHash())
            return@LaunchedEffect
        }

        // Save whatever we have (can be partial; missing keys fallback to English in UI)
        cache.saveUiTexts(code, uiTexts)
        cache.setBaseHash(code, baseUiTextsHash())
    }

    return appLanguageState to updateAppLanguage
}