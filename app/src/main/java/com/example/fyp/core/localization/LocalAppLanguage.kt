package com.example.fyp.core

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.fyp.data.ui.UpdateAppLanguage
import com.example.fyp.model.ui.AppLanguageState

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
