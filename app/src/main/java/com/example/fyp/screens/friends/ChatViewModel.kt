package com.example.fyp.screens.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.MarkMessagesAsReadUseCase
import com.example.fyp.domain.friends.ObserveMessagesUseCase
import com.example.fyp.domain.friends.SendMessageUseCase
import com.example.fyp.domain.friends.TranslateAllMessagesUseCase
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Chat screen.
 */
data class ChatUiState(
    val isLoading: Boolean = true,
    val messages: List<FriendMessage> = emptyList(),
    val messageText: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val friendUsername: String = "",
    val friendDisplayName: String = "",
    val currentUserId: String = "",
    val isTranslating: Boolean = false,
    val translatedMessages: Map<String, String> = emptyMap(), // original -> translated
    val showTranslation: Boolean = false,
    val translationError: String? = null,
    val isLoadingOlder: Boolean = false,
    val hasMoreMessages: Boolean = true  // Assume more until proven otherwise
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepo: FirebaseAuthRepository,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase,
    private val translateAllMessagesUseCase: TranslateAllMessagesUseCase,
    private val userSettingsRepository: UserSettingsRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var messagesJob: Job? = null
    private var currentUserId: UserId? = null
    private val friendId: UserId
    private val friendUsername: String
    private val friendDisplayName: String

    init {
        // Get navigation arguments
        friendId = UserId(checkNotNull(savedStateHandle.get<String>("friendId")))
        friendUsername = checkNotNull(savedStateHandle.get<String>("friendUsername"))
        friendDisplayName = savedStateHandle.get<String>("friendDisplayName") ?: ""

        _uiState.value = _uiState.value.copy(
            friendUsername = friendUsername,
            friendDisplayName = friendDisplayName
        )

        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = UserId(auth.user.uid)
                        _uiState.value = _uiState.value.copy(currentUserId = auth.user.uid)
                        loadMessages(UserId(auth.user.uid))
                        markMessagesAsRead(UserId(auth.user.uid))
                    }
                    AuthState.LoggedOut -> {
                        messagesJob?.cancel()
                        currentUserId = null
                        _uiState.value = ChatUiState(
                            isLoading = false,
                            friendUsername = friendUsername,
                            friendDisplayName = friendDisplayName
                        )
                    }
                    AuthState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private var markReadJob: Job? = null

    private fun loadMessages(userId: UserId) {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            try {
                observeMessagesUseCase(userId, friendId).collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages,
                        isLoading = false
                    )
                    // Debounce mark-as-read so rapid message updates don't trigger
                    // redundant Firestore writes
                    markReadJob?.cancel()
                    markReadJob = launch {
                        kotlinx.coroutines.delay(300)
                        markMessagesAsRead(userId)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load messages"
                )
            }
        }
    }

    private fun markMessagesAsRead(userId: UserId) {
        viewModelScope.launch {
            try {
                markMessagesAsReadUseCase(userId, friendId)
            } catch (_: Exception) {
                // Non-critical: ignore errors marking messages as read
            }
        }
    }

    fun onMessageTextChange(text: String) {
        _uiState.value = _uiState.value.copy(messageText = text)
    }

    fun sendMessage() {
        val userId = currentUserId ?: return
        val messageText = _uiState.value.messageText.trim()
        
        if (messageText.isBlank() || _uiState.value.isSending) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, error = null)
            
            val chatId = chatRepository.generateChatId(userId, friendId)
            val result = sendMessageUseCase(chatId, userId, friendId, messageText)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        messageText = "",
                        isSending = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to send message",
                        isSending = false
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Load older messages when user scrolls to the top of the chat.
     * Fetches 25 messages before the oldest currently loaded message.
     */
    fun loadOlderMessages() {
        val state = _uiState.value
        if (state.isLoadingOlder || !state.hasMoreMessages) return
        val oldest = state.messages.firstOrNull() ?: return

        val chatId = chatRepository.generateChatId(
            currentUserId ?: return,
            friendId
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingOlder = true)
            try {
                val olderMessages = chatRepository.loadOlderMessages(
                    chatId = chatId,
                    beforeTimestamp = oldest.createdAt,
                    limit = 25
                )
                _uiState.value = _uiState.value.copy(
                    messages = olderMessages + _uiState.value.messages,
                    isLoadingOlder = false,
                    hasMoreMessages = olderMessages.size >= 25
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingOlder = false)
            }
        }
    }

    fun translateAllMessages() {
        val userId = currentUserId ?: return
        val messages = _uiState.value.messages
        
        if (messages.isEmpty() || _uiState.value.isTranslating) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslating = true, translationError = null)
            
            try {
                // Get user's preferred language from settings
                val settings = userSettingsRepository.fetchUserSettings(userId)
                val targetLanguage = settings.primaryLanguageCode.substringBefore("-") // Get language code (e.g., "en" from "en-US")
                
                // Translate all messages
                val result = translateAllMessagesUseCase(messages, targetLanguage)
                
                result.fold(
                    onSuccess = { translations ->
                        _uiState.value = _uiState.value.copy(
                            translatedMessages = translations,
                            showTranslation = true,
                            isTranslating = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            translationError = error.message ?: "Translation failed",
                            isTranslating = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    translationError = e.message ?: "Translation failed",
                    isTranslating = false
                )
            }
        }
    }
    
    fun toggleTranslation() {
        _uiState.value = _uiState.value.copy(
            showTranslation = !_uiState.value.showTranslation
        )
    }
    
    fun clearTranslation() {
        _uiState.value = _uiState.value.copy(
            translatedMessages = emptyMap(),
            showTranslation = false,
            translationError = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        messagesJob?.cancel()
    }
}
