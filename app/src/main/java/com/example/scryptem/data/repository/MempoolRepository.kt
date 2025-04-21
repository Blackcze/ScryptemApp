package com.example.scryptem.data.repository

import com.example.scryptem.data.remote.MempoolApiService
import javax.inject.Inject

class MempoolRepository @Inject constructor(
    private val api: MempoolApiService
) {
    suspend fun getAddressInfo(address: String) = api.getAddressInfo(address)
    suspend fun getFees() = api.getRecommendedFees()
}
