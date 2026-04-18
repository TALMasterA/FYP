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
import com.example.fyp.model.ui.CantoneseUiTexts
import com.example.fyp.model.ui.ZhTwUiTexts
import com.example.fyp.model.ui.ZhCnUiTexts
import com.example.fyp.model.ui.JaJpUiTexts
import com.example.fyp.model.ui.KoKrUiTexts
import com.example.fyp.model.ui.FrFrUiTexts
import com.example.fyp.model.ui.DeDeUiTexts
import com.example.fyp.model.ui.EsEsUiTexts
import com.example.fyp.model.ui.IdIdUiTexts
import com.example.fyp.model.ui.ViVnUiTexts
import com.example.fyp.model.ui.ThThUiTexts
import com.example.fyp.model.ui.FilPhUiTexts
import com.example.fyp.model.ui.MsMyUiTexts
import com.example.fyp.model.ui.PtBrUiTexts
import com.example.fyp.model.ui.ItItUiTexts
import com.example.fyp.model.ui.RuRuUiTexts

typealias UiTextMap = Map<UiTextKey, String>
typealias UpdateAppLanguage = (String, UiTextMap) -> Unit

/** Maps a BCP-47 language code to its hardcoded UI-text map. */
private val hardcodedUiTexts: Map<String, Map<UiTextKey, String>> = mapOf(
    "zh-HK" to CantoneseUiTexts,
    "zh-TW" to ZhTwUiTexts,
    "zh-CN" to ZhCnUiTexts,
    "ja-JP" to JaJpUiTexts,
    "ko-KR" to KoKrUiTexts,
    "fr-FR" to FrFrUiTexts,
    "de-DE" to DeDeUiTexts,
    "es-ES" to EsEsUiTexts,
    "id-ID" to IdIdUiTexts,
    "vi-VN" to ViVnUiTexts,
    "th-TH" to ThThUiTexts,
    "fil-PH" to FilPhUiTexts,
    "ms-MY" to MsMyUiTexts,
    "pt-BR" to PtBrUiTexts,
    "it-IT" to ItItUiTexts,
    "ru-RU" to RuRuUiTexts,
)

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

        // All languages are now hardcoded — instant restore, no cache-hash check needed.
        val texts = if (selected.startsWith("en")) emptyMap()
                    else hardcodedUiTexts[selected] ?: emptyMap()

        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = selected,
            uiTexts = texts
        )
    }

    val updateAppLanguage: UpdateAppLanguage = { code, uiTexts ->
        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = code,
            uiTexts = uiTexts
        )
    }

    return appLanguageState to updateAppLanguage
}