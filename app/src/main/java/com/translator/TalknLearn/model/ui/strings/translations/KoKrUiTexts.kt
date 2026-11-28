package com.translator.TalknLearn.model.ui

/**
 * Korean (ko-KR) UI text map — 한국어 인터페이스 텍스트.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val KoKrUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "마이크 사용",
    UiTextKey.CopyButton to "복사",
    UiTextKey.SpeakScriptButton to "원문 읽기",
    UiTextKey.TranslateButton to "번역",
    UiTextKey.CopyTranslationButton to "번역 복사",
    UiTextKey.SpeakTranslationButton to "번역 읽기",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "표시 언어",
    UiTextKey.DetectLanguageLabel to "언어 감지",
    UiTextKey.TranslateToLabel to "번역 대상",

    // Language names
    UiTextKey.LangEnUs to "영어",
    UiTextKey.LangZhHk to "광둥어",
    UiTextKey.LangJaJp to "일본어",
    UiTextKey.LangZhCn to "중국어(간체)",
    UiTextKey.LangFrFr to "프랑스어",
    UiTextKey.LangDeDe to "독일어",
    UiTextKey.LangKoKr to "한국어",
    UiTextKey.LangEsEs to "스페인어",
    UiTextKey.LangIdId to "인도네시아어",
    UiTextKey.LangViVn to "베트남어",
    UiTextKey.LangThTh to "태국어",
    UiTextKey.LangFilPh to "필리핀어",
    UiTextKey.LangMsMy to "말레이어",
    UiTextKey.LangPtBr to "포르투갈어",
    UiTextKey.LangItIt to "이탈리아어",
    UiTextKey.LangRuRu to "러시아어",

    // Navigation
    UiTextKey.NavHistory to "기록",
    UiTextKey.NavLogin to "로그인",
    UiTextKey.NavLogout to "로그아웃",
    UiTextKey.NavBack to "뒤로",
    UiTextKey.ActionCancel to "취소",
    UiTextKey.ActionDelete to "삭제",
    UiTextKey.ActionOpen to "열기",
    UiTextKey.ActionName to "이름 지정",
    UiTextKey.ActionSave to "저장",
    UiTextKey.ActionConfirm to "확인",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "여기에 입력하거나 마이크 사용...",
    UiTextKey.SpeechTranslatedPlaceholder to "번역 결과가 여기에 표시됩니다...",

    // Pagination
    UiTextKey.PaginationPrevLabel to "이전 페이지",
    UiTextKey.PaginationNextLabel to "다음 페이지",
    UiTextKey.PaginationPageLabelTemplate to "{page} / {total} 페이지",

    // Toast
    UiTextKey.DisableText to "번역 기능과 기록 저장을 사용하려면 로그인이 필요합니다.",

    // Error
    UiTextKey.ErrorRetryButton to "재시도",

    // Shop
    UiTextKey.ShopTitle to "상점",
    UiTextKey.ShopCoinBalance to "내 코인",
    UiTextKey.ShopHistoryExpansionTitle to "기록 한도 확장",
    UiTextKey.ShopHistoryExpansionDesc to "기록 표시 한도를 확장하여 더 많은 번역 기록을 확인하세요.",
    UiTextKey.ShopCurrentLimit to "현재 한도: {limit} 건",
    UiTextKey.ShopMaxLimit to "최대 한도:",
    UiTextKey.ShopBuyHistoryExpansion to "구매 (+{increment} 건, {cost} 코인)",
    UiTextKey.ShopInsufficientCoins to "코인 부족",
    UiTextKey.ShopMaxLimitReached to "최대 한도에 도달했습니다",
    UiTextKey.ShopHistoryExpandedTitle to "확장 성공!",
    UiTextKey.ShopHistoryExpandedMessage to "기록 한도가 {limit} 건으로 확장되었습니다! 더 많은 번역 기록을 확인할 수 있습니다!",
    UiTextKey.ShopColorPaletteTitle to "컬러 테마",
    UiTextKey.ShopColorPaletteDesc to "앱의 컬러 테마를 선택하세요, 각 테마 10코인",
    UiTextKey.ShopEntry to "상점",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "음성 설정",
    UiTextKey.VoiceSettingsDesc to "각 언어의 텍스트 읽기 음성을 선택합니다.",

    // Instructions
    UiTextKey.SpeechInstructions to "마이크 버튼을 탭하여 음성 인식을 시작하고, 완료 후 번역을 탭하세요. 텍스트나 언어 변경 후 자동 감지가 업데이트되지 않으면 오른쪽 상단의 새로고침을 탭하세요.",
    UiTextKey.HomeInstructions to "기능을 선택하여 시작하세요.",
    UiTextKey.ContinuousInstructions to "두 언어를 선택하고 대화 모드를 시작하세요.",

    // Home
    UiTextKey.HomeTitle to "즉시 번역",
    UiTextKey.HelpTitle to "도움말",
    UiTextKey.SpeechTitle to "빠른 번역",
    UiTextKey.HomeStartButton to "번역 시작",

    // Help
    UiTextKey.HelpCurrentTitle to "현재 기능",
    UiTextKey.HelpCautionTitle to "주의사항",
    UiTextKey.HelpCurrentFeatures to "현재 기능:\n" +
            "  • 빠른 번역: 음성 인식 후 번역\n" +
            "  • 실시간 대화: 양방향 음성 번역\n" +
            "  • 기록: 번역 기록 보기\n" +
            "  • 학습 자료: 기록에서 어휘와 퀴즈 생성\n\n" +
            "번역:\n" +
            "  • Azure AI 음성 인식 사용\n" +
            "  • Azure 번역 서비스 사용\n",
    UiTextKey.HelpCaution to "주의사항:\n" +
            "  • 음성 인식에는 인터넷 연결이 필요합니다\n" +
            "  • 로컬 번역 캐시는 오프라인에서도 사용 가능\n" +
            "  • 중요한 번역은 전문 서비스로 확인하세요\n\n" +
            "계정 및 데이터:\n" +
            "  • 기록, 학습, 코인 기능에는 로그인이 필요합니다\n" +
            "  • 사용자 데이터는 Firebase Firestore에 안전하게 저장됩니다\n\n" +
            "문제 해결:\n" +
            "  • 모든 단계를 시도해도 작동하지 않으면 앱을 재시작하세요\n",
    UiTextKey.HelpNotesTitle to "메모",
    UiTextKey.HelpNotes to "💡 사용 팁과 문제 해결:\n\n" +
            "최적의 번역을 위해:\n" +
            "  • 명확하고 적절한 속도로 말하세요\n" +
            "  • 배경 소음을 줄여 인식 정확도를 높이세요\n" +
            "  • 빠른 번역은 짧은 문장에 적합합니다\n\n" +
            "표시 언어:\n" +
            "  • 기본 언어는 영어, 다른 언어는 AI 번역\n" +
            "  • 광둥어 버전은 수동 번역으로 정확도가 높습니다\n" +
            "업데이트 및 피드백:\n" +
            "  • 앱 버전은 설정 → 정보에서 확인\n" +
            "  • 설정 → 피드백에서 의견을 보내주세요\n",

    // Feedback
    UiTextKey.FeedbackTitle to "피드백",
    UiTextKey.FeedbackDesc to "피드백 감사합니다! 제안, 버그 신고, 앱에 대한 의견을 알려주세요.",
    UiTextKey.FeedbackMessagePlaceholder to "피드백을 입력해 주세요...",
    UiTextKey.FeedbackSubmitButton to "피드백 보내기",
    UiTextKey.FeedbackSubmitting to "보내는 중...",
    UiTextKey.FeedbackSuccessTitle to "감사합니다!",
    UiTextKey.FeedbackSuccessMessage to "피드백이 성공적으로 전송되었습니다. 소중한 의견 감사합니다!",
    UiTextKey.FeedbackErrorTitle to "전송 실패",
    UiTextKey.FeedbackErrorMessage to "피드백 전송에 실패했습니다. 연결을 확인하고 다시 시도해 주세요.",
    UiTextKey.FeedbackMessageRequired to "피드백 내용을 입력해 주세요.",

    // Continuous mode
    UiTextKey.ContinuousTitle to "실시간 대화",
    UiTextKey.ContinuousStartButton to "대화 시작",
    UiTextKey.ContinuousStopButton to "녹음 중지",
    UiTextKey.ContinuousStartScreenButton to "실시간 대화",
    UiTextKey.ContinuousPersonALabel to "A가 말하고 있습니다",
    UiTextKey.ContinuousPersonBLabel to "B가 말하고 있습니다",
    UiTextKey.ContinuousCurrentStringLabel to "현재 텍스트:",
    UiTextKey.ContinuousSpeakerAName to "사람 A",
    UiTextKey.ContinuousSpeakerBName to "사람 B",
    UiTextKey.ContinuousTranslationSuffix to " · 번역",
    UiTextKey.ContinuousPreparingMicText to "마이크 준비 중...(말하지 마세요)",
    UiTextKey.ContinuousTranslatingText to "번역 중...",

    // History
    UiTextKey.HistoryTitle to "기록",
    UiTextKey.HistoryTabDiscrete to "빠른 번역",
    UiTextKey.HistoryTabContinuous to "실시간 대화",
    UiTextKey.HistoryNoContinuousSessions to "대화 기록이 없습니다.",
    UiTextKey.HistoryNoDiscreteRecords to "번역 기록이 없습니다.",
    UiTextKey.DialogDeleteRecordTitle to "기록을 삭제하시겠습니까?",
    UiTextKey.DialogDeleteRecordMessage to "이 작업은 되돌릴 수 없습니다.",
    UiTextKey.DialogDeleteSessionTitle to "대화를 삭제하시겠습니까?",
    UiTextKey.DialogDeleteSessionMessage to "이 대화의 모든 기록이 삭제됩니다. 되돌릴 수 없습니다.",
    UiTextKey.HistoryDeleteSessionButton to "삭제",
    UiTextKey.HistoryNameSessionTitle to "이름 지정",
    UiTextKey.HistorySessionNameLabel to "대화 이름",
    UiTextKey.HistorySessionTitleTemplate to "대화 {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} 건",

    // Filter
    UiTextKey.FilterDropdownDefault to "모든 언어",
    UiTextKey.FilterTitle to "기록 필터",
    UiTextKey.FilterLangDrop to "언어",
    UiTextKey.FilterKeyword to "키워드",
    UiTextKey.FilterApply to "적용",
    UiTextKey.FilterCancel to "취소",
    UiTextKey.FilterClear to "초기화",
    UiTextKey.FilterHistoryScreenTitle to "필터",

    // Auth
    UiTextKey.AuthLoginTitle to "로그인",
    UiTextKey.AuthRegisterTitle to "등록 (중지)",
    UiTextKey.AuthLoginHint to "등록된 이메일과 비밀번호를 사용해 주세요.",
    UiTextKey.AuthRegisterRules to "개발 단계이므로 등록 기능이 중지되었습니다.\n주의: 존재하지 않는 이메일을 사용하면 비밀번호를 재설정할 수 없습니다.\n" +
            "등록 규칙:\n" +
            "• 올바른 이메일 형식 (예: name@example.com)\n" +
            "• 비밀번호는 8자 이상\n" +
            "• 확인 비밀번호 일치",
    UiTextKey.AuthEmailLabel to "이메일",
    UiTextKey.AuthPasswordLabel to "비밀번호",
    UiTextKey.AuthConfirmPasswordLabel to "비밀번호 확인",
        UiTextKey.AuthLoginButton to "로그인",
        UiTextKey.AuthGoogleSignInButton to "Google로 로그인",
    UiTextKey.AuthRegisterButton to "등록",
    UiTextKey.AuthToggleToRegister to "계정이 없으신가요? 등록 (중지)",
    UiTextKey.AuthToggleToLogin to "계정이 있으신가요? 로그인",
    UiTextKey.AuthErrorPasswordsMismatch to "비밀번호가 일치하지 않습니다.",
    UiTextKey.AuthErrorPasswordTooShort to "비밀번호는 8자 이상이어야 합니다.",
    UiTextKey.AuthRegistrationDisabled to "개발 단계이므로 등록 기능이 중지되었습니다.",
    UiTextKey.AuthResetEmailSent to "재설정 이메일을 전송했습니다 (이메일이 존재하는 경우). 받은편지함을 확인하세요.",

    // Password reset
    UiTextKey.ForgotPwText to "비밀번호를 잊으셨나요?",
    UiTextKey.ResetPwTitle to "비밀번호 재설정",
    UiTextKey.ResetPwText to "계정 이메일 주소를 입력하세요. 재설정 링크를 전송합니다.\n앱에 등록된 이메일인지 확인해 주세요.\n",
    UiTextKey.ResetSendingText to "전송 중...",
    UiTextKey.ResetSendText to "재설정 이메일 전송",

    // Settings
    UiTextKey.SettingsTitle to "설정",
    UiTextKey.SettingsPrimaryLanguageTitle to "주요 언어",
    UiTextKey.SettingsPrimaryLanguageDesc to "학습 설명과 제안에 사용",
    UiTextKey.SettingsPrimaryLanguageLabel to "주요 언어",
    UiTextKey.SettingsFontSizeTitle to "글꼴 크기",
    UiTextKey.SettingsFontSizeDesc to "텍스트 크기를 조절하여 가독성을 높입니다 (기기 간 동기화)",
    UiTextKey.SettingsScaleTemplate to "크기: {pct}%",
    UiTextKey.SettingsColorCostTemplate to "{cost} 코인",
    UiTextKey.SettingsColorSelectButton to "선택",
    UiTextKey.SettingsColorAlreadyUnlocked to "해제됨",
    UiTextKey.SettingsPreviewHeadline to "제목: 큰 텍스트 미리보기",
    UiTextKey.SettingsPreviewBody to "본문: 일반 텍스트 미리보기",
    UiTextKey.SettingsPreviewLabel to "라벨: 작은 텍스트 미리보기",
    UiTextKey.SettingsAboutTitle to "정보",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "로그인하면 설정이 자동으로 계정에 저장·동기화됩니다.",
    UiTextKey.SettingsThemeTitle to "테마",
    UiTextKey.SettingsThemeDesc to "외관 적용 방식 선택: 시스템, 라이트, 다크 또는 예약.",
    UiTextKey.SettingsThemeSystem to "시스템",
    UiTextKey.SettingsThemeLight to "라이트",
    UiTextKey.SettingsThemeDark to "다크",
    UiTextKey.SettingsThemeScheduled to "예약",
    UiTextKey.SettingsResetPW to "비밀번호 재설정",
    UiTextKey.SettingsQuickLinks to "상세 설정",
    UiTextKey.SettingsNotLoggedInWarning to "계정 설정을 사용하려면 로그인하세요. 앱 언어는 변경 가능합니다.",
    UiTextKey.SettingsVoiceLanguageLabel to "언어",
    UiTextKey.SettingsVoiceSelectLabel to "음성",
    UiTextKey.SettingsVoiceDefault to "기본값",

    // Learning
    UiTextKey.LearningTitle to "학습",
    UiTextKey.LearningHintCount to "(*) 횟수 = 해당 언어가 포함된 번역 기록 수.",
    UiTextKey.LearningErrorTemplate to "오류: %s",
    UiTextKey.LearningGenerate to "생성",
    UiTextKey.LearningRegenerate to "재생성",
    UiTextKey.LearningGenerating to "생성 중...",
    UiTextKey.LearningOpenSheetTemplate to "{speclanguage} 학습 시트",
    UiTextKey.LearningSheetTitleTemplate to "{speclanguage} 학습 시트",
    UiTextKey.LearningSheetPrimaryTemplate to "주요 언어: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "현재 기록 수: {nowCount} (생성 시: {savedCount})",
    UiTextKey.LearningSheetNoContent to "학습 시트 내용이 없습니다.",
    UiTextKey.LearningSheetRegenerate to "재생성",
    UiTextKey.LearningSheetGenerating to "생성 중...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 이것은 무엇인가요?",
    UiTextKey.LearningSheetWhatIsThisDesc to "번역 기록을 기반으로 생성된 학습 시트입니다. 어휘, 의미, 예문, 문법 포인트가 포함되어 있습니다. 아래 퀴즈 버튼으로 지식을 테스트하세요!",
    UiTextKey.LearningRegenBlockedTitle to "현재 재생성할 수 없습니다",
    UiTextKey.LearningRegenBlockedMessage to "재생성하려면 이전보다 최소 5건 이상의 기록이 필요합니다. {needed} 건 더 필요합니다.",
    UiTextKey.LearningRegenInfoTitle to "재생성 규칙",
    UiTextKey.LearningRegenInfoMessage to "학습 자료를 재생성하려면:\n\n• 첫 생성: 언제든 가능\n• 재생성: 이전보다 최소 5건 이상의 번역 기록 필요\n\n충분한 새 기록이 있으면 버튼이 활성화(파란색)됩니다. 회색이면 번역을 계속하세요!\n\n💡 힌트: 번역 후 건수가 업데이트되지 않으면 앱을 재시작하세요.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "퀴즈: {language}",
    UiTextKey.QuizOpenButton to "📝 퀴즈",
    UiTextKey.QuizGenerateButton to "🔄 퀴즈 생성",
    UiTextKey.QuizGenerating to "⏳ 생성 중...",
    UiTextKey.QuizBlocked to "🚫 차단됨",
    UiTextKey.QuizWait to "⏳ 잠시만...",
    UiTextKey.QuizCanEarnCoins to "🪙 코인을 획득할 수 있습니다!",
    UiTextKey.QuizCancelButton to "취소",
    UiTextKey.QuizPreviousButton to "이전 문제",
    UiTextKey.QuizNextButton to "다음 문제",
    UiTextKey.QuizSubmitButton to "제출",
    UiTextKey.QuizRetakeButton to "다시 풀기",
    UiTextKey.QuizBackButton to "뒤로",
    UiTextKey.QuizLoadingText to "퀴즈 로딩 중...",
    UiTextKey.QuizGeneratingText to "퀴즈 생성 중...",
    UiTextKey.QuizNoMaterialsTitle to "학습 자료를 찾을 수 없습니다",
    UiTextKey.QuizNoMaterialsMessage to "먼저 학습 자료를 생성한 후 퀴즈를 확인하세요.",
    UiTextKey.QuizErrorTitle to "⚠️ 퀴즈 오류",
    UiTextKey.QuizErrorSuggestion to "제안: 위의 버튼으로 퀴즈를 생성하세요.",
    UiTextKey.QuizCompletedTitle to "퀴즈 완료!",
    UiTextKey.QuizAnswerReviewTitle to "답안 검토",
    UiTextKey.QuizYourAnswerTemplate to "내 답: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "정답: {Answer}",
    UiTextKey.QuizQuestionTemplate to "문제 {current} / {total}",
    UiTextKey.QuizAnotherGenInProgress to "⏳ 다른 생성이 진행 중입니다. 잠시 기다려 주세요.",
    UiTextKey.QuizCoinRulesTitle to "🪙 코인 획득 규칙",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ 획득 방법:",
    UiTextKey.QuizCoinRulesRequirements to "조건:",
    UiTextKey.QuizCoinRulesCurrentStatus to "현재 상태:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ 다음 퀴즈에서 코인을 획득할 수 있습니다!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• 코인 획득에 {count} 건 더 필요",
    UiTextKey.QuizCoinRule1Coin to "• 정답당 1코인",
    UiTextKey.QuizCoinRuleFirstAttempt to "• 각 퀴즈 버전의 첫 시도만 유효",
    UiTextKey.QuizCoinRuleMatchMaterials to "• 퀴즈는 자료 버전과 일치해야 함",
    UiTextKey.QuizCoinRulePlus10 to "• 이전 코인 획득 퀴즈보다 10건 이상의 기록 필요",
    UiTextKey.QuizCoinRuleNoDelete to "• 기록 삭제 후 코인을 다시 획득할 수 없음",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• 자료: {count} 건",
    UiTextKey.QuizCoinRuleQuizTemplate to "• 퀴즈: {count} 건",
    UiTextKey.QuizCoinRuleGotIt to "확인!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 새 퀴즈를 생성하시겠습니까?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ 이 퀴즈에서 코인을 획득할 수 있습니다! (첫 시도만)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ 이 퀴즈에서는 코인을 획득할 수 없습니다.",
    UiTextKey.QuizRegenNeedMoreTemplate to "코인 자격을 위해 {count} 건의 번역 기록이 더 필요합니다 (이전 코인 획득보다 10건 이상).",
    UiTextKey.QuizRegenReminder to "힌트: 연습과 재시도는 가능하지만, 코인은 첫 시도이며 충분한 기록이 있는 경우에만 부여됩니다.",
    UiTextKey.QuizRegenGenerateButton to "생성",
    UiTextKey.QuizCoinsEarnedTitle to "✨ 코인 획득!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "축하합니다! {Coins} 코인을 획득했습니다!",
    UiTextKey.QuizCoinsRule1 to "• 첫 시도에만 정답당 1코인",
    UiTextKey.QuizCoinsRule2 to "• 같은 퀴즈의 재시도에는 코인 없음",
    UiTextKey.QuizCoinsRule3 to "• 새 퀴즈에는 이전보다 10건 이상의 기록 필요",
    UiTextKey.QuizCoinsRule4 to "• 퀴즈는 현재 자료 버전과 일치해야 함",
    UiTextKey.QuizCoinsRule5 to "• 총 코인은 기록 페이지에서 확인",
    UiTextKey.QuizCoinsGreatButton to "좋아요!",
    UiTextKey.QuizRecordsLabel to "건의 기록",

    // History coins

    // History info
    UiTextKey.HistoryInfoTitle to "기록 정보",
    UiTextKey.HistoryInfoLimitMessage to "기록은 최신 {limit} 건을 표시합니다. 상점에서 한도를 확장할 수 있습니다!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "이전 기록은 저장되어 있지만 성능을 위해 숨겨져 있습니다.",
    UiTextKey.HistoryInfoFavoritesMessage to "중요한 번역을 영구 저장하려면 기록의 ❤️ 아이콘을 탭하여 즐겨찾기에 추가하세요.",
    UiTextKey.HistoryInfoViewFavoritesMessage to "설정 → 즐겨찾기에서 저장된 기록을 확인하세요.",
    UiTextKey.HistoryInfoFilterMessage to "필터 버튼으로 표시 중인 {limit} 건에서 검색하세요.",
    UiTextKey.HistoryInfoGotItButton to "확인",

    // Word bank
    UiTextKey.WordBankTitle to "단어장",
    UiTextKey.WordBankSelectLanguage to "언어를 선택하여 단어장을 보거나 생성하세요:",
    UiTextKey.WordBankNoHistory to "번역 기록 없음",
    UiTextKey.WordBankNoHistoryHint to "번역을 시작하여 단어장을 만드세요!",
    UiTextKey.WordBankWordsCount to "어",
    UiTextKey.WordBankGenerating to "생성 중...",
    UiTextKey.WordBankGenerate to "단어장 생성",
    UiTextKey.WordBankRefresh to "🔄 단어장 업데이트",
    UiTextKey.WordBankEmpty to "단어장 없음",
    UiTextKey.WordBankEmptyHint to "위의 버튼을 탭하여 번역 기록에서 단어장을 생성하세요.",
    UiTextKey.WordBankExample to "예문:",
    UiTextKey.WordBankDifficulty to "난이도:",
    UiTextKey.WordBankFilterCategory to "카테고리",
    UiTextKey.WordBankFilterCategoryAll to "모든 카테고리",
    UiTextKey.WordBankFilterDifficultyLabel to "난이도:",
    UiTextKey.WordBankFilterNoResults to "필터 조건에 맞는 단어가 없습니다",
    UiTextKey.WordBankRegenInfoTitle to "업데이트 규칙",
    UiTextKey.WordBankRegenInfoMessage to "단어장을 업데이트하려면:\n\n• 첫 생성: 언제든 가능\n• 업데이트: 이전보다 최소 20건 이상의 번역 기록 필요\n\n충분한 새 기록이 있으면 버튼이 활성화(파란색)됩니다. 회색이면 번역을 계속하세요!\n\n💡 힌트: 번역 후 건수가 업데이트되지 않으면 앱을 재시작하세요.",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "로그아웃하시겠습니까?",
    UiTextKey.DialogLogoutMessage to "번역 기능 사용과 기록 저장·보기에는 다시 로그인이 필요합니다.",
    UiTextKey.DialogGenerateOverwriteTitle to "자료를 덮어쓰시겠습니까?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "이전 자료가 덮어쓰여집니다 (있는 경우).\n{speclanguage}의 자료를 생성하시겠습니까?",

    // Profile
    UiTextKey.ProfileTitle to "프로필",
    UiTextKey.ProfileUsernameLabel to "사용자명",
    UiTextKey.ProfileUsernameHint to "사용자명 입력",
    UiTextKey.ProfileUpdateButton to "프로필 업데이트",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "계정 삭제",
    UiTextKey.AccountDeleteWarning to "⚠️ 이 작업은 영구적으로 되돌릴 수 없습니다!",
    UiTextKey.AccountDeleteConfirmMessage to "기록, 단어장, 학습 자료, 설정을 포함한 모든 데이터가 영구 삭제됩니다. 비밀번호를 입력하여 확인하세요.",
    UiTextKey.AccountDeletePasswordLabel to "비밀번호",
        UiTextKey.AccountDeleteButton to "계정 및 데이터 삭제",

    // Favorites
    UiTextKey.FavoritesTitle to "즐겨찾기",
    UiTextKey.FavoritesEmpty to "즐겨찾기 없음",
    UiTextKey.FavoritesTabRecords to "기록",
    UiTextKey.FavoritesTabSessions to "대화",
    UiTextKey.FavoritesSessionsEmpty to "저장된 대화 없음",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} 건의 메시지",

    // Custom words
    UiTextKey.CustomWordsTitle to "맞춤 단어",
    UiTextKey.CustomWordsAdd to "단어 추가",
    UiTextKey.CustomWordsEdit to "단어 편집",
    UiTextKey.CustomWordsDelete to "단어 삭제",
    UiTextKey.CustomWordsOriginalLabel to "원어",
    UiTextKey.CustomWordsTranslatedLabel to "번역",
    UiTextKey.CustomWordsPronunciationLabel to "발음 (선택)",
    UiTextKey.CustomWordsExampleLabel to "예문 (선택)",
    UiTextKey.CustomWordsOriginalLanguageLabel to "원어 언어",
    UiTextKey.CustomWordsTranslationLanguageLabel to "번역 언어",
    UiTextKey.CustomWordsSaveButton to "저장",
    UiTextKey.CustomWordsCancelButton to "취소",

    // Language detection
    UiTextKey.LanguageDetectAuto to "자동 감지",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "이미지에서 텍스트 스캔",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ 주의: 이미지 텍스트 인식은 완전히 정확하지 않을 수 있습니다. 추출된 텍스트를 확인하세요." +
            "라틴 문자 (영어 등), 중국어, 일본어, 한국어를 지원합니다.",

    // Cache

    // Auto theme
    UiTextKey.SettingsAutoThemePreview to "테마는 설정한 시간에 자동으로 전환됩니다",

    // Offline mode
    UiTextKey.OfflineSyncPending to "온라인 시 변경사항을 동기화합니다",

    // Image capture
    UiTextKey.ImageSourceTitle to "이미지 소스 선택",
    UiTextKey.ImageSourceCamera to "카메라로 촬영",
    UiTextKey.ImageSourceGallery to "갤러리에서 선택",
    UiTextKey.ImageSourceCancel to "취소",
    UiTextKey.CameraCaptureContentDesc to "촬영",

    // Friends
    UiTextKey.FriendsTitle to "친구",
    UiTextKey.FriendsAddButton to "친구 추가",
    UiTextKey.FriendsSearchTitle to "사용자 검색",
    UiTextKey.FriendsSearchPlaceholder to "사용자명 또는 사용자 ID 입력...",
    UiTextKey.FriendsSearchNoResults to "사용자를 찾을 수 없습니다",
    UiTextKey.FriendsRequestsSection to "친구 요청 ({count})",
    UiTextKey.FriendsSectionTitle to "친구 ({count})",
    UiTextKey.FriendsAcceptButton to "수락",
    UiTextKey.FriendsRejectButton to "거절",
    UiTextKey.FriendsSendRequestButton to "추가",
    UiTextKey.FriendsCloseButton to "닫기",
    UiTextKey.FriendsCancelButton to "취소",
    UiTextKey.FriendsRemoveConfirm to "삭제",
    UiTextKey.FriendsNewRequestsTemplate to "새로운 친구 요청이 {count} 건 있습니다!",
    UiTextKey.FriendsSentRequestsSection to "보낸 요청 ({count})",
    UiTextKey.FriendsPendingStatus to "대기 중",
    UiTextKey.FriendsCancelRequestButton to "요청 취소",
    UiTextKey.FriendsUnreadMessageDesc to "메시지 보내기",
    UiTextKey.FriendsDeleteModeButton to "친구 삭제",
    UiTextKey.FriendsDeleteMultipleTitle to "친구 삭제",
    UiTextKey.FriendsDeleteMultipleMessage to "선택한 {count} 명의 친구를 삭제하시겠습니까?",
    UiTextKey.FriendsSearchMinChars3 to "사용자명 검색에 3자 이상 입력하세요",
    UiTextKey.FriendsSearchByUserIdHint to "또는 사용자 ID로 정확히 검색",
    UiTextKey.FriendsStatusAlreadyFriends to "이미 친구입니다",
    UiTextKey.FriendsStatusRequestSent to "요청 전송됨 — 응답 대기 중",
    UiTextKey.FriendsStatusRequestReceived to "이 사용자로부터 요청이 왔습니다",

    // Chat
    UiTextKey.ChatTitle to "{username}와(과) 채팅",
    UiTextKey.ChatInputPlaceholder to "메시지 입력...",
    UiTextKey.ChatSendButton to "보내기",
    UiTextKey.ChatEmpty to "메시지가 없습니다. 채팅을 시작하세요!",
    UiTextKey.ChatUnreadBadge to "{count} 건 읽지 않음",
    UiTextKey.ChatTranslateButton to "번역",
    UiTextKey.ChatTranslateDialogTitle to "대화 번역",
    UiTextKey.ChatTranslateDialogMessage to "친구의 메시지를 내 언어로 번역하시겠습니까? 각 메시지의 언어를 감지하여 번역합니다.",
    UiTextKey.ChatTranslateConfirm to "모두 번역",
    UiTextKey.ChatShowOriginal to "원문 보기",
    UiTextKey.ChatShowTranslation to "번역 보기",
    UiTextKey.ChatTranslatedLabel to "번역됨",

    // Sharing
    UiTextKey.ShareInboxTitle to "공유 받은편지함",
    UiTextKey.ShareInboxEmpty to "공유 항목이 없습니다. 친구가 단어와 자료를 공유할 수 있습니다!",
    UiTextKey.ShareWordButton to "단어 공유",
    UiTextKey.ShareMaterialButton to "자료 공유",
    UiTextKey.ShareSelectFriendTitle to "친구 선택",
    UiTextKey.ShareSelectFriendMessage to "공유할 친구를 선택하세요:",
    UiTextKey.ShareSuccess to "공유 성공!",
    UiTextKey.ShareAcceptButton to "수락",
    UiTextKey.ShareDismissButton to "무시",
    UiTextKey.ShareTypeWord to "단어",
    UiTextKey.ShareTypeLearningSheet to "학습 시트",
    UiTextKey.ShareReceivedFrom to "보낸이: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} 건의 새 항목!",
    UiTextKey.ShareViewFullMaterial to "\"보기\"를 탭하여 전체 자료 열람",
    UiTextKey.ShareDeleteItemTitle to "항목 삭제",
    UiTextKey.ShareDeleteItemMessage to "이 공유 항목을 삭제하시겠습니까? 되돌릴 수 없습니다.",
    UiTextKey.ShareDeleteButton to "삭제",
    UiTextKey.ShareViewButton to "보기",
    UiTextKey.ShareItemNotFound to "항목을 찾을 수 없습니다.",
    UiTextKey.ShareNoContent to "이 자료에 내용이 없습니다.",
    UiTextKey.ShareSaveToSelf to "내 받은편지함에 저장",
    UiTextKey.ShareSavedToSelf to "받은편지함에 저장했습니다!",

    // My profile
    UiTextKey.MyProfileTitle to "내 프로필",
    UiTextKey.MyProfileUserId to "사용자 ID",
    UiTextKey.MyProfileUsername to "사용자명",
    UiTextKey.MyProfileCopyUserId to "사용자 ID 복사",
    UiTextKey.MyProfileCopyUsername to "사용자명 복사",
    UiTextKey.MyProfileShare to "프로필 공유",
    UiTextKey.MyProfileCopied to "클립보드에 복사했습니다!",
    UiTextKey.MyProfilePrimaryLanguage to "주요 언어",
    UiTextKey.MyProfileLearningLanguages to "학습 중인 언어",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "친구 페이지 정보",
    UiTextKey.FriendsInfoMessage to "• 아래로 스와이프하여 친구 목록, 요청, 상태를 수동 업데이트.\n" +
            "• 친구 카드를 탭하여 채팅 열기.\n" +
            "• 카드의 빨간 점(●)은 읽지 않은 메시지를 나타내며, ✓✓로 일괄 읽음 처리 가능.\n" +
            "• 📥 받은편지함 아이콘으로 공유 자료 확인, ✓✓로 빨간 점 제거.\n" +
            "• 친구 카드의 🚫 아이콘으로 차단 — 상대는 목록에서 삭제되고 연락 불가.\n" +
            "• 차단하면 상대와의 채팅 기록도 삭제됩니다.\n" +
            "• 휴지통 아이콘으로 삭제 모드에 들어가 친구 삭제.\n" +
            "• 친구 삭제 시 모든 채팅 메시지도 삭제됩니다.\n" +
            "• 검색 버튼으로 사용자명 또는 ID로 새 친구 검색·추가.\n" +
            "• 푸시 알림은 기본으로 꺼져 있음 — 알림 설정에서 활성화 가능.\n",
    UiTextKey.FriendsEmptyTitle to "친구가 없습니다",
    UiTextKey.FriendsEmptyMessage to "\"친구 추가\" 버튼으로 사용자명 또는 ID로 친구를 검색하세요.\n",
    UiTextKey.FriendsInfoGotItButton to "확인",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "학습 페이지 정보",
    UiTextKey.LearningInfoMessage to "• 아래로 스와이프하여 언어 기록 수 수동 업데이트.\n" +
            "• 각 카드는 언어와 번역 기록 수를 표시.\n" +
            "• \"생성\"을 탭하여 학습 시트 생성 (첫 생성은 무료).\n" +
            "• 재생성에는 이전보다 최소 5건 이상의 기록 필요.\n" +
            "• 학습 시트 버튼으로 생성된 자료를 열어 학습.\n" +
            "• 학습 시트 생성 후 퀴즈도 풀 수 있습니다.",
    UiTextKey.LearningEmptyMessage to "번역을 시작하여 기록을 만드세요.\n" +
            "학습 시트는 번역 기록에서 생성됩니다.\n" +
            "번역 후 아래로 스와이프하여 업데이트.",
    UiTextKey.LearningInfoGotItButton to "확인",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "단어장 페이지 정보",
    UiTextKey.WordBankInfoMessage to "• 아래로 스와이프하여 단어장 언어 목록 수동 업데이트.\n" +
            "• 언어를 선택하여 단어장 보기 또는 생성.\n" +
            "• 단어장은 번역 기록에서 생성됩니다.\n" +
            "• 업데이트에는 이전보다 최소 20건 이상의 기록 필요.\n" +
            "• 맞춤 단어 기능으로 수동으로 단어 추가 가능.\n" +
            "• 친구와 단어장의 단어를 공유 가능.",
    UiTextKey.WordBankInfoGotItButton to "확인",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "공유 받은편지함 정보",
    UiTextKey.ShareInboxInfoMessage to "• 아래로 스와이프하여 공유 받은편지함 수동 업데이트.\n" +
            "• 친구가 공유한 항목이 여기에 표시됩니다.\n" +
            "• 단어는 수락하여 단어장에 추가하거나 무시 가능.\n" +
            "• 학습 시트와 퀴즈는 \"보기\"로 상세 열람.\n" +
            "• 빨간 점(●)은 새로운/읽지 않은 항목을 나타냅니다.\n" +
            "• 공유 단어를 무시하기 전에 확인이 필요합니다.",
    UiTextKey.ShareInboxInfoGotItButton to "확인",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "프로필 공개 설정",
    UiTextKey.MyProfileVisibilityPublic to "공개",
    UiTextKey.MyProfileVisibilityPrivate to "비공개",
    UiTextKey.MyProfileVisibilityDescription to "공개: 누구나 검색하여 친구 추가 가능.\n비공개: 검색에서 찾을 수 없습니다.",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "단어 무시",
    UiTextKey.ShareDismissWordMessage to "이 공유 단어를 무시하시겠습니까? 되돌릴 수 없습니다.",

    // Shared inbox sheet label

    // Accessibility
    UiTextKey.AccessibilityDismiss to "닫기",
    UiTextKey.AccessibilityNewMessages to "새 메시지",
    UiTextKey.AccessibilityNewReleasesIcon to "새 항목 표시",
    UiTextKey.AccessibilitySuccessIcon to "성공",
    UiTextKey.AccessibilityErrorIcon to "오류",
    UiTextKey.AccessibilitySharedItemTypeIcon to "공유 항목 유형",
    UiTextKey.AccessibilityWordBankExists to "단어장 존재",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.피드백",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "알림 설정",
    UiTextKey.FriendsNotifSettingsTitle to "알림 설정",
    UiTextKey.FriendsNotifNewMessages to "새 채팅 메시지",
    UiTextKey.FriendsNotifFriendRequests to "친구 요청 수신",
    UiTextKey.FriendsNotifRequestAccepted to "친구 요청 수락됨",
    UiTextKey.FriendsNotifSharedInbox to "새 공유 항목",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "앱 내 배지 (빨간 점)",
    UiTextKey.InAppBadgeMessages to "읽지 않은 채팅 메시지 배지",
    UiTextKey.InAppBadgeFriendRequests to "친구 요청 배지",
    UiTextKey.InAppBadgeSharedInbox to "읽지 않은 공유 받은편지함 배지",

    // Error messages

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "자료 생성에 {needed} 건의 번역이 더 필요합니다",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "실시간 대화로 전환 →",

    // Chat clear
    UiTextKey.ChatClearConversationTitle to "대화 지우기",
    UiTextKey.ChatClearConversationMessage to "이 대화의 모든 메시지를 숨기시겠습니까? 나가고 돌아와도 숨겨진 상태입니다. 상대방에게는 영향 없습니다.",
    UiTextKey.ChatClearConversationConfirm to "모두 지우기",
    UiTextKey.ChatClearConversationSuccess to "대화를 지웠습니다",

    // Block user
    UiTextKey.BlockUserButton to "차단",
    UiTextKey.BlockUserTitle to "사용자를 차단하시겠습니까?",
    UiTextKey.BlockUserMessage to "{username}을(를) 차단하시겠습니까? 친구 목록에서 삭제되고 연락할 수 없게 됩니다.",
    UiTextKey.BlockUserConfirm to "차단",
    UiTextKey.BlockedUsersTitle to "차단한 사용자",
    UiTextKey.BlockedUsersEmpty to "차단한 사용자가 없습니다.",
    UiTextKey.UnblockUserButton to "차단 해제",
    UiTextKey.UnblockUserTitle to "차단 해제하시겠습니까?",
    UiTextKey.UnblockUserMessage to "{username}의 차단을 해제하시겠습니까? 다시 친구 요청을 보낼 수 있습니다.",
    UiTextKey.BlockedUsersManageButton to "차단 사용자 관리",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "요청 메모 (선택)",
    UiTextKey.FriendsRequestNotePlaceholder to "짧은 메모 추가...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "학습 시트가 완성되었습니다! 탭하여 열기.",
    UiTextKey.GenerationBannerWordBank to "단어장이 완성되었습니다! 탭하여 확인.",
    UiTextKey.GenerationBannerQuiz to "퀴즈가 완성되었습니다! 탭하여 시작.",

    // Notification settings quick link

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "중국어(번체)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "친구 시스템",
    UiTextKey.HelpFriendSystemBody to "• 사용자명 또는 ID로 친구 검색\n" +
            "• 친구 요청 보내기, 수락, 거절\n" +
            "• 친구와 실시간 채팅, 대화 번역 가능\n" +
            "• 단어와 학습 자료를 친구와 공유\n" +
            "• 공유 받은편지함에서 친구의 콘텐츠 수신·관리\n" +
            "• 카드와 받은편지함의 빨간 점(●)은 읽지 않은 메시지 또는 새 콘텐츠\n" +
            "• 아래로 스와이프로 친구 목록과 요청 업데이트",
    UiTextKey.HelpProfileVisibilityTitle to "프로필 공개 설정",
    UiTextKey.HelpProfileVisibilityBody to "• 내 프로필 설정에서 공개 또는 비공개 설정 가능\n" +
            "• 공개: 누구나 검색하여 요청 전송 가능\n" +
            "• 비공개: 검색 결과에 표시되지 않음\n" +
            "• 비공개에서도 사용자 ID를 공유하여 친구 추가 가능",
    UiTextKey.HelpColorPalettesTitle to "컬러 테마와 코인",
    UiTextKey.HelpColorPalettesBody to "• 1개의 무료 테마: 스카이블루 (기본)\n" +
            "• 10개의 잠금 해제 가능한 테마, 각 10코인\n" +
            "• 퀴즈 완료로 코인 획득\n" +
            "• 코인은 컬러 테마 해제와 기록 한도 확장에 사용\n" +
            "• 자동 테마: 오전 6시~오후 6시 라이트, 오후 6시~오전 6시 다크",
    UiTextKey.HelpPrivacyTitle to "개인정보와 데이터",
    UiTextKey.HelpPrivacyBody to "• 음성은 인식에만 사용되며 영구 저장되지 않음\n" +
            "• OCR은 기기에서 처리 (개인정보 우선)\n" +
            "• 언제든 계정과 모든 데이터를 삭제 가능\n" +
            "• 프로필을 비공개로 설정하면 검색 불가\n" +
            "• 모든 데이터는 Firebase로 안전하게 동기화",
    UiTextKey.HelpAppVersionTitle to "앱 버전",
    UiTextKey.HelpAppVersionNotes to "• 기록 한도는 30~60건 (코인으로 확장 가능)\n" +
            "• 사용자명은 고유 — 변경 시 이전 이름 해제\n" +
            "• 버전 업데이트 시 보안을 위해 자동 로그아웃\n" +
            "• 모든 번역은 Azure AI 서비스 제공",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "즉시 번역",
    UiTextKey.OnboardingPage1Desc to "빠른 번역은 짧은 문장에, 실시간 대화는 양방향 대화에.",
    UiTextKey.OnboardingPage2Title to "어휘 학습",
    UiTextKey.OnboardingPage2Desc to "번역 기록으로 어휘 시트와 퀴즈를 생성.",
    UiTextKey.OnboardingPage3Title to "친구와 연결",
    UiTextKey.OnboardingPage3Desc to "채팅하고, 단어를 공유하고, 함께 배우세요.",
    UiTextKey.OnboardingSkipButton to "건너뛰기",
    UiTextKey.OnboardingNextButton to "다음",
    UiTextKey.OnboardingGetStartedButton to "시작하기",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 다시 오셨군요, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "사용자명:",
    UiTextKey.ChatUserIdLabel to "사용자 ID:",
    UiTextKey.ChatLearningLabel to "학습 중:",
    UiTextKey.ChatBlockedMessage to "이 사용자에게 메시지를 보낼 수 없습니다.",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "검색",
    UiTextKey.CustomWordsEmptyState to "맞춤 단어 없음",
    UiTextKey.CustomWordsEmptyHint to "+를 탭하여 단어 추가",
    UiTextKey.CustomWordsNoSearchResults to "검색에 일치하는 단어 없음",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} 건의 기록",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "이메일: {email}",
    UiTextKey.ProfileUsernameHintFull to "친구가 검색할 사용자명 (3~20자, 영숫자/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "이 언어에서는 음성 옵션이 없습니다",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "앱이 업데이트되었습니다. 다시 로그인하세요",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "즐겨찾기 한도에 도달했습니다",
    UiTextKey.FavoritesLimitMessage to "즐겨찾기는 최대 20건입니다. 새로 추가하려면 기존 즐겨찾기를 삭제하세요.",
    UiTextKey.FavoritesLimitGotIt to "확인",
    UiTextKey.FavoritesInfoTitle to "즐겨찾기 정보",
    UiTextKey.FavoritesInfoMessage to "즐겨찾기는 최대 20건 (기록과 대화 포함). 이 제한은 데이터베이스 부하를 줄여 앱을 쾌적하게 유지하기 위함입니다. 추가하려면 기존 즐겨찾기를 삭제하세요.",
    UiTextKey.FavoritesInfoGotIt to "확인",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "언어를 변경할 수 없습니다",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "주요 언어는 30일마다만 변경 가능합니다. {days} 일 더 기다려 주세요.",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "주요 언어는 30일마다만 변경 가능합니다. {hours} 시간 더 기다려 주세요.",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "언어 변경 확인",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "주요 언어를 변경하면 30일간 변경할 수 없습니다. 계속하시겠습니까?",

    // Bottom navigation
    UiTextKey.NavHome to "홈",
    UiTextKey.NavTranslate to "번역",
    UiTextKey.NavLearn to "학습",
    UiTextKey.NavFriends to "친구",
    UiTextKey.NavSettings to "설정",

    // Permissions
    UiTextKey.CameraPermissionTitle to "카메라 권한 필요",
    UiTextKey.CameraPermissionMessage to "텍스트 인식에 사용하기 위해 카메라 권한을 허용해 주세요.",
    UiTextKey.CameraPermissionGrant to "허용",
    UiTextKey.MicPermissionMessage to "음성 인식에 마이크 권한이 필요합니다.",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "선택한 {count} 건을 삭제하시겠습니까? 되돌릴 수 없습니다.",
    UiTextKey.WordBankDeleteConfirm to "\"{word}\"을(를) 삭제하시겠습니까?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "모두 수락",
    UiTextKey.FriendsRejectAllButton to "모두 거절",
    UiTextKey.ChatBlockedCannotSend to "메시지를 보낼 수 없습니다",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "{name}을(를) 잠금 해제하시겠습니까?",
    UiTextKey.ShopUnlockCost to "비용: {cost} 코인",
    UiTextKey.ShopYourCoins to "내 코인: {coins}",
    UiTextKey.ShopUnlockButton to "잠금 해제",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "주요 언어",
    UiTextKey.HelpPrimaryLanguageBody to "• 주요 언어는 학습 시트의 설명과 제안에 사용\n" +
            "• 학습 일관성을 위해 30일마다만 변경 가능\n" +
            "• 설정에서 주요 언어를 변경할 수 있습니다\n" +
            "• 전역 설정으로 모든 페이지에 적용",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 팁: 더 정확한 인식을 위해 \"소스 언어\"를 스캔할 텍스트의 언어로 설정하세요.",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "사용자명을 변경할 수 없습니다",
    UiTextKey.SettingsUsernameCooldownMessage to "사용자명은 30일마다만 변경 가능합니다. {days} 일 더 기다려 주세요.",
    UiTextKey.SettingsUsernameCooldownMessageHours to "사용자명은 30일마다만 변경 가능합니다. {hours} 시간 더 기다려 주세요.",
    UiTextKey.SettingsUsernameConfirmTitle to "사용자명 변경 확인",
    UiTextKey.SettingsUsernameConfirmMessage to "사용자명을 변경하면 30일간 변경할 수 없습니다. 계속하시겠습니까?",

    // Phase 1 fixes
    UiTextKey.SettingsPasswordManagedByGoogle to "비밀번호는 Google에서 관리합니다",
    UiTextKey.SpeechListeningLabel to "듣는 중… 말씀하세요",
    UiTextKey.QuizLeaveTitle to "퀴즈를 나가시겠습니까?",
    UiTextKey.QuizLeaveMessage to "답변이 사라집니다.",
    UiTextKey.QuizLeaveConfirm to "나가기",
    UiTextKey.QuizLeaveStay to "계속하기",

    // Phase 1 part 2
    UiTextKey.A11yAddFavorite to "즐겨찾기에 추가",
    UiTextKey.A11yRemoveFavorite to "즐겨찾기에서 삭제",
    UiTextKey.A11yViewFriendProfile to "친구 프로필 보기",
    UiTextKey.A11yRefreshAutoDetect to "자동 감지 새로 고침",
    UiTextKey.A11yInstructions to "사용 방법",
    UiTextKey.A11yClearText to "텍스트 지우기",
    UiTextKey.A11yAddCustomWords to "사용자 단어 추가",
    UiTextKey.A11yFilter to "필터",
    UiTextKey.A11ySpeakOriginal to "원문 읽기",
    UiTextKey.A11ySpeakTranslation to "번역 읽기",
    UiTextKey.A11ySpeakExample to "예문 읽기",
    UiTextKey.A11yCoinRules to "코인 규칙",
    UiTextKey.StatusWaiting to "대기 중…",
    UiTextKey.ErrorNotLoggedIn to "로그인되지 않음",
    UiTextKey.HistoryErrorDeleteFailed to "삭제에 실패했습니다. 다시 시도해 주세요.",
    UiTextKey.HistoryErrorRenameFailed to "이름 변경에 실패했습니다. 다시 시도해 주세요.",
    UiTextKey.HistoryErrorDeleteSessionFailed to "세션을 삭제할 수 없습니다. 다시 시도해 주세요.",
    UiTextKey.HistoryErrorLoadMoreFailed to "기록을 더 불러올 수 없습니다.",
    UiTextKey.HistoryErrorRemoveFavoriteFailed to "즐겨찾기에서 삭제할 수 없습니다.",
    UiTextKey.HistoryErrorAddFavoriteFailed to "즐겨찾기에 추가할 수 없습니다.",
    UiTextKey.HistoryErrorFavoriteSessionFailed to "세션을 즐겨찾기에 저장할 수 없습니다.",
    UiTextKey.HistoryErrorUnfavoriteSessionFailed to "세션을 즐겨찾기에서 삭제할 수 없습니다.",
    UiTextKey.TtsSpeaking to "읽는 중…",
    UiTextKey.TtsSpeakingTranslation to "번역 읽는 중…",
    UiTextKey.TtsSpoken to "✓ 읽기 완료",
    UiTextKey.TtsError to "✗ {detail}",
    UiTextKey.SpeechStatusRecognitionError to "인식 오류: {detail}",
    UiTextKey.SpeechStatusAutoDetecting to "언어 자동 감지 중…",
    UiTextKey.SpeechStatusDetected to "감지됨: {language}",
    UiTextKey.SpeechStatusDetectedConfidence to "감지됨: {language} (신뢰도 {confidence}%)",
    UiTextKey.SpeechStatusEnterText to "번역할 텍스트를 입력하거나 말해 주세요.",
    UiTextKey.SpeechStatusLoginRequired to "번역을 사용하려면 로그인이 필요합니다.",
    UiTextKey.SpeechStatusDetectFailed to "원본 언어를 감지할 수 없습니다. 직접 선택해 주세요.",
    UiTextKey.SpeechStatusTranslationError to "번역 오류: {detail}",
    UiTextKey.SpeechStatusScanningImage to "이미지에서 텍스트 스캔 중…",
    UiTextKey.SpeechStatusTextExtracted to "텍스트를 추출했습니다",
    UiTextKey.SpeechStatusOcrError to "이미지 스캔 오류: {detail}",
    UiTextKey.SpeechStatusEnterTextFirst to "먼저 텍스트를 입력하거나 말해 주세요.",
    UiTextKey.SpeechStatusDetectFailedForSpeech to "음성의 원본 언어를 감지할 수 없습니다. 원본 언어를 선택해 주세요.",
    UiTextKey.SpeechStatusRefreshed to "새로 고쳤습니다. 다음 번역 또는 읽기 때 다시 자동 감지합니다.",
    UiTextKey.ContinuousStatusLoginRequired to "연속 번역을 사용하려면 로그인해 주세요.",
    UiTextKey.ContinuousStatusPreparingMic to "마이크 준비 중…",
    UiTextKey.ContinuousStatusTranslationError to "연속 번역 오류: {detail}",
    UiTextKey.ContinuousStatusRateLimited to "{detail} 반복 실패를 막기 위해 연속 모드를 중지했습니다.",
    UiTextKey.ContinuousStatusRecognitionError to "연속 인식 오류: {detail}",
    UiTextKey.ContinuousStatusStartError to "연속 모드를 시작할 수 없습니다: {detail}",

    // Phase 1 part 3: social area messages
    UiTextKey.StatusRaw to "{detail}",
    UiTextKey.ErrGeneric to "문제가 발생했습니다. 다시 시도해 주세요.",
    UiTextKey.ErrNoInternet to "인터넷에 연결되어 있지 않습니다. 네트워크를 확인한 후 다시 시도해 주세요.",
    UiTextKey.ErrPermissionDenied to "이 작업을 수행할 권한이 없습니다.",
    UiTextKey.ErrSessionExpired to "세션이 만료되었습니다. 다시 로그인해 주세요.",
    UiTextKey.ErrItemNotFound to "요청한 항목을 찾을 수 없습니다. 삭제되었을 수 있습니다.",
    UiTextKey.ErrAccessDenied to "접근이 거부되었습니다.",
    UiTextKey.ErrAlreadyFriends to "이미 이 사용자와 친구입니다.",
    UiTextKey.ErrUserMaybeBlocked to "이 작업을 완료할 수 없습니다. 사용자가 차단되었을 수 있습니다.",
    UiTextKey.ErrRequestGone to "이 친구 요청은 더 이상 존재하지 않습니다.",
    UiTextKey.ErrRequestHandled to "이 요청은 이미 다른 사람이 처리했습니다.",
    UiTextKey.ErrNotAuthorized to "이 작업을 수행할 권한이 없습니다.",
    UiTextKey.ErrTooManyRequests to "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.",
    UiTextKey.ErrInvalidInput to "입력이 올바르지 않습니다. 확인 후 다시 시도해 주세요.",
    UiTextKey.ErrCannotCompleteNow to "지금은 이 작업을 완료할 수 없습니다.",
    UiTextKey.ErrTimedOut to "작업 시간이 초과되었습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgRemoved to "친구를 삭제했습니다.",
    UiTextKey.FriendsMsgRemoveFailed to "친구를 삭제할 수 없습니다. 연결을 확인한 후 다시 시도해 주세요.",
    UiTextKey.FriendsMsgRemovedMany to "친구를 삭제했습니다.",
    UiTextKey.FriendsMsgNeedUsername to "친구 요청을 관리하려면 먼저 프로필에서 사용자 이름을 설정해 주세요.",
    UiTextKey.FriendsMsgMaxPending to "대기 중인 친구 요청이 최대 {limit}개에 도달했습니다. 일부가 수락될 때까지 기다리거나 취소한 후 보내 주세요.",
    UiTextKey.FriendsMsgRateLimit to "요청 한도를 초과했습니다. 친구 요청은 시간당 최대 {limit}개까지 보낼 수 있습니다. 약 {minutes}분 후에 다시 시도해 주세요.",
    UiTextKey.FriendsMsgNoteTooLong to "메모는 {max}자를 넘을 수 없습니다",
    UiTextKey.FriendsMsgRequestSent to "친구 요청을 보냈습니다! 상대방에게 알림이 전송됩니다.",
    UiTextKey.FriendsMsgAlreadyFriends to "이미 이 사용자와 친구입니다.",
    UiTextKey.FriendsMsgRequestPending to "이 사용자에게 이미 대기 중인 요청이 있습니다. 답장을 기다려 주세요.",
    UiTextKey.FriendsMsgProfileMissing to "사용자의 프로필을 찾을 수 없습니다. 계정을 삭제했을 수 있습니다.",
    UiTextKey.FriendsMsgYouBlocked to "이 사용자를 차단했습니다. 먼저 차단을 해제해 주세요.",
    UiTextKey.FriendsMsgUnableToSend to "친구 요청을 보낼 수 없습니다.",
    UiTextKey.FriendsMsgSendFailed to "친구 요청을 보내지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgAccepted to "친구 요청을 수락했습니다! 이제 친구입니다.",
    UiTextKey.FriendsMsgAcceptGone to "이 요청은 더 이상 존재하지 않습니다. 취소되었을 수 있습니다.",
    UiTextKey.FriendsMsgAcceptNotAuthorized to "이 요청을 수락할 권한이 없습니다.",
    UiTextKey.FriendsMsgAcceptFailed to "요청을 수락하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgDeclined to "요청을 거절했습니다.",
    UiTextKey.FriendsMsgDeclineGone to "이 요청은 더 이상 존재하지 않습니다.",
    UiTextKey.FriendsMsgDeclineFailed to "요청을 거절하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgCancelled to "친구 요청을 취소했습니다.",
    UiTextKey.FriendsMsgCancelGone to "이 요청은 더 이상 존재하지 않습니다. 이미 수락되었거나 거절되었을 수 있습니다.",
    UiTextKey.FriendsMsgCancelFailed to "요청을 취소하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgBatchAcceptAll to "친구 요청 {count}개를 모두 수락했습니다!",
    UiTextKey.FriendsMsgBatchAcceptFailed to "요청을 수락하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgBatchAcceptMixed to "{success}개 수락, {failed}개 실패. 실패한 요청을 다시 시도해 주세요.",
    UiTextKey.FriendsMsgBatchAcceptStoppedAt to "{processed}/{total}개 요청을 처리한 후 일괄 수락을 취소했습니다.",
    UiTextKey.FriendsMsgBatchAcceptStopped to "일괄 수락을 취소했습니다.",
    UiTextKey.FriendsMsgBatchDeclined to "요청 {count}개를 거절했습니다.",
    UiTextKey.FriendsMsgBatchDeclineStoppedAt to "{processed}/{total}개 요청을 처리한 후 일괄 거절을 취소했습니다.",
    UiTextKey.FriendsMsgBatchDeclineStopped to "일괄 거절을 취소했습니다.",
    UiTextKey.FriendsMsgBlockedRemoved to "사용자를 차단하고 친구에서 삭제했습니다.",
    UiTextKey.FriendsMsgBlockFailed to "사용자를 차단하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.FriendsMsgBlocked to "사용자를 차단했습니다.",
    UiTextKey.FriendsMsgUnblocked to "사용자 차단을 해제했습니다.",
    UiTextKey.FriendsMsgUnblockFailed to "차단을 해제하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.ChatMsgLoadFailed to "메시지를 불러오지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.ChatMsgBlockFailed to "사용자를 차단하지 못했습니다.",
    UiTextKey.ChatMsgUnblockFailed to "차단을 해제하지 못했습니다.",
    UiTextKey.ChatMsgTooLong to "메시지는 {max}자를 넘을 수 없습니다",
    UiTextKey.ChatMsgTooFast to "메시지를 너무 빨리 보내고 있습니다. 잠시 기다려 주세요.",
    UiTextKey.ChatMsgSendFailed to "메시지를 보내지 못했습니다",
    UiTextKey.ChatMsgTranslateFailed to "번역에 실패했습니다. 다시 시도해 주세요.",
    UiTextKey.MyProfileMsgLoadFailed to "프로필을 불러오지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.MyProfileMsgNeedUsername to "프로필을 공개하기 전에 사용자 이름을 설정해 주세요.",
    UiTextKey.MyProfileMsgPublic to "프로필을 공개로 설정했습니다",
    UiTextKey.MyProfileMsgPrivate to "프로필을 비공개로 설정했습니다",
    UiTextKey.MyProfileMsgVisibilityFailed to "공개 설정을 업데이트하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.SharedMsgAdded to "컬렉션에 추가했습니다",
    UiTextKey.SharedMsgDeleted to "항목을 삭제했습니다",
    UiTextKey.SharedMsgDismissed to "항목을 숨겼습니다",
    UiTextKey.SharedMsgAcceptFailed to "항목을 받지 못했습니다",
    UiTextKey.SharedMsgDeleteFailed to "항목을 삭제하지 못했습니다",
    UiTextKey.SharedMsgDismissFailed to "항목을 숨기지 못했습니다",
    UiTextKey.SharedMsgNoItemId to "항목 ID가 없습니다",
    UiTextKey.SharedMsgLoadFailed to "공유된 자료를 불러오지 못했습니다. 다시 시도해 주세요.",

    // Phase 1 part 4: settings, shop, profile, login and feedback messages
    UiTextKey.FeedbackMsgEmpty to "메시지를 입력해 주세요",
    UiTextKey.FeedbackMsgTooShort to "피드백은 {min}자 이상이어야 합니다",
    UiTextKey.FeedbackMsgTooLong to "피드백은 {max}자를 넘을 수 없습니다",
    UiTextKey.FeedbackMsgRateLimited to "최근에 피드백을 너무 많이 보냈습니다. 나중에 다시 시도해 주세요.",
    UiTextKey.FeedbackMsgSubmitFailed to "피드백을 보내지 못했습니다. 인터넷 연결을 확인해 주세요.",
    UiTextKey.ShopMsgLoadFailed to "상점 데이터를 불러오지 못했습니다",
    UiTextKey.ShopMsgPurchaseFailed to "구매에 실패했습니다",
    UiTextKey.ShopMsgHistoryExpanded to "기록 한도가 {limit}개로 늘어났습니다!",
    UiTextKey.ShopMsgPaletteAlreadyUnlocked to "이미 잠금 해제된 팔레트입니다",
    UiTextKey.ShopMsgUnlockFailed to "팔레트 잠금 해제에 실패했습니다",
    UiTextKey.ShopMsgPaletteUnlocked to "팔레트가 잠금 해제되었습니다!",
    UiTextKey.ShopMsgPaletteSelectFailed to "색상 팔레트를 변경하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.ProfileMsgLoadFailed to "프로필을 불러오지 못했습니다",
    UiTextKey.ProfileMsgUsernameTooShort to "사용자 이름은 {min}자 이상이어야 합니다",
    UiTextKey.ProfileMsgUsernameTooLong to "사용자 이름은 {max}자를 넘을 수 없습니다",
    UiTextKey.ProfileMsgUsernameInvalidChars to "사용자 이름에는 영문자, 숫자, 밑줄만 사용할 수 있습니다",
    UiTextKey.ProfileMsgVerifyFailed to "계정 상태를 확인할 수 없습니다. 다시 시도해 주세요.",
    UiTextKey.ProfileMsgUsernameTaken to "이미 사용 중인 사용자 이름입니다",
    UiTextKey.ProfileMsgUsernameUpdatedSyncing to "사용자 이름이 업데이트되었습니다. 친구 목록이 새로 고쳐지기까지 잠시 걸릴 수 있습니다.",
    UiTextKey.ProfileMsgUsernameUpdated to "사용자 이름이 업데이트되었습니다",
    UiTextKey.ProfileMsgUpdateFailed to "프로필을 업데이트하지 못했습니다",
    UiTextKey.ProfileMsgSetUsernameFailed to "사용자 이름을 설정하지 못했습니다",
    UiTextKey.ProfileMsgDeleteFailed to "계정을 삭제하지 못했습니다",
    UiTextKey.ProfileMsgInvalidPassword to "비밀번호가 올바르지 않습니다. 다시 시도해 주세요.",
    UiTextKey.ProfileMsgGoogleReauthFailed to "Google 재인증에 실패했습니다. 다시 시도해 주세요.",
    UiTextKey.ProfileMsgGoogleNoToken to "Google 로그인 실패: ID 토큰이 없습니다.",
    UiTextKey.ProfileMsgGoogleSignInFailed to "Google 로그인에 실패했습니다. 다시 시도해 주세요.",
    UiTextKey.ProfileGoogleReauthHint to "확인을 위해 Google 계정으로 다시 인증해 주세요.",
    UiTextKey.ProfileContinueWithGoogle to "Google로 계속",
    UiTextKey.SettingsMsgLanguageSaveFailed to "언어 설정을 저장하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.SettingsMsgFontSaveFailed to "글꼴 크기를 저장하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.SettingsMsgThemeSaveFailed to "테마 설정을 저장하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.SettingsMsgAutoThemeFailed to "자동 테마를 끄지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.SettingsMsgVoiceSaveFailed to "음성 설정을 저장하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.SettingsMsgNotificationSaveFailed to "알림 설정을 저장하지 못했습니다. 다시 시도해 주세요.",
    UiTextKey.AuthMsgEmailEmpty to "이메일을 입력해 주세요",
    UiTextKey.AuthMsgEmailInvalid to "이메일 형식이 올바르지 않습니다",
    UiTextKey.AuthMsgTooManyLogins to "로그인 시도가 너무 많습니다. 나중에 다시 시도해 주세요.",
    UiTextKey.AuthMsgTooManyResets to "비밀번호 재설정 시도가 너무 많습니다. 나중에 다시 시도해 주세요.",
    UiTextKey.AuthMsgGoogleEmptyToken to "Google 로그인 실패: ID 토큰이 비어 있습니다.",
    UiTextKey.AuthMsgGoogleNoToken to "Google 로그인 실패: ID 토큰을 받지 못했습니다.",
    UiTextKey.AuthMsgGoogleFailedCode to "Google 로그인 실패(코드 {code}).",
    UiTextKey.AuthMsgGoogleNotConfigured to "이 빌드에는 Google 로그인이 설정되어 있지 않습니다.",
    UiTextKey.ErrUnknown to "알 수 없는 오류가 발생했습니다.",
    UiTextKey.ErrNetworkFailed to "네트워크 연결에 실패했습니다. 인터넷 연결을 확인해 주세요.",
    UiTextKey.ErrOperationTimedOut to "작업 시간이 초과되었습니다. 다시 시도해 주세요.",
    UiTextKey.ErrCancelled to "작업이 취소되었습니다.",
    UiTextKey.ErrUnexpected to "예기치 않은 오류가 발생했습니다. 다시 시도해 주세요.",
    UiTextKey.ErrAuthInvalidEmail to "이메일 주소 형식이 잘못되었습니다.",
    UiTextKey.ErrAuthWrongPassword to "비밀번호가 올바르지 않습니다.",
    UiTextKey.ErrAuthUserNotFound to "이 이메일로 등록된 계정이 없습니다.",
    UiTextKey.ErrAuthUserDisabled to "이 계정은 비활성화되었습니다.",
    UiTextKey.ErrAuthTooManyRequests to "요청이 너무 많습니다. 나중에 다시 시도해 주세요.",
    UiTextKey.ErrAuthEmailInUse to "이 이메일 주소는 이미 다른 계정에서 사용 중입니다.",
    UiTextKey.ErrAuthFailed to "인증에 실패했습니다. 다시 시도해 주세요.",
    UiTextKey.ErrDbPermission to "이 데이터에 접근할 권한이 없습니다.",
    UiTextKey.ErrDbUnavailable to "현재 서비스를 사용할 수 없습니다. 나중에 다시 시도해 주세요.",
    UiTextKey.ErrDbGeneric to "데이터베이스 오류입니다. 다시 시도해 주세요.",

    // Extended Error Messages
)
