package com.example.fyp.screens.speech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyListState

@Composable
fun ContinuousConversationEffects(
    messagesCount: Int,
    listState: LazyListState,
    isPersonATalking: Boolean,
    isLoggedIn: Boolean,
    isRunning: Boolean,
    isPreparing: Boolean,
    isProcessing: Boolean,
    fromLanguage: String,
    toLanguage: String,
    onRestartRecognizer: (speakingLang: String, targetLang: String, isFromPersonA: Boolean) -> Unit,
    onStopRecognizer: () -> Unit,
) {
    // Auto scroll to newest message
    LaunchedEffect(messagesCount) {
        if (messagesCount > 0) listState.animateScrollToItem(messagesCount - 1)
    }

    // Restart recognizer on person switch while running (but never during preparing/processing)
    LaunchedEffect(isPersonATalking, isLoggedIn) {
        if (isRunning && isLoggedIn && !isPreparing && !isProcessing) {
            onStopRecognizer()

            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
            val otherLang = if (isPersonATalking) toLanguage else fromLanguage

            onRestartRecognizer(
                speakLang,
                otherLang,
                isPersonATalking,
            )
        }
    }
}