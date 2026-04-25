package com.example.fyp.core

import com.example.fyp.core.security.SecureStorage
import com.example.fyp.data.cloud.LanguageDetectionCache
import com.example.fyp.data.cloud.TranslationCache
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clears every per-session local cache that survives a Firebase Auth signOut.
 *
 * Invoked from the logout path ([com.example.fyp.screens.login.AuthViewModel.logout])
 * and from the account-deletion path
 * ([com.example.fyp.screens.settings.ProfileViewModel.deleteAccount]) so that the
 * next user on a shared device cannot read prior cached translations, language
 * detections, OkHttp responses, or Azure Speech session tokens.
 *
 * Targets that are *not* covered here because they are already cleaned elsewhere:
 *  - WordBank DataStore: [com.example.fyp.appstate.AppViewModel] invalidates it on
 *    `AuthState.LoggedOut` via `wordBankCacheDataStore.invalidateAllForUser(uid)`.
 *  - FCM device token: [com.example.fyp.core.notifications.FcmNotificationService.removeTokenOnSignOut]
 *    is called from `FirebaseAuthRepository.logout()` and clears the cached token in
 *    [SecureStorage] under [SecureStorage.KEY_FCM_TOKEN].
 *
 * If a new per-user DataStore or sensitive cache is added, register it here AND in
 * `SessionDataCleanerRegistrationTest` to keep this contract enforceable.
 */
@Singleton
class SessionDataCleaner @Inject constructor(
    private val secureStorage: SecureStorage,
    private val okHttpClient: OkHttpClient,
    private val translationCache: TranslationCache,
    private val languageDetectionCache: LanguageDetectionCache,
) {

    /**
     * Wipes session-scoped caches. Each step is independently guarded so a
     * single failure cannot leave the others uncleared.
     */
    suspend fun clearSessionData() {
        runCatching { secureStorage.clearSessionTokens() }
            .onFailure { android.util.Log.w(TAG, "clearSessionTokens failed", it) }

        runCatching { okHttpClient.cache?.evictAll() }
            .onFailure { android.util.Log.w(TAG, "okhttp evictAll failed", it) }

        runCatching { translationCache.clearAll() }
            .onFailure { android.util.Log.w(TAG, "translationCache.clearAll failed", it) }

        runCatching { languageDetectionCache.clearAll() }
            .onFailure { android.util.Log.w(TAG, "languageDetectionCache.clearAll failed", it) }
    }

    private companion object {
        const val TAG = "SessionDataCleaner"
    }
}
