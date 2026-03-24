package com.example.fyp.screens.wordbank

import android.content.Context
import com.example.fyp.data.friends.SharedFriendsDataSource
import com.example.fyp.data.history.SharedHistoryDataSource
import com.example.fyp.data.settings.SharedSettingsDataSource
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.wordbank.FirestoreCustomWordsRepository
import com.example.fyp.data.wordbank.FirestoreWordBankRepository
import com.example.fyp.data.wordbank.WordBankCacheDataStore
import com.example.fyp.data.wordbank.WordBankGenerationRepository
import com.example.fyp.domain.friends.ShareWordUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.example.fyp.model.SpeechResult
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.FriendRelation
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.user.User
import com.example.fyp.model.user.UserSettings
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
 * Unit tests for WordBankViewModel.
 *
 * Tests:
 * 1. Logout resets state with "Not logged in" error
 * 2. Login starts listening for history records
 * 3. selectLanguage success loads merged word bank
 * 4. selectLanguage failure sets error
 * 5. clearSelection resets selected state
 * 6. deleteWordFromBank removes word from current bank
 * 7. canRegenerate returns true when enough new records
 * 8. canRegenerate returns false when insufficient history growth
 * 9. cancelGeneration resets generating state
 * 10. consumeWordBankGenerationCompleted clears event
 * 11. setPrimaryLanguageCode updates and clears selection
 * 12. speakWord triggers TTS with correct language
 * 13. shareWord sends through ShareWordUseCase
 * 14. clearShareMessages clears share state
 * 15. generateWordBank rejects when no records
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WordBankViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val settingsFlow = MutableStateFlow(UserSettings())
    private val historyRecordsFlow = MutableStateFlow<List<TranslationRecord>>(emptyList())
    private val languageCountsFlow = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val friendsFlow = MutableStateFlow<List<FriendRelation>>(emptyList())

    private lateinit var context: Context
    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sharedHistoryDataSource: SharedHistoryDataSource
    private lateinit var wordBankRepo: FirestoreWordBankRepository
    private lateinit var wordBankGenRepo: WordBankGenerationRepository
    private lateinit var speakTextUseCase: SpeakTextUseCase
    private lateinit var customWordsRepo: FirestoreCustomWordsRepository
    private lateinit var translateTextUseCase: TranslateTextUseCase
    private lateinit var sharedSettings: SharedSettingsDataSource
    private lateinit var wordBankCacheDataStore: WordBankCacheDataStore
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource
    private lateinit var shareWordUseCase: ShareWordUseCase

    private val testUser = User(uid = "u1", email = "test@test.com")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        context = mock()
        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        sharedHistoryDataSource = mock {
            on { historyRecords } doReturn historyRecordsFlow
            on { languageCounts } doReturn languageCountsFlow
            on { isLoading } doReturn MutableStateFlow(false)
            on { error } doReturn MutableStateFlow(null)
            on { historyCount } doReturn MutableStateFlow(0)
        }
        wordBankRepo = mock()
        wordBankGenRepo = mock()
        speakTextUseCase = mock()
        customWordsRepo = mock()
        translateTextUseCase = mock()
        sharedSettings = mock { on { settings } doReturn settingsFlow }
        wordBankCacheDataStore = mock()
        sharedFriendsDataSource = mock {
            on { friends } doReturn friendsFlow
        }
        shareWordUseCase = mock()

        runTest {
            whenever(customWordsRepo.getAllCustomWordsOnce(any())).thenReturn(emptyList())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WordBankViewModel(
        context = context,
        authRepo = authRepo,
        sharedHistoryDataSource = sharedHistoryDataSource,
        wordBankRepo = wordBankRepo,
        wordBankGenRepo = wordBankGenRepo,
        speakTextUseCase = speakTextUseCase,
        customWordsRepo = customWordsRepo,
        translateTextUseCase = translateTextUseCase,
        sharedSettings = sharedSettings,
        wordBankCacheDataStore = wordBankCacheDataStore,
        sharedFriendsDataSource = sharedFriendsDataSource,
        shareWordUseCase = shareWordUseCase,
    )

    // ── Logout resets state ─────────────────────────────────────────

    @Test
    fun `logout sets error not logged in`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedOut

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Not logged in", state.error)
    }

    // ── Loading state during auth loading ───────────────────────────

    @Test
    fun `loading state shows during auth loading`() = runTest {
        authStateFlow.value = AuthState.Loading
        val vm = buildViewModel()

        assertTrue(vm.uiState.value.isLoading)
    }

    // ── Login starts listening ──────────────────────────────────────

    @Test
    fun `login starts observing history`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        verify(sharedHistoryDataSource).startObserving("u1")
    }

    // ── selectLanguage success ──────────────────────────────────────

    @Test
    fun `selectLanguage loads word bank`() = runTest {
        val wordBank = WordBank(
            primaryLanguageCode = "en-US",
            targetLanguageCode = "ja",
            words = listOf(
                WordBankItem(id = "1", originalWord = "hello", translatedWord = "こんにちは",
                    pronunciation = "konnichiwa", example = "Hello world", category = "greetings", difficulty = "easy")
            )
        )
        whenever(wordBankRepo.getWordBank(any(), any(), any())).thenReturn(wordBank)
        whenever(customWordsRepo.getCustomWordsOnce(any(), any(), any())).thenReturn(emptyList())

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.selectLanguage("ja")

        val state = vm.uiState.value
        assertEquals("ja", state.selectedLanguageCode)
        assertNotNull(state.currentWordBank)
        assertEquals(1, state.currentWordBank!!.words.size)
        assertFalse(state.isLoading)
    }

    // ── selectLanguage failure ──────────────────────────────────────

    @Test
    fun `selectLanguage failure sets error`() = runTest {
        whenever(wordBankRepo.getWordBank(any(), any(), any()))
            .thenThrow(RuntimeException("Firestore unavailable"))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.selectLanguage("ja")

        assertNotNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    // ── clearSelection ──────────────────────────────────────────────

    @Test
    fun `clearSelection resets selected state`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        whenever(wordBankRepo.getWordBank(any(), any(), any())).thenReturn(null)
        whenever(customWordsRepo.getCustomWordsOnce(any(), any(), any())).thenReturn(emptyList())
        vm.selectLanguage("ja")

        vm.clearSelection()

        assertNull(vm.uiState.value.selectedLanguageCode)
        assertNull(vm.uiState.value.currentWordBank)
        assertFalse(vm.uiState.value.isCustomWordBankSelected)
    }

    // ── deleteWordFromBank ──────────────────────────────────────────

    @Test
    fun `deleteWordFromBank removes word from list`() = runTest {
        val words = listOf(
            WordBankItem(id = "w1", originalWord = "hello", translatedWord = "こんにちは",
                pronunciation = "", example = "", category = "", difficulty = ""),
            WordBankItem(id = "w2", originalWord = "goodbye", translatedWord = "さようなら",
                pronunciation = "", example = "", category = "", difficulty = "")
        )
        val wordBank = WordBank(primaryLanguageCode = "en-US", targetLanguageCode = "ja", words = words)

        whenever(wordBankRepo.getWordBank(any(), any(), any())).thenReturn(wordBank)
        whenever(customWordsRepo.getCustomWordsOnce(any(), any(), any())).thenReturn(emptyList())

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.selectLanguage("ja")

        vm.deleteWordFromBank("w1", "ja")

        val remaining = vm.uiState.value.currentWordBank?.words ?: emptyList()
        assertEquals(1, remaining.size)
        assertEquals("w2", remaining[0].id)
    }

    // ── cancelGeneration ────────────────────────────────────────────

    @Test
    fun `cancelGeneration resets isGenerating`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.cancelGeneration()

        assertFalse(vm.uiState.value.isGenerating)
    }

    // ── consumeWordBankGenerationCompleted ───────────────────────────

    @Test
    fun `consumeWordBankGenerationCompleted clears event`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.consumeWordBankGenerationCompleted()

        assertNull(vm.uiState.value.wordBankGenerationCompleted)
    }

    // ── setPrimaryLanguageCode ──────────────────────────────────────

    @Test
    fun `setPrimaryLanguageCode updates primary and clears selection`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.setPrimaryLanguageCode("ja")

        assertEquals("ja", vm.getPrimaryLanguageCode())
        assertNull(vm.uiState.value.selectedLanguageCode)
        assertNull(vm.uiState.value.currentWordBank)
    }

    @Test
    fun `settings primary language change updates word bank primary automatically`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        settingsFlow.value = UserSettings(primaryLanguageCode = "en-US")
        assertEquals("en-US", vm.getPrimaryLanguageCode())

        settingsFlow.value = UserSettings(primaryLanguageCode = "zh-HK")
        assertEquals("zh-HK", vm.getPrimaryLanguageCode())

        verify(sharedSettings, atLeastOnce()).startObserving("u1")
    }

    // ── shareWord success ───────────────────────────────────────────

    @Test
    fun `shareWord success sets shareSuccess message`() = runTest {
        val word = WordBankItem(id = "w1", originalWord = "hello", translatedWord = "こんにちは",
            pronunciation = "", example = "Hello example", category = "", difficulty = "")
        val wordBank = WordBank(primaryLanguageCode = "en-US", targetLanguageCode = "ja", words = listOf(word))

        whenever(wordBankRepo.getWordBank(any(), any(), any())).thenReturn(wordBank)
        whenever(customWordsRepo.getCustomWordsOnce(any(), any(), any())).thenReturn(emptyList())
        whenever(sharedFriendsDataSource.getCachedUsername(any())).thenReturn("myuser")
        whenever(shareWordUseCase.invoke(eq(UserId("u1")), any(), eq(UserId("friend1")), any(), any(), any(), any(), any()))
            .thenReturn(Result.success(SharedItem(itemId = "si1", fromUserId = "u1", toUserId = "friend1")))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.selectLanguage("ja")

        vm.shareWord(word, UserId("friend1"))

        assertNotNull(vm.uiState.value.shareSuccess)
        assertFalse(vm.uiState.value.isSharing)
    }

    // ── shareWord failure ───────────────────────────────────────────

    @Test
    fun `shareWord failure sets shareError message`() = runTest {
        val word = WordBankItem(id = "w1", originalWord = "hello", translatedWord = "こんにちは",
            pronunciation = "", example = "", category = "", difficulty = "")
        val wordBank = WordBank(primaryLanguageCode = "en-US", targetLanguageCode = "ja", words = listOf(word))

        whenever(wordBankRepo.getWordBank(any(), any(), any())).thenReturn(wordBank)
        whenever(customWordsRepo.getCustomWordsOnce(any(), any(), any())).thenReturn(emptyList())
        whenever(sharedFriendsDataSource.getCachedUsername(any())).thenReturn("myuser")
        // Use raw values to avoid inline value class boxing mismatch with eq() matchers
        whenever(shareWordUseCase.invoke(UserId("u1"), "myuser", UserId("friend1"), "hello", "こんにちは", "en-US", "ja", ""))
            .thenReturn(Result.failure(RuntimeException("Network error")))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.selectLanguage("ja")

        vm.shareWord(word, UserId("friend1"))

        assertNotNull(vm.uiState.value.shareError)
        assertFalse(vm.uiState.value.isSharing)
    }

    // ── clearShareMessages ──────────────────────────────────────────

    @Test
    fun `clearShareMessages clears share state`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.clearShareMessages()

        assertNull(vm.uiState.value.shareSuccess)
        assertNull(vm.uiState.value.shareError)
    }

    // ── setCustomWordBankSelected ───────────────────────────────────

    @Test
    fun `setCustomWordBankSelected updates flag`() = runTest {
        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)

        vm.setCustomWordBankSelected(true)
        assertTrue(vm.uiState.value.isCustomWordBankSelected)

        vm.setCustomWordBankSelected(false)
        assertFalse(vm.uiState.value.isCustomWordBankSelected)
    }

    // ── speakWord delegates to TTS ──────────────────────────────────

    @Test
    fun `speakWord triggers TTS with correct text`() = runTest {
        val word = WordBankItem(id = "w1", originalWord = "hello", translatedWord = "こんにちは",
            pronunciation = "", example = "", category = "", difficulty = "")
        val wordBank = WordBank(primaryLanguageCode = "en-US", targetLanguageCode = "ja", words = listOf(word))

        whenever(wordBankRepo.getWordBank(any(), any(), any())).thenReturn(wordBank)
        whenever(customWordsRepo.getCustomWordsOnce(any(), any(), any())).thenReturn(emptyList())
        whenever(speakTextUseCase.invoke(any(), any(), anyOrNull()))
            .thenReturn(SpeechResult.Success(""))

        val vm = buildViewModel()
        authStateFlow.value = AuthState.LoggedIn(testUser)
        vm.selectLanguage("ja")

        vm.speakWord(word, SpeakingType.ORIGINAL)

        // Verify TTS was called with original word text in target language
        verify(speakTextUseCase).invoke(eq("hello"), eq("ja"), anyOrNull())
    }
}
