package com.translator.TalknLearn.domain.friends

import com.translator.TalknLearn.data.friends.SharingRepository
import com.translator.TalknLearn.model.GeneratedLearningContent
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.friends.SharedItem
import com.translator.TalknLearn.model.friends.SharedItemType
import javax.inject.Inject

data class LearningMaterialToShare(
    val materialId: String,
    val title: String,
    val description: String = "",
    val fullContent: GeneratedLearningContent = GeneratedLearningContent("", null)
)

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
     * @param material The material's identity, preview, and full content
     */
    suspend operator fun invoke(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        type: SharedItemType,
        material: LearningMaterialToShare
    ): Result<SharedItem> {
        val materialData = buildMap {
            put("materialId", material.materialId)
            put("title", material.title)
            put("description", material.description)
            put("fullContent", material.fullContent.content)
            material.fullContent.model?.let { put("generatedModel", it) }
        }

        return sharingRepository.shareLearningMaterial(
            fromUserId,
            fromUsername,
            toUserId,
            type,
            materialData
        )
    }
}
