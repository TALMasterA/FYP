package com.translator.TalknLearn.data.ondevice

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

/**
 * On-device translation used as an offline fallback when the cloud translator is unreachable.
 * Language models are downloaded (Wi-Fi only) after a successful online translation so the
 * language pair is available offline later.
 */
interface OnDeviceTranslator {
    /** True if both languages have ML Kit support (app codes such as "zh-CN" are accepted). */
    fun isSupported(fromLanguage: String, toLanguage: String): Boolean

    /** Translates offline, or returns null when unsupported / models are not downloaded. */
    suspend fun translate(text: String, fromLanguage: String, toLanguage: String): String?

    /** Best-effort background download of the models for a language pair. */
    suspend fun prefetch(fromLanguage: String, toLanguage: String)
}

object OnDeviceLanguageMapper {
    /** Maps an app/BCP-47 language code to an ML Kit language code, or null if unsupported. */
    fun toMlKitLanguage(code: String): String? {
        val primary = code.trim().replace('_', '-').substringBefore('-').lowercase()
        if (primary.isEmpty()) return null
        val normalized = if (primary == "fil") "tl" else primary
        return TranslateLanguage.fromLanguageTag(normalized)
    }
}

class MlKitOnDeviceTranslator : OnDeviceTranslator {

    override fun isSupported(fromLanguage: String, toLanguage: String): Boolean {
        val from = OnDeviceLanguageMapper.toMlKitLanguage(fromLanguage)
        val to = OnDeviceLanguageMapper.toMlKitLanguage(toLanguage)
        return from != null && to != null && from != to
    }

    override suspend fun translate(text: String, fromLanguage: String, toLanguage: String): String? {
        val from = OnDeviceLanguageMapper.toMlKitLanguage(fromLanguage) ?: return null
        val to = OnDeviceLanguageMapper.toMlKitLanguage(toLanguage) ?: return null
        if (from == to) return text
        val translator = Translation.getClient(
            TranslatorOptions.Builder().setSourceLanguage(from).setTargetLanguage(to).build()
        )
        return try {
            val manager = RemoteModelManager.getInstance()
            val downloaded = manager.getDownloadedModels(TranslateRemoteModel::class.java).await()
                .map { it.language }.toSet()
            // English is the pivot language: both models must be present (English is built in
            // as soon as any pair is downloaded).
            if (!downloaded.containsAll(listOf(from, to).filter { it != TranslateLanguage.ENGLISH })) {
                return null
            }
            translator.translate(text).await()
        } catch (e: Exception) {
            null
        } finally {
            translator.close()
        }
    }

    override suspend fun prefetch(fromLanguage: String, toLanguage: String) {
        val from = OnDeviceLanguageMapper.toMlKitLanguage(fromLanguage) ?: return
        val to = OnDeviceLanguageMapper.toMlKitLanguage(toLanguage) ?: return
        if (from == to) return
        val translator = Translation.getClient(
            TranslatorOptions.Builder().setSourceLanguage(from).setTargetLanguage(to).build()
        )
        try {
            translator.downloadModelIfNeeded(DownloadConditions.Builder().requireWifi().build()).await()
        } catch (e: Exception) {
            // Best effort only.
        } finally {
            translator.close()
        }
    }
}
