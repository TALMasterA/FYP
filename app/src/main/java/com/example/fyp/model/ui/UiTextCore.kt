package com.example.fyp.model.ui

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
    LangIdId,
    LangViVn,
    LangThTh,
    LangFilPh,
    LangMsMy,
    LangPtBr,
    LangItIt,
    LangRuRu,

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

    // --- Standard UI Components ---
    ErrorRetryButton,
    ErrorGenericMessage,

    // --- Shop ---
    ShopTitle,
    ShopCoinBalance,
    ShopHistoryExpansionTitle,
    ShopHistoryExpansionDesc,
    ShopCurrentLimit,
    ShopMaxLimit,
    ShopBuyHistoryExpansion,
    ShopInsufficientCoins,
    ShopMaxLimitReached,
    ShopHistoryExpandedTitle,
    ShopHistoryExpandedMessage,
    ShopColorPaletteTitle,
    ShopColorPaletteDesc,
    ShopEntry,

    // --- Voice Settings Screen ---
    VoiceSettingsTitle,
    VoiceSettingsDesc,

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
    HomeFeaturesTitle,
    HomeDiscreteDescription,
    HomeContinuousDescription,
    HomeLearningDescription,
    HelpCurrentTitle,
    HelpCautionTitle,
    HelpCurrentFeatures,
    HelpCaution,
    HelpNotesTitle,
    HelpNotes,

    // --- Feedback ---
    FeedbackTitle,
    FeedbackDesc,
    FeedbackMessagePlaceholder,
    FeedbackSubmitButton,
    FeedbackSubmitting,
    FeedbackSuccessTitle,
    FeedbackSuccessMessage,
    FeedbackErrorTitle,
    FeedbackErrorMessage,
    FeedbackMessageRequired,

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
    SettingsColorPaletteTitle,
    SettingsColorPaletteDesc,
    SettingsColorCostTemplate,
    SettingsColorUnlockButton,
    SettingsColorSelectButton,
    SettingsColorAlreadyUnlocked,
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
    SettingsThemeScheduled,
    SettingsResetPW,
    SettingsQuickLinks,
    SettingsNotLoggedInWarning,
    SettingsVoiceTitle,
    SettingsVoiceDesc,
    SettingsVoiceLanguageLabel,
    SettingsVoiceSelectLabel,
    SettingsVoiceDefault,

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
    LearningRegenBlockedTitle,
    LearningRegenBlockedMessage,
    LearningRegenNeedMoreRecords,
    LearningRegenCountNotHigher,
    LearningRegenInfoTitle,
    LearningRegenInfoMessage,
    QuizRegenBlockedSameMaterial,

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

    // --- History Info Dialog ---
    HistoryInfoTitle,
    HistoryInfoLimitMessage,
    HistoryInfoOlderRecordsMessage,
    HistoryInfoFavoritesMessage,
    HistoryInfoViewFavoritesMessage,
    HistoryInfoFilterMessage,
    HistoryInfoGotItButton,

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
    WordBankRegenInfoTitle,
    WordBankRegenInfoMessage,
    WordBankHistoryCountTemplate,

    // --- Dialogs ---
    DialogLogoutTitle,
    DialogLogoutMessage,
    DialogGenerateOverwriteTitle,
    DialogGenerateOverwriteMessageTemplate,

    // --- Profile Management ---
    ProfileTitle,
    ProfileUsernameLabel,
    ProfileUsernameHint,
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
    CustomWordsOriginalLanguageLabel,
    CustomWordsTranslationLanguageLabel,
    CustomWordsSaveButton,
    CustomWordsCancelButton,

    // --- Language Detection ---
    LanguageDetectAuto,
    LanguageDetectDetecting,
    LanguageDetectedTemplate,
    LanguageDetectFailed,

    // --- Image Recognition (OCR) ---
    ImageRecognitionButton,
    ImageRecognitionAccuracyWarning,
    ImageRecognitionScanning,
    ImageRecognitionSuccess,

    // --- Cache ---
    CacheClearButton,
    CacheClearSuccess,
    CacheStatsTemplate,

    // --- Auto Theme ---
    SettingsAutoThemeTitle,
    SettingsAutoThemeDesc,
    SettingsAutoThemeEnabled,
    SettingsAutoThemeDisabled,
    SettingsAutoThemeDarkStartLabel,
    SettingsAutoThemeLightStartLabel,
    SettingsAutoThemePreview,

    // --- Offline Mode ---
    OfflineModeTitle,
    OfflineModeMessage,
    OfflineModeRetry,
    OfflineDataCached,
    OfflineSyncPending,

    // --- Image Capture ---
    ImageSourceTitle,
    ImageSourceCamera,
    ImageSourceGallery,
    ImageSourceCancel,
    CameraCaptureContentDesc,

    // --- Friends System ---
    FriendsTitle,
    FriendsMenuButton,
    FriendsAddButton,
    FriendsSearchTitle,
    FriendsSearchPlaceholder,
    FriendsSearchMinChars,
    FriendsSearchNoResults,
    FriendsListEmpty,
    FriendsRequestsSection,
    FriendsSectionTitle,
    FriendsAcceptButton,
    FriendsRejectButton,
    FriendsRemoveButton,
    FriendsRemoveDialogTitle,
    FriendsRemoveDialogMessage,
    FriendsSendRequestButton,
    FriendsRequestSentSuccess,
    FriendsRequestAcceptedSuccess,
    FriendsRequestRejectedSuccess,
    FriendsRemovedSuccess,
    FriendsRequestFailed,
    FriendsCloseButton,
    FriendsCancelButton,
    FriendsRemoveConfirm,
    FriendsNewRequestsTemplate,
    FriendsSentRequestsSection,
    FriendsPendingStatus,
    FriendsCancelRequestButton,
    FriendsUnreadMessageDesc,
    FriendsDeleteModeButton,
    FriendsDeleteSelectedButton,
    FriendsDeleteMultipleTitle,
    FriendsDeleteMultipleMessage,
    FriendsSearchMinChars3,
    FriendsSearchByUserIdHint,

    // --- Chat ---
    ChatTitle,
    ChatInputPlaceholder,
    ChatSendButton,
    ChatEmpty,
    ChatMessageSent,
    ChatMessageFailed,
    ChatMarkingRead,
    ChatLoadingMessages,
    ChatToday,
    ChatYesterday,
    ChatUnreadBadge,
    
    // --- Chat Translation ---
    ChatTranslateButton,
    ChatTranslateDialogTitle,
    ChatTranslateDialogMessage,
    ChatTranslateConfirm,
    ChatTranslating,
    ChatTranslated,
    ChatShowOriginal,
    ChatShowTranslation,
    ChatTranslateFailed,
    ChatTranslatedLabel,

    // --- Sharing Feature ---
    ShareTitle,
    ShareInboxTitle,
    ShareInboxEmpty,
    ShareWordButton,
    ShareMaterialButton,
    ShareSelectFriendTitle,
    ShareSelectFriendMessage,
    ShareSuccess,
    ShareFailed,
    ShareWordWith,
    ShareMaterialWith,
    ShareAcceptButton,
    ShareDismissButton,
    ShareAccepted,
    ShareDismissed,
    ShareActionFailed,
    ShareTypeWord,
    ShareTypeLearningSheet,
    ShareTypeQuiz,
    ShareReceivedFrom,
    ShareNewItemsTemplate,
    ShareViewFullMaterial,
    ShareDeleteItemTitle,
    ShareDeleteItemMessage,
    ShareDeleteButton,
    ShareViewButton,
    ShareItemNotFound,
    ShareNoContent,

    // --- My Profile ---
    MyProfileTitle,
    MyProfileUserId,
    MyProfileUsername,
    MyProfileDisplayName,
    MyProfileCopyUserId,
    MyProfileCopyUsername,
    MyProfileShare,
    MyProfileCopied,
    MyProfileLanguages,
    MyProfilePrimaryLanguage,
    MyProfileLearningLanguages,

    // --- Friends Info Dialog & Empty States ---
    FriendsInfoTitle,
    FriendsInfoMessage,
    FriendsEmptyTitle,
    FriendsEmptyMessage,
    FriendsInfoGotItButton,

    // --- Learning Info Dialog & Empty States ---
    LearningInfoTitle,
    LearningInfoMessage,
    LearningEmptyTitle,
    LearningEmptyMessage,
    LearningInfoGotItButton,

    // --- Word Bank Info Dialog & Empty States ---
    WordBankInfoTitle,
    WordBankInfoMessage,
    WordBankInfoGotItButton,

    // --- SharedInbox Info Dialog ---
    ShareInboxInfoTitle,
    ShareInboxInfoMessage,
    ShareInboxInfoGotItButton,

    // --- Accessibility Strings ---
    AccessibilityDismiss,
    AccessibilityAlreadyConnectedOrPending,
    AccessibilityNewMessages,
    AccessibilityNewReleasesIcon,
    AccessibilitySuccessIcon,
    AccessibilityErrorIcon,
    AccessibilitySharedItemTypeIcon,
    AccessibilityAddCustomWords,
    AccessibilityWordBankExists,
}

// Core Ui Texts list
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
    // LangIdId
    "Indonesian",
    // LangViVn
    "Vietnamese",
    // LangThTh
    "Thai",
    // LangFilPh
    "Filipino",
    // LangMsMy
    "Malay",
    // LangPtBr
    "Portuguese",
    // LangItIt
    "Italian",
    // LangRuRu
    "Russian",

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
    "You have already changed the UI language once. Please login to change it again. Switching between cached languages is free.",

    // --- Placeholders & Input ---
    // SpeechInputPlaceholder
    "Type here or use microphone...",
    // SpeechTranslatedPlaceholder
    "The translated result will be shown here.",
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

    // --- Standard UI Components ---
    // ErrorRetryButton
    "Retry",
    // ErrorGenericMessage
    "An error occurred. Please try again.",

    // --- Shop ---
    // ShopTitle
    "Shop",
    // ShopCoinBalance
    "Your Coins",
    // ShopHistoryExpansionTitle
    "History View Expansion",
    // ShopHistoryExpansionDesc
    "Expand your history view limit to see more recent translation records.",
    // ShopCurrentLimit
    "Current limit: {limit} records",
    // ShopMaxLimit
    "Max:",
    // ShopBuyHistoryExpansion
    "Buy +{increment} records ({cost} coins)",
    // ShopInsufficientCoins
    "Not enough coins for this purchase.",
    // ShopMaxLimitReached
    "Maximum limit reached!",
    // ShopHistoryExpandedTitle
    "History Expand?",
    // ShopHistoryExpandedMessage
    "Your history limit has been expanded to {limit} records. You can now view more of your translation history!",
    // ShopColorPaletteTitle
    "Color Palettes",
    // ShopColorPaletteDesc
    "Unlock new color themes for your app.",
    // ShopEntry
    "Shop",

    // --- Voice Settings Screen ---
    // VoiceSettingsTitle
    "Voice Settings",
    // VoiceSettingsDesc
    "Choose a voice for text-to-speech output per language.",
)