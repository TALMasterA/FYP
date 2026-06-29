package com.translator.TalknLearn

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.translator.TalknLearn.ui.theme.FYPTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val PREFS_NAME = "app_update_prefs"
private const val KEY_LAST_VERSION_CODE = "last_version_code"
private const val KEY_LOGOUT_REASON = "logout_reason"
private const val LOGOUT_REASON_UPDATED = "updated"

/**
 * Single-Activity entry point for the FYP language-learning app.
 *
 * On each launch the Activity checks whether the app version has changed
 * since the last run. If an update is detected the current Firebase user
 * is signed out to force re-authentication (ensuring token freshness and
 * Firestore rule compatibility). The Compose navigation graph is set as
 * the content root via [AppNavigation].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastVersion = prefs.getInt(KEY_LAST_VERSION_CODE, -1)
        val currentVersion = BuildConfig.VERSION_CODE
        val isUpdate = (lastVersion != -1 && lastVersion != currentVersion)

        // Sign-out decision stays synchronous: it must settle before the nav graph
        // reads the current user. Disk writes are deferred off the main thread so
        // they no longer block the first frame.
        val signedOutForUpdate = isUpdate && FirebaseAuth.getInstance().currentUser != null
        if (signedOutForUpdate) {
            FirebaseAuth.getInstance().signOut()
        }
        if (isUpdate) {
            persistUpdateBookkeeping(prefs, currentVersion, signedOutForUpdate)
        } else {
            persistVersionCode(prefs, currentVersion)
        }

        setContent {
            FYPTheme { AppNavigation() }
        }
    }

    private fun persistUpdateBookkeeping(
        prefs: SharedPreferences,
        currentVersion: Int,
        signedOutForUpdate: Boolean,
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            prefs.edit().apply {
                if (signedOutForUpdate) {
                    putString(KEY_LOGOUT_REASON, LOGOUT_REASON_UPDATED)
                } else {
                    remove(KEY_LOGOUT_REASON)
                }
                putInt(KEY_LAST_VERSION_CODE, currentVersion)
            }.apply()
        }
    }

    private fun persistVersionCode(prefs: SharedPreferences, currentVersion: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            prefs.edit().putInt(KEY_LAST_VERSION_CODE, currentVersion).apply()
        }
    }
}