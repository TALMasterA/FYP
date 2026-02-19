package com.example.fyp

import android.app.Application
import android.os.StrictMode
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FYPApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Enable StrictMode in debug builds for ANR prevention
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        
        FirebaseApp.initializeApp(this)
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