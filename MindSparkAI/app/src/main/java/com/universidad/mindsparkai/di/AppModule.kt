package com.universidad.mindsparkai.di

import android.content.Context
import com.universidad.mindsparkai.data.repository.LocalDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalDataRepository(
        @ApplicationContext context: Context
    ): LocalDataRepository {
        return LocalDataRepository(context)
    }
}