package com.translator.TalknLearn.screens.learning

import androidx.lifecycle.SavedStateHandle
import com.translator.TalknLearn.data.friends.SharedFriendsDataSource
import com.translator.TalknLearn.data.history.SharedHistoryDataSource
import com.translator.TalknLearn.data.learning.LearningSheetDoc
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.domain.friends.ShareLearningMaterialUseCase
import com.translator.TalknLearn.domain.learning.GeneratedQuizDoc
import com.translator.TalknLearn.domain.learning.LearningSheetsRepository
import com.translator.TalknLearn.domain.learning.ParseAndStoreQuizUseCase
import com.translator.TalknLearn.domain.learning.QuizRepository
import com.translator.TalknLearn.model.LanguageCode
import com.translator.TalknLearn.model.QuizAttempt
import com.translator.TalknLearn.model.QuizQuestion
import com.translator.TalknLearn.model.TranslationRecord
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.FriendRelation
import com.translator.TalknLearn.model.friends.SharedItem
import com.translator.TalknLearn.model.friends.SharedItemType
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.translator.TalknLearn.observability.PerformanceTracer
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
 * Unit tests for LearningSheetViewModel.
 *
 * Tests:
 *  1. LoggedOut sets error and resets quiz state
 *  2. Loading sets isLoading
 *  3. LoggedIn loads sheet
 *  4. loadSheet success sets content
 *  5. loadSheet failure sets error
 *  6. loadSheet returns early when codes are blank
 *  7. initializeQuiz no doc sets error
 *  8. initializeQuiz empty questions sets corrupted error
 *  9. initializeQuiz success sets questions and attempt
 * 10. initializeQuiz exception sets quizError
 * 11. recordQuizAnswer no attempt is no-op
 * 12. submitQuiz failure sets quizError
 * 13. resetQuiz clears quiz state
 * 14. shareSheet with blank content sets error
 * 15. shareSheet success sets shareSuccessKey
 * 16. shareSheet failure sets shareError
 * 17. showShareDialog and dismissShareDialog toggle state
 * 18. clearShareMessages clears both messages
 * 19. saveSheetToSelf uses own uid
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LearningSheetViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val historyRecordsFlow = MutableStateFlow<List<TranslationRecord>>(emptyList())
    private val friendsFlow = MutableStateFlow<List<FriendRelation>>(emptyList())

    private val testUserId = "user123"
    private val testUser = User(uid = testUserId, email = "test@test.com")
    private val primaryCode = "en-US"
    private val targetCode = "ja-JP"

    // Concrete value class instances to avoid Mockito any() NPE with inlined value classes
    private val uid = UserId(testUserId)
    private val primary = LanguageCode(primaryCode)
    private val target = LanguageCode(targetCode)

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var sheetsRepo: LearningSheetsRepository
    private lateinit var sharedHistoryDataSource: SharedHistoryDataSource
    private lateinit var parseAndStoreQuiz: ParseAndStoreQuizUseCase
    private lateinit var quizRepo: QuizRepository
    private lateinit var shareLearningMaterialUseCase: ShareLearningMaterialUseCase
    private lateinit var sharedFriendsDataSource: SharedFriendsDataSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepo = mock { on { currentUserState } doReturn authStateFlow }
        sheetsRepo = mock()
        sharedHistoryDataSource = mock {
            on { historyRecords } doReturn historyRecordsFlow
        }
        parseAndStoreQuiz = mock()
        quizRepo = mock()
        shareLearningMaterialUseCase = mock()
        sharedFriendsDataSource = mock {
            on { friends } doReturn friendsFlow
        }

        whenever(sharedHistoryDataSource.getCountForLanguage(any())).thenReturn(0)
        whenever(sharedFriendsDataSource.getCachedUsername(any())).thenReturn(null)
    }

    private fun buildSavedStateHandle(): SavedStateHandle = SavedStateHandle(
        mapOf("primaryCode" to primaryCode, "targetCode" to targetCode)
    )

    private fun buildViewModel(
        savedState: SavedStateHandle = buildSavedStateHandle()
    ): LearningSheetViewModel {
        authStateFlow.value = AuthState.LoggedIn(testUser)
        return LearningSheetViewModel(
            savedStateHandle = savedState,
            authRepo = authRepo,
            sheetsRepo = sheetsRepo,
            sharedHistoryDataSource = sharedHistoryDataSource,
            parseAndStoreQuiz = parseAndStoreQuiz,
            quizRepo = quizRepo,
            shareLearningMaterialUseCase = shareLearningMaterialUseCase,
            sharedFriendsDataSource = sharedFriendsDataSource,
            performanceTracer = PerformanceTracer(),
            funnelTracker = mock()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Auth lifecycle ──

    @Test
    fun `LoggedOut sets error and resets quiz state`() = runTest {
        authStateFlow.value = AuthState.LoggedOut
        val vm = LearningSheetViewModel(
            buildSavedStateHandle(), authRepo, sheetsRepo, sharedHistoryDataSource,
            parseAndStoreQuiz, quizRepo, shareLearningMaterialUseCase, sharedFriendsDataSource,
            PerformanceTracer(), mock()
        )

        assertEquals("Not logged in", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.quizQuestions.isEmpty())
        assertNull(vm.uiState.value.currentAttempt)
    }

    @Test
    fun `Loading sets isLoading true`() = runTest {
        authStateFlow.value = AuthState.Loading
        val vm = LearningSheetViewModel(
            buildSavedStateHandle(), authRepo, sheetsRepo, sharedHistoryDataSource,
            parseAndStoreQuiz, quizRepo, shareLearningMaterialUseCase, sharedFriendsDataSource,
            PerformanceTracer(), mock()
        )

        assertTrue(vm.uiState.value.isLoading)
    }

    @Test
    fun `LoggedIn calls loadSheet`() = runTest {
        val doc = LearningSheetDoc(content = "Test content", historyCountAtGenerate = 10)
        sheetsRepo.stub {
            onBlocking { getSheet(uid, primary, target) } doReturn doc
        }

        val vm = buildViewModel()

        verify(sheetsRepo).getSheet(uid, primary, target)
    }

    // ── loadSheet ──

    @Test
    fun `loadSheet success sets content and historyCountAtGenerate`() = runTest {
        val doc = LearningSheetDoc(content = "Lesson content here", historyCountAtGenerate = 15)
        sheetsRepo.stub {
            onBlocking { getSheet(uid, primary, target) } doReturn doc
        }

        val vm = buildViewModel()

        assertEquals("Lesson content here", vm.uiState.value.content)
        assertEquals(15, vm.uiState.value.historyCountAtGenerate)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `loadSheet failure sets error`() = runTest {
        sheetsRepo.stub {
            onBlocking { getSheet(uid, primary, target) } doThrow RuntimeException("Network error")
        }

        val vm = buildViewModel()

        assertEquals("Network error", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `loadSheet returns early when codes are blank`() = runTest {
        val blankSavedState = SavedStateHandle(mapOf("primaryCode" to "", "targetCode" to ""))
        authStateFlow.value = AuthState.LoggedIn(testUser)
        val vm = LearningSheetViewModel(
            blankSavedState, authRepo, sheetsRepo, sharedHistoryDataSource,
            parseAndStoreQuiz, quizRepo, shareLearningMaterialUseCase, sharedFriendsDataSource,
            PerformanceTracer(), mock()
        )

        // Sheet content should be null since loadSheet skipped the fetch
        assertNull(vm.uiState.value.content)
    }

    // ── initializeQuiz ──

    @Test
    fun `initializeQuiz no doc sets quizError`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        quizRepo.stub {
            onBlocking { getGeneratedQuizDoc(uid, primary, target) } doReturn null
        }

        val vm = buildViewModel()
        vm.initializeQuiz()

        assertEquals("No quiz generated yet.", vm.uiState.value.quizError)
        assertTrue(vm.uiState.value.quizQuestions.isEmpty())
    }

    @Test
    fun `initializeQuiz empty questions sets corrupted error`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        val doc = GeneratedQuizDoc(questionsJson = "[]", historyCountAtGenerate = 5)
        quizRepo.stub {
            onBlocking { getGeneratedQuizDoc(uid, primary, target) } doReturn doc
        }

        val vm = buildViewModel()
        vm.initializeQuiz()

        assertEquals("Stored quiz is corrupted. Please regenerate.", vm.uiState.value.quizError)
        assertTrue(vm.uiState.value.isQuizOutdated)
    }

    @Test
    fun `initializeQuiz success sets questions and attempt`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }

        val questionsJson = """[{"id":"q1","question":"What?","options":["A","B","C","D"],"correctOptionIndex":0}]"""
        val doc = GeneratedQuizDoc(questionsJson = questionsJson, historyCountAtGenerate = 5)
        quizRepo.stub {
            onBlocking { getGeneratedQuizDoc(uid, primary, target) } doReturn doc
        }

        val mockAttempt = QuizAttempt(
            userId = testUserId,
            primaryLanguageCode = primaryCode,
            targetLanguageCode = targetCode
        )
        parseAndStoreQuiz.stub {
            on { createAttempt(any(), any(), any(), any(), any()) } doReturn mockAttempt
        }

        val vm = buildViewModel()
        vm.initializeQuiz()

        assertFalse(vm.uiState.value.quizLoading)
        assertNotNull(vm.uiState.value.currentAttempt)
        assertFalse(vm.uiState.value.isQuizTaken)
        assertNull(vm.uiState.value.quizError)
    }

    @Test
    fun `initializeQuiz exception sets quizError`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        quizRepo.stub {
            onBlocking { getGeneratedQuizDoc(uid, primary, target) } doThrow RuntimeException("DB fail")
        }

        val vm = buildViewModel()
        vm.initializeQuiz()

        assertEquals("DB fail", vm.uiState.value.quizError)
    }

    // ── recordQuizAnswer ──

    @Test
    fun `recordQuizAnswer with no attempt is no-op`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        val vm = buildViewModel()

        vm.recordQuizAnswer("q1", 0) // should not throw

        verifyNoInteractions(parseAndStoreQuiz)
    }

    // ── submitQuiz ──

    @Test
    fun `submitQuiz failure sets quizError`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }

        val questionsJson = """[{"id":"q1","question":"What?","options":["A","B"],"correctOptionIndex":0}]"""
        val doc = GeneratedQuizDoc(questionsJson = questionsJson, historyCountAtGenerate = 5)
        quizRepo.stub {
            onBlocking { getGeneratedQuizDoc(uid, primary, target) } doReturn doc
        }

        val mockAttempt = QuizAttempt(userId = testUserId, primaryLanguageCode = primaryCode, targetLanguageCode = targetCode)
        parseAndStoreQuiz.stub {
            on { createAttempt(any(), any(), any(), any(), any()) } doReturn mockAttempt
            on { completeAttempt(any()) } doThrow RuntimeException("Submit failed")
        }

        val vm = buildViewModel()
        vm.initializeQuiz()
        vm.submitQuiz()

        assertEquals("Submit failed", vm.uiState.value.quizError)
    }

    // ── resetQuiz ──

    @Test
    fun `resetQuiz clears quiz state`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        val vm = buildViewModel()

        vm.resetQuiz()

        assertTrue(vm.uiState.value.quizQuestions.isEmpty())
        assertNull(vm.uiState.value.currentAttempt)
        assertFalse(vm.uiState.value.isQuizTaken)
        assertNull(vm.uiState.value.quizError)
    }

    // ── Share feature ──

    @Test
    fun `shareSheet with blank content sets error`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }

        val vm = buildViewModel()
        vm.shareSheet(UserId("friend1"))

        assertEquals("Sheet content is not ready. Please wait for it to load.", vm.uiState.value.shareError)
    }

    @Test
    fun `shareSheet success sets shareSuccessKey`() = runTest {
        val doc = LearningSheetDoc(content = "Full content", historyCountAtGenerate = 5)
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn doc }
        val sharedItem = SharedItem(itemId = "shared1", fromUserId = testUserId)

        // Stub with all concrete values - no matchers to avoid mixing issues with value classes
        shareLearningMaterialUseCase.stub {
            onBlocking {
                invoke(
                    fromUserId = uid,
                    fromUsername = "",
                    toUserId = UserId("friend1"),
                    type = SharedItemType.LEARNING_SHEET,
                    materialId = "${primaryCode}_${targetCode}",
                    title = "Learning Sheet: $primaryCode \u2192 $targetCode",
                    description = "Full content",
                    fullContent = "Full content"
                )
            } doReturn Result.success(sharedItem)
        }

        val vm = buildViewModel()
        vm.shareSheet(UserId("friend1"))

        assertEquals(UiTextKey.ShareSuccess, vm.uiState.value.shareSuccessKey)
        assertFalse(vm.uiState.value.isSharing)
    }

    @Test
    fun `shareSheet failure sets shareError`() = runTest {
        val doc = LearningSheetDoc(content = "Content", historyCountAtGenerate = 5)
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn doc }

        shareLearningMaterialUseCase.stub {
            onBlocking {
                invoke(
                    fromUserId = uid,
                    fromUsername = "",
                    toUserId = UserId("friend1"),
                    type = SharedItemType.LEARNING_SHEET,
                    materialId = "${primaryCode}_${targetCode}",
                    title = "Learning Sheet: $primaryCode \u2192 $targetCode",
                    description = "Content",
                    fullContent = "Content"
                )
            } doReturn Result.failure(RuntimeException("Share failed"))
        }

        val vm = buildViewModel()
        vm.shareSheet(UserId("friend1"))

        assertEquals("Share failed", vm.uiState.value.shareError)
    }

    @Test
    fun `showShareDialog and dismissShareDialog toggle state`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        val vm = buildViewModel()

        vm.showShareDialog()
        assertTrue(vm.uiState.value.showShareDialog)

        vm.dismissShareDialog()
        assertFalse(vm.uiState.value.showShareDialog)
    }

    @Test
    fun `clearShareMessages clears both success and error`() = runTest {
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn null }
        val vm = buildViewModel()

        vm.clearShareMessages()

        assertNull(vm.uiState.value.shareSuccessKey)
        assertNull(vm.uiState.value.shareError)
    }

    @Test
    fun `saveSheetToSelf shares to own uid`() = runTest {
        val doc = LearningSheetDoc(content = "My content", historyCountAtGenerate = 3)
        sheetsRepo.stub { onBlocking { getSheet(uid, primary, target) } doReturn doc }
        val sharedItem = SharedItem(itemId = "shared2", fromUserId = testUserId)

        shareLearningMaterialUseCase.stub {
            onBlocking {
                invoke(
                    fromUserId = uid,
                    fromUsername = "",
                    toUserId = uid,
                    type = SharedItemType.LEARNING_SHEET,
                    materialId = "${primaryCode}_${targetCode}",
                    title = "Learning Sheet: $primaryCode \u2192 $targetCode",
                    description = "My content",
                    fullContent = "My content"
                )
            } doReturn Result.success(sharedItem)
        }

        val vm = buildViewModel()
        vm.saveSheetToSelf()

        assertEquals(UiTextKey.ShareSavedToSelf, vm.uiState.value.shareSuccessKey)
    }
}
