package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.FriendsRepository
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.PublicUserProfile
import javax.inject.Inject

/**
 * Use case for searching users by username.
 * Returns Result.failure if query is too short (< 2 chars).
 */
class SearchUsersUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(
        query: String,
        limit: Long = 20,
        callerUserId: UserId? = null
    ): Result<List<PublicUserProfile>> {
        if (query.length < 3) {
            return Result.failure(IllegalArgumentException("Search query must be at least 3 characters"))
        }
        return friendsRepository.searchUsersByUsername(query, limit, callerUserId)
    }
}
