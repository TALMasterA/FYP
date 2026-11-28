package com.translator.TalknLearn.utils

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.translator.TalknLearn.model.ui.BaseUiTexts
import com.translator.TalknLearn.model.ui.UiTextKey
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

/**
 * Tests for [ErrorMessageMapper.mapKey], the localizable form of
 * [ErrorMessageMapper.map]. Each case checks the key and that [map] still
 * returns the English text of that key, so both forms stay in step.
 */
class ErrorMessageMapperKeyTest {

    private fun authError(code: String): FirebaseAuthException = mock {
        on { errorCode } doReturn code
    }

    private fun firestoreError(code: FirebaseFirestoreException.Code) =
        FirebaseFirestoreException("db", code)

    private val cases: List<Pair<Throwable?, UiTextKey>> by lazy {
        listOf(
            null to UiTextKey.ErrUnknown,
            IOException("io") to UiTextKey.ErrNetworkFailed,
            SocketTimeoutException("slow") to UiTextKey.ErrNetworkFailed,
            TimeoutException("slow") to UiTextKey.ErrOperationTimedOut,
            CancellationException("stop") to UiTextKey.ErrCancelled,
            IllegalStateException("boom") to UiTextKey.ErrUnexpected,
            authError("ERROR_INVALID_EMAIL") to UiTextKey.ErrAuthInvalidEmail,
            authError("ERROR_WRONG_PASSWORD") to UiTextKey.ErrAuthWrongPassword,
            authError("ERROR_USER_NOT_FOUND") to UiTextKey.ErrAuthUserNotFound,
            authError("ERROR_USER_DISABLED") to UiTextKey.ErrAuthUserDisabled,
            authError("ERROR_TOO_MANY_REQUESTS") to UiTextKey.ErrAuthTooManyRequests,
            authError("ERROR_EMAIL_ALREADY_IN_USE") to UiTextKey.ErrAuthEmailInUse,
            authError("ERROR_SOMETHING_ELSE") to UiTextKey.ErrAuthFailed,
            firestoreError(FirebaseFirestoreException.Code.PERMISSION_DENIED) to UiTextKey.ErrDbPermission,
            firestoreError(FirebaseFirestoreException.Code.UNAVAILABLE) to UiTextKey.ErrDbUnavailable,
            firestoreError(FirebaseFirestoreException.Code.ABORTED) to UiTextKey.ErrDbGeneric,
        )
    }

    @Test
    fun `mapKey returns the expected key for each error type`() {
        cases.forEach { (error, key) ->
            assertEquals("key for $error", key, ErrorMessageMapper.mapKey(error))
        }
    }

    @Test
    fun `map returns the English text of mapKey`() {
        cases.forEach { (error, key) ->
            assertEquals("text for $error", BaseUiTexts[key.ordinal], ErrorMessageMapper.map(error))
        }
    }

    @Test
    fun `English texts match the original mapper messages`() {
        assertEquals("Unknown error occurred.", ErrorMessageMapper.map(null))
        assertEquals("Invalid password.", ErrorMessageMapper.map(authError("ERROR_WRONG_PASSWORD")))
        assertEquals(
            "You do not have permission to access this data.",
            ErrorMessageMapper.map(firestoreError(FirebaseFirestoreException.Code.PERMISSION_DENIED))
        )
        assertEquals(
            "Database error. Please try again.",
            ErrorMessageMapper.map(firestoreError(FirebaseFirestoreException.Code.ABORTED))
        )
    }
}
