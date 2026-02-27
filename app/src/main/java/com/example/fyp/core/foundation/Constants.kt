package com.example.fyp.core

/**
 * Centralized constants for UI and business logic.
 * Eliminates magic numbers scattered throughout the codebase.
 */
object UiConstants {
    // Error/Success message display durations
    const val ERROR_AUTO_DISMISS_MS = 3000L
    const val SUCCESS_MESSAGE_DURATION_MS = 5000L
    const val PASSWORD_RESET_SUCCESS_DURATION_MS = 5000L
    const val COIN_UNLOCK_SUCCESS_DURATION_MS = 3000L

    // UI transition and animation delays
    const val UI_TRANSITION_DELAY_MS = 200L
    const val SPEECH_PREPARE_DELAY_MS = 200L
    const val SPEECH_LISTENING_DEBOUNCE_MS = 150L
    const val TTS_FINISH_DELAY_MS = 300L

    // Text-to-speech delays
    const val TTS_START_DELAY_MS = 500L
    const val TTS_ERROR_WAIT_MS = 3000L
}

/**
 * AI and generation related constants
 */
object AiConfig {
    /**
     * Azure OpenAI deployment name - must match the deployment name in Azure Portal.
     * Go to: Azure Portal → Azure OpenAI → Deployments → copy the exact name.
     */
    const val DEFAULT_DEPLOYMENT = "gpt-5-mini"
    const val AI_GENERATION_TIMEOUT_MINUTES = 5L
}

/**
 * Firestore and data constants
 */
object DataConstants {
    const val DEFAULT_HISTORY_LIMIT = 200L
    const val COUNT_REFRESH_DEBOUNCE_MS = 5_000L
    const val BATCH_DELETE_LIMIT = 400
}

/**
 * Generation eligibility constants
 */
object GenerationConstants {
    const val MIN_RECORDS_FOR_REGEN = 20
}

