package com.example.fyp.data

import com.example.fyp.model.SpeechResult

class FirebaseTranslationRepository(
    private val cloudTranslatorClient: CloudTranslatorClient = CloudTranslatorClient()
) : TranslationRepository {

    override suspend fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): SpeechResult {
        return try {
            val translated = cloudTranslatorClient.translateText(
                text = text,
                from = fromLanguage,
                to = toLanguage
            )
            SpeechResult.Success(translated)
        } catch (e: Exception) {
            SpeechResult.Error(e.message ?: "Translation failed")
        }
    }
}