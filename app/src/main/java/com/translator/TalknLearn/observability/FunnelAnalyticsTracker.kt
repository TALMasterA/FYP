package com.translator.TalknLearn.observability

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Item 51 (docs/APP_SUGGESTIONS.md §9): emits funnel analytics events
 * for the four key activation points used by the demo dashboards:
 *  - onboarding_complete
 *  - first_translation
 *  - first_quiz
 *  - first_friend_add
 *
 * "First-X" events are gated by a SharedPreferences flag so each event
 * fires at most once per install. [onboarding_complete] is a one-shot
 * by virtue of the onboarding screen only being shown once.
 *
 * Firebase Analytics calls are wrapped in [runCatching] so unit tests
 * silently no-op without breaking caller paths.
 */
@Singleton
open class FunnelAnalyticsTracker @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** Fires unconditionally — onboarding screen is itself a one-shot. */
    open fun logOnboardingComplete() {
        log(Event.ONBOARDING_COMPLETE)
    }

    /** Fires only on the first successful translation per install. */
    open fun logFirstTranslation() {
        logFirstTime(Event.FIRST_TRANSLATION)
    }

    /** Fires only on the first completed quiz attempt per install. */
    open fun logFirstQuiz() {
        logFirstTime(Event.FIRST_QUIZ)
    }

    /** Fires only on the first accepted friend request per install. */
    open fun logFirstFriendAdd() {
        logFirstTime(Event.FIRST_FRIEND_ADD)
    }

    private fun logFirstTime(eventName: String) {
        val flagKey = FLAG_PREFIX + eventName
        if (prefs.getBoolean(flagKey, false)) return
        // Mark first BEFORE logging so concurrent callers don't double-fire.
        prefs.edit().putBoolean(flagKey, true).apply()
        log(eventName)
    }

    private fun log(eventName: String) {
        runCatching {
            FirebaseAnalytics.getInstance(context).logEvent(eventName, null)
        }
    }

    object Event {
        const val ONBOARDING_COMPLETE = "onboarding_complete"
        const val FIRST_TRANSLATION = "first_translation"
        const val FIRST_QUIZ = "first_quiz"
        const val FIRST_FRIEND_ADD = "first_friend_add"
    }

    companion object {
        const val PREFS_NAME = "funnel_analytics_prefs"
        const val FLAG_PREFIX = "fired_"
    }
}
