package com.example.scryptem.di

import android.content.Context
import androidx.room.Room
import com.example.scryptem.data.local.AppDatabase
import com.example.scryptem.data.local.dao.FavoriteCoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "favorite_coins_db"
        ).build()
    }

    @Provides
    fun provideFavoriteCoinDao(db: AppDatabase): FavoriteCoinDao {
        return db.favoriteCoinDao()
    }
}
