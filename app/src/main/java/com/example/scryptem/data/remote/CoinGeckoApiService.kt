package com.example.scryptem.data.remote

import com.example.scryptem.data.remote.dto.CoinDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Long,
    val price_change_percentage_24h: Double
)

interface CoinGeckoApiService {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<Coin>

    @GET("coins/{id}")
    suspend fun getCoinDetail(
        @Path("id") coinId: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false
    ): CoinDetail


    @GET("coins/{id}/ohlc")
    suspend fun getOhlcData(
        @Path("id") coinId: String,
        @Query("vs_currency") vsCurrency: String,
        @Query("days") days: Int
    ): List<List<Double>>
}
