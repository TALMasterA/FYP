package com.translator.TalknLearn.observability

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for resolving observability singletons from non-injected
 * surfaces such as top-level Composables or static helpers.
 *
 * Most call sites should still receive these helpers via constructor injection
 * (Hilt ViewModels). This entry point exists for places where injection is
 * impossible — currently:
 *
 *  - [com.translator.TalknLearn.screens.onboarding.OnboardingScreen]'s
 *    file-level `markOnboardingComplete` helper.
 *  - [com.translator.TalknLearn.navigation.AppNavigation] when it sets
 *    Crashlytics user-context keys outside the ViewModel layer.
 *
 * Resolved via [dagger.hilt.android.EntryPointAccessors.fromApplication].
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ObservabilityEntryPoint {
    fun crashlyticsKeysController(): CrashlyticsKeysController
    fun funnelAnalyticsTracker(): FunnelAnalyticsTracker
}
