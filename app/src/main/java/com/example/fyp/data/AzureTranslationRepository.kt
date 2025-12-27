package com.example.fyp.data

import com.example.fyp.model.SpeechResult

class AzureTranslationRepository : TranslationRepository {
    override suspend fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): SpeechResult {
        return TranslatorClient.translateText(
            text = text,
            toLanguage = toLanguage,
            fromLanguage = fromLanguage
        )
    }
}