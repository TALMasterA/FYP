package com.example.fyp.screens.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
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
import com.example.fyp.core.security.RateLimiter
import com.example.fyp.core.security.ValidationResult
import com.example.fyp.core.security.sanitizeInput
import com.example.fyp.core.security.validateTextLength
import com.google.firebase.functions.FirebaseFunctionsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val friendProfile: PublicUserProfile? = null,
    /** Timestamp at which this user cleared the conversation (messages before this are hidden). */
    val clearedAt: com.google.firebase.Timestamp? = null
)

/**
 * ViewModel for the 1-on-1 Chat screen.
 *
 * Manages real-time message observation, sending, pagination of
 * older messages, batch translation of chat messages, and
 * read-receipt marking. Uses [SavedStateHandle] for the friendId
 * route argument.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepo: FirebaseAuthRepository,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase,
    private val translateAllMessagesUseCase: TranslateAllMessagesUseCase,
    private val userSettingsRepository: UserSettingsRepository,
    private val chatRepository: ChatRepository,
    private val friendsRepository: FriendsRepository,
    private val sharedFriendsDataSource: SharedFriendsDataSource
) : ViewModel() {

    companion object {
        private val messageRateLimiter = RateLimiter(maxAttempts = 10, windowMillis = 60_000L)
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var messagesJob: Job? = null
    private var profileLoadJob: Job? = null
    private var markReadJob: Job? = null
    private var currentUserId: UserId? = null
    private var isChatScreenActive: Boolean = false
    private val friendId: UserId
    private val friendUsername: String

    private inline fun updateUiState(transform: (ChatUiState) -> ChatUiState) {
        _uiState.update(transform)
    }

    private fun logTranslationIssue(message: String, throwable: Throwable) {
        if (throwable is FirebaseFunctionsException) {
            when (throwable.code) {
                FirebaseFunctionsException.Code.INVALID_ARGUMENT,
                FirebaseFunctionsException.Code.UNAUTHENTICATED,
                FirebaseFunctionsException.Code.PERMISSION_DENIED,
                FirebaseFunctionsException.Code.FAILED_PRECONDITION -> {
                    AppLogger.w("ChatViewModel", message, throwable)
                    return
                }
                else -> Unit
            }
        }

        AppLogger.e("ChatViewModel", message, throwable)
    }

    init {
        // Get navigation arguments
        friendId = UserId(checkNotNull(savedStateHandle.get<String>("friendId")))
        friendUsername = checkNotNull(savedStateHandle.get<String>("friendUsername"))

        // Restore draft message from SavedStateHandle (survives process death)
        val restoredDraft = savedStateHandle.get<String>("draft_message").orEmpty()

        updateUiState {
            it.copy(
            friendUsername = friendUsername,
            messageText = restoredDraft
            )
        }

        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        val isFirstLogin = currentUserId == null
                        currentUserId = UserId(auth.user.uid)
                        updateUiState { it.copy(currentUserId = auth.user.uid) }
                        // Load cleared timestamp first, then start message observation
                        loadClearedAt(UserId(auth.user.uid))
                        if (isChatScreenActive) {
                            markMessagesAsRead(UserId(auth.user.uid))
                        }

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
                            friendUsername = friendUsername
                        )
                    }
                    AuthState.Loading -> {
                        updateUiState { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    /** Load the user's clearedAt timestamp, then start observing messages filtered by it. */
    private fun loadClearedAt(userId: UserId) {
        viewModelScope.launch {
            try {
                val chatId = chatRepository.generateChatId(userId, friendId)
                val clearedAt = chatRepository.getClearedAt(chatId, userId)
                updateUiState { it.copy(clearedAt = clearedAt) }
            } catch (e: Exception) {
                android.util.Log.w("ChatViewModel", "getClearedAt failed, loading unfiltered messages", e)
            }
            loadMessages(userId)
        }
    }

    private fun loadMessages(userId: UserId) {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            try {
                observeMessagesUseCase(userId, friendId, _uiState.value.clearedAt).collect { messages ->
                    updateUiState { it.copy(messages = messages, isLoading = false) }
                    // Debounce mark-as-read so rapid message updates don't trigger
                    // redundant Firestore writes
                    if (isChatScreenActive) {
                        markReadJob?.cancel()
                        markReadJob = launch {
                            kotlinx.coroutines.delay(300)
                            markMessagesAsRead(userId)
                        }
                    }
                }
            } catch (_: kotlinx.coroutines.CancellationException) {
                // Normal cancellation — e.g. user navigated away or loadMessages called again
            } catch (e: Exception) {
                AppLogger.e("ChatViewModel", "loadMessages failed", e)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load messages. Please try again."
                    )
                }
            }
        }
    }

    fun onScreenVisibilityChanged(isVisible: Boolean) {
        if (isChatScreenActive == isVisible) return
        isChatScreenActive = isVisible
        if (!isVisible) {
            markReadJob?.cancel()
            return
        }
        currentUserId?.let { markMessagesAsRead(it) }
    }

    private fun markMessagesAsRead(userId: UserId) {
        if (!isChatScreenActive) return
        viewModelScope.launch {
            try {
                markMessagesAsReadUseCase(userId, friendId)
                // Mark this friend's messages as seen (in-memory + persisted) via a single source.
                sharedFriendsDataSource.markMessageFriendSeen(friendId.value)
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
                updateUiState { it.copy(friendProfile = profile) }
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
                updateUiState { it.copy(isBlocked = blocked, isBlockedBy = blockedBy) }
            } catch (e: Exception) {
                android.util.Log.w("ChatViewModel", "Failed to check block status", e)
            }
        }
    }

    fun blockFriend() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            friendsRepository.blockUser(userId, friendId).fold(
                onSuccess = {
                    updateUiState { it.copy(isBlocked = true, error = null) }
                },
                onFailure = {
                    updateUiState { it.copy(error = "Failed to block user.") }
                }
            )
        }
    }

    fun unblockFriend() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            friendsRepository.unblockUser(userId, friendId).fold(
                onSuccess = {
                    updateUiState { it.copy(isBlocked = false, error = null) }
                },
                onFailure = {
                    updateUiState { it.copy(error = "Failed to unblock user.") }
                }
            )
        }
    }

    fun onMessageTextChange(text: String) {
        updateUiState { it.copy(messageText = text) }
        savedStateHandle["draft_message"] = text
    }

    fun sendMessage() {
        val userId = currentUserId ?: return
        val messageText = _uiState.value.messageText.trim()
        if (messageText.isBlank() || _uiState.value.isSending) return

        // Validate message length (max 2000 chars)
        val lengthValidation = validateTextLength(messageText, minLength = 1, maxLength = 2000, fieldName = "Message")
        if (lengthValidation is ValidationResult.Invalid) {
            updateUiState { it.copy(error = lengthValidation.message) }
            return
        }

        // Check rate limiter
        if (!messageRateLimiter.isAllowed(userId.value)) {
            updateUiState { it.copy(error = "You are sending messages too quickly. Please wait a moment.") }
            return
        }

        // Prevent sending if blocked by the friend
        if (_uiState.value.isBlockedBy) {
            updateUiState { it.copy(error = "You cannot send messages to this user.") }
            return
        }

        // Sanitize message content before sending
        val sanitizedMessage = sanitizeInput(messageText)

        viewModelScope.launch {
            updateUiState { it.copy(isSending = true, error = null) }
            val chatId = chatRepository.generateChatId(userId, friendId)
            val senderUsername = sharedFriendsDataSource.getCachedUsername(userId.value).orEmpty()
            val result = sendMessageUseCase(
                chatId = chatId,
                fromUserId = userId,
                toUserId = friendId,
                content = sanitizedMessage,
                senderUsername = senderUsername
            )
            result.fold(
                onSuccess = {
                    updateUiState { it.copy(messageText = "", isSending = false) }
                    savedStateHandle["draft_message"] = ""
                },
                onFailure = { error ->
                    updateUiState {
                        it.copy(
                            error = error.message ?: "Failed to send message",
                            isSending = false
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        updateUiState { it.copy(error = null) }
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
            updateUiState { it.copy(isLoadingOlder = true) }
            try {
                val olderMessages = chatRepository.loadOlderMessages(
                    chatId = chatId,
                    beforeTimestamp = oldest.createdAt,
                    limit = 25
                )
                updateUiState { state ->
                    val merged = (olderMessages + state.messages)
                        .distinctBy { it.messageId }
                    state.copy(
                        messages = merged,
                        isLoadingOlder = false,
                        hasMoreMessages = olderMessages.size >= 25
                    )
                }
            } catch (_: Exception) {
                updateUiState { it.copy(isLoadingOlder = false) }
            }
        }
    }

    fun translateAllMessages() {
        val userId = currentUserId ?: return
        val messages = _uiState.value.messages
        if (messages.isEmpty() || _uiState.value.isTranslating) return

        viewModelScope.launch {
            updateUiState { it.copy(isTranslating = true, translationError = null) }
            try {
                val settings = userSettingsRepository.fetchUserSettings(userId)
                val targetLanguage = settings.primaryLanguageCode.ifBlank { "en-US" }

                // Only translate friend's messages not already in the cache
                val existingCache = _uiState.value.translatedMessages
                val uncachedMessages = messages.filter { msg ->
                    msg.senderId != userId.value && !existingCache.containsKey(msg.content)
                }

                if (uncachedMessages.isEmpty()) {
                    // All already cached — just show translations
                    updateUiState { it.copy(showTranslation = true, isTranslating = false) }
                    return@launch
                }

                // Build a messages list with only uncached items for the use case
                val result = translateAllMessagesUseCase(uncachedMessages, targetLanguage, userId.value)
                result.fold(
                    onSuccess = { newTranslations ->
                        // Merge new translations into existing cache
                        updateUiState { state ->
                            state.copy(
                                translatedMessages = state.translatedMessages + newTranslations,
                                showTranslation = true,
                                isTranslating = false
                            )
                        }
                    },
                    onFailure = { error ->
                        logTranslationIssue("translateAllMessages failed", error)
                        updateUiState {
                            it.copy(
                                translationError = "Translation failed. Please try again.",
                                isTranslating = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                logTranslationIssue("translateAllMessages catch failed", e)
                updateUiState {
                    it.copy(
                        translationError = "Translation failed. Please try again.",
                        isTranslating = false
                    )
                }
            }
        }
    }

    /**
     * Persists a "clearedAt" timestamp for this user in Firestore so that
     * messages sent before this moment are permanently hidden — even after
     * re-entering the chat or re-adding the friend.
     * The actual Firestore documents are NOT deleted; only the view is filtered.
     */
    fun clearConversation() {
        val userId = currentUserId ?: return
        val chatId = chatRepository.generateChatId(userId, friendId)
        val now = com.google.firebase.Timestamp.now()

        // Persist the clearedAt timestamp
        viewModelScope.launch {
            chatRepository.clearConversationForUser(chatId, userId)
        }

        // Stop observing and clear local state
        messagesJob?.cancel()
        updateUiState {
            it.copy(
                messages = emptyList(),
                translatedMessages = emptyMap(),
                showTranslation = false,
                clearSuccess = true,
                hasMoreMessages = false,
                clearedAt = now
            )
        }

        // Restart message observation with the new clearedAt filter
        // so incoming messages after the clear are still displayed.
        loadMessages(userId)
    }

    fun dismissClearSuccess() {
        updateUiState { it.copy(clearSuccess = false) }
    }

    fun toggleTranslation() {
        updateUiState { it.copy(showTranslation = !it.showTranslation) }
    }

    fun clearTranslation() {
        updateUiState {
            it.copy(
                translatedMessages = emptyMap(),
                showTranslation = false,
                translationError = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        isChatScreenActive = false
        messagesJob?.cancel()
        profileLoadJob?.cancel()
        markReadJob?.cancel()
    }
}
