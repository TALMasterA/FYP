package com.example.fyp

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FYPApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Check if app was updated and force logout if needed
        checkAndHandleAppUpdate()
    }

    /**
     * Check if app version changed since last run.
     * If yes, clear all local preferences to force re-login.
     */
    private fun checkAndHandleAppUpdate() {
        try {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

            // Get current app version code
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val currentVersionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }

            // Get last stored version code from preferences
            val lastVersionCode = prefs.getInt("last_version_code", -1)

            // If this is an update (not first install), logout user
            if (lastVersionCode != -1 && currentVersionCode > lastVersionCode) {
                logoutUserOnUpdate(prefs)
            }

            // Store current version for next app start
            prefs.edit().putInt("last_version_code", currentVersionCode).apply()

        } catch (e: Exception) {
            e.printStackTrace()
            // Safe to ignore - logout check will skip on error
        }
    }

    /**
     * Clear all local preferences on app update.
     * This invalidates old sessions and forces re-authentication.
     */
    private fun logoutUserOnUpdate(prefs: SharedPreferences) {
        prefs.edit().clear().apply()
        // Firebase auth will automatically clear when app loads
        // AuthViewModel will detect LoggedOut state and show Login screen
    }
}