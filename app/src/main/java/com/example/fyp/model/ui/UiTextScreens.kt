package com.example.fyp.model.ui

// Screen-specific UI texts - used by individual screens
val ScreenUiTexts: List<String> = listOf(
    // --- Speech/Home Instructions ---
    // SpeechInstructions
    "Select the detect and translate languages below. Only suitable for short pharse. \n" +
            "Support languages: English, Cantonese, Japanese, Mandarin... \n" +
            "Switch button for swiping the languages selected",

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
    "- Choose UI language, detection language, and target language. \n" +
            "- Discrete mode: For short sentences translation. \n" +
            "- Continuous mode: Live conversation capture with user switching. \n" +
            "- Learning: Learning materials generate by AI (use your translation history). \n" +
            "- History: View your translation history. (Recent 100 records, expandable to 150 in Shop)\n" +
            "- Quiz: Test your ability! \n" +
            "- Word bank (Next to the setting): Generate/Refresh the word bank for study words appear in your history. \n" +
            "- App UI language translation function. \n",

    // HelpCaution
    "- Requires internet connection. \n" +
            "- Microphone audio is used for speech recognition; avoid speaking sensitive info in public. \n" +
            "- Use earphones if using 🔊 during continuous listening to avoid the app re-capturing its own audio. \n" +
            "- Do not use for medical/legal/safety-critical decisions; recognition/translation may be wrong. \n",

    // HelpNotesTitle
    "Notes",

    // HelpNotes
    "- The base language of this app is English, you can use the app UI list to change the languages but it may contain error. \n" +
            "- If some words have not translated, select the language again. \n" +
            "- If you encounter any lagging/bug, restart the app.",

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

    // SettingsResetPW
    "Reset password here",

    // SettingsQuickLinks
    "Quick Links",

    // SettingsNotLoggedInWarning
    "Not logged in, amendments below will not take effect/saved.",

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
    "Re-generate",

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
    "Re-gen",

    // LearningSheetGenerating
    "Generating...",

    // LearningRegenBlockedTitle
    "Cannot Regenerate Yet",

    // LearningRegenBlockedMessage
    "You need at least {minRecords} more translation records to regenerate the learning sheet. You need {needed} more records.",

    // LearningRegenNeedMoreRecords
    "Need {needed} more records to regenerate",

    // LearningRegenCountNotHigher
    "Record count must be higher than the previous generation",

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
    "🔒 Requirements:",

    // QuizCoinRulesCurrentStatus
    "📊 Current Status:",

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
    "✅ You can earn coins on this quiz!",

    // QuizRegenCannotEarnCoins
    "⚠️ You cannot earn coins on this quiz yet.",

    // QuizRegenNeedMoreTemplate
    "You need {count} more translation records to earn coins.",

    // QuizRegenReminder
    "📜 Reminder: You can still practice and retake quizzes for learning, but coins are only awarded on first attempts with sufficient new records.",

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
    "✅ How to Earn:",

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
    "📊 History shows your most recent {limit} records. You can expand this limit in the Shop!",

    // HistoryInfoOlderRecordsMessage
    "💾 Older records are still stored but not displayed here to optimize performance.",

    // HistoryInfoFavoritesMessage
    "⭐ To keep important translations permanently accessible, add them to your Favorites by tapping the heart ❤️ icon on any record.",

    // HistoryInfoViewFavoritesMessage
    "📖 View your saved Favorites in Settings → Favorites.",

    // HistoryInfoFilterMessage
    "🔍 Use the Filter button to search within the displayed {limit} records.",

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
    "✅ New records - refresh available",

    // WordBankRecordsNeeded
    "📊 Records needed to refresh",

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

    // --- Cache ---
    // CacheClearButton
    "Clear Cache",

    // CacheClearSuccess
    "Cache cleared successfully",

    // CacheStatsTemplate
    "Cache: {count} translations stored",
)