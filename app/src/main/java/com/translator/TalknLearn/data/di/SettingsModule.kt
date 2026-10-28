package com.translator.TalknLearn.data.di

import com.translator.TalknLearn.data.settings.FirestoreUserSettingsRepository
import com.translator.TalknLearn.data.settings.SharedSettingsDataSource
import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        firestore: FirebaseFirestore
    ): UserSettingsRepository {
        return FirestoreUserSettingsRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideSharedSettingsDataSource(
        settingsRepo: UserSettingsRepository
    ): SharedSettingsDataSource {
        return SharedSettingsDataSource(settingsRepo)
    }
}