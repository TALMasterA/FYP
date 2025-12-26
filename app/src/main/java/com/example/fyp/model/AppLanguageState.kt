package com.example.fyp.model

data class AppLanguageState(
    val selectedUiLanguage: String,
    val uiTexts: Map<UiTextKey, String>
)