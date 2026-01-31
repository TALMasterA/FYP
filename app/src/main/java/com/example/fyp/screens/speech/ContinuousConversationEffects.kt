package com.example.fyp.screens.speech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // Track if user has manually scrolled away from bottom
    val isAtBottom by remember {
        derivedStateOf {
            val lastIndex = listState.layoutInfo.totalItemsCount - 1
            if (lastIndex < 0) true
            else {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == lastIndex
            }
        }
    }

    // Track if user manually scrolled (not at bottom)
    var userScrolledAway by remember { mutableStateOf(false) }

    // Detect when user scrolls away from bottom
    LaunchedEffect(isAtBottom) {
        if (!isAtBottom && messagesCount > 0) {
            userScrolledAway = true
        }
    }

    // Reset userScrolledAway when conversation restarts or when user scrolls back to bottom
    LaunchedEffect(isAtBottom, isRunning) {
        if (isAtBottom) {
            userScrolledAway = false
        }
    }

    // Auto scroll to newest message only if user hasn't scrolled away
    LaunchedEffect(messagesCount) {
        if (messagesCount > 0 && !userScrolledAway) {
            listState.animateScrollToItem(messagesCount - 1)
        }
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