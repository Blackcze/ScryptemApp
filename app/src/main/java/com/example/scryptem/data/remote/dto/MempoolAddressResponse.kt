package com.example.scryptem.data.remote.dto

data class MempoolAddressResponse(
    val address: String,
    val chain_stats: ChainStats
)

data class ChainStats(
    val funded_txo_sum: Long,
    val spent_txo_sum: Long
)
