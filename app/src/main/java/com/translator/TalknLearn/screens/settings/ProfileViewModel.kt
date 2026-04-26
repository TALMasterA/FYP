package com.translator.TalknLearn.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.data.user.FirestoreProfileRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.Username
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.friends.PublicUserProfile
import com.translator.TalknLearn.core.SessionDataCleaner
import com.translator.TalknLearn.core.security.AuditLogger
import com.translator.TalknLearn.core.security.ValidationResult
import com.translator.TalknLearn.core.security.sanitizeInput
import com.translator.TalknLearn.core.security.validateUsername
import com.translator.TalknLearn.data.settings.FirestoreUserSettingsRepository
import com.translator.TalknLearn.model.user.UserSettings
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
    val accountDeleted: Boolean = false,
    val usernameCooldownDays: Int? = null,
    val usernameCooldownHours: Int? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val profileRepo: FirestoreProfileRepository,
    private val friendsRepo: FriendsRepository,
    private val settingsRepo: FirestoreUserSettingsRepository,
    private val sessionDataCleaner: SessionDataCleaner
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

        // Use shared validation function for consistent username rules
        val validation = validateUsername(username, minLength = 3, maxLength = 20)
        if (validation is ValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(error = validation.message)
            return
        }

        // Skip cooldown check if the username is unchanged
        if (username == _uiState.value.profile.username) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            // Check cooldown before proceeding
            try {
                val settings = settingsRepo.fetchUserSettings(UserId(userId))
                val now = System.currentTimeMillis()
                if (!UserSettings.canChangeUsername(settings.lastUsernameChangeMs, now)) {
                    val remainingMs = UserSettings.usernameCooldownRemainingMs(
                        settings.lastUsernameChangeMs, now
                    )
                    val totalHours = ((remainingMs / (60 * 60 * 1000)) + 1).toInt()
                    val days = totalHours / 24
                    val hours = totalHours % 24
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        usernameCooldownDays = days,
                        usernameCooldownHours = hours
                    )
                    return@launch
                }
            } catch (e: Exception) {
                // Fail closed: block username change if we can't verify cooldown
                android.util.Log.w("ProfileViewModel", "Settings fetch failed during cooldown check", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unable to verify account status. Please try again."
                )
                return@launch
            }

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
                    // Record the cooldown timestamp
                    runCatching {
                        settingsRepo.setLastUsernameChangeMs(UserId(userId), System.currentTimeMillis())
                    }
                    // Update public profile
                    friendsRepo.updatePublicProfile(
                        UserId(userId),
                        mapOf("username" to username)
                    ).onSuccess {
                        // Propagate the new username to all friends' cached FriendRelation docs
                        // so they see the updated name immediately in their Friends list.
                        val propagateResult = friendsRepo.propagateUsernameChange(UserId(userId), username)
                        val successMsg = if (propagateResult.isFailure) {
                            "Username updated. Your friends list may take a moment to refresh."
                        } else {
                            "Username updated successfully"
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = successMsg
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

    fun dismissUsernameCooldownDialog() {
        _uiState.value = _uiState.value.copy(usernameCooldownDays = null, usernameCooldownHours = null)
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
                            AuditLogger.logAccountDeleted(userId = userId)
                            // Wipe session-scoped local caches before signalling success.
                            // Failures are swallowed individually inside the cleaner so they
                            // cannot block the deleted-account UI signal.
                            runCatching { sessionDataCleaner.clearSessionData() }
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

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        profileJob?.cancel()
    }
}
