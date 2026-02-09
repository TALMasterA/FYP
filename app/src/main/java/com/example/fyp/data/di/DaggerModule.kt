package com.example.fyp.data.di

import android.content.Context
import com.example.fyp.data.cloud.LanguageDetectionCache
import com.example.fyp.data.cloud.TranslationCache
import com.example.fyp.data.clients.CloudSpeechTokenClient
import com.example.fyp.data.clients.CloudTranslatorClient
import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.data.user.FirestoreFavoritesRepository
import com.example.fyp.data.user.FirestoreProfileRepository
import com.example.fyp.data.repositories.AzureSpeechRepository
import com.example.fyp.data.repositories.FirebaseTranslationRepository
import com.example.fyp.data.repositories.SpeechRepository
import com.example.fyp.data.repositories.TranslationRepository
import com.example.fyp.domain.speech.DetectLanguageUseCase
import com.example.fyp.domain.speech.RecognizeFromMicUseCase
import com.example.fyp.domain.speech.RecognizeWithAutoDetectUseCase
import com.example.fyp.domain.speech.SpeakTextUseCase
import com.example.fyp.domain.speech.StartContinuousConversationUseCase
import com.example.fyp.domain.speech.TranslateBatchUseCase
import com.example.fyp.domain.speech.TranslateTextUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.fyp.data.cloud.CloudGenAiClient
import com.example.fyp.data.learning.LearningContentRepositoryImpl
import com.example.fyp.data.learning.FirestoreLearningSheetsRepository
import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.history.FirestoreHistoryRepository
import com.example.fyp.domain.history.HistoryRepository
import com.example.fyp.domain.learning.GenerateLearningMaterialsUseCase
import com.example.fyp.domain.learning.GenerateQuizUseCase
import com.example.fyp.domain.learning.LearningContentRepository
import com.example.fyp.domain.learning.LearningSheetsRepository
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.domain.learning.QuizGenerationRepository
import com.example.fyp.data.learning.QuizGenerationRepositoryImpl
import com.example.fyp.data.wordbank.FirestoreCustomWordsRepository
import com.example.fyp.data.wordbank.WordBankGenerationRepository
import com.example.fyp.data.wordbank.FirestoreWordBankRepository
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.domain.settings.SetVoiceForLanguageUseCase

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
    fun provideFirebaseAuthRepository(auth: FirebaseAuth): FirebaseAuthRepository =
        FirebaseAuthRepository(auth)

    @Provides @Singleton
    fun provideCloudSpeechTokenClient(functions: FirebaseFunctions): CloudSpeechTokenClient =
        CloudSpeechTokenClient(functions)

    @Provides @Singleton
    fun provideSpeechRepository(tokenClient: CloudSpeechTokenClient): SpeechRepository =
        AzureSpeechRepository(tokenClient)

    @Provides
    fun provideRecognizeUseCase(repo: SpeechRepository) = RecognizeFromMicUseCase(repo)

    @Provides
    fun provideRecognizeWithAutoDetectUseCase(repo: SpeechRepository) = RecognizeWithAutoDetectUseCase(repo)

    @Provides
    fun provideSpeakUseCase(repo: SpeechRepository) = SpeakTextUseCase(repo)

    @Provides
    fun provideContinuousUseCase(repo: SpeechRepository) = StartContinuousConversationUseCase(repo)

    @Provides
    fun provideTranslateUseCase(repo: TranslationRepository) = TranslateTextUseCase(repo)

    @Provides
    fun provideTranslateBatchUseCase(repo: TranslationRepository) = TranslateBatchUseCase(repo)

    @Provides
    fun provideDetectLanguageUseCase(repo: TranslationRepository) = DetectLanguageUseCase(repo)

    @Provides @Singleton
    fun provideCloudTranslatorClient(functions: FirebaseFunctions): CloudTranslatorClient =
        CloudTranslatorClient(functions)

    @Provides @Singleton
    fun provideTranslationCache(@ApplicationContext context: Context): TranslationCache =
        TranslationCache(context)

    @Provides @Singleton
    fun provideLanguageDetectionCache(@ApplicationContext context: Context): LanguageDetectionCache =
        LanguageDetectionCache(context)

    @Provides @Singleton
    fun provideWordBankCacheDataStore(@ApplicationContext context: Context): com.example.fyp.data.wordbank.WordBankCacheDataStore =
        com.example.fyp.data.wordbank.WordBankCacheDataStore(context)

    @Provides @Singleton
    fun provideTranslationRepository(
        client: CloudTranslatorClient,
        cache: TranslationCache,
        detectionCache: LanguageDetectionCache
    ): TranslationRepository =
        FirebaseTranslationRepository(client, cache, detectionCache)

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

    @Provides
    @Singleton
    fun provideFirestoreProfileRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirestoreProfileRepository =
        FirestoreProfileRepository(db, auth)

    @Provides
    @Singleton
    fun provideFirestoreFavoritesRepository(db: FirebaseFirestore): FirestoreFavoritesRepository =
        FirestoreFavoritesRepository(db)

    @Provides
    @Singleton
    fun provideFirestoreCustomWordsRepository(db: FirebaseFirestore): FirestoreCustomWordsRepository =
        FirestoreCustomWordsRepository(db)

    @Provides
    fun provideSetVoiceForLanguageUseCase(repo: UserSettingsRepository) =
        SetVoiceForLanguageUseCase(repo)

    @Provides
    @Singleton
    fun provideLearningSheetsRepository(db: FirebaseFirestore): LearningSheetsRepository =
        FirestoreLearningSheetsRepository(db)

    @Provides
    @Singleton
    fun provideQuizRepository(db: FirebaseFirestore): QuizRepository =
        FirestoreQuizRepository(db)

    @Provides
    @Singleton
    fun provideHistoryRepository(db: FirebaseFirestore): HistoryRepository =
        FirestoreHistoryRepository(db)
}