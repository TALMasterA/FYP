package com.example.fyp.data.providers

import com.microsoft.cognitiveservices.speech.SpeechConfig

object AzureSpeechProvider {
    fun speechConfigFromToken(token: String, region: String): SpeechConfig {
        val config = SpeechConfig.fromAuthorizationToken(token, region)
        return config
    }
}