package com.example.fyp.data.settings

import com.example.fyp.model.LanguageCode
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreUserSettingsRepositoryVoiceFieldPathTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var usersCollection: CollectionReference
    private lateinit var userDocument: DocumentReference
    private lateinit var profileCollection: CollectionReference
    private lateinit var settingsDocument: DocumentReference
    private lateinit var repository: FirestoreUserSettingsRepository

    @Before
    fun setup() {
        db = mock()
        usersCollection = mock()
        userDocument = mock()
        profileCollection = mock()
        settingsDocument = mock()

        whenever(db.collection("users")).thenReturn(usersCollection)
        whenever(usersCollection.document("u1")).thenReturn(userDocument)
        whenever(userDocument.collection("profile")).thenReturn(profileCollection)
        whenever(profileCollection.document("settings")).thenReturn(settingsDocument)

        repository = FirestoreUserSettingsRepository(db)
    }

    @Test
    fun `setVoiceForLanguage updates only target nested language field`() = runTest {
        whenever(settingsDocument.update("voiceSettings.en-US", "en-US-JennyNeural"))
            .thenReturn(Tasks.forResult(null))

        repository.setVoiceForLanguage(
            userId = UserId("u1"),
            languageCode = LanguageCode("en-US"),
            voiceName = VoiceName("en-US-JennyNeural")
        )

        verify(settingsDocument).update("voiceSettings.en-US", "en-US-JennyNeural")
        verify(settingsDocument, never()).set(
            mapOf("voiceSettings" to mapOf("en-US" to "en-US-JennyNeural")),
            SetOptions.merge()
        )
    }

    @Test
    fun `setVoiceForLanguage falls back to set-merge when settings doc missing`() = runTest {
        whenever(settingsDocument.update("voiceSettings.zh-TW", "zh-TW-HsiaoChenNeural"))
            .thenReturn(
                Tasks.forException(
                    FirebaseFirestoreException(
                        "missing",
                        FirebaseFirestoreException.Code.NOT_FOUND
                    )
                )
            )
        whenever(
            settingsDocument.set(
                mapOf("voiceSettings" to mapOf("zh-TW" to "zh-TW-HsiaoChenNeural")),
                SetOptions.merge()
            )
        ).thenReturn(Tasks.forResult(null))

        repository.setVoiceForLanguage(
            userId = UserId("u1"),
            languageCode = LanguageCode("zh-TW"),
            voiceName = VoiceName("zh-TW-HsiaoChenNeural")
        )

        verify(settingsDocument).update("voiceSettings.zh-TW", "zh-TW-HsiaoChenNeural")
        verify(settingsDocument).set(
            mapOf("voiceSettings" to mapOf("zh-TW" to "zh-TW-HsiaoChenNeural")),
            SetOptions.merge()
        )
    }

    @Test(expected = FirebaseFirestoreException::class)
    fun `setVoiceForLanguage rethrows non-NOT_FOUND firestore errors`() = runTest {
        whenever(settingsDocument.update("voiceSettings.ja-JP", "ja-JP-NanamiNeural"))
            .thenReturn(
                Tasks.forException(
                    FirebaseFirestoreException(
                        "permission denied",
                        FirebaseFirestoreException.Code.PERMISSION_DENIED
                    )
                )
            )

        repository.setVoiceForLanguage(
            userId = UserId("u1"),
            languageCode = LanguageCode("ja-JP"),
            voiceName = VoiceName("ja-JP-NanamiNeural")
        )
    }
}
