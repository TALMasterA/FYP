package com.example.fyp.model.ui

data class AppLanguageState(
    val selectedUiLanguage: String,
    val uiTexts: Map<UiTextKey, String>
)