package com.example.scryptem.data.remote.dto

data class MempoolFeeResponse(
    val fastestFee: Int,
    val halfHourFee: Int,
    val hourFee: Int
)
