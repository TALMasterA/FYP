package com.translator.TalknLearn.data.repositories

import com.translator.TalknLearn.data.clients.CloudTranslatorClient
import com.translator.TalknLearn.data.cloud.LanguageDetectionCache
import com.translator.TalknLearn.data.cloud.TranslationCache
import com.translator.TalknLearn.data.ondevice.OnDeviceLanguageMapper
import com.translator.TalknLearn.data.ondevice.OnDeviceTranslator
import com.translator.TalknLearn.model.SpeechResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

class OnDeviceFallbackTest {

    @Test
    fun `language mapper handles app codes`() {
        assertEquals("zh", OnDeviceLanguageMapper.toMlKitLanguage("zh-CN"))
        assertEquals("ja", OnDeviceLanguageMapper.toMlKitLanguage("ja-JP"))
        assertEquals("tl", OnDeviceLanguageMapper.toMlKitLanguage("fil-PH"))
        assertNull(OnDeviceLanguageMapper.toMlKitLanguage(""))
    }

    @Test
    fun `translate falls back to on-device when cloud fails`() = runTest {
        val cloud: CloudTranslatorClient = mock()
        val cache: TranslationCache = mock()
        val detection: LanguageDetectionCache = mock()
        val onDevice: OnDeviceTranslator = mock()
        whenever(cache.getCached(any(), any(), any())).thenReturn(null)
        whenever(cloud.translateText(any(), any(), any())).thenThrow(RuntimeException("network down"))
        whenever(onDevice.isSupported(any(), any())).thenReturn(true)
        whenever(onDevice.translate(any(), any(), any())).thenReturn("bonjour")

        val repo = FirebaseTranslationRepository(cloud, cache, detection, onDevice)
        val result = repo.translate("hello", "en-US", "fr-FR")

        assertEquals("bonjour", (result as SpeechResult.Success).text)
    }

    @Test
    fun `translate returns error when offline and no model`() = runTest {
        val cloud: CloudTranslatorClient = mock()
        val cache: TranslationCache = mock()
        val detection: LanguageDetectionCache = mock()
        val onDevice: OnDeviceTranslator = mock()
        whenever(cache.getCached(any(), any(), any())).thenReturn(null)
        whenever(cloud.translateText(any(), any(), any())).thenThrow(RuntimeException("network down"))
        whenever(onDevice.isSupported(any(), any())).thenReturn(true)
        whenever(onDevice.translate(any(), any(), any())).thenReturn(null)

        val repo = FirebaseTranslationRepository(cloud, cache, detection, onDevice)
        assertTrue(repo.translate("hello", "en-US", "fr-FR") is SpeechResult.Error)
    }
}
