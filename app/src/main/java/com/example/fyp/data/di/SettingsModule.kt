package com.example.fyp.data.di

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.data.settings.UserSettingsRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Singleton
    @Provides
    fun provideUserSettingsRepository(
        firestore: FirebaseFirestore
    ): UserSettingsRepository {
        return UserSettingsRepositoryImpl(firestore)
    }
}