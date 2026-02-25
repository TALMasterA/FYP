package com.example.fyp.model.ui

/**
 * Cantonese (Hong Kong) UI text map — 廣東話介面文字。
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val YueUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "收音",
    UiTextKey.CopyButton to "複製",
    UiTextKey.SpeakScriptButton to "朗讀原文",
    UiTextKey.TranslateButton to "翻譯",
    UiTextKey.CopyTranslationButton to "複製翻譯",
    UiTextKey.SpeakTranslationButton to "朗讀翻譯",
    UiTextKey.RecognizingStatus to "正在辨認語音...",
    UiTextKey.TranslatingStatus to "翻譯緊...",
    UiTextKey.SpeakingOriginalStatus to "正在朗讀原文...",
    UiTextKey.SpeakingTranslationStatus to "正在朗讀翻譯...",
    UiTextKey.SpeakingLabel to "朗讀中",
    UiTextKey.FinishedSpeakingOriginal to "原文朗讀完畢",
    UiTextKey.FinishedSpeakingTranslation to "翻譯朗讀完畢",
    UiTextKey.TtsErrorTemplate to "語音錯誤：{error}",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "介面語言",
    UiTextKey.DetectLanguageLabel to "偵測語言",
    UiTextKey.TranslateToLabel to "翻譯去",

    // Language names
    UiTextKey.LangEnUs to "英文",
    UiTextKey.LangZhHk to "廣東話",
    UiTextKey.LangJaJp to "日文",
    UiTextKey.LangZhCn to "簡體中文",
    UiTextKey.LangFrFr to "法文",
    UiTextKey.LangDeDe to "德文",
    UiTextKey.LangKoKr to "韓文",
    UiTextKey.LangEsEs to "西班牙文",
    UiTextKey.LangIdId to "印尼文",
    UiTextKey.LangViVn to "越南文",
    UiTextKey.LangThTh to "泰文",
    UiTextKey.LangFilPh to "菲律賓文",
    UiTextKey.LangMsMy to "馬來文",
    UiTextKey.LangPtBr to "葡萄牙文",
    UiTextKey.LangItIt to "意大利文",
    UiTextKey.LangRuRu to "俄文",

    // Navigation
    UiTextKey.NavHistory to "歷史紀錄",
    UiTextKey.NavLogin to "登入",
    UiTextKey.NavLogout to "登出",
    UiTextKey.NavBack to "返回",
    UiTextKey.ActionCancel to "取消",
    UiTextKey.ActionDelete to "刪除",
    UiTextKey.ActionOpen to "開啟",
    UiTextKey.ActionName to "命名",
    UiTextKey.ActionSave to "儲存",
    UiTextKey.ActionConfirm to "確認",

    // Guest limit
    UiTextKey.GuestTranslationLimitTitle to "需要登入",
    UiTextKey.GuestTranslationLimitMessage to "你已經換過一次介面語言喇。請登入先可以再換。登入用戶可以無限次換介面語言，仲有本地快取功能。",

    // Speech
    UiTextKey.SpeechInputPlaceholder to "語音辨認結果會喺度顯示...",
    UiTextKey.SpeechTranslatedPlaceholder to "翻譯結果會喺度顯示...",
    UiTextKey.StatusAzureErrorTemplate to "Azure 錯誤：{error}",
    UiTextKey.StatusTranslationErrorTemplate to "翻譯錯誤：{error}",
    UiTextKey.StatusLoginRequiredTranslation to "請登入先可以用翻譯功能",
    UiTextKey.StatusRecognizePreparing to "準備緊麥克風...",
    UiTextKey.StatusRecognizeListening to "聽緊你講嘢...",

    // Pagination
    UiTextKey.PaginationPrevLabel to "上一頁",
    UiTextKey.PaginationNextLabel to "下一頁",
    UiTextKey.PaginationPageLabelTemplate to "第 {current} / {total} 頁",

    // Toast
    UiTextKey.ToastCopied to "已複製",
    UiTextKey.DisableText to "停用",

    // Error
    UiTextKey.ErrorRetryButton to "重試",
    UiTextKey.ErrorGenericMessage to "出咗問題，請再試過。",

    // Shop
    UiTextKey.ShopTitle to "商店",
    UiTextKey.ShopCoinBalance to "你嘅金幣：{balance}",
    UiTextKey.ShopHistoryExpansionTitle to "擴充歷史紀錄上限",
    UiTextKey.ShopHistoryExpansionDesc to "將歷史紀錄查看上限加多 50 條，費用 10 個金幣",
    UiTextKey.ShopCurrentLimit to "目前上限：{limit} 條",
    UiTextKey.ShopMaxLimit to "已達最高上限 ({limit} 條)",
    UiTextKey.ShopBuyHistoryExpansion to "購買 (+50 條，10 個金幣)",
    UiTextKey.ShopInsufficientCoins to "金幣唔夠",
    UiTextKey.ShopMaxLimitReached to "已達最高上限",
    UiTextKey.ShopHistoryExpandedTitle to "擴充成功！",
    UiTextKey.ShopHistoryExpandedMessage to "歷史紀錄上限已增加！",
    UiTextKey.ShopColorPaletteTitle to "顏色主題",
    UiTextKey.ShopColorPaletteDesc to "揀你嘅應用程式顏色主題，每個新主題 10 個金幣",
    UiTextKey.ShopEntry to "商店",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "語音設定",
    UiTextKey.VoiceSettingsDesc to "為每種語言揀文字轉語音嘅聲線。",

    // Instructions
    UiTextKey.SpeechInstructions to "撳麥克風掣開始語音辨認，完成後撳翻譯。",
    UiTextKey.HomeInstructions to "揀功能開始使用。",
    UiTextKey.ContinuousInstructions to "揀兩種語言，然後開始對話模式。",

    // Home
    UiTextKey.HomeTitle to "即時翻譯",
    UiTextKey.HelpTitle to "幫助",
    UiTextKey.SpeechTitle to "快速翻譯",
    UiTextKey.HomeStartButton to "開始翻譯",
    UiTextKey.HomeFeaturesTitle to "功能介紹",
    UiTextKey.HomeDiscreteDescription to "快速翻譯語音並查看歷史紀錄",
    UiTextKey.HomeContinuousDescription to "即時雙向對話翻譯",
    UiTextKey.HomeLearningDescription to "從翻譯紀錄生成學習材料同測驗",

    // Help
    UiTextKey.HelpCurrentTitle to "目前功能",
    UiTextKey.HelpCautionTitle to "注意事項",
    UiTextKey.HelpCurrentFeatures to "目前功能：\n" +
            "  • 快速翻譯：語音辨認後翻譯\n" +
            "  • 即時對話：雙向語音翻譯\n" +
            "  • 歷史紀錄：睇翻譯紀錄\n" +
            "  • 學習材料：從紀錄生成詞彙同測驗\n\n" +
            "翻譯：\n" +
            "  • 使用 Azure AI 語音辨認\n" +
            "  • 使用 Azure 翻譯服務\n",
    UiTextKey.HelpCaution to "注意事項：\n" +
            "  • 語音辨認需要網絡連線\n" +
            "  • 本地翻譯快取喺離線時都可以用\n" +
            "  • 重要翻譯請用專業服務核實\n\n" +
            "帳號同資料：\n" +
            "  • 歷史紀錄、學習同金幣功能需要登入\n" +
            "  • 用戶資料安全儲存喺 Firebase Firestore\n",
    UiTextKey.HelpNotesTitle to "備註",
    UiTextKey.HelpNotes to "💡 使用技巧同排解問題：\n\n" +
            "攞到最佳翻譯效果：\n" +
            "  • 清晰同速度適中噉講嘢\n" +
            "  • 減少背景噪音以提升辨認準確度\n" +
            "  • 快速翻譯模式以短句效果最好\n\n" +
            "介面語言：\n" +
            "  • 基礎語言係英語；其他介面語言係 AI 翻譯\n" +
            "  • 廣東話版本係手動翻譯，準確度更高\n" +
            "更新同回饋：\n" +
            "  • 應用程式版本顯示喺設定 → 關於\n" +
            "  • 請透過 GitHub 回報錯誤或建議功能\n",

    // Feedback
    UiTextKey.FeedbackTitle to "回饋",
    UiTextKey.FeedbackDesc to "多謝你嘅回饋！請分享你嘅建議、錯誤回報或對應用程式嘅評論。",
    UiTextKey.FeedbackMessagePlaceholder to "請喺度輸入你嘅回饋...",
    UiTextKey.FeedbackSubmitButton to "提交回饋",
    UiTextKey.FeedbackSubmitting to "提交緊...",
    UiTextKey.FeedbackSuccessTitle to "多謝你！",
    UiTextKey.FeedbackSuccessMessage to "你嘅回饋已成功提交，多謝你嘅寶貴意見！",
    UiTextKey.FeedbackErrorTitle to "提交失敗",
    UiTextKey.FeedbackErrorMessage to "提交回饋失敗，請檢查你嘅網絡連線再試過。",
    UiTextKey.FeedbackMessageRequired to "請輸入你嘅回饋內容。",

    // Continuous mode
    UiTextKey.ContinuousTitle to "即時對話",
    UiTextKey.ContinuousStartButton to "開始對話",
    UiTextKey.ContinuousStopButton to "停止收音",
    UiTextKey.ContinuousStartScreenButton to "即時對話",
    UiTextKey.ContinuousPersonALabel to "A 講緊嘢",
    UiTextKey.ContinuousPersonBLabel to "B 講緊嘢",
    UiTextKey.ContinuousCurrentStringLabel to "目前文字：",
    UiTextKey.ContinuousSpeakerAName to "人物 A",
    UiTextKey.ContinuousSpeakerBName to "人物 B",
    UiTextKey.ContinuousTranslationSuffix to "・翻譯",
    UiTextKey.ContinuousPreparingMicText to "準備緊麥克風...（唔好講嘢）",
    UiTextKey.ContinuousTranslatingText to "翻譯緊...",

    // History
    UiTextKey.HistoryTitle to "歷史紀錄",
    UiTextKey.HistoryTabDiscrete to "快速翻譯",
    UiTextKey.HistoryTabContinuous to "即時對話",
    UiTextKey.HistoryNoContinuousSessions to "未有對話紀錄。",
    UiTextKey.HistoryNoDiscreteRecords to "未有翻譯紀錄。",
    UiTextKey.DialogDeleteRecordTitle to "刪除紀錄？",
    UiTextKey.DialogDeleteRecordMessage to "呢個操作無法還原。",
    UiTextKey.DialogDeleteSessionTitle to "刪除對話？",
    UiTextKey.DialogDeleteSessionMessage to "呢個對話入面所有紀錄都會刪除，操作無法還原。",
    UiTextKey.HistoryDeleteSessionButton to "刪除",
    UiTextKey.HistoryNameSessionTitle to "命名",
    UiTextKey.HistorySessionNameLabel to "對話名稱",
    UiTextKey.HistorySessionTitleTemplate to "對話 {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} 條紀錄",

    // Filter
    UiTextKey.FilterDropdownDefault to "所有語言",
    UiTextKey.FilterTitle to "篩選歷史紀錄",
    UiTextKey.FilterLangDrop to "語言",
    UiTextKey.FilterKeyword to "關鍵字",
    UiTextKey.FilterApply to "套用",
    UiTextKey.FilterCancel to "取消",
    UiTextKey.FilterClear to "清除",
    UiTextKey.FilterHistoryScreenTitle to "篩選",

    // Auth
    UiTextKey.AuthLoginTitle to "登入",
    UiTextKey.AuthRegisterTitle to "註冊（停用中）",
    UiTextKey.AuthLoginHint to "請用你已登記嘅電郵同密碼。",
    UiTextKey.AuthRegisterRules to "開發期間停用註冊功能。\n注意：如果用唔存在嘅電郵，就唔可以重設密碼。\n" +
            "註冊規則：\n" +
            "• 電郵格式需正確（例如 name@example.com）\n" +
            "• 密碼最少 6 個字元\n" +
            "• 確認密碼需同密碼一樣",
    UiTextKey.AuthEmailLabel to "電郵",
    UiTextKey.AuthPasswordLabel to "密碼",
    UiTextKey.AuthConfirmPasswordLabel to "確認密碼",
    UiTextKey.AuthLoginButton to "登入",
    UiTextKey.AuthRegisterButton to "註冊",
    UiTextKey.AuthToggleToRegister to "未有帳號？註冊（停用中）",
    UiTextKey.AuthToggleToLogin to "已有帳號？登入",
    UiTextKey.AuthErrorPasswordsMismatch to "密碼唔一樣。",
    UiTextKey.AuthErrorPasswordTooShort to "密碼最少需要 6 個字元。",
    UiTextKey.AuthRegistrationDisabled to "開發期間停用註冊功能。",
    UiTextKey.AuthResetEmailSent to "重設電郵已發出（如果電郵真實存在）。請查收你嘅收件箱。",

    // Password reset
    UiTextKey.ForgotPwText to "唔記得密碼？喺度重設",
    UiTextKey.ResetPwTitle to "重設密碼",
    UiTextKey.ResetPwText to "輸入你帳號嘅電郵，會發送重設連結。\n請確認該電郵已喺應用程式登記，否則唔會發郵件。\n",
    UiTextKey.ResetSendingText to "發送緊...",
    UiTextKey.ResetSendText to "發送重設郵件",

    // Settings
    UiTextKey.SettingsTitle to "設定",
    UiTextKey.SettingsPrimaryLanguageTitle to "主要語言",
    UiTextKey.SettingsPrimaryLanguageDesc to "用於學習說明同建議",
    UiTextKey.SettingsPrimaryLanguageLabel to "主要語言",
    UiTextKey.SettingsFontSizeTitle to "字體大小",
    UiTextKey.SettingsFontSizeDesc to "調整文字大小以提升閱讀體驗（跨裝置同步）",
    UiTextKey.SettingsScaleTemplate to "縮放：{pct}%",
    UiTextKey.SettingsColorPaletteTitle to "顏色主題",
    UiTextKey.SettingsColorPaletteDesc to "揀應用程式顏色主題，每個新主題 10 個金幣",
    UiTextKey.SettingsColorCostTemplate to "{cost} 個金幣",
    UiTextKey.SettingsColorUnlockButton to "解鎖",
    UiTextKey.SettingsColorSelectButton to "揀",
    UiTextKey.SettingsColorAlreadyUnlocked to "已解鎖",
    UiTextKey.SettingsPreviewHeadline to "標題：大文字預覽",
    UiTextKey.SettingsPreviewBody to "內文：一般文字預覽",
    UiTextKey.SettingsPreviewLabel to "標籤：細文字預覽",
    UiTextKey.SettingsAboutTitle to "關於",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "你嘅偏好設定已自動儲存同同步到你嘅帳號。",
    UiTextKey.SettingsThemeTitle to "主題",
    UiTextKey.SettingsThemeDesc to "跟隨系統 / 淺色 / 深色（未登入時鎖定）",
    UiTextKey.SettingsThemeSystem to "跟隨系統",
    UiTextKey.SettingsThemeLight to "淺色",
    UiTextKey.SettingsThemeDark to "深色",
    UiTextKey.SettingsThemeScheduled to "排程",
    UiTextKey.SettingsResetPW to "喺度重設密碼",
    UiTextKey.SettingsQuickLinks to "詳細設定",
    UiTextKey.SettingsNotLoggedInWarning to "未登入，以下設定更改唔會儲存。",
    UiTextKey.SettingsVoiceTitle to "語音設定",
    UiTextKey.SettingsVoiceDesc to "為每種語言揀文字轉語音嘅聲線。",
    UiTextKey.SettingsVoiceLanguageLabel to "語言",
    UiTextKey.SettingsVoiceSelectLabel to "聲線",
    UiTextKey.SettingsVoiceDefault to "預設",

    // Learning
    UiTextKey.LearningTitle to "學習",
    UiTextKey.LearningHintCount to "(*) 次數 = 包含此語言嘅歷史翻譯紀錄數目。",
    UiTextKey.LearningErrorTemplate to "錯誤：%s",
    UiTextKey.LearningGenerate to "生成",
    UiTextKey.LearningRegenerate to "重新生成",
    UiTextKey.LearningGenerating to "生成緊...",
    UiTextKey.LearningOpenSheetTemplate to "{speclanguage} 學習表",
    UiTextKey.LearningSheetTitleTemplate to "{speclanguage} 學習表",
    UiTextKey.LearningSheetPrimaryTemplate to "主要語言：{speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "目前紀錄數：{nowCount}（生成時：{savedCount}）",
    UiTextKey.LearningSheetNoContent to "未有學習表內容。",
    UiTextKey.LearningSheetRegenerate to "重新生成",
    UiTextKey.LearningSheetGenerating to "生成緊...",
    UiTextKey.LearningRegenBlockedTitle to "目前無法重新生成",
    UiTextKey.LearningRegenBlockedMessage to "重新生成需要比上次生成多最少 5 條紀錄，你仲需要 {needed} 條。",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ 仲需 {needed} 條紀錄先可以重新生成（最少 5 條）",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ 紀錄數需高過上次生成時嘅數目",
    UiTextKey.LearningRegenInfoTitle to "重新生成規則",
    UiTextKey.LearningRegenInfoMessage to "要重新生成學習材料：\n\n• 首次生成：隨時都可以生成\n• 重新生成：需比上次多最少 5 條翻譯紀錄\n\n當你有足夠新紀錄，掣就會啟用（藍色）。若掣係停用（灰色），請繼續翻譯以解鎖重新生成！\n\n💡 提示：若翻譯後數目未更新，請重啟應用程式以重新整理。",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ 呢個版本材料已生成測驗，請生成新學習表以建立新測驗。",

    // Quiz
    UiTextKey.QuizTitleTemplate to "測驗：{language}",
    UiTextKey.QuizOpenButton to "📝 測驗",
    UiTextKey.QuizGenerateButton to "🔄 生成測驗",
    UiTextKey.QuizGenerating to "⏳ 生成緊...",
    UiTextKey.QuizUpToDate to "✓ 最新版本",
    UiTextKey.QuizBlocked to "🚫 已封鎖",
    UiTextKey.QuizWait to "⏳ 等緊...",
    UiTextKey.QuizMaterialsQuizTemplate to "材料：{materials} | 測驗：{quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 可以攞金幣！",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 仲需 {count} 條紀錄先可以攞金幣",
    UiTextKey.QuizCancelButton to "取消",
    UiTextKey.QuizPreviousButton to "上一題",
    UiTextKey.QuizNextButton to "下一題",
    UiTextKey.QuizSubmitButton to "提交",
    UiTextKey.QuizRetakeButton to "重新測驗",
    UiTextKey.QuizBackButton to "返回",
    UiTextKey.QuizLoadingText to "載入測驗中...",
    UiTextKey.QuizGeneratingText to "生成測驗緊...",
    UiTextKey.QuizNoMaterialsTitle to "搵唔到學習材料",
    UiTextKey.QuizNoMaterialsMessage to "請返回先生成學習材料，再睇測驗。",
    UiTextKey.QuizErrorTitle to "⚠️ 測驗錯誤",
    UiTextKey.QuizErrorSuggestion to "建議：用上面嘅掣生成測驗。",
    UiTextKey.QuizCompletedTitle to "測驗完成！",
    UiTextKey.QuizAnswerReviewTitle to "答案回顧",
    UiTextKey.QuizYourAnswerTemplate to "你嘅答案：{answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "正確答案：{answer}",
    UiTextKey.QuizQuestionTemplate to "第 {current} 題，共 {total} 題",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ 無法重新生成：材料（{materials}）< 測驗（{quiz}），請加多啲翻譯。",
    UiTextKey.QuizAnotherGenInProgress to "⏳ 另一個生成任務進行緊，請等一等。",
    UiTextKey.QuizCoinRulesTitle to "🪙 金幣攞取規則",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ 點樣攞：",
    UiTextKey.QuizCoinRulesRequirements to "條件：",
    UiTextKey.QuizCoinRulesCurrentStatus to "目前狀態：",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ 下次測驗可以攞金幣！",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• 仲需 {count} 條紀錄先可以攞金幣",
    UiTextKey.QuizCoinRule1Coin to "• 每題答啱攞 1 個金幣",
    UiTextKey.QuizCoinRuleFirstAttempt to "• 每個測驗版本只係第一次作答有效",
    UiTextKey.QuizCoinRuleMatchMaterials to "• 測驗需同材料版本相符",
    UiTextKey.QuizCoinRulePlus10 to "• 需比上次攞金幣嘅測驗多 10 條以上紀錄",
    UiTextKey.QuizCoinRuleNoDelete to "• 刪除歷史紀錄唔可以重新攞金幣",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• 材料：{count} 條紀錄",
    UiTextKey.QuizCoinRuleQuizTemplate to "• 測驗：{count} 條紀錄",
    UiTextKey.QuizCoinRuleGotIt to "明白喇！",
    UiTextKey.QuizRegenConfirmTitle to "🔄 生成新測驗？",
    UiTextKey.QuizRegenCanEarnCoins to "✅ 呢個測驗可以攞金幣！（只限第一次作答）",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ 呢個測驗目前無法攞金幣。",
    UiTextKey.QuizRegenNeedMoreTemplate to "仲需多 {count} 條翻譯紀錄先符合金幣資格（比上次攞金幣嘅測驗多最少 10 條）。",
    UiTextKey.QuizRegenReminder to "提示：你仍然可以練習同重做測驗，但金幣只係第一次作答且有足夠新紀錄時先發放。",
    UiTextKey.QuizRegenGenerateButton to "生成",
    UiTextKey.QuizCoinsEarnedTitle to "✨ 攞到金幣！",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "恭喜！你攞到咗 {coins} 個金幣！",
    UiTextKey.QuizCoinsRule1 to "• 只係第一次作答每題答啱攞 1 個金幣",
    UiTextKey.QuizCoinsRule2 to "• 重做相同測驗唔攞金幣",
    UiTextKey.QuizCoinsRule3 to "• 新測驗需比上次多 10 條以上紀錄",
    UiTextKey.QuizCoinsRule4 to "• 測驗需同目前材料版本相符",
    UiTextKey.QuizCoinsRule5 to "• 喺歷史紀錄頁面睇總金幣數",
    UiTextKey.QuizCoinsGreatButton to "正喎！",
    UiTextKey.QuizOutdatedMessage to "呢個測驗係基於舊版學習表。",
    UiTextKey.QuizRecordsLabel to "條紀錄",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 你嘅金幣",
    UiTextKey.HistoryCoinRulesTitle to "金幣攞取規則：",
    UiTextKey.HistoryCoinHowToEarnTitle to "點樣攞：",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 每題答啱攞 1 個金幣",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• 只係每個測驗版本嘅第一次作答有效",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• 測驗需同目前學習材料相符",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 防作弊規則：",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• 比上次攞金幣嘅測驗需多 10 條以上新翻譯先可以再攞",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• 測驗版本需同材料版本相同",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• 刪除歷史紀錄會封鎖測驗重新生成（除非數目高過上次紀錄）",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• 重做相同測驗唔攞金幣",
    UiTextKey.HistoryCoinTipsTitle to "💡 提示：",
    UiTextKey.HistoryCoinTipsRule1 to "• 定期加多啲翻譯",
    UiTextKey.HistoryCoinTipsRule2 to "• 第一次作答前好好學習！",
    UiTextKey.HistoryCoinGotItButton to "明白喇！",

    // History info
    UiTextKey.HistoryInfoTitle to "歷史紀錄說明",
    UiTextKey.HistoryInfoLimitMessage to "歷史紀錄顯示最近 {limit} 條，可以喺商店擴充上限！",
    UiTextKey.HistoryInfoOlderRecordsMessage to "舊啲嘅紀錄仍然儲存緊，但唔顯示以優化效能。",
    UiTextKey.HistoryInfoFavoritesMessage to "要永久儲存重要翻譯，請撳任何紀錄上嘅愛心 ❤️ 圖示加入最愛。",
    UiTextKey.HistoryInfoViewFavoritesMessage to "喺設定 → 最愛中睇已儲存嘅最愛紀錄。",
    UiTextKey.HistoryInfoFilterMessage to "用篩選掣喺顯示嘅 {limit} 條紀錄中搜尋。",
    UiTextKey.HistoryInfoGotItButton to "明白喇",

    // Word bank
    UiTextKey.WordBankTitle to "生字庫",
    UiTextKey.WordBankSelectLanguage to "揀語言以睇或生成生字庫：",
    UiTextKey.WordBankNoHistory to "無翻譯紀錄",
    UiTextKey.WordBankNoHistoryHint to "開始翻譯以建立你嘅生字庫！",
    UiTextKey.WordBankWordsCount to "個生字",
    UiTextKey.WordBankGenerating to "生成緊...",
    UiTextKey.WordBankGenerate to "生成生字庫",
    UiTextKey.WordBankRegenerate to "重新生成生字庫",
    UiTextKey.WordBankRefresh to "🔄 重新整理生字庫",
    UiTextKey.WordBankEmpty to "未有生字庫",
    UiTextKey.WordBankEmptyHint to "撳上面嘅掣，從翻譯歷史生成生字庫。",
    UiTextKey.WordBankExample to "例句：",
    UiTextKey.WordBankDifficulty to "難度：",
    UiTextKey.WordBankFilterCategory to "類別",
    UiTextKey.WordBankFilterCategoryAll to "所有類別",
    UiTextKey.WordBankFilterDifficultyLabel to "難度等級：",
    UiTextKey.WordBankFilterNoResults to "冇符合篩選條件嘅生字",
    UiTextKey.WordBankRefreshAvailable to "✅ 可以重新整理！",
    UiTextKey.WordBankRecordsNeeded to "條紀錄（需 20 條先可以重新整理）",
    UiTextKey.WordBankRegenInfoTitle to "重新整理規則",
    UiTextKey.WordBankRegenInfoMessage to "要重新整理你嘅生字庫：\n\n• 首次生成：隨時都可以生成\n• 重新整理：需比上次生成多最少 20 條翻譯紀錄\n\n當你有足夠新紀錄，重新整理掣就會啟用（藍色）。若掣係停用（灰色），請繼續翻譯以解鎖重新整理！\n\n💡 提示：若翻譯後數目未更新，請重啟應用程式以重新整理。",
    UiTextKey.WordBankHistoryCountTemplate to "目前紀錄數：{nowCount}（生成時：{savedCount}）",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "登出？",
    UiTextKey.DialogLogoutMessage to "你需要重新登入先可以用翻譯功能 / 儲存同查看歷史紀錄。",
    UiTextKey.DialogGenerateOverwriteTitle to "覆蓋材料？",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "之前嘅材料將被覆蓋（如存在）。\n為 {speclanguage} 生成材料？",

    // Profile
    UiTextKey.ProfileTitle to "個人資料",
    UiTextKey.ProfileUsernameLabel to "用戶名",
    UiTextKey.ProfileUsernameHint to "輸入你嘅用戶名",
    UiTextKey.ProfileDisplayNameLabel to "顯示名稱",
    UiTextKey.ProfileDisplayNameHint to "輸入你嘅顯示名稱",
    UiTextKey.ProfileUpdateButton to "更新個人資料",
    UiTextKey.ProfileUpdateSuccess to "個人資料更新成功",
    UiTextKey.ProfileUpdateError to "個人資料更新失敗",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "刪除帳號",
    UiTextKey.AccountDeleteWarning to "⚠️ 呢個操作永久無法還原！",
    UiTextKey.AccountDeleteConfirmMessage to "你所有嘅資料，包括歷史紀錄、生字庫、學習材料同設定，將會永久刪除。請輸入密碼確認。",
    UiTextKey.AccountDeletePasswordLabel to "密碼",
    UiTextKey.AccountDeleteButton to "刪除我嘅帳號",
    UiTextKey.AccountDeleteSuccess to "帳號刪除成功",
    UiTextKey.AccountDeleteError to "帳號刪除失敗",
    UiTextKey.AccountDeleteReauthRequired to "請重新輸入密碼以確認刪除",

    // Favorites
    UiTextKey.FavoritesTitle to "最愛",
    UiTextKey.FavoritesEmpty to "未有最愛紀錄",
    UiTextKey.FavoritesAddSuccess to "已加入最愛",
    UiTextKey.FavoritesRemoveSuccess to "已從最愛移除",
    UiTextKey.FavoritesAddButton to "加入最愛",
    UiTextKey.FavoritesRemoveButton to "從最愛移除",
    UiTextKey.FavoritesNoteLabel to "備註",
    UiTextKey.FavoritesNoteHint to "加備註（可選）",

    // Custom words
    UiTextKey.CustomWordsTitle to "自訂生字",
    UiTextKey.CustomWordsAdd to "加生字",
    UiTextKey.CustomWordsEdit to "編輯生字",
    UiTextKey.CustomWordsDelete to "刪除生字",
    UiTextKey.CustomWordsOriginalLabel to "原本生字",
    UiTextKey.CustomWordsTranslatedLabel to "翻譯",
    UiTextKey.CustomWordsPronunciationLabel to "發音（可選）",
    UiTextKey.CustomWordsExampleLabel to "例句（可選）",
    UiTextKey.CustomWordsSaveSuccess to "生字儲存成功",
    UiTextKey.CustomWordsDeleteSuccess to "生字刪除成功",
    UiTextKey.CustomWordsAlreadyExists to "呢個生字已存在",
    UiTextKey.CustomWordsOriginalLanguageLabel to "原本語言",
    UiTextKey.CustomWordsTranslationLanguageLabel to "翻譯語言",
    UiTextKey.CustomWordsSaveButton to "儲存",
    UiTextKey.CustomWordsCancelButton to "取消",

    // Language detection
    UiTextKey.LanguageDetectAuto to "自動偵測",
    UiTextKey.LanguageDetectDetecting to "偵測緊...",
    UiTextKey.LanguageDetectedTemplate to "偵測到：{language}",
    UiTextKey.LanguageDetectFailed to "偵測失敗",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "從圖片掃描文字",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ 注意：圖片文字辨認可能唔係完全準確，請檢查擷取嘅文字。" +
            "支援拉丁字元（英語等）、中文、日文同韓文。",
    UiTextKey.ImageRecognitionScanning to "正在掃描圖片文字...",
    UiTextKey.ImageRecognitionSuccess to "文字擷取成功",

    // Cache
    UiTextKey.CacheClearButton to "清除快取",
    UiTextKey.CacheClearSuccess to "快取已成功清除",
    UiTextKey.CacheStatsTemplate to "快取：已儲存 {count} 條翻譯",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "自動切換主題",
    UiTextKey.SettingsAutoThemeDesc to "根據時間自動喺淺色同深色主題之間切換",
    UiTextKey.SettingsAutoThemeEnabled to "已啟用",
    UiTextKey.SettingsAutoThemeDisabled to "已停用",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "深色模式開始時間：",
    UiTextKey.SettingsAutoThemeLightStartLabel to "淺色模式開始時間：",
    UiTextKey.SettingsAutoThemePreview to "主題會喺排程時間自動切換",

    // Offline mode
    UiTextKey.OfflineModeTitle to "離線模式",
    UiTextKey.OfflineModeMessage to "你目前係離線，正在瀏覽快取資料。",
    UiTextKey.OfflineModeRetry to "重試連線",
    UiTextKey.OfflineDataCached to "快取資料可用",
    UiTextKey.OfflineSyncPending to "連線後將同步更改",

    // Image capture
    UiTextKey.ImageSourceTitle to "揀圖片來源",
    UiTextKey.ImageSourceCamera to "影相",
    UiTextKey.ImageSourceGallery to "從相簿揀",
    UiTextKey.ImageSourceCancel to "取消",
    UiTextKey.CameraCaptureContentDesc to "拍攝",

    // Friends
    UiTextKey.FriendsTitle to "朋友",
    UiTextKey.FriendsMenuButton to "朋友",
    UiTextKey.FriendsAddButton to "加朋友",
    UiTextKey.FriendsSearchTitle to "搜尋用戶",
    UiTextKey.FriendsSearchPlaceholder to "輸入用戶名或用戶 ID...",
    UiTextKey.FriendsSearchMinChars to "請輸入最少 2 個字元以搜尋",
    UiTextKey.FriendsSearchNoResults to "搵唔到用戶",
    UiTextKey.FriendsListEmpty to "加朋友以聯繫同分享學習材料。",
    UiTextKey.FriendsRequestsSection to "朋友請求（{count}）",
    UiTextKey.FriendsSectionTitle to "朋友（{count}）",
    UiTextKey.FriendsAcceptButton to "接受",
    UiTextKey.FriendsRejectButton to "拒絕",
    UiTextKey.FriendsRemoveButton to "移除",
    UiTextKey.FriendsRemoveDialogTitle to "移除朋友",
    UiTextKey.FriendsRemoveDialogMessage to "確定要將 {username} 從朋友名單移除？",
    UiTextKey.FriendsSendRequestButton to "加",
    UiTextKey.FriendsRequestSentSuccess to "朋友請求已送出！",
    UiTextKey.FriendsRequestAcceptedSuccess to "朋友請求已接受！",
    UiTextKey.FriendsRequestRejectedSuccess to "請求已拒絕",
    UiTextKey.FriendsRemovedSuccess to "朋友已移除",
    UiTextKey.FriendsRequestFailed to "請求送出失敗",
    UiTextKey.FriendsCloseButton to "關閉",
    UiTextKey.FriendsCancelButton to "取消",
    UiTextKey.FriendsRemoveConfirm to "移除",
    UiTextKey.FriendsNewRequestsTemplate to "你有 {count} 個新朋友請求！",
    UiTextKey.FriendsSentRequestsSection to "已送出嘅請求（{count}）",
    UiTextKey.FriendsPendingStatus to "等緊處理",
    UiTextKey.FriendsCancelRequestButton to "取消請求",
    UiTextKey.FriendsUnreadMessageDesc to "發送訊息",
    UiTextKey.FriendsDeleteModeButton to "刪除朋友",
    UiTextKey.FriendsDeleteSelectedButton to "刪除所選",
    UiTextKey.FriendsDeleteMultipleTitle to "移除朋友",
    UiTextKey.FriendsDeleteMultipleMessage to "移除所選 {count} 位朋友？",
    UiTextKey.FriendsSearchMinChars3 to "請輸入最少 3 個字元以按用戶名搜尋",
    UiTextKey.FriendsSearchByUserIdHint to "或輸入完整用戶 ID 進行精確查詢",

    // Chat
    UiTextKey.ChatTitle to "同 {username} 傾偈",
    UiTextKey.ChatInputPlaceholder to "輸入訊息...",
    UiTextKey.ChatSendButton to "發送",
    UiTextKey.ChatEmpty to "未有訊息，開始傾偈啦！",
    UiTextKey.ChatMessageSent to "訊息已發送",
    UiTextKey.ChatMessageFailed to "訊息發送失敗",
    UiTextKey.ChatMarkingRead to "標記為已讀...",
    UiTextKey.ChatLoadingMessages to "載入訊息緊...",
    UiTextKey.ChatToday to "今日",
    UiTextKey.ChatYesterday to "琴日",
    UiTextKey.ChatUnreadBadge to "{count} 條未讀",
    UiTextKey.ChatTranslateButton to "翻譯",
    UiTextKey.ChatTranslateDialogTitle to "翻譯對話",
    UiTextKey.ChatTranslateDialogMessage to "將朋友嘅訊息翻譯成你嘅偏好語言？系統會偵測每條訊息嘅語言並進行翻譯。",
    UiTextKey.ChatTranslateConfirm to "全部翻譯",
    UiTextKey.ChatTranslating to "翻譯訊息緊...",
    UiTextKey.ChatTranslated to "訊息已翻譯",
    UiTextKey.ChatShowOriginal to "顯示原文",
    UiTextKey.ChatShowTranslation to "顯示翻譯",
    UiTextKey.ChatTranslateFailed to "翻譯失敗",
    UiTextKey.ChatTranslatedLabel to "已翻譯",

    // Sharing
    UiTextKey.ShareTitle to "分享",
    UiTextKey.ShareInboxTitle to "共享收件箱",
    UiTextKey.ShareInboxEmpty to "未有共享項目，朋友可以同你分享生字同學習材料！",
    UiTextKey.ShareWordButton to "分享生字",
    UiTextKey.ShareMaterialButton to "分享材料",
    UiTextKey.ShareSelectFriendTitle to "揀朋友",
    UiTextKey.ShareSelectFriendMessage to "揀要分享嘅朋友：",
    UiTextKey.ShareSuccess to "分享成功！",
    UiTextKey.ShareFailed to "分享失敗",
    UiTextKey.ShareWordWith to "同 {username} 分享生字",
    UiTextKey.ShareMaterialWith to "同 {username} 分享材料",
    UiTextKey.ShareAcceptButton to "接受",
    UiTextKey.ShareDismissButton to "忽略",
    UiTextKey.ShareAccepted to "已加入你嘅收藏",
    UiTextKey.ShareDismissed to "項目已忽略",
    UiTextKey.ShareActionFailed to "操作失敗",
    UiTextKey.ShareTypeWord to "生字",
    UiTextKey.ShareTypeLearningSheet to "學習表",
    UiTextKey.ShareTypeQuiz to "測驗",
    UiTextKey.ShareReceivedFrom to "來自：{username}",
    UiTextKey.ShareNewItemsTemplate to "收到 {count} 個新項目！",
    UiTextKey.ShareViewFullMaterial to "撳「查看」以閱讀完整材料",
    UiTextKey.ShareDeleteItemTitle to "刪除項目",
    UiTextKey.ShareDeleteItemMessage to "確定要刪除呢個共享項目？操作無法還原。",
    UiTextKey.ShareDeleteButton to "刪除",
    UiTextKey.ShareViewButton to "查看",
    UiTextKey.ShareItemNotFound to "搵唔到項目。",
    UiTextKey.ShareNoContent to "呢個材料冇可用內容。",

    // My profile
    UiTextKey.MyProfileTitle to "我嘅個人資料",
    UiTextKey.MyProfileUserId to "用戶 ID",
    UiTextKey.MyProfileUsername to "用戶名",
    UiTextKey.MyProfileDisplayName to "顯示名稱",
    UiTextKey.MyProfileCopyUserId to "複製用戶 ID",
    UiTextKey.MyProfileCopyUsername to "複製用戶名",
    UiTextKey.MyProfileShare to "分享個人資料",
    UiTextKey.MyProfileCopied to "已複製到剪貼板！",
    UiTextKey.MyProfileLanguages to "語言",
    UiTextKey.MyProfilePrimaryLanguage to "主要語言",
    UiTextKey.MyProfileLearningLanguages to "學習語言",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "朋友頁面說明",
    UiTextKey.FriendsInfoMessage to "• 向下拉以手動重新整理朋友名單、請求同朋友狀態。\n" +
            "• 撳朋友卡片以開啟傾偈。\n" +
            "• 朋友卡片左上角嘅紅點（●）表示有未讀訊息。\n" +
            "• 用 📥 收件箱圖示睇共享材料。\n" +
            "• 用垃圾桶圖示進入刪除模式以移除朋友。\n" +
            "• 用搜尋掣透過用戶名或 ID 搵同加新朋友。\n",
    UiTextKey.FriendsEmptyTitle to "未有朋友",
    UiTextKey.FriendsEmptyMessage to "用「加朋友」掣，透過用戶名或用戶 ID 搵朋友。\n",
    UiTextKey.FriendsInfoGotItButton to "明白喇",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "學習頁面說明",
    UiTextKey.LearningInfoMessage to "• 向下拉以手動重新整理你嘅語言紀錄數目。\n" +
            "• 每張卡片顯示一種語言同你擁有嘅翻譯紀錄數目。\n" +
            "• 撳「生成」以建立學習表（首次生成免費）。\n" +
            "• 重新生成需要比上次多最少 5 條紀錄。\n" +
            "• 撳學習表掣以開啟同學習你生成嘅材料。\n" +
            "• 生成學習表後可以進行測驗。",
    UiTextKey.LearningEmptyTitle to "冇翻譯紀錄",
    UiTextKey.LearningEmptyMessage to "開始翻譯以建立歷史紀錄。\n" +
            "學習表從你嘅翻譯歷史生成。\n" +
            "翻譯後向下拉以重新整理。",
    UiTextKey.LearningInfoGotItButton to "明白喇",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "生字庫頁面說明",
    UiTextKey.WordBankInfoMessage to "• 向下拉以手動重新整理你嘅生字庫語言清單。\n" +
            "• 揀語言以睇或生成其生字庫。\n" +
            "• 生字庫從你嘅翻譯歷史生成。\n" +
            "• 重新整理生字庫需要比上次多最少 20 條紀錄。\n" +
            "• 用自訂生字功能手動加你嘅詞彙。\n" +
            "• 你可以同朋友分享生字庫入面嘅生字。",
    UiTextKey.WordBankInfoGotItButton to "明白喇",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "共享收件箱說明",
    UiTextKey.ShareInboxInfoMessage to "• 向下拉以手動重新整理你嘅共享收件箱。\n" +
            "• 朋友分享嘅項目會顯示喺度。\n" +
            "• 生字可以接受加入你嘅生字庫，或者忽略。\n" +
            "• 學習表同測驗可以撳「查看」以閱讀詳細內容。\n" +
            "• 紅點（●）表示有新嘅/未讀項目。\n" +
            "• 忽略共享生字前會要求確認。",
    UiTextKey.ShareInboxInfoGotItButton to "明白喇",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "個人資料可見性",
    UiTextKey.MyProfileVisibilityPublic to "公開",
    UiTextKey.MyProfileVisibilityPrivate to "私人",
    UiTextKey.MyProfileVisibilityDescription to "公開：任何人都可以搜尋同加你為朋友。\n私人：搜尋時搵唔到你。",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "忽略生字",
    UiTextKey.ShareDismissWordMessage to "確定要忽略呢個共享生字？操作無法還原。",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "語言：{language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "忽略",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "已連接或等待中",
    UiTextKey.AccessibilityNewMessages to "新訊息",
    UiTextKey.AccessibilityNewReleasesIcon to "新項目指示",
    UiTextKey.AccessibilitySuccessIcon to "成功",
    UiTextKey.AccessibilityErrorIcon to "錯誤",
    UiTextKey.AccessibilitySharedItemTypeIcon to "共享項目類型",
    UiTextKey.AccessibilityAddCustomWords to "加自訂生字",
    UiTextKey.AccessibilityWordBankExists to "生字庫已存在",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.回饋",
    UiTextKey.SettingsSystemNotesButton to "系統備註同資訊",
    UiTextKey.SystemNotesTitle to "系統備註",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "通知設定",
    UiTextKey.FriendsNotifSettingsTitle to "通知偏好設定",
    UiTextKey.FriendsNotifNewMessages to "新傾偈訊息",
    UiTextKey.FriendsNotifFriendRequests to "收到朋友請求",
    UiTextKey.FriendsNotifRequestAccepted to "朋友請求已接受",
    UiTextKey.FriendsNotifSharedInbox to "新共享收件箱項目",
    UiTextKey.FriendsNotifCloseButton to "完成",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "應用程式內徽章（紅點）",
    UiTextKey.InAppBadgeMessages to "未讀傾偈訊息徽章",
    UiTextKey.InAppBadgeFriendRequests to "等待中朋友請求徽章",
    UiTextKey.InAppBadgeSharedInbox to "未讀共享收件箱徽章",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "請登入先可以繼續。",
    UiTextKey.ErrorSaveFailedRetry to "儲存失敗，請再試過。",
    UiTextKey.ErrorLoadFailedRetry to "載入失敗，請再試過。",
    UiTextKey.ErrorNetworkRetry to "網絡錯誤，請檢查你嘅連線再試過。",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "仲需 {needed} 條翻譯先可以生成材料",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "切換去即時對話 →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "清除傾偈",
    UiTextKey.ChatClearConversationTitle to "清除對話",
    UiTextKey.ChatClearConversationMessage to "刪除呢個對話入面所有訊息？操作無法還原。",
    UiTextKey.ChatClearConversationConfirm to "全部清除",
    UiTextKey.ChatClearConversationSuccess to "對話已清除",

    // Block user
    UiTextKey.BlockUserButton to "封鎖",
    UiTextKey.BlockUserTitle to "封鎖用戶？",
    UiTextKey.BlockUserMessage to "封鎖 {username}？對方將從你嘅朋友名單中移除，仲無法再聯繫你。",
    UiTextKey.BlockUserConfirm to "封鎖",
    UiTextKey.BlockUserSuccess to "用戶已封鎖並從朋友中移除。",
    UiTextKey.BlockedUsersTitle to "已封鎖用戶",
    UiTextKey.BlockedUsersEmpty to "冇已封鎖嘅用戶。",
    UiTextKey.UnblockUserButton to "解除封鎖",
    UiTextKey.UnblockUserTitle to "解除封鎖？",
    UiTextKey.UnblockUserMessage to "解除封鎖 {username}？對方將可以再次發送朋友請求。",
    UiTextKey.UnblockUserSuccess to "用戶已解除封鎖。",
    UiTextKey.BlockedUsersManageButton to "管理已封鎖用戶",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "請求備註（可選）",
    UiTextKey.FriendsRequestNotePlaceholder to "加一個短備註...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "學習表已就緒！撳嚟開啟。",
    UiTextKey.GenerationBannerWordBank to "生字庫已就緒！撳嚟查看。",
    UiTextKey.GenerationBannerQuiz to "測驗已就緒！撳嚟開始。",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "通知",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "繁體中文",
)
