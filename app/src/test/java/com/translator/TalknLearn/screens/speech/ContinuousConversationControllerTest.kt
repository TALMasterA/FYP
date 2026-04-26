package com.translator.TalknLearn.screens.speech

import com.translator.TalknLearn.domain.speech.StartContinuousConversationUseCase
import com.translator.TalknLearn.domain.speech.TranslateTextUseCase
import com.translator.TalknLearn.model.SpeechResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for ContinuousConversationController.
 *
 * The controller uses Compose mutableStateOf for public state, so we
 * read values through property getters. The CoroutineScope is injected,
 * letting us pass a TestScope for deterministic execution.
 *
 * Tests:
 *  1. start returns early and sets status when not logged in
 *  2. start returns early if already preparing
 *  3. start returns early if already running
 *  4. resetSession=true clears messages and resets ids
 *  5. endSession clears all state
 *  6. stop resets running/preparing/processing flags
 *  7. Messages are added in correct order with incrementing ids
 *  8. Session id is generated on first start and reused across restarts
 *  9. History is saved with correct speaker/direction for person A
 * 10. History is saved with correct speaker/direction for person B
 * 11. Translation error sets status and does not add translation message
 * 12. onError callback sets status and stops the controller
 * 13. Use-case exception during start sets error status
 * 14. stop when never started is a safe no-op
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ContinuousConversationControllerTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var continuousUseCase: StartContinuousConversationUseCase
    private lateinit var translateTextUseCase: TranslateTextUseCase

    private val statusMessages = mutableListOf<String>()
    private val savedHistoryEntries = mutableListOf<Map<String, Any?>>()
    private var loggedIn = true

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        continuousUseCase = mock()
        translateTextUseCase = mock()
        statusMessages.clear()
        savedHistoryEntries.clear()
        loggedIn = true
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Helper that builds a controller using the given [TestScope] so that
     * coroutines launched by the controller are children of the test.
     */
    private fun TestScope.buildController(): ContinuousConversationController {
        return ContinuousConversationController(
            scope = this,
            continuousUseCase = continuousUseCase,
            translateTextUseCase = translateTextUseCase,
            setStatus = { statusMessages += it },
            saveHistory = { mode, sourceText, targetText, sourceLang, targetLang,
                            sessionId, speaker, direction, sequence ->
                savedHistoryEntries += mapOf(
                    "mode" to mode,
                    "sourceText" to sourceText,
                    "targetText" to targetText,
                    "sourceLang" to sourceLang,
                    "targetLang" to targetLang,
                    "sessionId" to sessionId,
                    "speaker" to speaker,
                    "direction" to direction,
                    "sequence" to sequence,
                )
            },
            isLoggedIn = { loggedIn },
        )
    }

    /**
     * Convenience: stub the continuous use-case to capture callbacks and
     * return a mock recognizer.  Returns a holder whose fields are populated
     * when the controller invokes the use-case.
     */
    private data class CallbackHolder(
        var onPartial: ((String) -> Unit)? = null,
        var onFinal: ((String) -> Unit)? = null,
        var onError: ((String) -> Unit)? = null,
    )

    private fun stubUseCaseCapturingCallbacks(
        recognizer: SpeechRecognizer = mock(),
    ): CallbackHolder {
        val holder = CallbackHolder()
        continuousUseCase.stub {
            onBlocking { invoke(any(), any(), any(), any()) } doAnswer { invocation ->
                holder.onPartial = invocation.getArgument(1)
                holder.onFinal = invocation.getArgument(2)
                holder.onError = invocation.getArgument(3)
                recognizer
            }
        }
        return holder
    }

    // ── 1. Guard: not logged in ─────────────────────────────────────

    @Test
    fun `start returns early and sets status when not logged in`() = runTest {
        val controller = buildController()
        loggedIn = false

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        assertEquals(1, statusMessages.size)
        assertEquals("Please login to use continuous translation.", statusMessages[0])
        assertFalse(controller.isContinuousPreparing)
        assertFalse(controller.isContinuousRunning)
        assertFalse(controller.isContinuousProcessing)
    }

    // ── 2. Guard: already preparing ─────────────────────────────────

    @Test
    fun `start returns early if already preparing`() = runTest {
        val controller = buildController()
        val mockRecognizer = mock<SpeechRecognizer>()
        whenever(continuousUseCase.invoke(any(), any(), any(), any()))
            .thenReturn(mockRecognizer)

        // First start: launches a coroutine that sets isContinuousPreparing = true
        // before hitting delay(200).  With StandardTestDispatcher we must
        // drain the current virtual-time tasks so the coroutine reaches the delay.
        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        runCurrent() // executes up to the delay(200) suspension point

        assertTrue("Should be in preparing state", controller.isContinuousPreparing)

        // Second start should hit the preparing guard and return immediately.
        val statusCountBefore = statusMessages.size
        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)

        assertEquals(
            "Second start must not add any status messages",
            statusCountBefore, statusMessages.size,
        )

        // Let the first coroutine finish normally.
        advanceUntilIdle()
    }

    // ── 3. Guard: already running ───────────────────────────────────

    @Test
    fun `start returns early if already running`() = runTest {
        val controller = buildController()
        val mockRecognizer = mock<SpeechRecognizer>()
        whenever(continuousUseCase.invoke(any(), any(), any(), any()))
            .thenReturn(mockRecognizer)

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        assertTrue("Should be running after full start", controller.isContinuousRunning)
        assertFalse("Should no longer be preparing", controller.isContinuousPreparing)

        // Second start hits the running guard.
        val statusCountBefore = statusMessages.size
        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)

        assertEquals(
            "Second start must not add any status messages",
            statusCountBefore, statusMessages.size,
        )
    }

    // ── 4. resetSession clears messages and resets ids ───────────────

    @Test
    fun `start with resetSession clears messages and resets ids`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("translated"))

        // Start and produce a message so there are entries in the list.
        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("hello")
        advanceUntilIdle()

        assertEquals(2, controller.continuousMessages.size) // source + translation
        assertEquals(0L, controller.continuousMessages[0].id)
        assertEquals(1L, controller.continuousMessages[1].id)

        // Stop so we can start again (guards check running).
        controller.stop()
        advanceUntilIdle()

        // Restart with resetSession = true.
        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = true)
        advanceUntilIdle()

        // Messages should be cleared.
        assertTrue("Messages should be empty after reset", controller.continuousMessages.isEmpty())

        // Produce a new message; ids must start from 0 again.
        holder.onFinal?.invoke("world")
        advanceUntilIdle()

        assertEquals(2, controller.continuousMessages.size)
        assertEquals(0L, controller.continuousMessages[0].id)
        assertEquals(1L, controller.continuousMessages[1].id)
        assertEquals("world", controller.continuousMessages[0].text)
    }

    // ── 5. endSession clears all state ──────────────────────────────

    @Test
    fun `endSession clears all state`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("hola"))

        // Start, produce a message, and observe non-empty state.
        controller.start("en-US", "es-ES", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("hello")
        advanceUntilIdle()

        assertTrue("Should have messages", controller.continuousMessages.isNotEmpty())
        assertTrue("Should be running", controller.isContinuousRunning)
        assertEquals("hola", controller.lastSegmentTranslation)

        // End the session.
        controller.endSession()
        advanceUntilIdle()

        assertFalse(controller.isContinuousRunning)
        assertFalse(controller.isContinuousPreparing)
        assertFalse(controller.isContinuousProcessing)
        assertTrue("Messages should be cleared", controller.continuousMessages.isEmpty())
        assertEquals("", controller.livePartialText)
        assertEquals("", controller.lastSegmentTranslation)
    }

    // ── 6. stop resets running state ────────────────────────────────

    @Test
    fun `stop resets running and preparing flags`() = runTest {
        val controller = buildController()
        val mockRecognizer = mock<SpeechRecognizer>()
        whenever(continuousUseCase.invoke(any(), any(), any(), any()))
            .thenReturn(mockRecognizer)

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()
        assertTrue("Should be running", controller.isContinuousRunning)

        controller.stop()
        advanceUntilIdle()

        assertFalse("Running should be false after stop", controller.isContinuousRunning)
        assertFalse("Preparing should be false after stop", controller.isContinuousPreparing)
        assertFalse("Processing should be false after stop", controller.isContinuousProcessing)
    }

    // ── 7. Messages added in order with incrementing ids ────────────

    @Test
    fun `messages are added in correct order with incrementing ids`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(eq("hello"), any(), any()))
            .thenReturn(SpeechResult.Success("hola"))
        whenever(translateTextUseCase.invoke(eq("goodbye"), any(), any()))
            .thenReturn(SpeechResult.Success("adios"))

        controller.start("en-US", "es-ES", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        // First recognition segment.
        holder.onFinal?.invoke("hello")
        advanceUntilIdle()

        // Second recognition segment.
        holder.onFinal?.invoke("goodbye")
        advanceUntilIdle()

        val msgs = controller.continuousMessages
        assertEquals(4, msgs.size)

        // Sequential ids: 0, 1, 2, 3
        assertEquals(0L, msgs[0].id)
        assertEquals(1L, msgs[1].id)
        assertEquals(2L, msgs[2].id)
        assertEquals(3L, msgs[3].id)

        // Alternating: source, translation, source, translation
        assertEquals("hello", msgs[0].text)
        assertEquals("en-US", msgs[0].lang)
        assertFalse(msgs[0].isTranslation)
        assertTrue(msgs[0].isFromPersonA)

        assertEquals("hola", msgs[1].text)
        assertEquals("es-ES", msgs[1].lang)
        assertTrue(msgs[1].isTranslation)
        assertTrue(msgs[1].isFromPersonA)

        assertEquals("goodbye", msgs[2].text)
        assertFalse(msgs[2].isTranslation)

        assertEquals("adios", msgs[3].text)
        assertTrue(msgs[3].isTranslation)
    }

    // ── 8. Session id generated on first start and reused ───────────

    @Test
    fun `session id is generated on first start and reused across restarts`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("translated"))

        // First start: a session id should be created.
        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("hello")
        advanceUntilIdle()

        assertEquals(1, savedHistoryEntries.size)
        val sessionId1 = savedHistoryEntries[0]["sessionId"] as String
        assertTrue("Session id must not be blank", sessionId1.isNotBlank())

        // Stop and restart without reset: same session id.
        controller.stop()
        advanceUntilIdle()

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("world")
        advanceUntilIdle()

        assertEquals(2, savedHistoryEntries.size)
        val sessionId2 = savedHistoryEntries[1]["sessionId"] as String
        assertEquals("Session id should be reused", sessionId1, sessionId2)
    }

    // ── 9. History saved with correct speaker/direction for person A ─

    @Test
    fun `history saved with correct speaker and direction for person A`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("bonjour"))

        controller.start("en-US", "fr-FR", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("hello")
        advanceUntilIdle()

        assertEquals(1, savedHistoryEntries.size)
        val entry = savedHistoryEntries[0]
        assertEquals("continuous", entry["mode"])
        assertEquals("hello", entry["sourceText"])
        assertEquals("bonjour", entry["targetText"])
        assertEquals("en-US", entry["sourceLang"])
        assertEquals("fr-FR", entry["targetLang"])
        assertEquals("A", entry["speaker"])
        assertEquals("A_to_B", entry["direction"])
        assertEquals(0L, entry["sequence"])
    }

    // ── 10. History saved with correct speaker/direction for person B ─

    @Test
    fun `history saved with correct speaker and direction for person B`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("hello"))

        controller.start("fr-FR", "en-US", isFromPersonA = false, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("bonjour")
        advanceUntilIdle()

        assertEquals(1, savedHistoryEntries.size)
        val entry = savedHistoryEntries[0]
        assertEquals("B", entry["speaker"])
        assertEquals("B_to_A", entry["direction"])
    }

    // ── 11. Translation error ───────────────────────────────────────

    @Test
    fun `translation error sets status and does not add translation message`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Error("Network error"))

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        holder.onFinal?.invoke("hello")
        advanceUntilIdle()

        // Only the source message should be present (no translation).
        val msgs = controller.continuousMessages
        assertEquals("Should have only the source message", 1, msgs.size)
        assertEquals("hello", msgs[0].text)
        assertFalse(msgs[0].isTranslation)

        // Error status should be reported.
        assertTrue(
            "Should contain translation error status",
            statusMessages.any { it.contains("Continuous translation error") },
        )

        // No history entry saved on translation failure.
        assertTrue("Should not save history on error", savedHistoryEntries.isEmpty())
    }

    // ── 12. onError callback ────────────────────────────────────────

    @Test
    fun `onError callback sets status and stops controller`() = runTest {
        val controller = buildController()
        val holder = stubUseCaseCapturingCallbacks()

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()
        assertTrue("Should be running", controller.isContinuousRunning)

        // Simulate a recognition error from the Azure SDK.
        holder.onError?.invoke("Microphone disconnected")
        advanceUntilIdle()

        assertFalse("Should not be running after error", controller.isContinuousRunning)
        assertTrue(
            "Should contain recognition error status",
            statusMessages.any {
                it.contains("Continuous recognition error: Microphone disconnected")
            },
        )
    }

    // ── 13. Use-case exception during start ─────────────────────────

    @Test
    fun `use-case exception during start sets error status and resets flags`() = runTest {
        val controller = buildController()
        whenever(continuousUseCase.invoke(any(), any(), any(), any()))
            .thenAnswer { throw RuntimeException("Azure SDK init failed") }

        controller.start("en-US", "zh-CN", isFromPersonA = true, resetSession = false)
        advanceUntilIdle()

        assertFalse(controller.isContinuousPreparing)
        assertFalse(controller.isContinuousRunning)
        assertFalse(controller.isContinuousProcessing)
        assertTrue(
            "Should contain start error status",
            statusMessages.any { it.contains("Continuous start error: Azure SDK init failed") },
        )
    }

    // ── 14. stop when never started is a no-op ──────────────────────

    @Test
    fun `stop when never started is a safe no-op`() = runTest {
        val controller = buildController()

        // Calling stop without ever starting must not crash or change state.
        controller.stop()
        advanceUntilIdle()

        assertFalse(controller.isContinuousRunning)
        assertFalse(controller.isContinuousPreparing)
        assertFalse(controller.isContinuousProcessing)
        assertTrue("Messages should remain empty", controller.continuousMessages.isEmpty())
    }
}
