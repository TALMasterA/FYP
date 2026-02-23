package com.example.fyp

import android.app.Application
import android.os.StrictMode
import com.google.firebase.FirebaseApp
import com.example.fyp.core.FcmNotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FYPApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Enable StrictMode in debug builds for ANR prevention
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        
        // Initialize Firebase with error handling
        try {
            FirebaseApp.initializeApp(this)
            android.util.Log.d("FYPApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("FYPApplication", "Firebase initialization failed", e)
        }

        // Create notification channels for push notifications (required on Android 8+)
        try {
            FcmNotificationService.createNotificationChannels(this)
        } catch (e: Exception) {
            android.util.Log.e("FYPApplication", "Failed to create notification channels", e)
        }
    }
    
    /**
     * Enables StrictMode to detect performance issues during development
     * - Detects disk reads/writes on main thread
     * - Detects network operations on main thread  
     * - Helps prevent ANR (Application Not Responding) issues
     */
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .penaltyLog() // Log violations instead of crashing
                .build()
        )
        
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .penaltyLog()
                .build()
        )
    }
}