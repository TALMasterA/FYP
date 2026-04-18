package com.example.fyp.model.ui

/**
 * Filipino (fil-PH) UI text map — Mga teksto sa Filipino.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val FilPhUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Gamitin ang mikropono",
    UiTextKey.CopyButton to "Kopyahin",
    UiTextKey.SpeakScriptButton to "Basahin ang orihinal",
    UiTextKey.TranslateButton to "Isalin",
    UiTextKey.CopyTranslationButton to "Kopyahin ang salin",
    UiTextKey.SpeakTranslationButton to "Basahin ang salin",
    UiTextKey.RecognizingStatus to "Nagre-record...magsalita na, awtomatikong titigil",
    UiTextKey.TranslatingStatus to "Nagsasalin...",
    UiTextKey.SpeakingOriginalStatus to "Binabasa ang orihinal...",
    UiTextKey.SpeakingTranslationStatus to "Binabasa ang salin...",
    UiTextKey.SpeakingLabel to "Nagsasalita",
    UiTextKey.FinishedSpeakingOriginal to "Tapos na basahin ang orihinal",
    UiTextKey.FinishedSpeakingTranslation to "Tapos na basahin ang salin",
    UiTextKey.TtsErrorTemplate to "Error sa boses: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Wika ng app",
    UiTextKey.DetectLanguageLabel to "Tukuyin ang wika",
    UiTextKey.TranslateToLabel to "Isalin sa",

    // Language names
    UiTextKey.LangEnUs to "Ingles",
    UiTextKey.LangZhHk to "Cantonese",
    UiTextKey.LangJaJp to "Hapon",
    UiTextKey.LangZhCn to "Tsino (Simplified)",
    UiTextKey.LangFrFr to "Pranses",
    UiTextKey.LangDeDe to "Aleman",
    UiTextKey.LangKoKr to "Koreano",
    UiTextKey.LangEsEs to "Espanyol",
    UiTextKey.LangIdId to "Indonesian",
    UiTextKey.LangViVn to "Vietnamese",
    UiTextKey.LangThTh to "Thai",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Malay",
    UiTextKey.LangPtBr to "Portuges",
    UiTextKey.LangItIt to "Italyano",
    UiTextKey.LangRuRu to "Ruso",

    // Navigation
    UiTextKey.NavHistory to "Kasaysayan",
    UiTextKey.NavLogin to "Mag-log in",
    UiTextKey.NavLogout to "Mag-log out",
    UiTextKey.NavBack to "Bumalik",
    UiTextKey.ActionCancel to "Kanselahin",
    UiTextKey.ActionDelete to "Burahin",
    UiTextKey.ActionOpen to "Buksan",
    UiTextKey.ActionName to "Pangalanan",
    UiTextKey.ActionSave to "I-save",
    UiTextKey.ActionConfirm to "Kumpirmahin",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Mag-type dito o gamitin ang mikropono...",
    UiTextKey.SpeechTranslatedPlaceholder to "Dito lalabas ang salin...",
    UiTextKey.StatusAzureErrorTemplate to "Azure error: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Error sa pagsasalin: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Mag-log in para makapagsalin",
    UiTextKey.StatusRecognizePreparing to "Inihahanda ang mikropono...(huwag munang magsalita)",
    UiTextKey.StatusRecognizeListening to "Nakikinig...magsalita na",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Nakaraan",
    UiTextKey.PaginationNextLabel to "Susunod",
    UiTextKey.PaginationPageLabelTemplate to "Pahina {page} / {total}",

    // Toast
    UiTextKey.ToastCopied to "Nakopya na",
    UiTextKey.DisableText to "Mag-log in para magsalin at mag-save ng kasaysayan",

    // Error
    UiTextKey.ErrorRetryButton to "Subukang muli",
    UiTextKey.ErrorGenericMessage to "May nangyaring error. Subukan ulit",

    // Shop
    UiTextKey.ShopTitle to "Tindahan",
    UiTextKey.ShopCoinBalance to "Mga barya ko",
    UiTextKey.ShopHistoryExpansionTitle to "Palawakin ang kasaysayan",
    UiTextKey.ShopHistoryExpansionDesc to "Dagdagan ang kasaysayan para makita ang mas maraming salin",
    UiTextKey.ShopCurrentLimit to "Kasalukuyang bilang: {limit} na talaan",
    UiTextKey.ShopMaxLimit to "Pinakamataas na bilang:",
    UiTextKey.ShopBuyHistoryExpansion to "Bumili (+{increment} talaan, {cost} barya)",
    UiTextKey.ShopInsufficientCoins to "Hindi sapat ang barya",
    UiTextKey.ShopMaxLimitReached to "Naabot na ang pinakamataas na bilang",
    UiTextKey.ShopHistoryExpandedTitle to "Matagumpay na napalawak!",
    UiTextKey.ShopHistoryExpandedMessage to "Ang kasaysayan mo ngayon ay {limit} na talaan! Makakakita ka na ng mas maraming salin!",
    UiTextKey.ShopColorPaletteTitle to "Mga tema ng kulay",
    UiTextKey.ShopColorPaletteDesc to "Pumili ng tema ng kulay. 10 barya bawat tema",
    UiTextKey.ShopEntry to "Tindahan",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Setting ng boses",
    UiTextKey.VoiceSettingsDesc to "Pumili ng boses para sa bawat wika",

    // Instructions
    UiTextKey.SpeechInstructions to "Pindutin ang mikropono para sa pagkilala ng boses. Kung ang auto-detect ay hindi nag-update matapos lumipat, pindutin ang refresh sa kanang itaas",
    UiTextKey.HomeInstructions to "Pumili ng feature para magsimula",
    UiTextKey.ContinuousInstructions to "Pumili ng dalawang wika at simulan ang usapan",

    // Home
    UiTextKey.HomeTitle to "Agarang Salin",
    UiTextKey.HelpTitle to "Tulong",
    UiTextKey.SpeechTitle to "Mabilisang Salin",
    UiTextKey.HomeStartButton to "Simulan ang pagsasalin",
    UiTextKey.HomeFeaturesTitle to "Mga Feature",
    UiTextKey.HomeDiscreteDescription to "Isalin ang teksto at maikling audio",
    UiTextKey.HomeContinuousDescription to "Real-time na dalawang-daang usapan",
    UiTextKey.HomeLearningDescription to "Lumikha ng mga materyal at pagsusulit mula sa kasaysayan",

    // Help
    UiTextKey.HelpCurrentTitle to "Kasalukuyang mga feature",
    UiTextKey.HelpCautionTitle to "Pag-iingat",
    UiTextKey.HelpCurrentFeatures to "Kasalukuyang mga feature:\n" +
            "  • Mabilisang Salin: pagkilala ng boses at pagsasalin\n" +
            "  • Tuloy-tuloy na Usapan: dalawang-daang pagsasalin ng boses\n" +
            "  • Kasaysayan: tingnan ang nakaraang salin\n" +
            "  • Mga Materyal sa Pag-aaral: lumikha ng bokabularyo at pagsusulit\n\n" +
            "Pagsasalin:\n" +
            "  • Azure AI pagkilala ng boses\n" +
            "  • Azure Translation Service\n",
    UiTextKey.HelpCaution to "Pag-iingat:\n" +
            "  • Kailangan ng internet ang pagkilala ng boses\n" +
            "  • Gumagana ang naka-cache na salin offline\n" +
            "  • I-verify ang mga mahahalagang salin sa eksperto\n\n" +
            "Account at data:\n" +
            "  • Kailangan mag-log in para sa kasaysayan, pag-aaral, at barya\n" +
            "  • Ligtas na naka-store sa Firebase Firestore\n\n" +
            "Troubleshooting:\n" +
            "  • Kung hindi gumagana, i-restart ang app\n",
    UiTextKey.HelpNotesTitle to "Mga tip",
    UiTextKey.HelpNotes to "💡 Mga tip sa paggamit:\n\n" +
            "Para sa mas magandang salin:\n" +
            "  • Magsalita nang malinaw sa normal na bilis\n" +
            "  • Bawasan ang ingay para sa mas magandang pagkilala\n" +
            "  • Mabilisang salin para sa mga maikling pangungusap\n\n" +
            "Wika ng app:\n" +
            "  • Default: Ingles, iba sa pamamagitan ng AI\n" +
            "  • Manu-manong salin para sa Cantonese\n" +
            "Mga update at feedback:\n" +
            "  • Bersyon ng app sa Settings → About\n" +
            "  • Mag-iwan ng feedback sa Settings → Feedback\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Feedback",
    UiTextKey.FeedbackDesc to "Salamat sa feedback! Ibahagi ang iyong mungkahi, bug, o rating",
    UiTextKey.FeedbackMessagePlaceholder to "I-type ang feedback...",
    UiTextKey.FeedbackSubmitButton to "Ipadala",
    UiTextKey.FeedbackSubmitting to "Ipinapadala...",
    UiTextKey.FeedbackSuccessTitle to "Salamat!",
    UiTextKey.FeedbackSuccessMessage to "Matagumpay na naipadala ang feedback. Salamat!",
    UiTextKey.FeedbackErrorTitle to "Hindi naipadala",
    UiTextKey.FeedbackErrorMessage to "Hindi naipadala. Suriin ang koneksyon at subukan ulit",
    UiTextKey.FeedbackMessageRequired to "Kailangan ng mensahe ng feedback",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Tuloy-tuloy na Usapan",
    UiTextKey.ContinuousStartButton to "Simulan ang usapan",
    UiTextKey.ContinuousStopButton to "Itigil ang pag-record",
    UiTextKey.ContinuousStartScreenButton to "Tuloy-tuloy na Usapan",
    UiTextKey.ContinuousPersonALabel to "Nagsasalita si A",
    UiTextKey.ContinuousPersonBLabel to "Nagsasalita si B",
    UiTextKey.ContinuousCurrentStringLabel to "Kasalukuyang teksto:",
    UiTextKey.ContinuousSpeakerAName to "Tao A",
    UiTextKey.ContinuousSpeakerBName to "Tao B",
    UiTextKey.ContinuousTranslationSuffix to " · Salin",
    UiTextKey.ContinuousPreparingMicText to "Inihahanda ang mikropono...(huwag munang magsalita)",
    UiTextKey.ContinuousTranslatingText to "Nagsasalin...",

    // History
    UiTextKey.HistoryTitle to "Kasaysayan",
    UiTextKey.HistoryTabDiscrete to "Mabilisang Salin",
    UiTextKey.HistoryTabContinuous to "Tuloy-tuloy na Usapan",
    UiTextKey.HistoryNoContinuousSessions to "Wala pang session ng usapan",
    UiTextKey.HistoryNoDiscreteRecords to "Wala pang salin",
    UiTextKey.DialogDeleteRecordTitle to "Burahin ang talaan?",
    UiTextKey.DialogDeleteRecordMessage to "Hindi ito maaaring ibalik",
    UiTextKey.DialogDeleteSessionTitle to "Burahin ang session?",
    UiTextKey.DialogDeleteSessionMessage to "Lahat ng talaan sa session na ito ay mabubura. Hindi maaaring ibalik",
    UiTextKey.HistoryDeleteSessionButton to "Burahin",
    UiTextKey.HistoryNameSessionTitle to "Pangalanan",
    UiTextKey.HistorySessionNameLabel to "Pangalan ng session",
    UiTextKey.HistorySessionTitleTemplate to "Session {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} na talaan",

    // Filter
    UiTextKey.FilterDropdownDefault to "Lahat ng wika",
    UiTextKey.FilterTitle to "I-filter ang kasaysayan",
    UiTextKey.FilterLangDrop to "Wika",
    UiTextKey.FilterKeyword to "Keyword",
    UiTextKey.FilterApply to "Ilapat",
    UiTextKey.FilterCancel to "Kanselahin",
    UiTextKey.FilterClear to "I-clear ang filter",
    UiTextKey.FilterHistoryScreenTitle to "Filter",

    // Auth
    UiTextKey.AuthLoginTitle to "Mag-log in",
    UiTextKey.AuthRegisterTitle to "Magrehistro (Naka-pause)",
    UiTextKey.AuthLoginHint to "Gamitin ang narehistrong email at password",
    UiTextKey.AuthRegisterRules to "Ang pagrehistro ay naka-pause habang nasa development\nPaalala: Hindi maaaring ma-recover ang password kung mali ang email\n" +
            "Mga patakaran sa pagrehistro:\n" +
            "• Tamang format ng email (hal. name@example.com)\n" +
            "• Hindi bababa sa 6 na karakter ang password\n" +
            "• Dapat magkatugma ang kumpirmasyon ng password",
    UiTextKey.AuthEmailLabel to "Email",
    UiTextKey.AuthPasswordLabel to "Password",
    UiTextKey.AuthConfirmPasswordLabel to "Kumpirmahin ang password",
    UiTextKey.AuthLoginButton to "Mag-log in",
    UiTextKey.AuthRegisterButton to "Magrehistro",
    UiTextKey.AuthToggleToRegister to "Walang account? Magrehistro (Naka-pause)",
    UiTextKey.AuthToggleToLogin to "May account na? Mag-log in",
    UiTextKey.AuthErrorPasswordsMismatch to "Hindi magkatugma ang password",
    UiTextKey.AuthErrorPasswordTooShort to "Hindi bababa sa 6 na karakter ang password",
    UiTextKey.AuthRegistrationDisabled to "Ang pagrehistro ay naka-pause habang nasa development",
    UiTextKey.AuthResetEmailSent to "Naipadala na ang reset email (kung may account). Suriin ang inbox",

    // Password reset
    UiTextKey.ForgotPwText to "Nakalimutan ang password?",
    UiTextKey.ResetPwTitle to "I-reset ang password",
    UiTextKey.ResetPwText to "Ilagay ang email ng account. Magpapadala kami ng reset link\nSiguraduhing nakarehistro ang email\n",
    UiTextKey.ResetSendingText to "Ipinapadala...",
    UiTextKey.ResetSendText to "Ipadala ang reset email",

    // Settings
    UiTextKey.SettingsTitle to "Settings",
    UiTextKey.SettingsPrimaryLanguageTitle to "Pangunahing wika",
    UiTextKey.SettingsPrimaryLanguageDesc to "Ginagamit para sa paliwanag at mga gabay sa pag-aaral",
    UiTextKey.SettingsPrimaryLanguageLabel to "Pangunahing wika",
    UiTextKey.SettingsFontSizeTitle to "Laki ng font",
    UiTextKey.SettingsFontSizeDesc to "I-adjust ang laki ng font (naka-sync sa mga device)",
    UiTextKey.SettingsScaleTemplate to "Scale: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Mga tema ng kulay",
    UiTextKey.SettingsColorPaletteDesc to "Pumili ng tema ng kulay. 10 barya bawat tema",
    UiTextKey.SettingsColorCostTemplate to "{cost} barya",
    UiTextKey.SettingsColorUnlockButton to "I-unlock",
    UiTextKey.SettingsColorSelectButton to "Piliin",
    UiTextKey.SettingsColorAlreadyUnlocked to "Naka-unlock na",
    UiTextKey.SettingsPreviewHeadline to "Headline: Malaking teksto",
    UiTextKey.SettingsPreviewBody to "Body: Karaniwang teksto",
    UiTextKey.SettingsPreviewLabel to "Label: Maliit na teksto",
    UiTextKey.SettingsAboutTitle to "Tungkol sa",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Naka-log in. Awtomatikong naka-save at naka-sync ang settings",
    UiTextKey.SettingsThemeTitle to "Tema",
    UiTextKey.SettingsThemeDesc to "Pumili ng tema: System, Maliwanag, Madilim, o Naka-schedule",
    UiTextKey.SettingsThemeSystem to "System",
    UiTextKey.SettingsThemeLight to "Maliwanag",
    UiTextKey.SettingsThemeDark to "Madilim",
    UiTextKey.SettingsThemeScheduled to "Naka-schedule",
    UiTextKey.SettingsResetPW to "I-reset ang password",
    UiTextKey.SettingsQuickLinks to "Iba pang settings",
    UiTextKey.SettingsNotLoggedInWarning to "Mag-log in para makita ang account settings. Maaaring baguhin ang wika ng app nang hindi naka-log in",
    UiTextKey.SettingsVoiceTitle to "Setting ng boses",
    UiTextKey.SettingsVoiceDesc to "Pumili ng boses para sa bawat wika",
    UiTextKey.SettingsVoiceLanguageLabel to "Wika",
    UiTextKey.SettingsVoiceSelectLabel to "Boses",
    UiTextKey.SettingsVoiceDefault to "Default",

    // Learning
    UiTextKey.LearningTitle to "Pag-aaral",
    UiTextKey.LearningHintCount to "(*) Bilang = mga salin na may wikang ito",
    UiTextKey.LearningErrorTemplate to "Error: %s",
    UiTextKey.LearningGenerate to "Lumikha",
    UiTextKey.LearningRegenerate to "Muling lumikha",
    UiTextKey.LearningGenerating to "Lumilikha...",
    UiTextKey.LearningOpenSheetTemplate to "Materyal {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Materyal sa pag-aaral {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Pangunahing wika: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Kasalukuyang bilang: {nowCount} (noong lumikha: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Walang nilalaman",
    UiTextKey.LearningSheetRegenerate to "Muling lumikha",
    UiTextKey.LearningSheetGenerating to "Lumilikha...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Ano ito?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Ang mga materyal sa pag-aaral ay nalilikha mula sa kasaysayan ng pagsasalin. May mga bokabularyo, depinisyon, halimbawa, at tala sa grammar. Subukan ang iyong kaalaman sa pagsusulit!",
    UiTextKey.LearningRegenBlockedTitle to "Hindi maaaring muling lumikha",
    UiTextKey.LearningRegenBlockedMessage to "Kailangan ng hindi bababa sa 5 na karagdagang talaan para muling lumikha. Kailangan pa ng {needed}",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Kailangan pa ng {needed} na talaan para muling lumikha (kailangan 5 pa)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Ang bilang ay kailangang higit pa sa noong huling paglikha",
    UiTextKey.LearningRegenInfoTitle to "Mga patakaran sa muling paglikha",
    UiTextKey.LearningRegenInfoMessage to "Muling lumikha ng materyal:\n\n• Unang beses: kahit kailan\n• Muling lumikha: kailangan ng 5 pa na talaan\n\nMagiging berde ang button kapag handa na. Kung kulay abo, magsalin pa!\n\n💡 Tip: Kung hindi nag-update ang bilang, i-restart ang app",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Nalikha na ang pagsusulit para sa bersyong ito. Muling lumikha ng materyal para sa bagong pagsusulit",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Pagsusulit: {language}",
    UiTextKey.QuizOpenButton to "📝 Pagsusulit",
    UiTextKey.QuizGenerateButton to "🔄 Lumikha ng pagsusulit",
    UiTextKey.QuizGenerating to "⏳ Lumilikha...",
    UiTextKey.QuizUpToDate to "✓ Pinakabago",
    UiTextKey.QuizBlocked to "🚫 Na-block",
    UiTextKey.QuizWait to "⏳ Maghintay...",
    UiTextKey.QuizMaterialsQuizTemplate to "Materyal: {materials} | Pagsusulit: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Maaaring kumita ng barya!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 Kailangan pa ng {count} para sa barya",
    UiTextKey.QuizCancelButton to "Kanselahin",
    UiTextKey.QuizPreviousButton to "Nakaraan",
    UiTextKey.QuizNextButton to "Susunod",
    UiTextKey.QuizSubmitButton to "Ipasa",
    UiTextKey.QuizRetakeButton to "Ulitin",
    UiTextKey.QuizBackButton to "Bumalik",
    UiTextKey.QuizLoadingText to "Naglo-load ng pagsusulit...",
    UiTextKey.QuizGeneratingText to "Lumilikha ng pagsusulit...",
    UiTextKey.QuizNoMaterialsTitle to "Walang nahanap na materyal",
    UiTextKey.QuizNoMaterialsMessage to "Lumikha muna ng materyal sa pag-aaral bago subukan ang pagsusulit",
    UiTextKey.QuizErrorTitle to "⚠️ Error sa pagsusulit",
    UiTextKey.QuizErrorSuggestion to "Mungkahi: Gamitin ang button sa itaas para lumikha ng pagsusulit",
    UiTextKey.QuizCompletedTitle to "Tapos na ang pagsusulit!",
    UiTextKey.QuizAnswerReviewTitle to "Suriin ang mga sagot",
    UiTextKey.QuizYourAnswerTemplate to "Sagot mo: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Tamang sagot: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Tanong {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Hindi maaaring muling lumikha: materyal({materials}) < pagsusulit({quiz}), magsalin pa",
    UiTextKey.QuizAnotherGenInProgress to "⏳ May ibang paglikha na nagaganap. Maghintay",
    UiTextKey.QuizCoinRulesTitle to "🪙 Mga patakaran sa barya",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Paano kumita:",
    UiTextKey.QuizCoinRulesRequirements to "Mga kinakailangan:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Kasalukuyang status:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Maaari kang kumita ng barya sa susunod na pagsusulit!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• Kailangan pa ng {count} para sa barya",
    UiTextKey.QuizCoinRule1Coin to "• 1 barya bawat tamang sagot",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Unang pagsubok lamang ang binibilang",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Dapat tumugma ang pagsusulit sa materyal",
    UiTextKey.QuizCoinRulePlus10 to "• Hindi bababa sa 10 karagdagang talaan mula sa huling gantimpala",
    UiTextKey.QuizCoinRuleNoDelete to "• Hindi ibabalik ang barya kapag nabura ang talaan",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Materyal: {count} talaan",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Pagsusulit: {count} talaan",
    UiTextKey.QuizCoinRuleGotIt to "Naintindihan ko!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Muling lumikha ng pagsusulit?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Maaaring kumita ng barya sa pagsusulit na ito! (unang pagsubok lamang)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Hindi maaaring kumita ng barya sa pagsusulit na ito",
    UiTextKey.QuizRegenNeedMoreTemplate to "Kailangan pa ng {count} na salin para maging karapat-dapat (10 higit pa sa huling gantimpala)",
    UiTextKey.QuizRegenReminder to "Tip: Puwedeng mag-practice at ulitin, pero barya lang sa unang pagsubok na may sapat na talaan",
    UiTextKey.QuizRegenGenerateButton to "Lumikha",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Nakakuha ng barya!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Congratulations! Nakakuha ka ng {Coins} barya!",
    UiTextKey.QuizCoinsRule1 to "• 1 barya bawat tamang sagot sa unang pagsubok",
    UiTextKey.QuizCoinsRule2 to "• Walang barya sa mga susunod na pagsubok",
    UiTextKey.QuizCoinsRule3 to "• Ang bagong pagsusulit ay nangangailangan ng 10 karagdagang talaan",
    UiTextKey.QuizCoinsRule4 to "• Dapat tumugma ang pagsusulit sa materyal",
    UiTextKey.QuizCoinsRule5 to "• Ang mga barya ay bahagi ng kasaysayan",
    UiTextKey.QuizCoinsGreatButton to "Mahusay!",
    UiTextKey.QuizOutdatedMessage to "Ang pagsusulit na ito ay gumagamit ng lumang materyal",
    UiTextKey.QuizRecordsLabel to "Mga talaan",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Mga barya ko",
    UiTextKey.HistoryCoinRulesTitle to "Mga patakaran sa barya:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Paano kumita:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 barya bawat tamang sagot",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Unang pagsubok lamang bawat bersyon",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Dapat tumugma ang pagsusulit sa kasalukuyang materyal",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Mga patakaran laban sa pandaraya:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Kailangan ng 10 bagong salin mula sa huling gantimpala",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• Bersyon ng pagsusulit dapat tumugma sa materyal",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Ang pagbura ng talaan ay nagba-block ng muling paglikha (maliban kung mas mataas ang bilang)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Walang barya sa mga susunod na pagsubok",
    UiTextKey.HistoryCoinTipsTitle to "💡 Mga tip:",
    UiTextKey.HistoryCoinTipsRule1 to "• Regular na magdagdag ng salin",
    UiTextKey.HistoryCoinTipsRule2 to "• Mag-aral mabuti bago ang unang pagsubok!",
    UiTextKey.HistoryCoinGotItButton to "Naintindihan ko!",

    // History info
    UiTextKey.HistoryInfoTitle to "Impormasyon ng kasaysayan",
    UiTextKey.HistoryInfoLimitMessage to "Ang kasaysayan ay nagpapakita ng {limit} na pinakabagong talaan. Palawakin sa tindahan!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Ang mas lumang talaan ay naka-store pero nakatago para sa performance",
    UiTextKey.HistoryInfoFavoritesMessage to "Kung gusto mong itago ang isang salin, pindutin ang ❤️ para i-save sa mga paborito",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Tingnan ang mga na-save sa Settings → Mga Paborito",
    UiTextKey.HistoryInfoFilterMessage to "Gamitin ang filter para maghanap sa {limit} na ipinapakitang talaan",
    UiTextKey.HistoryInfoGotItButton to "Naintindihan ko",

    // Word bank
    UiTextKey.WordBankTitle to "Bangko ng salita",
    UiTextKey.WordBankSelectLanguage to "Pumili ng wika para tingnan o lumikha ng bangko ng salita:",
    UiTextKey.WordBankNoHistory to "Wala pang kasaysayan ng pagsasalin",
    UiTextKey.WordBankNoHistoryHint to "Magsimulang magsalin para lumikha ng bangko ng salita!",
    UiTextKey.WordBankWordsCount to "Mga salita",
    UiTextKey.WordBankGenerating to "Lumilikha...",
    UiTextKey.WordBankGenerate to "Lumikha ng bangko ng salita",
    UiTextKey.WordBankRegenerate to "Muling lumikha ng bangko ng salita",
    UiTextKey.WordBankRefresh to "🔄 I-refresh ang bangko",
    UiTextKey.WordBankEmpty to "Walang laman ang bangko ng salita",
    UiTextKey.WordBankEmptyHint to "Pindutin sa itaas para lumikha mula sa kasaysayan",
    UiTextKey.WordBankExample to "Halimbawa:",
    UiTextKey.WordBankDifficulty to "Antas:",
    UiTextKey.WordBankFilterCategory to "Kategorya",
    UiTextKey.WordBankFilterCategoryAll to "Lahat ng kategorya",
    UiTextKey.WordBankFilterDifficultyLabel to "Antas:",
    UiTextKey.WordBankFilterNoResults to "Walang salitang tumutugma sa filter",
    UiTextKey.WordBankRefreshAvailable to "✅ May update na!",
    UiTextKey.WordBankRecordsNeeded to "Mga talaan (kailangan 20 para mag-update)",
    UiTextKey.WordBankRegenInfoTitle to "Mga patakaran sa pag-update",
    UiTextKey.WordBankRegenInfoMessage to "I-update ang bangko ng salita:\n\n• Unang beses: kahit kailan\n• Update: kailangan ng 20 karagdagang talaan\n\nMagiging berde ang button kapag handa na. Kung kulay abo, magsalin pa!\n\n💡 Tip: Kung hindi nag-update ang bilang, i-restart ang app",
    UiTextKey.WordBankHistoryCountTemplate to "Kasalukuyang bilang: {nowCount} (noong lumikha: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Mag-log out?",
    UiTextKey.DialogLogoutMessage to "Kailangan mag-log in ulit para magsalin at makita ang kasaysayan",
    UiTextKey.DialogGenerateOverwriteTitle to "I-overwrite ang materyal?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Ang kasalukuyang materyal ay mao-overwrite\nLumikha ng materyal para sa {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Profile",
    UiTextKey.ProfileUsernameLabel to "Username",
    UiTextKey.ProfileUsernameHint to "Ilagay ang username",
    UiTextKey.ProfileUpdateButton to "I-update ang profile",
    UiTextKey.ProfileUpdateSuccess to "Na-update na ang profile",
    UiTextKey.ProfileUpdateError to "Hindi na-update",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Burahin ang account",
    UiTextKey.AccountDeleteWarning to "⚠️ Permanente at hindi na maaaring ibalik!",
    UiTextKey.AccountDeleteConfirmMessage to "Lahat ng data ay permanenteng mabubura: kasaysayan, bangko ng salita, materyal, settings. Ilagay ang password para kumpirmahin",
    UiTextKey.AccountDeletePasswordLabel to "Password",
    UiTextKey.AccountDeleteButton to "Burahin ang account",
    UiTextKey.AccountDeleteSuccess to "Matagumpay na nabura ang account",
    UiTextKey.AccountDeleteError to "Hindi nabura",
    UiTextKey.AccountDeleteReauthRequired to "Ilagay ang password para kumpirmahin ang pagbura",

    // Favorites
    UiTextKey.FavoritesTitle to "Mga Paborito",
    UiTextKey.FavoritesEmpty to "Wala pang paborito",
    UiTextKey.FavoritesAddSuccess to "Naidagdag sa mga paborito",
    UiTextKey.FavoritesRemoveSuccess to "Naalis sa mga paborito",
    UiTextKey.FavoritesAddButton to "Idagdag sa paborito",
    UiTextKey.FavoritesRemoveButton to "Alisin sa paborito",
    UiTextKey.FavoritesNoteLabel to "Tala",
    UiTextKey.FavoritesNoteHint to "Magdagdag ng tala (opsyonal)",
    UiTextKey.FavoritesTabRecords to "Mga talaan",
    UiTextKey.FavoritesTabSessions to "Mga session",
    UiTextKey.FavoritesSessionsEmpty to "Wala pang paboritong session",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} na mensahe",

    // Custom words
    UiTextKey.CustomWordsTitle to "Mga custom na salita",
    UiTextKey.CustomWordsAdd to "Magdagdag ng salita",
    UiTextKey.CustomWordsEdit to "I-edit ang salita",
    UiTextKey.CustomWordsDelete to "Burahin ang salita",
    UiTextKey.CustomWordsOriginalLabel to "Orihinal",
    UiTextKey.CustomWordsTranslatedLabel to "Salin",
    UiTextKey.CustomWordsPronunciationLabel to "Pagbigkas (opsyonal)",
    UiTextKey.CustomWordsExampleLabel to "Halimbawa (opsyonal)",
    UiTextKey.CustomWordsSaveSuccess to "Na-save na ang salita",
    UiTextKey.CustomWordsDeleteSuccess to "Nabura na ang salita",
    UiTextKey.CustomWordsAlreadyExists to "May ganitong salita na",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Wikang pinagmulan",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Wikang pagsasalin",
    UiTextKey.CustomWordsSaveButton to "I-save",
    UiTextKey.CustomWordsCancelButton to "Kanselahin",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Auto-detect",
    UiTextKey.LanguageDetectDetecting to "Tinutukoy...",
    UiTextKey.LanguageDetectedTemplate to "Natukoy: {language}",
    UiTextKey.LanguageDetectFailed to "Hindi natukoy",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "I-scan ang teksto mula sa larawan",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Babala: Ang pagkilala ng teksto ay maaaring hindi ganap na tumpak. Suriin ulit ang nakilalang teksto. " +
            "Sinusuportahan ang Latin (Ingles, atbp.), Tsino, Hapon, at Koreano",
    UiTextKey.ImageRecognitionScanning to "Ini-scan ang teksto...",
    UiTextKey.ImageRecognitionSuccess to "Matagumpay na nakilala ang teksto",

    // Cache
    UiTextKey.CacheClearButton to "I-clear ang cache",
    UiTextKey.CacheClearSuccess to "Na-clear na ang cache",
    UiTextKey.CacheStatsTemplate to "Cache: {count} na naka-save na salin",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Awtomatikong tema",
    UiTextKey.SettingsAutoThemeDesc to "Awtomatikong magpalit ng maliwanag/madilim batay sa oras",
    UiTextKey.SettingsAutoThemeEnabled to "Naka-on",
    UiTextKey.SettingsAutoThemeDisabled to "Naka-off",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Simula ng madilim:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Simula ng maliwanag:",
    UiTextKey.SettingsAutoThemePreview to "Awtomatikong magpapalit ng tema batay sa oras na itinakda",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Offline mode",
    UiTextKey.OfflineModeMessage to "Offline ka. Ipapakita ang naka-cache na data",
    UiTextKey.OfflineModeRetry to "Subukang kumonekta ulit",
    UiTextKey.OfflineDataCached to "Available ang naka-cache na data",
    UiTextKey.OfflineSyncPending to "Ang mga pagbabago ay isi-sync kapag online na",

    // Image capture
    UiTextKey.ImageSourceTitle to "Pumili ng source ng larawan",
    UiTextKey.ImageSourceCamera to "Kumuha ng litrato",
    UiTextKey.ImageSourceGallery to "Pumili mula sa gallery",
    UiTextKey.ImageSourceCancel to "Kanselahin",
    UiTextKey.CameraCaptureContentDesc to "Kumuha ng litrato",

    // Friends
    UiTextKey.FriendsTitle to "Mga Kaibigan",
    UiTextKey.FriendsMenuButton to "Mga Kaibigan",
    UiTextKey.FriendsAddButton to "Magdagdag ng kaibigan",
    UiTextKey.FriendsSearchTitle to "Maghanap ng gumagamit",
    UiTextKey.FriendsSearchPlaceholder to "Username o ID...",
    UiTextKey.FriendsSearchMinChars to "Maglagay ng hindi bababa sa 2 karakter",
    UiTextKey.FriendsSearchNoResults to "Walang nahanap na gumagamit",
    UiTextKey.FriendsListEmpty to "Magdagdag ng kaibigan para mag-chat at magbahagi ng materyal",
    UiTextKey.FriendsRequestsSection to "Mga kahilingan ({count})",
    UiTextKey.FriendsSectionTitle to "Mga Kaibigan ({count})",
    UiTextKey.FriendsAcceptButton to "Tanggapin",
    UiTextKey.FriendsRejectButton to "Tanggihan",
    UiTextKey.FriendsRemoveButton to "Alisin",
    UiTextKey.FriendsRemoveDialogTitle to "Alisin ang kaibigan",
    UiTextKey.FriendsRemoveDialogMessage to "Alisin si {username} sa listahan ng kaibigan?",
    UiTextKey.FriendsSendRequestButton to "Idagdag",
    UiTextKey.FriendsRequestSentSuccess to "Naipadala na ang kahilingan!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Tinanggap na ang kahilingan!",
    UiTextKey.FriendsRequestRejectedSuccess to "Tinanggihan ang kahilingan",
    UiTextKey.FriendsRemovedSuccess to "Naalis na ang kaibigan",
    UiTextKey.FriendsRequestFailed to "Hindi naipadala",
    UiTextKey.FriendsCloseButton to "Isara",
    UiTextKey.FriendsCancelButton to "Kanselahin",
    UiTextKey.FriendsRemoveConfirm to "Alisin",
    UiTextKey.FriendsNewRequestsTemplate to "May {count} bagong kahilingan!",
    UiTextKey.FriendsSentRequestsSection to "Mga naipadala ({count})",
    UiTextKey.FriendsPendingStatus to "Naghihintay",
    UiTextKey.FriendsCancelRequestButton to "Kanselahin ang kahilingan",
    UiTextKey.FriendsUnreadMessageDesc to "Magpadala ng mensahe",
    UiTextKey.FriendsDeleteModeButton to "Burahin ang kaibigan",
    UiTextKey.FriendsDeleteSelectedButton to "Burahin ang napili",
    UiTextKey.FriendsDeleteMultipleTitle to "Burahin ang kaibigan",
    UiTextKey.FriendsDeleteMultipleMessage to "Burahin ang {count} na napiling kaibigan?",
    UiTextKey.FriendsSearchMinChars3 to "Maglagay ng hindi bababa sa 3 karakter para sa pangalan",
    UiTextKey.FriendsSearchByUserIdHint to "O maghanap nang eksakto gamit ang User ID",
    UiTextKey.FriendsStatusAlreadyFriends to "Magkaibigan na",
    UiTextKey.FriendsStatusRequestSent to "Naipadala na — naghihintay ng tugon",
    UiTextKey.FriendsStatusRequestReceived to "Ang gumagamit na ito ay nagpadala ng kahilingan sa iyo",

    // Chat
    UiTextKey.ChatTitle to "Chat kay {username}",
    UiTextKey.ChatInputPlaceholder to "Mag-type ng mensahe...",
    UiTextKey.ChatSendButton to "Ipadala",
    UiTextKey.ChatEmpty to "Wala pang mensahe. Magsimula na ng usapan!",
    UiTextKey.ChatMessageSent to "Naipadala na ang mensahe",
    UiTextKey.ChatMessageFailed to "Hindi naipadala",
    UiTextKey.ChatMarkingRead to "Binabasa...",
    UiTextKey.ChatLoadingMessages to "Naglo-load ng mga mensahe...",
    UiTextKey.ChatToday to "Ngayong araw",
    UiTextKey.ChatYesterday to "Kahapon",
    UiTextKey.ChatUnreadBadge to "{count} hindi pa nabasa",
    UiTextKey.ChatTranslateButton to "Isalin",
    UiTextKey.ChatTranslateDialogTitle to "Isalin ang chat",
    UiTextKey.ChatTranslateDialogMessage to "Isalin ang mga mensahe ng kaibigan sa wika mo? Awtomatikong matutukoy at maisasalin ang bawat mensahe",
    UiTextKey.ChatTranslateConfirm to "Isalin lahat",
    UiTextKey.ChatTranslating to "Isinasalin ang mga mensahe...",
    UiTextKey.ChatTranslated to "Naisalin na ang mga mensahe",
    UiTextKey.ChatShowOriginal to "Ipakita ang orihinal",
    UiTextKey.ChatShowTranslation to "Ipakita ang salin",
    UiTextKey.ChatTranslateFailed to "Hindi naisalin",
    UiTextKey.ChatTranslatedLabel to "Naisalin",

    // Sharing
    UiTextKey.ShareTitle to "Ibahagi",
    UiTextKey.ShareInboxTitle to "Inbox ng ibinahagi",
    UiTextKey.ShareInboxEmpty to "Wala pang ibinahagi. Maaaring magbahagi ng salita at materyal ang mga kaibigan!",
    UiTextKey.ShareWordButton to "Ibahagi ang salita",
    UiTextKey.ShareMaterialButton to "Ibahagi ang materyal",
    UiTextKey.ShareSelectFriendTitle to "Pumili ng kaibigan",
    UiTextKey.ShareSelectFriendMessage to "Pumili ng kaibigan na pagbabahagian:",
    UiTextKey.ShareSuccess to "Matagumpay na naibahagi!",
    UiTextKey.ShareFailed to "Hindi naibahagi",
    UiTextKey.ShareWordWith to "Ibahagi ang salita kay {username}",
    UiTextKey.ShareMaterialWith to "Ibahagi ang materyal kay {username}",
    UiTextKey.ShareAcceptButton to "Tanggapin",
    UiTextKey.ShareDismissButton to "I-dismiss",
    UiTextKey.ShareAccepted to "Naidagdag sa koleksyon",
    UiTextKey.ShareDismissed to "Na-dismiss ang item",
    UiTextKey.ShareActionFailed to "Hindi nagawa",
    UiTextKey.ShareTypeWord to "Salita",
    UiTextKey.ShareTypeLearningSheet to "Materyal sa pag-aaral",
    UiTextKey.ShareTypeQuiz to "Pagsusulit",
    UiTextKey.ShareReceivedFrom to "Mula kay: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} bagong item!",
    UiTextKey.ShareViewFullMaterial to "Pindutin ang \"Tingnan\" para makita ang buong materyal",
    UiTextKey.ShareDeleteItemTitle to "Burahin ang item",
    UiTextKey.ShareDeleteItemMessage to "Burahin ang ibinahaging item na ito? Hindi na maibabalik",
    UiTextKey.ShareDeleteButton to "Burahin",
    UiTextKey.ShareViewButton to "Tingnan",
    UiTextKey.ShareItemNotFound to "Hindi nahanap ang item",
    UiTextKey.ShareNoContent to "Walang nilalaman sa materyal",
    UiTextKey.ShareSaveToSelf to "I-save sa sariling inbox",
    UiTextKey.ShareSavedToSelf to "Na-save na sa inbox mo!",

    // My profile
    UiTextKey.MyProfileTitle to "Profile ko",
    UiTextKey.MyProfileUserId to "User ID",
    UiTextKey.MyProfileUsername to "Username",
    UiTextKey.MyProfileDisplayName to "Display name",
    UiTextKey.MyProfileCopyUserId to "Kopyahin ang ID",
    UiTextKey.MyProfileCopyUsername to "Kopyahin ang username",
    UiTextKey.MyProfileShare to "Ibahagi ang profile",
    UiTextKey.MyProfileCopied to "Nakopya na!",
    UiTextKey.MyProfileLanguages to "Mga wika",
    UiTextKey.MyProfilePrimaryLanguage to "Pangunahing wika",
    UiTextKey.MyProfileLearningLanguages to "Mga wikang pinag-aaralan",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Pahina ng mga kaibigan",
    UiTextKey.FriendsInfoMessage to "• Mag-pull down para i-refresh ang listahan, kahilingan, at status\n" +
            "• Pindutin ang card para magbukas ng chat\n" +
            "• Pulang tuldok (●) para sa hindi pa nababasang mensahe; ✓✓ para sa nabasa lahat\n" +
            "• 📥 para sa inbox ng ibinahagi; ✓✓ para i-clear ang badge\n" +
            "• 🚫 para mag-block — maaalis ang kaibigan at hindi na maaaring makipag-ugnayan\n" +
            "• Ang pag-block ay nagbubura rin ng kasaysayan ng chat\n" +
            "• Trash icon para sa delete mode\n" +
            "• Ang pag-alis ng kaibigan ay nagbubura ng lahat ng mensahe\n" +
            "• Search icon para maghanap sa pamamagitan ng pangalan o ID\n" +
            "• Naka-off ang notipikasyon bilang default — i-on sa Settings\n",
    UiTextKey.FriendsEmptyTitle to "Wala pang kaibigan",
    UiTextKey.FriendsEmptyMessage to "Pindutin ang \"Magdagdag ng kaibigan\" para maghanap sa pamamagitan ng pangalan o ID\n",
    UiTextKey.FriendsInfoGotItButton to "Naintindihan ko",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Pahina ng pag-aaral",
    UiTextKey.LearningInfoMessage to "• Mag-pull para i-refresh ang listahan\n" +
            "• Bawat card ay nagpapakita ng wika at bilang\n" +
            "• \"Lumikha\" para sa materyal (libre ang unang beses)\n" +
            "• Muling lumikha kailangan ng 5 karagdagang talaan\n" +
            "• Button ng materyal para buksan ang nilikha\n" +
            "• Matapos lumikha ng materyal, maaari nang mag-pagsusulit",
    UiTextKey.LearningEmptyTitle to "Wala pang kasaysayan ng pagsasalin",
    UiTextKey.LearningEmptyMessage to "Magsimulang magsalin para lumikha ng talaan\n" +
            "Ang materyal ay gawa mula sa kasaysayan\n" +
            "Pagkatapos magsalin, mag-pull para i-refresh",
    UiTextKey.LearningInfoGotItButton to "Naintindihan ko",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Pahina ng bangko ng salita",
    UiTextKey.WordBankInfoMessage to "• Mag-pull para i-refresh ang listahan ng wika\n" +
            "• Pumili ng wika para tingnan o lumikha\n" +
            "• Bangko ng salita ay gawa mula sa kasaysayan\n" +
            "• Update kailangan ng 20 karagdagang talaan\n" +
            "• Magdagdag ng mga custom na salita nang manu-mano\n" +
            "• Ibahagi ang mga salita sa kaibigan",
    UiTextKey.WordBankInfoGotItButton to "Naintindihan ko",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Inbox ng ibinahagi",
    UiTextKey.ShareInboxInfoMessage to "• Mag-pull para i-refresh ang inbox\n" +
            "• Ang mga ibinahagi ng kaibigan ay lalabas dito\n" +
            "• Tanggapin o i-dismiss ang mga salita\n" +
            "• \"Tingnan\" para sa materyal at pagsusulit\n" +
            "• Pulang tuldok (●) para sa bago/hindi pa nababasang item\n" +
            "• Kumpirmasyon bago i-dismiss ang ibinahaging salita",
    UiTextKey.ShareInboxInfoGotItButton to "Naintindihan ko",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Visibility ng profile",
    UiTextKey.MyProfileVisibilityPublic to "Pampubliko",
    UiTextKey.MyProfileVisibilityPrivate to "Pribado",
    UiTextKey.MyProfileVisibilityDescription to "Pampubliko: Mahahanap at maidaragdag ng sinuman\nPribado: Hindi lalabas sa paghahanap",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "I-dismiss ang salita",
    UiTextKey.ShareDismissWordMessage to "I-dismiss ang ibinahaging salita? Hindi na maibabalik",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Wika: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "I-dismiss",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Konektado o naghihintay na",
    UiTextKey.AccessibilityNewMessages to "Bagong mga mensahe",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicator ng bagong item",
    UiTextKey.AccessibilitySuccessIcon to "Tagumpay",
    UiTextKey.AccessibilityErrorIcon to "Error",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Uri ng ibinahaging item",
    UiTextKey.AccessibilityAddCustomWords to "Magdagdag ng custom na salita",
    UiTextKey.AccessibilityWordBankExists to "May bangko na ng salita",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "PH.Feedback",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Setting ng notipikasyon",
    UiTextKey.FriendsNotifSettingsTitle to "Setting ng notipikasyon",
    UiTextKey.FriendsNotifNewMessages to "Bagong mensahe sa chat",
    UiTextKey.FriendsNotifFriendRequests to "Mga natanggap na kahilingan",
    UiTextKey.FriendsNotifRequestAccepted to "Mga tinanggap na kahilingan",
    UiTextKey.FriendsNotifSharedInbox to "Bagong ibinahaging item",
    UiTextKey.FriendsNotifCloseButton to "Tapos na",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "In-app badge (pulang tuldok)",
    UiTextKey.InAppBadgeMessages to "Badge ng hindi pa nababasang mensahe",
    UiTextKey.InAppBadgeFriendRequests to "Badge ng kahilingan ng kaibigan",
    UiTextKey.InAppBadgeSharedInbox to "Badge ng inbox na hindi pa nababasa",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Mag-log in para magpatuloy",
    UiTextKey.ErrorSaveFailedRetry to "Hindi na-save. Subukan ulit",
    UiTextKey.ErrorLoadFailedRetry to "Hindi na-load. Subukan ulit",
    UiTextKey.ErrorNetworkRetry to "Error sa network. Suriin ang koneksyon",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Kailangan pa ng {needed} na salin para lumikha ng materyal",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Lumipat sa tuloy-tuloy na usapan →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "I-clear ang chat",
    UiTextKey.ChatClearConversationTitle to "I-clear ang chat",
    UiTextKey.ChatClearConversationMessage to "Itago ang lahat ng mensahe? Mananatiling nakatago kapag binuksan ulit. Hindi apektado ang iba",
    UiTextKey.ChatClearConversationConfirm to "I-clear lahat",
    UiTextKey.ChatClearConversationSuccess to "Na-clear na ang chat",

    // Block user
    UiTextKey.BlockUserButton to "I-block",
    UiTextKey.BlockUserTitle to "I-block ang gumagamit na ito?",
    UiTextKey.BlockUserMessage to "I-block si {username}? Maaalis sa listahan at hindi na maaaring makipag-ugnayan",
    UiTextKey.BlockUserConfirm to "I-block",
    UiTextKey.BlockUserSuccess to "Na-block at naalis na sa listahan",
    UiTextKey.BlockedUsersTitle to "Mga na-block na gumagamit",
    UiTextKey.BlockedUsersEmpty to "Walang na-block na gumagamit",
    UiTextKey.UnblockUserButton to "I-unblock",
    UiTextKey.UnblockUserTitle to "I-unblock?",
    UiTextKey.UnblockUserMessage to "I-unblock si {username}? Maaari na silang magpadala ng kahilingan ulit",
    UiTextKey.UnblockUserSuccess to "Na-unblock na",
    UiTextKey.BlockedUsersManageButton to "Pamahalaan ang mga na-block",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Tala ng kahilingan (opsyonal)",
    UiTextKey.FriendsRequestNotePlaceholder to "Magdagdag ng maikling tala...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Handa na ang materyal! Pindutin para buksan",
    UiTextKey.GenerationBannerWordBank to "Handa na ang bangko ng salita! Pindutin para tingnan",
    UiTextKey.GenerationBannerQuiz to "Handa na ang pagsusulit! Pindutin para simulan",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Mga notipikasyon",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Tsino (Traditional)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Sistema ng kaibigan",
    UiTextKey.HelpFriendSystemBody to "• Maghanap ng kaibigan gamit ang pangalan o ID\n" +
            "• Magpadala, tumanggap, o tumanggi ng kahilingan\n" +
            "• Direktang chat na may pagsasalin\n" +
            "• Magbahagi ng salita at materyal sa pag-aaral\n" +
            "• Pamahalaan ang ibinahaging nilalaman sa inbox\n" +
            "• Pulang tuldok (●) para sa bagong/hindi pa nababasang nilalaman\n" +
            "• Mag-pull down para i-refresh",
    UiTextKey.HelpProfileVisibilityTitle to "Visibility ng profile",
    UiTextKey.HelpProfileVisibilityBody to "• Itakda ang profile bilang pampubliko o pribado sa Settings\n" +
            "• Pampubliko: Mahahanap ng sinuman\n" +
            "• Pribado: Hindi lalabas sa paghahanap\n" +
            "• Gamitin ang pribado: ibahagi ang ID para madagdag",
    UiTextKey.HelpColorPalettesTitle to "Mga tema at barya",
    UiTextKey.HelpColorPalettesBody to "• 1 libreng tema: Sky Blue (default)\n" +
            "• 10 tema na naa-unlock sa 10 barya bawat isa\n" +
            "• Kumita ng barya mula sa pagsusulit\n" +
            "• Gamitin ang barya para sa tema at pagpapalawak ng kasaysayan\n" +
            "• Awtomatikong tema: maliwanag 6-18, madilim 18-6",
    UiTextKey.HelpPrivacyTitle to "Privacy at data",
    UiTextKey.HelpPrivacyBody to "• Ginagamit lang ang boses para sa pagkilala, hindi permanenteng naka-store\n" +
            "• Naka-process ang OCR sa device (ligtas)\n" +
            "• Maaaring burahin ang account at data kahit kailan\n" +
            "• Pribadong mode: hindi lalabas sa paghahanap\n" +
            "• Lahat ng data ay ligtas na naka-sync sa Firebase",
    UiTextKey.HelpAppVersionTitle to "Bersyon ng app",
    UiTextKey.HelpAppVersionNotes to "• Limitasyon ng kasaysayan: 30 hanggang 60 talaan (palawakin gamit ang barya)\n" +
            "• Natatanging username — palitan ang pangalan para mapalaya ang luma\n" +
            "• Awtomatikong log-out kapag may security update\n" +
            "• Lahat ng salin ay sa pamamagitan ng Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Agarang Pagsasalin",
    UiTextKey.OnboardingPage1Desc to "Mabilisang salin para sa maikling pangungusap. Tuloy-tuloy na usapan para sa dalawang-daang pag-uusap",
    UiTextKey.OnboardingPage2Title to "Mag-aral ng bokabularyo",
    UiTextKey.OnboardingPage2Desc to "Lumikha ng mga listahan ng salita at pagsusulit mula sa kasaysayan",
    UiTextKey.OnboardingPage3Title to "Makipag-ugnayan sa kaibigan",
    UiTextKey.OnboardingPage3Desc to "Mag-chat, magbahagi ng salita, at mag-aral nang magkasama",
    UiTextKey.OnboardingSkipButton to "Laktawan",
    UiTextKey.OnboardingNextButton to "Susunod",
    UiTextKey.OnboardingGetStartedButton to "Simulan na",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Maligayang pagbabalik, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "User:",
    UiTextKey.ChatUserIdLabel to "User ID:",
    UiTextKey.ChatLearningLabel to "Nag-aaral ng:",
    UiTextKey.ChatBlockedMessage to "Hindi makapagpadala ng mensahe sa gumagamit na ito",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Maghanap",
    UiTextKey.CustomWordsEmptyState to "Wala pang custom na salita",
    UiTextKey.CustomWordsEmptyHint to "Pindutin ang + para magdagdag ng salita",
    UiTextKey.CustomWordsNoSearchResults to "Walang tumutugmang salita",
    UiTextKey.AddCustomWordHintTemplate to "Ilagay ang salita sa {from} at salin sa {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} na talaan",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Email: {email}",
    UiTextKey.ProfileUsernameHintFull to "Username para sa mga kaibigan (3–20 karakter, letters/numbers/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Walang pagpipilian ng boses para sa wikang ito",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "Na-update ang app. Mag-log in ulit",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Naabot na ang limitasyon ng paborito",
    UiTextKey.FavoritesLimitMessage to "Hanggang 20 paborito lang. Burahin ang iba para makadagdag",
    UiTextKey.FavoritesLimitGotIt to "Naintindihan ko",
    UiTextKey.FavoritesInfoTitle to "Impormasyon ng paborito",
    UiTextKey.FavoritesInfoMessage to "Hanggang 20 paborito (talaan at session). Limitado para sa database load. Burahin para makadagdag ng bago",
    UiTextKey.FavoritesInfoGotIt to "Naintindihan ko",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Hindi maaaring baguhin",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "Maaaring baguhin ang pangunahing wika tuwing 30 araw. Natitira pang {days} araw",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "Maaaring baguhin ang pangunahing wika tuwing 30 araw. Natitira pang {hours} oras",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Kumpirmahin ang pagpapalit ng wika",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Ang pagpapalit ng pangunahing wika ay hindi na mababago sa loob ng 30 araw. Magpatuloy?",

    // Bottom navigation
    UiTextKey.NavHome to "Home",
    UiTextKey.NavTranslate to "Isalin",
    UiTextKey.NavLearn to "Mag-aral",
    UiTextKey.NavFriends to "Mga Kaibigan",
    UiTextKey.NavSettings to "Settings",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Kailangan ng pahintulot sa camera",
    UiTextKey.CameraPermissionMessage to "Payagan ang camera para sa pagkilala ng teksto",
    UiTextKey.CameraPermissionGrant to "Payagan",
    UiTextKey.MicPermissionMessage to "Kailangan ng pahintulot sa mikropono para sa pagkilala ng boses",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Burahin ang {count} napiling item? Hindi na maibabalik",
    UiTextKey.WordBankDeleteConfirm to "Burahin ang \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Tanggapin lahat",
    UiTextKey.FriendsRejectAllButton to "Tanggihan lahat",
    UiTextKey.ChatBlockedCannotSend to "Hindi makapagpadala ng mensahe",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "I-unlock ang {name}?",
    UiTextKey.ShopUnlockCost to "Halaga: {cost} barya",
    UiTextKey.ShopYourCoins to "Mga barya ko: {coins}",
    UiTextKey.ShopUnlockButton to "I-unlock",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Pangunahing wika",
    UiTextKey.HelpPrimaryLanguageBody to "• Ginagamit ang pangunahing wika para sa paliwanag sa pag-aaral\n" +
            "• Maaaring baguhin tuwing 30 araw para sa pagkakapare-pareho\n" +
            "• Baguhin sa Settings\n" +
            "• Naka-apply ang setting sa lahat ng pahina",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Tip: Para sa mas magandang pagkilala, itakda ang \"Source Language\" sa wika ng teksto na isi-scan",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Hindi maaaring baguhin",
    UiTextKey.SettingsUsernameCooldownMessage to "Maaaring baguhin ang username tuwing 30 araw. Natitira pang {days} araw",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Maaaring baguhin ang username tuwing 30 araw. Natitira pang {hours} oras",
    UiTextKey.SettingsUsernameConfirmTitle to "Kumpirmahin ang pagpapalit ng username",
    UiTextKey.SettingsUsernameConfirmMessage to "Ang pagpapalit ng username ay hindi na mababago sa loob ng 30 araw. Magpatuloy?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Walang koneksyon sa internet. Suriin ang koneksyon",
    UiTextKey.ErrorPermissionDenied to "Walang pahintulot para sa aksyon na ito",
    UiTextKey.ErrorSessionExpired to "Nag-expire na ang session. Mag-log in ulit",
    UiTextKey.ErrorItemNotFound to "Hindi nahanap ang item. Maaaring nabura na",
    UiTextKey.ErrorAccessDenied to "Tinanggihan ang access",
    UiTextKey.ErrorAlreadyFriends to "Magkaibigan na",
    UiTextKey.ErrorUserBlocked to "Hindi pinapayagan. Maaaring na-block ang gumagamit",
    UiTextKey.ErrorRequestNotFound to "Hindi na umiiral ang kahilingan na ito",
    UiTextKey.ErrorRequestAlreadyHandled to "Nahawakan na ang kahilingan na ito",
    UiTextKey.ErrorNotAuthorized to "Walang pahintulot para gawin ito",
    UiTextKey.ErrorRateLimited to "Masyadong maraming kahilingan. Subukan ulit mamaya",
    UiTextKey.ErrorInvalidInput to "Hindi tamang input. Suriin at subukan ulit",
    UiTextKey.ErrorOperationNotAllowed to "Hindi pinapayagan ang operasyong ito sa ngayon",
    UiTextKey.ErrorTimeout to "Nag-timeout. Subukan ulit",
    UiTextKey.ErrorSendMessageFailed to "Hindi naipadala ang mensahe. Subukan ulit",
    UiTextKey.ErrorFriendRequestSent to "Naipadala na ang kahilingan!",
    UiTextKey.ErrorFriendRequestFailed to "Hindi naipadala ang kahilingan",
    UiTextKey.ErrorFriendRemoved to "Naalis na ang kaibigan",
    UiTextKey.ErrorFriendRemoveFailed to "Hindi naalis. Suriin ang koneksyon",
    UiTextKey.ErrorBlockSuccess to "Na-block na ang gumagamit",
    UiTextKey.ErrorBlockFailed to "Hindi na-block. Subukan ulit",
    UiTextKey.ErrorUnblockSuccess to "Na-unblock na",
    UiTextKey.ErrorUnblockFailed to "Hindi na-unblock. Subukan ulit",
    UiTextKey.ErrorAcceptRequestSuccess to "Tinanggap na ang kahilingan!",
    UiTextKey.ErrorAcceptRequestFailed to "Hindi natanggap. Subukan ulit",
    UiTextKey.ErrorRejectRequestSuccess to "Tinanggihan na ang kahilingan",
    UiTextKey.ErrorRejectRequestFailed to "Hindi natanggihan. Subukan ulit",
    UiTextKey.ErrorOfflineMessage to "Offline ka. Maaaring hindi available ang ilang feature",
    UiTextKey.ErrorChatDeletionFailed to "Hindi na-clear ang chat. Subukan ulit",
    UiTextKey.ErrorGenericRetry to "May error na nangyari. Subukan ulit",
)
