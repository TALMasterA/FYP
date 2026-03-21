package com.example.fyp.screens.speech

import android.net.Uri
import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.domain.ocr.RecognizeTextFromImageUseCase
import com.example.fyp.data.repositories.AutoDetectRecognitionResult
import com.example.fyp.domain.speech.*
import com.example.fyp.model.OcrResult
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for SpeechViewModel.
 *
 * SpeechViewModel uses Compose mutableStateOf (not StateFlow) for UI state,
 * so we read values through public property getters.
 *
 * Tests:
 * 1. recognize success sets recognizedText and returns to Idle
 * 2. recognize error sets statusMessage
 * 3. translate requires text
 * 4. translate requires login
 * 5. translate success sets translatedText and saves history
 * 6. translate error sets statusMessage
 * 7. translate with auto-detect detects language then translates
 * 8. OCR success sets recognizedText
 * 9. OCR error sets statusMessage
 * 10. updateSourceText updates recognized text
 * 11. swapTexts swaps recognized and translated
 * 12. speakOriginal delegates to ttsController with voice settings
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SpeechViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    private val settingsFlow = MutableStateFlow(UserSettings())

    private lateinit var recognizeFromMic: RecognizeFromMicUseCase
    private lateinit var autoDetectRecognizeUseCase: RecognizeWithAutoDetectUseCase
    private lateinit var translateTextUseCase: TranslateTextUseCase
    private lateinit var speakTextUseCase: SpeakTextUseCase
    private lateinit var detectLanguageUseCase: DetectLanguageUseCase
    private lateinit var recognizeTextFromImageUseCase: RecognizeTextFromImageUseCase
    private lateinit var continuousUseCase: StartContinuousConversationUseCase
    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var historyRepo: FirestoreHistoryRepository
    private lateinit var sharedSettings: SharedSettingsDataSource

    private val testUser = User(uid = "u1", email = "test@test.com")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        recognizeFromMic = mock()
        autoDetectRecognizeUseCase = mock()
        translateTextUseCase = mock()
        speakTextUseCase = mock()
        detectLanguageUseCase = mock()
        recognizeTextFromImageUseCase = mock()
        continuousUseCase = mock()
        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        historyRepo = mock()
        sharedSettings = mock { on { settings } doReturn settingsFlow }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): SpeechViewModel {
        return SpeechViewModel(
            recognizeFromMic = recognizeFromMic,
            autoDetectRecognizeUseCase = autoDetectRecognizeUseCase,
            translateTextUseCase = translateTextUseCase,
            speakTextUseCase = speakTextUseCase,
            detectLanguageUseCase = detectLanguageUseCase,
            recognizeTextFromImageUseCase = recognizeTextFromImageUseCase,
            continuousUseCase = continuousUseCase,
            authRepo = authRepo,
            historyRepo = historyRepo,
            sharedSettings = sharedSettings,
        )
    }

    // ── recognize success ───────────────────────────────────────────

    @Test
    fun `recognize success sets recognizedText and returns to Idle`() = runTest {
        whenever(recognizeFromMic.invoke(any()))
            .thenReturn(SpeechResult.Success("Hello world"))

        val vm = buildViewModel()
        vm.recognize("en-US")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Hello world", vm.recognizedText)
        assertEquals(RecognizePhase.Idle, vm.recognizePhase)
        assertTrue(vm.statusMessage.isEmpty())
    }

    // ── recognize error ─────────────────────────────────────────────

    @Test
    fun `recognize error sets statusMessage`() = runTest {
        whenever(recognizeFromMic.invoke(any()))
            .thenReturn(SpeechResult.Error("Mic not available"))

        val vm = buildViewModel()
        vm.recognize("en-US")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(RecognizePhase.Idle, vm.recognizePhase)
        assertTrue(vm.statusMessage.contains("Mic not available"))
    }

    // ── translate requires text ─────────────────────────────────────

    @Test
    fun `translate with empty text shows prompt message`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.translate("en", "ja")

        assertTrue(vm.statusMessage.contains("enter or speak", ignoreCase = true))
    }

    // ── translate requires login ────────────────────────────────────

    @Test
    fun `translate without login shows login required`() = runTest {
        val vm = buildViewModel()
        // Default is LoggedOut
        vm.updateSourceText("Hello")
        vm.translate("en", "ja")

        assertTrue(vm.statusMessage.contains("Login", ignoreCase = true))
    }

    // ── translate success ───────────────────────────────────────────

    @Test
    fun `translate success sets translatedText and saves history`() = runTest {
        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("こんにちは"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.updateSourceText("Hello")
        vm.translate("en", "ja")

        assertEquals("こんにちは", vm.translatedText)
        assertTrue(vm.statusMessage.isEmpty())
        verify(historyRepo).save(any())
    }

    // ── translate error ─────────────────────────────────────────────

    @Test
    fun `translate error sets statusMessage`() = runTest {
        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Error("Service unavailable"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.updateSourceText("Hello")
        vm.translate("en", "ja")

        assertTrue(vm.statusMessage.contains("Service unavailable"))
    }

    // ── translate with auto-detect ──────────────────────────────────

    @Test
    fun `translate with auto-detect uses detected language from translation result`() = runTest {
        // The translation API now returns detected language inline (single call)
        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success(
                text = "Translated",
                detectedLanguage = "en",
                detectedScore = 0.95
            ))

        var detectedLang: String? = null
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.updateSourceText("Hello world")
        vm.translate("auto", "ja") { detectedLang = it }

        assertNotNull(detectedLang)
        assertEquals("Translated", vm.translatedText)
        // Should pass empty string for auto-detect (not "auto")
        verify(translateTextUseCase).invoke("Hello world", "", "ja")
        // detectLanguageUseCase should NOT be called (single-call auto-detect)
        verifyNoInteractions(detectLanguageUseCase)
    }

    @Test
    fun `translate with auto-detect falls back to detectLanguage when translation response omits detected language`() = runTest {
        // Translation succeeds but without detected language info
        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success(text = "Translated"))
        whenever(detectLanguageUseCase.invoke(any())).thenReturn(
            DetectedLanguage(language = "en", score = 0.91, isTranslationSupported = true)
        )

        var detectedLang: String? = null
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.updateSourceText("Hello world")
        vm.translate("auto", "ja") { detectedLang = it }

        assertEquals("Translated", vm.translatedText)
        assertEquals("en-US", detectedLang)
        verify(detectLanguageUseCase).invoke("Hello world")
        verify(historyRepo).save(any())
    }

    @Test
    fun `speakOriginal in auto mode detects language and speaks using mapped code`() = runTest {
        whenever(detectLanguageUseCase.invoke(any())).thenReturn(
            DetectedLanguage(language = "es", score = 0.87, isTranslationSupported = true)
        )
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success("ok"))

        var callbackLang: String? = null
        val vm = buildViewModel()
        vm.updateSourceText("Hola")
        vm.speakOriginal("auto") { callbackLang = it }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("es-ES", callbackLang)
        verify(detectLanguageUseCase).invoke("Hola")
        verify(speakTextUseCase).invoke(eq("Hola"), eq("es-ES"), isNull())
    }

    // ── OCR success ─────────────────────────────────────────────────

    @Test
    fun `recognizeTextFromImage success sets recognizedText`() = runTest {
        val uri = mock<Uri>()
        whenever(recognizeTextFromImageUseCase.invoke(any(), anyOrNull()))
            .thenReturn(OcrResult.Success("OCR text"))

        val vm = buildViewModel()
        vm.recognizeTextFromImage(uri)

        assertEquals("OCR text", vm.recognizedText)
        assertEquals(RecognizePhase.Idle, vm.recognizePhase)
    }

    // ── OCR error ───────────────────────────────────────────────────

    @Test
    fun `recognizeTextFromImage error sets statusMessage`() = runTest {
        val uri = mock<Uri>()
        whenever(recognizeTextFromImageUseCase.invoke(any(), anyOrNull()))
            .thenReturn(OcrResult.Error("Image too blurry"))

        val vm = buildViewModel()
        vm.recognizeTextFromImage(uri)

        assertTrue(vm.statusMessage.contains("Image too blurry"))
    }

    // ── updateSourceText ────────────────────────────────────────────

    @Test
    fun `updateSourceText updates recognized text`() = runTest {
        val vm = buildViewModel()
        vm.updateSourceText("New input text")

        assertEquals("New input text", vm.recognizedText)
    }

    // ── swapTexts ───────────────────────────────────────────────────

    @Test
    fun `swapTexts swaps recognized and translated text`() = runTest {
        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("Translated"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.updateSourceText("Original")
        vm.translate("en", "ja")

        assertEquals("Original", vm.recognizedText)
        assertEquals("Translated", vm.translatedText)

        vm.swapTexts()
        assertEquals("Translated", vm.recognizedText)
        assertEquals("Original", vm.translatedText)
    }

    // ── recognizeWithAutoDetect success ─────────────────────────────

    @Test
    fun `recognizeWithAutoDetect success sets text and calls callback`() = runTest {
        val result = AutoDetectRecognitionResult(
            text = "Detected speech",
            detectedLanguage = "en-US"
        )
        whenever(autoDetectRecognizeUseCase.invoke(any())).thenReturn(Result.success(result))

        var callbackLang: String? = null
        val vm = buildViewModel()
        vm.recognizeWithAutoDetect(listOf("en-US", "ja-JP")) { callbackLang = it }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Detected speech", vm.recognizedText)
        assertEquals(RecognizePhase.Idle, vm.recognizePhase)
        assertEquals("en-US", callbackLang)
        assertTrue("Detected status should auto-clear", vm.statusMessage.isEmpty())
    }

    @Test
    fun `refreshQuickTranslateState clears translated text for retry`() = runTest {
        whenever(translateTextUseCase.invoke(any(), any(), any()))
            .thenReturn(SpeechResult.Success("Translated"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.updateSourceText("Hello")
        vm.translate("en", "ja")
        assertEquals("Translated", vm.translatedText)

        vm.refreshQuickTranslateState()

        assertTrue(vm.translatedText.isEmpty())
        assertTrue(vm.statusMessage.contains("Auto-detect", ignoreCase = true))
    }

    // ── recognizeWithAutoDetect error ───────────────────────────────

    @Test
    fun `recognizeWithAutoDetect error sets statusMessage`() = runTest {
        whenever(autoDetectRecognizeUseCase.invoke(any()))
            .thenReturn(Result.failure(RuntimeException("Recognition failed")))

        val vm = buildViewModel()
        vm.recognizeWithAutoDetect(listOf("en-US")) {}
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.statusMessage.contains("Recognition error"))
        assertEquals(RecognizePhase.Idle, vm.recognizePhase)
    }
}
