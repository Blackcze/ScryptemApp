package com.example.scryptem.data.repository

import com.example.scryptem.data.remote.Coin
import com.example.scryptem.data.remote.CoinGeckoApiService
import com.example.scryptem.data.remote.CoinDetail
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinGeckoApiService
) {
    suspend fun getCoins(vsCurrency: String): List<Coin> {
        return api.getCoins(vsCurrency = vsCurrency)
    }

    suspend fun getCoinDetail(coinId: String): CoinDetail {
        return api.getCoinDetail(coinId = coinId)
    }

    suspend fun getOhlcData(coinId: String, vsCurrency: String, days: Int): List<List<Double>> {
        return api.getOhlcData(coinId = coinId, vsCurrency = vsCurrency, days = days)
    }
}

