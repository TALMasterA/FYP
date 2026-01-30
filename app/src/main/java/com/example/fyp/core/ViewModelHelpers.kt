// For future use
@file:Suppress("unused")

package com.example.fyp.core

import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.model.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * Helper for ViewModels that need to observe auth state and start/stop jobs accordingly.
 * Reduces boilerplate for the common "collect auth state → start/stop jobs" pattern.
 *
 * Usage in ViewModel init:
 * ```
 * init {
 *     collectLatestLoggedInUid(viewModelScope, authRepo) { uid ->
 *         // This block runs when user is logged in
 *         // Automatically cancelled when user logs out
 *         observeUserData(uid)
 *     }
 * }
 * ```
 */
fun collectLatestLoggedInUid(
    scope: CoroutineScope,
    authRepo: FirebaseAuthRepository,
    onLoading: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    onLoggedIn: suspend (uid: String) -> Unit
): Job {
    return scope.launch {
        authRepo.currentUserState.collectLatest { auth ->
            when (auth) {
                is AuthState.LoggedIn -> onLoggedIn(auth.user.uid)
                AuthState.Loading -> onLoading()
                AuthState.LoggedOut -> onLoggedOut()
            }
        }
    }
}

/**
 * Extension to transform a flow based on auth state.
 * Automatically switches to empty flow when logged out.
 *
 * Usage:
 * ```
 * authRepo.currentUserState
 *     .flatMapLatestLoggedIn { uid ->
 *         repository.observeUserData(uid)
 *     }
 *     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<AuthState>.flatMapLatestLoggedIn(
    transform: (uid: String) -> Flow<T>
): Flow<T?> = flatMapLatest { auth ->
    when (auth) {
        is AuthState.LoggedIn -> transform(auth.user.uid)
        else -> flowOf(null)
    }
}

/**
 * Generic cache keyed by primary + target language code.
 * Reduces duplication between LearningViewModel and WordBankViewModel caches.
 *
 * @param T The type of cached value
 */
class LanguagePairCache<T> {
    private val cache = mutableMapOf<String, T>()
    private var lastPrimaryCode: String? = null

    /**
     * Get cached value for a language code, or null if not cached.
     */
    operator fun get(languageCode: String): T? = cache[languageCode]

    /**
     * Set cached value for a language code.
     */
    operator fun set(languageCode: String, value: T) {
        cache[languageCode] = value
    }

    /**
     * Check if a language code is in the cache.
     */
    operator fun contains(languageCode: String): Boolean = languageCode in cache

    /**
     * Remove a specific language from cache (e.g., after generation).
     */
    fun invalidate(languageCode: String) {
        cache.remove(languageCode)
    }

    /**
     * Clear entire cache if primary language changed, otherwise no-op.
     * Returns true if cache was cleared.
     */
    fun clearIfPrimaryChanged(primaryCode: String): Boolean {
        return if (lastPrimaryCode != primaryCode) {
            cache.clear()
            lastPrimaryCode = primaryCode
            true
        } else {
            false
        }
    }

    /**
     * Get all cached keys (language codes).
     */
    fun keys(): Set<String> = cache.keys.toSet()

    /**
     * Clear the entire cache.
     */
    fun clear() {
        cache.clear()
        lastPrimaryCode = null
    }
}
