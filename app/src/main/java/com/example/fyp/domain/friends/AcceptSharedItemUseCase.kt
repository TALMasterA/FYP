package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

/**
 * Use case for accepting a shared item and adding it to user's collection.
 */
class AcceptSharedItemUseCase @Inject constructor(
    private val sharingRepository: SharingRepository
) {
    /**
     * Accept a shared item.
     * For words: adds to word bank
     * For materials: adds reference to user's learning materials
     * 
     * @param itemId ID of the shared item
     * @param userId Current user ID
     */
    suspend operator fun invoke(
        itemId: String,
        userId: UserId
    ): Result<Unit> {
        return sharingRepository.acceptSharedItem(itemId, userId)
    }
}
