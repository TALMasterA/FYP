package com.example.fyp.screens.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedMaterialDetailUiState(
    val isLoading: Boolean = true,
    val item: SharedItem? = null,
    val fullContent: String? = null,
    val error: String? = null
)

/**
 * Dedicated ViewModel for SharedMaterialDetailScreen.
 *
 * Independently loads a single shared item and its full content from Firestore.
 * This avoids the ViewModel-sharing problem where a fresh SharedInboxViewModel
 * wouldn't have the item in its list yet.
 */
@HiltViewModel
class SharedMaterialDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: FirebaseAuthRepository,
    private val sharingRepository: SharingRepository
) : ViewModel() {

    private val itemId: String = savedStateHandle.get<String>("itemId") ?: ""

    private val _uiState = MutableStateFlow(SharedMaterialDetailUiState())
    val uiState: StateFlow<SharedMaterialDetailUiState> = _uiState.asStateFlow()

    init {
        if (itemId.isNotBlank()) {
            loadItem()
        } else {
            _uiState.value = SharedMaterialDetailUiState(isLoading = false, error = "No item ID")
        }
    }

    private fun loadItem() {
        val currentUser = authRepository.currentUser ?: run {
            _uiState.value = SharedMaterialDetailUiState(isLoading = false, error = "Not logged in")
            return
        }
        val userId = UserId(currentUser.uid)

        viewModelScope.launch {
            try {
                // Fetch item metadata
                val item = sharingRepository.fetchSharedItemById(userId, itemId)
                if (item == null) {
                    _uiState.value = SharedMaterialDetailUiState(isLoading = false, error = "Item not found")
                    return@launch
                }

                // Show item immediately with loading indicator for full content
                _uiState.value = SharedMaterialDetailUiState(isLoading = false, item = item)

                // Fetch full content from sub-document
                val fullContent = sharingRepository.fetchSharedItemFullContent(userId, itemId)
                _uiState.value = _uiState.value.copy(fullContent = fullContent ?: "")
            } catch (e: Exception) {
                _uiState.value = SharedMaterialDetailUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load"
                )
            }
        }
    }
}

