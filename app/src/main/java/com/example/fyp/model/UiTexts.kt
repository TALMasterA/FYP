package com.example.fyp.model

enum class UiTextKey {
    SpeechInstructions,
    HomeInstructions,
    ContinuousInstructions,
    AzureRecognizeButton,
    CopyButton,
    SpeakScriptButton,
    TranslateButton,
    CopyTranslationButton,
    SpeakTranslationButton,
    RecognizingStatus,
    TranslatingStatus,
    SpeakingOriginalStatus,
    SpeakingTranslationStatus,
    AppUiLanguageLabel,
    DetectLanguageLabel,
    TranslateToLabel,
    SpeakingLabel,
    FinishedSpeakingOriginal,
    FinishedSpeakingTranslation,
    TtsErrorTemplate,

    // Language names
    LangEnUs,
    LangZhHk,
    LangJaJp,
    LangZhCn,
    LangFrFr,
    LangDeDe,
    LangKoKr,
    LangEsEs,

    HomeTitle,
    HelpTitle,
    SpeechTitle,
    HomeStartButton,
    HelpCurrentTitle,
    HelpCautionTitle,
    HelpCurrentFeatures,
    HelpCaution,
    ContinuousTitle,
    ContinuousStartButton,
    ContinuousStopButton,
    ContinuousStartScreenButton,
    ContinuousPersonALabel,
    ContinuousPersonBLabel,
    ContinuousCurrentStringLabel,
    ContinuousSpeakerAName,
    ContinuousSpeakerBName,
    ContinuousTranslationSuffix,
    HelpNotesTitle,
    HelpNotes,

    NavHistory,
    NavLogin,
    NavLogout,
    NavBack,

    ActionCancel,
    ActionDelete,
    ActionOpen,
    ActionName,
    ActionSave,

    DialogLogoutTitle,
    DialogLogoutMessage,

    HistoryTitle,
    HistoryTabDiscrete,
    HistoryTabContinuous,
    HistoryNoContinuousSessions,
    HistoryNoDiscreteRecords,

    DialogDeleteRecordTitle,
    DialogDeleteRecordMessage,
    DialogDeleteSessionTitle,
    DialogDeleteSessionMessage,
    HistoryDeleteSessionButton,

    HistoryNameSessionTitle,
    HistorySessionNameLabel,
    HistorySessionTitleTemplate,
    HistoryItemsCountTemplate,

    AuthLoginTitle,
    AuthRegisterTitle,
    AuthLoginHint,
    AuthRegisterRules,
    AuthEmailLabel,
    AuthPasswordLabel,
    AuthConfirmPasswordLabel,
    AuthLoginButton,
    AuthRegisterButton,
    AuthToggleToRegister,
    AuthToggleToLogin,
    AuthErrorPasswordsMismatch,
    AuthErrorPasswordTooShort,

    DisableText,
    ForgotPwText,
    ResetPwTitle,
    ResetPwText,
    ResetSendingText,
    ResetSendText,

    SpeechInputPlaceholder,
    SpeechTranslatedPlaceholder,
    StatusAzureErrorTemplate,
    StatusTranslationErrorTemplate,
    StatusLoginRequiredTranslation,
    StatusRecognizePreparing,
    StatusRecognizeListening,

    PaginationPrevLabel,
    PaginationNextLabel,
    PaginationPageLabelTemplate,

    ContinuousPreparingMicText,
    ContinuousTranslatingText,

    // --- Learning ---
    LearningTitle,
    LearningHintCount,
    LearningErrorTemplate,                // "Error: %s"
    LearningGenerate,
    LearningRegenerate,
    LearningGenerating,
    LearningOpenSheetTemplate,            // "{language} Sheet"

    // --- Learning Sheet ---
    LearningSheetTitleTemplate,           // "{language} Sheet"
    LearningSheetPrimaryTemplate,         // "Primary: {language}"
    LearningSheetHistoryCountTemplate,    // "History count now: {nowCount} (saved at gen: {savedCount})"
    LearningSheetNoContent,
    LearningSheetRegenerate,
    LearningSheetGenerating,

    // --- Settings ---
    SettingsTitle,
    SettingsPrimaryLanguageTitle,
    SettingsPrimaryLanguageDesc,
    SettingsPrimaryLanguageLabel,
    SettingsFontSizeTitle,
    SettingsFontSizeDesc,
    SettingsScaleTemplate,                // "Scale: {pct}%"
    SettingsPreviewHeadline,
    SettingsPreviewBody,
    SettingsPreviewLabel,
    SettingsAboutTitle,
    SettingsAppVersion,                   // "Talk & Learn Translator v"
    SettingsSyncInfo,                     // "Your preferences are automatically saved..."
    FilterDropdownDefault,
    FilterTitle,
    FilterLangDrop,
    FilterKeyword,
    FilterApply,
    FilterCancel,
    FilterClear,
    FilterHistoryScreenTitle,
    SettingsThemeTitle,
    SettingsThemeDesc,
    SettingsThemeSystem,
    SettingsThemeLight,
    SettingsThemeDark,

}

val BaseUiTexts: List<String> = listOf(
    // must match UiTextKey order exactly

    // SpeechInstructions
    "Select the detect and translate languages below. Only suitable for short pharse. \n" +
            "Support languages: English, Cantonese, Japanese, Mandarin... \n" +
            "Switch button for swiping the languages selected",

    // HomeInstructions
    "You can change the app UI language by the dropdown on top. \n" +
            "Please look at the ! information before using the app. \n" +
            "Below is the two mode of speech-to-text: \n" +
            "First is the discrete recognition for texting, second is the continuous mode for face communication.",

    // ContinuousInstructions
    "Set Speaker A and B languages below. \n" +
            "Use the toggle to switch who is speaking.",

    // AzureRecognizeButton
    "Use Microphone",

    // CopyButton
    "Copy",

    // SpeakScriptButton
    "Speak",

    // TranslateButton
    "Translate",

    // CopyTranslationButton
    "Copy",

    // SpeakTranslationButton
    "Speak",

    // RecognizingStatus
    "Recording with Azure, SPEAK and please WAIT...(Stop listening after slient)",

    // TranslatingStatus
    "Translating, please wait...",

    // SpeakingOriginalStatus
    "Speaking original text, please wait...",

    // SpeakingTranslationStatus
    "Speaking translation, please wait...",

    // AppUiLanguageLabel
    "App UI language",

    // DetectLanguageLabel
    "Detect",

    // TranslateToLabel
    "Translate",

    // SpeakingLabel
    "Speaking...",

    // FinishedSpeakingOriginal
    "Finished speaking original text.",

    // FinishedSpeakingTranslation
    "Finished speaking translation.",

    // TtsErrorTemplate
    "TTS error: %s",

    // LangEnUs
    "English",

    // LangZhHk
    "Cantonese",

    // LangJaJp
    "Japanese",

    // LangZhCn
    "Mandarin",

    // LangFrFr
    "French",

    // LangDeDe
    "German",

    // LangKoKr
    "Korean",

    // LangEsEs
    "Spanish",

    // HomeTitle
    "Translator",

    // HelpTitle
    "Notes & Cautions",

    // SpeechTitle
    "Discrete mode",

    // HomeStartButton
    "Discrete speech translation",

    // HelpCurrentTitle
    "Current features",

    // HelpCautionTitle
    "Cautions",

    // HelpCurrentFeatures
    "- Choose UI language, detection language, and target language. \n" +
            "- Discrete mode: For short sentences translation. \n" +
            "- Continuous mode: Live conversation capture with user switching. \n" +
            "- Learning: Learning materials generate by AI (use your translation history). \n" +
            "- App UI language translation. \n",

    // HelpCaution
    "- Requires internet connection. \n" +
            "- Microphone audio is used for speech recognition; avoid speaking sensitive info in public. \n" +
            "- Use earphones if using 🔊 during continuous listening to avoid the app re-capturing its own audio. \n" +
            "- Do not use for medical/legal/safety-critical decisions; recognition/translation may be wrong. \n",

    // ContinuousTitle
    "Continuous mode",

    // ContinuousStartButton
    "Start conversation",

    // ContinuousStopButton
    "Stop listening",

    // ContinuousStartScreenButton
    "Continuous translation mode",

    // ContinuousPersonALabel
    "A speaking",

    // ContinuousPersonBLabel
    "B speaking",

    // ContinuousCurrentStringLabel
    "Current string: ",

    // ContinuousSpeakerAName
    "Person A",

    // ContinuousSpeakerBName
    "Person B",

    // ContinuousTranslationSuffix
    " · translation",

    // HelpNotesTitle
    "Notes",

    // HelpNotes
    "- The base language of this app is English, you can use the app UI list to change the languages but it may contain error. \n" +
            "- If some words have not translated, select the language again. \n",

    // NavHistory
    "History",

    // NavLogin
    "Login",

    // NavLogout
    "Logout",

    // NavBack
    "Back",

    // ActionCancel
    "Cancel",

    // ActionDelete
    "Delete",

    // ActionOpen
    "Open",

    // ActionName
    "Name",

    // ActionSave
    "Save",

    // DialogLogoutTitle
    "Logout?",

    // DialogLogoutMessage
    "You will need to login again to use translation function / store + view your history.",

    // HistoryTitle
    "History",

    // HistoryTabDiscrete
    "Discrete",

    // HistoryTabContinuous
    "Continuous",

    // HistoryNoContinuousSessions
    "No session(s) yet.",

    //HistoryNoDiscreteRecords
    "No record(s) yet.",

    // DialogDeleteRecordTitle
    "Delete record?",

    // DialogDeleteRecordMessage
    "This action cannot be undone.",

    // DialogDeleteSessionTitle
    "Delete session?",

    // DialogDeleteSessionMessage
    "All record(s) in this session will be deleted. This action cannot be undone.",

    // HistoryDeleteSessionButton
    "Delete",

    // HistoryNameSessionTitle
    "Name",

    // HistorySessionNameLabel
    "Session name",

    // HistorySessionTitleTemplate
    "Session {id}",

    // HistoryItemsCountTemplate
    "{count} item(s)",

    // AuthLoginTitle
    "Login",

    // AuthRegisterTitle
    "Register (Disabled)",

    // AuthLoginHint
    "Use your registered (provided) email and password.",

    // AuthRegisterRules
    "Register is disabled in development stage. \n" +
            "Caution: You cannot reset password if you use email that not exist. \n" +
            "Register rules: \n" +
            "• Email must be a valid format (e.g., name@example.com) \n" +
            "• Password must be at least 6 characters \n" +
            "• Confirm password must match",

    // AuthEmailLabel
    "Email",

    // AuthPasswordLabel
    "Password",

    // AuthConfirmPasswordLabel
    "Confirm password",

    // AuthLoginButton
    "Login",

    // AuthRegisterButton
    "Register",

    // AuthToggleToRegister
    "Don't have account? Register (Disabled)",

    // AuthToggleToLogin
    "Have account? Login",

    // AuthErrorPasswordsMismatch
    "Passwords do not match.",

    // AuthErrorPasswordTooShort
    "Password must be at least 6 characters.",

    // DisableText
    "Login is required to use translation features & storing translation history.",

    // ForgotPwText
    "Forgot password? Reset here",

    //ResetPwTitle
    "Reset Password",

    // ResetPwText
    "Enter your account email and a reset link will be sent. \n" +
            "Make sure the email is real & register for the app or no email will be sent. \n",

    // ResetSendingText
    "Sending...",

    // ResetSendText
    "Send reset email",

    // SpeechInputPlaceholder
    "Type here or use microphone...",

    //SpeechTranslatedPlaceholder
    "The translated result will be show here.",

    // StatusAzureErrorTemplate
    "Azure error: %s",

    // StatusTranslationErrorTemplate
    "Translation error: %s",

    // StatusLoginRequiredTranslation
    "Login is required to use translation.",

    // StatusRecognizePreparing
    "Preparing mic... (Do not speak now)",

    // StatusRecognizeListening
    "Listening... Please speak now.",

    // PaginationPrevLabel
    "< Prev",

    // PaginationNextLabel
    "Next >",

    // PaginationPageLabelTemplate
    "Page {page} / {total}",

    // ContinuousPreparingMicText
    "Preparing mic... (Do not speak now)",

    // ContinuousTranslatingText
    "Translating...",

    // --- Learning ---
    "Learning",
    "(*) Count = number of history records involving this language.",
    "Error: %s",
    "Generate",
    "Re-generate",
    "Generating...",
    "{speclanguage} Sheet",

    // --- Learning Sheet ---
    "{speclanguage} Sheet",
    "Primary language: {speclanguage}",
    "History count now: {nowCount} (saved at gen: {savedCount})",
    "No sheet content yet.",
    "Re-gen",
    "Generating...",

    // --- Settings ---
    "Settings",
    "Primary Language",
    "Used for learning explanations and recommendations",
    "Primary language",
    "Font Size",
    "Adjust text size for better readability (synced across devices)",
    "Scale: {pct}%",
    "Headline: Large text preview",
    "Body: This is normal text preview",
    "Label: Small text preview",
    "About",
    "Talk & Learn Translator v",
    "Your preferences are automatically saved and synced to your account.",
    "All languages",
    "Filter history",
    "Language",
    "Keyword",
    "Apply",
    "Cancel",
    "Clear",
    "Filter",
    "Theme",
    "Follow system / Light / Dark",
    "Follow system",
    "Light",
    "Dark",
)

fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')
    val map = UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts[index]
        key to value
    }.toMap().toMutableMap()

    // Ensure tokens exist (translation sometimes breaks/removes them)
    fun ensureContains(key: UiTextKey, vararg tokens: String) {
        val v = map[key] ?: BaseUiTexts[key.ordinal]
        if (tokens.any { !v.contains(it) }) {
            map[key] = BaseUiTexts[key.ordinal]
        }
    }

    ensureContains(UiTextKey.HistorySessionTitleTemplate, "{id}")
    ensureContains(UiTextKey.HistoryItemsCountTemplate, "{count}")
    ensureContains(UiTextKey.PaginationPageLabelTemplate, "{page}", "{total}")

    ensureContains(UiTextKey.LearningOpenSheetTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetTitleTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetPrimaryTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetHistoryCountTemplate, "{nowCount}", "{savedCount}")
    ensureContains(UiTextKey.SettingsScaleTemplate, "{pct}")

    return map
}

fun baseUiTextsHash(): Int {
    return BaseUiTexts.joinToString(separator = "\u0001").hashCode()
}