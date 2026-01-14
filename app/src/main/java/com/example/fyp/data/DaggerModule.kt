package com.example.fyp.data

import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.domain.speech.RecognizeFromMicUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.StartContinuousConversationUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // NEW: Firebase Functions instance
    @Provides @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    // NEW: Token client (uses functions)
    @Provides @Singleton
    fun provideCloudSpeechTokenClient(functions: FirebaseFunctions): CloudSpeechTokenClient =
        CloudSpeechTokenClient(functions)

    // UPDATED: SpeechRepository now needs token client
    @Provides @Singleton
    fun provideSpeechRepository(tokenClient: CloudSpeechTokenClient): SpeechRepository =
        AzureSpeechRepository(tokenClient)

    @Provides
    fun provideRecognizeUseCase(repo: SpeechRepository) = RecognizeFromMicUseCase(repo)

    @Provides
    fun provideSpeakUseCase(repo: SpeechRepository) = SpeakTextUseCase(repo)

    @Provides
    fun provideContinuousUseCase(repo: SpeechRepository) = StartContinuousConversationUseCase(repo)

    @Provides
    fun provideTranslateUseCase(repo: TranslationRepository) = TranslateTextUseCase(repo)

    @Provides @Singleton
    fun provideCloudTranslatorClient(functions: FirebaseFunctions): CloudTranslatorClient =
        CloudTranslatorClient(functions)

    @Provides @Singleton
    fun provideTranslationRepository(client: CloudTranslatorClient): TranslationRepository =
        FirebaseTranslationRepository(client)
}