package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Fake SharingRepository that records calls and returns configured results.
 * Avoids Mockito which cannot handle @JvmInline value class parameters.
 */
private class RecordingSharingRepository : SharingRepository {
    var lastFromUserId: UserId? = null
    var lastFromUsername: String? = null
    var lastToUserId: UserId? = null
    var lastType: SharedItemType? = null
    var lastMaterialData: Map<String, Any>? = null
    var resultToReturn: Result<SharedItem> = Result.success(SharedItem())

    override suspend fun shareLearningMaterial(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        type: SharedItemType,
        materialData: Map<String, Any>
    ): Result<SharedItem> {
        lastFromUserId = fromUserId
        lastFromUsername = fromUsername
        lastToUserId = toUserId
        lastType = type
        lastMaterialData = materialData
        return resultToReturn
    }

    // Unused methods - required by interface
    override suspend fun canShareToUser(fromUserId: UserId, toUserId: UserId) = true
    override suspend fun shareWord(fromUserId: UserId, fromUsername: String, toUserId: UserId, wordData: Map<String, Any>) = Result.success(SharedItem())
    override suspend fun acceptSharedItem(itemId: String, userId: UserId) = Result.success(Unit)
    override suspend fun dismissSharedItem(itemId: String, userId: UserId) = Result.success(Unit)
    override fun observeSharedInbox(userId: UserId): Flow<List<SharedItem>> = emptyFlow()
    override suspend fun getPendingItemsCount(userId: UserId) = 0
    override suspend fun fetchSharedItemFullContent(userId: UserId, itemId: String): String? = null
    override suspend fun fetchSharedItemById(userId: UserId, itemId: String): SharedItem? = null
}

/**
 * Unit tests for ShareLearningMaterialUseCase.
 *
 * Tests:
 * 1. Share learning sheet succeeds with valid parameters
 * 2. Share quiz succeeds
 * 3. Handles repository failure
 * 4. Constructs correct material data map with fullContent
 * 5. Handles empty description and fullContent defaults
 * 6. Full content over 200 chars is NOT truncated (unlike description)
 * 7. Full content key is always included in materialData even when blank
 * 8. Share to same user (self) succeeds — toUserId == fromUserId
 */
class ShareLearningMaterialUseCaseTest {

    private lateinit var repo: RecordingSharingRepository
    private lateinit var useCase: ShareLearningMaterialUseCase

    @Before
    fun setup() {
        repo = RecordingSharingRepository()
        useCase = ShareLearningMaterialUseCase(repo)
    }

    @Test
    fun `share learning sheet succeeds with valid parameters`() = runTest {
        val fromUserId = UserId("user1")
        val toUserId = UserId("user2")
        repo.resultToReturn = Result.success(SharedItem(itemId = "item1", fromUserId = "user1", toUserId = "user2"))

        val result = useCase(
            fromUserId = fromUserId,
            fromUsername = "sender",
            toUserId = toUserId,
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet_en-US_es-ES",
            title = "English to Spanish",
            description = "A learning sheet...",
            fullContent = "Full content here"
        )

        assertTrue(result.isSuccess)
        assertEquals("item1", result.getOrNull()?.itemId)
        assertEquals(fromUserId, repo.lastFromUserId)
        assertEquals("sender", repo.lastFromUsername)
        assertEquals(toUserId, repo.lastToUserId)
        assertEquals(SharedItemType.LEARNING_SHEET, repo.lastType)
    }

    @Test
    fun `handles repository failure`() = runTest {
        repo.resultToReturn = Result.failure(RuntimeException("Permission denied"))

        val result = useCase(
            fromUserId = UserId("user1"),
            fromUsername = "sender",
            toUserId = UserId("user2"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet1",
            title = "Sheet"
        )

        assertTrue(result.isFailure)
        assertEquals("Permission denied", result.exceptionOrNull()?.message)
    }

    @Test
    fun `constructs material data map with all fields`() = runTest {
        repo.resultToReturn = Result.success(SharedItem(itemId = "item1"))

        useCase(
            fromUserId = UserId("user1"),
            fromUsername = "sender",
            toUserId = UserId("user2"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet1",
            title = "Test Title",
            description = "Test Desc",
            fullContent = "Full content"
        )

        val data = repo.lastMaterialData!!
        assertEquals("sheet1", data["materialId"])
        assertEquals("Test Title", data["title"])
        assertEquals("Test Desc", data["description"])
        assertEquals("Full content", data["fullContent"])
    }

    @Test
    fun `default description and fullContent are empty strings`() = runTest {
        repo.resultToReturn = Result.success(SharedItem(itemId = "item1"))

        useCase(
            fromUserId = UserId("user1"),
            fromUsername = "sender",
            toUserId = UserId("user2"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet1",
            title = "Title"
        )

        val data = repo.lastMaterialData!!
        assertEquals("", data["description"])
        assertEquals("", data["fullContent"])
    }

    /**
     * Guards against accidentally truncating fullContent in the use case.
     * The ViewModel passes description = content.take(200) (truncated preview)
     * but fullContent must always be the COMPLETE text for the sub-document.
     */
    @Test
    fun `full content over 200 chars is passed to repository untruncated`() = runTest {
        repo.resultToReturn = Result.success(SharedItem(itemId = "item1"))
        val longContent = "A".repeat(5000)

        useCase(
            fromUserId = UserId("user1"),
            fromUsername = "sender",
            toUserId = UserId("user2"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet1",
            title = "Title",
            description = longContent.take(200),
            fullContent = longContent
        )

        val data = repo.lastMaterialData!!
        assertEquals(5000, (data["fullContent"] as String).length)
        assertEquals(longContent, data["fullContent"])
        // description IS truncated by caller, fullContent is NOT
        assertEquals(200, (data["description"] as String).length)
    }

    /**
     * Ensures fullContent key is always present in materialData map,
     * even when blank. The repository uses this key to write the sub-document;
     * omitting it would silently skip the sub-doc write (the original bug).
     */
    @Test
    fun `fullContent key is always present in materialData even when blank`() = runTest {
        repo.resultToReturn = Result.success(SharedItem(itemId = "item1"))

        useCase(
            fromUserId = UserId("user1"),
            fromUsername = "sender",
            toUserId = UserId("user2"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet1",
            title = "Title",
            fullContent = ""
        )

        val data = repo.lastMaterialData!!
        assertTrue("fullContent key must exist in materialData", data.containsKey("fullContent"))
        assertEquals("", data["fullContent"])
    }

    /**
     * Guards self-sharing: toUserId == fromUserId. This is the "Save to Self"
     * feature that lets users archive a sheet to their own inbox.
     */
    @Test
    fun `share to same user (self) succeeds`() = runTest {
        repo.resultToReturn = Result.success(SharedItem(itemId = "self1", fromUserId = "user1", toUserId = "user1"))

        val result = useCase(
            fromUserId = UserId("user1"),
            fromUsername = "myself",
            toUserId = UserId("user1"),
            type = SharedItemType.LEARNING_SHEET,
            materialId = "sheet1",
            title = "My Sheet",
            fullContent = "Full content for self"
        )

        assertTrue(result.isSuccess)
        assertEquals(UserId("user1"), repo.lastFromUserId)
        assertEquals(UserId("user1"), repo.lastToUserId)
        assertEquals("Full content for self", repo.lastMaterialData!!["fullContent"])
    }
}
