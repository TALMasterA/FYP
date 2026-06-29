package com.translator.TalknLearn.model.ui

// Auto-split from UiTextScreens.kt — order must match UiTextKey enum
val SpeechHomeScreenTexts: List<String> = listOf(
    // --- Speech/Home Instructions ---
    // SpeechInstructions
        "Choose source and target languages below. Quick Translate is best for short phrases and single-turn translations.\n" +
            "Supported languages: English, Cantonese, Japanese, Mandarin and more.\n" +
            "Use the ⇄ button to swap the selected languages.\n" +
            "If auto-detect looks stale after you change text/language, tap the refresh icon and try again.",

    // HomeInstructions
        "Select a feature to get started.\n" +
            "Please review Notes & Cautions before using the app.",

    // ContinuousInstructions
    "Set Speaker A and Speaker B languages below. Live Conversation is for continuous, multi-turn dialogue. \n" +
            "Use the toggle to switch who is speaking.",

    // --- Home/Help Screens ---
    // HomeTitle
    "Translator",

    // HelpTitle
    "Notes & Cautions",

    // SpeechTitle
    "Quick Translate",

    // HomeStartButton
    "Quick Translate",

    // HomeFeaturesTitle
    "Features",

    // HomeDiscreteDescription
    "Short phrases & voice translation",

    // HomeContinuousDescription
    "Multi-turn live conversation",

    // HomeLearningDescription
    "Study vocabulary and take quizzes",

    // HelpCurrentTitle
    "Current features",

    // HelpCautionTitle
    "Cautions",

    // HelpCurrentFeatures
    "TRANSLATION FEATURES:\n" +
            "  • Quick Translate - Real-time voice translation for short phrases and sentences\n" +
            "  • Live Conversation - Multi-turn conversation mode with automatic speaker detection\n" +
            "  • Multi-language support including English, Cantonese, Japanese, Mandarin, and more\n" +
            "  • Text-to-speech playback for both original and translated text\n\n" +
            
            "LEARNING & STUDY:\n" +
            "  • Learning Sheets - AI-generated study materials based on your translation history\n" +
            "  • Quiz System - Test your knowledge and earn coins (🪙)\n" +
            "  • Word Bank - Automatically generated vocabulary list from your translations\n" +
            "  • Favorites - Bookmark important translations and save entire conversation sessions\n\n" +
            
            "CUSTOMIZATION:\n" +
            "  • UI Language - Change app interface language (English, Chinese, Japanese, etc.)\n" +
            "  • Theme Settings - Switch between light, dark, or system theme\n" +
            "  • Font Size - Adjust text size from 80% to 150%\n" +
            "  • Color Palettes - Unlock and apply different color themes using coins\n" +
            "  • Voice Settings - Customize text-to-speech voices for different languages\n\n" +
            
            "HISTORY & ORGANIZATION:\n" +
            "  • Translation History - Recent records (expandable via Shop)\n" +
            "  • Filter & Search - Find translations by language or keyword\n" +
            "  • Session Management - Organize Live Conversation recordings by session\n" +
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
            "  • Use earphones during Live Conversation mode to prevent audio feedback loops\n" +
            "  • The app may re-capture its own TTS output if using speakers\n\n" +
            
            "Accuracy & Limitations:\n" +
            "  • Do NOT rely on translations for medical, legal, or safety-critical decisions\n" +
            "  • AI translations may contain errors or cultural misinterpretations\n" +
            "  • Always verify important translations with professional services\n\n" +
            
            "Account & Data:\n" +
            "  • Login required for history, learning, and coin features\n" +
            "  • User data is stored securely in Firebase Firestore\n\n" +
            
            "Troubleshooting:\n" +
            "  • If an action is still unavailable after you completed the required steps, restart the app and try again\n",

    // HelpNotesTitle
    "Notes",

    // HelpNotes
    "💡 TIPS & TROUBLESHOOTING:\n\n" +
            
            "For Best Translation Results:\n" +
            "  • Speak clearly and at a moderate pace\n" +
            "  • Minimize background noise for better recognition accuracy\n" +
            "  • Short, simple sentences work best in Quick Translate mode\n\n" +
            
            "UI Language:\n" +
            "  • Base language is English; other UI languages are AI-translated\n" +
            "  • Some translations may contain minor errors\n" +

            "Updates & Feedback:\n" +
            "  • App version displayed in Settings → About\n" +
            "  • Send feedback via Settings → Feedback, or report issues on GitHub\n",

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
    "Live Conversation",

    // ContinuousStartButton
    "Start conversation",

    // ContinuousStopButton
    "Stop listening",

    // ContinuousStartScreenButton
    "Live Conversation",

    // ContinuousPersonALabel
    "A speaking",

    // ContinuousPersonBLabel
    "B speaking",

    // ContinuousCurrentStringLabel
        "Current speech:",

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
)
