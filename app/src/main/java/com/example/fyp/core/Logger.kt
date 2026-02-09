package com.example.fyp.core

import android.util.Log
import com.example.fyp.BuildConfig

/**
 * Centralized logger for the FYP application.
 * Provides consistent logging across the app with support for different log levels.
 * Can be extended to integrate with crash reporting (Crashlytics) or analytics.
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
     * Log error message
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("FYP:$tag", message, throwable)
        // TODO: Integrate with Firebase Crashlytics for production error tracking
        // FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}

