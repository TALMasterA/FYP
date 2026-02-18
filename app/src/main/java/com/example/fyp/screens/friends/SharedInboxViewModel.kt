package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.domain.friends.AcceptSharedItemUseCase
import com.example.fyp.domain.friends.DismissSharedItemUseCase
import com.example.fyp.domain.friends.ObserveSharedInboxUseCase
import com.example.fyp.model.ValueTypes.UserId
import com.example.fyp.model.friends.SharedItem
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var observeJob: kotlinx.coroutines.Job? = null

    init {
        observeInbox()
    }

    private fun observeInbox() {
        val currentUserId = authRepository.getCurrentUserId() ?: return
        
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            try {
                observeSharedInboxUseCase(UserId(currentUserId)).collect { items ->
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
        val currentUserId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            
            acceptSharedItemUseCase(itemId, UserId(currentUserId))
                .onSuccess {
                    _uiState.update { it.copy(
                        isProcessing = false,
                        successMessage = "Added to your collection"
                    )}
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
        val currentUserId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            
            dismissSharedItemUseCase(itemId, UserId(currentUserId))
                .onSuccess {
                    _uiState.update { it.copy(
                        isProcessing = false,
                        successMessage = "Item dismissed"
                    )}
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
