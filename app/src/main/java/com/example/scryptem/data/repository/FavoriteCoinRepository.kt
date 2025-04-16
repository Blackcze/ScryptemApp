package com.example.scryptem.data.repository

import com.example.scryptem.data.local.dao.FavoriteCoinDao
import com.example.scryptem.data.local.entity.FavoriteCoinEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteCoinRepository @Inject constructor(
    private val dao: FavoriteCoinDao
) {

    suspend fun addToFavorites(coin: FavoriteCoinEntity) {
        dao.insertCoin(coin)
    }

    suspend fun removeFromFavorites(coin: FavoriteCoinEntity) {
        dao.deleteCoin(coin)
    }

    fun getAllFavorites(): Flow<List<FavoriteCoinEntity>> {
        return dao.getAllFavoriteCoins()
    }

    fun isFavorite(coinId: String): Flow<Boolean> {
        return dao.isCoinFavorite(coinId)
    }
}
