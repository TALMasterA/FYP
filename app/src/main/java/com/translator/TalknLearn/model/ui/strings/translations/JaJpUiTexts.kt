package com.translator.TalknLearn.model.ui

/**
 * Japanese (ja-JP) UI text map — 日本語インターフェーステキスト。
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val JaJpUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "マイクを使用",
    UiTextKey.CopyButton to "コピー",
    UiTextKey.SpeakScriptButton to "原文を読み上げ",
    UiTextKey.TranslateButton to "翻訳",
    UiTextKey.CopyTranslationButton to "翻訳をコピー",
    UiTextKey.SpeakTranslationButton to "翻訳を読み上げ",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "表示言語",
    UiTextKey.DetectLanguageLabel to "言語を検出",
    UiTextKey.TranslateToLabel to "翻訳先",

    // Language names
    UiTextKey.LangEnUs to "英語",
    UiTextKey.LangZhHk to "広東語",
    UiTextKey.LangJaJp to "日本語",
    UiTextKey.LangZhCn to "簡体字中国語",
    UiTextKey.LangFrFr to "フランス語",
    UiTextKey.LangDeDe to "ドイツ語",
    UiTextKey.LangKoKr to "韓国語",
    UiTextKey.LangEsEs to "スペイン語",
    UiTextKey.LangIdId to "インドネシア語",
    UiTextKey.LangViVn to "ベトナム語",
    UiTextKey.LangThTh to "タイ語",
    UiTextKey.LangFilPh to "フィリピン語",
    UiTextKey.LangMsMy to "マレー語",
    UiTextKey.LangPtBr to "ポルトガル語",
    UiTextKey.LangItIt to "イタリア語",
    UiTextKey.LangRuRu to "ロシア語",

    // Navigation
    UiTextKey.NavHistory to "履歴",
    UiTextKey.NavLogin to "ログイン",
    UiTextKey.NavLogout to "ログアウト",
    UiTextKey.NavBack to "戻る",
    UiTextKey.ActionCancel to "キャンセル",
    UiTextKey.ActionDelete to "削除",
    UiTextKey.ActionOpen to "開く",
    UiTextKey.ActionName to "名前を付ける",
    UiTextKey.ActionSave to "保存",
    UiTextKey.ActionConfirm to "確認",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "ここに入力またはマイクを使用...",
    UiTextKey.SpeechTranslatedPlaceholder to "翻訳結果がここに表示されます...",

    // Pagination
    UiTextKey.PaginationPrevLabel to "前のページ",
    UiTextKey.PaginationNextLabel to "次のページ",
    UiTextKey.PaginationPageLabelTemplate to "{page} / {total} ページ",

    // Toast
    UiTextKey.DisableText to "翻訳機能や履歴の保存にはログインが必要です。",

    // Error
    UiTextKey.ErrorRetryButton to "再試行",

    // Shop
    UiTextKey.ShopTitle to "ショップ",
    UiTextKey.ShopCoinBalance to "あなたのコイン",
    UiTextKey.ShopHistoryExpansionTitle to "履歴上限を拡張",
    UiTextKey.ShopHistoryExpansionDesc to "履歴の表示上限を拡張して、より多くの翻訳記録を確認できます。",
    UiTextKey.ShopCurrentLimit to "現在の上限：{limit} 件",
    UiTextKey.ShopMaxLimit to "最大上限：",
    UiTextKey.ShopBuyHistoryExpansion to "購入（+{increment} 件、{cost} コイン）",
    UiTextKey.ShopInsufficientCoins to "コイン不足",
    UiTextKey.ShopMaxLimitReached to "最大上限に達しました",
    UiTextKey.ShopHistoryExpandedTitle to "拡張成功！",
    UiTextKey.ShopHistoryExpandedMessage to "履歴上限が {limit} 件に拡張されました！より多くの翻訳記録を確認できます！",
    UiTextKey.ShopColorPaletteTitle to "カラーテーマ",
    UiTextKey.ShopColorPaletteDesc to "アプリのカラーテーマを選択、各テーマ10コイン",
    UiTextKey.ShopEntry to "ショップ",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "音声設定",
    UiTextKey.VoiceSettingsDesc to "各言語のテキスト読み上げ音声を選択します。",

    // Instructions
    UiTextKey.SpeechInstructions to "マイクボタンをタップして音声認識を開始し、完了後に翻訳をタップしてください。テキストや言語を変更後、自動検出が更新されない場合は、右上のリフレッシュをタップしてください。",
    UiTextKey.HomeInstructions to "機能を選択して開始します。",
    UiTextKey.ContinuousInstructions to "2つの言語を選択して、会話モードを開始します。",

    // Home
    UiTextKey.HomeTitle to "即時翻訳",
    UiTextKey.HelpTitle to "ヘルプ",
    UiTextKey.SpeechTitle to "クイック翻訳",
    UiTextKey.HomeStartButton to "翻訳を開始",

    // Help
    UiTextKey.HelpCurrentTitle to "現在の機能",
    UiTextKey.HelpCautionTitle to "注意事項",
    UiTextKey.HelpCurrentFeatures to "現在の機能：\n" +
            "  • クイック翻訳：音声認識後に翻訳\n" +
            "  • リアルタイム会話：双方向音声翻訳\n" +
            "  • 履歴：翻訳記録を表示\n" +
            "  • 学習教材：履歴から語彙とクイズを生成\n\n" +
            "翻訳：\n" +
            "  • Azure AI 音声認識を使用\n" +
            "  • Azure 翻訳サービスを使用\n",
    UiTextKey.HelpCaution to "注意事項：\n" +
            "  • 音声認識にはインターネット接続が必要です\n" +
            "  • ローカル翻訳キャッシュはオフラインでも使用可能\n" +
            "  • 重要な翻訳は専門サービスで確認してください\n\n" +
            "アカウントとデータ：\n" +
            "  • 履歴、学習、コイン機能にはログインが必要\n" +
            "  • ユーザーデータは Firebase Firestore に安全に保存\n\n" +
            "トラブルシューティング：\n" +
            "  • すべての手順を試しても機能しない場合は、アプリを再起動してください\n",
    UiTextKey.HelpNotesTitle to "メモ",
    UiTextKey.HelpNotes to "💡 使い方のコツとトラブルシューティング：\n\n" +
            "最適な翻訳のために：\n" +
            "  • はっきりと適度な速さで話す\n" +
            "  • 背景の雑音を減らして認識精度を向上\n" +
            "  • クイック翻訳は短い文に最適\n\n" +
            "表示言語：\n" +
            "  • 基本言語は英語、他の言語はAI翻訳\n" +
            "  • 広東語版は手動翻訳で精度が高い\n" +
            "更新とフィードバック：\n" +
            "  • アプリバージョンは設定 → 概要で確認\n" +
            "  • 設定 → フィードバックでご意見をお寄せください\n",

    // Feedback
    UiTextKey.FeedbackTitle to "フィードバック",
    UiTextKey.FeedbackDesc to "フィードバックありがとうございます！ご提案、バグ報告、アプリの感想をお聞かせください。",
    UiTextKey.FeedbackMessagePlaceholder to "フィードバックを入力してください...",
    UiTextKey.FeedbackSubmitButton to "フィードバックを送信",
    UiTextKey.FeedbackSubmitting to "送信中...",
    UiTextKey.FeedbackSuccessTitle to "ありがとうございます！",
    UiTextKey.FeedbackSuccessMessage to "フィードバックが正常に送信されました。貴重なご意見ありがとうございます！",
    UiTextKey.FeedbackErrorTitle to "送信に失敗",
    UiTextKey.FeedbackErrorMessage to "フィードバックの送信に失敗しました。接続を確認して再試行してください。",
    UiTextKey.FeedbackMessageRequired to "フィードバックの内容を入力してください。",

    // Continuous mode
    UiTextKey.ContinuousTitle to "リアルタイム会話",
    UiTextKey.ContinuousStartButton to "会話を開始",
    UiTextKey.ContinuousStopButton to "録音を停止",
    UiTextKey.ContinuousStartScreenButton to "リアルタイム会話",
    UiTextKey.ContinuousPersonALabel to "Aが話しています",
    UiTextKey.ContinuousPersonBLabel to "Bが話しています",
    UiTextKey.ContinuousCurrentStringLabel to "現在のテキスト：",
    UiTextKey.ContinuousSpeakerAName to "人物A",
    UiTextKey.ContinuousSpeakerBName to "人物B",
    UiTextKey.ContinuousTranslationSuffix to "・翻訳",
    UiTextKey.ContinuousPreparingMicText to "マイク準備中...（話さないでください）",
    UiTextKey.ContinuousTranslatingText to "翻訳中...",

    // History
    UiTextKey.HistoryTitle to "履歴",
    UiTextKey.HistoryTabDiscrete to "クイック翻訳",
    UiTextKey.HistoryTabContinuous to "リアルタイム会話",
    UiTextKey.HistoryNoContinuousSessions to "会話記録がありません。",
    UiTextKey.HistoryNoDiscreteRecords to "翻訳記録がありません。",
    UiTextKey.DialogDeleteRecordTitle to "記録を削除しますか？",
    UiTextKey.DialogDeleteRecordMessage to "この操作は元に戻せません。",
    UiTextKey.DialogDeleteSessionTitle to "会話を削除しますか？",
    UiTextKey.DialogDeleteSessionMessage to "この会話のすべての記録が削除されます。元に戻せません。",
    UiTextKey.HistoryDeleteSessionButton to "削除",
    UiTextKey.HistoryNameSessionTitle to "名前を付ける",
    UiTextKey.HistorySessionNameLabel to "会話名",
    UiTextKey.HistorySessionTitleTemplate to "会話 {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} 件",

    // Filter
    UiTextKey.FilterDropdownDefault to "すべての言語",
    UiTextKey.FilterTitle to "履歴を絞り込み",
    UiTextKey.FilterLangDrop to "言語",
    UiTextKey.FilterKeyword to "キーワード",
    UiTextKey.FilterApply to "適用",
    UiTextKey.FilterCancel to "キャンセル",
    UiTextKey.FilterClear to "クリア",
    UiTextKey.FilterHistoryScreenTitle to "絞り込み",

    // Auth
    UiTextKey.AuthLoginTitle to "ログイン",
    UiTextKey.AuthRegisterTitle to "登録（停止中）",
    UiTextKey.AuthLoginHint to "登録済みのメールアドレスとパスワードをご使用ください。",
    UiTextKey.AuthRegisterRules to "開発段階のため登録機能は停止中です。\n注意：存在しないメールを使用するとパスワードをリセットできません。\n" +
            "登録ルール：\n" +
            "• メール形式は正しくする（例：name@example.com）\n" +
            "• パスワードは8文字以上\n" +
            "• 確認パスワードは一致させる",
    UiTextKey.AuthEmailLabel to "メール",
    UiTextKey.AuthPasswordLabel to "パスワード",
    UiTextKey.AuthConfirmPasswordLabel to "パスワード確認",
        UiTextKey.AuthLoginButton to "ログイン",
        UiTextKey.AuthGoogleSignInButton to "Google でログイン",
    UiTextKey.AuthRegisterButton to "登録",
    UiTextKey.AuthToggleToRegister to "アカウントをお持ちでないですか？登録（停止中）",
    UiTextKey.AuthToggleToLogin to "アカウントをお持ちですか？ログイン",
    UiTextKey.AuthErrorPasswordsMismatch to "パスワードが一致しません。",
    UiTextKey.AuthErrorPasswordTooShort to "パスワードは8文字以上必要です。",
    UiTextKey.AuthRegistrationDisabled to "開発段階のため登録機能は停止中です。",
    UiTextKey.AuthResetEmailSent to "リセットメールを送信しました（メールが存在する場合）。受信トレイを確認してください。",

    // Password reset
    UiTextKey.ForgotPwText to "パスワードを忘れた方はこちら",
    UiTextKey.ResetPwTitle to "パスワードリセット",
    UiTextKey.ResetPwText to "アカウントのメールアドレスを入力してください。リセットリンクを送信します。\nアプリに登録済みのメールであることをご確認ください。\n",
    UiTextKey.ResetSendingText to "送信中...",
    UiTextKey.ResetSendText to "リセットメールを送信",

    // Settings
    UiTextKey.SettingsTitle to "設定",
    UiTextKey.SettingsPrimaryLanguageTitle to "主要言語",
    UiTextKey.SettingsPrimaryLanguageDesc to "学習の説明と提案に使用",
    UiTextKey.SettingsPrimaryLanguageLabel to "主要言語",
    UiTextKey.SettingsFontSizeTitle to "フォントサイズ",
    UiTextKey.SettingsFontSizeDesc to "テキストサイズを調整して読みやすくします（デバイス間で同期）",
    UiTextKey.SettingsScaleTemplate to "スケール：{pct}%",
    UiTextKey.SettingsColorCostTemplate to "{cost} コイン",
    UiTextKey.SettingsColorSelectButton to "選択",
    UiTextKey.SettingsColorAlreadyUnlocked to "解除済み",
    UiTextKey.SettingsPreviewHeadline to "見出し：大きなテキストプレビュー",
    UiTextKey.SettingsPreviewBody to "本文：通常のテキストプレビュー",
    UiTextKey.SettingsPreviewLabel to "ラベル：小さなテキストプレビュー",
    UiTextKey.SettingsAboutTitle to "概要",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "ログインすると、設定は自動的にアカウントに保存・同期されます。",
    UiTextKey.SettingsThemeTitle to "テーマ",
    UiTextKey.SettingsThemeDesc to "外観の適用方法を選択：システム、ライト、ダーク、またはスケジュール。",
    UiTextKey.SettingsThemeSystem to "システム",
    UiTextKey.SettingsThemeLight to "ライト",
    UiTextKey.SettingsThemeDark to "ダーク",
    UiTextKey.SettingsThemeScheduled to "スケジュール",
    UiTextKey.SettingsResetPW to "パスワードをリセット",
    UiTextKey.SettingsQuickLinks to "詳細設定",
    UiTextKey.SettingsNotLoggedInWarning to "アカウント設定を使用するにはログインしてください。アプリ言語は変更可能です。",
    UiTextKey.SettingsVoiceLanguageLabel to "言語",
    UiTextKey.SettingsVoiceSelectLabel to "音声",
    UiTextKey.SettingsVoiceDefault to "デフォルト",

    // Learning
    UiTextKey.LearningTitle to "学習",
    UiTextKey.LearningHintCount to "(*) 回数 = その言語を含む翻訳履歴の件数。",
    UiTextKey.LearningErrorTemplate to "エラー：%s",
    UiTextKey.LearningGenerate to "生成",
    UiTextKey.LearningRegenerate to "再生成",
    UiTextKey.LearningGenerating to "生成中...",
    UiTextKey.LearningOpenSheetTemplate to "{speclanguage} 学習シート",
    UiTextKey.LearningSheetTitleTemplate to "{speclanguage} 学習シート",
    UiTextKey.LearningSheetPrimaryTemplate to "主要言語：{speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "現在の記録数：{nowCount}（生成時：{savedCount}）",
    UiTextKey.LearningSheetNoContent to "学習シートの内容がありません。",
    UiTextKey.LearningSheetRegenerate to "再生成",
    UiTextKey.LearningSheetGenerating to "生成中...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 これは何ですか？",
    UiTextKey.LearningSheetWhatIsThisDesc to "翻訳履歴に基づいて生成された学習シートです。語彙、意味、例文、文法ポイントが含まれています。下のクイズボタンで知識をテストしましょう！",
    UiTextKey.LearningRegenBlockedTitle to "現在再生成できません",
    UiTextKey.LearningRegenBlockedMessage to "再生成には前回より少なくとも5件多い記録が必要です。あと {needed} 件必要です。",
    UiTextKey.LearningRegenInfoTitle to "再生成のルール",
    UiTextKey.LearningRegenInfoMessage to "学習教材を再生成するには：\n\n• 初回生成：いつでも可能\n• 再生成：前回より少なくとも5件多い翻訳記録が必要\n\n十分な新しい記録があるとボタンが有効（青色）になります。グレーの場合は翻訳を続けてください！\n\n💡 ヒント：翻訳後に件数が更新されない場合はアプリを再起動してください。",

    // Quiz
    UiTextKey.QuizTitleTemplate to "クイズ：{language}",
    UiTextKey.QuizOpenButton to "📝 クイズ",
    UiTextKey.QuizGenerateButton to "🔄 クイズを生成",
    UiTextKey.QuizGenerating to "⏳ 生成中...",
    UiTextKey.QuizBlocked to "🚫 ブロック",
    UiTextKey.QuizWait to "⏳ お待ちください...",
    UiTextKey.QuizCanEarnCoins to "🪙 コインを獲得できます！",
    UiTextKey.QuizCancelButton to "キャンセル",
    UiTextKey.QuizPreviousButton to "前の問題",
    UiTextKey.QuizNextButton to "次の問題",
    UiTextKey.QuizSubmitButton to "提出",
    UiTextKey.QuizRetakeButton to "再受験",
    UiTextKey.QuizBackButton to "戻る",
    UiTextKey.QuizLoadingText to "クイズを読み込み中...",
    UiTextKey.QuizGeneratingText to "クイズを生成中...",
    UiTextKey.QuizNoMaterialsTitle to "学習教材が見つかりません",
    UiTextKey.QuizNoMaterialsMessage to "まず学習教材を生成してからクイズをご覧ください。",
    UiTextKey.QuizErrorTitle to "⚠️ クイズエラー",
    UiTextKey.QuizErrorSuggestion to "提案：上のボタンでクイズを生成してください。",
    UiTextKey.QuizCompletedTitle to "クイズ完了！",
    UiTextKey.QuizAnswerReviewTitle to "解答レビュー",
    UiTextKey.QuizYourAnswerTemplate to "あなたの回答：{Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "正解：{Answer}",
    UiTextKey.QuizQuestionTemplate to "問題 {current} / {total}",
    UiTextKey.QuizAnotherGenInProgress to "⏳ 別の生成が進行中です。お待ちください。",
    UiTextKey.QuizCoinRulesTitle to "🪙 コイン獲得ルール",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ 獲得方法：",
    UiTextKey.QuizCoinRulesRequirements to "条件：",
    UiTextKey.QuizCoinRulesCurrentStatus to "現在の状態：",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ 次のクイズでコインを獲得できます！",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• コイン獲得にはあと {count} 件必要",
    UiTextKey.QuizCoinRule1Coin to "• 正解ごとに1コイン",
    UiTextKey.QuizCoinRuleFirstAttempt to "• 各クイズバージョンの初回のみ有効",
    UiTextKey.QuizCoinRuleMatchMaterials to "• クイズは教材バージョンと一致が必要",
    UiTextKey.QuizCoinRulePlus10 to "• 前回のコイン獲得クイズより10件以上多い記録が必要",
    UiTextKey.QuizCoinRuleNoDelete to "• 履歴を削除してもコインは再獲得できません",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• 教材：{count} 件",
    UiTextKey.QuizCoinRuleQuizTemplate to "• クイズ：{count} 件",
    UiTextKey.QuizCoinRuleGotIt to "了解！",
    UiTextKey.QuizRegenConfirmTitle to "🔄 新しいクイズを生成しますか？",
    UiTextKey.QuizRegenCanEarnCoins to "✅ このクイズでコインを獲得できます！（初回のみ）",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ このクイズではコインを獲得できません。",
    UiTextKey.QuizRegenNeedMoreTemplate to "コイン資格にはあと {count} 件の翻訳記録が必要です（前回のコイン獲得より10件以上）。",
    UiTextKey.QuizRegenReminder to "ヒント：練習や再受験は可能ですが、コインは初回かつ十分な記録がある場合のみ付与されます。",
    UiTextKey.QuizRegenGenerateButton to "生成",
    UiTextKey.QuizCoinsEarnedTitle to "✨ コイン獲得！",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "おめでとうございます！{Coins} コインを獲得しました！",
    UiTextKey.QuizCoinsRule1 to "• 初回のみ正解ごとに1コイン",
    UiTextKey.QuizCoinsRule2 to "• 同じクイズの再受験ではコインなし",
    UiTextKey.QuizCoinsRule3 to "• 新クイズには前回より10件以上の記録が必要",
    UiTextKey.QuizCoinsRule4 to "• クイズは現在の教材バージョンと一致が必要",
    UiTextKey.QuizCoinsRule5 to "• 合計コインは履歴ページで確認",
    UiTextKey.QuizCoinsGreatButton to "素晴らしい！",
    UiTextKey.QuizRecordsLabel to "件の記録",

    // History coins

    // History info
    UiTextKey.HistoryInfoTitle to "履歴について",
    UiTextKey.HistoryInfoLimitMessage to "履歴は最新 {limit} 件を表示、ショップで上限を拡張できます！",
    UiTextKey.HistoryInfoOlderRecordsMessage to "古い記録は保存されていますが、パフォーマンスのため非表示です。",
    UiTextKey.HistoryInfoFavoritesMessage to "重要な翻訳を永久保存するには、記録の ❤️ アイコンをタップしてお気に入りに追加。",
    UiTextKey.HistoryInfoViewFavoritesMessage to "設定 → お気に入りで保存した記録を確認。",
    UiTextKey.HistoryInfoFilterMessage to "フィルターボタンで表示中の {limit} 件から検索。",
    UiTextKey.HistoryInfoGotItButton to "了解",

    // Word bank
    UiTextKey.WordBankTitle to "単語帳",
    UiTextKey.WordBankSelectLanguage to "言語を選択して単語帳を表示または生成：",
    UiTextKey.WordBankNoHistory to "翻訳記録なし",
    UiTextKey.WordBankNoHistoryHint to "翻訳を始めて単語帳を作りましょう！",
    UiTextKey.WordBankWordsCount to "語",
    UiTextKey.WordBankGenerating to "生成中...",
    UiTextKey.WordBankGenerate to "単語帳を生成",
    UiTextKey.WordBankRefresh to "🔄 単語帳を更新",
    UiTextKey.WordBankEmpty to "単語帳なし",
    UiTextKey.WordBankEmptyHint to "上のボタンをタップして翻訳履歴から単語帳を生成。",
    UiTextKey.WordBankExample to "例文：",
    UiTextKey.WordBankDifficulty to "難易度：",
    UiTextKey.WordBankFilterCategory to "カテゴリー",
    UiTextKey.WordBankFilterCategoryAll to "すべてのカテゴリー",
    UiTextKey.WordBankFilterDifficultyLabel to "難易度レベル：",
    UiTextKey.WordBankFilterNoResults to "フィルター条件に一致する単語がありません",
    UiTextKey.WordBankRegenInfoTitle to "更新ルール",
    UiTextKey.WordBankRegenInfoMessage to "単語帳を更新するには：\n\n• 初回生成：いつでも可能\n• 更新：前回より少なくとも20件多い翻訳記録が必要\n\n十分な新しい記録があるとボタンが有効（青色）になります。グレーの場合は翻訳を続けてください！\n\n💡 ヒント：翻訳後に件数が更新されない場合はアプリを再起動してください。",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "ログアウトしますか？",
    UiTextKey.DialogLogoutMessage to "翻訳機能の使用や履歴の保存・表示には再ログインが必要です。",
    UiTextKey.DialogGenerateOverwriteTitle to "教材を上書きしますか？",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "以前の教材は上書きされます（存在する場合）。\n{speclanguage} の教材を生成しますか？",

    // Profile
    UiTextKey.ProfileTitle to "プロフィール",
    UiTextKey.ProfileUsernameLabel to "ユーザー名",
    UiTextKey.ProfileUsernameHint to "ユーザー名を入力",
    UiTextKey.ProfileUpdateButton to "プロフィールを更新",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "アカウント削除",
    UiTextKey.AccountDeleteWarning to "⚠️ この操作は永久に取り消せません！",
    UiTextKey.AccountDeleteConfirmMessage to "履歴、単語帳、学習教材、設定を含むすべてのデータが永久に削除されます。パスワードを入力して確認してください。",
    UiTextKey.AccountDeletePasswordLabel to "パスワード",
        UiTextKey.AccountDeleteButton to "アカウントとデータを削除",

    // Favorites
    UiTextKey.FavoritesTitle to "お気に入り",
    UiTextKey.FavoritesEmpty to "お気に入りなし",
    UiTextKey.FavoritesTabRecords to "記録",
    UiTextKey.FavoritesTabSessions to "会話",
    UiTextKey.FavoritesSessionsEmpty to "保存された会話なし",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} 件のメッセージ",

    // Custom words
    UiTextKey.CustomWordsTitle to "カスタム単語",
    UiTextKey.CustomWordsAdd to "単語を追加",
    UiTextKey.CustomWordsEdit to "単語を編集",
    UiTextKey.CustomWordsDelete to "単語を削除",
    UiTextKey.CustomWordsOriginalLabel to "原語",
    UiTextKey.CustomWordsTranslatedLabel to "翻訳",
    UiTextKey.CustomWordsPronunciationLabel to "発音（任意）",
    UiTextKey.CustomWordsExampleLabel to "例文（任意）",
    UiTextKey.CustomWordsOriginalLanguageLabel to "原語言語",
    UiTextKey.CustomWordsTranslationLanguageLabel to "翻訳言語",
    UiTextKey.CustomWordsSaveButton to "保存",
    UiTextKey.CustomWordsCancelButton to "キャンセル",

    // Language detection
    UiTextKey.LanguageDetectAuto to "自動検出",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "画像からテキストをスキャン",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ 注意：画像テキスト認識は完全に正確ではない場合があります。抽出されたテキストを確認してください。" +
            "ラテン文字（英語等）、中国語、日本語、韓国語に対応。",

    // Cache

    // Auto theme
    UiTextKey.SettingsAutoThemePreview to "テーマは設定した時間に自動で切り替わります",

    // Offline mode
    UiTextKey.OfflineSyncPending to "オンライン時に変更を同期",

    // Image capture
    UiTextKey.ImageSourceTitle to "画像ソースを選択",
    UiTextKey.ImageSourceCamera to "カメラで撮影",
    UiTextKey.ImageSourceGallery to "ギャラリーから選択",
    UiTextKey.ImageSourceCancel to "キャンセル",
    UiTextKey.CameraCaptureContentDesc to "撮影",

    // Friends
    UiTextKey.FriendsTitle to "フレンド",
    UiTextKey.FriendsAddButton to "フレンドを追加",
    UiTextKey.FriendsSearchTitle to "ユーザーを検索",
    UiTextKey.FriendsSearchPlaceholder to "ユーザー名またはユーザーIDを入力...",
    UiTextKey.FriendsSearchNoResults to "ユーザーが見つかりません",
    UiTextKey.FriendsRequestsSection to "フレンドリクエスト（{count}）",
    UiTextKey.FriendsSectionTitle to "フレンド（{count}）",
    UiTextKey.FriendsAcceptButton to "承認",
    UiTextKey.FriendsRejectButton to "拒否",
    UiTextKey.FriendsSendRequestButton to "追加",
    UiTextKey.FriendsCloseButton to "閉じる",
    UiTextKey.FriendsCancelButton to "キャンセル",
    UiTextKey.FriendsRemoveConfirm to "削除",
    UiTextKey.FriendsNewRequestsTemplate to "新しいフレンドリクエストが {count} 件あります！",
    UiTextKey.FriendsSentRequestsSection to "送信済みリクエスト（{count}）",
    UiTextKey.FriendsPendingStatus to "保留中",
    UiTextKey.FriendsCancelRequestButton to "リクエストをキャンセル",
    UiTextKey.FriendsUnreadMessageDesc to "メッセージを送信",
    UiTextKey.FriendsDeleteModeButton to "フレンドを削除",
    UiTextKey.FriendsDeleteMultipleTitle to "フレンドを削除",
    UiTextKey.FriendsDeleteMultipleMessage to "選択した {count} 人のフレンドを削除しますか？",
    UiTextKey.FriendsSearchMinChars3 to "ユーザー名検索には3文字以上入力してください",
    UiTextKey.FriendsSearchByUserIdHint to "またはユーザーIDで正確に検索",
    UiTextKey.FriendsStatusAlreadyFriends to "すでにフレンドです",
    UiTextKey.FriendsStatusRequestSent to "リクエスト送信済み — 返信待ち",
    UiTextKey.FriendsStatusRequestReceived to "このユーザーからリクエストが届いています",

    // Chat
    UiTextKey.ChatTitle to "{username} とチャット",
    UiTextKey.ChatInputPlaceholder to "メッセージを入力...",
    UiTextKey.ChatSendButton to "送信",
    UiTextKey.ChatEmpty to "メッセージがありません。チャットを始めましょう！",
    UiTextKey.ChatUnreadBadge to "{count} 件の未読",
    UiTextKey.ChatTranslateButton to "翻訳",
    UiTextKey.ChatTranslateDialogTitle to "会話を翻訳",
    UiTextKey.ChatTranslateDialogMessage to "フレンドのメッセージをあなたの言語に翻訳しますか？各メッセージの言語を検出して翻訳します。",
    UiTextKey.ChatTranslateConfirm to "すべて翻訳",
    UiTextKey.ChatShowOriginal to "原文を表示",
    UiTextKey.ChatShowTranslation to "翻訳を表示",
    UiTextKey.ChatTranslatedLabel to "翻訳済み",

    // Sharing
    UiTextKey.ShareInboxTitle to "共有受信箱",
    UiTextKey.ShareInboxEmpty to "共有アイテムはありません。フレンドが単語や教材を共有できます！",
    UiTextKey.ShareWordButton to "単語を共有",
    UiTextKey.ShareMaterialButton to "教材を共有",
    UiTextKey.ShareSelectFriendTitle to "フレンドを選択",
    UiTextKey.ShareSelectFriendMessage to "共有するフレンドを選択：",
    UiTextKey.ShareSuccess to "共有成功！",
    UiTextKey.ShareAcceptButton to "承認",
    UiTextKey.ShareDismissButton to "無視",
    UiTextKey.ShareTypeWord to "単語",
    UiTextKey.ShareTypeLearningSheet to "学習シート",
    UiTextKey.ShareReceivedFrom to "送信者：{username}",
    UiTextKey.ShareNewItemsTemplate to "{count} 件の新しいアイテム！",
    UiTextKey.ShareViewFullMaterial to "「表示」をタップして教材全文を閲覧",
    UiTextKey.ShareDeleteItemTitle to "アイテムを削除",
    UiTextKey.ShareDeleteItemMessage to "この共有アイテムを削除しますか？元に戻せません。",
    UiTextKey.ShareDeleteButton to "削除",
    UiTextKey.ShareViewButton to "表示",
    UiTextKey.ShareItemNotFound to "アイテムが見つかりません。",
    UiTextKey.ShareNoContent to "この教材にコンテンツがありません。",
    UiTextKey.ShareSaveToSelf to "自分の受信箱に保存",
    UiTextKey.ShareSavedToSelf to "受信箱に保存しました！",

    // My profile
    UiTextKey.MyProfileTitle to "マイプロフィール",
    UiTextKey.MyProfileUserId to "ユーザーID",
    UiTextKey.MyProfileUsername to "ユーザー名",
    UiTextKey.MyProfileCopyUserId to "ユーザーIDをコピー",
    UiTextKey.MyProfileCopyUsername to "ユーザー名をコピー",
    UiTextKey.MyProfileShare to "プロフィールを共有",
    UiTextKey.MyProfileCopied to "クリップボードにコピーしました！",
    UiTextKey.MyProfilePrimaryLanguage to "主要言語",
    UiTextKey.MyProfileLearningLanguages to "学習中の言語",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "フレンドページについて",
    UiTextKey.FriendsInfoMessage to "• 下にスワイプしてフレンドリスト、リクエスト、ステータスを手動で更新。\n" +
            "• フレンドカードをタップしてチャットを開く。\n" +
            "• カードの赤い点（●）は未読メッセージを示し、✓✓ で一括既読にできます。\n" +
            "• 📥 受信箱アイコンで共有教材を確認、✓✓ で赤い点を消せます。\n" +
            "• フレンドカードの 🚫 アイコンでブロック — 相手はリストから削除され、連絡できなくなります。\n" +
            "• ブロックすると相手とのチャット履歴も消去されます。\n" +
            "• ゴミ箱アイコンで削除モードに入り、フレンドを削除。\n" +
            "• フレンド削除時、すべてのチャットメッセージも削除されます。\n" +
            "• 検索ボタンでユーザー名またはIDで新しいフレンドを検索・追加。\n" +
            "• プッシュ通知はデフォルトでオフ — 通知設定で有効にできます。\n",
    UiTextKey.FriendsEmptyTitle to "フレンドがいません",
    UiTextKey.FriendsEmptyMessage to "「フレンドを追加」ボタンで、ユーザー名またはIDでフレンドを検索しましょう。\n",
    UiTextKey.FriendsInfoGotItButton to "了解",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "学習ページについて",
    UiTextKey.LearningInfoMessage to "• 下にスワイプして言語の記録数を手動更新。\n" +
            "• 各カードは言語と翻訳記録数を表示。\n" +
            "• 「生成」をタップして学習シートを作成（初回は無料）。\n" +
            "• 再生成には前回より少なくとも5件多い記録が必要。\n" +
            "• 学習シートボタンで生成した教材を開いて学習。\n" +
            "• 学習シート生成後にクイズも受けられます。",
    UiTextKey.LearningEmptyMessage to "翻訳を始めて履歴を作りましょう。\n" +
            "学習シートは翻訳履歴から生成されます。\n" +
            "翻訳後に下にスワイプして更新。",
    UiTextKey.LearningInfoGotItButton to "了解",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "単語帳ページについて",
    UiTextKey.WordBankInfoMessage to "• 下にスワイプして単語帳の言語リストを手動更新。\n" +
            "• 言語を選択して単語帳を表示または生成。\n" +
            "• 単語帳は翻訳履歴から生成されます。\n" +
            "• 更新には前回より少なくとも20件多い記録が必要。\n" +
            "• カスタム単語機能で手動で単語を追加できます。\n" +
            "• フレンドと単語帳の単語を共有できます。",
    UiTextKey.WordBankInfoGotItButton to "了解",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "共有受信箱について",
    UiTextKey.ShareInboxInfoMessage to "• 下にスワイプして共有受信箱を手動更新。\n" +
            "• フレンドが共有したアイテムがここに表示されます。\n" +
            "• 単語は承認して単語帳に追加、または無視できます。\n" +
            "• 学習シートやクイズは「表示」で詳細を閲覧。\n" +
            "• 赤い点（●）は新しい/未読のアイテムを示します。\n" +
            "• 共有単語を無視する前に確認が求められます。",
    UiTextKey.ShareInboxInfoGotItButton to "了解",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "プロフィール公開設定",
    UiTextKey.MyProfileVisibilityPublic to "公開",
    UiTextKey.MyProfileVisibilityPrivate to "非公開",
    UiTextKey.MyProfileVisibilityDescription to "公開：誰でもあなたを検索してフレンドに追加可能。\n非公開：検索で見つからなくなります。",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "単語を無視",
    UiTextKey.ShareDismissWordMessage to "この共有単語を無視しますか？元に戻せません。",

    // Shared inbox sheet label

    // Accessibility
    UiTextKey.AccessibilityDismiss to "閉じる",
    UiTextKey.AccessibilityNewMessages to "新しいメッセージ",
    UiTextKey.AccessibilityNewReleasesIcon to "新しいアイテムの表示",
    UiTextKey.AccessibilitySuccessIcon to "成功",
    UiTextKey.AccessibilityErrorIcon to "エラー",
    UiTextKey.AccessibilitySharedItemTypeIcon to "共有アイテムの種類",
    UiTextKey.AccessibilityWordBankExists to "単語帳が存在します",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.フィードバック",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "通知設定",
    UiTextKey.FriendsNotifSettingsTitle to "通知の設定",
    UiTextKey.FriendsNotifNewMessages to "新しいチャットメッセージ",
    UiTextKey.FriendsNotifFriendRequests to "フレンドリクエスト受信",
    UiTextKey.FriendsNotifRequestAccepted to "フレンドリクエスト承認",
    UiTextKey.FriendsNotifSharedInbox to "新しい共有アイテム",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "アプリ内バッジ（赤い点）",
    UiTextKey.InAppBadgeMessages to "未読チャットメッセージバッジ",
    UiTextKey.InAppBadgeFriendRequests to "フレンドリクエストバッジ",
    UiTextKey.InAppBadgeSharedInbox to "未読共有受信箱バッジ",

    // Error messages

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "教材生成にはあと {needed} 件の翻訳が必要",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "リアルタイム会話に切り替え →",

    // Chat clear
    UiTextKey.ChatClearConversationTitle to "会話をクリア",
    UiTextKey.ChatClearConversationMessage to "この会話のすべてのメッセージを非表示にしますか？退出して戻っても非表示のままです。相手には影響しません。",
    UiTextKey.ChatClearConversationConfirm to "すべてクリア",
    UiTextKey.ChatClearConversationSuccess to "会話をクリアしました",

    // Block user
    UiTextKey.BlockUserButton to "ブロック",
    UiTextKey.BlockUserTitle to "ユーザーをブロックしますか？",
    UiTextKey.BlockUserMessage to "{username} をブロックしますか？フレンドリストから削除され、連絡できなくなります。",
    UiTextKey.BlockUserConfirm to "ブロック",
    UiTextKey.BlockedUsersTitle to "ブロックしたユーザー",
    UiTextKey.BlockedUsersEmpty to "ブロックしたユーザーはいません。",
    UiTextKey.UnblockUserButton to "ブロック解除",
    UiTextKey.UnblockUserTitle to "ブロック解除しますか？",
    UiTextKey.UnblockUserMessage to "{username} のブロックを解除しますか？再びフレンドリクエストを送れるようになります。",
    UiTextKey.BlockedUsersManageButton to "ブロックユーザー管理",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "リクエストメモ（任意）",
    UiTextKey.FriendsRequestNotePlaceholder to "短いメモを追加...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "学習シートが完成しました！タップして開く。",
    UiTextKey.GenerationBannerWordBank to "単語帳が完成しました！タップして確認。",
    UiTextKey.GenerationBannerQuiz to "クイズが完成しました！タップして開始。",

    // Notification settings quick link

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "繁体字中国語",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "フレンドシステム",
    UiTextKey.HelpFriendSystemBody to "• ユーザー名またはIDでフレンドを検索\n" +
            "• フレンドリクエストの送信、承認、拒否\n" +
            "• フレンドとリアルタイムチャット、会話の翻訳も可能\n" +
            "• 単語や学習教材をフレンドと共有\n" +
            "• 共有受信箱でフレンドからのコンテンツを受信・管理\n" +
            "• カードや受信箱の赤い点（●）は未読メッセージや新コンテンツを示す\n" +
            "• 下にスワイプでフレンドリストとリクエストを更新",
    UiTextKey.HelpProfileVisibilityTitle to "プロフィール公開設定",
    UiTextKey.HelpProfileVisibilityBody to "• マイプロフィール設定で公開または非公開に設定可能\n" +
            "• 公開：誰でもあなたを検索してリクエスト送信可能\n" +
            "• 非公開：検索結果に表示されない\n" +
            "• 非公開でもユーザーIDを共有してフレンド追加可能",
    UiTextKey.HelpColorPalettesTitle to "カラーテーマとコイン",
    UiTextKey.HelpColorPalettesBody to "• 1つの無料テーマ：スカイブルー（デフォルト）\n" +
            "• 10個のロック解除可能なテーマ、各10コイン\n" +
            "• クイズ完了でコイン獲得\n" +
            "• コインはカラーテーマの解除や履歴上限の拡張に使用\n" +
            "• 自動テーマ：6時〜18時ライト、18時〜6時ダーク",
    UiTextKey.HelpPrivacyTitle to "プライバシーとデータ",
    UiTextKey.HelpPrivacyBody to "• 音声は認識のみに使用、永久保存されない\n" +
            "• OCRはデバイス上で処理（プライバシー優先）\n" +
            "• いつでもアカウントと全データを削除可能\n" +
            "• プロフィールを非公開にすると検索で見つからない\n" +
            "• すべてのデータはFirebaseで安全に同期",
    UiTextKey.HelpAppVersionTitle to "アプリバージョン",
    UiTextKey.HelpAppVersionNotes to "• 履歴上限は30〜60件（コインで拡張可能）\n" +
            "• ユーザー名は一意 — 変更後に旧名は解放\n" +
            "• バージョン更新時にセキュリティのため自動ログアウト\n" +
            "• すべての翻訳はAzure AIサービスが提供",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "即時翻訳",
    UiTextKey.OnboardingPage1Desc to "クイック翻訳は短い文に、リアルタイム会話は双方向の会話に。",
    UiTextKey.OnboardingPage2Title to "語彙を学ぶ",
    UiTextKey.OnboardingPage2Desc to "翻訳履歴から語彙シートとクイズを生成。",
    UiTextKey.OnboardingPage3Title to "フレンドとつながる",
    UiTextKey.OnboardingPage3Desc to "チャットして、単語を共有して、一緒に学びましょう。",
    UiTextKey.OnboardingSkipButton to "スキップ",
    UiTextKey.OnboardingNextButton to "次へ",
    UiTextKey.OnboardingGetStartedButton to "始めましょう",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 おかえりなさい、{name}！",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "ユーザー名：",
    UiTextKey.ChatUserIdLabel to "ユーザーID：",
    UiTextKey.ChatLearningLabel to "学習中：",
    UiTextKey.ChatBlockedMessage to "このユーザーにメッセージを送信できません。",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "検索",
    UiTextKey.CustomWordsEmptyState to "カスタム単語はありません",
    UiTextKey.CustomWordsEmptyHint to "+ をタップして単語を追加",
    UiTextKey.CustomWordsNoSearchResults to "検索に一致する単語がありません",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} 件の記録",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID：{id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "メール：{email}",
    UiTextKey.ProfileUsernameHintFull to "フレンドが検索するユーザー名（3〜20文字、英数字/_）",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "この言語では音声オプションがありません",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "アプリが更新されました。再度ログインしてください",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "お気に入り上限に達しました",
    UiTextKey.FavoritesLimitMessage to "お気に入りは最大20件です。新しく追加するには既存のお気に入りを削除してください。",
    UiTextKey.FavoritesLimitGotIt to "了解",
    UiTextKey.FavoritesInfoTitle to "お気に入りについて",
    UiTextKey.FavoritesInfoMessage to "お気に入りは最大20件（記録と会話を含む）。この制限はデータベースの読み込みを減らし、アプリを快適に保つためです。追加するには既存のお気に入りを削除してください。",
    UiTextKey.FavoritesInfoGotIt to "了解",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "言語を変更できません",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "主要言語は30日ごとにのみ変更可能です。あと {days} 日お待ちください。",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "主要言語は30日ごとにのみ変更可能です。あと {hours} 時間お待ちください。",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "言語変更の確認",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "主要言語を変更すると、30日間変更できなくなります。続行しますか？",

    // Bottom navigation
    UiTextKey.NavHome to "ホーム",
    UiTextKey.NavTranslate to "翻訳",
    UiTextKey.NavLearn to "学習",
    UiTextKey.NavFriends to "フレンド",
    UiTextKey.NavSettings to "設定",

    // Permissions
    UiTextKey.CameraPermissionTitle to "カメラの許可が必要です",
    UiTextKey.CameraPermissionMessage to "テキスト認識に使用するため、カメラの許可をお願いします。",
    UiTextKey.CameraPermissionGrant to "許可",
    UiTextKey.MicPermissionMessage to "音声認識にマイクの許可が必要です。",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "選択した {count} 件を削除しますか？元に戻せません。",
    UiTextKey.WordBankDeleteConfirm to "「{word}」を削除しますか？",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "すべて承認",
    UiTextKey.FriendsRejectAllButton to "すべて拒否",
    UiTextKey.ChatBlockedCannotSend to "メッセージを送信できません",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "{name} をロック解除しますか？",
    UiTextKey.ShopUnlockCost to "費用：{cost} コイン",
    UiTextKey.ShopYourCoins to "あなたのコイン：{coins}",
    UiTextKey.ShopUnlockButton to "ロック解除",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "主要言語",
    UiTextKey.HelpPrimaryLanguageBody to "• 主要言語は学習シートの説明と提案に使用\n" +
            "• 学習の一貫性のため30日ごとにのみ変更可能\n" +
            "• 設定で主要言語を変更できます\n" +
            "• グローバル設定としてすべてのページに適用",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 ヒント：より正確な認識のため、「ソース言語」をスキャンするテキストの言語に設定してください。",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "ユーザー名を変更できません",
    UiTextKey.SettingsUsernameCooldownMessage to "ユーザー名は30日ごとにのみ変更可能です。あと {days} 日お待ちください。",
    UiTextKey.SettingsUsernameCooldownMessageHours to "ユーザー名は30日ごとにのみ変更可能です。あと {hours} 時間お待ちください。",
    UiTextKey.SettingsUsernameConfirmTitle to "ユーザー名変更の確認",
    UiTextKey.SettingsUsernameConfirmMessage to "ユーザー名を変更すると、30日間変更できなくなります。続行しますか？",

    // Phase 1 fixes
    UiTextKey.SettingsPasswordManagedByGoogle to "パスワードは Google で管理されています",
    UiTextKey.SpeechListeningLabel to "聞き取り中… 話してください",
    UiTextKey.QuizLeaveTitle to "クイズを終了しますか？",
    UiTextKey.QuizLeaveMessage to "回答は失われます。",
    UiTextKey.QuizLeaveConfirm to "終了",
    UiTextKey.QuizLeaveStay to "続ける",

    // Phase 1 part 2
    UiTextKey.A11yAddFavorite to "お気に入りに追加",
    UiTextKey.A11yRemoveFavorite to "お気に入りから削除",
    UiTextKey.A11yViewFriendProfile to "友達のプロフィールを見る",
    UiTextKey.A11yRefreshAutoDetect to "自動検出をやり直す",
    UiTextKey.A11yInstructions to "使い方",
    UiTextKey.A11yClearText to "テキストを消去",
    UiTextKey.A11yAddCustomWords to "カスタム単語を追加",
    UiTextKey.A11yFilter to "フィルター",
    UiTextKey.A11ySpeakOriginal to "原文を読み上げ",
    UiTextKey.A11ySpeakTranslation to "翻訳を読み上げ",
    UiTextKey.A11ySpeakExample to "例文を読み上げ",
    UiTextKey.A11yCoinRules to "コインのルール",
    UiTextKey.StatusWaiting to "待機中…",
    UiTextKey.ErrorNotLoggedIn to "ログインしていません",
    UiTextKey.HistoryErrorDeleteFailed to "削除に失敗しました。もう一度お試しください。",
    UiTextKey.HistoryErrorRenameFailed to "名前の変更に失敗しました。もう一度お試しください。",
    UiTextKey.HistoryErrorDeleteSessionFailed to "セッションを削除できませんでした。もう一度お試しください。",
    UiTextKey.HistoryErrorLoadMoreFailed to "これ以上の記録を読み込めませんでした。",
    UiTextKey.HistoryErrorRemoveFavoriteFailed to "お気に入りから削除できませんでした。",
    UiTextKey.HistoryErrorAddFavoriteFailed to "お気に入りに追加できませんでした。",
    UiTextKey.HistoryErrorFavoriteSessionFailed to "セッションをお気に入りに保存できませんでした。",
    UiTextKey.HistoryErrorUnfavoriteSessionFailed to "セッションをお気に入りから削除できませんでした。",
    UiTextKey.TtsSpeaking to "読み上げ中…",
    UiTextKey.TtsSpeakingTranslation to "翻訳を読み上げ中…",
    UiTextKey.TtsSpoken to "✓ 読み上げ完了",
    UiTextKey.TtsError to "✗ {detail}",
    UiTextKey.SpeechStatusRecognitionError to "認識エラー：{detail}",
    UiTextKey.SpeechStatusAutoDetecting to "言語を自動検出中…",
    UiTextKey.SpeechStatusDetected to "検出：{language}",
    UiTextKey.SpeechStatusDetectedConfidence to "検出：{language}（信頼度 {confidence}%）",
    UiTextKey.SpeechStatusEnterText to "翻訳するテキストを入力するか話してください。",
    UiTextKey.SpeechStatusLoginRequired to "翻訳を使うにはログインが必要です。",
    UiTextKey.SpeechStatusDetectFailed to "元の言語を検出できませんでした。手動で選択してください。",
    UiTextKey.SpeechStatusTranslationError to "翻訳エラー：{detail}",
    UiTextKey.SpeechStatusScanningImage to "画像の文字をスキャン中…",
    UiTextKey.SpeechStatusTextExtracted to "文字を抽出しました",
    UiTextKey.SpeechStatusOcrError to "画像スキャンエラー：{detail}",
    UiTextKey.SpeechStatusEnterTextFirst to "先にテキストを入力するか話してください。",
    UiTextKey.SpeechStatusDetectFailedForSpeech to "読み上げる言語を検出できませんでした。元の言語を選択してください。",
    UiTextKey.SpeechStatusRefreshed to "リセットしました。次の翻訳または読み上げで再び自動検出します。",
    UiTextKey.ContinuousStatusLoginRequired to "連続翻訳を使うにはログインしてください。",
    UiTextKey.ContinuousStatusPreparingMic to "マイクを準備中…",
    UiTextKey.ContinuousStatusTranslationError to "連続翻訳エラー：{detail}",
    UiTextKey.ContinuousStatusRateLimited to "{detail} 失敗が続くのを防ぐため、連続モードを停止しました。",
    UiTextKey.ContinuousStatusRecognitionError to "連続認識エラー：{detail}",
    UiTextKey.ContinuousStatusStartError to "連続モードを開始できません：{detail}",

    // Phase 1 part 3: social area messages
    UiTextKey.StatusRaw to "{detail}",
    UiTextKey.ErrGeneric to "問題が発生しました。もう一度お試しください。",
    UiTextKey.ErrNoInternet to "インターネットに接続されていません。ネットワークを確認してもう一度お試しください。",
    UiTextKey.ErrPermissionDenied to "この操作を行う権限がありません。",
    UiTextKey.ErrSessionExpired to "セッションの有効期限が切れました。もう一度サインインしてください。",
    UiTextKey.ErrItemNotFound to "要求された項目が見つかりません。削除された可能性があります。",
    UiTextKey.ErrAccessDenied to "アクセスが拒否されました。",
    UiTextKey.ErrAlreadyFriends to "このユーザーとはすでに友達です。",
    UiTextKey.ErrUserMaybeBlocked to "この操作を完了できません。ユーザーがブロックされている可能性があります。",
    UiTextKey.ErrRequestGone to "この友達リクエストはもう存在しません。",
    UiTextKey.ErrRequestHandled to "このリクエストはすでに他の人によって処理されています。",
    UiTextKey.ErrNotAuthorized to "この操作を行う権限がありません。",
    UiTextKey.ErrTooManyRequests to "リクエストが多すぎます。しばらく待ってからもう一度お試しください。",
    UiTextKey.ErrInvalidInput to "入力が無効です。確認してもう一度お試しください。",
    UiTextKey.ErrCannotCompleteNow to "現在この操作を完了できません。",
    UiTextKey.ErrTimedOut to "操作がタイムアウトしました。もう一度お試しください。",
    UiTextKey.FriendsMsgRemoved to "友達を削除しました。",
    UiTextKey.FriendsMsgRemoveFailed to "友達を削除できません。接続を確認してもう一度お試しください。",
    UiTextKey.FriendsMsgRemovedMany to "友達を削除しました。",
    UiTextKey.FriendsMsgNeedUsername to "友達リクエストを管理する前に、プロフィールでユーザー名を設定してください。",
    UiTextKey.FriendsMsgMaxPending to "保留中の友達リクエストが上限の {limit} 件に達しました。承認されるのを待つか、いくつかキャンセルしてから送信してください。",
    UiTextKey.FriendsMsgRateLimit to "送信回数の上限を超えました。友達リクエストは1時間に {limit} 件まで送信できます。約 {minutes} 分後にもう一度お試しください。",
    UiTextKey.FriendsMsgNoteTooLong to "メモは {max} 文字以内にしてください",
    UiTextKey.FriendsMsgRequestSent to "友達リクエストを送信しました！相手に通知されます。",
    UiTextKey.FriendsMsgAlreadyFriends to "このユーザーとはすでに友達です。",
    UiTextKey.FriendsMsgRequestPending to "このユーザーにはすでに保留中のリクエストがあります。返事をお待ちください。",
    UiTextKey.FriendsMsgProfileMissing to "ユーザーのプロフィールが見つかりません。アカウントが削除された可能性があります。",
    UiTextKey.FriendsMsgYouBlocked to "このユーザーをブロックしています。先にブロックを解除してください。",
    UiTextKey.FriendsMsgUnableToSend to "友達リクエストを送信できません。",
    UiTextKey.FriendsMsgSendFailed to "友達リクエストの送信に失敗しました。もう一度お試しください。",
    UiTextKey.FriendsMsgAccepted to "友達リクエストを承認しました！友達になりました。",
    UiTextKey.FriendsMsgAcceptGone to "このリクエストはもう存在しません。キャンセルされた可能性があります。",
    UiTextKey.FriendsMsgAcceptNotAuthorized to "このリクエストを承認する権限がありません。",
    UiTextKey.FriendsMsgAcceptFailed to "リクエストの承認に失敗しました。もう一度お試しください。",
    UiTextKey.FriendsMsgDeclined to "リクエストを拒否しました。",
    UiTextKey.FriendsMsgDeclineGone to "このリクエストはもう存在しません。",
    UiTextKey.FriendsMsgDeclineFailed to "リクエストの拒否に失敗しました。もう一度お試しください。",
    UiTextKey.FriendsMsgCancelled to "友達リクエストをキャンセルしました。",
    UiTextKey.FriendsMsgCancelGone to "このリクエストはもう存在しません。すでに承認または拒否された可能性があります。",
    UiTextKey.FriendsMsgCancelFailed to "リクエストのキャンセルに失敗しました。もう一度お試しください。",
    UiTextKey.FriendsMsgBatchAcceptAll to "{count} 件の友達リクエストをすべて承認しました！",
    UiTextKey.FriendsMsgBatchAcceptFailed to "リクエストの承認に失敗しました。もう一度お試しください。",
    UiTextKey.FriendsMsgBatchAcceptMixed to "{success} 件承認、{failed} 件失敗しました。失敗したものを再試行してください。",
    UiTextKey.FriendsMsgBatchAcceptStoppedAt to "{processed}/{total} 件を処理した後、一括承認をキャンセルしました。",
    UiTextKey.FriendsMsgBatchAcceptStopped to "一括承認をキャンセルしました。",
    UiTextKey.FriendsMsgBatchDeclined to "{count} 件のリクエストを拒否しました。",
    UiTextKey.FriendsMsgBatchDeclineStoppedAt to "{processed}/{total} 件を処理した後、一括拒否をキャンセルしました。",
    UiTextKey.FriendsMsgBatchDeclineStopped to "一括拒否をキャンセルしました。",
    UiTextKey.FriendsMsgBlockedRemoved to "ユーザーをブロックし、友達から削除しました。",
    UiTextKey.FriendsMsgBlockFailed to "ユーザーのブロックに失敗しました。もう一度お試しください。",
    UiTextKey.FriendsMsgBlocked to "ユーザーをブロックしました。",
    UiTextKey.FriendsMsgUnblocked to "ユーザーのブロックを解除しました。",
    UiTextKey.FriendsMsgUnblockFailed to "ブロックの解除に失敗しました。もう一度お試しください。",
    UiTextKey.ChatMsgLoadFailed to "メッセージの読み込みに失敗しました。もう一度お試しください。",
    UiTextKey.ChatMsgBlockFailed to "ユーザーのブロックに失敗しました。",
    UiTextKey.ChatMsgUnblockFailed to "ブロックの解除に失敗しました。",
    UiTextKey.ChatMsgTooLong to "メッセージは {max} 文字以内にしてください",
    UiTextKey.ChatMsgTooFast to "メッセージの送信が速すぎます。しばらくお待ちください。",
    UiTextKey.ChatMsgSendFailed to "メッセージの送信に失敗しました",
    UiTextKey.ChatMsgTranslateFailed to "翻訳に失敗しました。もう一度お試しください。",
    UiTextKey.MyProfileMsgLoadFailed to "プロフィールの読み込みに失敗しました。もう一度お試しください。",
    UiTextKey.MyProfileMsgNeedUsername to "プロフィールを公開する前にユーザー名を設定してください。",
    UiTextKey.MyProfileMsgPublic to "プロフィールを公開に設定しました",
    UiTextKey.MyProfileMsgPrivate to "プロフィールを非公開に設定しました",
    UiTextKey.MyProfileMsgVisibilityFailed to "公開設定の更新に失敗しました。もう一度お試しください。",
    UiTextKey.SharedMsgAdded to "コレクションに追加しました",
    UiTextKey.SharedMsgDeleted to "項目を削除しました",
    UiTextKey.SharedMsgDismissed to "項目を非表示にしました",
    UiTextKey.SharedMsgAcceptFailed to "項目の受け取りに失敗しました",
    UiTextKey.SharedMsgDeleteFailed to "項目の削除に失敗しました",
    UiTextKey.SharedMsgDismissFailed to "項目の非表示に失敗しました",
    UiTextKey.SharedMsgNoItemId to "項目 ID がありません",
    UiTextKey.SharedMsgLoadFailed to "共有された教材の読み込みに失敗しました。もう一度お試しください。",

    // Phase 1 part 4: settings, shop, profile, login and feedback messages
    UiTextKey.FeedbackMsgEmpty to "メッセージを入力してください",
    UiTextKey.FeedbackMsgTooShort to "フィードバックは {min} 文字以上で入力してください",
    UiTextKey.FeedbackMsgTooLong to "フィードバックは {max} 文字以内で入力してください",
    UiTextKey.FeedbackMsgRateLimited to "最近フィードバックを送信しすぎています。しばらくしてからもう一度お試しください。",
    UiTextKey.FeedbackMsgSubmitFailed to "フィードバックの送信に失敗しました。インターネット接続を確認してください。",
    UiTextKey.ShopMsgLoadFailed to "ショップのデータを読み込めませんでした",
    UiTextKey.ShopMsgPurchaseFailed to "購入に失敗しました",
    UiTextKey.ShopMsgHistoryExpanded to "履歴の上限が {limit} 件に拡張されました！",
    UiTextKey.ShopMsgPaletteAlreadyUnlocked to "このパレットはすでに解除されています",
    UiTextKey.ShopMsgUnlockFailed to "パレットの解除に失敗しました",
    UiTextKey.ShopMsgPaletteUnlocked to "パレットを解除しました！",
    UiTextKey.ShopMsgPaletteSelectFailed to "カラーパレットを変更できませんでした。もう一度お試しください。",
    UiTextKey.ProfileMsgLoadFailed to "プロフィールを読み込めませんでした",
    UiTextKey.ProfileMsgUsernameTooShort to "ユーザー名は {min} 文字以上で入力してください",
    UiTextKey.ProfileMsgUsernameTooLong to "ユーザー名は {max} 文字以内で入力してください",
    UiTextKey.ProfileMsgUsernameInvalidChars to "ユーザー名には英字、数字、アンダースコアのみ使用できます",
    UiTextKey.ProfileMsgVerifyFailed to "アカウントの状態を確認できませんでした。もう一度お試しください。",
    UiTextKey.ProfileMsgUsernameTaken to "このユーザー名はすでに使われています",
    UiTextKey.ProfileMsgUsernameUpdatedSyncing to "ユーザー名を更新しました。フレンドリストの更新には少し時間がかかる場合があります。",
    UiTextKey.ProfileMsgUsernameUpdated to "ユーザー名を更新しました",
    UiTextKey.ProfileMsgUpdateFailed to "プロフィールの更新に失敗しました",
    UiTextKey.ProfileMsgSetUsernameFailed to "ユーザー名の設定に失敗しました",
    UiTextKey.ProfileMsgDeleteFailed to "アカウントの削除に失敗しました",
    UiTextKey.ProfileMsgInvalidPassword to "パスワードが正しくありません。もう一度お試しください。",
    UiTextKey.ProfileMsgGoogleReauthFailed to "Google の再認証に失敗しました。もう一度お試しください。",
    UiTextKey.ProfileMsgGoogleNoToken to "Google ログインに失敗しました：ID トークンがありません。",
    UiTextKey.ProfileMsgGoogleSignInFailed to "Google ログインに失敗しました。もう一度お試しください。",
    UiTextKey.ProfileGoogleReauthHint to "確認のため、Google アカウントで再認証してください。",
    UiTextKey.ProfileContinueWithGoogle to "Google で続行",
    UiTextKey.SettingsMsgLanguageSaveFailed to "言語設定を保存できませんでした。もう一度お試しください。",
    UiTextKey.SettingsMsgFontSaveFailed to "文字サイズを保存できませんでした。もう一度お試しください。",
    UiTextKey.SettingsMsgThemeSaveFailed to "テーマ設定を保存できませんでした。もう一度お試しください。",
    UiTextKey.SettingsMsgAutoThemeFailed to "自動テーマをオフにできませんでした。もう一度お試しください。",
    UiTextKey.SettingsMsgVoiceSaveFailed to "音声設定を保存できませんでした。もう一度お試しください。",
    UiTextKey.SettingsMsgNotificationSaveFailed to "通知設定を保存できませんでした。もう一度お試しください。",
    UiTextKey.AuthMsgEmailEmpty to "メールアドレスを入力してください",
    UiTextKey.AuthMsgEmailInvalid to "メールアドレスの形式が正しくありません",
    UiTextKey.AuthMsgTooManyLogins to "ログインの試行回数が多すぎます。しばらくしてからもう一度お試しください。",
    UiTextKey.AuthMsgTooManyResets to "パスワードリセットの試行回数が多すぎます。しばらくしてからもう一度お試しください。",
    UiTextKey.AuthMsgGoogleEmptyToken to "Google ログインに失敗しました：ID トークンが空です。",
    UiTextKey.AuthMsgGoogleNoToken to "Google ログインに失敗しました：ID トークンが返されませんでした。",
    UiTextKey.AuthMsgGoogleFailedCode to "Google ログインに失敗しました（コード {code}）。",
    UiTextKey.AuthMsgGoogleNotConfigured to "このビルドでは Google ログインが設定されていません。",
    UiTextKey.ErrUnknown to "不明なエラーが発生しました。",
    UiTextKey.ErrNetworkFailed to "ネットワーク接続に失敗しました。インターネット接続を確認してください。",
    UiTextKey.ErrOperationTimedOut to "操作がタイムアウトしました。もう一度お試しください。",
    UiTextKey.ErrCancelled to "操作はキャンセルされました。",
    UiTextKey.ErrUnexpected to "予期しないエラーが発生しました。もう一度お試しください。",
    UiTextKey.ErrAuthInvalidEmail to "メールアドレスの形式が正しくありません。",
    UiTextKey.ErrAuthWrongPassword to "パスワードが正しくありません。",
    UiTextKey.ErrAuthUserNotFound to "このメールアドレスのアカウントが見つかりません。",
    UiTextKey.ErrAuthUserDisabled to "このアカウントは無効になっています。",
    UiTextKey.ErrAuthTooManyRequests to "リクエストが多すぎます。しばらくしてからもう一度お試しください。",
    UiTextKey.ErrAuthEmailInUse to "このメールアドレスは別のアカウントですでに使用されています。",
    UiTextKey.ErrAuthFailed to "認証に失敗しました。もう一度お試しください。",
    UiTextKey.ErrDbPermission to "このデータにアクセスする権限がありません。",
    UiTextKey.ErrDbUnavailable to "サービスは現在利用できません。しばらくしてからもう一度お試しください。",
    UiTextKey.ErrDbGeneric to "データベースエラーです。もう一度お試しください。",

    // Extended Error Messages
)
