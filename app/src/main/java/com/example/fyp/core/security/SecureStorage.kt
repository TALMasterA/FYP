package com.example.fyp.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Keystore-backed [EncryptedSharedPreferences] for sensitive local data.
 *
 * The master key is protected by the hardware-backed Android Keystore, ensuring
 * encryption keys cannot be extracted even with root access or backup extraction.
 *
 * Used for FCM device-token caching, session-token caching (e.g. Azure Speech
 * service tokens), rate-limiter timestamps, and notification seen-state persistence.
 */
@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getString(key: String, defaultValue: String? = null): String? =
        prefs.getString(key, defaultValue)

    fun putString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long =
        prefs.getLong(key, defaultValue)

    /**
     * Wipe Azure Speech session-token state (token, region, issuance time).
     *
     * Must be invoked by [com.example.fyp.core.SessionDataCleaner] on logout
     * and after account deletion so a subsequent user on a shared device
     * cannot reuse a cached speech token.
     */
    fun clearSessionTokens() {
        prefs.edit()
            .remove(KEY_SESSION_TOKEN)
            .remove(KEY_SESSION_TOKEN_REGION)
            .remove(KEY_SESSION_TOKEN_TIME)
            .apply()
    }

    init {
        @Suppress("LeakingThis")
        staticInstance = this
    }

    companion object {
        private const val PREFS_FILENAME = "secure_prefs"
        const val KEY_FCM_TOKEN = "fcm_token"
        const val KEY_SESSION_TOKEN = "session_token"
        const val KEY_SESSION_TOKEN_REGION = "session_token_region"
        const val KEY_SESSION_TOKEN_TIME = "session_token_time"

        @Volatile
        private var staticInstance: SecureStorage? = null

        /**
         * Obtain a [SecureStorage] without Hilt injection.
         *
         * Returns the Hilt-managed singleton when available; otherwise creates
         * a standalone instance backed by the same encrypted preference file.
         */
        fun forContext(context: Context): SecureStorage {
            return staticInstance ?: synchronized(this) {
                staticInstance ?: SecureStorage(context.applicationContext).also {
                    staticInstance = it
                }
            }
        }

        /** Access the singleton without context; null before first init. */
        internal fun instance(): SecureStorage? = staticInstance
    }
}
