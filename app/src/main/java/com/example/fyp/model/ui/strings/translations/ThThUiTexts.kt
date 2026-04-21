package com.example.fyp.model.ui

/**
 * Thai (th-TH) UI text map — ข้อความภาษาไทย.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val ThThUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "ใช้ไมโครโฟน",
    UiTextKey.CopyButton to "คัดลอก",
    UiTextKey.SpeakScriptButton to "อ่านข้อความต้นฉบับ",
    UiTextKey.TranslateButton to "แปล",
    UiTextKey.CopyTranslationButton to "คัดลอกคำแปล",
    UiTextKey.SpeakTranslationButton to "อ่านคำแปล",
    UiTextKey.RecognizingStatus to "กำลังบันทึก...พูดเลย จะหยุดเอง",
    UiTextKey.TranslatingStatus to "กำลังแปล...",
    UiTextKey.SpeakingOriginalStatus to "กำลังอ่านข้อความต้นฉบับ...",
    UiTextKey.SpeakingTranslationStatus to "กำลังอ่านคำแปล...",
    UiTextKey.SpeakingLabel to "กำลังอ่าน",
    UiTextKey.FinishedSpeakingOriginal to "อ่านข้อความต้นฉบับเสร็จแล้ว",
    UiTextKey.FinishedSpeakingTranslation to "อ่านคำแปลเสร็จแล้ว",
    UiTextKey.TtsErrorTemplate to "ข้อผิดพลาดเสียง: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "ภาษาแอป",
    UiTextKey.DetectLanguageLabel to "ตรวจจับภาษา",
    UiTextKey.TranslateToLabel to "แปลเป็น",

    // Language names
    UiTextKey.LangEnUs to "อังกฤษ",
    UiTextKey.LangZhHk to "กวางตุ้ง",
    UiTextKey.LangJaJp to "ญี่ปุ่น",
    UiTextKey.LangZhCn to "จีน (ตัวย่อ)",
    UiTextKey.LangFrFr to "ฝรั่งเศส",
    UiTextKey.LangDeDe to "เยอรมัน",
    UiTextKey.LangKoKr to "เกาหลี",
    UiTextKey.LangEsEs to "สเปน",
    UiTextKey.LangIdId to "อินโดนีเซีย",
    UiTextKey.LangViVn to "เวียดนาม",
    UiTextKey.LangThTh to "ไทย",
    UiTextKey.LangFilPh to "ฟิลิปปินส์",
    UiTextKey.LangMsMy to "มาเลย์",
    UiTextKey.LangPtBr to "โปรตุเกส",
    UiTextKey.LangItIt to "อิตาลี",
    UiTextKey.LangRuRu to "รัสเซีย",

    // Navigation
    UiTextKey.NavHistory to "ประวัติ",
    UiTextKey.NavLogin to "เข้าสู่ระบบ",
    UiTextKey.NavLogout to "ออกจากระบบ",
    UiTextKey.NavBack to "ย้อนกลับ",
    UiTextKey.ActionCancel to "ยกเลิก",
    UiTextKey.ActionDelete to "ลบ",
    UiTextKey.ActionOpen to "เปิด",
    UiTextKey.ActionName to "ตั้งชื่อ",
    UiTextKey.ActionSave to "บันทึก",
    UiTextKey.ActionConfirm to "ยืนยัน",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "พิมพ์ที่นี่หรือใช้ไมโครโฟน...",
    UiTextKey.SpeechTranslatedPlaceholder to "คำแปลจะแสดงที่นี่...",
    UiTextKey.StatusAzureErrorTemplate to "ข้อผิดพลาด Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "ข้อผิดพลาดการแปล: %s",
    UiTextKey.StatusLoginRequiredTranslation to "เข้าสู่ระบบเพื่อแปล",
    UiTextKey.StatusRecognizePreparing to "กำลังเตรียมไมค์...(ยังไม่ต้องพูด)",
    UiTextKey.StatusRecognizeListening to "กำลังฟัง...พูดเลย",

    // Pagination
    UiTextKey.PaginationPrevLabel to "หน้าก่อน",
    UiTextKey.PaginationNextLabel to "หน้าถัดไป",
    UiTextKey.PaginationPageLabelTemplate to "หน้า {page} / {total}",

    // Toast
    UiTextKey.ToastCopied to "คัดลอกแล้ว",
    UiTextKey.DisableText to "เข้าสู่ระบบเพื่อแปลและบันทึกประวัติ",

    // Error
    UiTextKey.ErrorRetryButton to "ลองใหม่",
    UiTextKey.ErrorGenericMessage to "เกิดข้อผิดพลาด กรุณาลองใหม่",

    // Shop
    UiTextKey.ShopTitle to "ร้านค้า",
    UiTextKey.ShopCoinBalance to "เหรียญของฉัน",
    UiTextKey.ShopHistoryExpansionTitle to "ขยายประวัติ",
    UiTextKey.ShopHistoryExpansionDesc to "ขยายจำนวนประวัติเพื่อดูคำแปลมากขึ้น",
    UiTextKey.ShopCurrentLimit to "จำนวนปัจจุบัน: {limit} รายการ",
    UiTextKey.ShopMaxLimit to "จำนวนสูงสุด:",
    UiTextKey.ShopBuyHistoryExpansion to "ซื้อ (+{increment} รายการ, {cost} เหรียญ)",
    UiTextKey.ShopInsufficientCoins to "เหรียญไม่พอ",
    UiTextKey.ShopMaxLimitReached to "ถึงจำนวนสูงสุดแล้ว",
    UiTextKey.ShopHistoryExpandedTitle to "ขยายสำเร็จ!",
    UiTextKey.ShopHistoryExpandedMessage to "จำนวนประวัติของคุณตอนนี้คือ {limit} รายการ! ดูคำแปลได้มากขึ้น!",
    UiTextKey.ShopColorPaletteTitle to "ธีมสี",
    UiTextKey.ShopColorPaletteDesc to "เลือกธีมสี 10 เหรียญต่อธีม",
    UiTextKey.ShopEntry to "ร้านค้า",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "ตั้งค่าเสียง",
    UiTextKey.VoiceSettingsDesc to "เลือกเสียงอ่านสำหรับแต่ละภาษา",

    // Instructions
    UiTextKey.SpeechInstructions to "กดไมค์เพื่อรู้จำเสียง แล้วแปล หากตรวจจับอัตโนมัติไม่อัพเดทหลังเปลี่ยน กดรีเฟรชที่มุมขวา",
    UiTextKey.HomeInstructions to "เลือกฟีเจอร์เพื่อเริ่มต้น",
    UiTextKey.ContinuousInstructions to "เลือกสองภาษาและเริ่มโหมดสนทนา",

    // Home
    UiTextKey.HomeTitle to "แปลทันที",
    UiTextKey.HelpTitle to "ช่วยเหลือ",
    UiTextKey.SpeechTitle to "แปลด่วน",
    UiTextKey.HomeStartButton to "เริ่มแปล",
    UiTextKey.HomeFeaturesTitle to "ฟีเจอร์",
    UiTextKey.HomeDiscreteDescription to "แปลข้อความและเสียงสั้นๆ",
    UiTextKey.HomeContinuousDescription to "แปลสนทนาสองทางแบบเรียลไทม์",
    UiTextKey.HomeLearningDescription to "สร้างสื่อการเรียนและแบบทดสอบจากประวัติ",

    // Help
    UiTextKey.HelpCurrentTitle to "ฟีเจอร์ปัจจุบัน",
    UiTextKey.HelpCautionTitle to "ข้อควรระวัง",
    UiTextKey.HelpCurrentFeatures to "ฟีเจอร์ปัจจุบัน:\n" +
            "  • แปลด่วน: รู้จำเสียงและแปล\n" +
            "  • สนทนาต่อเนื่อง: แปลเสียงสองทาง\n" +
            "  • ประวัติ: ดูคำแปลก่อนหน้า\n" +
            "  • สื่อการเรียน: สร้างคำศัพท์และแบบทดสอบ\n\n" +
            "การแปล:\n" +
            "  • รู้จำเสียง Azure AI\n" +
            "  • บริการแปล Azure\n",
    UiTextKey.HelpCaution to "ข้อควรระวัง:\n" +
            "  • รู้จำเสียงต้องใช้อินเทอร์เน็ต\n" +
            "  • แคชแปลในเครื่องใช้งานออฟไลน์ได้\n" +
            "  • ตรวจสอบคำแปลสำคัญกับผู้เชี่ยวชาญ\n\n" +
            "บัญชีและข้อมูล:\n" +
            "  • ประวัติ การเรียน และเหรียญต้องเข้าสู่ระบบ\n" +
            "  • ข้อมูลเก็บอย่างปลอดภัยใน Firebase Firestore\n\n" +
            "แก้ไขปัญหา:\n" +
            "  • หากไม่ทำงาน เปิดแอปใหม่\n",
    UiTextKey.HelpNotesTitle to "เคล็ดลับ",
    UiTextKey.HelpNotes to "💡 เคล็ดลับการใช้งาน:\n\n" +
            "เพื่อคำแปลที่ดี:\n" +
            "  • พูดชัดเจนด้วยความเร็วปกติ\n" +
            "  • ลดเสียงรบกวนเพื่อรู้จำได้ดีขึ้น\n" +
            "  • แปลด่วนเหมาะกับประโยคสั้น\n\n" +
            "ภาษาแอป:\n" +
            "  • ค่าเริ่มต้น: อังกฤษ, ภาษาอื่นโดย AI\n" +
            "  • เวอร์ชันกวางตุ้งแปลด้วยมือ\n" +
            "อัพเดทและฟีดแบ็ก:\n" +
            "  • เวอร์ชันแอปที่ ตั้งค่า → เกี่ยวกับ\n" +
            "  • ส่งฟีดแบ็กที่ ตั้งค่า → ฟีดแบ็ก\n",

    // Feedback
    UiTextKey.FeedbackTitle to "ฟีดแบ็ก",
    UiTextKey.FeedbackDesc to "ขอบคุณสำหรับฟีดแบ็ก! แชร์คำแนะนำ บั๊ก หรือการให้คะแนน",
    UiTextKey.FeedbackMessagePlaceholder to "พิมพ์ฟีดแบ็ก...",
    UiTextKey.FeedbackSubmitButton to "ส่ง",
    UiTextKey.FeedbackSubmitting to "กำลังส่ง...",
    UiTextKey.FeedbackSuccessTitle to "ขอบคุณ!",
    UiTextKey.FeedbackSuccessMessage to "ฟีดแบ็กของคุณถูกส่งเรียบร้อย ขอบคุณ!",
    UiTextKey.FeedbackErrorTitle to "ส่งไม่สำเร็จ",
    UiTextKey.FeedbackErrorMessage to "ส่งไม่สำเร็จ ตรวจสอบการเชื่อมต่อแล้วลองใหม่",
    UiTextKey.FeedbackMessageRequired to "กรุณาพิมพ์ฟีดแบ็ก",

    // Continuous mode
    UiTextKey.ContinuousTitle to "สนทนาต่อเนื่อง",
    UiTextKey.ContinuousStartButton to "เริ่มสนทนา",
    UiTextKey.ContinuousStopButton to "หยุดบันทึก",
    UiTextKey.ContinuousStartScreenButton to "สนทนาต่อเนื่อง",
    UiTextKey.ContinuousPersonALabel to "A กำลังพูด",
    UiTextKey.ContinuousPersonBLabel to "B กำลังพูด",
    UiTextKey.ContinuousCurrentStringLabel to "ข้อความปัจจุบัน:",
    UiTextKey.ContinuousSpeakerAName to "คน A",
    UiTextKey.ContinuousSpeakerBName to "คน B",
    UiTextKey.ContinuousTranslationSuffix to " · คำแปล",
    UiTextKey.ContinuousPreparingMicText to "กำลังเตรียมไมค์...(ยังไม่ต้องพูด)",
    UiTextKey.ContinuousTranslatingText to "กำลังแปล...",

    // History
    UiTextKey.HistoryTitle to "ประวัติ",
    UiTextKey.HistoryTabDiscrete to "แปลด่วน",
    UiTextKey.HistoryTabContinuous to "สนทนาต่อเนื่อง",
    UiTextKey.HistoryNoContinuousSessions to "ยังไม่มีเซสชันสนทนา",
    UiTextKey.HistoryNoDiscreteRecords to "ยังไม่มีคำแปล",
    UiTextKey.DialogDeleteRecordTitle to "ลบรายการ?",
    UiTextKey.DialogDeleteRecordMessage to "การกระทำนี้ไม่สามารถย้อนกลับได้",
    UiTextKey.DialogDeleteSessionTitle to "ลบเซสชัน?",
    UiTextKey.DialogDeleteSessionMessage to "รายการทั้งหมดในเซสชันนี้จะถูกลบ ไม่สามารถย้อนกลับได้",
    UiTextKey.HistoryDeleteSessionButton to "ลบ",
    UiTextKey.HistoryNameSessionTitle to "ตั้งชื่อ",
    UiTextKey.HistorySessionNameLabel to "ชื่อเซสชัน",
    UiTextKey.HistorySessionTitleTemplate to "เซสชัน {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} รายการ",

    // Filter
    UiTextKey.FilterDropdownDefault to "ทุกภาษา",
    UiTextKey.FilterTitle to "กรองประวัติ",
    UiTextKey.FilterLangDrop to "ภาษา",
    UiTextKey.FilterKeyword to "คำค้นหา",
    UiTextKey.FilterApply to "ใช้",
    UiTextKey.FilterCancel to "ยกเลิก",
    UiTextKey.FilterClear to "ล้างตัวกรอง",
    UiTextKey.FilterHistoryScreenTitle to "ตัวกรอง",

    // Auth
    UiTextKey.AuthLoginTitle to "เข้าสู่ระบบ",
    UiTextKey.AuthRegisterTitle to "สมัครสมาชิก (หยุดชั่วคราว)",
    UiTextKey.AuthLoginHint to "ใช้อีเมลและรหัสผ่านที่ลงทะเบียนไว้",
    UiTextKey.AuthRegisterRules to "สมัครสมาชิกหยุดชั่วคราวระหว่างพัฒนา\nหมายเหตุ: อีเมลไม่ถูกต้องจะไม่สามารถกู้รหัสผ่านได้\n" +
            "กฎการสมัคร:\n" +
            "• รูปแบบอีเมลที่ถูกต้อง (เช่น name@example.com)\n" +
            "• รหัสผ่านอย่างน้อย 6 ตัวอักษร\n" +
            "• ยืนยันรหัสผ่านต้องตรงกัน",
    UiTextKey.AuthEmailLabel to "อีเมล",
    UiTextKey.AuthPasswordLabel to "รหัสผ่าน",
    UiTextKey.AuthConfirmPasswordLabel to "ยืนยันรหัสผ่าน",
    UiTextKey.AuthLoginButton to "เข้าสู่ระบบ",
    UiTextKey.AuthRegisterButton to "สมัครสมาชิก",
    UiTextKey.AuthToggleToRegister to "ยังไม่มีบัญชี? สมัคร (หยุดชั่วคราว)",
    UiTextKey.AuthToggleToLogin to "มีบัญชีแล้ว? เข้าสู่ระบบ",
    UiTextKey.AuthErrorPasswordsMismatch to "รหัสผ่านไม่ตรงกัน",
    UiTextKey.AuthErrorPasswordTooShort to "รหัสผ่านอย่างน้อย 6 ตัวอักษร",
    UiTextKey.AuthRegistrationDisabled to "สมัครสมาชิกหยุดชั่วคราวระหว่างพัฒนา",
    UiTextKey.AuthResetEmailSent to "อีเมลรีเซ็ตถูกส่ง (ถ้ามีบัญชี) ตรวจสอบกล่องจดหมาย",

    // Password reset
    UiTextKey.ForgotPwText to "ลืมรหัสผ่าน?",
    UiTextKey.ResetPwTitle to "รีเซ็ตรหัสผ่าน",
    UiTextKey.ResetPwText to "กรอกอีเมลบัญชี เราจะส่งลิงก์รีเซ็ต\nตรวจสอบว่าอีเมลลงทะเบียนแล้ว\n",
    UiTextKey.ResetSendingText to "กำลังส่ง...",
    UiTextKey.ResetSendText to "ส่งอีเมลรีเซ็ต",

    // Settings
    UiTextKey.SettingsTitle to "ตั้งค่า",
    UiTextKey.SettingsPrimaryLanguageTitle to "ภาษาหลัก",
    UiTextKey.SettingsPrimaryLanguageDesc to "ใช้สำหรับคำอธิบายและแนะนำในการเรียน",
    UiTextKey.SettingsPrimaryLanguageLabel to "ภาษาหลัก",
    UiTextKey.SettingsFontSizeTitle to "ขนาดตัวอักษร",
    UiTextKey.SettingsFontSizeDesc to "ปรับขนาดตัวอักษร (ซิงค์ข้ามอุปกรณ์)",
    UiTextKey.SettingsScaleTemplate to "สเกล: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "ธีมสี",
    UiTextKey.SettingsColorPaletteDesc to "เลือกธีมสี 10 เหรียญต่อธีม",
    UiTextKey.SettingsColorCostTemplate to "{cost} เหรียญ",
    UiTextKey.SettingsColorUnlockButton to "ปลดล็อก",
    UiTextKey.SettingsColorSelectButton to "เลือก",
    UiTextKey.SettingsColorAlreadyUnlocked to "ปลดล็อกแล้ว",
    UiTextKey.SettingsPreviewHeadline to "หัวข้อ: ตัวอย่างตัวอักษรใหญ่",
    UiTextKey.SettingsPreviewBody to "เนื้อหา: ตัวอย่างตัวอักษรปกติ",
    UiTextKey.SettingsPreviewLabel to "ป้ายกำกับ: ตัวอย่างตัวอักษรเล็ก",
    UiTextKey.SettingsAboutTitle to "เกี่ยวกับ",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "เข้าสู่ระบบแล้ว ตั้งค่าบันทึกและซิงค์อัตโนมัติ",
    UiTextKey.SettingsThemeTitle to "ธีม",
    UiTextKey.SettingsThemeDesc to "เลือกธีม: ระบบ, สว่าง, มืด, หรือตามเวลา",
    UiTextKey.SettingsThemeSystem to "ระบบ",
    UiTextKey.SettingsThemeLight to "สว่าง",
    UiTextKey.SettingsThemeDark to "มืด",
    UiTextKey.SettingsThemeScheduled to "ตามเวลา",
    UiTextKey.SettingsResetPW to "รีเซ็ตรหัสผ่าน",
    UiTextKey.SettingsQuickLinks to "ตั้งค่าเพิ่มเติม",
    UiTextKey.SettingsNotLoggedInWarning to "เข้าสู่ระบบเพื่อดูตั้งค่าบัญชี ภาษาแอปเปลี่ยนได้โดยไม่ต้องเข้าสู่ระบบ",
    UiTextKey.SettingsVoiceTitle to "ตั้งค่าเสียง",
    UiTextKey.SettingsVoiceDesc to "เลือกเสียงอ่านสำหรับแต่ละภาษา",
    UiTextKey.SettingsVoiceLanguageLabel to "ภาษา",
    UiTextKey.SettingsVoiceSelectLabel to "เสียง",
    UiTextKey.SettingsVoiceDefault to "ค่าเริ่มต้น",

    // Learning
    UiTextKey.LearningTitle to "การเรียน",
    UiTextKey.LearningHintCount to "(*) จำนวน = คำแปลที่มีภาษานี้",
    UiTextKey.LearningErrorTemplate to "ข้อผิดพลาด: %s",
    UiTextKey.LearningGenerate to "สร้าง",
    UiTextKey.LearningRegenerate to "สร้างใหม่",
    UiTextKey.LearningGenerating to "กำลังสร้าง...",
    UiTextKey.LearningOpenSheetTemplate to "สื่อ {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "สื่อการเรียน {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "ภาษาหลัก: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "รายการปัจจุบัน: {nowCount} (ตอนสร้าง: {savedCount})",
    UiTextKey.LearningSheetNoContent to "ไม่มีเนื้อหา",
    UiTextKey.LearningSheetRegenerate to "สร้างใหม่",
    UiTextKey.LearningSheetGenerating to "กำลังสร้าง...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 นี่คืออะไร?",
    UiTextKey.LearningSheetWhatIsThisDesc to "สื่อการเรียนสร้างจากประวัติแปล ประกอบด้วยคำศัพท์ คำจำกัดความ ตัวอย่าง และหมายเหตุไวยากรณ์ ทดสอบความรู้ด้วยแบบทดสอบ!",
    UiTextKey.LearningRegenBlockedTitle to "ไม่สามารถสร้างใหม่",
    UiTextKey.LearningRegenBlockedMessage to "ต้องมีอย่างน้อย 5 รายการเพิ่มเพื่อสร้างใหม่ ยังขาดอีก {needed}",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ ยังขาดอีก {needed} รายการเพื่อสร้างใหม่ (ต้องการ 5 เพิ่ม)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ จำนวนต้องมากกว่าตอนสร้างครั้งก่อน",
    UiTextKey.LearningRegenInfoTitle to "กฎการสร้างใหม่",
    UiTextKey.LearningRegenInfoMessage to "สร้างสื่อการเรียนใหม่:\n\n• ครั้งแรก: เมื่อไหร่ก็ได้\n• สร้างใหม่: ต้องมี 5 รายการเพิ่ม\n\nปุ่มจะเปลี่ยนเขียวเมื่อพร้อม ถ้าเทา แปลเพิ่มอีก!\n\n💡 เคล็ดลับ: หากจำนวนไม่อัพเดท เปิดแอปใหม่",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ แบบทดสอบสร้างแล้วสำหรับเวอร์ชันนี้ สร้างสื่อใหม่เพื่อทำแบบทดสอบใหม่",

    // Quiz
    UiTextKey.QuizTitleTemplate to "แบบทดสอบ: {language}",
    UiTextKey.QuizOpenButton to "📝 แบบทดสอบ",
    UiTextKey.QuizGenerateButton to "🔄 สร้างแบบทดสอบ",
    UiTextKey.QuizGenerating to "⏳ กำลังสร้าง...",
    UiTextKey.QuizUpToDate to "✓ ล่าสุด",
    UiTextKey.QuizBlocked to "🚫 ถูกบล็อก",
    UiTextKey.QuizWait to "⏳ รอ...",
    UiTextKey.QuizMaterialsQuizTemplate to "สื่อ: {materials} | แบบทดสอบ: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 สามารถรับเหรียญ!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 ยังขาดอีก {count} สำหรับเหรียญ",
    UiTextKey.QuizCancelButton to "ยกเลิก",
    UiTextKey.QuizPreviousButton to "ข้อก่อน",
    UiTextKey.QuizNextButton to "ข้อถัดไป",
    UiTextKey.QuizSubmitButton to "ส่ง",
    UiTextKey.QuizRetakeButton to "ทำใหม่",
    UiTextKey.QuizBackButton to "กลับ",
    UiTextKey.QuizLoadingText to "กำลังโหลดแบบทดสอบ...",
    UiTextKey.QuizGeneratingText to "กำลังสร้างแบบทดสอบ...",
    UiTextKey.QuizNoMaterialsTitle to "ไม่พบสื่อ",
    UiTextKey.QuizNoMaterialsMessage to "สร้างสื่อการเรียนก่อน แล้วเข้าแบบทดสอบ",
    UiTextKey.QuizErrorTitle to "⚠️ ข้อผิดพลาดแบบทดสอบ",
    UiTextKey.QuizErrorSuggestion to "แนะนำ: ใช้ปุ่มด้านบนเพื่อสร้างแบบทดสอบ",
    UiTextKey.QuizCompletedTitle to "ทำแบบทดสอบเสร็จ!",
    UiTextKey.QuizAnswerReviewTitle to "ตรวจคำตอบ",
    UiTextKey.QuizYourAnswerTemplate to "คำตอบของคุณ: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "คำตอบที่ถูก: {Answer}",
    UiTextKey.QuizQuestionTemplate to "ข้อ {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ ไม่สามารถสร้างใหม่: สื่อ({materials}) < แบบทดสอบ({quiz}), แปลเพิ่ม",
    UiTextKey.QuizAnotherGenInProgress to "⏳ กำลังสร้างอื่น กรุณารอ",
    UiTextKey.QuizCoinRulesTitle to "🪙 กฎเหรียญ",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ วิธีรับ:",
    UiTextKey.QuizCoinRulesRequirements to "ข้อกำหนด:",
    UiTextKey.QuizCoinRulesCurrentStatus to "สถานะปัจจุบัน:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ รับเหรียญได้ในแบบทดสอบถัดไป!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• ยังขาดอีก {count} สำหรับเหรียญ",
    UiTextKey.QuizCoinRule1Coin to "• 1 เหรียญต่อคำตอบถูก",
    UiTextKey.QuizCoinRuleFirstAttempt to "• นับเฉพาะครั้งแรก",
    UiTextKey.QuizCoinRuleMatchMaterials to "• แบบทดสอบต้องตรงกับสื่อ",
    UiTextKey.QuizCoinRulePlus10 to "• อย่างน้อย 10 รายการเพิ่มจากครั้งรับสุดท้าย",
    UiTextKey.QuizCoinRuleNoDelete to "• เหรียญไม่คืนเมื่อลบรายการ",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• สื่อ: {count} รายการ",
    UiTextKey.QuizCoinRuleQuizTemplate to "• แบบทดสอบ: {count} รายการ",
    UiTextKey.QuizCoinRuleGotIt to "เข้าใจ!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 สร้างแบบทดสอบใหม่?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ รับเหรียญจากแบบทดสอบนี้ได้! (เฉพาะครั้งแรก)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ ไม่สามารถรับเหรียญจากแบบทดสอบนี้",
    UiTextKey.QuizRegenNeedMoreTemplate to "ต้องการอีก {count} คำแปลเพื่อมีสิทธิ์ (10 มากกว่าครั้งรับสุดท้าย)",
    UiTextKey.QuizRegenReminder to "เคล็ดลับ: ฝึกและทำใหม่ได้ แต่เหรียญเฉพาะครั้งแรกที่มีรายการเพียงพอ",
    UiTextKey.QuizRegenGenerateButton to "สร้าง",
    UiTextKey.QuizCoinsEarnedTitle to "✨ ได้เหรียญ!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "ยินดีด้วย! คุณได้รับ {Coins} เหรียญ!",
    UiTextKey.QuizCoinsRule1 to "• 1 เหรียญต่อคำตอบถูกในครั้งแรก",
    UiTextKey.QuizCoinsRule2 to "• ไม่มีเหรียญสำหรับครั้งต่อไป",
    UiTextKey.QuizCoinsRule3 to "• แบบทดสอบใหม่ต้องการ 10 รายการเพิ่ม",
    UiTextKey.QuizCoinsRule4 to "• แบบทดสอบต้องตรงกับสื่อ",
    UiTextKey.QuizCoinsRule5 to "• เหรียญรวมอยู่ในประวัติ",
    UiTextKey.QuizCoinsGreatButton to "เยี่ยม!",
    UiTextKey.QuizOutdatedMessage to "แบบทดสอบนี้ใช้สื่อเก่า",
    UiTextKey.QuizRecordsLabel to "รายการ",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 เหรียญของฉัน",
    UiTextKey.HistoryCoinRulesTitle to "กฎเหรียญ:",
    UiTextKey.HistoryCoinHowToEarnTitle to "วิธีรับ:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 เหรียญต่อคำตอบถูก",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• เฉพาะครั้งแรกต่อเวอร์ชัน",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• แบบทดสอบต้องตรงกับสื่อปัจจุบัน",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 กฎป้องกันโกง:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• ต้องมี 10 คำแปลใหม่จากครั้งรับสุดท้าย",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• เวอร์ชันแบบทดสอบต้องตรงกับสื่อ",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• ลบรายการบล็อกการสร้างใหม่ (เว้นแต่จำนวนมากกว่า)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• ไม่มีเหรียญสำหรับครั้งต่อไป",
    UiTextKey.HistoryCoinTipsTitle to "💡 เคล็ดลับ:",
    UiTextKey.HistoryCoinTipsRule1 to "• เพิ่มคำแปลอย่างสม่ำเสมอ",
    UiTextKey.HistoryCoinTipsRule2 to "• เรียนให้ดีก่อนลองครั้งแรก!",
    UiTextKey.HistoryCoinGotItButton to "เข้าใจ!",

    // History info
    UiTextKey.HistoryInfoTitle to "ข้อมูลประวัติ",
    UiTextKey.HistoryInfoLimitMessage to "ประวัติแสดง {limit} รายการล่าสุด ขยายได้ที่ร้านค้า!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "รายการเก่าถูกเก็บแต่ซ่อนเพื่อประสิทธิภาพ",
    UiTextKey.HistoryInfoFavoritesMessage to "หากต้องการเก็บคำแปลถาวร กด ❤️ เพื่อบันทึกในรายการโปรด",
    UiTextKey.HistoryInfoViewFavoritesMessage to "ดูรายการที่บันทึกที่ ตั้งค่า → รายการโปรด",
    UiTextKey.HistoryInfoFilterMessage to "ใช้ตัวกรองค้นหาใน {limit} รายการที่แสดง",
    UiTextKey.HistoryInfoGotItButton to "เข้าใจ",

    // Word bank
    UiTextKey.WordBankTitle to "คลังคำ",
    UiTextKey.WordBankSelectLanguage to "เลือกภาษาเพื่อดูหรือสร้างคลังคำ:",
    UiTextKey.WordBankNoHistory to "ยังไม่มีประวัติแปล",
    UiTextKey.WordBankNoHistoryHint to "เริ่มแปลเพื่อสร้างคลังคำ!",
    UiTextKey.WordBankWordsCount to "คำ",
    UiTextKey.WordBankGenerating to "กำลังสร้าง...",
    UiTextKey.WordBankGenerate to "สร้างคลังคำ",
    UiTextKey.WordBankRegenerate to "สร้างคลังคำใหม่",
    UiTextKey.WordBankRefresh to "🔄 รีเฟรชคลัง",
    UiTextKey.WordBankEmpty to "คลังคำว่าง",
    UiTextKey.WordBankEmptyHint to "กดด้านบนเพื่อสร้างจากประวัติ",
    UiTextKey.WordBankExample to "ตัวอย่าง:",
    UiTextKey.WordBankDifficulty to "ระดับ:",
    UiTextKey.WordBankFilterCategory to "หมวดหมู่",
    UiTextKey.WordBankFilterCategoryAll to "ทุกหมวดหมู่",
    UiTextKey.WordBankFilterDifficultyLabel to "ระดับ:",
    UiTextKey.WordBankFilterNoResults to "ไม่มีคำที่ตรงกับตัวกรอง",
    UiTextKey.WordBankRefreshAvailable to "✅ มีอัพเดทพร้อม!",
    UiTextKey.WordBankRecordsNeeded to "รายการ (ต้องการ 20 เพื่ออัพเดท)",
    UiTextKey.WordBankRegenInfoTitle to "กฎการอัพเดท",
    UiTextKey.WordBankRegenInfoMessage to "อัพเดทคลังคำ:\n\n• ครั้งแรก: เมื่อไหร่ก็ได้\n• อัพเดท: ต้องมี 20 รายการเพิ่ม\n\nปุ่มจะเขียวเมื่อพร้อม ถ้าเทา แปลเพิ่ม!\n\n💡 เคล็ดลับ: หากจำนวนไม่อัพเดท เปิดแอปใหม่",
    UiTextKey.WordBankHistoryCountTemplate to "รายการปัจจุบัน: {nowCount} (ตอนสร้าง: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "ออกจากระบบ?",
    UiTextKey.DialogLogoutMessage to "ต้องเข้าสู่ระบบอีกครั้งเพื่อแปลและดูประวัติ",
    UiTextKey.DialogGenerateOverwriteTitle to "เขียนทับสื่อ?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "สื่อปัจจุบันจะถูกเขียนทับ\nสร้างสื่อสำหรับ {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "โปรไฟล์",
    UiTextKey.ProfileUsernameLabel to "ชื่อผู้ใช้",
    UiTextKey.ProfileUsernameHint to "กรอกชื่อผู้ใช้",
    UiTextKey.ProfileUpdateButton to "อัพเดทโปรไฟล์",
    UiTextKey.ProfileUpdateSuccess to "อัพเดทโปรไฟล์แล้ว",
    UiTextKey.ProfileUpdateError to "อัพเดทไม่สำเร็จ",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "ลบบัญชี",
    UiTextKey.AccountDeleteWarning to "⚠️ การกระทำนี้ถาวรและไม่สามารถย้อนกลับ!",
    UiTextKey.AccountDeleteConfirmMessage to "ข้อมูลทั้งหมดจะถูกลบถาวร: ประวัติ คลังคำ สื่อการเรียน ตั้งค่า กรอกรหัสผ่านเพื่อยืนยัน",
    UiTextKey.AccountDeletePasswordLabel to "รหัสผ่าน",
    UiTextKey.AccountDeleteButton to "ลบบัญชี",
    UiTextKey.AccountDeleteSuccess to "ลบบัญชีสำเร็จ",
    UiTextKey.AccountDeleteError to "ลบไม่สำเร็จ",
    UiTextKey.AccountDeleteReauthRequired to "กรอกรหัสผ่านเพื่อยืนยันการลบ",

    // Favorites
    UiTextKey.FavoritesTitle to "รายการโปรด",
    UiTextKey.FavoritesEmpty to "ยังไม่มีรายการโปรด",
    UiTextKey.FavoritesAddSuccess to "เพิ่มในรายการโปรดแล้ว",
    UiTextKey.FavoritesRemoveSuccess to "ลบจากรายการโปรดแล้ว",
    UiTextKey.FavoritesAddButton to "เพิ่มในรายการโปรด",
    UiTextKey.FavoritesRemoveButton to "ลบจากรายการโปรด",
    UiTextKey.FavoritesNoteLabel to "บันทึก",
    UiTextKey.FavoritesNoteHint to "เพิ่มบันทึก (ไม่บังคับ)",
    UiTextKey.FavoritesTabRecords to "รายการ",
    UiTextKey.FavoritesTabSessions to "เซสชัน",
    UiTextKey.FavoritesSessionsEmpty to "ยังไม่มีเซสชันโปรด",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} ข้อความ",

    // Custom words
    UiTextKey.CustomWordsTitle to "คำกำหนดเอง",
    UiTextKey.CustomWordsAdd to "เพิ่มคำ",
    UiTextKey.CustomWordsEdit to "แก้ไขคำ",
    UiTextKey.CustomWordsDelete to "ลบคำ",
    UiTextKey.CustomWordsOriginalLabel to "คำต้นฉบับ",
    UiTextKey.CustomWordsTranslatedLabel to "คำแปล",
    UiTextKey.CustomWordsPronunciationLabel to "คำอ่าน (ไม่บังคับ)",
    UiTextKey.CustomWordsExampleLabel to "ตัวอย่าง (ไม่บังคับ)",
    UiTextKey.CustomWordsSaveSuccess to "บันทึกคำแล้ว",
    UiTextKey.CustomWordsDeleteSuccess to "ลบคำแล้ว",
    UiTextKey.CustomWordsAlreadyExists to "คำนี้มีอยู่แล้ว",
    UiTextKey.CustomWordsOriginalLanguageLabel to "ภาษาต้นฉบับ",
    UiTextKey.CustomWordsTranslationLanguageLabel to "ภาษาแปล",
    UiTextKey.CustomWordsSaveButton to "บันทึก",
    UiTextKey.CustomWordsCancelButton to "ยกเลิก",

    // Language detection
    UiTextKey.LanguageDetectAuto to "ตรวจจับอัตโนมัติ",
    UiTextKey.LanguageDetectDetecting to "กำลังตรวจจับ...",
    UiTextKey.LanguageDetectedTemplate to "ตรวจจับได้: {language}",
    UiTextKey.LanguageDetectFailed to "ตรวจจับไม่สำเร็จ",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "สแกนข้อความจากรูป",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ คำเตือน: การรู้จำตัวอักษรอาจไม่สมบูรณ์ ตรวจสอบข้อความที่รู้จำอีกครั้ง" +
            "รองรับ Latin (อังกฤษ ฯลฯ), จีน, ญี่ปุ่น, และเกาหลี",
    UiTextKey.ImageRecognitionScanning to "กำลังสแกนข้อความ...",
    UiTextKey.ImageRecognitionSuccess to "รู้จำข้อความสำเร็จ",

    // Cache
    UiTextKey.CacheClearButton to "ล้างแคช",
    UiTextKey.CacheClearSuccess to "ล้างแคชแล้ว",
    UiTextKey.CacheStatsTemplate to "แคช: {count} คำแปลที่บันทึก",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "ธีมอัตโนมัติ",
    UiTextKey.SettingsAutoThemeDesc to "สลับสว่าง/มืดตามเวลาอัตโนมัติ",
    UiTextKey.SettingsAutoThemeEnabled to "เปิด",
    UiTextKey.SettingsAutoThemeDisabled to "ปิด",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "โหมดมืดเริ่ม:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "โหมดสว่างเริ่ม:",
    UiTextKey.SettingsAutoThemePreview to "ธีมจะสลับอัตโนมัติตามเวลาที่ตั้งไว้",

    // Offline mode
    UiTextKey.OfflineModeTitle to "โหมดออฟไลน์",
    UiTextKey.OfflineModeMessage to "คุณออฟไลน์อยู่ ข้อมูลที่บันทึกจะแสดง",
    UiTextKey.OfflineModeRetry to "ลองเชื่อมต่อใหม่",
    UiTextKey.OfflineDataCached to "ข้อมูลที่บันทึกพร้อมใช้",
    UiTextKey.OfflineSyncPending to "การเปลี่ยนแปลงจะซิงค์เมื่อออนไลน์",

    // Image capture
    UiTextKey.ImageSourceTitle to "เลือกแหล่งรูป",
    UiTextKey.ImageSourceCamera to "ถ่ายรูป",
    UiTextKey.ImageSourceGallery to "เลือกจากแกลเลอรี",
    UiTextKey.ImageSourceCancel to "ยกเลิก",
    UiTextKey.CameraCaptureContentDesc to "ถ่ายรูป",

    // Friends
    UiTextKey.FriendsTitle to "เพื่อน",
    UiTextKey.FriendsMenuButton to "เพื่อน",
    UiTextKey.FriendsAddButton to "เพิ่มเพื่อน",
    UiTextKey.FriendsSearchTitle to "ค้นหาผู้ใช้",
    UiTextKey.FriendsSearchPlaceholder to "ชื่อผู้ใช้หรือ ID...",
    UiTextKey.FriendsSearchMinChars to "กรอกอย่างน้อย 2 ตัวอักษร",
    UiTextKey.FriendsSearchNoResults to "ไม่พบผู้ใช้",
    UiTextKey.FriendsListEmpty to "เพิ่มเพื่อนเพื่อแชทและแชร์สื่อ",
    UiTextKey.FriendsRequestsSection to "คำขอเป็นเพื่อน ({count})",
    UiTextKey.FriendsSectionTitle to "เพื่อน ({count})",
    UiTextKey.FriendsAcceptButton to "รับ",
    UiTextKey.FriendsRejectButton to "ปฏิเสธ",
    UiTextKey.FriendsRemoveButton to "ลบ",
    UiTextKey.FriendsRemoveDialogTitle to "ลบเพื่อน",
    UiTextKey.FriendsRemoveDialogMessage to "ลบ {username} ออกจากรายชื่อเพื่อน?",
    UiTextKey.FriendsSendRequestButton to "เพิ่ม",
    UiTextKey.FriendsRequestSentSuccess to "ส่งคำขอเป็นเพื่อนแล้ว!",
    UiTextKey.FriendsRequestAcceptedSuccess to "รับคำขอแล้ว!",
    UiTextKey.FriendsRequestRejectedSuccess to "ปฏิเสธคำขอแล้ว",
    UiTextKey.FriendsRemovedSuccess to "ลบเพื่อนแล้ว",
    UiTextKey.FriendsRequestFailed to "ส่งไม่สำเร็จ",
    UiTextKey.FriendsCloseButton to "ปิด",
    UiTextKey.FriendsCancelButton to "ยกเลิก",
    UiTextKey.FriendsRemoveConfirm to "ลบ",
    UiTextKey.FriendsNewRequestsTemplate to "คุณมี {count} คำขอเป็นเพื่อนใหม่!",
    UiTextKey.FriendsSentRequestsSection to "คำขอที่ส่ง ({count})",
    UiTextKey.FriendsPendingStatus to "รอดำเนินการ",
    UiTextKey.FriendsCancelRequestButton to "ยกเลิกคำขอ",
    UiTextKey.FriendsUnreadMessageDesc to "ส่งข้อความ",
    UiTextKey.FriendsDeleteModeButton to "ลบเพื่อน",
    UiTextKey.FriendsDeleteSelectedButton to "ลบที่เลือก",
    UiTextKey.FriendsDeleteMultipleTitle to "ลบเพื่อน",
    UiTextKey.FriendsDeleteMultipleMessage to "ลบ {count} เพื่อนที่เลือก?",
    UiTextKey.FriendsSearchMinChars3 to "กรอกอย่างน้อย 3 ตัวอักษรสำหรับชื่อ",
    UiTextKey.FriendsSearchByUserIdHint to "หรือค้นหาตรงด้วย ID ผู้ใช้",
    UiTextKey.FriendsStatusAlreadyFriends to "เป็นเพื่อนแล้ว",
    UiTextKey.FriendsStatusRequestSent to "ส่งคำขอแล้ว — รอตอบรับ",
    UiTextKey.FriendsStatusRequestReceived to "ผู้ใช้นี้ส่งคำขอให้คุณ",

    // Chat
    UiTextKey.ChatTitle to "แชทกับ {username}",
    UiTextKey.ChatInputPlaceholder to "พิมพ์ข้อความ...",
    UiTextKey.ChatSendButton to "ส่ง",
    UiTextKey.ChatEmpty to "ยังไม่มีข้อความ เริ่มสนทนาเลย!",
    UiTextKey.ChatMessageSent to "ส่งข้อความแล้ว",
    UiTextKey.ChatMessageFailed to "ส่งไม่สำเร็จ",
    UiTextKey.ChatMarkingRead to "กำลังอ่าน...",
    UiTextKey.ChatLoadingMessages to "กำลังโหลดข้อความ...",
    UiTextKey.ChatToday to "วันนี้",
    UiTextKey.ChatYesterday to "เมื่อวาน",
    UiTextKey.ChatUnreadBadge to "{count} ยังไม่อ่าน",
    UiTextKey.ChatTranslateButton to "แปล",
    UiTextKey.ChatTranslateDialogTitle to "แปลแชท",
    UiTextKey.ChatTranslateDialogMessage to "แปลข้อความเพื่อนเป็นภาษาของคุณ? ภาษาแต่ละข้อความจะถูกตรวจจับและแปล",
    UiTextKey.ChatTranslateConfirm to "แปลทั้งหมด",
    UiTextKey.ChatTranslating to "กำลังแปลข้อความ...",
    UiTextKey.ChatTranslated to "แปลข้อความแล้ว",
    UiTextKey.ChatShowOriginal to "แสดงต้นฉบับ",
    UiTextKey.ChatShowTranslation to "แสดงคำแปล",
    UiTextKey.ChatTranslateFailed to "แปลไม่สำเร็จ",
    UiTextKey.ChatTranslatedLabel to "แปลแล้ว",

    // Sharing
    UiTextKey.ShareTitle to "แชร์",
    UiTextKey.ShareInboxTitle to "กล่องแชร์",
    UiTextKey.ShareInboxEmpty to "ยังไม่มีการแชร์ เพื่อนสามารถแชร์คำและสื่อ!",
    UiTextKey.ShareWordButton to "แชร์คำ",
    UiTextKey.ShareMaterialButton to "แชร์สื่อ",
    UiTextKey.ShareSelectFriendTitle to "เลือกเพื่อน",
    UiTextKey.ShareSelectFriendMessage to "เลือกเพื่อนเพื่อแชร์:",
    UiTextKey.ShareSuccess to "แชร์สำเร็จ!",
    UiTextKey.ShareFailed to "แชร์ไม่สำเร็จ",
    UiTextKey.ShareWordWith to "แชร์คำกับ {username}",
    UiTextKey.ShareMaterialWith to "แชร์สื่อกับ {username}",
    UiTextKey.ShareAcceptButton to "รับ",
    UiTextKey.ShareDismissButton to "ข้าม",
    UiTextKey.ShareAccepted to "เพิ่มในคอลเลกชันแล้ว",
    UiTextKey.ShareDismissed to "ข้ามรายการแล้ว",
    UiTextKey.ShareActionFailed to "ดำเนินการไม่สำเร็จ",
    UiTextKey.ShareTypeWord to "คำ",
    UiTextKey.ShareTypeLearningSheet to "สื่อการเรียน",
    UiTextKey.ShareReceivedFrom to "จาก: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} รายการใหม่!",
    UiTextKey.ShareViewFullMaterial to "กด \"ดู\" เพื่อดูสื่อทั้งหมด",
    UiTextKey.ShareDeleteItemTitle to "ลบรายการ",
    UiTextKey.ShareDeleteItemMessage to "ลบรายการแชร์นี้? ไม่สามารถย้อนกลับ",
    UiTextKey.ShareDeleteButton to "ลบ",
    UiTextKey.ShareViewButton to "ดู",
    UiTextKey.ShareItemNotFound to "ไม่พบรายการ",
    UiTextKey.ShareNoContent to "ไม่มีเนื้อหาในสื่อ",
    UiTextKey.ShareSaveToSelf to "บันทึกในกล่องของฉัน",
    UiTextKey.ShareSavedToSelf to "บันทึกในกล่องของคุณแล้ว!",

    // My profile
    UiTextKey.MyProfileTitle to "โปรไฟล์ของฉัน",
    UiTextKey.MyProfileUserId to "ID ผู้ใช้",
    UiTextKey.MyProfileUsername to "ชื่อผู้ใช้",
    UiTextKey.MyProfileDisplayName to "ชื่อที่แสดง",
    UiTextKey.MyProfileCopyUserId to "คัดลอก ID",
    UiTextKey.MyProfileCopyUsername to "คัดลอกชื่อ",
    UiTextKey.MyProfileShare to "แชร์โปรไฟล์",
    UiTextKey.MyProfileCopied to "คัดลอกแล้ว!",
    UiTextKey.MyProfileLanguages to "ภาษา",
    UiTextKey.MyProfilePrimaryLanguage to "ภาษาหลัก",
    UiTextKey.MyProfileLearningLanguages to "ภาษาที่กำลังเรียน",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "หน้าเพื่อน",
    UiTextKey.FriendsInfoMessage to "• ดึงลงเพื่อรีเฟรชรายชื่อ คำขอ และสถานะ\n" +
            "• กดการ์ดเพื่อเปิดแชท\n" +
            "• จุดแดง (●) สำหรับข้อความยังไม่อ่าน ✓✓ เพื่ออ่านทั้งหมด\n" +
            "• 📥 สำหรับกล่องแชร์ ✓✓ เพื่อล้างจุด\n" +
            "• 🚫 เพื่อบล็อก — เพื่อนถูกลบและไม่สามารถติดต่อ\n" +
            "• บล็อกลบประวัติแชทด้วย\n" +
            "• ไอคอนถังขยะสำหรับโหมดลบ\n" +
            "• ลบเพื่อนจะลบข้อความทั้งหมด\n" +
            "• ไอคอนค้นหาเพื่อค้นหาตามชื่อหรือ ID\n" +
            "• แจ้งเตือนปิดเริ่มต้น — เปิดในตั้งค่า\n",
    UiTextKey.FriendsEmptyTitle to "ยังไม่มีเพื่อน",
    UiTextKey.FriendsEmptyMessage to "กด \"เพิ่มเพื่อน\" เพื่อค้นหาตามชื่อหรือ ID\n",
    UiTextKey.FriendsInfoGotItButton to "เข้าใจ",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "หน้าการเรียน",
    UiTextKey.LearningInfoMessage to "• ดึงเพื่อรีเฟรชรายการ\n" +
            "• แต่ละการ์ดแสดงภาษาและจำนวน\n" +
            "• \"สร้าง\" สำหรับสื่อ (ฟรีครั้งแรก)\n" +
            "• สร้างใหม่ต้องการ 5 รายการเพิ่ม\n" +
            "• ปุ่มสื่อเปิดเนื้อหาที่สร้าง\n" +
            "• หลังสร้างสื่อ ทำแบบทดสอบได้",
    UiTextKey.LearningEmptyTitle to "ยังไม่มีประวัติแปล",
    UiTextKey.LearningEmptyMessage to "เริ่มแปลเพื่อสร้างรายการ\n" +
            "สื่อสร้างจากประวัติ\n" +
            "หลังแปล ดึงเพื่อรีเฟรช",
    UiTextKey.LearningInfoGotItButton to "เข้าใจ",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "หน้าคลังคำ",
    UiTextKey.WordBankInfoMessage to "• ดึงเพื่อรีเฟรชรายชื่อภาษา\n" +
            "• เลือกภาษาเพื่อดูหรือสร้าง\n" +
            "• คลังคำสร้างจากประวัติ\n" +
            "• อัพเดทต้องการ 20 รายการเพิ่ม\n" +
            "• เพิ่มคำกำหนดเองด้วยมือ\n" +
            "• แชร์คำกับเพื่อน",
    UiTextKey.WordBankInfoGotItButton to "เข้าใจ",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "กล่องแชร์",
    UiTextKey.ShareInboxInfoMessage to "• ดึงเพื่อรีเฟรชกล่อง\n" +
            "• รายการที่เพื่อนแชร์จะแสดงที่นี่\n" +
            "• รับหรือข้ามคำ\n" +
            "• \"ดู\" สำหรับสื่อและแบบทดสอบ\n" +
            "• จุดแดง (●) สำหรับรายการใหม่/ยังไม่อ่าน\n" +
            "• ยืนยันก่อนข้ามคำแชร์",
    UiTextKey.ShareInboxInfoGotItButton to "เข้าใจ",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "การมองเห็นโปรไฟล์",
    UiTextKey.MyProfileVisibilityPublic to "สาธารณะ",
    UiTextKey.MyProfileVisibilityPrivate to "ส่วนตัว",
    UiTextKey.MyProfileVisibilityDescription to "สาธารณะ: ทุกคนค้นหาและเพิ่มได้\nส่วนตัว: ไม่แสดงในการค้นหา",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "ข้ามคำ",
    UiTextKey.ShareDismissWordMessage to "ข้ามคำแชร์นี้? ไม่สามารถย้อนกลับ",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "ภาษา: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "ปิด",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "เชื่อมต่อหรือรอดำเนินการแล้ว",
    UiTextKey.AccessibilityNewMessages to "ข้อความใหม่",
    UiTextKey.AccessibilityNewReleasesIcon to "ตัวบ่งชี้รายการใหม่",
    UiTextKey.AccessibilitySuccessIcon to "สำเร็จ",
    UiTextKey.AccessibilityErrorIcon to "ข้อผิดพลาด",
    UiTextKey.AccessibilitySharedItemTypeIcon to "ประเภทรายการแชร์",
    UiTextKey.AccessibilityAddCustomWords to "เพิ่มคำกำหนดเอง",
    UiTextKey.AccessibilityWordBankExists to "คลังคำมีอยู่",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "PH.ฟีดแบ็ก",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "ตั้งค่าแจ้งเตือน",
    UiTextKey.FriendsNotifSettingsTitle to "ตั้งค่าแจ้งเตือน",
    UiTextKey.FriendsNotifNewMessages to "ข้อความแชทใหม่",
    UiTextKey.FriendsNotifFriendRequests to "คำขอเป็นเพื่อนที่ได้รับ",
    UiTextKey.FriendsNotifRequestAccepted to "คำขอเป็นเพื่อนที่ตอบรับ",
    UiTextKey.FriendsNotifSharedInbox to "รายการแชร์ใหม่",
    UiTextKey.FriendsNotifCloseButton to "เสร็จ",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "ป้ายในแอป (จุดแดง)",
    UiTextKey.InAppBadgeMessages to "ป้ายข้อความยังไม่อ่าน",
    UiTextKey.InAppBadgeFriendRequests to "ป้ายคำขอเป็นเพื่อน",
    UiTextKey.InAppBadgeSharedInbox to "ป้ายกล่องแชร์ยังไม่อ่าน",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "เข้าสู่ระบบเพื่อดำเนินการต่อ",
    UiTextKey.ErrorSaveFailedRetry to "บันทึกไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorLoadFailedRetry to "โหลดไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorNetworkRetry to "ข้อผิดพลาดเครือข่าย ตรวจสอบการเชื่อมต่อ",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "ต้องการอีก {needed} คำแปลเพื่อสร้างสื่อ",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "สลับไปสนทนาต่อเนื่อง →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "ล้างแชท",
    UiTextKey.ChatClearConversationTitle to "ล้างแชท",
    UiTextKey.ChatClearConversationMessage to "ซ่อนข้อความทั้งหมด? จะยังซ่อนเมื่อเปิดใหม่ คนอื่นไม่ได้รับผลกระทบ",
    UiTextKey.ChatClearConversationConfirm to "ล้างทั้งหมด",
    UiTextKey.ChatClearConversationSuccess to "ล้างแชทแล้ว",

    // Block user
    UiTextKey.BlockUserButton to "บล็อก",
    UiTextKey.BlockUserTitle to "บล็อกผู้ใช้นี้?",
    UiTextKey.BlockUserMessage to "บล็อก {username}? จะถูกลบจากรายชื่อและไม่สามารถติดต่อ",
    UiTextKey.BlockUserConfirm to "บล็อก",
    UiTextKey.BlockUserSuccess to "บล็อกและลบจากรายชื่อแล้ว",
    UiTextKey.BlockedUsersTitle to "ผู้ใช้ที่ถูกบล็อก",
    UiTextKey.BlockedUsersEmpty to "ไม่มีผู้ใช้ที่ถูกบล็อก",
    UiTextKey.UnblockUserButton to "เลิกบล็อก",
    UiTextKey.UnblockUserTitle to "เลิกบล็อก?",
    UiTextKey.UnblockUserMessage to "เลิกบล็อก {username}? สามารถส่งคำขอได้อีกครั้ง",
    UiTextKey.UnblockUserSuccess to "เลิกบล็อกแล้ว",
    UiTextKey.BlockedUsersManageButton to "จัดการบล็อก",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "บันทึกคำขอ (ไม่บังคับ)",
    UiTextKey.FriendsRequestNotePlaceholder to "เพิ่มบันทึกสั้นๆ...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "สื่อพร้อมแล้ว! กดเพื่อเปิด",
    UiTextKey.GenerationBannerWordBank to "คลังคำพร้อมแล้ว! กดเพื่อดู",
    UiTextKey.GenerationBannerQuiz to "แบบทดสอบพร้อม! กดเพื่อเริ่ม",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "แจ้งเตือน",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "จีน (ตัวเต็ม)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "ระบบเพื่อน",
    UiTextKey.HelpFriendSystemBody to "• ค้นหาเพื่อนตามชื่อหรือ ID\n" +
            "• ส่ง รับ หรือปฏิเสธคำขอ\n" +
            "• แชทโดยตรงพร้อมแปล\n" +
            "• แชร์คำและสื่อการเรียน\n" +
            "• จัดการเนื้อหาแชร์ในกล่อง\n" +
            "• จุดแดง (●) สำหรับเนื้อหาใหม่/ยังไม่อ่าน\n" +
            "• ดึงเพื่อรีเฟรช",
    UiTextKey.HelpProfileVisibilityTitle to "การมองเห็นโปรไฟล์",
    UiTextKey.HelpProfileVisibilityBody to "• โปรไฟล์สาธารณะหรือส่วนตัวในตั้งค่า\n" +
            "• สาธารณะ: ทุกคนค้นหาเจอ\n" +
            "• ส่วนตัว: ไม่แสดงในค้นหา\n" +
            "• ใช้ส่วนตัว: แชร์ ID เพื่อให้เพิ่มได้",
    UiTextKey.HelpColorPalettesTitle to "ธีมและเหรียญ",
    UiTextKey.HelpColorPalettesBody to "• 1 ธีมฟรี: ท้องฟ้าสีฟ้า (ค่าเริ่มต้น)\n" +
            "• 10 ธีมปลดล็อก 10 เหรียญต่อธีม\n" +
            "• รับเหรียญจากแบบทดสอบ\n" +
            "• เหรียญใช้ซื้อธีมและขยายประวัติ\n" +
            "• ธีมอัตโนมัติ: สว่าง 6-18, มืด 18-6",
    UiTextKey.HelpPrivacyTitle to "ความเป็นส่วนตัวและข้อมูล",
    UiTextKey.HelpPrivacyBody to "• เสียงใช้รู้จำเท่านั้น ไม่เก็บถาวร\n" +
            "• OCR ประมวลผลบนอุปกรณ์ (ปลอดภัย)\n" +
            "• บัญชีและข้อมูลลบได้ทุกเมื่อ\n" +
            "• โหมดส่วนตัว: ไม่แสดงในค้นหา\n" +
            "• ข้อมูลทั้งหมดซิงค์ปลอดภัยผ่าน Firebase",
    UiTextKey.HelpAppVersionTitle to "เวอร์ชันแอป",
    UiTextKey.HelpAppVersionNotes to "• จำกัดประวัติ: 30 ถึง 60 รายการ (ขยายด้วยเหรียญ)\n" +
            "• ชื่อผู้ใช้ไม่ซ้ำ — เปลี่ยนชื่อปล่อยชื่อเก่า\n" +
            "• ออกจากระบบอัตโนมัติเมื่ออัพเดทความปลอดภัย\n" +
            "• คำแปลทั้งหมดโดย Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "แปลทันที",
    UiTextKey.OnboardingPage1Desc to "แปลด่วนสำหรับประโยคสั้น สนทนาต่อเนื่องสำหรับบทสนทนาสองทาง",
    UiTextKey.OnboardingPage2Title to "เรียนคำศัพท์",
    UiTextKey.OnboardingPage2Desc to "สร้างรายการคำศัพท์และแบบทดสอบจากประวัติ",
    UiTextKey.OnboardingPage3Title to "เชื่อมต่อกับเพื่อน",
    UiTextKey.OnboardingPage3Desc to "แชท แชร์คำ และเรียนรู้ด้วยกัน",
    UiTextKey.OnboardingSkipButton to "ข้าม",
    UiTextKey.OnboardingNextButton to "ถัดไป",
    UiTextKey.OnboardingGetStartedButton to "เริ่มเลย",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 ยินดีต้อนรับกลับ, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "ผู้ใช้:",
    UiTextKey.ChatUserIdLabel to "ID ผู้ใช้:",
    UiTextKey.ChatLearningLabel to "กำลังเรียน:",
    UiTextKey.ChatBlockedMessage to "ไม่สามารถส่งข้อความให้ผู้ใช้นี้",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "ค้นหา",
    UiTextKey.CustomWordsEmptyState to "ยังไม่มีคำกำหนดเอง",
    UiTextKey.CustomWordsEmptyHint to "กด + เพื่อเพิ่มคำ",
    UiTextKey.CustomWordsNoSearchResults to "ไม่มีคำที่ตรงกัน",
    UiTextKey.AddCustomWordHintTemplate to "กรอกคำใน {from} และคำแปลใน {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} รายการ",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "อีเมล: {email}",
    UiTextKey.ProfileUsernameHintFull to "ชื่อผู้ใช้สำหรับเพื่อน (3–20 ตัว ตัวอักษร/ตัวเลข/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "ไม่มีตัวเลือกเสียงสำหรับภาษานี้",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "แอปอัพเดทแล้ว กรุณาเข้าสู่ระบบใหม่",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "ถึงจำนวนรายการโปรดสูงสุด",
    UiTextKey.FavoritesLimitMessage to "สูงสุด 20 รายการโปรด ลบรายการเพื่อเพิ่มใหม่",
    UiTextKey.FavoritesLimitGotIt to "เข้าใจ",
    UiTextKey.FavoritesInfoTitle to "ข้อมูลรายการโปรด",
    UiTextKey.FavoritesInfoMessage to "สูงสุด 20 รายการโปรด (รายการและเซสชัน) จำกัดเพื่อลดภาระฐานข้อมูล ลบรายการเพื่อเพิ่มใหม่",
    UiTextKey.FavoritesInfoGotIt to "เข้าใจ",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "ไม่สามารถเปลี่ยน",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "ภาษาหลักเปลี่ยนได้ทุก 30 วัน เหลืออีก {days} วัน",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "ภาษาหลักเปลี่ยนได้ทุก 30 วัน เหลืออีก {hours} ชั่วโมง",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "ยืนยันเปลี่ยนภาษา",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "เปลี่ยนภาษาหลักจะไม่สามารถเปลี่ยนได้ใน 30 วัน ดำเนินการต่อ?",

    // Bottom navigation
    UiTextKey.NavHome to "หน้าหลัก",
    UiTextKey.NavTranslate to "แปล",
    UiTextKey.NavLearn to "เรียน",
    UiTextKey.NavFriends to "เพื่อน",
    UiTextKey.NavSettings to "ตั้งค่า",

    // Permissions
    UiTextKey.CameraPermissionTitle to "ต้องการสิทธิ์กล้อง",
    UiTextKey.CameraPermissionMessage to "อนุญาตกล้องเพื่อรู้จำข้อความ",
    UiTextKey.CameraPermissionGrant to "อนุญาต",
    UiTextKey.MicPermissionMessage to "ต้องการสิทธิ์ไมค์เพื่อรู้จำเสียง",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "ลบ {count} รายการที่เลือก? ไม่สามารถย้อนกลับ",
    UiTextKey.WordBankDeleteConfirm to "ลบ \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "รับทั้งหมด",
    UiTextKey.FriendsRejectAllButton to "ปฏิเสธทั้งหมด",
    UiTextKey.ChatBlockedCannotSend to "ไม่สามารถส่งข้อความ",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "ปลดล็อก {name}?",
    UiTextKey.ShopUnlockCost to "ราคา: {cost} เหรียญ",
    UiTextKey.ShopYourCoins to "เหรียญของฉัน: {coins}",
    UiTextKey.ShopUnlockButton to "ปลดล็อก",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "ภาษาหลัก",
    UiTextKey.HelpPrimaryLanguageBody to "• ภาษาหลักใช้สำหรับคำอธิบายการเรียน\n" +
            "• เปลี่ยนได้ทุก 30 วันเพื่อความต่อเนื่อง\n" +
            "• เปลี่ยนในตั้งค่า\n" +
            "• ตั้งค่ารวมสำหรับทุกหน้า",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 เคล็ดลับ: เพื่อรู้จำที่ดีขึ้น ตั้ง \"ภาษาต้นทาง\" เป็นภาษาของข้อความที่จะสแกน",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "ไม่สามารถเปลี่ยน",
    UiTextKey.SettingsUsernameCooldownMessage to "ชื่อผู้ใช้เปลี่ยนได้ทุก 30 วัน เหลืออีก {days} วัน",
    UiTextKey.SettingsUsernameCooldownMessageHours to "ชื่อผู้ใช้เปลี่ยนได้ทุก 30 วัน เหลืออีก {hours} ชั่วโมง",
    UiTextKey.SettingsUsernameConfirmTitle to "ยืนยันเปลี่ยนชื่อ",
    UiTextKey.SettingsUsernameConfirmMessage to "เปลี่ยนชื่อผู้ใช้จะไม่สามารถเปลี่ยนได้ใน 30 วัน ดำเนินการต่อ?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "ไม่มีการเชื่อมต่ออินเทอร์เน็ต ตรวจสอบการเชื่อมต่อ",
    UiTextKey.ErrorPermissionDenied to "คุณไม่มีสิทธิ์สำหรับการกระทำนี้",
    UiTextKey.ErrorSessionExpired to "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่",
    UiTextKey.ErrorItemNotFound to "ไม่พบรายการ อาจถูกลบไปแล้ว",
    UiTextKey.ErrorAccessDenied to "ปฏิเสธการเข้าถึง",
    UiTextKey.ErrorAlreadyFriends to "เป็นเพื่อนกันแล้ว",
    UiTextKey.ErrorUserBlocked to "ไม่อนุญาต ผู้ใช้อาจถูกบล็อก",
    UiTextKey.ErrorRequestNotFound to "คำขอนี้ไม่มีอยู่แล้ว",
    UiTextKey.ErrorRequestAlreadyHandled to "คำขอนี้ถูกจัดการแล้ว",
    UiTextKey.ErrorNotAuthorized to "คุณไม่ได้รับอนุญาตให้ดำเนินการนี้",
    UiTextKey.ErrorRateLimited to "คำขอมากเกินไป ลองใหม่ภายหลัง",
    UiTextKey.ErrorInvalidInput to "ข้อมูลไม่ถูกต้อง ตรวจสอบแล้วลองใหม่",
    UiTextKey.ErrorOperationNotAllowed to "การดำเนินการนี้ไม่ได้รับอนุญาตขณะนี้",
    UiTextKey.ErrorTimeout to "หมดเวลา ลองใหม่",
    UiTextKey.ErrorSendMessageFailed to "ส่งข้อความไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorFriendRequestSent to "ส่งคำขอเป็นเพื่อนแล้ว!",
    UiTextKey.ErrorFriendRequestFailed to "ส่งคำขอไม่สำเร็จ",
    UiTextKey.ErrorFriendRemoved to "ลบเพื่อนแล้ว",
    UiTextKey.ErrorFriendRemoveFailed to "ลบไม่สำเร็จ ตรวจสอบการเชื่อมต่อ",
    UiTextKey.ErrorBlockSuccess to "บล็อกผู้ใช้แล้ว",
    UiTextKey.ErrorBlockFailed to "บล็อกไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorUnblockSuccess to "เลิกบล็อกแล้ว",
    UiTextKey.ErrorUnblockFailed to "เลิกบล็อกไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorAcceptRequestSuccess to "รับคำขอแล้ว!",
    UiTextKey.ErrorAcceptRequestFailed to "รับคำขอไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorRejectRequestSuccess to "ปฏิเสธคำขอแล้ว",
    UiTextKey.ErrorRejectRequestFailed to "ปฏิเสธไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorOfflineMessage to "คุณออฟไลน์อยู่ บางฟีเจอร์อาจไม่พร้อมใช้",
    UiTextKey.ErrorChatDeletionFailed to "ล้างแชทไม่สำเร็จ ลองใหม่",
    UiTextKey.ErrorGenericRetry to "เกิดข้อผิดพลาด ลองใหม่",
)
