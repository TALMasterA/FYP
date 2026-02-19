package com.example.fyp.domain.friends

import com.example.fyp.data.friends.SharingRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.friends.SharedItem
import javax.inject.Inject

/**
 * Use case for sharing a word bank word with a friend.
 */
class ShareWordUseCase @Inject constructor(
    private val sharingRepository: SharingRepository
) {
    /**
     * Share a word with a friend.
     * 
     * @param fromUserId Current user sharing the word
     * @param fromUsername Caller's own username (in-memory — avoids a Firestore profile read)
     * @param toUserId Friend receiving the word
     * @param sourceText The word in source language
     * @param targetText The word in target language
     * @param sourceLang Source language code
     * @param targetLang Target language code
     * @param notes Optional notes about the word
     */
    suspend operator fun invoke(
        fromUserId: UserId,
        fromUsername: String,
        toUserId: UserId,
        sourceText: String,
        targetText: String,
        sourceLang: String,
        targetLang: String,
        notes: String = ""
    ): Result<SharedItem> {
        val wordData = mapOf(
            "sourceText" to sourceText,
            "targetText" to targetText,
            "sourceLang" to sourceLang,
            "targetLang" to targetLang,
            "notes" to notes
        )
        
        return sharingRepository.shareWord(fromUserId, fromUsername, toUserId, wordData)
    }
}
