package com.example.fyp.screens.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.MarkMessagesAsReadUseCase
import com.example.fyp.domain.friends.ObserveMessagesUseCase
import com.example.fyp.domain.friends.SendMessageUseCase
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
    val currentUserId: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepo: FirebaseAuthRepository,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase
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

    private fun loadMessages(userId: UserId) {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            observeMessagesUseCase(userId, friendId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false
                )
            }
        }
    }

    private fun markMessagesAsRead(userId: UserId) {
        viewModelScope.launch {
            markMessagesAsReadUseCase(userId, friendId)
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
            
            val result = sendMessageUseCase(userId, friendId, messageText)
            
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

    override fun onCleared() {
        super.onCleared()
        messagesJob?.cancel()
    }
}
