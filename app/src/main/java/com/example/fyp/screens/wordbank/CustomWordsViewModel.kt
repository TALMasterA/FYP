package com.example.fyp.screens.wordbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.wordbank.FirestoreCustomWordsRepository
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.user.AuthState
import com.example.fyp.core.security.ValidationResult
import com.example.fyp.core.security.sanitizeInput
import com.example.fyp.core.security.validateTextLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for custom words management.
 * Separated from [WordBankUiState] to keep responsibilities focused.
 */
data class CustomWordsUiState(
    val isCustomWordBankSelected: Boolean = false,
    val customWords: List<WordBankItem> = emptyList(),
    val customWordsCount: Int = 0,
    val isTranslatingCustomWord: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel managing custom word CRUD operations.
 * Extracted from the original WordBankViewModel to reduce its size
 * and separate custom-word concerns from AI generation/cluster logic.
 */
@HiltViewModel
class CustomWordsViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val customWordsRepo: FirestoreCustomWordsRepository,
    private val translateTextUseCase: TranslateTextUseCase,
    private val sharedSettings: SharedSettingsDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomWordsUiState())
    val uiState: StateFlow<CustomWordsUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var primaryLanguageCode: String = "en-US"
    private var settingsJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        val switchedUser = currentUserId != null && currentUserId != auth.user.uid
                        currentUserId = auth.user.uid
                        if (switchedUser) {
                            _uiState.value = _uiState.value.copy(
                                customWords = emptyList(),
                                customWordsCount = 0,
                                isLoading = _uiState.value.isCustomWordBankSelected,
                                error = null
                            )
                        }
                        sharedSettings.startObserving(auth.user.uid)
                        settingsJob?.cancel()
                        settingsJob = launch {
                            sharedSettings.settings.collect { settings ->
                                primaryLanguageCode = settings.primaryLanguageCode.ifBlank { "en-US" }
                            }
                        }
                        if (switchedUser && _uiState.value.isCustomWordBankSelected) {
                            loadCustomWords()
                        }
                    }
                    AuthState.LoggedOut -> {
                        currentUserId = null
                        settingsJob?.cancel()
                        settingsJob = null
                        primaryLanguageCode = "en-US"
                        _uiState.value = CustomWordsUiState()
                    }
                    AuthState.Loading -> Unit
                }
            }
        }
    }

    fun setPrimaryLanguageCode(code: String) {
        primaryLanguageCode = code
    }

    fun selectCustomWordBank() {
        _uiState.value = _uiState.value.copy(
            isCustomWordBankSelected = true,
            isLoading = true
        )
        loadCustomWords()
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            isCustomWordBankSelected = false,
            customWords = emptyList()
        )
    }

    /**
     * Adds a new custom word entry to the user's personal vocabulary.
     *
     * Validation Flow:
     * 1. Validates text length for all fields using SecurityUtils validation
     * 2. Sanitizes all inputs to escape HTML entities and prevent XSS
     * 3. Stores word in Firestore with language pair for categorization
     *
     * Language Code Extraction:
     * The category field "${sourceLang} → ${targetLang}" allows filtering custom
     * words by language pair without additional Firestore queries.
     *
     * Auto-refresh: If custom word bank is currently selected, triggers reload
     * to immediately show the new word in the UI.
     */
    fun addCustomWord(
        originalWord: String,
        translatedWord: String,
        pronunciation: String = "",
        example: String = "",
        sourceLang: String,
        targetLang: String
    ) {
        val uid = currentUserId ?: return

        // Validate originalWord (1-100 chars) - required field
        val originalValidation = validateTextLength(originalWord, minLength = 1, maxLength = 100, fieldName = "Original word")
        if (originalValidation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(error = originalValidation.message)
            return
        }

        // Validate translatedWord (1-100 chars) - required field
        val translatedValidation = validateTextLength(translatedWord, minLength = 1, maxLength = 100, fieldName = "Translated word")
        if (translatedValidation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(error = translatedValidation.message)
            return
        }

        // Validate pronunciation (0-200 chars) - optional field
        val pronunciationValidation = validateTextLength(pronunciation, minLength = 0, maxLength = 200, fieldName = "Pronunciation")
        if (pronunciationValidation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(error = pronunciationValidation.message)
            return
        }

        // Validate example (0-500 chars) - optional field
        val exampleValidation = validateTextLength(example, minLength = 0, maxLength = 500, fieldName = "Example")
        if (exampleValidation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(error = exampleValidation.message)
            return
        }

        // Sanitize all text inputs to escape HTML entities (prevents XSS attacks)
        val sanitizedOriginal = sanitizeInput(originalWord)
        val sanitizedTranslated = sanitizeInput(translatedWord)
        val sanitizedPronunciation = sanitizeInput(pronunciation)
        val sanitizedExample = sanitizeInput(example)

        viewModelScope.launch {
            customWordsRepo.addCustomWord(
                userId = uid,
                originalWord = sanitizedOriginal,
                translatedWord = sanitizedTranslated,
                pronunciation = sanitizedPronunciation,
                example = sanitizedExample,
                sourceLang = sourceLang,
                targetLang = targetLang
            ).onSuccess {
                if (_uiState.value.isCustomWordBankSelected) {
                    loadCustomWords()
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Failed to add word")
            }
        }
    }

    fun deleteCustomWord(wordId: String) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            customWordsRepo.deleteCustomWord(uid, wordId).onSuccess {
                if (_uiState.value.isCustomWordBankSelected) {
                    loadCustomWords()
                }
            }
        }
    }

    fun updateCustomWordTargetLanguage(word: WordBankItem, newTargetLang: String) {
        val uid = currentUserId ?: return
        val wordId = word.id.removePrefix("custom_")
        val (parsedSourceLang, parsedTargetLang) = parseLanguagePair(word.category)
        val sourceLang = normalizeLanguageCode(parsedSourceLang.ifBlank { primaryLanguageCode })
        val currentTargetLang = normalizeLanguageCode(parsedTargetLang)
        val targetLang = normalizeLanguageCode(newTargetLang)
        if (wordId.isBlank() || sourceLang.isBlank() || targetLang.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Invalid language update request")
            return
        }

        if (currentTargetLang.isNotBlank() && currentTargetLang == targetLang) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = true, error = null)

            // Always translate from originalWord/sourceLang so changing target language
            // cannot mutate the source side of the custom word entry.
            val translatedWord = when (val translation = translateTextUseCase(word.originalWord, sourceLang, targetLang)) {
                is SpeechResult.Success -> translation.text
                is SpeechResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isTranslatingCustomWord = false,
                        error = "Translation failed: ${translation.message}"
                    )
                    return@launch
                }
            }

            customWordsRepo.updateCustomWord(
                userId = uid,
                wordId = wordId,
                originalWord = word.originalWord,
                translatedWord = translatedWord,
                pronunciation = word.pronunciation,
                example = word.example,
                sourceLang = sourceLang,
                targetLang = targetLang
            ).onSuccess {
                if (_uiState.value.isCustomWordBankSelected) {
                    loadCustomWords()
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = "Failed to update word language")
            }

            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = false)
        }
    }

    /**
     * Translates text using the primary language as source.
     * Uses TranslateTextUseCase which handles caching and API calls.
     *
     * Translation Caching Strategy:
     * - The use case layer caches translations to reduce Azure API calls
     * - Cache key: "${sourceLang}_${targetLang}_$text"
     * - Callback pattern allows UI to update fields without state management
     */
    fun translateCustomWord(
        text: String,
        targetLanguageCode: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = true)

            when (val result = translateTextUseCase(text, primaryLanguageCode, targetLanguageCode)) {
                is SpeechResult.Success -> onResult(result.text)
                is SpeechResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Translation failed: ${result.message}")
                }
            }

            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = false)
        }
    }

    /**
     * Translates text with explicit source and target languages.
     * Used when user wants to translate between any two languages (not just primary).
     *
     * This method is separate from translateCustomWord() to provide flexibility
     * for custom word entries where the user may want to specify both languages
     * independently of their primary language setting.
     */
    fun translateForCustomWord(
        text: String,
        sourceLang: String,
        targetLang: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = true)

            when (val result = translateTextUseCase(text, sourceLang, targetLang)) {
                is SpeechResult.Success -> onResult(result.text)
                is SpeechResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Translation failed: ${result.message}")
                }
            }

            _uiState.value = _uiState.value.copy(isTranslatingCustomWord = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        settingsJob?.cancel()
    }

    private fun parseSourceLang(category: String): String {
        return parseLanguagePair(category).first
    }

    private fun parseLanguagePair(category: String): Pair<String, String> {
        val normalized = category.trim()
        if (normalized.isBlank()) return "" to ""

        // Accept both normalized current format and legacy variants that may have
        // been persisted with/without spaces or with a unicode arrow.
        val separators = listOf(" -> ", "→", "->")
        for (separator in separators) {
            val parts = normalized.split(separator)
            if (parts.size == 2) {
                return parts[0].trim() to parts[1].trim()
            }
        }

        return "" to ""
    }

    private fun normalizeLanguageCode(code: String): String {
        val normalized = code.trim()
        return when (normalized.lowercase()) {
            "en" -> "en-US"
            "zh" -> "zh-CN"
            "yue", "yue-hk" -> "zh-HK"
            "ja" -> "ja-JP"
            "fr" -> "fr-FR"
            "de" -> "de-DE"
            "ko" -> "ko-KR"
            "es" -> "es-ES"
            "id" -> "id-ID"
            "vi" -> "vi-VN"
            "th" -> "th-TH"
            "fil" -> "fil-PH"
            "ms" -> "ms-MY"
            "pt" -> "pt-BR"
            "it" -> "it-IT"
            "ru" -> "ru-RU"
            "zh-hk" -> "zh-HK"
            "zh-tw" -> "zh-TW"
            "zh-cn" -> "zh-CN"
            "en-us" -> "en-US"
            "ja-jp" -> "ja-JP"
            "fr-fr" -> "fr-FR"
            "de-de" -> "de-DE"
            "ko-kr" -> "ko-KR"
            "es-es" -> "es-ES"
            "id-id" -> "id-ID"
            "vi-vn" -> "vi-VN"
            "th-th" -> "th-TH"
            "fil-ph" -> "fil-PH"
            "ms-my" -> "ms-MY"
            "pt-br" -> "pt-BR"
            "it-it" -> "it-IT"
            "ru-ru" -> "ru-RU"
            else -> normalized
        }
    }

    /**
     * Loads all custom words for the current user from Firestore.
     *
     * Language Code Extraction from Category:
     * Custom words are stored with sourceLang and targetLang fields.
     * The category "${sourceLang} → ${targetLang}" format allows:
     * - Visual grouping in the UI by language pair
     * - Potential future filtering without re-querying Firestore
     * - Clear indication of translation direction
     *
     * ID Prefix Strategy:
     * Custom words use "custom_${cw.id}" prefix to differentiate them
     * from AI-generated word bank items in the UI and prevent ID collisions.
     */
    private fun loadCustomWords() {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            try {
                val customWords = customWordsRepo.getAllCustomWordsOnce(uid)
                val customWordItems = customWords.map { cw ->
                    WordBankItem(
                        id = "custom_${cw.id}",
                        originalWord = cw.originalWord,
                        translatedWord = cw.translatedWord,
                        pronunciation = cw.pronunciation,
                        example = cw.example,
                        category = "${cw.sourceLang} → ${cw.targetLang}",
                        difficulty = ""
                    )
                }
                _uiState.value = _uiState.value.copy(
                    customWords = customWordItems,
                    customWordsCount = customWordItems.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load custom words"
                )
            }
        }
    }
}
