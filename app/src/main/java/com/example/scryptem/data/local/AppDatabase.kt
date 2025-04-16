package com.example.scryptem.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scryptem.data.local.dao.FavoriteCoinDao
import com.example.scryptem.data.local.entity.FavoriteCoinEntity

@Database(
    entities = [FavoriteCoinEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCoinDao(): FavoriteCoinDao
}
