package com.example.fyp.data.di

import com.example.fyp.data.clients.CloudSpeechTokenClient
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.repositories.AzureSpeechRepository
import com.example.fyp.data.repositories.FirebaseTranslationRepository
import com.example.fyp.data.repositories.SpeechRepository
import com.example.fyp.data.repositories.TranslationRepository
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
import com.example.fyp.data.genai.CloudGenAiClient
import com.example.fyp.data.learning.LearningContentRepositoryImpl
import com.example.fyp.domain.learning.GenerateLearningMaterialsUseCase
import com.example.fyp.domain.learning.GenerateQuizUseCase
import com.example.fyp.domain.learning.LearningContentRepository
import com.example.fyp.domain.learning.QuizGenerationRepository
import com.example.fyp.data.learning.QuizGenerationRepositoryImpl
import com.example.fyp.data.wordbank.WordBankGenerationRepository
import com.example.fyp.data.wordbank.FirestoreWordBankRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Provides @Singleton
    fun provideCloudSpeechTokenClient(functions: FirebaseFunctions): CloudSpeechTokenClient =
        CloudSpeechTokenClient(functions)

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

    @Provides
    @Singleton
    fun provideCloudGenAiClient(functions: FirebaseFunctions): CloudGenAiClient =
        CloudGenAiClient(functions)

    @Provides
    @Singleton
    fun provideLearningContentRepository(genAi: CloudGenAiClient): LearningContentRepository =
        LearningContentRepositoryImpl(genAi)

    @Provides
    @Singleton
    fun provideQuizGenerationRepository(genAi: CloudGenAiClient): QuizGenerationRepository =
        QuizGenerationRepositoryImpl(genAi)

    @Provides
    fun provideGenerateLearningMaterialsUseCase(repo: LearningContentRepository) =
        GenerateLearningMaterialsUseCase(repo)

    @Provides
    fun provideGenerateQuizUseCase(repo: QuizGenerationRepository) =
        GenerateQuizUseCase(repo)

    @Provides
    @Singleton
    fun provideWordBankGenerationRepository(genAi: CloudGenAiClient): WordBankGenerationRepository =
        WordBankGenerationRepository(genAi)

    @Provides
    @Singleton
    fun provideFirestoreWordBankRepository(db: FirebaseFirestore): FirestoreWordBankRepository =
        FirestoreWordBankRepository(db)

}