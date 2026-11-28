package com.translator.TalknLearn.model.ui

/**
 * Malay (ms-MY) UI text map — Teks UI dalam Bahasa Melayu.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val MsMyUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Guna mikrofon",
    UiTextKey.CopyButton to "Salin",
    UiTextKey.SpeakScriptButton to "Baca asal",
    UiTextKey.TranslateButton to "Terjemah",
    UiTextKey.CopyTranslationButton to "Salin terjemahan",
    UiTextKey.SpeakTranslationButton to "Baca terjemahan",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Bahasa aplikasi",
    UiTextKey.DetectLanguageLabel to "Kesan bahasa",
    UiTextKey.TranslateToLabel to "Terjemah ke",

    // Language names
    UiTextKey.LangEnUs to "Inggeris",
    UiTextKey.LangZhHk to "Kantonis",
    UiTextKey.LangJaJp to "Jepun",
    UiTextKey.LangZhCn to "Cina (Ringkas)",
    UiTextKey.LangFrFr to "Perancis",
    UiTextKey.LangDeDe to "Jerman",
    UiTextKey.LangKoKr to "Korea",
    UiTextKey.LangEsEs to "Sepanyol",
    UiTextKey.LangIdId to "Indonesia",
    UiTextKey.LangViVn to "Vietnam",
    UiTextKey.LangThTh to "Thai",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Melayu",
    UiTextKey.LangPtBr to "Portugis",
    UiTextKey.LangItIt to "Itali",
    UiTextKey.LangRuRu to "Rusia",

    // Navigation
    UiTextKey.NavHistory to "Sejarah",
    UiTextKey.NavLogin to "Log masuk",
    UiTextKey.NavLogout to "Log keluar",
    UiTextKey.NavBack to "Kembali",
    UiTextKey.ActionCancel to "Batal",
    UiTextKey.ActionDelete to "Padam",
    UiTextKey.ActionOpen to "Buka",
    UiTextKey.ActionName to "Namakan",
    UiTextKey.ActionSave to "Simpan",
    UiTextKey.ActionConfirm to "Sahkan",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Taip di sini atau guna mikrofon...",
    UiTextKey.SpeechTranslatedPlaceholder to "Terjemahan akan muncul di sini...",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Sebelum",
    UiTextKey.PaginationNextLabel to "Seterus",
    UiTextKey.PaginationPageLabelTemplate to "Halaman {page} / {total}",

    // Toast
    UiTextKey.DisableText to "Log masuk untuk menterjemah dan menyimpan sejarah",

    // Error
    UiTextKey.ErrorRetryButton to "Cuba semula",

    // Shop
    UiTextKey.ShopTitle to "Kedai",
    UiTextKey.ShopCoinBalance to "Syiling saya",
    UiTextKey.ShopHistoryExpansionTitle to "Kembangkan sejarah",
    UiTextKey.ShopHistoryExpansionDesc to "Tambah had sejarah untuk melihat lebih banyak terjemahan",
    UiTextKey.ShopCurrentLimit to "Had semasa: {limit} rekod",
    UiTextKey.ShopMaxLimit to "Had maksimum:",
    UiTextKey.ShopBuyHistoryExpansion to "Beli (+{increment} rekod, {cost} syiling)",
    UiTextKey.ShopInsufficientCoins to "Syiling tidak mencukupi",
    UiTextKey.ShopMaxLimitReached to "Had maksimum dicapai",
    UiTextKey.ShopHistoryExpandedTitle to "Berjaya dikembangkan!",
    UiTextKey.ShopHistoryExpandedMessage to "Sejarah anda kini {limit} rekod! Anda boleh melihat lebih banyak terjemahan!",
    UiTextKey.ShopColorPaletteTitle to "Tema warna",
    UiTextKey.ShopColorPaletteDesc to "Pilih tema warna. 10 syiling setiap tema",
    UiTextKey.ShopEntry to "Kedai",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Tetapan suara",
    UiTextKey.VoiceSettingsDesc to "Pilih suara untuk setiap bahasa",

    // Instructions
    UiTextKey.SpeechInstructions to "Tekan mikrofon untuk pengecaman suara. Jika auto-detect tidak dikemas kini selepas bertukar, tekan refresh di kanan atas",
    UiTextKey.HomeInstructions to "Pilih ciri untuk bermula",
    UiTextKey.ContinuousInstructions to "Pilih dua bahasa dan mulakan perbualan",

    // Home
    UiTextKey.HomeTitle to "Terjemahan Segera",
    UiTextKey.HelpTitle to "Bantuan",
    UiTextKey.SpeechTitle to "Terjemahan Pantas",
    UiTextKey.HomeStartButton to "Mula menterjemah",

    // Help
    UiTextKey.HelpCurrentTitle to "Ciri-ciri semasa",
    UiTextKey.HelpCautionTitle to "Perhatian",
    UiTextKey.HelpCurrentFeatures to "Ciri-ciri semasa:\n" +
            "  • Terjemahan Pantas: pengecaman suara dan terjemahan\n" +
            "  • Perbualan Berterusan: terjemahan suara dua hala\n" +
            "  • Sejarah: lihat terjemahan lalu\n" +
            "  • Bahan Pembelajaran: cipta perbendaharaan kata dan kuiz\n\n" +
            "Terjemahan:\n" +
            "  • Pengecaman suara Azure AI\n" +
            "  • Perkhidmatan Terjemahan Azure\n",
    UiTextKey.HelpCaution to "Perhatian:\n" +
            "  • Pengecaman suara memerlukan internet\n" +
            "  • Terjemahan yang dicache boleh digunakan luar talian\n" +
            "  • Sahkan terjemahan penting dengan pakar\n\n" +
            "Akaun dan data:\n" +
            "  • Log masuk diperlukan untuk sejarah, pembelajaran, dan syiling\n" +
            "  • Disimpan dengan selamat di Firebase Firestore\n\n" +
            "Penyelesaian masalah:\n" +
            "  • Jika tidak berfungsi, mulakan semula aplikasi\n",
    UiTextKey.HelpNotesTitle to "Petua",
    UiTextKey.HelpNotes to "💡 Petua penggunaan:\n\n" +
            "Untuk terjemahan lebih baik:\n" +
            "  • Bercakap dengan jelas pada kelajuan biasa\n" +
            "  • Kurangkan bunyi bising untuk pengecaman lebih baik\n" +
            "  • Terjemahan pantas untuk ayat pendek\n\n" +
            "Bahasa aplikasi:\n" +
            "  • Lalai: Inggeris, lain melalui AI\n" +
            "  • Terjemahan manual untuk Kantonis\n" +
            "Kemas kini dan maklum balas:\n" +
            "  • Versi aplikasi di Settings → About\n" +
            "  • Beri maklum balas di Settings → Feedback\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Maklum balas",
    UiTextKey.FeedbackDesc to "Terima kasih! Kongsi cadangan, pepijat, atau penilaian anda",
    UiTextKey.FeedbackMessagePlaceholder to "Taip maklum balas...",
    UiTextKey.FeedbackSubmitButton to "Hantar",
    UiTextKey.FeedbackSubmitting to "Menghantar...",
    UiTextKey.FeedbackSuccessTitle to "Terima kasih!",
    UiTextKey.FeedbackSuccessMessage to "Maklum balas berjaya dihantar. Terima kasih!",
    UiTextKey.FeedbackErrorTitle to "Gagal menghantar",
    UiTextKey.FeedbackErrorMessage to "Gagal menghantar. Semak sambungan dan cuba semula",
    UiTextKey.FeedbackMessageRequired to "Mesej maklum balas diperlukan",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Perbualan Berterusan",
    UiTextKey.ContinuousStartButton to "Mulakan perbualan",
    UiTextKey.ContinuousStopButton to "Hentikan rakaman",
    UiTextKey.ContinuousStartScreenButton to "Perbualan Berterusan",
    UiTextKey.ContinuousPersonALabel to "Penutur A",
    UiTextKey.ContinuousPersonBLabel to "Penutur B",
    UiTextKey.ContinuousCurrentStringLabel to "Teks semasa:",
    UiTextKey.ContinuousSpeakerAName to "Orang A",
    UiTextKey.ContinuousSpeakerBName to "Orang B",
    UiTextKey.ContinuousTranslationSuffix to " · Terjemahan",
    UiTextKey.ContinuousPreparingMicText to "Menyediakan mikrofon...(jangan bercakap dulu)",
    UiTextKey.ContinuousTranslatingText to "Menterjemah...",

    // History
    UiTextKey.HistoryTitle to "Sejarah",
    UiTextKey.HistoryTabDiscrete to "Terjemahan Pantas",
    UiTextKey.HistoryTabContinuous to "Perbualan Berterusan",
    UiTextKey.HistoryNoContinuousSessions to "Tiada sesi perbualan lagi",
    UiTextKey.HistoryNoDiscreteRecords to "Tiada terjemahan lagi",
    UiTextKey.DialogDeleteRecordTitle to "Padam rekod?",
    UiTextKey.DialogDeleteRecordMessage to "Tindakan ini tidak boleh dibatalkan",
    UiTextKey.DialogDeleteSessionTitle to "Padam sesi?",
    UiTextKey.DialogDeleteSessionMessage to "Semua rekod dalam sesi ini akan dipadam. Tidak boleh dibatalkan",
    UiTextKey.HistoryDeleteSessionButton to "Padam",
    UiTextKey.HistoryNameSessionTitle to "Namakan",
    UiTextKey.HistorySessionNameLabel to "Nama sesi",
    UiTextKey.HistorySessionTitleTemplate to "Sesi {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} rekod",

    // Filter
    UiTextKey.FilterDropdownDefault to "Semua bahasa",
    UiTextKey.FilterTitle to "Tapis sejarah",
    UiTextKey.FilterLangDrop to "Bahasa",
    UiTextKey.FilterKeyword to "Kata kunci",
    UiTextKey.FilterApply to "Guna",
    UiTextKey.FilterCancel to "Batal",
    UiTextKey.FilterClear to "Kosongkan tapis",
    UiTextKey.FilterHistoryScreenTitle to "Tapis",

    // Auth
    UiTextKey.AuthLoginTitle to "Log masuk",
    UiTextKey.AuthRegisterTitle to "Daftar (Ditangguhkan)",
    UiTextKey.AuthLoginHint to "Guna emel dan kata laluan berdaftar",
    UiTextKey.AuthRegisterRules to "Pendaftaran ditangguhkan semasa pembangunan\nPeringatan: Kata laluan tidak boleh dipulihkan jika emel salah\n" +
            "Peraturan pendaftaran:\n" +
            "• Format emel yang sah (cth. name@example.com)\n" +
            "• Kata laluan sekurang-kurangnya 8 aksara\n" +
            "• Pengesahan kata laluan mesti sepadan",
    UiTextKey.AuthEmailLabel to "Emel",
    UiTextKey.AuthPasswordLabel to "Kata laluan",
    UiTextKey.AuthConfirmPasswordLabel to "Sahkan kata laluan",
        UiTextKey.AuthLoginButton to "Log masuk",
        UiTextKey.AuthGoogleSignInButton to "Log masuk dengan Google",
    UiTextKey.AuthRegisterButton to "Daftar",
    UiTextKey.AuthToggleToRegister to "Tiada akaun? Daftar (Ditangguhkan)",
    UiTextKey.AuthToggleToLogin to "Sudah ada akaun? Log masuk",
    UiTextKey.AuthErrorPasswordsMismatch to "Kata laluan tidak sepadan",
    UiTextKey.AuthErrorPasswordTooShort to "Kata laluan sekurang-kurangnya 8 aksara",
    UiTextKey.AuthRegistrationDisabled to "Pendaftaran ditangguhkan semasa pembangunan",
    UiTextKey.AuthResetEmailSent to "Emel tetapan semula dihantar (jika akaun wujud). Semak peti masuk",

    // Password reset
    UiTextKey.ForgotPwText to "Lupa kata laluan?",
    UiTextKey.ResetPwTitle to "Tetapkan semula kata laluan",
    UiTextKey.ResetPwText to "Masukkan emel akaun. Kami akan hantar pautan tetapan semula\nPastikan emel telah didaftarkan\n",
    UiTextKey.ResetSendingText to "Menghantar...",
    UiTextKey.ResetSendText to "Hantar emel tetapan semula",

    // Settings
    UiTextKey.SettingsTitle to "Tetapan",
    UiTextKey.SettingsPrimaryLanguageTitle to "Bahasa utama",
    UiTextKey.SettingsPrimaryLanguageDesc to "Digunakan untuk penerangan dan panduan pembelajaran",
    UiTextKey.SettingsPrimaryLanguageLabel to "Bahasa utama",
    UiTextKey.SettingsFontSizeTitle to "Saiz fon",
    UiTextKey.SettingsFontSizeDesc to "Laras saiz fon (diselaraskan merentas peranti)",
    UiTextKey.SettingsScaleTemplate to "Skala: {pct}%",
    UiTextKey.SettingsColorCostTemplate to "{cost} syiling",
    UiTextKey.SettingsColorSelectButton to "Pilih",
    UiTextKey.SettingsColorAlreadyUnlocked to "Telah dibuka kunci",
    UiTextKey.SettingsPreviewHeadline to "Tajuk utama: Teks besar",
    UiTextKey.SettingsPreviewBody to "Badan: Teks biasa",
    UiTextKey.SettingsPreviewLabel to "Label: Teks kecil",
    UiTextKey.SettingsAboutTitle to "Perihal",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Log masuk. Tetapan disimpan dan diselaraskan secara auto",
    UiTextKey.SettingsThemeTitle to "Tema",
    UiTextKey.SettingsThemeDesc to "Pilih tema: Sistem, Cerah, Gelap, atau Berjadual",
    UiTextKey.SettingsThemeSystem to "Sistem",
    UiTextKey.SettingsThemeLight to "Cerah",
    UiTextKey.SettingsThemeDark to "Gelap",
    UiTextKey.SettingsThemeScheduled to "Berjadual",
    UiTextKey.SettingsResetPW to "Tetapkan semula kata laluan",
    UiTextKey.SettingsQuickLinks to "Tetapan lain",
    UiTextKey.SettingsNotLoggedInWarning to "Log masuk untuk melihat tetapan akaun. Bahasa aplikasi boleh ditukar tanpa log masuk",
    UiTextKey.SettingsVoiceLanguageLabel to "Bahasa",
    UiTextKey.SettingsVoiceSelectLabel to "Suara",
    UiTextKey.SettingsVoiceDefault to "Lalai",

    // Learning
    UiTextKey.LearningTitle to "Pembelajaran",
    UiTextKey.LearningHintCount to "(*) Kiraan = terjemahan yang melibatkan bahasa ini",
    UiTextKey.LearningErrorTemplate to "Ralat: %s",
    UiTextKey.LearningGenerate to "Jana",
    UiTextKey.LearningRegenerate to "Jana semula",
    UiTextKey.LearningGenerating to "Menjana...",
    UiTextKey.LearningOpenSheetTemplate to "Bahan {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Bahan pembelajaran {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Bahasa utama: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Kiraan semasa: {nowCount} (semasa jana: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Tiada kandungan",
    UiTextKey.LearningSheetRegenerate to "Jana semula",
    UiTextKey.LearningSheetGenerating to "Menjana...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Apa ini?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Bahan pembelajaran dijana dari sejarah terjemahan. Termasuk perbendaharaan kata, definisi, contoh, dan nota tatabahasa. Uji pengetahuan anda dengan kuiz!",
    UiTextKey.LearningRegenBlockedTitle to "Tidak boleh jana semula",
    UiTextKey.LearningRegenBlockedMessage to "Memerlukan sekurang-kurangnya 5 rekod tambahan untuk jana semula. Masih perlukan {needed}",
    UiTextKey.LearningRegenInfoTitle to "Peraturan jana semula",
    UiTextKey.LearningRegenInfoMessage to "Jana semula bahan:\n\n• Kali pertama: bila-bila masa\n• Jana semula: perlu 5 rekod lagi\n\nButang akan menjadi hijau apabila sedia. Jika kelabu, buat lebih banyak terjemahan!\n\n💡 Petua: Jika kiraan tidak dikemas kini, mulakan semula aplikasi",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Kuiz: {language}",
    UiTextKey.QuizOpenButton to "📝 Kuiz",
    UiTextKey.QuizGenerateButton to "🔄 Jana kuiz",
    UiTextKey.QuizGenerating to "⏳ Menjana...",
    UiTextKey.QuizBlocked to "🚫 Disekat",
    UiTextKey.QuizWait to "⏳ Tunggu...",
    UiTextKey.QuizCanEarnCoins to "🪙 Boleh dapat syiling!",
    UiTextKey.QuizCancelButton to "Batal",
    UiTextKey.QuizPreviousButton to "Sebelum",
    UiTextKey.QuizNextButton to "Seterus",
    UiTextKey.QuizSubmitButton to "Hantar",
    UiTextKey.QuizRetakeButton to "Ambil semula",
    UiTextKey.QuizBackButton to "Kembali",
    UiTextKey.QuizLoadingText to "Memuatkan kuiz...",
    UiTextKey.QuizGeneratingText to "Menjana kuiz...",
    UiTextKey.QuizNoMaterialsTitle to "Tiada bahan ditemui",
    UiTextKey.QuizNoMaterialsMessage to "Jana bahan pembelajaran terlebih dahulu sebelum mencuba kuiz",
    UiTextKey.QuizErrorTitle to "⚠️ Ralat kuiz",
    UiTextKey.QuizErrorSuggestion to "Cadangan: Guna butang di atas untuk jana kuiz",
    UiTextKey.QuizCompletedTitle to "Kuiz selesai!",
    UiTextKey.QuizAnswerReviewTitle to "Semak jawapan",
    UiTextKey.QuizYourAnswerTemplate to "Jawapan anda: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Jawapan betul: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Soalan {current} / {total}",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Penjanaan lain sedang berjalan. Tunggu",
    UiTextKey.QuizCoinRulesTitle to "🪙 Peraturan syiling",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Cara mendapat:",
    UiTextKey.QuizCoinRulesRequirements to "Keperluan:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Status semasa:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Anda boleh mendapat syiling pada kuiz seterusnya!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• Perlukan {count} lagi untuk syiling",
    UiTextKey.QuizCoinRule1Coin to "• 1 syiling setiap jawapan betul",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Percubaan pertama sahaja yang dikira",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Kuiz mesti sepadan dengan bahan",
    UiTextKey.QuizCoinRulePlus10 to "• Sekurang-kurangnya 10 rekod tambahan sejak ganjaran terakhir",
    UiTextKey.QuizCoinRuleNoDelete to "• Syiling tidak dipulangkan jika rekod dipadam",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Bahan: {count} rekod",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Kuiz: {count} rekod",
    UiTextKey.QuizCoinRuleGotIt to "Faham!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Jana semula kuiz?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Boleh dapat syiling pada kuiz ini! (percubaan pertama sahaja)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Tidak boleh dapat syiling pada kuiz ini",
    UiTextKey.QuizRegenNeedMoreTemplate to "Perlukan {count} lagi terjemahan untuk layak (10 lagi sejak ganjaran terakhir)",
    UiTextKey.QuizRegenReminder to "Petua: Boleh berlatih dan ambil semula, tapi syiling hanya pada percubaan pertama dengan rekod mencukupi",
    UiTextKey.QuizRegenGenerateButton to "Jana",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Syiling diperolehi!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Tahniah! Anda telah mendapat {Coins} syiling!",
    UiTextKey.QuizCoinsRule1 to "• 1 syiling setiap jawapan betul pada percubaan pertama",
    UiTextKey.QuizCoinsRule2 to "• Tiada syiling pada percubaan seterusnya",
    UiTextKey.QuizCoinsRule3 to "• Kuiz baru memerlukan 10 rekod tambahan",
    UiTextKey.QuizCoinsRule4 to "• Kuiz mesti sepadan dengan bahan",
    UiTextKey.QuizCoinsRule5 to "• Syiling adalah sebahagian daripada sejarah",
    UiTextKey.QuizCoinsGreatButton to "Bagus!",
    UiTextKey.QuizRecordsLabel to "Rekod",

    // History coins

    // History info
    UiTextKey.HistoryInfoTitle to "Maklumat sejarah",
    UiTextKey.HistoryInfoLimitMessage to "Sejarah menunjukkan {limit} rekod terkini. Kembangkan di kedai!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Rekod lama disimpan tetapi disembunyikan untuk prestasi",
    UiTextKey.HistoryInfoFavoritesMessage to "Jika anda mahu simpan terjemahan, tekan ❤️ untuk simpan ke kegemaran",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Lihat yang disimpan di Settings → Kegemaran",
    UiTextKey.HistoryInfoFilterMessage to "Guna tapis untuk mencari dalam {limit} rekod yang dipaparkan",
    UiTextKey.HistoryInfoGotItButton to "Faham",

    // Word bank
    UiTextKey.WordBankTitle to "Bank perkataan",
    UiTextKey.WordBankSelectLanguage to "Pilih bahasa untuk melihat atau jana bank perkataan:",
    UiTextKey.WordBankNoHistory to "Tiada sejarah terjemahan lagi",
    UiTextKey.WordBankNoHistoryHint to "Mulakan menterjemah untuk membina bank perkataan!",
    UiTextKey.WordBankWordsCount to "Perkataan",
    UiTextKey.WordBankGenerating to "Menjana...",
    UiTextKey.WordBankGenerate to "Jana bank perkataan",
    UiTextKey.WordBankRefresh to "🔄 Muat semula bank",
    UiTextKey.WordBankEmpty to "Bank perkataan kosong",
    UiTextKey.WordBankEmptyHint to "Tekan di atas untuk jana dari sejarah",
    UiTextKey.WordBankExample to "Contoh:",
    UiTextKey.WordBankDifficulty to "Tahap:",
    UiTextKey.WordBankFilterCategory to "Kategori",
    UiTextKey.WordBankFilterCategoryAll to "Semua kategori",
    UiTextKey.WordBankFilterDifficultyLabel to "Tahap:",
    UiTextKey.WordBankFilterNoResults to "Tiada perkataan sepadan dengan tapis",
    UiTextKey.WordBankRegenInfoTitle to "Peraturan kemas kini",
    UiTextKey.WordBankRegenInfoMessage to "Kemas kini bank perkataan:\n\n• Kali pertama: bila-bila masa\n• Kemas kini: perlu 20 rekod tambahan\n\nButang akan menjadi hijau apabila sedia. Jika kelabu, buat lebih banyak terjemahan!\n\n💡 Petua: Jika kiraan tidak dikemas kini, mulakan semula aplikasi",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Log keluar?",
    UiTextKey.DialogLogoutMessage to "Perlu log masuk semula untuk menterjemah dan melihat sejarah",
    UiTextKey.DialogGenerateOverwriteTitle to "Tulis ganti bahan?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Bahan semasa akan ditulis ganti\nJana bahan untuk {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Profil",
    UiTextKey.ProfileUsernameLabel to "Nama pengguna",
    UiTextKey.ProfileUsernameHint to "Masukkan nama pengguna",
    UiTextKey.ProfileUpdateButton to "Kemas kini profil",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Padam akaun",
    UiTextKey.AccountDeleteWarning to "⚠️ Kekal dan tidak boleh dibatalkan!",
    UiTextKey.AccountDeleteConfirmMessage to "Semua data akan dipadam secara kekal: sejarah, bank perkataan, bahan, tetapan. Masukkan kata laluan untuk mengesahkan",
    UiTextKey.AccountDeletePasswordLabel to "Kata laluan",
        UiTextKey.AccountDeleteButton to "Padam akaun dan data saya",

    // Favorites
    UiTextKey.FavoritesTitle to "Kegemaran",
    UiTextKey.FavoritesEmpty to "Tiada kegemaran lagi",
    UiTextKey.FavoritesTabRecords to "Rekod",
    UiTextKey.FavoritesTabSessions to "Sesi",
    UiTextKey.FavoritesSessionsEmpty to "Tiada sesi kegemaran",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} mesej",

    // Custom words
    UiTextKey.CustomWordsTitle to "Perkataan tersuai",
    UiTextKey.CustomWordsAdd to "Tambah perkataan",
    UiTextKey.CustomWordsEdit to "Sunting perkataan",
    UiTextKey.CustomWordsDelete to "Padam perkataan",
    UiTextKey.CustomWordsOriginalLabel to "Asal",
    UiTextKey.CustomWordsTranslatedLabel to "Terjemahan",
    UiTextKey.CustomWordsPronunciationLabel to "Sebutan (pilihan)",
    UiTextKey.CustomWordsExampleLabel to "Contoh (pilihan)",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Bahasa asal",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Bahasa terjemahan",
    UiTextKey.CustomWordsSaveButton to "Simpan",
    UiTextKey.CustomWordsCancelButton to "Batal",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Auto-kesan",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Imbas teks dari gambar",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Amaran: Pengecaman teks mungkin tidak tepat sepenuhnya. Semak semula teks yang dikesan. " +
            "Menyokong Latin (Inggeris, dll.), Cina, Jepun, dan Korea",

    // Cache

    // Auto theme
    UiTextKey.SettingsAutoThemePreview to "Tema akan bertukar secara automatik mengikut masa yang ditetapkan",

    // Offline mode
    UiTextKey.OfflineSyncPending to "Perubahan akan diselaraskan apabila dalam talian",

    // Image capture
    UiTextKey.ImageSourceTitle to "Pilih sumber gambar",
    UiTextKey.ImageSourceCamera to "Ambil gambar",
    UiTextKey.ImageSourceGallery to "Pilih dari galeri",
    UiTextKey.ImageSourceCancel to "Batal",
    UiTextKey.CameraCaptureContentDesc to "Ambil gambar",

    // Friends
    UiTextKey.FriendsTitle to "Kawan",
    UiTextKey.FriendsAddButton to "Tambah kawan",
    UiTextKey.FriendsSearchTitle to "Cari pengguna",
    UiTextKey.FriendsSearchPlaceholder to "Nama pengguna atau ID...",
    UiTextKey.FriendsSearchNoResults to "Tiada pengguna ditemui",
    UiTextKey.FriendsRequestsSection to "Permintaan ({count})",
    UiTextKey.FriendsSectionTitle to "Kawan ({count})",
    UiTextKey.FriendsAcceptButton to "Terima",
    UiTextKey.FriendsRejectButton to "Tolak",
    UiTextKey.FriendsSendRequestButton to "Tambah",
    UiTextKey.FriendsCloseButton to "Tutup",
    UiTextKey.FriendsCancelButton to "Batal",
    UiTextKey.FriendsRemoveConfirm to "Keluarkan",
    UiTextKey.FriendsNewRequestsTemplate to "{count} permintaan baru!",
    UiTextKey.FriendsSentRequestsSection to "Dihantar ({count})",
    UiTextKey.FriendsPendingStatus to "Menunggu",
    UiTextKey.FriendsCancelRequestButton to "Batal permintaan",
    UiTextKey.FriendsUnreadMessageDesc to "Hantar mesej",
    UiTextKey.FriendsDeleteModeButton to "Padam kawan",
    UiTextKey.FriendsDeleteMultipleTitle to "Padam kawan",
    UiTextKey.FriendsDeleteMultipleMessage to "Padam {count} kawan terpilih?",
    UiTextKey.FriendsSearchMinChars3 to "Masukkan sekurang-kurangnya 3 aksara untuk nama",
    UiTextKey.FriendsSearchByUserIdHint to "Atau cari tepat menggunakan User ID",
    UiTextKey.FriendsStatusAlreadyFriends to "Sudah berkawan",
    UiTextKey.FriendsStatusRequestSent to "Dihantar — menunggu respons",
    UiTextKey.FriendsStatusRequestReceived to "Pengguna ini telah menghantar permintaan kepada anda",

    // Chat
    UiTextKey.ChatTitle to "Berbual dengan {username}",
    UiTextKey.ChatInputPlaceholder to "Taip mesej...",
    UiTextKey.ChatSendButton to "Hantar",
    UiTextKey.ChatEmpty to "Tiada mesej lagi. Mulakan perbualan!",
    UiTextKey.ChatUnreadBadge to "{count} belum dibaca",
    UiTextKey.ChatTranslateButton to "Terjemah",
    UiTextKey.ChatTranslateDialogTitle to "Terjemah sembang",
    UiTextKey.ChatTranslateDialogMessage to "Terjemah mesej kawan ke bahasa anda? Setiap mesej akan dikesan dan diterjemah secara automatik",
    UiTextKey.ChatTranslateConfirm to "Terjemah semua",
    UiTextKey.ChatShowOriginal to "Tunjuk asal",
    UiTextKey.ChatShowTranslation to "Tunjuk terjemahan",
    UiTextKey.ChatTranslatedLabel to "Diterjemah",

    // Sharing
    UiTextKey.ShareInboxTitle to "Peti masuk perkongsian",
    UiTextKey.ShareInboxEmpty to "Tiada perkongsian lagi. Kawan boleh berkongsi perkataan dan bahan!",
    UiTextKey.ShareWordButton to "Kongsi perkataan",
    UiTextKey.ShareMaterialButton to "Kongsi bahan",
    UiTextKey.ShareSelectFriendTitle to "Pilih kawan",
    UiTextKey.ShareSelectFriendMessage to "Pilih kawan untuk berkongsi:",
    UiTextKey.ShareSuccess to "Berjaya dikongsi!",
    UiTextKey.ShareAcceptButton to "Terima",
    UiTextKey.ShareDismissButton to "Tolak",
    UiTextKey.ShareTypeWord to "Perkataan",
    UiTextKey.ShareTypeLearningSheet to "Bahan pembelajaran",
    UiTextKey.ShareReceivedFrom to "Dari: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} item baru!",
    UiTextKey.ShareViewFullMaterial to "Tekan \"Lihat\" untuk melihat bahan penuh",
    UiTextKey.ShareDeleteItemTitle to "Padam item",
    UiTextKey.ShareDeleteItemMessage to "Padam item berkongsi ini? Tidak boleh dibatalkan",
    UiTextKey.ShareDeleteButton to "Padam",
    UiTextKey.ShareViewButton to "Lihat",
    UiTextKey.ShareItemNotFound to "Item tidak ditemui",
    UiTextKey.ShareNoContent to "Tiada kandungan dalam bahan",
    UiTextKey.ShareSaveToSelf to "Simpan ke peti masuk sendiri",
    UiTextKey.ShareSavedToSelf to "Disimpan ke peti masuk anda!",

    // My profile
    UiTextKey.MyProfileTitle to "Profil saya",
    UiTextKey.MyProfileUserId to "User ID",
    UiTextKey.MyProfileUsername to "Nama pengguna",
    UiTextKey.MyProfileCopyUserId to "Salin ID",
    UiTextKey.MyProfileCopyUsername to "Salin nama pengguna",
    UiTextKey.MyProfileShare to "Kongsi profil",
    UiTextKey.MyProfileCopied to "Disalin!",
    UiTextKey.MyProfilePrimaryLanguage to "Bahasa utama",
    UiTextKey.MyProfileLearningLanguages to "Bahasa yang dipelajari",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Halaman kawan",
    UiTextKey.FriendsInfoMessage to "• Tarik untuk muat semula senarai, permintaan, dan status\n" +
            "• Tekan kad untuk buka sembang\n" +
            "• Titik merah (●) untuk mesej belum dibaca; ✓✓ untuk semua dibaca\n" +
            "• 📥 untuk peti masuk perkongsian; ✓✓ untuk kosongkan lencana\n" +
            "• 🚫 untuk sekat — keluarkan kawan dan sekat interaksi\n" +
            "• Menyekat juga memadamkan sejarah sembang\n" +
            "• Ikon tong sampah untuk mod padam\n" +
            "• Mengeluarkan kawan memadamkan semua mesej\n" +
            "• Ikon cari untuk mencari melalui nama atau ID\n" +
            "• Pemberitahuan dimatikan secara lalai — aktifkan di Settings\n",
    UiTextKey.FriendsEmptyTitle to "Tiada kawan lagi",
    UiTextKey.FriendsEmptyMessage to "Tekan \"Tambah kawan\" untuk mencari melalui nama atau ID\n",
    UiTextKey.FriendsInfoGotItButton to "Faham",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Halaman pembelajaran",
    UiTextKey.LearningInfoMessage to "• Tarik untuk muat semula senarai\n" +
            "• Setiap kad menunjukkan bahasa dan kiraan\n" +
            "• \"Jana\" untuk bahan (percuma kali pertama)\n" +
            "• Jana semula perlu 5 rekod tambahan\n" +
            "• Butang bahan untuk buka yang dijana\n" +
            "• Selepas jana bahan, boleh buat kuiz",
    UiTextKey.LearningEmptyMessage to "Mulakan menterjemah untuk membina rekod\n" +
            "Bahan dijana dari sejarah\n" +
            "Selepas menterjemah, tarik untuk muat semula",
    UiTextKey.LearningInfoGotItButton to "Faham",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Halaman bank perkataan",
    UiTextKey.WordBankInfoMessage to "• Tarik untuk muat semula senarai bahasa\n" +
            "• Pilih bahasa untuk melihat atau jana\n" +
            "• Bank perkataan dijana dari sejarah\n" +
            "• Kemas kini perlu 20 rekod tambahan\n" +
            "• Tambah perkataan tersuai secara manual\n" +
            "• Kongsi perkataan dengan kawan",
    UiTextKey.WordBankInfoGotItButton to "Faham",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Peti masuk perkongsian",
    UiTextKey.ShareInboxInfoMessage to "• Tarik untuk muat semula peti masuk\n" +
            "• Perkongsian kawan akan muncul di sini\n" +
            "• Terima atau tolak perkataan\n" +
            "• \"Lihat\" untuk bahan dan kuiz\n" +
            "• Titik merah (●) untuk item baru/belum dibaca\n" +
            "• Pengesahan sebelum menolak perkataan berkongsi",
    UiTextKey.ShareInboxInfoGotItButton to "Faham",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Keterlihatan profil",
    UiTextKey.MyProfileVisibilityPublic to "Awam",
    UiTextKey.MyProfileVisibilityPrivate to "Peribadi",
    UiTextKey.MyProfileVisibilityDescription to "Awam: Sesiapa boleh mencari dan menambah anda\nPeribadi: Tidak muncul dalam carian",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Tolak perkataan",
    UiTextKey.ShareDismissWordMessage to "Tolak perkataan berkongsi? Tidak boleh dibatalkan",

    // Shared inbox sheet label

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Tolak",
    UiTextKey.AccessibilityNewMessages to "Mesej baru",
    UiTextKey.AccessibilityNewReleasesIcon to "Penunjuk item baru",
    UiTextKey.AccessibilitySuccessIcon to "Berjaya",
    UiTextKey.AccessibilityErrorIcon to "Ralat",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Jenis item berkongsi",
    UiTextKey.AccessibilityWordBankExists to "Bank perkataan wujud",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "MS.Maklum Balas",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Tetapan pemberitahuan",
    UiTextKey.FriendsNotifSettingsTitle to "Tetapan pemberitahuan",
    UiTextKey.FriendsNotifNewMessages to "Mesej sembang baru",
    UiTextKey.FriendsNotifFriendRequests to "Permintaan diterima",
    UiTextKey.FriendsNotifRequestAccepted to "Permintaan diluluskan",
    UiTextKey.FriendsNotifSharedInbox to "Item berkongsi baru",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Lencana dalam aplikasi (titik merah)",
    UiTextKey.InAppBadgeMessages to "Lencana mesej belum dibaca",
    UiTextKey.InAppBadgeFriendRequests to "Lencana permintaan kawan",
    UiTextKey.InAppBadgeSharedInbox to "Lencana peti masuk belum dibaca",

    // Error messages

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Perlukan {needed} lagi terjemahan untuk jana bahan",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Tukar ke perbualan berterusan →",

    // Chat clear
    UiTextKey.ChatClearConversationTitle to "Kosongkan sembang",
    UiTextKey.ChatClearConversationMessage to "Sembunyikan semua mesej? Kekal tersembunyi apabila dibuka semula. Tidak menjejaskan orang lain",
    UiTextKey.ChatClearConversationConfirm to "Kosongkan semua",
    UiTextKey.ChatClearConversationSuccess to "Sembang dikosongkan",

    // Block user
    UiTextKey.BlockUserButton to "Sekat",
    UiTextKey.BlockUserTitle to "Sekat pengguna ini?",
    UiTextKey.BlockUserMessage to "Sekat {username}? Akan dikeluarkan daripada senarai dan disekat daripada berinteraksi",
    UiTextKey.BlockUserConfirm to "Sekat",
    UiTextKey.BlockedUsersTitle to "Pengguna disekat",
    UiTextKey.BlockedUsersEmpty to "Tiada pengguna disekat",
    UiTextKey.UnblockUserButton to "Nyahsekat",
    UiTextKey.UnblockUserTitle to "Nyahsekat?",
    UiTextKey.UnblockUserMessage to "Nyahsekat {username}? Mereka boleh menghantar permintaan semula",
    UiTextKey.BlockedUsersManageButton to "Urus pengguna disekat",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Nota permintaan (pilihan)",
    UiTextKey.FriendsRequestNotePlaceholder to "Tambah nota ringkas...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Bahan sedia! Tekan untuk buka",
    UiTextKey.GenerationBannerWordBank to "Bank perkataan sedia! Tekan untuk lihat",
    UiTextKey.GenerationBannerQuiz to "Kuiz sedia! Tekan untuk mula",

    // Notification settings quick link

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Cina (Tradisional)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Sistem kawan",
    UiTextKey.HelpFriendSystemBody to "• Cari kawan melalui nama atau ID\n" +
            "• Hantar, terima, atau tolak permintaan\n" +
            "• Sembang terus dengan terjemahan\n" +
            "• Kongsi perkataan dan bahan pembelajaran\n" +
            "• Urus kandungan berkongsi di peti masuk\n" +
            "• Titik merah (●) untuk kandungan baru/belum dibaca\n" +
            "• Tarik ke bawah untuk muat semula",
    UiTextKey.HelpProfileVisibilityTitle to "Keterlihatan profil",
    UiTextKey.HelpProfileVisibilityBody to "• Tetapkan profil sebagai awam atau peribadi di Settings\n" +
            "• Awam: Sesiapa boleh mencari anda\n" +
            "• Peribadi: Tidak muncul dalam carian\n" +
            "• Guna peribadi: kongsi ID untuk ditambah",
    UiTextKey.HelpColorPalettesTitle to "Tema dan syiling",
    UiTextKey.HelpColorPalettesBody to "• 1 tema percuma: Sky Blue (lalai)\n" +
            "• 10 tema boleh dibuka kunci pada 10 syiling setiap satu\n" +
            "• Dapatkan syiling dari kuiz\n" +
            "• Guna syiling untuk tema dan pengembangan sejarah\n" +
            "• Tema automatik: cerah 6-18, gelap 18-6",
    UiTextKey.HelpPrivacyTitle to "Privasi dan data",
    UiTextKey.HelpPrivacyBody to "• Suara hanya digunakan untuk pengecaman, tidak disimpan secara kekal\n" +
            "• OCR diproses pada peranti (selamat)\n" +
            "• Boleh padam akaun dan data bila-bila masa\n" +
            "• Mod peribadi: tidak muncul dalam carian\n" +
            "• Semua data diselaraskan dengan selamat di Firebase",
    UiTextKey.HelpAppVersionTitle to "Versi aplikasi",
    UiTextKey.HelpAppVersionNotes to "• Had sejarah: 30 hingga 60 rekod (kembangkan dengan syiling)\n" +
            "• Nama pengguna unik — tukar nama untuk membebaskan yang lama\n" +
            "• Log keluar automatik apabila kemas kini keselamatan\n" +
            "• Semua terjemahan melalui Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Terjemahan Segera",
    UiTextKey.OnboardingPage1Desc to "Terjemahan pantas untuk ayat pendek. Perbualan berterusan untuk perbincangan dua hala",
    UiTextKey.OnboardingPage2Title to "Belajar perbendaharaan kata",
    UiTextKey.OnboardingPage2Desc to "Cipta senarai perkataan dan kuiz daripada sejarah",
    UiTextKey.OnboardingPage3Title to "Berhubung dengan kawan",
    UiTextKey.OnboardingPage3Desc to "Berbual, berkongsi perkataan, dan belajar bersama",
    UiTextKey.OnboardingSkipButton to "Langkau",
    UiTextKey.OnboardingNextButton to "Seterus",
    UiTextKey.OnboardingGetStartedButton to "Mulakan",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Selamat kembali, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Pengguna:",
    UiTextKey.ChatUserIdLabel to "User ID:",
    UiTextKey.ChatLearningLabel to "Mempelajari:",
    UiTextKey.ChatBlockedMessage to "Tidak boleh menghantar mesej kepada pengguna ini",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Cari",
    UiTextKey.CustomWordsEmptyState to "Tiada perkataan tersuai lagi",
    UiTextKey.CustomWordsEmptyHint to "Tekan + untuk tambah perkataan",
    UiTextKey.CustomWordsNoSearchResults to "Tiada perkataan sepadan",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} rekod",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Emel: {email}",
    UiTextKey.ProfileUsernameHintFull to "Nama pengguna untuk kawan (3–20 aksara, huruf/nombor/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Tiada pilihan suara untuk bahasa ini",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "Aplikasi dikemas kini. Sila log masuk semula",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Had kegemaran dicapai",
    UiTextKey.FavoritesLimitMessage to "Maksimum 20 kegemaran. Padam yang lain untuk menambah",
    UiTextKey.FavoritesLimitGotIt to "Faham",
    UiTextKey.FavoritesInfoTitle to "Maklumat kegemaran",
    UiTextKey.FavoritesInfoMessage to "Maksimum 20 kegemaran (rekod dan sesi). Dihadkan untuk beban pangkalan data. Padam untuk menambah baru",
    UiTextKey.FavoritesInfoGotIt to "Faham",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Tidak boleh ditukar",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "Bahasa utama boleh ditukar setiap 30 hari. Masih {days} hari lagi",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "Bahasa utama boleh ditukar setiap 30 hari. Masih {hours} jam lagi",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Sahkan pertukaran bahasa",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Menukar bahasa utama tidak boleh ditukar selama 30 hari. Teruskan?",

    // Bottom navigation
    UiTextKey.NavHome to "Utama",
    UiTextKey.NavTranslate to "Terjemah",
    UiTextKey.NavLearn to "Belajar",
    UiTextKey.NavFriends to "Kawan",
    UiTextKey.NavSettings to "Tetapan",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Kebenaran kamera diperlukan",
    UiTextKey.CameraPermissionMessage to "Benarkan kamera untuk pengecaman teks",
    UiTextKey.CameraPermissionGrant to "Benarkan",
    UiTextKey.MicPermissionMessage to "Kebenaran mikrofon diperlukan untuk pengecaman suara",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Padam {count} item terpilih? Tidak boleh dibatalkan",
    UiTextKey.WordBankDeleteConfirm to "Padam \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Terima semua",
    UiTextKey.FriendsRejectAllButton to "Tolak semua",
    UiTextKey.ChatBlockedCannotSend to "Tidak boleh menghantar mesej",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Buka kunci {name}?",
    UiTextKey.ShopUnlockCost to "Kos: {cost} syiling",
    UiTextKey.ShopYourCoins to "Syiling saya: {coins}",
    UiTextKey.ShopUnlockButton to "Buka kunci",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Bahasa utama",
    UiTextKey.HelpPrimaryLanguageBody to "• Bahasa utama digunakan untuk penerangan dalam pembelajaran\n" +
            "• Boleh ditukar setiap 30 hari untuk konsistensi\n" +
            "• Tukar di Settings\n" +
            "• Tetapan diguna pakai di semua halaman",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Petua: Untuk pengecaman lebih baik, tetapkan \"Bahasa Sumber\" kepada bahasa teks yang diimbas",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Tidak boleh ditukar",
    UiTextKey.SettingsUsernameCooldownMessage to "Nama pengguna boleh ditukar setiap 30 hari. Masih {days} hari lagi",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Nama pengguna boleh ditukar setiap 30 hari. Masih {hours} jam lagi",
    UiTextKey.SettingsUsernameConfirmTitle to "Sahkan pertukaran nama pengguna",
    UiTextKey.SettingsUsernameConfirmMessage to "Menukar nama pengguna tidak boleh ditukar selama 30 hari. Teruskan?",

    // Phase 1 fixes
    UiTextKey.SettingsPasswordManagedByGoogle to "Kata laluan diurus oleh Google",
    UiTextKey.SpeechListeningLabel to "Mendengar… sila bercakap",
    UiTextKey.QuizLeaveTitle to "Keluar dari kuiz?",
    UiTextKey.QuizLeaveMessage to "Jawapan anda akan hilang.",
    UiTextKey.QuizLeaveConfirm to "Keluar",
    UiTextKey.QuizLeaveStay to "Kekal",

    // Phase 1 part 2
    UiTextKey.A11yAddFavorite to "Tambah ke kegemaran",
    UiTextKey.A11yRemoveFavorite to "Buang daripada kegemaran",
    UiTextKey.A11yViewFriendProfile to "Lihat profil rakan",
    UiTextKey.A11yRefreshAutoDetect to "Muat semula pengesanan auto",
    UiTextKey.A11yInstructions to "Arahan",
    UiTextKey.A11yClearText to "Kosongkan teks",
    UiTextKey.A11yAddCustomWords to "Tambah perkataan sendiri",
    UiTextKey.A11yFilter to "Tapis",
    UiTextKey.A11ySpeakOriginal to "Sebut teks asal",
    UiTextKey.A11ySpeakTranslation to "Sebut terjemahan",
    UiTextKey.A11ySpeakExample to "Sebut contoh",
    UiTextKey.A11yCoinRules to "Peraturan syiling",
    UiTextKey.StatusWaiting to "Menunggu…",
    UiTextKey.ErrorNotLoggedIn to "Belum log masuk",
    UiTextKey.HistoryErrorDeleteFailed to "Gagal memadam. Sila cuba lagi.",
    UiTextKey.HistoryErrorRenameFailed to "Gagal menukar nama. Sila cuba lagi.",
    UiTextKey.HistoryErrorDeleteSessionFailed to "Tidak dapat memadam sesi. Sila cuba lagi.",
    UiTextKey.HistoryErrorLoadMoreFailed to "Tidak dapat memuatkan lebih banyak rekod.",
    UiTextKey.HistoryErrorRemoveFavoriteFailed to "Tidak dapat membuang daripada kegemaran.",
    UiTextKey.HistoryErrorAddFavoriteFailed to "Tidak dapat menambah ke kegemaran.",
    UiTextKey.HistoryErrorFavoriteSessionFailed to "Tidak dapat menyimpan sesi ke kegemaran.",
    UiTextKey.HistoryErrorUnfavoriteSessionFailed to "Tidak dapat membuang sesi daripada kegemaran.",
    UiTextKey.TtsSpeaking to "Menyebut…",
    UiTextKey.TtsSpeakingTranslation to "Menyebut terjemahan…",
    UiTextKey.TtsSpoken to "✓ Sudah disebut",
    UiTextKey.TtsError to "✗ {detail}",
    UiTextKey.SpeechStatusRecognitionError to "Ralat pengecaman: {detail}",
    UiTextKey.SpeechStatusAutoDetecting to "Mengesan bahasa secara automatik…",
    UiTextKey.SpeechStatusDetected to "Dikesan: {language}",
    UiTextKey.SpeechStatusDetectedConfidence to "Dikesan: {language} (keyakinan {confidence}%)",
    UiTextKey.SpeechStatusEnterText to "Sila masukkan atau sebut teks untuk diterjemah.",
    UiTextKey.SpeechStatusLoginRequired to "Log masuk diperlukan untuk menggunakan terjemahan.",
    UiTextKey.SpeechStatusDetectFailed to "Bahasa sumber tidak dapat dikesan. Sila pilih secara manual.",
    UiTextKey.SpeechStatusTranslationError to "Ralat terjemahan: {detail}",
    UiTextKey.SpeechStatusScanningImage to "Mengimbas teks dalam imej…",
    UiTextKey.SpeechStatusTextExtracted to "Teks berjaya diekstrak",
    UiTextKey.SpeechStatusOcrError to "Ralat imbasan imej: {detail}",
    UiTextKey.SpeechStatusEnterTextFirst to "Sila masukkan atau sebut teks dahulu.",
    UiTextKey.SpeechStatusDetectFailedForSpeech to "Bahasa sumber untuk pertuturan tidak dapat dikesan. Sila pilih bahasa sumber.",
    UiTextKey.SpeechStatusRefreshed to "Dimuat semula. Pengesanan auto akan berjalan semula pada terjemahan atau sebutan seterusnya.",
    UiTextKey.ContinuousStatusLoginRequired to "Sila log masuk untuk menggunakan terjemahan berterusan.",
    UiTextKey.ContinuousStatusPreparingMic to "Menyediakan mikrofon…",
    UiTextKey.ContinuousStatusTranslationError to "Ralat terjemahan berterusan: {detail}",
    UiTextKey.ContinuousStatusRateLimited to "{detail} Mod berterusan dihentikan untuk mengelakkan kegagalan berulang.",
    UiTextKey.ContinuousStatusRecognitionError to "Ralat pengecaman berterusan: {detail}",
    UiTextKey.ContinuousStatusStartError to "Tidak dapat memulakan mod berterusan: {detail}",

    // Phase 1 part 3: social area messages
    UiTextKey.StatusRaw to "{detail}",
    UiTextKey.ErrGeneric to "Berlaku masalah. Sila cuba lagi.",
    UiTextKey.ErrNoInternet to "Tiada sambungan internet. Sila semak rangkaian anda dan cuba lagi.",
    UiTextKey.ErrPermissionDenied to "Anda tiada kebenaran untuk melakukan tindakan ini.",
    UiTextKey.ErrSessionExpired to "Sesi anda telah tamat. Sila log masuk semula.",
    UiTextKey.ErrItemNotFound to "Item yang diminta tidak ditemui. Ia mungkin telah dipadam.",
    UiTextKey.ErrAccessDenied to "Akses ditolak.",
    UiTextKey.ErrAlreadyFriends to "Anda sudah berkawan dengan pengguna ini.",
    UiTextKey.ErrUserMaybeBlocked to "Tidak dapat melengkapkan tindakan ini. Pengguna mungkin telah disekat.",
    UiTextKey.ErrRequestGone to "Permintaan rakan ini tidak wujud lagi.",
    UiTextKey.ErrRequestHandled to "Permintaan ini telah dikendalikan oleh orang lain.",
    UiTextKey.ErrNotAuthorized to "Anda tidak dibenarkan melakukan tindakan ini.",
    UiTextKey.ErrTooManyRequests to "Terlalu banyak permintaan. Sila tunggu sebentar dan cuba lagi.",
    UiTextKey.ErrInvalidInput to "Input tidak sah. Sila semak dan cuba lagi.",
    UiTextKey.ErrCannotCompleteNow to "Operasi tidak dapat diselesaikan sekarang.",
    UiTextKey.ErrTimedOut to "Operasi tamat masa. Sila cuba lagi.",
    UiTextKey.FriendsMsgRemoved to "Rakan berjaya dibuang.",
    UiTextKey.FriendsMsgRemoveFailed to "Tidak dapat membuang rakan. Sila semak sambungan anda dan cuba lagi.",
    UiTextKey.FriendsMsgRemovedMany to "Rakan dibuang.",
    UiTextKey.FriendsMsgNeedUsername to
        "Sila tetapkan nama pengguna dalam profil anda sebelum mengurus permintaan rakan.",
    UiTextKey.FriendsMsgMaxPending to
        "Anda telah mencapai had {limit} permintaan rakan yang belum selesai. Tunggu sebahagian diterima atau " +
            "batalkan beberapa sebelum menghantar lagi.",
    UiTextKey.FriendsMsgRateLimit to
        "Had melebihi. Anda boleh menghantar sehingga {limit} permintaan rakan sejam. Cuba lagi dalam kira-kira " +
            "{minutes} minit.",
    UiTextKey.FriendsMsgNoteTooLong to "Nota tidak boleh melebihi {max} aksara",
    UiTextKey.FriendsMsgRequestSent to "Permintaan rakan dihantar! Mereka akan dimaklumkan.",
    UiTextKey.FriendsMsgAlreadyFriends to "Anda sudah berkawan dengan pengguna ini.",
    UiTextKey.FriendsMsgRequestPending to
        "Anda sudah ada permintaan yang belum selesai kepada pengguna ini. Sila tunggu balasan mereka.",
    UiTextKey.FriendsMsgProfileMissing to "Profil pengguna tidak ditemui. Mereka mungkin telah memadam akaun.",
    UiTextKey.FriendsMsgYouBlocked to "Anda telah menyekat pengguna ini. Nyahsekat mereka dahulu.",
    UiTextKey.FriendsMsgUnableToSend to "Tidak dapat menghantar permintaan rakan.",
    UiTextKey.FriendsMsgSendFailed to "Gagal menghantar permintaan rakan. Sila cuba lagi.",
    UiTextKey.FriendsMsgAccepted to "Permintaan rakan diterima! Anda kini berkawan.",
    UiTextKey.FriendsMsgAcceptGone to "Permintaan ini tidak wujud lagi — ia mungkin telah dibatalkan.",
    UiTextKey.FriendsMsgAcceptNotAuthorized to "Anda tidak dibenarkan menerima permintaan ini.",
    UiTextKey.FriendsMsgAcceptFailed to "Gagal menerima permintaan. Sila cuba lagi.",
    UiTextKey.FriendsMsgDeclined to "Permintaan ditolak.",
    UiTextKey.FriendsMsgDeclineGone to "Permintaan ini tidak wujud lagi.",
    UiTextKey.FriendsMsgDeclineFailed to "Gagal menolak permintaan. Sila cuba lagi.",
    UiTextKey.FriendsMsgCancelled to "Permintaan rakan dibatalkan.",
    UiTextKey.FriendsMsgCancelGone to "Permintaan ini tidak wujud lagi — ia mungkin telah diterima atau ditolak.",
    UiTextKey.FriendsMsgCancelFailed to "Gagal membatalkan permintaan. Sila cuba lagi.",
    UiTextKey.FriendsMsgBatchAcceptAll to "Semua {count} permintaan rakan diterima!",
    UiTextKey.FriendsMsgBatchAcceptFailed to "Gagal menerima permintaan. Sila cuba lagi.",
    UiTextKey.FriendsMsgBatchAcceptMixed to "{success} diterima, {failed} gagal. Sila cuba semula yang gagal.",
    UiTextKey.FriendsMsgBatchAcceptStoppedAt to "Terima pukal dibatalkan selepas {processed}/{total} permintaan.",
    UiTextKey.FriendsMsgBatchAcceptStopped to "Terima pukal dibatalkan.",
    UiTextKey.FriendsMsgBatchDeclined to "{count} permintaan ditolak.",
    UiTextKey.FriendsMsgBatchDeclineStoppedAt to "Tolak pukal dibatalkan selepas {processed}/{total} permintaan.",
    UiTextKey.FriendsMsgBatchDeclineStopped to "Tolak pukal dibatalkan.",
    UiTextKey.FriendsMsgBlockedRemoved to "Pengguna disekat dan dibuang daripada rakan.",
    UiTextKey.FriendsMsgBlockFailed to "Gagal menyekat pengguna. Sila cuba lagi.",
    UiTextKey.FriendsMsgBlocked to "Pengguna disekat.",
    UiTextKey.FriendsMsgUnblocked to "Pengguna dinyahsekat.",
    UiTextKey.FriendsMsgUnblockFailed to "Gagal menyahsekat pengguna. Sila cuba lagi.",
    UiTextKey.ChatMsgLoadFailed to "Gagal memuatkan mesej. Sila cuba lagi.",
    UiTextKey.ChatMsgBlockFailed to "Gagal menyekat pengguna.",
    UiTextKey.ChatMsgUnblockFailed to "Gagal menyahsekat pengguna.",
    UiTextKey.ChatMsgTooLong to "Mesej tidak boleh melebihi {max} aksara",
    UiTextKey.ChatMsgTooFast to "Anda menghantar mesej terlalu cepat. Sila tunggu sebentar.",
    UiTextKey.ChatMsgSendFailed to "Gagal menghantar mesej",
    UiTextKey.ChatMsgTranslateFailed to "Terjemahan gagal. Sila cuba lagi.",
    UiTextKey.MyProfileMsgLoadFailed to "Gagal memuatkan profil. Sila cuba lagi.",
    UiTextKey.MyProfileMsgNeedUsername to "Tetapkan nama pengguna sebelum menjadikan profil anda awam.",
    UiTextKey.MyProfileMsgPublic to "Profil ditetapkan kepada Awam",
    UiTextKey.MyProfileMsgPrivate to "Profil ditetapkan kepada Peribadi",
    UiTextKey.MyProfileMsgVisibilityFailed to "Gagal mengemas kini keterlihatan. Sila cuba lagi.",
    UiTextKey.SharedMsgAdded to "Ditambah ke koleksi anda",
    UiTextKey.SharedMsgDeleted to "Item dipadam",
    UiTextKey.SharedMsgDismissed to "Item diketepikan",
    UiTextKey.SharedMsgAcceptFailed to "Gagal menerima item",
    UiTextKey.SharedMsgDeleteFailed to "Gagal memadam item",
    UiTextKey.SharedMsgDismissFailed to "Gagal mengetepikan item",
    UiTextKey.SharedMsgNoItemId to "Tiada ID item",
    UiTextKey.SharedMsgLoadFailed to "Gagal memuatkan bahan yang dikongsi. Sila cuba lagi.",

    // Phase 1 part 4: settings, shop, profile, login and feedback messages
    UiTextKey.FeedbackMsgEmpty to "Mesej tidak boleh kosong",
    UiTextKey.FeedbackMsgTooShort to "Maklum balas mesti sekurang-kurangnya {min} aksara",
    UiTextKey.FeedbackMsgTooLong to "Maklum balas tidak boleh melebihi {max} aksara",
    UiTextKey.FeedbackMsgRateLimited to
        "Anda telah menghantar terlalu banyak maklum balas baru-baru ini. Sila cuba lagi nanti.",
    UiTextKey.FeedbackMsgSubmitFailed to "Gagal menghantar maklum balas. Sila semak sambungan internet anda.",
    UiTextKey.ShopMsgLoadFailed to "Gagal memuatkan data kedai",
    UiTextKey.ShopMsgPurchaseFailed to "Pembelian gagal",
    UiTextKey.ShopMsgHistoryExpanded to "Had sejarah dilanjutkan kepada {limit} rekod!",
    UiTextKey.ShopMsgPaletteAlreadyUnlocked to "Palet sudah dibuka",
    UiTextKey.ShopMsgUnlockFailed to "Gagal membuka palet",
    UiTextKey.ShopMsgPaletteUnlocked to "Palet dibuka!",
    UiTextKey.ShopMsgPaletteSelectFailed to "Gagal menukar palet warna. Sila cuba lagi.",
    UiTextKey.ProfileMsgLoadFailed to "Gagal memuatkan profil",
    UiTextKey.ProfileMsgUsernameTooShort to "Nama pengguna mesti sekurang-kurangnya {min} aksara",
    UiTextKey.ProfileMsgUsernameTooLong to "Nama pengguna tidak boleh melebihi {max} aksara",
    UiTextKey.ProfileMsgUsernameInvalidChars to "Nama pengguna hanya boleh mengandungi huruf, nombor dan garis bawah",
    UiTextKey.ProfileMsgVerifyFailed to "Tidak dapat mengesahkan status akaun. Sila cuba lagi.",
    UiTextKey.ProfileMsgUsernameTaken to "Nama pengguna sudah diambil",
    UiTextKey.ProfileMsgUsernameUpdatedSyncing to
        "Nama pengguna dikemas kini. Senarai rakan anda mungkin mengambil sedikit masa untuk dimuat semula.",
    UiTextKey.ProfileMsgUsernameUpdated to "Nama pengguna berjaya dikemas kini",
    UiTextKey.ProfileMsgUpdateFailed to "Gagal mengemas kini profil",
    UiTextKey.ProfileMsgSetUsernameFailed to "Gagal menetapkan nama pengguna",
    UiTextKey.ProfileMsgDeleteFailed to "Gagal memadam akaun",
    UiTextKey.ProfileMsgInvalidPassword to "Kata laluan salah. Sila cuba lagi.",
    UiTextKey.ProfileMsgGoogleReauthFailed to "Pengesahan semula Google gagal. Sila cuba lagi.",
    UiTextKey.ProfileMsgGoogleNoToken to "Log masuk Google gagal: tiada token ID.",
    UiTextKey.ProfileMsgGoogleSignInFailed to "Log masuk Google gagal. Sila cuba lagi.",
    UiTextKey.ProfileGoogleReauthHint to "Sahkan semula dengan akaun Google anda untuk mengesahkan.",
    UiTextKey.ProfileContinueWithGoogle to "Teruskan dengan Google",
    UiTextKey.SettingsMsgLanguageSaveFailed to "Gagal menyimpan tetapan bahasa. Sila cuba lagi.",
    UiTextKey.SettingsMsgFontSaveFailed to "Gagal menyimpan saiz fon. Sila cuba lagi.",
    UiTextKey.SettingsMsgThemeSaveFailed to "Gagal menyimpan tetapan tema. Sila cuba lagi.",
    UiTextKey.SettingsMsgAutoThemeFailed to "Gagal mematikan tema automatik. Sila cuba lagi.",
    UiTextKey.SettingsMsgVoiceSaveFailed to "Gagal menyimpan tetapan suara. Sila cuba lagi.",
    UiTextKey.SettingsMsgNotificationSaveFailed to "Gagal menyimpan tetapan pemberitahuan. Sila cuba lagi.",
    UiTextKey.AuthMsgEmailEmpty to "E-mel tidak boleh kosong",
    UiTextKey.AuthMsgEmailInvalid to "Format e-mel tidak sah",
    UiTextKey.AuthMsgTooManyLogins to "Terlalu banyak cubaan log masuk. Sila cuba lagi nanti.",
    UiTextKey.AuthMsgTooManyResets to "Terlalu banyak cubaan menetapkan semula kata laluan. Sila cuba lagi nanti.",
    UiTextKey.AuthMsgGoogleEmptyToken to "Log masuk Google gagal: token ID kosong.",
    UiTextKey.AuthMsgGoogleNoToken to "Log masuk Google gagal: tiada token ID diterima.",
    UiTextKey.AuthMsgGoogleFailedCode to "Log masuk Google gagal (kod {code}).",
    UiTextKey.AuthMsgGoogleNotConfigured to "Log masuk Google tidak dikonfigurasikan untuk binaan ini.",
    UiTextKey.ErrUnknown to "Ralat tidak diketahui berlaku.",
    UiTextKey.ErrNetworkFailed to "Sambungan rangkaian gagal. Sila semak sambungan internet anda.",
    UiTextKey.ErrOperationTimedOut to "Operasi tamat masa. Sila cuba lagi.",
    UiTextKey.ErrCancelled to "Operasi telah dibatalkan.",
    UiTextKey.ErrUnexpected to "Ralat tidak dijangka berlaku. Sila cuba lagi.",
    UiTextKey.ErrAuthInvalidEmail to "Format alamat e-mel tidak betul.",
    UiTextKey.ErrAuthWrongPassword to "Kata laluan salah.",
    UiTextKey.ErrAuthUserNotFound to "Tiada akaun ditemui dengan e-mel ini.",
    UiTextKey.ErrAuthUserDisabled to "Akaun ini telah dinyahaktifkan.",
    UiTextKey.ErrAuthTooManyRequests to "Terlalu banyak permintaan. Sila cuba lagi nanti.",
    UiTextKey.ErrAuthEmailInUse to "Alamat e-mel ini sudah digunakan oleh akaun lain.",
    UiTextKey.ErrAuthFailed to "Pengesahan gagal. Sila cuba lagi.",
    UiTextKey.ErrDbPermission to "Anda tiada kebenaran untuk mengakses data ini.",
    UiTextKey.ErrDbUnavailable to "Perkhidmatan tidak tersedia buat masa ini. Sila cuba lagi nanti.",
    UiTextKey.ErrDbGeneric to "Ralat pangkalan data. Sila cuba lagi.",

    // Phase 1 part 5: learning, word bank, custom words, favorites and empty states
    UiTextKey.LearningMsgLoadFailed to "Gagal memuatkan helaian pembelajaran.",
    UiTextKey.LearningMsgSheetLoadFailed to "Gagal memuatkan helaian pembelajaran.",
    UiTextKey.LearningMsgSheetGenerateCancelled to "Penjanaan helaian pembelajaran dibatalkan.",
    UiTextKey.LearningMsgSheetGenerateFailed to "Gagal menjana helaian pembelajaran. Sila cuba lagi.",
    UiTextKey.LearningMsgQuizParseIncomplete to "Kuiz hanya ada {parsed} daripada {total} soalan. Sila jana semula.",
    UiTextKey.LearningMsgQuizGenerateCancelled to "Penjanaan kuiz dibatalkan.",
    UiTextKey.LearningMsgQuizGenerateFailed to "Gagal menjana kuiz. Sila cuba lagi.",
    UiTextKey.LearningMsgShareNotReady to "Kandungan helaian belum sedia. Sila tunggu sehingga selesai dimuatkan.",
    UiTextKey.QuizMsgNotGenerated to "Belum ada kuiz yang dijana.",
    UiTextKey.QuizMsgCorrupted to "Kuiz yang disimpan rosak. Sila jana semula.",
    UiTextKey.QuizMsgLoadFailed to "Gagal memuatkan kuiz.",
    UiTextKey.QuizMsgSubmitFailed to "Gagal menghantar kuiz. Sila cuba lagi.",
    UiTextKey.ShareMsgFailed to "Gagal berkongsi. Sila cuba lagi.",
    UiTextKey.WordBankMsgLoadFailed to "Gagal memuatkan bank perkataan.",
    UiTextKey.WordBankMsgDeleteFailed to "Gagal memadam perkataan. Sila cuba lagi.",
    UiTextKey.WordBankMsgNeedMoreRecords to "Perlu {count} rekod lagi untuk menyegarkan bank perkataan",
    UiTextKey.WordBankMsgNoHistory to "Tiada sejarah terjemahan untuk bahasa ini",
    UiTextKey.WordBankMsgGenerateFailed to "Gagal menjana bank perkataan. Sila cuba lagi.",
    UiTextKey.WordBankMsgShareIncomplete to "Data perkataan tidak lengkap. Segar semula dan cuba lagi.",
    UiTextKey.CustomWordsMsgOriginalRequired to "Perkataan asal diperlukan",
    UiTextKey.CustomWordsMsgOriginalTooLong to "Perkataan asal tidak boleh melebihi {max} aksara",
    UiTextKey.CustomWordsMsgTranslatedRequired to "Perkataan terjemahan diperlukan",
    UiTextKey.CustomWordsMsgTranslatedTooLong to "Perkataan terjemahan tidak boleh melebihi {max} aksara",
    UiTextKey.CustomWordsMsgPronunciationTooLong to "Sebutan tidak boleh melebihi {max} aksara",
    UiTextKey.CustomWordsMsgExampleTooLong to "Contoh tidak boleh melebihi {max} aksara",
    UiTextKey.CustomWordsMsgDuplicate to "Perkataan ini sudah ada dalam bank perkataan tersuai anda",
    UiTextKey.CustomWordsMsgAddFailed to "Gagal menambah perkataan",
    UiTextKey.CustomWordsMsgInvalidLanguageUpdate to "Permintaan kemas kini bahasa tidak sah",
    UiTextKey.CustomWordsMsgTranslateFailed to "Terjemahan gagal: {detail}",
    UiTextKey.CustomWordsMsgUpdateLanguageFailed to "Gagal mengemas kini bahasa perkataan",
    UiTextKey.CustomWordsMsgLoadFailed to "Gagal memuatkan perkataan tersuai",
    UiTextKey.FavoritesMsgLoadFailed to "Gagal memuatkan kegemaran",
    UiTextKey.FavoritesMsgLoadMoreFailed to "Gagal memuatkan lebih banyak kegemaran",
    UiTextKey.FavoritesMsgRefreshFailed to "Gagal menyegarkan kegemaran",
    UiTextKey.FavoritesMsgRemoveFailed to "Gagal membuang kegemaran",
    UiTextKey.FavoritesMsgDeleteSelectedFailed to "Gagal memadam item yang dipilih",
    UiTextKey.FavoritesLoadMore to "Muat Lagi",
    UiTextKey.A11yConfirmDelete to "Sahkan padam",
    UiTextKey.A11yDeleteMode to "Mod padam",
    UiTextKey.A11yExpand to "Kembangkan",
    UiTextKey.A11yCollapse to "Runtuhkan",
    UiTextKey.EmptyNoHistoryTitle to "Belum Ada Sejarah",
    UiTextKey.EmptyNoFavoritesTitle to "Tiada Kegemaran",
    UiTextKey.EmptyNoLearningTitle to "Tiada Bahan Pembelajaran",
    UiTextKey.EmptySharedItemsMessage to "Belum ada item yang dikongsi dengan anda",
    UiTextKey.SharedMaterialLoadingContent to "Memuatkan kandungan...",
    UiTextKey.HelpVersionTemplate to "Versi: {version}",
    UiTextKey.StartupTagline to "Terjemah & Belajar",

    // Extended Error Messages
)
