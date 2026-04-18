package com.example.fyp.model.ui

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
    UiTextKey.RecognizingStatus to "録音中...話してください、自動停止を待ちます。",
    UiTextKey.TranslatingStatus to "翻訳中...",
    UiTextKey.SpeakingOriginalStatus to "原文を読み上げ中...",
    UiTextKey.SpeakingTranslationStatus to "翻訳を読み上げ中...",
    UiTextKey.SpeakingLabel to "読み上げ中",
    UiTextKey.FinishedSpeakingOriginal to "原文の読み上げ完了",
    UiTextKey.FinishedSpeakingTranslation to "翻訳の読み上げ完了",
    UiTextKey.TtsErrorTemplate to "音声エラー：%s",

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
    UiTextKey.StatusAzureErrorTemplate to "Azureエラー：%s",
    UiTextKey.StatusTranslationErrorTemplate to "翻訳エラー：%s",
    UiTextKey.StatusLoginRequiredTranslation to "翻訳にはログインが必要です",
    UiTextKey.StatusRecognizePreparing to "マイク準備中...（話さないでください）",
    UiTextKey.StatusRecognizeListening to "聞いています...話してください。",

    // Pagination
    UiTextKey.PaginationPrevLabel to "前のページ",
    UiTextKey.PaginationNextLabel to "次のページ",
    UiTextKey.PaginationPageLabelTemplate to "{page} / {total} ページ",

    // Toast
    UiTextKey.ToastCopied to "コピーしました",
    UiTextKey.DisableText to "翻訳機能や履歴の保存にはログインが必要です。",

    // Error
    UiTextKey.ErrorRetryButton to "再試行",
    UiTextKey.ErrorGenericMessage to "エラーが発生しました。もう一度お試しください。",

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
    UiTextKey.HomeFeaturesTitle to "機能紹介",
    UiTextKey.HomeDiscreteDescription to "短文と音声翻訳",
    UiTextKey.HomeContinuousDescription to "リアルタイム双方向会話翻訳",
    UiTextKey.HomeLearningDescription to "翻訳履歴から学習教材とクイズを生成",

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
            "• パスワードは6文字以上\n" +
            "• 確認パスワードは一致させる",
    UiTextKey.AuthEmailLabel to "メール",
    UiTextKey.AuthPasswordLabel to "パスワード",
    UiTextKey.AuthConfirmPasswordLabel to "パスワード確認",
    UiTextKey.AuthLoginButton to "ログイン",
    UiTextKey.AuthRegisterButton to "登録",
    UiTextKey.AuthToggleToRegister to "アカウントをお持ちでないですか？登録（停止中）",
    UiTextKey.AuthToggleToLogin to "アカウントをお持ちですか？ログイン",
    UiTextKey.AuthErrorPasswordsMismatch to "パスワードが一致しません。",
    UiTextKey.AuthErrorPasswordTooShort to "パスワードは6文字以上必要です。",
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
    UiTextKey.SettingsColorPaletteTitle to "カラーテーマ",
    UiTextKey.SettingsColorPaletteDesc to "アプリのカラーテーマを選択、各テーマ10コイン",
    UiTextKey.SettingsColorCostTemplate to "{cost} コイン",
    UiTextKey.SettingsColorUnlockButton to "ロック解除",
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
    UiTextKey.SettingsVoiceTitle to "音声設定",
    UiTextKey.SettingsVoiceDesc to "各言語のテキスト読み上げ音声を選択します。",
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
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ 再生成にはあと {needed} 件の記録が必要（最低5件）",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ 記録数は前回の生成時より多い必要があります",
    UiTextKey.LearningRegenInfoTitle to "再生成のルール",
    UiTextKey.LearningRegenInfoMessage to "学習教材を再生成するには：\n\n• 初回生成：いつでも可能\n• 再生成：前回より少なくとも5件多い翻訳記録が必要\n\n十分な新しい記録があるとボタンが有効（青色）になります。グレーの場合は翻訳を続けてください！\n\n💡 ヒント：翻訳後に件数が更新されない場合はアプリを再起動してください。",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ この教材バージョンのクイズは生成済みです。新しいクイズを作成するには新しい学習シートを生成してください。",

    // Quiz
    UiTextKey.QuizTitleTemplate to "クイズ：{language}",
    UiTextKey.QuizOpenButton to "📝 クイズ",
    UiTextKey.QuizGenerateButton to "🔄 クイズを生成",
    UiTextKey.QuizGenerating to "⏳ 生成中...",
    UiTextKey.QuizUpToDate to "✓ 最新版",
    UiTextKey.QuizBlocked to "🚫 ブロック",
    UiTextKey.QuizWait to "⏳ お待ちください...",
    UiTextKey.QuizMaterialsQuizTemplate to "教材：{materials} | クイズ：{quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 コインを獲得できます！",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 コイン獲得にはあと {count} 件必要",
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
    UiTextKey.QuizCannotRegenTemplate to "⚠️ 再生成不可：教材（{materials}）< クイズ（{quiz}）、翻訳を追加してください。",
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
    UiTextKey.QuizOutdatedMessage to "このクイズは古い学習シートに基づいています。",
    UiTextKey.QuizRecordsLabel to "件の記録",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 あなたのコイン",
    UiTextKey.HistoryCoinRulesTitle to "コイン獲得ルール：",
    UiTextKey.HistoryCoinHowToEarnTitle to "獲得方法：",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 正解ごとに1コイン",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• 各クイズバージョンの初回のみ",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• クイズは現在の学習教材と一致が必要",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 不正防止ルール：",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• 前回のコイン獲得より10件以上の新翻訳が必要",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• クイズバージョンは教材バージョンと同じが必要",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• 履歴削除はクイズ再生成をブロック（件数が前回より多い場合を除く）",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• 同じクイズの再受験ではコインなし",
    UiTextKey.HistoryCoinTipsTitle to "💡 ヒント：",
    UiTextKey.HistoryCoinTipsRule1 to "• 定期的に翻訳を追加",
    UiTextKey.HistoryCoinTipsRule2 to "• 初回受験前にしっかり学習！",
    UiTextKey.HistoryCoinGotItButton to "了解！",

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
    UiTextKey.WordBankRegenerate to "単語帳を再生成",
    UiTextKey.WordBankRefresh to "🔄 単語帳を更新",
    UiTextKey.WordBankEmpty to "単語帳なし",
    UiTextKey.WordBankEmptyHint to "上のボタンをタップして翻訳履歴から単語帳を生成。",
    UiTextKey.WordBankExample to "例文：",
    UiTextKey.WordBankDifficulty to "難易度：",
    UiTextKey.WordBankFilterCategory to "カテゴリー",
    UiTextKey.WordBankFilterCategoryAll to "すべてのカテゴリー",
    UiTextKey.WordBankFilterDifficultyLabel to "難易度レベル：",
    UiTextKey.WordBankFilterNoResults to "フィルター条件に一致する単語がありません",
    UiTextKey.WordBankRefreshAvailable to "✅ 更新可能！",
    UiTextKey.WordBankRecordsNeeded to "件（更新には20件必要）",
    UiTextKey.WordBankRegenInfoTitle to "更新ルール",
    UiTextKey.WordBankRegenInfoMessage to "単語帳を更新するには：\n\n• 初回生成：いつでも可能\n• 更新：前回より少なくとも20件多い翻訳記録が必要\n\n十分な新しい記録があるとボタンが有効（青色）になります。グレーの場合は翻訳を続けてください！\n\n💡 ヒント：翻訳後に件数が更新されない場合はアプリを再起動してください。",
    UiTextKey.WordBankHistoryCountTemplate to "現在の記録数：{nowCount}（生成時：{savedCount}）",

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
    UiTextKey.ProfileUpdateSuccess to "プロフィール更新成功",
    UiTextKey.ProfileUpdateError to "プロフィール更新に失敗",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "アカウント削除",
    UiTextKey.AccountDeleteWarning to "⚠️ この操作は永久に取り消せません！",
    UiTextKey.AccountDeleteConfirmMessage to "履歴、単語帳、学習教材、設定を含むすべてのデータが永久に削除されます。パスワードを入力して確認してください。",
    UiTextKey.AccountDeletePasswordLabel to "パスワード",
    UiTextKey.AccountDeleteButton to "アカウントを削除",
    UiTextKey.AccountDeleteSuccess to "アカウント削除成功",
    UiTextKey.AccountDeleteError to "アカウント削除に失敗",
    UiTextKey.AccountDeleteReauthRequired to "削除を確認するにはパスワードを再入力してください",

    // Favorites
    UiTextKey.FavoritesTitle to "お気に入り",
    UiTextKey.FavoritesEmpty to "お気に入りなし",
    UiTextKey.FavoritesAddSuccess to "お気に入りに追加しました",
    UiTextKey.FavoritesRemoveSuccess to "お気に入りから削除しました",
    UiTextKey.FavoritesAddButton to "お気に入りに追加",
    UiTextKey.FavoritesRemoveButton to "お気に入りから削除",
    UiTextKey.FavoritesNoteLabel to "メモ",
    UiTextKey.FavoritesNoteHint to "メモを追加（任意）",
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
    UiTextKey.CustomWordsSaveSuccess to "単語を保存しました",
    UiTextKey.CustomWordsDeleteSuccess to "単語を削除しました",
    UiTextKey.CustomWordsAlreadyExists to "この単語はすでに存在します",
    UiTextKey.CustomWordsOriginalLanguageLabel to "原語言語",
    UiTextKey.CustomWordsTranslationLanguageLabel to "翻訳言語",
    UiTextKey.CustomWordsSaveButton to "保存",
    UiTextKey.CustomWordsCancelButton to "キャンセル",

    // Language detection
    UiTextKey.LanguageDetectAuto to "自動検出",
    UiTextKey.LanguageDetectDetecting to "検出中...",
    UiTextKey.LanguageDetectedTemplate to "検出：{language}",
    UiTextKey.LanguageDetectFailed to "検出失敗",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "画像からテキストをスキャン",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ 注意：画像テキスト認識は完全に正確ではない場合があります。抽出されたテキストを確認してください。" +
            "ラテン文字（英語等）、中国語、日本語、韓国語に対応。",
    UiTextKey.ImageRecognitionScanning to "画像テキストをスキャン中...",
    UiTextKey.ImageRecognitionSuccess to "テキスト抽出成功",

    // Cache
    UiTextKey.CacheClearButton to "キャッシュをクリア",
    UiTextKey.CacheClearSuccess to "キャッシュをクリアしました",
    UiTextKey.CacheStatsTemplate to "キャッシュ：{count} 件の翻訳を保存",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "テーマ自動切り替え",
    UiTextKey.SettingsAutoThemeDesc to "時間帯に応じてライトとダークテーマを自動切り替え",
    UiTextKey.SettingsAutoThemeEnabled to "有効",
    UiTextKey.SettingsAutoThemeDisabled to "無効",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "ダークモード開始時間：",
    UiTextKey.SettingsAutoThemeLightStartLabel to "ライトモード開始時間：",
    UiTextKey.SettingsAutoThemePreview to "テーマは設定した時間に自動で切り替わります",

    // Offline mode
    UiTextKey.OfflineModeTitle to "オフラインモード",
    UiTextKey.OfflineModeMessage to "現在オフラインです。キャッシュデータを閲覧しています。",
    UiTextKey.OfflineModeRetry to "接続を再試行",
    UiTextKey.OfflineDataCached to "キャッシュデータ利用可能",
    UiTextKey.OfflineSyncPending to "オンライン時に変更を同期",

    // Image capture
    UiTextKey.ImageSourceTitle to "画像ソースを選択",
    UiTextKey.ImageSourceCamera to "カメラで撮影",
    UiTextKey.ImageSourceGallery to "ギャラリーから選択",
    UiTextKey.ImageSourceCancel to "キャンセル",
    UiTextKey.CameraCaptureContentDesc to "撮影",

    // Friends
    UiTextKey.FriendsTitle to "フレンド",
    UiTextKey.FriendsMenuButton to "フレンド",
    UiTextKey.FriendsAddButton to "フレンドを追加",
    UiTextKey.FriendsSearchTitle to "ユーザーを検索",
    UiTextKey.FriendsSearchPlaceholder to "ユーザー名またはユーザーIDを入力...",
    UiTextKey.FriendsSearchMinChars to "検索には2文字以上入力してください",
    UiTextKey.FriendsSearchNoResults to "ユーザーが見つかりません",
    UiTextKey.FriendsListEmpty to "フレンドを追加して交流や学習教材を共有しましょう。",
    UiTextKey.FriendsRequestsSection to "フレンドリクエスト（{count}）",
    UiTextKey.FriendsSectionTitle to "フレンド（{count}）",
    UiTextKey.FriendsAcceptButton to "承認",
    UiTextKey.FriendsRejectButton to "拒否",
    UiTextKey.FriendsRemoveButton to "削除",
    UiTextKey.FriendsRemoveDialogTitle to "フレンドを削除",
    UiTextKey.FriendsRemoveDialogMessage to "{username} をフレンドリストから削除しますか？",
    UiTextKey.FriendsSendRequestButton to "追加",
    UiTextKey.FriendsRequestSentSuccess to "フレンドリクエストを送信しました！",
    UiTextKey.FriendsRequestAcceptedSuccess to "フレンドリクエストを承認しました！",
    UiTextKey.FriendsRequestRejectedSuccess to "リクエストを拒否しました",
    UiTextKey.FriendsRemovedSuccess to "フレンドを削除しました",
    UiTextKey.FriendsRequestFailed to "リクエスト送信に失敗",
    UiTextKey.FriendsCloseButton to "閉じる",
    UiTextKey.FriendsCancelButton to "キャンセル",
    UiTextKey.FriendsRemoveConfirm to "削除",
    UiTextKey.FriendsNewRequestsTemplate to "新しいフレンドリクエストが {count} 件あります！",
    UiTextKey.FriendsSentRequestsSection to "送信済みリクエスト（{count}）",
    UiTextKey.FriendsPendingStatus to "保留中",
    UiTextKey.FriendsCancelRequestButton to "リクエストをキャンセル",
    UiTextKey.FriendsUnreadMessageDesc to "メッセージを送信",
    UiTextKey.FriendsDeleteModeButton to "フレンドを削除",
    UiTextKey.FriendsDeleteSelectedButton to "選択を削除",
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
    UiTextKey.ChatMessageSent to "メッセージ送信済み",
    UiTextKey.ChatMessageFailed to "メッセージ送信に失敗",
    UiTextKey.ChatMarkingRead to "既読にしています...",
    UiTextKey.ChatLoadingMessages to "メッセージを読み込み中...",
    UiTextKey.ChatToday to "今日",
    UiTextKey.ChatYesterday to "昨日",
    UiTextKey.ChatUnreadBadge to "{count} 件の未読",
    UiTextKey.ChatTranslateButton to "翻訳",
    UiTextKey.ChatTranslateDialogTitle to "会話を翻訳",
    UiTextKey.ChatTranslateDialogMessage to "フレンドのメッセージをあなたの言語に翻訳しますか？各メッセージの言語を検出して翻訳します。",
    UiTextKey.ChatTranslateConfirm to "すべて翻訳",
    UiTextKey.ChatTranslating to "メッセージを翻訳中...",
    UiTextKey.ChatTranslated to "メッセージを翻訳しました",
    UiTextKey.ChatShowOriginal to "原文を表示",
    UiTextKey.ChatShowTranslation to "翻訳を表示",
    UiTextKey.ChatTranslateFailed to "翻訳に失敗",
    UiTextKey.ChatTranslatedLabel to "翻訳済み",

    // Sharing
    UiTextKey.ShareTitle to "共有",
    UiTextKey.ShareInboxTitle to "共有受信箱",
    UiTextKey.ShareInboxEmpty to "共有アイテムはありません。フレンドが単語や教材を共有できます！",
    UiTextKey.ShareWordButton to "単語を共有",
    UiTextKey.ShareMaterialButton to "教材を共有",
    UiTextKey.ShareSelectFriendTitle to "フレンドを選択",
    UiTextKey.ShareSelectFriendMessage to "共有するフレンドを選択：",
    UiTextKey.ShareSuccess to "共有成功！",
    UiTextKey.ShareFailed to "共有に失敗",
    UiTextKey.ShareWordWith to "{username} と単語を共有",
    UiTextKey.ShareMaterialWith to "{username} と教材を共有",
    UiTextKey.ShareAcceptButton to "承認",
    UiTextKey.ShareDismissButton to "無視",
    UiTextKey.ShareAccepted to "コレクションに追加しました",
    UiTextKey.ShareDismissed to "アイテムを無視しました",
    UiTextKey.ShareActionFailed to "操作に失敗",
    UiTextKey.ShareTypeWord to "単語",
    UiTextKey.ShareTypeLearningSheet to "学習シート",
    UiTextKey.ShareTypeQuiz to "クイズ",
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
    UiTextKey.MyProfileDisplayName to "表示名",
    UiTextKey.MyProfileCopyUserId to "ユーザーIDをコピー",
    UiTextKey.MyProfileCopyUsername to "ユーザー名をコピー",
    UiTextKey.MyProfileShare to "プロフィールを共有",
    UiTextKey.MyProfileCopied to "クリップボードにコピーしました！",
    UiTextKey.MyProfileLanguages to "言語",
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
    UiTextKey.LearningEmptyTitle to "翻訳記録なし",
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
    UiTextKey.ShareLearningSheetLanguageLabel to "言語：{language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "閉じる",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "接続済みまたは保留中",
    UiTextKey.AccessibilityNewMessages to "新しいメッセージ",
    UiTextKey.AccessibilityNewReleasesIcon to "新しいアイテムの表示",
    UiTextKey.AccessibilitySuccessIcon to "成功",
    UiTextKey.AccessibilityErrorIcon to "エラー",
    UiTextKey.AccessibilitySharedItemTypeIcon to "共有アイテムの種類",
    UiTextKey.AccessibilityAddCustomWords to "カスタム単語を追加",
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
    UiTextKey.FriendsNotifCloseButton to "完了",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "アプリ内バッジ（赤い点）",
    UiTextKey.InAppBadgeMessages to "未読チャットメッセージバッジ",
    UiTextKey.InAppBadgeFriendRequests to "フレンドリクエストバッジ",
    UiTextKey.InAppBadgeSharedInbox to "未読共有受信箱バッジ",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "続行するにはログインしてください。",
    UiTextKey.ErrorSaveFailedRetry to "保存に失敗しました。再試行してください。",
    UiTextKey.ErrorLoadFailedRetry to "読み込みに失敗しました。再試行してください。",
    UiTextKey.ErrorNetworkRetry to "ネットワークエラーです。接続を確認してください。",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "教材生成にはあと {needed} 件の翻訳が必要",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "リアルタイム会話に切り替え →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "チャットをクリア",
    UiTextKey.ChatClearConversationTitle to "会話をクリア",
    UiTextKey.ChatClearConversationMessage to "この会話のすべてのメッセージを非表示にしますか？退出して戻っても非表示のままです。相手には影響しません。",
    UiTextKey.ChatClearConversationConfirm to "すべてクリア",
    UiTextKey.ChatClearConversationSuccess to "会話をクリアしました",

    // Block user
    UiTextKey.BlockUserButton to "ブロック",
    UiTextKey.BlockUserTitle to "ユーザーをブロックしますか？",
    UiTextKey.BlockUserMessage to "{username} をブロックしますか？フレンドリストから削除され、連絡できなくなります。",
    UiTextKey.BlockUserConfirm to "ブロック",
    UiTextKey.BlockUserSuccess to "ユーザーをブロックし、フレンドから削除しました。",
    UiTextKey.BlockedUsersTitle to "ブロックしたユーザー",
    UiTextKey.BlockedUsersEmpty to "ブロックしたユーザーはいません。",
    UiTextKey.UnblockUserButton to "ブロック解除",
    UiTextKey.UnblockUserTitle to "ブロック解除しますか？",
    UiTextKey.UnblockUserMessage to "{username} のブロックを解除しますか？再びフレンドリクエストを送れるようになります。",
    UiTextKey.UnblockUserSuccess to "ユーザーのブロックを解除しました。",
    UiTextKey.BlockedUsersManageButton to "ブロックユーザー管理",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "リクエストメモ（任意）",
    UiTextKey.FriendsRequestNotePlaceholder to "短いメモを追加...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "学習シートが完成しました！タップして開く。",
    UiTextKey.GenerationBannerWordBank to "単語帳が完成しました！タップして確認。",
    UiTextKey.GenerationBannerQuiz to "クイズが完成しました！タップして開始。",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "通知",

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
    UiTextKey.AddCustomWordHintTemplate to "{from} に単語を入力し、{to} に翻訳を入力",

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

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "インターネット接続がありません。接続を確認してください。",
    UiTextKey.ErrorPermissionDenied to "この操作を行う権限がありません。",
    UiTextKey.ErrorSessionExpired to "セッションが期限切れです。再度ログインしてください。",
    UiTextKey.ErrorItemNotFound to "リクエストされたアイテムが見つかりません。削除された可能性があります。",
    UiTextKey.ErrorAccessDenied to "アクセスが拒否されました。",
    UiTextKey.ErrorAlreadyFriends to "このユーザーとはすでにフレンドです。",
    UiTextKey.ErrorUserBlocked to "この操作を完了できません。ユーザーがブロックされている可能性があります。",
    UiTextKey.ErrorRequestNotFound to "このフレンドリクエストは存在しなくなりました。",
    UiTextKey.ErrorRequestAlreadyHandled to "このリクエストは既に処理されています。",
    UiTextKey.ErrorNotAuthorized to "この操作を行う権限がありません。",
    UiTextKey.ErrorRateLimited to "リクエストが多すぎます。しばらくしてから再試行してください。",
    UiTextKey.ErrorInvalidInput to "入力が無効です。確認して再試行してください。",
    UiTextKey.ErrorOperationNotAllowed to "現在この操作は許可されていません。",
    UiTextKey.ErrorTimeout to "操作がタイムアウトしました。再試行してください。",
    UiTextKey.ErrorSendMessageFailed to "メッセージの送信に失敗しました。再試行してください。",
    UiTextKey.ErrorFriendRequestSent to "フレンドリクエストを送信しました！",
    UiTextKey.ErrorFriendRequestFailed to "フレンドリクエストの送信に失敗しました。",
    UiTextKey.ErrorFriendRemoved to "フレンドを削除しました。",
    UiTextKey.ErrorFriendRemoveFailed to "フレンドの削除に失敗しました。接続を確認してください。",
    UiTextKey.ErrorBlockSuccess to "ユーザーをブロックしました。",
    UiTextKey.ErrorBlockFailed to "ブロックに失敗しました。再試行してください。",
    UiTextKey.ErrorUnblockSuccess to "ブロックを解除しました。",
    UiTextKey.ErrorUnblockFailed to "ブロック解除に失敗しました。再試行してください。",
    UiTextKey.ErrorAcceptRequestSuccess to "フレンドリクエストを承認しました！",
    UiTextKey.ErrorAcceptRequestFailed to "承認に失敗しました。再試行してください。",
    UiTextKey.ErrorRejectRequestSuccess to "フレンドリクエストを拒否しました。",
    UiTextKey.ErrorRejectRequestFailed to "拒否に失敗しました。再試行してください。",
    UiTextKey.ErrorOfflineMessage to "オフラインです。一部の機能が利用できない場合があります。",
    UiTextKey.ErrorChatDeletionFailed to "会話の削除に失敗しました。再試行してください。",
    UiTextKey.ErrorGenericRetry to "エラーが発生しました。再試行してください。",
)
