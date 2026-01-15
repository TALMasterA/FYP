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
    DialogLogoutMessage,HistoryTitle,
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
    DisableText
}

val BaseUiTexts: List<String> = listOf(
    // must match UiTextKey order exactly
    "Select the detect and translate languages below. \n" +
            "Support languages: English, Cantonese, Japanese, Mandarin...",
    "You can change the app UI language by the dropdown on top. \nPlease look at the ! information before using the app. \n" +
            "Below is the two mode of speech-to-text. " +
            "First is the discrete recognition for texting, second is the continuous mode for face communication.",
    "Set Speaker A and B languages below. \n" +
            "Use the toggle to switch who is speaking.",
    "Microphone Recognize",
    "Copy speech",
    "Speak",
    "Translate",
    "Copy Translation",
    "Speak Translation",
    "Recording with Azure, SPEAK and please WAIT...",
    "Translating, please wait...",
    "Speaking original text, please wait...",
    "Speaking translation, please wait...",
    "App UI language",
    "Detect language",
    "Translate to",
    "Speaking...",
    "Finished speaking original text.",
    "Finished speaking translation.",
    "TTS error: %s",
    "English",
    "Cantonese",
    "Japanese",
    "Mandarin",
    "French",
    "German",
    "Korean",
    "Spanish",
    "Translator",
    "Notes & Cautions",
    "Discrete mode",
    "Discrete speech translation",
    "Current features",
    "Cautions",
    "- Choose UI language, detection language, and target language.\n" +
            "- Discrete mode: Speech-to-text → Translate → Speak out.\n" +
            "- Continuous mode: Live conversation capture with Speaker A/B switching.\n" +
            "- Login (Email/Password) for storing translation history.\n" +
            "- App UI language translation.",
    "- Requires internet connection.\n" +
            "- Microphone audio is used for speech recognition; avoid speaking sensitive info in public.\n" +
            "- Use earphones if using 🔊 during continuous listening to avoid the app re-capturing its own audio.\n" +
            "- Do not use for medical/legal/safety-critical decisions; recognition/translation may be wrong.",
    "Continuous mode",
    "Start conversation",
    "Stop listening",
    "Continuous translation mode",
    "A speaking",
    "B speaking",
    "Current string",
    "Person A",
    "Person B",
    " · translation",
    "Notes",
    "The base language of this app is English, you can use the app UI list to change the languages but it may contain error. \n" +
            "App project goal: build a translator app and store translation history in a database for (future) learning features.\n" +
            "Planned learning part: use saved history to extract frequent vocabulary/phrases and generate practice content.",
    "History",
    "Login",
    "Logout",
    "Back",
    "Cancel",
    "Delete",
    "Open",
    "Name",
    "Save",
    "Logout?",
    "You will need to login again to store & view your history.",
    "History",
    "Discrete",
    "Continuous",
    "No record(s) yet.",
    "Delete record?",
    "This action cannot be undone.",
    "Delete session?",
    "All records in this session will be deleted. This action cannot be undone.",
    "Delete",
    "Name",
    "Session name",
    "Session {id}",
    "{count} item(s)",
    "Login",
    "Register",
    "Use your registered (provided) email and password.",
    "Register is disabled in development stage.\n" +
    "Register rules:\n" +
            "• Email must be a valid format (e.g., name@example.com)\n" +
            "• Password must be at least 6 characters\n" +
            "• Confirm password must match",
    "Email",
    "Password",
    "Confirm password",
    "Login",
    "Register",
    "Don't have account? Register (Disabled)",
    "Have account? Login",
    "Passwords do not match.",
    "Password must be at least 6 characters.",
    "Login is required to use translation features & storing translation history."
)

fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')

    val map = UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts[index]
        key to value
    }.toMap().toMutableMap()

    // Ensure tokens exist (translation sometimes breaks/removes them)
    val sessionTemplate = map[UiTextKey.HistorySessionTitleTemplate] ?: BaseUiTexts[UiTextKey.HistorySessionTitleTemplate.ordinal]
    if (!sessionTemplate.contains("{id}")) {
        map[UiTextKey.HistorySessionTitleTemplate] = BaseUiTexts[UiTextKey.HistorySessionTitleTemplate.ordinal] // "Session {id}"
    }

    val countTemplate = map[UiTextKey.HistoryItemsCountTemplate] ?: BaseUiTexts[UiTextKey.HistoryItemsCountTemplate.ordinal]
    if (!countTemplate.contains("{count}")) {
        map[UiTextKey.HistoryItemsCountTemplate] = BaseUiTexts[UiTextKey.HistoryItemsCountTemplate.ordinal] // "{count} items"
    }

    return map
}

fun baseUiTextsHash(): Int {
    return BaseUiTexts.joinToString(separator = "\u0001").hashCode()
}
