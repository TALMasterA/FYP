package com.example.fyp.screens.feedback

import com.example.fyp.domain.feedback.FeedbackRepository
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
    private lateinit var viewModel: FeedbackViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        feedbackRepo = mock()
        viewModel = FeedbackViewModel(feedbackRepo)
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
        assertEquals("Message cannot be empty", state.errorMessage)
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
        assertEquals("Network error", state.errorMessage)
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
        viewModel.setError("Custom error")
        assertEquals("Custom error", viewModel.uiState.value.errorMessage)
    }
}
