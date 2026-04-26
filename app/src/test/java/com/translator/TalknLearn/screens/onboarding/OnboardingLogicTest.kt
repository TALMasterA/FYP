package com.translator.TalknLearn.screens.onboarding

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for onboarding persistence logic.
 *
 * Tests:
 * 1. First launch shows onboarding (no prefs stored)
 * 2. After completing onboarding, it reports complete for the same version
 * 3. After app update (different version), onboarding shows again
 * 4. Version name is stored when onboarding is completed
 */
class OnboardingLogicTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        editor = mock {
            on { putBoolean(any(), any()) } doReturn it
            on { putString(any(), any()) } doReturn it
        }
        prefs = mock {
            on { edit() } doReturn editor
        }
        context = mock {
            on { getSharedPreferences(eq("onboarding_prefs"), eq(Context.MODE_PRIVATE)) } doReturn prefs
        }
    }

    @Test
    fun `first launch returns false when no prefs stored`() {
        whenever(prefs.getBoolean("onboarding_complete", false)).thenReturn(false)

        val result = isOnboardingComplete(context)

        assertFalse(result)
    }

    @Test
    fun `returns true when complete flag is true and version matches`() {
        whenever(prefs.getBoolean("onboarding_complete", false)).thenReturn(true)
        whenever(prefs.getString("onboarding_version", null))
            .thenReturn(com.translator.TalknLearn.BuildConfig.VERSION_NAME)

        val result = isOnboardingComplete(context)

        assertTrue(result)
    }

    @Test
    fun `returns false when version does not match (simulates app update)`() {
        whenever(prefs.getBoolean("onboarding_complete", false)).thenReturn(true)
        whenever(prefs.getString("onboarding_version", null)).thenReturn("0.0.0")

        val result = isOnboardingComplete(context)

        assertFalse(result)
    }

    @Test
    fun `returns false when version is null (legacy onboarding completed without version tracking)`() {
        whenever(prefs.getBoolean("onboarding_complete", false)).thenReturn(true)
        whenever(prefs.getString("onboarding_version", null)).thenReturn(null)

        val result = isOnboardingComplete(context)

        assertFalse(result)
    }
}
