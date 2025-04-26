package com.example.scryptem.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface MempoolApiService {
    @GET("address/{address}")
    suspend fun getAddressInfo(@Path("address") address: String): MempoolAddressResponse

    @GET("v1/fees/recommended")
    suspend fun getRecommendedFees(): MempoolFeeResponse
}
