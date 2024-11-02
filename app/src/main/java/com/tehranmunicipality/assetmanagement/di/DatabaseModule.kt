package com.tehranmunicipality.assetmanagement.di

import android.content.Context
import androidx.room.Room
import com.tehranmunicipality.assetmanagement.data.DATABASE_NAME
import com.tehranmunicipality.assetmanagement.data.local.AppDao
import com.tehranmunicipality.assetmanagement.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callbacks: AppDatabase.Callback
    ) = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .addCallback(callbacks)
        .build()

    @Singleton
    @Provides
    fun provideAppDao(database: AppDatabase): AppDao = database.appDao()

}