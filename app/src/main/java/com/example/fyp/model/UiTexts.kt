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
    HelpNotes
}

val BaseUiTexts: List<String> = listOf(
    // must match UiTextKey order exactly
    "Select the detect and translate languages below. \n" +
            "Support languages: English, Cantonese, Japanese, Mandarin...",
    "You can change the app UI language by the dropdown on top. \nPlease look at the ! information before using the app. \n" +
            "Support languages: English, Cantonese, Japanese. \nBelow is the two mode of speech-to-text. " +
            "First is the discrete recognition, second is the continuous mode, good for conversation translation.",
    "Set Speaker A and B languages below. \n" +
            "Use the toggle to switch who is speaking.",
    "Recognize (from Microphone)",
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
    "Discrete speech recognition & translation",
    "Current features",
    "Cautions",
    "- Choose UI language, detection language, and target language.\n" +
            "- Discrete mode:  Press Azure Recognize to capture short sentences/paragraph from microphone.\n" +
            "Tap Translate, then use Speak buttons to listen. \n" +
            "- Continuous mode:  Press start to continuous listen conversation from microphone.\n" +
            "You can use 🔊 to listen, but be careful if use it without earphone and the listening is still ongoing, " +
            "the speech will capture by the system.",
    "- Requires internet and valid Azure keys.\n" +
            "- Do not use for medical, legal, or safety‑critical decisions; translations may contain errors.",
    "Continuous mode",
    "Start conversation",
    "Stop listening",
    "Continuous translation mode",
    "Person A speaking",
    "Person B speaking",
    "Current string",
    "Person A",
    "Person B",
    " · translation",
    "Notes",
    "The base language of this app is English, you can use the app UI list to change the languages but it may contain error."
)

fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')
    // If translation result is shorter, fill missing entries with the English base text
    return UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts[index]
        key to value
    }.toMap()
}
