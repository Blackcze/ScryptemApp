package com.example.scryptem.data.remote.dto

data class BlockchairStatsResponse(
    val data: StatsData
)

data class StatsData(
    val suggested_transaction_fee_per_byte_sat: Int?,
    val gas_price_wei: String?
)
