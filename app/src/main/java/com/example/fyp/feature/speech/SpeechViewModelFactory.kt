package com.example.fyp.feature.speech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fyp.data.AzureSpeechRepository
import com.example.fyp.data.AzureTranslationRepository
import com.example.fyp.domain.speech.RecognizeFromMicUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.StartContinuousConversationUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase

class SpeechViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Create repositories
        val speechRepo = AzureSpeechRepository()
        val translationRepo = AzureTranslationRepository()

        // 2. Create use cases
        val recognizeUseCase = RecognizeFromMicUseCase(speechRepo)
        val translateUseCase = TranslateTextUseCase(translationRepo)
        val speakUseCase = SpeakTextUseCase(speechRepo)
        val continuousUseCase = StartContinuousConversationUseCase(speechRepo)

        // 3. Create ViewModel with use cases
        return SpeechViewModel(
            recognizeFromMic = recognizeUseCase,
            translateTextUseCase = translateUseCase,
            speakTextUseCase = speakUseCase,
            continuousUseCase = continuousUseCase
        ) as T
    }
}