package com.example.fyp.model

enum class UiTextKey {
    // --- Basic UI Controls ---
    AzureRecognizeButton,
    CopyButton,
    SpeakScriptButton,
    TranslateButton,
    CopyTranslationButton,
    SpeakTranslationButton,

    // --- Status Messages ---
    RecognizingStatus,
    TranslatingStatus,
    SpeakingOriginalStatus,
    SpeakingTranslationStatus,
    SpeakingLabel,
    FinishedSpeakingOriginal,
    FinishedSpeakingTranslation,
    TtsErrorTemplate,

    // --- Dropdowns & Labels ---
    AppUiLanguageLabel,
    DetectLanguageLabel,
    TranslateToLabel,

    // --- Language Names ---
    LangEnUs,
    LangZhHk,
    LangJaJp,
    LangZhCn,
    LangFrFr,
    LangDeDe,
    LangKoKr,
    LangEsEs,

    // --- Navigation & Common Actions ---
    NavHistory,
    NavLogin,
    NavLogout,
    NavBack,
    ActionCancel,
    ActionDelete,
    ActionOpen,
    ActionName,
    ActionSave,
    ActionConfirm,

    // --- Guest Translation Limit ---
    GuestTranslationLimitTitle,
    GuestTranslationLimitMessage,

    // --- Placeholders & Input ---
    SpeechInputPlaceholder,
    SpeechTranslatedPlaceholder,
    StatusAzureErrorTemplate,
    StatusTranslationErrorTemplate,
    StatusLoginRequiredTranslation,
    StatusRecognizePreparing,
    StatusRecognizeListening,

    // --- Pagination ---
    PaginationPrevLabel,
    PaginationNextLabel,
    PaginationPageLabelTemplate,

    // --- Toast & Messages ---
    ToastCopied,
    DisableText,

    // ========== SCREEN-SPECIFIC KEYS (Start) ==========

    // --- Speech/Home Instructions ---
    SpeechInstructions,
    HomeInstructions,
    ContinuousInstructions,

    // --- Home/Help Screens ---
    HomeTitle,
    HelpTitle,
    SpeechTitle,
    HomeStartButton,
    HelpCurrentTitle,
    HelpCautionTitle,
    HelpCurrentFeatures,
    HelpCaution,
    HelpNotesTitle,
    HelpNotes,

    // --- Continuous Mode ---
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
    ContinuousPreparingMicText,
    ContinuousTranslatingText,

    // --- History Screen ---
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

    // --- Filter ---
    FilterDropdownDefault,
    FilterTitle,
    FilterLangDrop,
    FilterKeyword,
    FilterApply,
    FilterCancel,
    FilterClear,
    FilterHistoryScreenTitle,

    // --- Authentication ---
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
    AuthRegistrationDisabled,
    AuthResetEmailSent,

    // --- Password Reset ---
    ForgotPwText,
    ResetPwTitle,
    ResetPwText,
    ResetSendingText,
    ResetSendText,

    // --- Settings ---
    SettingsTitle,
    SettingsPrimaryLanguageTitle,
    SettingsPrimaryLanguageDesc,
    SettingsPrimaryLanguageLabel,
    SettingsFontSizeTitle,
    SettingsFontSizeDesc,
    SettingsScaleTemplate,
    SettingsPreviewHeadline,
    SettingsPreviewBody,
    SettingsPreviewLabel,
    SettingsAboutTitle,
    SettingsAppVersion,
    SettingsSyncInfo,
    SettingsThemeTitle,
    SettingsThemeDesc,
    SettingsThemeSystem,
    SettingsThemeLight,
    SettingsThemeDark,
    SettingsResetPW,
    SettingsNotLoggedInWarning,

    // --- Learning ---
    LearningTitle,
    LearningHintCount,
    LearningErrorTemplate,
    LearningGenerate,
    LearningRegenerate,
    LearningGenerating,
    LearningOpenSheetTemplate,
    LearningSheetTitleTemplate,
    LearningSheetPrimaryTemplate,
    LearningSheetHistoryCountTemplate,
    LearningSheetNoContent,
    LearningSheetRegenerate,
    LearningSheetGenerating,

    // --- Quiz ---
    QuizTitleTemplate,
    QuizOpenButton,
    QuizGenerateButton,
    QuizGenerating,
    QuizUpToDate,
    QuizBlocked,
    QuizWait,
    QuizMaterialsQuizTemplate,
    QuizCanEarnCoins,
    QuizNeedMoreRecordsTemplate,
    QuizCancelButton,
    QuizPreviousButton,
    QuizNextButton,
    QuizSubmitButton,
    QuizRetakeButton,
    QuizBackButton,
    QuizLoadingText,
    QuizGeneratingText,
    QuizNoMaterialsTitle,
    QuizNoMaterialsMessage,
    QuizErrorTitle,
    QuizErrorSuggestion,
    QuizCompletedTitle,
    QuizAnswerReviewTitle,
    QuizYourAnswerTemplate,
    QuizCorrectAnswerTemplate,
    QuizQuestionTemplate,
    QuizCannotRegenTemplate,
    QuizAnotherGenInProgress,
    QuizCoinRulesTitle,
    QuizCoinRulesHowToEarn,
    QuizCoinRulesRequirements,
    QuizCoinRulesCurrentStatus,
    QuizCoinRulesCanEarn,
    QuizCoinRulesNeedMoreTemplate,
    QuizCoinRule1Coin,
    QuizCoinRuleFirstAttempt,
    QuizCoinRuleMatchMaterials,
    QuizCoinRulePlus10,
    QuizCoinRuleNoDelete,
    QuizCoinRuleMaterialsTemplate,
    QuizCoinRuleQuizTemplate,
    QuizCoinRuleGotIt,
    QuizRegenConfirmTitle,
    QuizRegenCanEarnCoins,
    QuizRegenCannotEarnCoins,
    QuizRegenNeedMoreTemplate,
    QuizRegenReminder,
    QuizRegenGenerateButton,
    QuizCoinsEarnedTitle,
    QuizCoinsEarnedMessageTemplate,
    QuizCoinsRule1,
    QuizCoinsRule2,
    QuizCoinsRule3,
    QuizCoinsRule4,
    QuizCoinsRule5,
    QuizCoinsGreatButton,
    QuizOutdatedMessage,
    QuizRecordsLabel,

    // --- History Screen Coins ---
    HistoryCoinsDialogTitle,
    HistoryCoinRulesTitle,
    HistoryCoinHowToEarnTitle,
    HistoryCoinHowToEarnRule1,
    HistoryCoinHowToEarnRule2,
    HistoryCoinHowToEarnRule3,
    HistoryCoinAntiCheatTitle,
    HistoryCoinAntiCheatRule1,
    HistoryCoinAntiCheatRule2,
    HistoryCoinAntiCheatRule3,
    HistoryCoinAntiCheatRule4,
    HistoryCoinTipsTitle,
    HistoryCoinTipsRule1,
    HistoryCoinTipsRule2,
    HistoryCoinGotItButton,

    // --- Word Bank ---
    WordBankTitle,
    WordBankSelectLanguage,
    WordBankNoHistory,
    WordBankNoHistoryHint,
    WordBankWordsCount,
    WordBankGenerating,
    WordBankGenerate,
    WordBankRegenerate,
    WordBankRefresh,
    WordBankEmpty,
    WordBankEmptyHint,
    WordBankExample,
    WordBankDifficulty,
    WordBankFilterCategory,
    WordBankFilterCategoryAll,
    WordBankFilterDifficultyLabel,
    WordBankFilterNoResults,
    WordBankRefreshAvailable,
    WordBankRecordsNeeded,

    // --- Dialogs ---
    DialogLogoutTitle,
    DialogLogoutMessage,
    DialogGenerateOverwriteTitle,
    DialogGenerateOverwriteMessageTemplate,

    // --- Profile Management ---
    ProfileTitle,
    ProfileDisplayNameLabel,
    ProfileDisplayNameHint,
    ProfileUpdateButton,
    ProfileUpdateSuccess,
    ProfileUpdateError,

    // --- Account Deletion ---
    AccountDeleteTitle,
    AccountDeleteWarning,
    AccountDeleteConfirmMessage,
    AccountDeletePasswordLabel,
    AccountDeleteButton,
    AccountDeleteSuccess,
    AccountDeleteError,
    AccountDeleteReauthRequired,

    // --- Favorites ---
    FavoritesTitle,
    FavoritesEmpty,
    FavoritesAddSuccess,
    FavoritesRemoveSuccess,
    FavoritesAddButton,
    FavoritesRemoveButton,
    FavoritesNoteLabel,
    FavoritesNoteHint,

    // --- Custom Words ---
    CustomWordsTitle,
    CustomWordsAdd,
    CustomWordsEdit,
    CustomWordsDelete,
    CustomWordsOriginalLabel,
    CustomWordsTranslatedLabel,
    CustomWordsPronunciationLabel,
    CustomWordsExampleLabel,
    CustomWordsSaveSuccess,
    CustomWordsDeleteSuccess,
    CustomWordsAlreadyExists,

    // --- Language Detection ---
    LanguageDetectAuto,
    LanguageDetectDetecting,
    LanguageDetectedTemplate,
    LanguageDetectFailed,

    // --- Cache ---
    CacheClearButton,
    CacheClearSuccess,
    CacheStatsTemplate,
}

// Core UI texts - used throughout the app
val CoreUiTexts: List<String> = listOf(
    // --- Basic UI Controls ---
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

    // --- Status Messages ---
    // RecognizingStatus
    "Recording with Azure, SPEAK and please WAIT...(Stop listening after slient)",

    // TranslatingStatus
    "Translating, please wait...",

    // SpeakingOriginalStatus
    "Speaking original text, please wait...",

    // SpeakingTranslationStatus
    "Speaking translation, please wait...",

    // SpeakingLabel
    "Speaking...",

    // FinishedSpeakingOriginal
    "Finished speaking original text.",

    // FinishedSpeakingTranslation
    "Finished speaking translation.",

    // TtsErrorTemplate
    "TTS error: %s",

    // --- Dropdowns & Labels ---
    // AppUiLanguageLabel
    "App UI language",

    // DetectLanguageLabel
    "Detect",

    // TranslateToLabel
    "Translate",

    // --- Language Names ---
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

    // --- Navigation & Common Actions ---
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

    // ActionConfirm
    "Confirm",

    // --- Guest Translation Limit ---
    // GuestTranslationLimitTitle
    "Login Required",

    // GuestTranslationLimitMessage
    "You can only changed the UI language once.",

    // --- Placeholders & Input ---
    // SpeechInputPlaceholder
    "Type here or use microphone...",

    // SpeechTranslatedPlaceholder
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

    // --- Pagination ---
    // PaginationPrevLabel
    "< Prev",

    // PaginationNextLabel
    "Next >",

    // PaginationPageLabelTemplate
    "Page {page} / {total}",

    // --- Toast & Messages ---
    // ToastCopied
    "Copied to clipboard",

    // DisableText
    "Login is required to use translation features & storing translation history.",
)