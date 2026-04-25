package com.example.fyp.core

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Guards the SessionDataCleaner cleanup target list.
 *
 * When a new per-user DataStore or sensitive cache is added that survives a
 * Firebase Auth signOut, you MUST also register it as a constructor dependency
 * of [SessionDataCleaner] AND extend [clearSessionData] to wipe it. This test
 * forces a deliberate update to the expected dependency set whenever the
 * cleaner contract changes.
 */
class SessionDataCleanerRegistrationTest {

    companion object {
        /** Constructor-injected cleanup targets for [SessionDataCleaner]. */
        val EXPECTED_CLEANUP_DEPS = setOf(
            "secureStorage",
            "okHttpClient",
            "translationCache",
            "languageDetectionCache",
        )
    }

    @Test
    fun `SessionDataCleaner declares exactly the expected cleanup targets`() {
        val declared = SessionDataCleaner::class.java.declaredFields
            .map { it.name }
            // Filter synthetic/companion fields (TAG, Companion, $stable, etc.).
            .filterNot { it == "TAG" || it == "Companion" || it.startsWith("\$") }
            .toSet()

        assertEquals(
            "If you add or remove a per-session cache, update SessionDataCleaner " +
                "constructor AND clearSessionData() AND this test in lockstep.",
            EXPECTED_CLEANUP_DEPS,
            declared
        )
    }

    @Test
    fun `expected dep count is stable`() {
        assertEquals(4, EXPECTED_CLEANUP_DEPS.size)
    }
}
