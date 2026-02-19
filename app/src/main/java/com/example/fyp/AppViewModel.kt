package com.example.fyp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.friends.EnsurePublicProfileExistsUseCase
import com.example.fyp.model.user.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application-level ViewModel that handles cross-cutting concerns
 * such as initializing user profile data when logging in.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val ensurePublicProfileExistsUseCase: EnsurePublicProfileExistsUseCase
) : ViewModel() {

    private var lastInitializedUserId: String? = null

    init {
        // Monitor authentication state and initialize profile when user logs in
        viewModelScope.launch {
            authRepository.currentUserState.collect { authState ->
                when (authState) {
                    is AuthState.LoggedIn -> {
                        // Only initialize if this is a new user session
                        if (lastInitializedUserId != authState.user.uid) {
                            lastInitializedUserId = authState.user.uid
                            initializeUserProfile(authState.user.uid)
                        }
                    }
                    is AuthState.LoggedOut -> {
                        lastInitializedUserId = null
                    }
                    is AuthState.Loading -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    private fun initializeUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                // Ensure public profile exists for friends feature
                ensurePublicProfileExistsUseCase(userId)
            } catch (e: Exception) {
                // Log error but don't crash the app
                android.util.Log.e("AppViewModel", "Failed to initialize profile for user $userId", e)
            }
        }
    }
}

