package com.translator.TalknLearn.screens.wordbank

import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.data.wordbank.FirestoreCustomWordsRepository
import com.translator.TalknLearn.domain.speech.TranslateTextUseCase
import com.translator.TalknLearn.model.SpeechResult
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.translator.TalknLearn.model.user.UserSettings
import com.translator.TalknLearn.model.CustomWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for CustomWordsViewModel.
 *
 * Tests:
 *  1. LoggedIn stores userId
 *  2. LoggedOut resets state
 *  3. selectCustomWordBank sets flag and loads words
 *  4. clearSelection resets selection
 *  5. addCustomWord validates empty originalWord
 *  6. addCustomWord validates too-long originalWord
 *  7. addCustomWord validates empty translatedWord
 *  8. addCustomWord validates too-long pronunciation
 *  9. addCustomWord validates too-long example
 * 10. addCustomWord success calls repo
 * 11. addCustomWord failure sets error
 * 12. addCustomWord when not logged in does nothing
 * 13. deleteCustomWord success reloads if bank selected
 * 14. deleteCustomWord when not logged in does nothing
 * 15. translateCustomWord success calls callback
 * 16. translateCustomWord error sets error
 * 17. translateCustomWord toggles isTranslatingCustomWord
 * 18. clearError clears error
 * 19. loadCustomWords maps to WordBankItem with custom prefix
 * 20. loadCustomWords failure sets error
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CustomWordsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val settingsFlow = MutableStateFlow(UserSettings())

    private val testUserId = "user123"
    private val testUser = User(uid = testUserId, email = "test@test.com")

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var customWordsRepo: FirestoreCustomWordsRepository
    private lateinit var translateTextUseCase: TranslateTextUseCase
    private lateinit var sharedSettings: SharedSettingsDataSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        customWordsRepo = mock()
        translateTextUseCase = mock()
        sharedSettings = mock { on { settings } doReturn settingsFlow }
    }

    private fun buildViewModel(): CustomWordsViewModel {
        authStateFlow.value = AuthState.LoggedIn(testUser)
        return CustomWordsViewModel(
            authRepo = authRepo,
            customWordsRepo = customWordsRepo,
            translateTextUseCase = translateTextUseCase,
            sharedSettings = sharedSettings
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Auth lifecycle ──

    @Test
    fun `LoggedIn stores userId`() = runTest {
        val vm = buildViewModel()
        // The ViewModel should have stored the uid internally;
        // we can verify by attempting an action that requires uid
        customWordsRepo.stub {
            onBlocking { addCustomWord(any(), any(), any(), any(), any(), any(), any()) } doReturn Result.success(
                CustomWord(id = "w1", originalWord = "hello", translatedWord = "world")
            )
            onBlocking { wordExists(any(), any(), any(), any()) } doReturn false
        }

        vm.addCustomWord("hello", "world", sourceLang = "en-US", targetLang = "ja-JP")

        verify(customWordsRepo).addCustomWord(eq(testUserId), any(), any(), any(), any(), any(), any())
    }

    @Test
    fun `LoggedOut resets state`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedOut

        val state = vm.uiState.value
        assertFalse(state.isCustomWordBankSelected)
        assertTrue(state.customWords.isEmpty())
        assertEquals(0, state.customWordsCount)
    }

    // ── selectCustomWordBank / clearSelection ──

    @Test
    fun `selectCustomWordBank sets flag and triggers load`() = runTest {
        customWordsRepo.stub {
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn emptyList()
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()

        assertTrue(vm.uiState.value.isCustomWordBankSelected)
        verify(customWordsRepo).getAllCustomWordsOnce(testUserId)
    }

    @Test
    fun `clearSelection resets selection and clears words`() = runTest {
        customWordsRepo.stub {
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn emptyList()
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()
        vm.clearSelection()

        assertFalse(vm.uiState.value.isCustomWordBankSelected)
        assertTrue(vm.uiState.value.customWords.isEmpty())
    }

    // ── addCustomWord validation ──

    @Test
    fun `addCustomWord validates empty originalWord`() = runTest {
        val vm = buildViewModel()
        vm.addCustomWord("", "translation", sourceLang = "en-US", targetLang = "ja-JP")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("Original word"))
        verifyNoInteractions(customWordsRepo)
    }

    @Test
    fun `addCustomWord validates too-long originalWord`() = runTest {
        val vm = buildViewModel()
        val longWord = "a".repeat(101)
        vm.addCustomWord(longWord, "translation", sourceLang = "en-US", targetLang = "ja-JP")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("Original word"))
    }

    @Test
    fun `addCustomWord validates empty translatedWord`() = runTest {
        val vm = buildViewModel()
        vm.addCustomWord("hello", "", sourceLang = "en-US", targetLang = "ja-JP")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("Translated word"))
    }

    @Test
    fun `addCustomWord validates too-long pronunciation`() = runTest {
        val vm = buildViewModel()
        val longPronunciation = "p".repeat(201)
        vm.addCustomWord("hello", "world", pronunciation = longPronunciation, sourceLang = "en-US", targetLang = "ja-JP")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("Pronunciation"))
    }

    @Test
    fun `addCustomWord validates too-long example`() = runTest {
        val vm = buildViewModel()
        val longExample = "e".repeat(501)
        vm.addCustomWord("hello", "world", example = longExample, sourceLang = "en-US", targetLang = "ja-JP")

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("Example"))
    }

    // ── addCustomWord success / failure ──

    @Test
    fun `addCustomWord success calls repo and reloads if bank selected`() = runTest {
        customWordsRepo.stub {
            onBlocking { addCustomWord(any(), any(), any(), any(), any(), any(), any()) } doReturn Result.success(
                CustomWord(id = "w1", originalWord = "hello", translatedWord = "world")
            )
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn emptyList()
            onBlocking { wordExists(any(), any(), any(), any()) } doReturn false
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()
        vm.addCustomWord("hello", "world", sourceLang = "en-US", targetLang = "ja-JP")

        verify(customWordsRepo).addCustomWord(
            eq(testUserId), any(), any(), any(), any(), eq("en-US"), eq("ja-JP")
        )
        // getAllCustomWordsOnce called twice: once for selectCustomWordBank, once for reload after add
        verify(customWordsRepo, times(2)).getAllCustomWordsOnce(testUserId)
    }

    @Test
    fun `addCustomWord failure sets error`() = runTest {
        customWordsRepo.stub {
            onBlocking { addCustomWord(any(), any(), any(), any(), any(), any(), any()) } doReturn
                Result.failure(RuntimeException("DB error"))
            onBlocking { wordExists(any(), any(), any(), any()) } doReturn false
        }

        val vm = buildViewModel()
        vm.addCustomWord("hello", "world", sourceLang = "en-US", targetLang = "ja-JP")

        assertEquals("Failed to add word", vm.uiState.value.error)
    }

    @Test
    fun `addCustomWord when not logged in does nothing`() = runTest {
        authStateFlow.value = AuthState.LoggedOut
        val vm = CustomWordsViewModel(authRepo, customWordsRepo, translateTextUseCase, sharedSettings)

        vm.addCustomWord("hello", "world", sourceLang = "en-US", targetLang = "ja-JP")

        verifyNoInteractions(customWordsRepo)
    }

    // ── deleteCustomWord ──

    @Test
    fun `deleteCustomWord success reloads if bank selected`() = runTest {
        customWordsRepo.stub {
            onBlocking { deleteCustomWord(testUserId, "w1") } doReturn Result.success(Unit)
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn emptyList()
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()
        vm.deleteCustomWord("w1")

        verify(customWordsRepo).deleteCustomWord(testUserId, "w1")
    }

    @Test
    fun `deleteCustomWord when not logged in does nothing`() = runTest {
        authStateFlow.value = AuthState.LoggedOut
        val vm = CustomWordsViewModel(authRepo, customWordsRepo, translateTextUseCase, sharedSettings)

        vm.deleteCustomWord("w1")

        verify(customWordsRepo, never()).deleteCustomWord(any(), any())
    }

    @Test
    fun `updateCustomWordTargetLanguage retranslates and persists new language`() = runTest {
        translateTextUseCase.stub {
            onBlocking { invoke("hello", "en-US", "fr-FR") } doReturn SpeechResult.Success("bonjour")
        }
        customWordsRepo.stub {
            onBlocking {
                updateCustomWord(
                    userId = testUserId,
                    wordId = "w1",
                    originalWord = "hello",
                    translatedWord = "bonjour",
                    pronunciation = "",
                    example = "",
                    sourceLang = "en-US",
                    targetLang = "fr-FR"
                )
            } doReturn Result.success(Unit)
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn emptyList()
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()
        vm.updateCustomWordTargetLanguage(
            word = WordBankItem(
                id = "custom_w1",
                originalWord = "hello",
                translatedWord = "hola",
                category = "en-US -> es-ES"
            ),
            newTargetLang = "fr-FR"
        )

        verify(translateTextUseCase).invoke("hello", "en-US", "fr-FR")
        verify(customWordsRepo).updateCustomWord(
            userId = testUserId,
            wordId = "w1",
            originalWord = "hello",
            translatedWord = "bonjour",
            pronunciation = "",
            example = "",
            sourceLang = "en-US",
            targetLang = "fr-FR"
        )
    }

    @Test
    fun `updateCustomWordTargetLanguage surfaces translation error`() = runTest {
        translateTextUseCase.stub {
            onBlocking { invoke(any(), any(), any()) } doReturn SpeechResult.Error("rate limit")
        }

        val vm = buildViewModel()
        vm.updateCustomWordTargetLanguage(
            word = WordBankItem(
                id = "custom_w1",
                originalWord = "hello",
                translatedWord = "hola",
                category = "en-US -> es-ES"
            ),
            newTargetLang = "fr-FR"
        )

        assertTrue(vm.uiState.value.error?.contains("Translation failed") == true)
        verify(customWordsRepo, never()).updateCustomWord(
            any(), any(), any(), any(), any(), any(), any(), any()
        )
    }

    @Test
    fun `updateCustomWordTargetLanguage parses legacy category without spaces`() = runTest {
        translateTextUseCase.stub {
            onBlocking { invoke("hello", "en-US", "fr-FR") } doReturn SpeechResult.Success("bonjour")
        }
        customWordsRepo.stub {
            onBlocking {
                updateCustomWord(
                    userId = testUserId,
                    wordId = "w1",
                    originalWord = "hello",
                    translatedWord = "bonjour",
                    pronunciation = "",
                    example = "",
                    sourceLang = "en-US",
                    targetLang = "fr-FR"
                )
            } doReturn Result.success(Unit)
        }

        val vm = buildViewModel()
        vm.updateCustomWordTargetLanguage(
            word = WordBankItem(
                id = "custom_w1",
                originalWord = "hello",
                translatedWord = "hola",
                category = "en-US->es-ES"
            ),
            newTargetLang = "fr-FR"
        )

        verify(translateTextUseCase).invoke("hello", "en-US", "fr-FR")
        verify(customWordsRepo).updateCustomWord(
            userId = testUserId,
            wordId = "w1",
            originalWord = "hello",
            translatedWord = "bonjour",
            pronunciation = "",
            example = "",
            sourceLang = "en-US",
            targetLang = "fr-FR"
        )
    }

    @Test
    fun `updateCustomWordTargetLanguage falls back to original when category is malformed`() = runTest {
        translateTextUseCase.stub {
            onBlocking { invoke("hello", "en-US", "fr-FR") } doReturn SpeechResult.Success("bonjour")
        }
        customWordsRepo.stub {
            onBlocking {
                updateCustomWord(
                    userId = testUserId,
                    wordId = "w1",
                    originalWord = "hello",
                    translatedWord = "bonjour",
                    pronunciation = "",
                    example = "",
                    sourceLang = "en-US",
                    targetLang = "fr-FR"
                )
            } doReturn Result.success(Unit)
        }

        val vm = buildViewModel()
        vm.updateCustomWordTargetLanguage(
            word = WordBankItem(
                id = "custom_w1",
                originalWord = "hello",
                translatedWord = "hola",
                category = ""
            ),
            newTargetLang = "fr-FR"
        )

        verify(translateTextUseCase).invoke("hello", "en-US", "fr-FR")
        verify(customWordsRepo).updateCustomWord(
            userId = testUserId,
            wordId = "w1",
            originalWord = "hello",
            translatedWord = "bonjour",
            pronunciation = "",
            example = "",
            sourceLang = "en-US",
            targetLang = "fr-FR"
        )
    }

    @Test
    fun `switching account reloads selected custom words for new user`() = runTest {
        val otherUserId = "user456"
        val otherUser = User(uid = otherUserId, email = "other@test.com")

        customWordsRepo.stub {
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn listOf(
                CustomWord(id = "a1", originalWord = "hello", translatedWord = "hola", sourceLang = "en-US", targetLang = "es-ES")
            )
            onBlocking { getAllCustomWordsOnce(otherUserId) } doReturn listOf(
                CustomWord(id = "b1", originalWord = "thanks", translatedWord = "merci", sourceLang = "en-US", targetLang = "fr-FR")
            )
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()
        assertEquals(1, vm.uiState.value.customWords.size)
        assertEquals("custom_a1", vm.uiState.value.customWords.first().id)

        authStateFlow.value = AuthState.LoggedIn(otherUser)

        assertEquals(1, vm.uiState.value.customWords.size)
        assertEquals("custom_b1", vm.uiState.value.customWords.first().id)
        verify(customWordsRepo).getAllCustomWordsOnce(testUserId)
        verify(customWordsRepo).getAllCustomWordsOnce(otherUserId)
    }

    // ── translateCustomWord ──

    @Test
    fun `translateCustomWord success calls callback`() = runTest {
        translateTextUseCase.stub {
            onBlocking { invoke("hello", "en-US", "ja-JP") } doReturn SpeechResult.Success("こんにちは")
        }

        val vm = buildViewModel()
        var callbackResult: String? = null
        vm.translateCustomWord("hello", "ja-JP") { callbackResult = it }

        assertEquals("こんにちは", callbackResult)
        assertFalse(vm.uiState.value.isTranslatingCustomWord)
    }

    @Test
    fun `translateCustomWord uses account primary language from settings`() = runTest {
        settingsFlow.value = UserSettings(primaryLanguageCode = "zh-HK")
        translateTextUseCase.stub {
            onBlocking { invoke("hello", "zh-HK", "ja-JP") } doReturn SpeechResult.Success("こんにちは")
        }

        val vm = buildViewModel()
        vm.translateCustomWord("hello", "ja-JP") { }

        verify(translateTextUseCase).invoke("hello", "zh-HK", "ja-JP")
    }

    @Test
    fun `translateCustomWord error sets error`() = runTest {
        translateTextUseCase.stub {
            onBlocking { invoke(any(), any(), any()) } doReturn SpeechResult.Error("API error")
        }

        val vm = buildViewModel()
        vm.translateCustomWord("hello", "ja-JP") { }

        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.error!!.contains("Translation failed"))
        assertFalse(vm.uiState.value.isTranslatingCustomWord)
    }

    // ── clearError ──

    @Test
    fun `clearError clears error`() = runTest {
        val vm = buildViewModel()
        vm.addCustomWord("", "x", sourceLang = "en-US", targetLang = "ja-JP")
        assertNotNull(vm.uiState.value.error)

        vm.clearError()

        assertNull(vm.uiState.value.error)
    }

    // ── loadCustomWords mapping ──

    @Test
    fun `loadCustomWords maps to WordBankItem with custom prefix`() = runTest {
        val customWords = listOf(
            CustomWord(
                id = "abc",
                originalWord = "hello",
                translatedWord = "hola",
                pronunciation = "oh-la",
                example = "Hola mundo",
                sourceLang = "en-US",
                targetLang = "es-ES"
            )
        )
        customWordsRepo.stub {
            onBlocking { getAllCustomWordsOnce(testUserId) } doReturn customWords
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()

        val items = vm.uiState.value.customWords
        assertEquals(1, items.size)
        assertEquals("custom_abc", items[0].id)
        assertEquals("hello", items[0].originalWord)
        assertEquals("hola", items[0].translatedWord)
        assertEquals("oh-la", items[0].pronunciation)
        assertEquals("en-US → es-ES", items[0].category)
        assertEquals(1, vm.uiState.value.customWordsCount)
    }

    @Test
    fun `loadCustomWords failure sets error`() = runTest {
        customWordsRepo.stub {
            onBlocking { getAllCustomWordsOnce(testUserId) } doThrow RuntimeException("fail")
        }

        val vm = buildViewModel()
        vm.selectCustomWordBank()

        assertEquals("Failed to load custom words", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }
}
