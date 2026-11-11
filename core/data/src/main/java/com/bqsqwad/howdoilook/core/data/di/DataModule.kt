package com.bqsqwad.howdoilook.core.data.di

import android.content.Context
import androidx.room.Room
import com.bqsqwad.howdoilook.core.data.local.AppDatabase
import com.bqsqwad.howdoilook.core.data.local.LookRatingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "howdoilook.db"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideLookRatingDao(db: AppDatabase): LookRatingDao = db.lookRatingDao()
}
