package com.translator.TalknLearn.screens.feedback

import com.translator.TalknLearn.core.security.FakePersistentRateLimiter
import com.translator.TalknLearn.domain.feedback.FeedbackRepository
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.model.ui.english
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

/**
 * Unit tests for FeedbackViewModel.
 *
 * Tests:
 * 1. Initial state is correct
 * 2. Blank message shows validation error
 * 3. Successful feedback submission
 * 4. Failed feedback submission shows error
 * 5. Dismiss success dialog
 * 6. Dismiss error dialog
 * 7. Set error message
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackViewModelTest {

    private lateinit var feedbackRepo: FeedbackRepository
    private lateinit var rateLimiter: FakePersistentRateLimiter
    private lateinit var viewModel: FeedbackViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        feedbackRepo = mock()
        rateLimiter = FakePersistentRateLimiter()
        viewModel = FeedbackViewModel(feedbackRepo, rateLimiter)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertFalse(state.showSuccessDialog)
        assertFalse(state.showErrorDialog)
        assertNull(state.errorMessage)
    }

    @Test
    fun `blank message shows validation error`() {
        viewModel.submitFeedback("   ")

        val state = viewModel.uiState.value
        assertTrue(state.showErrorDialog)
        assertEquals(UiTextKey.FeedbackMsgEmpty, state.errorMessage?.key)
        assertEquals("Message cannot be empty", state.errorMessage.english())
    }

    @Test
    fun `blank message does not call repository`() = runTest {
        viewModel.submitFeedback("")
        verify(feedbackRepo, never()).submitFeedback(any())
    }

    @Test
    fun `successful submission shows success dialog`() = runTest {
        feedbackRepo.stub {
            onBlocking { submitFeedback(any()) } doAnswer {}
        }

        viewModel.submitFeedback("Great app!")

        val state = viewModel.uiState.value
        assertTrue(state.showSuccessDialog)
        assertFalse(state.isSubmitting)
        assertFalse(state.showErrorDialog)
    }

    @Test
    fun `failed submission shows error dialog`() = runTest {
        feedbackRepo.stub {
            onBlocking { submitFeedback(any()) } doAnswer { throw RuntimeException("Network error") }
        }

        viewModel.submitFeedback("Feedback text")

        val state = viewModel.uiState.value
        assertTrue(state.showErrorDialog)
        assertEquals(
            "No internet connection. Please check your network and try again.",
            state.errorMessage.english()
        )
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `dismiss success dialog clears flag`() {
        viewModel.dismissSuccessDialog()
        assertFalse(viewModel.uiState.value.showSuccessDialog)
    }

    @Test
    fun `dismiss error dialog clears flag and submitting`() {
        viewModel.dismissErrorDialog()
        assertFalse(viewModel.uiState.value.showErrorDialog)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `setError updates error message`() {
        viewModel.setError(UiTextKey.FeedbackMessageRequired)
        assertEquals(UiTextKey.FeedbackMessageRequired, viewModel.uiState.value.errorMessage?.key)
    }

    @Test
    fun `too short message shows min length error`() = runTest {
        viewModel.submitFeedback("short")

        val state = viewModel.uiState.value
        assertTrue(state.showErrorDialog)
        assertEquals("Feedback must be at least 10 characters", state.errorMessage.english())
        verify(feedbackRepo, never()).submitFeedback(any())
    }

    @Test
    fun `too long message shows max length error`() = runTest {
        viewModel.submitFeedback("a".repeat(5001))

        val state = viewModel.uiState.value
        assertTrue(state.showErrorDialog)
        assertEquals("Feedback must not exceed 5000 characters", state.errorMessage.english())
        verify(feedbackRepo, never()).submitFeedback(any())
    }

    @Test
    fun `unknown submit error falls back to submit failed message`() = runTest {
        feedbackRepo.stub {
            onBlocking { submitFeedback(any()) } doAnswer { throw UnsupportedOperationException("boom") }
        }

        viewModel.submitFeedback("Feedback text")

        assertEquals(UiTextKey.FeedbackMsgSubmitFailed, viewModel.uiState.value.errorMessage?.key)
    }
}
