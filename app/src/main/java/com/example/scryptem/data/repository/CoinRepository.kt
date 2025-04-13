package com.example.scryptem.data.repository


import com.example.scryptem.data.remote.Coin
import com.example.scryptem.data.remote.CoinGeckoApiService
import com.example.scryptem.data.remote.dto.CoinDetail
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinGeckoApiService
) {
    suspend fun getCoins(): List<Coin> {
        return api.getCoins()
    }
    suspend fun getCoinDetail(id: String): CoinDetail {
        return api.getCoinDetail(id)
    }
    suspend fun getOhlcData(id: String, days: Int): List<List<Double>> {
        return api.getOhlcData(id, days = days)
    }

}
