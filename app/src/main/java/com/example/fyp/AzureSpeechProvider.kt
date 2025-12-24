package com.example.fyp

import com.microsoft.cognitiveservices.speech.SpeechConfig

object AzureSpeechProvider {
    private var config: SpeechConfig? = null

    fun speechConfig(): SpeechConfig {
        val key = BuildConfig.AZURE_SPEECH_KEY
        val region = BuildConfig.AZURE_SPEECH_REGION
        if (config == null) {
            config = SpeechConfig.fromSubscription(key, region)
        }
        return config!!
    }
}
