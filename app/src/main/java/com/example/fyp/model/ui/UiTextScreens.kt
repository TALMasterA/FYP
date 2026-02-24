package com.example.fyp.model.ui

// Screen-specific UI texts - used by individual screens
val ScreenUiTexts: List<String> = listOf(
    // --- Speech/Home Instructions ---
    // SpeechInstructions
    "Select From and To languages below. Only suitable for short phrases.\n" +
            "Supported languages: English, Cantonese, Japanese, Mandarin and more.\n" +
            "Use the ⇄ button to swap the selected languages.",

    // HomeInstructions
    "You can change the app UI language by the dropdown on top. \n" +
            "Please look at the ! information before using the app.",

    // ContinuousInstructions
    "Set Speaker A and B languages below. \n" +
            "Use the toggle to switch who is speaking.",

    // --- Home/Help Screens ---
    // HomeTitle
    "Translator",

    // HelpTitle
    "Notes & Cautions",

    // SpeechTitle
    "Discrete mode",

    // HomeStartButton
    "Discrete speech translation",

    // HomeFeaturesTitle
    "Features",

    // HomeDiscreteDescription
    "Real-time voice translation",

    // HomeContinuousDescription
    "Multi-turn conversation mode",

    // HomeLearningDescription
    "Study vocabulary and take quizzes",

    // HelpCurrentTitle
    "Current features",

    // HelpCautionTitle
    "Cautions",

    // HelpCurrentFeatures
    "TRANSLATION FEATURES:\n" +
            "  • Discrete Mode - Real-time voice translation for short phrases and sentences\n" +
            "  • Continuous Mode - Live conversation capture with automatic speaker detection\n" +
            "  • Multi-language support including English, Cantonese, Japanese, Mandarin, and more\n" +
            "  • Text-to-speech playback for both original and translated text\n\n" +
            
            "LEARNING & STUDY:\n" +
            "  • Learning Sheets - AI-generated study materials based on your translation history\n" +
            "  • Quiz System - Test your knowledge and earn coins (🪙)\n" +
            "  • Word Bank - Automatically generated vocabulary list from your translations\n" +
            "  • Favorites - Bookmark important translations for quick access\n\n" +
            
            "CUSTOMIZATION:\n" +
            "  • UI Language - Change app interface language (English, Chinese, Japanese, etc.)\n" +
            "  • Theme Settings - Switch between light, dark, or system theme\n" +
            "  • Font Size - Adjust text size from 80% to 150%\n" +
            "  • Color Palettes - Unlock and apply different color themes using coins\n" +
            "  • Voice Settings - Customize text-to-speech voices for different languages\n\n" +
            
            "HISTORY & ORGANIZATION:\n" +
            "  • Translation History - Recent records (expandable via Shop)\n" +
            "  • Filter & Search - Find translations by language or keyword\n" +
            "  • Session Management - Organize continuous conversations by session\n" +
            "  • Cloud Sync - All data synced to your Firebase account\n\n" +
            
            "COINS & REWARDS:\n" +
            "  • Earn coins by completing quizzes with good performance\n" +
            "  • Spend coins to unlock color palettes or expand history limit\n" +
            "  • Anti-cheat system ensures fair coin distribution\n",

    // HelpCaution
    "⚠️ IMPORTANT SAFETY INFORMATION:\n\n" +
            
            "Connectivity:\n" +
            "  • Requires stable internet connection for translation and speech recognition\n" +
            "  • Firestore Cloud Functions process all translation requests securely\n\n" +
            
            "Microphone & Privacy:\n" +
            "  • Audio is captured only for speech recognition, not stored permanently\n" +
            "  • Avoid discussing sensitive or confidential information in public settings\n" +
            "  • Grant microphone permissions for speech features to work\n\n" +
            
            "Audio:\n" +
            "  • Use earphones during continuous mode to prevent audio feedback loops\n" +
            "  • The app may re-capture its own TTS output if using speakers\n\n" +
            
            "Accuracy & Limitations:\n" +
            "  • Do NOT rely on translations for medical, legal, or safety-critical decisions\n" +
            "  • AI translations may contain errors or cultural misinterpretations\n" +
            "  • Always verify important translations with professional services\n\n" +
            
            "Account & Data:\n" +
            "  • Login required for history, learning, and coin features\n" +
            "  • User data is stored securely in Firebase Firestore\n",

    // HelpNotesTitle
    "Notes",

    // HelpNotes
    "💡 TIPS & TROUBLESHOOTING:\n\n" +
            
            "For Best Translation Results:\n" +
            "  • Speak clearly and at a moderate pace\n" +
            "  • Minimize background noise for better recognition accuracy\n" +
            "  • Short, simple sentences work best in discrete mode\n\n" +
            
            "UI Language:\n" +
            "  • Base language is English; other UI languages are AI-translated\n" +
            "  • Some translations may contain minor errors\n" +
            
            "Updates & Feedback:\n" +
            "  • App version displayed in Settings → About\n" +
            "  • Report bugs or suggest features via GitHub repository\n",

    // --- Feedback ---
    // FeedbackTitle
    "Feedback",

    // FeedbackDesc
    "We appreciate your feedback! Please share your suggestions, bug reports, or general comments about the app.",

    // FeedbackMessagePlaceholder
    "Enter your feedback here...",

    // FeedbackSubmitButton
    "Submit Feedback",

    // FeedbackSubmitting
    "Submitting...",

    // FeedbackSuccessTitle
    "Thank You!",

    // FeedbackSuccessMessage
    "Your feedback has been submitted successfully. We appreciate your input!",

    // FeedbackErrorTitle
    "Submission Failed",

    // FeedbackErrorMessage
    "Failed to submit feedback. Please check your internet connection and try again.",

    // FeedbackMessageRequired
    "Please enter your feedback message.",

    // --- Continuous Mode ---
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

    // ContinuousPreparingMicText
    "Preparing mic... (Do not speak now)",

    // ContinuousTranslatingText
    "Translating...",

    // --- History Screen ---
    // HistoryTitle
    "History",

    // HistoryTabDiscrete
    "Discrete",

    // HistoryTabContinuous
    "Continuous",

    // HistoryNoContinuousSessions
    "No session(s) yet.",

    // HistoryNoDiscreteRecords
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

    // --- Filter ---
    // FilterDropdownDefault
    "All languages",

    // FilterTitle
    "Filter history",

    // FilterLangDrop
    "Language",

    // FilterKeyword
    "Keyword",

    // FilterApply
    "Apply",

    // FilterCancel
    "Cancel",

    // FilterClear
    "Clear",

    // FilterHistoryScreenTitle
    "Filter",

    // --- Authentication ---
    // AuthLoginTitle
    "Login",

    // AuthRegisterTitle
    "Register (Disabled)",

    // AuthLoginHint
    "Use your registered (provided) email and password.",

    // AuthRegisterRules
    "Register is disabled in development stage. \n" +
            "Caution: You cannot reset password if you use an email that does not exist. \n" +
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

    // AuthRegistrationDisabled
    "Registration is disabled during development.",

    // AuthResetEmailSent
    "Reset email sent (if email is real & exists). Please check your inbox.",

    // --- Password Reset ---
    // ForgotPwText
    "Forgot password? Reset here",

    // ResetPwTitle
    "Reset Password",

    // ResetPwText
    "Enter your account email and a reset link will be sent. \n" +
            "Make sure the email is real & register for the app or no email will be sent. \n",

    // ResetSendingText
    "Sending...",

    // ResetSendText
    "Send reset email",

    // --- Settings ---
    // SettingsTitle
    "Settings",

    // SettingsPrimaryLanguageTitle
    "Primary Language",

    // SettingsPrimaryLanguageDesc
    "Used for learning explanations and recommendations",

    // SettingsPrimaryLanguageLabel
    "Primary language",

    // SettingsFontSizeTitle
    "Font Size",

    // SettingsFontSizeDesc
    "Adjust text size for better readability (synced across devices)",

    // SettingsScaleTemplate
    "Scale: {pct}%",

    // SettingsColorPaletteTitle
    "Color Palette",

    // SettingsColorPaletteDesc
    "Choose your app's color theme. Unlock new palettes for 10 coins each!",

    // SettingsColorCostTemplate
    "{cost} coins",

    // SettingsColorUnlockButton
    "Unlock",

    // SettingsColorSelectButton
    "Select",

    // SettingsColorAlreadyUnlocked
    "Unlocked",

    // SettingsPreviewHeadline
    "Headline: Large text preview",

    // SettingsPreviewBody
    "Body: This is normal text preview",

    // SettingsPreviewLabel
    "Label: Small text preview",

    // SettingsAboutTitle
    "About",

    // SettingsAppVersion
    "Talk & Learn Translator v",

    // SettingsSyncInfo
    "Your preferences are automatically saved and synced to your account.",

    // SettingsThemeTitle
    "Theme",

    // SettingsThemeDesc
    "Follow system / Light / Dark (Locked when not logged in)",

    // SettingsThemeSystem
    "Follow system",

    // SettingsThemeLight
    "Light",

    // SettingsThemeDark
    "Dark",

    //SettingsThemeScheduled
    "Scheduled",

    // SettingsResetPW
    "Reset password here",

    // SettingsQuickLinks
    "Details Settings",

    // SettingsNotLoggedInWarning
    "Not logged in. Changes below will not be saved.",

    // SettingsVoiceTitle
    "Voice Settings",

    // SettingsVoiceDesc
    "Choose a voice for text-to-speech output per language.",

    // SettingsVoiceLanguageLabel
    "Language",

    // SettingsVoiceSelectLabel
    "Voice",

    // SettingsVoiceDefault
    "Default",

    // --- Learning ---
    // LearningTitle
    "Learning",

    // LearningHintCount
    "(*) Count = number of history records involving this language.",

    // LearningErrorTemplate
    "Error: %s",

    // LearningGenerate
    "Generate",

    // LearningRegenerate
    "Regenerate",

    // LearningGenerating
    "Generating...",

    // LearningOpenSheetTemplate
    "{speclanguage} Sheet",

    // LearningSheetTitleTemplate
    "{speclanguage} Sheet",

    // LearningSheetPrimaryTemplate
    "Primary language: {speclanguage}",

    // LearningSheetHistoryCountTemplate
    "History count now: {nowCount} (saved at gen: {savedCount})",

    // LearningSheetNoContent
    "No sheet content yet.",

    // LearningSheetRegenerate
    "Regenerate",

    // LearningSheetGenerating
    "Generating...",

    // LearningRegenBlockedTitle
    "Cannot Regenerate Yet",

    // LearningRegenBlockedMessage
    "Regeneration requires at least 5 more records than the previous generation. You currently need {needed} more records.",

    // LearningRegenNeedMoreRecords
    "⚠️ Need {needed} more records to regenerate (minimum 5)",

    // LearningRegenCountNotHigher
    "⚠️ Record count must be higher than the previous generation",

    // LearningRegenInfoTitle
    "Regeneration Rules",

    // LearningRegenInfoMessage
    "To regenerate learning materials:\n\n• First generation: Always allowed\n• Regeneration: Requires at least 5 MORE translation records than the previous generation\n\nThe button will be enabled (blue) when you have enough new records. If it's disabled (gray), keep translating to unlock regeneration!\n\n💡 Note: If the count doesn't update after translating, please restart the app to refresh.",

    // QuizRegenBlockedSameMaterial
    "❌ Quiz already generated for this material version. Generate a new learning sheet to create a new quiz.",

    // --- Quiz ---
    // QuizTitleTemplate
    "Quiz: {language}",

    // QuizOpenButton
    "📝 Quiz",

    // QuizGenerateButton
    "🔄 Generate Quiz",

    // QuizGenerating
    "⏳ Generating...",

    // QuizUpToDate
    "✓ Up-to-date",

    // QuizBlocked
    "🚫 Blocked",

    // QuizWait
    "⏳ Wait...",

    // QuizMaterialsQuizTemplate
    "Materials: {materials} | Quiz: {quiz}",

    // QuizCanEarnCoins
    "🪙 Can earn coins!",

    // QuizNeedMoreRecordsTemplate
    "🪙 Need {count} more records for coins",

    // QuizCancelButton
    "Cancel",

    // QuizPreviousButton
    "Previous",

    // QuizNextButton
    "Next",

    // QuizSubmitButton
    "Submit",

    // QuizRetakeButton
    "Retake Quiz",

    // QuizBackButton
    "Back",

    // QuizLoadingText
    "Loading quiz...",

    // QuizGeneratingText
    "Generating quiz...",

    // QuizNoMaterialsTitle
    "No learning materials found",

    // QuizNoMaterialsMessage
    "Please go back and generate the learning materials before viewing the quiz.",

    // QuizErrorTitle
    "⚠️ Quiz Error",

    // QuizErrorSuggestion
    "Suggestion: Generate the quiz using the button above.",

    // QuizCompletedTitle
    "Quiz Completed!",

    // QuizAnswerReviewTitle
    "Answer Review",

    // QuizYourAnswerTemplate
    "Your answer: {answer}",

    // QuizCorrectAnswerTemplate
    "Correct: {answer}",

    // QuizQuestionTemplate
    "Question {current} of {total}",

    // QuizCannotRegenTemplate
    "⚠️ Cannot regenerate: Materials ({materials}) < Quiz ({quiz}). Add more translations.",

    // QuizAnotherGenInProgress
    "⏳ Another generation is in progress. Please wait.",

    // QuizCoinRulesTitle
    "🪙 Coin Earning Rules",

    // QuizCoinRulesHowToEarn
    "✅ How to Earn:",

    // QuizCoinRulesRequirements
    "Requirements:",

    // QuizCoinRulesCurrentStatus
    "Current Status:",

    // QuizCoinRulesCanEarn
    "• ✅ Can earn coins on next quiz!",

    // QuizCoinRulesNeedMoreTemplate
    "• Need {count} more records for coins",

    // QuizCoinRule1Coin
    "• 1 coin per correct answer",

    // QuizCoinRuleFirstAttempt
    "• Only first attempt of each quiz version",

    // QuizCoinRuleMatchMaterials
    "• Quiz must match materials version",

    // QuizCoinRulePlus10
    "• Need 10+ more records than last awarded quiz",

    // QuizCoinRuleNoDelete
    "• Cannot delete history to re-earn",

    // QuizCoinRuleMaterialsTemplate
    "• Materials: {count} records",

    // QuizCoinRuleQuizTemplate
    "• Quiz: {count} records",

    // QuizCoinRuleGotIt
    "Got it!",

    // QuizRegenConfirmTitle
    "🔄 Generate New Quiz?",

    // QuizRegenCanEarnCoins
    "✅ You can earn coins on this quiz! (First attempt only)",

    // QuizRegenCannotEarnCoins
    "⚠️ You cannot earn coins on this quiz yet.",

    // QuizRegenNeedMoreTemplate
    "Need {count} more translation records to be eligible for coins (minimum 10 more than last earned quiz).",

    // QuizRegenReminder
    "Reminder: You can still practice and retake quizzes for learning, but coins are only awarded on first attempts with sufficient new records.",

    // QuizRegenGenerateButton
    "Generate",

    // QuizCoinsEarnedTitle
    "✨ Coins Earned!",

    // QuizCoinsEarnedMessageTemplate
    "Congratulations! You earned {coins} coins!",

    // QuizCoinsRule1
    "• 1 coin per correct answer on first attempt only",

    // QuizCoinsRule2
    "• Retaking the same quiz earns no coins",

    // QuizCoinsRule3
    "• New quiz must have 10+ more records than previous",

    // QuizCoinsRule4
    "• Quiz must match current materials version",

    // QuizCoinsRule5
    "• View total coins in History screen",

    // QuizCoinsGreatButton
    "Great!",

    // QuizOutdatedMessage
    "This quiz is based on an old sheet version.",

    // QuizRecordsLabel
    "records",

    // --- History Screen Coins ---
    // HistoryCoinsDialogTitle
    "🪙 Your Coins",

    // HistoryCoinRulesTitle
    "Coin Earning Rules:",

    // HistoryCoinHowToEarnTitle
    "How to Earn:",

    // HistoryCoinHowToEarnRule1
    "• 1 coin per correct answer",

    // HistoryCoinHowToEarnRule2
    "• Only first attempt of each quiz version counts",

    // HistoryCoinHowToEarnRule3
    "• Quiz must match current learning materials",

    // HistoryCoinAntiCheatTitle
    "🔒 Anti Cheat/Farming Rules:",

    // HistoryCoinAntiCheatRule1
    "• Need 10+ new translations compare to previous earned coin quiz to earn again",

    // HistoryCoinAntiCheatRule2
    "• Quiz version must equal materials version",

    // HistoryCoinAntiCheatRule3
    "• Deleting history blocks quiz regenerate (unless the count is higher than previous record)",

    // HistoryCoinAntiCheatRule4
    "• Retaking same quiz earns no coins",

    // HistoryCoinTipsTitle
    "💡 Tips:",

    // HistoryCoinTipsRule1
    "• Add more translations regularly",

    // HistoryCoinTipsRule2
    "• Study well before first attempt!",

    // HistoryCoinGotItButton
    "Got it!",

    // --- History Info Dialog ---
    // HistoryInfoTitle
    "History Information",

    // HistoryInfoLimitMessage
    "History shows your most recent {limit} records. You can expand this limit in the Shop!",

    // HistoryInfoOlderRecordsMessage
    "Older records are still stored but not displayed here to optimize performance.",

    // HistoryInfoFavoritesMessage
    "To keep important translations permanently accessible, add them to your Favorites by tapping the heart ❤️ icon on any record.",

    // HistoryInfoViewFavoritesMessage
    "View your saved Favorites in Settings → Favorites.",

    // HistoryInfoFilterMessage
    "Use the Filter button to search within the displayed {limit} records.",

    // HistoryInfoGotItButton
    "Got it",

    // --- Word Bank ---
    // WordBankTitle
    "Word Bank",

    // WordBankSelectLanguage
    "Select a language to view or generate word bank:",

    // WordBankNoHistory
    "No Translation History",

    // WordBankNoHistoryHint
    "Start translating to build your word bank!",

    // WordBankWordsCount
    "words",

    // WordBankGenerating
    "Generating...",

    // WordBankGenerate
    "Generate Word Bank",

    // WordBankRegenerate
    "Regenerate Word Bank",

    // WordBankRefresh
    "🔄 Refresh Word Bank",

    // WordBankEmpty
    "No Word Bank Yet",

    // WordBankEmptyHint
    "Tap the button above to generate a word bank from your translation history.",

    // WordBankExample
    "Example:",

    // WordBankDifficulty
    "Difficulty:",

    // WordBankFilterCategory
    "Category",

    // WordBankFilterCategoryAll
    "All Categories",

    // WordBankFilterDifficultyLabel
    "Difficulty Level:",

    // WordBankFilterNoResults
    "No words match your filter",

    // WordBankRefreshAvailable
    "✅ Refresh available!",

    // WordBankRecordsNeeded
    "records (need 20 to refresh)",

    // WordBankRegenInfoTitle
    "Refresh Rules",

    // WordBankRegenInfoMessage
    "To refresh your word bank:\n\n• First generation: Always allowed\n• Refresh: Requires at least 20 MORE translation records than the previous generation\n\nThe refresh button will be enabled (blue) when you have enough new records. If it's disabled (gray), keep translating to unlock refresh!\n\n💡 Note: If the count doesn't update after translating, please restart the app to refresh.",

    // WordBankHistoryCountTemplate
    "History count now: {nowCount} (saved at gen: {savedCount})",

    // --- Dialogs ---
    // DialogLogoutTitle
    "Logout?",

    // DialogLogoutMessage
    "You will need to login again to use translation function / store + view your history.",

    // DialogGenerateOverwriteTitle
    "Overwrite materials?",

    // DialogGenerateOverwriteMessageTemplate
    "Previous materials will be overwritten (if exist). \n" +
            "Generate materials for {speclanguage}?",

    // --- Profile Management ---
    // ProfileTitle
    "Profile",

    // ProfileUsernameLabel
    "Username",

    // ProfileUsernameHint
    "Enter your username",

    // ProfileDisplayNameLabel
    "Display Name",

    // ProfileDisplayNameHint
    "Enter your display name",

    // ProfileUpdateButton
    "Update Profile",

    // ProfileUpdateSuccess
    "Profile updated successfully",

    // ProfileUpdateError
    "Failed to update profile",

    // --- Account Deletion ---
    // AccountDeleteTitle
    "Delete Account",

    // AccountDeleteWarning
    "⚠️ This action is permanent and cannot be undone!",

    // AccountDeleteConfirmMessage
    "All your data including history, word banks, learning materials, and settings will be permanently deleted. Enter your password to confirm.",

    // AccountDeletePasswordLabel
    "Password",

    // AccountDeleteButton
    "Delete My Account",

    // AccountDeleteSuccess
    "Account deleted successfully",

    // AccountDeleteError
    "Failed to delete account",

    // AccountDeleteReauthRequired
    "Please re-enter your password to confirm deletion",

    // --- Favorites ---
    // FavoritesTitle
    "Favorites",

    // FavoritesEmpty
    "No favorites yet",

    // FavoritesAddSuccess
    "Added to favorites",

    // FavoritesRemoveSuccess
    "Removed from favorites",

    // FavoritesAddButton
    "Add to Favorites",

    // FavoritesRemoveButton
    "Remove from Favorites",

    // FavoritesNoteLabel
    "Note",

    // FavoritesNoteHint
    "Add a note (optional)",

    // --- Custom Words ---
    // CustomWordsTitle
    "Custom Words",

    // CustomWordsAdd
    "Add Word",

    // CustomWordsEdit
    "Edit Word",

    // CustomWordsDelete
    "Delete Word",

    // CustomWordsOriginalLabel
    "Original Word",

    // CustomWordsTranslatedLabel
    "Translation",

    // CustomWordsPronunciationLabel
    "Pronunciation (optional)",

    // CustomWordsExampleLabel
    "Example (optional)",

    // CustomWordsSaveSuccess
    "Word saved successfully",

    // CustomWordsDeleteSuccess
    "Word deleted successfully",

    // CustomWordsAlreadyExists
    "This word already exists",

    // CustomWordsOriginalLanguageLabel
    "Original Language",

    // CustomWordsTranslationLanguageLabel
    "Translation Language",

    // CustomWordsSaveButton
    "Save",

    // CustomWordsCancelButton
    "Cancel",

    // --- Language Detection ---
    // LanguageDetectAuto
    "Auto-detect",

    // LanguageDetectDetecting
    "Detecting...",

    // LanguageDetectedTemplate
    "Detected: {language}",

    // LanguageDetectFailed
    "Detection failed",

    // --- Image Recognition (OCR) ---
    // ImageRecognitionButton
    "Scan Text from Image",

    // ImageRecognitionAccuracyWarning
    "⚠️ Note: Image text recognition may not be 100% accurate. Please review the extracted text." +
            "Support latin characters (English...), Chinese, Japanese and Korean.",

    // ImageRecognitionScanning
    "Scanning image for text...",

    // ImageRecognitionSuccess
    "Text extracted successfully",

    // --- Cache ---
    // CacheClearButton
    "Clear Cache",

    // CacheClearSuccess
    "Cache cleared successfully",

    // CacheStatsTemplate
    "Cache: {count} translations stored",

    // --- Auto Theme ---
    // SettingsAutoThemeTitle
    "Auto Theme Switching",
    // SettingsAutoThemeDesc
    "Automatically switch between light and dark themes based on time of day",
    // SettingsAutoThemeEnabled
    "Enabled",
    // SettingsAutoThemeDisabled
    "Disabled",
    // SettingsAutoThemeDarkStartLabel
    "Dark mode starts at:",
    // SettingsAutoThemeLightStartLabel
    "Light mode starts at:",
    // SettingsAutoThemePreview
    "Theme will automatically switch at scheduled times",

    // --- Offline Mode ---
    // OfflineModeTitle
    "Offline Mode",
    // OfflineModeMessage
    "You're offline. Browsing cached data.",
    // OfflineModeRetry
    "Retry Connection",
    // OfflineDataCached
    "Cached data available",
    // OfflineSyncPending
    "Changes will sync when online",

    // --- Image Capture ---
    // ImageSourceTitle
    "Select Image Source",
    // ImageSourceCamera
    "Take Photo",
    // ImageSourceGallery
    "Choose from Gallery",
    // ImageSourceCancel
    "Cancel",
    // CameraCaptureContentDesc
    "Capture",

    // --- Friends System ---
    // FriendsTitle
    "Friends",
    // FriendsMenuButton
    "Friends",
    // FriendsAddButton
    "Add Friends",
    // FriendsSearchTitle
    "Search Users",
    // FriendsSearchPlaceholder
    "Enter username or user ID...",
    // FriendsSearchMinChars
    "Enter at least 2 characters to search",
    // FriendsSearchNoResults
    "No users found",
    // FriendsListEmpty
    "Add friends to connect and share learning materials.",
    // FriendsRequestsSection
    "Friend Requests ({count})",
    // FriendsSectionTitle
    "Friends ({count})",
    // FriendsAcceptButton
    "Accept",
    // FriendsRejectButton
    "Reject",
    // FriendsRemoveButton
    "Remove",
    // FriendsRemoveDialogTitle
    "Remove Friend",
    // FriendsRemoveDialogMessage
    "Are you sure you want to remove {username} from your friends list?",
    // FriendsSendRequestButton
    "Add",
    // FriendsRequestSentSuccess
    "Friend request sent!",
    // FriendsRequestAcceptedSuccess
    "Friend request accepted!",
    // FriendsRequestRejectedSuccess
    "Request rejected",
    // FriendsRemovedSuccess
    "Friend removed",
    // FriendsRequestFailed
    "Failed to send request",
    // FriendsCloseButton
    "Close",
    // FriendsCancelButton
    "Cancel",
    // FriendsRemoveConfirm
    "Remove",

    // FriendsNewRequestsTemplate
    "You have {count} new friend request(s)!",
    // FriendsSentRequestsSection
    "Sent Requests ({count})",
    // FriendsPendingStatus
    "Pending",
    // FriendsCancelRequestButton
    "Cancel request",
    // FriendsUnreadMessageDesc
    "Send message",
    // FriendsDeleteModeButton
    "Delete friends",
    // FriendsDeleteSelectedButton
    "Delete selected",
    // FriendsDeleteMultipleTitle
    "Remove Friends",
    // FriendsDeleteMultipleMessage
    "Remove {count} selected friend(s)?",
    // FriendsSearchMinChars3
    "Enter at least 3 characters to search by username",
    // FriendsSearchByUserIdHint
    "Or enter full User ID for exact lookup",

    // --- Chat ---
    // ChatTitle
    "Chat with {username}",
    // ChatInputPlaceholder
    "Type a message...",
    // ChatSendButton
    "Send",
    // ChatEmpty
    "No messages yet. Start the conversation!",
    // ChatMessageSent
    "Message sent",
    // ChatMessageFailed
    "Failed to send message",
    // ChatMarkingRead
    "Marking as read...",
    // ChatLoadingMessages
    "Loading messages...",
    // ChatToday
    "Today",
    // ChatYesterday
    "Yesterday",
    // ChatUnreadBadge
    "{count} unread",

    // --- Chat Translation ---
    // ChatTranslateButton
    "Translate",
    // ChatTranslateDialogTitle
    "Translate Conversation",
    // ChatTranslateDialogMessage
    "Translate your friend's messages to your preferred language? This will detect the language of each message and translate them.",
    // ChatTranslateConfirm
    "Translate All",
    // ChatTranslating
    "Translating messages...",
    // ChatTranslated
    "Messages translated",
    // ChatShowOriginal
    "Show Original",
    // ChatShowTranslation
    "Show Translation",
    // ChatTranslateFailed
    "Translation failed",

    // ChatTranslatedLabel
    "Translated",

    // --- Sharing Feature ---
    // ShareTitle
    "Share",
    // ShareInboxTitle
    "Shared Inbox",
    // ShareInboxEmpty
    "No shared items yet. Friends can share words and learning materials with you!",
    // ShareWordButton
    "Share Word",
    // ShareMaterialButton
    "Share Material",
    // ShareSelectFriendTitle
    "Select Friend",
    // ShareSelectFriendMessage
    "Choose a friend to share with:",
    // ShareSuccess
    "Successfully shared!",
    // ShareFailed
    "Failed to share",
    // ShareWordWith
    "Share word with {username}",
    // ShareMaterialWith
    "Share material with {username}",
    // ShareAcceptButton
    "Accept",
    // ShareDismissButton
    "Dismiss",
    // ShareAccepted
    "Added to your collection",
    // ShareDismissed
    "Item dismissed",
    // ShareActionFailed
    "Action failed",
    // ShareTypeWord
    "Word",
    // ShareTypeLearningSheet
    "Learning Sheet",
    // ShareTypeQuiz
    "Quiz",
    // ShareReceivedFrom
    "From: {username}",

    // ShareNewItemsTemplate
    "{count} new item(s) received!",
    // ShareViewFullMaterial
    "Tap \"View\" to read the full material",
    // ShareDeleteItemTitle
    "Delete Item",
    // ShareDeleteItemMessage
    "Are you sure you want to delete this shared item? This action cannot be undone.",
    // ShareDeleteButton
    "Delete",
    // ShareViewButton
    "View",
    // ShareItemNotFound
    "Item not found.",
    // ShareNoContent
    "No content available for this material.",

    // --- My Profile ---
    // MyProfileTitle
    "My Profile",
    // MyProfileUserId
    "User ID",
    // MyProfileUsername
    "Username",
    // MyProfileDisplayName
    "Display Name",
    // MyProfileCopyUserId
    "Copy User ID",
    // MyProfileCopyUsername
    "Copy Username",
    // MyProfileShare
    "Share Profile",
    // MyProfileCopied
    "Copied to clipboard!",
    // MyProfileLanguages
    "Languages",
    // MyProfilePrimaryLanguage
    "Primary Language",
    // MyProfileLearningLanguages
    "Learning Languages",

    // --- Friends Info Dialog & Empty States ---
    // FriendsInfoTitle
    "Friends Screen Info",
    // FriendsInfoMessage
    "• Pull down to manually refresh your friends list, requests, and friend status.\n" +
    "• Tap a friend card to open chat.\n" +
    "• A red dot (●) in the top-left corner of a friend card means unread messages.\n" +
    "• Use the 📥 inbox icon to view shared materials.\n" +
    "• Use the trash icon to enter delete mode and remove friends.\n" +
    "• Use the search button to find and add new friends by username or User ID.\n",
    // FriendsEmptyTitle
    "No Friends Yet",
    // FriendsEmptyMessage
    "Search for friends by username or User ID using the 'Add Friends' button.\n",
    // FriendsInfoGotItButton
    "Got it",

    // --- Learning Info Dialog & Empty States ---
    // LearningInfoTitle
    "Learning Screen Info",
    // LearningInfoMessage
    "• Pull down to manually refresh your language record counts.\n" +
    "• Each card shows a language and how many translation records you have.\n" +
    "• Press 'Generate' to create a learning sheet (first time is always free).\n" +
    "• Regeneration requires at least 5 more records than the last generation.\n" +
    "• Press the sheet button to open and study your generated materials.\n" +
    "• Quiz is available after generating a learning sheet.",
    // LearningEmptyTitle
    "No Translation Records",
    // LearningEmptyMessage
    "Start translating to build up history records.\n" +
    "Learning sheets are generated from your translation history.\n" +
    "Pull down to refresh after translating.",
    // LearningInfoGotItButton
    "Got it",

    // --- Word Bank Info Dialog & Empty States ---
    // WordBankInfoTitle
    "Word Bank Screen Info",
    // WordBankInfoMessage
    "• Pull down to manually refresh your word bank language list.\n" +
    "• Select a language to view or generate its word bank.\n" +
    "• Word banks are generated from your translation history.\n" +
    "• Refreshing a word bank requires at least 20 more records than the last generation.\n" +
    "• Use the Custom Words section to manually add your own vocabulary.\n" +
    "• You can share words from your word bank with friends.",
    // WordBankInfoGotItButton
    "Got it",

    // --- SharedInbox Info Dialog ---
    // ShareInboxInfoTitle
    "Shared Inbox Info",
    // ShareInboxInfoMessage
    "• Pull down to manually refresh your shared inbox.\n" +
    "• Shared items from friends appear here.\n" +
    "• Words can be accepted into your word bank or dismissed.\n" +
    "• Learning sheets and quizzes can be viewed in detail by tapping View.\n" +
    "• A red dot (●) indicates new/unread items.\n" +
    "• Dismissing a shared word will ask for confirmation before removing it.",
    // ShareInboxInfoGotItButton
    "Got it",

    // --- Profile Visibility ---
    // MyProfileVisibilityLabel
    "Profile Visibility",
    // MyProfileVisibilityPublic
    "Public",
    // MyProfileVisibilityPrivate
    "Private",
    // MyProfileVisibilityDescription
    "Public: anyone can search and add you as a friend.\nPrivate: you cannot be found via search.",

    // --- Shared Word Dismiss Confirm ---
    // ShareDismissWordTitle
    "Dismiss Word",
    // ShareDismissWordMessage
    "Are you sure you want to dismiss this shared word? This action cannot be undone.",

    // --- Shared Inbox Learning Sheet Language Label ---
    // ShareLearningSheetLanguageLabel
    "Language: {language}",

    // --- Accessibility Strings ---
    // AccessibilityDismiss
    "Dismiss",
    // AccessibilityAlreadyConnectedOrPending
    "Already connected or pending",
    // AccessibilityNewMessages
    "New messages",
    // AccessibilityNewReleasesIcon
    "New items indicator",
    // AccessibilitySuccessIcon
    "Success",
    // AccessibilityErrorIcon
    "Error",
    // AccessibilitySharedItemTypeIcon
    "Shared item type",
    // AccessibilityAddCustomWords
    "Add custom words",
    // AccessibilityWordBankExists
    "Word bank exists",

    // --- Settings Hard-coded Strings ---
    // SettingsTesterFeedback
    "T.Feedback",
    // SettingsSystemNotesButton
    "System Notes & Info",
    // SystemNotesTitle
    "System Notes",

    // --- Friends Notification Settings ---
    // FriendsNotifSettingsButton
    "Notification Settings",
    // FriendsNotifSettingsTitle
    "Notification Preferences",
    // FriendsNotifNewMessages
    "New chat messages",
    // FriendsNotifFriendRequests
    "Incoming friend requests",
    // FriendsNotifRequestAccepted
    "Friend request accepted",
    // FriendsNotifSharedInbox
    "New shared-inbox items",
    // FriendsNotifCloseButton
    "Done",

    // --- In-App Badge Settings ---
    // InAppBadgeSectionTitle
    "In-App Badges (Red Dots)",
    // InAppBadgeMessages
    "Unread chat message badge",
    // InAppBadgeFriendRequests
    "Pending friend request badge",
    // InAppBadgeSharedInbox
    "Unseen shared-inbox badge",
)