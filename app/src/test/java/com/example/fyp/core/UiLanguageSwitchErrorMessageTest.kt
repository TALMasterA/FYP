package com.example.fyp.core

import org.junit.Assert.assertEquals
import org.junit.Test

class UiLanguageSwitchErrorMessageTest {

    @Test
    fun `rate-limit signatures map to retry-later message`() {
        val throwable = IllegalStateException("HTTP 429 resource-exhausted")

        val message = toUiLanguageSwitchErrorMessage(throwable)

        assertEquals(
            "UI language change is rate-limited right now. Please wait and try again.",
            message
        )
    }

    @Test
    fun `blank errors map to generic language-switch failure message`() {
        val throwable = IllegalStateException("   ")

        val message = toUiLanguageSwitchErrorMessage(throwable)

        assertEquals("UI language change failed. Please try again.", message)
    }
}

