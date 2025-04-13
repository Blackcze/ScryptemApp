package com.example.scryptem.data.remote.dto

data class OhlcEntry(
    val timestamp: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)
