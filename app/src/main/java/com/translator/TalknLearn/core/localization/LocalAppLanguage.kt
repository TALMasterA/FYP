package com.translator.TalknLearn.core

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.translator.TalknLearn.data.ui.UpdateAppLanguage
import com.translator.TalknLearn.model.ui.AppLanguageState

/**
 * CompositionLocal for app language state.
 * Allows screens to read the current language state directly without parameter threading.
 */
val LocalAppLanguageState = compositionLocalOf {
    AppLanguageState(
        selectedUiLanguage = "en-US",
        uiTexts = emptyMap()
    )
}

/**
 * CompositionLocal for supported UI languages.
 * List of (code, displayName) pairs.
 */
val LocalUiLanguages = staticCompositionLocalOf<List<Pair<String, String>>> {
    emptyList()
}

/**
 * CompositionLocal for the update app language callback.
 */
val LocalUpdateAppLanguage = staticCompositionLocalOf<UpdateAppLanguage> {
    { _, _ -> } // No-op default
}
