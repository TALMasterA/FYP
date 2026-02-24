package com.example.fyp.screens.wordbank

import com.example.fyp.model.friends.FriendRelation
import com.google.firebase.Timestamp

/**
 * A single word/phrase in the word bank
 */
data class WordBankItem(
    val id: String = "",
    val originalWord: String = "",
    val translatedWord: String = "",
    val pronunciation: String = "", // Optional pronunciation guide
    val example: String = "",       // Example sentence
    val category: String = "",      // e.g., "noun", "verb", "phrase"
    val difficulty: String = ""     // e.g., "beginner", "intermediate", "advanced"
)

/**
 * Word bank for a specific language pair
 */
data class WordBank(
    val primaryLanguageCode: String = "",
    val targetLanguageCode: String = "",
    val words: List<WordBankItem> = emptyList(),
    val generatedAt: Timestamp? = null,
    val historyCountAtGenerate: Int = 0
)

/**
 * UI state for the word bank screen
 */
data class WordBankUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val languageClusters: List<WordBankLanguageCluster> = emptyList(),
    val selectedLanguageCode: String? = null,
    val currentWordBank: WordBank? = null,
    val isGenerating: Boolean = false,
    val isSpeaking: Boolean = false,
    val speakingItemId: String? = null,
    val speakingType: SpeakingType? = null,
    val isTranslatingCustomWord: Boolean = false,
    // Custom word bank specific state
    val isCustomWordBankSelected: Boolean = false,
    val customWords: List<WordBankItem> = emptyList(),
    val customWordsCount: Int = 0,
    // Share feature state
    val friends: List<FriendRelation> = emptyList(),
    val isSharing: Boolean = false,
    val shareSuccess: String? = null,
    val shareError: String? = null,
    val pendingShareWord: WordBankItem? = null,
    // Generation completion event (consumed once by banner)
    val wordBankGenerationCompleted: String? = null
)

/**
 * Represents a language cluster for the word bank selection
 */
data class WordBankLanguageCluster(
    val languageCode: String,
    val recordCount: Int,
    val hasWordBank: Boolean
)

/**
 * Type of text being spoken
 */
enum class SpeakingType {
    ORIGINAL,
    TRANSLATED
}
