package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.GetCurrentUserProfileUseCase
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.friends.PublicUserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyProfileUiState(
    val isLoading: Boolean = true,
    val profile: PublicUserProfile? = null,
    val userId: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            authRepository.currentUserState.collect { authState ->
                when (authState) {
                    is AuthState.LoggedIn -> {
                        currentUserId = authState.user.uid
                        _uiState.update { it.copy(userId = authState.user.uid) }
                        loadProfile(authState.user.uid)
                    }
                    is AuthState.LoggedOut -> {
                        currentUserId = null
                        _uiState.update { MyProfileUiState() }
                    }
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = getCurrentUserProfileUseCase(userId)
                // Cache own username so share operations don't need a Firestore profile read
                if (profile?.username?.isNotBlank() == true) {
                    sharedFriendsDataSource.cacheOwnUsername(userId, profile.username)
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        error = if (profile == null) "Profile not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load profile")
                }
            }
        }
    }

    fun showSuccessMessage(message: String) {
        _uiState.update { it.copy(successMessage = message) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(successMessage = null) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshProfile() {
        currentUserId?.let { loadProfile(it) }
    }
}
