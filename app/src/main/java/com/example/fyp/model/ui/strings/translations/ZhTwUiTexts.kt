package com.example.fyp.model.ui

/**
 * Traditional Chinese (Taiwan) UI text map.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val ZhTwUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "辨識",
    UiTextKey.CopyButton to "複製",
    UiTextKey.SpeakScriptButton to "朗讀原文",
    UiTextKey.TranslateButton to "翻譯",
    UiTextKey.CopyTranslationButton to "複製翻譯",
    UiTextKey.SpeakTranslationButton to "朗讀翻譯",
    UiTextKey.RecognizingStatus to "錄音中...請說話，靜音後自動停止。",
    UiTextKey.TranslatingStatus to "翻譯中...",
    UiTextKey.SpeakingOriginalStatus to "正在朗讀原文...",
    UiTextKey.SpeakingTranslationStatus to "正在朗讀翻譯...",
    UiTextKey.SpeakingLabel to "朗讀中",
    UiTextKey.FinishedSpeakingOriginal to "原文朗讀完畢",
    UiTextKey.FinishedSpeakingTranslation to "翻譯朗讀完畢",
    UiTextKey.TtsErrorTemplate to "語音錯誤：%s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "介面語言",
    UiTextKey.DetectLanguageLabel to "偵測語言",
    UiTextKey.TranslateToLabel to "翻譯至",

    // Language names
    UiTextKey.LangEnUs to "英語",
    UiTextKey.LangZhHk to "粵語",
    UiTextKey.LangJaJp to "日語",
    UiTextKey.LangZhCn to "簡體中文",
    UiTextKey.LangFrFr to "法語",
    UiTextKey.LangDeDe to "德語",
    UiTextKey.LangKoKr to "韓語",
    UiTextKey.LangEsEs to "西班牙語",
    UiTextKey.LangIdId to "印尼語",
    UiTextKey.LangViVn to "越南語",
    UiTextKey.LangThTh to "泰語",
    UiTextKey.LangFilPh to "菲律賓語",
    UiTextKey.LangMsMy to "馬來語",
    UiTextKey.LangPtBr to "葡萄牙語",
    UiTextKey.LangItIt to "義大利語",
    UiTextKey.LangRuRu to "俄語",

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
    UiTextKey.GuestTranslationLimitMessage to "您已更換一次介面語言。請登入後再次更換。已登入用戶可無限次更換介面語言並享有本地快取功能。",

    // Speech
    UiTextKey.SpeechInputPlaceholder to "在此輸入或使用麥克風...",
    UiTextKey.SpeechTranslatedPlaceholder to "翻譯結果將顯示於此...",
    UiTextKey.StatusAzureErrorTemplate to "Azure 錯誤：%s",
    UiTextKey.StatusTranslationErrorTemplate to "翻譯錯誤：%s",
    UiTextKey.StatusLoginRequiredTranslation to "請登入以使用翻譯功能",
    UiTextKey.StatusRecognizePreparing to "準備麥克風中...（請勿說話）",
    UiTextKey.StatusRecognizeListening to "正在聆聽...請說話。",

    // Pagination
    UiTextKey.PaginationPrevLabel to "上一頁",
    UiTextKey.PaginationNextLabel to "下一頁",
    UiTextKey.PaginationPageLabelTemplate to "第 {page} / {total} 頁",

    // Toast
    UiTextKey.ToastCopied to "已複製",
    UiTextKey.DisableText to "需要登入以使用翻譯功能及儲存翻譯紀錄。",

    // Error
    UiTextKey.ErrorRetryButton to "重試",
    UiTextKey.ErrorGenericMessage to "發生錯誤，請再試一次。",

    // Shop
    UiTextKey.ShopTitle to "商店",
    UiTextKey.ShopCoinBalance to "您的硬幣",
    UiTextKey.ShopHistoryExpansionTitle to "擴充歷史紀錄上限",
    UiTextKey.ShopHistoryExpansionDesc to "擴充歷史紀錄查看上限，以查看更多近期翻譯紀錄。",
    UiTextKey.ShopCurrentLimit to "目前上限：{limit} 筆",
    UiTextKey.ShopMaxLimit to "最高上限：",
    UiTextKey.ShopBuyHistoryExpansion to "購買 (+{increment} 筆，{cost} 枚硬幣)",
    UiTextKey.ShopInsufficientCoins to "硬幣不足",
    UiTextKey.ShopMaxLimitReached to "已達最高上限",
    UiTextKey.ShopHistoryExpandedTitle to "擴充成功！",
    UiTextKey.ShopHistoryExpandedMessage to "您的歷史紀錄上限已擴充至 {limit} 筆，現在可以查看更多翻譯紀錄了！",
    UiTextKey.ShopColorPaletteTitle to "色彩主題",
    UiTextKey.ShopColorPaletteDesc to "選擇您的應用程式色彩主題，每個新主題 10 枚硬幣",
    UiTextKey.ShopEntry to "商店",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "語音設定",
    UiTextKey.VoiceSettingsDesc to "為每種語言選擇文字轉語音的語音。",

    // Instructions
    UiTextKey.SpeechInstructions to "點擊麥克風按鈕開始語音辨識，完成後點擊翻譯。若切換文字或語言後自動偵測看起來未更新，請點右上角重新整理再試一次。",
    UiTextKey.HomeInstructions to "選擇功能開始使用。",
    UiTextKey.ContinuousInstructions to "選擇兩種語言並開始對話模式。",

    // Home
    UiTextKey.HomeTitle to "即時翻譯",
    UiTextKey.HelpTitle to "說明",
    UiTextKey.SpeechTitle to "快速翻譯",
    UiTextKey.HomeStartButton to "開始翻譯",
    UiTextKey.HomeFeaturesTitle to "功能介紹",
    UiTextKey.HomeDiscreteDescription to "短句和語音翻譯",
    UiTextKey.HomeContinuousDescription to "即時雙向對話翻譯",
    UiTextKey.HomeLearningDescription to "從翻譯紀錄生成學習教材和測驗",

    // Help
    UiTextKey.HelpCurrentTitle to "目前功能",
    UiTextKey.HelpCautionTitle to "注意事項",
    UiTextKey.HelpCurrentFeatures to "目前功能：\n" +
            "  • 快速翻譯：語音辨識後翻譯\n" +
            "  • 即時對話：雙向語音翻譯\n" +
            "  • 歷史紀錄：檢視翻譯紀錄\n" +
            "  • 學習教材：從紀錄生成詞彙和測驗\n\n" +
            "翻譯：\n" +
            "  • 使用 Azure AI 語音辨識\n" +
            "  • 使用 Azure 翻譯服務\n",
    UiTextKey.HelpCaution to "注意事項：\n" +
            "  • 語音辨識需要網路連線\n" +
            "  • 本地翻譯快取在離線時可用\n" +
            "  • 重要翻譯請以專業服務驗證\n\n" +
            "帳號與資料：\n" +
            "  • 歷史紀錄、學習和硬幣功能需要登入\n" +
            "  • 用戶資料安全儲存於 Firebase Firestore\n\n" +
            "疑難排解：\n" +
            "  • 若完成所需步驟後某個功能仍無法操作，請重新啟動 App 後再試一次\n",
    UiTextKey.HelpNotesTitle to "備註",
    UiTextKey.HelpNotes to "💡 使用技巧與疑難排解：\n\n" +
            "獲得最佳翻譯效果：\n" +
            "  • 清晰且速度適中地說話\n" +
            "  • 減少背景噪音以提升辨識準確度\n" +
            "  • 快速翻譯模式以簡短句子效果最佳\n\n" +
            "介面語言：\n" +
            "  • 基礎語言為英語；其他介面語言為 AI 翻譯\n" +
            "  • 繁體中文版本為手動翻譯，準確度更高\n" +
            "更新與回饋：\n" +
            "  • 應用程式版本顯示於設定 → 關於\n" +
            "  • 請在設定 → 回饋提交意見，或在 GitHub 回報問題\n",

    // Feedback
    UiTextKey.FeedbackTitle to "回饋",
    UiTextKey.FeedbackDesc to "感謝您的回饋！請分享您的建議、錯誤回報或對應用程式的評論。",
    UiTextKey.FeedbackMessagePlaceholder to "請在此輸入您的回饋...",
    UiTextKey.FeedbackSubmitButton to "提交回饋",
    UiTextKey.FeedbackSubmitting to "提交中...",
    UiTextKey.FeedbackSuccessTitle to "感謝您！",
    UiTextKey.FeedbackSuccessMessage to "您的回饋已成功提交，感謝您的寶貴意見！",
    UiTextKey.FeedbackErrorTitle to "提交失敗",
    UiTextKey.FeedbackErrorMessage to "提交回饋失敗，請檢查您的網路連線並再試一次。",
    UiTextKey.FeedbackMessageRequired to "請輸入您的回饋內容。",

    // Continuous mode
    UiTextKey.ContinuousTitle to "即時對話",
    UiTextKey.ContinuousStartButton to "開始對話",
    UiTextKey.ContinuousStopButton to "停止聆聽",
    UiTextKey.ContinuousStartScreenButton to "即時對話",
    UiTextKey.ContinuousPersonALabel to "A 說話中",
    UiTextKey.ContinuousPersonBLabel to "B 說話中",
    UiTextKey.ContinuousCurrentStringLabel to "目前文字：",
    UiTextKey.ContinuousSpeakerAName to "人物 A",
    UiTextKey.ContinuousSpeakerBName to "人物 B",
    UiTextKey.ContinuousTranslationSuffix to "・翻譯",
    UiTextKey.ContinuousPreparingMicText to "準備麥克風中...（請勿說話）",
    UiTextKey.ContinuousTranslatingText to "翻譯中...",

    // History
    UiTextKey.HistoryTitle to "歷史紀錄",
    UiTextKey.HistoryTabDiscrete to "快速翻譯",
    UiTextKey.HistoryTabContinuous to "即時對話",
    UiTextKey.HistoryNoContinuousSessions to "尚無對話紀錄。",
    UiTextKey.HistoryNoDiscreteRecords to "尚無翻譯紀錄。",
    UiTextKey.DialogDeleteRecordTitle to "刪除紀錄？",
    UiTextKey.DialogDeleteRecordMessage to "此操作無法復原。",
    UiTextKey.DialogDeleteSessionTitle to "刪除對話？",
    UiTextKey.DialogDeleteSessionMessage to "此對話中的所有紀錄將被刪除，此操作無法復原。",
    UiTextKey.HistoryDeleteSessionButton to "刪除",
    UiTextKey.HistoryNameSessionTitle to "命名",
    UiTextKey.HistorySessionNameLabel to "對話名稱",
    UiTextKey.HistorySessionTitleTemplate to "對話 {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} 筆紀錄",

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
    UiTextKey.AuthRegisterTitle to "註冊（已停用）",
    UiTextKey.AuthLoginHint to "請使用您的已登記電子郵件和密碼。",
    UiTextKey.AuthRegisterRules to "開發階段停用註冊功能。\n注意：如使用不存在的電子郵件，將無法重設密碼。\n" +
            "註冊規則：\n" +
            "• 電子郵件格式需正確（例如 name@example.com）\n" +
            "• 密碼至少 6 個字元\n" +
            "• 確認密碼需與密碼相符",
    UiTextKey.AuthEmailLabel to "電子郵件",
    UiTextKey.AuthPasswordLabel to "密碼",
    UiTextKey.AuthConfirmPasswordLabel to "確認密碼",
    UiTextKey.AuthLoginButton to "登入",
    UiTextKey.AuthRegisterButton to "註冊",
    UiTextKey.AuthToggleToRegister to "沒有帳號？註冊（已停用）",
    UiTextKey.AuthToggleToLogin to "已有帳號？登入",
    UiTextKey.AuthErrorPasswordsMismatch to "密碼不相符。",
    UiTextKey.AuthErrorPasswordTooShort to "密碼至少需要 6 個字元。",
    UiTextKey.AuthRegistrationDisabled to "開發期間停用註冊功能。",
    UiTextKey.AuthResetEmailSent to "重設電子郵件已發送（如電子郵件真實存在）。請檢查您的收件匣。",

    // Password reset
    UiTextKey.ForgotPwText to "忘記密碼？在此重設",
    UiTextKey.ResetPwTitle to "重設密碼",
    UiTextKey.ResetPwText to "輸入您的帳號電子郵件，將傳送重設連結。\n請確認該電子郵件已於應用程式中登記，否則不會傳送郵件。\n",
    UiTextKey.ResetSendingText to "傳送中...",
    UiTextKey.ResetSendText to "傳送重設郵件",

    // Settings
    UiTextKey.SettingsTitle to "設定",
    UiTextKey.SettingsPrimaryLanguageTitle to "主要語言",
    UiTextKey.SettingsPrimaryLanguageDesc to "用於學習說明和建議",
    UiTextKey.SettingsPrimaryLanguageLabel to "主要語言",
    UiTextKey.SettingsFontSizeTitle to "字體大小",
    UiTextKey.SettingsFontSizeDesc to "調整文字大小以提升閱讀體驗（跨裝置同步）",
    UiTextKey.SettingsScaleTemplate to "縮放：{pct}%",
    UiTextKey.SettingsColorPaletteTitle to "色彩主題",
    UiTextKey.SettingsColorPaletteDesc to "選擇應用程式色彩主題，每個新主題 10 枚硬幣",
    UiTextKey.SettingsColorCostTemplate to "{cost} 枚硬幣",
    UiTextKey.SettingsColorUnlockButton to "解鎖",
    UiTextKey.SettingsColorSelectButton to "選擇",
    UiTextKey.SettingsColorAlreadyUnlocked to "已解鎖",
    UiTextKey.SettingsPreviewHeadline to "標題：大文字預覽",
    UiTextKey.SettingsPreviewBody to "內文：一般文字預覽",
    UiTextKey.SettingsPreviewLabel to "標籤：小文字預覽",
    UiTextKey.SettingsAboutTitle to "關於",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "登入後，您的設定會自動儲存並同步至帳號。",
    UiTextKey.SettingsThemeTitle to "主題",
    UiTextKey.SettingsThemeDesc to "選擇外觀套用方式：跟隨系統、淺色、深色或排程。",
    UiTextKey.SettingsThemeSystem to "跟隨系統",
    UiTextKey.SettingsThemeLight to "淺色",
    UiTextKey.SettingsThemeDark to "深色",
    UiTextKey.SettingsThemeScheduled to "排程",
    UiTextKey.SettingsResetPW to "在此重設密碼",
    UiTextKey.SettingsQuickLinks to "詳細設定",
    UiTextKey.SettingsNotLoggedInWarning to "請先登入以使用帳號設定。您仍可更改應用程式語言。",
    UiTextKey.SettingsVoiceTitle to "語音設定",
    UiTextKey.SettingsVoiceDesc to "為每種語言選擇文字轉語音的語音。",
    UiTextKey.SettingsVoiceLanguageLabel to "語言",
    UiTextKey.SettingsVoiceSelectLabel to "語音",
    UiTextKey.SettingsVoiceDefault to "預設",

    // Learning
    UiTextKey.LearningTitle to "學習",
    UiTextKey.LearningHintCount to "(*) 次數 = 包含此語言的歷史翻譯紀錄數量。",
    UiTextKey.LearningErrorTemplate to "錯誤：%s",
    UiTextKey.LearningGenerate to "生成",
    UiTextKey.LearningRegenerate to "重新生成",
    UiTextKey.LearningGenerating to "生成中...",
    UiTextKey.LearningOpenSheetTemplate to "{speclanguage} 學習表",
    UiTextKey.LearningSheetTitleTemplate to "{speclanguage} 學習表",
    UiTextKey.LearningSheetPrimaryTemplate to "主要語言：{speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "目前紀錄數：{nowCount}（生成時：{savedCount}）",
    UiTextKey.LearningSheetNoContent to "尚無學習表內容。",
    UiTextKey.LearningSheetRegenerate to "重新生成",
    UiTextKey.LearningSheetGenerating to "生成中...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 這是什麼？",
    UiTextKey.LearningSheetWhatIsThisDesc to "這份是依據您的翻譯紀錄量身生成的學習表，包含詞彙、意思、例句與文法重點，協助您複習。想測試自己，可點選「測驗」按鈕。",
    UiTextKey.LearningRegenBlockedTitle to "目前無法重新生成",
    UiTextKey.LearningRegenBlockedMessage to "重新生成需要比上次生成多至少 5 筆紀錄，您目前還需要 {needed} 筆。",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ 需要再 {needed} 筆紀錄才能重新生成（最少 5 筆）",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ 紀錄數需高於上次生成時的數量",
    UiTextKey.LearningRegenInfoTitle to "重新生成規則",
    UiTextKey.LearningRegenInfoMessage to "要重新生成學習教材：\n\n• 首次生成：隨時可生成\n• 重新生成：需比上次生成多至少 5 筆翻譯紀錄\n\n當您有足夠的新紀錄時，按鈕將啟用（藍色）。若按鈕為停用（灰色），請繼續翻譯以解鎖重新生成！\n\n💡 提示：若翻譯後數量未更新，請重啟應用程式以重新整理。",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ 此版本教材已生成測驗，請生成新學習表以建立新測驗。",

    // Quiz
    UiTextKey.QuizTitleTemplate to "測驗：{language}",
    UiTextKey.QuizOpenButton to "📝 測驗",
    UiTextKey.QuizGenerateButton to "🔄 生成測驗",
    UiTextKey.QuizGenerating to "⏳ 生成中...",
    UiTextKey.QuizUpToDate to "✓ 最新版本",
    UiTextKey.QuizBlocked to "🚫 已封鎖",
    UiTextKey.QuizWait to "⏳ 等待...",
    UiTextKey.QuizMaterialsQuizTemplate to "教材：{materials} | 測驗：{quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 可獲得硬幣！",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 需再 {count} 筆紀錄才可獲得硬幣",
    UiTextKey.QuizCancelButton to "取消",
    UiTextKey.QuizPreviousButton to "上一題",
    UiTextKey.QuizNextButton to "下一題",
    UiTextKey.QuizSubmitButton to "提交",
    UiTextKey.QuizRetakeButton to "重新測驗",
    UiTextKey.QuizBackButton to "返回",
    UiTextKey.QuizLoadingText to "載入測驗中...",
    UiTextKey.QuizGeneratingText to "生成測驗中...",
    UiTextKey.QuizNoMaterialsTitle to "找不到學習教材",
    UiTextKey.QuizNoMaterialsMessage to "請返回並先生成學習教材，再查看測驗。",
    UiTextKey.QuizErrorTitle to "⚠️ 測驗錯誤",
    UiTextKey.QuizErrorSuggestion to "建議：使用上方按鈕生成測驗。",
    UiTextKey.QuizCompletedTitle to "測驗完成！",
    UiTextKey.QuizAnswerReviewTitle to "答案回顧",
    UiTextKey.QuizYourAnswerTemplate to "您的答案：{Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "正確答案：{Answer}",
    UiTextKey.QuizQuestionTemplate to "第 {current} 題，共 {total} 題",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ 無法重新生成：教材（{materials}）< 測驗（{quiz}），請新增更多翻譯。",
    UiTextKey.QuizAnotherGenInProgress to "⏳ 另一個生成任務進行中，請稍後。",
    UiTextKey.QuizCoinRulesTitle to "🪙 硬幣獲取規則",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ 如何獲取：",
    UiTextKey.QuizCoinRulesRequirements to "條件：",
    UiTextKey.QuizCoinRulesCurrentStatus to "目前狀態：",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ 下次測驗可獲得硬幣！",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• 需再 {count} 筆紀錄才可獲得硬幣",
    UiTextKey.QuizCoinRule1Coin to "• 每題答對獲得 1 枚硬幣",
    UiTextKey.QuizCoinRuleFirstAttempt to "• 每個測驗版本僅第一次作答有效",
    UiTextKey.QuizCoinRuleMatchMaterials to "• 測驗需與教材版本相符",
    UiTextKey.QuizCoinRulePlus10 to "• 需比上次獲得硬幣的測驗多 10 筆以上紀錄",
    UiTextKey.QuizCoinRuleNoDelete to "• 刪除歷史紀錄不可重新獲得硬幣",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• 教材：{count} 筆紀錄",
    UiTextKey.QuizCoinRuleQuizTemplate to "• 測驗：{count} 筆紀錄",
    UiTextKey.QuizCoinRuleGotIt to "明白了！",
    UiTextKey.QuizRegenConfirmTitle to "🔄 生成新測驗？",
    UiTextKey.QuizRegenCanEarnCoins to "✅ 此測驗可獲得硬幣！（僅限首次作答）",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ 此測驗目前無法獲得硬幣。",
    UiTextKey.QuizRegenNeedMoreTemplate to "需再多 {count} 筆翻譯紀錄才符合硬幣資格（比上次獲得硬幣的測驗多至少 10 筆）。",
    UiTextKey.QuizRegenReminder to "提醒：您仍可練習並重做測驗，但硬幣僅於首次作答且有足夠新紀錄時發放。",
    UiTextKey.QuizRegenGenerateButton to "生成",
    UiTextKey.QuizCoinsEarnedTitle to "✨ 獲得硬幣！",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "恭喜！您獲得了 {Coins} 枚硬幣！",
    UiTextKey.QuizCoinsRule1 to "• 僅首次作答每題答對獲得 1 枚硬幣",
    UiTextKey.QuizCoinsRule2 to "• 重做相同測驗不獲得硬幣",
    UiTextKey.QuizCoinsRule3 to "• 新測驗需比上次多 10 筆以上紀錄",
    UiTextKey.QuizCoinsRule4 to "• 測驗需與目前教材版本相符",
    UiTextKey.QuizCoinsRule5 to "• 在歷史紀錄頁面查看總硬幣數",
    UiTextKey.QuizCoinsGreatButton to "太好了！",
    UiTextKey.QuizOutdatedMessage to "此測驗基於舊版學習表。",
    UiTextKey.QuizRecordsLabel to "筆紀錄",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 您的硬幣",
    UiTextKey.HistoryCoinRulesTitle to "硬幣獲取規則：",
    UiTextKey.HistoryCoinHowToEarnTitle to "如何獲取：",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 每題答對獲得 1 枚硬幣",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• 僅每個測驗版本的首次作答有效",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• 測驗需與目前學習教材相符",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 防作弊規則：",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• 比上次獲得硬幣的測驗需多 10 筆以上新翻譯才可再次獲取",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• 測驗版本需與教材版本相同",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• 刪除歷史紀錄會封鎖測驗重新生成（除非數量高於上次紀錄）",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• 重做相同測驗不獲得硬幣",
    UiTextKey.HistoryCoinTipsTitle to "💡 提示：",
    UiTextKey.HistoryCoinTipsRule1 to "• 定期新增更多翻譯",
    UiTextKey.HistoryCoinTipsRule2 to "• 首次作答前好好學習！",
    UiTextKey.HistoryCoinGotItButton to "明白了！",

    // History info
    UiTextKey.HistoryInfoTitle to "歷史紀錄說明",
    UiTextKey.HistoryInfoLimitMessage to "歷史紀錄顯示最近 {limit} 筆，可在商店中擴充上限！",
    UiTextKey.HistoryInfoOlderRecordsMessage to "較舊的紀錄仍有儲存，但不會顯示以優化效能。",
    UiTextKey.HistoryInfoFavoritesMessage to "要永久保存重要翻譯，請點擊任何紀錄上的愛心 ❤️ 圖示加入最愛。",
    UiTextKey.HistoryInfoViewFavoritesMessage to "在設定 → 最愛中查看已儲存的最愛紀錄。",
    UiTextKey.HistoryInfoFilterMessage to "使用篩選按鈕在顯示的 {limit} 筆紀錄中搜尋。",
    UiTextKey.HistoryInfoGotItButton to "明白了",

    // Word bank
    UiTextKey.WordBankTitle to "單字庫",
    UiTextKey.WordBankSelectLanguage to "選擇語言以查看或生成單字庫：",
    UiTextKey.WordBankNoHistory to "無翻譯紀錄",
    UiTextKey.WordBankNoHistoryHint to "開始翻譯以建立您的單字庫！",
    UiTextKey.WordBankWordsCount to "個單字",
    UiTextKey.WordBankGenerating to "生成中...",
    UiTextKey.WordBankGenerate to "生成單字庫",
    UiTextKey.WordBankRegenerate to "重新生成單字庫",
    UiTextKey.WordBankRefresh to "🔄 重新整理單字庫",
    UiTextKey.WordBankEmpty to "尚無單字庫",
    UiTextKey.WordBankEmptyHint to "點擊上方按鈕，從翻譯歷史生成單字庫。",
    UiTextKey.WordBankExample to "例句：",
    UiTextKey.WordBankDifficulty to "難度：",
    UiTextKey.WordBankFilterCategory to "類別",
    UiTextKey.WordBankFilterCategoryAll to "所有類別",
    UiTextKey.WordBankFilterDifficultyLabel to "難度等級：",
    UiTextKey.WordBankFilterNoResults to "沒有符合篩選條件的單字",
    UiTextKey.WordBankRefreshAvailable to "✅ 可重新整理！",
    UiTextKey.WordBankRecordsNeeded to "筆紀錄（需 20 筆才可重新整理）",
    UiTextKey.WordBankRegenInfoTitle to "重新整理規則",
    UiTextKey.WordBankRegenInfoMessage to "要重新整理您的單字庫：\n\n• 首次生成：隨時可生成\n• 重新整理：需比上次生成多至少 20 筆翻譯紀錄\n\n當您有足夠的新紀錄時，重新整理按鈕將啟用（藍色）。若按鈕為停用（灰色），請繼續翻譯以解鎖重新整理！\n\n💡 提示：若翻譯後數量未更新，請重啟應用程式以重新整理。",
    UiTextKey.WordBankHistoryCountTemplate to "目前紀錄數：{nowCount}（生成時：{savedCount}）",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "登出？",
    UiTextKey.DialogLogoutMessage to "您需要重新登入才能使用翻譯功能 / 儲存和查看歷史紀錄。",
    UiTextKey.DialogGenerateOverwriteTitle to "覆蓋教材？",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "先前的教材將被覆蓋（如存在）。\n為 {speclanguage} 生成教材？",

    // Profile
    UiTextKey.ProfileTitle to "個人資料",
    UiTextKey.ProfileUsernameLabel to "用戶名稱",
    UiTextKey.ProfileUsernameHint to "輸入您的用戶名稱",
    UiTextKey.ProfileUpdateButton to "更新個人資料",
    UiTextKey.ProfileUpdateSuccess to "個人資料更新成功",
    UiTextKey.ProfileUpdateError to "個人資料更新失敗",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "刪除帳號",
    UiTextKey.AccountDeleteWarning to "⚠️ 此操作永久不可復原！",
    UiTextKey.AccountDeleteConfirmMessage to "您的所有資料，包括歷史紀錄、單字庫、學習教材和設定，將被永久刪除。請輸入密碼確認。",
    UiTextKey.AccountDeletePasswordLabel to "密碼",
    UiTextKey.AccountDeleteButton to "刪除我的帳號",
    UiTextKey.AccountDeleteSuccess to "帳號刪除成功",
    UiTextKey.AccountDeleteError to "帳號刪除失敗",
    UiTextKey.AccountDeleteReauthRequired to "請重新輸入密碼以確認刪除",

    // Favorites
    UiTextKey.FavoritesTitle to "最愛",
    UiTextKey.FavoritesEmpty to "尚無最愛紀錄",
    UiTextKey.FavoritesAddSuccess to "已加入最愛",
    UiTextKey.FavoritesRemoveSuccess to "已從最愛移除",
    UiTextKey.FavoritesAddButton to "加入最愛",
    UiTextKey.FavoritesRemoveButton to "從最愛移除",
    UiTextKey.FavoritesNoteLabel to "備註",
    UiTextKey.FavoritesNoteHint to "新增備註（可選）",
    UiTextKey.FavoritesTabRecords to "紀錄",
    UiTextKey.FavoritesTabSessions to "對話",
    UiTextKey.FavoritesSessionsEmpty to "尚無已儲存的對話",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} 則訊息",

    // Custom words
    UiTextKey.CustomWordsTitle to "自訂單字",
    UiTextKey.CustomWordsAdd to "新增單字",
    UiTextKey.CustomWordsEdit to "編輯單字",
    UiTextKey.CustomWordsDelete to "刪除單字",
    UiTextKey.CustomWordsOriginalLabel to "原始單字",
    UiTextKey.CustomWordsTranslatedLabel to "翻譯",
    UiTextKey.CustomWordsPronunciationLabel to "發音（可選）",
    UiTextKey.CustomWordsExampleLabel to "例句（可選）",
    UiTextKey.CustomWordsSaveSuccess to "單字儲存成功",
    UiTextKey.CustomWordsDeleteSuccess to "單字刪除成功",
    UiTextKey.CustomWordsAlreadyExists to "此單字已存在",
    UiTextKey.CustomWordsOriginalLanguageLabel to "原始語言",
    UiTextKey.CustomWordsTranslationLanguageLabel to "翻譯語言",
    UiTextKey.CustomWordsSaveButton to "儲存",
    UiTextKey.CustomWordsCancelButton to "取消",

    // Language detection
    UiTextKey.LanguageDetectAuto to "自動偵測",
    UiTextKey.LanguageDetectDetecting to "偵測中...",
    UiTextKey.LanguageDetectedTemplate to "已偵測：{language}",
    UiTextKey.LanguageDetectFailed to "偵測失敗",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "從圖片掃描文字",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ 注意：圖片文字辨識可能不完全準確，請檢查擷取的文字。" +
            "支援拉丁字元（英語等）、中文、日語和韓語。",
    UiTextKey.ImageRecognitionScanning to "正在掃描圖片文字...",
    UiTextKey.ImageRecognitionSuccess to "文字擷取成功",

    // Cache
    UiTextKey.CacheClearButton to "清除快取",
    UiTextKey.CacheClearSuccess to "快取已成功清除",
    UiTextKey.CacheStatsTemplate to "快取：已儲存 {count} 筆翻譯",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "自動切換主題",
    UiTextKey.SettingsAutoThemeDesc to "根據時間自動在淺色和深色主題之間切換",
    UiTextKey.SettingsAutoThemeEnabled to "已啟用",
    UiTextKey.SettingsAutoThemeDisabled to "已停用",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "深色模式開始時間：",
    UiTextKey.SettingsAutoThemeLightStartLabel to "淺色模式開始時間：",
    UiTextKey.SettingsAutoThemePreview to "主題將於排程時間自動切換",

    // Offline mode
    UiTextKey.OfflineModeTitle to "離線模式",
    UiTextKey.OfflineModeMessage to "您目前離線，正在瀏覽快取資料。",
    UiTextKey.OfflineModeRetry to "重試連線",
    UiTextKey.OfflineDataCached to "快取資料可用",
    UiTextKey.OfflineSyncPending to "連線後將同步變更",

    // Image capture
    UiTextKey.ImageSourceTitle to "選擇圖片來源",
    UiTextKey.ImageSourceCamera to "拍照",
    UiTextKey.ImageSourceGallery to "從相簿選擇",
    UiTextKey.ImageSourceCancel to "取消",
    UiTextKey.CameraCaptureContentDesc to "拍攝",

    // Friends
    UiTextKey.FriendsTitle to "好友",
    UiTextKey.FriendsMenuButton to "好友",
    UiTextKey.FriendsAddButton to "新增好友",
    UiTextKey.FriendsSearchTitle to "搜尋用戶",
    UiTextKey.FriendsSearchPlaceholder to "輸入用戶名稱或用戶 ID...",
    UiTextKey.FriendsSearchMinChars to "請輸入至少 2 個字元以搜尋",
    UiTextKey.FriendsSearchNoResults to "找不到用戶",
    UiTextKey.FriendsListEmpty to "新增好友以聯繫並分享學習教材。",
    UiTextKey.FriendsRequestsSection to "好友請求（{count}）",
    UiTextKey.FriendsSectionTitle to "好友（{count}）",
    UiTextKey.FriendsAcceptButton to "接受",
    UiTextKey.FriendsRejectButton to "拒絕",
    UiTextKey.FriendsRemoveButton to "移除",
    UiTextKey.FriendsRemoveDialogTitle to "移除好友",
    UiTextKey.FriendsRemoveDialogMessage to "確定要將 {username} 從好友名單中移除嗎？",
    UiTextKey.FriendsSendRequestButton to "新增",
    UiTextKey.FriendsRequestSentSuccess to "好友請求已送出！",
    UiTextKey.FriendsRequestAcceptedSuccess to "好友請求已接受！",
    UiTextKey.FriendsRequestRejectedSuccess to "請求已拒絕",
    UiTextKey.FriendsRemovedSuccess to "好友已移除",
    UiTextKey.FriendsRequestFailed to "請求送出失敗",
    UiTextKey.FriendsCloseButton to "關閉",
    UiTextKey.FriendsCancelButton to "取消",
    UiTextKey.FriendsRemoveConfirm to "移除",
    UiTextKey.FriendsNewRequestsTemplate to "您有 {count} 個新好友請求！",
    UiTextKey.FriendsSentRequestsSection to "已送出的請求（{count}）",
    UiTextKey.FriendsPendingStatus to "待處理",
    UiTextKey.FriendsCancelRequestButton to "取消請求",
    UiTextKey.FriendsUnreadMessageDesc to "發送訊息",
    UiTextKey.FriendsDeleteModeButton to "刪除好友",
    UiTextKey.FriendsDeleteSelectedButton to "刪除所選",
    UiTextKey.FriendsDeleteMultipleTitle to "移除好友",
    UiTextKey.FriendsDeleteMultipleMessage to "移除所選 {count} 位好友？",
    UiTextKey.FriendsSearchMinChars3 to "請輸入至少 3 個字元以按用戶名稱搜尋",
    UiTextKey.FriendsSearchByUserIdHint to "或輸入完整用戶 ID 進行精確查詢",
    UiTextKey.FriendsStatusAlreadyFriends to "已是好友",
    UiTextKey.FriendsStatusRequestSent to "請求已發送 — 等待回覆",
    UiTextKey.FriendsStatusRequestReceived to "此用戶已向你發送請求",

    // Chat
    UiTextKey.ChatTitle to "與 {username} 聊天",
    UiTextKey.ChatInputPlaceholder to "輸入訊息...",
    UiTextKey.ChatSendButton to "傳送",
    UiTextKey.ChatEmpty to "尚無訊息，開始對話吧！",
    UiTextKey.ChatMessageSent to "訊息已傳送",
    UiTextKey.ChatMessageFailed to "訊息傳送失敗",
    UiTextKey.ChatMarkingRead to "標記為已讀...",
    UiTextKey.ChatLoadingMessages to "載入訊息中...",
    UiTextKey.ChatToday to "今天",
    UiTextKey.ChatYesterday to "昨天",
    UiTextKey.ChatUnreadBadge to "{count} 則未讀",
    UiTextKey.ChatTranslateButton to "翻譯",
    UiTextKey.ChatTranslateDialogTitle to "翻譯對話",
    UiTextKey.ChatTranslateDialogMessage to "將好友的訊息翻譯成您的偏好語言？系統將偵測每則訊息的語言並進行翻譯。",
    UiTextKey.ChatTranslateConfirm to "全部翻譯",
    UiTextKey.ChatTranslating to "翻譯訊息中...",
    UiTextKey.ChatTranslated to "訊息已翻譯",
    UiTextKey.ChatShowOriginal to "顯示原文",
    UiTextKey.ChatShowTranslation to "顯示翻譯",
    UiTextKey.ChatTranslateFailed to "翻譯失敗",
    UiTextKey.ChatTranslatedLabel to "已翻譯",

    // Sharing
    UiTextKey.ShareTitle to "分享",
    UiTextKey.ShareInboxTitle to "共享收件匣",
    UiTextKey.ShareInboxEmpty to "尚無共享項目，好友可以與您分享單字和學習教材！",
    UiTextKey.ShareWordButton to "分享單字",
    UiTextKey.ShareMaterialButton to "分享教材",
    UiTextKey.ShareSelectFriendTitle to "選擇好友",
    UiTextKey.ShareSelectFriendMessage to "選擇要分享的好友：",
    UiTextKey.ShareSuccess to "分享成功！",
    UiTextKey.ShareFailed to "分享失敗",
    UiTextKey.ShareWordWith to "與 {username} 分享單字",
    UiTextKey.ShareMaterialWith to "與 {username} 分享教材",
    UiTextKey.ShareAcceptButton to "接受",
    UiTextKey.ShareDismissButton to "忽略",
    UiTextKey.ShareAccepted to "已加入您的收藏",
    UiTextKey.ShareDismissed to "項目已忽略",
    UiTextKey.ShareActionFailed to "操作失敗",
    UiTextKey.ShareTypeWord to "單字",
    UiTextKey.ShareTypeLearningSheet to "學習表",
    UiTextKey.ShareTypeQuiz to "測驗",
    UiTextKey.ShareReceivedFrom to "來自：{username}",
    UiTextKey.ShareNewItemsTemplate to "收到 {count} 個新項目！",
    UiTextKey.ShareViewFullMaterial to "點擊「查看」以閱讀完整教材",
    UiTextKey.ShareDeleteItemTitle to "刪除項目",
    UiTextKey.ShareDeleteItemMessage to "確定要刪除此共享項目？此操作無法復原。",
    UiTextKey.ShareDeleteButton to "刪除",
    UiTextKey.ShareViewButton to "查看",
    UiTextKey.ShareItemNotFound to "找不到項目。",
    UiTextKey.ShareNoContent to "此教材無可用內容。",
    UiTextKey.ShareSaveToSelf to "儲存到自己的收件匣",
    UiTextKey.ShareSavedToSelf to "已儲存到您的收件匣！",

    // My profile
    UiTextKey.MyProfileTitle to "我的個人資料",
    UiTextKey.MyProfileUserId to "用戶 ID",
    UiTextKey.MyProfileUsername to "用戶名稱",
    UiTextKey.MyProfileDisplayName to "顯示名稱",
    UiTextKey.MyProfileCopyUserId to "複製用戶 ID",
    UiTextKey.MyProfileCopyUsername to "複製用戶名稱",
    UiTextKey.MyProfileShare to "分享個人資料",
    UiTextKey.MyProfileCopied to "已複製到剪貼簿！",
    UiTextKey.MyProfileLanguages to "語言",
    UiTextKey.MyProfilePrimaryLanguage to "主要語言",
    UiTextKey.MyProfileLearningLanguages to "學習語言",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "好友頁面說明",
    UiTextKey.FriendsInfoMessage to "• 下拉以手動重新整理好友名單、請求和好友狀態。\n" +
            "• 點擊好友卡片以開啟聊天。\n" +
            "• 好友卡片上的紅點（●）表示有未讀訊息，點擊 ✓✓ 可一次全部標記已讀。\n" +
            "• 使用 📥 收件匣圖示查看共享教材，點擊旁邊的 ✓✓ 可消除紅點。\n" +
            "• 使用好友卡片上的 🚫 圖示封鎖該用戶——對方將從您的名單中移除，且無法再聯繫您。\n" +
            "• 封鎖用戶後，您與對方的聊天紀錄也將被清除。\n" +
            "• 使用垃圾桶圖示進入刪除模式以移除好友。\n" +
            "• 移除好友後，您們之間的所有聊天訊息也將被刪除。\n" +
            "• 使用搜尋按鈕透過用戶名稱或用戶 ID 尋找並新增新好友。\n" +
            "• 推播通知預設為關閉——可在通知設定中開啟。\n",
    UiTextKey.FriendsEmptyTitle to "尚無好友",
    UiTextKey.FriendsEmptyMessage to "使用「新增好友」按鈕，透過用戶名稱或用戶 ID 搜尋好友。\n",
    UiTextKey.FriendsInfoGotItButton to "明白了",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "學習頁面說明",
    UiTextKey.LearningInfoMessage to "• 下拉以手動重新整理您的語言紀錄數量。\n" +
            "• 每張卡片顯示一種語言及您擁有的翻譯紀錄數量。\n" +
            "• 按「生成」以建立學習表（首次生成免費）。\n" +
            "• 重新生成需要比上次多至少 5 筆紀錄。\n" +
            "• 按學習表按鈕以開啟並學習您生成的教材。\n" +
            "• 生成學習表後可進行測驗。",
    UiTextKey.LearningEmptyTitle to "無翻譯紀錄",
    UiTextKey.LearningEmptyMessage to "開始翻譯以建立歷史紀錄。\n" +
            "學習表從您的翻譯歷史生成。\n" +
            "翻譯後下拉以重新整理。",
    UiTextKey.LearningInfoGotItButton to "明白了",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "單字庫頁面說明",
    UiTextKey.WordBankInfoMessage to "• 下拉以手動重新整理您的單字庫語言清單。\n" +
            "• 選擇語言以查看或生成其單字庫。\n" +
            "• 單字庫從您的翻譯歷史生成。\n" +
            "• 重新整理單字庫需要比上次多至少 20 筆紀錄。\n" +
            "• 使用自訂單字功能手動新增您的詞彙。\n" +
            "• 您可以與好友分享單字庫中的單字。",
    UiTextKey.WordBankInfoGotItButton to "明白了",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "共享收件匣說明",
    UiTextKey.ShareInboxInfoMessage to "• 下拉以手動重新整理您的共享收件匣。\n" +
            "• 好友分享的項目會顯示在這裡。\n" +
            "• 單字可以接受加入您的單字庫，或忽略。\n" +
            "• 學習表和測驗可以點擊「查看」以閱讀詳細內容。\n" +
            "• 紅點（●）表示有新的/未讀項目。\n" +
            "• 忽略共享單字前會要求確認。",
    UiTextKey.ShareInboxInfoGotItButton to "明白了",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "個人資料可見性",
    UiTextKey.MyProfileVisibilityPublic to "公開",
    UiTextKey.MyProfileVisibilityPrivate to "私人",
    UiTextKey.MyProfileVisibilityDescription to "公開：任何人均可搜尋並加您為好友。\n私人：搜尋時將無法找到您。",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "忽略單字",
    UiTextKey.ShareDismissWordMessage to "確定要忽略此共享單字？此操作無法復原。",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "語言：{language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "忽略",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "已連接或待處理",
    UiTextKey.AccessibilityNewMessages to "新訊息",
    UiTextKey.AccessibilityNewReleasesIcon to "新項目指示器",
    UiTextKey.AccessibilitySuccessIcon to "成功",
    UiTextKey.AccessibilityErrorIcon to "錯誤",
    UiTextKey.AccessibilitySharedItemTypeIcon to "共享項目類型",
    UiTextKey.AccessibilityAddCustomWords to "新增自訂單字",
    UiTextKey.AccessibilityWordBankExists to "單字庫已存在",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.回饋",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "通知設定",
    UiTextKey.FriendsNotifSettingsTitle to "通知偏好設定",
    UiTextKey.FriendsNotifNewMessages to "新聊天訊息",
    UiTextKey.FriendsNotifFriendRequests to "收到好友請求",
    UiTextKey.FriendsNotifRequestAccepted to "好友請求已接受",
    UiTextKey.FriendsNotifSharedInbox to "新共享收件匣項目",
    UiTextKey.FriendsNotifCloseButton to "完成",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "應用程式內徽章（紅點）",
    UiTextKey.InAppBadgeMessages to "未讀聊天訊息徽章",
    UiTextKey.InAppBadgeFriendRequests to "待處理好友請求徽章",
    UiTextKey.InAppBadgeSharedInbox to "未讀共享收件匣徽章",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "請登入以繼續。",
    UiTextKey.ErrorSaveFailedRetry to "儲存失敗，請再試一次。",
    UiTextKey.ErrorLoadFailedRetry to "載入失敗，請再試一次。",
    UiTextKey.ErrorNetworkRetry to "網路錯誤，請檢查您的連線並再試一次。",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "還需 {needed} 筆翻譯才能生成教材",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "切換至即時對話 →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "清除聊天",
    UiTextKey.ChatClearConversationTitle to "清除對話",
    UiTextKey.ChatClearConversationMessage to "隱藏此對話中的所有訊息？即使您離開並重新進入聊天室，訊息仍將對您永久隱藏。對方不受影響。",
    UiTextKey.ChatClearConversationConfirm to "全部清除",
    UiTextKey.ChatClearConversationSuccess to "對話已清除",

    // Block user
    UiTextKey.BlockUserButton to "封鎖",
    UiTextKey.BlockUserTitle to "封鎖用戶？",
    UiTextKey.BlockUserMessage to "封鎖 {username}？對方將從您的好友名單中移除，且無法再聯繫您。",
    UiTextKey.BlockUserConfirm to "封鎖",
    UiTextKey.BlockUserSuccess to "用戶已封鎖並從好友中移除。",
    UiTextKey.BlockedUsersTitle to "已封鎖用戶",
    UiTextKey.BlockedUsersEmpty to "沒有已封鎖的用戶。",
    UiTextKey.UnblockUserButton to "解除封鎖",
    UiTextKey.UnblockUserTitle to "解除封鎖？",
    UiTextKey.UnblockUserMessage to "解除封鎖 {username}？對方將可以再次發送好友請求。",
    UiTextKey.UnblockUserSuccess to "用戶已解除封鎖。",
    UiTextKey.BlockedUsersManageButton to "管理已封鎖用戶",

    // Friend request note (new)
    UiTextKey.FriendsRequestNoteLabel to "請求備註（可選）",
    UiTextKey.FriendsRequestNotePlaceholder to "新增一個簡短備註...",

    // Generation banners (new)
    UiTextKey.GenerationBannerSheet to "學習表已就緒！點擊開啟。",
    UiTextKey.GenerationBannerWordBank to "單字庫已就緒！點擊查看。",
    UiTextKey.GenerationBannerQuiz to "測驗已就緒！點擊開始。",

    // Notification settings quick link (new)
    UiTextKey.NotifSettingsQuickLink to "通知",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "繁體中文",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "好友系統",
    UiTextKey.HelpFriendSystemBody to "• 透過用戶名或用戶 ID 搜尋並新增好友\n" +
            "• 發送、接受或拒絕好友請求\n" +
            "• 與好友即時聊天並翻譯對話\n" +
            "• 與好友分享單字和學習教材\n" +
            "• 共享收件匣可接收和管理好友傳送的項目\n" +
            "• 好友卡片或收件匣上的紅點 (●) 表示有未讀訊息或新項目\n" +
            "• 向下拉動以重新整理好友名單和請求",
    UiTextKey.HelpProfileVisibilityTitle to "個人資料可見度",
    UiTextKey.HelpProfileVisibilityBody to "• 您可以在「我的個人資料」設定中將個人資料設為公開或私人\n" +
            "• 公開：任何用戶均可搜尋您並發送好友請求\n" +
            "• 私人：您的個人資料不會出現在搜尋結果中\n" +
            "• 即使設為私人，仍可透過分享用戶 ID 新增好友",
    UiTextKey.HelpColorPalettesTitle to "色彩主題與硬幣",
    UiTextKey.HelpColorPalettesBody to "• 1 個免費主題：天藍（預設）\n" +
            "• 10 個可解鎖主題，每個需 10 枚硬幣\n" +
            "• 完成測驗即可獲得硬幣\n" +
            "• 硬幣可用於解鎖色彩主題或擴充歷史紀錄上限\n" +
            "• 自動主題：早上 6 點至下午 6 點為淺色模式，下午 6 點至早上 6 點為深色模式",
    UiTextKey.HelpPrivacyTitle to "隱私與資料",
    UiTextKey.HelpPrivacyBody to "• 音訊僅用於語音辨識，不會永久儲存\n" +
            "• OCR 處理在裝置端進行（隱私優先）\n" +
            "• 您可以隨時刪除帳號及所有資料\n" +
            "• 將個人資料設為私人可防止他人透過搜尋找到您\n" +
            "• 所有資料透過 Firebase 安全同步",
    UiTextKey.HelpAppVersionTitle to "應用程式版本",
    UiTextKey.HelpAppVersionNotes to "• 歷史紀錄上限為 30–60 筆（可使用硬幣擴充）\n" +
            "• 用戶名必須唯一——更改後舊名稱即釋出\n" +
            "• 應用程式版本更新時將自動登出以確保安全\n" +
            "• 所有翻譯由 Azure AI 服務提供支援",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "即時翻譯",
    UiTextKey.OnboardingPage1Desc to "快速翻譯適合短句，即時對話適合多輪對話。",
    UiTextKey.OnboardingPage2Title to "學習詞彙",
    UiTextKey.OnboardingPage2Desc to "根據您的翻譯歷史生成詞彙表和測驗。",
    UiTextKey.OnboardingPage3Title to "與好友連結",
    UiTextKey.OnboardingPage3Desc to "聊天、分享詞彙，一起學習。",
    UiTextKey.OnboardingSkipButton to "跳過",
    UiTextKey.OnboardingNextButton to "下一步",
    UiTextKey.OnboardingGetStartedButton to "開始使用",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 歡迎回來，{name}！",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "用戶名：",
    UiTextKey.ChatUserIdLabel to "用戶 ID：",
    UiTextKey.ChatLearningLabel to "學習語言：",
    UiTextKey.ChatBlockedMessage to "您無法向此用戶發送訊息。",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "搜尋",
    UiTextKey.CustomWordsEmptyState to "尚無自訂單字",
    UiTextKey.CustomWordsEmptyHint to "點擊 + 新增您的單字",
    UiTextKey.CustomWordsNoSearchResults to "無符合搜尋的單字",
    UiTextKey.AddCustomWordHintTemplate to "在 {from} 中輸入單字，並在 {to} 中輸入翻譯",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} 筆紀錄",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID：{id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "電子郵件：{email}",
    UiTextKey.ProfileUsernameHintFull to "供好友搜尋的用戶名（3-20 個字元，字母/數字/_）",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "此語言沒有可用的語音選項",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "應用程式已更新，請重新登入",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "最愛上限已達",
    UiTextKey.FavoritesLimitMessage to "您最多可以儲存 20 個最愛項目，請先移除部分最愛再新增。",
    UiTextKey.FavoritesLimitGotIt to "好的",
    UiTextKey.FavoritesInfoTitle to "關於最愛",
    UiTextKey.FavoritesInfoMessage to "您可以儲存最多 20 個最愛項目（包括紀錄和對話），此限制有助於減少資料庫讀取並維持應用程式的流暢度。如需新增更多，請先移除現有的最愛。",
    UiTextKey.FavoritesInfoGotIt to "明白了",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "無法變更語言",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "主要語言每 30 天只能變更一次。請在 {days} 天後再試。",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "主要語言每 30 天只能變更一次。請在 {hours} 小時後再試。",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "確認變更語言",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "變更主要語言後，需等待 30 天才能再次變更。是否繼續？",

    // Bottom navigation
    UiTextKey.NavHome to "首頁",
    UiTextKey.NavTranslate to "翻譯",
    UiTextKey.NavLearn to "學習",
    UiTextKey.NavFriends to "好友",
    UiTextKey.NavSettings to "設定",

    // Permissions
    UiTextKey.CameraPermissionTitle to "需要相機權限",
    UiTextKey.CameraPermissionMessage to "此功能需要相機存取權限以拍攝影像進行文字辨識，請授予相機權限以繼續。",
    UiTextKey.CameraPermissionGrant to "授予權限",
    UiTextKey.MicPermissionMessage to "需要麥克風權限以進行語音辨識，請授予權限。",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "刪除 {count} 個已選項目？此操作無法復原。",
    UiTextKey.WordBankDeleteConfirm to "確定要刪除「{word}」嗎？",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "全部接受",
    UiTextKey.FriendsRejectAllButton to "全部拒絕",
    UiTextKey.ChatBlockedCannotSend to "無法傳送訊息",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "解鎖 {name}？",
    UiTextKey.ShopUnlockCost to "費用：{cost} 枚硬幣",
    UiTextKey.ShopYourCoins to "您的硬幣：{coins}",
    UiTextKey.ShopUnlockButton to "解鎖",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "主要語言",
    UiTextKey.HelpPrimaryLanguageBody to "• 主要語言用於學習表的說明和建議\n" +
            "• 每 30 天只能變更一次以維持學習一致性\n" +
            "• 您可以在設定中變更主要語言\n" +
            "• 主要語言設定為全域設定，適用於所有畫面",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 提示：為獲得最佳辨識效果，請將「來源語言」設定為您掃描的文字語言。",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "無法變更用戶名",
    UiTextKey.SettingsUsernameCooldownMessage to "用戶名每 30 天只能變更一次。請在 {days} 天後再試。",
    UiTextKey.SettingsUsernameCooldownMessageHours to "用戶名每 30 天只能變更一次。請在 {hours} 小時後再試。",
    UiTextKey.SettingsUsernameConfirmTitle to "確認變更用戶名",
    UiTextKey.SettingsUsernameConfirmMessage to "變更用戶名後，需等待 30 天才能再次變更。是否繼續？",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "無網路連線。請檢查網路後重試。",
    UiTextKey.ErrorPermissionDenied to "您沒有權限執行此操作。",
    UiTextKey.ErrorSessionExpired to "您的工作階段已過期，請重新登入。",
    UiTextKey.ErrorItemNotFound to "找不到請求的項目，可能已被刪除。",
    UiTextKey.ErrorAccessDenied to "拒絕存取。",
    UiTextKey.ErrorAlreadyFriends to "您已經和此用戶是好友了。",
    UiTextKey.ErrorUserBlocked to "無法完成此操作，該用戶可能已被封鎖。",
    UiTextKey.ErrorRequestNotFound to "此好友邀請已不存在。",
    UiTextKey.ErrorRequestAlreadyHandled to "此邀請已由其他人處理。",
    UiTextKey.ErrorNotAuthorized to "您無權執行此操作。",
    UiTextKey.ErrorRateLimited to "請求過於頻繁，請稍後再試。",
    UiTextKey.ErrorInvalidInput to "輸入無效，請檢查後重試。",
    UiTextKey.ErrorOperationNotAllowed to "目前無法完成此操作。",
    UiTextKey.ErrorTimeout to "操作逾時，請重試。",
    UiTextKey.ErrorSendMessageFailed to "傳送訊息失敗，請重試。",
    UiTextKey.ErrorFriendRequestSent to "好友邀請已發送！",
    UiTextKey.ErrorFriendRequestFailed to "發送好友邀請失敗，請重試。",
    UiTextKey.ErrorFriendRemoved to "好友已成功移除。",
    UiTextKey.ErrorFriendRemoveFailed to "無法移除好友，請檢查網路連線後重試。",
    UiTextKey.ErrorBlockSuccess to "已成功封鎖用戶。",
    UiTextKey.ErrorBlockFailed to "封鎖用戶失敗，請重試。",
    UiTextKey.ErrorUnblockSuccess to "已解除封鎖用戶。",
    UiTextKey.ErrorUnblockFailed to "解除封鎖失敗，請重試。",
    UiTextKey.ErrorAcceptRequestSuccess to "已接受好友邀請！",
    UiTextKey.ErrorAcceptRequestFailed to "接受好友邀請失敗，請重試。",
    UiTextKey.ErrorRejectRequestSuccess to "已拒絕好友邀請。",
    UiTextKey.ErrorRejectRequestFailed to "拒絕好友邀請失敗，請重試。",
    UiTextKey.ErrorOfflineMessage to "您目前離線，部分功能可能無法使用。",
    UiTextKey.ErrorChatDeletionFailed to "無法刪除對話，請重試。",
    UiTextKey.ErrorGenericRetry to "發生錯誤，請重試。",
)
