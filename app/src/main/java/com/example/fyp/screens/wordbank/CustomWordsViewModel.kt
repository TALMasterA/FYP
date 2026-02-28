package com.example.fyp.screens.wordbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.wordbank.FirestoreCustomWordsRepository
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val translateTextUseCase: TranslateTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomWordsUiState())
    val uiState: StateFlow<CustomWordsUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var primaryLanguageCode: String = "en-US"

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                    }
                    AuthState.LoggedOut -> {
                        currentUserId = null
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

    fun addCustomWord(
        originalWord: String,
        translatedWord: String,
        pronunciation: String = "",
        example: String = "",
        sourceLang: String,
        targetLang: String
    ) {
        val uid = currentUserId ?: return

        viewModelScope.launch {
            customWordsRepo.addCustomWord(
                userId = uid,
                originalWord = originalWord,
                translatedWord = translatedWord,
                pronunciation = pronunciation,
                example = example,
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
