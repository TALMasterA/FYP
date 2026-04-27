package com.translator.TalknLearn.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CrashlyticsPiiGuardTest {

    @Test
    fun `foundation logger does not send raw error message text to Crashlytics`() {
        val loggerSource = File("src/main/java/com/translator/TalknLearn/core/foundation/Logger.kt").readText()

        assertFalse(
            "Crashlytics must not receive the raw AppLogger message string",
            Regex("""crashlytics\.log\([^\n)]*${'$'}message""").containsMatchIn(loggerSource)
        )
        assertFalse(
            "Crashlytics custom keys must not receive the raw AppLogger message string",
            Regex("""setCustomKey\([^\n)]*,\s*message\s*\)""").containsMatchIn(loggerSource)
        )
        assertTrue(loggerSource.contains("messageHash(message)"))
        assertTrue(loggerSource.contains("last_error_message_hash"))
    }

    @Test
    fun `core foundation Crashlytics calls are limited to sanitized metadata`() {
        val foundationDir = File("src/main/java/com/translator/TalknLearn/core/foundation")
        val offenders = foundationDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filter { file ->
                val text = file.readText()
                text.contains("FirebaseCrashlytics") &&
                    Regex(
                        """crashlytics\.log\([^\n)]*${'$'}message|setCustomKey\([^\n)]*,\s*message\s*\)"""
                    )
                        .containsMatchIn(text)
            }
            .map { it.name }
            .toList()

        assertTrue(offenders.isEmpty())
    }
}
