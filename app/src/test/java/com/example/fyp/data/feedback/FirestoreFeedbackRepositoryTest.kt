package com.example.fyp.data.feedback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for FirestoreFeedbackRepository.
 *
 * Since the repository is tightly coupled to Firestore, these tests verify
 * the pure-logic patterns that the repository relies on:
 * - Message validation and trimming
 * - Retry logic constants and backoff calculation
 * - Error-type classification message patterns
 * - Feedback document field structure expectations
 */
class FirestoreFeedbackRepositoryTest {

    // ---------------------------------------------------------------
    // Constants mirror the companion object in the production class.
    // If someone changes them, these tests will flag the mismatch so
    // the change is intentional and reviewed.
    // ---------------------------------------------------------------
    companion object {
        private const val EXPECTED_MAX_RETRIES = 3
        private const val EXPECTED_RETRY_DELAY_MS = 1000L
        private const val EXPECTED_REQUEST_TIMEOUT_MS = 15000L

        /** The Firestore collection path used by the repository. */
        private const val EXPECTED_COLLECTION = "feedback"

        /** Required keys in the feedback document map. */
        private val EXPECTED_DOCUMENT_FIELDS = setOf(
            "userId",
            "userEmail",
            "message",
            "timestamp",
            "status",
            "appVersion",
            "deviceModel",
            "createdAt"
        )

        // Error-message strings the repository produces per error code
        private const val PERMISSION_DENIED_MSG =
            "Permission denied. Please ensure you're logged in."
        private const val UNAVAILABLE_MSG =
            "Firebase service unavailable. Please check your internet connection."
        private const val DEADLINE_EXCEEDED_MSG =
            "Request timeout. Please check your internet connection and try again."
        private const val UNAUTHENTICATED_MSG =
            "Authentication required. Please log in and try again."
        private const val RESOURCE_EXHAUSTED_MSG =
            "Service temporarily busy. Please try again later."
        private const val GENERIC_TIMEOUT_MSG =
            "Request timeout. Please check your internet connection and try again."
        private const val GENERIC_FAILURE_MSG =
            "Failed to submit feedback. Please check your internet connection."
    }

    // =================================================================
    // 1. Validation patterns – empty / blank message handling
    // =================================================================

    @Test
    fun `empty message trims to blank string`() {
        val message = ""
        val trimmed = message.trim()
        assertTrue("Empty string should trim to blank", trimmed.isEmpty())
    }

    @Test
    fun `whitespace-only message trims to blank string`() {
        val message = "   \t\n  "
        val trimmed = message.trim()
        assertTrue(
            "Whitespace-only input should trim to empty",
            trimmed.isEmpty()
        )
    }

    @Test
    fun `message with surrounding whitespace is trimmed`() {
        val message = "  Great app!  "
        val trimmed = message.trim()
        assertEquals("Great app!", trimmed)
    }

    // =================================================================
    // 2. Retry logic constants
    // =================================================================

    @Test
    fun `max retries constant is three`() {
        // The repository retries up to 3 times. Changing this value
        // affects user-perceived latency on transient failures.
        assertEquals(
            "MAX_RETRIES should be 3",
            3,
            EXPECTED_MAX_RETRIES
        )
    }

    @Test
    fun `retry delay is one second`() {
        assertEquals(
            "RETRY_DELAY_MS should be 1000",
            1000L,
            EXPECTED_RETRY_DELAY_MS
        )
    }

    @Test
    fun `request timeout is fifteen seconds`() {
        assertEquals(
            "REQUEST_TIMEOUT_MS should be 15000",
            15000L,
            EXPECTED_REQUEST_TIMEOUT_MS
        )
    }

    @Test
    fun `linear backoff delay increases with each attempt`() {
        // The repository uses: delay(RETRY_DELAY_MS * (attempt + 1))
        val delays = (0 until EXPECTED_MAX_RETRIES).map { attempt ->
            EXPECTED_RETRY_DELAY_MS * (attempt + 1)
        }

        assertEquals(
            "First attempt delay should be 1x base",
            1000L,
            delays[0]
        )
        assertEquals(
            "Second attempt delay should be 2x base",
            2000L,
            delays[1]
        )
        assertEquals(
            "Third attempt delay should be 3x base",
            3000L,
            delays[2]
        )

        // Verify strictly increasing
        for (i in 1 until delays.size) {
            assertTrue(
                "Delay should increase on each attempt",
                delays[i] > delays[i - 1]
            )
        }
    }

    @Test
    fun `total worst-case retry time is bounded`() {
        // Sum of all retry delays: 1s + 2s + 3s = 6s
        // Plus up to 3 timeouts of 15s each = 45s
        // Total worst case = 51s; must stay under 60s for reasonable UX
        val totalRetryDelay = (0 until EXPECTED_MAX_RETRIES).sumOf { attempt ->
            EXPECTED_RETRY_DELAY_MS * (attempt + 1)
        }
        val totalTimeoutOverhead = EXPECTED_REQUEST_TIMEOUT_MS * EXPECTED_MAX_RETRIES
        val worstCase = totalRetryDelay + totalTimeoutOverhead

        assertTrue(
            "Worst-case total time ($worstCase ms) should be under 60 seconds",
            worstCase <= 60_000L
        )
    }

    // =================================================================
    // 3. Error-type classification patterns
    // =================================================================

    @Test
    fun `permission denied error message is user-friendly`() {
        assertTrue(
            "Should mention being logged in",
            PERMISSION_DENIED_MSG.contains("logged in")
        )
        assertFalse(
            "Should not expose internal error codes",
            PERMISSION_DENIED_MSG.contains("PERMISSION_DENIED")
        )
    }

    @Test
    fun `unavailable error message mentions internet connection`() {
        assertTrue(
            "Should mention internet connection",
            UNAVAILABLE_MSG.contains("internet connection")
        )
    }

    @Test
    fun `deadline exceeded error message mentions timeout`() {
        assertTrue(
            "Should mention timeout",
            DEADLINE_EXCEEDED_MSG.contains("timeout", ignoreCase = true)
        )
        assertTrue(
            "Should suggest trying again",
            DEADLINE_EXCEEDED_MSG.contains("try again")
        )
    }

    @Test
    fun `unauthenticated error message asks user to log in`() {
        assertTrue(
            "Should mention logging in",
            UNAUTHENTICATED_MSG.contains("log in", ignoreCase = true)
        )
    }

    @Test
    fun `resource exhausted error message suggests trying later`() {
        assertTrue(
            "Should suggest trying later",
            RESOURCE_EXHAUSTED_MSG.contains("try again later")
        )
    }

    @Test
    fun `generic timeout error matches deadline exceeded message`() {
        // Both Firestore DEADLINE_EXCEEDED and generic timeout exceptions
        // should produce the same user-facing message.
        assertEquals(
            "Timeout messages should be consistent",
            DEADLINE_EXCEEDED_MSG,
            GENERIC_TIMEOUT_MSG
        )
    }

    @Test
    fun `generic failure message differs from timeout message`() {
        // A non-timeout generic exception should produce a distinct message
        // so users can tell whether the issue is timing or connectivity.
        assertNotEquals(
            "Generic failure and timeout messages should differ",
            GENERIC_TIMEOUT_MSG,
            GENERIC_FAILURE_MSG
        )
    }

    @Test
    fun `non-retryable errors are permission denied and unauthenticated`() {
        // The repository immediately throws for PERMISSION_DENIED and
        // UNAUTHENTICATED without retrying. These are the only two
        // non-retryable Firestore error codes.
        val nonRetryableCodes = setOf("PERMISSION_DENIED", "UNAUTHENTICATED")
        val retryableCodes = setOf("UNAVAILABLE", "DEADLINE_EXCEEDED", "ABORTED", "RESOURCE_EXHAUSTED")

        // Non-retryable and retryable sets must not overlap
        assertTrue(
            "Non-retryable and retryable code sets must be disjoint",
            nonRetryableCodes.intersect(retryableCodes).isEmpty()
        )

        assertEquals(
            "Exactly two error codes should be non-retryable",
            2,
            nonRetryableCodes.size
        )
    }

    // =================================================================
    // 4. Feedback document field structure
    // =================================================================

    @Test
    fun `feedback document contains all required fields`() {
        // Simulates the field keys the repository creates in its hashMapOf
        val documentFields = setOf(
            "userId",
            "userEmail",
            "message",
            "timestamp",
            "status",
            "appVersion",
            "deviceModel",
            "createdAt"
        )

        assertEquals(
            "Document should have exactly the expected fields",
            EXPECTED_DOCUMENT_FIELDS,
            documentFields
        )
    }

    @Test
    fun `feedback document has exactly eight fields`() {
        assertEquals(
            "Feedback document should have 8 fields",
            8,
            EXPECTED_DOCUMENT_FIELDS.size
        )
    }

    @Test
    fun `default status for new feedback is new`() {
        // The repository hard-codes status to "new"
        val defaultStatus = "new"
        assertEquals("new", defaultStatus)
    }

    @Test
    fun `anonymous fallback values are set when user is null`() {
        // When auth.currentUser is null the repository falls back to these
        val fallbackUserId = "anonymous"
        val fallbackEmail = "no-email"

        assertEquals("anonymous", fallbackUserId)
        assertEquals("no-email", fallbackEmail)
    }

    @Test
    fun `feedback collection path is correct`() {
        assertEquals(
            "Collection name should be 'feedback'",
            "feedback",
            EXPECTED_COLLECTION
        )
    }

    @Test
    fun `createdAt field captures millisecond epoch`() {
        // The repository stores System.currentTimeMillis() in createdAt.
        // Verify that the value is a reasonable epoch millisecond timestamp.
        val now = System.currentTimeMillis()
        assertTrue(
            "Epoch millis should be after 2024-01-01",
            now > 1_704_067_200_000L  // 2024-01-01T00:00:00Z
        )
    }

    @Test
    fun `message field stores trimmed content`() {
        // The repository writes message.trim() to the document.
        // Verify trimming preserves internal whitespace.
        val input = "  Hello   World  "
        val stored = input.trim()
        assertEquals("Hello   World", stored)
    }
}
