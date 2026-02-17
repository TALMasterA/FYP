package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the shared inbox (items shared with the user).
 */
class ObserveSharedInboxUseCase @Inject constructor(
    private val sharingRepository: SharingRepository
) {
    /**
     * Observe real-time updates to the user's shared inbox.
     * Returns Flow of pending shared items.
     * 
     * @param userId Current user ID
     * @return Flow of list of SharedItems
     */
    operator fun invoke(userId: UserId): Flow<List<SharedItem>> {
        return sharingRepository.observeSharedInbox(userId)
    }
}
