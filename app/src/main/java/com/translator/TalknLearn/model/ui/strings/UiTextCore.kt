package com.translator.TalknLearn.model.ui

enum class UiTextKey {
    // --- Basic UI Controls ---
    AzureRecognizeButton,
    CopyButton,
    SpeakScriptButton,
    TranslateButton,
    CopyTranslationButton,
    SpeakTranslationButton,

    // --- Status Messages ---

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

    // --- Placeholders & Input ---
    SpeechInputPlaceholder,
    SpeechTranslatedPlaceholder,

    // --- Pagination ---
    PaginationPrevLabel,
    PaginationNextLabel,
    PaginationPageLabelTemplate,

    // --- Toast & Messages ---
    DisableText,

    // --- Standard UI Components ---
    ErrorRetryButton,

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
    AuthGoogleSignInButton,
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
    SettingsColorCostTemplate,
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
    LearningSheetWhatIsThisTitle,
    LearningSheetWhatIsThisDesc,
    LearningRegenBlockedTitle,
    LearningRegenBlockedMessage,
    LearningRegenInfoTitle,
    LearningRegenInfoMessage,

    // --- Quiz ---
    QuizTitleTemplate,
    QuizOpenButton,
    QuizGenerateButton,
    QuizGenerating,
    QuizBlocked,
    QuizWait,
    QuizCanEarnCoins,
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
    QuizRecordsLabel,

    // --- History Screen Coins ---

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
    WordBankRefresh,
    WordBankEmpty,
    WordBankEmptyHint,
    WordBankExample,
    WordBankDifficulty,
    WordBankFilterCategory,
    WordBankFilterCategoryAll,
    WordBankFilterDifficultyLabel,
    WordBankFilterNoResults,
    WordBankRegenInfoTitle,
    WordBankRegenInfoMessage,

    // --- Dialogs ---
    DialogLogoutTitle,
    DialogLogoutMessage,
    DialogGenerateOverwriteTitle,
    DialogGenerateOverwriteMessageTemplate,

    // --- Profile Management ---
    ProfileTitle,
    ProfileUsernameLabel,
    ProfileUsernameHint,
    ProfileUpdateButton,

    // --- Account Deletion ---
    AccountDeleteTitle,
    AccountDeleteWarning,
    AccountDeleteConfirmMessage,
    AccountDeletePasswordLabel,
    AccountDeleteButton,

    // --- Favorites ---
    FavoritesTitle,
    FavoritesEmpty,
    FavoritesTabRecords,
    FavoritesTabSessions,
    FavoritesSessionsEmpty,
    FavoritesSessionItemsTemplate,
    FavoritesLimitTitle,
    FavoritesLimitMessage,
    FavoritesLimitGotIt,
    FavoritesInfoTitle,
    FavoritesInfoMessage,
    FavoritesInfoGotIt,

    // --- Custom Words ---
    CustomWordsTitle,
    CustomWordsAdd,
    CustomWordsEdit,
    CustomWordsDelete,
    CustomWordsOriginalLabel,
    CustomWordsTranslatedLabel,
    CustomWordsPronunciationLabel,
    CustomWordsExampleLabel,
    CustomWordsOriginalLanguageLabel,
    CustomWordsTranslationLanguageLabel,
    CustomWordsSaveButton,
    CustomWordsCancelButton,

    // --- Language Detection ---
    LanguageDetectAuto,

    // --- Image Recognition (OCR) ---
    ImageRecognitionButton,
    ImageRecognitionAccuracyWarning,

    // --- Cache ---

    // --- Auto Theme ---
    SettingsAutoThemePreview,

    // --- Offline Mode ---
    OfflineSyncPending,

    // --- Image Capture ---
    ImageSourceTitle,
    ImageSourceCamera,
    ImageSourceGallery,
    ImageSourceCancel,
    CameraCaptureContentDesc,

    // --- Friends System ---
    FriendsTitle,
    FriendsAddButton,
    FriendsSearchTitle,
    FriendsSearchPlaceholder,
    FriendsSearchNoResults,
    FriendsRequestsSection,
    FriendsSectionTitle,
    FriendsAcceptButton,
    FriendsRejectButton,
    FriendsSendRequestButton,
    FriendsCloseButton,
    FriendsCancelButton,
    FriendsRemoveConfirm,
    FriendsNewRequestsTemplate,
    FriendsSentRequestsSection,
    FriendsPendingStatus,
    FriendsCancelRequestButton,
    FriendsUnreadMessageDesc,
    FriendsDeleteModeButton,
    FriendsDeleteMultipleTitle,
    FriendsDeleteMultipleMessage,
    FriendsSearchMinChars3,
    FriendsSearchByUserIdHint,
    FriendsStatusAlreadyFriends,
    FriendsStatusRequestSent,
    FriendsStatusRequestReceived,

    // --- Chat ---
    ChatTitle,
    ChatInputPlaceholder,
    ChatSendButton,
    ChatEmpty,
    ChatUnreadBadge,
    
    // --- Chat Translation ---
    ChatTranslateButton,
    ChatTranslateDialogTitle,
    ChatTranslateDialogMessage,
    ChatTranslateConfirm,
    ChatShowOriginal,
    ChatShowTranslation,
    ChatTranslatedLabel,

    // --- Sharing Feature ---
    ShareInboxTitle,
    ShareInboxEmpty,
    ShareWordButton,
    ShareMaterialButton,
    ShareSelectFriendTitle,
    ShareSelectFriendMessage,
    ShareSuccess,
    ShareAcceptButton,
    ShareDismissButton,
    ShareTypeWord,
    ShareTypeLearningSheet,
    ShareReceivedFrom,
    ShareNewItemsTemplate,
    ShareViewFullMaterial,
    ShareDeleteItemTitle,
    ShareDeleteItemMessage,
    ShareDeleteButton,
    ShareViewButton,
    ShareItemNotFound,
    ShareNoContent,
    ShareSaveToSelf,
    ShareSavedToSelf,

    // --- My Profile ---
    MyProfileTitle,
    MyProfileUserId,
    MyProfileUsername,
    MyProfileCopyUserId,
    MyProfileCopyUsername,
    MyProfileShare,
    MyProfileCopied,
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

    // --- Profile Visibility ---
    MyProfileVisibilityLabel,
    MyProfileVisibilityPublic,
    MyProfileVisibilityPrivate,
    MyProfileVisibilityDescription,

    // --- Shared Word Dismiss Confirm ---
    ShareDismissWordTitle,
    ShareDismissWordMessage,

    // --- Shared Inbox Learning Sheet Language Label ---

    // --- Accessibility Strings ---
    AccessibilityDismiss,
    AccessibilityNewMessages,
    AccessibilityNewReleasesIcon,
    AccessibilitySuccessIcon,
    AccessibilityErrorIcon,
    AccessibilitySharedItemTypeIcon,
    AccessibilityWordBankExists,

    // --- Settings Hard-coded Strings ---
    SettingsTesterFeedback,

    // --- Friends Notification Settings ---
    FriendsNotifSettingsButton,
    FriendsNotifSettingsTitle,
    FriendsNotifNewMessages,
    FriendsNotifFriendRequests,
    FriendsNotifRequestAccepted,
    FriendsNotifSharedInbox,

    // --- In-App Badge Settings ---
    InAppBadgeSectionTitle,
    InAppBadgeMessages,
    InAppBadgeFriendRequests,
    InAppBadgeSharedInbox,

    // --- Common Error Messages ---

    // --- Learning Progress ---
    LearningProgressNeededTemplate,

    // --- Quick Translate shortcut ---
    SpeechSwitchToConversation,

    // --- Chat Clear Conversation ---
    ChatClearConversationTitle,
    ChatClearConversationMessage,
    ChatClearConversationConfirm,
    ChatClearConversationSuccess,

    // --- Block User ---
    BlockUserButton,
    BlockUserTitle,
    BlockUserMessage,
    BlockUserConfirm,
    BlockedUsersTitle,
    BlockedUsersEmpty,
    UnblockUserButton,
    UnblockUserTitle,
    UnblockUserMessage,
    BlockedUsersManageButton,

    // --- Friend Request Note ---
    FriendsRequestNoteLabel,
    FriendsRequestNotePlaceholder,

    // --- Generation Completion Banners ---
    GenerationBannerSheet,
    GenerationBannerWordBank,
    GenerationBannerQuiz,

    // --- Notification Settings Quick Link ---

    // --- Language Name for Traditional Chinese ---
    LangZhTw,

    // --- Help Screen Extra Sections ---
    HelpFriendSystemTitle,
    HelpFriendSystemBody,
    HelpProfileVisibilityTitle,
    HelpProfileVisibilityBody,
    HelpColorPalettesTitle,
    HelpColorPalettesBody,
    HelpPrivacyTitle,
    HelpPrivacyBody,
    HelpAppVersionTitle,
    HelpAppVersionNotes,

    // --- Onboarding Screen ---
    OnboardingPage1Title,
    OnboardingPage1Desc,
    OnboardingPage2Title,
    OnboardingPage2Desc,
    OnboardingPage3Title,
    OnboardingPage3Desc,
    OnboardingSkipButton,
    OnboardingNextButton,
    OnboardingGetStartedButton,

    // --- Home Welcome ---
    HomeWelcomeBack,

    // --- Chat Extra Labels ---
    ChatUsernameLabel,
    ChatUserIdLabel,
    ChatLearningLabel,
    ChatBlockedMessage,

    // --- Custom Word Bank Extra ---
    CustomWordsSearchPlaceholder,
    CustomWordsEmptyState,
    CustomWordsEmptyHint,
    CustomWordsNoSearchResults,

    // --- Word Bank Records Count ---
    WordBankRecordsCountTemplate,

    // --- Blocked Users ID ---
    BlockedUserIdTemplate,

    // --- Profile Extra ---
    ProfileEmailTemplate,
    ProfileUsernameHintFull,

    // --- Voice Settings ---
    VoiceSettingsNoOptions,

    // --- Login ---
    AuthUpdatedLoginAgain,

    // --- Primary Language Cooldown ---
    SettingsPrimaryLanguageCooldownTitle,
    SettingsPrimaryLanguageCooldownMessage,
    SettingsPrimaryLanguageCooldownMessageHours,
    SettingsPrimaryLanguageConfirmTitle,
    SettingsPrimaryLanguageConfirmMessage,

    // --- Bottom Navigation ---
    NavHome,
    NavTranslate,
    NavLearn,
    NavFriends,
    NavSettings,

    // --- Permissions ---
    CameraPermissionTitle,
    CameraPermissionMessage,
    CameraPermissionGrant,
    MicPermissionMessage,

    // --- Delete Confirmations ---
    FavoritesDeleteConfirm,
    WordBankDeleteConfirm,

    // --- Friends Extras ---
    FriendsAcceptAllButton,
    FriendsRejectAllButton,
    ChatBlockedCannotSend,

    // --- Shop / Unlock ---
    ShopUnlockConfirmTitle,
    ShopUnlockCost,
    ShopYourCoins,
    ShopUnlockButton,

    // --- Help: Primary Language Info ---
    HelpPrimaryLanguageTitle,
    HelpPrimaryLanguageBody,

    // --- Camera Language Hint ---
    CameraLanguageHint,

    // --- Username Change Cooldown ---
    SettingsUsernameCooldownTitle,
    SettingsUsernameCooldownMessage,
    SettingsUsernameCooldownMessageHours,
    SettingsUsernameConfirmTitle,
    SettingsUsernameConfirmMessage,

    // --- Phase 1 fixes ---
    SettingsPasswordManagedByGoogle,
    SpeechListeningLabel,
    QuizLeaveTitle,
    QuizLeaveMessage,
    QuizLeaveConfirm,
    QuizLeaveStay,

    // --- Phase 1 part 2: a11y labels and status text ---
    A11yAddFavorite,
    A11yRemoveFavorite,
    A11yViewFriendProfile,
    A11yRefreshAutoDetect,
    A11yInstructions,
    A11yClearText,
    A11yAddCustomWords,
    A11yFilter,
    A11ySpeakOriginal,
    A11ySpeakTranslation,
    A11ySpeakExample,
    A11yCoinRules,
    StatusWaiting,
    ErrorNotLoggedIn,
    HistoryErrorDeleteFailed,
    HistoryErrorRenameFailed,
    HistoryErrorDeleteSessionFailed,
    HistoryErrorLoadMoreFailed,
    HistoryErrorRemoveFavoriteFailed,
    HistoryErrorAddFavoriteFailed,
    HistoryErrorFavoriteSessionFailed,
    HistoryErrorUnfavoriteSessionFailed,
    TtsSpeaking,
    TtsSpeakingTranslation,
    TtsSpoken,
    TtsError,
    SpeechStatusRecognitionError,
    SpeechStatusAutoDetecting,
    SpeechStatusDetected,
    SpeechStatusDetectedConfidence,
    SpeechStatusEnterText,
    SpeechStatusLoginRequired,
    SpeechStatusDetectFailed,
    SpeechStatusTranslationError,
    SpeechStatusScanningImage,
    SpeechStatusTextExtracted,
    SpeechStatusOcrError,
    SpeechStatusEnterTextFirst,
    SpeechStatusDetectFailedForSpeech,
    SpeechStatusRefreshed,
    ContinuousStatusLoginRequired,
    ContinuousStatusPreparingMic,
    ContinuousStatusTranslationError,
    ContinuousStatusRateLimited,
    ContinuousStatusRecognitionError,
    ContinuousStatusStartError,

    // --- Phase 1 part 3: social area messages ---
    StatusRaw,
    ErrGeneric,
    ErrNoInternet,
    ErrPermissionDenied,
    ErrSessionExpired,
    ErrItemNotFound,
    ErrAccessDenied,
    ErrAlreadyFriends,
    ErrUserMaybeBlocked,
    ErrRequestGone,
    ErrRequestHandled,
    ErrNotAuthorized,
    ErrTooManyRequests,
    ErrInvalidInput,
    ErrCannotCompleteNow,
    ErrTimedOut,
    FriendsMsgRemoved,
    FriendsMsgRemoveFailed,
    FriendsMsgRemovedMany,
    FriendsMsgNeedUsername,
    FriendsMsgMaxPending,
    FriendsMsgRateLimit,
    FriendsMsgNoteTooLong,
    FriendsMsgRequestSent,
    FriendsMsgAlreadyFriends,
    FriendsMsgRequestPending,
    FriendsMsgProfileMissing,
    FriendsMsgYouBlocked,
    FriendsMsgUnableToSend,
    FriendsMsgSendFailed,
    FriendsMsgAccepted,
    FriendsMsgAcceptGone,
    FriendsMsgAcceptNotAuthorized,
    FriendsMsgAcceptFailed,
    FriendsMsgDeclined,
    FriendsMsgDeclineGone,
    FriendsMsgDeclineFailed,
    FriendsMsgCancelled,
    FriendsMsgCancelGone,
    FriendsMsgCancelFailed,
    FriendsMsgBatchAcceptAll,
    FriendsMsgBatchAcceptFailed,
    FriendsMsgBatchAcceptMixed,
    FriendsMsgBatchAcceptStoppedAt,
    FriendsMsgBatchAcceptStopped,
    FriendsMsgBatchDeclined,
    FriendsMsgBatchDeclineStoppedAt,
    FriendsMsgBatchDeclineStopped,
    FriendsMsgBlockedRemoved,
    FriendsMsgBlockFailed,
    FriendsMsgBlocked,
    FriendsMsgUnblocked,
    FriendsMsgUnblockFailed,
    ChatMsgLoadFailed,
    ChatMsgBlockFailed,
    ChatMsgUnblockFailed,
    ChatMsgTooLong,
    ChatMsgTooFast,
    ChatMsgSendFailed,
    ChatMsgTranslateFailed,
    MyProfileMsgLoadFailed,
    MyProfileMsgNeedUsername,
    MyProfileMsgPublic,
    MyProfileMsgPrivate,
    MyProfileMsgVisibilityFailed,
    SharedMsgAdded,
    SharedMsgDeleted,
    SharedMsgDismissed,
    SharedMsgAcceptFailed,
    SharedMsgDeleteFailed,
    SharedMsgDismissFailed,
    SharedMsgNoItemId,
    SharedMsgLoadFailed,

    // --- Phase 1 part 4: settings, shop, profile, login and feedback messages ---
    FeedbackMsgEmpty,
    FeedbackMsgTooShort,
    FeedbackMsgTooLong,
    FeedbackMsgRateLimited,
    FeedbackMsgSubmitFailed,
    ShopMsgLoadFailed,
    ShopMsgPurchaseFailed,
    ShopMsgHistoryExpanded,
    ShopMsgPaletteAlreadyUnlocked,
    ShopMsgUnlockFailed,
    ShopMsgPaletteUnlocked,
    ShopMsgPaletteSelectFailed,
    ProfileMsgLoadFailed,
    ProfileMsgUsernameTooShort,
    ProfileMsgUsernameTooLong,
    ProfileMsgUsernameInvalidChars,
    ProfileMsgVerifyFailed,
    ProfileMsgUsernameTaken,
    ProfileMsgUsernameUpdatedSyncing,
    ProfileMsgUsernameUpdated,
    ProfileMsgUpdateFailed,
    ProfileMsgSetUsernameFailed,
    ProfileMsgDeleteFailed,
    ProfileMsgInvalidPassword,
    ProfileMsgGoogleReauthFailed,
    ProfileMsgGoogleNoToken,
    ProfileMsgGoogleSignInFailed,
    ProfileGoogleReauthHint,
    ProfileContinueWithGoogle,
    SettingsMsgLanguageSaveFailed,
    SettingsMsgFontSaveFailed,
    SettingsMsgThemeSaveFailed,
    SettingsMsgAutoThemeFailed,
    SettingsMsgVoiceSaveFailed,
    SettingsMsgNotificationSaveFailed,
    AuthMsgEmailEmpty,
    AuthMsgEmailInvalid,
    AuthMsgTooManyLogins,
    AuthMsgTooManyResets,
    AuthMsgGoogleEmptyToken,
    AuthMsgGoogleNoToken,
    AuthMsgGoogleFailedCode,
    AuthMsgGoogleNotConfigured,
    ErrUnknown,
    ErrNetworkFailed,
    ErrOperationTimedOut,
    ErrCancelled,
    ErrUnexpected,
    ErrAuthInvalidEmail,
    ErrAuthWrongPassword,
    ErrAuthUserNotFound,
    ErrAuthUserDisabled,
    ErrAuthTooManyRequests,
    ErrAuthEmailInUse,
    ErrAuthFailed,
    ErrDbPermission,
    ErrDbUnavailable,
    ErrDbGeneric,

    // --- Phase 1 part 5: learning, word bank, custom words, favorites and empty states ---
    LearningMsgLoadFailed,
    LearningMsgSheetLoadFailed,
    LearningMsgSheetGenerateCancelled,
    LearningMsgSheetGenerateFailed,
    LearningMsgQuizParseIncomplete,
    LearningMsgQuizGenerateCancelled,
    LearningMsgQuizGenerateFailed,
    LearningMsgShareNotReady,
    QuizMsgNotGenerated,
    QuizMsgCorrupted,
    QuizMsgLoadFailed,
    QuizMsgSubmitFailed,
    ShareMsgFailed,
    WordBankMsgLoadFailed,
    WordBankMsgDeleteFailed,
    WordBankMsgNeedMoreRecords,
    WordBankMsgNoHistory,
    WordBankMsgGenerateFailed,
    WordBankMsgShareIncomplete,
    CustomWordsMsgOriginalRequired,
    CustomWordsMsgOriginalTooLong,
    CustomWordsMsgTranslatedRequired,
    CustomWordsMsgTranslatedTooLong,
    CustomWordsMsgPronunciationTooLong,
    CustomWordsMsgExampleTooLong,
    CustomWordsMsgDuplicate,
    CustomWordsMsgAddFailed,
    CustomWordsMsgInvalidLanguageUpdate,
    CustomWordsMsgTranslateFailed,
    CustomWordsMsgUpdateLanguageFailed,
    CustomWordsMsgLoadFailed,
    FavoritesMsgLoadFailed,
    FavoritesMsgLoadMoreFailed,
    FavoritesMsgRefreshFailed,
    FavoritesMsgRemoveFailed,
    FavoritesMsgDeleteSelectedFailed,
    FavoritesLoadMore,
    A11yConfirmDelete,
    A11yDeleteMode,
    A11yExpand,
    A11yCollapse,
    EmptyNoHistoryTitle,
    EmptyNoFavoritesTitle,
    EmptyNoLearningTitle,
    EmptySharedItemsMessage,
    SharedMaterialLoadingContent,
    HelpVersionTemplate,
    StartupTagline,

    // --- Extended Error Messages (localized) ---
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

    // --- Dropdowns & Labels ---
    // AppUiLanguageLabel
    "App UI language",
    // DetectLanguageLabel
    "From",
    // TranslateToLabel
    "To",

    // --- Language Names ---
    // LangEnUs
    "English",
    // LangZhHk
    "Cantonese",
    // LangJaJp
    "Japanese",
    // LangZhCn
    "Simplified Chinese",
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

    // --- Placeholders & Input ---
    // SpeechInputPlaceholder
    "Type here or use microphone...",
    // SpeechTranslatedPlaceholder
    "The translated result will be shown here.",

    // --- Pagination ---
    // PaginationPrevLabel
    "< Prev",
    // PaginationNextLabel
    "Next >",
    // PaginationPageLabelTemplate
    "Page {page} / {total}",

    // --- Toast & Messages ---
    // DisableText
    "Login is required to use translation features & storing translation history.",

    // --- Standard UI Components ---
    // ErrorRetryButton
    "Retry",

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