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

        FirebaseApp.initializeApp(this)
        checkAndHandleAppUpdate()
    }

    private fun checkAndHandleAppUpdate() {
        try {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val currentVersionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }

            val lastVersionCode = prefs.getInt("last_version_code", -1)

            if (lastVersionCode != -1 && currentVersionCode > lastVersionCode) {
                logoutUserOnUpdate(prefs)
            }

            prefs.edit().putInt("last_version_code", currentVersionCode).apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logoutUserOnUpdate(prefs: SharedPreferences) {
        prefs.edit().clear().apply()
    }
}