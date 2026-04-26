package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.SharingRepository
import com.translator.TalknLearn.model.UserId
import javax.inject.Inject

/**
 * Use case for dismissing a shared item without adding it to collection.
 */
class DismissSharedItemUseCase @Inject constructor(
    private val sharingRepository: SharingRepository
) {
    /**
     * Dismiss a shared item (mark as dismissed, don't add to collection).
     * 
     * @param itemId ID of the shared item
     * @param userId Current user ID
     */
    suspend operator fun invoke(
        itemId: String,
        userId: UserId
    ): Result<Unit> {
        return sharingRepository.dismissSharedItem(itemId, userId)
    }
}
