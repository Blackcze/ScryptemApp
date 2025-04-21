package com.example.scryptem.data.remote.dto

data class BlockchairAddressResponse(
    val data: Map<String, AddressData>
)

data class AddressData(
    val address: AddressInfo
)

data class AddressInfo(
    val balance: Long,
    val received: Long,
    val spent: Long
)
