package com.translator.TalknLearn.model.ui

data class AppLanguageState(
    val selectedUiLanguage: String,
    val uiTexts: Map<UiTextKey, String>
)