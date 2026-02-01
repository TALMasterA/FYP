package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.profile.FirestoreProfileRepository
import com.example.fyp.model.AuthState
import com.example.fyp.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile = UserProfile(),
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
    private val profileRepo: FirestoreProfileRepository
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
            profileRepo.observeProfile(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to load profile"
                    )
                }
                .collect { profile ->
                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        isLoading = false
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
}
