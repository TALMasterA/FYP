package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.SpeechRepository
import com.example.fyp.model.SpeechResult

/**
 * Use case for text-to-speech synthesis.
 * Converts text to spoken audio using Azure Cognitive Services.
 *
 * @param speechRepository Repository handling Azure Speech SDK integration
 */
class SpeakTextUseCase(
    private val speechRepository: SpeechRepository
) {
    /**
     * Synthesizes speech from text in the specified language.
     *
     * @param text The text to speak
     * @param languageCode Language code for speech synthesis (e.g., "en-US", "zh-CN")
     * @param voiceName Optional specific voice name (e.g., "en-US-JennyNeural"). If null, uses default voice.
     * @return SpeechResult.Success when speech completes or SpeechResult.Error with error message
     */
    suspend operator fun invoke(
        text: String,
        languageCode: String,
        voiceName: String? = null
    ): SpeechResult {
        return speechRepository.speak(
            text = text,
            languageCode = languageCode,
            voiceName = voiceName
        )
    }
}