/**
 * Primary Hilt dependency-injection module (SingletonComponent).
 *
 * Provides app-wide singletons: Firebase instances, Azure clients,
 * repository bindings, use-case factories, and caches.
 */
package com.translator.TalknLearn.data.di

import android.content.Context
import com.translator.TalknLearn.data.cloud.LanguageDetectionCache
import com.translator.TalknLearn.data.cloud.TranslationCache
import com.translator.TalknLearn.data.clients.CloudSpeechTokenClient
import com.translator.TalknLearn.data.clients.CloudTranslatorClient
import com.translator.TalknLearn.core.security.SecureStorage
import com.translator.TalknLearn.data.friends.FriendRequestRateLimiter
import com.translator.TalknLearn.data.friends.SharedPreferencesFriendRequestRateLimiter
import com.translator.TalknLearn.data.user.FirebaseAuthRepository
import com.translator.TalknLearn.data.user.FirestoreFavoritesRepository
import com.translator.TalknLearn.data.user.FirestoreProfileRepository
import com.translator.TalknLearn.data.repositories.AzureSpeechRepository
import com.translator.TalknLearn.data.repositories.FirebaseTranslationRepository
import com.translator.TalknLearn.data.repositories.SpeechRepository
import com.translator.TalknLearn.data.repositories.TranslationRepository
import com.translator.TalknLearn.domain.speech.DetectLanguageUseCase
import com.translator.TalknLearn.domain.speech.RecognizeFromMicUseCase
import com.translator.TalknLearn.domain.speech.RecognizeWithAutoDetectUseCase
import com.translator.TalknLearn.domain.speech.SpeakTextUseCase
import com.translator.TalknLearn.domain.speech.StartContinuousConversationUseCase
import com.translator.TalknLearn.domain.speech.TranslateTextUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.translator.TalknLearn.data.cloud.CloudGenAiClient
import com.translator.TalknLearn.data.cloud.CloudAccountDeletionClient
import com.translator.TalknLearn.data.learning.LearningContentRepositoryImpl
import com.translator.TalknLearn.data.learning.FirestoreLearningSheetsRepository
import com.translator.TalknLearn.data.learning.FirestoreQuizRepository
import com.translator.TalknLearn.data.history.FirestoreHistoryRepository
import com.translator.TalknLearn.domain.history.HistoryRepository
import com.translator.TalknLearn.domain.learning.GenerateLearningMaterialsUseCase
import com.translator.TalknLearn.domain.learning.GenerateQuizUseCase
import com.translator.TalknLearn.domain.learning.LearningContentRepository
import com.translator.TalknLearn.domain.learning.LearningSheetsRepository
import com.translator.TalknLearn.domain.learning.QuizRepository
import com.translator.TalknLearn.domain.learning.QuizGenerationRepository
import com.translator.TalknLearn.data.learning.QuizGenerationRepositoryImpl
import com.translator.TalknLearn.data.wordbank.FirestoreCustomWordsRepository
import com.translator.TalknLearn.data.wordbank.WordBankGenerationRepository
import com.translator.TalknLearn.data.wordbank.FirestoreWordBankRepository
import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.domain.settings.SetVoiceForLanguageUseCase
import com.translator.TalknLearn.domain.feedback.FeedbackRepository
import com.translator.TalknLearn.data.feedback.FirestoreFeedbackRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

        try {
            val persistentCacheSettings = com.google.firebase.firestore.PersistentCacheSettings.newBuilder()
                .setSizeBytes(50L * 1024L * 1024L) // 50 MB — prevents unbounded disk growth
                .build()

            firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(persistentCacheSettings)
                .build()
        } catch (e: Exception) {
            // Fallback if settings already configured
            android.util.Log.w("DaggerModule", "Firestore settings already configured", e)
        }

        return firestore
    }

    @Provides @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        // Configure disk cache for network assets (Priority 3 #17: Image/Audio Asset Caching)
        // Cache size: 50 MB for audio/image assets
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(
            directory = cacheDir,
            maxSize = 50L * 1024L * 1024L // 50 MB
        )

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(com.translator.TalknLearn.data.network.RequestIdInterceptor()) // §5.1: per-request correlation header
            .addInterceptor(com.translator.TalknLearn.data.network.CacheInterceptor()) // Add cache interceptor
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Provides @Singleton
    fun provideFirebaseAuthRepository(
        auth: FirebaseAuth,
        secureStorage: SecureStorage
    ): FirebaseAuthRepository =
        FirebaseAuthRepository(auth, secureStorage)

    @Provides @Singleton
    fun provideCloudSpeechTokenClient(functions: FirebaseFunctions): CloudSpeechTokenClient =
        CloudSpeechTokenClient(functions)

    @Provides @Singleton
    fun provideSpeechRepository(
        tokenClient: CloudSpeechTokenClient,
        secureStorage: SecureStorage
    ): SpeechRepository =
        AzureSpeechRepository(tokenClient, secureStorage)

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
    fun provideWordBankCacheDataStore(@ApplicationContext context: Context): com.translator.TalknLearn.data.wordbank.WordBankCacheDataStore =
        com.translator.TalknLearn.data.wordbank.WordBankCacheDataStore(context)

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
    fun provideCloudQuizClient(functions: FirebaseFunctions): com.translator.TalknLearn.data.cloud.CloudQuizClient =
        com.translator.TalknLearn.data.cloud.CloudQuizClient(functions)

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
        auth: FirebaseAuth,
        accountDeletionClient: CloudAccountDeletionClient
    ): FirestoreProfileRepository =
        FirestoreProfileRepository(auth, accountDeletionClient)

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
    fun provideQuizRepository(
        db: FirebaseFirestore,
        cloudQuizClient: com.translator.TalknLearn.data.cloud.CloudQuizClient
    ): QuizRepository =
        FirestoreQuizRepository(db, cloudQuizClient)

    @Provides
    @Singleton
    fun provideHistoryRepository(db: FirebaseFirestore): HistoryRepository =
        FirestoreHistoryRepository(db)

    @Provides
    @Singleton
    fun provideFeedbackRepository(db: FirebaseFirestore, auth: FirebaseAuth): FeedbackRepository =
        FirestoreFeedbackRepository(db, auth)

    @Provides
    @Singleton
    fun provideFriendsRepository(db: FirebaseFirestore): com.translator.TalknLearn.data.friends.FriendsRepository =
        com.translator.TalknLearn.data.friends.FirestoreFriendsRepository(db)

    @Provides
    @Singleton
    fun provideFriendRequestRateLimiter(
        secureStorage: com.translator.TalknLearn.core.security.SecureStorage
    ): FriendRequestRateLimiter = SharedPreferencesFriendRequestRateLimiter(secureStorage)

    @Provides
    @Singleton
    fun provideChatRepository(
        db: FirebaseFirestore,
        friendsRepository: com.translator.TalknLearn.data.friends.FriendsRepository
    ): com.translator.TalknLearn.data.friends.ChatRepository =
        com.translator.TalknLearn.data.friends.FirestoreChatRepository(db, friendsRepository)

    @Provides
    @Singleton
    fun provideSharingRepository(
        db: FirebaseFirestore,
        translationRepository: TranslationRepository,
        friendsRepository: com.translator.TalknLearn.data.friends.FriendsRepository
    ): com.translator.TalknLearn.data.friends.SharingRepository =
        com.translator.TalknLearn.data.friends.FirestoreSharingRepository(db, translationRepository, friendsRepository)
}
