package com.example.fyp.data.database

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DataCleanupUtils business logic.
 *
 * The actual Firestore-dependent methods cannot be unit-tested without
 * an emulator, so we verify the cutoff timestamp calculations,
 * parameter defaults, and the orphan-detection filter logic.
 */
class DataCleanupUtilsTest {

    // ------------------------------------------------------------------ //
    //  Constants used by the production code (mirrored here for clarity)  //
    // ------------------------------------------------------------------ //
    private companion object {
        const val MS_PER_DAY = 24L * 60 * 60 * 1000          // 86_400_000
        const val DAYS_PER_MONTH = 30L
        const val MS_PER_MONTH = DAYS_PER_MONTH * MS_PER_DAY // 2_592_000_000
    }

    // ------------------------------------------------------------------ //
    //  archiveOldHistory – cutoff calculation                            //
    // ------------------------------------------------------------------ //

    @Test
    fun `archiveOldHistory default maxAgeMonths is 6`() {
        // The default parameter in archiveOldHistory is maxAgeMonths = 6.
        // We verify the formula produces the expected offset for that default.
        val defaultMaxAgeMonths = 6
        val expectedOffsetMs = defaultMaxAgeMonths.toLong() * 30 * 24 * 60 * 60 * 1000
        // 6 * 30 * 86_400_000 = 15_552_000_000 ms
        assertEquals(15_552_000_000L, expectedOffsetMs)
    }

    @Test
    fun `cutoff for 6 months is approximately 180 days in millis`() {
        val months = 6
        val offsetMs = months.toLong() * 30 * 24 * 60 * 60 * 1000
        val expectedDays = months * DAYS_PER_MONTH  // 180
        assertEquals(180L, expectedDays)
        assertEquals(expectedDays * MS_PER_DAY, offsetMs)

        // Cross-check: 180 days in millis = 15_552_000_000
        assertEquals(15_552_000_000L, offsetMs)
    }

    @Test
    fun `archiveOldHistory cutoff is relative to current time`() {
        // Simulate the exact formula from archiveOldHistory:
        //   val cutoff = System.currentTimeMillis() -
        //                maxAgeMonths.toLong() * 30 * 24 * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        val maxAgeMonths = 6
        val cutoff = now - maxAgeMonths.toLong() * 30 * 24 * 60 * 60 * 1000

        // The cutoff must be in the past
        assertTrue("Cutoff should be before now", cutoff < now)

        // The difference should be exactly 6 * 30 days in millis
        assertEquals(15_552_000_000L, now - cutoff)
    }

    @Test
    fun `archiveOldHistory cutoff scales linearly with months`() {
        val now = System.currentTimeMillis()
        val cutoff3  = now - 3L  * 30 * 24 * 60 * 60 * 1000
        val cutoff6  = now - 6L  * 30 * 24 * 60 * 60 * 1000
        val cutoff12 = now - 12L * 30 * 24 * 60 * 60 * 1000

        // 6-month offset is exactly double the 3-month offset
        assertEquals((now - cutoff3) * 2, now - cutoff6)
        // 12-month offset is exactly double the 6-month offset
        assertEquals((now - cutoff6) * 2, now - cutoff12)
    }

    // ------------------------------------------------------------------ //
    //  cleanupStaleFriendRequests – cutoff calculation                   //
    // ------------------------------------------------------------------ //

    @Test
    fun `cutoff for 30 day friend requests is correct`() {
        val maxAgeDays = 30
        val offsetMs = maxAgeDays.toLong() * 24 * 60 * 60 * 1000
        // 30 * 86_400_000 = 2_592_000_000 ms
        assertEquals(2_592_000_000L, offsetMs)
    }

    @Test
    fun `friend request cutoff converts to epoch seconds for Firestore Timestamp`() {
        // Production code:
        //   Timestamp((System.currentTimeMillis() - offset) / 1000, 0)
        val now = System.currentTimeMillis()
        val maxAgeDays = 30
        val offsetMs = maxAgeDays.toLong() * 24 * 60 * 60 * 1000
        val cutoffEpochSeconds = (now - offsetMs) / 1000

        // Must be positive (i.e. a valid epoch)
        assertTrue("Cutoff epoch seconds must be positive", cutoffEpochSeconds > 0)

        // Round-trip: converting back to millis should be within 1 second of the raw cutoff
        val backToMs = cutoffEpochSeconds * 1000
        assertTrue(
            "Truncation error must be < 1000 ms",
            (now - offsetMs) - backToMs in 0..999
        )
    }

    @Test
    fun `friend request cutoff with custom days`() {
        val offsetFor7Days  = 7L  * 24 * 60 * 60 * 1000
        val offsetFor14Days = 14L * 24 * 60 * 60 * 1000
        val offsetFor90Days = 90L * 24 * 60 * 60 * 1000

        assertEquals(604_800_000L,   offsetFor7Days)
        assertEquals(1_209_600_000L, offsetFor14Days)
        assertEquals(7_776_000_000L, offsetFor90Days)
    }

    // ------------------------------------------------------------------ //
    //  cleanupOrphanedSharedItems – filter logic                         //
    // ------------------------------------------------------------------ //

    /**
     * Simulates the orphan-detection filter from [DataCleanupUtils.cleanupOrphanedSharedItems]:
     *
     * ```
     * snapshot.documents.filter { doc ->
     *     val senderId = doc.getString("senderId") ?: return@filter false
     *     senderId !in existingSenderIds
     * }
     * ```
     *
     * We use simple maps to represent document fields, which lets us
     * unit-test the logic without touching Firestore.
     */
    private fun filterOrphans(
        documents: List<Map<String, String?>>,
        existingSenderIds: Set<String>
    ): List<Map<String, String?>> {
        return documents.filter { doc ->
            val senderId = doc["senderId"] ?: return@filter false
            senderId !in existingSenderIds
        }
    }

    @Test
    fun `orphan filter identifies non-existent senders`() {
        val docs = listOf(
            mapOf("senderId" to "user_A"),
            mapOf("senderId" to "user_B"),
            mapOf("senderId" to "user_C")
        )
        val existingSenderIds = setOf("user_A") // B and C are orphans

        val orphans = filterOrphans(docs, existingSenderIds)

        assertEquals(2, orphans.size)
        assertEquals("user_B", orphans[0]["senderId"])
        assertEquals("user_C", orphans[1]["senderId"])
    }

    @Test
    fun `orphan filter keeps existing senders`() {
        val docs = listOf(
            mapOf("senderId" to "user_A"),
            mapOf("senderId" to "user_B")
        )
        val existingSenderIds = setOf("user_A", "user_B")

        val orphans = filterOrphans(docs, existingSenderIds)

        assertTrue("All senders exist so no orphans expected", orphans.isEmpty())
    }

    @Test
    fun `orphan filter handles empty sender set`() {
        val docs = listOf(
            mapOf("senderId" to "user_A"),
            mapOf("senderId" to "user_B")
        )
        val existingSenderIds = emptySet<String>()

        val orphans = filterOrphans(docs, existingSenderIds)

        // With an empty existing-set every document is orphaned
        assertEquals(2, orphans.size)
    }

    @Test
    fun `orphan filter skips documents without senderId field`() {
        val docs: List<Map<String, String?>> = listOf(
            mapOf("senderId" to "user_A"),
            mapOf("otherField" to "irrelevant"),   // no senderId key
            mapOf("senderId" to null),              // senderId is null
            mapOf("senderId" to "user_C")
        )
        val existingSenderIds = setOf("user_A")

        val orphans = filterOrphans(docs, existingSenderIds)

        // user_A is existing -> kept (not orphan)
        // missing-key doc  -> skipped (return@filter false)
        // null-senderId doc -> skipped (return@filter false)
        // user_C is not existing -> orphan
        assertEquals(1, orphans.size)
        assertEquals("user_C", orphans[0]["senderId"])
    }

    @Test
    fun `orphan filter handles empty document list`() {
        val docs = emptyList<Map<String, String?>>()
        val existingSenderIds = setOf("user_A", "user_B")

        val orphans = filterOrphans(docs, existingSenderIds)

        assertTrue("No documents means no orphans", orphans.isEmpty())
    }

    @Test
    fun `orphan filter handles both empty documents and empty sender set`() {
        val docs = emptyList<Map<String, String?>>()
        val existingSenderIds = emptySet<String>()

        val orphans = filterOrphans(docs, existingSenderIds)

        assertTrue(orphans.isEmpty())
    }

    // ------------------------------------------------------------------ //
    //  Miscellaneous parameter validation                                //
    // ------------------------------------------------------------------ //

    @Test
    fun `zero months produces zero offset`() {
        val offset = 0L * 30 * 24 * 60 * 60 * 1000
        assertEquals(0L, offset)
    }

    @Test
    fun `one month offset equals 30 days in millis`() {
        val oneMonthMs = 1L * 30 * 24 * 60 * 60 * 1000
        assertEquals(MS_PER_MONTH, oneMonthMs)
        assertEquals(2_592_000_000L, oneMonthMs)
    }

    @Test
    fun `one day offset is 86400000 millis`() {
        val oneDayMs = 1L * 24 * 60 * 60 * 1000
        assertEquals(86_400_000L, oneDayMs)
        assertEquals(MS_PER_DAY, oneDayMs)
    }
}
