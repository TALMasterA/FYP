package com.example.fyp.domain.speech

import com.example.fyp.data.repositories.AutoDetectRecognitionResult
import com.example.fyp.data.repositories.SpeechRepository

class RecognizeWithAutoDetectUseCase(
    private val speechRepository: SpeechRepository
) {
    /**
     * Recognize speech with auto-detect language.
     * @param candidateLanguages List of possible languages (max 4 supported by Azure)
     * @return Result containing the recognized text and detected language
     */
    suspend operator fun invoke(candidateLanguages: List<String>): Result<AutoDetectRecognitionResult> {
        return speechRepository.recognizeOnceWithAutoDetect(candidateLanguages)
    }
}
