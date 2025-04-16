package com.example.scryptem.data.local.dao

import androidx.room.*
import com.example.scryptem.data.local.entity.FavoriteCoinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: FavoriteCoinEntity)

    @Delete
    suspend fun deleteCoin(coin: FavoriteCoinEntity)

    @Query("SELECT * FROM favorite_coins")
    fun getAllFavoriteCoins(): Flow<List<FavoriteCoinEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_coins WHERE id = :id)")
    fun isCoinFavorite(id: String): Flow<Boolean>
}
