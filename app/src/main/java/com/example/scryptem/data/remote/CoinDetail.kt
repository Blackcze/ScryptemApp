package com.example.scryptem.data.remote

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    val id: String,
    val name: String,
    val symbol: String,
    val description: Description,
    val market_data: MarketData,
    val image: Image
)

data class Description(
    val en: String
)

data class MarketData(
    @SerializedName("current_price")
    val currentPrice: Map<String, Double>,
    @SerializedName("price_change_percentage_24h")
    val priceChange24h: Double
)

data class Image(
    val large: String
)