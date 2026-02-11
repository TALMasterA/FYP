package com.example.fyp.core

import android.util.Log
import com.example.fyp.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

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
            crashlytics.log("E/$tag: $message")
            if (throwable != null) {
                crashlytics.recordException(throwable)
            }
        } catch (_: Exception) {
            // Crashlytics not initialized yet (e.g., during early startup)
        }
    }
}

