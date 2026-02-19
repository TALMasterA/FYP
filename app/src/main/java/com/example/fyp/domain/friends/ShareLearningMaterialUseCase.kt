package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import com.example.fyp.model.friends.SharedItemType
import javax.inject.Inject

/**
 * Use case for sharing a learning material (sheet or quiz) with a friend.
 */
class ShareLearningMaterialUseCase @Inject constructor(
    private val sharingRepository: SharingRepository
) {
    /**
     * Share a learning sheet or quiz with a friend.
     *
     * @param fromUserId Current user sharing the material
     * @param fromUsername Caller's own username (in-memory — avoids a Firestore profile read)
     * @param toUserId Friend receiving the material
     * @param type Type of material (LEARNING_SHEET or QUIZ)
     * @param materialId ID of the material
     * @param title Title of the material
     * @param description Optional description
     */
    suspend operator fun invoke(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        type: SharedItemType,
        materialId: String,
        title: String,
        description: String = ""
    ): Result<SharedItem> {
        val materialData = mapOf(
            "materialId" to materialId,
            "title" to title,
            "description" to description
        )

        return sharingRepository.shareLearningMaterial(
            fromUserId,
            fromUsername,
            toUserId,
            type,
            materialData
        )
    }
}
