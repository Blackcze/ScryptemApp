package com.example.scryptem.data.repository

import com.example.scryptem.data.remote.Coin
import com.example.scryptem.data.remote.CoinGeckoApiService
import com.example.scryptem.data.remote.dto.CoinDetail
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinGeckoApiService
) {
    suspend fun getCoins(vsCurrency: String): List<Coin> {
        return api.getCoins(
            vsCurrency = vsCurrency,
            order = "market_cap_desc",
            perPage = 50,
            page = 1,
            sparkline = false
        )
    }

    suspend fun getCoinDetail(coinId: String): CoinDetail {
        return api.getCoinDetail(coinId)
    }

    suspend fun getOhlcData(coinId: String, days: Int, vsCurrency: String): List<List<Double>> {
        return api.getOhlcData(coinId, vsCurrency, days)
    }
}
