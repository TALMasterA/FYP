package com.example.fyp.screens.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.MarkMessagesAsReadUseCase
import com.example.fyp.domain.friends.ObserveMessagesUseCase
import com.example.fyp.domain.friends.SendMessageUseCase
import com.example.fyp.domain.friends.TranslateAllMessagesUseCase
import com.example.fyp.data.friends.ChatRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendMessage
import com.example.fyp.model.friends.PublicUserProfile
import com.example.fyp.model.user.AuthState
import com.example.fyp.core.AppLogger
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
    val translatedMessages: Map<String, String> = emptyMap(),
    val showTranslation: Boolean = false,
    val translationError: String? = null,
    val isLoadingOlder: Boolean = false,
    val hasMoreMessages: Boolean = true,
    val clearSuccess: Boolean = false,
    val isBlocked: Boolean = false,           // current user has blocked this friend
    val isBlockedBy: Boolean = false,         // friend has blocked current user
    /** Friend's public profile — loaded on screen open for the profile dialog. */
    val friendProfile: PublicUserProfile? = null
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
    private val chatRepository: ChatRepository,
    private val friendsRepository: FriendsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var messagesJob: Job? = null
    private var profileLoadJob: Job? = null
    private var markReadJob: Job? = null
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
                        val isFirstLogin = currentUserId == null
                        currentUserId = UserId(auth.user.uid)
                        _uiState.value = _uiState.value.copy(currentUserId = auth.user.uid)
                        loadMessages(UserId(auth.user.uid))
                        markMessagesAsRead(UserId(auth.user.uid))

                        // Only load profile once on first login, or if not yet loaded
                        if (isFirstLogin || _uiState.value.friendProfile == null) {
                            loadFriendProfile()
                            checkBlockStatus(UserId(auth.user.uid))
                        }
                    }
                    AuthState.LoggedOut -> {
                        messagesJob?.cancel()
                        profileLoadJob?.cancel()
                        markReadJob?.cancel()
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
            } catch (_: kotlinx.coroutines.CancellationException) {
                // Normal cancellation — e.g. user navigated away or loadMessages called again
            } catch (e: Exception) {
                AppLogger.e("ChatViewModel", "loadMessages failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load messages. Please try again."
                )
            }
        }
    }

    private fun markMessagesAsRead(userId: UserId) {
        viewModelScope.launch {
            try {
                markMessagesAsReadUseCase(userId, friendId)
            } catch (_: Exception) {
                // Non-critical
            }
        }
    }

    /** Loads the friend's public profile for the profile dialog. */
    private fun loadFriendProfile() {
        // Cancel any in-flight load before starting a new one
        profileLoadJob?.cancel()
        profileLoadJob = viewModelScope.launch {
            try {
                val profile = friendsRepository.getPublicProfile(friendId)
                _uiState.value = _uiState.value.copy(friendProfile = profile)
            } catch (_: kotlinx.coroutines.CancellationException) {
                // Normal cancellation — do NOT log or re-throw
            } catch (e: Exception) {
                // Non-critical: profile dialog will fall back to nav-arg data
                android.util.Log.e("ChatViewModel", "Failed to load friend profile: ${e.message}")
            }
        }
    }

    /** Check block status in both directions and update UI state. */
    private fun checkBlockStatus(userId: UserId) {
        viewModelScope.launch {
            try {
                val blocked = friendsRepository.isBlocked(userId, friendId)
                val blockedBy = friendsRepository.isBlockedBy(userId, friendId)
                _uiState.value = _uiState.value.copy(isBlocked = blocked, isBlockedBy = blockedBy)
            } catch (_: Exception) { /* non-fatal */ }
        }
    }

    fun blockFriend() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            friendsRepository.blockUser(userId, friendId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isBlocked = true, error = null)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(error = "Failed to block user.")
                }
            )
        }
    }

    fun unblockFriend() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            friendsRepository.unblockUser(userId, friendId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isBlocked = false, error = null)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(error = "Failed to unblock user.")
                }
            )
        }
    }

    fun onMessageTextChange(text: String) {
        _uiState.value = _uiState.value.copy(messageText = text)
    }

    fun sendMessage() {
        val userId = currentUserId ?: return
        val messageText = _uiState.value.messageText.trim()
        if (messageText.isBlank() || _uiState.value.isSending) return

        // Prevent sending if blocked by the friend
        if (_uiState.value.isBlockedBy) {
            _uiState.value = _uiState.value.copy(error = "You cannot send messages to this user.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, error = null)
            val chatId = chatRepository.generateChatId(userId, friendId)
            val result = sendMessageUseCase(chatId, userId, friendId, messageText)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(messageText = "", isSending = false)
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
        val chatId = chatRepository.generateChatId(currentUserId ?: return, friendId)

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
                val settings = userSettingsRepository.fetchUserSettings(userId)
                val targetLanguage = settings.primaryLanguageCode.substringBefore("-")

                // Only translate friend's messages not already in the cache
                val existingCache = _uiState.value.translatedMessages
                val uncachedMessages = messages.filter { msg ->
                    msg.senderId != userId.value && !existingCache.containsKey(msg.content)
                }

                if (uncachedMessages.isEmpty()) {
                    // All already cached — just show translations
                    _uiState.value = _uiState.value.copy(showTranslation = true, isTranslating = false)
                    return@launch
                }

                // Build a messages list with only uncached items for the use case
                val result = translateAllMessagesUseCase(uncachedMessages, targetLanguage, userId.value)
                result.fold(
                    onSuccess = { newTranslations ->
                        // Merge new translations into existing cache
                        _uiState.value = _uiState.value.copy(
                            translatedMessages = existingCache + newTranslations,
                            showTranslation = true,
                            isTranslating = false
                        )
                    },
                    onFailure = { error ->
                        AppLogger.e("ChatViewModel", "translateAllMessages failed", error)
                        _uiState.value = _uiState.value.copy(
                            translationError = "Translation failed. Please try again.",
                            isTranslating = false
                        )
                    }
                )
            } catch (e: Exception) {
                AppLogger.e("ChatViewModel", "translateAllMessages catch failed", e)
                _uiState.value = _uiState.value.copy(
                    translationError = "Translation failed. Please try again.",
                    isTranslating = false
                )
            }
        }
    }

    /**
     * Hides all messages locally for this session only.
     * Does NOT delete anything from Firestore — full deletion only happens on unfriend.
     * The messages will reappear if the user leaves and re-enters the chat.
     */
    fun clearConversation() {
        // Stop observing new messages temporarily so the UI stays blank
        messagesJob?.cancel()
        _uiState.value = _uiState.value.copy(
            messages = emptyList(),
            translatedMessages = emptyMap(),
            showTranslation = false,
            clearSuccess = true,
            hasMoreMessages = false  // prevent load-older trigger
        )
    }

    fun dismissClearSuccess() {
        _uiState.value = _uiState.value.copy(clearSuccess = false)
    }

    fun toggleTranslation() {
        _uiState.value = _uiState.value.copy(showTranslation = !_uiState.value.showTranslation)
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
        profileLoadJob?.cancel()
        markReadJob?.cancel()
    }
}
