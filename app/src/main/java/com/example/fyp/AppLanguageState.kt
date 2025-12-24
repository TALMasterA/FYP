package com.example.fyp

import com.example.fyp.UiTextKey

data class AppLanguageState(
    val selectedUiLanguage: String,
    val uiTexts: Map<UiTextKey, String>
)