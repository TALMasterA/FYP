package com.translator.TalknLearn.model.ui

/**
 * Vietnamese (vi-VN) UI text map — Giao diện tiếng Việt.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val ViVnUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Sử dụng micro",
    UiTextKey.CopyButton to "Sao chép",
    UiTextKey.SpeakScriptButton to "Đọc văn bản gốc",
    UiTextKey.TranslateButton to "Dịch",
    UiTextKey.CopyTranslationButton to "Sao chép bản dịch",
    UiTextKey.SpeakTranslationButton to "Đọc bản dịch",
    UiTextKey.RecognizingStatus to "Đang ghi âm...Hãy nói, sẽ tự dừng.",
    UiTextKey.TranslatingStatus to "Đang dịch...",
    UiTextKey.SpeakingOriginalStatus to "Đang đọc văn bản gốc...",
    UiTextKey.SpeakingTranslationStatus to "Đang đọc bản dịch...",
    UiTextKey.SpeakingLabel to "Đang đọc",
    UiTextKey.FinishedSpeakingOriginal to "Đã đọc xong văn bản gốc",
    UiTextKey.FinishedSpeakingTranslation to "Đã đọc xong bản dịch",
    UiTextKey.TtsErrorTemplate to "Lỗi giọng nói: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Ngôn ngữ ứng dụng",
    UiTextKey.DetectLanguageLabel to "Phát hiện ngôn ngữ",
    UiTextKey.TranslateToLabel to "Dịch sang",

    // Language names
    UiTextKey.LangEnUs to "Tiếng Anh",
    UiTextKey.LangZhHk to "Tiếng Quảng Đông",
    UiTextKey.LangJaJp to "Tiếng Nhật",
    UiTextKey.LangZhCn to "Tiếng Trung (Giản thể)",
    UiTextKey.LangFrFr to "Tiếng Pháp",
    UiTextKey.LangDeDe to "Tiếng Đức",
    UiTextKey.LangKoKr to "Tiếng Hàn",
    UiTextKey.LangEsEs to "Tiếng Tây Ban Nha",
    UiTextKey.LangIdId to "Tiếng Indonesia",
    UiTextKey.LangViVn to "Tiếng Việt",
    UiTextKey.LangThTh to "Tiếng Thái",
    UiTextKey.LangFilPh to "Tiếng Filipino",
    UiTextKey.LangMsMy to "Tiếng Mã Lai",
    UiTextKey.LangPtBr to "Tiếng Bồ Đào Nha",
    UiTextKey.LangItIt to "Tiếng Ý",
    UiTextKey.LangRuRu to "Tiếng Nga",

    // Navigation
    UiTextKey.NavHistory to "Lịch sử",
    UiTextKey.NavLogin to "Đăng nhập",
    UiTextKey.NavLogout to "Đăng xuất",
    UiTextKey.NavBack to "Quay lại",
    UiTextKey.ActionCancel to "Hủy",
    UiTextKey.ActionDelete to "Xóa",
    UiTextKey.ActionOpen to "Mở",
    UiTextKey.ActionName to "Đặt tên",
    UiTextKey.ActionSave to "Lưu",
    UiTextKey.ActionConfirm to "Xác nhận",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Nhập tại đây hoặc sử dụng micro...",
    UiTextKey.SpeechTranslatedPlaceholder to "Bản dịch sẽ hiện tại đây...",
    UiTextKey.StatusAzureErrorTemplate to "Lỗi Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Lỗi dịch: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Đăng nhập để dịch",
    UiTextKey.StatusRecognizePreparing to "Đang chuẩn bị micro...(chưa nói)",
    UiTextKey.StatusRecognizeListening to "Đang nghe...Hãy nói.",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Trang trước",
    UiTextKey.PaginationNextLabel to "Trang sau",
    UiTextKey.PaginationPageLabelTemplate to "Trang {page} / {total}",

    // Toast
    UiTextKey.ToastCopied to "Đã sao chép",
    UiTextKey.DisableText to "Đăng nhập để dịch và lưu lịch sử.",

    // Error
    UiTextKey.ErrorRetryButton to "Thử lại",
    UiTextKey.ErrorGenericMessage to "Đã xảy ra lỗi. Vui lòng thử lại.",

    // Shop
    UiTextKey.ShopTitle to "Cửa hàng",
    UiTextKey.ShopCoinBalance to "Xu của tôi",
    UiTextKey.ShopHistoryExpansionTitle to "Mở rộng lịch sử",
    UiTextKey.ShopHistoryExpansionDesc to "Mở rộng giới hạn lịch sử để xem thêm bản dịch.",
    UiTextKey.ShopCurrentLimit to "Giới hạn hiện tại: {limit} mục",
    UiTextKey.ShopMaxLimit to "Giới hạn tối đa:",
    UiTextKey.ShopBuyHistoryExpansion to "Mua (+{increment} mục, {cost} xu)",
    UiTextKey.ShopInsufficientCoins to "Không đủ xu",
    UiTextKey.ShopMaxLimitReached to "Đã đạt giới hạn tối đa",
    UiTextKey.ShopHistoryExpandedTitle to "Mở rộng thành công!",
    UiTextKey.ShopHistoryExpandedMessage to "Giới hạn lịch sử của bạn giờ là {limit} mục! Bạn có thể xem thêm bản dịch!",
    UiTextKey.ShopColorPaletteTitle to "Chủ đề màu",
    UiTextKey.ShopColorPaletteDesc to "Chọn chủ đề màu, 10 xu mỗi chủ đề",
    UiTextKey.ShopEntry to "Cửa hàng",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Cài đặt giọng nói",
    UiTextKey.VoiceSettingsDesc to "Chọn giọng đọc cho từng ngôn ngữ.",

    // Instructions
    UiTextKey.SpeechInstructions to "Nhấn micro để nhận diện giọng nói, rồi dịch. Nếu phát hiện tự động không cập nhật sau khi thay đổi, nhấn làm mới ở góc phải.",
    UiTextKey.HomeInstructions to "Chọn tính năng để bắt đầu.",
    UiTextKey.ContinuousInstructions to "Chọn hai ngôn ngữ và bắt đầu chế độ hội thoại.",

    // Home
    UiTextKey.HomeTitle to "Dịch tức thì",
    UiTextKey.HelpTitle to "Trợ giúp",
    UiTextKey.SpeechTitle to "Dịch nhanh",
    UiTextKey.HomeStartButton to "Bắt đầu dịch",
    UiTextKey.HomeFeaturesTitle to "Tính năng",
    UiTextKey.HomeDiscreteDescription to "Dịch văn bản và giọng nói ngắn",
    UiTextKey.HomeContinuousDescription to "Dịch hai chiều trực tiếp",
    UiTextKey.HomeLearningDescription to "Tạo tài liệu học và câu đố từ lịch sử",

    // Help
    UiTextKey.HelpCurrentTitle to "Tính năng hiện tại",
    UiTextKey.HelpCautionTitle to "Lưu ý",
    UiTextKey.HelpCurrentFeatures to "Tính năng hiện tại:\n" +
            "  • Dịch nhanh: nhận diện giọng nói và dịch\n" +
            "  • Hội thoại trực tiếp: dịch giọng nói hai chiều\n" +
            "  • Lịch sử: xem các bản dịch trước\n" +
            "  • Tài liệu học: tạo từ vựng và câu đố\n\n" +
            "Dịch thuật:\n" +
            "  • Nhận diện giọng nói Azure AI\n" +
            "  • Dịch vụ dịch thuật Azure\n",
    UiTextKey.HelpCaution to "Lưu ý:\n" +
            "  • Nhận diện giọng nói cần kết nối internet\n" +
            "  • Bộ nhớ đệm dịch cục bộ có sẵn ngoại tuyến\n" +
            "  • Xác minh bản dịch quan trọng với chuyên gia\n\n" +
            "Tài khoản và dữ liệu:\n" +
            "  • Lịch sử, học tập và xu cần đăng nhập\n" +
            "  • Dữ liệu được lưu trữ an toàn trên Firebase Firestore\n\n" +
            "Khắc phục sự cố:\n" +
            "  • Nếu không hoạt động, khởi động lại ứng dụng\n",
    UiTextKey.HelpNotesTitle to "Mẹo",
    UiTextKey.HelpNotes to "💡 Mẹo sử dụng:\n\n" +
            "Để dịch tốt nhất:\n" +
            "  • Nói rõ ràng với tốc độ vừa phải\n" +
            "  • Giảm tiếng ồn để nhận diện tốt hơn\n" +
            "  • Dịch nhanh phù hợp cho câu ngắn\n\n" +
            "Ngôn ngữ ứng dụng:\n" +
            "  • Mặc định: Tiếng Anh, thêm ngôn ngữ bởi AI\n" +
            "  • Phiên bản Quảng Đông được dịch thủ công\n" +
            "Cập nhật và phản hồi:\n" +
            "  • Phiên bản ứng dụng tại Cài đặt → Giới thiệu\n" +
            "  • Gửi phản hồi tại Cài đặt → Phản hồi\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Phản hồi",
    UiTextKey.FeedbackDesc to "Cảm ơn phản hồi của bạn! Chia sẻ đề xuất, lỗi hoặc đánh giá.",
    UiTextKey.FeedbackMessagePlaceholder to "Nhập phản hồi...",
    UiTextKey.FeedbackSubmitButton to "Gửi",
    UiTextKey.FeedbackSubmitting to "Đang gửi...",
    UiTextKey.FeedbackSuccessTitle to "Cảm ơn!",
    UiTextKey.FeedbackSuccessMessage to "Phản hồi của bạn đã được gửi thành công. Cảm ơn!",
    UiTextKey.FeedbackErrorTitle to "Gửi thất bại",
    UiTextKey.FeedbackErrorMessage to "Gửi thất bại. Kiểm tra kết nối và thử lại.",
    UiTextKey.FeedbackMessageRequired to "Vui lòng nhập phản hồi.",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Hội thoại trực tiếp",
    UiTextKey.ContinuousStartButton to "Bắt đầu hội thoại",
    UiTextKey.ContinuousStopButton to "Dừng ghi âm",
    UiTextKey.ContinuousStartScreenButton to "Hội thoại trực tiếp",
    UiTextKey.ContinuousPersonALabel to "A đang nói",
    UiTextKey.ContinuousPersonBLabel to "B đang nói",
    UiTextKey.ContinuousCurrentStringLabel to "Văn bản hiện tại:",
    UiTextKey.ContinuousSpeakerAName to "Người A",
    UiTextKey.ContinuousSpeakerBName to "Người B",
    UiTextKey.ContinuousTranslationSuffix to " · Bản dịch",
    UiTextKey.ContinuousPreparingMicText to "Đang chuẩn bị micro...(chưa nói)",
    UiTextKey.ContinuousTranslatingText to "Đang dịch...",

    // History
    UiTextKey.HistoryTitle to "Lịch sử",
    UiTextKey.HistoryTabDiscrete to "Dịch nhanh",
    UiTextKey.HistoryTabContinuous to "Hội thoại trực tiếp",
    UiTextKey.HistoryNoContinuousSessions to "Chưa có phiên hội thoại nào.",
    UiTextKey.HistoryNoDiscreteRecords to "Chưa có bản dịch nào.",
    UiTextKey.DialogDeleteRecordTitle to "Xóa bản ghi?",
    UiTextKey.DialogDeleteRecordMessage to "Hành động này không thể hoàn tác.",
    UiTextKey.DialogDeleteSessionTitle to "Xóa hội thoại?",
    UiTextKey.DialogDeleteSessionMessage to "Tất cả bản ghi trong hội thoại này sẽ bị xóa. Không thể hoàn tác.",
    UiTextKey.HistoryDeleteSessionButton to "Xóa",
    UiTextKey.HistoryNameSessionTitle to "Đặt tên",
    UiTextKey.HistorySessionNameLabel to "Tên hội thoại",
    UiTextKey.HistorySessionTitleTemplate to "Hội thoại {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} mục",

    // Filter
    UiTextKey.FilterDropdownDefault to "Tất cả ngôn ngữ",
    UiTextKey.FilterTitle to "Lọc lịch sử",
    UiTextKey.FilterLangDrop to "Ngôn ngữ",
    UiTextKey.FilterKeyword to "Từ khóa",
    UiTextKey.FilterApply to "Áp dụng",
    UiTextKey.FilterCancel to "Hủy",
    UiTextKey.FilterClear to "Xóa bộ lọc",
    UiTextKey.FilterHistoryScreenTitle to "Bộ lọc",

    // Auth
    UiTextKey.AuthLoginTitle to "Đăng nhập",
    UiTextKey.AuthRegisterTitle to "Đăng ký (tạm dừng)",
    UiTextKey.AuthLoginHint to "Sử dụng email và mật khẩu đã đăng ký.",
    UiTextKey.AuthRegisterRules to "Đăng ký tạm dừng trong quá trình phát triển.\nLưu ý: Email không hợp lệ sẽ ngăn khôi phục mật khẩu.\n" +
            "Quy tắc đăng ký:\n" +
            "• Định dạng email hợp lệ (vd. ten@vidu.com)\n" +
            "• Mật khẩu tối thiểu 8 ký tự\n" +
            "• Xác nhận phải khớp",
    UiTextKey.AuthEmailLabel to "Email",
    UiTextKey.AuthPasswordLabel to "Mật khẩu",
    UiTextKey.AuthConfirmPasswordLabel to "Xác nhận mật khẩu",
        UiTextKey.AuthLoginButton to "Đăng nhập",
        UiTextKey.AuthGoogleSignInButton to "Đăng nhập bằng Google",
    UiTextKey.AuthRegisterButton to "Đăng ký",
    UiTextKey.AuthToggleToRegister to "Chưa có tài khoản? Đăng ký (tạm dừng)",
    UiTextKey.AuthToggleToLogin to "Đã có tài khoản? Đăng nhập",
    UiTextKey.AuthErrorPasswordsMismatch to "Mật khẩu không khớp.",
    UiTextKey.AuthErrorPasswordTooShort to "Mật khẩu tối thiểu 8 ký tự.",
    UiTextKey.AuthRegistrationDisabled to "Đăng ký tạm dừng trong quá trình phát triển.",
    UiTextKey.AuthResetEmailSent to "Email khôi phục đã gửi (nếu có). Kiểm tra hộp thư.",

    // Password reset
    UiTextKey.ForgotPwText to "Quên mật khẩu?",
    UiTextKey.ResetPwTitle to "Khôi phục mật khẩu",
    UiTextKey.ResetPwText to "Nhập email tài khoản. Chúng tôi sẽ gửi liên kết khôi phục.\nĐảm bảo email đã đăng ký.\n",
    UiTextKey.ResetSendingText to "Đang gửi...",
    UiTextKey.ResetSendText to "Gửi email khôi phục",

    // Settings
    UiTextKey.SettingsTitle to "Cài đặt",
    UiTextKey.SettingsPrimaryLanguageTitle to "Ngôn ngữ chính",
    UiTextKey.SettingsPrimaryLanguageDesc to "Dùng cho giải thích và gợi ý học tập",
    UiTextKey.SettingsPrimaryLanguageLabel to "Ngôn ngữ chính",
    UiTextKey.SettingsFontSizeTitle to "Cỡ chữ",
    UiTextKey.SettingsFontSizeDesc to "Điều chỉnh cỡ chữ (đồng bộ giữa các thiết bị)",
    UiTextKey.SettingsScaleTemplate to "Tỷ lệ: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Chủ đề màu",
    UiTextKey.SettingsColorPaletteDesc to "Chọn chủ đề màu, 10 xu mỗi chủ đề",
    UiTextKey.SettingsColorCostTemplate to "{cost} xu",
    UiTextKey.SettingsColorUnlockButton to "Mở khóa",
    UiTextKey.SettingsColorSelectButton to "Chọn",
    UiTextKey.SettingsColorAlreadyUnlocked to "Đã mở khóa",
    UiTextKey.SettingsPreviewHeadline to "Tiêu đề: Xem trước chữ lớn",
    UiTextKey.SettingsPreviewBody to "Nội dung: Xem trước chữ thường",
    UiTextKey.SettingsPreviewLabel to "Nhãn: Xem trước chữ nhỏ",
    UiTextKey.SettingsAboutTitle to "Giới thiệu",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Đã đăng nhập, cài đặt được lưu và đồng bộ tự động.",
    UiTextKey.SettingsThemeTitle to "Giao diện",
    UiTextKey.SettingsThemeDesc to "Chọn giao diện: Hệ thống, Sáng, Tối, hoặc Lên lịch.",
    UiTextKey.SettingsThemeSystem to "Hệ thống",
    UiTextKey.SettingsThemeLight to "Sáng",
    UiTextKey.SettingsThemeDark to "Tối",
    UiTextKey.SettingsThemeScheduled to "Lên lịch",
    UiTextKey.SettingsResetPW to "Khôi phục mật khẩu",
    UiTextKey.SettingsQuickLinks to "Cài đặt chi tiết",
    UiTextKey.SettingsNotLoggedInWarning to "Đăng nhập để xem cài đặt tài khoản. Ngôn ngữ ứng dụng có thể đổi mà không cần đăng nhập.",
    UiTextKey.SettingsVoiceTitle to "Cài đặt giọng nói",
    UiTextKey.SettingsVoiceDesc to "Chọn giọng đọc cho từng ngôn ngữ.",
    UiTextKey.SettingsVoiceLanguageLabel to "Ngôn ngữ",
    UiTextKey.SettingsVoiceSelectLabel to "Giọng nói",
    UiTextKey.SettingsVoiceDefault to "Mặc định",

    // Learning
    UiTextKey.LearningTitle to "Học tập",
    UiTextKey.LearningHintCount to "(*) Số lượng = bản dịch có ngôn ngữ này.",
    UiTextKey.LearningErrorTemplate to "Lỗi: %s",
    UiTextKey.LearningGenerate to "Tạo",
    UiTextKey.LearningRegenerate to "Tạo lại",
    UiTextKey.LearningGenerating to "Đang tạo...",
    UiTextKey.LearningOpenSheetTemplate to "Tài liệu {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Tài liệu học {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Ngôn ngữ chính: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Bản ghi hiện tại: {nowCount} (khi tạo: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Không có nội dung.",
    UiTextKey.LearningSheetRegenerate to "Tạo lại",
    UiTextKey.LearningSheetGenerating to "Đang tạo...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Đây là gì?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Tài liệu học được tạo từ lịch sử dịch. Bao gồm từ vựng, định nghĩa, ví dụ, và ghi chú ngữ pháp. Kiểm tra kiến thức với câu đố!",
    UiTextKey.LearningRegenBlockedTitle to "Không thể tạo lại",
    UiTextKey.LearningRegenBlockedMessage to "Cần tối thiểu 5 bản ghi thêm để tạo lại. Còn thiếu {needed}.",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Còn thiếu {needed} bản ghi để tạo lại (tối thiểu 5)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Số lượng phải vượt lần tạo trước",
    UiTextKey.LearningRegenInfoTitle to "Quy tắc tạo lại",
    UiTextKey.LearningRegenInfoMessage to "Tạo lại tài liệu học:\n\n• Lần đầu: bất kỳ lúc nào\n• Tạo lại: tối thiểu 5 bản ghi thêm\n\nNút chuyển xanh khi đủ. Nếu xám, hãy dịch thêm!\n\n💡 Mẹo: Nếu số lượng không cập nhật, khởi động lại ứng dụng.",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Câu đố đã tạo cho phiên bản này. Tạo tài liệu mới cho câu đố mới.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Câu đố: {language}",
    UiTextKey.QuizOpenButton to "📝 Câu đố",
    UiTextKey.QuizGenerateButton to "🔄 Tạo câu đố",
    UiTextKey.QuizGenerating to "⏳ Đang tạo...",
    UiTextKey.QuizUpToDate to "✓ Mới nhất",
    UiTextKey.QuizBlocked to "🚫 Bị khóa",
    UiTextKey.QuizWait to "⏳ Chờ...",
    UiTextKey.QuizMaterialsQuizTemplate to "Tài liệu: {materials} | Câu đố: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Có thể nhận xu!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 Còn {count} nữa cho xu",
    UiTextKey.QuizCancelButton to "Hủy",
    UiTextKey.QuizPreviousButton to "Câu trước",
    UiTextKey.QuizNextButton to "Câu sau",
    UiTextKey.QuizSubmitButton to "Nộp",
    UiTextKey.QuizRetakeButton to "Làm lại",
    UiTextKey.QuizBackButton to "Quay lại",
    UiTextKey.QuizLoadingText to "Đang tải câu đố...",
    UiTextKey.QuizGeneratingText to "Đang tạo câu đố...",
    UiTextKey.QuizNoMaterialsTitle to "Không tìm thấy tài liệu",
    UiTextKey.QuizNoMaterialsMessage to "Tạo tài liệu học trước, sau đó truy cập câu đố.",
    UiTextKey.QuizErrorTitle to "⚠️ Lỗi câu đố",
    UiTextKey.QuizErrorSuggestion to "Gợi ý: Sử dụng nút ở trên để tạo câu đố.",
    UiTextKey.QuizCompletedTitle to "Hoàn thành câu đố!",
    UiTextKey.QuizAnswerReviewTitle to "Xem lại đáp án",
    UiTextKey.QuizYourAnswerTemplate to "Đáp án của bạn: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Đáp án đúng: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Câu {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Không thể tạo lại: Tài liệu({materials}) < Câu đố({quiz}), dịch thêm.",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Đang tạo khác. Vui lòng chờ.",
    UiTextKey.QuizCoinRulesTitle to "🪙 Quy tắc xu",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Cách nhận:",
    UiTextKey.QuizCoinRulesRequirements to "Yêu cầu:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Trạng thái hiện tại:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Có thể nhận xu ở câu đố tiếp!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• Còn {count} nữa cho xu",
    UiTextKey.QuizCoinRule1Coin to "• 1 xu cho mỗi câu đúng",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Chỉ lần thử đầu được tính",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Câu đố phải khớp phiên bản tài liệu",
    UiTextKey.QuizCoinRulePlus10 to "• Tối thiểu 10 bản ghi hơn lần nhận xu cuối",
    UiTextKey.QuizCoinRuleNoDelete to "• Xu không được hoàn khi xóa bản ghi",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Tài liệu: {count} bản ghi",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Câu đố: {count} bản ghi",
    UiTextKey.QuizCoinRuleGotIt to "Đã hiểu!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Tạo câu đố mới?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Có thể nhận xu từ câu đố này! (chỉ lần thử đầu)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Không thể nhận xu từ câu đố này.",
    UiTextKey.QuizRegenNeedMoreTemplate to "Cần thêm {count} bản dịch để đủ điều kiện (10 hơn lần nhận xu cuối).",
    UiTextKey.QuizRegenReminder to "Mẹo: Bạn có thể luyện tập và làm lại, nhưng xu chỉ được trao ở lần thử đầu với đủ bản ghi.",
    UiTextKey.QuizRegenGenerateButton to "Tạo",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Nhận xu!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Chúc mừng! Bạn đã nhận {Coins} xu!",
    UiTextKey.QuizCoinsRule1 to "• 1 xu cho mỗi câu đúng ở lần thử đầu",
    UiTextKey.QuizCoinsRule2 to "• Không có xu cho các lần thử sau",
    UiTextKey.QuizCoinsRule3 to "• Câu đố mới cần thêm 10 bản ghi",
    UiTextKey.QuizCoinsRule4 to "• Câu đố phải khớp phiên bản tài liệu",
    UiTextKey.QuizCoinsRule5 to "• Tổng xu hiện ở lịch sử",
    UiTextKey.QuizCoinsGreatButton to "Tuyệt!",
    UiTextKey.QuizOutdatedMessage to "Câu đố này dựa trên tài liệu cũ.",
    UiTextKey.QuizRecordsLabel to "Bản ghi",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Xu của tôi",
    UiTextKey.HistoryCoinRulesTitle to "Quy tắc xu:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Cách nhận:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 xu cho mỗi câu đúng",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Chỉ lần thử đầu mỗi phiên bản",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Câu đố phải khớp tài liệu hiện tại",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Quy tắc chống gian lận:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Tối thiểu 10 bản dịch mới kể từ lần nhận cuối",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• Phiên bản câu đố phải khớp tài liệu",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Xóa bản ghi chặn tạo lại (trừ khi số lượng vượt trước)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Không có xu cho các lần thử sau",
    UiTextKey.HistoryCoinTipsTitle to "💡 Mẹo:",
    UiTextKey.HistoryCoinTipsRule1 to "• Thêm bản dịch đều đặn",
    UiTextKey.HistoryCoinTipsRule2 to "• Học kỹ trước lần thử đầu!",
    UiTextKey.HistoryCoinGotItButton to "Đã hiểu!",

    // History info
    UiTextKey.HistoryInfoTitle to "Thông tin lịch sử",
    UiTextKey.HistoryInfoLimitMessage to "Lịch sử hiển thị {limit} mục gần nhất. Mở rộng giới hạn ở cửa hàng!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Bản ghi cũ được lưu nhưng ẩn để tối ưu hiệu suất.",
    UiTextKey.HistoryInfoFavoritesMessage to "Để lưu bản dịch vĩnh viễn, nhấn ❤️ để yêu thích trong lịch sử.",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Xem mục đã lưu tại Cài đặt → Yêu thích.",
    UiTextKey.HistoryInfoFilterMessage to "Sử dụng bộ lọc để tìm trong {limit} mục hiển thị.",
    UiTextKey.HistoryInfoGotItButton to "Đã hiểu",

    // Word bank
    UiTextKey.WordBankTitle to "Ngân hàng từ",
    UiTextKey.WordBankSelectLanguage to "Chọn ngôn ngữ để xem hoặc tạo ngân hàng từ:",
    UiTextKey.WordBankNoHistory to "Chưa có lịch sử dịch",
    UiTextKey.WordBankNoHistoryHint to "Bắt đầu dịch để xây dựng ngân hàng từ!",
    UiTextKey.WordBankWordsCount to "Từ",
    UiTextKey.WordBankGenerating to "Đang tạo...",
    UiTextKey.WordBankGenerate to "Tạo ngân hàng từ",
    UiTextKey.WordBankRegenerate to "Tạo lại ngân hàng từ",
    UiTextKey.WordBankRefresh to "🔄 Làm mới ngân hàng",
    UiTextKey.WordBankEmpty to "Ngân hàng từ trống",
    UiTextKey.WordBankEmptyHint to "Nhấn ở trên để tạo từ lịch sử.",
    UiTextKey.WordBankExample to "Ví dụ:",
    UiTextKey.WordBankDifficulty to "Độ khó:",
    UiTextKey.WordBankFilterCategory to "Danh mục",
    UiTextKey.WordBankFilterCategoryAll to "Tất cả danh mục",
    UiTextKey.WordBankFilterDifficultyLabel to "Độ khó:",
    UiTextKey.WordBankFilterNoResults to "Không có từ phù hợp với bộ lọc",
    UiTextKey.WordBankRefreshAvailable to "✅ Có bản cập nhật!",
    UiTextKey.WordBankRecordsNeeded to "Bản ghi (cần 20 để cập nhật)",
    UiTextKey.WordBankRegenInfoTitle to "Quy tắc cập nhật",
    UiTextKey.WordBankRegenInfoMessage to "Cập nhật ngân hàng từ:\n\n• Lần đầu: bất kỳ lúc nào\n• Cập nhật: tối thiểu 20 bản ghi thêm\n\nNút chuyển xanh khi đủ. Nếu xám, hãy dịch thêm!\n\n💡 Mẹo: Nếu số lượng không cập nhật, khởi động lại ứng dụng.",
    UiTextKey.WordBankHistoryCountTemplate to "Bản ghi hiện tại: {nowCount} (khi tạo: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Đăng xuất?",
    UiTextKey.DialogLogoutMessage to "Dịch và truy cập lịch sử cần đăng nhập lại.",
    UiTextKey.DialogGenerateOverwriteTitle to "Ghi đè tài liệu?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Tài liệu hiện có sẽ bị ghi đè.\nTạo tài liệu cho {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Hồ sơ",
    UiTextKey.ProfileUsernameLabel to "Tên người dùng",
    UiTextKey.ProfileUsernameHint to "Nhập tên người dùng",
    UiTextKey.ProfileUpdateButton to "Cập nhật hồ sơ",
    UiTextKey.ProfileUpdateSuccess to "Hồ sơ đã cập nhật",
    UiTextKey.ProfileUpdateError to "Cập nhật thất bại",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Xóa tài khoản",
    UiTextKey.AccountDeleteWarning to "⚠️ Hành động này vĩnh viễn và không thể hoàn tác!",
    UiTextKey.AccountDeleteConfirmMessage to "Tất cả dữ liệu sẽ bị xóa vĩnh viễn: lịch sử, ngân hàng từ, tài liệu học, cài đặt. Nhập mật khẩu để xác nhận.",
    UiTextKey.AccountDeletePasswordLabel to "Mật khẩu",
        UiTextKey.AccountDeleteButton to "Xóa tài khoản và dữ liệu",
    UiTextKey.AccountDeleteSuccess to "Tài khoản đã xóa thành công",
    UiTextKey.AccountDeleteError to "Xóa thất bại",
    UiTextKey.AccountDeleteReauthRequired to "Nhập mật khẩu để xác nhận xóa",

    // Favorites
    UiTextKey.FavoritesTitle to "Yêu thích",
    UiTextKey.FavoritesEmpty to "Chưa có mục yêu thích",
    UiTextKey.FavoritesAddSuccess to "Đã thêm vào yêu thích",
    UiTextKey.FavoritesRemoveSuccess to "Đã xóa khỏi yêu thích",
    UiTextKey.FavoritesAddButton to "Thêm vào yêu thích",
    UiTextKey.FavoritesRemoveButton to "Xóa khỏi yêu thích",
    UiTextKey.FavoritesNoteLabel to "Ghi chú",
    UiTextKey.FavoritesNoteHint to "Thêm ghi chú (tùy chọn)",
    UiTextKey.FavoritesTabRecords to "Bản ghi",
    UiTextKey.FavoritesTabSessions to "Hội thoại",
    UiTextKey.FavoritesSessionsEmpty to "Chưa có hội thoại yêu thích",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} tin nhắn",

    // Custom words
    UiTextKey.CustomWordsTitle to "Từ tùy chỉnh",
    UiTextKey.CustomWordsAdd to "Thêm từ",
    UiTextKey.CustomWordsEdit to "Sửa từ",
    UiTextKey.CustomWordsDelete to "Xóa từ",
    UiTextKey.CustomWordsOriginalLabel to "Từ gốc",
    UiTextKey.CustomWordsTranslatedLabel to "Bản dịch",
    UiTextKey.CustomWordsPronunciationLabel to "Phát âm (tùy chọn)",
    UiTextKey.CustomWordsExampleLabel to "Ví dụ (tùy chọn)",
    UiTextKey.CustomWordsSaveSuccess to "Đã lưu từ",
    UiTextKey.CustomWordsDeleteSuccess to "Đã xóa từ",
    UiTextKey.CustomWordsAlreadyExists to "Từ này đã tồn tại",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Ngôn ngữ gốc",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Ngôn ngữ dịch",
    UiTextKey.CustomWordsSaveButton to "Lưu",
    UiTextKey.CustomWordsCancelButton to "Hủy",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Tự động phát hiện",
    UiTextKey.LanguageDetectDetecting to "Đang phát hiện...",
    UiTextKey.LanguageDetectedTemplate to "Đã phát hiện: {language}",
    UiTextKey.LanguageDetectFailed to "Phát hiện thất bại",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Quét chữ từ ảnh",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Cảnh báo: Nhận diện chữ có thể không hoàn toàn chính xác. Xác minh lại chữ đã nhận diện." +
            "Hỗ trợ Latin (Tiếng Anh, v.v.), Trung Quốc, Nhật, và Hàn.",
    UiTextKey.ImageRecognitionScanning to "Đang quét chữ...",
    UiTextKey.ImageRecognitionSuccess to "Nhận diện chữ thành công",

    // Cache
    UiTextKey.CacheClearButton to "Xóa bộ nhớ đệm",
    UiTextKey.CacheClearSuccess to "Đã xóa bộ nhớ đệm",
    UiTextKey.CacheStatsTemplate to "Bộ nhớ đệm: {count} bản dịch đã lưu",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Giao diện tự động",
    UiTextKey.SettingsAutoThemeDesc to "Tự chuyển sáng/tối theo thời gian",
    UiTextKey.SettingsAutoThemeEnabled to "Bật",
    UiTextKey.SettingsAutoThemeDisabled to "Tắt",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Chế độ tối bắt đầu:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Chế độ sáng bắt đầu:",
    UiTextKey.SettingsAutoThemePreview to "Giao diện sẽ tự chuyển vào thời gian đã cài đặt",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Chế độ ngoại tuyến",
    UiTextKey.OfflineModeMessage to "Bạn đang ngoại tuyến. Dữ liệu đã lưu được hiển thị.",
    UiTextKey.OfflineModeRetry to "Thử kết nối lại",
    UiTextKey.OfflineDataCached to "Dữ liệu đã lưu có sẵn",
    UiTextKey.OfflineSyncPending to "Thay đổi sẽ được đồng bộ khi trực tuyến",

    // Image capture
    UiTextKey.ImageSourceTitle to "Chọn nguồn ảnh",
    UiTextKey.ImageSourceCamera to "Chụp ảnh",
    UiTextKey.ImageSourceGallery to "Chọn từ thư viện",
    UiTextKey.ImageSourceCancel to "Hủy",
    UiTextKey.CameraCaptureContentDesc to "Chụp ảnh",

    // Friends
    UiTextKey.FriendsTitle to "Bạn bè",
    UiTextKey.FriendsMenuButton to "Bạn bè",
    UiTextKey.FriendsAddButton to "Thêm bạn",
    UiTextKey.FriendsSearchTitle to "Tìm người dùng",
    UiTextKey.FriendsSearchPlaceholder to "Tên người dùng hoặc ID...",
    UiTextKey.FriendsSearchMinChars to "Nhập tối thiểu 2 ký tự",
    UiTextKey.FriendsSearchNoResults to "Không tìm thấy người dùng",
    UiTextKey.FriendsListEmpty to "Thêm bạn để trò chuyện và chia sẻ tài liệu.",
    UiTextKey.FriendsRequestsSection to "Lời mời kết bạn ({count})",
    UiTextKey.FriendsSectionTitle to "Bạn bè ({count})",
    UiTextKey.FriendsAcceptButton to "Chấp nhận",
    UiTextKey.FriendsRejectButton to "Từ chối",
    UiTextKey.FriendsRemoveButton to "Xóa",
    UiTextKey.FriendsRemoveDialogTitle to "Xóa bạn",
    UiTextKey.FriendsRemoveDialogMessage to "Xóa {username} khỏi danh sách bạn bè?",
    UiTextKey.FriendsSendRequestButton to "Thêm",
    UiTextKey.FriendsRequestSentSuccess to "Đã gửi lời mời kết bạn!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Đã chấp nhận lời mời!",
    UiTextKey.FriendsRequestRejectedSuccess to "Đã từ chối lời mời",
    UiTextKey.FriendsRemovedSuccess to "Đã xóa bạn",
    UiTextKey.FriendsRequestFailed to "Gửi thất bại",
    UiTextKey.FriendsCloseButton to "Đóng",
    UiTextKey.FriendsCancelButton to "Hủy",
    UiTextKey.FriendsRemoveConfirm to "Xóa",
    UiTextKey.FriendsNewRequestsTemplate to "Bạn có {count} lời mời kết bạn mới!",
    UiTextKey.FriendsSentRequestsSection to "Lời mời đã gửi ({count})",
    UiTextKey.FriendsPendingStatus to "Đang chờ",
    UiTextKey.FriendsCancelRequestButton to "Hủy lời mời",
    UiTextKey.FriendsUnreadMessageDesc to "Gửi tin nhắn",
    UiTextKey.FriendsDeleteModeButton to "Xóa bạn",
    UiTextKey.FriendsDeleteSelectedButton to "Xóa đã chọn",
    UiTextKey.FriendsDeleteMultipleTitle to "Xóa bạn bè",
    UiTextKey.FriendsDeleteMultipleMessage to "Xóa {count} bạn đã chọn?",
    UiTextKey.FriendsSearchMinChars3 to "Nhập tối thiểu 3 ký tự cho tên",
    UiTextKey.FriendsSearchByUserIdHint to "Hoặc tìm chính xác theo ID người dùng",
    UiTextKey.FriendsStatusAlreadyFriends to "Đã là bạn bè",
    UiTextKey.FriendsStatusRequestSent to "Đã gửi lời mời — đang chờ phản hồi",
    UiTextKey.FriendsStatusRequestReceived to "Người dùng này đã gửi lời mời cho bạn",

    // Chat
    UiTextKey.ChatTitle to "Chat với {username}",
    UiTextKey.ChatInputPlaceholder to "Nhập tin nhắn...",
    UiTextKey.ChatSendButton to "Gửi",
    UiTextKey.ChatEmpty to "Chưa có tin nhắn. Bắt đầu trò chuyện!",
    UiTextKey.ChatMessageSent to "Tin nhắn đã gửi",
    UiTextKey.ChatMessageFailed to "Gửi thất bại",
    UiTextKey.ChatMarkingRead to "Đang đánh dấu...",
    UiTextKey.ChatLoadingMessages to "Đang tải tin nhắn...",
    UiTextKey.ChatToday to "Hôm nay",
    UiTextKey.ChatYesterday to "Hôm qua",
    UiTextKey.ChatUnreadBadge to "{count} chưa đọc",
    UiTextKey.ChatTranslateButton to "Dịch",
    UiTextKey.ChatTranslateDialogTitle to "Dịch cuộc trò chuyện",
    UiTextKey.ChatTranslateDialogMessage to "Dịch tin nhắn bạn bè sang ngôn ngữ của bạn? Ngôn ngữ mỗi tin nhắn sẽ được phát hiện và dịch.",
    UiTextKey.ChatTranslateConfirm to "Dịch tất cả",
    UiTextKey.ChatTranslating to "Đang dịch tin nhắn...",
    UiTextKey.ChatTranslated to "Đã dịch tin nhắn",
    UiTextKey.ChatShowOriginal to "Hiện bản gốc",
    UiTextKey.ChatShowTranslation to "Hiện bản dịch",
    UiTextKey.ChatTranslateFailed to "Dịch thất bại",
    UiTextKey.ChatTranslatedLabel to "Đã dịch",

    // Sharing
    UiTextKey.ShareTitle to "Chia sẻ",
    UiTextKey.ShareInboxTitle to "Hộp chia sẻ",
    UiTextKey.ShareInboxEmpty to "Chưa có nội dung chia sẻ. Bạn bè có thể chia sẻ từ và tài liệu!",
    UiTextKey.ShareWordButton to "Chia sẻ từ",
    UiTextKey.ShareMaterialButton to "Chia sẻ tài liệu",
    UiTextKey.ShareSelectFriendTitle to "Chọn bạn",
    UiTextKey.ShareSelectFriendMessage to "Chọn bạn để chia sẻ:",
    UiTextKey.ShareSuccess to "Chia sẻ thành công!",
    UiTextKey.ShareFailed to "Chia sẻ thất bại",
    UiTextKey.ShareWordWith to "Chia sẻ từ với {username}",
    UiTextKey.ShareMaterialWith to "Chia sẻ tài liệu với {username}",
    UiTextKey.ShareAcceptButton to "Chấp nhận",
    UiTextKey.ShareDismissButton to "Bỏ qua",
    UiTextKey.ShareAccepted to "Đã thêm vào bộ sưu tập",
    UiTextKey.ShareDismissed to "Mục đã bỏ qua",
    UiTextKey.ShareActionFailed to "Thao tác thất bại",
    UiTextKey.ShareTypeWord to "Từ",
    UiTextKey.ShareTypeLearningSheet to "Tài liệu học",
    UiTextKey.ShareReceivedFrom to "Từ: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} mục mới!",
    UiTextKey.ShareViewFullMaterial to "Nhấn \"Xem\" để xem tài liệu đầy đủ",
    UiTextKey.ShareDeleteItemTitle to "Xóa mục",
    UiTextKey.ShareDeleteItemMessage to "Xóa mục chia sẻ này? Không thể hoàn tác.",
    UiTextKey.ShareDeleteButton to "Xóa",
    UiTextKey.ShareViewButton to "Xem",
    UiTextKey.ShareItemNotFound to "Không tìm thấy mục.",
    UiTextKey.ShareNoContent to "Không có nội dung trong tài liệu.",
    UiTextKey.ShareSaveToSelf to "Lưu vào hộp của tôi",
    UiTextKey.ShareSavedToSelf to "Đã lưu vào hộp của bạn!",

    // My profile
    UiTextKey.MyProfileTitle to "Hồ sơ của tôi",
    UiTextKey.MyProfileUserId to "ID người dùng",
    UiTextKey.MyProfileUsername to "Tên người dùng",
    UiTextKey.MyProfileDisplayName to "Tên hiển thị",
    UiTextKey.MyProfileCopyUserId to "Sao chép ID",
    UiTextKey.MyProfileCopyUsername to "Sao chép tên",
    UiTextKey.MyProfileShare to "Chia sẻ hồ sơ",
    UiTextKey.MyProfileCopied to "Đã sao chép!",
    UiTextKey.MyProfileLanguages to "Ngôn ngữ",
    UiTextKey.MyProfilePrimaryLanguage to "Ngôn ngữ chính",
    UiTextKey.MyProfileLearningLanguages to "Ngôn ngữ đang học",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Trang bạn bè",
    UiTextKey.FriendsInfoMessage to "• Vuốt xuống để làm mới danh sách, lời mời, và trạng thái.\n" +
            "• Nhấn thẻ để mở chat.\n" +
            "• Dấu chấm đỏ (●) cho tin nhắn chưa đọc, ✓✓ để đánh dấu đã đọc.\n" +
            "• 📥 cho hộp chia sẻ, ✓✓ để xóa dấu chấm.\n" +
            "• 🚫 để chặn — bạn bị xóa và không thể liên lạc.\n" +
            "• Chặn cũng xóa lịch sử chat.\n" +
            "• Biểu tượng thùng rác cho chế độ xóa.\n" +
            "• Xóa bạn cũng xóa tất cả tin nhắn.\n" +
            "• Biểu tượng tìm kiếm để tìm theo tên hoặc ID.\n" +
            "• Thông báo đẩy mặc định tắt — bật trong cài đặt.\n",
    UiTextKey.FriendsEmptyTitle to "Chưa có bạn bè",
    UiTextKey.FriendsEmptyMessage to "Nhấn \"Thêm bạn\" để tìm theo tên hoặc ID.\n",
    UiTextKey.FriendsInfoGotItButton to "Đã hiểu",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Trang học tập",
    UiTextKey.LearningInfoMessage to "• Vuốt để làm mới bản ghi.\n" +
            "• Mỗi thẻ hiển thị ngôn ngữ và số lượng.\n" +
            "• \"Tạo\" cho tài liệu (miễn phí lần đầu).\n" +
            "• Tạo lại cần tối thiểu 5 bản ghi thêm.\n" +
            "• Nút tài liệu mở nội dung đã tạo.\n" +
            "• Sau tài liệu, bạn có thể làm câu đố.",
    UiTextKey.LearningEmptyTitle to "Chưa có lịch sử dịch",
    UiTextKey.LearningEmptyMessage to "Bắt đầu dịch để tạo bản ghi.\n" +
            "Tài liệu được tạo từ lịch sử.\n" +
            "Sau khi dịch, vuốt để làm mới.",
    UiTextKey.LearningInfoGotItButton to "Đã hiểu",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Trang ngân hàng từ",
    UiTextKey.WordBankInfoMessage to "• Vuốt để làm mới danh sách ngôn ngữ.\n" +
            "• Chọn ngôn ngữ để xem hoặc tạo.\n" +
            "• Ngân hàng từ được tạo từ lịch sử.\n" +
            "• Cập nhật cần tối thiểu 20 bản ghi thêm.\n" +
            "• Thêm từ tùy chỉnh thủ công.\n" +
            "• Chia sẻ từ với bạn bè.",
    UiTextKey.WordBankInfoGotItButton to "Đã hiểu",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Hộp chia sẻ",
    UiTextKey.ShareInboxInfoMessage to "• Vuốt để làm mới hộp.\n" +
            "• Mục bạn bè chia sẻ hiện ở đây.\n" +
            "• Chấp nhận hoặc bỏ qua từ.\n" +
            "• \"Xem\" cho tài liệu và câu đố.\n" +
            "• Dấu chấm đỏ (●) cho mục mới/chưa đọc.\n" +
            "• Xác nhận trước khi bỏ qua từ chia sẻ.",
    UiTextKey.ShareInboxInfoGotItButton to "Đã hiểu",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Hiển thị hồ sơ",
    UiTextKey.MyProfileVisibilityPublic to "Công khai",
    UiTextKey.MyProfileVisibilityPrivate to "Riêng tư",
    UiTextKey.MyProfileVisibilityDescription to "Công khai: Ai cũng có thể tìm và thêm bạn.\nRiêng tư: Không hiện trong tìm kiếm.",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Bỏ qua từ",
    UiTextKey.ShareDismissWordMessage to "Bỏ qua từ chia sẻ này? Không thể hoàn tác.",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Ngôn ngữ: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Đóng",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Đã kết nối hoặc đang chờ",
    UiTextKey.AccessibilityNewMessages to "Tin nhắn mới",
    UiTextKey.AccessibilityNewReleasesIcon to "Chỉ báo mục mới",
    UiTextKey.AccessibilitySuccessIcon to "Thành công",
    UiTextKey.AccessibilityErrorIcon to "Lỗi",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Loại mục chia sẻ",
    UiTextKey.AccessibilityAddCustomWords to "Thêm từ tùy chỉnh",
    UiTextKey.AccessibilityWordBankExists to "Ngân hàng từ tồn tại",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "PH.Phản hồi",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Cài đặt thông báo",
    UiTextKey.FriendsNotifSettingsTitle to "Cài đặt thông báo",
    UiTextKey.FriendsNotifNewMessages to "Tin nhắn chat mới",
    UiTextKey.FriendsNotifFriendRequests to "Lời mời kết bạn đã nhận",
    UiTextKey.FriendsNotifRequestAccepted to "Lời mời kết bạn được chấp nhận",
    UiTextKey.FriendsNotifSharedInbox to "Mục chia sẻ mới",
    UiTextKey.FriendsNotifCloseButton to "Xong",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Huy hiệu trong ứng dụng (chấm đỏ)",
    UiTextKey.InAppBadgeMessages to "Huy hiệu tin nhắn chưa đọc",
    UiTextKey.InAppBadgeFriendRequests to "Huy hiệu lời mời kết bạn",
    UiTextKey.InAppBadgeSharedInbox to "Huy hiệu hộp chia sẻ chưa đọc",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Đăng nhập để tiếp tục.",
    UiTextKey.ErrorSaveFailedRetry to "Lưu thất bại. Thử lại.",
    UiTextKey.ErrorLoadFailedRetry to "Tải thất bại. Thử lại.",
    UiTextKey.ErrorNetworkRetry to "Lỗi mạng. Kiểm tra kết nối.",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Cần thêm {needed} bản dịch để tạo tài liệu",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Chuyển sang hội thoại trực tiếp →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Xóa chat",
    UiTextKey.ChatClearConversationTitle to "Xóa cuộc trò chuyện",
    UiTextKey.ChatClearConversationMessage to "Ẩn tất cả tin nhắn? Sẽ vẫn ẩn khi mở lại. Người khác không bị ảnh hưởng.",
    UiTextKey.ChatClearConversationConfirm to "Xóa tất cả",
    UiTextKey.ChatClearConversationSuccess to "Đã xóa cuộc trò chuyện",

    // Block user
    UiTextKey.BlockUserButton to "Chặn",
    UiTextKey.BlockUserTitle to "Chặn người dùng này?",
    UiTextKey.BlockUserMessage to "Chặn {username}? Sẽ bị xóa khỏi danh sách và không thể liên lạc.",
    UiTextKey.BlockUserConfirm to "Chặn",
    UiTextKey.BlockUserSuccess to "Đã chặn và xóa khỏi danh sách.",
    UiTextKey.BlockedUsersTitle to "Người dùng bị chặn",
    UiTextKey.BlockedUsersEmpty to "Không có người dùng bị chặn.",
    UiTextKey.UnblockUserButton to "Bỏ chặn",
    UiTextKey.UnblockUserTitle to "Bỏ chặn?",
    UiTextKey.UnblockUserMessage to "Bỏ chặn {username}? Có thể gửi lời mời lại.",
    UiTextKey.UnblockUserSuccess to "Đã bỏ chặn.",
    UiTextKey.BlockedUsersManageButton to "Quản lý chặn",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Ghi chú lời mời (tùy chọn)",
    UiTextKey.FriendsRequestNotePlaceholder to "Thêm ghi chú ngắn...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Tài liệu sẵn sàng! Nhấn để mở.",
    UiTextKey.GenerationBannerWordBank to "Ngân hàng từ sẵn sàng! Nhấn để xem.",
    UiTextKey.GenerationBannerQuiz to "Câu đố sẵn sàng! Nhấn để bắt đầu.",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Thông báo",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Tiếng Trung (Phồn thể)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Hệ thống bạn bè",
    UiTextKey.HelpFriendSystemBody to "• Tìm bạn theo tên hoặc ID\n" +
            "• Gửi, chấp nhận, hoặc từ chối lời mời\n" +
            "• Chat trực tiếp với dịch thuật\n" +
            "• Chia sẻ từ và tài liệu học\n" +
            "• Quản lý nội dung chia sẻ trong hộp\n" +
            "• Dấu chấm đỏ (●) cho nội dung chưa đọc hoặc mới\n" +
            "• Vuốt để làm mới",
    UiTextKey.HelpProfileVisibilityTitle to "Hiển thị hồ sơ",
    UiTextKey.HelpProfileVisibilityBody to "• Hồ sơ công khai hoặc riêng tư trong cài đặt\n" +
            "• Công khai: Ai cũng tìm thấy bạn\n" +
            "• Riêng tư: Không hiện trong tìm kiếm\n" +
            "• Giữ riêng tư: Chia sẻ ID để được thêm",
    UiTextKey.HelpColorPalettesTitle to "Giao diện và xu",
    UiTextKey.HelpColorPalettesBody to "• 1 chủ đề miễn phí: Bầu trời xanh (mặc định)\n" +
            "• 10 chủ đề có thể mở khóa, 10 xu mỗi cái\n" +
            "• Nhận xu từ câu đố\n" +
            "• Xu dùng cho chủ đề và mở rộng lịch sử\n" +
            "• Giao diện tự động: Sáng 6–18, Tối 18–6",
    UiTextKey.HelpPrivacyTitle to "Riêng tư và dữ liệu",
    UiTextKey.HelpPrivacyBody to "• Âm thanh chỉ để nhận diện, không lưu vĩnh viễn\n" +
            "• OCR xử lý trên thiết bị (bảo mật)\n" +
            "• Tài khoản và dữ liệu có thể xóa bất kỳ lúc nào\n" +
            "• Chế độ riêng tư: không hiện trong tìm kiếm\n" +
            "• Tất cả dữ liệu đồng bộ an toàn qua Firebase",
    UiTextKey.HelpAppVersionTitle to "Phiên bản ứng dụng",
    UiTextKey.HelpAppVersionNotes to "• Giới hạn lịch sử: 30 đến 60 mục (mở rộng bằng xu)\n" +
            "• Tên người dùng duy nhất — đổi tên giải phóng tên cũ\n" +
            "• Tự đăng xuất khi cập nhật bảo mật\n" +
            "• Tất cả dịch thuật bởi Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Dịch tức thì",
    UiTextKey.OnboardingPage1Desc to "Dịch nhanh cho câu ngắn, hội thoại trực tiếp cho đối thoại hai chiều.",
    UiTextKey.OnboardingPage2Title to "Học từ vựng",
    UiTextKey.OnboardingPage2Desc to "Tạo bảng từ vựng và câu đố từ lịch sử.",
    UiTextKey.OnboardingPage3Title to "Kết nối với bạn bè",
    UiTextKey.OnboardingPage3Desc to "Chat, chia sẻ từ, và học cùng nhau.",
    UiTextKey.OnboardingSkipButton to "Bỏ qua",
    UiTextKey.OnboardingNextButton to "Tiếp theo",
    UiTextKey.OnboardingGetStartedButton to "Bắt đầu",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Chào mừng trở lại, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Người dùng:",
    UiTextKey.ChatUserIdLabel to "ID người dùng:",
    UiTextKey.ChatLearningLabel to "Đang học:",
    UiTextKey.ChatBlockedMessage to "Không thể gửi tin nhắn cho người dùng này.",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Tìm kiếm",
    UiTextKey.CustomWordsEmptyState to "Chưa có từ tùy chỉnh",
    UiTextKey.CustomWordsEmptyHint to "Nhấn + để thêm từ",
    UiTextKey.CustomWordsNoSearchResults to "Không có từ phù hợp",
    UiTextKey.AddCustomWordHintTemplate to "Nhập từ bằng {from} và bản dịch bằng {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} bản ghi",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Email: {email}",
    UiTextKey.ProfileUsernameHintFull to "Tên người dùng cho bạn bè (3–20 ký tự, chữ cái/số/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Không có tùy chọn giọng nói cho ngôn ngữ này",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "Ứng dụng đã cập nhật. Vui lòng đăng nhập lại",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Đã đạt giới hạn yêu thích",
    UiTextKey.FavoritesLimitMessage to "Tối đa 20 mục yêu thích. Xóa một mục để thêm mới.",
    UiTextKey.FavoritesLimitGotIt to "Đã hiểu",
    UiTextKey.FavoritesInfoTitle to "Thông tin yêu thích",
    UiTextKey.FavoritesInfoMessage to "Tối đa 20 mục yêu thích (bản ghi và hội thoại). Giới hạn giảm tải cơ sở dữ liệu. Xóa một mục để thêm mới.",
    UiTextKey.FavoritesInfoGotIt to "Đã hiểu",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Không thể thay đổi",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "Ngôn ngữ chính chỉ thay đổi được mỗi 30 ngày. Còn {days} ngày nữa.",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "Ngôn ngữ chính chỉ thay đổi được mỗi 30 ngày. Còn {hours} giờ nữa.",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Xác nhận thay đổi ngôn ngữ",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Thay đổi ngôn ngữ chính sẽ ngăn thay đổi trong 30 ngày. Tiếp tục?",

    // Bottom navigation
    UiTextKey.NavHome to "Trang chủ",
    UiTextKey.NavTranslate to "Dịch",
    UiTextKey.NavLearn to "Học",
    UiTextKey.NavFriends to "Bạn bè",
    UiTextKey.NavSettings to "Cài đặt",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Cần quyền camera",
    UiTextKey.CameraPermissionMessage to "Cho phép camera để nhận diện chữ.",
    UiTextKey.CameraPermissionGrant to "Cho phép",
    UiTextKey.MicPermissionMessage to "Cần quyền micro để nhận diện giọng nói.",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Xóa {count} mục đã chọn? Không thể hoàn tác.",
    UiTextKey.WordBankDeleteConfirm to "Xóa \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Chấp nhận tất cả",
    UiTextKey.FriendsRejectAllButton to "Từ chối tất cả",
    UiTextKey.ChatBlockedCannotSend to "Không thể gửi tin nhắn",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Mở khóa {name}?",
    UiTextKey.ShopUnlockCost to "Chi phí: {cost} xu",
    UiTextKey.ShopYourCoins to "Xu của tôi: {coins}",
    UiTextKey.ShopUnlockButton to "Mở khóa",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Ngôn ngữ chính",
    UiTextKey.HelpPrimaryLanguageBody to "• Ngôn ngữ chính dùng cho giải thích học tập\n" +
            "• Chỉ thay đổi được mỗi 30 ngày để nhất quán\n" +
            "• Thay đổi trong cài đặt\n" +
            "• Cài đặt toàn cục cho tất cả trang",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Mẹo: Để nhận diện tốt hơn, đặt \"Ngôn ngữ nguồn\" sang ngôn ngữ của chữ cần quét.",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Không thể thay đổi",
    UiTextKey.SettingsUsernameCooldownMessage to "Tên người dùng chỉ thay đổi được mỗi 30 ngày. Còn {days} ngày nữa.",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Tên người dùng chỉ thay đổi được mỗi 30 ngày. Còn {hours} giờ nữa.",
    UiTextKey.SettingsUsernameConfirmTitle to "Xác nhận đổi tên",
    UiTextKey.SettingsUsernameConfirmMessage to "Đổi tên người dùng sẽ ngăn thay đổi trong 30 ngày. Tiếp tục?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Không có kết nối internet. Kiểm tra kết nối.",
    UiTextKey.ErrorPermissionDenied to "Bạn không có quyền cho hành động này.",
    UiTextKey.ErrorSessionExpired to "Phiên đã hết. Vui lòng đăng nhập lại.",
    UiTextKey.ErrorItemNotFound to "Không tìm thấy mục. Có thể đã bị xóa.",
    UiTextKey.ErrorAccessDenied to "Truy cập bị từ chối.",
    UiTextKey.ErrorAlreadyFriends to "Đã là bạn bè.",
    UiTextKey.ErrorUserBlocked to "Hành động không được phép. Người dùng có thể bị chặn.",
    UiTextKey.ErrorRequestNotFound to "Lời mời này không còn tồn tại.",
    UiTextKey.ErrorRequestAlreadyHandled to "Lời mời này đã được xử lý.",
    UiTextKey.ErrorNotAuthorized to "Bạn không được phép thực hiện hành động này.",
    UiTextKey.ErrorRateLimited to "Quá nhiều yêu cầu. Thử lại sau.",
    UiTextKey.ErrorInvalidInput to "Đầu vào không hợp lệ. Kiểm tra và thử lại.",
    UiTextKey.ErrorOperationNotAllowed to "Thao tác này hiện không được phép.",
    UiTextKey.ErrorTimeout to "Hết thời gian. Thử lại.",
    UiTextKey.ErrorSendMessageFailed to "Gửi tin nhắn thất bại. Thử lại.",
    UiTextKey.ErrorFriendRequestSent to "Đã gửi lời mời kết bạn!",
    UiTextKey.ErrorFriendRequestFailed to "Gửi lời mời thất bại.",
    UiTextKey.ErrorFriendRemoved to "Đã xóa bạn.",
    UiTextKey.ErrorFriendRemoveFailed to "Xóa thất bại. Kiểm tra kết nối.",
    UiTextKey.ErrorBlockSuccess to "Đã chặn người dùng.",
    UiTextKey.ErrorBlockFailed to "Chặn thất bại. Thử lại.",
    UiTextKey.ErrorUnblockSuccess to "Đã bỏ chặn.",
    UiTextKey.ErrorUnblockFailed to "Bỏ chặn thất bại. Thử lại.",
    UiTextKey.ErrorAcceptRequestSuccess to "Đã chấp nhận lời mời!",
    UiTextKey.ErrorAcceptRequestFailed to "Chấp nhận thất bại. Thử lại.",
    UiTextKey.ErrorRejectRequestSuccess to "Đã từ chối lời mời.",
    UiTextKey.ErrorRejectRequestFailed to "Từ chối thất bại. Thử lại.",
    UiTextKey.ErrorOfflineMessage to "Bạn đang ngoại tuyến. Một số tính năng có thể không khả dụng.",
    UiTextKey.ErrorChatDeletionFailed to "Xóa chat thất bại. Thử lại.",
    UiTextKey.ErrorGenericRetry to "Đã xảy ra lỗi. Thử lại.",
)
