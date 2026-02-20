package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreProfileRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.Username
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.friends.PublicUserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: PublicUserProfile = PublicUserProfile(),
    val email: String? = null,
    val error: String? = null,
    val successMessage: String? = null,
    val isDeletingAccount: Boolean = false,
    val deleteError: String? = null,
    val accountDeleted: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val profileRepo: FirestoreProfileRepository,
    private val friendsRepo: FriendsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var profileJob: Job? = null
    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        _uiState.value = _uiState.value.copy(
                            email = auth.user.email
                        )
                        observeProfile(auth.user.uid)
                    }
                    AuthState.LoggedOut -> {
                        profileJob?.cancel()
                        currentUserId = null
                        _uiState.value = ProfileUiState()
                    }
                    AuthState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun observeProfile(userId: String) {
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            try {
                // Load public profile for friends feature.
                // If the profile document doesn't exist yet (new user), use an empty default —
                // no error is shown so the screen renders normally and the user can set a username.
                val profile = friendsRepo.getPublicProfile(UserId(userId))
                _uiState.value = _uiState.value.copy(
                    profile = profile ?: PublicUserProfile(),
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load profile",
                    isLoading = false
                )
            }
        }
    }

    fun updateUsername(username: String) {
        val userId = currentUserId ?: return

        // Validate username format
        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            _uiState.value = _uiState.value.copy(
                error = "Username can only contain letters, numbers, and underscores"
            )
            return
        }

        if (username.length !in 3..20) {
            _uiState.value = _uiState.value.copy(
                error = "Username must be 3-20 characters"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            // First, check if username is available
            val isAvailable = friendsRepo.isUsernameAvailable(Username(username))
            if (!isAvailable && username != _uiState.value.profile.username) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Username already taken"
                )
                return@launch
            }

            // Update username in the registry
            friendsRepo.setUsername(UserId(userId), Username(username))
                .onSuccess {
                    // Update public profile
                    friendsRepo.updatePublicProfile(
                        UserId(userId),
                        mapOf("username" to username)
                    ).onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Username updated successfully"
                        )
                        // Reload profile
                        observeProfile(userId)
                    }.onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to update profile"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to set username"
                    )
                }
        }
    }

    fun updateDisplayName(displayName: String) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            profileRepo.updateDisplayName(userId, displayName)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Profile updated successfully"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update profile"
                    )
                }
        }
    }

    fun deleteAccount(password: String) {
        val userId = currentUserId ?: return
        val email = _uiState.value.email ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeletingAccount = true,
                deleteError = null
            )

            // First re-authenticate
            profileRepo.reauthenticate(email, password)
                .onSuccess {
                    // Then delete account
                    profileRepo.deleteAccount(userId)
                        .onSuccess {
                            _uiState.value = _uiState.value.copy(
                                isDeletingAccount = false,
                                accountDeleted = true
                            )
                        }
                        .onFailure { e ->
                            _uiState.value = _uiState.value.copy(
                                isDeletingAccount = false,
                                deleteError = e.message ?: "Failed to delete account"
                            )
                        }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isDeletingAccount = false,
                        deleteError = "Invalid password. Please try again."
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearDeleteError() {
        _uiState.value = _uiState.value.copy(deleteError = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
