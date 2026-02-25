package com.example.fyp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fyp.core.AudioRecorder
import com.example.fyp.ui.theme.FYPTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

private const val PREFS_NAME = "app_update_prefs"
private const val KEY_LAST_VERSION_CODE = "last_version_code"
private const val KEY_LOGOUT_REASON = "logout_reason"
private const val LOGOUT_REASON_UPDATED = "updated"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen — must be called before super.onCreate / setContent.
        // The splash is shown with Theme.FYP.Splash (ic_splash_logo on #FAFAFA background).
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastVersion = prefs.getInt(KEY_LAST_VERSION_CODE, -1)
        val currentVersion = BuildConfig.VERSION_CODE
        val isUpdate = (lastVersion != -1 && lastVersion != currentVersion)

        if (isUpdate) {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                auth.signOut()
                prefs.edit().putString(KEY_LOGOUT_REASON, LOGOUT_REASON_UPDATED).apply()
            } else {
                prefs.edit().remove(KEY_LOGOUT_REASON).apply()
            }
        }

        prefs.edit().putInt(KEY_LAST_VERSION_CODE, currentVersion).apply()

        setContent {
            FYPTheme { AppNavigation() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioRecorder.stopIfRecording()
    }
}