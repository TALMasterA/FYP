package com.example.fyp.model.ui

/**
 * Simplified Chinese (zh-CN) UI text map — 简体中文界面文字。
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val ZhCnUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "使用麦克风",
    UiTextKey.CopyButton to "复制",
    UiTextKey.SpeakScriptButton to "朗读原文",
    UiTextKey.TranslateButton to "翻译",
    UiTextKey.CopyTranslationButton to "复制翻译",
    UiTextKey.SpeakTranslationButton to "朗读翻译",
    UiTextKey.RecognizingStatus to "录音中...请讲话，等待自动停止。",
    UiTextKey.TranslatingStatus to "翻译中...",
    UiTextKey.SpeakingOriginalStatus to "正在朗读原文...",
    UiTextKey.SpeakingTranslationStatus to "正在朗读翻译...",
    UiTextKey.SpeakingLabel to "朗读中",
    UiTextKey.FinishedSpeakingOriginal to "原文朗读完毕",
    UiTextKey.FinishedSpeakingTranslation to "翻译朗读完毕",
    UiTextKey.TtsErrorTemplate to "语音错误：%s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "界面语言",
    UiTextKey.DetectLanguageLabel to "检测语言",
    UiTextKey.TranslateToLabel to "翻译至",

    // Language names
    UiTextKey.LangEnUs to "英语",
    UiTextKey.LangZhHk to "粤语",
    UiTextKey.LangJaJp to "日语",
    UiTextKey.LangZhCn to "简体中文",
    UiTextKey.LangFrFr to "法语",
    UiTextKey.LangDeDe to "德语",
    UiTextKey.LangKoKr to "韩语",
    UiTextKey.LangEsEs to "西班牙语",
    UiTextKey.LangIdId to "印尼语",
    UiTextKey.LangViVn to "越南语",
    UiTextKey.LangThTh to "泰语",
    UiTextKey.LangFilPh to "菲律宾语",
    UiTextKey.LangMsMy to "马来语",
    UiTextKey.LangPtBr to "葡萄牙语",
    UiTextKey.LangItIt to "意大利语",
    UiTextKey.LangRuRu to "俄语",

    // Navigation
    UiTextKey.NavHistory to "历史记录",
    UiTextKey.NavLogin to "登录",
    UiTextKey.NavLogout to "退出",
    UiTextKey.NavBack to "返回",
    UiTextKey.ActionCancel to "取消",
    UiTextKey.ActionDelete to "删除",
    UiTextKey.ActionOpen to "打开",
    UiTextKey.ActionName to "命名",
    UiTextKey.ActionSave to "保存",
    UiTextKey.ActionConfirm to "确认",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "在此输入或使用麦克风...",
    UiTextKey.SpeechTranslatedPlaceholder to "翻译结果将在此显示...",
    UiTextKey.StatusAzureErrorTemplate to "Azure 错误：%s",
    UiTextKey.StatusTranslationErrorTemplate to "翻译错误：%s",
    UiTextKey.StatusLoginRequiredTranslation to "请登录后使用翻译功能",
    UiTextKey.StatusRecognizePreparing to "正在准备麦克风...（请勿讲话）",
    UiTextKey.StatusRecognizeListening to "正在聆听...请讲话。",

    // Pagination
    UiTextKey.PaginationPrevLabel to "上一页",
    UiTextKey.PaginationNextLabel to "下一页",
    UiTextKey.PaginationPageLabelTemplate to "第 {page} / {total} 页",

    // Toast
    UiTextKey.ToastCopied to "已复制",
    UiTextKey.DisableText to "需要登录后才能使用翻译功能和保存翻译记录。",

    // Error
    UiTextKey.ErrorRetryButton to "重试",
    UiTextKey.ErrorGenericMessage to "出了问题，请重试。",

    // Shop
    UiTextKey.ShopTitle to "商店",
    UiTextKey.ShopCoinBalance to "您的金币",
    UiTextKey.ShopHistoryExpansionTitle to "扩展历史记录上限",
    UiTextKey.ShopHistoryExpansionDesc to "扩展历史记录查看上限，查看更多近期翻译记录。",
    UiTextKey.ShopCurrentLimit to "当前上限：{limit} 条",
    UiTextKey.ShopMaxLimit to "最高上限：",
    UiTextKey.ShopBuyHistoryExpansion to "购买（+{increment} 条，{cost} 枚金币）",
    UiTextKey.ShopInsufficientCoins to "金币不足",
    UiTextKey.ShopMaxLimitReached to "已达最高上限",
    UiTextKey.ShopHistoryExpandedTitle to "扩展成功！",
    UiTextKey.ShopHistoryExpandedMessage to "历史记录上限已扩展至 {limit} 条！现在可以查看更多翻译记录了！",
    UiTextKey.ShopColorPaletteTitle to "颜色主题",
    UiTextKey.ShopColorPaletteDesc to "选择应用程序颜色主题，每个新主题 10 枚金币",
    UiTextKey.ShopEntry to "商店",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "语音设置",
    UiTextKey.VoiceSettingsDesc to "为每种语言选择文字转语音的声音。",

    // Instructions
    UiTextKey.SpeechInstructions to "点击麦克风按钮开始语音识别，完成后点击翻译。如果修改文字或语言后自动检测似乎未更新，请点击右上角刷新再试一次。",
    UiTextKey.HomeInstructions to "选择功能开始使用。",
    UiTextKey.ContinuousInstructions to "选择两种语言，然后开始对话模式。",

    // Home
    UiTextKey.HomeTitle to "即时翻译",
    UiTextKey.HelpTitle to "帮助",
    UiTextKey.SpeechTitle to "快速翻译",
    UiTextKey.HomeStartButton to "开始翻译",
    UiTextKey.HomeFeaturesTitle to "功能介绍",
    UiTextKey.HomeDiscreteDescription to "短句和语音翻译",
    UiTextKey.HomeContinuousDescription to "实时双向对话翻译",
    UiTextKey.HomeLearningDescription to "从翻译记录生成学习材料和测验",

    // Help
    UiTextKey.HelpCurrentTitle to "当前功能",
    UiTextKey.HelpCautionTitle to "注意事项",
    UiTextKey.HelpCurrentFeatures to "当前功能：\n" +
            "  • 快速翻译：语音识别后翻译\n" +
            "  • 实时对话：双向语音翻译\n" +
            "  • 历史记录：查看翻译记录\n" +
            "  • 学习材料：从记录生成词汇和测验\n\n" +
            "翻译：\n" +
            "  • 使用 Azure AI 语音识别\n" +
            "  • 使用 Azure 翻译服务\n",
    UiTextKey.HelpCaution to "注意事项：\n" +
            "  • 语音识别需要网络连接\n" +
            "  • 本地翻译缓存在离线时也可使用\n" +
            "  • 重要翻译请用专业服务核实\n\n" +
            "账号和数据：\n" +
            "  • 历史记录、学习和金币功能需要登录\n" +
            "  • 用户数据安全存储在 Firebase Firestore\n\n" +
            "排除故障：\n" +
            "  • 如果完成所有步骤后某个功能仍无法使用，请重启应用再试\n",
    UiTextKey.HelpNotesTitle to "备注",
    UiTextKey.HelpNotes to "💡 使用技巧和排除故障：\n\n" +
            "获得最佳翻译效果：\n" +
            "  • 清晰、适中的语速讲话\n" +
            "  • 减少背景噪音以提高识别准确度\n" +
            "  • 快速翻译模式适合短句翻译\n\n" +
            "界面语言：\n" +
            "  • 基础语言为英语；其他界面语言为 AI 翻译\n" +
            "  • 粤语版本为手动翻译，准确度更高\n" +
            "更新和反馈：\n" +
            "  • 应用版本显示在设置 → 关于\n" +
            "  • 请在设置 → 反馈中提交意见，或在 GitHub 上报告问题\n",

    // Feedback
    UiTextKey.FeedbackTitle to "反馈",
    UiTextKey.FeedbackDesc to "感谢您的反馈！请分享您的建议、错误报告或对应用的评价。",
    UiTextKey.FeedbackMessagePlaceholder to "请在此输入您的反馈...",
    UiTextKey.FeedbackSubmitButton to "提交反馈",
    UiTextKey.FeedbackSubmitting to "提交中...",
    UiTextKey.FeedbackSuccessTitle to "谢谢您！",
    UiTextKey.FeedbackSuccessMessage to "您的反馈已成功提交，感谢您的宝贵意见！",
    UiTextKey.FeedbackErrorTitle to "提交失败",
    UiTextKey.FeedbackErrorMessage to "提交反馈失败，请检查网络连接后再试。",
    UiTextKey.FeedbackMessageRequired to "请输入反馈内容。",

    // Continuous mode
    UiTextKey.ContinuousTitle to "实时对话",
    UiTextKey.ContinuousStartButton to "开始对话",
    UiTextKey.ContinuousStopButton to "停止录音",
    UiTextKey.ContinuousStartScreenButton to "实时对话",
    UiTextKey.ContinuousPersonALabel to "A 在讲话",
    UiTextKey.ContinuousPersonBLabel to "B 在讲话",
    UiTextKey.ContinuousCurrentStringLabel to "当前文字：",
    UiTextKey.ContinuousSpeakerAName to "人物 A",
    UiTextKey.ContinuousSpeakerBName to "人物 B",
    UiTextKey.ContinuousTranslationSuffix to "·翻译",
    UiTextKey.ContinuousPreparingMicText to "正在准备麦克风...（请勿讲话）",
    UiTextKey.ContinuousTranslatingText to "翻译中...",

    // History
    UiTextKey.HistoryTitle to "历史记录",
    UiTextKey.HistoryTabDiscrete to "快速翻译",
    UiTextKey.HistoryTabContinuous to "实时对话",
    UiTextKey.HistoryNoContinuousSessions to "暂无对话记录。",
    UiTextKey.HistoryNoDiscreteRecords to "暂无翻译记录。",
    UiTextKey.DialogDeleteRecordTitle to "删除记录？",
    UiTextKey.DialogDeleteRecordMessage to "此操作无法撤销。",
    UiTextKey.DialogDeleteSessionTitle to "删除对话？",
    UiTextKey.DialogDeleteSessionMessage to "此对话中所有记录将被删除，操作无法撤销。",
    UiTextKey.HistoryDeleteSessionButton to "删除",
    UiTextKey.HistoryNameSessionTitle to "命名",
    UiTextKey.HistorySessionNameLabel to "对话名称",
    UiTextKey.HistorySessionTitleTemplate to "对话 {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} 条记录",

    // Filter
    UiTextKey.FilterDropdownDefault to "所有语言",
    UiTextKey.FilterTitle to "筛选历史记录",
    UiTextKey.FilterLangDrop to "语言",
    UiTextKey.FilterKeyword to "关键词",
    UiTextKey.FilterApply to "应用",
    UiTextKey.FilterCancel to "取消",
    UiTextKey.FilterClear to "清除",
    UiTextKey.FilterHistoryScreenTitle to "筛选",

    // Auth
    UiTextKey.AuthLoginTitle to "登录",
    UiTextKey.AuthRegisterTitle to "注册（已停用）",
    UiTextKey.AuthLoginHint to "请使用您已注册的邮箱和密码。",
    UiTextKey.AuthRegisterRules to "开发阶段已停用注册功能。\n注意：如果使用不存在的邮箱，将无法重置密码。\n" +
            "注册规则：\n" +
            "• 邮箱格式需正确（例如 name@example.com）\n" +
            "• 密码至少 6 个字符\n" +
            "• 确认密码需与密码一致",
    UiTextKey.AuthEmailLabel to "邮箱",
    UiTextKey.AuthPasswordLabel to "密码",
    UiTextKey.AuthConfirmPasswordLabel to "确认密码",
    UiTextKey.AuthLoginButton to "登录",
    UiTextKey.AuthRegisterButton to "注册",
    UiTextKey.AuthToggleToRegister to "没有账号？注册（已停用）",
    UiTextKey.AuthToggleToLogin to "已有账号？登录",
    UiTextKey.AuthErrorPasswordsMismatch to "密码不一致。",
    UiTextKey.AuthErrorPasswordTooShort to "密码至少需要 6 个字符。",
    UiTextKey.AuthRegistrationDisabled to "开发阶段已停用注册功能。",
    UiTextKey.AuthResetEmailSent to "重置邮件已发送（如果邮箱真实存在）。请查收您的收件箱。",

    // Password reset
    UiTextKey.ForgotPwText to "忘记密码？点此重置",
    UiTextKey.ResetPwTitle to "重置密码",
    UiTextKey.ResetPwText to "输入您账号的邮箱，我们将发送重置链接。\n请确保该邮箱已在应用中注册，否则不会收到邮件。\n",
    UiTextKey.ResetSendingText to "发送中...",
    UiTextKey.ResetSendText to "发送重置邮件",

    // Settings
    UiTextKey.SettingsTitle to "设置",
    UiTextKey.SettingsPrimaryLanguageTitle to "主要语言",
    UiTextKey.SettingsPrimaryLanguageDesc to "用于学习说明和建议",
    UiTextKey.SettingsPrimaryLanguageLabel to "主要语言",
    UiTextKey.SettingsFontSizeTitle to "字体大小",
    UiTextKey.SettingsFontSizeDesc to "调整文字大小以提升阅读体验（跨设备同步）",
    UiTextKey.SettingsScaleTemplate to "缩放：{pct}%",
    UiTextKey.SettingsColorPaletteTitle to "颜色主题",
    UiTextKey.SettingsColorPaletteDesc to "选择应用颜色主题，每个新主题 10 枚金币",
    UiTextKey.SettingsColorCostTemplate to "{cost} 枚金币",
    UiTextKey.SettingsColorUnlockButton to "解锁",
    UiTextKey.SettingsColorSelectButton to "选择",
    UiTextKey.SettingsColorAlreadyUnlocked to "已解锁",
    UiTextKey.SettingsPreviewHeadline to "标题：大字预览",
    UiTextKey.SettingsPreviewBody to "正文：普通文字预览",
    UiTextKey.SettingsPreviewLabel to "标签：小字预览",
    UiTextKey.SettingsAboutTitle to "关于",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "登录后，您的设置会自动保存并同步到账号。",
    UiTextKey.SettingsThemeTitle to "主题",
    UiTextKey.SettingsThemeDesc to "选择外观应用方式：跟随系统、浅色、深色或定时。",
    UiTextKey.SettingsThemeSystem to "跟随系统",
    UiTextKey.SettingsThemeLight to "浅色",
    UiTextKey.SettingsThemeDark to "深色",
    UiTextKey.SettingsThemeScheduled to "定时",
    UiTextKey.SettingsResetPW to "点此重置密码",
    UiTextKey.SettingsQuickLinks to "详细设置",
    UiTextKey.SettingsNotLoggedInWarning to "请先登录以使用账号设置。您仍然可以更改应用语言。",
    UiTextKey.SettingsVoiceTitle to "语音设置",
    UiTextKey.SettingsVoiceDesc to "为每种语言选择文字转语音的声音。",
    UiTextKey.SettingsVoiceLanguageLabel to "语言",
    UiTextKey.SettingsVoiceSelectLabel to "声音",
    UiTextKey.SettingsVoiceDefault to "默认",

    // Learning
    UiTextKey.LearningTitle to "学习",
    UiTextKey.LearningHintCount to "(*) 次数 = 包含该语言的历史翻译记录数。",
    UiTextKey.LearningErrorTemplate to "错误：%s",
    UiTextKey.LearningGenerate to "生成",
    UiTextKey.LearningRegenerate to "重新生成",
    UiTextKey.LearningGenerating to "生成中...",
    UiTextKey.LearningOpenSheetTemplate to "{speclanguage} 学习表",
    UiTextKey.LearningSheetTitleTemplate to "{speclanguage} 学习表",
    UiTextKey.LearningSheetPrimaryTemplate to "主要语言：{speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "当前记录数：{nowCount}（生成时：{savedCount}）",
    UiTextKey.LearningSheetNoContent to "暂无学习表内容。",
    UiTextKey.LearningSheetRegenerate to "重新生成",
    UiTextKey.LearningSheetGenerating to "生成中...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 这是什么？",
    UiTextKey.LearningSheetWhatIsThisDesc to "这是根据您的翻译记录量身生成的学习表，包含词汇、含义、例句和语法要点，帮助您学习。点击下方的测验按钮来测试您的知识！",
    UiTextKey.LearningRegenBlockedTitle to "目前无法重新生成",
    UiTextKey.LearningRegenBlockedMessage to "重新生成需要比上次生成多至少 5 条记录，您还需要 {needed} 条。",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ 还需 {needed} 条记录才能重新生成（至少 5 条）",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ 记录数需高于上次生成时的数量",
    UiTextKey.LearningRegenInfoTitle to "重新生成规则",
    UiTextKey.LearningRegenInfoMessage to "要重新生成学习材料：\n\n• 首次生成：随时可以\n• 重新生成：需比上次多至少 5 条翻译记录\n\n当您有足够的新记录时，按钮会启用（蓝色）。若按钮为灰色，请继续翻译以解锁重新生成！\n\n💡 提示：翻译后如果数量未更新，请重启应用刷新。",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ 此版本材料已生成测验，请生成新的学习表以创建新测验。",

    // Quiz
    UiTextKey.QuizTitleTemplate to "测验：{language}",
    UiTextKey.QuizOpenButton to "📝 测验",
    UiTextKey.QuizGenerateButton to "🔄 生成测验",
    UiTextKey.QuizGenerating to "⏳ 生成中...",
    UiTextKey.QuizUpToDate to "✓ 最新版本",
    UiTextKey.QuizBlocked to "🚫 已阻止",
    UiTextKey.QuizWait to "⏳ 请等待...",
    UiTextKey.QuizMaterialsQuizTemplate to "材料：{materials} | 测验：{quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 可以获得金币！",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 还需 {count} 条记录才能获得金币",
    UiTextKey.QuizCancelButton to "取消",
    UiTextKey.QuizPreviousButton to "上一题",
    UiTextKey.QuizNextButton to "下一题",
    UiTextKey.QuizSubmitButton to "提交",
    UiTextKey.QuizRetakeButton to "重新测验",
    UiTextKey.QuizBackButton to "返回",
    UiTextKey.QuizLoadingText to "加载测验中...",
    UiTextKey.QuizGeneratingText to "生成测验中...",
    UiTextKey.QuizNoMaterialsTitle to "未找到学习材料",
    UiTextKey.QuizNoMaterialsMessage to "请返回先生成学习材料，再查看测验。",
    UiTextKey.QuizErrorTitle to "⚠️ 测验错误",
    UiTextKey.QuizErrorSuggestion to "建议：使用上方按钮生成测验。",
    UiTextKey.QuizCompletedTitle to "测验完成！",
    UiTextKey.QuizAnswerReviewTitle to "答案回顾",
    UiTextKey.QuizYourAnswerTemplate to "您的答案：{Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "正确答案：{Answer}",
    UiTextKey.QuizQuestionTemplate to "第 {current} 题，共 {total} 题",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ 无法重新生成：材料（{materials}）< 测验（{quiz}），请添加更多翻译。",
    UiTextKey.QuizAnotherGenInProgress to "⏳ 另一个生成任务进行中，请稍等。",
    UiTextKey.QuizCoinRulesTitle to "🪙 金币获取规则",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ 如何获取：",
    UiTextKey.QuizCoinRulesRequirements to "条件：",
    UiTextKey.QuizCoinRulesCurrentStatus to "当前状态：",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ 下次测验可以获得金币！",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• 还需 {count} 条记录才能获得金币",
    UiTextKey.QuizCoinRule1Coin to "• 每题答对获得 1 枚金币",
    UiTextKey.QuizCoinRuleFirstAttempt to "• 每个测验版本仅首次作答有效",
    UiTextKey.QuizCoinRuleMatchMaterials to "• 测验需与材料版本匹配",
    UiTextKey.QuizCoinRulePlus10 to "• 需比上次获得金币的测验多 10 条以上记录",
    UiTextKey.QuizCoinRuleNoDelete to "• 删除历史记录不能重新获得金币",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• 材料：{count} 条记录",
    UiTextKey.QuizCoinRuleQuizTemplate to "• 测验：{count} 条记录",
    UiTextKey.QuizCoinRuleGotIt to "明白了！",
    UiTextKey.QuizRegenConfirmTitle to "🔄 生成新测验？",
    UiTextKey.QuizRegenCanEarnCoins to "✅ 此测验可以获得金币！（仅限首次作答）",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ 此测验目前无法获得金币。",
    UiTextKey.QuizRegenNeedMoreTemplate to "还需 {count} 条翻译记录才符合金币资格（比上次获得金币的测验多至少 10 条）。",
    UiTextKey.QuizRegenReminder to "提示：您仍然可以练习和重做测验，但金币仅在首次作答且有足够新记录时发放。",
    UiTextKey.QuizRegenGenerateButton to "生成",
    UiTextKey.QuizCoinsEarnedTitle to "✨ 获得金币！",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "恭喜！您获得了 {Coins} 枚金币！",
    UiTextKey.QuizCoinsRule1 to "• 仅首次作答每题答对获得 1 枚金币",
    UiTextKey.QuizCoinsRule2 to "• 重做相同测验不获得金币",
    UiTextKey.QuizCoinsRule3 to "• 新测验需比上次多 10 条以上记录",
    UiTextKey.QuizCoinsRule4 to "• 测验需与当前材料版本匹配",
    UiTextKey.QuizCoinsRule5 to "• 在历史记录页面查看总金币数",
    UiTextKey.QuizCoinsGreatButton to "太好了！",
    UiTextKey.QuizOutdatedMessage to "此测验基于旧版学习表。",
    UiTextKey.QuizRecordsLabel to "条记录",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 您的金币",
    UiTextKey.HistoryCoinRulesTitle to "金币获取规则：",
    UiTextKey.HistoryCoinHowToEarnTitle to "如何获取：",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 每题答对获得 1 枚金币",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• 仅每个测验版本的首次作答有效",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• 测验需与当前学习材料匹配",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 防作弊规则：",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• 比上次获得金币的测验需多 10 条以上新翻译才能再次获取",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• 测验版本需与材料版本相同",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• 删除历史记录会阻止测验重新生成（除非数量高于上次记录）",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• 重做相同测验不获得金币",
    UiTextKey.HistoryCoinTipsTitle to "💡 提示：",
    UiTextKey.HistoryCoinTipsRule1 to "• 定期添加更多翻译",
    UiTextKey.HistoryCoinTipsRule2 to "• 首次作答前好好学习！",
    UiTextKey.HistoryCoinGotItButton to "明白了！",

    // History info
    UiTextKey.HistoryInfoTitle to "历史记录说明",
    UiTextKey.HistoryInfoLimitMessage to "历史记录显示最近 {limit} 条，可以在商店扩展上限！",
    UiTextKey.HistoryInfoOlderRecordsMessage to "较早的记录仍然保存，但不显示以优化性能。",
    UiTextKey.HistoryInfoFavoritesMessage to "要永久保存重要翻译，请点击任意记录上的爱心 ❤️ 图标添加到收藏夹。",
    UiTextKey.HistoryInfoViewFavoritesMessage to "在设置 → 收藏夹中查看已保存的收藏记录。",
    UiTextKey.HistoryInfoFilterMessage to "使用筛选按钮在显示的 {limit} 条记录中搜索。",
    UiTextKey.HistoryInfoGotItButton to "明白了",

    // Word bank
    UiTextKey.WordBankTitle to "词库",
    UiTextKey.WordBankSelectLanguage to "选择语言以查看或生成词库：",
    UiTextKey.WordBankNoHistory to "无翻译记录",
    UiTextKey.WordBankNoHistoryHint to "开始翻译以建立您的词库！",
    UiTextKey.WordBankWordsCount to "个单词",
    UiTextKey.WordBankGenerating to "生成中...",
    UiTextKey.WordBankGenerate to "生成词库",
    UiTextKey.WordBankRegenerate to "重新生成词库",
    UiTextKey.WordBankRefresh to "🔄 刷新词库",
    UiTextKey.WordBankEmpty to "暂无词库",
    UiTextKey.WordBankEmptyHint to "点击上方按钮，从翻译历史生成词库。",
    UiTextKey.WordBankExample to "例句：",
    UiTextKey.WordBankDifficulty to "难度：",
    UiTextKey.WordBankFilterCategory to "类别",
    UiTextKey.WordBankFilterCategoryAll to "所有类别",
    UiTextKey.WordBankFilterDifficultyLabel to "难度等级：",
    UiTextKey.WordBankFilterNoResults to "没有符合筛选条件的单词",
    UiTextKey.WordBankRefreshAvailable to "✅ 可以刷新！",
    UiTextKey.WordBankRecordsNeeded to "条记录（需 20 条才能刷新）",
    UiTextKey.WordBankRegenInfoTitle to "刷新规则",
    UiTextKey.WordBankRegenInfoMessage to "要刷新您的词库：\n\n• 首次生成：随时可以\n• 刷新：需比上次生成多至少 20 条翻译记录\n\n当您有足够的新记录时，刷新按钮会启用（蓝色）。若按钮为灰色，请继续翻译以解锁刷新！\n\n💡 提示：翻译后如果数量未更新，请重启应用刷新。",
    UiTextKey.WordBankHistoryCountTemplate to "当前记录数：{nowCount}（生成时：{savedCount}）",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "退出登录？",
    UiTextKey.DialogLogoutMessage to "您需要重新登录才能使用翻译功能 / 保存和查看历史记录。",
    UiTextKey.DialogGenerateOverwriteTitle to "覆盖材料？",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "之前的材料将被覆盖（如果存在）。\n为 {speclanguage} 生成材料？",

    // Profile
    UiTextKey.ProfileTitle to "个人资料",
    UiTextKey.ProfileUsernameLabel to "用户名",
    UiTextKey.ProfileUsernameHint to "输入您的用户名",
    UiTextKey.ProfileUpdateButton to "更新个人资料",
    UiTextKey.ProfileUpdateSuccess to "个人资料更新成功",
    UiTextKey.ProfileUpdateError to "个人资料更新失败",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "删除账号",
    UiTextKey.AccountDeleteWarning to "⚠️ 此操作永久不可撤销！",
    UiTextKey.AccountDeleteConfirmMessage to "您所有的数据，包括历史记录、词库、学习材料和设置，将被永久删除。请输入密码确认。",
    UiTextKey.AccountDeletePasswordLabel to "密码",
    UiTextKey.AccountDeleteButton to "删除我的账号",
    UiTextKey.AccountDeleteSuccess to "账号删除成功",
    UiTextKey.AccountDeleteError to "账号删除失败",
    UiTextKey.AccountDeleteReauthRequired to "请重新输入密码以确认删除",

    // Favorites
    UiTextKey.FavoritesTitle to "收藏夹",
    UiTextKey.FavoritesEmpty to "暂无收藏",
    UiTextKey.FavoritesAddSuccess to "已添加到收藏夹",
    UiTextKey.FavoritesRemoveSuccess to "已从收藏夹移除",
    UiTextKey.FavoritesAddButton to "添加到收藏夹",
    UiTextKey.FavoritesRemoveButton to "从收藏夹移除",
    UiTextKey.FavoritesNoteLabel to "备注",
    UiTextKey.FavoritesNoteHint to "添加备注（可选）",
    UiTextKey.FavoritesTabRecords to "记录",
    UiTextKey.FavoritesTabSessions to "对话",
    UiTextKey.FavoritesSessionsEmpty to "暂无已保存的对话",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} 条消息",

    // Custom words
    UiTextKey.CustomWordsTitle to "自定义单词",
    UiTextKey.CustomWordsAdd to "添加单词",
    UiTextKey.CustomWordsEdit to "编辑单词",
    UiTextKey.CustomWordsDelete to "删除单词",
    UiTextKey.CustomWordsOriginalLabel to "原词",
    UiTextKey.CustomWordsTranslatedLabel to "翻译",
    UiTextKey.CustomWordsPronunciationLabel to "发音（可选）",
    UiTextKey.CustomWordsExampleLabel to "例句（可选）",
    UiTextKey.CustomWordsSaveSuccess to "单词保存成功",
    UiTextKey.CustomWordsDeleteSuccess to "单词删除成功",
    UiTextKey.CustomWordsAlreadyExists to "该单词已存在",
    UiTextKey.CustomWordsOriginalLanguageLabel to "原语言",
    UiTextKey.CustomWordsTranslationLanguageLabel to "翻译语言",
    UiTextKey.CustomWordsSaveButton to "保存",
    UiTextKey.CustomWordsCancelButton to "取消",

    // Language detection
    UiTextKey.LanguageDetectAuto to "自动检测",
    UiTextKey.LanguageDetectDetecting to "检测中...",
    UiTextKey.LanguageDetectedTemplate to "检测到：{language}",
    UiTextKey.LanguageDetectFailed to "检测失败",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "从图片扫描文字",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ 注意：图片文字识别可能不完全准确，请检查提取的文字。" +
            "支持拉丁字符（英语等）、中文、日文和韩文。",
    UiTextKey.ImageRecognitionScanning to "正在扫描图片文字...",
    UiTextKey.ImageRecognitionSuccess to "文字提取成功",

    // Cache
    UiTextKey.CacheClearButton to "清除缓存",
    UiTextKey.CacheClearSuccess to "缓存已成功清除",
    UiTextKey.CacheStatsTemplate to "缓存：已存储 {count} 条翻译",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "自动切换主题",
    UiTextKey.SettingsAutoThemeDesc to "根据时间自动在浅色和深色主题之间切换",
    UiTextKey.SettingsAutoThemeEnabled to "已启用",
    UiTextKey.SettingsAutoThemeDisabled to "已停用",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "深色模式开始时间：",
    UiTextKey.SettingsAutoThemeLightStartLabel to "浅色模式开始时间：",
    UiTextKey.SettingsAutoThemePreview to "主题将在预定时间自动切换",

    // Offline mode
    UiTextKey.OfflineModeTitle to "离线模式",
    UiTextKey.OfflineModeMessage to "您当前处于离线状态，正在浏览缓存数据。",
    UiTextKey.OfflineModeRetry to "重试连接",
    UiTextKey.OfflineDataCached to "缓存数据可用",
    UiTextKey.OfflineSyncPending to "联网后将同步更改",

    // Image capture
    UiTextKey.ImageSourceTitle to "选择图片来源",
    UiTextKey.ImageSourceCamera to "拍照",
    UiTextKey.ImageSourceGallery to "从相册选择",
    UiTextKey.ImageSourceCancel to "取消",
    UiTextKey.CameraCaptureContentDesc to "拍摄",

    // Friends
    UiTextKey.FriendsTitle to "好友",
    UiTextKey.FriendsMenuButton to "好友",
    UiTextKey.FriendsAddButton to "添加好友",
    UiTextKey.FriendsSearchTitle to "搜索用户",
    UiTextKey.FriendsSearchPlaceholder to "输入用户名或用户 ID...",
    UiTextKey.FriendsSearchMinChars to "请输入至少 2 个字符以搜索",
    UiTextKey.FriendsSearchNoResults to "未找到用户",
    UiTextKey.FriendsListEmpty to "添加好友以联系和分享学习材料。",
    UiTextKey.FriendsRequestsSection to "好友请求（{count}）",
    UiTextKey.FriendsSectionTitle to "好友（{count}）",
    UiTextKey.FriendsAcceptButton to "接受",
    UiTextKey.FriendsRejectButton to "拒绝",
    UiTextKey.FriendsRemoveButton to "移除",
    UiTextKey.FriendsRemoveDialogTitle to "移除好友",
    UiTextKey.FriendsRemoveDialogMessage to "确定要将 {username} 从好友列表中移除？",
    UiTextKey.FriendsSendRequestButton to "添加",
    UiTextKey.FriendsRequestSentSuccess to "好友请求已发送！",
    UiTextKey.FriendsRequestAcceptedSuccess to "好友请求已接受！",
    UiTextKey.FriendsRequestRejectedSuccess to "请求已拒绝",
    UiTextKey.FriendsRemovedSuccess to "好友已移除",
    UiTextKey.FriendsRequestFailed to "请求发送失败",
    UiTextKey.FriendsCloseButton to "关闭",
    UiTextKey.FriendsCancelButton to "取消",
    UiTextKey.FriendsRemoveConfirm to "移除",
    UiTextKey.FriendsNewRequestsTemplate to "您有 {count} 个新好友请求！",
    UiTextKey.FriendsSentRequestsSection to "已发送的请求（{count}）",
    UiTextKey.FriendsPendingStatus to "等待处理",
    UiTextKey.FriendsCancelRequestButton to "取消请求",
    UiTextKey.FriendsUnreadMessageDesc to "发送消息",
    UiTextKey.FriendsDeleteModeButton to "删除好友",
    UiTextKey.FriendsDeleteSelectedButton to "删除所选",
    UiTextKey.FriendsDeleteMultipleTitle to "移除好友",
    UiTextKey.FriendsDeleteMultipleMessage to "移除所选 {count} 位好友？",
    UiTextKey.FriendsSearchMinChars3 to "请输入至少 3 个字符以按用户名搜索",
    UiTextKey.FriendsSearchByUserIdHint to "或输入完整用户 ID 进行精确查询",
    UiTextKey.FriendsStatusAlreadyFriends to "已经是好友",
    UiTextKey.FriendsStatusRequestSent to "请求已发送 — 等待回复",
    UiTextKey.FriendsStatusRequestReceived to "该用户已向您发送请求",

    // Chat
    UiTextKey.ChatTitle to "与 {username} 聊天",
    UiTextKey.ChatInputPlaceholder to "输入消息...",
    UiTextKey.ChatSendButton to "发送",
    UiTextKey.ChatEmpty to "暂无消息，开始聊天吧！",
    UiTextKey.ChatMessageSent to "消息已发送",
    UiTextKey.ChatMessageFailed to "消息发送失败",
    UiTextKey.ChatMarkingRead to "标记为已读...",
    UiTextKey.ChatLoadingMessages to "加载消息中...",
    UiTextKey.ChatToday to "今天",
    UiTextKey.ChatYesterday to "昨天",
    UiTextKey.ChatUnreadBadge to "{count} 条未读",
    UiTextKey.ChatTranslateButton to "翻译",
    UiTextKey.ChatTranslateDialogTitle to "翻译对话",
    UiTextKey.ChatTranslateDialogMessage to "将好友的消息翻译成您的偏好语言？系统会检测每条消息的语言并进行翻译。",
    UiTextKey.ChatTranslateConfirm to "全部翻译",
    UiTextKey.ChatTranslating to "翻译消息中...",
    UiTextKey.ChatTranslated to "消息已翻译",
    UiTextKey.ChatShowOriginal to "显示原文",
    UiTextKey.ChatShowTranslation to "显示翻译",
    UiTextKey.ChatTranslateFailed to "翻译失败",
    UiTextKey.ChatTranslatedLabel to "已翻译",

    // Sharing
    UiTextKey.ShareTitle to "分享",
    UiTextKey.ShareInboxTitle to "共享收件箱",
    UiTextKey.ShareInboxEmpty to "暂无共享项目，好友可以与您分享单词和学习材料！",
    UiTextKey.ShareWordButton to "分享单词",
    UiTextKey.ShareMaterialButton to "分享材料",
    UiTextKey.ShareSelectFriendTitle to "选择好友",
    UiTextKey.ShareSelectFriendMessage to "选择要分享的好友：",
    UiTextKey.ShareSuccess to "分享成功！",
    UiTextKey.ShareFailed to "分享失败",
    UiTextKey.ShareWordWith to "与 {username} 分享单词",
    UiTextKey.ShareMaterialWith to "与 {username} 分享材料",
    UiTextKey.ShareAcceptButton to "接受",
    UiTextKey.ShareDismissButton to "忽略",
    UiTextKey.ShareAccepted to "已添加到您的收藏",
    UiTextKey.ShareDismissed to "项目已忽略",
    UiTextKey.ShareActionFailed to "操作失败",
    UiTextKey.ShareTypeWord to "单词",
    UiTextKey.ShareTypeLearningSheet to "学习表",
    UiTextKey.ShareReceivedFrom to "来自：{username}",
    UiTextKey.ShareNewItemsTemplate to "收到 {count} 个新项目！",
    UiTextKey.ShareViewFullMaterial to "点击「查看」以阅读完整材料",
    UiTextKey.ShareDeleteItemTitle to "删除项目",
    UiTextKey.ShareDeleteItemMessage to "确定要删除此共享项目？操作无法撤销。",
    UiTextKey.ShareDeleteButton to "删除",
    UiTextKey.ShareViewButton to "查看",
    UiTextKey.ShareItemNotFound to "未找到项目。",
    UiTextKey.ShareNoContent to "此材料没有可用内容。",
    UiTextKey.ShareSaveToSelf to "保存到自己的收件箱",
    UiTextKey.ShareSavedToSelf to "已保存到您的收件箱！",

    // My profile
    UiTextKey.MyProfileTitle to "我的个人资料",
    UiTextKey.MyProfileUserId to "用户 ID",
    UiTextKey.MyProfileUsername to "用户名",
    UiTextKey.MyProfileDisplayName to "显示名称",
    UiTextKey.MyProfileCopyUserId to "复制用户 ID",
    UiTextKey.MyProfileCopyUsername to "复制用户名",
    UiTextKey.MyProfileShare to "分享个人资料",
    UiTextKey.MyProfileCopied to "已复制到剪贴板！",
    UiTextKey.MyProfileLanguages to "语言",
    UiTextKey.MyProfilePrimaryLanguage to "主要语言",
    UiTextKey.MyProfileLearningLanguages to "学习语言",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "好友页面说明",
    UiTextKey.FriendsInfoMessage to "• 下拉以手动刷新好友列表、请求和好友状态。\n" +
            "• 点击好友卡片以打开聊天。\n" +
            "• 好友卡片上的红点（●）表示有未读消息，点击 ✓✓ 可一次全部标记为已读。\n" +
            "• 使用 📥 收件箱图标查看共享材料，点击旁边的 ✓✓ 可消除红点。\n" +
            "• 使用好友卡片上的 🚫 图标拉黑该用户——对方将从您的列表中移除，且无法再联系您。\n" +
            "• 拉黑用户后，您与对方的聊天记录也会被清除。\n" +
            "• 使用垃圾桶图标进入删除模式以移除好友。\n" +
            "• 移除好友后，你们之间的所有聊天消息也会被删除。\n" +
            "• 使用搜索按钮通过用户名或用户 ID 查找和添加新好友。\n" +
            "• 推送通知默认关闭——可在通知设置中开启。\n",
    UiTextKey.FriendsEmptyTitle to "暂无好友",
    UiTextKey.FriendsEmptyMessage to "使用「添加好友」按钮，通过用户名或用户 ID 查找好友。\n",
    UiTextKey.FriendsInfoGotItButton to "明白了",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "学习页面说明",
    UiTextKey.LearningInfoMessage to "• 下拉以手动刷新您的语言记录数。\n" +
            "• 每张卡片显示一种语言和您拥有的翻译记录数。\n" +
            "• 点击「生成」以创建学习表（首次生成免费）。\n" +
            "• 重新生成需要比上次多至少 5 条记录。\n" +
            "• 点击学习表按钮以打开和学习您生成的材料。\n" +
            "• 生成学习表后可以进行测验。",
    UiTextKey.LearningEmptyTitle to "无翻译记录",
    UiTextKey.LearningEmptyMessage to "开始翻译以建立历史记录。\n" +
            "学习表从您的翻译历史生成。\n" +
            "翻译后下拉刷新。",
    UiTextKey.LearningInfoGotItButton to "明白了",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "词库页面说明",
    UiTextKey.WordBankInfoMessage to "• 下拉以手动刷新您的词库语言列表。\n" +
            "• 选择语言以查看或生成其词库。\n" +
            "• 词库从您的翻译历史生成。\n" +
            "• 刷新词库需要比上次多至少 20 条记录。\n" +
            "• 使用自定义单词功能手动添加您的词汇。\n" +
            "• 您可以与好友分享词库中的单词。",
    UiTextKey.WordBankInfoGotItButton to "明白了",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "共享收件箱说明",
    UiTextKey.ShareInboxInfoMessage to "• 下拉以手动刷新您的共享收件箱。\n" +
            "• 好友分享的项目会显示在此。\n" +
            "• 单词可以接受添加到您的词库，或忽略。\n" +
            "• 学习表和测验可以点击「查看」阅读详细内容。\n" +
            "• 红点（●）表示有新的/未读项目。\n" +
            "• 忽略共享单词前会要求确认。",
    UiTextKey.ShareInboxInfoGotItButton to "明白了",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "个人资料可见性",
    UiTextKey.MyProfileVisibilityPublic to "公开",
    UiTextKey.MyProfileVisibilityPrivate to "私密",
    UiTextKey.MyProfileVisibilityDescription to "公开：任何人都可以搜索并添加您为好友。\n私密：搜索时无法找到您。",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "忽略单词",
    UiTextKey.ShareDismissWordMessage to "确定要忽略此共享单词？操作无法撤销。",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "语言：{language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "忽略",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "已连接或等待中",
    UiTextKey.AccessibilityNewMessages to "新消息",
    UiTextKey.AccessibilityNewReleasesIcon to "新项目指示",
    UiTextKey.AccessibilitySuccessIcon to "成功",
    UiTextKey.AccessibilityErrorIcon to "错误",
    UiTextKey.AccessibilitySharedItemTypeIcon to "共享项目类型",
    UiTextKey.AccessibilityAddCustomWords to "添加自定义单词",
    UiTextKey.AccessibilityWordBankExists to "词库已存在",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.反馈",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "通知设置",
    UiTextKey.FriendsNotifSettingsTitle to "通知偏好设置",
    UiTextKey.FriendsNotifNewMessages to "新聊天消息",
    UiTextKey.FriendsNotifFriendRequests to "收到好友请求",
    UiTextKey.FriendsNotifRequestAccepted to "好友请求已接受",
    UiTextKey.FriendsNotifSharedInbox to "新共享收件箱项目",
    UiTextKey.FriendsNotifCloseButton to "完成",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "应用内徽章（红点）",
    UiTextKey.InAppBadgeMessages to "未读聊天消息徽章",
    UiTextKey.InAppBadgeFriendRequests to "待处理好友请求徽章",
    UiTextKey.InAppBadgeSharedInbox to "未读共享收件箱徽章",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "请登录后继续。",
    UiTextKey.ErrorSaveFailedRetry to "保存失败，请重试。",
    UiTextKey.ErrorLoadFailedRetry to "加载失败，请重试。",
    UiTextKey.ErrorNetworkRetry to "网络错误，请检查连接后重试。",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "还需 {needed} 条翻译才能生成材料",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "切换到实时对话 →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "清除聊天",
    UiTextKey.ChatClearConversationTitle to "清除对话",
    UiTextKey.ChatClearConversationMessage to "隐藏此对话中所有消息？即使您离开再返回，消息仍将对您永久隐藏。对方不受影响。",
    UiTextKey.ChatClearConversationConfirm to "全部清除",
    UiTextKey.ChatClearConversationSuccess to "对话已清除",

    // Block user
    UiTextKey.BlockUserButton to "拉黑",
    UiTextKey.BlockUserTitle to "拉黑用户？",
    UiTextKey.BlockUserMessage to "拉黑 {username}？对方将从您的好友列表中移除，且无法再联系您。",
    UiTextKey.BlockUserConfirm to "拉黑",
    UiTextKey.BlockUserSuccess to "用户已拉黑并从好友中移除。",
    UiTextKey.BlockedUsersTitle to "已拉黑用户",
    UiTextKey.BlockedUsersEmpty to "没有已拉黑的用户。",
    UiTextKey.UnblockUserButton to "取消拉黑",
    UiTextKey.UnblockUserTitle to "取消拉黑？",
    UiTextKey.UnblockUserMessage to "取消拉黑 {username}？对方将可以再次发送好友请求。",
    UiTextKey.UnblockUserSuccess to "用户已取消拉黑。",
    UiTextKey.BlockedUsersManageButton to "管理已拉黑用户",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "请求备注（可选）",
    UiTextKey.FriendsRequestNotePlaceholder to "添加一段短备注...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "学习表已就绪！点击打开。",
    UiTextKey.GenerationBannerWordBank to "词库已就绪！点击查看。",
    UiTextKey.GenerationBannerQuiz to "测验已就绪！点击开始。",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "通知",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "繁体中文",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "好友系统",
    UiTextKey.HelpFriendSystemBody to "• 通过用户名或用户 ID 查找好友\n" +
            "• 发送、接受或拒绝好友请求\n" +
            "• 与好友实时聊天，还可以翻译对话\n" +
            "• 与好友分享单词和学习材料\n" +
            "• 共享收件箱可接收和管理好友发送的内容\n" +
            "• 好友卡片或收件箱上的红点（●）表示有未读消息或新内容\n" +
            "• 下拉刷新好友列表和请求",
    UiTextKey.HelpProfileVisibilityTitle to "个人资料可见性",
    UiTextKey.HelpProfileVisibilityBody to "• 您可以在「我的个人资料」设置中将资料设为公开或私密\n" +
            "• 公开：任何人都可以搜索到您并发送好友请求\n" +
            "• 私密：您的资料不会出现在搜索结果中\n" +
            "• 即使设为私密，仍可以分享用户 ID 来添加好友",
    UiTextKey.HelpColorPalettesTitle to "颜色主题和金币",
    UiTextKey.HelpColorPalettesBody to "• 1 个免费主题：天蓝（默认）\n" +
            "• 10 个可解锁主题，每个需 10 枚金币\n" +
            "• 完成测验即可获得金币\n" +
            "• 金币可用于解锁颜色主题或扩展历史记录上限\n" +
            "• 自动主题：早上 6 点至下午 6 点浅色模式，下午 6 点至早上 6 点深色模式",
    UiTextKey.HelpPrivacyTitle to "隐私和数据",
    UiTextKey.HelpPrivacyBody to "• 音频仅用于语音识别，不会永久存储\n" +
            "• OCR 在设备上处理（隐私优先）\n" +
            "• 您可以随时删除账号和所有数据\n" +
            "• 将个人资料设为私密可防止他人通过搜索找到您\n" +
            "• 所有数据通过 Firebase 安全同步",
    UiTextKey.HelpAppVersionTitle to "应用版本",
    UiTextKey.HelpAppVersionNotes to "• 历史记录上限为 30-60 条（可用金币扩展）\n" +
            "• 用户名必须唯一——更改后旧名会释放\n" +
            "• 应用版本更新时会自动退出以保障安全\n" +
            "• 所有翻译由 Azure AI 服务提供",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "实时翻译",
    UiTextKey.OnboardingPage1Desc to "快速翻译适合短句，实时对话适合多轮对话。",
    UiTextKey.OnboardingPage2Title to "学习词汇",
    UiTextKey.OnboardingPage2Desc to "根据您的翻译历史生成词汇表和测验。",
    UiTextKey.OnboardingPage3Title to "与好友联系",
    UiTextKey.OnboardingPage3Desc to "聊天、分享词汇，一起学习。",
    UiTextKey.OnboardingSkipButton to "跳过",
    UiTextKey.OnboardingNextButton to "下一步",
    UiTextKey.OnboardingGetStartedButton to "开始使用",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 欢迎回来，{name}！",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "用户名：",
    UiTextKey.ChatUserIdLabel to "用户 ID：",
    UiTextKey.ChatLearningLabel to "正在学习：",
    UiTextKey.ChatBlockedMessage to "您无法向此用户发送消息。",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "搜索",
    UiTextKey.CustomWordsEmptyState to "暂无自定义单词",
    UiTextKey.CustomWordsEmptyHint to "点击 + 添加您的单词",
    UiTextKey.CustomWordsNoSearchResults to "没有匹配搜索的单词",
    UiTextKey.AddCustomWordHintTemplate to "在 {from} 输入单词，在 {to} 输入翻译",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} 条记录",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID：{id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "邮箱：{email}",
    UiTextKey.ProfileUsernameHintFull to "供好友搜索的用户名（3-20 个字符，字母/数字/_）",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "此语言没有可用的语音选项",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "应用已更新，请重新登录",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "收藏夹上限已达",
    UiTextKey.FavoritesLimitMessage to "您最多可以保存 20 个收藏项目，请先移除部分收藏后再添加新的。",
    UiTextKey.FavoritesLimitGotIt to "好",
    UiTextKey.FavoritesInfoTitle to "关于收藏夹",
    UiTextKey.FavoritesInfoMessage to "您可以保存最多 20 个收藏项目（包括记录和对话），此限制有助于减少数据库读取，保持应用流畅运行。要添加更多，请先移除现有收藏。",
    UiTextKey.FavoritesInfoGotIt to "明白了",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "无法更改语言",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "主要语言每 30 天只能更改一次。请在 {days} 天后再试。",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "主要语言每 30 天只能更改一次。请在 {hours} 小时后再试。",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "确认更改语言",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "更改主要语言后，需要等 30 天才能再次更改。继续？",

    // Bottom navigation
    UiTextKey.NavHome to "主页",
    UiTextKey.NavTranslate to "翻译",
    UiTextKey.NavLearn to "学习",
    UiTextKey.NavFriends to "好友",
    UiTextKey.NavSettings to "设置",

    // Permissions
    UiTextKey.CameraPermissionTitle to "需要相机权限",
    UiTextKey.CameraPermissionMessage to "此功能需要相机权限来拍照识别文字，请授予相机权限以继续。",
    UiTextKey.CameraPermissionGrant to "授权",
    UiTextKey.MicPermissionMessage to "需要麦克风权限进行语音识别，请授权。",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "删除 {count} 个已选项目？此操作无法撤销。",
    UiTextKey.WordBankDeleteConfirm to "确定要删除「{word}」？",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "全部接受",
    UiTextKey.FriendsRejectAllButton to "全部拒绝",
    UiTextKey.ChatBlockedCannotSend to "无法发送消息",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "解锁 {name}？",
    UiTextKey.ShopUnlockCost to "费用：{cost} 枚金币",
    UiTextKey.ShopYourCoins to "您的金币：{coins}",
    UiTextKey.ShopUnlockButton to "解锁",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "主要语言",
    UiTextKey.HelpPrimaryLanguageBody to "• 主要语言用于学习表的解释和建议\n" +
            "• 每 30 天只能更改一次以保持学习一致性\n" +
            "• 您可以在设置中更改主要语言\n" +
            "• 主要语言设置为全局设置，适用于所有页面",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 提示：为了更准确的识别效果，请将「来源语言」设置为您扫描的文字语言。",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "无法更改用户名",
    UiTextKey.SettingsUsernameCooldownMessage to "用户名每 30 天只能更改一次。请在 {days} 天后再试。",
    UiTextKey.SettingsUsernameCooldownMessageHours to "用户名每 30 天只能更改一次。请在 {hours} 小时后再试。",
    UiTextKey.SettingsUsernameConfirmTitle to "确认更改用户名",
    UiTextKey.SettingsUsernameConfirmMessage to "更改用户名后，需要等 30 天才能再次更改。继续？",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "没有网络连接。请检查网络后重试。",
    UiTextKey.ErrorPermissionDenied to "您没有权限执行此操作。",
    UiTextKey.ErrorSessionExpired to "您的会话已过期，请重新登录。",
    UiTextKey.ErrorItemNotFound to "未找到请求的项目，可能已被删除。",
    UiTextKey.ErrorAccessDenied to "拒绝访问。",
    UiTextKey.ErrorAlreadyFriends to "您已经与此用户是好友了。",
    UiTextKey.ErrorUserBlocked to "无法完成此操作，该用户可能已被拉黑。",
    UiTextKey.ErrorRequestNotFound to "此好友请求已不存在。",
    UiTextKey.ErrorRequestAlreadyHandled to "此请求已被其他人处理。",
    UiTextKey.ErrorNotAuthorized to "您无权执行此操作。",
    UiTextKey.ErrorRateLimited to "请求过于频繁，请稍后再试。",
    UiTextKey.ErrorInvalidInput to "输入无效，请检查后重试。",
    UiTextKey.ErrorOperationNotAllowed to "当前无法完成此操作。",
    UiTextKey.ErrorTimeout to "操作超时，请重试。",
    UiTextKey.ErrorSendMessageFailed to "发送消息失败，请重试。",
    UiTextKey.ErrorFriendRequestSent to "好友请求已发送！",
    UiTextKey.ErrorFriendRequestFailed to "发送好友请求失败，请重试。",
    UiTextKey.ErrorFriendRemoved to "好友已成功移除。",
    UiTextKey.ErrorFriendRemoveFailed to "无法移除好友，请检查网络连接后重试。",
    UiTextKey.ErrorBlockSuccess to "已成功拉黑用户。",
    UiTextKey.ErrorBlockFailed to "拉黑用户失败，请重试。",
    UiTextKey.ErrorUnblockSuccess to "已取消拉黑用户。",
    UiTextKey.ErrorUnblockFailed to "取消拉黑失败，请重试。",
    UiTextKey.ErrorAcceptRequestSuccess to "已接受好友请求！",
    UiTextKey.ErrorAcceptRequestFailed to "接受好友请求失败，请重试。",
    UiTextKey.ErrorRejectRequestSuccess to "已拒绝好友请求。",
    UiTextKey.ErrorRejectRequestFailed to "拒绝好友请求失败，请重试。",
    UiTextKey.ErrorOfflineMessage to "您当前处于离线状态，部分功能可能无法使用。",
    UiTextKey.ErrorChatDeletionFailed to "无法删除对话，请重试。",
    UiTextKey.ErrorGenericRetry to "出了问题，请重试。",
)
