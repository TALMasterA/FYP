package com.example.fyp.model

// Screen-specific UI texts - used by individual screens
val ScreenUiTexts: List<String> = listOf(
    // --- Speech/Home Instructions ---
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

    // --- Home/Help Screens ---
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

    // HelpNotesTitle
    "Notes",

    // HelpNotes
    "- The base language of this app is English, you can use the app UI list to change the languages but it may contain error. \n" +
            "- If some words have not translated, select the language again. \n",

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

    // SettingsNotLoggedInWarning
    "Not logged in, amendments below will not take effect/saved.",

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
)