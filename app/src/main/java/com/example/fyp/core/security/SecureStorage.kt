package com.example.fyp.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Wrapper around [EncryptedSharedPreferences] that uses the Android Keystore
 * to protect sensitive data at rest (e.g., FCM tokens, session tokens).
 *
 * Data is encrypted with AES-256 GCM; the encryption key lives in the
 * hardware-backed Android Keystore and never leaves the device.
 *
 * Usage:
 * ```
 * val secure = SecureStorage(applicationContext)
 * secure.putString("fcm_token", token)
 * val token = secure.getString("fcm_token")
 * ```
 */
class SecureStorage(context: Context) {

    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /** Stores a string value securely. */
    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    /** Retrieves a securely stored string, or null if absent. */
    fun getString(key: String): String? = prefs.getString(key, null)

    /** Stores a boolean value securely. */
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    /** Retrieves a securely stored boolean (default false). */
    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    /** Stores a long value securely. */
    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    /** Retrieves a securely stored long (default 0). */
    fun getLong(key: String, default: Long = 0L): Long =
        prefs.getLong(key, default)

    /** Removes a single key. */
    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    /** Clears all securely stored data. */
    fun clear() {
        prefs.edit().clear().apply()
    }

    /** Returns true if the key exists. */
    fun contains(key: String): Boolean = prefs.contains(key)

    companion object {
        private const val PREFS_FILE_NAME = "fyp_secure_prefs"

        /** Well-known keys for common secure values. */
        object Keys {
            const val FCM_TOKEN = "fcm_token"
            const val SESSION_TOKEN = "session_token"
            const val LAST_AUTH_TIME = "last_auth_time"
        }
    }
}
