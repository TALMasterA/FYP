package com.translator.TalknLearn

import android.app.Application
import android.os.StrictMode
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.translator.TalknLearn.core.FcmNotificationService
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

        // Install Firebase App Check provider so that callable Cloud Functions
        // (which enforce App Check) accept requests from this app. Debug builds
        // use the debug provider so a developer-issued debug token can be
        // registered in the Firebase console; release builds use Play Integrity.
        try {
            val factory = if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)

            // In debug builds: proactively fetch the App Check token so App Check setup
            // failures appear immediately in Logcat before translation or speech calls.
            if (BuildConfig.DEBUG) {
                FirebaseAppCheck.getInstance().getToken(false)
                    .addOnSuccessListener { _ ->
                        android.util.Log.d("AppCheckSetup", "✓ App Check token obtained. Translation/Speech should work.")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("AppCheckSetup", "✗ App Check FAILED: ${e.message}")
                        android.util.Log.e("AppCheckSetup",
                            "ACTION REQUIRED: confirm local.properties has appCheckDebugToken set " +
                            "to a Firebase Console debug token for com.translator.TalknLearn. " +
                            "If no local token is configured, search Logcat for the Firebase SDK " +
                            "debug-secret message and add that generated UUID in App Check → " +
                            "Apps → Manage debug tokens.")
                    }
            }
        } catch (e: Exception) {
            android.util.Log.e("FYPApplication", "App Check initialization failed", e)
        }

        // Firestore offline persistence + 50 MB cache cap is configured exclusively in
        // DaggerModule.provideFirestore() using PersistentCacheSettings (the modern API).
        // Do not configure FirebaseFirestoreSettings here — the first caller wins and
        // the loser silently throws, hiding misconfiguration.

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