package com.example.scryptem.presentation.coin_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.remote.dto.CoinDetail
import com.example.scryptem.data.remote.dto.OhlcEntry
import com.example.scryptem.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import com.example.scryptem.data.local.AddressPreferences
import com.example.scryptem.data.repository.MempoolRepository

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val mempoolRepository: MempoolRepository,
    private val addressPreferences: AddressPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = savedStateHandle["coinId"] ?: ""

    private val _coinDetail = MutableStateFlow<CoinDetail?>(null)
    val coinDetail: StateFlow<CoinDetail?> = _coinDetail

    private val _ohlcData = MutableStateFlow<List<OhlcEntry>>(emptyList())
    val ohlcData: StateFlow<List<OhlcEntry>> = _ohlcData

    val addressInput = MutableStateFlow("")
    private val _addressInfo = MutableStateFlow<AddressBalanceInfo?>(null)
    val addressInfo: StateFlow<AddressBalanceInfo?> = _addressInfo

    init {
        viewModelScope.launch {
            _coinDetail.value = repository.getCoinDetail(coinId)
        }
        loadOhlcData(30)
    }

    fun loadOhlcData(days: Int) {
        viewModelScope.launch {
            val rawData = repository.getOhlcData(coinId, days)
            _ohlcData.value = rawData.map {
                OhlcEntry(
                    timestamp = it[0].toLong(),
                    open = it[1].toFloat(),
                    high = it[2].toFloat(),
                    low = it[3].toFloat(),
                    close = it[4].toFloat()
                )
            }
        }
    }
    fun loadAddressInfo(address: String, network: String, coinPriceUsd: Double?) {
        Log.d("AddressCheck", "Kontrola: $address na síti $network")

        viewModelScope.launch {
            try {
                val info = mempoolRepository.getAddressInfo(address)
                val fees = mempoolRepository.getFees()

                val balance = info.chain_stats.funded_txo_sum - info.chain_stats.spent_txo_sum
                val usd = coinPriceUsd?.let {
                    BigDecimal(balance)
                        .divide(BigDecimal(1e8), 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(it))
                        .setScale(2, RoundingMode.HALF_UP)
                }

                _addressInfo.value = AddressBalanceInfo(
                    balance = balance,
                    received = info.chain_stats.funded_txo_sum,
                    spent = info.chain_stats.spent_txo_sum,
                    usdValue = usd,
                    feeSuggestion = "${fees.hourFee} sat/vB"
                )
            } catch (e: Exception) {
                Log.e("AddressCheck", " Chyba při načítání adresy", e)
                _addressInfo.value = null
            }
        }
    }

    /*fun loadAddressInfo(address: String, network: String, coinPriceUsd: Double?) {
        viewModelScope.launch {
            _addressInfo.value = AddressBalanceInfo(
                balance = 500000000L,
                received = 1000000000L,
                spent = 500000000L,
                usdValue = BigDecimal("30214.55"),
                feeSuggestion = "12 sat/B"
            )
        }
    }*/
}
