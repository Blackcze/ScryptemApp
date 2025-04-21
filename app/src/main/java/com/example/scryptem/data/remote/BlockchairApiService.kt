package com.example.scryptem.data.remote

import com.example.scryptem.data.remote.dto.BlockchairAddressResponse
import com.example.scryptem.data.remote.dto.BlockchairStatsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BlockchairApiService {

    @GET("{network}/dashboards/address/{address}")
    suspend fun getAddressInfo(
        @Path("network") network: String,
        @Path("address") address: String
    ): BlockchairAddressResponse

    @GET("{network}/stats")
    suspend fun getNetworkStats(
        @Path("network") network: String
    ): BlockchairStatsResponse
}
