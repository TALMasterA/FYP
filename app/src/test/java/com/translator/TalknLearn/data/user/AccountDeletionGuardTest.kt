package com.translator.TalknLearn.data.user

import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Guards the account deletion cleanup path count.
 *
 * When a new Firestore subcollection is added for a user, you MUST also add
 * its cleanup call to the server-side deleteAccountAndData Cloud Function.
 * This test ensures the deletion code references all expected collections.
 *
 * Current expected subcollections (18):
 *   history, word_banks, learning_sheets, quiz_attempts, quiz_stats,
 *   generated_quizzes, quiz_versions, favorites, custom_words, sessions,
 *   coin_awards, last_awarded_quiz, user_stats, friends, shared_inbox,
 *   favorite_sessions, blocked_users, fcm_tokens
 *
 * Current expected profile docs (3):
 *   profile/settings, profile/info, profile/public
 *
 * Current expected top-level docs (2):
 *   usernames/{username}, user_search/{uid}
 */
class AccountDeletionGuardTest {

    companion object {
        /** All user subcollections that must be deleted on account removal. */
        val EXPECTED_SUBCOLLECTIONS = setOf(
            "history",
            "word_banks",
            "learning_sheets",
            "quiz_attempts",
            "quiz_stats",
            "generated_quizzes",
            "quiz_versions",
            "favorites",
            "custom_words",
            "sessions",
            "coin_awards",
            "last_awarded_quiz",
            "user_stats",
            "friends",
            "shared_inbox",
            "favorite_sessions",
            "blocked_users",
            "fcm_tokens",
        )
    }

    @Test
    fun `expected subcollection count is 18`() {
        assertEquals(
            "Update this test if you add a new user subcollection — and also update deleteAccount()!",
            18,
            EXPECTED_SUBCOLLECTIONS.size
        )
    }

    @Test
    fun `all expected subcollections are unique`() {
        // Guard that we don't accidentally have duplicates in the set
        val list = EXPECTED_SUBCOLLECTIONS.toList()
        assertEquals(list.size, list.distinct().size)
    }

    @Test
    fun `server-side deleteAccountAndData references every expected subcollection`() {
        val source = File("../fyp-backend/functions/src/accountDeletion.ts").readText()

        EXPECTED_SUBCOLLECTIONS.forEach { collectionName ->
            assertTrue(
                "deleteAccountAndData must delete users/{uid}/$collectionName",
                source.contains("\"$collectionName\"")
            )
        }
    }
}
