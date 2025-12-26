package com.example.fyp.model

enum class UiTextKey {
    Instructions,
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
    ContinuousTranslationSuffix
}

val BaseUiTexts: List<String> = listOf(
    // must match UiTextKey order exactly
    "Select User Interface language on top, then the detect and translate languages. " +
            "Support languages: English, Cantonese, Japanese, Mandarin...",
    "Use Azure Recognize (from Mic)",
    "Copy",
    "Speak script",
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
    "FYP Translator",
    "How to use",
    "Speech & Translation",
    "Start speech & translation",
    "Current features",
    "Caution",
    "- Choose UI language, detection language, and target language.\n" +
            "- Press Azure Recognize to capture one sentence from microphone.\n" +
            "- Tap Translate to translate once, then use Speak buttons to listen.",
    "- Requires internet and valid Azure keys.\n" +
            "- Do not use for medical, legal, or safety‑critical decisions; translations may contain errors.",
    "Continuous conversation",
    "Start conversation",
    "Stop listening",
    "Start continuous translate & speak",
    "Person A speaking",
    "Person B speaking",
    "Current string",
    "Person A",
    "Person B",
    " · translation"
)

fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')
    // If translation result is shorter, fill missing entries with the English base text
    return UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts[index]
        key to value
    }.toMap()
}
