package com.translator.TalknLearn.model.ui

// Auto-split from UiTextScreens.kt — order must match UiTextKey enum
val LearningQuizScreenTexts: List<String> = listOf(
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

    // LearningSheetWhatIsThisTitle
    "📚 What Is This?",

    // LearningSheetWhatIsThisDesc
    "Your personalized learning sheet based on your translation history. Contains vocabulary, meanings, examples, and grammar notes to help you study. Tap the Quiz button below to test your knowledge!",

    // LearningRegenBlockedTitle
    "Cannot Regenerate Yet",

    // LearningRegenBlockedMessage
    "Regeneration requires at least 5 more records than the previous generation. You currently need {needed} more records.",

    // LearningRegenInfoTitle
    "Regeneration Rules",

    // LearningRegenInfoMessage
    "To regenerate learning materials:\n\n• First generation: Always allowed\n• Regeneration: Requires at least 5 MORE translation records than the previous generation\n\nThe button will be enabled (blue) when you have enough new records. If it's disabled (gray), keep translating to unlock regeneration!\n\n💡 Note: If the count doesn't update after translating, please restart the app to refresh.",

    // --- Quiz ---
    // QuizTitleTemplate
    "Quiz: {language}",

    // QuizOpenButton
    "📝 Quiz",

    // QuizGenerateButton
    "🔄 Generate Quiz",

    // QuizGenerating
    "⏳ Generating...",

    // QuizBlocked
    "🚫 Blocked",

    // QuizWait
    "⏳ Wait...",

    // QuizCanEarnCoins
    "🪙 Can earn coins!",

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
    "Your answer: {Answer}",

    // QuizCorrectAnswerTemplate
    "Correct: {Answer}",

    // QuizQuestionTemplate
    "Question {current} of {total}",

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
    "Congratulations! You earned {Coins} coins!",

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

    // QuizRecordsLabel
    "records",

    // --- History Screen Coins ---

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
        "Open your saved Favorites by tapping the heart icon in the top bar of this History screen.",

    // HistoryInfoFilterMessage
    "Use the Filter button to search within the displayed {limit} records.",

    // HistoryInfoGotItButton
    "Got it",
)
