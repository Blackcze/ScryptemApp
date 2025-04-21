package com.example.scryptem.presentation.coin_detail

import java.math.BigDecimal

data class AddressBalanceInfo(
    val balance: Long,
    val received: Long,
    val spent: Long,
    val usdValue: BigDecimal?,
    val feeSuggestion: String?
)
