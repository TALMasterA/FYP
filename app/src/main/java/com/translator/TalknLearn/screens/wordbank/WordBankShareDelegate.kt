package com.translator.TalknLearn.screens.wordbank

import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.domain.friends.ShareWordUseCase
import com.translator.TalknLearn.model.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Word-sharing logic (share a word-bank item with a friend).
 * Extracted from [WordBankViewModel] for maintainability.
 */
internal class WordBankShareDelegate(
    private val uiState: MutableStateFlow<WordBankUiState>,
    private val scope: CoroutineScope,
    private val shareWordUseCase: ShareWordUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val getCurrentUserId: () -> String?,
    private val getPrimaryLanguageCode: () -> String
) {
    fun setPendingShareWord(word: WordBankItem?) {
        uiState.value = uiState.value.copy(pendingShareWord = word, shareError = null, shareSuccess = null)
    }

    fun shareWord(word: WordBankItem, toUserId: UserId) {
        val fromId = getCurrentUserId() ?: return
        val state = uiState.value
        if (state.isSharing) return

        val (sourceLang, targetLang) = if (state.isCustomWordBankSelected) {
            val (parsedSource, parsedTarget) = parseLanguagePair(word.category)
            Pair(parsedSource.ifBlank { getPrimaryLanguageCode() }, parsedTarget)
        } else {
            Pair(
                state.currentWordBank?.targetLanguageCode ?: state.selectedLanguageCode ?: "",
                state.currentWordBank?.primaryLanguageCode ?: getPrimaryLanguageCode()
            )
        }

        if (word.originalWord.isBlank() || word.translatedWord.isBlank() || sourceLang.isBlank() || targetLang.isBlank()) {
            uiState.value = state.copy(
                isSharing = false,
                shareSuccess = null,
                shareError = "Word data is incomplete. Refresh and try again.",
                pendingShareWord = null
            )
            return
        }

        val fromUsername = sharedFriendsDataSource.getCachedUsername(fromId) ?: ""

        scope.launch {
            uiState.value = uiState.value.copy(isSharing = true, shareError = null, shareSuccess = null, pendingShareWord = null)
            shareWordUseCase(
                fromUserId = UserId(fromId),
                fromUsername = fromUsername,
                toUserId = toUserId,
                sourceText = word.originalWord,
                targetText = word.translatedWord,
                sourceLang = sourceLang,
                targetLang = targetLang,
                notes = word.example
            ).onSuccess {
                uiState.value = uiState.value.copy(isSharing = false, shareSuccess = "Shared successfully!")
            }.onFailure { e ->
                uiState.value = uiState.value.copy(isSharing = false, shareError = e.message ?: "Failed to share")
            }
        }
    }

    fun clearShareMessages() {
        uiState.value = uiState.value.copy(shareSuccess = null, shareError = null)
    }

    private fun parseLanguagePair(category: String): Pair<String, String> {
        val normalized = category.trim()
        if (normalized.isBlank()) return "" to ""

        val separators = listOf(" -> ", "->", " \u2192 ", "\u2192")
        for (separator in separators) {
            val parts = normalized.split(separator)
            if (parts.size == 2) {
                return parts[0].trim() to parts[1].trim()
            }
        }

        return "" to ""
    }
}
