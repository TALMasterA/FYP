package com.translator.TalknLearn.observability

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Item 49 (docs/APP_SUGGESTIONS.md §9): attaches user-context Crashlytics
 * custom keys so non-fatal logs and crashes carry enough context to
 * triage without a repro session.
 *
 * Set keys:
 *  - currentScreenRoute  — top-level NavHost destination (e.g. "speech_screen")
 *  - appUiLanguage       — UI language code (e.g. "en-US")
 *  - primaryLanguage     — user's primary language code (UserSettings)
 *  - accountAgeBucket    — coarse account age bucket (privacy-friendly)
 *
 * All Firebase calls are wrapped in [runCatching] so unit tests where
 * Firebase isn't initialised silently no-op.
 */
@Singleton
open class CrashlyticsKeysController @Inject constructor() {

    /** Update the current screen route key on every navigation event. */
    open fun setCurrentScreenRoute(route: String?) {
        runCatching {
            FirebaseCrashlytics.getInstance()
                .setCustomKey(KEY_CURRENT_SCREEN, route ?: "unknown")
        }
    }

    /** Update the user-context keys (UI language, primary language, account age bucket). */
    open fun setUserContext(
        appUiLanguage: String?,
        primaryLanguage: String?,
        accountCreationTimestampMillis: Long?,
        nowMillis: Long = System.currentTimeMillis(),
    ) {
        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            if (appUiLanguage != null) crashlytics.setCustomKey(KEY_APP_UI_LANGUAGE, appUiLanguage)
            if (primaryLanguage != null) crashlytics.setCustomKey(KEY_PRIMARY_LANGUAGE, primaryLanguage)
            crashlytics.setCustomKey(
                KEY_ACCOUNT_AGE_BUCKET,
                accountAgeBucket(accountCreationTimestampMillis, nowMillis),
            )
        }
    }

    companion object {
        const val KEY_CURRENT_SCREEN = "currentScreenRoute"
        const val KEY_APP_UI_LANGUAGE = "appUiLanguage"
        const val KEY_PRIMARY_LANGUAGE = "primaryLanguage"
        const val KEY_ACCOUNT_AGE_BUCKET = "accountAgeBucket"

        /**
         * Returns a coarse, privacy-friendly bucket for the account age in
         * days. We deliberately bucket so individual users can't be
         * fingerprinted by exact creation timestamps in Crashlytics.
         */
        fun accountAgeBucket(
            creationTimestampMillis: Long?,
            nowMillis: Long = System.currentTimeMillis(),
        ): String {
            if (creationTimestampMillis == null || creationTimestampMillis <= 0L) return "unknown"
            val ageMillis = nowMillis - creationTimestampMillis
            if (ageMillis < 0L) return "unknown"
            val ageDays = ageMillis / MILLIS_PER_DAY
            return when {
                ageDays < 1L -> "0d"
                ageDays < 7L -> "1-6d"
                ageDays < 30L -> "7-29d"
                ageDays < 90L -> "30-89d"
                ageDays < 365L -> "90-364d"
                else -> "365d+"
            }
        }

        private const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L
    }
}
