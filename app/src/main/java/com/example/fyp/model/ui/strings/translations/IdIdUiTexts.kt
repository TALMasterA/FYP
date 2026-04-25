package com.example.fyp.model.ui

/**
 * Indonesian (id-ID) UI text map — Antarmuka dalam Bahasa Indonesia.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val IdIdUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Gunakan mikrofon",
    UiTextKey.CopyButton to "Salin",
    UiTextKey.SpeakScriptButton to "Baca teks asli",
    UiTextKey.TranslateButton to "Terjemahkan",
    UiTextKey.CopyTranslationButton to "Salin terjemahan",
    UiTextKey.SpeakTranslationButton to "Baca terjemahan",
    UiTextKey.RecognizingStatus to "Merekam...Bicara sekarang, berhenti otomatis.",
    UiTextKey.TranslatingStatus to "Menerjemahkan...",
    UiTextKey.SpeakingOriginalStatus to "Membaca asli...",
    UiTextKey.SpeakingTranslationStatus to "Membaca terjemahan...",
    UiTextKey.SpeakingLabel to "Membaca",
    UiTextKey.FinishedSpeakingOriginal to "Selesai membaca asli",
    UiTextKey.FinishedSpeakingTranslation to "Selesai membaca terjemahan",
    UiTextKey.TtsErrorTemplate to "Kesalahan suara: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Bahasa aplikasi",
    UiTextKey.DetectLanguageLabel to "Deteksi bahasa",
    UiTextKey.TranslateToLabel to "Terjemahkan ke",

    // Language names
    UiTextKey.LangEnUs to "Inggris",
    UiTextKey.LangZhHk to "Kanton",
    UiTextKey.LangJaJp to "Jepang",
    UiTextKey.LangZhCn to "Tionghoa (Sederhana)",
    UiTextKey.LangFrFr to "Prancis",
    UiTextKey.LangDeDe to "Jerman",
    UiTextKey.LangKoKr to "Korea",
    UiTextKey.LangEsEs to "Spanyol",
    UiTextKey.LangIdId to "Indonesia",
    UiTextKey.LangViVn to "Vietnam",
    UiTextKey.LangThTh to "Thai",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Melayu",
    UiTextKey.LangPtBr to "Portugis",
    UiTextKey.LangItIt to "Italia",
    UiTextKey.LangRuRu to "Rusia",

    // Navigation
    UiTextKey.NavHistory to "Riwayat",
    UiTextKey.NavLogin to "Masuk",
    UiTextKey.NavLogout to "Keluar",
    UiTextKey.NavBack to "Kembali",
    UiTextKey.ActionCancel to "Batal",
    UiTextKey.ActionDelete to "Hapus",
    UiTextKey.ActionOpen to "Buka",
    UiTextKey.ActionName to "Namai",
    UiTextKey.ActionSave to "Simpan",
    UiTextKey.ActionConfirm to "Konfirmasi",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Ketik di sini atau gunakan mikrofon...",
    UiTextKey.SpeechTranslatedPlaceholder to "Terjemahan akan muncul di sini...",
    UiTextKey.StatusAzureErrorTemplate to "Kesalahan Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Kesalahan terjemahan: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Masuk untuk menerjemahkan",
    UiTextKey.StatusRecognizePreparing to "Menyiapkan mikrofon...(jangan bicara)",
    UiTextKey.StatusRecognizeListening to "Mendengarkan...Bicara sekarang.",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Halaman sebelumnya",
    UiTextKey.PaginationNextLabel to "Halaman berikutnya",
    UiTextKey.PaginationPageLabelTemplate to "Halaman {page} dari {total}",

    // Toast
    UiTextKey.ToastCopied to "Disalin",
    UiTextKey.DisableText to "Masuk untuk menerjemahkan dan menyimpan riwayat.",

    // Error
    UiTextKey.ErrorRetryButton to "Coba lagi",
    UiTextKey.ErrorGenericMessage to "Terjadi kesalahan. Silakan coba lagi.",

    // Shop
    UiTextKey.ShopTitle to "Toko",
    UiTextKey.ShopCoinBalance to "Koin saya",
    UiTextKey.ShopHistoryExpansionTitle to "Perluasan riwayat",
    UiTextKey.ShopHistoryExpansionDesc to "Perluas batas riwayat Anda untuk melihat lebih banyak terjemahan.",
    UiTextKey.ShopCurrentLimit to "Batas saat ini: {limit} entri",
    UiTextKey.ShopMaxLimit to "Batas maksimum:",
    UiTextKey.ShopBuyHistoryExpansion to "Beli (+{increment} entri, {cost} koin)",
    UiTextKey.ShopInsufficientCoins to "Koin tidak cukup",
    UiTextKey.ShopMaxLimitReached to "Batas maksimum tercapai",
    UiTextKey.ShopHistoryExpandedTitle to "Berhasil diperluas!",
    UiTextKey.ShopHistoryExpandedMessage to "Batas riwayat Anda sekarang {limit} entri! Anda dapat melihat lebih banyak terjemahan!",
    UiTextKey.ShopColorPaletteTitle to "Tema warna",
    UiTextKey.ShopColorPaletteDesc to "Pilih tema warna Anda, 10 koin per tema",
    UiTextKey.ShopEntry to "Toko",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Pengaturan suara",
    UiTextKey.VoiceSettingsDesc to "Pilih suara pembacaan untuk setiap bahasa.",

    // Instructions
    UiTextKey.SpeechInstructions to "Ketuk mikrofon untuk pengenalan suara, lalu terjemahkan. Jika deteksi otomatis tidak diperbarui setelah mengganti teks atau bahasa, ketuk segarkan di kanan atas.",
    UiTextKey.HomeInstructions to "Pilih fitur untuk memulai.",
    UiTextKey.ContinuousInstructions to "Pilih dua bahasa dan mulai mode percakapan.",

    // Home
    UiTextKey.HomeTitle to "Terjemahan Instan",
    UiTextKey.HelpTitle to "Bantuan",
    UiTextKey.SpeechTitle to "Terjemahan Cepat",
    UiTextKey.HomeStartButton to "Mulai terjemahan",
    UiTextKey.HomeFeaturesTitle to "Fitur",
    UiTextKey.HomeDiscreteDescription to "Terjemahan teks dan suara singkat",
    UiTextKey.HomeContinuousDescription to "Terjemahan dua arah secara langsung",
    UiTextKey.HomeLearningDescription to "Buat materi belajar dan kuis dari riwayat",

    // Help
    UiTextKey.HelpCurrentTitle to "Fitur saat ini",
    UiTextKey.HelpCautionTitle to "Perhatian",
    UiTextKey.HelpCurrentFeatures to "Fitur saat ini:\n" +
            "  • Terjemahan Cepat: pengenalan suara dan terjemahan\n" +
            "  • Percakapan Langsung: terjemahan suara dua arah\n" +
            "  • Riwayat: lihat terjemahan sebelumnya\n" +
            "  • Materi Belajar: buat kosakata dan kuis\n\n" +
            "Terjemahan:\n" +
            "  • Pengenalan suara Azure AI\n" +
            "  • Layanan terjemahan Azure\n",
    UiTextKey.HelpCaution to "Perhatian:\n" +
            "  • Pengenalan suara memerlukan koneksi internet\n" +
            "  • Cache terjemahan lokal tersedia offline\n" +
            "  • Verifikasi terjemahan penting dengan profesional\n\n" +
            "Akun dan data:\n" +
            "  • Riwayat, pembelajaran, dan koin memerlukan login\n" +
            "  • Data disimpan dengan aman di Firebase Firestore\n\n" +
            "Pemecahan masalah:\n" +
            "  • Jika tidak ada yang berfungsi, mulai ulang aplikasi\n",
    UiTextKey.HelpNotesTitle to "Tips",
    UiTextKey.HelpNotes to "💡 Tips penggunaan:\n\n" +
            "Untuk terjemahan terbaik:\n" +
            "  • Bicara dengan jelas dan kecepatan sedang\n" +
            "  • Kurangi kebisingan untuk pengenalan lebih baik\n" +
            "  • Terjemahan cepat cocok untuk frasa pendek\n\n" +
            "Bahasa aplikasi:\n" +
            "  • Bahasa default: Inggris, lebih banyak bahasa oleh AI\n" +
            "  • Versi Kanton diterjemahkan secara manual\n" +
            "Pembaruan dan umpan balik:\n" +
            "  • Versi aplikasi di Pengaturan → Tentang\n" +
            "  • Kirim umpan balik di Pengaturan → Umpan Balik\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Umpan Balik",
    UiTextKey.FeedbackDesc to "Terima kasih atas umpan balik Anda! Bagikan saran, bug, atau penilaian.",
    UiTextKey.FeedbackMessagePlaceholder to "Ketik umpan balik Anda...",
    UiTextKey.FeedbackSubmitButton to "Kirim",
    UiTextKey.FeedbackSubmitting to "Mengirim...",
    UiTextKey.FeedbackSuccessTitle to "Terima kasih!",
    UiTextKey.FeedbackSuccessMessage to "Umpan balik Anda berhasil dikirim. Terima kasih!",
    UiTextKey.FeedbackErrorTitle to "Gagal mengirim",
    UiTextKey.FeedbackErrorMessage to "Gagal mengirim. Periksa koneksi Anda dan coba lagi.",
    UiTextKey.FeedbackMessageRequired to "Silakan tulis umpan balik Anda.",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Percakapan Langsung",
    UiTextKey.ContinuousStartButton to "Mulai percakapan",
    UiTextKey.ContinuousStopButton to "Hentikan rekaman",
    UiTextKey.ContinuousStartScreenButton to "Percakapan Langsung",
    UiTextKey.ContinuousPersonALabel to "A berbicara",
    UiTextKey.ContinuousPersonBLabel to "B berbicara",
    UiTextKey.ContinuousCurrentStringLabel to "Teks saat ini:",
    UiTextKey.ContinuousSpeakerAName to "Orang A",
    UiTextKey.ContinuousSpeakerBName to "Orang B",
    UiTextKey.ContinuousTranslationSuffix to " · Terjemahan",
    UiTextKey.ContinuousPreparingMicText to "Menyiapkan mikrofon...(jangan bicara)",
    UiTextKey.ContinuousTranslatingText to "Menerjemahkan...",

    // History
    UiTextKey.HistoryTitle to "Riwayat",
    UiTextKey.HistoryTabDiscrete to "Terjemahan Cepat",
    UiTextKey.HistoryTabContinuous to "Percakapan Langsung",
    UiTextKey.HistoryNoContinuousSessions to "Belum ada sesi percakapan.",
    UiTextKey.HistoryNoDiscreteRecords to "Belum ada catatan terjemahan.",
    UiTextKey.DialogDeleteRecordTitle to "Hapus catatan?",
    UiTextKey.DialogDeleteRecordMessage to "Tindakan ini tidak dapat dibatalkan.",
    UiTextKey.DialogDeleteSessionTitle to "Hapus percakapan?",
    UiTextKey.DialogDeleteSessionMessage to "Semua catatan dalam percakapan ini akan dihapus. Tidak dapat dibatalkan.",
    UiTextKey.HistoryDeleteSessionButton to "Hapus",
    UiTextKey.HistoryNameSessionTitle to "Namai",
    UiTextKey.HistorySessionNameLabel to "Nama percakapan",
    UiTextKey.HistorySessionTitleTemplate to "Percakapan {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} entri",

    // Filter
    UiTextKey.FilterDropdownDefault to "Semua bahasa",
    UiTextKey.FilterTitle to "Filter riwayat",
    UiTextKey.FilterLangDrop to "Bahasa",
    UiTextKey.FilterKeyword to "Kata kunci",
    UiTextKey.FilterApply to "Terapkan",
    UiTextKey.FilterCancel to "Batal",
    UiTextKey.FilterClear to "Hapus filter",
    UiTextKey.FilterHistoryScreenTitle to "Filter",

    // Auth
    UiTextKey.AuthLoginTitle to "Masuk",
    UiTextKey.AuthRegisterTitle to "Daftar (dijeda)",
    UiTextKey.AuthLoginHint to "Gunakan email dan kata sandi terdaftar Anda.",
    UiTextKey.AuthRegisterRules to "Pendaftaran dijeda selama pengembangan.\nCatatan: Email tidak valid akan mencegah pemulihan kata sandi.\n" +
            "Aturan pendaftaran:\n" +
            "• Format email valid (cth. nama@contoh.com)\n" +
            "• Kata sandi minimal 8 karakter\n" +
            "• Konfirmasi harus cocok",
    UiTextKey.AuthEmailLabel to "Email",
    UiTextKey.AuthPasswordLabel to "Kata sandi",
    UiTextKey.AuthConfirmPasswordLabel to "Konfirmasi kata sandi",
    UiTextKey.AuthLoginButton to "Masuk",
    UiTextKey.AuthRegisterButton to "Daftar",
    UiTextKey.AuthToggleToRegister to "Belum punya akun? Daftar (dijeda)",
    UiTextKey.AuthToggleToLogin to "Sudah punya akun? Masuk",
    UiTextKey.AuthErrorPasswordsMismatch to "Kata sandi tidak cocok.",
    UiTextKey.AuthErrorPasswordTooShort to "Kata sandi minimal 8 karakter.",
    UiTextKey.AuthRegistrationDisabled to "Pendaftaran dijeda selama pengembangan.",
    UiTextKey.AuthResetEmailSent to "Email pemulihan terkirim (jika ada). Periksa kotak masuk.",

    // Password reset
    UiTextKey.ForgotPwText to "Lupa kata sandi?",
    UiTextKey.ResetPwTitle to "Pulihkan kata sandi",
    UiTextKey.ResetPwText to "Masukkan email akun Anda. Kami akan mengirim tautan pemulihan.\nPastikan email yang terdaftar.\n",
    UiTextKey.ResetSendingText to "Mengirim...",
    UiTextKey.ResetSendText to "Kirim email pemulihan",

    // Settings
    UiTextKey.SettingsTitle to "Pengaturan",
    UiTextKey.SettingsPrimaryLanguageTitle to "Bahasa utama",
    UiTextKey.SettingsPrimaryLanguageDesc to "Digunakan untuk penjelasan dan saran pembelajaran",
    UiTextKey.SettingsPrimaryLanguageLabel to "Bahasa utama",
    UiTextKey.SettingsFontSizeTitle to "Ukuran font",
    UiTextKey.SettingsFontSizeDesc to "Sesuaikan ukuran teks untuk kenyamanan membaca (sinkronisasi antar perangkat)",
    UiTextKey.SettingsScaleTemplate to "Ukuran: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Tema warna",
    UiTextKey.SettingsColorPaletteDesc to "Pilih tema warna Anda, 10 koin per tema",
    UiTextKey.SettingsColorCostTemplate to "{cost} koin",
    UiTextKey.SettingsColorUnlockButton to "Buka kunci",
    UiTextKey.SettingsColorSelectButton to "Pilih",
    UiTextKey.SettingsColorAlreadyUnlocked to "Sudah dibuka",
    UiTextKey.SettingsPreviewHeadline to "Judul: Pratinjau teks besar",
    UiTextKey.SettingsPreviewBody to "Isi: Pratinjau teks normal",
    UiTextKey.SettingsPreviewLabel to "Label: Pratinjau teks kecil",
    UiTextKey.SettingsAboutTitle to "Tentang",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Sudah masuk, pengaturan disimpan dan disinkronkan otomatis.",
    UiTextKey.SettingsThemeTitle to "Tema",
    UiTextKey.SettingsThemeDesc to "Pilih tampilan: Sistem, Terang, Gelap, atau Terjadwal.",
    UiTextKey.SettingsThemeSystem to "Sistem",
    UiTextKey.SettingsThemeLight to "Terang",
    UiTextKey.SettingsThemeDark to "Gelap",
    UiTextKey.SettingsThemeScheduled to "Terjadwal",
    UiTextKey.SettingsResetPW to "Pulihkan kata sandi",
    UiTextKey.SettingsQuickLinks to "Pengaturan detail",
    UiTextKey.SettingsNotLoggedInWarning to "Masuk untuk melihat pengaturan akun. Bahasa aplikasi dapat diganti tanpa masuk.",
    UiTextKey.SettingsVoiceTitle to "Pengaturan suara",
    UiTextKey.SettingsVoiceDesc to "Pilih suara pembacaan untuk setiap bahasa.",
    UiTextKey.SettingsVoiceLanguageLabel to "Bahasa",
    UiTextKey.SettingsVoiceSelectLabel to "Suara",
    UiTextKey.SettingsVoiceDefault to "Default",

    // Learning
    UiTextKey.LearningTitle to "Pembelajaran",
    UiTextKey.LearningHintCount to "(*) Jumlah = terjemahan dengan bahasa ini.",
    UiTextKey.LearningErrorTemplate to "Kesalahan: %s",
    UiTextKey.LearningGenerate to "Buat",
    UiTextKey.LearningRegenerate to "Buat ulang",
    UiTextKey.LearningGenerating to "Membuat...",
    UiTextKey.LearningOpenSheetTemplate to "Lembar {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Lembar belajar {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Bahasa utama: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Catatan saat ini: {nowCount} (saat dibuat: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Tidak ada konten di lembar.",
    UiTextKey.LearningSheetRegenerate to "Buat ulang",
    UiTextKey.LearningSheetGenerating to "Membuat...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Apa ini?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Lembar belajar yang dibuat dari riwayat terjemahan Anda. Berisi kosakata, definisi, contoh, dan catatan tata bahasa. Uji pengetahuan Anda dengan kuis!",
    UiTextKey.LearningRegenBlockedTitle to "Tidak dapat membuat ulang",
    UiTextKey.LearningRegenBlockedMessage to "Diperlukan minimal 5 catatan tambahan untuk membuat ulang. Kurang {needed}.",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Kurang {needed} catatan untuk membuat ulang (min. 5)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Jumlah harus melebihi pembuatan terakhir",
    UiTextKey.LearningRegenInfoTitle to "Aturan pembuatan ulang",
    UiTextKey.LearningRegenInfoMessage to "Membuat ulang materi belajar:\n\n• Pembuatan pertama: kapan saja\n• Pembuatan ulang: minimal 5 catatan tambahan\n\nTombol menjadi biru saat cukup. Jika abu-abu, terus terjemahkan!\n\n💡 Tips: Jika jumlah tidak diperbarui, mulai ulang aplikasi.",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Kuis sudah dibuat untuk versi ini. Buat lembar baru untuk kuis baru.",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Kuis: {language}",
    UiTextKey.QuizOpenButton to "📝 Kuis",
    UiTextKey.QuizGenerateButton to "🔄 Buat kuis",
    UiTextKey.QuizGenerating to "⏳ Membuat...",
    UiTextKey.QuizUpToDate to "✓ Terbaru",
    UiTextKey.QuizBlocked to "🚫 Diblokir",
    UiTextKey.QuizWait to "⏳ Tunggu...",
    UiTextKey.QuizMaterialsQuizTemplate to "Materi: {materials} | Kuis: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Bisa mendapat koin!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 {count} lagi untuk koin",
    UiTextKey.QuizCancelButton to "Batal",
    UiTextKey.QuizPreviousButton to "Pertanyaan sebelumnya",
    UiTextKey.QuizNextButton to "Pertanyaan berikutnya",
    UiTextKey.QuizSubmitButton to "Kirim",
    UiTextKey.QuizRetakeButton to "Ulangi",
    UiTextKey.QuizBackButton to "Kembali",
    UiTextKey.QuizLoadingText to "Memuat kuis...",
    UiTextKey.QuizGeneratingText to "Membuat kuis...",
    UiTextKey.QuizNoMaterialsTitle to "Materi tidak ditemukan",
    UiTextKey.QuizNoMaterialsMessage to "Buat materi belajar terlebih dahulu, lalu akses kuis.",
    UiTextKey.QuizErrorTitle to "⚠️ Kesalahan kuis",
    UiTextKey.QuizErrorSuggestion to "Saran: Gunakan tombol di atas untuk membuat kuis.",
    UiTextKey.QuizCompletedTitle to "Kuis selesai!",
    UiTextKey.QuizAnswerReviewTitle to "Tinjau jawaban",
    UiTextKey.QuizYourAnswerTemplate to "Jawaban Anda: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Jawaban benar: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Pertanyaan {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Tidak dapat membuat ulang: Materi({materials}) < Kuis({quiz}), tambah terjemahan.",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Pembuatan lain sedang berlangsung. Tunggu.",
    UiTextKey.QuizCoinRulesTitle to "🪙 Aturan koin",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Cara mendapat:",
    UiTextKey.QuizCoinRulesRequirements to "Persyaratan:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Status saat ini:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Bisa mendapat koin di kuis berikutnya!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• {count} lagi untuk koin",
    UiTextKey.QuizCoinRule1Coin to "• 1 koin per jawaban benar",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Hanya percobaan pertama yang dihitung",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Kuis harus sesuai versi materi",
    UiTextKey.QuizCoinRulePlus10 to "• Minimal 10 catatan lebih dari kuis berbayar terakhir",
    UiTextKey.QuizCoinRuleNoDelete to "• Koin tidak dikembalikan saat menghapus catatan",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Materi: {count} catatan",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Kuis: {count} catatan",
    UiTextKey.QuizCoinRuleGotIt to "Mengerti!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Buat kuis baru?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Bisa mendapat koin dengan kuis ini! (hanya percobaan pertama)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Tidak bisa mendapat koin dengan kuis ini.",
    UiTextKey.QuizRegenNeedMoreTemplate to "Butuh {count} terjemahan lagi untuk memenuhi syarat (10 lebih dari kuis berbayar terakhir).",
    UiTextKey.QuizRegenReminder to "Tips: Anda bisa berlatih dan mengulangi, tapi koin hanya diberikan pada percobaan pertama dengan catatan cukup.",
    UiTextKey.QuizRegenGenerateButton to "Buat",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Koin diperoleh!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Selamat! Anda mendapat {Coins} koin!",
    UiTextKey.QuizCoinsRule1 to "• 1 koin per jawaban benar di percobaan pertama",
    UiTextKey.QuizCoinsRule2 to "• Tidak ada koin di percobaan selanjutnya",
    UiTextKey.QuizCoinsRule3 to "• Kuis baru memerlukan 10 catatan tambahan",
    UiTextKey.QuizCoinsRule4 to "• Kuis harus sesuai versi materi",
    UiTextKey.QuizCoinsRule5 to "• Total koin terlihat di riwayat",
    UiTextKey.QuizCoinsGreatButton to "Hebat!",
    UiTextKey.QuizOutdatedMessage to "Kuis ini berdasarkan lembar sebelumnya.",
    UiTextKey.QuizRecordsLabel to "Catatan",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Koin saya",
    UiTextKey.HistoryCoinRulesTitle to "Aturan koin:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Cara mendapat:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 koin per jawaban benar",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Hanya percobaan pertama per versi",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Kuis harus sesuai materi saat ini",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Aturan anti-curang:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Minimal 10 terjemahan baru sejak perolehan terakhir",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• Versi kuis harus sesuai materi",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Menghapus catatan memblokir pembuatan ulang (kecuali jumlah melebihi sebelumnya)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Tidak ada koin di percobaan selanjutnya",
    UiTextKey.HistoryCoinTipsTitle to "💡 Tips:",
    UiTextKey.HistoryCoinTipsRule1 to "• Tambahkan terjemahan secara teratur",
    UiTextKey.HistoryCoinTipsRule2 to "• Belajar dengan baik sebelum percobaan pertama!",
    UiTextKey.HistoryCoinGotItButton to "Mengerti!",

    // History info
    UiTextKey.HistoryInfoTitle to "Info riwayat",
    UiTextKey.HistoryInfoLimitMessage to "Riwayat menampilkan {limit} entri terakhir. Perluas batas di toko!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Catatan lama disimpan tapi disembunyikan untuk performa.",
    UiTextKey.HistoryInfoFavoritesMessage to "Untuk menyimpan terjemahan secara permanen, ketuk ❤️ untuk favorit di riwayat.",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Lihat yang disimpan di Pengaturan → Favorit.",
    UiTextKey.HistoryInfoFilterMessage to "Gunakan filter untuk mencari di {limit} entri yang ditampilkan.",
    UiTextKey.HistoryInfoGotItButton to "Mengerti",

    // Word bank
    UiTextKey.WordBankTitle to "Bank kata",
    UiTextKey.WordBankSelectLanguage to "Pilih bahasa untuk melihat atau membuat bank kata:",
    UiTextKey.WordBankNoHistory to "Tidak ada riwayat terjemahan",
    UiTextKey.WordBankNoHistoryHint to "Mulai menerjemahkan untuk membangun bank kata!",
    UiTextKey.WordBankWordsCount to "Kata",
    UiTextKey.WordBankGenerating to "Membuat...",
    UiTextKey.WordBankGenerate to "Buat bank kata",
    UiTextKey.WordBankRegenerate to "Buat ulang bank kata",
    UiTextKey.WordBankRefresh to "🔄 Segarkan bank",
    UiTextKey.WordBankEmpty to "Bank kata kosong",
    UiTextKey.WordBankEmptyHint to "Ketuk di atas untuk membuat dari riwayat Anda.",
    UiTextKey.WordBankExample to "Contoh:",
    UiTextKey.WordBankDifficulty to "Kesulitan:",
    UiTextKey.WordBankFilterCategory to "Kategori",
    UiTextKey.WordBankFilterCategoryAll to "Semua kategori",
    UiTextKey.WordBankFilterDifficultyLabel to "Kesulitan:",
    UiTextKey.WordBankFilterNoResults to "Tidak ada kata yang cocok dengan filter",
    UiTextKey.WordBankRefreshAvailable to "✅ Pembaruan tersedia!",
    UiTextKey.WordBankRecordsNeeded to "Catatan (20 diperlukan untuk pembaruan)",
    UiTextKey.WordBankRegenInfoTitle to "Aturan pembaruan",
    UiTextKey.WordBankRegenInfoMessage to "Memperbarui bank kata:\n\n• Pembuatan pertama: kapan saja\n• Pembaruan: minimal 20 catatan tambahan\n\nTombol menjadi biru saat cukup. Jika abu-abu, terus terjemahkan!\n\n💡 Tips: Jika jumlah tidak diperbarui, mulai ulang aplikasi.",
    UiTextKey.WordBankHistoryCountTemplate to "Catatan saat ini: {nowCount} (saat dibuat: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Keluar?",
    UiTextKey.DialogLogoutMessage to "Terjemahan dan akses riwayat memerlukan login kembali.",
    UiTextKey.DialogGenerateOverwriteTitle to "Timpa materi?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Materi yang ada akan ditimpa.\nBuat materi untuk {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Profil",
    UiTextKey.ProfileUsernameLabel to "Nama pengguna",
    UiTextKey.ProfileUsernameHint to "Masukkan nama pengguna",
    UiTextKey.ProfileUpdateButton to "Perbarui profil",
    UiTextKey.ProfileUpdateSuccess to "Profil diperbarui",
    UiTextKey.ProfileUpdateError to "Gagal memperbarui",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Hapus akun",
    UiTextKey.AccountDeleteWarning to "⚠️ Tindakan ini permanen dan tidak dapat dibatalkan!",
    UiTextKey.AccountDeleteConfirmMessage to "Semua data akan dihapus permanen: riwayat, bank kata, materi belajar, pengaturan. Masukkan kata sandi untuk konfirmasi.",
    UiTextKey.AccountDeletePasswordLabel to "Kata sandi",
    UiTextKey.AccountDeleteButton to "Hapus akun",
    UiTextKey.AccountDeleteSuccess to "Akun berhasil dihapus",
    UiTextKey.AccountDeleteError to "Gagal menghapus",
    UiTextKey.AccountDeleteReauthRequired to "Masukkan kata sandi untuk mengonfirmasi penghapusan",

    // Favorites
    UiTextKey.FavoritesTitle to "Favorit",
    UiTextKey.FavoritesEmpty to "Tidak ada favorit",
    UiTextKey.FavoritesAddSuccess to "Ditambahkan ke favorit",
    UiTextKey.FavoritesRemoveSuccess to "Dihapus dari favorit",
    UiTextKey.FavoritesAddButton to "Tambah ke favorit",
    UiTextKey.FavoritesRemoveButton to "Hapus dari favorit",
    UiTextKey.FavoritesNoteLabel to "Catatan",
    UiTextKey.FavoritesNoteHint to "Tambah catatan (opsional)",
    UiTextKey.FavoritesTabRecords to "Catatan",
    UiTextKey.FavoritesTabSessions to "Percakapan",
    UiTextKey.FavoritesSessionsEmpty to "Tidak ada percakapan tersimpan",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} pesan",

    // Custom words
    UiTextKey.CustomWordsTitle to "Kata kustom",
    UiTextKey.CustomWordsAdd to "Tambah kata",
    UiTextKey.CustomWordsEdit to "Edit kata",
    UiTextKey.CustomWordsDelete to "Hapus kata",
    UiTextKey.CustomWordsOriginalLabel to "Kata asli",
    UiTextKey.CustomWordsTranslatedLabel to "Terjemahan",
    UiTextKey.CustomWordsPronunciationLabel to "Pelafalan (opsional)",
    UiTextKey.CustomWordsExampleLabel to "Contoh (opsional)",
    UiTextKey.CustomWordsSaveSuccess to "Kata tersimpan",
    UiTextKey.CustomWordsDeleteSuccess to "Kata dihapus",
    UiTextKey.CustomWordsAlreadyExists to "Kata ini sudah ada",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Bahasa asli",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Bahasa terjemahan",
    UiTextKey.CustomWordsSaveButton to "Simpan",
    UiTextKey.CustomWordsCancelButton to "Batal",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Deteksi otomatis",
    UiTextKey.LanguageDetectDetecting to "Mendeteksi...",
    UiTextKey.LanguageDetectedTemplate to "Terdeteksi: {language}",
    UiTextKey.LanguageDetectFailed to "Deteksi gagal",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Pindai teks dari gambar",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Peringatan: Pengenalan teks mungkin tidak sepenuhnya akurat. Verifikasi teks yang dikenali." +
            "Mendukung Latin (Inggris, dll.), Tionghoa, Jepang, dan Korea.",
    UiTextKey.ImageRecognitionScanning to "Memindai teks...",
    UiTextKey.ImageRecognitionSuccess to "Teks berhasil dikenali",

    // Cache
    UiTextKey.CacheClearButton to "Hapus cache",
    UiTextKey.CacheClearSuccess to "Cache dihapus",
    UiTextKey.CacheStatsTemplate to "Cache: {count} terjemahan tersimpan",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Tema otomatis",
    UiTextKey.SettingsAutoThemeDesc to "Beralih otomatis antara terang dan gelap berdasarkan waktu",
    UiTextKey.SettingsAutoThemeEnabled to "Aktif",
    UiTextKey.SettingsAutoThemeDisabled to "Nonaktif",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Mode gelap mulai:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Mode terang mulai:",
    UiTextKey.SettingsAutoThemePreview to "Tema akan berubah otomatis pada waktu yang dikonfigurasi",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Mode offline",
    UiTextKey.OfflineModeMessage to "Anda sedang offline. Data tersimpan ditampilkan.",
    UiTextKey.OfflineModeRetry to "Coba sambungkan lagi",
    UiTextKey.OfflineDataCached to "Data tersimpan tersedia",
    UiTextKey.OfflineSyncPending to "Perubahan akan disinkronkan saat online",

    // Image capture
    UiTextKey.ImageSourceTitle to "Pilih sumber gambar",
    UiTextKey.ImageSourceCamera to "Ambil foto",
    UiTextKey.ImageSourceGallery to "Pilih dari galeri",
    UiTextKey.ImageSourceCancel to "Batal",
    UiTextKey.CameraCaptureContentDesc to "Ambil gambar",

    // Friends
    UiTextKey.FriendsTitle to "Teman",
    UiTextKey.FriendsMenuButton to "Teman",
    UiTextKey.FriendsAddButton to "Tambah teman",
    UiTextKey.FriendsSearchTitle to "Cari pengguna",
    UiTextKey.FriendsSearchPlaceholder to "Nama pengguna atau ID...",
    UiTextKey.FriendsSearchMinChars to "Masukkan minimal 2 karakter",
    UiTextKey.FriendsSearchNoResults to "Pengguna tidak ditemukan",
    UiTextKey.FriendsListEmpty to "Tambah teman untuk mengobrol dan berbagi materi belajar.",
    UiTextKey.FriendsRequestsSection to "Permintaan pertemanan ({count})",
    UiTextKey.FriendsSectionTitle to "Teman ({count})",
    UiTextKey.FriendsAcceptButton to "Terima",
    UiTextKey.FriendsRejectButton to "Tolak",
    UiTextKey.FriendsRemoveButton to "Hapus",
    UiTextKey.FriendsRemoveDialogTitle to "Hapus teman",
    UiTextKey.FriendsRemoveDialogMessage to "Hapus {username} dari daftar teman Anda?",
    UiTextKey.FriendsSendRequestButton to "Tambah",
    UiTextKey.FriendsRequestSentSuccess to "Permintaan pertemanan terkirim!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Permintaan diterima!",
    UiTextKey.FriendsRequestRejectedSuccess to "Permintaan ditolak",
    UiTextKey.FriendsRemovedSuccess to "Teman dihapus",
    UiTextKey.FriendsRequestFailed to "Gagal mengirim",
    UiTextKey.FriendsCloseButton to "Tutup",
    UiTextKey.FriendsCancelButton to "Batal",
    UiTextKey.FriendsRemoveConfirm to "Hapus",
    UiTextKey.FriendsNewRequestsTemplate to "Anda punya {count} permintaan pertemanan baru!",
    UiTextKey.FriendsSentRequestsSection to "Permintaan terkirim ({count})",
    UiTextKey.FriendsPendingStatus to "Menunggu",
    UiTextKey.FriendsCancelRequestButton to "Batalkan permintaan",
    UiTextKey.FriendsUnreadMessageDesc to "Kirim pesan",
    UiTextKey.FriendsDeleteModeButton to "Hapus teman",
    UiTextKey.FriendsDeleteSelectedButton to "Hapus yang dipilih",
    UiTextKey.FriendsDeleteMultipleTitle to "Hapus teman",
    UiTextKey.FriendsDeleteMultipleMessage to "Hapus {count} teman yang dipilih?",
    UiTextKey.FriendsSearchMinChars3 to "Masukkan minimal 3 karakter untuk nama",
    UiTextKey.FriendsSearchByUserIdHint to "Atau cari persis berdasarkan ID pengguna",
    UiTextKey.FriendsStatusAlreadyFriends to "Sudah berteman",
    UiTextKey.FriendsStatusRequestSent to "Permintaan terkirim — menunggu tanggapan",
    UiTextKey.FriendsStatusRequestReceived to "Pengguna ini mengirim permintaan kepada Anda",

    // Chat
    UiTextKey.ChatTitle to "Chat dengan {username}",
    UiTextKey.ChatInputPlaceholder to "Tulis pesan...",
    UiTextKey.ChatSendButton to "Kirim",
    UiTextKey.ChatEmpty to "Tidak ada pesan. Mulai percakapan!",
    UiTextKey.ChatMessageSent to "Pesan terkirim",
    UiTextKey.ChatMessageFailed to "Gagal mengirim",
    UiTextKey.ChatMarkingRead to "Menandai...",
    UiTextKey.ChatLoadingMessages to "Memuat pesan...",
    UiTextKey.ChatToday to "Hari ini",
    UiTextKey.ChatYesterday to "Kemarin",
    UiTextKey.ChatUnreadBadge to "{count} belum dibaca",
    UiTextKey.ChatTranslateButton to "Terjemahkan",
    UiTextKey.ChatTranslateDialogTitle to "Terjemahkan percakapan",
    UiTextKey.ChatTranslateDialogMessage to "Terjemahkan pesan teman ke bahasa Anda? Bahasa setiap pesan akan dideteksi dan diterjemahkan.",
    UiTextKey.ChatTranslateConfirm to "Terjemahkan semua",
    UiTextKey.ChatTranslating to "Menerjemahkan pesan...",
    UiTextKey.ChatTranslated to "Pesan diterjemahkan",
    UiTextKey.ChatShowOriginal to "Tampilkan asli",
    UiTextKey.ChatShowTranslation to "Tampilkan terjemahan",
    UiTextKey.ChatTranslateFailed to "Gagal menerjemahkan",
    UiTextKey.ChatTranslatedLabel to "Diterjemahkan",

    // Sharing
    UiTextKey.ShareTitle to "Berbagi",
    UiTextKey.ShareInboxTitle to "Kotak berbagi",
    UiTextKey.ShareInboxEmpty to "Tidak ada konten berbagi. Teman Anda bisa berbagi kata dan materi!",
    UiTextKey.ShareWordButton to "Bagikan kata",
    UiTextKey.ShareMaterialButton to "Bagikan materi",
    UiTextKey.ShareSelectFriendTitle to "Pilih teman",
    UiTextKey.ShareSelectFriendMessage to "Pilih teman untuk berbagi:",
    UiTextKey.ShareSuccess to "Berhasil dibagikan!",
    UiTextKey.ShareFailed to "Gagal membagikan",
    UiTextKey.ShareWordWith to "Bagikan kata dengan {username}",
    UiTextKey.ShareMaterialWith to "Bagikan materi dengan {username}",
    UiTextKey.ShareAcceptButton to "Terima",
    UiTextKey.ShareDismissButton to "Abaikan",
    UiTextKey.ShareAccepted to "Ditambahkan ke koleksi Anda",
    UiTextKey.ShareDismissed to "Item diabaikan",
    UiTextKey.ShareActionFailed to "Gagal melakukan tindakan",
    UiTextKey.ShareTypeWord to "Kata",
    UiTextKey.ShareTypeLearningSheet to "Lembar belajar",
    UiTextKey.ShareReceivedFrom to "Dari: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} item baru!",
    UiTextKey.ShareViewFullMaterial to "Ketuk \"Lihat\" untuk materi lengkap",
    UiTextKey.ShareDeleteItemTitle to "Hapus item",
    UiTextKey.ShareDeleteItemMessage to "Hapus item berbagi ini? Tidak dapat dibatalkan.",
    UiTextKey.ShareDeleteButton to "Hapus",
    UiTextKey.ShareViewButton to "Lihat",
    UiTextKey.ShareItemNotFound to "Item tidak ditemukan.",
    UiTextKey.ShareNoContent to "Tidak ada konten di materi ini.",
    UiTextKey.ShareSaveToSelf to "Simpan ke kotak saya",
    UiTextKey.ShareSavedToSelf to "Tersimpan di kotak Anda!",

    // My profile
    UiTextKey.MyProfileTitle to "Profil saya",
    UiTextKey.MyProfileUserId to "ID pengguna",
    UiTextKey.MyProfileUsername to "Nama pengguna",
    UiTextKey.MyProfileDisplayName to "Nama tampilan",
    UiTextKey.MyProfileCopyUserId to "Salin ID",
    UiTextKey.MyProfileCopyUsername to "Salin nama",
    UiTextKey.MyProfileShare to "Bagikan profil",
    UiTextKey.MyProfileCopied to "Disalin ke clipboard!",
    UiTextKey.MyProfileLanguages to "Bahasa",
    UiTextKey.MyProfilePrimaryLanguage to "Bahasa utama",
    UiTextKey.MyProfileLearningLanguages to "Bahasa yang dipelajari",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Halaman teman",
    UiTextKey.FriendsInfoMessage to "• Geser ke bawah untuk menyegarkan daftar, permintaan, dan status.\n" +
            "• Ketuk kartu untuk membuka chat.\n" +
            "• Titik merah (●) menunjukkan pesan belum dibaca, ✓✓ untuk tandai dibaca.\n" +
            "• 📥 untuk kotak berbagi, ✓✓ untuk hapus titik.\n" +
            "• 🚫 untuk blokir — teman dihapus dan tidak bisa menghubungi Anda.\n" +
            "• Memblokir juga menghapus riwayat chat.\n" +
            "• Ikon sampah untuk mode hapus.\n" +
            "• Menghapus teman juga menghapus semua pesan.\n" +
            "• Ikon cari untuk mencari berdasarkan nama atau ID.\n" +
            "• Notifikasi push nonaktif secara default — aktifkan di pengaturan.\n",
    UiTextKey.FriendsEmptyTitle to "Belum ada teman",
    UiTextKey.FriendsEmptyMessage to "Ketuk \"Tambah teman\" untuk mencari berdasarkan nama atau ID.\n",
    UiTextKey.FriendsInfoGotItButton to "Mengerti",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Halaman pembelajaran",
    UiTextKey.LearningInfoMessage to "• Geser untuk menyegarkan catatan.\n" +
            "• Setiap kartu menampilkan bahasa dan jumlah.\n" +
            "• \"Buat\" untuk lembar (gratis pertama kali).\n" +
            "• Pembuatan ulang memerlukan min. 5 catatan tambahan.\n" +
            "• Tombol lembar membuka materi yang dibuat.\n" +
            "• Setelah lembar, Anda bisa mengikuti kuis.",
    UiTextKey.LearningEmptyTitle to "Belum ada riwayat terjemahan",
    UiTextKey.LearningEmptyMessage to "Mulai menerjemahkan untuk membuat catatan.\n" +
            "Lembar dibuat dari riwayat.\n" +
            "Setelah menerjemahkan, geser untuk menyegarkan.",
    UiTextKey.LearningInfoGotItButton to "Mengerti",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Halaman bank kata",
    UiTextKey.WordBankInfoMessage to "• Geser untuk menyegarkan daftar bahasa.\n" +
            "• Pilih bahasa untuk melihat atau membuat.\n" +
            "• Bank kata dibuat dari riwayat.\n" +
            "• Pembaruan memerlukan min. 20 catatan tambahan.\n" +
            "• Tambahkan kata kustom secara manual.\n" +
            "• Bagikan kata dengan teman.",
    UiTextKey.WordBankInfoGotItButton to "Mengerti",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Kotak berbagi",
    UiTextKey.ShareInboxInfoMessage to "• Geser untuk menyegarkan kotak.\n" +
            "• Item yang dibagikan teman muncul di sini.\n" +
            "• Terima atau abaikan kata.\n" +
            "• \"Lihat\" untuk lembar dan kuis.\n" +
            "• Titik merah (●) menunjukkan item baru/belum dibaca.\n" +
            "• Konfirmasi sebelum mengabaikan kata yang dibagikan.",
    UiTextKey.ShareInboxInfoGotItButton to "Mengerti",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Visibilitas profil",
    UiTextKey.MyProfileVisibilityPublic to "Publik",
    UiTextKey.MyProfileVisibilityPrivate to "Privat",
    UiTextKey.MyProfileVisibilityDescription to "Publik: Siapa saja bisa menemukan dan menambahkan Anda.\nPrivat: Tidak muncul di pencarian.",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Abaikan kata",
    UiTextKey.ShareDismissWordMessage to "Abaikan kata yang dibagikan ini? Tidak dapat dibatalkan.",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Bahasa: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Tutup",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Terhubung atau menunggu",
    UiTextKey.AccessibilityNewMessages to "Pesan baru",
    UiTextKey.AccessibilityNewReleasesIcon to "Indikator item baru",
    UiTextKey.AccessibilitySuccessIcon to "Berhasil",
    UiTextKey.AccessibilityErrorIcon to "Kesalahan",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Jenis item berbagi",
    UiTextKey.AccessibilityAddCustomWords to "Tambah kata kustom",
    UiTextKey.AccessibilityWordBankExists to "Bank kata ada",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "T.Umpan Balik",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Pengaturan notif.",
    UiTextKey.FriendsNotifSettingsTitle to "Pengaturan notifikasi",
    UiTextKey.FriendsNotifNewMessages to "Pesan chat baru",
    UiTextKey.FriendsNotifFriendRequests to "Permintaan pertemanan diterima",
    UiTextKey.FriendsNotifRequestAccepted to "Permintaan pertemanan diterima",
    UiTextKey.FriendsNotifSharedInbox to "Item berbagi baru",
    UiTextKey.FriendsNotifCloseButton to "Selesai",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Lencana dalam aplikasi (titik merah)",
    UiTextKey.InAppBadgeMessages to "Lencana pesan belum dibaca",
    UiTextKey.InAppBadgeFriendRequests to "Lencana permintaan pertemanan",
    UiTextKey.InAppBadgeSharedInbox to "Lencana kotak berbagi belum dibaca",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Masuk untuk melanjutkan.",
    UiTextKey.ErrorSaveFailedRetry to "Gagal menyimpan. Coba lagi.",
    UiTextKey.ErrorLoadFailedRetry to "Gagal memuat. Coba lagi.",
    UiTextKey.ErrorNetworkRetry to "Kesalahan jaringan. Periksa koneksi Anda.",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Perlu {needed} terjemahan lagi untuk membuat materi",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Beralih ke percakapan langsung →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Hapus chat",
    UiTextKey.ChatClearConversationTitle to "Hapus percakapan",
    UiTextKey.ChatClearConversationMessage to "Sembunyikan semua pesan dari percakapan ini? Akan tetap tersembunyi saat dibuka kembali. Orang lain tidak terpengaruh.",
    UiTextKey.ChatClearConversationConfirm to "Hapus semua",
    UiTextKey.ChatClearConversationSuccess to "Percakapan dihapus",

    // Block user
    UiTextKey.BlockUserButton to "Blokir",
    UiTextKey.BlockUserTitle to "Blokir pengguna ini?",
    UiTextKey.BlockUserMessage to "Blokir {username}? Akan dihapus dari daftar Anda dan tidak bisa menghubungi Anda.",
    UiTextKey.BlockUserConfirm to "Blokir",
    UiTextKey.BlockUserSuccess to "Pengguna diblokir dan dihapus dari daftar.",
    UiTextKey.BlockedUsersTitle to "Pengguna diblokir",
    UiTextKey.BlockedUsersEmpty to "Tidak ada pengguna diblokir.",
    UiTextKey.UnblockUserButton to "Buka blokir",
    UiTextKey.UnblockUserTitle to "Buka blokir?",
    UiTextKey.UnblockUserMessage to "Buka blokir {username}? Dapat mengirim permintaan kembali.",
    UiTextKey.UnblockUserSuccess to "Pengguna tidak diblokir.",
    UiTextKey.BlockedUsersManageButton to "Kelola blokir",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Catatan permintaan (opsional)",
    UiTextKey.FriendsRequestNotePlaceholder to "Tambah catatan singkat...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Lembar siap! Ketuk untuk membuka.",
    UiTextKey.GenerationBannerWordBank to "Bank kata siap! Ketuk untuk melihat.",
    UiTextKey.GenerationBannerQuiz to "Kuis siap! Ketuk untuk memulai.",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Notifikasi",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Tionghoa (Tradisional)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Sistem teman",
    UiTextKey.HelpFriendSystemBody to "• Cari teman berdasarkan nama atau ID\n" +
            "• Kirim, terima, atau tolak permintaan\n" +
            "• Chat langsung dengan terjemahan\n" +
            "• Bagikan kata dan materi belajar\n" +
            "• Kelola konten berbagi di kotak\n" +
            "• Titik merah (●) menunjukkan konten belum dibaca atau baru\n" +
            "• Geser untuk menyegarkan",
    UiTextKey.HelpProfileVisibilityTitle to "Visibilitas profil",
    UiTextKey.HelpProfileVisibilityBody to "• Profil publik atau privat di pengaturan\n" +
            "• Publik: Siapa saja bisa menemukan Anda\n" +
            "• Privat: Tidak terlihat di pencarian\n" +
            "• Tetap privat: Bagikan ID Anda untuk ditambahkan",
    UiTextKey.HelpColorPalettesTitle to "Tema dan koin",
    UiTextKey.HelpColorPalettesBody to "• 1 tema gratis: Langit biru (default)\n" +
            "• 10 tema bisa dibuka, 10 koin per tema\n" +
            "• Dapatkan koin dari kuis\n" +
            "• Koin untuk tema dan perluasan riwayat\n" +
            "• Tema otomatis: Terang 6–18, Gelap 18–6",
    UiTextKey.HelpPrivacyTitle to "Privasi dan data",
    UiTextKey.HelpPrivacyBody to "• Audio hanya untuk pengenalan, tidak disimpan permanen\n" +
            "• OCR diproses di perangkat (privasi)\n" +
            "• Akun dan data bisa dihapus kapan saja\n" +
            "• Mode privat: tidak terlihat di pencarian\n" +
            "• Semua data disinkronkan dengan aman via Firebase",
    UiTextKey.HelpAppVersionTitle to "Versi aplikasi",
    UiTextKey.HelpAppVersionNotes to "• Batas riwayat: 30 hingga 60 entri (bisa diperluas dengan koin)\n" +
            "• Nama pengguna unik — menggantinya membebaskan yang lama\n" +
            "• Keluar otomatis saat pembaruan keamanan\n" +
            "• Semua terjemahan oleh Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Terjemahan Instan",
    UiTextKey.OnboardingPage1Desc to "Terjemahan cepat untuk frasa singkat, percakapan langsung untuk dialog dua arah.",
    UiTextKey.OnboardingPage2Title to "Pelajari kosakata",
    UiTextKey.OnboardingPage2Desc to "Buat lembar kosakata dan kuis dari riwayat Anda.",
    UiTextKey.OnboardingPage3Title to "Terhubung dengan teman",
    UiTextKey.OnboardingPage3Desc to "Mengobrol, berbagi kata, dan belajar bersama.",
    UiTextKey.OnboardingSkipButton to "Lewati",
    UiTextKey.OnboardingNextButton to "Berikutnya",
    UiTextKey.OnboardingGetStartedButton to "Mulai",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Selamat datang kembali, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Pengguna:",
    UiTextKey.ChatUserIdLabel to "ID pengguna:",
    UiTextKey.ChatLearningLabel to "Sedang belajar:",
    UiTextKey.ChatBlockedMessage to "Tidak bisa mengirim pesan ke pengguna ini.",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Cari",
    UiTextKey.CustomWordsEmptyState to "Belum ada kata kustom",
    UiTextKey.CustomWordsEmptyHint to "Ketuk + untuk menambahkan kata",
    UiTextKey.CustomWordsNoSearchResults to "Tidak ada kata yang cocok",
    UiTextKey.AddCustomWordHintTemplate to "Masukkan kata dalam {from} dan terjemahan dalam {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} catatan",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Email: {email}",
    UiTextKey.ProfileUsernameHintFull to "Nama pengguna untuk teman (3–20 karakter, alfanumerik/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Tidak ada pilihan suara untuk bahasa ini",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "Aplikasi diperbarui. Silakan masuk kembali",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Batas favorit tercapai",
    UiTextKey.FavoritesLimitMessage to "Maksimum 20 favorit. Hapus satu untuk menambahkan yang lain.",
    UiTextKey.FavoritesLimitGotIt to "Mengerti",
    UiTextKey.FavoritesInfoTitle to "Info favorit",
    UiTextKey.FavoritesInfoMessage to "Maksimum 20 favorit (catatan dan percakapan). Batas ini mengurangi beban database. Hapus satu untuk menambahkan yang lain.",
    UiTextKey.FavoritesInfoGotIt to "Mengerti",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Perubahan tidak tersedia",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "Bahasa utama hanya dapat diubah setiap 30 hari. Tunggu {days} hari lagi.",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "Bahasa utama hanya dapat diubah setiap 30 hari. Tunggu {hours} jam lagi.",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Konfirmasi perubahan bahasa",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Mengubah bahasa utama akan mencegah perubahan selama 30 hari. Lanjutkan?",

    // Bottom navigation
    UiTextKey.NavHome to "Beranda",
    UiTextKey.NavTranslate to "Terjemahkan",
    UiTextKey.NavLearn to "Belajar",
    UiTextKey.NavFriends to "Teman",
    UiTextKey.NavSettings to "Pengaturan",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Izin kamera diperlukan",
    UiTextKey.CameraPermissionMessage to "Izinkan akses kamera untuk pengenalan teks.",
    UiTextKey.CameraPermissionGrant to "Izinkan",
    UiTextKey.MicPermissionMessage to "Akses mikrofon diperlukan untuk pengenalan suara.",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Hapus {count} item yang dipilih? Tidak dapat dibatalkan.",
    UiTextKey.WordBankDeleteConfirm to "Hapus \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Terima semua",
    UiTextKey.FriendsRejectAllButton to "Tolak semua",
    UiTextKey.ChatBlockedCannotSend to "Tidak bisa mengirim pesan",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Buka kunci {name}?",
    UiTextKey.ShopUnlockCost to "Biaya: {cost} koin",
    UiTextKey.ShopYourCoins to "Koin saya: {coins}",
    UiTextKey.ShopUnlockButton to "Buka kunci",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Bahasa utama",
    UiTextKey.HelpPrimaryLanguageBody to "• Bahasa utama digunakan untuk penjelasan pembelajaran\n" +
            "• Hanya dapat diubah setiap 30 hari untuk konsistensi\n" +
            "• Dapat diubah di pengaturan\n" +
            "• Pengaturan global untuk semua halaman",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Tips: Untuk pengenalan lebih baik, atur \"Bahasa sumber\" ke bahasa teks yang akan dipindai.",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Perubahan tidak tersedia",
    UiTextKey.SettingsUsernameCooldownMessage to "Nama pengguna hanya dapat diubah setiap 30 hari. Tunggu {days} hari lagi.",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Nama pengguna hanya dapat diubah setiap 30 hari. Tunggu {hours} jam lagi.",
    UiTextKey.SettingsUsernameConfirmTitle to "Konfirmasi perubahan nama",
    UiTextKey.SettingsUsernameConfirmMessage to "Mengubah nama pengguna akan mencegah perubahan selama 30 hari. Lanjutkan?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Tidak ada koneksi internet. Periksa koneksi Anda.",
    UiTextKey.ErrorPermissionDenied to "Anda tidak memiliki izin untuk tindakan ini.",
    UiTextKey.ErrorSessionExpired to "Sesi berakhir. Silakan masuk kembali.",
    UiTextKey.ErrorItemNotFound to "Item tidak ditemukan. Mungkin sudah dihapus.",
    UiTextKey.ErrorAccessDenied to "Akses ditolak.",
    UiTextKey.ErrorAlreadyFriends to "Sudah berteman dengan pengguna ini.",
    UiTextKey.ErrorUserBlocked to "Tindakan tidak diizinkan. Pengguna mungkin diblokir.",
    UiTextKey.ErrorRequestNotFound to "Permintaan ini tidak lagi ada.",
    UiTextKey.ErrorRequestAlreadyHandled to "Permintaan ini sudah diproses.",
    UiTextKey.ErrorNotAuthorized to "Anda tidak berwenang untuk melakukan tindakan ini.",
    UiTextKey.ErrorRateLimited to "Terlalu banyak permintaan. Coba lagi nanti.",
    UiTextKey.ErrorInvalidInput to "Input tidak valid. Periksa dan coba lagi.",
    UiTextKey.ErrorOperationNotAllowed to "Operasi ini tidak diizinkan saat ini.",
    UiTextKey.ErrorTimeout to "Waktu habis. Coba lagi.",
    UiTextKey.ErrorSendMessageFailed to "Gagal mengirim pesan. Coba lagi.",
    UiTextKey.ErrorFriendRequestSent to "Permintaan pertemanan terkirim!",
    UiTextKey.ErrorFriendRequestFailed to "Gagal mengirim permintaan.",
    UiTextKey.ErrorFriendRemoved to "Teman dihapus.",
    UiTextKey.ErrorFriendRemoveFailed to "Gagal menghapus. Periksa koneksi Anda.",
    UiTextKey.ErrorBlockSuccess to "Pengguna diblokir.",
    UiTextKey.ErrorBlockFailed to "Gagal memblokir. Coba lagi.",
    UiTextKey.ErrorUnblockSuccess to "Pengguna tidak diblokir.",
    UiTextKey.ErrorUnblockFailed to "Gagal membuka blokir. Coba lagi.",
    UiTextKey.ErrorAcceptRequestSuccess to "Permintaan pertemanan diterima!",
    UiTextKey.ErrorAcceptRequestFailed to "Gagal menerima. Coba lagi.",
    UiTextKey.ErrorRejectRequestSuccess to "Permintaan pertemanan ditolak.",
    UiTextKey.ErrorRejectRequestFailed to "Gagal menolak. Coba lagi.",
    UiTextKey.ErrorOfflineMessage to "Anda sedang offline. Beberapa fitur mungkin tidak tersedia.",
    UiTextKey.ErrorChatDeletionFailed to "Gagal menghapus chat. Coba lagi.",
    UiTextKey.ErrorGenericRetry to "Terjadi kesalahan. Coba lagi.",
)
