package com.example.fyp.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.GetCurrentUserProfileUseCase
import com.example.fyp.model.UserId
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
    val successMessage: String? = null,
    val isUpdatingVisibility: Boolean = false
)

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val sharedFriendsDataSource: SharedFriendsDataSource,
    private val friendsRepository: FriendsRepository
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
                var profile = getCurrentUserProfileUseCase(UserId(userId))
                // Auto-create profile if it doesn't exist
                if (profile == null) {
                    val newProfile = PublicUserProfile(
                        uid = userId,
                        username = "",
                        displayName = "",
                        avatarUrl = "",
                        primaryLanguage = "",
                        learningLanguages = emptyList(),
                        isDiscoverable = true,
                        createdAt = com.google.firebase.Timestamp.now(),
                        lastActiveAt = com.google.firebase.Timestamp.now()
                    )
                    friendsRepository.createOrUpdatePublicProfile(UserId(userId), newProfile)
                    profile = newProfile
                }
                // Cache own username so share operations don't need a Firestore profile read
                if (profile.username.isNotBlank()) {
                    sharedFriendsDataSource.cacheOwnUsername(userId, profile.username)
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        error = null
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

    fun updateVisibility(isPublic: Boolean) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingVisibility = true) }
            friendsRepository.updatePublicProfile(
                UserId(userId),
                mapOf("isDiscoverable" to isPublic)
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isUpdatingVisibility = false,
                        profile = it.profile?.copy(isDiscoverable = isPublic)
                    )
                }
                showSuccessMessage(if (isPublic) "Profile set to Public" else "Profile set to Private")
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isUpdatingVisibility = false, error = e.message ?: "Failed to update visibility")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshProfile() {
        currentUserId?.let { loadProfile(it) }
    }
}
