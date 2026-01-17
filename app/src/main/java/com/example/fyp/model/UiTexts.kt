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
    ResetPwText,
    ResetSendingText,
    ResetSendText,
    SpeechInputPlaceholder,
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
}

val BaseUiTexts: List<String> = listOf(
    // must match UiTextKey order exactly

    // SpeechInstructions
    "Select the detect and translate languages below.\n" +
            "Support languages: English, Cantonese, Japanese, Mandarin...",
    // HomeInstructions
    "You can change the app UI language by the dropdown on top.\nPlease look at the ! information before using the app.\n" +
            "Below is the two mode of speech-to-text. " +
            "First is the discrete recognition for texting, second is the continuous mode for face communication.",
    // ContinuousInstructions
    "Set Speaker A and B languages below.\n" +
            "Use the toggle to switch who is speaking.",
    // AzureRecognizeButton
    "Microphone Recognize",
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
    "Recording with Azure, SPEAK and please WAIT...",
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
    "- Choose UI language, detection language, and target language.\n" +
            "- Discrete mode: Speech-to-text → Translate → Speak out.\n" +
            "- Continuous mode: Live conversation capture with Speaker A/B switching.\n" +
            "- Login (Email/Password) for storing translation history.\n" +
            "- App UI language translation.",
    // HelpCaution
    "- Requires internet connection.\n" +
            "- Microphone audio is used for speech recognition; avoid speaking sensitive info in public.\n" +
            "- Use earphones if using 🔊 during continuous listening to avoid the app re-capturing its own audio.\n" +
            "- Do not use for medical/legal/safety-critical decisions; recognition/translation may be wrong.",
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
    "Current string",
    // ContinuousSpeakerAName
    "Person A",
    // ContinuousSpeakerBName
    "Person B",
    // ContinuousTranslationSuffix
    " · translation",
    // HelpNotesTitle
    "Notes",
    // HelpNotes
    "The base language of this app is English, you can use the app UI list to change the languages but it may contain error.\n" +
            "App project goal: build a translator app and store translation history in a database for (future) learning features.\n" +
            "Planned learning part: use saved history to extract frequent vocabulary/phrases and generate practice content.",
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
    "You will need to login again to store & view your history.",
    // HistoryTitle
    "History",
    // HistoryTabDiscrete
    "Discrete",
    // HistoryTabContinuous
    "Continuous",
    // HistoryNoContinuousSessions
    "No record(s) yet.",
    // DialogDeleteRecordTitle
    "Delete record?",
    // DialogDeleteRecordMessage
    "This action cannot be undone.",
    // DialogDeleteSessionTitle
    "Delete session?",
    // DialogDeleteSessionMessage
    "All records in this session will be deleted. This action cannot be undone.",
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
    "Register",
    // AuthLoginHint
    "Use your registered (provided) email and password.",
    // AuthRegisterRules
    "Register is disabled in development stage.\n" +
            "Caution: You cannot reset password if you use email that not exist\n" +
            "Register rules:\n" +
            "• Email must be a valid format (e.g., name@example.com)\n" +
            "• Password must be at least 6 characters\n" +
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
    // ResetPwText
    "Enter your account email and a reset link will be sent." +
            "Make sure the email is real & register for the app or no email will be sent.",
    // ResetSendingText
    "Sending...",
    // ResetSendText
    "Send reset email",

    // SpeechInputPlaceholder
    "Type here or use microphone...",
    // StatusAzureErrorTemplate
    "Azure error: %s",
    // StatusTranslationErrorTemplate
    "Translation error: %s",
    // StatusLoginRequiredTranslation
    "Login is required to use translation.",
    // StatusRecognizePreparing
    "Preparing mic...",
    // StatusRecognizeListening
    "Listening... Please speak now.",
    // PaginationPrevLabel
    "Prev",
    // PaginationNextLabel
    "Next",
    // PaginationPageLabelTemplate
    "Page {page} / {total}",

    // ContinuousPreparingMicText
    "Preparing mic...",
    // ContinuousTranslatingText
    "Translating...",
)

fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')
    val map = UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts[index]
        key to value
    }.toMap().toMutableMap()

    // Ensure tokens exist (translation sometimes breaks/removes them)
    val sessionTemplate =
        map[UiTextKey.HistorySessionTitleTemplate] ?: BaseUiTexts[UiTextKey.HistorySessionTitleTemplate.ordinal]
    if (!sessionTemplate.contains("{id}")) {
        map[UiTextKey.HistorySessionTitleTemplate] =
            BaseUiTexts[UiTextKey.HistorySessionTitleTemplate.ordinal]
    }

    val countTemplate =
        map[UiTextKey.HistoryItemsCountTemplate] ?: BaseUiTexts[UiTextKey.HistoryItemsCountTemplate.ordinal]
    if (!countTemplate.contains("{count}")) {
        map[UiTextKey.HistoryItemsCountTemplate] =
            BaseUiTexts[UiTextKey.HistoryItemsCountTemplate.ordinal]
    }

    val pageTemplate =
        map[UiTextKey.PaginationPageLabelTemplate] ?: BaseUiTexts[UiTextKey.PaginationPageLabelTemplate.ordinal]
    if (!pageTemplate.contains("{page}") || !pageTemplate.contains("{total}")) {
        map[UiTextKey.PaginationPageLabelTemplate] =
            BaseUiTexts[UiTextKey.PaginationPageLabelTemplate.ordinal]
    }

    return map
}

fun baseUiTextsHash(): Int {
    return BaseUiTexts.joinToString(separator = "\u0001").hashCode()
}