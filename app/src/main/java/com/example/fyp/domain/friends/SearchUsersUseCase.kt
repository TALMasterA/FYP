package com.example.fyp.domain.friends

import com.example.fyp.data.friends.FriendsRepository
import com.example.fyp.model.friends.PublicUserProfile
import javax.inject.Inject

/**
 * Use case for searching users by username.
 */
class SearchUsersUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(query: String, limit: Long = 20): List<PublicUserProfile> {
        if (query.length < 2) {
            return emptyList()
        }
        return friendsRepository.searchByUsername(query, limit)
    }
}
