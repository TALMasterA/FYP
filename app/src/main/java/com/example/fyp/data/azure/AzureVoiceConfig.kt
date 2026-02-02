package com.example.fyp.data.azure

/**
 * Azure Neural TTS voice configuration per language.
 * Each language has multiple voice options (male/female).
 * Voice names follow Azure's naming convention: locale-VoiceNameNeural
 */
object AzureVoiceConfig {

    data class VoiceOption(
        val name: String,        // Azure voice name (e.g., "en-US-JennyNeural")
        val displayName: String, // User-friendly name (e.g., "Jenny (Female)")
        val gender: String       // "Female" or "Male"
    )

    /**
     * Map of language code to available voices.
     * First voice in each list is the default.
     */
    private val voicesByLanguage: Map<String, List<VoiceOption>> = mapOf(
        "en-US" to listOf(
            VoiceOption("en-US-JennyNeural", "Jenny (Female)", "Female"),
            VoiceOption("en-US-GuyNeural", "Guy (Male)", "Male"),
            VoiceOption("en-US-AriaNeural", "Aria (Female)", "Female"),
            VoiceOption("en-US-DavisNeural", "Davis (Male)", "Male"),
            VoiceOption("en-US-AmberNeural", "Amber (Female)", "Female"),
            VoiceOption("en-US-AnaNeural", "Ana (Female, Child)", "Female"),
            VoiceOption("en-US-BrandonNeural", "Brandon (Male)", "Male"),
            VoiceOption("en-US-ChristopherNeural", "Christopher (Male)", "Male"),
            VoiceOption("en-US-CoraNeural", "Cora (Female)", "Female"),
            VoiceOption("en-US-ElizabethNeural", "Elizabeth (Female)", "Female"),
            VoiceOption("en-US-EricNeural", "Eric (Male)", "Male"),
            VoiceOption("en-US-JacobNeural", "Jacob (Male)", "Male"),
            VoiceOption("en-US-MichelleNeural", "Michelle (Female)", "Female"),
            VoiceOption("en-US-MonicaNeural", "Monica (Female)", "Female"),
            VoiceOption("en-US-SaraNeural", "Sara (Female)", "Female"),
        ),
        "zh-HK" to listOf(
            VoiceOption("zh-HK-HiuMaanNeural", "HiuMaan (Female)", "Female"),
            VoiceOption("zh-HK-WanLungNeural", "WanLung (Male)", "Male"),
            VoiceOption("zh-HK-HiuGaaiNeural", "HiuGaai (Female)", "Female"),
        ),
        "zh-CN" to listOf(
            VoiceOption("zh-CN-XiaoxiaoNeural", "Xiaoxiao (Female)", "Female"),
            VoiceOption("zh-CN-YunxiNeural", "Yunxi (Male)", "Male"),
            VoiceOption("zh-CN-YunjianNeural", "Yunjian (Male)", "Male"),
            VoiceOption("zh-CN-XiaoyiNeural", "Xiaoyi (Female)", "Female"),
            VoiceOption("zh-CN-YunyangNeural", "Yunyang (Male)", "Male"),
            VoiceOption("zh-CN-XiaochenNeural", "Xiaochen (Female)", "Female"),
            VoiceOption("zh-CN-XiaohanNeural", "Xiaohan (Female)", "Female"),
            VoiceOption("zh-CN-XiaomengNeural", "Xiaomeng (Female)", "Female"),
            VoiceOption("zh-CN-XiaomoNeural", "Xiaomo (Female)", "Female"),
            VoiceOption("zh-CN-XiaoruiNeural", "Xiaorui (Female)", "Female"),
            VoiceOption("zh-CN-XiaoshuangNeural", "Xiaoshuang (Female, Child)", "Female"),
            VoiceOption("zh-CN-XiaoxuanNeural", "Xiaoxuan (Female)", "Female"),
            VoiceOption("zh-CN-XiaoyanNeural", "Xiaoyan (Female)", "Female"),
            VoiceOption("zh-CN-XiaoyouNeural", "Xiaoyou (Female, Child)", "Female"),
            VoiceOption("zh-CN-XiaozhenNeural", "Xiaozhen (Female)", "Female"),
            VoiceOption("zh-CN-YunfengNeural", "Yunfeng (Male)", "Male"),
            VoiceOption("zh-CN-YunhaoNeural", "Yunhao (Male)", "Male"),
            VoiceOption("zh-CN-YunxiaNeural", "Yunxia (Male)", "Male"),
            VoiceOption("zh-CN-YunyeNeural", "Yunye (Male)", "Male"),
            VoiceOption("zh-CN-YunzeNeural", "Yunze (Male)", "Male"),
        ),
        "ja-JP" to listOf(
            VoiceOption("ja-JP-NanamiNeural", "Nanami (Female)", "Female"),
            VoiceOption("ja-JP-KeitaNeural", "Keita (Male)", "Male"),
            VoiceOption("ja-JP-AoiNeural", "Aoi (Female)", "Female"),
            VoiceOption("ja-JP-DaichiNeural", "Daichi (Male)", "Male"),
            VoiceOption("ja-JP-MayuNeural", "Mayu (Female)", "Female"),
            VoiceOption("ja-JP-NaokiNeural", "Naoki (Male)", "Male"),
            VoiceOption("ja-JP-ShioriNeural", "Shiori (Female)", "Female"),
        ),
        "ko-KR" to listOf(
            VoiceOption("ko-KR-SunHiNeural", "SunHi (Female)", "Female"),
            VoiceOption("ko-KR-InJoonNeural", "InJoon (Male)", "Male"),
            VoiceOption("ko-KR-BongJinNeural", "BongJin (Male)", "Male"),
            VoiceOption("ko-KR-GookMinNeural", "GookMin (Male)", "Male"),
            VoiceOption("ko-KR-JiMinNeural", "JiMin (Female)", "Female"),
            VoiceOption("ko-KR-SeoHyeonNeural", "SeoHyeon (Female)", "Female"),
            VoiceOption("ko-KR-SoonBokNeural", "SoonBok (Female)", "Female"),
            VoiceOption("ko-KR-YuJinNeural", "YuJin (Female)", "Female"),
        ),
        "fr-FR" to listOf(
            VoiceOption("fr-FR-DeniseNeural", "Denise (Female)", "Female"),
            VoiceOption("fr-FR-HenriNeural", "Henri (Male)", "Male"),
            VoiceOption("fr-FR-AlainNeural", "Alain (Male)", "Male"),
            VoiceOption("fr-FR-BrigitteNeural", "Brigitte (Female)", "Female"),
            VoiceOption("fr-FR-CelesteNeural", "Celeste (Female)", "Female"),
            VoiceOption("fr-FR-ClaudeNeural", "Claude (Male)", "Male"),
            VoiceOption("fr-FR-CoralieNeural", "Coralie (Female)", "Female"),
            VoiceOption("fr-FR-EloiseNeural", "Eloise (Female, Child)", "Female"),
            VoiceOption("fr-FR-JacquelineNeural", "Jacqueline (Female)", "Female"),
            VoiceOption("fr-FR-JeromeNeural", "Jerome (Male)", "Male"),
            VoiceOption("fr-FR-JosephineNeural", "Josephine (Female)", "Female"),
            VoiceOption("fr-FR-MauriceNeural", "Maurice (Male)", "Male"),
            VoiceOption("fr-FR-YvesNeural", "Yves (Male)", "Male"),
            VoiceOption("fr-FR-YvetteNeural", "Yvette (Female)", "Female"),
        ),
        "de-DE" to listOf(
            VoiceOption("de-DE-KatjaNeural", "Katja (Female)", "Female"),
            VoiceOption("de-DE-ConradNeural", "Conrad (Male)", "Male"),
            VoiceOption("de-DE-AmalaNeural", "Amala (Female)", "Female"),
            VoiceOption("de-DE-BerndNeural", "Bernd (Male)", "Male"),
            VoiceOption("de-DE-ChristophNeural", "Christoph (Male)", "Male"),
            VoiceOption("de-DE-ElkeNeural", "Elke (Female)", "Female"),
            VoiceOption("de-DE-GiselaNeural", "Gisela (Female, Child)", "Female"),
            VoiceOption("de-DE-KasperNeural", "Kasper (Male)", "Male"),
            VoiceOption("de-DE-KillianNeural", "Killian (Male)", "Male"),
            VoiceOption("de-DE-KlarissaNeural", "Klarissa (Female)", "Female"),
            VoiceOption("de-DE-KlausNeural", "Klaus (Male)", "Male"),
            VoiceOption("de-DE-LouisaNeural", "Louisa (Female)", "Female"),
            VoiceOption("de-DE-MajaNeural", "Maja (Female)", "Female"),
            VoiceOption("de-DE-RalfNeural", "Ralf (Male)", "Male"),
            VoiceOption("de-DE-TanjaNeural", "Tanja (Female)", "Female"),
        ),
        "es-ES" to listOf(
            VoiceOption("es-ES-ElviraNeural", "Elvira (Female)", "Female"),
            VoiceOption("es-ES-AlvaroNeural", "Alvaro (Male)", "Male"),
            VoiceOption("es-ES-AbrilNeural", "Abril (Female)", "Female"),
            VoiceOption("es-ES-ArnauNeural", "Arnau (Male)", "Male"),
            VoiceOption("es-ES-DarioNeural", "Dario (Male)", "Male"),
            VoiceOption("es-ES-EliasNeural", "Elias (Male)", "Male"),
            VoiceOption("es-ES-EstrellaNeural", "Estrella (Female)", "Female"),
            VoiceOption("es-ES-IreneNeural", "Irene (Female)", "Female"),
            VoiceOption("es-ES-LaiaNeural", "Laia (Female)", "Female"),
            VoiceOption("es-ES-LiaNeural", "Lia (Female)", "Female"),
            VoiceOption("es-ES-NilNeural", "Nil (Male)", "Male"),
            VoiceOption("es-ES-SaulNeural", "Saul (Male)", "Male"),
            VoiceOption("es-ES-TeoNeural", "Teo (Male)", "Male"),
            VoiceOption("es-ES-TrianaNeural", "Triana (Female)", "Female"),
            VoiceOption("es-ES-VeraNeural", "Vera (Female)", "Female"),
        ),
    )

    /**
     * Get available voices for a language.
     * Returns empty list if language not supported.
     */
    fun getVoicesForLanguage(languageCode: String): List<VoiceOption> {
        return voicesByLanguage[languageCode] ?: emptyList()
    }

    /**
     * Get the default voice for a language.
     * Returns null if language not supported.
     */
    fun getDefaultVoice(languageCode: String): VoiceOption? {
        return voicesByLanguage[languageCode]?.firstOrNull()
    }

    /**
     * Get a specific voice by name, or the default for the language.
     */
    fun getVoiceOrDefault(languageCode: String, voiceName: String?): VoiceOption? {
        if (voiceName.isNullOrBlank()) return getDefaultVoice(languageCode)
        val voices = voicesByLanguage[languageCode] ?: return null
        return voices.find { it.name == voiceName } ?: voices.firstOrNull()
    }

    /**
     * Get all supported language codes that have voice options.
     */
    fun getSupportedLanguages(): Set<String> = voicesByLanguage.keys
}
