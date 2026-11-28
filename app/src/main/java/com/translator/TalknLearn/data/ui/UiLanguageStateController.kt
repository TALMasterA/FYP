package com.translator.TalknLearn.data.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.translator.TalknLearn.model.ui.AppLanguageState
import com.translator.TalknLearn.model.ui.CantoneseUiTexts
import com.translator.TalknLearn.model.ui.DeDeUiTexts
import com.translator.TalknLearn.model.ui.EsEsUiTexts
import com.translator.TalknLearn.model.ui.FilPhUiTexts
import com.translator.TalknLearn.model.ui.FrFrUiTexts
import com.translator.TalknLearn.model.ui.IdIdUiTexts
import com.translator.TalknLearn.model.ui.ItItUiTexts
import com.translator.TalknLearn.model.ui.JaJpUiTexts
import com.translator.TalknLearn.model.ui.KoKrUiTexts
import com.translator.TalknLearn.model.ui.MsMyUiTexts
import com.translator.TalknLearn.model.ui.PtBrUiTexts
import com.translator.TalknLearn.model.ui.RuRuUiTexts
import com.translator.TalknLearn.model.ui.ThThUiTexts
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.model.ui.ViVnUiTexts
import com.translator.TalknLearn.model.ui.ZhCnUiTexts
import com.translator.TalknLearn.model.ui.ZhTwUiTexts
import java.util.Locale

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
        // Before the user picks a UI language, follow the device language when the
        // app has texts for it. The choice is not saved, so it keeps following the
        // device until the user picks one in the language menu.
        val defaultCode = DeviceLocaleMatcher.match(
            locale = Locale.getDefault(),
            supported = hardcodedUiTexts.keys + uiLanguages[0].first,
            fallback = uiLanguages[0].first
        )
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