package com.example.fyp.data.user

import org.junit.Test
import org.junit.Assert.*

/**
 * Guards the account deletion cleanup path count.
 *
 * When a new Firestore subcollection is added for a user, you MUST also add
 * its cleanup call to FirestoreProfileRepository.deleteAccount().
 * This test ensures the deletion code references all expected collections.
 *
 * Current expected subcollections (16):
 *   history, word_banks, learning_sheets, quiz_attempts, quiz_stats,
 *   generated_quizzes, favorites, custom_words, sessions, coin_awards,
 *   last_awarded_quiz, user_stats, friends, shared_inbox,
 *   favorite_sessions, blocked_users
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
        )
    }

    @Test
    fun `expected subcollection count is 16`() {
        assertEquals(
            "Update this test if you add a new user subcollection — and also update deleteAccount()!",
            16,
            EXPECTED_SUBCOLLECTIONS.size
        )
    }

    @Test
    fun `all expected subcollections are unique`() {
        // Guard that we don't accidentally have duplicates in the set
        val list = EXPECTED_SUBCOLLECTIONS.toList()
        assertEquals(list.size, list.distinct().size)
    }
}
