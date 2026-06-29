package com.translator.TalknLearn.model.ui

/**
 * Combined screen-specific UI texts — order must exactly match UiTextKey enum ordinal
 * (from the SCREEN-SPECIFIC KEYS section onward).
 *
 * Each sub-list is defined in its own file for maintainability:
 *   UiTextSpeech.kt, UiTextHistory.kt, UiTextAuth.kt, UiTextSettings.kt,
 *   UiTextLearning.kt, UiTextWordBank.kt, UiTextMiscFeatures.kt,
 *   UiTextFriends.kt, UiTextNotifications.kt, UiTextOnboarding.kt
 */
val ScreenUiTexts: List<String> =
    SpeechHomeScreenTexts +
    HistoryFilterScreenTexts +
    AuthScreenTexts +
    SettingsScreenTexts +
    LearningQuizScreenTexts +
    WordBankFavoritesScreenTexts +
    CustomMiscScreenTexts +
    FriendsScreenTexts +
    NotificationsExtrasScreenTexts +
    OnboardingExtrasScreenTexts
