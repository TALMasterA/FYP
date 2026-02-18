package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.AcceptSharedItemUseCase
import com.example.fyp.domain.friends.DismissSharedItemUseCase
import com.example.fyp.domain.friends.ObserveSharedInboxUseCase
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedInboxUiState(
    val isLoading: Boolean = true,
    val sharedItems: List<SharedItem> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val isProcessing: Boolean = false
)

@HiltViewModel
class SharedInboxViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val observeSharedInboxUseCase: ObserveSharedInboxUseCase,
    private val acceptSharedItemUseCase: AcceptSharedItemUseCase,
    private val dismissSharedItemUseCase: DismissSharedItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharedInboxUiState())
    val uiState: StateFlow<SharedInboxUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var currentUserId: UserId? = null

    init {
        viewModelScope.launch {
            authRepository.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = UserId(auth.user.uid)
                        observeInbox(UserId(auth.user.uid))
                    }
                    is AuthState.LoggedOut -> {
                        currentUserId = null
                        observeJob?.cancel()
                        _uiState.update { SharedInboxUiState() }
                    }
                }
            }
        }
    }

    private fun observeInbox(userId: UserId) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            try {
                observeSharedInboxUseCase(userId).collect { items ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        sharedItems = items,
                        error = null
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load shared items"
                )}
            }
        }
    }

    fun acceptItem(itemId: String) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            
            acceptSharedItemUseCase(itemId, userId)
                .onSuccess {
                    _uiState.update { it.copy(
                        isProcessing = false,
                        successMessage = "Added to your collection"
                    )}
                    // Clear success message after 3 seconds
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isProcessing = false,
                        error = error.message ?: "Failed to accept item"
                    )}
                }
        }
    }

    fun dismissItem(itemId: String) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            
            dismissSharedItemUseCase(itemId, userId)
                .onSuccess {
                    _uiState.update { it.copy(
                        isProcessing = false,
                        successMessage = "Item dismissed"
                    )}
                    // Clear success message after 3 seconds
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isProcessing = false,
                        error = error.message ?: "Failed to dismiss item"
                    )}
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(
            error = null,
            successMessage = null
        )}
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }
}
