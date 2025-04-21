package com.example.scryptem.data.repository

import com.example.scryptem.data.remote.BlockchairApiService
import com.example.scryptem.data.remote.dto.BlockchairAddressResponse
import com.example.scryptem.data.remote.dto.BlockchairStatsResponse
import javax.inject.Inject

class BlockchairRepository @Inject constructor(
    private val api: BlockchairApiService
) {
    suspend fun getAddressInfo(network: String, address: String): BlockchairAddressResponse {
        return api.getAddressInfo(network, address)
    }

    suspend fun getNetworkStats(network: String): BlockchairStatsResponse {
        return api.getNetworkStats(network)
    }
}
