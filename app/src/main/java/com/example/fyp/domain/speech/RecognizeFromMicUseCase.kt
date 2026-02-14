package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.SpeechRepository
import com.example.fyp.model.SpeechResult

/**
 * Use case for recognizing speech from microphone input.
 * Captures a single utterance and converts it to text.
 *
 * @param speechRepository Repository handling Azure Speech SDK integration
 */
class RecognizeFromMicUseCase(
    private val speechRepository: SpeechRepository
) {
    /**
     * Recognizes speech from the microphone in the specified language.
     * Requires microphone permission to be granted.
     *
     * @param languageCode Language code for speech recognition (e.g., "en-US", "zh-CN")
     * @return SpeechResult.Success with recognized text or SpeechResult.Error with error message
     */
    suspend operator fun invoke(languageCode: String): SpeechResult {
        return speechRepository.recognizeOnce(languageCode)
    }
}