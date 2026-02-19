package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.PublicUserProfile
import javax.inject.Inject

/**
 * Use case for searching users by username or userId.
 * - If query looks like a userId (long string), search by userId
 * - Otherwise, search by username prefix
 */
class SearchUsersUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(query: String, limit: Long = 20): List<PublicUserProfile> {
        if (query.length < 2) {
            return emptyList()
        }

        // Check if query looks like a userId (28+ characters, alphanumeric)
        // Firebase UIDs are 28 characters long
        val results = mutableListOf<PublicUserProfile>()

        if (query.length >= 10 && query.all { it.isLetterOrDigit() }) {
            // Try to find by exact userId
            val userById = friendsRepository.findByUserId(UserId(query))
            if (userById != null) {
                results.add(userById)
            }
        }

        // Also search by username (unless we found exact userId match)
        if (results.isEmpty()) {
            results.addAll(friendsRepository.searchByUsername(query, limit))
        }

        return results
    }
}
