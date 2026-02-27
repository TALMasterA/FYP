package com.example.fyp.model.ui

/**
 * Combined BaseUiTexts that merges Core and Screen-specific strings.
 * CRITICAL: Order must exactly match UiTextKey enum order!
 */
val BaseUiTexts: List<String>
    get() = CoreUiTexts + ScreenUiTexts

/**
 * Predefined language name translations to avoid translation API errors.
 * Map: UI Language Code -> (Language Key -> Translated Name)
 *
 */
val LanguageNameTranslations: Map<String, Map<UiTextKey, String>> = mapOf(
    "id-ID" to mapOf(
        UiTextKey.LangEnUs to "Inggris",
        UiTextKey.LangZhTw to "Tionghoa Tradisional",
        UiTextKey.LangZhHk to "Kanton",
        UiTextKey.LangJaJp to "Jepang",
        UiTextKey.LangZhCn to "Tionghoa Sederhana",
        UiTextKey.LangDeDe to "Jerman",
        UiTextKey.LangKoKr to "Korea",
        UiTextKey.LangEsEs to "Spanyol",
        UiTextKey.LangIdId to "Indonesia",
        UiTextKey.LangViVn to "Vietnam",
        UiTextKey.LangThTh to "Thai",
        UiTextKey.LangFilPh to "Filipina",
        UiTextKey.LangMsMy to "Melayu",
        UiTextKey.LangPtBr to "Portugis",
        UiTextKey.LangItIt to "Italia",
        UiTextKey.LangRuRu to "Rusia"
    ),
    "zh-HK" to mapOf(
        UiTextKey.LangEnUs to "英文",
        UiTextKey.LangZhTw to "繁體中文",
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
        UiTextKey.LangRuRu to "俄文"
    ),
    "zh-CN" to mapOf(
        UiTextKey.LangEnUs to "英语",
        UiTextKey.LangZhTw to "繁体中文",
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
        UiTextKey.LangRuRu to "俄语"
    ),
    "ja-JP" to mapOf(
        UiTextKey.LangEnUs to "英語",
        UiTextKey.LangZhTw to "繁体字中国語",
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
        UiTextKey.LangRuRu to "ロシア語"
    ),
    "ko-KR" to mapOf(
        UiTextKey.LangEnUs to "영어",
        UiTextKey.LangZhTw to "번체 중국어",
        UiTextKey.LangZhHk to "광둥어",
        UiTextKey.LangJaJp to "일본어",
        UiTextKey.LangZhCn to "중국어 (간체)",
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
        UiTextKey.LangRuRu to "러시아어"
    ),
    "vi-VN" to mapOf(
        UiTextKey.LangEnUs to "Tiếng Anh",
        UiTextKey.LangZhTw to "Tiếng Trung Phồn thể",
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
        UiTextKey.LangFilPh to "Tiếng Philippines",
        UiTextKey.LangMsMy to "Tiếng Mã Lai",
        UiTextKey.LangPtBr to "Tiếng Bồ Đào Nha",
        UiTextKey.LangItIt to "Tiếng Ý",
        UiTextKey.LangRuRu to "Tiếng Nga"
    ),
    "th-TH" to mapOf(
        UiTextKey.LangEnUs to "ภาษาอังกฤษ",
        UiTextKey.LangZhTw to "ภาษาจีนตัวเต็ม",
        UiTextKey.LangZhHk to "ภาษากวางตุ้ง",
        UiTextKey.LangJaJp to "ภาษาญี่ปุ่น",
        UiTextKey.LangZhCn to "ภาษาจีนตัวย่อ",
        UiTextKey.LangFrFr to "ภาษาฝรั่งเศส",
        UiTextKey.LangDeDe to "ภาษาเยอรมัน",
        UiTextKey.LangKoKr to "ภาษาเกาหลี",
        UiTextKey.LangEsEs to "ภาษาสเปน",
        UiTextKey.LangIdId to "ภาษาอินโดนีเซีย",
        UiTextKey.LangViVn to "ภาษาเวียดนาม",
        UiTextKey.LangThTh to "ภาษาไทย",
        UiTextKey.LangFilPh to "ภาษาฟิลิปปินส์",
        UiTextKey.LangMsMy to "ภาษามาเลย์",
        UiTextKey.LangPtBr to "ภาษาโปรตุเกส",
        UiTextKey.LangItIt to "ภาษาอิตาลี",
        UiTextKey.LangRuRu to "ภาษารัสเซีย"
    ),
    "fr-FR" to mapOf(
        UiTextKey.LangEnUs to "Anglais",
        UiTextKey.LangZhTw to "Chinois traditionnel",
        UiTextKey.LangZhHk to "Cantonais",
        UiTextKey.LangJaJp to "Japonais",
        UiTextKey.LangZhCn to "Chinois simplifié",
        UiTextKey.LangDeDe to "Allemand",
        UiTextKey.LangKoKr to "Coréen",
        UiTextKey.LangEsEs to "Espagnol",
        UiTextKey.LangIdId to "Indonésien",
        UiTextKey.LangViVn to "Vietnamien",
        UiTextKey.LangThTh to "Thaï",
        UiTextKey.LangFilPh to "Philippin",
        UiTextKey.LangMsMy to "Malais",
        UiTextKey.LangPtBr to "Portugais",
        UiTextKey.LangItIt to "Italien",
        UiTextKey.LangRuRu to "Russe"
    ),
    "de-DE" to mapOf(
        UiTextKey.LangEnUs to "Englisch",
        UiTextKey.LangZhTw to "Traditionelles Chinesisch",
        UiTextKey.LangZhHk to "Kantonesisch",
        UiTextKey.LangJaJp to "Japanisch",
        UiTextKey.LangZhCn to "Vereinfachtes Chinesisch",
        UiTextKey.LangDeDe to "Deutsch",
        UiTextKey.LangKoKr to "Koreanisch",
        UiTextKey.LangEsEs to "Spanisch",
        UiTextKey.LangIdId to "Indonesisch",
        UiTextKey.LangViVn to "Vietnamesisch",
        UiTextKey.LangThTh to "Thailändisch",
        UiTextKey.LangFilPh to "Filipino",
        UiTextKey.LangMsMy to "Malaiisch",
        UiTextKey.LangPtBr to "Portugiesisch",
        UiTextKey.LangItIt to "Italienisch",
        UiTextKey.LangRuRu to "Russisch"
    ),
    "es-ES" to mapOf(
        UiTextKey.LangEnUs to "Inglés",
        UiTextKey.LangZhTw to "Chino tradicional",
        UiTextKey.LangZhHk to "Cantonés",
        UiTextKey.LangJaJp to "Japonés",
        UiTextKey.LangZhCn to "Chino simplificado",
        UiTextKey.LangFrFr to "Francés",
        UiTextKey.LangDeDe to "Alemán",
        UiTextKey.LangKoKr to "Coreano",
        UiTextKey.LangEsEs to "Español",
        UiTextKey.LangIdId to "Indonesio",
        UiTextKey.LangViVn to "Vietnamita",
        UiTextKey.LangThTh to "Tailandés",
        UiTextKey.LangFilPh to "Filipino",
        UiTextKey.LangMsMy to "Malayo",
        UiTextKey.LangPtBr to "Portugués",
        UiTextKey.LangItIt to "Italiano",
        UiTextKey.LangRuRu to "Ruso"
    ),
    "pt-BR" to mapOf(
        UiTextKey.LangEnUs to "Inglês",
        UiTextKey.LangZhTw to "Chinês tradicional",
        UiTextKey.LangZhHk to "Cantonês",
        UiTextKey.LangJaJp to "Japonês",
        UiTextKey.LangZhCn to "Chinês simplificado",
        UiTextKey.LangFrFr to "Francês",
        UiTextKey.LangDeDe to "Alemão",
        UiTextKey.LangKoKr to "Coreano",
        UiTextKey.LangEsEs to "Espanhol",
        UiTextKey.LangIdId to "Indonésio",
        UiTextKey.LangViVn to "Vietnamita",
        UiTextKey.LangThTh to "Tailandês",
        UiTextKey.LangFilPh to "Filipino",
        UiTextKey.LangMsMy to "Malaio",
        UiTextKey.LangPtBr to "Português",
        UiTextKey.LangItIt to "Italiano",
        UiTextKey.LangRuRu to "Russo"
    ),
    "it-IT" to mapOf(
        UiTextKey.LangEnUs to "Inglese",
        UiTextKey.LangZhTw to "Cinese tradizionale",
        UiTextKey.LangZhHk to "Cantonese",
        UiTextKey.LangJaJp to "Giapponese",
        UiTextKey.LangZhCn to "Cinese semplificato",
        UiTextKey.LangFrFr to "Francese",
        UiTextKey.LangDeDe to "Tedesco",
        UiTextKey.LangKoKr to "Coreano",
        UiTextKey.LangEsEs to "Spagnolo",
        UiTextKey.LangIdId to "Indonesiano",
        UiTextKey.LangViVn to "Vietnamita",
        UiTextKey.LangThTh to "Tailandese",
        UiTextKey.LangFilPh to "Filippino",
        UiTextKey.LangMsMy to "Malese",
        UiTextKey.LangPtBr to "Portoghese",
        UiTextKey.LangItIt to "Italiano",
        UiTextKey.LangRuRu to "Russo"
    ),
    "ru-RU" to mapOf(
        UiTextKey.LangEnUs to "Английский",
        UiTextKey.LangZhTw to "Традиционный китайский",
        UiTextKey.LangZhHk to "Кантонский",
        UiTextKey.LangJaJp to "Японский",
        UiTextKey.LangZhCn to "Упрощённый китайский",
        UiTextKey.LangFrFr to "Французский",
        UiTextKey.LangDeDe to "Немецкий",
        UiTextKey.LangKoKr to "Корейский",
        UiTextKey.LangEsEs to "Испанский",
        UiTextKey.LangIdId to "Индонезийский",
        UiTextKey.LangViVn to "Вьетнамский",
        UiTextKey.LangThTh to "Тайский",
        UiTextKey.LangFilPh to "Филиппинский",
        UiTextKey.LangMsMy to "Малайский",
        UiTextKey.LangPtBr to "Португальский",
        UiTextKey.LangItIt to "Итальянский",
        UiTextKey.LangRuRu to "Русский"
    ),
    "fil-PH" to mapOf(
        UiTextKey.LangEnUs to "Ingles",
        UiTextKey.LangZhTw to "Tradisyonal na Intsik",
        UiTextKey.LangZhHk to "Cantonese",
        UiTextKey.LangJaJp to "Hapon",
        UiTextKey.LangZhCn to "Simplified Chinese",
        UiTextKey.LangFrFr to "Pranses",
        UiTextKey.LangDeDe to "Aleman",
        UiTextKey.LangKoKr to "Koreano",
        UiTextKey.LangEsEs to "Espanyol",
        UiTextKey.LangIdId to "Indonesian",
        UiTextKey.LangViVn to "Vietnamese",
        UiTextKey.LangThTh to "Thai",
        UiTextKey.LangFilPh to "Filipino",
        UiTextKey.LangMsMy to "Malay",
        UiTextKey.LangPtBr to "Portuges",
        UiTextKey.LangItIt to "Italyano",
        UiTextKey.LangRuRu to "Ruso"
    ),
    "ms-MY" to mapOf(
        UiTextKey.LangEnUs to "Inggeris",
        UiTextKey.LangZhTw to "Cina Tradisional",
        UiTextKey.LangZhHk to "Kantonis",
        UiTextKey.LangJaJp to "Jepun",
        UiTextKey.LangZhCn to "Cina Ringkas",
        UiTextKey.LangDeDe to "Jerman",
        UiTextKey.LangKoKr to "Korea",
        UiTextKey.LangEsEs to "Sepanyol",
        UiTextKey.LangIdId to "Indonesia",
        UiTextKey.LangViVn to "Vietnam",
        UiTextKey.LangThTh to "Thai",
        UiTextKey.LangFilPh to "Filipina",
        UiTextKey.LangMsMy to "Melayu",
        UiTextKey.LangPtBr to "Portugis",
        UiTextKey.LangItIt to "Itali",
        UiTextKey.LangRuRu to "Rusia"
    ),
    "zh-TW" to mapOf(
        UiTextKey.LangEnUs to "英語",
        UiTextKey.LangZhTw to "繁體中文",
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
        UiTextKey.LangRuRu to "俄語"
    )
)

/**
 * Language name keys that should use predefined translations instead of API
 */
val LanguageNameKeys: Set<UiTextKey> = setOf(
    UiTextKey.LangEnUs,
    UiTextKey.LangZhTw,
    UiTextKey.LangZhHk,
    UiTextKey.LangJaJp,
    UiTextKey.LangZhCn,
    UiTextKey.LangFrFr,
    UiTextKey.LangDeDe,
    UiTextKey.LangKoKr,
    UiTextKey.LangEsEs,
    UiTextKey.LangIdId,
    UiTextKey.LangViVn,
    UiTextKey.LangThTh,
    UiTextKey.LangFilPh,
    UiTextKey.LangMsMy,
    UiTextKey.LangPtBr,
    UiTextKey.LangItIt,
    UiTextKey.LangRuRu
)

/**
 * Builds a UI text map from translated strings.
 * Handles translation fallback and ensures template tokens are preserved.
 *
 * @param translatedJoined Translated strings joined by '\u0001' separator
 * @return Map of UiTextKey to translated (or fallback) strings
 */
fun buildUiTextMap(translatedJoined: String): Map<UiTextKey, String> {
    return buildUiTextMap(translatedJoined, null)
}

/**
 * Builds a UI text map from translated strings with language name correction.
 * Uses predefined language name translations to avoid translation API errors.
 *
 * @param translatedJoined Translated strings joined by '\u0001' separator
 * @param uiLanguageCode The target UI language code (e.g., "id-ID") for language name lookup
 * @return Map of UiTextKey to translated (or fallback) strings
 */
fun buildUiTextMap(translatedJoined: String, uiLanguageCode: String?): Map<UiTextKey, String> {
    val parts = translatedJoined.split('\u0001')
    val map = UiTextKey.entries.mapIndexed { index, key ->
        val value = parts.getOrNull(index) ?: BaseUiTexts.getOrNull(index).orEmpty()
        key to value
    }.toMap().toMutableMap()

    // Apply predefined language name translations if available
    if (uiLanguageCode != null && uiLanguageCode != "en-US") {
        val langNameTranslations = LanguageNameTranslations[uiLanguageCode]
        if (langNameTranslations != null) {
            LanguageNameKeys.forEach { key ->
                langNameTranslations[key]?.let { correctTranslation ->
                    map[key] = correctTranslation
                }
            }
        }
    }

    // Ensure tokens exist (translation sometimes breaks/removes them)
    fun ensureContains(key: UiTextKey, vararg tokens: String) {
        val v = map[key].orEmpty()
        if (tokens.any { !v.contains(it) }) {
            map[key] = BaseUiTexts.getOrNull(key.ordinal).orEmpty()
        }
    }

    // Template strings that must contain specific tokens
    ensureContains(UiTextKey.HistorySessionTitleTemplate, "{id}")
    ensureContains(UiTextKey.HistoryItemsCountTemplate, "{count}")
    ensureContains(UiTextKey.PaginationPageLabelTemplate, "{page}", "{total}")

    ensureContains(UiTextKey.LearningOpenSheetTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetTitleTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetPrimaryTemplate, "{speclanguage}")
    ensureContains(UiTextKey.LearningSheetHistoryCountTemplate, "{nowCount}", "{savedCount}")
    ensureContains(UiTextKey.SettingsScaleTemplate, "{pct}")
    ensureContains(UiTextKey.DialogGenerateOverwriteMessageTemplate, "{speclanguage}")

    // Quiz templates
    ensureContains(UiTextKey.QuizTitleTemplate, "{language}")
    ensureContains(UiTextKey.QuizMaterialsQuizTemplate, "{materials}", "{quiz}")
    ensureContains(UiTextKey.QuizNeedMoreRecordsTemplate, "{count}")
    ensureContains(UiTextKey.QuizYourAnswerTemplate, "{Answer}")
    ensureContains(UiTextKey.QuizCorrectAnswerTemplate, "{Answer}")
    ensureContains(UiTextKey.QuizQuestionTemplate, "{current}", "{total}")
    ensureContains(UiTextKey.QuizCannotRegenTemplate, "{materials}", "{quiz}")
    ensureContains(UiTextKey.QuizCoinRulesNeedMoreTemplate, "{count}")
    ensureContains(UiTextKey.QuizCoinRuleMaterialsTemplate, "{count}")
    ensureContains(UiTextKey.QuizCoinRuleQuizTemplate, "{count}")
    ensureContains(UiTextKey.QuizRegenNeedMoreTemplate, "{count}")
    ensureContains(UiTextKey.QuizCoinsEarnedMessageTemplate, "{Coins}")

    return map
}

/**
 * Generates a hash of all base UI texts for version comparison.
 * Useful for detecting when translations are out of sync.
 */
fun baseUiTextsHash(): Int {
    return BaseUiTexts.joinToString(separator = "\u0001").hashCode()
}

/**
 * Helper: Get a single UI text by key with fallback
 */
fun getUiText(key: UiTextKey): String {
    return BaseUiTexts.getOrNull(key.ordinal).orEmpty()
}

/**
 * Helper: Replace template variables in UI text
 * Example: "Scale: {pct}%" -> "Scale: 125%"
 */
fun String.replaceTemplateTokens(vararg replacements: Pair<String, String>): String {
    var result = this
    replacements.forEach { (token, value) ->
        result = result.replace("{$token}", value)
    }
    return result
}