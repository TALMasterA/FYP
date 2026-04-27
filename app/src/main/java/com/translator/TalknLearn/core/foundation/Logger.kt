package com.translator.TalknLearn.core

import android.util.Log
import com.translator.TalknLearn.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.security.MessageDigest

/**
 * Centralized logger for the FYP application.
 * Provides consistent logging across the app with support for different log levels.
 * Integrates with Firebase Crashlytics for production error tracking.
 */
object AppLogger {

    /**
     * Log debug message (only in DEBUG builds)
     */
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("FYP:$tag", message)
        }
    }

    /**
     * Log info message
     */
    fun i(tag: String, message: String) {
        Log.i("FYP:$tag", message)
    }

    /**
     * Log warning message
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w("FYP:$tag", message, throwable)
    }

    /**
     * Log error message and report to Crashlytics for production monitoring.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("FYP:$tag", message, throwable)
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            val safeTag = crashlyticsSafeTag(tag)
            crashlytics.log("E/$safeTag")
            crashlytics.setCustomKey("last_error_tag", safeTag)
            crashlytics.setCustomKey("last_error_message_hash", messageHash(message))
            if (throwable != null) {
                crashlytics.recordException(throwable)
            }
        } catch (_: Exception) {
            // Crashlytics not initialized yet (e.g., during early startup)
        }
    }

    private fun crashlyticsSafeTag(tag: String): String =
        tag.replace(Regex("[^A-Za-z0-9_.-]"), "_").take(64).ifBlank { "unknown" }

    private fun messageHash(message: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(message.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }.take(16)
    }
}

