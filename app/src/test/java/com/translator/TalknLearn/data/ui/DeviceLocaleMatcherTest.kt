package com.translator.TalknLearn.data.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class DeviceLocaleMatcherTest {

    private val supported = setOf(
        "en-US", "zh-HK", "zh-TW", "zh-CN", "ja-JP", "ko-KR", "fr-FR", "de-DE", "es-ES",
        "id-ID", "vi-VN", "th-TH", "fil-PH", "ms-MY", "pt-BR", "it-IT", "ru-RU"
    )

    private fun match(tag: String) =
        DeviceLocaleMatcher.match(Locale.forLanguageTag(tag), supported, fallback = "en-US")

    @Test
    fun `exact language and region matches`() {
        assertEquals("ja-JP", match("ja-JP"))
        assertEquals("ko-KR", match("ko-KR"))
    }

    @Test
    fun `other regions match by language`() {
        assertEquals("fr-FR", match("fr-CA"))
        assertEquals("pt-BR", match("pt-PT"))
        assertEquals("es-ES", match("es-MX"))
        assertEquals("en-US", match("en-GB"))
    }

    @Test
    fun `chinese is matched by script and region`() {
        assertEquals("zh-HK", match("zh-HK"))
        assertEquals("zh-HK", match("zh-Hant-MO"))
        assertEquals("zh-TW", match("zh-TW"))
        assertEquals("zh-TW", match("zh-Hant"))
        assertEquals("zh-CN", match("zh-CN"))
        assertEquals("zh-CN", match("zh-Hans-HK"))
        assertEquals("zh-CN", match("zh-SG"))
        assertEquals("zh-CN", match("zh"))
    }

    @Test
    fun `old java codes for indonesian and filipino match`() {
        assertEquals("id-ID", DeviceLocaleMatcher.match(Locale("in", "ID"), supported, "en-US"))
        assertEquals("fil-PH", DeviceLocaleMatcher.match(Locale("tl", "PH"), supported, "en-US"))
        assertEquals("fil-PH", match("fil-PH"))
    }

    @Test
    fun `unsupported or empty locale uses the fallback`() {
        assertEquals("en-US", match("ar-EG"))
        assertEquals("en-US", DeviceLocaleMatcher.match(Locale.ROOT, supported, "en-US"))
    }

    @Test
    fun `a match that is not in the supported set uses the fallback`() {
        val onlyEnglish = setOf("en-US")
        assertEquals("en-US", DeviceLocaleMatcher.match(Locale.JAPAN, onlyEnglish, "en-US"))
        assertEquals("en-US", DeviceLocaleMatcher.match(Locale.TAIWAN, onlyEnglish, "en-US"))
    }
}
